#####################################################
#
# smartcomponent.py
#
# Copyright 2007 Hewlett-Packard Development Company, L.P.
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
#    James Abendroth
# 
# Description:
#    Manages Smart Component objects for Breckenridge.
#
#####################################################

import os
import tarfile
import tempfile
from xml.dom.minidom import parse
from threading import Thread
import logging
log = logging.getLogger(__name__)

SC_SHARE_PATH='static/sc_share'

class SmartComponent:
    def __init__(self):
        self.version = "Unknown"
        self.name = "Unknown Component"
        self.filename = ""

    def __repr__(self):
        return str(self.__dict__)

    def json(self):
        return json.dumps(self.__dict__)

class SmartComponentManager:

    def __init__(self):
        self.components = []
        self.path = os.path.abspath(SC_SHARE_PATH)
        log.debug("Initializing SmartComponentManager")

    def get_sc_file_list(self):
        for root, directories, files in os.walk(self.path):
            return files

    def delete_component(self, sc_filename):
        sc_path = self.path + '\\' + sc_filename
        try:
            os.remove(sc_path)
            for component in self.components:
                if component.filename == sc_filename:
                    self.components.remove(component)
        except:
            pass

    def discover_components(self):
        t = Thread(target=self.do_discover_components)
        t.daemon = True
        t.start()

    def do_discover_components(self):
        files = self.get_sc_file_list()
        if files:
            for file in files:
                if file.lower().endswith('.scexe') :
                    log.debug("Discovered component %s", file)
                    self.add_component(file)

    def add_component(self, filename):

        log.debug("Adding component %s", filename)
        if filename in [c.filename for c in self.components ] :        
            log.error("Component %s already added", filename)
            return False

        component = SmartComponent()
        component.filename = filename

        digest_filename = self.extract_component_digest(filename)
        if digest_filename != "":
            self.parse_digest(digest_filename, component)
            # Remove the digest file.
            try:
                os.remove(self.path + '\\' + digest_filename)
            except:
                log.exception("Error removing digest file %s", digest_filename)
                pass
        self.components.append(component)

    # Each smart component should have an XML file inside. We'll use
    # tar to extract that file and obtain information about the component.
    # This function will return the filename of the xml file after it
    # has been extracted from the archive.
    def extract_component_digest(self, sc_filename):

        # The first step is to find the line where the tarfile starts.
        sc_path = self.path + '\\' + sc_filename
        sc_file = open(sc_path, "rb")

        line_offset = -1
        while True:
            buf = sc_file.read(1024)
            if buf == "":
                break

            skip_idx = buf.find("_SKIP=")
            if (skip_idx != -1):
                buf = buf[(skip_idx+6):]
                newline = buf.find('\n')
                line_offset = buf[0:newline]
                break        
                
        # We didn't find the line offset. We can't go any further.
        if line_offset == -1:
            sc_file.close()
            log.error("Unable to find tar offset in %s", sc_filename)
            return ""
        
        line_offset = int(line_offset)-1
        sc_file.seek(0)

        log.debug("tar file in %s at offset %d", sc_filename, line_offset)
        
        # Now that we have our line offset, move to the correct position in the file.
        for line in range(0, line_offset):
            sc_file.readline()

        tar_file = tempfile.mkstemp(dir=self.path)

        while True:
            buf = sc_file.read(1024)
            if buf == "":
                break

            os.write(tar_file[0], buf)

        sc_file.close()
        os.close(tar_file[0])

        # Now we should have a good tarfile to extract the digest.
        digest_name = ""

        try:
            tf = tarfile.open(tar_file[1], 'r')            
            for filename in tf.getnames() :
                if filename.lower() ==  sc_filename.lower().replace('.scexe', '.xml') :           
                    digest_name = filename
                    log.debug("Extracting %s from tar", digest_name)
                    tf.extract(digest_name, self.path)
                    tf.close()
                    os.remove(tar_file[1])
                    break
        except:
            log.exception("Error getting XML file name from %s", sc_filename)
            os.remove(tar_file[1])
            tf.close()

        if not digest_name :
            log.error("Unable to find component xml file in %s", sc_filename)    
            
        return digest_name

    # Parses the digest file and adds the required attributes to the SmartComponent object.
    def parse_digest(self, filename, component):
        log.debug("Parsing %s", filename)
        digest_dom = None
        try:
            digest_dom = parse(self.path + '\\' + filename)
        except:
            log.exception("Error parsing file %s",filename)
            return None

        package_elem = digest_dom.getElementsByTagName('cpq_package')
        if not package_elem:
            log.error("No cpq_package found")
            return None

        package_elem = package_elem[0]
        version_elem = None
        name_elem = None

        for node in package_elem.childNodes:
            if node.nodeName == 'version':
                version_elem = node
            elif node.nodeName == 'name':
                name_elem = node

        # Add the version from the version element's value attribute.
        if version_elem:
            component.version = version_elem.getAttribute('value')
            log.debug("%s component version: %s", component.filename, component.version)
        else :
            log.error("Error: no component version in %s", filename)

        if name_elem:
            for node in name_elem.childNodes:
                if node.nodeName == "name_xlate" and node.getAttribute("lang") == "en":
                    for cn in node.childNodes:
                        component.name = cn.wholeText
                        log.debug("%s component name: %s", component.filename, component.name)
        else :
            log.error("Error: no component name in %s", filename)

    def json(self):
        json_list = []
        for component in self.components:
            json_list.append(component.__dict__)
        return json_list

# The main smart component manager object.
__scm = SmartComponentManager()

def get_scm():
    return __scm
