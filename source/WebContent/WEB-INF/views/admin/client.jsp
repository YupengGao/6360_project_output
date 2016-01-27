<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../inc.jsp"></jsp:include>
<meta http-equiv="X-UA-Compatible" content="edge" />
<c:if test="${fn:contains(sessionInfo.resourceList, '/client/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<c:if test="${fn:contains(sessionInfo.resourceList, '/client/delete')}">
	<script type="text/javascript">
		$.canDelete = true;
	</script>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>client manage</title>
	<script type="text/javascript">
	var dataGrid;
	$(function() {
	
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}' + '/client/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'loginname',
			sortOrder : 'asc',
			pageSize : 50,
			pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns : [ [ {
				width : '100',
				title : 'login name',
				field : 'loginname',
				sortable : true
			}, {
				width : '80',
				title : 'name',
				field : 'name',
				sortable : true
			}] ],
			columns : [ [ {
				width : '120',
				title : 'create time',
				field : 'createDate',
				sortable : true
			}, {
				width : '80',
				title : 'phone',
				field : 'phone',
				sortable : true
			}, {
				width : '80',
				title : 'cell phone',
				field : 'cellPhone',
				sortable : true
			}, {
				width : '150',
				title : 'email',
				field : 'email',
				sortable : true
			}, {
				width : '120',
				title : 'address',
				field : 'address',
				sortable : false
			}, {
				width : '80',
				title : 'status',
				field : 'status',
				sortable : true,
				formatter : function(value, row, index) {
					switch (value) {
					case 0:
						return 'in use';
					case 1:
						return 'stop';
					}
				}
			} , {
				field : 'action',
				title : 'operation',
				width : 100,
				formatter : function(value, row, index) {
					var str = '';
					if(row.isdefault!=0){
						if ($.canEdit) {
							str += $.formatString('<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >edit</a>', row.id);
						}
						if ($.canDelete) {
							str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
							str += $.formatString('<a href="javascript:void(0)" onclick="deleteFun(\'{0}\');" >delete</a>', row.id);
						}
					}
					return str;
				}
			}] ],
			toolbar : '#toolbar'
		});
	});
	
	function addFun() {
		parent.$.modalDialog({
			title : 'add',
			width : 500,
			height : 300,
			href : '${ctx}/client/addPage',
			buttons : [ {
				text : 'add',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler.find('#clientAddForm');
					f.submit();
				}
			} ]
		});
	}
	
	function deleteFun(id) {
		if (id == undefined) {//点击右键菜单才会触发这个
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {//点击操作里面的删除图标会触发这个
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.messager.confirm('ask', 'Are you sure to delete the client？', function(b) {
			if (b) {
				var currentUserId = '${sessionInfo.id}';/*当前登录用户的ID*/
				if (currentUserId != id) {
					progressLoad();
					$.post('${ctx}/client/delete', {
						id : id
					}, function(result) {
						if (result.success) {
							parent.$.messager.alert('info', result.msg, 'info');
							dataGrid.datagrid('reload');
						}
						else {
							parent.$.messager.alert('info', result.msg, 'error');
						}
						progressClose();
					}, 'JSON');
				} else {
					parent.$.messager.show({
						title : 'info',
						msg : 'You cannot delete yourself！'
					});
				}
			}
		});
	}
	
	function editFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.modalDialog({
			title : 'edit',
			width : 500,
			height : 310,
			href : '${ctx}/client/editPage?id=' + id,
			buttons : [ {
				text : 'edit',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler.find('#clientEditForm');
					f.submit();
				}
			} ]
		});
	}
	</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',fit:true,border:false" title="client list">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;">
		<c:if test="${fn:contains(sessionInfo.resourceList, '/client/add')}">
			<a onclick="addFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon_add'">add</a>
		</c:if>
	</div>
</body>
</html>