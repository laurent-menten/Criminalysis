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

package be.lmenten.criminalysis.plugins;

import be.lmenten.criminalysis.Criminalysis;
import be.lmenten.criminalysis.db.CriminalysisDatabase;
import be.lmenten.criminalysis.ui.CriminalysisMainFrame;

import javax.swing.Icon;
import java.lang.annotation.*;

/**
 * Plugin base class.
 *
 * @version 1.0.1
 * @since 1.0.1
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 */
public abstract class Plugin
	implements Runnable
{
	// ========================================================================
	// = Marker interface for internal plugins ================================
	// ========================================================================

	@Inherited
	@Target( ElementType.TYPE )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Internal
	{
	}

	// ========================================================================
	// =
	// ========================================================================

	public static final String KEY_NAME = "plugin.name";
	public static final String KEY_DESCRIPTION = "plugin.description";
	public static final String KEY_SHORT_DESCRIPTION = "plugin.short.description";
	public static final String KEY_ICON = "plugin.icon";

	// ========================================================================
	// = Data =================================================================
	// ========================================================================

	private final String identifier;
	private final Runtime.Version version;

	private Criminalysis app;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	/**
	 * Constructor.
	 *
	 * @param identifier the identifier of this plugin service provider.
	 * @param version the version of this plugin service provider.
	 *
	 */
	protected Plugin( String identifier, Runtime.Version version )
	{
		this.identifier = identifier;
		this.version = version;
	}

	// ========================================================================
	// = Application access ===================================================
	// ========================================================================

	/**
	 * Set the application instance for this plugin.
	 *
	 * @param app the application instance
	 */
	public final void setApplication( Criminalysis app )
	{
		if( this.app != null )
		{
			throw new IllegalStateException( "Application instance already set." );
		}

		this.app = app;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the application instance.
	 *
	 * @return the application instance
	 */
	public final Criminalysis getApplication()
	{
		return app;
	}

	/**
	 * Shortcut to get the UI instance.
	 *
	 * @return the application UI
	 */
	public final CriminalysisMainFrame getMainFrame()
	{
		return app.getMainFrame();
	}

	/**
	 * Shortcut to get the database instance.
	 *
	 * @return the application database
	 */
	public final CriminalysisDatabase getDatabase()
	{
		return app.getDatabase();
	}

	// ========================================================================
	// = Plugin callbacks =====================================================
	// ========================================================================

	/**
	 * Pre-initialize the plugin.
	 * <p>
	 * This method is called when the plugin is loaded. At that point the
	 * application was pre-initialized but the database is not yet opened.
	 * </p>
	 * <p>
	 * This is typically where the plugin will register its components like
	 * database helpers, actions etc.
	 * </p>
	 */
	public void preInit()
	{
		// default: do nothing.
	}

	/**
	 * Initialize plugin.
	 * <p>
	 * This method is called when the database is open and the application
	 * was initialized.
	 * </p>
	 * <p>
	 * This is typically where the plugin will configure itself according to
	 * the preferences and data that are stored in the database.
	 * </p>
	 */
	public abstract void init();

	/**
	 * Initialize plugin UI.
	 * <p>
	 * This method is called when the application UI is initialized.
	 * </p>
	 * <p>
	 * This runs on the swing thread !!!
	 * </p>
	 */
	public void initUI()
	{
		// default: do nothing.
	}

	// ------------------------------------------------------------------------

	/**
	 * Run plugin
	 */
	@Override
	public void run()
	{
		// default: do nothing.
	}

	/**
	 * Called when main frame enters or leaves idle state.
	 *
	 * @param isIdle true if entering idle state, false if leaving idle state
	 */
	public void idle( boolean isIdle )
	{
	}

	// ------------------------------------------------------------------------

	/**
	 * Called for every plugin when application is closing.
	 */
	public void close()
	{
		// default: do nothing.
	}

	// ------------------------------------------------------------------------

	/**
	 * Called when plugins are reloaded. This method can check for version
	 * update, copy of private state and values,  etc.
	 *
	 * @param previousInstance The new instance of the plugin service provider.
	 */
	public void reloadPlugin( Plugin previousInstance )
	{
		// default: do nothing.
	}

	// ========================================================================
	// = Plugin infos =========================================================
	// ========================================================================

	/**
	 * Get the unique identifier for this plugin service provider.
	 *
	 * @return the identifier.
	 */
	public final String getIdentifier()
	{
		return identifier;
	}

	/**
	 * Get the version of this plugin service provider.
	 *
	 * @return the version.
	 */
	public final Runtime.Version getVersion()
	{
		return version;
	}

	// ========================================================================
	// = DisplayableItem interface ============================================
	// ========================================================================

	/**
	 * Get the (localized) name of this plugin service provider.
	 *
	 * @return the name.
	 */
	public abstract String getName();

	/**
	 * Get the (localized) description of this plugin service provider.
	 *
	 * @return the description.
	 */
	public abstract String getDescription();

	/**
	 * Get the (localized) short description of this plugin service provider.
	 * This defaults to the value of the description.
	 *
	 * @return the short description.
	 */
	public  String getShortDescription()
	{
		return getDescription();
	}

	/**
	 * Get the icon of this plugin service provider.
	 *
	 * @return the icon.
	 */
	public Icon getIcon()
	{
		return null; //FontIcon.of( Win10.ELECTRICAL, 16 );
	}
}
