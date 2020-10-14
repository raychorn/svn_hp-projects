def __timestamp__(format='%Y-%m-%dT%H:%M:%S'):
    """ get number of seconds """
    import time
    return time.strftime(format, time.gmtime(time.time()))

def __grab_data_if_being_debugged__(__data__):
    import os, sys
    import json
    import time
    from engines.ic4vc_webserver import SudsEncoder

    __isBeingDebugged__ = lambda environ:0 if (not environ.has_key('WINGDB_ACTIVE')) else int(environ['WINGDB_ACTIVE'])
    isBeingDebugged = __isBeingDebugged__(os.environ) == 1
    if (isBeingDebugged):
	tstamp = __timestamp__()
	__json__ = json.dumps(__data__,cls=SudsEncoder)
	fpath = os.path.dirname(sys.argv[0])
	toks = fpath.replace(os.sep,'/').split('/')
	fp = '/'.join(toks[0:2])
	fname = '/'.join([fp,os.path.splitext(os.path.basename(__name__))[0]+'_'+tstamp.replace(":",'-')+'.json'])
	fOut = open(fname,'w')
	fOut.write(__json__)
	fOut.flush()
	fOut.close()
__grab_data_if_being_debugged__(data)
