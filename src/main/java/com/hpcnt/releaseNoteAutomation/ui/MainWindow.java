package com.hpcnt.releaseNoteAutomation.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import com.hpcnt.releaseNoteAutomation.util.AESEncryptUtil;
import com.hpcnt.releaseNoteAutomation.util.CacheUtil;
import com.hpcnt.releaseNoteAutomation.util.GoogleSpreadSheetsUtil;
import com.hpcnt.releaseNoteAutomation.util.JiraConstants;
import com.hpcnt.releaseNoteAutomation.util.JiraLoginUtil;
import com.hpcnt.releaseNoteAutomation.util.JiraParseUtil;
import com.hpcnt.releaseNoteAutomation.util.Pair;
import com.hpcnt.releaseNoteAutomation.util.PanelFactory;
import com.hpcnt.releaseNoteAutomation.vo.Issue;

/**
 * 
 * @author owen151128
 * 
 *         ReleasenoteAutomation Tool GUI MainWindow
 * 
 *         using MVP pattern
 * 
 *         this is a View
 *
 */
public class MainWindow extends JFrame implements MainContract.View {

	/**
	 * for Default serialize
	 */
	private static final long serialVersionUID = 1L;
	private static final String APP_TITLE = "Jira Release Note Automation";
	private static final String BUTTON_TEXT = "Create ReleaseNote";
	private static final int LAYOUT_PARAM = 1;
	private static final int WIDTH = 650;
	private static final int HEIGHT = 240;

	/**
	 * Global instance of the {@link MainWindow}
	 */
	private static MainWindow instance;

	/**
	 * Global instance of the {@link MainContract.Presenter}
	 */
	private MainContract.Presenter mPresenter;

	private JiraLoginUtil login;
	private JiraParseUtil parse;
	private GoogleSpreadSheetsUtil sheet;
	private CacheUtil cache;

	private Map<String, String> cookies;
	/**
	 * Global instance of the {@link Container} MainWindow Container
	 */
	private Container container;

	/**
	 * Global instance of the {@link JTextPane} MainWindow Edit
	 */
	private JTextField edit;

	/**
	 * Global instance of the {@link JDialog} MainWindow Splash
	 */
	private JDialog splash;

	/**
	 * Global instance of the {@link JButton} MainWindow LoginButton
	 */
	private JButton loginButton;

	/**
	 * Global instance of the {@link JDialog} MainWindow LoginDialog
	 */
	private JDialog loginDialog;

	/**
	 * Global instance of the {@link JDialog} MainWindow SelectDialog
	 */
	private JDialog selectDialog;

	/**
	 * Global instance of the {@link JTextField} LoginDialog id
	 */
	private JTextField id;

	/**
	 * Global instance of the {@link JPasswordField} LoginDialog pw
	 */
	private JPasswordField pw;

	/**
	 * Global instance of the {@link JLabel} Window main text
	 */
	private JLabel mainText;

	/**
	 * Global instance of the {@link JLabel} login article
	 */
	private JLabel article;

