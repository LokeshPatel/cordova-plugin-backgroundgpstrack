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


## 1) Get Current Location
  ```
  navigator.gpstrack.getCurrentLocation(function(result){
     console.log(result.latitude + " == "+result.longitude );
   },function(e){
    console.log("Error" + e);
    });
  
  
```
## 2) Start service 

 //add service value in ServerDetails like :
  [{latitude: "",longitude: "","params1":"value","params2":"value","params3":"value","params4":"value", ......}]
 ```  
    var option = { "ServerDetails":[{latitude: "",longitude: "",  .....}],
                   "IntervalTime": 60, // Time set in second default time one min.
                   "IntervalDistance":"100", // Distance set in meter "now not working distance"
                   "ServerURL": setUrl //Server URL
                  };
          navigator.gpstrack.start(function(a){console.log("start")},function(){console.log("Error")},option);
     
 ``` 
  
## 3) Stop service 
  ```
  navigator.gpstrack.stop(function(a){console.log("stop")},function(){console.log("Error")});
  
```

<a href="https://www.paypal.me/LokeshPatel" target="_blank"><img src="https://dl.dropboxusercontent.com/s/r5azqieu9stu0pc/pay-now-button-afme.png?dl=0" alt="Count 0" width="160"/></a>
