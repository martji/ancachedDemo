package com.example.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import android.util.Log;

public class Tokenizer {

	@SuppressWarnings("rawtypes")
	public static void getKeys(String text){
		String api_key = "z1K3G4n3LTgRsYjwXVMrlt7TIvXkmqqCunNVccjw";
		String pattern = "pos";
		String format = "xml";
		try {
			text = URLEncoder.encode(text, "utf-8");
			URL url = new URL("http://api.ltp-cloud.com/analysis/?" + "api_key="
					+ api_key + "&" + "text=" + text + "&" + "format=" + format
					+ "&" + "pattern=" + pattern);
			URLConnection conn = url.openConnection();
			conn.connect();
	
			BufferedReader innet = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(innet);
			Element root = document.getRootElement();
			Element word = null;
			Iterator tickets = null;
			for (tickets = root.element("doc").element("para").element("sent")
					.elementIterator(); tickets.hasNext();) {
				word = (Element) tickets.next();
				Log.i("words", word.attributeValue("cont") + "  " + word.attributeValue("pos"));
			}
			innet.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
