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
import org.jetbrains.annotations.PropertyKey;
import org.reflections.Reflections;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @param <T> The plugin service provider base class.
 */
public class PluginsManager<T extends Plugin>
{
	// ========================================================================
	// = Custom class loader ==================================================
	// ========================================================================

	// This custom URLClassLoader exposes its addURL method as public

	private static class PluginsClassLoader
		extends URLClassLoader
	{
		public PluginsClassLoader()
		{
			super( "PluginsClassLoader", new URL [0], ClassLoader.getSystemClassLoader() );
		}

		@Override
		public void addURL( URL url )
		{
			super.addURL( url );
		}
	}

	// ========================================================================
	// = Data =================================================================
	// ========================================================================

	private final PluginsClassLoader pluginClassLoader
		= new PluginsClassLoader();

	private final ServiceLoader<T> pluginsLoader;

	// ------------------------------------------------------------------------

	private final Map<String,T> plugins
		= new LinkedHashMap<>();

	// ========================================================================
	// = Constructors =========================================================
	// ========================================================================

	public PluginsManager( Class<T> clazz, File ... pluginDirectories )
	{
		pluginsLoader = ServiceLoader.load( clazz, pluginClassLoader );

		reloadPlugins( pluginDirectories );
	}

	// ========================================================================
	// =
	// ========================================================================

	/**
	 *
	 * @param pluginDirectories list of directories to search for plugins
	 */
	public void reloadPlugins( File ... pluginDirectories )
	{
		for( File pluginDirectory : pluginDirectories )
		{
			if( ! pluginDirectory.isDirectory() )
			{
				log.log( Level.WARNING, "{0} is not a directory.", pluginDirectory.getAbsolutePath() );
				continue;
			}

			configureClassloader( pluginDirectory );
		}

		// --------------------------------------------------------------------

		// When not running from jar, services are not discovered...
		// We mark them with @Plugin.Internal and do it ourselves.

		if( ! Criminalysis.isRunningFromJar() )
		{
			log.info( "Not running from jar, self-discovering internal plugins." );

			Reflections reflections = new Reflections( "be.lmenten.criminalysis" );
			Set<Class<? extends Plugin>> classes = reflections.getSubTypesOf( Plugin.class );

			int count = 0;
			for( Class<? extends Plugin> clazz : classes )
			{
				if( clazz.isAnnotationPresent( Plugin.Internal.class ) )
				{
					try
					{
						T plugin = (T) clazz.getDeclaredConstructor().newInstance();
						registerPlugin( plugin );
						count++;
					}
					catch( NoSuchMethodException
							| InstantiationException | IllegalAccessException
							| InvocationTargetException ex )
					{
						log.log( Level.SEVERE, "Could not instantiate plugin " + clazz.getName(), ex );
					}
				}
			}

			log.log( Level.FINE, "Discovered {0} internal plugins.", count );
		}

		// --------------------------------------------------------------------

		pluginsLoader.reload();

		int count = 0;
		for( T plugin : pluginsLoader )
		{
			registerPlugin( plugin );
			count++;
		}

		log.log( Level.FINE, "Discovered {0} plugins.", count );
	}

	/**
	 * This method registers a plugin. If a plugin with the same identifier is
	 * already registered and the versions do not match, the newly loaded plugin
	 * is notifier and the older plugin is replaced in the list.
	 *
	 * @param plugin the newly loaded plugin
	 */
	private void registerPlugin( T plugin )
	{
		String identifier = plugin.getIdentifier();

		if( plugins.containsKey( identifier ) )
		{
			plugin.reloadPlugin( plugins.get( identifier ) );
		}

		plugins.put( identifier, plugin );
	}

	/**
	 *
	 * @param pluginDirectory directory to search for plugins
	 */
	private void configureClassloader( File pluginDirectory )
	{
		log.log( Level.CONFIG, $("msg.processing.directory"), pluginDirectory.getAbsolutePath() );

		File[] plugins = pluginDirectory.listFiles( (direName, fileName) ->
			fileName.toLowerCase().endsWith( ".jar" )
		);

		int pluginsCount = 0;
		if( plugins != null )
		{
			for( File plugin : plugins )
			{
				try
				{
					log.log( Level.FINER, $( "msg.jar.url" ), plugin.getAbsolutePath() );

					// if URL is already in the classloader list, addURL do nothing.

					pluginClassLoader.addURL( plugin.toURI().toURL() );
					pluginsCount++;
				}
				catch( Exception ex )
				{
					String message = MessageFormat.format( $( "err.processing.url" ), plugin );
					log.log( Level.WARNING, message, ex );
				}
			}
		}

		log.log( Level.FINE, $("msg.found.jars"), pluginsCount );
	}

	// ========================================================================
	// =
	// ========================================================================

	public void forEach( Consumer<T> consumer )
	{
		for( T plugin : plugins.values() )
		{
			consumer.accept( plugin );
		}
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
		= ResourceBundle.getBundle( MethodHandles.lookup().lookupClass().getName() );

	private static String $( @PropertyKey(resourceBundle = "be.lmenten.criminalysis.plugins.PluginsManager") String key )
	{
		return rs.getString( key );
	}
}
