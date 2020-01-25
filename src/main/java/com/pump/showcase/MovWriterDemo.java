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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.animation.quicktime.JPEGMovWriter;
import com.pump.animation.quicktime.MovWriter;
import com.pump.animation.quicktime.PNGMovWriter;
import com.pump.icon.file.FileIcon;
import com.pump.swing.AnimationController;
import com.pump.swing.FileDialogUtils;
import com.pump.swing.PartialLineBorder;
import com.pump.util.list.ObservableList;

/**
 * A minimal UI that creates .mov files using the MovWriter classes.
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/samples/MovWriterApp/sample.png"
 * alt="new&#160;com.pump.qt.io.MovWriterApp(&#160;)"> <!-- ======== END OF
 * AUTOGENERATED SAMPLES ======== -->
 * 
 * <a href=
 * "https://javagraphics.blogspot.com/2008/06/movies-writing-mov-files-without.html"
 * >Movies: Writing MOV Files Without QuickTime</a>
 */
public class MovWriterDemo extends JPanel {
	private static final long serialVersionUID = 1L;

	class MoviePainter {
		public void paintFrame(Graphics g0, int w, int h, int frame) {
			Graphics2D g = (Graphics2D) g0.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			Random random = new Random(0);
			g.setColor(Color.white);
			g.fillRect(0, 0, w, h);
			Point2D center = new Point2D.Float(w / 2, h / 2);
			double r = 100;
			double theta = 0;
			float hue1 = .5f;
			float hueAnchor1 = .7f;
			float hue2 = .75f;
			float hueAnchor2 = .4f;
			g.setColor(Color.gray);
			float visibleFrameLimit = 20;

			Point2D lastCursor = null;

			Point2D cursor = new Point2D.Double(center.getX(), center.getY());
			Point2D lastZ = null;
			g.rotate(((double) frame) / 50.0, center.getX(), center.getY());
			for (double a = 0; a < frame; a++) {
				r += 20 * Math.cos(4 + 2 * Math.cos(.5 + a / 10.0));
				r = Math.min(r, Math.min(w / 2, h / 2));
				r = Math.max(20, r);
				theta += Math.cos(2 + 2 * Math.sin(a / 2.0)) * .25;
				hueAnchor1 += (float) ((random.nextDouble() - .5) * .2);
				hueAnchor2 += (float) ((random.nextDouble() - .5) * .2);

				Point2D anchor = new Point2D.Double(center.getX() + r
						* Math.cos(theta), center.getY() + r * Math.sin(theta));
				hue1 = .7f * hue1 + hueAnchor1 * .3f;
				hue2 = .7f * hue2 + hueAnchor2 * .3f;

				float opacity = (float) Math.max(0, a - (frame - 1)
						+ visibleFrameLimit);
				opacity = opacity / visibleFrameLimit;

				cursor = new Point2D.Double(cursor.getX() * .8 + anchor.getX()
						* .2, cursor.getY() * .8 + anchor.getY() * .2);

				if (opacity > .001) {
					g.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, opacity));

					if (lastCursor != null) {
						for (int b = 0; b < 1000; b++) {
							Point2D z = tween(cursor, lastCursor,
									((float) b) / 1000f);
							if (lastZ == null || lastZ.distance(z) > 2) {
								paintSymmetryDot((Graphics2D) g.create(), z,
										w / 2, h / 2, hue1, hue2);
								lastZ = z;
							}
						}
					} else {
						paintSymmetryDot((Graphics2D) g.create(), cursor,
								w / 2, h / 2, hue1, hue2);
					}
				}

