'''
Created on Dec 16, 2011

@author: IslamM
'''
from suds.sax.parser import Parser
from suds.umx.basic import Basic as bumx

class MasterXMLParser():
    def __init__(self, filename):
        sax = Parser()
        xml = sax.parse(filename).root()
        data = bumx().process(xml)
        self.trapDescriptions = []
        for msg in data.MessageSource.MessageDefinitions.Message:
            desc = msg.Description.split('%n%n')
            self.trapDescriptions.append((msg.LookupIDs.id, desc))            

    def get_description(self, trapid):
        for key, value in self.trapDescriptions:
            if trapid == key:
                return value 
        return ''

if __name__ == '__main__':
    filename = 'MasterXML.xml'
    masterXml = MasterXMLParser(filename)
    
    print masterXml.get_description('6050')