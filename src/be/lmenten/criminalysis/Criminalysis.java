package be.lmenten.criminalysis;

import java.lang.Runtime.Version;
import java.util.logging.Logger;

import be.lmenten.criminalysis.db.CriminalysisDB;
import be.lmenten.utils.app.Application;
import be.lmenten.utils.h2.H2Database;
import be.lmenten.utils.swing.SwingUtils;

public class Criminalysis
	extends Application<Criminalysis>
{
	public static final Version VERSION
		= Version.parse( "1.0.1-ea+1-20200605" );

	// =========================================================================
	// === CONSTRUCTOR(s) ======================================================
	// =========================================================================

	public Criminalysis()
	{
	}

	// =========================================================================
	// ===
	// =========================================================================

	@Override
	public void initialize()
	{
		SwingUtils.setNimbusLookAndFeel();

		if( logWindow != null )
		{
			logWindow.setVisible( true );
		}
	}

	@Override
	protected void cleanup()
	{
	}

	// =========================================================================
	// ===
	// =========================================================================

	@Override
	protected void run()
	{
		try( H2Database db = new CriminalysisDB( "./test" ) )
		{
			db.open();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------

		try
		{
			for( int i = 0 ; i < 1 ; i++ )
			{
				log.info( "loop " + i );

				Thread.sleep( 500 );
			}
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}

		finish();
	}

	// =========================================================================
	// === LOGGING =============================================================
	// =========================================================================

	private static final Logger log
		= Logger.getLogger( Criminalysis.class.getName() );
}
