import requests
from py2neo import Node, Relationship, Graph, NodeMatcher, RelationshipMatcher  # 导入我们需要的头文件
import re
from bs4 import BeautifulSoup

structure = {
    'class': 'Class',
    'package': 'Package',
    'method': 'Method',
    'param': 'Parameter',
    'description': 'Description',
    'callorder': 'Call_Order',
    'return': 'Return',
    'constraint': 'Constraint',
    'field': 'Field',
    'type': 'Type',
    'paramdetail': 'Param Description',
    'throw': 'Throw',
    'version': 'Version',
}
relation = {
    'instanceof': 'instanceOf',
    'hasclass': 'hasClass',
    'hastype': 'hasType',
    'hasparam': 'hasParameter',
    'hasmethod': 'hasMethod',
    'detail': 'description',
    'hasreturn': 'hasReturn',
    'throws': 'throws',
    'version': 'version',
    'description': 'hasDescription',
    'extends': 'extends',
    'implements': 'implements'
}


def find_package_list():
    webpage = requests.get('https://docs.oracle.com/javase/8/docs/api/overview-summary.html')
    package_list = []
    soup = BeautifulSoup(webpage.content, 'html.parser')
    above = soup.find_all(class_='colFirst')
    for e in above:
        link = e.find('a', href=True)
        if link is not None:
            package_list.append([e.text, link["href"]])

    print('package crawled')
    return package_list

def graph_creator():
    for pkg_name, url in packagelist:
        print('===========', pkg_name, '===========')
        packageNode = Node(structure['package'], name=pkg_name)
        graph.create(packageNode)  # 创建包结点
        url = "https://docs.oracle.com/javase/8/docs/api/" + url
        soup = BeautifulSoup(requests.get(url).content, 'html.parser')
        above = soup.find_all(class_='colFirst')
        for e in above:
            link = e.find('a', href=True)
            if link is not None:
                classNode = Node(structure['class'], name=pkg_name + '.' + e.text)
                if len(node_matcher.match('Class').where(name=pkg_name + '.' + e.text)) == 0:
                    graph.create(classNode)
                graph.create(Relationship(packageNode, relation['hasclass'], classNode))
                url = link["href"]
                url2 = url.replace(re.findall(r"(.*)/java|org", url)[0], "https://docs.oracle.com/javase/8/docs/api")
                print(url2)
                soup = BeautifulSoup(requests.get(url2).content, 'html.parser')
                extends_node = soup.find(text='\nextends ')  # 获取继承关系
                implements_node = soup.find(text='\nimplements ')
                if extends_node is not None:
                    extends_node = extends_node.parent
                    extend = extends_node.find('a')['href'].strip(' ../../').replace('/', '.').replace('.html', '')
                    if len(node_matcher.match('Class').where(name=extend)) == 0:
                        extendNode = Node(structure['class'], name=extend)
                        graph.create(extendNode)
                        graph.create(Relationship(classNode, relation['extends'], extendNode))
                    else:
                        extendNode = node_matcher.match('Class').where(name=extend).first()
                        graph.create(Relationship(classNode, relation['extends'], extendNode))
                if implements_node is not None:
                    implements_node = implements_node.parent
                    implements = implements_node.find_all('a')
                    for i in implements:
                        try:
                            if 'interface' in i['title']:
                                implement = i['href'].strip(' ../../').replace('/', '.').replace('.html', '')
                                if len(node_matcher.match('Class').where(name=implement)) == 0:
                                    implementsNode = Node(structure['class'], name=implement)
                                    graph.create(implementsNode)
                                    graph.create(Relationship(classNode, relation['implements'], implementsNode))
                                else:
                                    implementsNode = node_matcher.match('Class').where(name=implement).first()
                                    graph.create(Relationship(classNode, relation['implements'], implementsNode))
                        except KeyError:
                            pass
                count = 0
                while True:
                    mi = soup.find(id=("i" + str(count)))
                    if mi is None:
                        break
                    methodNode = Node(structure['method'],
                                      name=re.sub('\(.*?\)', '', mi.find(class_='colLast').find('code').text.replace(' ', '').replace(u'\xa0', ' ').replace('\n', '')))
                    graph.create(methodNode)
                    graph.create(Relationship(classNode, relation['hasmethod'], methodNode))

                    paramNames = re.findall(re.compile('[A-Za-z<>]*?\s(.*?)(?=,|\))'),
                                            mi.find(class_='colLast').find('code').text.replace(' ', '').replace(
                                                u'\xa0', ' ').replace('\n', ''))
                    typeNames = re.findall(re.compile('(?<=\(|,)([A-Za-z<>,0-9_?\s\[\].]*?)\s'),
                                           mi.find(class_='colLast').find('code').text.replace(' ', '').replace(u'\xa0',
                                                                                                                ' ').replace(
                                               '\n', ''))
                    for i, paramName in enumerate(paramNames):
                        parameterNode = Node(structure['param'], name=typeNames[i] + ' ' + paramName)
                        graph.create(parameterNode)
                        graph.create(Relationship(methodNode, relation['hasparam'], parameterNode))
                    count = count + 1


if __name__ == '__main__':
    """
    MATCH (n)
    OPTIONAL MATCH (n)-[r]-()
    DELETE n,r
    """
    graph = Graph('http://localhost:7474', auth=('neo4j', '123456'))  # 连接neo4j 数据库
    node_matcher = NodeMatcher(graph)
    packagelist = find_package_list()
    graph_creator()
