'''
Created on Feb 27, 2012

@author: IslamM
'''
import web
from web.wsgiserver.ssl_builtin import BuiltinSSLAdapter
from logging import getLogger
log = getLogger(__name__)

web.config.debug = False

class UIMWEBApp(web.application):

    def run(self, certinfo, server_address=('0.0.0.0', 8080), timeout=900, rqs=10, nthreads=20):

        from web.wsgiserver import CherryPyWSGIServer
        from SimpleHTTPServer import SimpleHTTPRequestHandler
        from BaseHTTPServer import BaseHTTPRequestHandler
    
        class StaticApp(SimpleHTTPRequestHandler):
            """WSGI application for serving static files."""
            def __init__(self, environ, start_response):
                self.headers = []
                self.environ = environ
                self.start_response = start_response
    
            def send_response(self, status, msg=""):
                self.status = str(status) + " " + msg
    
            def send_header(self, name, value):
                self.headers.append((name, value))
    
            def end_headers(self):
                pass
    
            def log_message(self, *a): pass
    
            def __iter__(self):
                environ = self.environ
    
                self.path = environ.get('PATH_INFO', '')
                self.client_address = environ.get('REMOTE_ADDR','-'), \
                                      environ.get('REMOTE_PORT','-')
                self.command = environ.get('REQUEST_METHOD', '-')
    
                from cStringIO import StringIO
                self.wfile = StringIO() # for capturing error
    
                f = self.send_head()
                self.start_response(self.status, self.headers)
    
                if f:
                    block_size = 16 * 1024
                    while True:
                        buf = f.read(block_size)
                        if not buf:
                            break
                        yield buf
                    f.close()
                else:
                    value = self.wfile.getvalue()
                    yield value
                        
        class WSGIWrapper(BaseHTTPRequestHandler):
            """WSGI wrapper for logging the status and serving static files."""
            def __init__(self, app):
                self.app = app
                self.format = '%s - - [%s] "%s %s %s" - %s'
    
            def __call__(self, environ, start_response):
                def xstart_response(status, response_headers, *args):
                    write = start_response(status, response_headers, *args)
                    self.log(status, environ)
                    return write
    
                path = environ.get('PATH_INFO', '').lower()
                if path.startswith('/static/') and not ('..' in path or '%2f' in path or '%2e' in path or '%5c' in path) and not path.endswith('/'):
                    return StaticApp(environ, xstart_response)
                else:
                    return self.app(environ, xstart_response)
    
            def log(self, status, environ):
                req = environ.get('PATH_INFO', '_')
                protocol = environ.get('ACTUAL_SERVER_PROTOCOL', '-')
                method = environ.get('REQUEST_METHOD', '-')
                host = '%s:%s' % (environ.get('REMOTE_ADDR', '-'),
                                  environ.get('REMOTE_PORT', '-'))
        
                time = self.log_date_time_string()
                msg = self.format % (host, time, protocol, method, req, status)
                log.debug(msg)
        
        #wrapper for the CherryPyWSGIServer to enable certificates
        #force the server address so the port is available 
        func = WSGIWrapper(self.wsgifunc())
        self.server = CherryPyWSGIServer(server_address, func, timeout = timeout, request_queue_size=rqs, numthreads=nthreads)
        protocol = 'http'
        if certinfo:
            protocol = 'https'
            adapter = BuiltinSSLAdapter(certinfo['cert'], certinfo['key'])
            self.server.ssl_adapter = adapter
        self.server.thread_name = 'HPIC4VCUIM'
        log.info('%s://%s:%d/', protocol, server_address[0], server_address[1])
        try:
            self.server.start()
        except KeyboardInterrupt:
            log.info('Exception starting uim...')
            self.server.stop()
        log.info('Started uim...')
