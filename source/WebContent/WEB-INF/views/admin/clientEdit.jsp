<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	$(function() {

		$('#traderId').combotree({
		    url: '${ctx}/user/tree',
		    multiple: false,
		    required: true,
		    panelHeight : 'auto',
		    value : '${client.traderId}'
		});

		$('#roleIds').combotree({
			url : '${ctx}/role/tree',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto',
			multiple : false,
			required: true,
			cascadeCheck : false,
			editable : false,
			value : $.stringToList('${client.roleIds}')
		});
	
		$('#clientEditForm').form({
			url : '${ctx}/client/edit',
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
 	$.extend($.fn.validatebox.defaults.rules, {
		  phoneRex: {
		    validator: function(value){	 
			    var rex=/^[1-9]\d{9}$/;
			    if(rex.test(value)){
			      return true;
			    } else {
			       return false;
			    }  
		    },
		    message: 'please input correct phone number'
		  }
		});
		$.extend($.fn.validatebox.defaults.rules, {
			  zipCode: {
			    validator: function(value){	 
				    var rex=/^[1-9]\d{4}$/;
				    if(rex.test(value)){
				      return true;
				    } else {
				       return false;
				    }  
			    },
			    message: 'please input correct zip code'
			  }
			});
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
		<form id="clientEditForm" method="post">
			<div class="light-info" style="overflow: hidden;padding: 3px;">
				<div>If you do not plan to modify password, please leave it blank! </div>
			</div>
			<table class="grid">
				<tr>
					<td>login name</td>
					<td><input name="id" type="hidden"  value="${client.id}">
					<input name="addressId" type="hidden"  value="${client.addressId}">
					<input name="loginname" type="text" placeholder="please input login name" class="easyui-validatebox" data-options="required:true" value="${client.loginname}"></td>
					<td>name</td>
					<td><input name="name" type="text" placeholder="please input name" class="easyui-validatebox" data-options="required:true" value="${client.name}"></td>
				</tr>
				<tr>
					<td>password</td>
					<td><input type="text"
					<c:if test="${client.loginname ne sessionInfo.loginname}">
							disabled="disabled" 
						</c:if>
					 name="password"/></td>
					<td>phone</td>
					<td><input name="phone" type="text" placeholder="please input phone" class="easyui-validatebox" data-options="required:true,validType:'phoneRex'" value="${client.phone}"></td>
				</tr>
				<tr>
					<td>cell phone</td>
					<td><input name="cellPhone" type="text" placeholder="please input cell phone" class="easyui-validatebox" data-options="required:true,validType:'phoneRex'" value="${client.cellPhone}"></td>
					<td>email</td>
					<td><input name="email" type="text" placeholder="please input email" class="easyui-validatebox" data-options="required:true" value="${client.email}"></td>
				</tr>
				<tr>
					<td>city</td>
					<td><input name="city" type="text" placeholder="please input city" class="easyui-validatebox" data-options="required:true" value="${client.city}"></td>
					<td>zipcode</td>
					<td><input name="zipcode" type="text" placeholder="please input zipcode" class="easyui-validatebox" data-options="required:true,validType:'zipCode'" value="${client.zipcode}"></td>
				</tr>
				    <tr>
					<td>state</td>
					<td><input name="state" type="text" placeholder="please input state" class="easyui-validatebox" data-options="required:true" value="${client.state}"></td>
					<td>role</td>
					<td><input  id="roleIds" name="roleIds" disabled="disabled" style="width: 140px; height: 29px;"/></td>
				</tr>
				<tr>
					<td>trader</td>
					<td><input  id="traderId" name="traderId" disabled="disabled" style="width: 140px; height: 29px;"/></td>
				</tr>
			</table>
		</form>
	</div>
</div>