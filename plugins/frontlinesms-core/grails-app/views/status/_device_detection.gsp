<div id="device-detection">
	<h3 id="detection-title"><g:message code="status.devises.header"/></h3>
	<g:link class="btn" action="detectDevices"><g:message code="status.detect.modems"/></g:link>
	<table id="detected-devices">
		<thead>
			<tr>
				<th><g:message code="modem.port"/></th>
				<th class="description"><g:message code="modem.description"/></th>
				<th><g:message code="modem.locked"/></th>
			</tr>
		</thead>
		<tbody>
			<g:if test="${detectedDevices.size() == 0}">
				<tr class="no-content"><td colspan="3"><g:message code="status.modems.none"/></td></tr>
			</g:if>
			<g:else>
				<g:each in="${detectedDevices}" var="d">
					<%-- TODO once functionality is merged, include the following as a template --%>
					<tr>
						<td>${d.port}</td>
						<td>${d.description}</td>
						<td>${d.lockType}</td>
					</tr>
				</g:each>
			</g:else>
		</tbody>
	</table>
</div>

<%-- TODO once functionality is merged, include the following as a template with type=sanchez --%>
<script id="detected-device-detail" type="text/x-sanchez-template">
	<tr>
		<td>{{port}}</td>
		<td>{{description}}</td>
		<td>{{lockType}}</td>
	</tr>
</script>

<r:script>
	app_info.listen("device_detection", function(data) {
		var table;
		data = data.device_detection;
		if(!data) { return; }

		table = $("#detected-devices tbody");
		table.empty();
		$.each(data, function(i, d) {
			sanchez.append(table, "detected-device-detail", d);
		});
	});
</r:script>

