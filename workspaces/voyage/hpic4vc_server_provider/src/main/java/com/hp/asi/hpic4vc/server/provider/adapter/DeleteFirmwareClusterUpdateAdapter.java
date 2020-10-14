package com.hp.asi.hpic4vc.server.provider.adapter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.type.TypeReference;

import com.hp.asi.hpic4vc.provider.adapter.HttpDeleteWithBody;
import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.error.DataMapException;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.server.provider.data.FirmwareJobs;
import com.hp.asi.hpic4vc.server.provider.data.FirmwareJobsForCluster;
import com.hp.asi.hpic4vc.server.provider.data.FirmwareJobsForClusterResult;
import com.hp.asi.hpic4vc.server.provider.data.Jobs;
import com.hp.asi.hpic4vc.server.provider.data.Queue;
import com.hp.asi.hpic4vc.server.provider.data.UpdateFirmwareComponentMessageResult;
import com.hp.asi.hpic4vc.server.provider.model.FirmwareJobsForClusterModel;
import com.hp.asi.hpic4vc.server.provider.model.FirmwareJobsModel;
import com.hp.asi.hpic4vc.server.provider.model.FirmwareListOfJobsForClusterModel;
import com.hp.asi.hpic4vc.server.provider.model.JobsModel;
import com.hp.asi.hpic4vc.server.provider.model.QueueClusterModel;
import com.hp.asi.hpic4vc.server.provider.model.UpdateFirmwareComponentMessageModel;

public class DeleteFirmwareClusterUpdateAdapter  extends PostAdapter<FirmwareJobsForClusterResult, FirmwareListOfJobsForClusterModel>{
	
	private static final String SERVICE_NAME = "services/host/firmware";
	private String  data;
	private ObjectMapper objectMapper;
	private Boolean RESPONSE_404 = false;
	private static final String NOT_AVAILABLE_404 = "Unable to delete job.  Installation may have already started.";
	private static final String STATUS_404 = "404";
	private UpdateFirmwareComponentMessageResult messageResult;
	public Class<UpdateFirmwareComponentMessageResult> tClass1;
	public FirmwareJobsForClusterResult firmwareJobsForClusterResult;
	private String[] hosts;
	
	public DeleteFirmwareClusterUpdateAdapter () {
	  	super();
	}
	
	public DeleteFirmwareClusterUpdateAdapter (String data,final String[] args) {
	   super();
	   this.objectMapper = new ObjectMapper();
	   objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	   this.tClass1 = UpdateFirmwareComponentMessageResult.class;
	   this.data = data;
	   this.hosts = new String[args.length];
	   for (int j = 0; j< args.length;++j)
		   hosts[j]= args[j];   
	}
	
	@Override
    public HttpDeleteWithBody getHttpRequest(final String urlString) throws UnsupportedEncodingException {
		HttpDeleteWithBody httpDelete   = new HttpDeleteWithBody(urlString);
        StringEntity params = new StringEntity( getDeleteParams());
		 httpDelete.addHeader("content-type", "application/x-www-form-urlencoded");
		httpDelete.setEntity(params);
		return httpDelete;
    }
	

