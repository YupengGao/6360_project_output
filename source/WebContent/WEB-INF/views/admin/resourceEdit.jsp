<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	

	$(function() {
		
		$('#pid').combotree({
			url : '${ctx}/resource/tree',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto'
		});
		
		if ($(':input[name="id"]').val().length > 0) {
			$.post( '${ctx}/resource/get', {
				id : $(':input[name="id"]').val(),
			}, function(result) {
				if (result.id != undefined) {
					$('form').form('load', {
						'id' : result.id,
						'name' : result.name,
						'url' : result.url,
						'resourcetype' : result.resourcetype,
						'description' : result.description,
						'icon' : result.icon,
						'seq' : result.seq,
						'cstate':result.cstate
					});
					$('#pid').combotree('setValue',result.pid);
				}
			}, 'json');
		}
		
		$('#resourceEditForm').form({
			url : '${pageContext.request.contextPath}/resource/edit',
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
					parent.$.modalDialog.openner_treeGrid.treegrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_treeGrid这个对象，是因为resource.jsp页面预定义好了
					parent.layout_west_tree.tree('reload');
					parent.$.modalDialog.handler.dialog('close');
				}
			}
		});
		
	});
</script>
<div style="padding: 3px;">
	<form id="resourceEditForm" method="post">
		<table  class="grid">
			<tr>
				<td>Name</td>
				<td><input name="id" type="hidden"  value="${resource.id}" >
				<input name="name" type="text" placeholder="please input resource name" class="easyui-validatebox span2" data-options="required:true" ></td>
				<td>Type</td>
				<td><select name="resourcetype" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0">menu</option>
							<option value="1">button</option>
				</select></td>
			</tr>
			<tr>
				<td>URL</td>
				<td><input name="url" type="text" placeholder="please input resource url" class="easyui-validatebox span2" ></td>
				<td>Sequence</td>
				<td><input name="seq"  class="easyui-numberspinner" style="width: 140px; height: 29px;" required="required" data-options="editable:false"></td>
			</tr>
			<tr>
				<td>Icon</td>
				<td ><input  name="icon" /></td>
				<td>Status</td>
				<td ><select name="cstate" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0">normal</option>
							<option value="1">stop</option>
				</select></td>
			</tr>
			<tr>
				<td>Parent</td>
				<td colspan="3"><select id="pid" name="pid" style="width: 200px; height: 29px;"></select>
				<a class="easyui-linkbutton" href="javascript:void(0)" onclick="$('#pid').combotree('clear');" >clear</a></td>
			</tr>
			<tr>
				<td>Remark</td>
				<td colspan="3"><textarea name="description" rows="5" cols="50" ></textarea></td>
			</tr>
		</table>
	</form>
</div>
