'''
Created on Oct 4, 2011

@author: IslamM
'''
import getopt, sys
import logging

log = logging.getLogger(__name__)
sh = logging.StreamHandler()
sh.setLevel(logging.INFO)
log.addHandler(sh)

def vcenter():
    opts, args = getopt.getopt(sys.argv[1:], "t:u:p:", ['host=','username=','password=']) 
    log.setLevel(logging.INFO)
    target = ""
    username= ""
    password = ""
    
    for o, a in opts:
        if o in ("-t", "--target"):
            target = a
        elif o in ("-u", "--username"):
            username = a
        elif o in ("-p", "--password"):
            password = a
        else:
            assert False, "unhandled option"
    return target, username, password