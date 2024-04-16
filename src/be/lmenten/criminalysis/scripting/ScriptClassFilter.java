/*
 * ============================================================================
 * =- jDungeonMaster -=- A D&D toolbox for DMs  -=- (c) 2024+ Laurent Menten -=
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
 * jDungeonMaster is unofficial Fan Content permitted under the Fan Content
 * Policy. Not approved/endorsed by Wizards. Portions of the materials used
 * are property of Wizards of the Coast. ©Wizards of the Coast LLC.
 * See <https://company.wizards.com/en/legal/fancontentpolicy> for details.
 * ============================================================================
 */

/*
 * ============================================================================
 * =- jDungeonMaster -=- A D&D toolbox for DMs  -=- (c) 2024+ Laurent Menten -=
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
 * jDungeonMaster is unofficial Fan Content permitted under the Fan Content
 * Policy. Not approved/endorsed by Wizards. Portions of the materials used
 * are property of Wizards of the Coast. ©Wizards of the Coast LLC.
 * See <https://company.wizards.com/en/legal/fancontentpolicy> for details.
 * ============================================================================
 */

package be.lmenten.criminalysis.scripting;

import org.jetbrains.annotations.PropertyKey;
import org.openjdk.nashorn.api.scripting.ClassFilter;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ScriptClassFilter
	implements ClassFilter
{
	record PatternInfos( Pattern pattern, Boolean allowed )
	{
	};

	// ====================================================================
	// = Data =============================================================
	// ====================================================================

	private final Map<String,Boolean> allowedClasses
		= new HashMap<>();

	private final Map<String,Boolean> allowedPackages
		= new HashMap<>();

	private final Map<String,PatternInfos> allowedPackagesRegex
		= new HashMap<>();

	// ====================================================================
	// = Constructor ======================================================
	// ====================================================================

	public ScriptClassFilter()
	{
	}

	// ====================================================================
	// = Configuration ====================================================
	// ====================================================================

	// --------------------------------------------------------------------
	// - Classes ----------------------------------------------------------
	// --------------------------------------------------------------------

	public void addClass( Class<?> _class, boolean allowed )
	{
		addClass( _class.getName(), allowed );
	}

	public void addClass( String className, boolean allowed )
	{
		allowedClasses.put( className, allowed );
	}

	// --------------------------------------------------------------------

	public void removeClass( Class<?> _class )
	{
		removeClass( _class.getName() );
	}

	public void removeClass( String className )
	{
		allowedClasses.remove( className );
	}

	// --------------------------------------------------------------------
	// - Packages ---------------------------------------------------------
	// --------------------------------------------------------------------

	public void addPackage( Class<?> _class, boolean allowed )
	{
		addPackage( _class.getPackageName(), allowed );
	}

	public void addPackage( Package _package, boolean allowed )
	{
		addPackage( _package.getName(), allowed );
	}

	public void addPackage( String packageName, boolean allowed )
	{
		allowedPackages.put( packageName, allowed );
	}

	public void addPackageRegex( String packageNameRegex, boolean allowed )
		throws PatternSyntaxException
	{
		try
		{
			Pattern pattern = Pattern.compile( packageNameRegex.replace( ".", "\\." ) );
			PatternInfos patternInfos = new PatternInfos( pattern, allowed );

			allowedPackagesRegex.put( packageNameRegex, patternInfos );
		}
		catch( PatternSyntaxException ex )
		{
			String message = MessageFormat.format( $("err.pattern.compile"), packageNameRegex );
			log.log( Level.SEVERE, message, ex );

			throw  ex;
		}
	}

	// --------------------------------------------------------------------

	public void removePackage( Class<?> _class )
	{
		removePackage( _class.getPackageName() );
	}

	public void removePackage( Package _package )
	{
		removePackage( _package.getName() );
	}

	public void removePackage( String packageName )
	{
		allowedPackages.remove( packageName );
	}

	public void removePackageRegex( String packageNameRegex )
	{
		allowedPackagesRegex.remove( packageNameRegex );
	}

	// ====================================================================
	// = ClassFilter interface ============================================
	// ====================================================================

	@Override
	public boolean exposeToScripts( String className )
	{
		int lastDot = className.lastIndexOf( '.' );
		String packageName = "";

		if( lastDot != -1 )
		{
			packageName = className.substring( 0, lastDot );
		}

		boolean packageMatched = false;

		for( PatternInfos patternInfos : allowedPackagesRegex.values() )
		{
			Matcher matcher = patternInfos.pattern().matcher( className );
			if( matcher.find() && patternInfos.allowed() )
			{
				packageMatched = true;
			}
		}

		Boolean packageAllowed = allowedPackages.get( packageName );
		Boolean classAllowed = allowedClasses.get( className );

		if( (packageMatched || (packageAllowed != null && packageAllowed.equals( Boolean.TRUE )))
				&& (classAllowed == null || classAllowed.equals( Boolean.TRUE )) )
		{
			return true;
		}

		if( classAllowed != null && classAllowed.equals( Boolean.TRUE ) )
		{
			return true;
		}

		log.log( Level.WARNING, $("msg.class.not.allowed"), className  );

		return false;
	}

	// ========================================================================
	// = Utilities ============================================================
	// ========================================================================

	private static final Logger log
		= Logger.getLogger( ScriptClassFilter.class.getName() );

	// ------------------------------------------------------------------------

	private static final ResourceBundle rs
		= ResourceBundle.getBundle( ScriptClassFilter.class.getName() );

	private static String $( @PropertyKey( resourceBundle = "be.lmenten.jdm.scripting.ScriptClassFilter") String key )
	{
		return rs.getString( key );
	}
}
