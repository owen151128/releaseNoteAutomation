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

	private static Response session;

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
		request.addProperty(JiraConstants.ID, id);
		request.addProperty(JiraConstants.PW, pass);
		return Jsoup.connect(JiraConstants.REST_LOGIN_URL).method(Method.POST).userAgent(JiraConstants.REST_USER_AGENT)
				.ignoreContentType(true).requestBody(request.toString())
				.header(JiraConstants.CONTENT_TYPE, JiraConstants.JSON).execute();
	}

	public String getFullName(Document document) {
		Optional<Element> name = document.getElementsByTag(JiraConstants.CURRENT_TAG).stream().filter(
				e -> e.hasAttr(JiraConstants.NAME) && JiraConstants.NAME_PATTERN.equals(e.attr(JiraConstants.NAME)))
				.findFirst();
		return name.isPresent() ? name.get().attr(JiraConstants.CONTENT) : JiraConstants.NONE;
	}
}