				lastCursor = cursor;
			}
			g.dispose();
		}

		private Point2D tween(Point2D p1, Point2D p2, float f) {
			return new Point2D.Double(p1.getX() * f + p2.getX() * (1 - f),
					p1.getY() * f + p2.getY() * (1 - f));
		}

		private void paintSymmetryDot(Graphics2D g, Point2D p, int x, int y,
				float hue1, float hue2) {
			for (int k = 0; k < 32; k++) {
				if (k % 2 == 0)
					g.setColor(Color.getHSBColor(hue1, .8f, .8f));
				else
					g.setColor(Color.getHSBColor(hue2, .8f, .8f));
				g.rotate(1.0 / 32.0 * 2.0 * Math.PI, x, y);
				float r = (k % 4 >= 2) ? 2 : 3;
				g.fill(new Ellipse2D.Double(p.getX() - r, p.getY() - r, 2 * r,
						2 * r));
			}

		}
	}

	JPanel visualPanel = new JPanel(new GridBagLayout());
	JButton exportButton = new JButton("Export");
	JSpinner framesSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 50000,
			10));
	JSpinner fpsSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 30, 1));
	JComboBox formatComboBox = new JComboBox();
	JSpinner jpegQuality = new JSpinner(new SpinnerNumberModel(.9, .1, 1, .05));
	JCheckBox writeAudio = new JCheckBox("Write Remaining Audio");

	JPanel audioPanel = new JPanel(new GridBagLayout());
	ObservableList<File> audioFileList = new ObservableList<File>();
	JList audioFiles = new JList(audioFileList.createUIMirror(null));
	JButton addButton = new JButton("Add");
	AnimationController animationController = new AnimationController();
	JPanel animationPreview = new JPanel() {
		MoviePainter p = new MoviePainter();

		protected void paintComponent(Graphics g) {
			float time = animationController.getTime();
			int frame = (int) (time * ((Number) fpsSpinner.getValue())
					.intValue());
			p.paintFrame(g.create(), getWidth(), getHeight(), frame);

			g.setColor(Color.black);
			Font f = UIManager.getFont("Label.font");
			g.setFont(f);
			g.drawString("f = " + frame, 3, (int) (f.getSize2D() + 4));
			g.drawString("t = " + time, 3, (int) (2 * f.getSize2D() + 8));
		}
	};

	public MovWriterDemo() {
		visualPanel.setBorder(new TitledBorder("Visual Track"));
		audioPanel.setBorder(new TitledBorder("Audio Track"));

		PropertyChangeListener repaintListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				animationPreview.repaint();
			}
		};
		animationController.addPropertyChangeListener(
				AnimationController.TIME_PROPERTY, repaintListener);
		animationController.addPropertyChangeListener(
				AnimationController.PLAYING_PROPERTY, repaintListener);
		animationController.addPropertyChangeListener(
				AnimationController.DURATION_PROPERTY, repaintListener);

		ChangeListener updateAnimationChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				float fps = ((Number) fpsSpinner.getValue()).intValue();
				float frames = ((Number) framesSpinner.getValue()).intValue();
				animationController.setDuration(frames / fps);
			}
		};
		framesSpinner.addChangeListener(updateAnimationChangeListener);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(7, 7, 7, 7);
		visualPanel.add(new JLabel("Frames:"), c);
		c.gridy++;
		visualPanel.add(new JLabel("FPS:"), c);
		c.gridy++;
		visualPanel.add(new JLabel("Format:"), c);
		c.gridy++;
		visualPanel.add(new JLabel("Quality:"), c);
		c.anchor = GridBagConstraints.WEST;
		c.gridx++;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		visualPanel.add(framesSpinner, c);
		c.gridy++;
		visualPanel.add(fpsSpinner, c);
		c.gridy++;
		visualPanel.add(formatComboBox, c);
		c.gridy++;
		visualPanel.add(jpegQuality, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		audioPanel.add(new JScrollPane(audioFiles), c);
		c.gridy++;
		c.weighty = 0;
		audioPanel.add(writeAudio, c);
		c.gridy++;
		audioPanel.add(addButton, c);

		JPanel animationPreviewContainer = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		animationPreviewContainer.add(animationPreview, c);
		c.gridy++;
		c.weighty = 0;
		animationPreviewContainer.add(animationController, c);
		animationPreview.setBorder(new PartialLineBorder(Color.gray,
				new Insets(1, 1, 0, 1)));
		animationPreview.setPreferredSize(new Dimension(400, 300));

		c = new GridBagConstraints();
		setLayout(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 5, 5, 5);
		add(animationPreviewContainer, c);

		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTH;

		c.gridheight = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 10, 10, 10);
		add(audioPanel, c);

		c.gridheight = 1;
		c.gridx++;
		add(visualPanel, c);

		c.gridy++;
		add(exportButton, c);

		formatComboBox.addItem("JPEG");
		formatComboBox.addItem("PNG");

		formatComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSlider();
			}
		});

		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread thread = new Thread() {
					@Override
					public void run() {
						export();
					}
				};
				thread.start();
			}
		});

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addAudio();
			}
		});

		audioFiles.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value,
						index, isSelected, cellHasFocus);
				if (c instanceof JLabel && value instanceof File) {
					File file = (File) value;
					JLabel label = (JLabel) c;
					label.setText(file.getName());
					Icon icon = FileIcon.get().getIcon(file);
					if (icon == null)
						icon = UIManager.getIcon("FileView.fileIcon");
					label.setIcon(icon);
				}
				return c;
			}

		});

		jpegQuality
				.setToolTipText("Define the JPEG compression quality of the visual track.");
		fpsSpinner.setToolTipText("Define the number of frames per second.");
		framesSpinner.setToolTipText("Define the number of frames.");
	}

	private void updateSlider() {
		jpegQuality.setEnabled(formatComboBox.getSelectedIndex() == 0);
	}

	public void addAudio() {
		Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
		File file = FileDialogUtils.showOpenDialog(frame, "Open Audio", "wav",
				"aif");
		if (file == null)
			return;
		audioFileList.add(file);
	}

	public void export() {
		Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
		File file = FileDialogUtils.showSaveDialog(frame, "Save MOV", "mov");
		if (file == null)
			return;

		final JDialog progressDialog = new JDialog(frame, "Progress");

		JProgressBar progressBar = new JProgressBar();
		JButton cancelButton = new JButton("Cancel");
		JLabel label = new JLabel("Writing...");
		label.setHorizontalAlignment(SwingConstants.LEFT);

		progressDialog.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(12, 12, 12, 12);
		c.anchor = GridBagConstraints.WEST;
		progressDialog.getContentPane().add(label, c);
		c.gridy++;
		c.insets.top = 0;
		progressDialog.getContentPane().add(progressBar, c);
		c.gridy++;
		c.anchor = GridBagConstraints.SOUTHEAST;
		progressDialog.getContentPane().add(cancelButton, c);

		Dimension d = progressBar.getPreferredSize();
		d.width = 300;
		progressBar.setPreferredSize(d);

		progressDialog.pack();
		progressDialog.setLocationRelativeTo(null);

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				progressDialog.getRootPane().putClientProperty("cancelled",
						Boolean.TRUE);
			}
		});

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressDialog.setVisible(true);
			}
		});

		long time = System.currentTimeMillis();
		MovWriter anim = null;
		int framesWritten = 0;

		BufferedImage bi = new BufferedImage(640, 480,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		g.dispose();

		int frameCount = ((Number) framesSpinner.getValue()).intValue();
		int fps = ((Number) fpsSpinner.getValue()).intValue();

		try {
			if (formatComboBox.getSelectedIndex() == 0) {
				float quality = ((Number) jpegQuality.getValue()).floatValue();
				anim = new JPEGMovWriter(file, quality);
			} else if (formatComboBox.getSelectedIndex() == 1) {
				anim = new PNGMovWriter(file);
			}
			for (int a = 0; a < audioFileList.size(); a++) {
				File audioFile = audioFileList.get(a);
				AudioInputStream audioIn = AudioSystem
						.getAudioInputStream(audioFile);
				anim.addAudioTrack(audioIn, 0);
			}
			float frameDuration = 1f / (fps);
			progressBar.setMaximum(frameCount);

			for (; framesWritten < frameCount; framesWritten++) {
				if (progressDialog.isVisible() == false
						|| Boolean.TRUE.equals(progressDialog.getRootPane()
								.getClientProperty("cancelled"))) {
					throw new RuntimeException("cancelled");
				}
				progressBar.setValue(framesWritten);
				label.setText("Writing Frame " + (framesWritten + 1) + "...");

				MoviePainter p = new MoviePainter();
				Graphics2D g2 = bi.createGraphics();
				p.paintFrame(g2, bi.getWidth(), bi.getHeight(), framesWritten);
				g2.dispose();

				anim.addFrame(frameDuration, bi, null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (anim != null) {
				try {
					anim.close(framesWritten == frameCount
							&& writeAudio.isSelected());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			progressDialog.setVisible(false);
		}
		time = System.currentTimeMillis() - time;
		System.out.println("wrote " + framesWritten + " frames: "
				+ file.getAbsolutePath() + " (" + (time) / 1000.0 + " s)");
	}
}