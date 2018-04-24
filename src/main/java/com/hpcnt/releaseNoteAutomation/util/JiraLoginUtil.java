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

/**
 * 
 * @author owen151128
 * 
 *         JIRA login wrapper util
 *
 */
public class JiraLoginUtil {

	/**
	 * Java single-tone pattern instance
	 */
	private static JiraLoginUtil instance;

	/**
	 * Response for get session
	 */
	private static Response session;

	private JiraLoginUtil() {
	}

	/**
	 * Single-tone pattern
	 * 
	 * @return {@link JiraLoginUtil} instance
	 */
	public static synchronized JiraLoginUtil getInstance() {
		if (instance == null)
			instance = new JiraLoginUtil();
		return instance;
	}

	/**
	 * get JIRA credential cookies
	 * 
	 * @param id
	 * @param pass
	 * @return {@link Map<String, String>} cookies
	 * @throws IOException
	 */
	public Map<String, String> getJiraCredential(String id, String pass) throws IOException {
		if (session == null)
			session = sendJiraLoginRequest(id, pass);
		return session.cookies();
	}

	/**
	 * Send login request
	 * 
	 * @param id
	 * @param pass
	 * @return {@link Response} response
	 * @throws IOException
	 */
	private Response sendJiraLoginRequest(String id, String pass) throws IOException {
		JsonObject request = new JsonObject();
		request.addProperty(JiraConstants.ID, id);
		request.addProperty(JiraConstants.PW, pass);
		return Jsoup.connect(JiraConstants.REST_LOGIN_URL).method(Method.POST).userAgent(JiraConstants.REST_USER_AGENT)
				.ignoreContentType(true).requestBody(request.toString())
				.header(JiraConstants.CONTENT_TYPE, JiraConstants.JSON).execute();
	}

	/**
	 * check logined -> if logined return username, if not return none
	 * 
	 * @param document
	 *            <- Base url document
	 * @return {@link String} username
	 */
	public String getFullName(Document document) {
		Optional<Element> name = document.getElementsByTag(JiraConstants.CURRENT_TAG).stream().filter(
				e -> e.hasAttr(JiraConstants.NAME) && JiraConstants.NAME_PATTERN.equals(e.attr(JiraConstants.NAME)))
				.findFirst();
		return name.isPresent() ? name.get().attr(JiraConstants.CONTENT) : JiraConstants.NONE;
	}
}
