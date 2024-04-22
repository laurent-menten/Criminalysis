package be.lmenten.criminalysis.db;

import be.lmenten.criminalysis.Criminalysis;

import java.util.EventObject;

public class CriminalysisObjectEvent
	extends EventObject
{
	public enum EventType
	{
	};

	// ========================================================================
	// = Data =================================================================
	// ========================================================================

	private final EventType type;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	protected CriminalysisObjectEvent( CriminalysisObject source, EventType type )
	{
		super( source );

		this.type = type;
	}

	// ========================================================================
	// = Getters ==============================================================
	// ========================================================================

	public EventType getEventType()
	{
		return type;
	}
}
