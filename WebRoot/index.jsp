<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  <body>
    <form action="photo!realityVerification.action?userId=7&type=1&cycleId=1&cycleNo=201507001&descs=asdfyhh" id="pageFrom" name="" method="post" enctype="multipart/form-data">
		<br /><s:debug></s:debug>
		<fieldset class="fieldsetStyle">
			<legend>
				<font size="3">基本信息</font>
			</legend>
			<div class="fieldsetContent">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					class="infoTableSpace">
					<input type="hidden" name="imgName" id="imgName"/>
					<tr>
						<td align="right">上传图片：</td>
						<td align="left"><input type="file" id="image"
							name="image" value="${image}" onchange="uploadImg()" />
						</td>
						<td align="left"><input type="file" id="image"
							name="image" value="${image}" onchange="uploadImg()" />
						</td>
						<td align="left"><input type="file" id="image"
							name="image" value="${image}" onchange="uploadImg()" />
						</td>
					</tr>
					<tr>
						<td colspan="4" align="center"><input type="submit"
							id="submitBtn" value="保 存" class="button_b" /> 
						</td>
					</tr>
				</table>
			</div>
		</fieldset>
	</form>
	<form action="user!register.action" method="post">
		<table>
			<tr><td>用户名：<input name="loginname" type="text" /></td></tr>
			<tr><td>密码：<input name="pwd" type="password" /></td></tr>
			<tr><td><input type="submit" value="登录"/></td></tr>
		</table>
	</form>
	<form action="photo!comment.action?userId=5&pictureSetId=1" method="post">
		<table>
			<tr><td>评论：
			<textarea rows="6" cols="30" name="content"></textarea>
			</td></tr>
			<tr><td><input type="submit" value="登录"/></td></tr>
		</table>
	</form>
  </body>
</html>
