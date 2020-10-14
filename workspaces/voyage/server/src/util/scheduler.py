'''
Created on Oct 16, 2013

@author: ray.c.horn@hp.com
'''
import os, sys
import time
import datetime
from datetime import timedelta

from M2Crypto import EVP
import psycopg2
from psycopg2.extras import RealDictCursor

from util.stringcrypt import str_decrypt

from logging import getLogger
log = getLogger(__name__)
        
from util.threadpool import ThreadQueue, threadify

__Q__ = ThreadQueue(maxsize=1)
__QQ__ = ThreadQueue(maxsize=100)

isUsingWindows = (sys.platform.lower().find('win') > -1) and (os.name.lower() == 'nt')
isNotUsingLocalTimeConversions = not isUsingWindows
isUsingLocalTimeConversions = not isNotUsingLocalTimeConversions

@threadify(__QQ__)
def job_proxy(task,*args,**kwargs):
    s = '''
try:
    task(%s)
except:
    log.exception('Scheduled task has an issue, someone should fix something.')
''' % (', '.join(['"%s"'%(n) if (isinstance(n,str)) else str(n) for n in args[0]]))
    try:
        exec(s,{'task':task},{})
    except:
        log.exception('Scheduled task has an issue, someone should fix something.')

class Scheduler():
    def __init__(self,select_stmt="SELECT * FROM scheduled_tasks WHERE scheduled_tasks.status = 'scheduled'",callback=None,interval=60,usingLocalTime=True):
        self.interval = interval
        self.select_stmt = select_stmt
        self.callback = callback
        self.usingLocalTime = usingLocalTime
        
    def interval():
        doc = "interval property"
        def fget(self):
            return self.interval
        def fset(self, interval):
            self.interval = interval
        return locals()
    interval = property(**interval())

    def select_stmt():
        doc = "select_stmt property"
        def fget(self):
            return self.select_stmt
        def fset(self, select_stmt):
            self.select_stmt = select_stmt
        return locals()
    select_stmt = property(**select_stmt())

    def callback():
        doc = "callback property"
        def fget(self):
            return self.callback
        def fset(self, callback):
            self.callback = callback
        return locals()
    callback = property(**callback())

    def usingLocalTime():
        doc = "usingLocalTime property"
        def fget(self):
            return self.usingLocalTime
        def fset(self, usingLocalTime):
            self.usingLocalTime = usingLocalTime
        return locals()
    usingLocalTime = property(**usingLocalTime())

    def get_jobs(self):
        jobs = []
        _conn = db_connect()
        now = datetime.datetime.utcnow()
        select_stmt = self.select_stmt
        cur = _conn.cursor(cursor_factory=RealDictCursor)
        cur.execute(select_stmt)
        for rec in cur:
            taskname = rec['taskname']
            sched_time = rec['scheduledtime']
            #if (not self.usingLocalTime):
                #sched_time = datetime.datetime.utcfromtimestamp(time.mktime(sched_time.timetuple()))
            d = {'id':rec['id'],'taskname':taskname,'time':sched_time,'task':None,'rec':rec,'*args':None,'**kwargs':None}
            if (callable(self.callback)):
                try:
                    self.callback(d)
                except:
                    log.exception('Something went wrong with the callback, programmer check your logic !!!')
            jobs.append(d)
        cur.close()
        db_disconnect(_conn)
        log.info('There are %s runnable jobs.' % (len(jobs)))
        return jobs

    def sort_jobs(self,jobs):
        for job in jobs:
            t = job.get('time',None)
            if (t):
                now = today_localtime()
                if (not self.usingLocalTime):
                    now = datetime.datetime.utcfromtimestamp(time.mktime(now.timetuple()))
                delta = t - now
                job['delta'] = delta.total_seconds()
        return sorted(jobs, key=lambda k: k['delta'])

    def execute_jobs(self,jobs):
        log.info('BEGIN:')
        for i in xrange(len(jobs)):
            job = jobs[i]
            log.info('job is %s' % (job))
            delta = job.get('delta',None)
            if (delta) and (delta <= 0):
                log.info('Execute job %s' % (job))
                task = job.get('task',None)
                if (callable(task)):
                    try:
                        job_proxy(task,job.get('*args',[]),job.get('**kwargs',{}))
                    except Exception, ex:
                        log.exception('Scheduler failed to execute %s.' % (job))
                        update_task_status(job.get('id',None), 'Exception')
                    finally:
                        update_task_status(job.get('id',None), 'Success')
        log.info('END !!!')

    @threadify(__Q__)
    def run(self):
        while (1):
            jobs = self.get_jobs()
            jobs = self.sort_jobs(jobs)
            jobs = self.execute_jobs(jobs)
            time.sleep(self.interval)
        
