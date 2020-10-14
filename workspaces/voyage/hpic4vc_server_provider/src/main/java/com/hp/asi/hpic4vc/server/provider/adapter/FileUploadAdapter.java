package com.hp.asi.hpic4vc.server.provider.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.model.StringModel;

public class FileUploadAdapter extends PostAdapter<String, StringModel> {

	private static final String SERVICE_NAME = "services/host/smart_components";
	private String   data;
	private byte uploadedBinary[];

	public FileUploadAdapter () {
	  	super();
	}
	
	public FileUploadAdapter (String data, byte[] uploadedBinary) {
	   super();
	   this.data = data;
	   this.uploadedBinary = uploadedBinary;
	}
	
	@Override
	public String getServiceName () {
	   return SERVICE_NAME;
	} 

	@Override
	public List<NameValuePair> getPostParams () {
	   	String kvPairs[] = data.split(":");
	       List<NameValuePair> params = new ArrayList<NameValuePair>();
	       for (String kvPair:kvPairs){
	       	String kv[] = kvPair.split("=");
	       	if (kv.length == 2){
	       		try {
	       			params.add(new BasicNameValuePair(kv[0], kv[1]));
	       		}
	       		catch (Exception e){
	       			log.debug(e);
	       		}
	       	}
	       }
	       return params;
	 }
	@Override
	public
	HttpEntityEnclosingRequestBase getHttpRequest(final String urlString) throws UnsupportedEncodingException {
		//multipart/form-data 
        HttpEntityEnclosingRequestBase httpPost = new HttpPost(urlString);
        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody cbFile;
		try {
			cbFile = new FileBody(ConvertBytesToFile(uploadedBinary),"binary/octet-stream");
			StringBody filename = new StringBody(data);
			 mpEntity.addPart("filename", filename);
			 mpEntity.addPart("sc_file", cbFile);
		
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		}
        httpPost.setEntity(mpEntity) ;
        return httpPost;
    }
	
	public java.io.File ConvertBytesToFile(byte[] uploadedBinary2) throws IOException{
		
		File test = File.createTempFile("recbin","scexe");
		String tmp_filen = test.getAbsolutePath();
		FileOutputStream fileOuputStream = new FileOutputStream(test); 
	    fileOuputStream.write(uploadedBinary2);
	    fileOuputStream.close();
	    File test2 = new File(tmp_filen);
		return test2;
		
	}

	@Override
	public StringModel getEmptyModel() {
		// TODO Auto-generated method stub
		return new StringModel();
	}

	@Override
	public StringModel formatData(String response) {
		 StringModel model = new StringModel();
		 model.data = response; 
		 return model;
	}
	
}
