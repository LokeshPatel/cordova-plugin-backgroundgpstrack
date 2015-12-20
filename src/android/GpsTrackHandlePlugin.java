package com.gpstracksystem.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class GpsTrackHandlePlugin extends CordovaPlugin{

	public static final String LOG_TAG = "GpsTrackHandlePlugin";
	public static final String Shared_FILENAME = "GpsTrackHandleDetails";
	public static final String Shared_GPS_FILENAME = "GpsHandlerDetails";
    private static CallbackContext gpsTrackHandleContext;
	private static Context contextapp = null;
	@Override
	public boolean execute(final String action, final JSONArray data,final CallbackContext callbackContext) {
		    contextapp = this.cordova.getActivity().getApplicationContext();
		    gpsTrackHandleContext = callbackContext;
 		    if ("start".equals(action)) {
 		    	//GpsTrackHandlePlugin.sendEvent("start");
 		    	cordova.getThreadPool().execute(new Runnable() {
					public void run() {
						 try{
							JSONArray setJsonArrayValue =  data.getJSONObject(0).getJSONArray("ServerDetails");
						    String intervalTime =   data.getJSONObject(0).getString("IntervalTime");
						    String intervalDist = data.getJSONObject(0).getString("IntervalDistance");
						    String serverURL =  data.getJSONObject(0).getString("ServerURL");
						    String getHeaderTypeValue = "";
							 try{
							    getHeaderTypeValue = data.getJSONObject(0).getString("Content-type");
							  }
							 catch(Exception e)
							 {
								 
							 }
						    stopActiveService(contextapp);
							saveValueInShared(setJsonArrayValue,serverURL,getHeaderTypeValue,intervalTime,intervalDist,contextapp);
							contextapp.startService(new Intent(contextapp, GpsBackGroundService.class));
						 }
						 catch(JSONException e)
						 {}
						}
					});
 		} else if ("stop".equals(action)) {
 			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					 stopActiveService(contextapp);
				}
			});
			
		}else {
			Log.e(LOG_TAG, "Invalid action : " + action);
			callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
			return false;
		}
 		    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,action);
 		    pluginResult.setKeepCallback(true);
 			gpsTrackHandleContext.sendPluginResult(pluginResult);
 	   	return true;
	}

	public void saveValueInShared(JSONArray serverDetails,String ServerURL,String headerTypeValue,String timeInterval,String distanceInterval,Context ctx) 
	{
	 try{
		 SharedPreferences sharedpreferences = ctx.getSharedPreferences(GpsTrackHandlePlugin.Shared_FILENAME,Context.MODE_PRIVATE); 
		 SharedPreferences.Editor editor = sharedpreferences.edit();
		 int i =serverDetails.length();
		 while(i>0)
		 {
			i--;
			String Prefrencekey = "Key"+i;
			String PrefrenceValueKey = "Value"+i;
			String getParams = serverDetails.getJSONObject(i).getString("params");
			String getParamsValue = serverDetails.getJSONObject(i).getString("value");
			editor.putString(Prefrencekey, getParams);
			editor.putString(PrefrenceValueKey, getParamsValue);
		}
		 editor.putInt("NumberOfValues", serverDetails.length());
		 editor.putString("ServerURL", ServerURL);
		 editor.putString("Content-Type", headerTypeValue);
		 if(timeInterval!=null)
    	  {
    		  int setTime = 0;
			  try {
    			  setTime = Integer.parseInt(timeInterval);
    			} catch(NumberFormatException nfe) {
    			   System.out.println("Could not parse " + nfe);
    			}   
			  editor.putInt("IntervalTime", setTime);
    	  }	
        if(distanceInterval!=null)
    	  {
    		int setDistance = 100;  
    	    try {
    			  setDistance = Integer.parseInt(distanceInterval);
    			} catch(NumberFormatException nfe) {
    			   System.out.println("Could not parse " + nfe);
    			}   
    	    editor.putInt("IntervalDistance", setDistance);
    	  }
         editor.putInt("BGServiceID", 0);
		 editor.commit();
		}
         catch(Exception e){} 
	}	
	
	public void stopActiveService(Context ctx)
	{
	 try{
	 	  SharedPreferences sharedpreferences = ctx.getSharedPreferences(GpsTrackHandlePlugin.Shared_FILENAME, Context.MODE_PRIVATE);
		  int value = sharedpreferences.getInt("BGServiceID",0);
		   sharedpreferences.edit().clear().commit();
		   SharedPreferences gpsSavePreferences = ctx.getSharedPreferences(GpsTrackHandlePlugin.Shared_GPS_FILENAME, Context.MODE_PRIVATE);
		   gpsSavePreferences.edit().clear().commit();
		   if(value!=0)
		   {	  
		    contextapp.stopService(new Intent(contextapp, GpsBackGroundService.class));
		   }
		}
		catch(Exception e)
		{
			
		}
	}
	
//	public static void sendEvent(String _string) {
//		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,_string);
//		pluginResult.setKeepCallback(true);
//		if (gpsTrackHandleContext != null) {
//			gpsTrackHandleContext.sendPluginResult(pluginResult);
//		}
//	}

	public static void sendError(String message) {
		PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR,
				message);
		pluginResult.setKeepCallback(true);
		if (gpsTrackHandleContext != null) {
			gpsTrackHandleContext.sendPluginResult(pluginResult);
		}
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
	}

	@Override
	public void onPause(boolean multitasking) {
		super.onPause(multitasking);
	}

	@Override
	public void onResume(boolean multitasking) {
		super.onResume(multitasking);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}