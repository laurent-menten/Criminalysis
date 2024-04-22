package be.lmenten.criminalysis.actions.tests;

import be.lmenten.criminalysis.api.CriminalysisAction;
import be.lmenten.criminalysis.ui.frames.video.Player;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.win10.Win10;

import java.awt.event.ActionEvent;

public class VideoPlayer
	extends CriminalysisAction
{
	public static final String CATEGORY = "debug";
	public static final String ID = "video.player";

	public VideoPlayer()
	{
		super( CATEGORY, ID );

		setName( "Video Player" );
		setSmallIcon( FontIcon.of( Win10.VIDEO_FILE, 16 ) );
		setLargeIcon( FontIcon.of( Win10.VIDEO_FILE, 24 ) );
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		Player player = new Player();
		player.setVisible( true );

		getApplication().getMainFrame().addInternalFrame( player );
	}
}
