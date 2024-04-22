package be.lmenten.criminalysis.api;

import be.lmenten.criminalysis.Criminalysis;
import org.jdesktop.swingx.action.AbstractActionExt;

public abstract class CriminalysisAction
	extends AbstractActionExt
{
	private final String category;
	private final String id;

	private Criminalysis app;

	// ========================================================================
	// =
	// ========================================================================

	protected CriminalysisAction( String category, String id )
	{
		this.category = category;
		this.id = id;
	}

	public void setApplication( Criminalysis app )
	{
		this.app = app;
	}

	// ========================================================================
	// =
	// ========================================================================

	public String getCategory()
	{
		return category;
	}

	public String getId()
	{
		return id;
	}

	// ------------------------------------------------------------------------

	public Criminalysis getApplication()
	{
		return app;
	}
}
