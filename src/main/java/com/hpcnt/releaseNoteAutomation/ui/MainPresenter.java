package com.hpcnt.releaseNoteAutomation.ui;

/**
 * 
 * @author owen151128
 * 
 *         ReleasenoteAutomation Tool GUI MainWindow
 * 
 *         using MVP pattern
 * 
 *         this is a Presenter
 *
 */
public class MainPresenter implements MainContract.Presenter {

	private MainContract.View mView;

	public MainPresenter(MainContract.View view) {
		mView = view;
	}
}
