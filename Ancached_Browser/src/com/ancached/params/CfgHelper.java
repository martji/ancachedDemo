package com.ancached.params;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.example.model.CacheHelper;
import com.example.model.HtmlHelper;

public class CfgHelper {
	private List<String> sites;
	private Map<String, String> titles;
	private Map<String, String> siteMap;
	private HashMap<String, String> topicMap;
	private List<String> topics;
	
	private HashMap<String, Integer> modelSites;
	private HashMap<String, Integer> modelTypes;
	
	@SuppressWarnings("rawtypes")
	public void init(InputStream is) {
		sites = new ArrayList<String>();
		titles = new HashMap<String, String>();
		siteMap = new HashMap<String, String>();
		topicMap = new HashMap<String, String>();
		topics = new ArrayList<String>();
		modelSites = new HashMap<String, Integer>();
		modelTypes = new HashMap<String, Integer>();
		modelTypes.put("index", 0);
		
        try {
        	SAXReader reader = new SAXReader();
        	Document document = reader.read(is);
			Element root = document.getRootElement();
			Element sites = (Element) root.element("sites");
			for(Iterator it = sites.elementIterator(); it.hasNext(); ){
			      Element site = (Element) it.next();
			      String url = site.elementText("url");
			      String title = site.elementText("title");
			      int num = Integer.parseInt(site.elementText("num"));
			      String struct = site.elementText("struct");
			      this.sites.add(url);
			      this.titles.put(title, url);
			      this.siteMap.put(url, title);
			      CacheHelper.prefix.put("http://" + url, title);
			      this.modelSites.put(url, num);
			      if (struct != ""){
			    	  HtmlHelper.structMap.put(url, struct);
			      }
			}
			Element topics = (Element) root.element("topics");
			for(Iterator it = topics.elementIterator(); it.hasNext(); ){
			      Element topic = (Element) it.next();
			      String name = topic.elementText("name");
			      String title = topic.elementText("title");
			      int num = Integer.parseInt(topic.elementText("num"));
			      this.topics.add(name);
			      this.topicMap.put(title, name);
			      this.modelTypes.put(name, num);
			 }
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> getSites() {
		return sites;
	}
	public void setSites(List<String> sites) {
		this.sites = sites;
	}
	public Map<String, String> getTitles() {
		return titles;
	}
	public void setTitles(Map<String, String> titles) {
		this.titles = titles;
	}
	public Map<String, String> getSiteMap() {
		return siteMap;
	}
	public void setSiteMap(Map<String, String> siteMap) {
		this.siteMap = siteMap;
	}
	public HashMap<String, String> getTopicMap() {
		return topicMap;
	}
	public void setTopicMap(HashMap<String, String> topicMap) {
		this.topicMap = topicMap;
	}
	public List<String> getTopics() {
		return topics;
	}
	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
	public HashMap<String, Integer> getModelSites() {
		return modelSites;
	}
	public void setModelSites(HashMap<String, Integer> modelSites) {
		this.modelSites = modelSites;
	}
	public HashMap<String, Integer> getModelTypes() {
		return modelTypes;
	}
	public void setModelTypes(HashMap<String, Integer> modelTypes) {
		this.modelTypes = modelTypes;
	}
}
