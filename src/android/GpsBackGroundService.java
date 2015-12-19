package com.gpstracksystem.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;

public class GpsBackGroundService extends Service {
	  private String intervalTime,serviceURL;
      GPSTracker gps;
      static Timer timer = new Timer();
      int setTime = 0;
	  int setDistance = 0;
	  static ContentValues contentvalues;

	@SuppressWarnings("finally")
	public static String sendPost(String _url,Map<String,String> parameter,String lat, String lng)  {
		final String USER_AGENT = "Mozilla/5.0";
	    StringBuilder params=new StringBuilder("");
	    String result="";
	    try {
	    for(String s:parameter.keySet()){
	        params.append(s+"=");
            params.append(URLEncoder.encode(parameter.get(s),"UTF-8")+"&");
	    }
	    params.append("lat="+lat+"&lon="+lng);
       
	    String url =_url;
	    URL obj = new URL(_url);
	    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	    con.setRequestMethod("POST");
	    con.setRequestProperty("User-Agent", USER_AGENT);
	    con.setRequestProperty("Accept-Language", "UTF-8");
        con.setDoOutput(true);
        
	    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
	    outputStreamWriter.write(params.toString());
	    outputStreamWriter.flush();

	    int responseCode = con.getResponseCode();
	    System.out.println("\nSending 'POST' request to URL : " + url);
	    System.out.println("Post parameters : " + params);
	    System.out.println("Response Code : " + responseCode);

	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    String inputLine;
	    StringBuffer response = new StringBuffer();

	    while ((inputLine = in.readLine()) != null) {
	        response.append(inputLine + "\n");
	    }
	    in.close();

	        result = response.toString();
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	    }  catch (IOException e) {
	        e.printStackTrace();
	    }catch (Exception e) {
	        e.printStackTrace();
	    }finally {
	    return  result;
	    }
}
	

	  
  public void gpsTrackerOnDevice()
  {
	   SharedPreferences sharedpreferences = returnSharedPrefrence(); 
	   int serverValueCount = sharedpreferences.getInt("NumberOfValues", 0);
	   final String serviceURLurl = sharedpreferences.getString("ServerURL","No name defined");
	   int  timeInterval = sharedpreferences.getInt("IntervalTime", 0);
	   int i=0;
	   final Map<String,String> parameter = new HashMap<String, String>(); 
	   Location oldValue = getGPSSharedPreference();
	   gps = new GPSTracker(getApplicationContext(),timeInterval,setDistance,oldValue);
       // check if GPS enabled     
	   double latitude = 0 ;
	   double longitude = 0;
	   if(gps.canGetLocation()){
          latitude = gps.getLatitude();
          longitude = gps.getLongitude();
          saveGPSSharedPreference(gps.location);
          // \n is for new line
          if(gps.location == null)
        	  return;
          Log.d("get GPS Test =======", "Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
       }else{
           // can't get location
           // GPS or Network is not enabled
           // Ask user to enable GPS/network in settings
           gps.showSettingsAlert();
       } 
	   while(i<serverValueCount)
	  {
	    	String Prefrencekey = "Key"+i;
			String PrefrenceValueKey = "Value"+i;
	    	String paramskey = sharedpreferences.getString(Prefrencekey, "No name defined");
	    	String paramsValue = sharedpreferences.getString(PrefrenceValueKey, "No name defined");
	    	parameter.put(paramskey, paramsValue);
	    	i++;
	    }
	   final String  lat= ""+latitude;
	   final String  lng = ""+longitude;
	   Thread background = new Thread(new Runnable() {
	         public void run() {
	            try {
	                sendPost(serviceURLurl,parameter,lat,lng);
	            } catch (Throwable t) {
	                // just end the background thread
	                Log.i("Animation", "Thread  exception " + t);
	            }
	        }
		}); 
		background.start();
 }
  
	  
	  @Override
	  public void onCreate() {
	    // Start up the thread running the service.  Note that we create a
	    // separate thread because the service normally runs in the process's
	    // main thread, which we don't want to block.  We also make it
	    // background priority so CPU-intensive work will not disrupt our UI.
	    HandlerThread thread = new HandlerThread("ServiceStartArguments",Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
     }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	     Log.d("get GPS Test =======", "service starting");
	     SharedPreferences sharedpreferences = returnSharedPrefrence();  
	     setTime = sharedpreferences.getInt("IntervalTime", 0);//"No name defined" is the default value.
      	 setDistance   = sharedpreferences.getInt("IntervalDistance",0); //"No name defined" is the default value.
		  int BGServiceID   = sharedpreferences.getInt("BGServiceID",0);
		  if(BGServiceID != 0)
		   {
			  stopSelf(BGServiceID);
		   }
          Log.d("Test ======= + ====== + ====", "Service call in background == " + intervalTime +"==="+ serviceURL +"==="+ BGServiceID);
          updateServiceID(startId);   
          final Handler handler = new Handler();
          if(timer == null)
           timer = new Timer();
           TimerTask doAsynchronousTask = new TimerTask() {       
 	          @Override
 	          public void run() {
 	             handler.post(new Runnable() {
 	                  public void run() {       
 	                      try {
 	                    	   gpsTrackerOnDevice();
 	                    	} catch (Exception e) {
 	                          e.printStackTrace();
 	                      }
 	                  }
 	              });
 	          }
 	       };
 	      timer.schedule(doAsynchronousTask, 0, setTime*1000);
          return START_STICKY;
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	      // We don't provide binding, so return null
	      return null;
	  }

	  @Override
	  public void onDestroy() {
		     if(timer!=null)
		      timer.cancel();
	          timer = null;
	          updateServiceID(0);   
	           Log.d("get GPS Test =======", "service stop");   
	 }
	 
	  public void updateServiceID(int serviceID)
	  {
		  SharedPreferences sharedpreferences = returnSharedPrefrence(); 
 		  SharedPreferences.Editor editor = sharedpreferences.edit();
          editor.putInt("BGServiceID", serviceID);
          editor.commit();  
	  }
	  
	  public SharedPreferences returnSharedPrefrence()
	  {
		 return getApplicationContext().getSharedPreferences(GpsTrackHandlePlugin.Shared_FILENAME, Context.MODE_PRIVATE);
	  }
	  
	  public void saveGPSSharedPreference(Location addLocation)
	  {
		  SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(GpsTrackHandlePlugin.Shared_GPS_FILENAME, Context.MODE_PRIVATE);
		  SharedPreferences.Editor editor = sharedpreferences.edit();
		  Gson gson = new Gson();
		  String json = gson.toJson(addLocation);
		  editor.putString("locationObject", json);
		  editor.commit();  
	 }
	  
	 public Location getGPSSharedPreference()
	 {
		  SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(GpsTrackHandlePlugin.Shared_GPS_FILENAME, Context.MODE_PRIVATE);
		  Gson gson = new Gson();
		  String json = sharedpreferences.getString("locationObject", "");
		  if(json!="")
		  return gson.fromJson(json, Location.class);
		  else
		  return null;
	 }
 }