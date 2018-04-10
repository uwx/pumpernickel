/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.icon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

import javax.swing.Icon;

import com.pump.blog.ResourceSample;

/**
 * This is a monotone icon intended to trigger refreshes. It is a 3/4's circular
 * arc with a triangle at the end, similar to what Firefox and Safari show on
 * the far right side of the URL field.
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/samples/RefreshIcon/sample.png"
 * alt=
 * "new&#160;com.pump.swing.resources.RefreshIcon(&#160;24,&#160;java.awt.Color.gray)"> 
 * <!-- ======== END OF AUTOGENERATED SAMPLES ======== -->
 */
@ResourceSample(sample = { "new com.pump.swing.resources.RefreshIcon( 24, java.awt.Color.gray)" })
public class RefreshIcon implements Icon {

	int size;
	Color color;

	public RefreshIcon(int size) {
		this(size, Color.gray);
	}

	public RefreshIcon(int size, Color color) {
		if (color == null)
			throw new IllegalArgumentException();
		this.size = size;
		this.color = color;
	}

	public int getIconHeight() {
		return size;
	}

	public int getIconWidth() {
		return size;
	}

	/**
	 * Create one static instance of the shape we're painting. If we draw the
	 * icon as a combination of two layered shapes: if translucent colors are
	 * used the overlap will introduce strange artifacts.
	 */
	static Area refreshShape = null;
	static float idealSize = 14;

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (refreshShape == null) {
			Arc2D arc = new Arc2D.Double(Arc2D.OPEN);
			int rInset = 2;
			arc.setArc(rInset, rInset, idealSize - 2 * rInset, idealSize - 2
					* rInset, 45, 270, Arc2D.OPEN);
			refreshShape = new Area(new BasicStroke(2).createStrokedShape(arc));

			GeneralPath triangle = new GeneralPath();
			float arcX = (float) (idealSize / 2 + (idealSize / 2 - rInset)
					* Math.cos(-Math.PI / 4));
			float arcY = (float) (idealSize / 2 + (idealSize / 2 - rInset)
					* Math.sin(-Math.PI / 4));
			triangle.moveTo(arcX + 1.5f * rInset, arcY - 1.5f * rInset);
			triangle.lineTo(arcX + 1.5f * rInset, arcY + 1.5f * rInset);
			triangle.lineTo(arcX - 1.5f * rInset, arcY + 1.5f * rInset);
			triangle.closePath();
			refreshShape.add(new Area(triangle));
		}

		g2.translate(x, y);
		float scaleX = (getIconWidth()) / idealSize;
		float scaleY = (getIconHeight()) / idealSize;
		g2.scale(scaleX, scaleY);
		g2.setColor(color);
		g2.fill(refreshShape);
		g2.dispose();
	}

}