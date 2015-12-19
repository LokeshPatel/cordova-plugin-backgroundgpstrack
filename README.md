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
 
  [{ "params":"id","value":"1"},{ "params":"pwd","value":"password"}, .....]
 
 ```  
   var option = { 
               "ServerDetails":[{ "params":" ","value":" "}],
              "IntervalTime":textTimeVal, // Time set in second
              "IntervalDistance":"100", // Now only work with 100, don't change this value.
              "ServerURL":"http://demo.com/demo/" // add your server url
          }
   navigator.gpstrack.start(function(a){console.log("start")},function(){console.log("Error")},option);
 ``` 
  
## 2) Stop service 
  ```
  navigator.gpstrack.stop(function(a){console.log("stop")},function(){console.log("Error")});
  
```