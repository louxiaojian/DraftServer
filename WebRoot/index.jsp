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
    <form action="photo_uploadPhoto.action?currentUserId=5&type=1&themeCycleId=4" id="pageFrom" name="" method="post" enctype="multipart/form-data">
		<fieldset class="fieldsetStyle">
			<legend>
				<font size="3">选秀上传</font>
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
	<!-- 
	<form action="user!register.action" method="post">
		<table>
			<tr><td>用户名：<input name="loginname" type="text" /></td></tr>
			<tr><td>密码：<input name="pwd" type="password" /></td></tr>
			<tr><td><input type="submit" value="登录"/></td></tr>
		</table>
	</form> -->
	<a href="photo_replyComment.action?currentUserId=20&pictureSetId=133&content="+encodeuri(encodeuri('就看见啊回复技能'))>asdf</a>
	<form action="photo_replyComment.action?currentUserId=20&pictureSetId=133" method="post">
		<table>
			<tr><td>评论：
			<textarea rows="6" cols="30" name="content"></textarea>
			</td></tr>
			<tr><td><input type="submit" value="登录"/></td></tr>
		</table>
	</form>
	<form action="user_uploadPhoto.action?currentUserId=22&type=0" method="post" enctype="multipart/form-data">
		<table>
		<tr><th>个人图集</th></tr>
			<tr><td><input type="file" id="image"
							name="image" value="" onchange="uploadImg()" /><input type="file" id="image"
							name="image" value="" onchange="uploadImg()" /><input type="file" id="image"
							name="image" value="" onchange="uploadImg()" /></td></tr>
			<tr><td><input type="submit" value="登录"/></td></tr>
		</table>
  <s:debug></s:debug>
	</form>
  </body>
</html>
