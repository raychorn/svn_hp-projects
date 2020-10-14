#
# newsfeed.py
#
# Copyright 2011-2012 Hewlett-Packard Development Company, L.P.
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
#    Class for adding and querying newsfeed events in a PostgreSQL database
#
#####################################################

import psycopg2
from time import time
from datetime import datetime
import vmware
import pickle

from logging import getLogger
log = getLogger(__name__)
'''
Newsfeed messages have a level indications. Info, warning, error, ok. 

'''
class Newsfeed:
    def __init__(self) :
        self._db = None    
        
    def dbconnect(self, user, password, database='ic4vc', host='localhost', port=5432) :        
        try :
            self._db = psycopg2.connect(database=database, host=host, user=user, password=password, port=port)
        except :
            log.exception("Error connecting to database")
            raise
    
    def dbclose(self) :
        self._db.close()
    
    def add_object_event(self, vc_uuid, objectid, objectname, timestamp, pluginsource, eventsource, status, message, messagearguments=[]) :
        """Add a newsfeed item to the database."""
        cur = self._db.cursor()
        log.debug("the status message in news feed is : %s \n%s",status,message)
        d = {}        
        d['vc_uuid'] =  vc_uuid
        d['objectid'] = objectid
        d['objectname'] = objectname
        d['timestamp'] = timestamp
        d['pluginsource'] = pluginsource
        d['eventsource'] = eventsource
        d['status'] = status
        d['message'] = message
        d['messagearguments'] = pickle.dumps(messagearguments)
        
        log.debug('Add Newsfeed %s %s %s %s', vc_uuid, objectid, objectname, message)
        
        try :
            cur.execute("insert into newsfeed "
                        + "(vc_uuid, objectid, objectname, timestamp, pluginsource, eventsource, status, message, messagearguments) "
                        + "values (%(vc_uuid)s,%(objectid)s,%(objectname)s,%(timestamp)s,%(pluginsource)s,%(eventsource)s,%(status)s,%(message)s,%(messagearguments)s)"
                        , d)
            self._db.commit()
            cur.close()
        except :
            log.exception("Error adding event for %s %s to newsfeed", vc_id, obj_id)            
            raise
                
    
    def get_object_events(self, vc_uuid, objectid, limit=0) :
        """Get all the newsfeed entries for a given object."""
        cur = self._db.cursor()
        d = {'vc_uuid':vc_uuid, 'objectid':objectid}
        
        log.debug('get_host %s %s limit %d', vc_uuid, objectid, limit)
                
        q = "select id, vc_uuid, objectid, timestamp, pluginsource, eventsource, status, message, messagearguments, objectname from newsfeed where vc_uuid=%(vc_uuid)s and objectid=%(objectid)s order by timestamp desc, id desc"
        if limit :
            q += (' limit %d' % limit)
        cur.execute(q, d)
            
        rows = cur.fetchall()
        #log.debug("all fetched rows are : %s" ,rows)
        orows = []
        for row in rows :
            orow = {}
            orow['_id'] = row[0]                    # id
            orow['vc_uuid'] = row[1]                # vc_uuid - vCenter's guuid
            orow['vc_id'] = row[2]                  # objectid - object's id like 'HostSystem:host-96'
            orow['objectName'] = row[9] or row[5]   # objectname - The eventSource's name
            orow['eventDate'] = row[3]              # timestamp - Unix time stamp            
            orow['pluginSource'] = row[4]           # pluginsource - server or storage
            orow['eventSource'] = row[5]            # eventsource - who generated the event: iLO, OA, etc.
            orow['status'] = row[6]                 # status
            orow['message'] = row[7]                # message
            if row[8] :
                orow['messageArguments'] = pickle.loads(row[8])   # messageArguments
            
            try:
                dt = datetime.fromtimestamp(row[3])
                orow['formattedEventDate'] = dt.strftime("%Y-%m-%d %H:%M:%S")
                log.debug("time dispalyed for news feed is : %s",orow['formattedEventDate'])
            except:
                log.exception("Unabled to format event time stamp %s", row[2])
                orow['formattedEventDate'] = ''
                        
            
            orows.append(orow)
        
        cur.close()
        log.debug("all returned rows are : %s",orows)
        return orows
            
    def get_cluster_events(self, vc_uuid, objectid, limit=0) :
        vcenter = vmware.get_vc_instance(uuid = vc_uuid)
        if not vcenter :
            log.error("Newsfeed Invalid vCenter ID %s", vc_uuid)
            return self._get_error(ERR_INVALID_VC_ID)
        hosts = vcenter.get_cluster_hosts(objectid)
        log.debug("number of hosts %s", len(hosts))
        
        rows = []
        cluster = vcenter.get_cluster(objectid)
        for event in self.get_object_events(vc_uuid, objectid) :
            event['name'] = cluster.name
            rows.append(event)
        
        for host in hosts:
            host_id = host['moref']._type + ":" + host['moref'].value
            log.debug("Querying newsfeed find objectid %s, vc_uuid %s, limit %s", host_id, vc_uuid, limit)
            for event in self.get_object_events(vc_uuid, host_id) :
                event['name'] = host['name']
                rows.append(event)
            
        rows.sort(lambda a, b : cmp(b['eventDate'], a['eventDate']))    
        if limit :
            return rows[:limit]
        return rows

    def get_events(self, vc_uuid, objectid, limit=0) :
        rows = []
        if objectid.find('ClusterComputeResource') == 0 :
            rows = self.get_cluster_events(vc_uuid, objectid, limit)
        else :
            rows = self.get_object_events(vc_uuid, objectid, limit)
        log.debug("returned rows are  : %s ",rows)
        return rows
    
    def remove(self, num_of_days_old):
        log.debug("Removing newsfeed records older than %s days", num_of_days_old)
        try:
            cur = self._db.cursor()
            nodots =  time() - (int(num_of_days_old) * 60 * 60 * 24)            
            cur.execute("delete from newsfeed where timestamp < %s", (nodots,))
            self._db.commit()                
            cur.close()
        except:
            log.exception("Error removing newsfeed records older than %s days", num_of_days_old)                        
            raise        
            
if __name__ == '__main__' :    
    import logging    
    log.setLevel(logging.DEBUG)

    ch = logging.StreamHandler()
    ch.setLevel(logging.DEBUG)
    log.addHandler(ch)
    
    nf = Newsfeed()
    nf.dbconnect(user='postgres', password=None)
    
    nf.remove(0)
    nf.add_object_event("1234", "HostSystem:host-1", '1.2.3.4', time(), "test_plugin", "test_source", "TEST", "This is a test", [1,2,3])
    nf.add_object_event("1234", "HostSystem:host-1", '1.2.3.4', time(), "test_plugin", "test_source", "TEST", "This is a test")
    nf.add_object_event("1234", "HostSystem:host-2", '1.2.3.5', time(), "test_plugin", "test_source", "TEST", "This is a test")
    nf.add_object_event("1234", "HostSystem:host-2", '1.2.3.5', time(), "test_plugin", "test_source", "TEST", "This is a test")
    rows = nf.get_events("1234", "HostSystem:host-1")
    print rows
    print '---------'
    rows = nf.get_events('1234', "HostSystem:host-1", 1)
    print rows
    print '---------'
    rows = nf.get_events('1234','ClusterComputeResource:1')
    print rows