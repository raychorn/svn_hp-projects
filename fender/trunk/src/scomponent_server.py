#####################################################
#
# scomponent_server.py
#
# Copyright 2009 Hewlett-Packard Development Company, L.P.
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
#    Start a simple web server to serve smart component files.
#
#####################################################
import web
import web.wsgiserver
from web.net import htmlquote
import web.httpserver
from urlparse import urlparse
from logging import getLogger
log = getLogger(__name__)

# Subclass web.application to override the run() method.  Ultimately, this
# calls the same code as web.application.run(), but myapp.run() allows the
# listen address/port to be passed as an argument whereas web.application.run
# expects it to be in sys.argv[].
class myapp_basic(web.application):
    def run(self, server_address=('0.0.0.0', 8080), *middleware):
        return web.httpserver.runbasic(self.wsgifunc(*middleware), server_address)

def run_server():
   
    server_address = ('0.0.0.0', 63000)
    log.info('Starting sc_server on %s:%s',server_address[0], server_address[1])
    
    # No URLs are passed to the server, so it will only serve content
    # from /static.
    try:
        app = myapp_basic([], globals())
        app.run(server_address)
    except:
        pass

# vim: ts=4 sts=4 sw=4:
