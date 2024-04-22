package be.lmenten.criminalysis.actions.tests;

import be.lmenten.criminalysis.api.CriminalysisAction;
import be.lmenten.criminalysis.ui.frames.map.SimpleMap;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.win10.Win10;

import java.awt.event.ActionEvent;

public class NewSimpleMap
	extends CriminalysisAction
{
	public static final String ID = "new.simple.map";

	public NewSimpleMap()
	{
		super( "application" , ID );

		setName( "New Simple Map" );
		setSmallIcon( FontIcon.of( Win10.SETTINGS, 16 ) );
		setLargeIcon( FontIcon.of( Win10.SETTINGS, 24 ) );
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		SimpleMap simpleMap = new SimpleMap();
		simpleMap.setVisible( true );

		getApplication().getMainFrame().addInternalFrame( simpleMap );
	}
}
