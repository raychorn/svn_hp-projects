package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.type.TypeReference;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.error.DataMapException;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.server.provider.data.FirmwareJobs;
import com.hp.asi.hpic4vc.server.provider.data.FirmwareJobsForCluster;
import com.hp.asi.hpic4vc.server.provider.data.FirmwareJobsForClusterResult;
import com.hp.asi.hpic4vc.server.provider.data.Jobs;
import com.hp.asi.hpic4vc.server.provider.data.Queue;
import com.hp.asi.hpic4vc.server.provider.model.FirmwareJobsForClusterModel;
import com.hp.asi.hpic4vc.server.provider.model.FirmwareJobsModel;
import com.hp.asi.hpic4vc.server.provider.model.FirmwareListOfJobsForClusterModel;
import com.hp.asi.hpic4vc.server.provider.model.JobsModel;
import com.hp.asi.hpic4vc.server.provider.model.QueueClusterModel;

public class GetFirmwareJobsAdapter extends DataAdapter<FirmwareJobsForClusterResult, FirmwareListOfJobsForClusterModel>{

	private static final String SERVICE_NAME = "services/host/firmware_jobs";
    private static final int DEFAULT_TIMEOUT_SECONDS = 90;
	private static final int FIVE_MINUTES_IN_SECONDS = 300;
	
