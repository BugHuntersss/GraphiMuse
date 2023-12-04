import requests
from bs4 import BeautifulSoup


class ListObtainer:
    def __init__(self):
        pass

    def itext_list(self):
        def packageList():
            packagelist = []
            url = "https://api.itextpdf.com/iText5/java/5.5.9/overview-summary.html"
            response = requests.get(url)
            content = response.text
            soup = BeautifulSoup(content, "html.parser")
            temp = soup.find(string='ASN.1 Support Packages')
            List_content = temp.parent.parent.next_sibling.next_sibling.next_sibling.next_sibling
            for ele in List_content.find_all("a"):
                # print(ele.text, 'https://api.itextpdf.com/iText5/java/5.5.9/' + ele['href'])
                packagelist.append((ele.text, 'https://api.itextpdf.com/iText5/java/5.5.9/' + ele['href']))
            return packagelist

        packagelist = packageList()
        for p in packagelist:
            print(p)
        with open("data/itext.txt", "w+") as f:
            for packagename, url in packagelist:
                response = requests.get(url)
                content = response.text
                soup = BeautifulSoup(content, "html.parser")
                temp = soup.find_all(class_='colFirst')
                for t in temp:
                    if t.text != 'Interface' and t.text != 'Class':
                        print(packagename + '.' + t.next.text)
                        f.writelines(packagename + '.' + t.next.text + '\n')

    def asn1_list(self):
        def packageList():
            packagelist = []
            url = "https://javadoc.io/static/org.bouncycastle/bcprov-jdk15on/1.64/overview-summary.html"
            response = requests.get(url)
            content = response.text
            soup = BeautifulSoup(content, "html.parser")
            temp = soup.find(string='ASN.1 Support Packages')
            List_content = temp.parent.parent.next_sibling.next_sibling.next_sibling.next_sibling
            for ele in List_content.find_all("a"):
                # print(ele.text, 'https://api.itextpdf.com/iText5/java/5.5.9/' + ele['href'])
                packagelist.append(
                    (ele.text, 'https://javadoc.io/static/org.bouncycastle/bcprov-jdk15on/1.64/' + ele['href']))
            return packagelist

        packagelist = packageList()
        for p in packagelist:
            print(p)
        with open("data/asn1.txt", "w+") as f:
            for packagename, url in packagelist:
                response = requests.get(url)
                content = response.text
                soup = BeautifulSoup(content, "html.parser")
                temp = soup.find_all(class_='colFirst')
                for t in temp:
                    if t.text != 'Interface' and t.text != 'Class':
                        print(packagename + '.' + t.next.text)
                        f.writelines(packagename + '.' + t.next.text + '\n')


if __name__ == "__main__":
    a = ListObtainer()
