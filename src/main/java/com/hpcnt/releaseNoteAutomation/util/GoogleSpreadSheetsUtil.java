package com.hpcnt.releaseNoteAutomation.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.hpcnt.releaseNoteAutomation.vo.Issue;

/**
 * 
 * @author Owen151128
 * 
 *         about GoogleSpreadSheetUtil Util & Method
 *
 */
public class GoogleSpreadSheetsUtil {

	private static GoogleSpreadSheetsUtil instance;

	/**
	 * Get sheets id for regular expression from URL
	 */
	private static final String GET_ID_REGULAR_EXPRESSION = "(?<=\\/d\\/)[^\\/]*";

	/**
	 * variable of the getSheetsId
	 */
	private Pattern sheetsIdPattern;
	private Matcher sheetsIdMatcher;

	/**
	 * OAUTH 2.0 client name
	 */
	private final String AUTH_NAME = "hpcntqa";

	/**
	 * OAUTH 2.0 credential save_path for SpreadSheets
	 */
	private final static File SHEETS_DATA_STORE_DIR = new File(System.getProperty("user.home"),
			".credentials/sheets.releaseNote");

	/**
	 * OAUTH 2.0 credential save_path for Google Drive
	 */
	private final static File DRIVE_DATA_STORE_DIR = new File(System.getProperty("user.home"),
			".credentials/drive.releaseNote");

	/**
	 * Global instance of the {@link FileDataStoreFactory} for SpreadSheets
	 */
	private static FileDataStoreFactory SHEETS_DATA_STORE_FACTORY;

	/**
	 * Global instance of the {@link FileDataStoreFactory} for Google Drive
	 */
	private static FileDataStoreFactory DRIVE_DATA_STORE_FACTORY;

	/**
	 * Global instance of the {@link JsonFactory}
	 */
	private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/**
	 * Global instance of the {@link HTTP_TRANSPORT}
	 */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Google spreadsheets permission settings to SCOPE
	 */
	private final List<String> SPREAD_SHEETS_SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

	/**
	 * Google drive permission settings to SCOPE
	 */
	private final List<String> DRIVE_SHEETS_SCOPES = Arrays.asList(DriveScopes.DRIVE_FILE);

	/**
	 * Local callback port
	 */
	private final int LOCAL_SERVER_PORT = 8080;

	/**
	 * instance of {@link JLabel} MainWindow.mainText
	 */
	private JLabel mainText;

	/**
	 * HTTP_TRANSPORT, DATA_STORE_FACTORY initialize
	 */
	public static synchronized GoogleSpreadSheetsUtil getInstance(JLabel mainText) {
		if (instance == null)
			instance = new GoogleSpreadSheetsUtil(mainText);
		return instance;
	}

	private GoogleSpreadSheetsUtil(JLabel mainText) {
		sheetsIdPattern = Pattern.compile(GET_ID_REGULAR_EXPRESSION);
		this.mainText = mainText;
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			SHEETS_DATA_STORE_FACTORY = new FileDataStoreFactory(SHEETS_DATA_STORE_DIR);
			DRIVE_DATA_STORE_FACTORY = new FileDataStoreFactory(DRIVE_DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * get google spread sheets util Oauth2 authentication
	 * 
	 * @return Google Credential
	 * @throws IOException
	 */
	private Credential getSpreadSheetsOauth2Authorize() throws IOException {
		FileInputStream fis = new FileInputStream(new File(System.getProperty("user.home"), ".credentials/SECRET_KEY"));
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(fis));
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SPREAD_SHEETS_SCOPES).setDataStoreFactory(SHEETS_DATA_STORE_FACTORY)
						.setAccessType("offline").build();

		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(LOCAL_SERVER_PORT).build();

		Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

		return credential;
	}

