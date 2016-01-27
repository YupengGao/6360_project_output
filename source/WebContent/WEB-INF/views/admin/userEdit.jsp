<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	$(function() {
		$('#roleIds').combotree({
			url : '${ctx}/role/tree',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto',
			multiple : false,
			required: true,
			cascadeCheck : false,
			value : '${user.roleIds}'
		});
		
		$('#userEditForm').form({
			url : '${ctx}/user/edit',
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
					parent.$.modalDialog.openner_dataGrid.datagrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
					parent.$.modalDialog.handler.dialog('close');
				} else {
					parent.$.messager.alert('error', result.msg, 'error');
				}
			}
		});
		
		pubMethod.bind('usertype', 'usertype');
	});
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
		<form id="userEditForm" method="post">
			<div class="light-info" style="overflow: hidden;padding: 3px;">
				<div>If you do not plan to modify password, please leave it blank!</div>
			</div>
			<table class="grid">
				<tr>
					<td>login name</td>
					<td><input name="id" type="hidden"  value="${user.id}">
					<input name="loginname" type="text" 
						<c:if test="${user.loginname=='admin'}">
							disabled="disabled" 
						</c:if>
					placeholder="please input login name" class="easyui-validatebox" data-options="required:true" value="${user.loginname}"></td>
					<td>name</td>
					<td><input name="name" type="text" placeholder="please input name" class="easyui-validatebox" data-options="required:true" value="${user.name}"></td>
				</tr>
				<tr>
					<td>password</td>
					<td><input type="text" 
						<c:if test="${user.loginname ne sessionInfo.loginname}">
							disabled="disabled" 
						</c:if>
					name="password"/></td>
					<td>sex</td>
					<td><select name="sex" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<c:forEach items="${sexList}" var="sexList">
							<option value="${sexList.key}" <c:if test="${sexList.key == user.sex}">selected="selected"</c:if>>${sexList.value}</option>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>role</td>
					<td><input  id="roleIds" name="roleIds"  disabled="disabled" style="width: 140px; height: 29px;"/></td>
				</tr>
			</table>
		</form>
	</div>
</div>