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
package com.pump.showcase;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import com.pump.awt.HalftoneGradient;
import com.pump.blog.ResourceSample;
import com.pump.util.CompoundKey;

/**
 * A simple app demoing the {@link HalftoneGradient}.
 *
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/samples/HalftoneGradientDemo/sample.png"
 * alt="new&#160;com.pump.awt.HalftoneGradientDemo()"> 
 * <!-- ======== END OF AUTOGENERATED SAMPLES ======== -->
 */
@ResourceSample(sample = "new com.pump.awt.HalftoneGradientDemo()")
public class HalftoneGradientDemo extends JPanel {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame f = new JFrame();
				HalftoneGradientDemo demo = new HalftoneGradientDemo();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.add(demo);
				f.pack();
				f.setVisible(true);
			}
		});
	}

	JRadioButton circle = new JRadioButton("Circle");
	JRadioButton diamond = new JRadioButton("Diamond");
	JRadioButton triangle = new JRadioButton("Triangle");
	JSlider widthSlider = new JSlider(5, 40, HalftoneGradient.DEFAULT_WIDTH);
	JSlider offsetSlider = new JSlider(0, 100, 0);
	JSlider shearSlider = new JSlider(0, 100, 50);
	PreviewPanel preview = new PreviewPanel();
	Point2D p1, p2;
	JCheckBox cycle = new JCheckBox("Cycle");
	JCheckBox animate = new JCheckBox("Animate");
	JCheckBox paintTwoLayers = new JCheckBox("Paint Two Layers");

	public HalftoneGradientDemo() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3, 3, 3, 3);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.EAST;
		add(new JLabel("Width:"), c);
		c.gridy++;
		add(new JLabel("Offset:"), c);
		c.gridy++;
		add(new JLabel("Shear:"), c);
		c.gridy++;
		add(new JLabel("Shape:"), c);
		c.gridy++;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.WEST;
		add(cycle, c);
		c.gridy++;
		add(animate, c);
		c.gridy++;
		// this is just for fun... but I commented it out to keep
		// the demo really simple. I don't want to confuse what
		// the arguments for the HalftoneGradient are...
		// add(paintTwoLayers,c);

		c.gridy = 0;
		c.gridx++;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(widthSlider, c);
		c.gridy++;
		add(offsetSlider, c);
		c.gridy++;
		add(shearSlider, c);
		c.gridy++;
		c.gridwidth = 1;
		c.weightx = 0;
		add(circle, c);
		c.gridx++;
		add(diamond, c);
		c.gridx++;
		add(triangle, c);
		c.gridy += 3;
		c.gridx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(preview, c);

		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() != offsetSlider)
					cachedPaints.clear();
				preview.repaint();
			}
		};

		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cachedPaints.clear();
				preview.repaint();
			}
		};

		widthSlider.addChangeListener(changeListener);
		offsetSlider.addChangeListener(changeListener);
		shearSlider.addChangeListener(changeListener);
		circle.addActionListener(actionListener);
		diamond.addActionListener(actionListener);
		triangle.addActionListener(actionListener);
		cycle.addActionListener(actionListener);
		paintTwoLayers.addActionListener(actionListener);

		animate.addActionListener(new ActionListener() {
			Timer timer = new Timer(50, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int value = offsetSlider.getValue();
					value -= 5;
					if (value < 0)
						value = 100;
					offsetSlider.setValue(value);
				}
			});

			public void actionPerformed(ActionEvent e) {
				if (animate.isSelected()) {
					timer.start();
				} else {
					timer.stop();
				}
			}
		});

		circle.setOpaque(false);
		diamond.setOpaque(false);
		triangle.setOpaque(false);
		shearSlider.setOpaque(false);
		widthSlider.setOpaque(false);
		offsetSlider.setOpaque(false);
		animate.setOpaque(false);
		cycle.setOpaque(false);
		ButtonGroup bg = new ButtonGroup();
		bg.add(diamond);
		bg.add(circle);
		bg.add(triangle);
		circle.doClick();
	}

	class PreviewPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		MouseInputAdapter mouseListener = new MouseInputAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					return;

				Point p = e.getPoint();
				if (p.distance(p1) < p.distance(p2)) {
					p1.setLocation(p);
				} else {
					p2.setLocation(p);
				}
				preview.repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				mousePressed(e);
			}
		};

		public PreviewPanel() {
			setPreferredSize(new Dimension(500, 500));
			addMouseListener(mouseListener);
			addMouseMotionListener(mouseListener);
		}

		Ellipse2D dot = new Ellipse2D.Float();

		@Override
		protected void paintComponent(Graphics g0) {
			super.paintComponent(g0);

			if (p1 == null) {
				p1 = new Point2D.Double(getWidth() * .2, getHeight() * .2);
				p2 = new Point2D.Double(getWidth() * .8, getHeight() * .8);
			}

			Graphics2D g = (Graphics2D) g0;

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);

			try {
				int type = HalftoneGradient.TYPE_CIRCLE;
				if (diamond.isSelected())
					type = HalftoneGradient.TYPE_DIAMOND;
				if (triangle.isSelected())
					type = HalftoneGradient.TYPE_TRIANGLE;

				float offset = (offsetSlider.getValue()) / 100f;
				float shear = ((shearSlider.getValue() - 50f)) / 25f;
				if (paintTwoLayers.isSelected()) {
					HalftoneGradient p = getHalftoneGradient(p1, new Color(
							0xD46EE6), p2, new Color(0x9122C7),
							widthSlider.getValue(), type, cycle.isSelected(),
							offset, shear);

					g.setPaint(p);
					g.fillRect(0, 0, getWidth(), getHeight());

					p = getHalftoneGradient(p1, new Color(0x00D46EE6, true),
							p2, new Color(0xBB413EA7, true),
							widthSlider.getValue() * 2, type,
							cycle.isSelected(), offset, shear);

					g.setPaint(p);
					g.fillRect(0, 0, getWidth(), getHeight());
				} else {
					HalftoneGradient p = getHalftoneGradient(p1, new Color(
							0xE6D46E), p2, new Color(0xA7413E),
							widthSlider.getValue() * 2, type,
							cycle.isSelected(), offset, shear);

					g.setPaint(p);
					g.fillRect(0, 0, getWidth(), getHeight());
				}

				dot.setFrame(p1.getX() - 5, p1.getY() - 5, 10, 10);
				g.setColor(Color.white);
				g.fill(dot);
				g.setColor(Color.black);
				g.draw(dot);

				dot.setFrame(p2.getX() - 5, p2.getY() - 5, 10, 10);
				g.setColor(Color.white);
				g.fill(dot);
				g.setColor(Color.black);
				g.draw(dot);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	/**
	 * If the "Animate" checkbox is selected, then this program will quickly
	 * churn through megabytes of memory to constantly create the tiles for
	 * every repaint. Instead let's cache those paints. This cache gets cleared
	 * for any property other than the offset.
	 */
	Map<CompoundKey, HalftoneGradient> cachedPaints = new HashMap<>();

	protected HalftoneGradient getHalftoneGradient(Point2D p1, Color c1,
			Point2D p2, Color c2, int width, int type, boolean cycle,
			float offset, float shear) {

		// Object key =
		// p1+" "+c1+" "+p2+" "+c2+" "+width+" "+type+" "+cycle+" "+offset+" "+shear;
		CompoundKey key = new CompoundKey(new Object[] { p1, c1, p2, c2,
				new Integer(width), new Integer(type), new Boolean(cycle),
				new Float(offset), new Float(shear) });
		HalftoneGradient paint = cachedPaints.get(key);
		if (paint == null) {
			paint = new HalftoneGradient(p1, c1, p2, c2, width, type, cycle,
					offset, shear);
			cachedPaints.put(key, paint);
		}
		return paint;
	}
}