'''
$Author: partho.bhowmick@hp.com $
$Date: 2013-05-13 08:42:32 -0500 (Mon, 13 May 2013) $
$Header: https://svn02.atlanta.hp.com/local/ic4vc-dev/server/trunk/src/util/testlogs.py 5376 2013-05-13 13:42:32Z partho.bhowmick@hp.com $
$Revision: 5376 $
'''

import logging

class testlogger:
    def __init__(self, name = None, filename= None, level = logging.DEBUG):
        if not name:
            name = __file__
        if not filename:
            filename = name + '.log'
        self.logger = logging.getLogger(name)
        self.name = name
        self.logger.setLevel(level)
        formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        fh = logging.FileHandler(filename)
        fh.setLevel(level)
        fh.setFormatter(formatter)
        self.logger.addHandler(fh)
        
    def log(self, msg, level = logging.DEBUG, *args, **kwargs):
        self.logger.log(level, msg, args, kwargs)

    def debug(self, msg, *args, **kwargs):
        self.logger.debug(level, msg, args, kwargs)

    def info(self, msg, *args, **kwargs):
        self.logger.info(level, msg, args, kwargs)

    def info(self, msg, *args, **kwargs):
        self.logger.info(level, msg, args, kwargs)

    