def db_connect(database='ic4vc'):
    from util.config import config
    cfg = config()

    log.debug("config value in the kwargs values are : %s" , cfg)
    try :
        password = str_decrypt(cfg.uimconfig['private_config']['db']['password'])
    except EVP.EVPError :        
        log.warning("Assuming db password is not encrypted")
        log.debug("The password1  here is  : %s",cfg.uimconfig['private_config']['db']['password'])
        password = cfg.uimconfig['private_config']['db']['password']
    except :
        log.debug("The password2 here is  : %s",cfg.uimconfig['private_config']['db']['password'])
        password = cfg.uimconfig['private_config']['db']['password']
        log.exception("Error decrypting db password")
        log.warning("Assuming db password is not encrypted")

    user = cfg.uimconfig['private_config'].get('db', {}).get('username', None)
    host = cfg.uimconfig['private_config']['db'].get('ip',None)
    port = cfg.uimconfig['private_config'].get('db', {}).get('port', 5432)

    return psycopg2.connect(database=str(database), host=str(host), user=str(user), password=str(password), port=port)

def db_disconnect(conn):
    '''
    Disconnects from scheduler DB
    '''
    try:
        conn.close()
    except:
        log.exception("Cannot close db connection.")

def update_task_status(identifier,statusStr):
    '''
    This function update the database entry for identifier with given status
    @param identifier: An integer which is the primary key of the 
    scheduled task entry in DB.
    @param statusStr: The new status to be updated for the task 
    '''
    _conn = db_connect()
    cur = _conn.cursor()
    try:
        #cur.execute("UPDATE scheduled_tasks SET status = %s WHERE id = %s ",(statusStr)(identifier,))
        update_stmt = """UPDATE scheduled_tasks SET status = %s WHERE id = %s """
        cur.execute(update_stmt, (statusStr,identifier))
        ret = (cur.rowcount > 0)
        _conn.commit()
    finally:
        cur.close()
    db_disconnect(_conn)
    return ret

def _formatTimeStr():
    return '%Y-%m-%dT%H:%M:%S'

def getAsDateTimeStr(value, offset=0,fmt=_formatTimeStr()):
    """ return time as 2004-01-10T00:13:50.000Z """
    import sys,time
    import types
    from datetime import datetime

    strTypes = (types.StringType, types.UnicodeType)
    numTypes = (types.LongType, types.FloatType, types.IntType)
    if (not isinstance(offset,strTypes)):
        if isinstance(value, (types.TupleType, time.struct_time)):
            return time.strftime(fmt, value)
        if isinstance(value, numTypes):
            secs = time.gmtime(value+offset)
            return time.strftime(fmt, secs)

        if isinstance(value, strTypes):
            try: 
                value = time.strptime(value, fmt)
                return time.strftime(fmt, value)
            except Exception, details: 
                _fmt = get_format_from(value)
                secs = time.gmtime(time.time()+offset)
                return time.strftime(fmt, secs)
        elif (isinstance(value,datetime)):
            from datetime import timedelta
            if (offset is not None):
                value += timedelta(offset)
            ts = time.strftime(fmt, value.timetuple())
            return ts

