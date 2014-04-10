package com.ancached.model;

import java.io.IOException;  
import java.io.StringReader;  
import java.util.ArrayList;
import java.util.List;
  
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.core.IKSegmenter;  
import org.wltea.analyzer.core.Lexeme;  
import org.wltea.analyzer.lucene.IKAnalyzer;

import android.util.Log;
  
public class Tokenizer {  
    public static void init() {
    	String text="Ancached";  
        StringReader sr=new StringReader(text);  
        IKSegmenter ik=new IKSegmenter(sr, true);  
        Lexeme lex=null;  
        try {
			while((lex=ik.next())!=null){  
			    Log.i("Tokenizer_init", lex.getLexemeText());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static List<String> getTokens(String text) {
		List<String> tokens = new ArrayList<String>();
		StringReader sr=new StringReader(text);  
        IKSegmenter ik=new IKSegmenter(sr, true);  
        Lexeme lex=null;  
        try {
			while((lex=ik.next())!=null){  
			    tokens.add(lex.getLexemeText());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tokens;
	}
    
    public static String getString(String text) {
		String tokens = "";
		StringReader sr=new StringReader(text);  
        IKSegmenter ik=new IKSegmenter(sr, true);
        Lexeme lex=null;  
        try {
			while((lex=ik.next())!=null){  
			    tokens += lex.getLexemeText()+"|";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tokens;
	}
    
    @SuppressWarnings("resource")
	public static String getString2(String text) {
		String tokens = "";
		Analyzer anal=new IKAnalyzer(true);       
        StringReader reader=new StringReader(text);  
        //分词  
        TokenStream ts= anal.tokenStream("", reader);  
        CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);  
        //遍历分词数据  
        try {
			while(ts.incrementToken()){  
				tokens += term.toString()+"|";  
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        reader.close();
		return tokens;
	}
}
