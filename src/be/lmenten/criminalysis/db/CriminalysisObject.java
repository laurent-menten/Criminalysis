package be.lmenten.criminalysis.db;

import be.lmenten.util.jdbc.h2.H2Table;

import java.util.ArrayList;
import java.util.List;

public class CriminalysisObject<T extends H2Table<T,U>,U>
	extends H2Table<T,U>
{
	// ========================================================================
	// = Data =================================================================
	// ========================================================================

	private final List<CriminalysisObjectListener> listeners
		= new ArrayList<>();

	// ========================================================================
	// = CampaignObjectListener ===============================================
	// ========================================================================

	public synchronized void addCampaignObjectListener( CriminalysisObjectListener listener )
	{
		if( ! listeners.contains( listener ) )
		{
			listeners.add( listener );
		}
	}

	public synchronized void removeCampaignObjectListener( CriminalysisObjectListener listener )
	{
		listeners.remove( listener );
	}

	// ------------------------------------------------------------------------

	protected void fireCampaignObjectEvent( CriminalysisObjectEvent ev )
	{
		CriminalysisObjectListener [] threadSafeListenerList;

		synchronized( this )
		{
			threadSafeListenerList = listeners.toArray( new CriminalysisObjectListener [0] );
		}

		for( CriminalysisObjectListener listener : threadSafeListenerList )
		{
			listener.onCriminalysisObjectEvent( ev );
		}
	}
}
