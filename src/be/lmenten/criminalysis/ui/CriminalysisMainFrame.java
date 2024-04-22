/*
 * ============================================================================
 * =- Criminalysis -=- A crime analysis toolbox -=- (c) 2024+ Laurent Menten -=
 * ============================================================================
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 * ============================================================================
 */

package be.lmenten.criminalysis.ui;

import be.lmenten.criminalysis.Criminalysis;
import be.lmenten.criminalysis.actions.ExitAction;
import be.lmenten.criminalysis.actions.tests.NewSimpleMap;
import be.lmenten.criminalysis.actions.tests.TableViewer;
import be.lmenten.criminalysis.actions.tests.VideoPlayer;
import be.lmenten.criminalysis.api.CriminalysisInternalFrame;
import be.lmenten.criminalysis.ui.frames.map.SimpleMap;
import be.lmenten.util.exception.AbortException;
import be.lmenten.util.swing.scrollabledesktop.JScrollableDesktopInternalFrame;
import be.lmenten.util.swing.scrollabledesktop.JScrollableDesktopPane;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.jdesktop.swingx.*;
import org.jetbrains.annotations.PropertyKey;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.win10.Win10;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class CriminalysisMainFrame
	extends JXFrame
	implements CriminalysisMainFrameConstants, AutoCloseable
{
	private final Criminalysis app;

	// ------------------------------------------------------------------------
	// -
	// ------------------------------------------------------------------------

	private final JButton idleUnlockButton;
	private final IdleWaitPanel waitPanel;

	// ------------------------------------------------------------------------
	// -
	// ------------------------------------------------------------------------

	private final JMenuBar menuBar
		= new JMenuBar();

	private final JToolBar appToolBar
		= new JToolBar( "app", SwingConstants.HORIZONTAL );
	private final JToolBar leftToolBar
		= new JToolBar( "left", SwingConstants.VERTICAL );
	private final JToolBar rightToolBar
		= new JToolBar( "right", SwingConstants.VERTICAL );

	private final JXTaskPaneContainer navigator
		= new JXTaskPaneContainer();
	private final JScrollPane navigatorScrollPane
		= new JScrollPane( navigator );

	private final JScrollableDesktopPane workspace
		= new JScrollableDesktopPane();

	private final JSplitPane workspaceSplitPane
		= new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, navigatorScrollPane, workspace );

	private final JXStatusBar statusBar
		= new JXStatusBar();

	private final JLabel statusMessage
		= new JLabel();

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	public CriminalysisMainFrame( Criminalysis app )
	{
		this.app = app;
		this.app.setMainFrame( this );

		// --------------------------------------------------------------------

		setTitle( "Criminalysis" );
		setIconImage( app.getIcon().getImage() );

		// --------------------------------------------------------------------
		// - Windows close operation ------------------------------------------
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
		// - Idle state -------------------------------------------------------
		// --------------------------------------------------------------------

		idleUnlockButton = new JButton( $("msg.idle.locked"), FontIcon.of( Win10.LOCK_2, 100 ) );
		idleUnlockButton.setOpaque( false );
		idleUnlockButton.setFont( new Font( "Lucidia", Font.PLAIN, 100 ) );
		idleUnlockButton.setForeground( Color.RED );
		idleUnlockButton.setBackground( new Color( 0.f, 0.f, 0.f, .7f ) );

		idleUnlockButton.addActionListener( ev ->
		{
			log.info( $("msg.idle.leave") );

			idle( false );
		} );

		waitPanel = new IdleWaitPanel( this, idleUnlockButton );
		setWaitPane( waitPanel );

		addPropertyChangeListener( "idle", ev ->
		{
			if( isIdle() && ! isWaitPaneVisible() )
			{
				log.info( $("msg.idle.enter") );

				idle( true );
			}
		} );
	}

	// ========================================================================
	// = Initialization =======================================================
	// ========================================================================

	// ------------------------------------------------------------------------
	// preInit() is called BEFORE the database is opened.
	// Preferences ARE NOT accessible at this time.
	// ------------------------------------------------------------------------

	public void preInit()
		throws AbortException
	{
		log.config( $("msg.preinit") );

		// --------------------------------------------------------------------
		// - Flat l&f flavours ------------------------------------------------
		// --------------------------------------------------------------------

		log.fine( $("msg.install.flatlaf") );

		FlatDarkLaf.installLafInfo();
		FlatLightLaf.installLafInfo();
		FlatDarculaLaf.installLafInfo();
		FlatIntelliJLaf.installLafInfo();
		FlatMacDarkLaf.installLafInfo();
		FlatMacLightLaf.installLafInfo();
	}

	// ------------------------------------------------------------------------
	// init() is called AFTER the database was opened.
	// Preferences are now fully accessible.
	// ------------------------------------------------------------------------

	public void init()
		throws AbortException
	{
		log.config( $("msg.init") );

		try
		{
			SwingUtilities.invokeAndWait( this::createUI );
		}
		catch( Exception ex )
		{
			throw new AbortException( $("ex.create.ui"), ex );
		}
	}

	private void createUI()
	{
		Preferences node = Criminalysis.userPreferencesNode( this );

		// --------------------------------------------------------------------
		// -
		// --------------------------------------------------------------------

		setLayout( new BorderLayout() );

		// --------------------------------------------------------------------
		// -
		// --------------------------------------------------------------------

		initMenuBar();
		initAppToolBar();
		initLeftToolBar();
		initRightToolBar();
		initWorkspace();
		initStatusBar();

		// --------------------------------------------------------------------

		setJMenuBar( menuBar );

		add( appToolBar, BorderLayout.NORTH );
		add( leftToolBar, BorderLayout.WEST );
		add( rightToolBar, BorderLayout.EAST );

		add( workspaceSplitPane, BorderLayout.CENTER );

		setStatusBar( statusBar );

		// --------------------------------------------------------------------
		// - Restore window position, size, maximized state -------------------
		// --------------------------------------------------------------------

		int x = node.getInt( PREF_KEY_X, PREF_DEFAULT_X );
		int y = node.getInt( PREF_KEY_Y, PREF_DEFAULT_Y );
		int width = node.getInt( PREF_KEY_WIDTH, PREF_DEFAULT_WIDTH );
		int height = node.getInt( PREF_KEY_HEIGHT, PREF_DEFAULT_HEIGHT );
		setBounds( x, y, width, height );

		boolean maximized = node.getBoolean( PREF_KEY_MAXIMIZED, PREF_DEFAULT_MAXIMIZED );
		if( maximized )
		{
			setExtendedState( getExtendedState() | JFrame.MAXIMIZED_BOTH );
		}

		// --------------------------------------------------------------------
		// - Events for saving window position, size and maximized state ------
		// --------------------------------------------------------------------

		addComponentListener( new ComponentAdapter()
		{
			@Override
			public void componentMoved( ComponentEvent ev )
			{
				int x = ev.getComponent().getX();
				int y = ev.getComponent().getY();

				try
				{
					node.putInt( PREF_KEY_X, x );
					node.putInt( PREF_KEY_Y, y );
					node.flush();
				}
				catch( BackingStoreException ex )
				{
					throw new RuntimeException( ex );
				}
			}

			@Override
			public void componentResized( ComponentEvent ev )
			{
				int width = ev.getComponent().getWidth();
				int height = ev.getComponent().getHeight();

				try
				{
					node.putInt( PREF_KEY_WIDTH, width );
					node.putInt( PREF_KEY_HEIGHT, height );
					node.flush();
				}
				catch( BackingStoreException ex )
				{
					throw new RuntimeException( ex );
				}
			}
		} );

		addWindowStateListener( (ev) ->
		{
			boolean maximized1 = (ev.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;

			try
			{
				node.putBoolean( PREF_KEY_MAXIMIZED, maximized1 );
				node.flush();
			}
			catch( BackingStoreException ex )
			{
				throw new RuntimeException( ex );
			}
		} );

		// --------------------------------------------------------------------
		// - Idle state -------------------------------------------------------
		// --------------------------------------------------------------------

		long idleThreshold = node.getLong( PREF_KEY_IDLE_TIME, PREF_DEFAULT_IDLE_TIME );
		setIdleThreshold( idleThreshold );

		Duration thresholdDuration = Duration.ofMillis( idleThreshold );
		String thresholdTime = String.format( "%02d:%02d:%02d.%03d",
			thresholdDuration.toHoursPart(),
			thresholdDuration.toMinutesPart(),
			thresholdDuration.toSecondsPart(),
			thresholdDuration.toMillisPart()
		);

		log.log( Level.CONFIG, $("msg.idle.threshold"), thresholdTime );

		// --------------------------------------------------------------------
		// - Init plugins UI --------------------------------------------------
		// --------------------------------------------------------------------

		log.config( $("msg.init.plugins") );

		app.getPluginsManager().forEach( plugin ->
		{
			String message = MessageFormat.format( $("msg.init.plugin"), plugin.getIdentifier() );
			log.log( Level.INFO, message );

			plugin.initUI();
		} );

		// --------------------------------------------------------------------

		getRootPane().putClientProperty( com.formdev.flatlaf.FlatClientProperties.FULL_WINDOW_CONTENT, false );

		revalidate();
		setVisible( true );
	}

	private void idle( boolean enteringIdleState )
	{
		if( enteringIdleState )
		{
			setJMenuBar( null );
			waitPanel.refresh();

			setWaiting( true );
		}
		else
		{
			setJMenuBar( menuBar );

			setWaiting( false );
		}

		app.idle( enteringIdleState );
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

	// ========================================================================
	// =
	// ========================================================================

	private void initMenuBar()
	{
		JMenu fileMenu = new JMenu( "File" );
		JMenuItem exitMenuItem = new JMenuItem( app.getAction( "application", ExitAction.ID ) );
		fileMenu.add( exitMenuItem );
		menuBar.add( fileMenu );

		if( app.isDebugModeEnabled() )
		{
			JMenu debugMenu = new JMenu( "Debug" );

			JMenuItem tableViewerMenuItem = new JMenuItem( app.getAction( TableViewer.CATEGORY, TableViewer.ID ) );
			debugMenu.add( tableViewerMenuItem );

			menuBar.add( debugMenu );
		}
	}

	private void initAppToolBar()
	{
		appToolBar.add( app.getAction( VideoPlayer.CATEGORY, VideoPlayer.ID ) );
	}

	private void initLeftToolBar()
	{
		leftToolBar.add( app.getAction( "application", NewSimpleMap.ID ) );
	}

	private void initRightToolBar()
	{
		rightToolBar.add( new JButton( "test" ) );
	}

	private void initWorkspace()
	{
		workspaceSplitPane.setOneTouchExpandable( true );
		workspaceSplitPane.setDividerSize( 12 );

		var taskPane = new JXTaskPane( "One" );
		taskPane.setSpecial( true );
		taskPane.setCollapsed( false );
		navigator.add( taskPane );

		taskPane.add( new JLabel( "a" ) );
		taskPane.add( new JButton( "b" ) );
		taskPane.add( new JTree() );

		taskPane = new JXTaskPane( "Two" );
		taskPane.setSpecial( false );
		taskPane.setCollapsed( true );
		navigator.add( taskPane );

		taskPane = new JXTaskPane( "Three" );
		taskPane.setSpecial( false );
		taskPane.setCollapsed( true );
		navigator.add( taskPane );
	}

	private void initStatusBar()
	{
		// --------------------------------------------------------------------
		// - Message indicator ------------------------------------------------
		// --------------------------------------------------------------------

		JXStatusBar.Constraint c1 = new JXStatusBar.Constraint( JXStatusBar.Constraint.ResizeBehavior.FILL ); // Fill with no inserts
		statusBar.add( statusMessage, c1 );

		// --------------------------------------------------------------------
		// - Free memory indicator --------------------------------------------
		// --------------------------------------------------------------------

		JXLabel freeMemoryLabel = new JXLabel( $("msg.free.memory") );
		freeMemoryLabel.setAlignmentX( 0.5f );

		JXStatusBar.Constraint c3 = new JXStatusBar.Constraint();
		statusBar.add( freeMemoryLabel, c3 );

		Timer freeMemoryTimer = new Timer( 1000, ev ->
		{
			Runtime rt = Runtime.getRuntime();

			double totalMemory = rt.totalMemory() / 1024.d / 1024.d;
			double freeMemory = rt.freeMemory() / 1024.d / 1024.d;
			double usedMemory = totalMemory - freeMemory;

			String text = String.format( $("msg.free.memory"),
				freeMemory, totalMemory, usedMemory );
			freeMemoryLabel.setText( text );
		} );

		freeMemoryTimer.setInitialDelay( 0 );
		freeMemoryTimer.start();

		// --------------------------------------------------------------------
		// - Clock indicator --------------------------------------------------
		// --------------------------------------------------------------------

		JXLabel clockLabel = new JXLabel( $("msg.clock") );
		clockLabel.setAlignmentX( 0.5f );

		JXStatusBar.Constraint c4 = new JXStatusBar.Constraint();
		statusBar.add( clockLabel, c4 );

		Timer clockTimer = new Timer( 1000, ev ->
		{
			SimpleDateFormat formatter = new SimpleDateFormat( $("msg.clock") );
			Date date = new Date();
			clockLabel.setText( formatter.format( date ) );
		} );

		clockTimer.setInitialDelay( 0 );
		clockTimer.start();
	}

	// ========================================================================
	// =
	// ========================================================================

	public void setStatusMessage( String statusText )
	{
		statusMessage.setText( statusText );
	}

	// ------------------------------------------------------------------------

	public void addInternalFrame( CriminalysisInternalFrame internalFrame )
	{
		workspace.add( internalFrame );
	}

	// ========================================================================
	// = Logger ===============================================================
	// ========================================================================

	private static final Logger log
		= Logger.getLogger( MethodHandles.lookup().lookupClass().getName() );

	// ========================================================================
	// = i18n =================================================================
	// ========================================================================

	private static final ResourceBundle rs
		= ResourceBundle.getBundle( MethodHandles.lookup().lookupClass().getName() );

	private static String $( @PropertyKey(resourceBundle = "be.lmenten.criminalysis.ui.CriminalysisMainFrame") String key )
	{
		return rs.getString( key );
	}
}
