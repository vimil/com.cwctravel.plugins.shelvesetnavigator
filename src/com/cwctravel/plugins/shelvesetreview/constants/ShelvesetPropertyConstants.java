package com.cwctravel.plugins.shelvesetreview.constants;

public class ShelvesetPropertyConstants {
	public static final String SHELVESET_PROPERTY_BUILD_ID = "cwctravel.buildId";
	public static final String SHELVESET_PROPERTY_CHANGESET_ID = "cwctravel.changesetId";
	public static final String SHELVESET_PROPERTY_APPROVER_IDS = "cwctravel.approverIds";
	public static final String SHELVESET_PROPERTY_REVIEWER_IDS = "cwctravel.reviewerIds";
	public static final String SHELVESET_INACTIVE_FLAG = "cwctravel.inactive";

	public static final String[] SHELVESET_PROPERTIES = { SHELVESET_PROPERTY_REVIEWER_IDS, SHELVESET_PROPERTY_APPROVER_IDS,
			SHELVESET_PROPERTY_CHANGESET_ID, SHELVESET_PROPERTY_BUILD_ID, SHELVESET_INACTIVE_FLAG };
}
