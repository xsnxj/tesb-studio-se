package org.talend.camel.designer.ui.editor.dependencies.controls;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

class ImageControl extends Canvas implements PaintListener, MouseTrackListener {

	private boolean isActive = false;

	private Image activeImage = null;
	private Image deactiveImage = null;
	
	private int size = 16;

	ImageControl(Composite parent, int style) {
		super(parent, style);
		setBackground(parent.getBackground());
		addPaintListener(this);
		addMouseTrackListener(this);
	}

	@Override
	public void paintControl(PaintEvent e) {
		Rectangle bounds = getBounds();
		if (isActive && activeImage !=null) {
			e.gc.drawImage(activeImage, bounds.x, bounds.y);
		} else if(!isActive && deactiveImage !=null){
			e.gc.drawImage(deactiveImage, bounds.x, bounds.y);
		}
	}

	public void setActiveImage(Image activeImage) {
		this.activeImage = activeImage;
	}

	public void setDeactiveImage(Image deactiveImage) {
		this.deactiveImage = deactiveImage;
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(size, size);
	}

	@Override
	public Rectangle getClientArea() {
		return getBounds();
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(0, 0, size, size);
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		this.isActive = true;
		redraw();
	}

	@Override
	public void mouseExit(MouseEvent e) {
		this.isActive = false;
		redraw();
	}

	@Override
	public void mouseHover(MouseEvent e) {

	}
}