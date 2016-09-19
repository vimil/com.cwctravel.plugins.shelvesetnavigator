package com.cwctravel.plugins.shelvesetreview.constants;

public class ShelvesetPropertyConstants {
	public static final String SHELVESET_PROPERTY_BUILD_ID = "cwctravel.buildId";
	public static final String SHELVESET_PROPERTY_CHANGESET_ID = "cwctravel.changesetId";
	public static final String SHELVESET_PROPERTY_APPROVER_ID = "cwctravel.approverId";
	public static final String SHELVESET_PROPERTY_REVIEWER_IDS = "cwctravel.reviewerIds";
	public static final String SHELVESET_PROPERTY_WORKITEM_ID = "cwctravel.workItemId";
	public static final String SHELVESET_PROPERTY_APPROVED_FLAG = "cwctravel.approved";
	public static final String SHELVESET_PROPERTY_INACTIVE_FLAG = "cwctravel.inactive";

	public static final String[] SHELVESET_PROPERTIES = { SHELVESET_PROPERTY_REVIEWER_IDS, SHELVESET_PROPERTY_APPROVER_ID,
			SHELVESET_PROPERTY_CHANGESET_ID, SHELVESET_PROPERTY_WORKITEM_ID, SHELVESET_PROPERTY_BUILD_ID, SHELVESET_PROPERTY_APPROVED_FLAG,
			SHELVESET_PROPERTY_INACTIVE_FLAG };
}
