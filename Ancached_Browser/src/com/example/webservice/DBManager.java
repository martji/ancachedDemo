package com.example.webservice;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class DBManager {

	public static void  pushLogs(String logs) {
		String serviceUrl = "http://112.124.46.148:5001/axis2/services/DBHelper?wsdl";
		String methodName = "pushLog";
		SoapObject request = new SoapObject("http://model.magq.com", methodName);
		request.addProperty("logs", logs);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		
		envelope.bodyOut = request;
		HttpTransportSE ht = new HttpTransportSE(serviceUrl);
		try {
			ht.call(null, envelope);
			if (envelope.getResponse() != null){
				SoapObject object = (SoapObject) envelope.getResponse();
		        String result = object.getProperty(0).toString();
		        Log.i("webservice_result", result);
			}
		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
