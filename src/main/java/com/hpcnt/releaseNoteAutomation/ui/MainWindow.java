package com.hpcnt.releaseNoteAutomation.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import com.hpcnt.releaseNoteAutomation.util.PanelFactory;

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
	private static final long serialVersionUID = 1L;
	private static final String APP_TITLE = "AzarReleaseNoteAutomation";
	private static final String BUTTON_TEXT = "create ReleaseNote";
	private static final int LAYOUT_PARAM = 1;
	private static final int WIDTH = 650;
	private static final int HEIGHT = 80;

	/**
	 * Global instance of the {@link MainWindow}
	 */
	private static MainWindow instance;

	/**
	 * Global instance of the {@link MainContract.Presenter}
	 */
	private MainContract.Presenter mPresenter;

	/**
	 * Global instance of the {@link Container} MainWindow Container
	 */
	private Container container;

	/**
	 * Global instance of the {@link JTextPane} MainWindow Edit
	 */
	private JTextPane edit;

	/**
	 * MainWindow Constructor
	 */
	private MainWindow() {
		super(APP_TITLE);
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mPresenter = new MainPresenter(this);
		container = this.getContentPane();
		edit = new JTextPane();
		setLayout(new BorderLayout());
		initUi();
		setResizable(false);
		setVisible(true);
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

	private void initUi() {
		JPanel editPanel = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM, LAYOUT_PARAM));
		JPanel buttonPanel = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM, LAYOUT_PARAM));
		JPanel editOutPanel = PanelFactory.createPanel(new GridLayout(LAYOUT_PARAM, LAYOUT_PARAM));
		editPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
		editPanel.add(edit);
		editOutPanel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
		editOutPanel.add(editPanel);
		buttonPanel.add(createButton(BUTTON_TEXT));
		container.add(editOutPanel, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Create Button and setActionListener
	 * 
	 * @param Button_title
	 *            message
	 * @return Button Instance
	 */
	private JButton createButton(String title) {
		JButton result = new JButton(title);
		result.addActionListener(e -> {

		});
		return result;
	}

}
