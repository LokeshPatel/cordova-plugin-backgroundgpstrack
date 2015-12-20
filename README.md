# Cordova plugin background gps track
Cordova plugin background gps track Service With Cordova/Phonegap android application




## Master branch:
 
 ```
cordova plugin add https://github.com/LokeshPatel/cordova-plugin-backgroundgpstrack.git
 ```
## local folder:

 ``` 
cordova plugin add cordova-plugin-backgroundgpstrack --searchpath path

```

## 1) Start service 

 //add service value in ServerDetails like :
 
  [{ "params":"id","value":"1"},{ "params":"pwd","value":"password"}, ..... ,{ "params":"lat","value":""},{ "params":"lon","value":""}]
 
 ```  
    var option = {"ServerDetails":[{"params":"id","value":""},
                  {"params":"lat", "value":""}, // lat & lon params key & value user params starting set blank
                  {"params":"lon","value":""}],
              "IntervalTime":"30", // Time set in second
              "IntervalDistance":"100", // Distance set in meter "now not working distance"
              "ServerURL":setURL, // Server url 
              "Content-type":"application/json" // if content type based on url then used
          }
          navigator.gpstrack.start(function(a){console.log("start")},function(){console.log("Error")},option);
     
 ``` 
  
## 2) Stop service 
  ```
  navigator.gpstrack.stop(function(a){console.log("stop")},function(){console.log("Error")});
  
```