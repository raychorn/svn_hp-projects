'''
$Id: icsp.py 5294 2013-04-19 22:08:12Z ayates@hp.com $
$Author: ayates@hp.com $
$Revision: 5294 $
$Date: 2013-04-19 17:08:12 -0500 (Fri, 19 Apr 2013) $
$HeadURL: https://svn02.atlanta.hp.com/local/ic4vc-dev/server/trunk/src/core/icsp.py $
'''

import json
import httplib2
from time import  sleep

import logging
log = logging.getLogger(__name__)

NO_SERVER = -2
NO_JOB = -1
SUCCESS_JOB = 1
RUNNING_JOB = 2
UNKNOWN_ERR = -2

class ICSPJob:

    def __init__(self, baseUrl, certValidation = False):
        '''
        Send in the base url string to the Insight Control Server Provisioning server
        e.g. "https://10.10.15.245"
        @param baseUrl:  the Url to Insight Control Server Provisioning e.g. "https://10.10.15.245". Note

        '''
        self.baseUrl = baseUrl
        self.certValidation = certValidation
        self.headers = {'Accept':'application/json, text/javascript, */*; q=0.01', 'Content-Type':'application/json; charset=UTF-8',
                        'Content-Type':'application/json'}

    def _buildPostRest(self):
        http = httplib2.Http(timeout = 10,
                             disable_ssl_certificate_validation = not self.certValidation)
        return http

    def _buildGetRest(self):
        http = httplib2.Http(timeout = 10,
                             disable_ssl_certificate_validation = not self.certValidation)
        return http

    def _postJson(self, relUrl, json_body):
        http = self._buildPostRest()
        log.debug('ICSPJob POST ' + self.baseUrl + relUrl)
        return http.request(self.baseUrl + relUrl, 'POST', json_body,
                            headers = self.headers)

    def _getJson(self, relUrl):
        http = self._buildGetRest()
        log.debug('ICSPJob GET ' + self.baseUrl + relUrl)
        return http.request(self.baseUrl + relUrl, 'GET', body = None,
                            headers = self.headers)

    def _jobStatus(self, job_dict):
        progress = {}
        complete = {}
        jobServerInfo = {}
        for job in job_dict['jobServerInfo']:
            key = job['jobServerUri']
            del job['jobServerUri']
            jobServerInfo[key] = job
        
        if (job_dict['jobProgress']):
            for job in job_dict['jobProgress']:
                key = job['jobServerUri']
                del job['jobServerUri']
                progress[key] = job

        if (job_dict['jobResult']):
            for job in job_dict['jobResult']:
                key = job['jobServerUri']
                del job['jobServerUri']
                complete[key] = job

        return {'jobServerInfo':jobServerInfo, 'progress':progress,
                'complete':complete, 'status':job_dict['status']}

    def login(self, userName, password):
        # start test dummy
        # remove the following 2 lines for production
#        self.headers['auth'] = 'xyz'
#        return True
        # end end test dummy
        login_json = {"userName":userName, "password":password}
        json_body = json.dumps(login_json)
        resp, content = self._postJson('/rest/login-sessions', json_body)
        log.debug('ICSP login content: %s', content)
        log.debug('ICSP login response: %s',resp)
        if resp['status'] == '200':
            json_resp = json.loads(content)
            # self.sessionId = json_resp['sessionId']
            self.headers['auth'] = json_resp['sessionID']
            return True
        else:
            return False

    def pingJob(self, jobid, args = None):
            x = self._actual_ping_job(jobid)
#            x = self._test_ping_job_dummy(args)
            log.debug("ICSPJob Formatted job status %s", x)
            return x

    def _actual_ping_job(self, jobid):
        '''
        Look up status of job
        :param authCode: The authorization token returned by #login() to use to login
        :type authCode: string
        :param jobid: The job id returned by ICSP when the build job was posted to ICSP
        :type jobid: string
        Call login() to obtain the authorization code before calling this method
        '''
        log.debug('ICSPJob Getting job status for job %s',jobid)
        relUrl = '/rest/os-deployment-jobs/' + jobid        
        resp, content = self._getJson(relUrl)
        log.debug('ICSPJob _actual_ping_job response %s',resp)
        log.debug('ICSPJob _actual_ping_job content %s',content)
        if resp['status'] == '200' or resp['status'] == 200:
            job_dict = json.loads(content)
            log.debug("ICSPJob Got job status for job %s status: %s", jobid, job_dict)
            return self._jobStatus(job_dict)
        elif resp['status'] == '404' or resp['status'] == 404:
            log.error("ICSPJob Job status not found for job %s response 404", jobid)
            return NO_JOB
        else:
            log.error("ICSPJob Could not get job status for job %s response %s", jobid, resp['status'])
            return UNKNOWN_ERR

    def serverInfo(self, jobServerUri) :
        log.debug('ICSPJob Getting server information for server %s', jobServerUri)
        resp, content = self._getJson(jobServerUri)
        log.debug('ICSPJob serverInfo response %s', resp)
        log.debug('ICSPJob serverInfo content %s', content)
        if resp['status'] == '200' or resp['status'] == 200:
            data = json.loads(content)
            log.debug("ICSPJob Got server information for server %s status: %s", jobServerUri, data)
            return data
        elif resp['status'] == '404' or resp['status'] == 404:
            log.error("ICSPJob server information not found for server %s response 404", jobServerUri)
            return NO_SERVER
        else:
            log.error("ICSPJob Could not get server information for server %s response %s", jobServerUri, resp['status'])
            return UNKNOWN_ERR
            
    def _test_ping_job_dummy(self, args = None):
#        if (args<0):
#            error = open('icsp_error.json', 'r').read()
#            return self._jobStatus(json.loads(error))
#        if (args > 0):
            running = open('icsp_running.json', 'r').read()
            log.debug("_test_ping_job_dummy")
            x = json.loads(running)
            log.debug(x[0])
            return self._jobStatus(x[0])
#        if (args == 0):
#            success = open('icsp_complete.json', 'r').read()
#            return self._jobStatus(json.loads(success))
#        return None
if __name__ == '__main__':
    # Test code
    import sys
    handler = logging.FileHandler("icsp.log", mode = 'w')
    # handler=logging.StreamHandler()
    handler.setLevel(logging.DEBUG)
    log = logging.getLogger(__name__)
    log.addHandler(handler)
    log.setLevel(logging.INFO)

    argv = sys.argv
    # print argv[1:5]
    log.info(argv)

    if (len(argv) == 5):
        baseUrl = argv[1]
        username = argv[2]
        password = argv[3]
        jobid = argv[4]
    else:
        raise ValueError('expected 4 command line option in addition to Python filename\nUsage: python <scriptfile> <baseUrl> <username> <password> <jobid>')

    job = ICSPJob(baseUrl)
    if (not job.login(userName = username, password = password)):
        raise Exception("Could not log in")
    for x in range(1, 100):
        info = job.pingJob(jobid = jobid)
        log.info(info)
        sleep(2)

