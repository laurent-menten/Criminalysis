/*
 *   Swing Explorer. Tool for developers exploring Java/Swing-based application internals. 
 * 	 Copyright (C) 2012, Maxim Zakharenkov
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *   
 */
package org.swingexplorer.internal;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * 
 * @author Maxim Zakharenkov
 */
public class ActMoveOverDisplay extends MouseAdapter implements MouseMotionListener  {
	
	private PnlGuiDisplay display;
	MdlSwingExplorer model;
	
	ActMoveOverDisplay(PnlGuiDisplay displayP, MdlSwingExplorer modelP) {
		display = displayP;
		model = modelP;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Component over = display.getDisplayedComponentAt(e.getPoint());
		model.setCurrentComponent(over);
		if(over != null) {
			model.setStatustext(model.getComponentPath(over, false));
            model.setMouseLocation(e.getPoint());
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		model.setCurrentComponent(null);
		model.setMouseLocation(null);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}
}
