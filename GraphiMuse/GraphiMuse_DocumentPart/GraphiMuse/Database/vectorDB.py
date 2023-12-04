import re
import jsonlines
import json
import requests
from bs4 import BeautifulSoup
import os
from langchain.embeddings.openai import OpenAIEmbeddings
from langchain.vectorstores import Chroma
from langchain.prompts import PromptTemplate
from langchain.chat_models import ChatOpenAI
from langchain.chains import LLMChain

os.environ["OPENAI_API_KEY"] = ''
persist_directory = '../vector_store'
persist_directory2 = '../vector_store2'
persist_directory3 = '../vector_store3'
persist_directory4 = '../function_vector'


class PageObtain:
    def __init__(self, url, classname):
        self.url = url
        self.classname = classname

    def parseSummary(self):
        response = requests.get(self.url)
        html_content = response.text
        soup = BeautifulSoup(html_content, "html.parser")
        if soup.find('div', attrs={"class": "details"}) is not None:
            return soup.find('div', attrs={"class": "details"}).text

    def parse(self):
        response = requests.get(self.url)
        html_content = response.text
        soup = BeautifulSoup(html_content, "html.parser")
        if soup.find("a", attrs={"name": "method.detail"}) is not None:
            elements_method = soup.find("a", attrs={"name": "method.detail"}).parent
            methods = [self.classname + '\n' + ele.text.strip() for ele in
                       elements_method.find_all("ul", attrs={"class": "blockList"})] if elements_method is not None else []
        else:
            methods = []
        if soup.find("a", attrs={"name": "constructor.detail"}) is not None:
            elements_cons = soup.find("a", attrs={"name": "constructor.detail"}).parent
            cons = [self.classname + '\n' + ele.text.strip() for ele in
                    elements_cons.find_all("ul", attrs={"class": "blockList"})] if elements_cons is not None else []
        else:
            cons = []
        return methods + cons

    def extract_regex(self, element):
        element = element.replace('\xa0', ' ')
        text = element
        print('*' * 20)
        print(repr(text))
        print('*' * 20)
        # 类名
        class_name = re.search(r'(.+)', text).group(1)
        print('Class Name:', class_name if class_name else None)

        # 方法名和参数

        method_and_params = re.search(r'\n(.*?)(\((.|\n)*?\))', text)  # re.search(r'((public|protected)\s.+\((.|\n)*?\))', text, )
        method_and_params = re.sub(r'\s{2,}', ' ', method_and_params.group(0).replace('\n', '')).replace('public ', '').replace('final', '').strip() if method_and_params else None
        print('Method and Params:', method_and_params)

        isConstructor = True if len(re.sub(r'\(.*?\)', '', method_and_params.strip()).split(' ')) == 1 else False

        method_name = re.search(r'\s([^\s]*?\(.*?\))', method_and_params) if not isConstructor else method_and_params
        method_name = method_name.group(1) if method_name and not isConstructor else method_and_params
        print('Method Name:', method_name)

        return_type = re.search(r'([^()]*?)\s', method_and_params, )
        return_type = return_type.group(0) if return_type and not isConstructor else None
        print("Return type:", return_type)

        return_condition = re.search(r'Returns:\n((.|\n)*?)(Throws:|Parameters:|Since:|See Also:|$)', text, )
        return_condition = re.sub(r'\s{2,}', ' ', return_condition.group(1)) if return_condition else None
        return_condition = return_condition.replace('\n', '') if return_condition and not isConstructor else None
        print("Return conditon:", return_condition)

        try:
            params_and_types = re.search(r'\((.*?)\)', method_and_params, ).group(1)
            params_and_types = params_and_types.split(', ')
            params = ';'.join([re.search(r'\s([^\s]*)$', p).group(1) for p in params_and_types])
            types = ';'.join([re.search(r'^([^\s]*)\s', p).group(1) for p in params_and_types])
        except:
            params = None
            types = None
        print('Parameters:', params)
        print('Types:', types)

        param_description = re.search(r'Parameters:\n(.*?)(?:Returns:|Throws:|Since:|See Also:|Since|$)', text, re.DOTALL)
        param_description = re.sub(r'\s{2,}', ' ', param_description.group(1).replace('\n', ' ')) if param_description else None
        param_description = ';'.join(re.findall(r'(?:^|\.\s)(.*?\s-\s.*?)(?=\.\s|$)', param_description)) if param_description else None
        print("Parameter description:", param_description)

        description = re.search(r'(?<=\)\n)(?:.*?Exception)?((.|\n)*?)(Returns:|Throws:|Parameters:|Since:|See Also:|$)', text, )
        description = description.group(1) if description else None
        description = re.sub(r'\s{2,}', ' ', description).replace('\n', '') if description else None
        print('Descriptions:', description)

        function = re.search(r"(.*?)\.\s", description) if description else None
        function = function.group(1) if function else None
        print('Function:', function)

        throws = re.search(r'Throws:\n(.*?)(?:Returns:|Throws:|Parameters:|Since:|See Also:|Since|$)', text, re.DOTALL)
        throws = re.sub(r'\s{2,}', ' ', throws.group(1).replace('\n', ' ')) if throws else None
        throws = ';'.join(re.findall(r'[A-Za-z]+Exception.*?(?=[A-Za-z]+Exception|$)', throws, )) if throws else None
        print('Throw:', throws)

        data = {
            'class_name': class_name,
            'method_name': method_name,
            'parameters': params,
            'Types': types,
            'parameter_description': param_description,
            'return_type': return_type,
            'return_condition': return_condition,
            'description': description,
            'function': function,
            'exception': throws
        }
        return data


