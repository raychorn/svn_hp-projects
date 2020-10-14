from datetime import datetime, timedelta
import psycopg2
from psycopg2.extras import *
from M2Crypto import EVP
#import dbutils 
from threading import Timer
from stringcrypt import str_decrypt
from logging import getLogger
log = getLogger(__name__)

from util import scheduler

_dict_taskid_timer = {}
_conn = None


#(identifier,vcguid, host_uuid, baseline_uri, restart, enter_maintenance_mode, exit_maintenance_mode , task_info_id): 
def handle_firware_task(d):
    
    temp = d.get('rec',{})
    now = datetime.utcnow()
    tempTime = temp['scheduledtime']
    timedelta = tempTime - now
    if timedelta.total_seconds() >0 or timedelta.total_seconds() > -300:
               
        li =[]
        
        li.append(d['id'])
        li.append(temp['vcguid'])
        li.append(temp['host_uuid'])
        li.append(temp['baseline_uri'])
        li.append(temp['restart'])
        li.append(temp['enter_maintenance_mode'])
        li.append(temp['exit_maintenance_mode'])
        li.append(temp['task_info_id'])    
        
        d['*args'] = li
        
        d['task'] = _TASK_TABLE['firmware_jobs']['task_run_handler']
    else:
        d['task'] = None
        update_task_status(d['id'],'Cannot Start')
        
    return d

__is_debugging__ = os.environ.get('WINGDB_ACTIVE',False)

__scheduler__ = scheduler.Scheduler(select_stmt="SELECT scheduled_tasks.*, firmware_jobs.* FROM scheduled_tasks INNER JOIN firmware_jobs ON scheduled_tasks.id = firmware_jobs.id WHERE scheduled_tasks.status = 'scheduled'", 
                         interval=5 if (__is_debugging__) else 60, callback=handle_firware_task, usingLocalTime=False)

