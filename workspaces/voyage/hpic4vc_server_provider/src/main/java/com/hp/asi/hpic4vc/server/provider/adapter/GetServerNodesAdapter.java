package com.hp.asi.hpic4vc.server.provider.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.error.CommunicationException;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.error.NullDataException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.server.provider.data.Deployment.DeploymentServerDetail;
import com.hp.asi.hpic4vc.server.provider.data.Deployment.InterfaceData;
import com.hp.asi.hpic4vc.server.provider.data.Deployment.ServerNodesData;
import com.hp.asi.hpic4vc.server.provider.data.Deployment.ServerNodesResult;
import com.hp.asi.hpic4vc.server.provider.model.DeploymentDetailModel;

public class GetServerNodesAdapter extends DataAdapter<ServerNodesResult, DeploymentDetailModel>{
	
	 private static final String SERVICE_NAME = "/rest/os-deployment-servers";
	 private static final String HARDWARE_MODEL = "PROLIANT";
	 
	 private String auth;
	 private String host;
	 
	 public GetServerNodesAdapter(String _auth, String _host) {
	        super(ServerNodesResult.class);
	        this.auth = _auth;
	        this.host = _host;
	    }
	 
	 @Override
	    public String getServiceName () {
	        return SERVICE_NAME;
	    }
	 
	 
	 @Override
	 public DeploymentDetailModel getEmptyModel() {
	 	// TODO Auto-generated method stub
	 	return null;
	 }
	 
	 
	 protected String getWsUrl () throws InitializationException 
	 {
			String url;
			String developerHostname = "http://localhost:8085/";
			String hostname = SessionManagerImpl.getInstance().getWSURLHostname
	                (this.sessionInfo.getSessionId(),
	                 this.sessionInfo.getServerGuid());
			if(hostname.contains("localhost"))
			{
				hostname=developerHostname;
			}
			
			url="https://"+ host + SERVICE_NAME ;				 
	        return url;
	       
	  }
	 
	 private void replaceHttpClient() {
			try {
				if (httpClient != null) {
					httpClient.getConnectionManager().shutdown();
					httpClient = null;
				}

				httpClient = ServerProvisioningAdapter.getInstance();
			} catch (Exception e) {
				log.error("replaceHttpClient()", e);
			}

		}
	 
	 
	 @Override
	 protected String httpGet (final String urlString) throws NullDataException,
     CommunicationException {
	 try {
	     HttpGet      httpget   = new HttpGet(urlString);
	     httpget.setHeader("auth", auth);
	     
	     replaceHttpClient();
	     
	     HttpResponse response  = httpClient.execute(httpget);
	     checkHttpResponseForErrors(response);
	     
	     HttpEntity entity = response.getEntity();
	     BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
	     StringBuffer sb   = new StringBuffer();
	     String line;
	     while ((line = in.readLine()) != null) {
	         sb.append(line);
	     }
	     in.close();
	     String jsonString = sb.toString();
	     if (isErrorPage(jsonString, urlString)) {
	         log.error("Page for " + this.getServiceName()
	                 + " is Page Not Found.  Returning a null JSON string.");
	         return null;
	     }
	     return jsonString;
	 } catch (IOException e) {
	     String errorMessage = this.i18nProvider.getInternationalString
	             (locale, I18NProvider.Error_NoWsResponse);
	     log.error("httpGet for " + this.getServiceName()
	             + " caught an IOException.  "
	             + "Throwing a CommunicationException.", e);
	     throw new CommunicationException(errorMessage);
	 } finally {
	     httpClient.getConnectionManager().shutdown();
	     this.httpClient = null;
	 }
}
	 
		@Override
		public DeploymentDetailModel formatData(ServerNodesResult rawData)
		{
			 List<DeploymentServerDetail> serverlist = rawData.getMembers();
			 ServerNodesData serverNodesData ;
			 
			DeploymentDetailModel model = new DeploymentDetailModel();		 
			 for(DeploymentServerDetail deploymentServerdeDetail : serverlist){	
				 
				if (deploymentServerdeDetail.getHardwareModel().contains(HARDWARE_MODEL))
				{
					serverNodesData = new ServerNodesData();
					serverNodesData.setUUID(deploymentServerdeDetail.getUuid());
					serverNodesData.setName(deploymentServerdeDetail.getName());
					serverNodesData.setUri(deploymentServerdeDetail.getUri());
					serverNodesData.setManagementIP(deploymentServerdeDetail.getManagementIP());
					//serverNodesData.setMacAddress(deploymentServerdeDetail.getInterfaces())	
					serverNodesData.setMacAddress(getMacAddress(deploymentServerdeDetail.getInterfaces(),deploymentServerdeDetail.getManagementIP()));
					serverNodesData.setState(deploymentServerdeDetail.getState());
					serverNodesData.setOpswLifecycle(deploymentServerdeDetail.getOpswLifecycle());
					
					model.getServerNodes().add(serverNodesData);
				}
				
				
				}
			return model;
		}

		private String getMacAddress(List<InterfaceData> interfaces, String managingIP) {
			for (InterfaceData item : interfaces) {
				if (null !=item){
					
					if(null != item.getIpv4Addr()){
						if(item.getIpv4Addr().length() > 0)
						{
							if(item.getIpv4Addr().equals(managingIP))
							return item.getMacAddr();
						}
						
					}
					
				}
			}
		
			return null;
		}

}