	private ObjectMapper objectMapper;
	public GetFirmwareJobsAdapter()
	{
		super(FirmwareJobsForClusterResult.class);
		this.objectMapper = new ObjectMapper();
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

	@Override
	public FirmwareListOfJobsForClusterModel getEmptyModel() {
		
		return new FirmwareListOfJobsForClusterModel();
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	private JobsModel formatJobs(Jobs job){
		
		JobsModel jobsModel = new JobsModel();
			
		jobsModel.setJobDescription(job.getDescription());
		jobsModel.setStatus(job.getStatus());
		
		if(job.getStatusDescriptions()!=null){
			jobsModel.setStatusDescriptions(concatStringsWSep(job.getStatusDescriptions(),"."));
		}
		
		if(job.getJobState()== 10){
			jobsModel.setStatus(job.getStatus()+":"+getErrrorCodeText(job.getErrorCode(),job));
		}else if(job.getStatus().endsWith("Error")){
			jobsModel.setStatus(job.getStatus()+":"+job.getErrorDescription());
		}
		
		if(!job.getStatus().equals("Error")){
			if(job.getPercentComplete() == 0 || job.getPercentComplete() == 100 ){
		    	jobsModel.setPercentComplete(String.valueOf(job.getPercentComplete()+"%"));
		    }
		}
		
		return jobsModel;
	}
	
   private JobsModel formatNullJobs(){
		
	   JobsModel jobsNullModel = new JobsModel();
	   jobsNullModel.setNoJobs(" There are no firmware jobs on this host.");
		return jobsNullModel;
		
	}
   private JobsModel formatNotSupported(){
		
	   JobsModel jobsNullModel = new JobsModel();
	   jobsNullModel.setNoJobs(" Smart Component update is not supported on this host.");
		return jobsNullModel;
		
	}
	
  private QueueClusterModel formatNullQueue(){
		
	  QueueClusterModel nullQueuesModel = new QueueClusterModel();
	  nullQueuesModel.setPackage_url(" There are no pending jobs in this host.");
	  return nullQueuesModel;
		
	}
	
	private QueueClusterModel formatQueue(Queue queue,String host){
		
		QueueClusterModel queueClusterModel = new QueueClusterModel();
		StringBuffer sb = new StringBuffer();
		
		if(queue.getOptions().contains(3)){
			sb.append(queue.getPackage_url());
			sb.append("(force)");
		}else {
			sb.append(queue.getPackage_url());
		}
		
		queueClusterModel.setPackage_url(sb.toString());
		queueClusterModel.setHost(host);
		return queueClusterModel;
	}
	
	public String getErrrorCodeText(int errorCode,Jobs job){
		 switch(errorCode)
            {
                case 1 : return "The installation of the deliverable was successful.  A reboot is required for the deliverable to be enabled.";
                         
                case 2 : return "The installation was not attempted because the version to be installed matches the version already installed.";
                         
                case 3 : return "The installation was not attempted because the version to be installed is older than the version already installed or the supported hardware or environment was not available.";
                
                case 4 : return "The installation was not attempted because the supported hardware is not present.";
                
                case 5 : return "The installation was canceled by the user.";
                
                case 6 : return "The installation could not be performed due to an unmet dependency or installation tool failure.";
                
                case 7 : return "The installation was not performed due to an installation failure.";
                
                case 500 : return "Download of the SmartComponent failed. " + job.getErrorDescription();

                default : return "Unknown Error " + job.getErrorCode();
            }
    }
	
	public static String concatStringsWSep(List<String> strings, String separator) {
	    StringBuilder sb = new StringBuilder();
	    String sep = "";
	    for(String s: strings) {
	        sb.append(sep).append(s);
	        sep = separator;
	    }
	    return sb.toString();                           
	}
	
	public FirmwareJobsModel makeFirmwareJobsModel(FirmwareJobs firmwareJobs,String host){
		
		FirmwareJobsModel firmwareJobsModel = new FirmwareJobsModel();
		List<Jobs> jobsList = firmwareJobs.getJobs();
		List<Queue> queueList = firmwareJobs.getQueue();
		List<String> errorsList = firmwareJobs.getError();
		if(errorsList!=null && errorsList.size() > 0){
			firmwareJobsModel.errors.add(errorsList.get(1));	
		}else{
			if (jobsList!=null && jobsList.size() > 0){
				for(Jobs job : jobsList){
					firmwareJobsModel.jobs.add(formatJobs(job));
				}	
			}else if((jobsList!=null && jobsList.size() == 0)){
				firmwareJobsModel.jobs.add(formatNullJobs());   
			}else {
				firmwareJobsModel.jobs.add(formatNotSupported());
			}
			
			
			if (queueList!=null && queueList.size() > 0){
				for(Queue queue : queueList){
					firmwareJobsModel.queue.add(formatQueue(queue,host));
				}
			}else{
				firmwareJobsModel.queue.add(formatNullQueue());
			}
	     
	   }
		return firmwareJobsModel;
		
	}
	
	
	public FirmwareJobsForClusterModel makeFirmwareClusterModel(FirmwareJobsForCluster firmwareJobsForCluster,FirmwareListOfJobsForClusterModel firmwareJobsForClusterModelHosts){
		FirmwareJobsForClusterModel firmwareJobsForClusterModel = new FirmwareJobsForClusterModel();
		if(firmwareJobsForCluster.getHost()!=null){
			firmwareJobsForClusterModel.setFirmwareJobsModel(makeFirmwareJobsModel(firmwareJobsForCluster.getFirmwareJobs(),firmwareJobsForCluster.getHost()));
		}
		if(firmwareJobsForCluster.getHost()!=null){
			firmwareJobsForClusterModel.setHost(firmwareJobsForCluster.getHost());
		}
		return firmwareJobsForClusterModel;
	}

	public Boolean checkIfTheHostSupportsFirmwareUpdate(FirmwareJobsForCluster firmwareJobsForClusterCheck){
		Boolean check = true;
		if(firmwareJobsForClusterCheck.getHost()!=null){
			if(firmwareJobsForClusterCheck.getFirmwareJobs().getError()!=null && firmwareJobsForClusterCheck.getFirmwareJobs().getError().size() > 0){
				check = false;
			}
		}
		return check;
	}
	
	@Override
	public FirmwareListOfJobsForClusterModel formatData(FirmwareJobsForClusterResult rawData) {
		
		FirmwareListOfJobsForClusterModel firmwareListOfJobsForClusterModel = new FirmwareListOfJobsForClusterModel();
		List<FirmwareJobsForClusterModel> firmwareJobsForModel= new ArrayList<FirmwareJobsForClusterModel>();
		List<String> hosts = new ArrayList<String>();
		if( rawData.getFirmwareJobsForCluster()!=null){
			List<FirmwareJobsForCluster> firmwareJobsForCluster = rawData.getFirmwareJobsForCluster();
			for (FirmwareJobsForCluster firmwareJobs : firmwareJobsForCluster)
			{
               if(checkIfTheHostSupportsFirmwareUpdate(firmwareJobs)){
            	   hosts.add(firmwareJobs.getHost());   
               }
				firmwareJobsForModel.add(makeFirmwareClusterModel(firmwareJobs,firmwareListOfJobsForClusterModel));
			}
		}
		firmwareListOfJobsForClusterModel.setHosts(hosts);
		firmwareListOfJobsForClusterModel.setFirmwareListOfJobsForClusterModel(firmwareJobsForModel);
		
		return firmwareListOfJobsForClusterModel;
	}
	
	@Override
	public FirmwareJobsForClusterResult getWsObject (final String jsonData) throws DataMapException {
		
        try {
        	List<FirmwareJobsForCluster> result1 = objectMapper.readValue(jsonData, new TypeReference<List<FirmwareJobsForCluster>>(){});
            log.info("JSON data mapped to object: " + result1.toString());
            FirmwareJobsForClusterResult result = new FirmwareJobsForClusterResult(result1);
            
            return result;
        } catch (Exception e) {
            log.error("JSON ERROR: error mapping JSON string to object for service "
                    + this.getServiceName()
                    + ":  "
                    + jsonData
                    + ".  Throwing a DataMapException.");
            log.debug(e.getMessage(), e);
            String message = this.i18nProvider
                    .getInternationalString(locale,
                                            I18NProvider.Error_CannotMapJson);
            throw new DataMapException(message);
        }
    }
	
	public int getTimeoutSeconds() {
	    if (this.sessionInfo.isDeveloperMode()) {
	        return FIVE_MINUTES_IN_SECONDS;
	    }
	    return DEFAULT_TIMEOUT_SECONDS;
	}
}

