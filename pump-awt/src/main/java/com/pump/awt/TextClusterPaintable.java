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
package com.pump.awt;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pump.blog.ResourceSample;
import com.pump.geom.ShapeBounds;

/**
 * A cluster of strings that are randomly distributed, rotated, and sized.
 *
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/samples/TextClusterPaintable/sample.png"
 * alt="com.pump.awt.TextClusterPaintable.createDemo()">
 * <!-- ======== END OF AUTOGENERATED SAMPLES ======== -->
 */
@ResourceSample(sample = { "com.pump.awt.TextClusterPaintable.createDemo()" })
public class TextClusterPaintable extends CompositePaintable<TextBoxPaintable> {

	/**
	 * @return a demo of this paintable:
	 *         <p>
	 *         <img src=
	 *         "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/samples/TextClusterPaintable/sample.png"
	 *         alt="com.pump.awt.TextClusterPaintable.createDemo()">
	 */
	public static TextClusterPaintable createDemo() {
		return new TextClusterPaintable(new String[] { "One", "Two", "Three",
				"Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
				"Eleven", "Twelve", "Thirteen" }, new Font("Verdana", 0, 14),
				12, 24, 200, 0L);
	}

	public TextClusterPaintable(String[] s, Font font, float fontSizeMin,
			float fontSizeMax, float maxWidth, long randomSeed) {
		setTiling(Tiling.OFFSET);
		Random random = new Random(randomSeed);
		float minWidth = maxWidth * .3f;

		List<TextBoxPaintable> myPaintables = new ArrayList<TextBoxPaintable>(
				s.length);
		List<AffineTransform> myTransforms = new ArrayList<AffineTransform>(
				s.length);

		float textInsets = 10;
		Area outline = new Area();
		int angleMultiplier = 0;
		for (int a = 0; a < s.length; a++) {
			float fontSize = fontSizeMin + (fontSizeMax - fontSizeMin)
					* random.nextFloat();
			Font myFont = font.deriveFont(fontSizeMin);
			float myWidth = minWidth + (maxWidth - minWidth)
					* random.nextFloat();
			TextBoxPaintable tb = new TextBoxPaintable(s[a], myFont, myWidth,
					textInsets);
			if (tb.getLineCount() < 3) {
				// experiment with making this text box taller:
				int iter = 0;
				while (tb.getLineCount() < 4 && iter < 20) {
					fontSize = Math.min(fontSizeMax, fontSize + 2);
					myFont = font.deriveFont(fontSizeMin);
					myWidth = Math.max(myWidth - 20, minWidth);
					tb = new TextBoxPaintable(s[a], myFont, myWidth, textInsets);
					iter++;
				}
			} else if (tb.getLineCount() > 12 || tb.getHeight() > tb.getWidth()) {
				// experiment with making this text box shorter:
				int iter = 0;
				while ((tb.getLineCount() > 12 || tb.getHeight() > tb
						.getWidth()) && iter < 20) {
					fontSize = Math.max(fontSizeMin, fontSize - 2);
					myFont = font.deriveFont(fontSizeMin);
					myWidth = Math.min(myWidth + 10, maxWidth);
					tb = new TextBoxPaintable(s[a], myFont, myWidth, textInsets);
					iter++;
				}
			}

			Rectangle2D bounds = new Rectangle2D.Double(0, 0, tb.getWidth(),
					tb.getHeight());
			if (a == 0) {
				myPaintables.add(tb);
				myTransforms.add(new AffineTransform());
				outline.add(new Area(bounds));
			} else {
				angleMultiplier = (angleMultiplier + 1) % 4;
				double angle = angleMultiplier * Math.PI / 2.0;
				AffineTransform transform = AffineTransform
						.getRotateInstance(angle);
				Rectangle2D transformedOutline = ShapeBounds.getBounds(bounds,
						transform);
				double xIncr = 30.0 * random.nextDouble() - 15;
				double yIncr = 30.0 * random.nextDouble() - 15;
				while (Math.abs(xIncr) < .05 && Math.abs(yIncr) < .05) {
					xIncr = 30.0 * random.nextDouble() - 15;
					yIncr = 30.0 * random.nextDouble() - 15;
				}
				double dx = 0;
				double dy = 0;
				while (outline.intersects(transformedOutline)) {
					dx += xIncr;
					dy += yIncr;

					transform = AffineTransform.getTranslateInstance(dx, dy);
					transform.rotate(angle, dx, dy);
					ShapeBounds
							.getBounds(bounds, transform, transformedOutline);
				}
				myPaintables.add(tb);
				myTransforms.add(transform);
				outline.add(new Area(ShapeBounds.getBounds(bounds, transform)));
			}
		}
		Rectangle2D totalBounds = outline.getBounds2D();
		AffineTransform translation = AffineTransform.getTranslateInstance(
				-totalBounds.getX(), -totalBounds.getY());

		for (int a = 0; a < myPaintables.size(); a++) {
			myTransforms.get(a).preConcatenate(translation);
			addPaintable(myPaintables.get(a), myTransforms.get(a));
		}
	}
}