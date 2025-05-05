<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	/**
	 * request.getScheme() 获取协议名称，如 http 或 https
	 * request.getServerName() 获取服务器主机名，比如 localhost 或 www.example.com
	 * request.getServerPort() 获取服务器端口号，如 8080
	 * request.getContextPath() 获取当前 Web 应用的上下文路径（项目名），例如 /myApp
	 */
	String basePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(function () {
		// 给整个浏览器窗口添加键盘按下事件。e 代表“事件对象”，用来获取这次事件的详细信息，比如按了哪个键
		$(window).keydown(function (e){
			// e.keyCode==13 代表回车键
			if(e.keyCode==13){
				$("#loginBtn").click() // 触发登录按钮的点击操作
			}
		});

		//给"登录"按钮添加单击事件
		$("#loginBtn").click(function () {
			//收集参数
			var loginAct=$.trim($("#loginAct").val());
			var loginPwd=$.trim($("#loginPwd").val());
			var isRemPwd=$("#isRemPwd").prop("checked");
			//表单验证
			if(loginAct==""){
				alert("用户名不能为空");
				return;
			}
			if(loginPwd==""){
				alert("密码不能为空");
				return;
			}

			//发送请求
			$.ajax({
				url:'settings/qx/user/login.do',
				data:{
					loginAct:loginAct,
					loginPwd:loginPwd,
					isRemPwd:isRemPwd
				},
				type:'post',
				dataType:'json', // 将返回到前端页面的数据转成 json 格式
				// 回调函数根据返回的数据 data 做进一步处理
				success:function (data) {
					if(data.code=="1"){
						//跳转到业务主页面
						window.location.href="workbench/index.do";
					}else{
						//提示信息
						$("#msg").text(data.message);
					}
				},
				beforeSend:function (){
					$("#msg").text("正在验证用户信息");
				}
			});
		});
	});
</script>
</head>
<body>
	<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
		<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">里希不在内核 2025</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h1>登录</h1>
			</div>
			<form action="workbench/index.html" class="form-horizontal" role="form">
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<input class="form-control" id="loginAct" type="text" value="${cookie.loginAct.value}" placeholder="用户名">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" id="loginPwd" type="password" value="${cookie.loginPwd.value}" placeholder="密码">
					</div>
					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
						<label>
							<input type="checkbox" id="isRemPwd" checked> 十天内记住密码
						</label>
						&nbsp;&nbsp;
						<span id="msg" style="color: red"></span>
					</div>
					<button type="button" id="loginBtn" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>