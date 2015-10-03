package com.test.weather.activity;


import java.util.HashMap;
import java.util.Map;

import com.test.weather.R;
import com.test.weather.receiver.AutoUpdateReceiver;
import com.test.weather.service.AutoUpdateService;
import com.test.weather.util.HttpCallbackListener;
import com.test.weather.util.HttpUtil;
import com.test.weather.util.Utility;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout; 
	private TextView cityNameText;//显示城市名
	private TextView publishText;//显示发布时间
	private TextView weatherDespText;//显示天气描述信息
	private TextView temp1Text;//显示温度1
	private TextView temp2Text;//显示温度2
	private TextView currentDateText;//显示当前日期
	private Button switchCity;//切换城市按钮
	private Button refreshWeather;//更新天气按钮
	private enum WeatherKind{
		cloudy,fog,hailstone,light_rain,moderte_rain,overcast,rain_snow,sand_strom,rainstorm,
		shower_rain,snow,sunny,thundershower;
	}
	
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.weather_layout);
			//初始化各控件
			weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
			cityNameText = (TextView)findViewById(R.id.city_name);
			publishText = (TextView)findViewById(R.id.publish_text);
			weatherDespText = (TextView)findViewById(R.id.weather_desp);
			temp1Text = (TextView)findViewById(R.id.temp1);
			temp2Text = (TextView)findViewById(R.id.temp2);
			currentDateText = (TextView)findViewById(R.id.current_date);
			switchCity = (Button)findViewById(R.id.switch_city);
			refreshWeather = (Button)findViewById(R.id.refresh_weather);
			String countyCode = getIntent().getStringExtra("county_code");
			if (!TextUtils.isEmpty(countyCode)){
				//有县级代号时就去查询天气
				publishText.setText("同步中...");
				weatherInfoLayout.setVisibility(View.INVISIBLE);
				cityNameText.setVisibility(View.INVISIBLE);
				queryWeatherCode(countyCode);
			}else{
				//没有县级代号时就直接显示本地天气
				showWeather();
			}
			switchCity.setOnClickListener(this);
			refreshWeather.setOnClickListener(this);
		}
	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
			default:
				break;
		}
	}

	
		//查询县级代号所对应的天气代号
		private void queryWeatherCode(String countyCode) {
			String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
			queryFromServer(address,"countyCode");
		}
	
        //查询代码所对应的天气
		private void queryWeatherInfo(String weatherCode) {
			String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
			queryFromServer(address,"weatherCode");
		}

		//根据传入的地址和类型去向服务器查询天气代号或者天气信息
		private void queryFromServer(final String address,final String type){
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
				
				@Override
				public void onFinish(final String response) {
					if("countyCode".equals(type)){
						if(!TextUtils.isEmpty(response)){
							//从服务器返回的数据中解析出天气代号
							String[] array = response.split("\\|");
							if(array != null && array.length == 2){
								String weatherCode = array[1];
								queryWeatherInfo(weatherCode);
							}
						}
					}else if ("weatherCode".equals(type)){
						//处理服务器返回的天气信息
						Utility.handleWeatherResponse(WeatherActivity.this, response);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showWeather();
								
							}
						});
					}
				}
				
				@Override
				public void onError(Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							publishText.setText("同步失败");							
						}
					});
					
				}
			});
		}

		//从SharedPreferences文件中读取存储的天气信息，并显示到界面上
		private void showWeather(){
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			cityNameText.setText(prefs.getString("city_name", ""));
			temp1Text.setText(prefs.getString("temp1",""));
			temp2Text.setText(prefs.getString("temp2", ""));
			weatherDespText.setText(prefs.getString("weather_desp", ""));
			publishText.setText("今天"+prefs.getString("pulish_time", "")+"发布");
			currentDateText.setText(prefs.getString("current_date", ""));
			weatherInfoLayout.setVisibility(View.VISIBLE);
			cityNameText.setVisibility(View.VISIBLE);
			Intent intent = new Intent(this,AutoUpdateService.class);
			startService(intent);
			
			String weatherDesp = prefs.getString("weather_desp", "");  
	        weatherDespText.setText(weatherDesp);  
	        WeatherKind myWeather = weatherkind.get(weatherDesp);  
	        if (myWeather != null) {  
	            changeBackground(myWeather);  
	        }  
		}
		
		//添加静态的map对象存储String天气类型和枚举天气类型的对应关系
		private static Map<String, WeatherKind>weatherkind = new HashMap<String, WeatherKind>();
		static{
			weatherkind.put("多云", WeatherKind.cloudy);  
	        weatherkind.put("雾", WeatherKind.fog);  
	        weatherkind.put("冰雹", WeatherKind.hailstone);  
	        weatherkind.put("小雨", WeatherKind.light_rain);  
	        weatherkind.put("中雨", WeatherKind.moderte_rain);  
	        weatherkind.put("阴", WeatherKind.overcast);  
	        weatherkind.put("雨加雪", WeatherKind.rain_snow);  
	        weatherkind.put("沙尘暴", WeatherKind.sand_strom);  
	        weatherkind.put("暴雨", WeatherKind.rainstorm);  
	        weatherkind.put("阵雨", WeatherKind.shower_rain);  
	        weatherkind.put("小雪", WeatherKind.snow);  
	        weatherkind.put("晴", WeatherKind.sunny);  
	        weatherkind.put("雷阵雨", WeatherKind.thundershower);
		}


private void changeBackground(WeatherKind weather) {  
    View view = findViewById(R.id.background);  
    switch (weather) {  
    case cloudy:  
        view.setBackgroundDrawable(this.getResources().getDrawable(  
                R.drawable.cloudy));  
        break;  
    case fog:  
        view.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fog));  
        break;  
    case hailstone:  
        view.setBackgroundDrawable(this.getResources().getDrawable(  
                R.drawable.hailstone));  
        break;  
    case light_rain:  
        view.setBackgroundDrawable(this.getResources().getDrawable(  
                R.drawable.light_rain));  
        break;  
    case moderte_rain:  
        view.setBackgroundDrawable(this.getResources().getDrawable(  
                R.drawable.moderte_rain));  
        break;  
    case overcast:  
        view.setBackgroundDrawable(this.getResources().getDrawable(  
                R.drawable.overcast));  
        break;  
    case rain_snow:  
        view.setBackgroundDrawable(this.getResources().getDrawable(  
                R.drawable.rain_snow));  
        break;  
    case rainstorm:  
        view.setBackgroundDrawable(this.getResources().getDrawable(  
                R.drawable.rainstorm));  
        break;  
    case sand_strom:  
        view.setBackgroundDrawable(this.getResources().getDrawable(  
                R.drawable.sand_storm));  
        break;  
    case shower_rain:  
        view.setBackgroundDrawable(this.getResources().getDrawable(  
                R.drawable.shower_rain));  
        break;  
    case snow:  
        view.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.snow));  
        break;  
    case sunny:  
        view.setBackgroundDrawable(this.getResources()  
                .getDrawable(R.drawable.sunny));  
        break;  
    case thundershower:  
        view.setBackgroundDrawable(this.getResources().getDrawable(  
                R.drawable.thundershower));  
        break;  
    default:  
        break;  
    }  
}
}
