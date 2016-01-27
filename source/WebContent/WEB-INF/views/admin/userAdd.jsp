<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">


	$(function() {
		$('#roleIds').combotree({
		    url: '${ctx}/role/tree',
		    multiple: false,
		    required: true,
		    panelHeight : 'auto',
		    value: '${traderRoleId}'
		});
		
		$('#userAddForm').form({
			url : '${ctx}/user/add',
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
					parent.$.messager.alert('提示', result.msg, 'warning');
				}
			}
		});
		
		pubMethod.bind('usertype', 'usertype');
	});
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
		<form id="userAddForm" method="post">
			<table class="grid">
				<tr>
					<td>login name</td>
					<td><input name="loginname" type="text" placeholder="please input login name" class="easyui-validatebox" data-options="required:true" value=""></td>
					<td>name</td>
					<td><input name="name" type="text" placeholder="please input name" class="easyui-validatebox" data-options="required:true" value=""></td>
				</tr>
				<tr>
					<td>password</td>
					<td><input name="password" type="password" placeholder="please input password" class="easyui-validatebox" data-options="required:true"></td>
					<td>sex</td>
					<td><select name="sex" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<c:forEach items="${sexList}" var="sexList">
							<option value="${sexList.key}" >${sexList.value}</option>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>role</td>
					<td><select id="roleIds"  name="roleIds" disabled="disabled"  style="width: 140px; height: 29px;"></select></td>
				</tr>
			</table>
		</form>
	</div>
</div>