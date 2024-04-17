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

package be.lmenten.criminalysis.plugins.internal;

import be.lmenten.criminalysis.plugins.Plugin;
import com.google.auto.service.AutoService;

@AutoService( Plugin.class )
@Plugin.Internal
public class InternalTestPlugin
	extends Plugin
{
	private static final String IDENTIFIER = "InternalTestPlugin";
	private static final Runtime.Version VERSION = Runtime.Version.parse( "1.0.1" );

	public InternalTestPlugin()
	{
		super( IDENTIFIER, VERSION );
	}

	@Override
	public void init()
	{
	}

	@Override
	public String getName()
	{
		return "Internal test plugin";
	}

	@Override
	public String getDescription()
	{
		return "A beautiful internal test plugin";
	}
}
