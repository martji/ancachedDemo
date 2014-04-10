package com.ancached.model;

import java.util.ArrayList;
import java.util.List;
import com.ancached.db.TrackLogItem;
import android.util.Log;

public class Interest {
	private static final List<String> TITLE_FILTER = new ArrayList<String>();
	static{
		TITLE_FILTER.add("hao123����-���������￪ʼ");
		TITLE_FILTER.add("�ֻ�������");
		TITLE_FILTER.add("�Ѻ���");
		TITLE_FILTER.add("�ֻ���Ѷ��");
		TITLE_FILTER.add("�ֻ������");
		TITLE_FILTER.add("�ֻ�������");
	}
	
	public static void update(String title) {
		if (!TITLE_FILTER.contains(title)){
			List<String> keys = Tokenizer.getTokens(title);
			Log.e("keys", keys.toString());
			updateITmodel(keys);
		}
	}

	private static void updateITmodel(List<String> keys) {
		// TODO Auto-generated method stub
		
	}
	
	public static void test(List<TrackLogItem> items){
		for (int i = 0; i < items.size(); i++){
			String title = items.get(i).getTitle();
			if (title != ""){
				Log.e("test_tokens", Tokenizer.getString(title));
			}
		}
	}
	
	public static void getInterest(){
		
	}
}