def _tasks_table_check():
    '''
    Creates tables if they do not already exist. 
    Needs to be run once before the module can be used. 
    Only called from @see init
    '''
    global _conn
    cur = _conn.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS scheduled_tasks (id serial NOT NULL,
              taskname character varying(256) NOT NULL,
              scheduledtime timestamp without time zone NOT NULL,
              status character varying(64) NOT NULL,
              creationtime timestamp without time zone,
              statusmessage character varying(4096) DEFAULT NULL::character varying,
              CONSTRAINT scheduled_tasks_pkey PRIMARY KEY (id)
            )
            WITH (
              OIDS=FALSE
            )""")
    for task in _TASK_TABLE:
        _TASK_TABLE[task]['db_handler'](cursor = cur, op = 'create')
    _conn.commit()
    cur.close()

def _dummy_table_ops(cursor, op, identifier = None, **kwargs):
    '''
    A dummy table operations handler. Used for concept testing only.
    IGNORE.
    '''
    if op == 'get':
        rec = cursor.execute("""SELECT scheduled_tasks.*, dummy_table.arg 
                 FROM scheduled_tasks INNER JOIN dummy_table ON 
                 scheduled_tasks.id = dummy_table.id""")
        return rec
    elif op == 'put':
        assert(identifier)
        ret = cursor.execute('INSERT INTO dummy_table (id, arg)  VALUES (%s, %s) RETURNING dummy_id',
                             (identifier, str(kwargs['ar2g'])))
        ret_id =  cursor.fetchone()[0]
    elif op == 'create':
        create_stmt = """CREATE TABLE IF NOT EXISTS dummy_table  
                    ( arg character varying, id serial NOT NULL,
                    dummy_id serial NOT NULL,
                    CONSTRAINT pkey_dummy PRIMARY KEY (dummy_id),
                    CONSTRAINT id_fkey FOREIGN KEY (id)
                        REFERENCES scheduled_tasks (id) MATCH SIMPLE
                        ON UPDATE NO ACTION ON DELETE NO ACTION
                    ) WITH ( OIDS=FALSE)"""
        cursor.execute(create_stmt )

def _firmware_jobs_table_ops(cursor, op, identifier = None, **kwargs):
    '''
    DB Handler for firmare jobs. Adds/deletes/queries parameters specific to 
    firmware update jobs.
    @param cursor: A valid, open psycopg2 cursor object. 
                   To be used for all DB operations. 
    @param op: string. Must be one of 'get', 'put' or 'create'.
                For 'get', queries and returns all firmware jobs in DB that 
                are "yet to run " as measured from the time this method is 
                called. Cancelled tasks do not show up in the query
                For 'put', inserts into database the parameters relevant to
                scheduling firmware jobs. See kwargs
    @param identifier: An identifier returned from entering a scheduling job
                into the the common scheduling table. Required for 'put'
                operation only.  @see add_new_task
    @param kwargs:required for a 'put'. Otherwise ignored.
                  The named parameters expected are 
                  1. host_uuid - case insensitive string UUID representing 
                     the host for which the firware update job is to be 
                     scheduled. It must match the host UUID entry in the 
                     vCenter inventory of hosts.
                  2. baseline_uri - The URI of the firmware baseline. This is 
                     the firmware that the host will be upgraded to
                  3. restart - boolean value representing whether host needs 
                     to be restarted after applying the firmware update
                  4. enter_maintenance_mode - boolean value representing 
                     whether host needs to be put into maintenance mode before 
                     applying the firmware update.
                  5. exit_maintenance_mode - boolean value representing 
                     whether host needs to be brought out of maintenance mode 
                     after applying the firmware update. Should be False if
                     enter_maintenance_mode if False
    '''
    if op == 'get':
        print kwargs
        global _conn
        now = datetime.utcnow()
        recs = cursor.execute("""SELECT scheduled_tasks.*, firmware_jobs.* 
                 FROM scheduled_tasks INNER JOIN firmware_jobs ON 
                 scheduled_tasks.id = firmware_jobs.id WHERE 
                 firmware_jobs.host_uuid = %s AND extract(epoch from(scheduled_tasks.scheduledtime - current_timestamp at time zone 'UTC'))>0 AND scheduled_tasks.status = %s""", (str(kwargs['host_uuid']), 'scheduled'))
        #recs = cursor.execute("""SELECT scheduled_tasks.*, firmware_jobs.* 
        #         FROM scheduled_tasks INNER JOIN firmware_jobs ON 
        #         scheduled_tasks.id = firmware_jobs.id WHERE 
        #         firmware_jobs.host_uuid = %s AND scheduled_tasks.scheduledtime >= %s AND scheduled_tasks.status = %s""", (str(kwargs['host_uuid']), now.isoformat()  + ' z','scheduled'))




        return cursor
    elif op == 'put':
        assert(identifier)
        ret = cursor.execute("""INSERT INTO firmware_jobs (id, 
                host_uuid, baseline_uri, restart, enter_maintenance_mode, 
                exit_maintenance_mode,vcguid,task_info_id)  
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s) RETURNING firmware_job_id""",
                                                                                    (identifier, str(kwargs['host_uuid']), 
                                                                                     str(kwargs['baseline_uri']), (kwargs['restart']), 
                                                                                     kwargs['enter_maintenance_mode'], 
                                                                                     kwargs['exit_maintenance_mode'], 
                                                                                     kwargs['vcguid'],
                                                                                     kwargs['task_info_id']))
        ret_id =  cursor.fetchone()[0]
    elif op == 'create':
        create_stmt = """CREATE TABLE IF NOT EXISTS firmware_jobs
        (firmware_job_id serial NOT NULL,
          id serial NOT NULL,
          host_uuid character varying NOT NULL,
          baseline_uri character varying NOT NULL,
          enter_maintenance_mode boolean,
          exit_maintenance_mode boolean,
          restart boolean,
          vcguid character varying NOT NULL,
          task_info_id smallint,
          CONSTRAINT firmware_jobs_pkey PRIMARY KEY (firmware_job_id),
          CONSTRAINT firmware_jobs_id_fkey FOREIGN KEY (id)
              REFERENCES scheduled_tasks (id) MATCH SIMPLE
              ON UPDATE NO ACTION ON DELETE NO ACTION
        )
        WITH (
          OIDS=FALSE
        )"""
        cursor.execute(create_stmt)

    elif op == 'get2':
        print kwargs
        global _conn        
        recs = cursor.execute("""SELECT scheduled_tasks.*, firmware_jobs.* 
                 FROM scheduled_tasks INNER JOIN firmware_jobs ON 
                 scheduled_tasks.id = firmware_jobs.id WHERE 
                 firmware_jobs.host_uuid = %s AND scheduled_tasks.status = %s""", (str(kwargs['host_uuid']), 'Applying Baseline'))
        return cursor

