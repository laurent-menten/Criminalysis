package be.lmenten.criminalysis.ui.frames.map;

import be.lmenten.criminalysis.api.CriminalysisInternalFrame;
import be.lmenten.util.swing.scrollabledesktop.JScrollableDesktopInternalFrame;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

public class SimpleMap
	extends CriminalysisInternalFrame
{
	public SimpleMap()
	{
		super( "Simple Map" );


		final JXMapKit jXMapKit = new JXMapKit();
		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		jXMapKit.setTileFactory(tileFactory);

		final GeoPosition gp = new GeoPosition( 50.64450781, 5.56297660 );

		final JToolTip tooltip = new JToolTip();
		tooltip.setTipText( "home" );
		tooltip.setComponent(jXMapKit.getMainMap());
		jXMapKit.getMainMap().add(tooltip);

		jXMapKit.setZoom(11);
		jXMapKit.setAddressLocation(gp);

		jXMapKit.getMainMap().addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged( MouseEvent e) {
				// ignore
			}

			@Override
			public void mouseMoved(MouseEvent e)
			{
				JXMapViewer map = jXMapKit.getMainMap();

				// convert to world bitmap
				Point2D worldPos = map.getTileFactory().geoToPixel(gp, map.getZoom());

				// convert to screen
				Rectangle rect = map.getViewportBounds();
				int sx = (int) worldPos.getX() - rect.x;
				int sy = (int) worldPos.getY() - rect.y;
				Point screenPos = new Point(sx, sy);

				// check if near the mouse
				if (screenPos.distance(e.getPoint()) < 20)
				{
					screenPos.x -= tooltip.getWidth() / 2;

					tooltip.setLocation(screenPos);
					tooltip.setVisible(true);
				}
				else
				{
					tooltip.setVisible(false);
				}
			}
		} );

		getContentPane().add( jXMapKit, BorderLayout.CENTER );
		setSize( 800, 600 );
	}
}
