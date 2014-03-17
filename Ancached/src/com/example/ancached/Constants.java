package com.example.ancached;

import com.baidu.location.BDGeofence;

/**
 *此处以奎科大厦为例，设置一个地理围栏的相关参数，如
 * @param GEOFENCE_ID 			: 	围栏ID
 * @param GEOFENCE_COORD_TYPE 	:	坐标类型
 * @param GEOFENCE_LONGTITUDE	: 	中心点经度
 * @param GEOFENCE_LATITUDE		:	中心点纬度
 * @param GEOFENCE_RADIUS_TYPE	:	中心点半径类型
 * @param GEOFENCE_EXPIRATION	:	地理围栏生效时间
 */
public class Constants {
	public static final String GEOFENCE_ID = "奎科大厦";
	public static final String GEOFENCE_COORD_TYPE = BDGeofence.COORD_TYPE_GCJ;
	public static final double GEOFENCE_LONGTITUDE = 116.30677;
	public static final double GEOFENCE_LATITUDE = 40.04173;
	public static final int GEOFENCE_RADIUS_TYPE = 1;
	public static final long GEOFENCE_EXPIRATION = 10L * (3600 * 1000);//默认10小时
}
