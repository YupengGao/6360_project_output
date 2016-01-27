<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	$(function() {

		$('#editUserPwdForm').form({
			url : '${ctx}/user/editUserPwd',
			onSubmit : function() {
				progressLoad();
				var isValid = $(this).form('validate');
				if (!isValid) {
					progressClose();
				}
				return isValid;
			},
			success : function(result) {
				progressClose();
				result = $.parseJSON(result);
				if (result.success) {
					parent.$.messager.alert('prompt', result.msg, 'info');
					parent.$.modalDialog.handler.dialog('close');
				} else {
					parent.$.messager.alert('error', result.msg, 'error');
				}
			}
		});
	});
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title="" style="overflow: hidden;">
		<c:if test="${sessionInfo.name == null}">
			<div>login timed out，please relogin .</div>
			<script type="text/javascript" charset="utf-8">
				try {
					window.location.href='${ctx}/admin/index';
				} catch (e) {
				}
			</script>
		</c:if>
		<c:if test="${sessionInfo.name != null}">
			<form id="editUserPwdForm" method="post">
				<table>
					<tr>
						<th>Login Name</th>
						<td>${sessionInfo.name}</td>
					</tr>
					<tr>
						<th>Old Password</th>
						<td><input name="oldPwd" type="password" placeholder="please input old password" class="easyui-validatebox" data-options="required:true"></td>
					</tr>
					<tr>
						<th>New Password</th>
						<td><input name="pwd" type="password" placeholder="please input new password" class="easyui-validatebox" data-options="required:true"></td>
					</tr>
					<tr>
						<th>Repeat Password</th>
						<td><input name="rePwd" type="password" placeholder="please input new password again" class="easyui-validatebox" data-options="required:true,validType:'eqPwd[\'#editUserPwdForm input[name=pwd]\']'"></td>
					</tr>
				</table>
			</form>
		</c:if>
	</div>
</div>