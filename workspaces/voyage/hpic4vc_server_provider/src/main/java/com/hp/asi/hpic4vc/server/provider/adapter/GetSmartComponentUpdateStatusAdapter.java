package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.server.provider.data.Jobs;
import com.hp.asi.hpic4vc.server.provider.data.Queue;
import com.hp.asi.hpic4vc.server.provider.data.SmartComponentUpdateResult;
import com.hp.asi.hpic4vc.server.provider.model.JobsModel;
import com.hp.asi.hpic4vc.server.provider.model.QueueModel;
import com.hp.asi.hpic4vc.server.provider.model.SmartComponentUpdateModel;

public class GetSmartComponentUpdateStatusAdapter extends DataAdapter<SmartComponentUpdateResult, SmartComponentUpdateModel> {

	    private static final String SERVICE_NAME = "services/host/firmware_jobs";
		
		public GetSmartComponentUpdateStatusAdapter()
		{
			super(SmartComponentUpdateResult.class);
	    }

		@Override
		public SmartComponentUpdateModel getEmptyModel() {
			
			return new SmartComponentUpdateModel();
		}

		@Override
		public String getServiceName() {
			return SERVICE_NAME;
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

		@Override
		public SmartComponentUpdateModel formatData(SmartComponentUpdateResult rawData) {
			
			SmartComponentUpdateModel smartComponentUpdateModel = new SmartComponentUpdateModel();
			List<Jobs> jobsList = rawData.getJobs();
			List<Queue> queueList = rawData.getQueue();
			List<String> errorList = rawData.getError();
			
			if(errorList!=null && errorList.size() > 0){
				smartComponentUpdateModel.errors.add(errorList.get(1));
			}else {
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
 	
			}
						
			return smartComponentUpdateModel;
	   }

}
