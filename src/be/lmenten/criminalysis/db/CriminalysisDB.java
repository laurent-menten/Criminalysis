package be.lmenten.criminalysis.db;

import be.lmenten.utils.h2.H2Database;

public class CriminalysisDB
	extends H2Database
{
	private static final int DATABASE_VERSION = 1;

	// =========================================================================
	// === CONSTRUCTOR(s) ======================================================
	// =========================================================================

	public CriminalysisDB( String dbName )
	{
		super( dbName );
	}

	// =========================================================================
	// === class : H2Database ==================================================
	// =========================================================================

	@Override
	public int getDatabaseVersion()
	{
		return DATABASE_VERSION;
	}

	@Override
	protected void create()
	{
	}

	@Override
	protected void upgrade( int oldVersion )
	{
	}
}
