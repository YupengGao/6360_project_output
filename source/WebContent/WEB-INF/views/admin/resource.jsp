<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../inc.jsp"></jsp:include>
<meta http-equiv="X-UA-Compatible" content="edge" />
<c:if test="${fn:contains(sessionInfo.resourceList, '/resource/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<c:if test="${fn:contains(sessionInfo.resourceList, '/resource/delete')}">
	<script type="text/javascript">
		$.canDelete = true;
	</script>
</c:if>
<title>Resource Manage</title>
<script type="text/javascript">
	var treeGrid;
	$(function() {
		
		treeGrid = $('#treeGrid').treegrid({
			url : '${ctx}/resource/treeGrid',
			idField : 'id',
			treeField : 'name',
			parentField : 'pid',
			fit : true,
			fitColumns : false,
			border : false,
			frozenColumns : [ [ {
				title : 'No',
				field : 'id',
				width : 40
			} ] ],
			columns : [ [ {
				field : 'name',
				title : 'resrouce name',
				width : 200
			}, {
				field : 'url',
				title : 'resource url',
				width : 230
			}, {
				field : 'seq',
				title : 'seq',
				width : 40
			}, {
				field : 'icon',
				title : 'icon',
				width : 150
			}, {
				field : 'resourcetype',
				title : 'resrouce type',
				width : 80,
				formatter : function(value, row, index) {
					switch (value) {
					case 0:
						return 'menu';
					case 1:
						return 'button';
					}
				}
			}, {
				field : 'pid',
				title : 'parent ID',
				width : 150,
				hidden : true
			}, {
				field : 'cstate',
				title : 'status',
				width : 40,
				formatter : function(value, row, index) {
					switch (value) {
					case 0:
						return 'normal';
					case 1:
						return 'stop';
					}
				}
			}, {
				field : 'action',
				title : 'action',
				width : 80,
				formatter : function(value, row, index) {
					var str = '&nbsp;';
					if ($.canEdit) {
					str += $.formatString('<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >edit</a>', row.id);
					}
					str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
					if ($.canDelete) {
					str += $.formatString('<a href="javascript:void(0)" onclick="deleteFun(\'{0}\');" >delete</a>', row.id);
					}
					return str;
				}
			} ] ],
			toolbar : '#toolbar'
		});
	});
	
	function editFun(id) {
		if (id != undefined) {
			treeGrid.treegrid('select', id);
		}
		var node = treeGrid.treegrid('getSelected');
		if (node) {
			parent.$.modalDialog({
				title : 'edit',
				width : 500,
				height : 350,
				href : '${ctx}/resource/editPage?id=' + node.id,
				buttons : [ {
					text : 'edit',
					handler : function() {
						parent.$.modalDialog.openner_treeGrid = treeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
						var f = parent.$.modalDialog.handler.find('#resourceEditForm');
						f.submit();
					}
				} ]
			});
		}
	}
	
	function deleteFun(id) {
		if (id != undefined) {
			treeGrid.treegrid('select', id);
		}
		var node = treeGrid.treegrid('getSelected');
		if (node) {
			parent.$.messager.confirm('ask', 'Are you sure to delete this resource？Its son will be detelted too!', function(b) {
				if (b) {
					progressLoad();
					$.post('${pageContext.request.contextPath}/resource/delete', {
						id : node.id
					}, function(result) {
						if (result.success) {
							parent.$.messager.alert('prompt', result.msg, 'info');
							treeGrid.treegrid('reload');
							parent.layout_west_tree.tree('reload');
						}
						progressClose();
					}, 'JSON');
				}
			});
		}
	}
	
	function addFun() {
		parent.$.modalDialog({
			title : 'add',
			width : 500,
			height : 350,
			href : '${ctx}/resource/addPage',
			buttons : [ {
				text : 'add',
				handler : function() {
					parent.$.modalDialog.openner_treeGrid = treeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
					var f = parent.$.modalDialog.handler.find('#resourceAddForm');
					f.submit();
				}
			} ]
		});
	}
	</script>
</head>
<body>
	<div class="easyui-layout" data-options="fit:true,border:false">
		<div data-options="region:'center',border:false" title="" style="overflow: hidden;">
			<table id="treeGrid"></table>
		</div>
	</div>
	
	<div id="toolbar" style="display: none;">
		<c:if test="${fn:contains(sessionInfo.resourceList, '/resource/add')}">
			<a onclick="addFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon_add'">add</a>
		</c:if>
	</div>
</body>
</html>