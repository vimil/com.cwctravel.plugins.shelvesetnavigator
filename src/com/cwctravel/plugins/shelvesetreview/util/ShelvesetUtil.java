package com.cwctravel.plugins.shelvesetreview.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Status;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.constants.ShelvesetPropertyConstants;
import com.cwctravel.plugins.shelvesetreview.exceptions.ApproveException;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ReviewerInfo;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetWorkItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetWorkItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.comparators.ReviewerComparator;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.DiscussionService;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCommentInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadInfo;
import com.cwctravel.plugins.shelvesetreview.rest.workitems.dto.WorkItemInfo;
import com.microsoft.tfs.core.TFSConnection;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PropertyValue;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;
import com.microsoft.tfs.core.clients.versioncontrol.workspacecache.WorkItemCheckedInfo;
import com.microsoft.tfs.core.clients.webservices.TeamFoundationIdentity;

import ms.tfs.versioncontrol.clientservices._03._PropertyValue;
import ms.tfs.versioncontrol.clientservices._03._Shelveset;

public class ShelvesetUtil {
	private static final int MAX_ACTIVE_SHELVESET_AGE = 90;

	public static List<ShelvesetResourceItem> groupShelvesetFileItems(ShelvesetItem shelvesetItem, List<ShelvesetFileItem> shelvesetFileItems,
			DiscussionInfo discussionInfo, List<WorkItemInfo> workItems) {
		List<ShelvesetResourceItem> result = new ArrayList<ShelvesetResourceItem>();

		Map<String, Object> root = new HashMap<String, Object>();
		if (shelvesetFileItems != null) {
			for (ShelvesetFileItem shelvesetFileItem : shelvesetFileItems) {
				addFileItemToTree(root, shelvesetFileItem);
			}

			flattenTree(null, null, root);

			traverseTree(shelvesetItem, result, root);
		}

		ShelvesetDiscussionItem overallShelvesetDiscussionItem = processShelvesetItemDiscussions(shelvesetItem, shelvesetFileItems, discussionInfo);
		if (overallShelvesetDiscussionItem != null) {
			result.add(0, overallShelvesetDiscussionItem);
		}

		ShelvesetWorkItemContainer shelvesetWorkItemContainer = processShelvesetWorkItems(shelvesetItem, workItems);
		if (shelvesetWorkItemContainer != null) {
			result.add(0, shelvesetWorkItemContainer);
		}

		return result;
	}

	private static ShelvesetWorkItemContainer processShelvesetWorkItems(ShelvesetItem shelvesetItem, List<WorkItemInfo> workItems) {
		ShelvesetWorkItemContainer result = null;

		if (workItems != null && !workItems.isEmpty()) {
			result = new ShelvesetWorkItemContainer(shelvesetItem);
			List<ShelvesetWorkItem> shelvesetWorkItems = new ArrayList<ShelvesetWorkItem>();

			for (WorkItemInfo workItemInfo : workItems) {
				ShelvesetWorkItem shelvesetWorkItem = new ShelvesetWorkItem(result, workItemInfo);
				shelvesetWorkItems.add(shelvesetWorkItem);
			}
			result.setWorkItems(shelvesetWorkItems);
		}

		return result;
	}

	private static ShelvesetDiscussionItem processShelvesetItemDiscussions(ShelvesetItem shelvesetItem, List<ShelvesetFileItem> shelvesetFileItems,
			DiscussionInfo discussionInfo) {
		ShelvesetDiscussionItem overallShelvesetDiscussionItem = null;
		if (discussionInfo != null) {
			List<DiscussionThreadInfo> discussionThreadInfos = DiscussionUtil.findAllOverallDiscussionThreads(discussionInfo);
			if (!discussionThreadInfos.isEmpty()) {
				overallShelvesetDiscussionItem = processDiscussionThreadInfos(shelvesetItem, null, discussionInfo, discussionThreadInfos);
			}

			if (shelvesetFileItems != null) {
				for (ShelvesetFileItem shelvesetFileItem : shelvesetFileItems) {
					List<DiscussionThreadInfo> fileDiscussionThreadInfos = DiscussionUtil.findAllDiscussionThreads(discussionInfo,
							shelvesetFileItem.getPath());
					processDiscussionThreadInfos(shelvesetItem, shelvesetFileItem, discussionInfo, fileDiscussionThreadInfos);
				}
			}
		}
		return overallShelvesetDiscussionItem;
	}

