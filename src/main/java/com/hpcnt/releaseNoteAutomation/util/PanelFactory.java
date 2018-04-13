package com.hpcnt.releaseNoteAutomation.util;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public class PanelFactory {

	/**
	 * Not used Constructor
	 */
	private PanelFactory() {

	}

	/**
	 * Set layout method
	 * 
	 * @param LayoutManager
	 * @return JPanel
	 */
	public static JPanel createPanel(LayoutManager mgr) {
		JPanel result = new JPanel();
		if (mgr == null)
			return result;
		result.setLayout(mgr);
		return result;
	}
}
