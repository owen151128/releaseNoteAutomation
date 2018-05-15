package com.hpcnt.releaseNoteAutomation.util;

/**
 * 
 * @author owen151128
 * 
 *         JIRA Constants
 *
 */
public class JiraConstants {

	/**
	 * BLANK
	 */
	public static final String BLANK = " ";
	public static final String NULL = "";

	/**
	 * Google drive path
	 */
	public static final String DRIVE_PATH = "0B_Vxyf5seBisaTZ0MWVoNTVfWEk";

	/**
	 * JIRA base URL
	 */
	public static final String BASE_URL = "https://hyperconnect.atlassian.net/";

	/**
	 * JIRA request login URL
	 */
	public static final String REST_LOGIN_URL = BASE_URL + "rest/auth/1/session";

	/**
	 * JIRA request info URL
	 */
	public static final String REQUEST_INFO_URL = "rest/api/2/issue/";

	/**
	 * Request user agent
	 */
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
	public static final String REST_USER_AGENT = "Mozilla";

	public static final String MIME_TYPE = "application/vnd.google-apps.spreadsheet";

	/**
	 * JSON parsing constants
	 */
	public static final String JSON_FIELDS = "fields";
	public static final String JSON_ISSUE_TYPE = "issuetype";
	public static final String JSON_STATUS = "status";
	public static final String JSON_NAME = "name";
	public static final String JSON_LABELS = "labels";

	/**
	 * NOQA label
	 */
	public static final String NO_QA = "noqa";

	/**
	 * Request header constants
	 */
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String JSON = "application/json";
	public static final String CONTENT = "content";
	public static final String ID = "username";
	public static final String PW = "password";
	public static final String CURRENT_TAG = "meta";

	/**
	 * Info login constants
	 */
	public static final String NAME = "name";
	public static final String NAME_PATTERN = "ajs-remote-user-fullname";
	public static final String NONE = "none";

	public static final String GET_ISSUE_SELECTOR = "div > div > section > ul > li";
	public static final String GET_VERSION_SELECTOR = "head > meta[name=ajs-version-name]";

	/**
	 * ReleaseNote OS constants
	 */
	public static final int AZAND = 0;
	public static final int AZIOS = 1;
	public static final String AOS = "AZAND";
	public static final String IOS = "AZIOS";

	/**
	 * Google spreadsheet constants
	 */
	public static final String AOS_TITLE = "Android";
	public static final String IOS_TITLE = "Ios";
	public static final String NUMBER = "NO";
	public static final String ISSUE_TYPE = "Issue Type";
	public static final String ISSUE_KEY = "IssueKey";
	public static final String SUMMARY = "Summary";
	public static final String STATUS = "Status";
	public static final String LABELS = "Labels";
	public static final String DEFAULT_RANGE = "B2:G2";
	public static final String RANGE_PREFIX = "B3:G";
	public static final String HREF_FRONT = "=HYPERLINK(\"https://hyperconnect.atlassian.net/browse/";
	public static final String HREF_MIDDLE = "\",\"";
	public static final String HREF_REAR = "\")";

	/**
	 * Google sheet name
	 */
	public static final String SHEET_NAME = "_Release QA";

	/**
	 * REGEX constants
	 */
	public static final String SUMMARY_REGEX = "^\\[.*\\]\\s-\\s";
	public static final String KEY_REGEX = "\\[(.*?)\\]";

	private JiraConstants() {
	}

}
