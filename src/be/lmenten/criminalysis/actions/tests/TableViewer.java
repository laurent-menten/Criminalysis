package be.lmenten.criminalysis.actions.tests;

import be.lmenten.criminalysis.api.CriminalysisAction;
import be.lmenten.criminalysis.ui.frames.map.SimpleMap;
import be.lmenten.util.logging.swing.JLogHandlerFrame;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.win10.Win10;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class TableViewer
	extends CriminalysisAction
{
	public static final String CATEGORY = "debug";
	public static final String ID = "table.viewer";

	public TableViewer()
	{
		super( CATEGORY, ID );

		setName( "Tables viewer" );
		setSmallIcon( FontIcon.of( Win10.DATABASE, 16 ) );
		setLargeIcon( FontIcon.of( Win10.DATABASE, 24 ) );
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		try
		{
			be.lmenten.util.swing.h2.TableViewer tv = new be.lmenten.util.swing.h2.TableViewer( getApplication().getDatabase().getConnection() );
			tv.setSize(1024,768 );
			tv.setLocationRelativeTo( getApplication().getMainFrame() );
			tv.setDefaultCloseOperation( JLogHandlerFrame.DISPOSE_ON_CLOSE );
			tv.setVisible(true);
		}
		catch( SQLException ex )
		{
			throw new RuntimeException( ex );
		}
	}
}
