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

package be.lmenten.criminalysis.db;

import be.lmenten.util.jdbc.h2.H2FileDatabase;

import java.util.Properties;

public class CriminalysisDatabase
	extends H2FileDatabase
{
	public static final long DATABASE_SCHEMA_VERSION = 1L;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	public CriminalysisDatabase( String databasePath, Properties info )
	{
		super( databasePath, info );

		registerTableHelper( Person.helper );
	}

	@Override
	public long getDatabaseVersion()
	{
		return DATABASE_SCHEMA_VERSION;
	}

	// ========================================================================
	// = AutoCloseable interface ==============================================
	// ========================================================================
}
