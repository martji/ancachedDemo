package com.example.webservice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.util.Log;

import com.example.ancached_browser.MainActivity;
import com.example.struct.TrackLogItem;

public class WebServiceManager {
	
	@SuppressWarnings("rawtypes")
	private static String webservice(WsdlCfg cfg, Map<String, String> args){
		String nameSpace = cfg.getNameSpace();  
        String methodName = cfg.getMethodName();  
        String endPoint = cfg.getEndPoint();
        String soapAction = cfg.getSoapAction(); 

        SoapObject rpc = new SoapObject(nameSpace, methodName);  
        Iterator iter = args.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			rpc.addProperty((String)entry.getKey(), (String)entry.getValue());
		}

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);  
  
        HttpTransportSE transport = new HttpTransportSE(endPoint);  
        try {  
            transport.call(soapAction, envelope);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        SoapObject object = (SoapObject) envelope.bodyIn;  
        String result = object.getProperty(0).toString();  
        Log.i("webservice_result", result);
		return result;
	}
	
	public static void getUrl(TrackLogItem item) {
        WsdlCfg cfg = new WsdlCfg();
        Map<String, String> args = new HashMap<String, String>();
        String nameSpace = "http://model.magq.com";  
        String methodName = "getUrl";  
        String endPoint = "http://192.168.3.201:8080/axis2/services/ModelManager";  
        String soapAction = "http://model.magq.com/getUrl"; 
        
        cfg.setNameSpace(nameSpace);
        cfg.setMethodName(methodName);
        cfg.setEndPoint(endPoint);
        cfg.setSoapAction(soapAction);
        
        args.put("url", item.getUrl());
        args.put("title", item.getTitle());
        
        webservice(cfg, args);
    }
	
	public static void getTokens(String text) {  
		WsdlCfg cfg = new WsdlCfg();
        Map<String, String> args = new HashMap<String, String>();
        String nameSpace = "http://tokenizer.magq.com";  
        String methodName = "tokens";  
        String endPoint = "http://192.168.3.201:8080/axis2/services/Tokenzier";  
        String soapAction = "http://tokenizer.magq.com/tokens";  
        
        cfg.setNameSpace(nameSpace);
        cfg.setMethodName(methodName);
        cfg.setEndPoint(endPoint);
        cfg.setSoapAction(soapAction);
        
        args.put("text", text);
        
        webservice(cfg, args);
    }
	
	public static void getTokens2(String text) {  
		WsdlCfg cfg = new WsdlCfg();
        Map<String, String> args = new HashMap<String, String>();
        String nameSpace = "http://ws.apache.org/axis2";  
        String methodName = "fenCiResult";  
        String endPoint = "http://115.28.42.83:5000/axis2/services/fenci";  
        String soapAction = "http://ws.apache.org/axis2/fenCiResult";  

        cfg.setNameSpace(nameSpace);
        cfg.setMethodName(methodName);
        cfg.setEndPoint(endPoint);
        cfg.setSoapAction(soapAction);
        
        args.put("content", text);
        
        webservice(cfg, args);
    }
	
	public static void insertItem(List<TrackLogItem> items) {
		WsdlCfg cfg = new WsdlCfg();
        Map<String, String> args = new HashMap<String, String>();
		String nameSpace = "http://model.magq.com";  
        String methodName = "addItem";  
        String endPoint = "http://112.124.46.148:8080/axis2/services/DBHelper";  
        String soapAction = "http://model.magq.com/addItem"; 
        
        cfg.setNameSpace(nameSpace);
        cfg.setMethodName(methodName);
        cfg.setEndPoint(endPoint);
        cfg.setSoapAction(soapAction);
 
        
        String logs = MainActivity.deviceID + "@V@";
        for (int i = 0; i < items.size(); i++){
        	TrackLogItem item = items.get(i);
        	logs += item.getUrl() + "," + item.getTitle() + "," +
        			item.getvTime().getStr() + "," + item.getLocation() + "," +
        			Integer.toString(item.getNetState());
        	logs += i!=items.size()-1?"\n":"";
        }
        args.put("logs", logs); 

        webservice(cfg, args);
	}
}
