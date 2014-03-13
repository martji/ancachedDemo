package com.example.cache;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class urlParser {
	
	public static void parseHTML(String html){
		String url="";//address
		String value="";//name
		String title="";//title of the page
		Document doc=Jsoup.parse(html);
		
		//title=doc.getElementById("title").text().toString();//get the title
		title=doc.head().select("title").text().toString();
		Log.v("title","TITLE:"+title);
		Elements hrefs=doc.select("[href^=http]");//sub domain
		Elements srcs=doc.select("[src^=http]");//resources
		
		for(Element href:hrefs){
			url=href.attr("href").toString();
			value=href.text().toString();
			Log.v("href and value","URL:"+url+"  VALUE:"+value);
			
		}
		
		for(Element src:srcs){
			url=src.attr("src").toString();
			value=src.text().toString();
			Log.v("src and value","URL:"+url+"  VALUE:"+value);
		}
	}
}
