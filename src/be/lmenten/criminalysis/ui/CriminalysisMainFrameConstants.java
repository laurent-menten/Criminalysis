package be.lmenten.criminalysis.ui;

import com.formdev.flatlaf.FlatDarkLaf;

public interface CriminalysisMainFrameConstants
{
	// ========================================================================
	// = Preferences ==========================================================
	// ========================================================================

	// ------------------------------------------------------------------------
	// - UI -------------------------------------------------------------------
	// ------------------------------------------------------------------------

	String PREF_KEY_LOOKANDFEEL = "application.lookAndFeel.classname";

	String PREF_DEFAULT_LOOKANDFEEL = FlatDarkLaf.class.getName();

	// ------------------------------------------------------------------------
	// - Window ---------------------------------------------------------------
	// ------------------------------------------------------------------------

	String PREF_KEY_X = "window.x";
	int PREF_DEFAULT_X = 10;

	String PREF_KEY_Y = "window.y";
	int PREF_DEFAULT_Y = 10;

	String PREF_KEY_WIDTH = "window.width";
	int PREF_DEFAULT_WIDTH = 1280;

	String PREF_KEY_HEIGHT = "window.height";
	int PREF_DEFAULT_HEIGHT = 1024;

	String PREF_KEY_MAXIMIZED = "window.maximized";
	boolean PREF_DEFAULT_MAXIMIZED = true;

	// ------------------------------------------------------------------------

	String PREF_KEY_IDLE_TIME = "idle.time";
	long PREF_DEFAULT_IDLE_TIME = 1* 60* 1000;
}
