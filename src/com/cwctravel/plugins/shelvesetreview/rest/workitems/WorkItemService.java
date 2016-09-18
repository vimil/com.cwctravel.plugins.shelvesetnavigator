package com.cwctravel.plugins.shelvesetreview.rest.workitems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.boon.Boon;

import com.cwctravel.plugins.shelvesetreview.rest.httpmethods.PatchMethod;
import com.cwctravel.plugins.shelvesetreview.rest.workitems.dto.WorkItemInfo;
import com.cwctravel.plugins.shelvesetreview.util.StringUtil;
import com.microsoft.tfs.core.TFSConnection;
import com.microsoft.tfs.core.httpclient.HttpClient;
import com.microsoft.tfs.core.httpclient.NameValuePair;
import com.microsoft.tfs.core.httpclient.methods.GetMethod;
import com.microsoft.tfs.core.httpclient.methods.StringRequestEntity;

public class WorkItemService {

	public static List<WorkItemInfo> getWorkItems(TFSConnection tfsConnection, List<Integer> workItemIds) throws IOException {
		List<WorkItemInfo> result = Collections.emptyList();
		if (workItemIds != null && !workItemIds.isEmpty()) {
			HttpClient httpClient = tfsConnection.getHTTPClient();
			String baseURI = tfsConnection.getBaseURI().toString();

			String commaSeparatedIds = StringUtil.joinCollection(workItemIds, ",");

			GetMethod getMethod = new GetMethod(baseURI + "/_apis/wit/workitems");
			NameValuePair idsNameValuePair = new NameValuePair();
			idsNameValuePair.setName("ids");
			idsNameValuePair.setValue(commaSeparatedIds);

			NameValuePair fieldsNameValuePair = new NameValuePair();
			fieldsNameValuePair.setName("fields");
			fieldsNameValuePair.setValue("System.Id,System.Title");

			NameValuePair apiVersionNameValuePair = new NameValuePair();
			apiVersionNameValuePair.setName("api-version");
			apiVersionNameValuePair.setValue("1.0");

			NameValuePair[] nameValuePairs = new NameValuePair[] { idsNameValuePair, fieldsNameValuePair, apiVersionNameValuePair };
			getMethod.setQueryString(nameValuePairs);

			httpClient.executeMethod(getMethod);
			String response = getMethod.getResponseBodyAsString();
			result = parseWorkItemsResponse(response);
		}
		return result;
	}

	public static List<String> getWorkItemHyperLinks(TFSConnection tfsConnection, int workItemId) throws IOException {
		List<String> result = new ArrayList<String>();

		HttpClient httpClient = tfsConnection.getHTTPClient();
		String baseURI = tfsConnection.getBaseURI().toString();

		GetMethod getMethod = new GetMethod(baseURI + "/_apis/wit/workitems");
		NameValuePair idNameValuePair = new NameValuePair();
		idNameValuePair.setName("id");
		idNameValuePair.setValue(Integer.toString(workItemId));

		NameValuePair relationsNameValuePair = new NameValuePair();
		relationsNameValuePair.setName("$expand");
		relationsNameValuePair.setValue("relations");

		NameValuePair[] nameValuePairs = new NameValuePair[] { idNameValuePair, relationsNameValuePair };
		getMethod.setQueryString(nameValuePairs);

		httpClient.executeMethod(getMethod);
		String response = getMethod.getResponseBodyAsString();
		result = parseWorkItemHyperLinksResponse(response);
		return result;
	}

	private static List<String> parseWorkItemHyperLinksResponse(String response) {
		List<String> result = new ArrayList<String>();
		Object responseObj = Boon.fromJson(response);
		if (responseObj instanceof Map<?, ?>) {
			@SuppressWarnings("unchecked")
			Map<String, Object> root = (Map<String, Object>) responseObj;
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> relations = (List<Map<String, Object>>) root.get("relations");
			for (Map<String, Object> relation : relations) {
				String relationType = (String) relation.get("rel");
				if ("Hyperlink".equals(relationType)) {
					String url = (String) relation.get("url");
					if (url != null) {
						result.add(url);
					}
				}
			}
		}
		return result;
	}

	public static void updateWorkItem(TFSConnection tfsConnection, int workItemId, List<Map<String, Object>> request) throws IOException {
		HttpClient httpClient = tfsConnection.getHTTPClient();
		String baseURI = tfsConnection.getBaseURI().toString();

		PatchMethod patchMethod = new PatchMethod(baseURI + "/_apis/wit/workItems/" + workItemId + "?api-version=1.0");

		patchMethod.setRequestEntity(new StringRequestEntity(Boon.toJson(request), "application/json-patch+json", "UTF-8"));
		httpClient.executeMethod(patchMethod);
	}

	private static List<WorkItemInfo> parseWorkItemsResponse(String response) {
		List<WorkItemInfo> result = new ArrayList<WorkItemInfo>();
		Object responseObj = Boon.fromJson(response);
		if (responseObj instanceof Map<?, ?>) {
			@SuppressWarnings("unchecked")
			Map<String, Object> root = (Map<String, Object>) responseObj;
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> items = (List<Map<String, Object>>) root.get("value");
			for (Map<String, Object> item : items) {
				@SuppressWarnings("unchecked")
				Map<String, Object> fields = (Map<String, Object>) item.get("fields");
				String title = (String) fields.get("System.Title");

				WorkItemInfo workItemInfo = new WorkItemInfo();
				workItemInfo.setId((int) item.get("id"));
				workItemInfo.setTitle(title);
				result.add(workItemInfo);
			}
		}
		return result;
	}
}