	public String getDeleteParams() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"hosts\":");
		sb.append(generateFirmwareHostsInfo());
		sb.append(",\"package_url\":\"");
		sb.append(data);
		sb.append("\"}");
		return sb.toString();
	}
	
	String generateFirmwareHostsInfo(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		
		for (int i=0; i<hosts.length; i++) {
			    sb.append("\"");
				sb.append(hosts[i]);
				sb.append("\"");
				if(i != hosts.length-1){
				   sb.append(",");
				}
		}
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public String getServiceName () {
	   return SERVICE_NAME;
	} 

	@Override
	public List<NameValuePair> getPostParams () {
	       return null;
	 }
	
	 public FirmwareJobsForClusterResult getFirmwareUpdateResultObject(final String jsonData) throws DataMapException {
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
    
    public UpdateFirmwareComponentMessageResult getUpdateSmartComponentMessages(final String jsonData) throws DataMapException {
    	
    	try {
    		UpdateFirmwareComponentMessageResult result = objectMapper.readValue(jsonData, tClass1);
           
            log.info("JSON data mapped to object: " + result.toString());
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
    
    
    private UpdateFirmwareComponentMessageModel formatMessageModel(UpdateFirmwareComponentMessageResult updateFirmwareComponentMessageResult){
    	UpdateFirmwareComponentMessageModel updateFirmwareComponentMessageModel = new UpdateFirmwareComponentMessageModel();
    	updateFirmwareComponentMessageModel.setStatus(STATUS_404);
    	updateFirmwareComponentMessageModel.setMessage(NOT_AVAILABLE_404);
		return updateFirmwareComponentMessageModel;
    }
    
    private JobsModel formatJobs(Jobs job){
		
		JobsModel jobsModel = new JobsModel();
		
		
		if(job.getDescription()!=null){
			
			jobsModel.setJobDescription(job.getDescription());
			jobsModel.setStatus(job.getStatus());
			
			if(job.getStatusDescriptions()!=null){
				jobsModel.setStatusDescriptions(concatStringsWSep(job.getStatusDescriptions(),"."));
			}
			
			if(job.getJobState()== 10){
				jobsModel.setErrorDescription(getErrrorCodeText(job.getErrorCode(),job));
			}else if(job.getErrorDescription()!=null && job.getErrorCode() != 0){
				jobsModel.setErrorDescription(job.getErrorDescription());
			}else{
				 if(job.getPercentComplete() == 0 || job.getPercentComplete() == 100 ){
						jobsModel.setPercentComplete(String.valueOf(job.getPercentComplete()+"%"));
				 }
			}
			
		}else {
			jobsModel.setNoJobs("There are no firmware update jobs in progress.");
		}
		
		
		return jobsModel;
		
	}
	
   private JobsModel formatNullJobs(){
		
	   JobsModel jobsNullModel = new JobsModel();
	   jobsNullModel.setNoJobs(" There are no pending jobs in this host.");
		return jobsNullModel;
		
	}
	
  private QueueClusterModel formatNullQueue(){
		
	  QueueClusterModel nullQueuesModel = new QueueClusterModel();
	  nullQueuesModel.setPackage_url(" There are no pending jobs.");
	  return nullQueuesModel;
		
	}
	
	private QueueClusterModel formatQueue(Queue queue,String host){
		
		QueueClusterModel queueModel = new QueueClusterModel();
		StringBuffer sb = new StringBuffer();
		
		if(queue.getOptions().contains(3)){
			sb.append(queue.getPackage_url());
			sb.append("(force)");
		}else {
			sb.append(queue.getPackage_url());
		}
		
		queueModel.setPackage_url(sb.toString());
		queueModel.setHost(host);
		return queueModel;
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
	
	private JobsModel formatNotSupported(){
		
		   JobsModel jobsNullModel = new JobsModel();
		   jobsNullModel.setNoJobs("Smart Component update is not supported on this host.");
			return jobsNullModel;
			
	}
	
     public FirmwareJobsModel makeFirmwareJobsModel(FirmwareJobs firmwareJobs,String host){
		
		FirmwareJobsModel firmwareJobsModel = new FirmwareJobsModel();
		List<Jobs> jobsList = firmwareJobs.getJobs();
		List<Queue> queueList = firmwareJobs.getQueue();
		
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
  
		return firmwareJobsModel;
		
	}
	
	public FirmwareJobsForClusterModel makeFirmwareClusterModel(FirmwareJobsForCluster firmwareJobsForCluster,FirmwareListOfJobsForClusterModel firmwareJobsForClusterModelHosts){
		FirmwareJobsForClusterModel firmwareJobsForClusterModel = new FirmwareJobsForClusterModel();
		List<String> hosts = new ArrayList<String>();
		if(firmwareJobsForCluster.getHost()!=null){
			firmwareJobsForClusterModel.setFirmwareJobsModel(makeFirmwareJobsModel(firmwareJobsForCluster.getFirmwareJobs(),firmwareJobsForCluster.getHost()));
		}
		if(firmwareJobsForCluster.getHost()!=null){
			firmwareJobsForClusterModel.setHost(firmwareJobsForCluster.getHost());
			hosts.add(firmwareJobsForCluster.getHost());
		}
		firmwareJobsForClusterModelHosts.setHosts(hosts);
		return firmwareJobsForClusterModel;
	}

	public FirmwareListOfJobsForClusterModel  makeFirmwareListOfJobsModel(FirmwareJobsForClusterResult firmwareJobsForClusterResult){
		FirmwareListOfJobsForClusterModel firmwareListOfJobsForClusterModel = new FirmwareListOfJobsForClusterModel();
		List<FirmwareJobsForClusterModel> firmwareJobsForModel= new ArrayList<FirmwareJobsForClusterModel>();
		
		if( firmwareJobsForClusterResult.getFirmwareJobsForCluster()!=null){
			List<FirmwareJobsForCluster> firmwareJobsForCluster = firmwareJobsForClusterResult.getFirmwareJobsForCluster();
			for (FirmwareJobsForCluster firmwareJobs : firmwareJobsForCluster)
			{
				firmwareJobsForModel.add(makeFirmwareClusterModel(firmwareJobs,firmwareListOfJobsForClusterModel));
			}
		}
		
		firmwareListOfJobsForClusterModel.setFirmwareListOfJobsForClusterModel(firmwareJobsForModel);
		
		return firmwareListOfJobsForClusterModel;
	}
	
	public FirmwareListOfJobsForClusterModel makeSmartComponentUpdateMessageModel(UpdateFirmwareComponentMessageResult updateMessageResult){
		FirmwareListOfJobsForClusterModel firmwareListOfJobsForClusterModel = new FirmwareListOfJobsForClusterModel();
		firmwareListOfJobsForClusterModel.firmwareListOfJobsForClusterModel = null;
		firmwareListOfJobsForClusterModel.hosts = null;
		firmwareListOfJobsForClusterModel.updateFirmwareComponentMessageModel= formatMessageModel(updateMessageResult);
		return firmwareListOfJobsForClusterModel;
	}
	@Override
	public FirmwareListOfJobsForClusterModel formatData(String response) {
		
		FirmwareListOfJobsForClusterModel firmwareJobsForClustereModel  =new FirmwareListOfJobsForClusterModel();
		FirmwareJobsForClusterResult firmwareJobsForClusterResult;
		  try 
		  {
			  if(RESPONSE_404 && response.contains("not found")){
				  messageResult = getUpdateSmartComponentMessages(response);
				  firmwareJobsForClustereModel = makeSmartComponentUpdateMessageModel(messageResult);
			  }else {
				  firmwareJobsForClusterResult =  getFirmwareUpdateResultObject(response);  
				  firmwareJobsForClustereModel = makeFirmwareListOfJobsModel(firmwareJobsForClusterResult);
			  }
			  
		  } catch (DataMapException e) {
	    	e.printStackTrace();
		  }
		  
		return firmwareJobsForClustereModel;
	}
	
	@Override
	public FirmwareListOfJobsForClusterModel getEmptyModel() {
		// TODO Auto-generated method stub
		return new FirmwareListOfJobsForClusterModel();
	}
	

	@Override
	public FirmwareListOfJobsForClusterModel formatData(
			FirmwareJobsForClusterResult rawData) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
