from portlets.collector import Collector
from util import credential
from util.authclient import get_authclient
from engines import hponeview
from util.stringcrypt import str_decrypt
from vcm import VCM
from host import Host
from network import Network

from logging import getLogger
log = getLogger(__name__)

def inject(instance):
    '''
    Python method injection only if the method does not already exist.
    '''
    def decorator(f):
        import new
        f = new.instancemethod(f, instance, instance.__class__)
        try:
            value = getattr(instance, f.func_name)
        except AttributeError:
            setattr(instance, f.func_name, f)
        return f
    return decorator

def get_ip_address_from(host_or_ip):
    import socket
    __n__ = host_or_ip.split('://')[-1].replace('/','')
    return socket.gethostbyname(__n__)

def get_data():
    data = {}
    dc = Collector.get_collector()

    host_uuid=dc.host.hardware.systemInfo.uuid
    ovClient = Host.findOVClient(host_uuid)
    if ovClient :
        
        vcm = VCM.hponeview_vcm(ovClient,host_uuid)
        datastores = Network.get_fc_datastores(dc, vcm)
        
        Network.check_4_bottle_necks(vcm)
        VCM.hponeview_telemetry(vcm,ovClient)
        
        data['vcm']=vcm
        data['datastores'] = datastores
        
        dvss = Network.get_dvss(dc,vcm)
        vss = Network.get_vss(dc,vcm)

        data['vss'] = vss
        data['dvss'] = dvss
        
    return data
