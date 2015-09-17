<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0" />
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<link rel="shortcut icon"
	href="<%=request.getContextPath()%>/data/images/pandoraLogo.ico"
	type="image/x-icon" />
<title>${cycle.themeTitle }</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/data/css.css" />
</head>
<%
	response.setHeader("Cache-Control","max-age=1800");
%>
<body id="activity-detail" onload="addViews()" class="zh_CN " style="background-color: #000000;">
	<div class="rich_media_day">
	<div id="title" class="rich_media_meta_list" style="text-align: left;width: 100%;height:30%;background-color: #000000;">
		<p class="rich_media_title" style="text-align: center;color: #ffffff;padding-left: 15px;padding-right: 15px;padding-top: 25px;line-height: 34px;padding-bottom: 15px;"
						id="activity-name">${cycle.themeTitle }</p>
		<%--<p style="float: left;margin-top:-2px;color: #fff;padding-left: 17px;font-size: 12px;padding-bottom: 5px;">${cycle.starttime } </p> 
		<p style="text-align: right;font-size: 12px;margin-top:-2px;color: #fff;padding-right: 15px;padding-bottom: 5px;">--%>
<%--			<img alt="" src="<%=request.getContextPath()%>/data/images/eye.png" style="width: 16px;margin-bottom: -1.2px;">--%>
<%--			<span style="color: #fff;padding-left: 3px;">${dataImgTable.views}</span>--%>
<%--		</p>--%>
	</div>
	<div class="rich_media_inner_day" style="background-color: #000000;">
		<div id="page-content">
			<div id="img-content">
				<div class="rich_media_content_day" id="js_content" style="word-wrap: break-word;word-break:break-all;background-color: #000000;">
					<p style="margin-top: 12px;">
						<img alt="" src="${cycle.bgUrl}">
						<img alt="" src="${cycle.detailImageUrl}">
						<%--<span
							style="max-width: 100%; word-wrap: break-word !important; box-sizing: border-box !important;padding-top: 100px;">${cycle.descs}</span>
					 --%>
					</p>
				</div>
			<div style="height: 64px;"></div>
		</div>
	 	</div>
	</div>
	</div>
</body>
</html>