package be.lmenten.criminalysis.ui;

import be.lmenten.criminalysis.Criminalysis;
import be.lmenten.util.exception.AbortException;
import org.jdesktop.swingx.JXFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CriminalysisMainFrame
	extends JXFrame
	implements AutoCloseable
{
	private final Criminalysis app;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	public CriminalysisMainFrame( Criminalysis app )
	{
		this.app = app;
		this.app.setMainFrame( this );

		// --------------------------------------------------------------------

		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		addWindowListener( new WindowAdapter()
		{
			public void windowClosing( WindowEvent e )
			{
				app.requestWindowClose();
			}
		} );

		// --------------------------------------------------------------------

		setTitle( "Criminalysis" );
		setSize( 800, 600 );
	}

	// ========================================================================
	// =
	// ========================================================================

	public void preInit()
		throws AbortException
	{
	}

	public void init()
		throws AbortException
	{
	}

	// ========================================================================
	// = AutoCloseable interface ==============================================
	// ========================================================================

	@Override
	public void close()
		throws Exception
	{
		dispose();
	}
}
