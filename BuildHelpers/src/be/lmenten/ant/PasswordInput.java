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
 * jDungeonMaster is unofficial Fan Content permitted under the Fan Content
 * Policy. Not approved/endorsed by Wizards. Portions of the materials used
 * are property of Wizards of the Coast. ©Wizards of the Coast LLC.
 * See <https://company.wizards.com/en/legal/fancontentpolicy> for details.
 * ============================================================================
 */

package be.lmenten.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.input.DefaultInputHandler;
import org.apache.tools.ant.input.InputRequest;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PasswordInput
	extends DefaultInputHandler
{
	public void handleInput( InputRequest request) throws BuildException
	{
		String prompt = "ANT input: " + this.getPrompt( request );

		do
		{
			PasswordPanel passwordPanel = new PasswordPanel();

			JOptionPane optionPane
				= new JOptionPane( passwordPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION );

			JDialog dlg = optionPane.createDialog( prompt );
			dlg.addWindowFocusListener( new WindowAdapter()
			{
				@Override
				public void windowGainedFocus( WindowEvent e )
				{
					passwordPanel.gainedFocus();
				}
			} );

			dlg.setVisible( true );

			String password = "<none>";
			if( optionPane.getValue() != null && optionPane.getValue().equals( JOptionPane.OK_OPTION ) )
			{
				password = new String( passwordPanel.getPassword() );
			}

			request.setInput( password );
		}
		while( !request.isInputValid() );
	}
}


class PasswordPanel
	extends JPanel
{
	private final JPasswordField passwordField = new JPasswordField(12);
	private boolean gainedFocusBefore;

	/**
	 * "Hook" method that causes the JPasswordField to request focus the first time this method is called.
	 */
	void gainedFocus() {
		if (!gainedFocusBefore) {
			gainedFocusBefore = true;
			passwordField.requestFocusInWindow();
		}
	}

	public PasswordPanel() {
		super(new FlowLayout());

		add(new JLabel("Password: "));
		add(passwordField);
	}

	public char[] getPassword() {
		return passwordField.getPassword();
	}
}