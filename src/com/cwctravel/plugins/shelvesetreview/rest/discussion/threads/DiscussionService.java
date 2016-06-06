package com.cwctravel.plugins.shelvesetreview.rest.discussion.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.boon.Boon;

import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionAuthorInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCommentInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadPropertiesInfo;
import com.cwctravel.plugins.shelvesetreview.util.DateUtil;
import com.microsoft.tfs.core.TFSConnection;
import com.microsoft.tfs.core.httpclient.HttpClient;
import com.microsoft.tfs.core.httpclient.NameValuePair;
import com.microsoft.tfs.core.httpclient.methods.GetMethod;
import com.microsoft.tfs.core.httpclient.methods.PatchMethod;
import com.microsoft.tfs.core.httpclient.methods.StringRequestEntity;

public class DiscussionService {

	public static DiscussionCommentInfo updateShelvesetDiscussionComment(TFSConnection tfsConnection, DiscussionCommentInfo discussionCommentInfo)
			throws IOException {
		DiscussionCommentInfo result = null;
		HttpClient httpClient = tfsConnection.getHTTPClient();
		String baseURI = tfsConnection.getBaseURI().toString();

		PatchMethod patchMethod = new PatchMethod(baseURI + "/_apis/discussion/threads/" + discussionCommentInfo.getThreadId() + "/comments/"
				+ discussionCommentInfo.getId() + "?api-version=3.0-preview.1");

		Map<String, Object> jsonRequestBody = toJSONMap(discussionCommentInfo);
		patchMethod.setRequestEntity(new StringRequestEntity(Boon.toJson(jsonRequestBody), "application/json", "UTF-8"));

		httpClient.executeMethod(patchMethod);
		String response = patchMethod.getResponseBodyAsString();
		result = parseDiscussionCommentInfoResponse(response);
		return result;
	}

	public static DiscussionInfo getShelvesetDiscussion(TFSConnection tfsConnection, String shelvesetName, String shelvesetOwner) throws IOException {
		DiscussionInfo result = null;
		HttpClient httpClient = tfsConnection.getHTTPClient();
		String baseURI = tfsConnection.getBaseURI().toString();

		GetMethod getMethod = new GetMethod(baseURI + "/_apis/discussion/threads");
		NameValuePair nameValuePair = new NameValuePair();
		nameValuePair.setName("artifactUri");
		nameValuePair.setValue("vstfs:///VersionControl/Shelveset/" + shelvesetName + "&shelvesetOwner=" + shelvesetOwner);
		NameValuePair[] nameValuePairs = new NameValuePair[1];
		nameValuePairs[0] = nameValuePair;
		getMethod.setQueryString(nameValuePairs);

		httpClient.executeMethod(getMethod);

		String response = getMethod.getResponseBodyAsString();
		result = parseDiscussionInfoResponse(response);

		return result;
	}

