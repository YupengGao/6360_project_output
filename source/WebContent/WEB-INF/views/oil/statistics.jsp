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
<title>oil buy manage</title>
	<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}' + '/sum/dataGrid',
			striped : true,
			title: 'Statistics List',
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			pageSize : 10,
			pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns : [ [ 
			{
				width : '80',
				title : 'client',
				field : 'loginName'
			} , {
				width : '110',
				title : 'volume',
				field : 'volume',
			} , {
				width : '110',
				title : 'commission(cash$)',
				field : 'commission',	
			} , {
				width : '110',
				title : 'commission(oil)',
				field : 'commissionOil',	
				sortable : false	
			} , {
				width : '110',
				title : 'fee($)',
				field : 'price',	
				sortable : false	
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
		if ($('#buySearchForm').find('input[name="startDate"]').val() && $('#buySearchForm').find('input[name="endDate"]').val()) {
			$.messager.alert('prompt', "Only select one statistics kind!", 'info');
			$('#buySearchForm').find('input').val('');
			return;
		}
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
	function myformatter(date){
        var y = date.getFullYear();
        var m = date.getMonth()+1;
        var d = date.getDate();
        return y+'-'+(m<10?('0'+m):m);
    }


function myparser(s){
        if (!s) return new Date();
        var ss = (s.split('-'));
        var y = parseInt(ss[0],10);
        var m = parseInt(ss[1],10);
        var d = parseInt(ss[2],10);
        if (!isNaN(y) && !isNaN(m) && !isNaN(d)){
            return new Date(y,m-1,d);
        } else {
            return new Date();
        }
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
						<th>day statistics:</th>
						<td>
						<input class="easyui-datebox" id="startDate" name="startDate"
							   data-options="editable:false" style="width:150px">
						</td>
						<th>month statistics:</th>
						<td>
						<input class="easyui-datebox" id="endDate" name="endDate"
							   data-options="editable:false,formatter:myformatter,parser:myparser" style="width:150px">
						</td>
					</tr>
					
						<tr ><td colspan = "4" align="right">
					    	<a onclick="show();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon_search'">search</a>
						    <a onclick="clean();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon_search'">clear</a>
						</td></tr>
					
				</table>
        </form>
	</div>
	<div data-options="region:'center',border:false">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	
</div>
</html>