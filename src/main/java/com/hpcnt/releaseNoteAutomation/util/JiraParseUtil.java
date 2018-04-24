package com.hpcnt.releaseNoteAutomation.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpcnt.releaseNoteAutomation.vo.Issue;

public class JiraParseUtil {

	private static JiraParseUtil instance;

	private JsonParser jsonParser;

	private Pattern pattern;
	private Matcher matcher;

	private JiraParseUtil() {
		this.jsonParser = new JsonParser();
	}

	public static synchronized JiraParseUtil getInstance() {
		if (instance == null)
			instance = new JiraParseUtil();
		return instance;
	}

	public Document getDocument(String url, Map<String, String> cookies) throws IOException {
		return Jsoup.connect(url).cookies(cookies).method(Method.GET).userAgent(JiraConstants.USER_AGENT).get();
	}

	public ArrayList<Issue> parseReleaseNote(Document document, ArrayList<Issue> list, Map<String, String> cookies)
			throws IOException {
		ArrayList<Issue> resultList = new ArrayList<>();
		ArrayList<String> summaryList = new ArrayList<>();
		ArrayList<String> issueKeyList = new ArrayList<>();
		ArrayList<String> issueTypeList = new ArrayList<>();
		ArrayList<String> statusList = new ArrayList<>();
		ArrayList<String> labelList = new ArrayList<>();

		Elements result = document.select("div > div > section > ul > li");

		for (Element e : result) {
			summaryList.add(e.text().toString().replaceAll(JiraConstants.SUMMARY_REGEX, ""));
		}

		pattern = Pattern.compile(JiraConstants.KEY_REGEX);
		for (Element e : result) {
			matcher = pattern.matcher(e.text());
			if (matcher.find()) {
				issueKeyList.add(matcher.group(1));
			}
		}

		getIssueInformation(cookies, issueKeyList, issueTypeList, statusList, labelList);

		for (int i = 0; i < summaryList.size(); i++) {
			resultList.add(new Issue.Builder(i + 1, summaryList.get(i)).issueKey(issueKeyList.get(i))
					.issueType(issueTypeList.get(i)).status(statusList.get(i)).labels(labelList.get(i)).build());
		}

		return resultList;
	}

	private void getIssueInformation(Map<String, String> cookies, ArrayList<String> issueKeyList,
			ArrayList<String> issueTypeList, ArrayList<String> statusList, ArrayList<String> labelList)
			throws IOException {
		Document document;
		Map<String, String> param = new HashMap<>();
		param.put(JiraConstants.JSON_FIELDS,
				JiraConstants.JSON_ISSUE_TYPE + "," + JiraConstants.JSON_STATUS + "," + JiraConstants.JSON_LABELS);
		for (String s : issueKeyList) {
			document = Jsoup.connect(JiraConstants.BASE_URL + JiraConstants.REQUEST_INFO_URL + s).cookies(cookies)
					.data(param).ignoreContentType(true).header(JiraConstants.CONTENT_TYPE, JiraConstants.JSON)
					.userAgent(JiraConstants.REST_USER_AGENT).get();
			JsonElement json = jsonParser.parse(document.body().text());
			issueTypeList.add(json.getAsJsonObject().getAsJsonObject(JiraConstants.JSON_FIELDS)
					.getAsJsonObject(JiraConstants.JSON_ISSUE_TYPE).get(JiraConstants.JSON_NAME).getAsString());
			statusList.add(json.getAsJsonObject().getAsJsonObject(JiraConstants.JSON_FIELDS)
					.getAsJsonObject(JiraConstants.JSON_STATUS).get(JiraConstants.JSON_NAME).getAsString());
			JsonArray labelsArray = json.getAsJsonObject().getAsJsonObject(JiraConstants.JSON_FIELDS)
					.getAsJsonArray(JiraConstants.JSON_LABELS);
			boolean isNoQa = false;
			for (JsonElement e : labelsArray) {
				if (e.getAsString().equals(JiraConstants.NO_QA)) {
					isNoQa = true;
					break;
				}
			}

			if (isNoQa)
				labelList.add(JiraConstants.NO_QA);
			else
				labelList.add(JiraConstants.NONE);
		}
	}

}
