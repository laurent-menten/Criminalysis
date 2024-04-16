package be.lmenten.criminalysis;

import java.util.logging.Level;

public interface CriminalysisConstants
{
	// ========================================================================
	// =
	// ========================================================================

	String APPLICATION_NAME = "Criminalysis";

	// ========================================================================
	// = Misc. constants ======================================================
	// ========================================================================

	String LOGFILE_PREFIX = "Criminalysis";

	// ========================================================================
	// = System properties ====================================================
	// ========================================================================

	String SYSTEM_PROPERTY_DEBUG_MODE = "debugMode";
	boolean DEFAULT_DEBUG_MODE_ENABLED = false;

	String SYSTEM_PROPERTY_DEBUG_SWING_EXPLORER = "debugSwingExplorer";
	boolean DEFAULT_DEBUG_SWING_EXPLORER_ENABLED = false;

	String SYSTEM_PROPERTY_DEBUG_FLAT_INSPECTOR = "debugFlatInspector";
	boolean DEFAULT_DEBUG_FLAT_INSPECTOR_ENABLED = false;
	String DEFAULT_DEBUG_FLAT_INSPECTOR_KEY = "ctrl alt F1";
	String DEFAULT_DEBUG_FLAT_INSPECTOR_UIDEFAULTS_KEY = "ctrl alt F2";

	String SYSTEM_PROPERTY_LOGLEVEL = "logLevel";
	Level DEFAULT_LOGLEVEL = Level.CONFIG;
	Level DEFAULT_DEBUG_LOGLEVEL = Level.ALL;

	String SYSTEM_PROPERTY_LOGFILTER = "logFilter";
	String DEFAULT_LOGFILTER = "be\\.lmenten\\..*";

	String SYSTEM_PROPERTY_KEEP_LOGFILE = "keepLogFile";
	boolean DEFAULT_KEEP_LOGFILE = false;

	// ========================================================================
	// = Configuration ========================================================
	// ========================================================================

	String CONFIG_CATEGORY_EDITOR = "Editor";
	String CONFIG_CATEGORY_UI = "User interface";

	// ========================================================================
	// = Actions ==============================================================
	// ========================================================================

	String ACTION_CATEGORY_NONE = "";
	String ACTION_CATEGORY_DEBUG = "Debug";
}
