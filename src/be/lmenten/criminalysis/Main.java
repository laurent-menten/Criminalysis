package be.lmenten.criminalysis;

import be.lmenten.criminalysis.db.CriminalysisDatabase;
import be.lmenten.criminalysis.db.CriminalysisDatabaseDescriptor;
import be.lmenten.criminalysis.ui.CriminalysisMainFrame;
import be.lmenten.criminalysis.ui.CriminalysisSplashMenu;
import be.lmenten.util.app.AppUtils;
import be.lmenten.util.exception.AbortException;
import be.lmenten.util.jdbc.h2.H2Database;
import be.lmenten.util.logging.AnsiLogFormatter;
import be.lmenten.util.logging.LogLevel;
import be.lmenten.util.logging.LogRegExPackageFilter;
import be.lmenten.util.logging.swing.JLogHandlerFrame;
import be.lmenten.util.swing.SwingUtils;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.jetbrains.annotations.PropertyKey;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main
	implements CriminalysisConstants
{
	private static boolean swingExplorerRestart = false;

	private static JLogHandlerFrame logFrame = null;

	// ========================================================================
	// = Entrypoint ===========================================================
	// ========================================================================

	public static void main( String[] args )
		throws IOException
	{
		// --------------------------------------------------------------------
		// - Static setup -----------------------------------------------------
		// --------------------------------------------------------------------

		H2Database.useH2Preferences();

		// --------------------------------------------------------------------
		// - Debug configuration ----------------------------------------------
		// --------------------------------------------------------------------

		String debugModeProperty
			= System.getProperty( SYSTEM_PROPERTY_DEBUG_MODE, Boolean.toString( DEFAULT_DEBUG_MODE_ENABLED ) );
		boolean debugMode = Boolean.parseBoolean( debugModeProperty );

		// --------------------------------------------------------------------
		// - Swing Explorer activation ----------------------------------------
		// --------------------------------------------------------------------

		String debugSwingExplorerProperty
			= System.getProperty( SYSTEM_PROPERTY_DEBUG_SWING_EXPLORER, Boolean.toString( DEFAULT_DEBUG_SWING_EXPLORER_ENABLED ) );
		boolean debugSwingExplorer = Boolean.parseBoolean( debugSwingExplorerProperty );

		// SwingExplorer launcher will call our main() method again so we set
		// swingExplorerRestart to true to avoid an infinite loop.

		if( debugMode && debugSwingExplorer && !swingExplorerRestart )
		{
			swingExplorerRestart = true;

			String[] swingExplorerArgs = new String[ args.length + 1 ];
			swingExplorerArgs[0] = Main.class.getName();
			System.arraycopy( args, 0, swingExplorerArgs, 1, args.length );

			org.swingexplorer.Launcher.main( swingExplorerArgs );

			System.exit( 0 );
		}

		// --------------------------------------------------------------------
		// - FlatInspector installation ---------------------------------------
		// --------------------------------------------------------------------

		String debugFlatInspectorProperty
			= System.getProperty( SYSTEM_PROPERTY_DEBUG_FLAT_INSPECTOR, Boolean.toString( DEFAULT_DEBUG_FLAT_INSPECTOR_ENABLED ) );
		boolean debugFlatInspector = Boolean.parseBoolean( debugFlatInspectorProperty );

		if( debugMode && debugFlatInspector )
		{
			FlatInspector.install( DEFAULT_DEBUG_FLAT_INSPECTOR_KEY );
			FlatUIDefaultsInspector.install( DEFAULT_DEBUG_FLAT_INSPECTOR_UIDEFAULTS_KEY );
		}

		// --------------------------------------------------------------------
		// - Initialize logging service ---------------------------------------
		// --------------------------------------------------------------------

		// Default log level is CONFIG for release run, but it can be set to
		// another level using the logLevel system property.

		String logLevelProperty = System.getProperty( SYSTEM_PROPERTY_LOGLEVEL, DEFAULT_LOGLEVEL.getName() );
		Level logLevel = DEFAULT_LOGLEVEL;

		try
		{
			logLevel = LogLevel.valueOf( logLevelProperty ).getLevel();
		}
		catch( IllegalArgumentException ex )
		{
			System.err.println( MessageFormat.format( $( "err.invalid.loglevel" ), logLevelProperty ) );
			System.exit( -1 );
		}

		// In debug mode, log level is always forced to ALL.
		// Log window will be opened on the last screen from the screens list
		// rather than the default screen.

		if( debugMode )
		{
			logLevel = DEFAULT_DEBUG_LOGLEVEL;

			logFrame = new JLogHandlerFrame( $( "log.windows.title" ) );
			logFrame.installHandler();

			SwingUtils.showOnScreen( -1, logFrame );
			logFrame.setVisible( true );
		}

		// --------------------------------------------------------------------

		// Create log directory, we can create other application directories
		//  as well if needed.

		File logDir = Criminalysis.getUserLogDir();
		if( ! logDir.exists() )
		{
			Files.createDirectories( logDir.toPath() );
		}

		// Install file logger

		File logFile = AnsiLogFormatter.logToFile( logDir, LOGFILE_PREFIX );

		// --------------------------------------------------------------------

		// Install ANSI log formatter.

		AnsiLogFormatter.install();

		// --------------------------------------------------------------------

		// Install log filter.

		String logFilter
			= System.getProperty( SYSTEM_PROPERTY_LOGFILTER, DEFAULT_LOGFILTER );

		LogRegExPackageFilter.install( logFilter, logLevel );

		// --------------------------------------------------------------------
		// - Diagnostic -------------------------------------------------------
		// --------------------------------------------------------------------

		log.log( Level.INFO, $("msg.java.version"), System.getProperty("java.version") );
		log.log( Level.INFO, $("msg.java.vendor"), System.getProperty("java.vendor") );

		log.log( Level.INFO, $("msg.os.name"), System.getProperty("os.name") );
		log.log( Level.INFO, $("msg.os.version"), System.getProperty("os.version") );
		log.log( Level.INFO, $("msg.os.arch"), System.getProperty("os.arch") );

		log.log( Level.INFO, $("msg.app.version"), Criminalysis.APPLICATION_VERSION );
		log.log( Level.INFO, $("msg.app.db.schema.version" ), CriminalysisDatabase.DATABASE_SCHEMA_VERSION );

		log.log( Level.INFO, $("msg.app.running"), (AppUtils.runningFromJar( Main.class ) ? $("msg.app.running.jar") : $("msg.app.running.classpath")) );
		log.log( Level.INFO, $("msg.app.debug"), (debugMode ? $("msg.enabled") : $("msg.disabled")) );
		log.log( Level.INFO, $( "msg.app.log.level" ), logLevel );
		log.log( Level.INFO, $( "msg.app.log.filter" ), logFilter );
		log.log( Level.INFO, $("msg.app.swingexplorer"), (debugSwingExplorer ? $("msg.enabled") : $("msg.disabled")) );
		log.log( Level.INFO, $("msg.app.flatinspector"), (debugFlatInspector ? $("msg.enabled") : $("msg.disabled")) );

		String installationDir = Criminalysis.getInstallationDir().getAbsolutePath();
		log.log( Level.INFO, $("msg.app.installation.dir" ), installationDir );

		String homeDir = Criminalysis.getUserDir().getAbsolutePath();
		log.log( Level.INFO, $("msg.app.user.dir" ), homeDir );

		log.log( Level.INFO, $("msg.app.logfile"), logFile );

		// --------------------------------------------------------------------
		// - Install FlatLaf look and feels -----------------------------------
		// --------------------------------------------------------------------

		FlatDarkLaf.installLafInfo();
		FlatLightLaf.installLafInfo();
		FlatDarculaLaf.installLafInfo();
		FlatIntelliJLaf.installLafInfo();

		FlatMacDarkLaf.installLafInfo();
		FlatMacLightLaf.installLafInfo();

		// --------------------------------------------------------------------

		FlatDarkLaf.setup();

		// --------------------------------------------------------------------
		// - Database file selection ------------------------------------------
		// --------------------------------------------------------------------

		CriminalysisDatabaseDescriptor databaseDescriptor = new CriminalysisDatabaseDescriptor();

		CriminalysisSplashMenu.Choice choice = CriminalysisSplashMenu.Choice.SETTINGS;

		// --------------------------------------------------------------------
		// - Application ------------------------------------------------------
		// --------------------------------------------------------------------

		final Instant startTime = Instant.now();

		if( choice != CriminalysisSplashMenu.Choice.QUIT )
		{
			try( CriminalysisDatabase db = databaseDescriptor.getDatabase() ;
			        Criminalysis app = new Criminalysis( db, debugMode ) ;
				        CriminalysisMainFrame mainFrame = new CriminalysisMainFrame( app ) )
			{
				mainFrame.setVisible( true );

				// ------------------------------------------------------------
				// - Pre-initialize application -------------------------------
				// ------------------------------------------------------------

				app.preInit();

				mainFrame.preInit();

				// ------------------------------------------------------------
				// - Open database --------------------------------------------
				// ------------------------------------------------------------

				db.open();

				// ------------------------------------------------------------
				// - Initialize application -----------------------------------
				// ------------------------------------------------------------

				app.init();

				mainFrame.init();

				// ------------------------------------------------------------
				// -
				// ------------------------------------------------------------

				app.run();

				// ------------------------------------------------------------
				// - Sync on termination --------------------------------------
				// ------------------------------------------------------------

				app.waitForTermination();
			}

			catch( AbortException ex )
			{
				log.log( Level.SEVERE, $( "err.abort.exception" ), ex );
			}

			catch( SQLException ex )
			{
				log.log( Level.SEVERE, $( "err.sql.exception" ), ex );
				Criminalysis.reportUnexpectedException( ex, logFile );
			}

			catch( Exception ex )
			{
				log.log( Level.SEVERE, $( "err.unexpected.exception" ), ex );
				Criminalysis.reportUnexpectedException( ex, logFile );
			}
		}

		final Duration duration = Duration.between( startTime, Instant.now() );

		// --------------------------------------------------------------------
		// - Uptime -----------------------------------------------------------
		// --------------------------------------------------------------------

		String uptime = String.format( "%02d:%02d:%02d.%03d",
			duration.toHoursPart(),
			duration.toMinutesPart(),
			duration.toSecondsPart(),
			duration.toMillisPart()
		);

		log.log( Level.INFO, $("app.finished"), uptime );

		// --------------------------------------------------------------------
		// - Termination ------------------------------------------------------
		// --------------------------------------------------------------------

		// Close log frame

		if( debugMode && (logFrame != null) )
		{
			logFrame.dispose();
		}

		// --------------------------------------------------------------------

		// Close log file

		AnsiLogFormatter.closeLogFiles();

		// --------------------------------------------------------------------

		// Remove logfile unless otherwise request

		String keepLogFileProperty
			= System.getProperty( SYSTEM_PROPERTY_KEEP_LOGFILE, Boolean.toString( DEFAULT_KEEP_LOGFILE ) );
		boolean keepLogFile = Boolean.parseBoolean( keepLogFileProperty );

		if( ! debugMode && ! keepLogFile && (logFile != null) )
		{
			Files.deleteIfExists( logFile.toPath() );
		}

		// --------------------------------------------------------------------
		// - Exit -------------------------------------------------------------
		// --------------------------------------------------------------------

		System.exit( 0 );
	}

	// ========================================================================
	// = Logger ===============================================================
	// ========================================================================

	private static final Logger log
		= Logger.getLogger( MethodHandles.lookup().lookupClass().getName() );

	// ========================================================================
	// = i18n =================================================================
	// ========================================================================

	private static final ResourceBundle rs
		= ResourceBundle.getBundle( Criminalysis.class.getName() );

	private static String $( @PropertyKey(resourceBundle = "be.lmenten.criminalysis.Criminalysis") String key )
	{
		return rs.getString( key );
	}
}