# -----------------------
def _db_connect(user, password, host, database, port):
    '''
    Connect to scheduler database. A global call, needs to be done before 
    any other databse call.
    '''
    global _conn
    if not _conn:
        _conn = psycopg2.connect(database=str(database), host=str(host), 
                                 user=str(user), password=str(password), port=port)
    return _conn

def db_disconnect():
    '''
    Disconnects from scheduler DB
    '''
    global _conn
    if _conn:
        _conn.close()
        _conn = None

def db_clear():
    '''
    Deletes all entries in task scheduling tables in DB, w/o actually 
    dropping the tables themselves
    '''
    global _conn
    global _TASK_TABLE
    cur = _conn.cursor()
    for x in _TASK_TABLE:
        cur.execute("DELETE FROM "+ _TASK_TABLE[x]['table'])
    cur.execute("DELETE from scheduled_tasks ")
    try:
        _conn.commit()
    except:
        _conn.rollback()
    cur.close()

def db_drop():
    '''
    Drops all scheduling tables in DB.
    '''
    global _conn
    global _TASK_TABLE
    cur = _conn.cursor()
    for x in _TASK_TABLE:
        cur.execute("DROP TABLE IF EXISTS "+ _TASK_TABLE[x]['table'])
    cur.execute("DROP TABLE IF EXISTS scheduled_tasks ")
    try:
        _conn.commit()
    except:
        _conn.rollback()
    cur.close()

def add_task_type_and_handler(task_ptr, taskname, task_completion_handler =None):
    '''
    Used to register a task handler for a particulat type of task.
    Should be called ideally before #init method if there are jobs to be run 
    in the DB. 
    @param task_ptr: Reference to the function that will be called when the 
    scheduled job is to run. In essence, this IS the job
    @param taskname: The task type name eg:'firmware_jobs'
    @param task_completion_handler: The reference to the function to be 
    called after task_ptr finishes or an uncaught exception causes the 
    premature termination or even failure of the job.Typically, if the jobs 
    ends under expected conditions (could be either success or failure or 
    partial success) anything that task_ptr returns is passed on to 
    task_completion_handler. If there is an exception that task_ptr does not 
    handle, then the exception is passed on to task_completion_handler instead.
    '''
    global _TASK_TABLE
    assert(task_ptr)
    _TASK_TABLE[taskname]['task_run_handler'] = task_ptr
    _TASK_TABLE[taskname]['task_completion_handler'] = task_completion_handler

def get_task_handler(taskname):
    '''
    Used to return a reference to the function that will be called when the 
    scheduled job is to run. returns None if no handler had previously 
    been registered
    '''
    global _TASK_TABLE
    return _TASK_TABLE[taskname]['task_run_handler'] 

def get_task_completion_handler(taskname):
    '''
    Used to return a reference to the function that will be called when the 
    scheduled job is to run. returns None if no handler had previously 
    been registered
    '''
    global _TASK_TABLE
    return _TASK_TABLE[taskname]['task_completion_handler'] 

