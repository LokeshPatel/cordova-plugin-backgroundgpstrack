package com.gpstracksystem.plugin;

import java.util.Iterator;
import java.util.Timer;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class GpsTrackHandlePlugin extends CordovaPlugin{

	public static final String LOG_TAG = "GpsTrackHandlePlugin";
	public static final String Shared_FILENAME = "GpsTrackHandleDetails";
	private static CallbackContext gpsTrackHandleContext;
	private static Context contextapp = null;
	private  GPSTracker gps;
	@Override
	public boolean execute(final String action, final JSONArray data,final CallbackContext callbackContext) {
		    contextapp = this.cordova.getActivity().getApplicationContext();
		    gps = new GPSTracker(contextapp);
		    gpsTrackHandleContext = callbackContext;
 		    if ("start".equals(action)) {
 		    	if(gps.getLocationEnable())
 		    	{		
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
						 {
							 Log.e(LOG_TAG, "Invalid action : " + e);
						 }
						}
					});
 		    	}
 		    	else
 		    	{
 		    	alert("You currently have all location services for this device disabled", "Location Services Disabled", "Setting");
 		    	callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "Location Services Disabled"));
              }	
 		} else if ("stop".equals(action)) {
 			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					 stopActiveService(contextapp);
				}
			});
 		}
 		 else if ("getCurrentLocation".equals(action)) {
			gpsTrackHandleContext = callbackContext;
			if(gps.getLocationEnable())
		     {	
			   cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					   double latitude = 0 ;
					   double longitude = 0;
						 Looper.prepare();
				         Location location = gps.getLocation(10,100,null);
				         if(location == null)
				          {	  
				        
				        	  PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR);
								pluginResult.setKeepCallback(true);
								if (gpsTrackHandleContext != null) {
									gpsTrackHandleContext.sendPluginResult(pluginResult);
								} 
				          }
				          else
				          {	  
				        	  latitude = location.getLatitude();
					          longitude = location.getLongitude();
				          JSONObject obj = new JSONObject();
				            try {
				                obj.put("latitude", latitude);
				                obj.put("longitude", longitude);
				            } catch (JSONException e) {
				                // TODO Auto-generated catch block
				                e.printStackTrace();
				            }
				          PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,obj);
							pluginResult.setKeepCallback(true);
							if (gpsTrackHandleContext != null) {
								gpsTrackHandleContext.sendPluginResult(pluginResult);
							} 
				          Log.d("get GPS Test =======", "Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
				          }
				          Looper.loop();
				}
			});
		  }	
			 else
			   {
				 alert("You currently have all location services for this device disabled", "Location Services Disabled", "Setting");
				 callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "Location Services Disabled"));
			   }	
		}
 		else {
			Log.e(LOG_TAG, "Invalid action : " + action);
			callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
			return false;
		}
 		    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,action);
 		    pluginResult.setKeepCallback(true);
 			gpsTrackHandleContext.sendPluginResult(pluginResult);
 	   	return true;
	}
	
	 /**
     * Builds and shows a native Android alert with given Strings
     * @param message           The message the alert should display
     * @param title             The title of the alert
     * @param buttonLabel       The label of the button
     */
    public synchronized void alert(final String message, final String title, final String buttonLabel) {
    	final CordovaInterface cordova = this.cordova;

        Runnable runnable = new Runnable() {
            public void run() {

                AlertDialog.Builder dlg = createDialog(cordova); // new AlertDialog.Builder(cordova.getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                dlg.setMessage(message);
                dlg.setTitle(title);
                dlg.setCancelable(true);
                dlg.setPositiveButton(buttonLabel,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                         	    cordova.getActivity().startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                dlg.setNegativeButton("Cancel",
                        new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	dialog.dismiss();
                    }
                });
                dlg.setOnCancelListener(new AlertDialog.OnCancelListener() {
                    public void onCancel(DialogInterface dialog)
                    {
                        dialog.dismiss();
                    }
                });
                changeTextDirection(dlg);
            };
        };
        this.cordova.getActivity().runOnUiThread(runnable);
    }
   
    private AlertDialog.Builder createDialog(CordovaInterface cordova) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            return new AlertDialog.Builder(cordova.getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        } else {
            return new AlertDialog.Builder(cordova.getActivity());
        }
    }
    
  
    private void changeTextDirection(Builder dlg){
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        dlg.create();
        AlertDialog dialog =  dlg.show();
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            TextView messageview = (TextView)dialog.findViewById(android.R.id.message);
            messageview.setTextDirection(android.view.View.TEXT_DIRECTION_LOCALE);
        }
    }

	public void saveValueInShared(JSONArray serverDetails,String ServerURL,String headerTypeValue,String timeInterval,String distanceInterval,Context ctx) 
	{
	 try{
		 SharedPreferences sharedpreferences = ctx.getSharedPreferences(GpsTrackHandlePlugin.Shared_FILENAME,Context.MODE_PRIVATE); 
		 SharedPreferences.Editor editor = sharedpreferences.edit();
		 JSONObject object = serverDetails.getJSONObject(0);
		 Iterator iterator = object.keys();
		   while(iterator.hasNext()){
		    String key = (String)iterator.next();
		    String getParamsValue = object.getString(key);
			editor.putString(key+"_#value#", getParamsValue);
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
//		   SharedPreferences gpsSavePreferences = ctx.getSharedPreferences(GpsTrackHandlePlugin.Shared_GPS_FILENAME, Context.MODE_PRIVATE);
//		   gpsSavePreferences.edit().clear().commit();
		   if(value!=0)
		   {	  
		    contextapp.stopService(new Intent(contextapp, GpsBackGroundService.class));
		   }
		}
		catch(Exception e)
		{
			
		}
	}
	
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
