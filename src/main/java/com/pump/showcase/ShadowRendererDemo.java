package com.pump.showcase;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;

import com.pump.geom.Spiral2D;
import com.pump.geom.StarPolygon;
import com.pump.image.shadow.ARGBPixels;
import com.pump.image.shadow.BoxShadowRenderer;
import com.pump.image.shadow.DoubleBoxShadowRenderer;
import com.pump.image.shadow.GaussianKernel;
import com.pump.image.shadow.GaussianShadowRenderer;
import com.pump.image.shadow.ShadowAttributes;
import com.pump.image.shadow.ShadowRenderer;
import com.pump.inspector.Inspector;
import com.pump.plaf.AngleSliderUI;
import com.pump.showcase.chart.LineChartRenderer;
import com.pump.swing.JFancyBox;
import com.pump.swing.QDialog;

public class ShadowRendererDemo extends ShowcaseExampleDemo {

	/**
	 * For testing and comparison purposes: this is the original unoptimized
	 * Gaussian shadow renderer.
	 *
	 */
	public static class OriginalGaussianShadowRenderer
			implements ShadowRenderer {

		@Override
		public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
				ShadowAttributes attr) {
			GaussianKernel kernel = getKernel(attr);
			int k = kernel.getKernelRadius();

			int shadowSize = k * 2;

			int srcWidth = src.getWidth();
			int srcHeight = src.getHeight();

			int dstWidth = srcWidth + shadowSize;
			int dstHeight = srcHeight + shadowSize;

			if (dst == null)
				dst = new ARGBPixels(dstWidth, dstHeight);

			if (dst.getWidth() != dstWidth)
				throw new IllegalArgumentException(
						dst.getWidth() + " != " + dstWidth);
			if (dst.getHeight() != dstHeight)
				throw new IllegalArgumentException(
						dst.getWidth() + " != " + dstWidth);

			int[] dstBuffer = dst.getPixels();
			int[] srcBuffer = src.getPixels();

			int[] opacityLookup = new int[256];
			float opacity = attr.getShadowOpacity();
			for (int a = 0; a < opacityLookup.length; a++) {
				opacityLookup[a] = (int) (a * opacity);
			}

			int x1 = k;
			int x2 = k + srcWidth;

			int[] kernelArray = kernel.getArray();
			int kernelSum = kernel.getArraySum();

			// vertical pass:
			for (int dstX = x1; dstX < x2; dstX++) {
				int srcX = dstX - k;
				for (int dstY = 0; dstY < dstHeight; dstY++) {
					int srcY = dstY - k;
					int g = srcY - k;
					int w = 0;
					for (int j = 0; j < kernelArray.length; j++) {
						int kernelY = g + j;
						if (kernelY >= 0 && kernelY < srcHeight) {
							int argb = srcBuffer[srcX + kernelY * srcWidth];
							int alpha = argb >>> 24;
							w += alpha * kernelArray[j];
						}
					}
					w = w / kernelSum;
					dstBuffer[dstY * dstWidth + dstX] = w;
				}
			}

			// horizontal pass:
			int[] row = new int[dstWidth];
			for (int dstY = 0; dstY < dstHeight; dstY++) {
				System.arraycopy(dstBuffer, dstY * dstWidth, row, 0,
						row.length);
				for (int dstX = 0; dstX < dstWidth; dstX++) {
					int w = 0;
					for (int j = 0; j < kernelArray.length; j++) {
						int kernelX = dstX - k + j;
						if (kernelX >= 0 && kernelX < dstWidth) {
							w += row[kernelX] * kernelArray[j];
						}
					}
					w = w / kernelSum;
					dstBuffer[dstY * dstWidth + dstX] = opacityLookup[w] << 24;
				}
			}

			return dst;

		}

