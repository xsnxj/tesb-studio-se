package org.talend.designer.camel.dependencies.ui.editor.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SearchControl extends Composite implements PaintListener {

	private Text text;

	private int arcSize = 18;
	
	private int marginHorizon = 5;

	private ImageControl clear;

	private Color backgroundColor;

	public SearchControl(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.backgroundColor = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		initialize(style);
		addPaintListener(this);
	}

	private void initialize(int style) {
		text = new Text(this, style);
		text.setBackground(backgroundColor);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setMessage("search what you want");
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}
		});
		
		text.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				text.selectAll();
			}
		});
		
		clear = new ImageControl(this, SWT.NONE);
		clear.setBackground(backgroundColor);
		clear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				text.setText("");
			}
		});
		
		Point computeSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int height = computeSize.y+2;
		arcSize = height > arcSize ?height:arcSize;

		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = marginHorizon;
		layout.marginRight = marginHorizon;
		layout.marginTop = 1;
		layout.marginWidth = 0;
		setLayout(layout);
	}

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		gc.setAntialias(SWT.ON);
		Rectangle bounds = getBounds();
		Path path = new Path(getDisplay());
		path.addArc(bounds.x, bounds.y, arcSize, arcSize, 90, 180);
		path.addRectangle(bounds.x + arcSize / 2, bounds.y, bounds.width - arcSize,
				arcSize);
		path.addArc(bounds.x + bounds.width - arcSize, bounds.y, arcSize, arcSize,
				270, 180);
		gc.setClipping(path);
		gc.setBackground(backgroundColor);
		gc.fillPath(path);
		path.dispose();
		gc.setAntialias(SWT.OFF);
	}

	@Override
	public Rectangle getBounds() {
		Rectangle bounds = super.getBounds();
		bounds.height = arcSize;
		bounds.x = 0;
		bounds.y = 0;
		return bounds;
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point computeSize = super.computeSize(wHint, hHint, changed);
		computeSize.y = arcSize;
		return computeSize;
	}

	@Override
	public Rectangle computeTrim(int x, int y, int width, int height) {
		Rectangle computeTrim = super.computeTrim(x, y, width, height);
		computeTrim.width = width;
		return computeTrim;
	}

	@Override
	public Rectangle getClientArea() {
		return getBounds();
	}

	public Text getText() {
		return text;
	}

	@Override
	public void setBackground(Color color) {
		this.backgroundColor = color;
		super.setBackground(getParent().getBackground());
		redraw();
		text.setBackground(backgroundColor);
		clear.setBackground(backgroundColor);
	}

	public void setActiveImage(Image activeImage) {
		clear.setActiveImage(activeImage);
	}

	public void setDeactiveImage(Image deactiveImage) {
		clear.setDeactiveImage(deactiveImage);
	}
}