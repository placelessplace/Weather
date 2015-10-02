package com.test.weather.util;

import android.R.integer;
import android.R.string;
import android.text.TextUtils;

import com.test.weather.model.City;
import com.test.weather.model.County;
import com.test.weather.model.Province;
import com.test.weather.model.WeatherDB;

public class Utility {

	 //�����ʹ������������ص�ʡ������
	public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB,String response){
		if (!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length>0){
				for (String p :allProvinces){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//���������������ݴ洢��province��
					weatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	//�����ʹ������������ص��м�����
	public static boolean handleCitiesResponse(WeatherDB weatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length>0){
				for (String c : allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//�����������������ݴ洢��city��
					weatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	
	//�����ʹ������������ص��ؼ�����
	public static boolean handleCountiesResponse(WeatherDB weatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0 ){
				for (String c:allCounties){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//���������������ݴ洢��county��
					weatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
}