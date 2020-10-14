#
# dbutils.py
#
# Copyright 2013 Hewlett-Packard Development Company, L.P.
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
#    Functions to support applications specific database tasks
#
#####################################################

import psycopg2
import config
from utils.stringcrypt import str_decrypt
from M2Crypto.EVP import EVPError

from logging import getLogger
log = getLogger(__name__)

table_defs = [
    """CREATE TABLE IF NOT EXISTS newsfeed
(
  id serial NOT NULL,
  vc_uuid character varying(64),
  objectid character varying(64),
  pluginsource character varying(64),
  eventsource character varying(64),
  status character varying(64),
  message character varying(256),
  messagearguments character varying(256),
  "timestamp" double precision,
  objectname character varying(64),
  CONSTRAINT newsfeed_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

DO $$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_class c
    JOIN   pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'newsfeed_object_index'
    AND    n.nspname = 'public'
    ) THEN

    CREATE INDEX newsfeed_object_index
        ON newsfeed
        USING btree
        (vc_uuid COLLATE pg_catalog."default", objectid COLLATE pg_catalog."default");
END IF;

END$$;
""",

"""
CREATE TABLE IF NOT EXISTS tasks
(
  id serial NOT NULL,
  username character varying(64),
  starttime double precision,
  completedtime double precision,
  taskname character varying(256),
  tasknameargs character varying(256),
  taskdetails character varying(256),
  taskdetailargs character varying(256),
  status character varying(256),
  objectid character varying(64),
  vc_uuid character varying(64),
  CONSTRAINT tasks_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

DO $$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_class c
    JOIN   pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'tasks_object_index'
    AND    n.nspname = 'public'
    ) THEN

    CREATE INDEX tasks_object_index
        ON tasks
        USING btree
        (vc_uuid COLLATE pg_catalog."default", objectid COLLATE pg_catalog."default");
END IF;

END$$;
""",  
  
]

def create_db_tables(user, password, database='ic4vc', host='localhost', port=5432) :
    db = psycopg2.connect(database=database, host=host, user=user, password=password, port=port)
    cur = db.cursor()
    for q in table_defs :
        cur.execute(q)    
    
    db.commit()       
    cur.close()    
    db.close()
    
if __name__ == '__main__' :    
    create_tables(user='postgres', password=None)

def get_db_host() :
    return config.config['private_config']['db'].get('ip',None)
    
def get_db_password() :
    try :
        password = str_decrypt(config.config['private_config']['db']['password'])
    except EVPError :        
        log.warning("Assuming db password is not encrypted")
        password = config.config['private_config']['db']['password']
    except :
        password = config.config['private_config']['db']['password']
        log.exception("Error decrypting db password")
        log.warning("Assuming db password is not encrypted")
    
    return password
    
def get_db_username() :    
    return config.config['private_config'].get('db', {}).get('username', None)

def get_db_port() :    
    return config.config['private_config'].get('db', {}).get('port', 5432)    
    
    
    