	private static ShelvesetDiscussionItem processDiscussionThreadInfos(ShelvesetItem shelvesetItem, ShelvesetFileItem shelvesetFileItem,
			DiscussionInfo discussionInfo, List<DiscussionThreadInfo> discussionThreadInfos) {
		ShelvesetDiscussionItem overallShelvesetDiscussionItem = null;
		if (shelvesetFileItem == null) {
			overallShelvesetDiscussionItem = new ShelvesetDiscussionItem(shelvesetItem, null, null, null, null);
		}
		List<ShelvesetResourceItem> shelvesetDiscussionItems = new ArrayList<ShelvesetResourceItem>();
		for (DiscussionThreadInfo discussionThreadInfo : discussionThreadInfos) {
			List<DiscussionCommentInfo> discussionCommentInfos = DiscussionUtil.findRootDiscussionComments(discussionThreadInfo);
			for (DiscussionCommentInfo discussionCommentInfo : discussionCommentInfos) {
				ShelvesetDiscussionItem shelvesetDiscussionItem = new ShelvesetDiscussionItem(shelvesetItem, shelvesetFileItem,
						overallShelvesetDiscussionItem, discussionThreadInfo, discussionCommentInfo);
				shelvesetDiscussionItems.add(shelvesetDiscussionItem);
			}
		}

		for (ShelvesetResourceItem shelvesetResourceItem : shelvesetDiscussionItems) {
			ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) shelvesetResourceItem;
			List<ShelvesetResourceItem> childShelvesetDiscussionItems = new ArrayList<ShelvesetResourceItem>();
			DiscussionThreadInfo[] discussionThreadInfoHolder = new DiscussionThreadInfo[1];
			List<DiscussionCommentInfo> childDiscussionCommentInfos = DiscussionUtil.findChildDiscussions(discussionInfo,
					shelvesetDiscussionItem.getThreadId(), shelvesetDiscussionItem.getId(), discussionThreadInfoHolder);
			for (DiscussionCommentInfo discussionCommentInfo : childDiscussionCommentInfos) {
				ShelvesetDiscussionItem childShelvesetDiscussionItem = new ShelvesetDiscussionItem(shelvesetItem, shelvesetFileItem,
						shelvesetDiscussionItem, discussionThreadInfoHolder[0], discussionCommentInfo);
				childShelvesetDiscussionItems.add(childShelvesetDiscussionItem);
			}
			shelvesetDiscussionItem.setChildDiscussions(childShelvesetDiscussionItems);
		}

