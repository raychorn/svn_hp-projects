package com.hp.asi.hpic4vc.server.provider.adapter;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;

import com.hp.asi.hpic4vc.provider.adapter.HttpDeleteWithBody;
import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.error.CommunicationException;
import com.hp.asi.hpic4vc.provider.error.DataMapException;
import com.hp.asi.hpic4vc.provider.error.NullDataException;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.server.provider.data.Jobs;
import com.hp.asi.hpic4vc.server.provider.data.Queue;
import com.hp.asi.hpic4vc.server.provider.data.SmartComponentUpdateResult;
import com.hp.asi.hpic4vc.server.provider.data.UpdateSmartComponentMessageResult;
import com.hp.asi.hpic4vc.server.provider.model.JobsModel;
import com.hp.asi.hpic4vc.server.provider.model.QueueModel;
import com.hp.asi.hpic4vc.server.provider.model.SmartComponentUpdateModel;
import com.hp.asi.hpic4vc.server.provider.model.UpdateSoftwareComponentMessagModel;

public class DeleteSmartComponentUpdateJobAdapter extends PostAdapter<SmartComponentUpdateResult, SmartComponentUpdateModel> {
	private static final String SERVICE_NAME = "services/host/firmware";
	private Boolean RESPONSE_404 = false;
	private static final String NOT_AVAILABLE_404 = "Unable to delete job.  Installation may have already started.";
	private static final String STATUS_404 = "404";
	private ObjectMapper objectMapper;
	private SmartComponentUpdateResult smartComponentUpdateResult;
	private Class<SmartComponentUpdateResult> tClass;
	public Class<UpdateSmartComponentMessageResult> tClass1;
	private String   data;
	
	public DeleteSmartComponentUpdateJobAdapter () {
	  	super();
	}
	
	public DeleteSmartComponentUpdateJobAdapter (String data) {
	   super();
	   this.objectMapper = new ObjectMapper();
	   objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	   this.tClass       = SmartComponentUpdateResult.class;
	   this.tClass1 = UpdateSmartComponentMessageResult.class;
	   this.data = data;
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
		sb.append("{\"package_url\":\"");
		sb.append(data);
		sb.append("\"}");
		return sb.toString();
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
	   jobsNullModel.setNoJobs("There are no firmware update jobs in progress.");
		return jobsNullModel;
		
	}
	
  private QueueModel formatNullQueue(){
		
	  QueueModel nullQueuesModel = new QueueModel();
	  nullQueuesModel.setPackage_url(" There are no pending jobs.");
	  return nullQueuesModel;
		
	}
	
	private QueueModel formatQueue(Queue queue){
		
		QueueModel queueModel = new QueueModel();
		StringBuffer sb = new StringBuffer();
		
		if(queue.getOptions().contains(3)){
			sb.append(queue.getPackage_url());
			sb.append("(force)");
		}else {
			sb.append(queue.getPackage_url());
		}
		
		queueModel.setPackage_url(sb.toString());
		
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

	public SmartComponentUpdateModel makeSmartComponentUpdateModel(SmartComponentUpdateResult smartComponentUpdateResult){
		
		 SmartComponentUpdateModel smartComponentUpdateModel = new SmartComponentUpdateModel();
		List<Jobs> jobsList = smartComponentUpdateResult.getJobs();
		List<Queue> queueList = smartComponentUpdateResult.getQueue();
		
		if (jobsList!=null && jobsList.size() > 0){
			for(Jobs job : jobsList){
				smartComponentUpdateModel.jobs.add(formatJobs(job));
			}	
		}else{
			smartComponentUpdateModel.jobs.add(formatNullJobs());   
		}
		if (queueList!=null && queueList.size() > 0){
			for(Queue queue : queueList){
				smartComponentUpdateModel.queue.add(formatQueue(queue));
			}
		}else{
			    smartComponentUpdateModel.queue.add(formatNullQueue());
		}
		smartComponentUpdateModel.updateSoftwareComponentMessageModel = null;
		return smartComponentUpdateModel;
		
	}
	 public SmartComponentUpdateResult getSmartComponentUpdaterResultObject(final String jsonData) throws DataMapException {
	        try {
	        	SmartComponentUpdateResult result = objectMapper.readValue(jsonData, tClass);
	           
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
	
    private UpdateSoftwareComponentMessagModel formatMessageModel(UpdateSmartComponentMessageResult updateMessageResult){
    	UpdateSoftwareComponentMessagModel updateSoftwareComponentMessageModel = new UpdateSoftwareComponentMessagModel();
    	updateSoftwareComponentMessageModel.setStatus(STATUS_404);
    	updateSoftwareComponentMessageModel.setMessage(NOT_AVAILABLE_404);
		return updateSoftwareComponentMessageModel;
    }
	
	public SmartComponentUpdateModel makeSmartComponentUpdateMessageModel(UpdateSmartComponentMessageResult updateMessageResult){
		SmartComponentUpdateModel smartComponentUpdateModel = new SmartComponentUpdateModel();
		smartComponentUpdateModel.jobs = null;
		smartComponentUpdateModel.queue = null;
		smartComponentUpdateModel.updateSoftwareComponentMessageModel = formatMessageModel(updateMessageResult);
		return smartComponentUpdateModel;
	}
	
	
	@Override
	public List<NameValuePair> getPostParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SmartComponentUpdateModel formatData(String response) {
		// TODO Auto-generated method stub
		
		SmartComponentUpdateModel smartComponentUpdateModel  =new SmartComponentUpdateModel();
		  try 
		  {
			  if(RESPONSE_404 && response.contains("not found")){
				  UpdateSmartComponentMessageResult messageResult = new UpdateSmartComponentMessageResult();
				  smartComponentUpdateModel = makeSmartComponentUpdateMessageModel(messageResult);
			  }else {
				  smartComponentUpdateResult = getSmartComponentUpdaterResultObject(response);  
				  smartComponentUpdateModel = makeSmartComponentUpdateModel(smartComponentUpdateResult);
			  }
			  
		  } catch (DataMapException e) {
	    	e.printStackTrace();
		  }
		return smartComponentUpdateModel;
	}

	@Override
	public SmartComponentUpdateModel getEmptyModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceName () {
	   return SERVICE_NAME;
	} 

	@Override
	public SmartComponentUpdateModel formatData(
			SmartComponentUpdateResult rawData) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void checkHttpResponseForErrors(final HttpResponse response)
	            throws CommunicationException, NullDataException {
		
		 if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			 RESPONSE_404 = true;
	     }
		  
	}

	

}
