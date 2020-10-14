'''
Created on Dec 22, 2011

@author: IslamM
'''
import imp, os, sys
   
class Environment:
    def __init__(self):
        self.old_working_dir = os.getcwd()

    def main_is_frozen(self):
        return (hasattr(sys, "frozen") or # new py2exe
               hasattr(sys, "importers") # old py2exe
               or imp.is_frozen("__main__")) # tools/freeze
    
    def get_main_dir(self):
        if self.main_is_frozen():
            return os.path.dirname(sys.executable)
        return os.path.dirname(sys.argv[0])
      
    def set_root_dir(self):
        if self.main_is_frozen():
            os.chdir(self.get_main_dir())
