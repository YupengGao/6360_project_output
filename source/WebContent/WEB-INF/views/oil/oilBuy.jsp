<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../inc.jsp"></jsp:include>
<meta http-equiv="X-UA-Compatible" content="edge" />
<c:if test="${fn:contains(sessionInfo.resourceList, '/buy/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<c:if test="${fn:contains(sessionInfo.resourceList, '/buy/delete')}">
	<script type="text/javascript">
		$.canDelete = true;
	</script>
</c:if>
<c:if test="${fn:contains(sessionInfo.resourceList, '/buy/cancel')}">
	<script type="text/javascript">
		$.canCancel = true;
	</script>
</c:if>
<title>oil buy manage</title>
	<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}' + '/buy/dataGrid',
			striped : true,
			title: 'Buy List',
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'asc',
			pageSize : 10,
			pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns : [ [ {
				width : '40',
				title : 'id',
				field : 'id',
				sortable : true
			}, {
				width : '50',
				title : 'volume',
				field : 'volume',
				sortable : true
			} , {
				width : '80',
				title : 'client',
				field : 'clientName'
			} , {
				width : '110',
				title : 'commission(cash$)',
				field : 'commission',	
				sortable : true	
			} , {
				width : '90',
				title : 'commission(oil)',
				field : 'commissionOil',	
				sortable : true	
			} , {
				width : '40',
				title : 'fee($)',
				field : 'fee',	
				sortable : true	
			} , {
				width : '120',
				title : 'create date',
				field : 'date',	
				sortable : true
			} , {
				width : '120',
				title : 'pay date',
				field : 'payDate',	
				sortable : true
			} , {
				width : '80',
				title : 'trader',
				field : 'traderName',	
				sortable : false
			} , {
				width : '120',
				title : 'cancel date',
				field : 'cancelDate',	
				sortable : true
			} , {
				width : '55',
				title : 'status',
				field : 'status',	
				sortable : true ,
				formatter : function(value, row, index) {
					switch (value) {
					case 0:
						return 'unpaid';
					case 1:
						return 'paid';
					case 2:
						return 'canceled';
					}
				}
			} , {				
				field : 'action',
				title : 'operation',
				width : 160,
				formatter : function(value, row, index) {
					var str = '';
					if(row.isdefault!=0){
						if (row.status == 0) {
						str += '&nbsp;&nbsp;';
						if ($.canEdit) {
							str += $.formatString('<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >edit</a>', row.id);
						}
						str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
						if ($.canDelete) {
							str += $.formatString('<a href="javascript:void(0)" onclick="deleteFun(\'{0}\');" >delete</a>', row.id);
						}
						str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
						str += $.formatString('<a href="javascript:void(0)" onclick="payFun(\'{0}\');" >pay</a>', row.id);
						}
						if (row.status == 1) {
							str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
							if ($.canCancel) {
								str += $.formatString('<a href="javascript:void(0)" onclick="cancelFun(\'{0}\');" >cancel</a>', row.id);
							}
						}
					}
					return str;
				}
			} ] ],
			toolbar : '#toolbar'
		});
	});

	serializeObject = function(form){
	    var o = {};
	    $.each(form.serializeArray(), function(index){
	        if(o[this['name']]){
	            o[this['name']] = o[this['name']] + this['value'];
	        }else{
	            o[this['name']] = this['value'];
	        }
	    });
	    return o;
	};

	//查询数据
	function show(){
	    $('#dataGrid').datagrid('load',serializeObject($('#buySearchForm')));
	}
	
	function clean(){
		$('#dataGrid').datagrid('load',{});
	    $('#buySearchForm').find('input').val('');
	}
	   
	function addFun() {
		parent.$.modalDialog({
			title : 'add',
			width : 600,
			height : 300,
			href : '${ctx}/buy/addPage',
			buttons : [ {
				text : 'add',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
					var f = parent.$.modalDialog.handler.find('#buyAddForm');
					f.submit();
				}
			} ]
		});
	}
	
	function payFun(id) {
		if (id == undefined) {//点击右键菜单才会触发这个
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {//点击操作里面的删除图标会触发这个
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.messager.confirm('ask', 'Are you sure to pay for the transaction？', function(b) {
			if (b) {
					progressLoad();
					$.post('${ctx}/buy/pay', {
						id : id
					}, function(result) {
						if (result.success) {
							parent.$.messager.alert('prompt', result.msg, 'info');
							dataGrid.datagrid('reload');
						} else {
							parent.$.messager.alert('prompt', result.msg, 'info');
						}
						progressClose();
					}, 'JSON');
			}
		});
	}
	function cancelFun(id) {
		if (id == undefined) {//点击右键菜单才会触发这个
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {//点击操作里面的删除图标会触发这个
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.messager.confirm('ask', 'Are you sure to cance the transaction？', function(b) {
			if (b) {
					progressLoad();
					$.post('${ctx}/buy/cancel', {
						id : id
					}, function(result) {
						if (result.success) {
							parent.$.messager.alert('prompt', result.msg, 'info');
							dataGrid.datagrid('reload');
						} else {
							parent.$.messager.alert('prompt', result.msg, 'info');
						}
						progressClose();
					}, 'JSON');
			}
		});
	}
	
	function deleteFun(id) {
		if (id == undefined) {//点击右键菜单才会触发这个
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {//点击操作里面的删除图标会触发这个
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.messager.confirm('ask', 'Are you sure to delete the transcation？', function(b) {
			if (b) {
				
					progressLoad();
					$.post('${ctx}/buy/delete', {
						id : id
					}, function(result) {
						if (result.success) {
							parent.$.messager.alert('prompt', result.msg, 'info');
							dataGrid.datagrid('reload');
						} else {
							parent.$.messager.alert('prompt', result.msg, 'info');
						}
						progressClose();
					}, 'JSON');
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
	
					progressLoad();
					$.post('${ctx}/buy/canEdit', {
						id : id
					}, function(result) {
						if (result.success) {
							parent.$.modalDialog({
								title : 'edit',
								width : 600,
								height : 300,
								href : '${ctx}/buy/editPage?id=' + id,
								buttons : [ {
									text : 'edit',
									handler : function() {
										parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
										var f = parent.$.modalDialog.handler.find('#buyEditForm');
										f.submit();
									}
								} ]
							});
						} else {
							parent.$.messager.alert('prompt', result.msg, 'info');
						}
						progressClose();
					}, 'JSON');
	
		
	}
	</script>
</head>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false,title:'filter'" style="height:100px;overflow:hidden">
		<form id="buySearchForm" method="post">
				<table class="grid">
					<tr>
						<th>Start Date:</th>
						<td>
						<input class="easyui-datebox" name="startDate"
							   data-options="editable:false" style="width:150px">
						</td>
						<th>End Date:</th>
						<td>
						<input class="easyui-datebox" name="endDate"
							   data-options="editable:false" style="width:150px">
						</td>
					</tr>
					<c:if test="${fn:contains(sessionInfo.resourceList, '/buy/add')}">
						<tr ><td colspan = "4" align="right">
							<a onclick="addFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon_add'">add</a>
					    	<a onclick="show();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon_search'">search</a>
						    <a onclick="clean();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon_search'">clear</a>
						</td></tr>
					</c:if>
				</table>
        </form>
	</div>
	<div data-options="region:'center',border:false">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	
</div>
</html>