	/**
	 * MainWindow Constructor
	 */
	private MainWindow() {
		super(APP_TITLE);
		splashInit();
		splash.setVisible(true);
		article = new JLabel("로그인을 먼저 해주세요.");
		mainText = new JLabel("위 입력란에 ReleaseNote URL 을 입력해주세요.", SwingConstants.CENTER);
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		mPresenter = new MainPresenter(this);
		container = this.getContentPane();
		edit = new JTextField();
		setLayout(new BorderLayout());
		initUi();
		setResizable(false);
		login = JiraLoginUtil.getInstance();
		parse = JiraParseUtil.getInstance(mainText);
		sheet = GoogleSpreadSheetsUtil.getInstance(mainText);
		cache = CacheUtil.getInstance();
		splashDispose();
		setVisible(true);
		if (isExistCache()) {
			splash.setVisible(true);
			HashMap<String, String> account = cache.load();
			String secretKey = account.get("key");
			try {
				requestJiraLogin(AESEncryptUtil.Decode(account.get("id"), secretKey),
						AESEncryptUtil.Decode(account.get("pw"), secretKey));
			} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException
					| NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
					| BadPaddingException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "알수 없는 오류가 발생하였습니다. 로그를 개발자에게 전송한 후. 다시 시도해 주세요.", "오류",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Single-tone pattern Instance
	 * 
	 * @return MainWindow
	 */
	public static synchronized MainWindow getInstance() {
		if (instance == null)
			instance = new MainWindow();
		return instance;
	}

	/**
	 * Init Splash Dialog
	 */
	public void splashInit() {
		JLabel text = new JLabel("로딩중 ...", SwingConstants.CENTER);
		Font font = text.getFont();
		JLabel background = new JLabel(
				new ImageIcon(new ImageIcon(MainWindow.class.getResource(("/resources/banner.png"))).getImage()
						.getScaledInstance(720, 405, Image.SCALE_DEFAULT)));
		splash = new JDialog((JFrame) null);
		JProgressBar progress = new JProgressBar();
		progress.setIndeterminate(true);
		background.setSize(600, 700);
		background.setLayout(new BorderLayout());
		text.setForeground(Color.BLACK);
		text.setFont(font.deriveFont(font.getStyle() | Font.BOLD, 30.0f));
		background.add(text, BorderLayout.CENTER);
		background.add(progress, BorderLayout.SOUTH);
		splash.add(background);
		splash.setAlwaysOnTop(true);
		splash.setUndecorated(true);
		splash.pack();
		splash.setLocationRelativeTo(null);
	}

	/**
	 * show Login JDialog & Login action event
	 */
	public void showLoginDialog() {
		loginDialog = new JDialog((JFrame) null);
		JPanel textPanel = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM + 1, LAYOUT_PARAM));
		JPanel editPanel = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM + 1, LAYOUT_PARAM));
		JPanel loginPanel = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM, LAYOUT_PARAM));
		JLabel title = new JLabel("JIRA 로그인", SwingConstants.CENTER);
		JButton loginButton = createTryLoginButton("로그인");
		id = new JTextField();
		pw = new JPasswordField();

		id.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					checkInput(id.getText(), new String(pw.getPassword()));
				}
			}

		});
		pw.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					checkInput(id.getText(), new String(pw.getPassword()));
				}
			}

		});
		loginDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		loginDialog.setPreferredSize(new Dimension(350, 120));
		loginDialog.setLayout(new BorderLayout());
		loginDialog.add(title, BorderLayout.NORTH);
		textPanel.add(new JLabel("ID", SwingConstants.CENTER));
		textPanel.add(new JLabel("PW", SwingConstants.CENTER));
		editPanel.add(id);
		editPanel.add(pw);
		textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		loginPanel.add(loginButton);
		loginDialog.add(textPanel, BorderLayout.WEST);
		loginDialog.add(editPanel, BorderLayout.CENTER);
		loginDialog.add(loginPanel, BorderLayout.EAST);
		loginDialog.setResizable(false);
		loginDialog.pack();
		loginDialog.setLocationRelativeTo(null);
		loginDialog.setVisible(true);
	}

	/**
	 * show select JDialog & select action event
	 */
	public void showSelectDialog(int pairB, String pairC, ArrayList<Issue> issue, String time) {
		ArrayList<Issue> result = new ArrayList<>();
		selectDialog = new JDialog((JFrame) null);
		JCheckBox[] checkBox = new JCheckBox[issue.size()];
		checkBox[0] = new JCheckBox("tmp");
		Font specialFont = new Font(checkBox[0].getFont().getName(), Font.BOLD, checkBox[0].getFont().getSize());
		JPanel title = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM + 2, LAYOUT_PARAM));
		JPanel titleUp = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM, LAYOUT_PARAM + 1));
		title.add(titleUp);
		JPanel main;
		JScrollPane scrollMain;
		JPanel sub = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM, LAYOUT_PARAM));
		JButton allSelect = new JButton("모두 선택");
		JButton allRelease = new JButton("모두 해제");
		JButton accept = new JButton("확인");

		allSelect.addActionListener(e -> {
			for (JCheckBox c : checkBox)
				c.setSelected(true);
			selectDialog.pack();
			selectDialog.invalidate();
		});

		allRelease.addActionListener(e -> {
			for (JCheckBox c : checkBox)
				c.setSelected(false);
			selectDialog.pack();
			selectDialog.invalidate();
		});

		accept.addActionListener(e -> {
			result.clear();
			for (Issue i : issue)
				i.setSelect(false);
			for (int i = 0; i < checkBox.length; i++) {
				if (checkBox[i].isSelected())
					issue.get(i).setSelect(true);
			}
			for (int i = 0; i < issue.size(); i++) {
				if (issue.get(i).getSelect())
					result.add(issue.get(i));
			}

			try {
				writeSheet(pairB, pairC, result);
				JOptionPane.showMessageDialog(null, "작업이 완료되었습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException iE) {
				iE.printStackTrace();
				System.out.println("IO ERROR!!!");
				JOptionPane.showMessageDialog(null, "알수 없는 오류가 발생하였습니다. 로그를 개발자에게 전송한 후. 다시 시도해 주세요.", "오류",
						JOptionPane.ERROR_MESSAGE);
			} catch (IllegalArgumentException iAE) {
				iAE.printStackTrace();
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(null, "ReleaseNote URL 이 유효하지 않습니다. 다시 시도해 주세요.", "오류",
						JOptionPane.ERROR_MESSAGE);
			}
			mainText.setText("위 입력란에 ReleaseNote URL 을 입력해주세요.");
		});

		main = PanelFactory.createPanel(new GridLayout(issue.size(), LAYOUT_PARAM));
		scrollMain = new JScrollPane(main);
		scrollMain.getVerticalScrollBar().setUnitIncrement(16);
		scrollMain.getHorizontalScrollBar().setUnitIncrement(16);

		int j = 0;
		for (Issue i : issue) {
			checkBox[j] = new JCheckBox(i.getIssueNo() + " / " + i.getIssueType() + " / " + i.getIssueKey() + " / "
					+ i.getStatus() + " / " + i.getLabels() + " / " + i.getSummary());
			if (i.getLabels().equals(JiraConstants.NONE)) {
				checkBox[j].setForeground(Color.BLUE);
				checkBox[j].setFont(specialFont);
				checkBox[j].setSelected(true);
			}
			main.add(checkBox[j]);
			j++;
		}

		titleUp.add(allSelect);
		titleUp.add(allRelease);
		JLabel text = new JLabel("데이터 추출 기준시간 : " + time);
		text.setFont(text.getFont().deriveFont(14.0f));
		title.add(text);
		sub.add(accept);

		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		sub.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		selectDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		selectDialog.setPreferredSize(new Dimension(600, 600));
		selectDialog.setLayout(new BorderLayout());

		selectDialog.add(title, BorderLayout.NORTH);

		selectDialog.add(scrollMain, BorderLayout.CENTER);

		selectDialog.add(sub, BorderLayout.SOUTH);

		selectDialog.pack();
		selectDialog.setLocationRelativeTo(null);
		selectDialog.setVisible(true);
	}

	/**
	 * Core UI initialize
	 */
	private void initUi() {
		JPanel editPanel = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM, LAYOUT_PARAM));
		JPanel buttonPanel = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM, LAYOUT_PARAM));
		JPanel editOutPanel = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM + 1, LAYOUT_PARAM));
		JPanel titlePanel = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM, LAYOUT_PARAM));
		JPanel loginPanel = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM + 1, LAYOUT_PARAM));
		editOutPanel.add(editPanel);
		editPanel.add(edit);
		editOutPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
		editOutPanel.add(mainText);
		titlePanel.add(new JLabel("Jira ReleaseNote Automation Tool", SwingConstants.CENTER));
		titlePanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 2, 10));
		loginPanel.add(article);
		loginButton = new JButton("로그인");
		loginButton.addActionListener(e -> showLoginDialog());
		loginPanel.add(loginButton);
		loginPanel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
		buttonPanel.add(createButton(BUTTON_TEXT));
		container.add(titlePanel, BorderLayout.NORTH);
		container.add(loginPanel, BorderLayout.WEST);
		container.add(editOutPanel, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Create ReleaseNote Button and setActionListener
	 * 
	 * @param Button_title
	 *            message
	 * @return Button instance
	 */
	private JButton createButton(String title) {
		JButton result = new JButton(title);
		result.setPreferredSize(new Dimension(40, 40));
		result.addActionListener(e -> {
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					if (!isJiraLoggedIn()) {
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null, "JIRA 로그인이 필요한 기능입니다. 먼저 로그인을 해주세요.", "오류",
								JOptionPane.ERROR_MESSAGE);
					} else if (!(removeBlank(edit.getText()).equals(""))) {
						if (!isExistJson()) {
							try {
								if (Desktop.isDesktopSupported())
									Desktop.getDesktop().browse(new URI(
											"https://docs.google.com/presentation/d/1iPYsQ36dQYmP8EZl_C_dzjhjEhmzDAJrFzA9bpEdsZA"));
								saveGoogleSecretKey(JOptionPane.showInputDialog("Json key 값을 입력해주세요."));
							} catch (IOException e) {
								e.printStackTrace();
							} catch (URISyntaxException e2) {
								e2.printStackTrace();
							}
						}
						try {
							String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.format(Calendar.getInstance().getTime());
							Pair<ArrayList<Issue>, Integer, String> resultSet = parse
									.parseReleaseNote(parse.getDocument(edit.getText(), cookies), cookies);
							showSelectDialog(resultSet.b, resultSet.c, resultSet.a, nowTime);
						} catch (IOException iE) {
							iE.printStackTrace();
							System.out.println("IO ERROR!!!");
							JOptionPane.showMessageDialog(null, "알수 없는 오류가 발생하였습니다. 로그를 개발자에게 전송한 후. 다시 시도해 주세요.", "오류",
									JOptionPane.ERROR_MESSAGE);
						} catch (IllegalArgumentException iAE) {
							iAE.printStackTrace();
							Toolkit.getDefaultToolkit().beep();
							JOptionPane.showMessageDialog(null, "ReleaseNote URL 이 유효하지 않습니다. 다시 시도해 주세요.", "오류",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null, "ReleaseNote URL 이 입력되지 않았습니다. 다시 시도해 주세요.", "오류",
								JOptionPane.ERROR_MESSAGE);
					}
					mainText.setText("위 입력란에 ReleaseNote URL 을 입력해주세요.");
				}
			});
			thread.start();
		});
		return result;
	}

	/**
	 * write sheet by result
	 * 
	 * @param result
	 */
	private void writeSheet(int pairB, String pairC, ArrayList<Issue> result) throws IOException {
		String sheetId = sheet.createSheet(sheet.getGoogleDriveService(), pairB, pairC, JiraConstants.DRIVE_PATH);
		sheet.writeSheet(sheet.getGoogleSpreadSheetsService(), sheetId, result);
		Toolkit.getDefaultToolkit().beep();
	}

	/**
	 * 
	 * @param Button_title
	 *            message
	 * @return Button instance
	 */
	private JButton createTryLoginButton(String title) {
		JButton result = new JButton(title);
		result.addActionListener(e -> checkInput(id.getText(), new String(pw.getPassword())));
		return result;
	}

	/**
	 * Dispose splash
	 */
	private void splashDispose() {
		splash.dispose();
	}

	/**
	 * Remove AllBlank ( between text to text blank isn't remove )
	 * 
	 * @param text
	 * @return removedBlankText
	 */
	private String removeBlank(String text) {
		String regex[] = { " ", "\\p{Z}", "(^\\p{Z}+|\\p{Z}+$)" };
		for (String s : regex)
			text = text.replaceAll(s, "");
		return text;
	}

	/**
	 * Check LoginDialog input
	 * 
	 * @param id
	 * @param pw
	 */
	private void checkInput(String id, String pw) {
		String regex[] = { " ", "\\p{Z}", "(^\\p{Z}+|\\p{Z}+$)" };
		for (String s : regex)
			id = id.replaceAll(s, "");
		for (String s : regex)
			pw = pw.replaceAll(s, "");

		if (id != null && !id.equals("") && pw != null && !pw.equals("")) {
			requestJiraLogin(id, pw);
			loginDialog.dispose();
		} else {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호를 올바르게 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Send JIRA login request method
	 * 
	 * @param id
	 * @param pw
	 */
	private void requestJiraLogin(String id, String pw) {
		Thread thread = new Thread(new Runnable() {
			String result;

			@Override
			public void run() {
				try {
					cookies = login.getJiraCredential(id, pw);
					if (cookies != null) {
						result = login.getFullName(parse.getDocument(JiraConstants.BASE_URL, cookies));
						if (!result.equals(JiraConstants.NONE)) {
							try {
								String secretKey = UUID.randomUUID().toString().replaceAll("-", "");
								HashMap<String, String> account = new HashMap<>();
								account.put("id", AESEncryptUtil.Encode(id, secretKey));
								account.put("pw", AESEncryptUtil.Encode(pw, secretKey));
								account.put("key", secretKey);
								cache.save(account);
								article.setText(result + " 님 로그인 완료");
								loginButton.setText("로그아웃");
								loginButton.addActionListener(e -> {
									JiraLoginUtil.session = null;
									cookies = null;
									cache.invalidateCache();
									loginButton.setText("로그인");
									article.setText("로그인을 먼저 해주세요.");
								});
								splashDispose();
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println("Encrypt Error! please send this log to developer");
							}
						}
					} else
						throw new IOException("cookies not found");
				} catch (IOException e) {
					e.printStackTrace();
					splashDispose();
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호를 올바르게 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		splash.setVisible(true);
		thread.start();
		System.out.println("request");
	}

	/**
	 * Save GoogleSecretKey method
	 * 
	 * @param id
	 * @param pw
	 */
	private void saveGoogleSecretKey(String key) {
		try {
			cache.saveSecretKey(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		splashDispose();

	}

	/**
	 * Check exist cache
	 * 
	 * @return result
	 */
	private boolean isExistCache() {
		if (cache.load() != null)
			return true;
		else
			return false;
	}

	/**
	 * Check loggedIn JIRA
	 * 
	 * @return result
	 */
	private boolean isJiraLoggedIn() {
		if (cookies != null && JiraLoginUtil.session != null)
			return true;
		else
			return false;
	}

	/**
	 * check exist Json
	 * 
	 * @return result
	 */
	private boolean isExistJson() {
		if (cache.loadSecretKey() != null)
			return true;
		else
			return false;
	}

}
