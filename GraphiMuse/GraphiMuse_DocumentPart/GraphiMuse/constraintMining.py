import os
import json
import re
from simphile import jaccard_similarity
import jsonlines
from langchain.embeddings import OpenAIEmbeddings
from langchain.prompts import PromptTemplate
from langchain.chat_models import ChatOpenAI
from langchain.chains import LLMChain
from langchain.vectorstores import Chroma
from py2neo import Graph, Node, Relationship

os.environ["OPENAI_API_KEY"] = ''
persist_directory = 'vector_store'
persist_directory2 = 'vector_store2'
persist_directory3 = 'vector_store3'


class GraphQuery:
    def __init__(self):
        self.graph = Graph('http://localhost:7474', auth=('neo4j', '123456'))

    def check_package(self, src, trg):
        query = """
        MATCH (n)-[:hasClass]->(p) 
        WHERE p.name='{}'
        RETURN n
        """
        src = self.graph.run(query.format(src['class'])).data()[0]
        trg = self.graph.run(query.format(trg['class'])).data()[0]
        if src == trg:
            return True
        else:
            return False

    def find_method(self, method):
        query = """
        MATCH (a)-[:hasMethod]->(n)-[:hasParameter]->(m)
        WHERE n.name ='{}' and a.name='{}' 
        WITH n, count(m) as child_count
        WHERE child_count={}
        return n
        """
        query = query.format(re.sub('\(.*?\)', '', method['method_name']), method['class_name'], str(len(method['parameters'].split(';'))))
        print(query)
        node = self.graph.run(query).data()
        print(node)
        try:
            if len(node) > 0:
                node = node[0]['n']
                return node
            else:
                return None
        except:
            return None

    def add_rule(self, node, rule):
        if node is None:
            return False
        rulenode = Node('Rule', name=rule.split(':')[1])
        try:
            self.graph.create(Relationship(node, 'hasRule', rulenode))
        except:
            return False
        return True