		@Override
		public GaussianKernel getKernel(ShadowAttributes attr) {
			return new GaussianKernel(attr.getShadowKernelRadius(), false);
		}
	}

	public static BufferedImage createTestImage() {
		BufferedImage bi = new BufferedImage(300, 100,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.white);
		g.setStroke(new BasicStroke(4));
		g.draw(new Ellipse2D.Float(-10, -10, 20, 20));
		g.draw(new Ellipse2D.Float(bi.getWidth() - 10, bi.getHeight() - 10, 20,
				20));
		g.draw(new Ellipse2D.Float(bi.getWidth() - 10, -10, 20, 20));
		g.draw(new Ellipse2D.Float(-10, bi.getHeight() - 10, 20, 20));

		StarPolygon star = new StarPolygon(40);
		star.setCenter(50, 50);
		g.setColor(new Color(0x1BE7FF));
		g.fill(star);

		BufferedImage textureBI = new BufferedImage(20, 60,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = textureBI.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		for (int z = 0; z < 500; z++) {
			g2.setStroke(new BasicStroke(8));
			g2.setColor(new Color(0xFF5714));
			g2.drawLine(-100 + z * 20, 100, 100 + z * 20, -100);
			g2.setStroke(new BasicStroke(10));
			g2.setColor(new Color(0x6EEB83));
			g2.drawLine(200 - z * 20, 100, 0 - z * 20, -100);
		}
		g2.dispose();
		Rectangle r = new Rectangle(0, 0, textureBI.getWidth(),
				textureBI.getHeight());
		g.setPaint(new TexturePaint(textureBI, r));
		Shape roundRect = new RoundRectangle2D.Float(110, 10, 80, 80, 40, 40);
		g.fill(roundRect);

		Spiral2D spiral = new Spiral2D(250, 50, 20, 2, 0, 0, true);
		g.setStroke(new BasicStroke(10));
		g.setColor(new Color(0xE8AA14));
		g.draw(spiral);
		return bi;
	}

	private static final long serialVersionUID = 1L;

	static class ProfileResults {
		Map<String, SortedMap<Double, Double>> results = new TreeMap<>();

		public void store(ShadowRenderer renderer, float kernelSize,
				long time) {
			SortedMap<Double, Double> m = results
					.get(renderer.getClass().getSimpleName());
			if (m == null) {
				m = new TreeMap<>();
				results.put(renderer.getClass().getSimpleName(), m);
			}
			m.put((double) kernelSize, (double) time);
		}

		public void printTable() {
			StringBuilder sb = new StringBuilder();
			sb.append("Kernel\t");
			for (String name : results.keySet()) {
				sb.append(name);
				sb.append("\t");
			}
			System.out.println(sb.toString().trim());

			SortedSet<Double> allKeys = new TreeSet<>();
			for (SortedMap<Double, Double> m : results.values()) {
				allKeys.addAll(m.keySet());
			}
			for (Double key : allKeys) {
				sb.delete(0, sb.length());
				sb.append(key.toString());
				sb.append("\t");
				for (String name : results.keySet()) {
					sb.append(results.get(name).get(key));
					sb.append("\t");
				}
				System.out.println(sb.toString().trim());
			}
		}
	}

	JComboBox<String> rendererComboBox = new JComboBox<>();
	JSpinner kernelSizeSpinner = new JSpinner(
			new SpinnerNumberModel(5f, .1f, 25f, .1f));
	JSlider opacitySlider = new ShowcaseSlider(1, 100, 50);
	JSlider angleSlider = new ShowcaseSlider(0, 359, 45);
	JSlider offsetSlider = new ShowcaseSlider(0, 20, 10);

	BufferedImage srcImage;

	ActionListener refreshActionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshExample();
		}

	};

	ChangeListener refreshChangeListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			refreshExample();
		}

	};

	public ShadowRendererDemo() {
		super();
		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(new JLabel("Renderer:"), rendererComboBox);
		inspector.addRow(new JLabel("Kernel Size:"), kernelSizeSpinner);
		inspector.addRow(new JLabel("Opacity:"), opacitySlider);
		inspector.addRow(new JLabel("Angle:"), angleSlider);
		inspector.addRow(new JLabel("Offset:"), offsetSlider);

		rendererComboBox.addItem("Box");
		rendererComboBox.addItem("Double Box");
		rendererComboBox.addItem("Gaussian");

		addSliderPopover(opacitySlider, "%");
		addSliderPopover(offsetSlider, " pixels");

		SliderUI ui = new AngleSliderUI();
		angleSlider.setUI(ui);

		rendererComboBox.addActionListener(refreshActionListener);
		kernelSizeSpinner.addChangeListener(refreshChangeListener);
		opacitySlider.addChangeListener(refreshChangeListener);
		angleSlider.addChangeListener(refreshChangeListener);
		offsetSlider.addChangeListener(refreshChangeListener);

		((DefaultEditor) kernelSizeSpinner.getEditor()).getTextField()
				.setColumns(4);

		refreshExample();
	}

	static class RunSample implements Runnable {
		ShadowRenderer renderer;
		ShadowAttributes attr;
		ARGBPixels srcPixels, dstPixels;
		ProfileResults profileResults;

		public RunSample(ProfileResults profileResults, ShadowRenderer renderer,
				ShadowAttributes attr, ARGBPixels srcPixels,
				ARGBPixels dstPixels) {
			this.renderer = renderer;
			this.attr = attr;
			this.dstPixels = dstPixels;
			this.srcPixels = srcPixels;
			this.profileResults = profileResults;
		}

		public void run() {
			long[] times = new long[10];
			for (int a = 0; a < times.length; a++) {

				times[a] = System.currentTimeMillis();
				for (int b = 0; b < 100; b++) {
					Arrays.fill(dstPixels.getPixels(), 0);
					renderer.createShadow(srcPixels, dstPixels, attr);
				}
				times[a] = System.currentTimeMillis() - times[a];
			}
			Arrays.sort(times);
			profileResults.store(renderer, attr.getShadowKernelRadius(),
					times[times.length / 2]);
		}
	}

	static class UpdateProgressBar implements Runnable {
		JProgressBar progressBar;
		int min, max, value;

		UpdateProgressBar(JProgressBar progressBar, int min, int max,
				int value) {
			this.progressBar = progressBar;
			this.min = min;
			this.max = max;
			this.value = value;
		}

		@Override
		public void run() {
			progressBar.getModel().setRangeProperties(value, 1, min, max,
					false);
			progressBar.setIndeterminate(false);
		}
	}

	private void profileRenderers(ProfileResults profileResults,
			final QDialog dialog, final JProgressBar progressBar,
			Collection<ShadowRenderer> renderers) {
		try {
			ARGBPixels srcPixels = new ARGBPixels(srcImage);
			srcImage.getRaster().getDataElements(0, 0, srcPixels.getWidth(),
					srcPixels.getHeight(), srcPixels.getPixels());

			List<Runnable> runnables = new LinkedList<>();
			for (ShadowRenderer renderer : renderers) {
				float min = .5f;
				float max = 25;
				for (float kernelSize = min; kernelSize <= max; kernelSize += .5f) {
					ShadowAttributes attr = new ShadowAttributes(kernelSize,
							.5f);
					int k = renderer.getKernel(attr).getKernelRadius();
					ARGBPixels dstPixels = new ARGBPixels(
							srcImage.getWidth() + 2 * k,
							srcImage.getHeight() + 2 * k);
					runnables.add(new RunSample(profileResults, renderer, attr,
							srcPixels, dstPixels));
				}
			}

			SwingUtilities.invokeLater(
					new UpdateProgressBar(progressBar, 0, runnables.size(), 0));
			int ctr = 0;
			int size = runnables.size();
			while (!runnables.isEmpty()) {
				Runnable runnable = runnables.remove(0);
				runnable.run();
				SwingUtilities.invokeLater(
						new UpdateProgressBar(progressBar, 0, size, ctr++));
			}

			profileResults.printTable();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					dialog.setVisible(false);
				}
			});
		}
	}

	@Override
	protected JComponent getComponentBelowExamplePanel() {
		JButton profileButton = new JButton("Profile Performance");

		profileButton.addActionListener(new ActionListener() {
			ProfileResults profileResults;
			QDialog dialog;
			JProgressBar progressBar;

			Collection<ShadowRenderer> renderers = Arrays.asList(
					new OriginalGaussianShadowRenderer(),
					new GaussianShadowRenderer(), new DoubleBoxShadowRenderer(),
					new BoxShadowRenderer());

			public void actionPerformed(ActionEvent e) {
				progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0,
						100);
				progressBar.setIndeterminate(true);

				JFrame frame = (JFrame) SwingUtilities
						.getWindowAncestor(ShadowRendererDemo.this);
				if (profileResults == null) {
					profileResults = new ProfileResults();
					JComponent content = QDialog.createContentPanel(
							"Profiling ShadowRenderers...",
							"Please wait while I test the execution time for different shadow configurations.",
							progressBar, true);
					dialog = new QDialog(frame, "Profiling",
							QDialog.getIcon(QDialog.INFORMATION_MESSAGE),
							content, null, false);
					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					Thread profileThread = new Thread() {
						@Override
						public void run() {
							profileRenderers(profileResults, dialog,
									progressBar, renderers);
						}
					};
					profileThread.start();
					dialog.setVisible(true);
				}

				try {
					LineChartRenderer renderer = new LineChartRenderer(
							profileResults.results);
					BufferedImage bi = renderer.render(new Dimension(600, 300));
					JLabel content = new JLabel(new ImageIcon(bi));
					JFancyBox box = new JFancyBox(frame, content);
					box.setVisible(true);
				} catch (Exception e2) {
					throw new RuntimeException(e2);
				}
			}
		});

		return profileButton;
	}

	protected void refreshExample() {
		examplePanel.removeAll();
		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(3, 3, 3, 3);
		examplePanel.add(new JLabel(new ImageIcon(createShadowedImage())), c);
		c.gridy++;
		c.anchor = GridBagConstraints.WEST;
		examplePanel.revalidate();
		examplePanel.repaint();
	}

	private BufferedImage createShadowedImage() {
		if (srcImage == null)
			srcImage = createTestImage();

		Dimension size = new Dimension(srcImage.getWidth(),
				srcImage.getHeight());
		SpinnerNumberModel model = (SpinnerNumberModel) kernelSizeSpinner
				.getModel();
		Number max = (Number) model.getMaximum();
		size.width += 2 * max.intValue() + 2 * offsetSlider.getMaximum();
		size.height += 2 * max.intValue() + 2 * offsetSlider.getMaximum();

		BufferedImage returnValue = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = returnValue.createGraphics();
		g.translate(size.width / 2 - srcImage.getWidth() / 2,
				size.height / 2 - srcImage.getHeight() / 2);

		ShadowRenderer renderer;
		if (rendererComboBox.getSelectedIndex() == 0) {
			renderer = new BoxShadowRenderer();
		} else if (rendererComboBox.getSelectedIndex() == 1) {
			renderer = new DoubleBoxShadowRenderer();
		} else {
			renderer = new GaussianShadowRenderer();
		}

		float opacity = (float) (opacitySlider.getValue()) / 100f;

		Number k1 = (Number) kernelSizeSpinner.getValue();
		ShadowAttributes attr = new ShadowAttributes(k1.floatValue(), opacity);
		float k = renderer.getKernel(attr).getKernelRadius();
		BufferedImage shadow = renderer.createShadow(srcImage, attr);
		double theta = ((double) angleSlider.getValue()) * Math.PI / 180.0;
		double dx = offsetSlider.getValue() * Math.cos(theta);
		double dy = offsetSlider.getValue() * Math.sin(theta);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(dx - k, dy - k);
		g2.drawImage(shadow, 0, 0, null);
		g2.dispose();
		g.drawImage(srcImage, 0, 0, null);
		g.dispose();

		return returnValue;
	}

	@Override
	public String getTitle() {
		return "Shadow Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a couple of options for rendering shadows.";
	}

	@Override
	public URL getHelpURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "shadow", "gaussian", "blur", "image" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { ShadowRenderer.class, ShadowAttributes.class,
				BoxShadowRenderer.class, GaussianShadowRenderer.class };
	}

}
