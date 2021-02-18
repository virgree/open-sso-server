/**
 * Cookie操作工具类
 */
var Cookie = {

	get: function(name) {
		var cookies = document.cookie;
		cookies = cookies.split("; ")
		for (var i = 0, len = cookies.length; i < len; ++i) {

			var cookieItem = cookies[i];
			var pos = cookieItem.indexOf("=");
			var cname = cookieItem.substring(0, pos);
			var cval = unescape(cookieItem.substring(pos + 1));

			if (name == cname) {
				return cval;
			}
		}
	},

	set: function(name, val, path, expMinute, domain, secure) {
		var cookieItem = name + "=" + escape(val);
		
		if (path) {
			cookieItem += ";path=" + path;
		}
		
		if (expMinute) {
			cookieItem += ";expires=" + new Date(new Date().getTime() + expMinute * 60 * 1000).toGMTString();
		}
		
		if (domain) {
			cookieItem += ";domain=" + domain;
		}
		
		if (secure) {
			cookieItem += ";secure";
		}
		
		document.cookie = cookieItem;
		
	},
	del: function(name) {
		document.cookie = name + "=anyVal;expires=" + new Date(0).toGMTString();
	}
}