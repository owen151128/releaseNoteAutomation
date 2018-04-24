package com.hpcnt.releaseNoteAutomation.util;

public class JiraConstants {

	public static final String BASE_URL = "https://hyperconnect.atlassian.net/";
	public static final String REST_LOGIN_URL = BASE_URL + "rest/auth/1/session";
	public static final String REQUEST_INFO_URL = "rest/api/2/issue/";
	public static final String JSON_FIELDS = "fields";
	public static final String JSON_ISSUE_TYPE = "issuetype";
	public static final String JSON_STATUS = "status";
	public static final String JSON_NAME = "name";
	public static final String JSON_LABELS = "labels";
	public static final String NO_QA = "noqa";
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
	public static final String REST_USER_AGENT = "Mozilla";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String JSON = "application/json";
	public static final String ID = "username";
	public static final String PW = "password";
	public static final String CURRENT_TAG = "meta";
	public static final String NAME = "name";
	public static final String NAME_PATTERN = "ajs-remote-user-fullname";
	public static final String CONTENT = "content";
	public static final String NONE = "none";
	public static final String SUMMARY_REGEX = "^\\[.*\\]\\s-\\s";
	public static final String KEY_REGEX = "\\[(.*?)\\]";

	private JiraConstants() {
	}

}
