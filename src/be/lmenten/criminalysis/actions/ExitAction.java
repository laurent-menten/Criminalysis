package be.lmenten.criminalysis.actions;

import be.lmenten.criminalysis.api.CriminalysisAction;

import java.awt.event.ActionEvent;

public class ExitAction
	extends CriminalysisAction
{
	public static final String ID = "exit";

	public ExitAction()
	{
		super( "application" , ID );

		setName( "Exit" );
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		getApplication().requestWindowClose();
	}
}
