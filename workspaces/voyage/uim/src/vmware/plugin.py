#####################################################
#
# plugin.py
#
# Copyright 2011 Hewlett-Packard Development Company, L.P.
#
# Hewlett-Packard and the Hewlett-Packard logo are trademarks of
# Hewlett-Packard Development Company, L.P. in the U.S. and/or other countries.
#
# Confidential computer software. Valid license from Hewlett-Packard required
# for possession, use or copying. Consistent with FAR 12.211 and 12.212,
# Commercial Computer Software, Computer Software Documentation, and Technical
# Data for Commercial Items are licensed to the U.S. Government under
# vendor's standard commercial license.
#
# Author:
#    Andy Yates
# 
# Description:
#    Classes for creating VMWare vSphere Plug-in Configuration Files
#
#####################################################

from xml.dom.minidom import Document
import unittest

class Title :
    def __init__(self, title, locale='en') :
        self.title = title
        self.locale = locale
        
class Url :
    def __init__(self, url, display=None, **extras) :
        # The extras arg is needed to capture unused key word args like web_root and path.
        # The web_root and path are just configuration artifacts from building the real url.
        self.url = url
        self.display = display

class Extension :
    def __init__(self, parent, titles, url, icon = None, iconSmaller=None, 
                 customAttributes={}, privileges = [], treestyles=[], extensions=[]) :
        self.parent = parent
        # Please forgive the type checking
        # This allows the extension class to take either instances of Title or
        # dicts of Title args.  Same for url and extensions.
        self.titles = [ Title(**t) if isinstance(t, dict) else t for t in titles ]
        self.url = Url(**url) if isinstance(url, dict) else url
        self.icon = icon
        self.iconSmaller = iconSmaller
        self.customAttributes = customAttributes
        self.privileges = privileges
        self.treestyles = treestyles
        self.extensions = [ Extension(**e) if isinstance(e, dict) else e for e in extensions ]

    def createExtensionElement(self, doc) :
        e = doc.createElement('extension')
        e.setAttribute('parent', self.parent)
        
        if self.privileges :
            e.setAttribute('privilege', ', '.join(self.privileges) )
            
        if self.treestyles :
            e.setAttribute('treestyles', ', '.join(self.privileges) )
        
        for title in self.titles :
            t = doc.createElement('title')
            t.setAttribute('locale', title.locale)
            t.appendChild(doc.createTextNode(title.title))
            e.appendChild(t)
        
        url = doc.createElement('url')
        if self.url.display :
            url.setAttribute('display', self.url.display)
        url.appendChild(doc.createTextNode(self.url.url))
        e.appendChild(url)
        
        if self.icon :
            i = doc.createElement('icon')
            i.appendChild(doc.createTextNode(self.icon))
            e.appendChild(i)
            
        if self.iconSmaller :
            i = doc.createElement('iconSmaller')
            i.appendChild(doc.createTextNode(self.iconSmaller))
            e.appendChild(i)        
                    
        for k,v in self.customAttributes.items() :
            ca = doc.createElement('customAttribute')
            ca.setAttribute('name', k)
            ca.appendChild(doc.createTextNode(v))
            e.appendChild(ca)
                
        for ext in self.extensions :
            e.appendChild(ext.createExtensionElement(doc))
            
        return e
        
