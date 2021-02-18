<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${sysName}</title>
<script type="text/javascript" src="${ctx}/js/jquery.js"></script>
<script type="text/javascript" src="${ctx}/js/cookie_util.js"></script>
<script type="text/javascript">
	
	var USERNAME_COOKIE_NAME = "username";
	
	$(function() {
		var username = $("input[name=username]");
		username.val(Cookie.get(USERNAME_COOKIE_NAME));
		$("form").submit(function() {
			Cookie.set(USERNAME_COOKIE_NAME, $.trim(eleName.val()), null, 7 * 24 * 60);
		});
		//验证码
		drawCaptcha();
	});
	
	function drawCaptcha() {
		$.ajax("${appctx}/showCaptcha").done(function(data) {
			console.log(data);
			$("#captchaImg").attr("src", data.imgData);
		}).fail(function() {
			alert("验证码加载失败");
		});
	}
</script>
</head>
<body>


<c:if test="${empty loginUser}">
<c:if test="${not empty errorMsg}">
	<p style="color:red;font-weight:bold;">${errorMsg}</p>
</c:if>
<form action="/login" method="post">
	<p>账号：<input type="text" name="username" autocomplete="off" /></p>
	<p>密码：<input type="password" name="passwd" autocomplete="off" /></p>
	<p>验证码：<input style="width:80px;" type="text" name="captcha" autocomplete="off" /><img src="" onclick="drawCaptcha();" id="captchaImg" style="cursor:pointer;"></p>
	<p><input type="submit" value="登录" /></p>
</form>
</c:if>

<c:if test="${not empty loginUser}">
<p>欢迎：${loginUser}
	<button style="margin-left:20px;" onclick="location.href='http://www.chenfeng.com:8080/logout'">退出</button>
</p>
<ul>
	<c:forEach items="${apps }" var="app">
		<li><a href="${app.homeUrl }" target="_blank">${app.name}</a></li>
	</c:forEach>
</ul>
</c:if>

<c:forEach items="${sysList}" var="sys">
<script type="text/javascript" src="${sys.baseUrl}/setCookie?xtoken=${xtoken}"></script>
</c:forEach>
</body>
</html>