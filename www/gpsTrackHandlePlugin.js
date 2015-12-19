var exec = require('cordova/exec');
var gpsTrackHandlePlugin = {
	start:function(success,fail,option) {
		return cordova.exec(success,fail,"GpsTrackHandlePlugin","start",[option]);
	},
	stop:function(success,fail) {
		return cordova.exec(success,fail,"GpsTrackHandlePlugin","stop",[""]);
	}
};
module.exports = gpsTrackHandlePlugin;
