<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	$(function() {
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
			}
		});
		$('#sellEditForm').form({
			url : '${ctx}/sell/edit',
			onSubmit : function() {
				progressLoad();
				var isValid = $(this).form('validate');
				if (!isValid) {
					progressClose();
				}
			    isValid = verifyStock();
			    if (!isValid) {
			    	$.messager.alert('error', "Stock is not enough to sell!", 'error');
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
		
		
		//$("#description").val('${role.description}');
		
	});

	function verifyStock() {
    	if ($('#payMethod').combobox("getValue") == 0){
    		if (Number($('#stack').val()) + Number($('#originalVolume').val()) < Number($('#volume').val())){
				isValid = false;
			} else {
				isValid = true;
			}
    	} else {
    		if (Number($('#stack').val()) + Number($('#originalVolume').val()) + Number($('#originalCommissionOil').val()) < Number($('#volume').val()) + Number($('#commissionOil').val())) {
    			isValid = false
    		} else {
    			isValid = true;
    		}
    	}
    	return isValid;
	}

	function computeCommission(ele) {
		ele.value = ele.value.replace(/[^\d]/g,'');
		var cRate = 0.02;
		var originalVolume = $('#originalVolume').val();
		var monthAmount =  $('#monthAmount').val();
		var newMonthAmount = Number(monthAmount) + Number(ele.value) - Number(originalVolume);
		if ($('#causeUpgrade').val() == '0') {
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
		} else {
			if ($('#level').val() == 'gold') {
				if(newMonthAmount < 30) {
					$('#level').val('silver');
				} else {
					cRate = 0.01;
				}
			} else if ($('#level').val() == 'silver') {
				if(newMonthAmount >= 30) {
					$('#level').val('gold');
					cRate = 0.01;	
				}
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
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
		<form id="sellEditForm" method="post">
			<table class="grid">
					<tr>
					<td>client</td>
					<td>
						<select id="clientId" name="clientId" disabled="disabled" class="easyui-combobox" style="width: 140px; height: 29px;">
					    <option value="${oilSell.clientId}" selected>${oilSell.clientName}</option>
					    </select>
					 </td>
					<td>pay method</td>
					<td><select id="payMethod" name="payMethod" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0" <c:if test="${oilSell.payMethod == 0}"> selected </c:if>>by cash</option>
							<option value="1" <c:if test="${oilSell.payMethod == 1}"> selected </c:if>>by oil</option>
				    </select></td>
				</tr>
			    <tr>
					<td>level<input id="levelOld" name="levelOld" type="hidden" value=""></td>
					<td><input id="level" name="level" type="text" readonly class="easyui-validatebox"  value="${oilSell.level}"></td>
					<td>stock(gal)</td>
					<td><input id="stack" name="stack" type="text" readonly class="easyui-validatebox" value="${oilSell.stack}"></td>
				</tr>
				<tr>
					<td>month amount(gal)<input id="causeUpgrade" name="causeUpgrade" type="hidden"  value="${oilSell.causeUpgrade}"></td>
					<td><input id="monthAmount" name="monthAmount" type="text" readonly class="easyui-validatebox" value="${oilSell.monthAmount}"></td>
					<td>volume(gal)<input name="id" type="hidden"  value="${oilSell.id}"><input id="originalVolume" name="originalVolume" type="hidden"  value="${oilSell.volume}"></td>
					<td><input id="volume" name="volume" type="text"  placeholder="please input volume" onkeyup ="computeCommission(this)" class="easyui-validatebox" data-options="required:true" value="${oilSell.volume}"></td>
				</tr>
				<tr>
					<td>commission($)</td>
					<td><input id="commission" name="commission" type="text" readonly class="easyui-validatebox"  value="${oilSell.commission}"></td>
					<td>commission(gal)<input id="originalCommissionOil" name="originalCommissionOil" type="hidden"  value="${oilSell.commissionOil}"></td>
					<td><input id="commissionOil" name="commissionOil" type="text" readonly class="easyui-validatebox"  value="${oilSell.commissionOil }"></td>
				</tr>
			</table>
		</form>
	</div>
</div>