def getFromDateTimeStr(ts,format=_formatTimeStr()):
    from datetime import datetime
    try:
        return datetime.strptime(ts,format)
    except ValueError:
        return datetime.strptime('.'.join(ts.split('.')[0:-1]),format)

def _formatTimeStr():
    return '%Y-%m-%dT%H:%M:%S'

def getFromDateStr(ts,format=_formatTimeStr()):
    return getFromDateTimeStr(ts,format=format)

def formatSalesForceTimeStr():
    return '%Y-%m-%dT%H:%M:%S'

def timeSeconds(month=-1,day=-1,year=-1,format=formatSalesForceTimeStr()):
    """ get number of seconds """
    import time, datetime
    fromSecs = datetime.datetime.fromtimestamp(time.time())
    s = getAsDateTimeStr(fromSecs,fmt=format)
    _toks = s.split('T')
    toks = _toks[0].split('-')
    if (month > -1):
        toks[0] = '%02d' % (month)
    if (day > -1):
        toks[1] = '%02d' % (day)
    if (year > -1):
        toks[-1] = '%04d' % (year)
    _toks[0] = '-'.join(toks)
    s = 'T'.join(_toks)
    fromSecs = getFromDateStr(s,format=format)
    return time.mktime(fromSecs.timetuple())

def getFromNativeTimeStamp(ts,format=None):
    format = _formatTimeStr() if (format is None) else format
    if (':' not in ts):
        format = format.replace(':','')
    if ('T' not in ts):
        format = format.split('T')[0]
    return getFromDateTimeStr(ts,format=format)

def utcDelta():
    import datetime, time
    _uts = datetime.datetime.utcfromtimestamp(time.time())
    _ts = datetime.datetime.fromtimestamp(time.time())
    # This time conversion fails under Linux for some odd reason so let's just stick with UTC when this happens.
    _zero = datetime.timedelta(0)
    return _zero if (isNotUsingLocalTimeConversions) else (_uts - _ts if (_uts > _ts) else _ts - _uts)

def timeStamp(tsecs=0,format=_formatTimeStr(),useLocalTime=isUsingLocalTimeConversions):
    """ get standard timestamp """
    useLocalTime = isUsingLocalTimeConversions if (not isinstance(useLocalTime,bool)) else useLocalTime
    secs = 0 if (not useLocalTime) else -utcDelta().seconds
    tsecs = tsecs if (tsecs > 0) else timeSeconds()
    t = tsecs+secs
    return getAsDateTimeStr(t if (tsecs > abs(secs)) else tsecs,fmt=format)

def timeStampLocalTime(tsecs=0,format=_formatTimeStr()):
    """ get standard timestamp adjusted to local time """
    return timeStamp(tsecs=tsecs,format=format,useLocalTime=isUsingLocalTimeConversions)

def today_localtime(_timedelta=None,begin_at_midnight=False):
    dt = getFromNativeTimeStamp(timeStampLocalTime())
    begin_at_midnight = False if (not isinstance(begin_at_midnight,bool)) else begin_at_midnight
    if (begin_at_midnight):
        dt = strip_time_drom_datetime(dt)
    try:
        if (_timedelta is not None):
            return dt - _timedelta
    except Exception, details:
        info_string = formattedException(details=details)
        print >>sys.stderr, info_string
    return dt

if (__name__ == '__main__'):
    print today_localtime()
    select_stmt = "SELECT scheduled_tasks.*, firmware_jobs.* FROM scheduled_tasks INNER JOIN firmware_jobs ON scheduled_tasks.id = firmware_jobs.id WHERE scheduled_tasks.status = 'scheduled'"
    scheduler = Scheduler(select_stmt=select_stmt,interval=1)
    scheduler.run()
    __Q__.join()
