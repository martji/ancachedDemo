package com.example.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.ancached.R;
import com.example.cache.ImageMemoryCache.OnImageCallbackListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ImageCacheDemo extends Activity {
	public static final int COLUMNS = 2;// 列数
	public static final int IMAGEVIEW_DEFAULT_HEIGHT = 400;// 默认高度
	public static final String TAG_CACHE = "image_cache";// Tag
	public static final String DEFAULT_CACHE_FOLDER = new StringBuilder()
			.append(Environment.getExternalStorageDirectory().getAbsolutePath())
			.append(File.separator).append("Trinea").append(File.separator)
			.append("AndroidDemo").append(File.separator).append("ImageCache")
			.toString();// 文件夹路径
	private RelativeLayout parentLayout;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagecache);
		Context context = getApplicationContext();
		parentLayout = (RelativeLayout) findViewById(R.id.image_cache_parent_layout);
		initImageUrlList();// 初始化图片路径
		Log.v("Cache_Folder",DEFAULT_CACHE_FOLDER);
		IMAGE_CACHE.initData(this, TAG_CACHE);// load all data from db
		IMAGE_CACHE.setContext(context);
		IMAGE_CACHE.setCacheFolder(DEFAULT_CACHE_FOLDER);//二级存储位置 ImageSDCardCache

		int count = 0, viewId = 0x7F24FFF0;// viewId怎么确定的
		int verticalSpacing, horizontalSpacing;
		verticalSpacing = horizontalSpacing = getResources()
				.getDimensionPixelSize(R.dimen.dp_4);
		Display display = getWindowManager().getDefaultDisplay();
		int imageWidth = (display.getWidth() - (COLUMNS + 1)
				* horizontalSpacing)
				/ COLUMNS;
		for (String imageUrl : imageUrlList) {
			ImageView imageView = new ImageView(context);
			imageView.setId(++viewId);// ？
			imageView.setScaleType(ScaleType.CENTER);
			parentLayout.addView(imageView);

			// imageView layout参数配置
			LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView
					.getLayoutParams();
			layoutParams.width = imageWidth;
			layoutParams.topMargin = verticalSpacing;
			layoutParams.rightMargin = horizontalSpacing;
			int column = count % COLUMNS;// 列号
			int row = count / COLUMNS;// 行号
			if (row > 0) {
				layoutParams.addRule(RelativeLayout.BELOW, viewId - COLUMNS);
			}
			if (column > 0) {
				layoutParams.addRule(RelativeLayout.RIGHT_OF, viewId - 1);
			}
			layoutParams.height = IMAGEVIEW_DEFAULT_HEIGHT;

			// get image
			IMAGE_CACHE.get(imageUrl, imageView);
			count++;
		}
	}

	public static final ImageCache IMAGE_CACHE = new ImageCache(128, 512);// 一二级Cache的Thread
																			// pool
																			// size

	static {// 静态代码块
		OnImageCallbackListener imageCallBack = new OnImageCallbackListener() {

			@Override
			public void onPreGet(String imageUrl, View view) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetNotInCache(String imageUrl, View view) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetSuccess(String imageUrl, Bitmap loadedImage,
					View view, boolean isInCache) {
				// TODO Auto-generated method stub
				if (view != null && loadedImage != null) {
					ImageView imageView = (ImageView) view;
					imageView.setImageBitmap(loadedImage);
					// first time show with animation
					if (!isInCache) {
						imageView.startAnimation(getInAlphaAnimation(2000));
					}

					// auto set height accroding to rate between height and
					// weight
					LayoutParams imageParams = (LayoutParams) imageView
							.getLayoutParams();
					imageParams.height = imageParams.width
							* loadedImage.getHeight() / loadedImage.getWidth();
					imageView.setScaleType(ScaleType.FIT_XY);
				}
			}

			@Override
			public void onGetFailed(String imageUrl, Bitmap loadedImage,
					View view, FailedReason failedReason) {
				// TODO Auto-generated method stub
				Log.e(TAG_CACHE,
						new StringBuilder(128).append("get image ")
								.append(imageUrl)
								.append(" error, failed type is: ")
								.append(failedReason.getFailedType())
								.append(", failed reason is: ")
								.append(failedReason.getCause().getMessage())
								.toString());
			}

		};
		IMAGE_CACHE.setOnImageCallbackListener(imageCallBack);
		IMAGE_CACHE
				.setCacheFullRemoveType(new RemoveTypeLastUsedTimeFirst<Bitmap>());

		IMAGE_CACHE.setHttpReadTimeOut(10000);
		IMAGE_CACHE.setOpenWaitingQueue(true);
		IMAGE_CACHE.setValidTime(-1);
	}

    public static AlphaAnimation getInAlphaAnimation(long durationMillis) {
        AlphaAnimation inAlphaAnimation = new AlphaAnimation(0, 1);
        inAlphaAnimation.setDuration(durationMillis);
        return inAlphaAnimation;
    }
    
	private List<String> imageUrlList;

	private void initImageUrlList() {
		imageUrlList = new ArrayList<String>();
		imageUrlList
				.add("http://avatar.csdn.net/blogpic/20140222182141140.jpg");
		imageUrlList
				.add("http://www.pingwest.com/wp-content/uploads/2013/04/new_android_wallpaper.jpg");
		imageUrlList
				.add("http://www.199it.com/wp-content/uploads/2013/09/Android-Developer21.png");
		imageUrlList
				.add("http://www.androidanalyse.com/wp-content/uploads/2014/02/I-Love-Android-Wallpaper.jpeg");
		imageUrlList
				.add("http://img.uuhy.com/uploads/2010/10/Android_by_CoolPsTuts.jpg");
		imageUrlList
				.add("http://uffenorde.com/wp-content/uploads/2010/12/androidEvolution1920x1080.png");
		imageUrlList
				.add("http://bobbysonbroadway.com/wp-content/uploads/2014/01/Cute-Wallpapers-For-Android.jpg");
		imageUrlList.add("http://www.thebiblescholar.com/android_awesome.jpg");
		imageUrlList
				.add("http://u.img.huxiu.com/portal/201204/07/104525s2zpa2js6b8bdb91.png");
		imageUrlList
				.add("http://www.extremetech.com/wp-content/uploads/2011/08/android-fragmentation-640x451.jpg");
		imageUrlList
				.add("http://blogs.alfresco.com/wp/files/Android-Army-1.jpg");
	}
}
