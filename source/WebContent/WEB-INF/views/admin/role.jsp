<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../inc.jsp"></jsp:include>
<meta http-equiv="X-UA-Compatible" content="edge" />
<c:if test="${fn:contains(sessionInfo.resourceList, '/role/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<c:if test="${fn:contains(sessionInfo.resourceList, '/role/delete')}">
	<script type="text/javascript">
		$.canDelete = true;
	</script>
</c:if>
<c:if test="${fn:contains(sessionInfo.resourceList, '/role/grant')}">
	<script type="text/javascript">
		$.canGrant = true;
	</script>
</c:if>
<title>Role Manage</title>
	<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}' + '/role/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'asc',
			pageSize : 50,
			pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns : [ [ {
				width : '100',
				title : 'id',
				field : 'id',
				sortable : true
			}, {
				width : '80',
				title : 'name',
				field : 'name',
				sortable : true
			} , {
				width : '80',
				title : 'sequence',
				field : 'seq',
				sortable : true
			}, {
				width : '80',
				title : 'is default',
				field : 'isdefault',
				sortable : true,
				formatter : function(value, row, index) {
					switch (value) {
					case 0:
						return 'default';
					case 1:
						return 'not default';	
					}
				}
			}, {
				width : '200',
				title : 'description',
				field : 'description'
			} , {
				field : 'action',
				title : 'action',
				width : 120,
				formatter : function(value, row, index) {
					var str = '&nbsp;';
						if ($.canGrant) {
							str += $.formatString('<a href="javascript:void(0)" onclick="grantFun(\'{0}\');" >grant</a>', row.id);
						}
					if(row.isdefault!=0){
						str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
						if ($.canEdit) {
							str += $.formatString('<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >edit</a>', row.id);
						}
						str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
						if ($.canDelete) {
							str += $.formatString('<a href="javascript:void(0)" onclick="deleteFun(\'{0}\');" >delete</a>', row.id);
						}
					}
					return str;
				}
			} ] ],
			toolbar : '#toolbar'
		});
	});
	
	function addFun() {
		parent.$.modalDialog({
			title : 'add',
			width : 500,
			height : 300,
			href : '${ctx}/role/addPage',
			buttons : [ {
				text : 'add',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler.find('#roleAddForm');
					f.submit();
				}
			} ]
		});
	}
	
	function deleteFun(id) {
		if (id == undefined) {//
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {//
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.messager.confirm('ask', 'Are you sure to delete this record？', function(b) {
			if (b) {
				var currentUserId = '${sessionInfo.id}';
				if (currentUserId != id) {
					progressLoad();
					$.post('${ctx}/role/delete', {
						id : id
					}, function(result) {
						if (result.success) {
							parent.$.messager.alert('prompt', result.msg, 'info');
							dataGrid.datagrid('reload');
						}
						progressClose();
					}, 'JSON');
				} else {
					parent.$.messager.show({
						title : 'prompt',
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
			height : 300,
			href : '${ctx}/role/editPage?id=' + id,
			buttons : [ {
				text : 'edit',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler.find('#roleEditForm');
					f.submit();
				}
			} ]
		});
	}
	
	function grantFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		
		parent.$.modalDialog({
			title : 'grant',
			width : 500,
			height : 500,
			href : '${ctx}/role/grantPage?id=' + id,
			buttons : [ {
				text : 'grant',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler.find('#roleGrantForm');
					f.submit();
				}
			} ]
		});
	}
	
	</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',fit:true,border:false">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;">
		<c:if test="${fn:contains(sessionInfo.resourceList, '/role/add')}">
			<a onclick="addFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon_add'">add</a>
		</c:if>
	</div>
</body>
</html>