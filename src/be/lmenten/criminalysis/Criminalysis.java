/*
 * ============================================================================
 * =- Criminalysis -=- A crime analysis toolbox -=- (c) 2024+ Laurent Menten -=
 * ============================================================================
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 * ============================================================================
 */

package be.lmenten.criminalysis;

import be.lmenten.criminalysis.db.CriminalysisDatabase;
import be.lmenten.criminalysis.plugins.Plugin;
import be.lmenten.criminalysis.plugins.PluginsManager;
import be.lmenten.criminalysis.scripting.ScriptClassFilter;
import be.lmenten.criminalysis.ui.CriminalysisMainFrame;
import be.lmenten.util.exception.AbortException;
import org.jetbrains.annotations.PropertyKey;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Criminalysis
	implements CriminalysisConstants, AutoCloseable, Runnable
{
	// ========================================================================
	// = BEGIN OF GENERATED VALUES - DO NOT EDIT ==============================
	// ========================================================================

	// These values are mirrored from file build.properties

	private static final int VERSION_MAJOR = 1;
	private static final int VERSION_MINOR = 0;
	private static final int VERSION_SECURITY = 1;

	private static final String PRE_RELEASE_IDENTIFIER = "internal";

	// These values are computed by buildTools.ant.xml (task: "build number")

	private static final int BUILD_NUMBER = 51;
	private static final String BUILD_DATE = "20240417";
	private static final String BUILD_TIME = "203019";

	// ========================================================================
	// = END OF GENERATED VALUES - DO NOT EDIT ================================
	// ========================================================================

	private static final String APPLICATION_VERSION_STRING
		= String.format("%d.%d.%d-%s-%d-%s.%s",
			VERSION_MAJOR, VERSION_MINOR, VERSION_SECURITY,
			PRE_RELEASE_IDENTIFIER,
			BUILD_NUMBER,
			BUILD_DATE, BUILD_TIME
	);

	public static final Runtime.Version APPLICATION_VERSION
		= Runtime.Version.parse( APPLICATION_VERSION_STRING );

	private static final RuntimeConfiguration CONFIGURATION
		= new RuntimeConfiguration();

	// ========================================================================
	// =
	// ========================================================================

	private final boolean debugMode;

	private final CountDownLatch runningLatch
		= new CountDownLatch( 1 );

	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// - Plugins management ---------------------------------------------------
	// ------------------------------------------------------------------------

	private PluginsManager<Plugin> pluginsManager;

	// ------------------------------------------------------------------------
	// - Script engine --------------------------------------------------------
	// ------------------------------------------------------------------------

	private final NashornScriptEngineFactory scriptEngineFactory
		= new NashornScriptEngineFactory();

	private final ScriptClassFilter scriptClassFilter
		= new ScriptClassFilter();

	private final ScriptEngine scriptEngine
		= scriptEngineFactory.getScriptEngine( scriptClassFilter );

	// ------------------------------------------------------------------------

	private final ScriptEngineInterface scriptEngineInterface
		= new ScriptEngineInterface( this );

	// ------------------------------------------------------------------------
	// -
	// ------------------------------------------------------------------------

	private final CriminalysisDatabase db;

	private CriminalysisMainFrame mainFrame;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	public Criminalysis( CriminalysisDatabase db, boolean debugMode )
	{
		this.db = db;
		this.debugMode = debugMode;
	}

	public void setMainFrame( CriminalysisMainFrame mainFrame )
	{
		this.mainFrame = mainFrame;
	}

	// ========================================================================
	// = Initialization =======================================================
	// ========================================================================

	// ------------------------------------------------------------------------
	// preInit() is called BEFORE the database is opened.
	// Preferences ARE NOT accessible at this time.
	// ------------------------------------------------------------------------

	public void preInit()
		throws AbortException, IOException
	{
		log.config( $("app.preinit") );

		// --------------------------------------------------------------------
		// - Create mandatory directories -------------------------------------
		// --------------------------------------------------------------------

		if( ! getUserPluginsDir().exists() )
		{
			log.fine( $( "app.preinit.plugins.dir" ) );

			Files.createDirectories( getUserPluginsDir().toPath()  );
		}

		// --------------------------------------------------------------------
		// - Plugins discover & load ------------------------------------------
		// --------------------------------------------------------------------

		log.fine( $("app.preinit.plugins.load") );

		final File [] pluginDirectories =
			{
				getInstallationDir(),
				getInstallationPluginsDir(),
				getUserPluginsDir()
			};

		pluginsManager = new PluginsManager<>( Plugin.class, pluginDirectories );

		// --------------------------------------------------------------------
		// - preInit plugins --------------------------------------------------
		// --------------------------------------------------------------------

		log.fine( $("app.preinit.plugins.preinit") );

		getPluginsManager().forEach( plugin ->
		{
			String message = MessageFormat.format(
				$("app.preinit.plugins.do.preinit"),
				plugin.getIdentifier(),
				plugin.getVersion()
			);
			log.log( Level.FINER, message );

			plugin.setApplication( this );
			plugin.preInit();
		} );
	}

	// ------------------------------------------------------------------------
	// init() is called AFTER the database was opened.
	// Preferences are now fully accessible.
	// ------------------------------------------------------------------------

	public void init()
		throws AbortException
	{
		log.config( $("app.init") );

		// --------------------------------------------------------------------
		// - Preferences ------------------------------------------------------
		// --------------------------------------------------------------------

		try
		{
			Preferences node = systemPreferencesNode( this );

			long runCount = node.getLong( "runCount", 0 );
			runCount++;

			node.putLong( "runCount", runCount );
			node.flush();
		}
		catch( BackingStoreException ex )
		{
			throw new AbortException( "Could not store runs count", ex );
		}

		// --------------------------------------------------------------------
		// - Script engine configuration --------------------------------------
		// --------------------------------------------------------------------

		log.fine( $("app.init.script.engine") );

		getScriptClassFilter().addPackageRegex(
			"java.lang.*",
			true
		);

		getScriptEngine().put( "Criminalysis", scriptEngineInterface );

		// --------------------------------------------------------------------
		// - Init plugins -----------------------------------------------------
		// --------------------------------------------------------------------

		log.fine( $("app.init.plugins.init") );

		getPluginsManager().forEach( plugin ->
		{
			String message = MessageFormat.format( $("msg.init.plugin"), plugin.getIdentifier() );
			log.log( Level.INFO, message );

			plugin.init();
		} );

	}

	// ========================================================================
	// =
	// ========================================================================

	@Override
	public void run()
		throws AbortException
	{
		log.config( $("app.run") );

		// --------------------------------------------------------------------

		pluginsManager.forEach( Plugin::run );

		// --------------------------------------------------------------------

		getMainFrame().setStatusMessage( "Ready." );
	}

	/**
	 * Called when main frame enters or leaves idle state.
	 *
	 * @param isIdle true if entering idle state, false if leaving idle state
	 */
	public void idle( boolean isIdle )
	{
		pluginsManager.forEach( plugin -> plugin.idle( isIdle ) );
	}

	// ========================================================================
	// = AutoCloseable interface ==============================================
	// ========================================================================

	@Override
	public void close()
		throws Exception
	{
		log.info( $("app.close") );

		// --------------------------------------------------------------------
		// - Cleanup plugins --------------------------------------------------
		// --------------------------------------------------------------------

		pluginsManager.forEach( Plugin::close );
	}

	// ========================================================================
	// = Termination ==========================================================
	// ========================================================================

	public void waitForTermination()
	{
		while( runningLatch.getCount() != 0 )
		{
			try
			{
				runningLatch.await();
			}
			catch( InterruptedException ex )
			{
				log.log( Level.WARNING, "Running latch interrupted...", ex );
			}
		}
	}

	// ------------------------------------------------------------------------

	public void requestWindowClose()
	{
		if( mainFrame.isWaiting() )
		{
			return;
		}

		int rc = JOptionPane.showConfirmDialog(
			mainFrame,
			$( "msg.quit.confirm" ),
			$("quit.confirm.windows.title"),
			JOptionPane.OK_CANCEL_OPTION
		);

		if( rc == JOptionPane.OK_OPTION )
		{
			runningLatch.countDown();
		}
	}

	// ========================================================================
	// =
	// ========================================================================

	public static void reportUnexpectedException( Throwable t, File logFile )
	{
	}

	// ========================================================================
	// = Getters ==============================================================
	// ========================================================================

	// ------------------------------------------------------------------------
	// -
	// ------------------------------------------------------------------------

	public PluginsManager<Plugin> getPluginsManager()
	{
		return pluginsManager;
	}

	public CriminalysisMainFrame getMainFrame()
	{
		return mainFrame;
	}

	public CriminalysisDatabase getDatabase()
	{
		return db;
	}

	// ------------------------------------------------------------------------
	// - Preferences ----------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Get a user preferences node for the given object's class.
	 *
	 * @param o the object
	 * @return the preferences node
	 */
	public static Preferences userPreferencesNode( Object o )
	{
		return userPreferencesNodeForClass( o.getClass() );
	}

	/**
	 * Get a user preferences node for the given class.
	 *
	 * @param c the class
	 * @return the preferences node
	 */
	public static Preferences userPreferencesNodeForClass( Class<?> c )
	{
		return Preferences.userNodeForPackage( c )
			.node( c.getSimpleName() );
	}

	/**
	 * Get a system preferences node for the given object's class.
	 *
	 * @param o the object
	 * @return the preferences node
	 */
	public static Preferences systemPreferencesNode( Object o )
	{
		return systemPreferencesNodeForClass( o.getClass() );
	}

	/**
	 * Get a system preferences node for the given class.
	 *
	 * @param c the class
	 * @return the preferences node
	 */
	public static Preferences systemPreferencesNodeForClass( Class<?> c )
	{
		return Preferences.systemNodeForPackage( c )
			.node( c.getSimpleName() );
	}

	// ========================================================================
	// = Runtime configuration ================================================
	// ========================================================================
	// region Runtime Configuration

	// ------------------------------------------------------------------------
	// - Instance API ---------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 *
	 * @return true is debug mode is enabled
	 */
	public boolean isDebugModeEnabled()
	{
		return debugMode;
	}

	// ------------------------------------------------------------------------
	// - Static API -----------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * @return true if application is running from jar
	 */
	public static boolean isRunningFromJar()
	{
		return CONFIGURATION.runsFromJar;
	}

	// ------------------------------------------------------------------------

	public static File getInstallationDir()
	{
		return CONFIGURATION.installationDir;
	}

	public static File getInstallationPluginsDir()
	{
		return CONFIGURATION.installationPluginsDir;
	}

	// ------------------------------------------------------------------------

	public static File getUserDir()
	{
		return CONFIGURATION.userDir;
	}

	public static File getUserLogDir()
	{
		return CONFIGURATION.userLogDir;
	}

	public static File getUserPluginsDir()
	{
		return CONFIGURATION.userPluginsDir;
	}

	// ------------------------------------------------------------------------
	// - Support class --------------------------------------------------------
	// ------------------------------------------------------------------------

	private static class RuntimeConfiguration
	{
		private final boolean runsFromJar;

		// --------------------------------------------------------------------

		private static final String PLUGINS_SUB_PATH = "plugins";

		private final File installationDir;         // <applicationDir>/
		private final File installationPluginsDir;  // <applicationDir>/plugins

		private final File userDir;                 // <userDir>/
		private final File userLogDir;              // <userDir>/log
		private final File userPluginsDir;          // <userDir>/plugins

		// ====================================================================
		// = Constructors =====================================================
		// ====================================================================

		private RuntimeConfiguration()
		{
			// ----------------------------------------------------------------
			// - Top level directories (application & user) -------------------
			// ----------------------------------------------------------------

			String thisClassName = Criminalysis.class.getSimpleName() + ".class";
			URL thisCLassURL = Main.class.getResource( thisClassName );
			if( thisCLassURL == null )
			{
				String message = MessageFormat.format( $("err.application.location"),
					$("err.application.location.no.url"), thisClassName );
				throw new RuntimeException( message );
			}

			String protocol = thisCLassURL.getProtocol();
			runsFromJar = switch( protocol )
			{
				// When running from jar (i.e. production run)
				// "jar:file:<path to jar>!<path to resource in jar>"
				//           ^___________^

				case "jar" ->
				{
					String classFilePath = thisCLassURL.getFile();
					int limit = classFilePath.indexOf( '!' );
					classFilePath = classFilePath.substring( 5, limit ); // skip "file:" and remove extra

					installationDir = new File( classFilePath ).getParentFile();
					yield true;
				}

				// When running in IntelliJ IDEA (i.e. development run and debug)
				// file:<path to project>/out/production/<path to resource>
				//      ^_______________^

				case "file" ->
				{
					String classFilePath = thisCLassURL.getFile();
					int limit = classFilePath.indexOf( "/out/production" );
					if( limit != -1 )
					{
						classFilePath = classFilePath.substring( 0, limit ); // keep "<path to project> only
					}

					installationDir = new File( classFilePath );
					yield false;
				}

				// No protocol

				case null ->
				{
					String message = MessageFormat.format( $("err.application.location"),
						$("err.application.location.no.protocol"), "" );
					throw new RuntimeException( message );
				}

				// Unexpected protocol

				default ->
				{
					String message = MessageFormat.format( $("err.application.location"),
						$( "err.application.location.unexpected.protocol" ), protocol );
					throw new RuntimeException( message );
				}
			};

			File userDir = new File( System.getProperty( "user.home" ) );

			// ----------------------------------------------------------------
			// - Other directories --------------------------------------------
			// ----------------------------------------------------------------

			installationPluginsDir = new File( installationDir, PLUGINS_SUB_PATH );

			this.userDir = new File( userDir, APPLICATION_NAME );
			userLogDir = new File( this.userDir, "log" );
			userPluginsDir = new File( this.userDir, PLUGINS_SUB_PATH );
		}
	}

	// endregion

	// ========================================================================
	// = Script engine ========================================================
	// ========================================================================
	// region Script engine

	public final ScriptEngine getScriptEngine()
	{
		return scriptEngine;
	}

	public final ScriptClassFilter getScriptClassFilter()
	{
		return scriptClassFilter;
	}

	public final ScriptEngineInterface getScriptInterface()
	{
		return scriptEngineInterface;
	}

	// ------------------------------------------------------------------------
	// - Test implementation scripts interface --------------------------------
	// ------------------------------------------------------------------------

	private void test()
	{
		log.warning( "DM scripting interface test" );
	}

	// ------------------------------------------------------------------------
	// - Interface for scripts access to DungeonMaster instance ---------------
	// ------------------------------------------------------------------------

	public static class ScriptEngineInterface
	{
		@SuppressWarnings( "unused" )
		private final Criminalysis instance;

		// ====================================================================
		// = Constructor ======================================================
		// ====================================================================

		protected ScriptEngineInterface( Criminalysis instance )
		{
			this.instance = instance;
		}

		// ====================================================================
		// = Proxy methods for DungeonMaster instance =========================
		// ====================================================================

		public void test()
		{
			instance.test();
		}
	}

	// endregion

	// ========================================================================
	// = Logger ===============================================================
	// ========================================================================

	private static final Logger log
		= Logger.getLogger( MethodHandles.lookup().lookupClass().getName() );

	// ========================================================================
	// = i18n =================================================================
	// ========================================================================

	private static final ResourceBundle rs
		= ResourceBundle.getBundle( MethodHandles.lookup().lookupClass().getName() );

	private static String $( @PropertyKey(resourceBundle = "be.lmenten.criminalysis.Criminalysis") String key )
	{
		return rs.getString( key );
	}
}