class Miner:
    def __init__(self):
        self.embeddings = OpenAIEmbeddings()
        self.llm = ChatOpenAI(model_name="gpt-3.5-turbo", temperature=0)
        self.llm2 = ChatOpenAI(model_name="gpt-4", temperature=0)
        self.vectordb = Chroma(persist_directory=persist_directory, embedding_function=self.embeddings)
        self.vectordb2 = Chroma(persist_directory=persist_directory2, embedding_function=self.embeddings)
        self.vectordb3 = Chroma(persist_directory=persist_directory3, embedding_function=self.embeddings)
        self.graph = GraphQuery()

    def decompose(self, sentence):

        prompt_template = """
        - Decompose the main sentence {{{sentence}}} into clauses, following these steps: 
        1. Identify the subject of the main sentence (only one should exist).
        2. If it is already a complete sentence or no subject in main sentence, there is no need for further decomposition and just output the main sentence. 
        3. If not, divide it into multiple clauses based on logical relationships('or','and',',',';'). 
        4. Output each clause, replace pronouns with the full subject name in each clause. Output format should be [clause 1; clause 2; ....; clause n].
        your task is to decompose the main sentence that I give you first into clauses.
        Don't output your own caution.
        OUTPUT:
        """

        """
        - Example:{{
        1.main sentence: the named file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
          OUTPUT:[the named file does not exist; the named file is a directory rather than a regular file; the named file cannot be opened for reading for some other reason]
        2.main sentence: the class of the specified element prevents it from being added to this list
          OUTPUT:[the class of the specified element prevents it from being added to this list]
        }}
        These two are just example, 
        """
        PROMPT = PromptTemplate(template=prompt_template, input_variables=['sentence'])
        chain = LLMChain(llm=self.llm, prompt=PROMPT)
        output = chain.run({"sentence": sentence.strip()})
        result = re.findall(r"\[.*?]", output)
        print(result)
        clauses = []
        if len(result) > 0:
            for r in result:
                for i in r.strip("[]").split(";"):
                    clauses.append(i)
        else:
            return [sentence]
        return clauses

    def classify(self, information):
        prompt_classify = """
        You're a helpful assistant
        METHOD INFORMATION:'''{information}'''
        Task: '''{task}'''
        """
        task_classify = """
        - Give the type based on <exception>.
        - There are three types of preventing exception:
        1.pre-condition: means checking the pre-condition of METHOD.
        2.parameter value: means numbers range check, null check or boolean value check of the METHOD's parameter.
        3.call order: means checking calling order between other methods that mentioned in the <exception>. If <exception> contains 'call', it may be checking call order.
        - Provide the result in the following JSON format:
        {
        "type": "<call-order> or <parameter-value> or <pre-condition>"
        }
        OUTPUT:
        """
        PROMPT = PromptTemplate(template=prompt_classify,
                                input_variables=["task", "information"])
        classify_chain = LLMChain(llm=self.llm, prompt=PROMPT)
        classify_result = classify_chain.run(
            {"task": task_classify, "information": information}), "\n\n"
        print('\033[34m' + f"Method information------->:{information.strip()}" + '\033[0m', '\n',
              '\033[33m' + f"Classify Result-------->{classify_result[0]}" + '\033[0m')
        return classify_result

    def paramrule(self, information):
        prompt_para = """
        - METHOD INFORMATION:{information}
        - Your task is to generate the constraint that avoid the occurrence of conditions in <exception> of METHOD INFORMATION. 
        - Perform the task into 3 steps: 
        1. Identify which parameter of PARAMETERS is in <exception>. If no parameter is in <exception>, output None and exit. 
        2. Infer the anti-relationship and the value of the parameter which is anti-logic of the <exception> sentence. If no specific value or relationship of the format below is in the <exception>, output None and exit. 
        3. Generate the constraint based on anti-relationship and the value of above steps;
        - Output constraint format: [a;b;c]
        - In the triplet, first is the parameter name you identified, second is the relationship(<;>;>=;<=;!=,== or None), and the third is the specific value(numbers;null) .
        Think step by step .
        """
        paraPROMPT = PromptTemplate(template=prompt_para, input_variables=["information"])
        parachain = LLMChain(llm=self.llm, prompt=paraPROMPT)
        result = parachain.run({"information": information})
        print("\033[32mConstraints------->:\033[0m", '\033[32m' + result + '\033[0m')
        return result

    def orderrule(self, information, condition, classname, methodname):
        query = f""" {classname}|{condition}"""
        k = 5
        result = self.vectordb3.similarity_search_with_score(query, include_metadata=True, k=k)
        result = [r for r in result if re.sub(r"\(.*?\)", "", r[0].page_content.split('|')[1]) != re.sub(r"\(.*?\)", "", methodname)]
        if len(result) < 1:
            return None
        for res in result:
            print(res)
        docs = [result[0][0]]
        scores = [result[0][1]]
        jaccard_score = jaccard_similarity(docs[0].page_content.split('|')[2], condition)
        print(jaccard_score)
        if scores[0] > 0.3:
            return None
        temp = docs[0].page_content
        doc = f"""
                class:{temp.split('|')[0]}
                method_name:{temp.split('|')[1]}
                functionality:{temp.split('|')[2]}
                """
        prompt_order = """
                - input METHOD INFORMATION:{information}
                - POSSIBLE PROCESS FUNCTION:{context}
                - Your task is to infer the temporal constraint that in contradiction to given <exception> of input METHOD INFORMATION. 
                - Finish the task in 3 steps:
                    1.Check if the POSSIBLE PROCESS FUNCTION is in the <exception>. If not, do not execute step 2 and output None.
                    2.If it is mentioned, check if there is a mandatory temporal constraint between POSSIBLE PROCESS FUNCTION and METHOD. If there is no mandatory constraint, do not execute step 3 and output None.
                    3.If there is a mandatory constraint, generate a constraint triplet.
                - Output formats: [a;b;c] The first element is PROCESS FUNCTION's name, second element is the relationship between process function and METHOD(one of :should be called before;should be called after), the third element is METHOD's name
                - Output as concisely as possible. Do not output "[]" except final output.
                - STEPs:
                """
        orderPROMPT = PromptTemplate(template=prompt_order,
                                     input_variables=["context", "information"])
        orderchain = LLMChain(llm=self.llm, prompt=orderPROMPT)
        result2 = orderchain.run({"context": doc, "information": information})
        print("\033[32mConstraints------->:\033[0m", '\033[32m' + result2 + '\033[0m')
        return result2

    def conditionrule(self, information, condition, classname, methodname):
        query = f""" {classname}|{condition}"""
        k = 5
        result = self.vectordb3.similarity_search_with_score(query, include_metadata=True, k=k)
        result = [r for r in result if re.sub(r"\(.*?\)", "", r[0].page_content.split('|')[1]) != re.sub(r"\(.*?\)", "", methodname)]
        if len(result) < 1:
            return None
        for res in result:
            print(res)
        docs = [result[0][0]]
        scores = [result[0][1]]
        jaccard_score = jaccard_similarity(docs[0].page_content.split('|')[2], condition)
        print(jaccard_score)
        if scores[0] > 0.22:
            return None
        temp = docs[0].page_content
        doc = f"""
        class:{temp.split('|')[0]}
        method_name:{temp.split('|')[1]}
        functionality:{temp.split('|')[2]}
        """
        prompt_order = """
        - input METHOD INFORMATION:{information}
        - POSSIBLE PROCESS FUNCTION:{context}
        - Your task is to infer the rules from given <exception> of input METHOD INFORMATION. 
        - Finish the task in 2 steps:
            1.Check if the functionality of the POSSIBLE PROCESS FUNCTION can match the <exception>. If not, do not execute step 2 and output None.
            2.If it is equivalent, generate a rule triplet of POSSIBLE PROCESS FUNCTION. If it doesn't satisfy, output None and exit.
        - Output formats: [a;b;c] The first element is PROCESS FUNCTION's name, second element is the relationship between process function and METHOD(one of :should be called before;boolean value check;null value check;exception catch), the third element is METHOD's name
        - Output as concisely as possible. Do not output "[]" except final output.
        - STEPs:
        """
        orderPROMPT = PromptTemplate(template=prompt_order,
                                     input_variables=["context", "information"])
        orderchain = LLMChain(llm=self.llm, prompt=orderPROMPT)
        result2 = orderchain.run({"context": doc, "information": information})
        print("\033[32mConstraints------->:\033[0m", '\033[32m' + result2 + '\033[0m')
        return result2

    def mining(self, vc=True):
        with jsonlines.open("data/method.json", "r") as f, open('data/rule_exp.txt', "a+") as rulefile:
            for o in f.iter():
                rules = []
                if o['exception'] is None:
                    continue
                for sentence in o['exception'].split(";"):
                    print(sentence)
                    if "-" not in sentence:
                        if "Exception" in sentence:
                            rules.append('[' + o['method_name'] + ';' + 'try-catch' + ';' + sentence + ']')
                        continue
                    if len(sentence.split("-")) == 2:
                        rules.append('[' + o['method_name'] + ';' + 'try-catch' + ';' + sentence.split("-")[0] + ']')
                        for e in self.decompose(sentence.split("-")[1]):
                            information = f"""
                            METHOD: {o['method_name']},
                            CLASS:{o['class_name']},
                            PARAMETERS:{o['parameters']},
                            <exception>: {e}
                            """
                            classify_result = self.classify(information)
                            for ele in re.findall(r"\{.*?}", classify_result[0], flags=re.DOTALL):
                                type_ = json.loads(ele)['type']
                                if 'parameter' in type_:
                                    paramraw = self.paramrule(information)
                                    paramrules = re.findall(r"\[.*?]", paramraw)
                                    if o['parameters']:
                                        paramlist = [p.strip() for p in o['parameters'].split(';')]
                                    else:
                                        continue
                                    for r in paramrules:
                                        if 'None' in r:
                                            continue
                                        try:
                                            oprator = r.strip("[]").split(';')[1].strip().strip("\"\'")
                                            if oprator not in ['>', '<', '>=', '<=', '==', '!=']:
                                                continue
                                            param1 = r.strip("[]").split(';')[0].strip().strip("\"\'")
                                            if '.' not in param1:
                                                ele1 = 'param ' + str(
                                                    paramlist.index(param1) + 1) if param1 in paramlist else param1
                                            else:
                                                temp1_param1 = param1.split('.')[0]
                                                temp2_param1 = param1.split('.')[1]
                                                ele1 = 'param ' + str(paramlist.index(
                                                    temp1_param1) + 1) + '.' + temp2_param1 if temp1_param1 in paramlist else param1

                                            param2 = r.strip("[]").split(';')[2].strip().strip("\"\'")
                                            if '.' not in param2:
                                                ele2 = 'param ' + str(
                                                    paramlist.index(param2) + 1) if param2 in paramlist else param2
                                            else:
                                                temp1_param2 = param2.split('.')[0]
                                                temp2_param2 = param2.split('.')[1]
                                                ele2 = 'param ' + str(paramlist.index(
                                                    temp1_param2) + 1) + '.' + temp2_param2 if temp1_param2 in paramlist else param2

                                        except:
                                            print("param identify Error")
                                            continue
                                        temp = '[' + ele1 + ';' + r.strip().split(';')[1] + ';' + ele2 + ']'
                                        if 'param' in ele2 or ele2 in ['null', 'not null'] or str.isdigit(ele2):
                                            rules.append(temp)

                                elif 'order' in type_:
                                    orderraw = self.orderrule(information, e, o['class_name'], o['method_name'])
                                    orderrules = re.findall(r"\[.*?]", orderraw) if orderraw is not None else []
                                    for r in orderrules:
                                        rules.append(r)

                                elif 'condition' in type_:
                                    conditionraw = self.conditionrule(information, e, o['class_name'], o['method_name'])
                                    conditionrules = re.findall(r"\[.*?]", conditionraw) if conditionraw is not None else []
                                    for r in conditionrules:
                                        rules.append(r)

                            print("=" * 30)
                node = self.graph.find_method(o)
                for p in [s + "\n" for s in self.parserules(rules, o['class_name'] + "." + o['method_name'])]:
                    self.graph.add_rule(node, p)
                    print('\033[91m' + p + '\033[0m')
                rulefile.writelines([s + "\n" for s in self.parserules(rules, o['class_name'] + "." + o['method_name'])])
                rulefile.flush()
                print("*" * 30)

    def parserules(self, string, prestring, type_='None', param=None):
        parsed_rule = string
        result = []
        rulelist = []
        for p in parsed_rule:
            elements = p.strip("[]").split(";")
            if len(elements) != 3:
                continue
            flag = False
            for r in rulelist:
                if re.sub(r"\(.*?\)", "", r[0]) == re.sub(r"\(.*?\)", "", elements[0]) and \
                        re.sub(r"\(.*?\)", "", r[2]) == re.sub(r"\(.*?\)", "", elements[2]) or \
                        re.sub(r"\(.*?\)", "", r[0]) == re.sub(r"\(.*?\)", "", elements[2]) and \
                        re.sub(r"\(.*?\)", "", r[2]) == re.sub(r"\(.*?\)", "", elements[0]):
                    flag = True
                    break

            if not flag:
                result.append(prestring + ':' + p)
                rulelist.append(elements)
        if type_ == 'param':
            for p in parsed_rule:
                elements = p.split(";")
                if len(elements) != 3:
                    continue
                else:
                    if str.lower(elements[0].strip()) in param.split(';'):
                        result.append(prestring + ':' + p)
        return result


if __name__ == "__main__":
    miner = Miner()
    miner.mining()
