package com.cwctravel.plugins.shelvesetreview.rest.httpmethods;

import com.microsoft.tfs.core.httpclient.methods.PostMethod;

public class PatchMethod extends PostMethod {

	public PatchMethod() {
		super();
	}

	public PatchMethod(String uri) {
		super(uri);
	}

	public String getName() {
		return "PATCH";
	}
}
