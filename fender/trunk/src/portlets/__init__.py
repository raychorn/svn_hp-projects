import web

def mac_format(mac) :
    mac = mac.lower()
    mac = mac.replace('-',':')
    return mac

def stowwn(s) : 
    if s[:2] == '0x' :
        s = s[2:]
    if s[-1] == 'L' :
        s = s[:-1]    
    s = s[:16]
    s = ('0' * (16-len(s))) + s
    s = ':'.join(s[i:i+2] for i in range(0, len(s), 2)).lower()
    return s
    
def itowwn(i) :
    "Convert an integer to wwn string"
    s = hex(i)
    return stowwn(s)

def get_webinput():
    moref, serverGuid, sessionId = None, None, None
    try:
        q = web.input()
        moref = q.moref
        serverGuid = q.serverGuid
        sessionId = q.sessionId
    except:
        pass
    return moref, serverGuid, sessionId

