package com.hpcnt.releaseNoteAutomation.util;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.JsonObject;

public class JiraLoginUtil {

	private static JiraLoginUtil instance;

	private static final String USER_AGENT = "Mozilla";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String JSON = "application/json";
	private static final String ID = "username";
	private static final String PW = "password";
	private static final String CURRENT_TAG = "meta";
	private static final String NAME = "name";
	private static final String NAME_PATTERN = "ajs-remote-user-fullname";
	private static final String CONTENT = "content";
	private static final String NONE = "Not Found";

	private static Response session;
	private static final String JIRA_REST_URL = "https://hyperconnect.atlassian.net/rest/auth/1/session";

	private JiraLoginUtil() {
	}

	public static synchronized JiraLoginUtil getInstance() {
		if (instance == null)
			instance = new JiraLoginUtil();
		return instance;
	}

	public Map<String, String> getJiraCredential(String id, String pass) throws IOException {
		if (session == null)
			session = sendJiraLoginRequest(id, pass);
		return session.cookies();
	}

	private Response sendJiraLoginRequest(String id, String pass) throws IOException {
		JsonObject request = new JsonObject();
		request.addProperty(ID, id);
		request.addProperty(PW, pass);
		return Jsoup.connect(JIRA_REST_URL).method(Method.POST).userAgent(USER_AGENT).ignoreContentType(true)
				.requestBody(request.toString()).header(CONTENT_TYPE, JSON).execute();
	}

	public String getFullName(Document document) {
		Optional<Element> name = document.getElementsByTag(CURRENT_TAG).stream()
				.filter(e -> e.hasAttr(NAME) && NAME_PATTERN.equals(e.attr(NAME))).findFirst();
		return name.isPresent() ? name.get().attr(CONTENT) : NONE;
	}
}
