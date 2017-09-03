package cn.flyzy2005.fztutil.utils;

/**
 * Created by Fly on 2017/9/3.
 * 提供了百度坐标（BD09）、国测局坐标（火星坐标，GCJ02）、和WGS84坐标系之间的转换（https://github.com/xinconan/coordtransform）
 * 以及墨卡托与经纬度的转换
 */

public class CoordinateHelper {
    private static double X_PI = 3.14159265358979324 * 3000.0 / 180.0;
    private static double PI = 3.1415926535897932384626;
    private static double A = 6378245.0;
    private static double EE = 0.00669342162296594323;
    private static double M_PI = Math.PI;

    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     * 即 百度 转 谷歌、高德
     *
     * @param bd_lon
     * @param bd_lat
     * @return Double[lon, lat]
     */
    public static Double[] BD2GCJ(Double bd_lon, Double bd_lat) {
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        Double[] arr = new Double[2];
        arr[0] = z * Math.cos(theta);
        arr[1] = z * Math.sin(theta);
        return arr;
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     *
     * @param gcj_lon
     * @param gcj_lat
     * @return Double[lon, lat]
     */
    public static Double[] GCJ2BD(Double gcj_lon, Double gcj_lat) {
        double z = Math.sqrt(gcj_lon * gcj_lon + gcj_lat * gcj_lat) + 0.00002 * Math.sin(gcj_lat * X_PI);
        double theta = Math.atan2(gcj_lat, gcj_lon) + 0.000003 * Math.cos(gcj_lon * X_PI);
        Double[] arr = new Double[2];
        arr[0] = z * Math.cos(theta) + 0.0065;
        arr[1] = z * Math.sin(theta) + 0.006;
        return arr;
    }

    /**
     * WGS84转GCJ02
     *
     * @param wgs_lon
     * @param wgs_lat
     * @return Double[lon, lat]
     */
    public static Double[] WGS2GCJ(Double wgs_lon, Double wgs_lat) {
        if (outOfChina(wgs_lon, wgs_lat)) {
            return new Double[]{wgs_lon, wgs_lat};
        }
        double dlat = transformlat(wgs_lon - 105.0, wgs_lat - 35.0);
        double dlng = transformlng(wgs_lon - 105.0, wgs_lat - 35.0);
        double radlat = wgs_lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - EE * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((A * (1 - EE)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (A / sqrtmagic * Math.cos(radlat) * PI);
        Double[] arr = new Double[2];
        arr[0] = wgs_lon + dlng;
        arr[1] = wgs_lat + dlat;
        return arr;
    }

    /**
     * GCJ02转WGS84
     *
     * @param gcj_lon
     * @param gcj_lat
     * @return Double[lon, lat]
     */
    public static Double[] GCJ2WGS(Double gcj_lon, Double gcj_lat) {
        if (outOfChina(gcj_lon, gcj_lat)) {
            return new Double[]{gcj_lon, gcj_lat};
        }
        double dlat = transformlat(gcj_lon - 105.0, gcj_lat - 35.0);
        double dlng = transformlng(gcj_lon - 105.0, gcj_lat - 35.0);
        double radlat = gcj_lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - EE * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((A * (1 - EE)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (A / sqrtmagic * Math.cos(radlat) * PI);
        double mglat = gcj_lat + dlat;
        double mglng = gcj_lon + dlng;
        return new Double[]{gcj_lon * 2 - mglng, gcj_lat * 2 - mglat};
    }

    /**
     * 经纬度转墨卡托
     *
     * @param lon
     * @param lat
     * @return Double[lon, lat]
     */
    public static Double[] lonLat2Mercator(Double lon, Double lat) {
        Double[] xy = new Double[2];
        double x = lon * 20037508.342789 / 180;
        double y = Math.log(Math.tan((90 + lat) * M_PI / 360)) / (M_PI / 180);
        y = y * 20037508.34789 / 180;
        xy[0] = x;
        xy[1] = y;
        return xy;
    }

    /**
     * 墨卡托转经纬度
     *
     * @param mercatorX
     * @param mercatorY
     * @return Double[lon, lat]
     */
    public static Double[] Mercator2lonLat(Double mercatorX, Double mercatorY) {
        Double[] xy = new Double[2];
        double x = mercatorX / 20037508.34 * 180;
        double y = mercatorY / 20037508.34 * 180;
        y = 180 / M_PI * (2 * Math.atan(Math.exp(y * M_PI / 180)) - M_PI / 2);
        xy[0] = x;
        xy[1] = y;
        return xy;
    }

    private static Double transformlat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static Double transformlng(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    private static boolean outOfChina(Double lng, Double lat) {
        return (lng < 72.004 || lng > 137.8347) || ((lat < 0.8293 || lat > 55.8271));
    }
}
