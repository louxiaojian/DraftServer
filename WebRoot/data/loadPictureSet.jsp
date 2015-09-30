<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
<title>图集</title>
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
      margin-top: 40px;
      text-align: center;
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
  <script type="text/javascript">
  	function vote_pic(){alert(${currentUser.id}+"-----"+${pictureSet.id});
  		var params = {"currentUserId": '1',"pictureSetId":'${pictureSet.id}'};  
		var actionUrl = "<%=request.getContextPath()%>/photo_vote.action";  
		$.ajax({  
			  url : actionUrl,  
		      type : "post", 
		      data : params,  
		      dataType : "json",  
		      cache : false,  
		      error : function(textStatus, errorThrown) {  
		      },  
		      success : function(data, textStatus) {
		      	if(data.state=='0'){
		      		alert(data.result);
		      		if(data.result.state=='0'){
		      			alert("投票成功，谢谢参与！");
		      		}else{
		      			alert("今日票数已用尽！");
		      		}
		      	}else{
		      		alert(data.errorMsg);
		      	}
		    }  
		});
  	}
  </script>
</head>
<%
	response.setHeader("Cache-Control","max-age=1800");
%>
<body style="background-color: white;color: block;">
<div class="am-g am-g-fixed">
  <div class="am-u-md-9 am-u-md-push-3">
    <div class="am-g">
      <div class="am-u-sm-11 am-u-sm-centered">
        <div class="am-cf am-article">
            <%--<img src="${cycle.bgUrl}" alt="" width="100%">
          <h2>主题介绍</h2>
          <p>${cycle.descs}</p>
          <h2>活动细则</h2>
          <p>${cycle.role}</p>
          <h2>活动时间</h2>
          <p><fmt:formatDate value="${cycle.starttime}" pattern="yyyy年MM月dd日"/> 至 <fmt:formatDate value="${cycle.endtime }" pattern="yyyy年MM月dd日"/></p>
          <h2>奖项设置</h2>
          <p>${cycle.awardSetting}</p>
          --%>
          <img src="${pictureSet.user.headPortrait }" alt="" class="am-comment-avatar" width="48" height="48">
          <p style="width: 90%" class="title">${pictureSet.user.username }<br/><fmt:formatDate value="${pictureSet.uploadDate}" pattern="yyyy-MM-dd"/></p>
          <img src="${pictureSet.user.headPortrait }" alt="" class="am-comment-avatar-right" style="" width="48" height="48" onclick="vote_pic()">
          <p>${pictureSet.descs }</p>
          <hr>
          <c:forEach var="photo" items="${photos }">
          	<img src="${photo.photoUrl }" alt="" width="100%">
          </c:forEach>
          <c:if test="${themeId!=null }"><a href="http://pandora.hdlocker.com/draftServer/photo_loadThemeCycle.action?themeCycleId=${themeId }">活动规则</a></c:if> 
        </div>
      </div>
    </div>
  </div>
</div>
<footer class="my-footer">
  <p>享秀<br><small>© Copyright XXX. by the AmazeUI Team.</small></p>
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