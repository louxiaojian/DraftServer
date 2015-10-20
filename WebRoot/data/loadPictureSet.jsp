<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<!DOCTYPE html>
<html>
<head>
<title>享秀</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/data/css.css" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <meta name="format-detection" content="telephone=no">
  <meta name="renderer" content="webkit">
  <meta http-equiv="Cache-Control" content="no-siteapp" />
  <link rel="stylesheet" href="<%=request.getContextPath()%>/data/assets/css/amazeui.min.css"/>
<link rel="shortcut icon"
	href="<%=request.getContextPath()%>/data/images/pandoraLogo.ico"
	type="image/x-icon" />
<script type="text/javascript"
	src="<%=request.getContextPath()%>/data/jquery-1.7.2.min.js">
	</script>
	<style>
    @media only screen and (min-width: 641px) {
      .am-offcanvas {
        display: block;
        position: static;
        background: none;
      }

      .am-offcanvas-bar {
        position: static;
        width: auto;
        background: none;
        -webkit-transform: translate3d(0, 0, 0);
        -ms-transform: translate3d(0, 0, 0);
        transform: translate3d(0, 0, 0);
      }
      .am-offcanvas-bar:after {
        content: none;
      }

    }

    @media only screen and (max-width: 640px) {
      .am-offcanvas-bar .am-nav>li>a {
        color:#ccc;
        border-radius: 0;
        border-top: 1px solid rgba(0,0,0,.3);
        box-shadow: inset 0 1px 0 rgba(255,255,255,.05)
      }

      .am-offcanvas-bar .am-nav>li>a:hover {
        background: #404040;
        color: #fff
      }

      .am-offcanvas-bar .am-nav>li.am-nav-header {
        color: #777;
        background: #404040;
        box-shadow: inset 0 1px 0 rgba(255,255,255,.05);
        text-shadow: 0 1px 0 rgba(0,0,0,.5);
        border-top: 1px solid rgba(0,0,0,.3);
        font-weight: 400;
        font-size: 75%
      }

      .am-offcanvas-bar .am-nav>li.am-active>a {
        background: #1a1a1a;
        color: #fff;
        box-shadow: inset 0 1px 3px rgba(0,0,0,.3)
      }

      .am-offcanvas-bar .am-nav>li+li {
        margin-top: 0;
      }
    }

    .my-head {
      text-align: center;/*margin-bottom: 10px;*/
    }

    .my-button {
      position: fixed;
      top: 0;
      right: 0;
      border-radius: 0;
    }
    .my-sidebar {
      padding-right: 0;
      border-right: 1px solid #eeeeee;
    }

    .my-footer {
      border-top: 1px solid #eeeeee;
      padding: 10px 0;
      margin-top: 10px;
      text-align: center;
    }
    
  </style>
</head>
<%
	response.setHeader("Cache-Control","max-age=1800");
%>
<body style="background-color: #f8f8f8;color: block;">
<div class="am-g am-g-fixed">
  <div class="am-u-md-9 am-u-md-push-3">
    <div class="am-g">
<header class="am-g my-head">
  <div class="am-u-sm-12 am-article" style="padding-left:0px;padding-right: 0px;">
    		<div class="am-article-title">
    			<img src="${cycle.webTitleUrl }" alt="" class="" width="100%">
    		</div>
        <c:if test="${pictureSet!=null }">
    		<div class="vote" style="" align="center">
    			<input type="button" value="我也要参加" class="button" onclick="downloadApp()">
    		</div>
        </c:if>
  </div>
