package be.lmenten.criminalysis.ui.frames.video;

import be.lmenten.criminalysis.api.CriminalysisInternalFrame;

import uk.co.caprica.vlcj.player.base.Marquee;
import uk.co.caprica.vlcj.player.base.MarqueePosition;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public class Player
	extends CriminalysisInternalFrame
{
	private static final String url = "D:\\Documents\\Affaire Dutroux\\Videos\\Dutroux\\Faites entrer l accusé - Marc Dutroux, Le démon Belge.mp4";

	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;

	public Player()
	{
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		addInternalFrameListener( new InternalFrameAdapter()
		{
			public void internalFrameClosing( InternalFrameEvent e )
			{
				mediaPlayerComponent.release();
				dispose();
			}
		});

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable( false );
		add( toolBar, BorderLayout.NORTH );

		JButton playButton = new JButton( "Play" );
		playButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				mediaPlayerComponent.mediaPlayer().media().play( url );
			}
		} );
		toolBar.add( playButton );

		JButton pauseButton = new JButton( "Pause" );
		pauseButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				mediaPlayerComponent.mediaPlayer().controls().pause();
			}
		} );
		toolBar.add( pauseButton );

		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		add( mediaPlayerComponent, BorderLayout.CENTER );

		Marquee marquee = Marquee.marquee()
			.text( url )
			.size(20)
			.colour(Color.WHITE)
//			.timeout(3000)
			.position( MarqueePosition.BOTTOM_RIGHT)
			.opacity(0.8f)
			.enable();
		mediaPlayerComponent.mediaPlayer().marquee().set(marquee);

		setSize( 800, 600 );
		setVisible(true);
	}
}
