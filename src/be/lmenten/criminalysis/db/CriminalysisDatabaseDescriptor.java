package be.lmenten.criminalysis.db;

import be.lmenten.util.jdbc.h2.H2FileDatabaseDescriptor;

import java.util.Properties;

public class CriminalysisDatabaseDescriptor
	extends H2FileDatabaseDescriptor<CriminalysisDatabase>
{
	public CriminalysisDatabaseDescriptor()
	{
		super( ".crim" );
	}

	@Override
	public CriminalysisDatabase getDatabase()
	{
		return new CriminalysisDatabase( "./test", new Properties()  );
	}
}
