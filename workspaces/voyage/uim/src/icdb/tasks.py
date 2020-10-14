#
# tasks.py
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
#    Class for adding and querying tasks in a PostgreSQL database
#
#####################################################

import psycopg2
from time import time
from datetime import datetime
import vmware
import pickle

from logging import getLogger
log = getLogger(__name__)

class Tasks:
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

    def remove(self, num_of_days_old):
        log.debug("Removing task records older than %s days", num_of_days_old)
        try:
            cur = self._db.cursor()
            nodots =  time() - (int(num_of_days_old) * 60 * 60 * 24)            
            cur.execute("delete from tasks where starttime < %s", (nodots,))
            self._db.commit()                
            cur.close()
        except:
            log.exception("Error removing task records older than %s days", num_of_days_old)                        
            raise
        
    def add_task(self, vc_uuid, objectid, taskname, starttime, status, username, taskdetails, tasknameargs=[], taskdetailargs=[], completedtime=0.0 ) :
    
        cur = self._db.cursor()
        
        if not completedtime :
            completedtime = 0.0
        
        cur.execute("insert into tasks "
                        + "(vc_uuid, objectid, username, starttime, completedtime, status, taskname, tasknameargs, taskdetails, taskdetailargs) "
                        + "values (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s ) returning id"
                        , (vc_uuid, objectid, username, starttime, completedtime, status, taskname, pickle.dumps(tasknameargs), taskdetails, pickle.dumps(taskdetailargs)))
        id = cur.fetchone()[0]
        self._db.commit()
        cur.close()
        return id
    
    def get_object_tasks(self, vc_uuid, objectid, limit=0) :
        """Get all the task entries for a given object."""
        cur = self._db.cursor()
        d = {'vc_uuid':vc_uuid, 'objectid':objectid}
        
        log.debug('get_tasks %s %s limit %d', vc_uuid, objectid, limit)
                
        q = "select id, vc_uuid, objectid, starttime, completedtime, username, taskname, tasknameargs, taskdetails, taskdetailargs, status from tasks where vc_uuid=%(vc_uuid)s and objectid=%(objectid)s order by starttime desc, id desc"
        if limit :
            q += (' limit %d' % limit)
        cur.execute(q, d)
            
        rows = cur.fetchall()
        orows = []
        for row in rows :
            orow = {}
            orow['_id'] = row[0]            # id
            #orow['vc_uuid'] = row[1]        # vc_uuid - vCenter's guuid
            #orow['vc_id'] = row[2]          # objectid - object's id like 'HostSystem:host-96'
            orow['taskName'] = row[6]           
            orow['taskNameArguments'] = pickle.loads(row[7] or pickle.dumps(None))  
            orow['startTime'] = row[3]   
            orow['completedTime'] = row[4] 
            orow['status'] = row[10]
            orow['userName'] = row[5]
            orow['taskDetails'] = row[8]
            orow['taskDetailArguments'] = pickle.loads(row[9] or pickle.dumps(None))  
            orow['formattedStartTime'] = ''
            orow['formattedCompletedTime'] = ''
            
            try:
                if row[3] :
                    dt = datetime.fromtimestamp(row[3])
                    orow['formattedStartTime'] = dt.strftime("%Y-%m-%d %H:%M:%S")   
                    log.debug("start time dispalyed for tasks is  : %s",orow['formattedStartTime'])
            except:
                log.exception("Unabled to format task start time '%s' in task %s", row[3], row[0])
                orow['formattedStartTime'] = ''
            
            try:
                if row[4] :
                    dt = datetime.fromtimestamp(row[4])
                    orow['formattedCompletedTime'] = dt.strftime("%Y-%m-%d %H:%M:%S")   
                    log.debug("completed time dispalyed for tasks is : %s",orow['formattedCompletedTime'])
            except:
                log.exception("Unabled to format task completed time '%s' in task %s", row[4], row[0])
                orow['formattedCompletedTime'] = ''
            
            orows.append(orow)
        
        cur.close()
        log.debug("In tasks returned rows are : %s",orows)
        return orows

    def get_cluster_tasks(self, vc_uuid, objectid, limit=0) :
        vcenter = vmware.get_vc_instance(uuid = vc_uuid)
        if not vcenter :
            log.error("Tasks Invalid vCenter ID %s", vc_uuid)
            return self._get_error(ERR_INVALID_VC_ID)
        hosts = vcenter.get_cluster_hosts(objectid)
        log.debug("number of hosts %s", len(hosts))
        
        rows = []
        
        cluster = vcenter.get_cluster(objectid)
        for task in self.get_object_tasks(vc_uuid, objectid) :
            task['name'] = cluster.name
            rows.append(task)
        
        for host in hosts:
            host_id = host['moref']._type + ":" + host['moref'].value
            log.debug("Querying tasks find objectid %s, vc_uuid %s, limit %s", host_id, vc_uuid, limit)
            for task in self.get_object_tasks(vc_uuid, host_id, limit) :
                task['name'] = host['name']
                rows.append(task)
            
        rows.sort(lambda a, b : cmp(b['startTime'], a['startTime']))    
        if limit :
            return rows[:limit]
        return rows
        
    def get_tasks(self, vc_uuid, objectid, limit=0) :
        rows = []
        if objectid.find('ClusterComputeResource') == 0 :
            rows = self.get_cluster_tasks(vc_uuid, objectid, limit)
        else :
            rows = self.get_object_tasks(vc_uuid, objectid, limit)
            
        return rows
        
    
    def update_task(self, id, taskname, status, taskdetails, tasknameargs=[], taskdetailargs=[], completedtime=0.0 ) :
        cur = self._db.cursor()
        
        if not completedtime :
            completedtime = 0.0
        
        cur.execute("update tasks set taskname=%s, status=%s, taskdetails=%s, tasknameargs=%s, taskdetailargs=%s, completedtime=%s where id=%s", 
            (taskname, status, taskdetails, pickle.dumps(tasknameargs), pickle.dumps(taskdetailargs), completedtime, id))
            
        self._db.commit()
        cur.close()
        
        return {"errorCode": 0, "errorMessage":"The task was added successfully."}
        
        
    def remove(self, num_of_days_old):
        log.debug("Removing tasks older than %s days", num_of_days_old)
        try:
            cur = self._db.cursor()
            nodots =  time() - (int(num_of_days_old) * 60 * 60 * 24)            
            cur.execute("delete from tasks where starttime < %s", (nodots,))
            self._db.commit()            
            cur.close()
        except:
            log.exception("Error removing tasks older than %s days", num_of_days_old)              
            raise                      
            
if __name__ == '__main__' :    
    import logging    
    log.setLevel(logging.DEBUG)

    ch = logging.StreamHandler()
    ch.setLevel(logging.DEBUG)
    log.addHandler(ch)
    
    t = Tasks()
    t.dbconnect(user='ic4vc', password='password')      

    print t.add_task('1234','HOST-123', 'TaskName', time(), "STATUS", 'username', 'taskdetails', ['taskargs',], ['taskdetailargs',] )     
    id = t.add_task('1234', 'HOST-123', 'TaskName', time(), "STATUS", 'username', 'taskdetails', ['taskargs',], ['taskdetailargs',] )     
    print id
    
    t.update_task(id, 'TaskName', 'COMPLETED', 'taskdetails', ['taskargs',], ['taskdetailargs',], time())
    print
    
    tasks = t.get_tasks(vc_uuid = '1234', objectid='HOST-123')
    for task in tasks :
        print task['formattedStartTime'], task['status'], task['formattedCompletedTime']
        
    t.remove(0)
    