def _spawn_task_timer(identifier, task_ptr, sched_time, **kwargs):
    '''
    Spawns a task timer which will execute a supplied function at the scheduled time.
    @param identifier: The task identifier as retrieved from the 'id' field in
    the 'scheduled_tasks' field in DB
    '''
    now = datetime.utcnow()
    if now < sched_time:    
        t = Timer((sched_time - now).seconds, task_ptr, (identifier,), kwargs)
        t.start()
        _dict_taskid_timer[identifier] = t 
        return t
    else:
        raise Exception('Scheduled time needs to be in the future', sched_time, now)


def _rescan_db_for_tasks():
    now = datetime.utcnow()
    select_stmt = "SELECT * FROM scheduled_tasks"+\
        " WHERE scheduledtime > %s"
    cur = _conn.cursor(cursor_factory=psycopg2.extras.RealDictCursor)
    cur.execute(select_stmt, (now.isoformat()  + ' z', ))
    for rec in cur:
        #TODO
        if rec['id'] in _dict_taskid_timer:
            continue
        taskname = rec['taskname']
        sched_time = rec['scheduledtime']#datetime.strptime(rec['scheduledtime'], '%Y-%m-%d %H:%M:%S')
        task_ptr = get_task_handler(taskname)
        #print task_ptr
        #_spawn_task_timer(rec['id'], task_ptr, sched_time)#, args, kwargs)
    cur.close()

def add_new_task(taskname, sched_time, now_flag, **kwargs):
    '''
    Schedules a new task to be run, and adds the same to the DB.
    @param taskname: The task type name eg:'firmware_jobs'
    @param sched_time: A datetime object representing the date and time when 
    the job is to be run. Must be in the future or an AssertError will be thrown
    '''
    global _conn
    global _TASK_TABLE

    if datetime.utcnow() > sched_time : 
        #get the time difference and if it is less tahna 60 secs
        # and if now falg is set to true

        if now_flag!='false' or ((datetime.utcnow() - sched_time).seconds<300) :
            # add 10 sec to utc.now and insert into DB
            log.debug("we are in missed condition for now button")
            sched_time = datetime.utcnow()+ timedelta(seconds=10)
            log.debug("changed scheduled time fr now button is : %s",sched_time)
        else:
            assert(datetime.utcnow() < sched_time)



    #verify task handler exists
    task_ptr = get_task_handler(taskname)

    assert(_conn)
    assert(not _conn.closed)
    cur = _conn.cursor()
    try:
        insert_stmt = "INSERT INTO scheduled_tasks(" + \
            "taskname, scheduledtime, status, creationtime) " +\
            " VALUES (%s, %s, %s, %s) RETURNING id"
        ret = cur.execute(insert_stmt, (taskname, sched_time.isoformat() + ' z', 
                                        'scheduled', datetime.utcnow().isoformat() + ' z'))
        ret_id = cur.fetchone()[0]
        _TASK_TABLE[taskname]['db_handler'](cursor = cur, op = 'put', identifier = ret_id, **kwargs) 
        _conn.commit()
    finally:
        cur.close()
    #_spawn_task_timer(ret_id, task_ptr, sched_time, **kwargs)
    return ret_id

def _delete_task_from_db(identifier, taskname):
    '''
    Given an identifier of a scheduled, delete the task from DB
    @param identifier: The primary key 'id' in the scheduled_tasks table
    @param taskname: The task type name eg:'firmware_jobs'
    '''
    global _conn
    assert(_conn)
    assert(not _conn.closed)
    cur = _conn.cursor()
    try:
        cur.execute("DELETE FROM " + _TASK_TABLE[taskname]['table'] +" WHERE id = %s ",(identifier,))
        del_stmt1 = """DELETE FROM  scheduled_tasks WHERE id = %s """
        cur.execute(del_stmt1, (identifier,))
        ret = (cur.rowcount > 0)
        _conn.commit()
    finally:
        cur.close()
    return ret

