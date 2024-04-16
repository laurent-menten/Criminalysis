package be.lmenten.criminalysis.db;

import be.lmenten.util.jdbc.h2.H2FileDatabase;

import java.util.Properties;

public class CriminalysisDatabase
	extends H2FileDatabase
{
	public static final long DATABASE_SCHEMA_VERSION = 1L;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	public CriminalysisDatabase( String databasePath, Properties info )
	{
		super( databasePath, info );
	}

	@Override
	public long getDatabaseVersion()
	{
		return DATABASE_SCHEMA_VERSION;
	}

	// ========================================================================
	// = AutoCloseable interface ==============================================
	// ========================================================================
}