</header>
        <c:if test="${pictureSet!=null }">
        <div class="am-cf am-article" style="width: 100%;background-color: white;margin-left: auto;margin-right: auto;">
          <div class="user">
          	<div class="headerPic">
          		<img src="${pictureSet.user.headPortrait }" alt="" class="am-comment-avatar" width="48" height="48">
          	</div>
          	<div class="userInfo">
	          	<div style="float: left;">
	          		<p style="width: 100%;margin: 0;font-weight:bold;float: left;" class="title">${fn:substring(pictureSet.user.username, 0, 12) }<c:if test="${fn:length(pictureSet.user.username)>12 }">...</c:if>  </p>
	          	</div>
	          	<div style="float: right;">
	          		<p style="width: 100%;margin: 0;color: rgba(51, 51, 51, 0.8);float: right;" class="title">当前票数：<span id="vote">${pictureSet.votes }</span></p>
	          	</div>
          	</div>
          <div class="headerPic" style="width: 100%;height: auto;margin-bottom: -10px;">
          	<p class="descs">${pictureSet.descs }</p>
          </div>
          </div>
          </div></c:if>
      <div class="am-u-sm-11 am-u-sm-centered">
        <div class="am-cf am-article">
        <c:if test="${pictureSet!=null }">
          <p style="text-align: right;margin: 0;font-size: 12px;color: rgba(51, 51, 51, 0.6);"><fmt:formatDate value="${pictureSet.uploadDate}" pattern="MM/dd"/></p>
          <c:forEach var="photo" items="${photos }" varStatus="vs">
          	<c:if test="${vs.count!= fn:length(photos)}">
	         	<div style="background-color: white;margin: 0px;padding: 0px;">
	         	<img src="${photo.photoUrl }" alt="" width="100%" style="padding: 15px 15px 0px 15px;">
	         	</div>
          	</c:if>
          	<c:if test="${vs.count== fn:length(photos)}">
	         	<div class="lastPhoto">
	         	<img src="${photo.photoUrl }" alt="" width="100%" style="padding: 15px 15px 15px 15px;">
	         	</div>
          	</c:if>
          </c:forEach>
          <c:if test='${themeId!=null&&themeId!=0&&themeId!="" }'>
    		<div align="center">
    			<input type="button" value="为ta投票" class="button1" onclick="vote_pic()">
          		<p class="zhu">注：每人每天一个主题下限投3票，使用享秀APP还可以再投3票。</p>
          	</div>
          </c:if> 
          </c:if>
        </div>
      </div>
    </div>
  </div>
</div>
<footer class="my-footer">
  <p style="margin-bottom: -5px;">享秀<br><small>© Copyright x-show.net 北京智美点心科技有限公司.</small></p>
</footer>
<!--[if lt IE 9]>
<script src="http://libs.baidu.com/jquery/1.11.1/jquery.min.js"></script>
<script src="http://cdn.staticfile.org/modernizr/2.8.3/modernizr.js"></script>
<script src="assets/js/amazeui.ie8polyfill.min.js"></script>
<![endif]-->

<!--[if (gte IE 9)|!(IE)]><!-->
<script src="assets/js/jquery.min.js"></script>
<!--<![endif]-->
<script src="assets/js/amazeui.min.js"></script>
</body>
</html>
  <script type="text/javascript">
  	function vote_pic(){
  		//alert(${currentUser.id}+"-----"+${pictureSet.id});
  		var currentUserId='<%=request.getSession().getAttribute("currentUserId")%>';
  		//if(('${code}'!=null&&'${code}'!=""&&'${code}'!="null")||(currentUserId!=null&&currentUserId!="null"&&currentUserId!="")){
  		if(currentUserId!=null&&currentUserId!="null"&&currentUserId!=""){
  			var params = {"currentUserId": currentUserId,"pictureSetId":'${pictureSet.id}'};  
  			var actionUrl = "<%=request.getContextPath()%>/photo_vote.action";
  			$.ajax({  
  				  url : actionUrl,  
  			      type : "post", 
  			      data : params,  
  			      dataType : "json",  
  			      cache : false,  
  			      error : function(textStatus, errorThrown) {
  			    	  alert("系统错误");
  			      },  
  			      success : function(data, textStatus) {
  			      	if(data.state=='0'){
  			      		if(data.result.state=='0'){
  			      			document.getElementById("vote").innerHTML=document.getElementById("vote").innerHTML*1+1;
  			      			alert("投票成功，您还有"+data.result.surplusVotes+"票");
  			      		}else{
  			      			alert("今日票数已用尽！");
  			      		}
  			      	}else{
  			      		alert(data.errorMsg);
  			      	}
  			    }  
  			});
  		}else{
  	  		var codeUrl= 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx81b0d978030f90aa&redirect_uri='+encodeURIComponent('http://pandora.hdlocker.com/draftServer/photo_loadPictureSet.action?pictureSetId=${pictureSet.id}&themeId=${themeId}')+'&response_type=code&scope=snsapi_userinfo&state='+Math.round(Math.random()*10000000)+'#wechat_redirect';
  	  		window.location.href=codeUrl;
  		}
  	}
	function closeDiv(){
		document.getElementById("div1").style.display="none"
	}
	function downloadApp(){
		window.location.href="https://itunes.apple.com/cn/app/xiang-xiu/id1034657726?mt=8";
	}
  </script>