def get_task_schedule_from_db(taskname, **kwargs):
    '''
    Given a taskname, returns all unfinished, scheduled tasks as the time 
    this method is called 
    i.e. tasks scheduled in the past are not returned.
    @param taskname: The task type name eg:'firmware_jobs'
    @param kwargs: variable named argument list specific to the task type. 
    Used for filtering results. eg: for task type 'firmware_jobs", the 
    expected named parameter is 'host_uuid' (for the UUID of the host for 
    which the scheduled firmware jobs in DB are to be queried).
    '''
    global _conn
    cursor = _conn.cursor(cursor_factory=psycopg2.extras.RealDictCursor)
    try:
        rec = _TASK_TABLE[taskname]['db_handler'](cursor = cursor, op = 'get', **kwargs)
        ret = []
        for r in rec:
            log.debug("In task schedule : %s ", r)
            ret.append(r)
        if len(ret) == 0:
            #make a call to get2
            rec2 = _TASK_TABLE[taskname]['db_handler'](cursor = cursor, op = 'get2', **kwargs)
            for r in rec2:
                log.debug("the result from get2 in task schedule : %s ", r)
                ret.append(r)
    finally:
        cursor.close()
    return ret

def cancel_pending_task(identifier, taskname):
    '''
    Unschedule a scheduled task and remove it from database.
    @param identifier: An integer which is the primary key of the 
    scheduled task entry in DB.
    @param taskname: The task type name eg:'firmware_jobs'
    '''
    #_dict_taskid_timer[identifier].cancel() 
    #del _dict_taskid_timer[identifier]
    ret = _delete_task_from_db(identifier = identifier, taskname = taskname)
    return  ret


def update_task_status(identifier,statusStr):
    '''
    This function update the database entry for identifier with given status
    @param identifier: An integer which is the primary key of the 
    scheduled task entry in DB.
    @param statusStr: The new status to be updated for the task 
    '''
    global _conn
    assert(_conn)
    assert(not _conn.closed)
    cur = _conn.cursor()
    try:
        #cur.execute("UPDATE scheduled_tasks SET status = %s WHERE id = %s ",(statusStr)(identifier,))
        update_stmt = """UPDATE scheduled_tasks SET status = %s WHERE id = %s """
        cur.execute(update_stmt, (statusStr,identifier))
        ret = (cur.rowcount > 0)
        _conn.commit()
    finally:
        cur.close()
    return ret



_TASK_TABLE = {'dummy': {'db_handler':_dummy_table_ops,
                         'table':'dummy_table',
                         'task_run_handler': None,
                         'task_completion_handler': None},
               'firmware_jobs':{'db_handler':_firmware_jobs_table_ops,
                                'table':'firmware_jobs',
                                'task_run_handler': None,
                                'task_completion_handler': None}
               }


def init(**kwargs):
    '''
    Must be called before the module can be used. 
    Needs to be called only once.
    Makes a connection to database and creates all necessary tables
    @param kwargs: Named parameters - must have one of two sets named parameters:
    1. config
    OR
    2. user, host, port, password, database
    Case 1: This is the util.config.config class object. 
    config.uimconfig['private_config']['db'] entry is a dictionary entry that 
    contains the necessary username/password/dbname/host/port entries to connect 
    to PostgreSQL instance.
    Case 2: In this case, the keyword arguments  user, host, port, password 
    are required they provide the username/host/port/password required to 
    connect to the PostgreSQL server. The database keyword is optional and 
    represents the PostgreSQL dbname to connect to. If left out, it defaults 
    to 'ic4vc'
    '''
    if 'config' in kwargs:
        log.debug("the kwargs values are : %s" , kwargs)
        cfg = kwargs['config']

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
    else:
        user = kwargs['user']
        port = kwargs['port']
        host = kwargs['host']
        password = kwargs['password']

    database = kwargs.get('database','ic4vc')
    _db_connect(user = user, password = password, host = host, 
                database = database, port = port)
    _tasks_table_check()
    _rescan_db_for_tasks()
    __scheduler__.run()