	/**
	 * get google drive sheets util Oauth2 authentication
	 * 
	 * @return Google Credential
	 * @throws IOException
	 */
	private Credential getDriveOauth2Authorize() throws IOException {
		FileInputStream fis = new FileInputStream(new File(System.getProperty("user.home"), ".credentials/SECRET_KEY"));
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(fis));
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, DRIVE_SHEETS_SCOPES).setDataStoreFactory(DRIVE_DATA_STORE_FACTORY)
						.setAccessType("offline").build();

		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(LOCAL_SERVER_PORT).build();

		Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

		return credential;
	}

	/**
	 * get GoogleSpreadSheets
	 * 
	 * @return Sheets
	 * @throws IOException
	 */
	public Sheets getGoogleSpreadSheetsService() throws IOException {
		Credential credential = getSpreadSheetsOauth2Authorize();

		if (credential != null) {
			return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(AUTH_NAME).build();
		} else {
			throw new IOException();
		}
	}

	/**
	 * get GoogleDrive
	 * 
	 * @return Drive
	 * @throws IOException
	 */
	public Drive getGoogleDriveService() throws IOException {
		Credential credential = getDriveOauth2Authorize();

		if (credential != null) {
			return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(AUTH_NAME).build();
		} else {
			throw new IOException();
		}
	}

	/**
	 * URL to parse ID
	 * 
	 * @param Google
	 *            SpreadSheets id from url
	 * @return Google spread sheets ID
	 */
	public String getSpreadSheetsId(String url) {
		sheetsIdMatcher = sheetsIdPattern.matcher(url);
		if (sheetsIdMatcher.find())
			return sheetsIdMatcher.group();
		else
			return null;
	}

	/**
	 * Create google spreadsheet in google drive
	 * 
	 * @param drive
	 * @param os
	 * @param version
	 * @param pathId
	 * @return sheet id
	 * @throws IOException
	 */
	public String createSheet(Drive drive, int os, String version, String pathId) throws IOException {
		mainText.setText("3/4 구글 스프레드 시트 만드는중 ...");
		com.google.api.services.drive.model.File file = new com.google.api.services.drive.model.File();
		if (os == JiraConstants.AZAND) {
			file.setName(JiraConstants.AOS_TITLE + JiraConstants.BLANK + version + JiraConstants.SHEET_NAME);
		} else {
			file.setName(JiraConstants.IOS_TITLE + JiraConstants.BLANK + version + JiraConstants.SHEET_NAME);
		}
		file.setParents(Collections.singletonList(pathId));
		file.setMimeType(JiraConstants.MIME_TYPE);
		com.google.api.services.drive.model.File result = drive.files().create(file).setFields("id, parents").execute();
		return result.getId();
	}

	/**
	 * Write google spreadsheet
	 * 
	 * @param sheet
	 * @param id
	 * @param list
	 * @throws IOException
	 */
	public void writeSheet(Sheets sheet, String id, ArrayList<Issue> list) throws IOException {
		mainText.setText("4/4 구글 스프레드 시트에 릴리즈 노트  작성하는중 ...");
		String range = "B2:G2";
		List<List<Object>> writeData = new ArrayList<>();
		ArrayList<String> data = new ArrayList<>();

		data.add(JiraConstants.NUMBER);
		data.add(JiraConstants.ISSUE_TYPE);
		data.add(JiraConstants.ISSUE_KEY);
		data.add(JiraConstants.SUMMARY);
		data.add(JiraConstants.STATUS);
		data.add(JiraConstants.LABELS);

		List<Object> dataRow = new ArrayList<>();
		for (String s : data) {
			dataRow.add(s);
		}
		writeData.add(dataRow);
		ValueRange value = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
		sheet.spreadsheets().values().update(id, range, value).setValueInputOption("RAW").execute();

		/**
		 * array is start index 0, so range use list.size +2 integer
		 */
		range = JiraConstants.RANGE_PREFIX + (list.size() + 2);

		writeData.clear();

		for (Issue i : list) {
			dataRow = new ArrayList<>();
			dataRow.add(i.getIssueNo());
			dataRow.add(i.getIssueType());
			dataRow.add(JiraConstants.HREF_FRONT + i.getIssueKey() + JiraConstants.HREF_MIDDLE + i.getIssueKey()
					+ JiraConstants.HREF_REAR);
			dataRow.add(i.getSummary());
			dataRow.add(i.getStatus());
			if (i.getLabels().equals(JiraConstants.NO_QA))
				dataRow.add(JiraConstants.NO_QA);
			else
				dataRow.add(JiraConstants.NULL);
			writeData.add(dataRow);
		}

		value = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
		sheet.spreadsheets().values().update(id, range, value).setValueInputOption("USER_ENTERED").execute();
	}
}
