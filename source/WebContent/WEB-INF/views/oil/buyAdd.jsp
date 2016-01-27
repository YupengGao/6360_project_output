<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
	$('#payMethod').combobox({
	 onChange: function (n,o) {
		var volume = $('#volume').val();
     	if (volume) {
    		var cRate = 0.02;
    		if ($('#level').val() == 'gold') {
    			cRate = 0.01;
    		}
     		if (n == 0) {
     			var commission = volume * 2 * cRate;
    			$('#commission').val(commission.toFixed(2));
    			$('#commissionOil').val("");
     		} else if (n == 1) {
    			var commissionOil = volume * cRate;
    			$('#commissionOil').val(commissionOil.toFixed(2));
    			$('#commission').val("");
     		}
     	}
	}});
	$(function() {
		$('#clientId').combotree({
		    url: '${pageContext.request.contextPath}/client/tree',
		    multiple: false,
		    required: true,
		    panelHeight : 'auto',
		    value: '${client.id}',
		    
		    onChange: function (n,o) {
		    	var url = "${pageContext.request.contextPath}/buy/loadClient?clientId= " + n;
		    	$('#buyAddForm').form('load', url);
		    	reComputeCommission();
		    }
		});
		$('#buyAddForm').form({
			url : '${pageContext.request.contextPath}/buy/add',
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
					parent.$.messager.alert('错误', result.msg, 'error');
				}
			}
		});
	});
	
	function reComputeCommission() {
		$('#commission').val("");
		$('#volume').val("");
		$('#commissionOil').val("");
	}

	function computeCommission(ele) {
		ele.value = ele.value.replace(/[^\d]/g,'');
		var cRate = 0.02;
		var newMonthAmount = Number($('#monthAmount').val()) + Number(ele.value);
		if ($('#level').val() == 'gold') {
			if ($('#levelOld').val()=='silver'){
				if(newMonthAmount < 30) {
					$('#level').val('silver');
					$('#levelOld').val("");
				} else {
					cRate = 0.01;
				}
			} else {
				cRate = 0.01;
			}
		} else if ($('#level').val() == 'silver') {
			if(newMonthAmount >= 30) {
				$('#levelOld').val('silver');
				$('#level').val('gold');
				cRate = 0.01;	
			}
		}
		
		if ($('#payMethod').combobox("getValue") == 0){
			var commission = ele.value * 2 * cRate;
			$('#commission').val(commission.toFixed(2));
			$('#commissionOil').val("");
		} else {
			var commissionOil = ele.value * cRate;
			$('#commissionOil').val(commissionOil.toFixed(2));
			$('#commission').val("");
		}
	}
</script>
<div class="easyui-layout" data-options="fit:true,border:false" >
	<div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;" >
		<form id="clientForm" method="get">
		<table>
		<tr><td><input type="hidden" name = "clientId"></td></tr>
		</table>
		</form>
		<form id="buyAddForm" method="post">
			<table class="grid">
				<tr>
					<td>client</td>
					<td><select id="clientId" 
						<c:if test="${sessionInfo.isClient()}">
							disabled="disabled" 
						</c:if> 
					name="clientId" style="width: 140px; height: 29px;"></select></td>
										<td>pay method</td>
					<td><select id="payMethod" name="payMethod" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0">by cash</option>
							<option value="1">by oil</option>
				    </select></td>
					
				</tr>
				<tr>
					<td>level<input id="levelOld" name="levelOld" type="hidden" value=""></td>
					<td><input id="level" name="level" type="text" readonly class="easyui-validatebox"  value="${client.level}"></td>
					<td>stock(gal)</td>
					<td><input name="stack" type="text" readonly class="easyui-validatebox" value="${client.stack}"></td>
				</tr>
				<tr>
					<td>month amount(gal)</td>
					<td><input id="monthAmount" name="monthAmount" type="text" disabled="disabled" class="easyui-validatebox" value="${client.monthAmount}"></td>
					<td>volume(gal)</td>
					<td><input id="volume" name="volume" type="text"  placeholder="please input volume" onkeyup ="computeCommission(this)" class="easyui-validatebox span2" data-options="required:true" value=""></td>
				</tr>
				<tr>
					<td>commission($)</td>
					<td><input id="commission" name="commission" type="text" readonly class="easyui-validatebox"  value=""></td>
					<td>commission(gal)</td>
					<td><input id="commissionOil" name="commissionOil" type="text" readonly class="easyui-validatebox"  value=""></td>
				</tr>
			</table>
		</form>
	</div>
</div>