	@SuppressWarnings("unchecked")
	private static DiscussionCommentInfo parseDiscussionCommentInfoResponse(String response) {
		DiscussionCommentInfo result = null;
		Object responseObj = Boon.fromJson(response);
		if (responseObj instanceof Map<?, ?>) {
			Map<String, Object> root = (Map<String, Object>) responseObj;
			result = toDiscussionCommentInfo(root);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static DiscussionInfo parseDiscussionInfoResponse(String response) {
		DiscussionInfo result = new DiscussionInfo();
		Object responseObj = Boon.fromJson(response);
		if (responseObj instanceof Map<?, ?>) {
			Map<String, DiscussionAuthorInfo> discussionAuthorsMap = new HashMap<String, DiscussionAuthorInfo>();

			Map<String, ?> root = (Map<String, ?>) responseObj;
			List<Map<String, ?>> discussionThreadObjs = (List<Map<String, ?>>) root.get("value");
			if (discussionThreadObjs != null) {
				List<DiscussionThreadInfo> discussionThreadInfos = new ArrayList<DiscussionThreadInfo>();
				for (Map<String, ?> discussionThreadObj : discussionThreadObjs) {
					boolean isDeleted = (boolean) discussionThreadObj.get("isDeleted");
					if (!isDeleted) {
						DiscussionThreadInfo discussionThreadInfo = new DiscussionThreadInfo();
						discussionThreadInfo.setId((int) discussionThreadObj.get("id"));
						discussionThreadInfo.setArtifactUri((String) discussionThreadObj.get("arifactUri"));
						discussionThreadInfo.setPublishedDate(toCalendar(discussionThreadObj, "publishedDate"));
						discussionThreadInfo.setLastUpdatedDate(toCalendar(discussionThreadObj, "lastUpdatedDate"));
						discussionThreadInfo.setStatus((String) discussionThreadObj.get("status"));

						List<Map<String, Object>> discussionCommentObjs = (List<Map<String, Object>>) discussionThreadObj.get("comments");
						if (discussionCommentObjs != null) {
							List<DiscussionCommentInfo> discussionCommentInfos = new ArrayList<DiscussionCommentInfo>();
							for (Map<String, Object> discussionCommentObj : discussionCommentObjs) {
								boolean isCommentDeleted = (Boolean) discussionCommentObj.getOrDefault("isDeleted", false);
								if (!isCommentDeleted) {
									DiscussionCommentInfo discussionCommentInfo = new DiscussionCommentInfo();
									discussionCommentInfo.setId((int) discussionCommentObj.get("id"));
									discussionCommentInfo.setParentId((int) discussionCommentObj.getOrDefault("parentId", 0));
									discussionCommentInfo.setThreadId((int) discussionCommentObj.get("threadId"));
									discussionCommentInfo.setContent((String) discussionCommentObj.get("content"));
									discussionCommentInfo.setPublishedDate(toCalendar(discussionCommentObj, "publishedDate"));
									discussionCommentInfo.setLastUpdatedDate(toCalendar(discussionCommentObj, "lastUpdatedDate"));
									discussionCommentInfo.setCanDelete((boolean) discussionCommentObj.get("canDelete"));

									Map<String, ?> discussionAuthorObj = (Map<String, ?>) discussionCommentObj.get("author");
									if (discussionAuthorObj != null) {
										String authorId = (String) discussionAuthorObj.get("id");
										DiscussionAuthorInfo discussionAuthorInfo = discussionAuthorsMap.get(authorId);
										if (discussionAuthorInfo == null) {
											discussionAuthorInfo = new DiscussionAuthorInfo();
											discussionAuthorInfo.setId(authorId);
											discussionAuthorInfo.setDisplayName((String) discussionAuthorObj.get("displayName"));
											discussionAuthorInfo.setUniqueName((String) discussionAuthorObj.get("uniqueName"));
											discussionAuthorInfo.setUrl((String) discussionAuthorObj.get("url"));
											discussionAuthorInfo.setImageUrl((String) discussionAuthorObj.get("imageUrl"));
											discussionAuthorsMap.put(authorId, discussionAuthorInfo);
										}
										discussionCommentInfo.setAuthor(discussionAuthorInfo);
									}
									discussionCommentInfos.add(discussionCommentInfo);
								}
							}
							discussionThreadInfo.setComments(discussionCommentInfos);

							Map<String, ?> discussionThreadProperties = (Map<String, ?>) discussionThreadObj.get("properties");
							if (discussionThreadProperties != null) {
								DiscussionThreadPropertiesInfo discussionThreadPropertiesInfo = new DiscussionThreadPropertiesInfo();

								Map<String, ?> itemPathProperty = (Map<String, ?>) discussionThreadProperties
										.get("Microsoft.TeamFoundation.Discussion.ItemPath");
								if (itemPathProperty != null) {
									String itemPath = (String) itemPathProperty.get("$value");
									discussionThreadPropertiesInfo.setItemPath(itemPath);
								}

								Map<String, ?> positionContextProperty = (Map<String, ?>) discussionThreadProperties
										.get("Microsoft.TeamFoundation.Discussion.Position.PositionContext");
								if (positionContextProperty != null) {
									String positionContext = (String) positionContextProperty.get("$value");
									discussionThreadPropertiesInfo.setPositionContext(positionContext);
								}

								Map<String, ?> startLineProperty = (Map<String, ?>) discussionThreadProperties
										.get("Microsoft.TeamFoundation.Discussion.Position.StartLine");
								if (startLineProperty != null) {
									int startLine = (int) startLineProperty.get("$value");
									discussionThreadPropertiesInfo.setStartLine(startLine);
								}

								Map<String, ?> startColumnProperty = (Map<String, ?>) discussionThreadProperties
										.get("Microsoft.TeamFoundation.Discussion.Position.StartColumn");
								if (startColumnProperty != null) {
									int startColumn = (int) startColumnProperty.get("$value");
									discussionThreadPropertiesInfo.setStartColumn(startColumn);
								}

								Map<String, ?> endLineProperty = (Map<String, ?>) discussionThreadProperties
										.get("Microsoft.TeamFoundation.Discussion.Position.EndLine");
								if (endLineProperty != null) {
									int endLine = (int) endLineProperty.get("$value");
									discussionThreadPropertiesInfo.setEndLine(endLine);
								}

								Map<String, ?> endColumnProperty = (Map<String, ?>) discussionThreadProperties
										.get("Microsoft.TeamFoundation.Discussion.Position.EndColumn");
								if (endColumnProperty != null) {
									int endColumn = (int) endColumnProperty.get("$value");
									discussionThreadPropertiesInfo.setEndColumn(endColumn);
								}

								discussionThreadInfo.setThreadProperties(discussionThreadPropertiesInfo);
							}
						}
						discussionThreadInfos.add(discussionThreadInfo);
					}
				}
				result.setThreads(discussionThreadInfos);
			}
		}

		return result;
	}

	private static Calendar toCalendar(Map<String, ?> obj, String dateProperty) {
		Calendar result = null;
		Object dateObj = obj.get(dateProperty);
		if (dateObj instanceof Date) {
			result = new GregorianCalendar();
			result.setTime((Date) dateObj);
		} else if (dateObj instanceof String) {
			result = DateUtil.toCalendar((String) dateObj);
		}
		return result;
	}

	private static Map<String, Object> toJSONMap(DiscussionCommentInfo discussionCommentInfo) {
		Map<String, Object> result = null;
		if (discussionCommentInfo != null) {
			result = new HashMap<String, Object>();
			result.put("id", discussionCommentInfo.getId());
			int parentId = discussionCommentInfo.getParentId();
			if (parentId > 0) {
				result.put("parentId", parentId);
			}

			result.put("threadId", discussionCommentInfo.getThreadId());
			result.put("author", toJSONMap(discussionCommentInfo.getAuthor()));
			result.put("content", discussionCommentInfo.getContent());
			result.put("publishedDate", DateUtil.formatDate(discussionCommentInfo.getPublishedDate(), DateUtil.DATE_FORMAT_1));
			result.put("lastUpdatedDate", DateUtil.formatDate(discussionCommentInfo.getLastUpdatedDate(), DateUtil.DATE_FORMAT_1));
			result.put("canDelete", discussionCommentInfo.isCanDelete());
			result.put("isEditable", true);
		}
		return result;
	}

	private static DiscussionCommentInfo toDiscussionCommentInfo(Map<String, Object> discussionCommentObj) {
		DiscussionCommentInfo discussionCommentInfo = new DiscussionCommentInfo();
		discussionCommentInfo.setId((int) discussionCommentObj.get("id"));
		discussionCommentInfo.setParentId((int) discussionCommentObj.getOrDefault("parentId", 0));
		discussionCommentInfo.setThreadId((int) discussionCommentObj.get("threadId"));
		discussionCommentInfo.setContent((String) discussionCommentObj.get("content"));
		discussionCommentInfo.setPublishedDate(toCalendar(discussionCommentObj, "publishedDate"));
		discussionCommentInfo.setLastUpdatedDate(toCalendar(discussionCommentObj, "lastUpdatedDate"));
		discussionCommentInfo.setCanDelete((boolean) discussionCommentObj.get("canDelete"));

		@SuppressWarnings("unchecked")
		Map<String, ?> discussionAuthorObj = (Map<String, ?>) discussionCommentObj.get("author");
		if (discussionAuthorObj != null) {
			String authorId = (String) discussionAuthorObj.get("id");
			DiscussionAuthorInfo discussionAuthorInfo = new DiscussionAuthorInfo();
			discussionAuthorInfo.setId(authorId);
			discussionAuthorInfo.setDisplayName((String) discussionAuthorObj.get("displayName"));
			discussionAuthorInfo.setUniqueName((String) discussionAuthorObj.get("uniqueName"));
			discussionAuthorInfo.setUrl((String) discussionAuthorObj.get("url"));
			discussionAuthorInfo.setImageUrl((String) discussionAuthorObj.get("imageUrl"));
			discussionCommentInfo.setAuthor(discussionAuthorInfo);
		}

		return discussionCommentInfo;
	}

	private static Map<String, Object> toJSONMap(DiscussionAuthorInfo discussionAuthorInfo) {
		Map<String, Object> result = null;
		if (discussionAuthorInfo != null) {
			result = new HashMap<String, Object>();
			result.put("id", discussionAuthorInfo.getId());
			result.put("displayName", discussionAuthorInfo.getDisplayName());
			result.put("uniqueName", discussionAuthorInfo.getUniqueName());
			result.put("url", discussionAuthorInfo.getUrl());
			result.put("imageUrl", discussionAuthorInfo.getImageUrl());

		}
		return result;
	}
}
