package com.hpcnt.releaseNoteAutomation.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hpcnt.releaseNoteAutomation.vo.Issue;

/**
 * 
 * @author owen151128
 *
 *         JIRA parse wrapper util class
 */
public class JiraParseUtil {

	/**
	 * Java single-tone pattern instance
	 */
	private static JiraParseUtil instance;

	/**
	 * Global instance of {@link JsonParser}
	 */
	private JsonParser jsonParser;

	/**
	 * Global instance of variable for REGEX
	 */
	private Pattern pattern;
	private Matcher matcher;

	/**
	 * instance of {@link JLabel} MainWindow.mainText
	 */
	private JLabel mainText;

	private JiraParseUtil(JLabel mainText) {
		this.mainText = mainText;
		this.jsonParser = new JsonParser();
	}

	/**
	 * Single-tone pattern
	 * 
	 * @return {@link JiraParseUtil} instance
	 */
	public static synchronized JiraParseUtil getInstance(JLabel mainText) {
		if (instance == null)
			instance = new JiraParseUtil(mainText);
		return instance;
	}

	/**
	 * Get HTML Document
	 * 
	 * @param url
	 * @param cookies
	 * @return {@link Document} HTML document
	 * @throws IOException
	 */
	public Document getDocument(String url, Map<String, String> cookies) throws IOException {
		return Jsoup.connect(url).cookies(cookies).method(Method.GET).userAgent(JiraConstants.USER_AGENT).get();
	}

	/**
	 * Parsing releaseNote
	 * 
	 * @param document
	 * @param list
	 * @param cookies
	 * @return {@link Pair<ArrayList<Issue>, Integer, String>} resultPair
	 * @throws IOException
	 */
	public Pair<ArrayList<Issue>, Integer, String> parseReleaseNote(Document document, Map<String, String> cookies)
			throws IOException {
		mainText.setText("1/4 릴리즈 노트 파싱하는중 ...");
		Pair<ArrayList<Issue>, Integer, String> resultPair = null;
		ArrayList<Issue> resultList = new ArrayList<>();
		ArrayList<String> summaryList = new ArrayList<>();
		ArrayList<String> issueKeyList = new ArrayList<>();
		ArrayList<String> issueTypeList = new ArrayList<>();
		ArrayList<String> statusList = new ArrayList<>();
		ArrayList<String> labelList = new ArrayList<>();

		Elements result = document.select(JiraConstants.GET_ISSUE_SELECTOR);

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

		result = document.select(JiraConstants.GET_VERSION_SELECTOR);

		switch (resultList.get(0).getIssueKey().substring(0, 5)) {
		case JiraConstants.AOS:
			resultPair = new Pair<ArrayList<Issue>, Integer, String>(resultList, JiraConstants.AZAND,
					result.get(0).attr(JiraConstants.CONTENT));
			break;
		case JiraConstants.IOS:
			resultPair = new Pair<ArrayList<Issue>, Integer, String>(resultList, JiraConstants.AZIOS,
					result.get(0).attr(JiraConstants.CONTENT));
			break;
		}

		return resultPair;
	}

	/**
	 * Get issue information(issueType, issueStatus, issueLabels)
	 * 
	 * @param cookies
	 * @param issueKeyList
	 * @param issueTypeList
	 * @param statusList
	 * @param labelList
	 * @throws IOException
	 */
	private void getIssueInformation(Map<String, String> cookies, ArrayList<String> issueKeyList,
			ArrayList<String> issueTypeList, ArrayList<String> statusList, ArrayList<String> labelList)
			throws IOException {
		mainText.setText("2/4 이슈 정보들 불러오는중 ...");
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
