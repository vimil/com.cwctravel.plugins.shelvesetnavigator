package com.cwctravel.plugins.shelvesetreview.util;

import java.io.IOException;
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
import com.cwctravel.plugins.shelvesetreview.navigator.model.comparators.ReviewerComparator;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.DiscussionService;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCommentInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadInfo;
import com.microsoft.tfs.core.TFSConnection;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PropertyValue;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;
import com.microsoft.tfs.core.clients.webservices.TeamFoundationIdentity;

import ms.tfs.versioncontrol.clientservices._03._PropertyValue;
import ms.tfs.versioncontrol.clientservices._03._Shelveset;

public class ShelvesetUtil {
	private static final int MAX_ACTIVE_SHELVESET_AGE = 90;

	public static List<ShelvesetResourceItem> groupShelvesetFileItems(ShelvesetItem shelvesetItem, List<ShelvesetFileItem> shelvesetFileItems,
			DiscussionInfo discussionInfo) {
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

		boolean isShelvesetInactive = ShelvesetUtil.getPropertyAsBoolean(shelveset, ShelvesetPropertyConstants.SHELVESET_INACTIVE_FLAG,
				(shelvesetAge > MAX_ACTIVE_SHELVESET_AGE ? true : false));

		String changesetId = ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_CHANGESET_ID, null);
		return isShelvesetInactive || changesetId != null;
	}

	public static boolean canActivateShelveset(Shelveset shelveset) {
		boolean isShelvesetInactive = isShelvesetInactive(shelveset);
		String changesetId = ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_CHANGESET_ID, null);

		return isShelvesetInactive && changesetId == null;
	}

	public static boolean canAssignReviewers(Shelveset shelveset) {
		boolean isShelvesetInactive = isShelvesetInactive(shelveset);
		boolean shelvesetBelongsToCurrentUser = TFSUtil.userIdsSame(TFSUtil.getCurrentUserId(), shelveset.getOwnerName());
		return !isShelvesetInactive && shelvesetBelongsToCurrentUser;
	}

	public static String getShelvesetBuildId(Shelveset shelveset) {
		return ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_BUILD_ID, null);
	}

	public static String getShelvesetChangesetNumber(Shelveset shelveset) {
		return ShelvesetUtil.getProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_CHANGESET_ID, null);
	}

	public static void markShelvesetInactive(Shelveset shelveset) {
		setShelvesetProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_INACTIVE_FLAG, Boolean.toString(true));
	}

	public static void markShelvesetActive(Shelveset shelveset) {
		setShelvesetProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_INACTIVE_FLAG, Boolean.toString(false));
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

	public static List<ReviewerInfo> getShelvesetReviewers(Shelveset shelveset) {
		List<ReviewerInfo> result = new ArrayList<ReviewerInfo>();
		String[] reviewerIds = ShelvesetUtil.getPropertyAsStringArray(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_REVIEWER_IDS);

		String[] approverIds = ShelvesetUtil.getPropertyAsStringArray(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_IDS);

		Map<String, Boolean> reviewerIdMap = new HashMap<String, Boolean>();
		for (String reviewerId : reviewerIds) {
			reviewerId = TFSUtil.normalizeUserId(reviewerId);
			reviewerIdMap.put(reviewerId, false);
		}

		for (String approverId : approverIds) {
			approverId = TFSUtil.normalizeUserId(approverId);
			if (reviewerIdMap.containsKey(approverId)) {
				reviewerIdMap.put(approverId, true);
			}
		}

		for (Map.Entry<String, Boolean> reviewerIdMapEntry : reviewerIdMap.entrySet()) {
			ReviewerInfo reviewerInfo = new ReviewerInfo();
			reviewerInfo.setReviewerId(reviewerIdMapEntry.getKey());
			reviewerInfo.setApproved(reviewerIdMapEntry.getValue());
			reviewerInfo.setModifiable(true);
			reviewerInfo.setSource(ReviewerInfo.SOURCE_SHELVESET);
			result.add(reviewerInfo);
		}

		Collections.sort(result, ReviewerComparator.INSTANCE);

		return result;
	}

	public static void assignReviewers(Shelveset shelveset, List<ReviewerInfo> reviewerInfos) {
		Map<String, ReviewerInfo> currentReviewersMap = getReviewersMap(getShelvesetReviewers(shelveset));
		Map<String, ReviewerInfo> newReviewersMap = getReviewersMap(reviewerInfos);
		Map<String, ReviewerInfo> modifiedReviewersMap = new HashMap<String, ReviewerInfo>();

		for (Map.Entry<String, ReviewerInfo> currentReviewersMapEntry : currentReviewersMap.entrySet()) {
			String reviewerId = currentReviewersMapEntry.getKey();
			ReviewerInfo reviewerInfo = currentReviewersMapEntry.getValue();
			if (!reviewerInfo.isModifiable() || (reviewerInfo.isModifiable() && newReviewersMap.containsKey(reviewerId))) {
				modifiedReviewersMap.put(reviewerId, reviewerInfo);
			}
		}

		for (Map.Entry<String, ReviewerInfo> newReviewersMapEntry : newReviewersMap.entrySet()) {
			String reviewerId = newReviewersMapEntry.getKey();
			ReviewerInfo reviewerInfo = newReviewersMapEntry.getValue();
			ReviewerInfo currentReviewerInfo = currentReviewersMap.get(reviewerId);
			if (currentReviewerInfo == null) {
				modifiedReviewersMap.put(reviewerId, reviewerInfo);
			}
		}

		List<ReviewerInfo> modifiedReviewers = new ArrayList<ReviewerInfo>();
		for (Map.Entry<String, ReviewerInfo> modifiedReviewersMapEntry : modifiedReviewersMap.entrySet()) {
			ReviewerInfo reviewerInfo = modifiedReviewersMapEntry.getValue();
			if (reviewerInfo != null) {
				modifiedReviewers.add(reviewerInfo);
			}
		}

		Collections.sort(modifiedReviewers, ReviewerComparator.INSTANCE);

		StringBuilder reviewerIdsBuilder = new StringBuilder();
		StringBuilder approverIdsBuilder = new StringBuilder();

		int modifiedReviewersSize = modifiedReviewers.size();
		for (int i = 0; i < modifiedReviewersSize; i++) {
			ReviewerInfo reviewerInfo = modifiedReviewers.get(i);
			String reviewerId = reviewerInfo.getReviewerId();

			if (reviewerInfo.isModifiable()) {
				if (reviewerIdsBuilder.length() > 0) {
					reviewerIdsBuilder.append(",");
				}

				reviewerIdsBuilder.append(reviewerId);
			}

			if (reviewerInfo.isApproved()) {
				if (approverIdsBuilder.length() > 0) {
					approverIdsBuilder.append(",");
				}
				approverIdsBuilder.append(reviewerId);
			}
		}

		String reviewerIds = reviewerIdsBuilder.toString();
		String approverIds = approverIdsBuilder.toString();

		List<String[]> properties = new ArrayList<String[]>();
		properties.add(new String[] { ShelvesetPropertyConstants.SHELVESET_PROPERTY_REVIEWER_IDS, reviewerIds });
		properties.add(new String[] { ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_IDS, approverIds });
		setShelvesetProperties(shelveset, properties);

	}

	private static Map<String, ReviewerInfo> getReviewersMap(List<ReviewerInfo> reviewers) {
		Map<String, ReviewerInfo> result = new HashMap<String, ReviewerInfo>();
		if (reviewers != null) {
			for (ReviewerInfo reviewerInfo : reviewers) {
				result.put(reviewerInfo.getReviewerId(), reviewerInfo);
			}
		}
		return result;
	}

	public static DiscussionInfo retrieveDiscussion(Shelveset shelveset) throws IOException {
		DiscussionInfo result = null;
		TFSConnection connection = TFSUtil.getTFSConnection();
		if (connection != null) {
			result = DiscussionService.getShelvesetDiscussion(connection, shelveset.getName(), shelveset.getOwnerName());
		}
		return result;
	}

	public static void approve(Shelveset shelveset, List<TeamFoundationIdentity> reviewGroupMembers) throws ApproveException {
		if (!isShelvesetInactive(shelveset)) {
			String currentUserId = TFSUtil.getCurrentUserId();
			if (isUserReviewer(currentUserId, shelveset, reviewGroupMembers)) {
				String[] approverIds = getPropertyAsStringArray(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_IDS);
				Set<String> approverIdsSet = new HashSet<String>();
				if (approverIds != null) {
					for (String approverId : approverIds) {
						approverIdsSet.add(approverId);
					}
				}
				approverIdsSet.add(currentUserId);
				String newAppoverIdsStr = StringUtil.joinCollection(approverIdsSet, ",");
				setShelvesetProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_IDS, newAppoverIdsStr);
			} else {
				throw new ApproveException("Current User is not a reviewer of the shelveset");
			}
		} else {
			throw new ApproveException("Shelveset is not active");
		}
	}

	private static boolean isUserReviewer(String userId, Shelveset shelveset, List<TeamFoundationIdentity> reviewGroupMembers) {
		boolean result = false;

		List<ReviewerInfo> reviewerInfos = getShelvesetReviewers(shelveset);
		if (reviewerInfos != null) {
			for (ReviewerInfo reviewerInfo : reviewerInfos) {
				if (TFSUtil.userIdsSame(userId, reviewerInfo.getReviewerId())) {
					result = true;
					break;
				}
			}
		}

		if (!result && reviewGroupMembers != null) {
			for (TeamFoundationIdentity reviewGroupMember : reviewGroupMembers) {
				if (TFSUtil.userIdsSame(userId, reviewGroupMember.getUniqueName())) {
					result = true;
					break;
				}
			}
		}

		return result;
	}

	public static boolean canApprove(Shelveset shelveset, List<TeamFoundationIdentity> reviewGroupMembers) {
		String currentUserId = TFSUtil.getCurrentUserId();
		return !isShelvesetInactive(shelveset) && isUserReviewer(currentUserId, shelveset, reviewGroupMembers)
				&& !isApprovedbyUser(shelveset, currentUserId) && !TFSUtil.userIdsSame(currentUserId, shelveset.getOwnerName());
	}

	public static boolean isCurrentUserReviewer(Shelveset shelveset, List<TeamFoundationIdentity> reviewGroupMembers) {
		String currentUserId = TFSUtil.getCurrentUserId();
		return !isShelvesetInactive(shelveset) && isUserReviewer(currentUserId, shelveset, reviewGroupMembers)
				&& !TFSUtil.userIdsSame(currentUserId, shelveset.getOwnerName());
	}

	public static boolean isApprovedbyUser(Shelveset shelveset, String userId) {
		boolean result = false;
		String[] approverIds = getPropertyAsStringArray(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_IDS);
		if (approverIds != null) {
			String currentUserId = TFSUtil.getCurrentUserId();
			for (String approverId : approverIds) {
				if (TFSUtil.userIdsSame(currentUserId, approverId)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	public static void unapprove(Shelveset shelveset, List<TeamFoundationIdentity> reviewGroupMembers) throws ApproveException {
		if (!isShelvesetInactive(shelveset)) {
			String currentUserId = TFSUtil.getCurrentUserId();
			if (isUserReviewer(currentUserId, shelveset, reviewGroupMembers)) {
				String[] approverIds = getPropertyAsStringArray(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_IDS);
				Set<String> approverIdsSet = new HashSet<String>();
				if (approverIds != null) {
					for (String approverId : approverIds) {
						approverIdsSet.add(approverId);
					}
				}
				approverIdsSet.remove(currentUserId);
				String newAppoverIdsStr = StringUtil.joinCollection(approverIdsSet, ",");
				setShelvesetProperty(shelveset, ShelvesetPropertyConstants.SHELVESET_PROPERTY_APPROVER_IDS, newAppoverIdsStr);
			} else {
				throw new ApproveException("Current User is not a reviewer of the shelveset");
			}
		} else {
			throw new ApproveException("Shelveset is not active");
		}
	}

	public static Boolean canUnapprove(Shelveset shelveset, List<TeamFoundationIdentity> reviewGroupMembers) {
		String currentUserId = TFSUtil.getCurrentUserId();
		return !isShelvesetInactive(shelveset) && isUserReviewer(currentUserId, shelveset, reviewGroupMembers)
				&& isApprovedbyUser(shelveset, currentUserId) && !TFSUtil.userIdsSame(currentUserId, shelveset.getOwnerName());

	}
}
