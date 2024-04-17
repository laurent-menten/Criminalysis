package be.lmenten.criminalysis.ui;

import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.image.GaussianBlurFilter;
import org.jdesktop.swingx.util.GraphicsUtilities;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * A panel that show the content pane of JXFrame blurred with a specified component
 * above and centered.
 */
public class IdleWaitPanel
	extends JXPanel
{
	private final JXFrame parentFrame;

	// ------------------------------------------------------------------------

	float alpha = 1f;

	BufferedImage currentGraphics;
	BufferedImage backBuffer;
	BufferedImage blurBuffer;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	public IdleWaitPanel( JXFrame parentFrame, Component unlockComponent )
	{
		this.parentFrame = parentFrame;

		setOpaque( false );

		// install unlock component centered

		setLayout( new MigLayout() );

		add( unlockComponent, "push, align center" );

		// frame may not be ready yet... so use 1 by 1 size

		currentGraphics = new BufferedImage( 1, 1, BufferedImage.TYPE_3BYTE_BGR );
	}

	// ========================================================================
	// = API ==================================================================
	// ========================================================================

	public void refresh()
	{
		Container contentPane = parentFrame.getContentPane();
		currentGraphics = new BufferedImage( contentPane.getWidth(), contentPane.getHeight(), BufferedImage.TYPE_3BYTE_BGR );

		blurBuffer = GraphicsUtilities.createCompatibleImage( parentFrame.getWidth(), parentFrame.getHeight());
		Graphics2D g2d = blurBuffer.createGraphics();
		contentPane.paint( g2d );
		g2d.dispose();

		backBuffer = blurBuffer;
		blurBuffer = GraphicsUtilities.createThumbnailFast( blurBuffer, parentFrame.getWidth()/2 );
		blurBuffer = new GaussianBlurFilter( 10 ).filter( blurBuffer, null );
	}

	// ========================================================================
	// = JXPanel interface ====================================================
	// ========================================================================

	@Override
	protected void paintComponent( Graphics g )
	{
		if( isVisible() && blurBuffer != null )
		{
			Graphics2D g2d = (Graphics2D) g.create();

			g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
			g2d.drawImage( backBuffer, 0, 0,null );

			g2d.setComposite( AlphaComposite.SrcOver.derive( alpha ) );
			g2d.drawImage( blurBuffer, 0, 0, parentFrame.getWidth(), parentFrame.getHeight(),null );

			g2d.dispose();
		}
	}
}