		if (overallShelvesetDiscussionItem != null) {
			overallShelvesetDiscussionItem.setChildDiscussions(shelvesetDiscussionItems);
		} else {
			shelvesetFileItem.setDiscussions(shelvesetDiscussionItems);
		}
		return overallShelvesetDiscussionItem;
	}

	private static void traverseTree(ShelvesetItem shelvesetItem, Object parent, Object child) {
		@SuppressWarnings("unchecked")
		Map<String, Object> childMap = (Map<String, Object>) child;
		ShelvesetFolderItem parentFolder = null;
		if (parent instanceof ShelvesetFolderItem) {
			parentFolder = (ShelvesetFolderItem) parent;
		}
		for (Map.Entry<String, Object> childMapEntry : childMap.entrySet()) {
			String childEntryName = childMapEntry.getKey();
			Object childEntryValue = childMapEntry.getValue();
			if (childEntryValue instanceof ShelvesetFileItem) {
				ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) childEntryValue;
				if (parentFolder != null) {
					ShelvesetFolderItem shelvesetFolderItem = (ShelvesetFolderItem) parent;
					shelvesetFolderItem.addChild(shelvesetFileItem);
				} else {
					@SuppressWarnings("unchecked")
					List<Object> list = (List<Object>) parent;
					list.add(shelvesetFileItem);
				}
			} else {
				ShelvesetFolderItem folder = new ShelvesetFolderItem(shelvesetItem, childEntryName);
				if (parentFolder != null) {
					parentFolder.addChild(folder);
				} else {
					@SuppressWarnings("unchecked")
					List<Object> list = (List<Object>) parent;
					list.add(folder);
				}
				traverseTree(shelvesetItem, folder, childEntryValue);
			}
		}
	}

	private static boolean flattenTree(String parentPath, Map<String, Object> parent, Map<String, Object> child) {
		if (parent != null && child.size() == 1) {
			Map.Entry<String, Object> onlyChildEntry = child.entrySet().iterator().next();
			Object onlyChild = onlyChildEntry.getValue();
			if (onlyChild instanceof Map<?, ?>) {
				String path = onlyChildEntry.getKey();
				String newPath = parentPath + "/" + path;
				parent.remove(parentPath);

				parent.put(newPath, onlyChild);
				return true;
			}
			return false;
		} else {
			boolean result = false;
			do {
				result = false;
				Set<Map.Entry<String, Object>> childrenSet = new HashSet<>(child.entrySet());
				for (Map.Entry<String, Object> entry : childrenSet) {
					String path = entry.getKey();
					Object o = entry.getValue();
					if (o instanceof Map<?, ?>) {
						@SuppressWarnings("unchecked")
						Map<String, Object> currentChild = (Map<String, Object>) o;
						if (flattenTree(path, child, currentChild)) {
							result = true;
						}
					}
				}
			} while (result);
			return result;
		}

	}

	@SuppressWarnings("unchecked")
	private static void addFileItemToTree(Map<String, Object> root, ShelvesetFileItem shelvesetFileItem) {
		String path = shelvesetFileItem.getPath();
		String[] pathParts = path.split("/");

		Map<String, Object> current = root;
		for (int i = 0; i < pathParts.length - 1; i++) {
			String pathPart = pathParts[i];
			Object o = current.get(pathPart);
			if (o == null) {
				o = new HashMap<String, Object>();
				current.put(pathPart, o);
			}
			current = (Map<String, Object>) o;
		}

		shelvesetFileItem.setName(pathParts[pathParts.length - 1]);
		current.put(pathParts[pathParts.length - 1], shelvesetFileItem);
	}

	public static boolean getPropertyAsBoolean(Shelveset shelveset, String propertyName, boolean defaultValue) {
		boolean result = defaultValue;
		String propertyValue = getProperty(shelveset, propertyName, null);
		if (propertyValue != null) {
			result = Boolean.parseBoolean(propertyValue);
		}

		return result;
	}

	public static String[] getPropertyAsStringArray(Shelveset shelveset, String propertyName) {
		String[] result = new String[0];
		String propertyValue = getProperty(shelveset, propertyName, null);
		if (propertyValue != null && !propertyValue.isEmpty()) {
			result = propertyValue.split(",");
		}
		return result;
	}

	public static String getProperty(Shelveset shelveset, String propertyName, String defaultValue) {
		String result = defaultValue;
		PropertyValue[] propertyValues = shelveset.getPropertyValues();
		if (propertyValues != null) {
			for (PropertyValue propertyValue : propertyValues) {
				if (propertyValue.matchesName(propertyName)) {
					result = (String) propertyValue.getPropertyValue();
				}
			}
		}

		return result;
	}

	public static boolean isShelvesetInactive(Shelveset shelveset) {
		float shelvesetAge = DateUtil.differenceInDaysBetweenDates(DateUtil.currentDate(), shelveset.getCreationDate());

		boolean isShelvesetInactive = ShelvesetUtil.getPropertyAsBoolean(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_INACTIVE_FLAG,
				(shelvesetAge > MAX_ACTIVE_SHELVESET_AGE ? true : false));

		String changesetId = ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_CHANGESET_ID, null);
		String codeReviewWorkItemId = ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_WORKITEM_ID, null);
		return codeReviewWorkItemId == null && (isShelvesetInactive || changesetId != null);
	}

	public static boolean isShelvesetOpenForCurrentUser(Shelveset shelveset) {
		boolean result = !isShelvesetInactive(shelveset) && !isCurrentUserShelvesetOwner(shelveset) && isCurrentUserReviewer(shelveset, null)
				&& getShelvesetApproverId(shelveset) == null;
		return result;
	}

	private static String getShelvesetApproverId(Shelveset shelveset) {
		String approverId = ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_ID, null);
		return approverId;
	}

	public static boolean canActivateShelveset(Shelveset shelveset) {
		boolean isShelvesetInactive = isShelvesetInactive(shelveset);
		String changesetId = ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_CHANGESET_ID, null);

		return isShelvesetInactive && changesetId == null;
	}

	public static boolean canDiscardShelveset(Shelveset shelveset) {
		boolean isShelvesetInactive = isShelvesetInactive(shelveset);
		return !isShelvesetInactive && isCurrentUserShelvesetOwner(shelveset) && getCodeReviewWorkItemId(shelveset) == -1;
	}

	public static boolean isCurrentUserShelvesetOwner(Shelveset shelveset) {
		return IdentityUtil.userNamesSame(IdentityUtil.getCurrentUserName(), shelveset.getOwnerName());
	}

	public static boolean canRequestCodeReview(Shelveset shelveset) {
		boolean isShelvesetInactive = isShelvesetInactive(shelveset);
		boolean shelvesetBelongsToCurrentUser = isCurrentUserShelvesetOwner(shelveset);
		int codeReviewWorkItemId = getCodeReviewWorkItemId(shelveset);
		boolean hasReviewers = !getReviewers(shelveset).isEmpty();
		return !isShelvesetInactive && !hasReviewers && shelvesetBelongsToCurrentUser && shelveset.getBriefWorkItemInfo().length > 0
				&& codeReviewWorkItemId == -1;
	}

	public static String getShelvesetBuildId(Shelveset shelveset) {
		return ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_BUILD_ID, null);
	}

	public static String getShelvesetChangesetNumber(Shelveset shelveset) {
		return ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_CHANGESET_ID, null);
	}

	public static void markShelvesetInactive(Shelveset shelveset) {
		setShelvesetProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_INACTIVE_FLAG, Boolean.toString(true));
	}

	public static void markShelvesetActive(Shelveset shelveset) {
		setShelvesetProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_INACTIVE_FLAG, Boolean.toString(false));
	}

	public static void setShelvesetProperty(Shelveset shelveset, String property, String value) {
		List<String[]> properties = new ArrayList<String[]>();
		properties.add(new String[] { property, value });
		setShelvesetProperties(shelveset, properties);
	}

	@SuppressWarnings("restriction")
	public static void setShelvesetProperties(Shelveset shelveset, List<String[]> properties) {
		if (properties != null) {
			VersionControlClient versionControlClient = TFSUtil.getVersionControlClient();
			if (versionControlClient != null) {
				Shelveset[] shelvesets = versionControlClient.queryShelvesets(shelveset.getName(), shelveset.getOwnerName(),
						ShelvesetPropertyConstants.SHELVESET_PROPERTIES);
				if (shelvesets != null && shelvesets.length == 1) {
					_Shelveset _shelveset = shelveset.getWebServiceObject();
					Shelveset newShelveset = shelvesets[0];
					_Shelveset _newShelveset = newShelveset.getWebServiceObject();
					_PropertyValue[] propertyValuesArray = _newShelveset.getProperties();

					List<_PropertyValue> newPropertyValues = new ArrayList<_PropertyValue>();
					Map<String, _PropertyValue> newPropertyValuesMap = new HashMap<String, _PropertyValue>();
					for (String[] property : properties) {
						_PropertyValue newPropertyValue = new _PropertyValue(property[0], property[1], null, null);
						newPropertyValues.add(newPropertyValue);
						newPropertyValuesMap.put(property[0], newPropertyValue);
					}
					_newShelveset.setProperties(newPropertyValues.toArray(new _PropertyValue[0]));

					versionControlClient.getWebServiceLayer().updateShelveset(shelveset.getName(), shelveset.getOwnerName(), newShelveset);

					List<_PropertyValue> updatedPropertyValues = new ArrayList<_PropertyValue>();
					if (propertyValuesArray != null) {
						for (_PropertyValue propertyValue : propertyValuesArray) {
							updatedPropertyValues.add(propertyValue);
							_PropertyValue newPropertyValue = newPropertyValuesMap.remove(propertyValue.getPname());
							if (newPropertyValue != null) {
								propertyValue.setVal(newPropertyValue.getVal());
							}
						}
					}

					updatedPropertyValues.addAll(newPropertyValuesMap.values());
					_shelveset.setProperties(updatedPropertyValues.toArray(new _PropertyValue[0]));
				}
			}
		}
	}

	public static boolean deleteShelveset(Shelveset shelveset) {
		boolean result = false;
		if (shelveset != null) {
			VersionControlClient versionControlClient = TFSUtil.getVersionControlClient();
			if (versionControlClient != null) {
				try {
					versionControlClient.deleteShelveset(shelveset.getName(), shelveset.getOwnerName());
					result = true;
				} catch (RuntimeException e) {
					ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
					result = false;
				}
			}
		}
		return result;
	}

	public static List<ReviewerInfo> getReviewers(Shelveset shelveset) {
		return getReviewers(shelveset, null, false);
	}

	public static List<ReviewerInfo> getReviewers(Shelveset shelveset, TeamFoundationIdentity defaultReviewersGroup,
			boolean includeDefaultReviewersGroup) {
		List<ReviewerInfo> result = new ArrayList<ReviewerInfo>();
		String[] reviewerIds = ShelvesetUtil.getPropertyAsStringArray(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_REVIEWER_IDS);

		String approverId = ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_ID, null);

		Set<String> reviewerIdsSet = new HashSet<String>();
		for (String reviewerId : reviewerIds) {
			reviewerIdsSet.add(reviewerId);
		}

		boolean defaultReviewersGroupPresent = false;
		String defaultReviewersGroupUniqueName = null;
		if (defaultReviewersGroup != null) {
			defaultReviewersGroupUniqueName = defaultReviewersGroup.getUniqueName();
			defaultReviewersGroupPresent = reviewerIdsSet.contains(defaultReviewersGroupUniqueName);
			if (!defaultReviewersGroupPresent) {
				reviewerIdsSet.add(defaultReviewersGroupUniqueName);
			}
		}

		for (String reviewerId : reviewerIdsSet) {
			String matchedApproverId = null;
			if (IdentityUtil.isMember(reviewerId, approverId)) {
				matchedApproverId = approverId;
			}

			if (!reviewerId.equals(defaultReviewersGroupUniqueName) || defaultReviewersGroupPresent || includeDefaultReviewersGroup
					|| matchedApproverId != null) {
				ReviewerInfo reviewerInfo = new ReviewerInfo();
				reviewerInfo.setReviewerId(reviewerId);
				reviewerInfo.setApproverId(matchedApproverId);
				result.add(reviewerInfo);
			}
		}

		Collections.sort(result, ReviewerComparator.INSTANCE);

		return result;
	}

	public static void createCodeReviewRequest(Shelveset shelveset, int workItemId, List<ReviewerInfo> reviewerInfos) {
		Collections.sort(reviewerInfos, ReviewerComparator.INSTANCE);

		StringBuilder reviewerIdsBuilder = new StringBuilder();

		int reviewersSize = reviewerInfos.size();
		for (int i = 0; i < reviewersSize; i++) {
			ReviewerInfo reviewerInfo = reviewerInfos.get(i);
			String reviewerId = reviewerInfo.getReviewerId();

			if (reviewerIdsBuilder.length() > 0) {
				reviewerIdsBuilder.append(",");
			}
			reviewerIdsBuilder.append(reviewerId);
		}

		String reviewerIds = reviewerIdsBuilder.toString();

		List<String[]> properties = new ArrayList<String[]>();
		properties.add(new String[] { ShelvesetPropertyConstants.SHELVESET_PROPERTY_REVIEWER_IDS, reviewerIds });
		properties.add(new String[] { ShelvesetPropertyConstants.SHELVESET_PROPERTY_WORKITEM_ID, Integer.toString(workItemId) });
		setShelvesetProperties(shelveset, properties);

	}

	public static DiscussionInfo retrieveDiscussion(Shelveset shelveset) throws IOException {
		DiscussionInfo result = null;
		TFSConnection connection = TFSUtil.getTFSConnection();
		if (connection != null) {
			result = DiscussionService.getShelvesetDiscussion(connection, shelveset.getName(), shelveset.getOwnerName());
		}
		return result;
	}

	public static void approve(Shelveset shelveset, String approvalComment, TeamFoundationIdentity defaultReviewersGroup) throws ApproveException {
		if (!isShelvesetInactive(shelveset)) {
			String currentUserId = IdentityUtil.getCurrentUserName();
			if (isUserReviewer(currentUserId, shelveset, defaultReviewersGroup)) {

				List<String[]> shelvesetProperties = new ArrayList<String[]>();
				shelvesetProperties.add(new String[] { ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_ID, currentUserId });
				shelvesetProperties.add(new String[] { ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVED_FLAG, Boolean.toString(true) });
				setShelvesetProperties(shelveset, shelvesetProperties);

				addWorkItemApprovalComment(shelveset, approvalComment);

			} else {
				throw new ApproveException("Current User is not a reviewer of the shelveset");
			}
		} else {
			throw new ApproveException("Shelveset is not active");
		}
	}

	private static void addWorkItemApprovalComment(Shelveset shelveset, String approvalComment) {
		if (shelveset != null) {
			WorkItemCheckedInfo[] workedItemCheckedInfos = shelveset.getBriefWorkItemInfo();
			if (workedItemCheckedInfos != null) {
				for (WorkItemCheckedInfo workedItemCheckedInfo : workedItemCheckedInfos) {
					WorkItemUpdateRequestBuilder workItemRequestBuilder = new WorkItemUpdateRequestBuilder();
					workItemRequestBuilder.addComment(approvalComment);

					try {
						int workItemId = workedItemCheckedInfo.getID();
						List<String> workItemHyperLinks = WorkItemUtil.getWorkItemHyperLinks(workItemId);
						String shelvesetUrl = getShelvesetUrl(shelveset);
						if (!workItemHyperLinks.contains(shelvesetUrl)) {
							workItemRequestBuilder.addHyperLink(shelvesetUrl);
						}
						List<Map<String, Object>> workItemUpdateRequest = workItemRequestBuilder.build();
						WorkItemUtil.updateWorkItem(workItemId, workItemUpdateRequest);
					} catch (IOException e) {
						ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
					}
				}
			}
		}
	}

	public static String getShelvesetUrl(Shelveset shelveset) {
		String result = null;
		try {
			String baseUri = TFSUtil.getTFSConnection().getBaseURI().toString();
			if (!baseUri.endsWith("/")) {
				baseUri += "/";
			}

			result = baseUri + "_versionControl/shelveset?ss=" + URLEncoder.encode(shelveset.getName() + ";" + shelveset.getOwnerName(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
		}
		return result;
	}

	private static boolean isUserReviewer(String userId, Shelveset shelveset, TeamFoundationIdentity defaultReviewersGroup) {
		boolean result = false;

		List<ReviewerInfo> reviewerInfos = getReviewers(shelveset, defaultReviewersGroup, true);
		if (reviewerInfos != null) {
			for (ReviewerInfo reviewerInfo : reviewerInfos) {
				if (IdentityUtil.isMember(reviewerInfo.getReviewerId(), userId)) {
					result = true;
					break;
				}
			}
		}

		return result;
	}

	public static boolean canApprove(Shelveset shelveset) {
		String currentUserId = IdentityUtil.getCurrentUserName();
		return !isShelvesetInactive(shelveset)
				&& (isAcceptedByUser(shelveset, currentUserId)
						|| (!hasReviewers(shelveset) && isUserReviewer(currentUserId, shelveset, IdentityUtil.getDefaultReviewersGroup())))
				&& !isApprovedByUser(shelveset, currentUserId) && !IdentityUtil.userNamesSame(currentUserId, shelveset.getOwnerName());
	}

	private static boolean hasReviewers(Shelveset shelveset) {
		String[] reviewerIds = getPropertyAsStringArray(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_REVIEWER_IDS);
		return reviewerIds != null && reviewerIds.length > 0;
	}

	public static boolean isAcceptedByUser(Shelveset shelveset, String userId) {
		String approverId = getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_ID, null);
		boolean approvedFlag = getPropertyAsBoolean(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVED_FLAG, false);
		return IdentityUtil.userNamesSame(userId, approverId) && !approvedFlag;
	}

	public static boolean isCurrentUserReviewer(Shelveset shelveset, TeamFoundationIdentity defaultReviewersGroup) {
		String currentUserId = IdentityUtil.getCurrentUserName();
		return !isShelvesetInactive(shelveset) && isUserReviewer(currentUserId, shelveset, defaultReviewersGroup)
				&& !IdentityUtil.userNamesSame(currentUserId, shelveset.getOwnerName());
	}

	public static boolean isApprovedByUser(Shelveset shelveset, String userId) {
		boolean result = false;
		result = getPropertyAsBoolean(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVED_FLAG, false);
		if (result) {
			String approverId = getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_ID, null);
			if (approverId != null) {
				String currentUserId = IdentityUtil.getCurrentUserName();
				if (IdentityUtil.userNamesSame(currentUserId, approverId)) {
					result = true;
				}
			}
		}
		return result;
	}

	public static boolean isApproved(Shelveset shelveset) {
		boolean approvedFlag = getPropertyAsBoolean(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVED_FLAG, false);
		String approverId = getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_ID, null);
		return approvedFlag && approverId != null;
	}

	public static void unapprove(Shelveset shelveset, String revokeApprovalComment, TeamFoundationIdentity defaultReviewersGroup)
			throws ApproveException {
		if (!isShelvesetInactive(shelveset)) {
			String currentUserId = IdentityUtil.getCurrentUserName();
			if (isApprovedByUser(shelveset, currentUserId)) {
				List<String[]> shelvesetProperties = new ArrayList<String[]>();
				shelvesetProperties.add(new String[] { ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_ID, null });
				shelvesetProperties.add(new String[] { ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVED_FLAG, Boolean.toString(false) });
				setShelvesetProperties(shelveset, shelvesetProperties);
				addWorkItemApprovalComment(shelveset, revokeApprovalComment);
			} else {
				throw new ApproveException("Current User is not a reviewer of the shelveset");
			}
		} else {
			throw new ApproveException("Shelveset is not active");
		}
	}

	public static Boolean canUnapprove(Shelveset shelveset) {
		String currentUserId = IdentityUtil.getCurrentUserName();
		return !isShelvesetInactive(shelveset) && isApprovedByUser(shelveset, currentUserId)
				&& !IdentityUtil.userNamesSame(currentUserId, shelveset.getOwnerName());

	}

	public static int getCodeReviewWorkItemId(Shelveset shelveset) {
		int result = -1;
		String workItemIdStr = ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_WORKITEM_ID, null);
		result = StringUtil.toInt(workItemIdStr, -1);
		return result;
	}

}
