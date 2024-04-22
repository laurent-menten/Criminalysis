package be.lmenten.criminalysis.db;

import be.lmenten.criminalysis.Criminalysis;

import java.util.EventListener;

public interface CriminalysisObjectListener
	extends EventListener
{
	void onCriminalysisObjectEvent( CriminalysisObjectEvent event );
}
