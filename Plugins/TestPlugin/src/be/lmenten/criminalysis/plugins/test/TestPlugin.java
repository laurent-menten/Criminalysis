package be.lmenten.criminalysis.plugins.test;

import be.lmenten.criminalysis.plugins.Plugin;
import com.google.auto.service.AutoService;

@AutoService( Plugin.class )
public class TestPlugin
	extends Plugin
{
	private static final String IDENTIFIER = "TestPlugin";
	private static final Runtime.Version VERSION = Runtime.Version.parse( "1.0.1" );

	public TestPlugin()
	{
		super( IDENTIFIER, VERSION );
	}

	@Override
	public void init()
	{
	}

	@Override
	public String getName()
	{
		return "Test plugin";
	}

	@Override
	public String getDescription()
	{
		return "A beautiful test plugin";
	}
}