class PluginConfig :
    def __init__(self, version, key, extensions, description = None, name = None, vendor = None, 
                 multiVCSupported=True, supportNonSecureCommunication=False) :
        self.version = version
        self.key = key
        self.description = description
        self.name = name
        self.vendor = vendor
        self.multiVCSupported = multiVCSupported
        self.supportNonSecureCommunication = supportNonSecureCommunication
        self.extensions = [ Extension(**e) if isinstance(e, dict) else e for e in extensions ]        
        
    def toxml(self) :
        # Create the minidom document
        doc = Document()

        # Create the scriptConfiguration
        scriptConfiguration = doc.createElement("scriptConfiguration")
        scriptConfiguration.setAttribute('version', self.version)
        doc.appendChild(scriptConfiguration)
        
        # Create the key
        key = doc.createElement('key')
        key.appendChild(doc.createTextNode(self.key))
        scriptConfiguration.appendChild(key)
        
        # Create supportNonSecureCommunication
        if self.supportNonSecureCommunication :
            snsc = doc.createElement('supportNonSecureCommunication')
            snsc.appendChild(doc.createTextNode('true'))
            scriptConfiguration.appendChild(snsc)
        
        # Create description
        if self.description :
            desc = doc.createElement('description')
            desc.appendChild(doc.createTextNode(self.description))
            scriptConfiguration.appendChild(desc)
        
        # Create name
        if self.name :
            name = doc.createElement('name')
            name.appendChild(doc.createTextNode(self.name))
            scriptConfiguration.appendChild(name)
        
        # Create vendor
        if self.vendor :
            vendor = doc.createElement('vendor')
            vendor.appendChild(doc.createTextNode(self.vendor))
            scriptConfiguration.appendChild(vendor)
        
        # Create multiVCsupported
        mvcs = doc.createElement('multiVCsupported')
        if(self.multiVCSupported == True) :
            mvcs.appendChild(doc.createTextNode('true'))
        else :
            mvcs.appendChild(doc.createTextNode('false'))
        scriptConfiguration.appendChild(mvcs)
        
        # Create the extensions
        for ext in self.extensions :
            scriptConfiguration.appendChild(ext.createExtensionElement(doc))

        # Don't use toprettyxml()!  It inserts extra white space and line feeds to text nodes.
        #return doc.toprettyxml()
        return doc.toxml()
        

class PluginConfigTesting(unittest.TestCase):
    
    def test_generate_config(self):
    
        extensions = []
        extensions.append(Extension(parent = 'Home.Inventory', 
                                    titles = [ {'title' : 'Test Plugin', 'locale' : 'en'}, 
                                        {'title' : 'Prueba', 'locale' : 'es'}
                                    ], 
                                    url = {'url' : 'http://www.hp.com/ic4vc/plugin/test1.html', 'display' : 'window'},
                                    icon = 'http://hp.com/icon.png', 
                                    iconSmaller='hppp://hp.com/icon_smaller.png', 
                                    customAttributes={'A1': 'A1', 'A2': 'A2'}, 
                                    privileges = ['Host.Config.Connection', 'Host.Config.Storage'], 
                                    treestyles=['Physical', 'Virtual'], 
                                    extensions=[])
                                )
        
        extensions.append(Extension(parent = 'Home.Inventory', 
                                    titles = [ Title('Test 1', 'en'),                                     
                                    ], 
                                    url = Url('http://www.hp.com/ic4vc/plugin/test1.html'),
                                    # nested extension
                                    extensions=[Extension(parent = 'Home.Inventory', 
                                        titles = [ Title('Test 1-1', 'en'),                                     
                                        ], 
                                    url = Url('http://www.hp.com/ic4vc/plugin/test1-1.html'),
                                    
                                    extensions=[]),])
                                )
        
        p = PluginConfig('1.0', 'com.hp.ic4vc.test', extensions, description = 'Test Plugin Config', name = 'Test', vendor = 'HP', 
                    multiVCSupported=True, supportNonSecureCommunication=True)

        test_xml = """<?xml version="1.0" ?><scriptConfiguration version="1.0"><key>com.hp.ic4vc.test</key><supportNonSecureCommunication>true</supportNonSecureCommunication><description>Test Plugin Config</description><name>Test</name><vendor>HP</vendor><multiVCsupported>true</multiVCsupported><extension parent="Home.Inventory" privilege="Host.Config.Connection, Host.Config.Storage" treestyles="Host.Config.Connection, Host.Config.Storage"><title locale="en">Test Plugin</title><title locale="es">Prueba</title><url display="window">http://www.hp.com/ic4vc/plugin/test1.html</url><icon>http://hp.com/icon.png</icon><iconSmaller>hppp://hp.com/icon_smaller.png</iconSmaller><customAttribute name="A1">A1</customAttribute><customAttribute name="A2">A2</customAttribute></extension><extension parent="Home.Inventory"><title locale="en">Test 1</title><url>http://www.hp.com/ic4vc/plugin/test1.html</url><extension parent="Home.Inventory"><title locale="en">Test 1-1</title><url>http://www.hp.com/ic4vc/plugin/test1-1.html</url></extension></extension></scriptConfiguration>"""
        
        self.assertEqual(test_xml, p.toxml() )        
        
if __name__ == '__main__':
    unittest.main()
    
    
    