package be.lmenten.criminalysis.api;

import be.lmenten.util.swing.scrollabledesktop.JScrollableDesktopInternalFrame;

public class CriminalysisInternalFrame
	extends JScrollableDesktopInternalFrame
{
	protected CriminalysisInternalFrame()
	{
		this( "<untitled>" );
	}

	protected CriminalysisInternalFrame( String title )
	{
		super( title );

		setClosable( true );
		setResizable( true );
		setIconifiable( true );
	}
}