class VectorCreator:

    def __init__(self):
        self.embeddings = OpenAIEmbeddings()
        self.vectordb = Chroma(persist_directory=persist_directory, embedding_function=self.embeddings)  # 完整json
        self.vectordb2 = Chroma(persist_directory=persist_directory2, embedding_function=self.embeddings)
        self.vectordb3 = Chroma(persist_directory=persist_directory3, embedding_function=self.embeddings)
        self.llm = ChatOpenAI(model_name="gpt-3.5-turbo", temperature=0)

    def iter_crawl(self, classname=None, gpt=True):
        func = self.crawl if gpt else self.crawl_reg
        with open("classlist.txt", 'r') as f:
            lines = f.readlines()
            if classname is None:
                index = 0
                for line in lines:
                    print('=============', line)
                    func(line)
                    index = index + 1
            else:
                index = lines.index(classname)
                print(index)
                for line in lines[index:]:
                    print('=============', line)
                    func(line)
                    index = index + 1

    def json_convert(self, info):
        template = """Between >>> and <<< are method content.
        Please extract the information from the parameter request

        >>> {content} <<<
        Please return the data in the following JSON format:
        {{
          "class_name":"<full class_name>",
          "method_name":"<method_name>(<argumentstype argumentsname>)",
          "method_details":"<details>",
          "function":"<functionality of the method>",
          "returns":"<type>-<value>-<condition>",
          "parameters":"<name 1>;<name 2>;....;<name n>",
          "exception_description":"<exception-description_1>;<exception-description_2>;...;<exception-description_n>"
        }}
        Between < and > is the information you should output. If there is no information about this property, output "None".
        index is the number of the class, output as is
        """
        prompt = PromptTemplate(
            input_variables=["content"],
            template=template
        )

        chain = LLMChain(llm=self.llm, prompt=prompt)
        inputs = {
            "content": info
        }
        json_info = chain.run(inputs)
        return json_info

    def crawl_reg(self, classname):
        url = "https://docs.oracle.com/javase/8/docs/api" + '/' + re.sub(r"<.*?>", "", classname.replace('\n', '').replace('.', '/')) + '.html'
        pageobtainer = PageObtain(url=url, classname=classname)
        rawinfo = pageobtainer.parse()
        with jsonlines.open("../data/method.json", mode="a") as f:
            text = []
            for r in rawinfo:
                data = pageobtainer.extract_regex(r)
                print(data)
                data_text = f"{data['class_name']}|{data['method_name']}|{data['function']}"
                print('=' * 30)
                f.write(data)
                text.append(data_text)
            if len(text) > 0:
                self.vectordb3.add_texts(texts=text)
            self.vectordb3.persist()

    def crawl(self, classname, ex_url=None, proj_name=None):
        url = "https://docs.oracle.com/javase/8/docs/api" + '/' + re.sub(r"<.*?>", "", classname.replace('\n', '').replace('.', '/')) + '.html' if ex_url is None else ex_url
        pageobtainer = PageObtain(url=url, classname=classname)
        rawinfo = pageobtainer.parse()
        jsontext = []  # 完整json
        jsontext2 = []  # 查询用json
        with jsonlines.open("data/methods_full.json", mode="a") as f, jsonlines.open("data/functions.json", mode='a') as f2:
            for r in rawinfo:
                jsoninfo_str = self.json_convert(r)
                print(jsoninfo_str)
                try:
                    jsoninfo = json.loads(jsoninfo_str)
                except json.decoder.JSONDecodeError:
                    last_comma_index = jsoninfo_str.rfind(",")
                    if last_comma_index != -1:
                        jsoninfo_str = str(jsoninfo_str[:last_comma_index] + jsoninfo_str[last_comma_index + 1:])
                        try:
                            jsoninfo = json.loads(jsoninfo_str)
                        except json.decoder.JSONDecodeError:
                            print("**********json.decoder.JSONDecodeError**************")
                            continue
                jsoninfo2 = {
                    "class_name": jsoninfo['class_name'],
                    "method_name": jsoninfo['method_name'],
                    "function": jsoninfo["function"]
                }
                f.write(jsoninfo)
                f2.write(jsoninfo2)
                jsontext2.append(json.dumps(jsoninfo2))
                jsontext.append(json.dumps(jsoninfo))
            if len(rawinfo) > 0:
                self.vectordb.add_texts(texts=jsontext)
                self.vectordb2.add_texts(texts=jsontext2)
                print(f"Writing.....number of rawinfo:{len(rawinfo)}")
                self.vectordb.persist()
                self.vectordb2.persist()


if __name__ == "__main__":
    vectorcreator = VectorCreator()
    vectorcreator.iter_crawl(gpt=False)
