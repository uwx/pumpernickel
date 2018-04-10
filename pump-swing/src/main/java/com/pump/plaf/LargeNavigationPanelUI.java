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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;

import com.pump.blog.ResourceSample;
import com.pump.icon.FirstIcon;
import com.pump.icon.LastIcon;
import com.pump.icon.TriangleIcon;
import com.pump.swing.NavigationButtons;

/**
 * This NavigationPanelUI contains large buttons and a slider.
 * <p>
 * If the spinner is not implemented as a SpinnerNumberModel (which guarantees a
 * min and a max), then the slider, first and last buttons are all made
 * invisible. A label is added instead to describe the current selection.
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p>
 * Here are some samples:
 * <table summary="Resource&#160;Samples&#160;for&#160;com.pump.plaf.LargeNavigationPanelUI">
 * <tr>
 * <td></td>
 * <td><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/samples/LargeNavigationPanelUI/SpinnerNumberModel.png"
 * alt="com.pump.plaf.LargeNavigationPanelUI.createDemo(true)"></td>
 * <td><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/samples/LargeNavigationPanelUI/Any Other Model.png"
 * alt="com.pump.plaf.LargeNavigationPanelUI.createDemo(false)"></td>
 * </tr>
 * <tr>
 * <td></td>
 * <td>SpinnerNumberModel</td>
 * <td>Any Other Model</td>
 * </tr>
 * <tr>
 * </tr>
 * </table>
 * <!-- ======== END OF AUTOGENERATED SAMPLES ======== -->
 */
@ResourceSample(sample = {
		"com.pump.plaf.LargeNavigationPanelUI.createDemo(true)",
		"com.pump.plaf.LargeNavigationPanelUI.createDemo(false)" }, names = {
		"SpinnerNumberModel", "Any Other Model" })
public class LargeNavigationPanelUI extends NavigationPanelUI {

	/** Create a minimal demo for the javadoc. */
	public static JSpinner createDemo(boolean useNumbers) {
		JSpinner spinner;
		if (useNumbers) {
			spinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
		} else {
			spinner = new JSpinner(new SpinnerListModel(new Object[] { "Red",
					"Green", "Blue" }));
		}
		spinner.setUI(new LargeNavigationPanelUI());
		spinner.setBorder(new EmptyBorder(5, 5, 5, 5));
		return spinner;
	}

	protected static final String SLIDER_NAME = "Spinner.slider";
	protected static final String FIRST_BUTTON_NAME = "Spinner.firstButton";
	protected static final String LAST_BUTTON_NAME = "Spinner.lastButton";

	private int sliderAdjusting = 0;

	protected AbstractButton firstButton, lastButton;

	@Override
	protected void updateEnabledState(boolean isSpinnerEnabled,
			boolean hasPreviousValue, boolean hasNextValue) {
		super.updateEnabledState(isSpinnerEnabled, hasPreviousValue,
				hasNextValue);
		getComponent(spinner, FIRST_BUTTON_NAME).setEnabled(
				isSpinnerEnabled && hasPreviousValue);
		getComponent(spinner, LAST_BUTTON_NAME).setEnabled(
				isSpinnerEnabled && hasNextValue);
		getComponent(spinner, SLIDER_NAME).setEnabled(isSpinnerEnabled);

	}

	@Override
	protected Component createPreviousButton() {
		AbstractButton button = NavigationButtons.createPrev();
		format(button);
		button.setIcon(new TriangleIcon(SwingConstants.WEST, 24, 24,
				Color.lightGray));
		button.setRolloverIcon(new TriangleIcon(SwingConstants.WEST, 24, 24,
				Color.white));
		button.setDisabledIcon(new TriangleIcon(SwingConstants.WEST, 24, 24,
				Color.darkGray));
		button.setName(PREV_BUTTON_NAME);
		installPreviousButtonListeners(button);
		return button;
	}

	@Override
	protected Component createNextButton() {
		AbstractButton button = NavigationButtons.createNext();
		format(button);
		button.setIcon(new TriangleIcon(SwingConstants.EAST, 24, 24,
				Color.lightGray));
		button.setRolloverIcon(new TriangleIcon(SwingConstants.EAST, 24, 24,
				Color.white));
		button.setDisabledIcon(new TriangleIcon(SwingConstants.EAST, 24, 24,
				Color.darkGray));
		button.setName(NEXT_BUTTON_NAME);
		installNextButtonListeners(button);
		return button;
	}

	protected JSlider createSlider() {
		JSlider slider = new JSlider(0, 1000);
		slider.setOpaque(false);
		slider.setName(SLIDER_NAME);
		installSliderListener(slider);
		return slider;
	}

	protected void updateSliderState(JSlider slider) {
		sliderAdjusting++;
		try {
			boolean useSlider = false;
			if (spinner.getModel() instanceof SpinnerNumberModel) {
				useSlider = true;
				SpinnerNumberModel numberModel = (SpinnerNumberModel) spinner
						.getModel();
				Number min = (Number) numberModel.getMinimum();
				Number max = (Number) numberModel.getMaximum();
				float range = max.floatValue() - min.floatValue();
				float pos = (((Number) numberModel.getValue()).floatValue() - min
						.floatValue()) / range;
				int sliderRange = slider.getMaximum() - slider.getMinimum();
				int sliderValue = (int) (pos * sliderRange + slider
						.getMinimum());
				slider.setValue(sliderValue);
			}

			if (useSlider != slider.isVisible())
				slider.setVisible(useSlider);
			if (useSlider != firstButton.isVisible())
				firstButton.setVisible(useSlider);
			if (useSlider != lastButton.isVisible())
				lastButton.setVisible(useSlider);

			if (useSlider == getLabel().isVisible())
				getLabel().setVisible(!useSlider);
		} finally {
			sliderAdjusting--;
		}
	}

	protected void installSliderListener(final JSlider slider) {
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateSliderState(slider);

			}
		});
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (sliderAdjusting > 0)
					return;
				float sliderRange = slider.getMaximum() - slider.getMinimum();
				float pos = slider.getValue();
				pos = (pos - slider.getMinimum()) / sliderRange;

				SpinnerNumberModel numberModel = (SpinnerNumberModel) spinner
						.getModel();
				Number min = (Number) numberModel.getMinimum();
				Number max = (Number) numberModel.getMaximum();
				float range = max.floatValue() - min.floatValue();
				float spinnerPos = pos * range + min.floatValue();

				if (min instanceof Integer && max instanceof Integer) {
					spinner.setValue(Integer.valueOf((int) (spinnerPos + .5)));
				} else {
					spinner.setValue(Float.valueOf(spinnerPos));
				}
			}
		});
	}

	protected AbstractButton createFirstButton() {
		AbstractButton button = new JButton();
		format(button);
		button.setIcon(new FirstIcon(2, 24, 24, Color.lightGray));
		button.setRolloverIcon(new FirstIcon(2, 24, 24, Color.white));
		button.setDisabledIcon(new FirstIcon(2, 24, 24, Color.darkGray));
		button.setName("Spinner.firstButton");
		installFirstButtonListeners(button);
		return button;
	}

	protected AbstractButton createLastButton() {
		AbstractButton button = new JButton();
		format(button);
		button.setIcon(new LastIcon(2, 24, 24, Color.lightGray));
		button.setRolloverIcon(new LastIcon(2, 24, 24, Color.white));
		button.setDisabledIcon(new LastIcon(2, 24, 24, Color.darkGray));
		button.setName("Spinner.lastButton");
		installLastButtonListeners(button);
		return button;
	}

	protected void installFirstButtonListeners(final AbstractButton button) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SpinnerNumberModel model = (SpinnerNumberModel) spinner
						.getModel();
				model.setValue(model.getMinimum());
			}
		});
	}

	protected void installLastButtonListeners(final AbstractButton button) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SpinnerNumberModel model = (SpinnerNumberModel) spinner
						.getModel();
				model.setValue(model.getMaximum());
			}
		});
	}

	@Override
	protected LayoutManager createLayout() {
		return new LargeLayeredLayout();
	}

	@Override
	public void paint(Graphics g0, JComponent c) {
		Graphics2D g = (Graphics2D) g0.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		Shape roundRect = new RoundRectangle2D.Float(0, 0, c.getWidth(),
				c.getHeight(), 9, 9);
		g.setColor(new Color(30, 30, 30, 200));
		g.fill(roundRect);
		g.dispose();
	}

	@Override
	protected JComponent createEditor() {
		return null;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		JSlider slider = createSlider();

		firstButton = createFirstButton();
		lastButton = createLastButton();
		maybeAdd(firstButton, "First");
		maybeAdd(lastButton, "Last");
		maybeAdd(slider, "Slider");
		updateSliderState(slider);
		getLabel().setUI(
				new EmphasizedLabelUI(new Color(0xeeeeee), new Color(0xeeeeee),
						new Color(0x444444)));
	}

	protected void format(AbstractButton button) {
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setUI(new BasicButtonUI());
	}

	static class LargeLayeredLayout implements LayoutManager {

		Map<String, Component> constraintMap = new HashMap<>();
		Insets buttonInsets = new Insets(3, 3, 3, 3);
		Insets labelInsets = new Insets(3, 3, 3, 3);
		Insets masterInsets = new Insets(3, 3, 3, 3);

		@Override
		public void addLayoutComponent(String name, Component component) {
			constraintMap.put(name, component);
		}

		@Override
		public void removeLayoutComponent(Component comp) {
			constraintMap.values().remove(comp);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			Component prev = constraintMap.get("Previous");
			Component next = constraintMap.get("Next");
			Component first = constraintMap.get("First");
			Component last = constraintMap.get("Last");
			Component slider = constraintMap.get("Slider");
			Component label = constraintMap.get("Label");

			int width = masterInsets.left + masterInsets.right;
			int height = 0;
			if (first != null && first.isVisible()) {
				Dimension firstD = first.getPreferredSize();
				width += firstD.width + buttonInsets.left + buttonInsets.right;
				height = Math.max(firstD.height + buttonInsets.top
						+ buttonInsets.bottom, height);
			}
			if (prev != null && prev.isVisible()) {
				Dimension prevD = prev.getPreferredSize();
				width += prevD.width + buttonInsets.left + buttonInsets.right;
				height = Math.max(prevD.height + buttonInsets.top
						+ buttonInsets.bottom, height);
			}
			if (next != null && next.isVisible()) {
				Dimension nextD = next.getPreferredSize();
				width += nextD.width + buttonInsets.left + buttonInsets.right;
				height = Math.max(nextD.height + buttonInsets.top
						+ buttonInsets.bottom, height);
			}
			if (last != null && last.isVisible()) {
				Dimension lastD = last.getPreferredSize();
				width += lastD.width + buttonInsets.left + buttonInsets.right;
				height = Math.max(lastD.height + buttonInsets.top
						+ buttonInsets.bottom, height);
			}

			height += masterInsets.top + masterInsets.bottom;

			if (label != null && label.isVisible()) {
				Dimension labelD = label.getPreferredSize();
				height += labelD.height + labelInsets.top + labelInsets.bottom;
			}

			if (slider != null && slider.isVisible()) {
				Dimension editorD = slider.getPreferredSize();
				height += editorD.height;
			}
			return new Dimension(width, height);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return preferredLayoutSize(parent);
		}

		@Override
		public void layoutContainer(Container parent) {
			Component prev = constraintMap.get("Previous");
			Component next = constraintMap.get("Next");
			Component first = constraintMap.get("First");
			Component last = constraintMap.get("Last");
			Component slider = constraintMap.get("Slider");
			Component label = constraintMap.get("Label");

			int left = masterInsets.left;
			int x = left;
			int y = masterInsets.top + buttonInsets.top;
			int y2 = y;
			if (first != null && first.isVisible()) {
				Dimension firstD = first.getPreferredSize();
				x += buttonInsets.left;
				first.setBounds(x, y + buttonInsets.top, firstD.width,
						firstD.height);
				x += firstD.width + buttonInsets.right;
				y2 = Math.max(y2, y + firstD.height + buttonInsets.bottom);
			}
			if (prev != null && prev.isVisible()) {
				Dimension prevD = prev.getPreferredSize();
				x += buttonInsets.left;
				prev.setBounds(x, y + buttonInsets.top, prevD.width,
						prevD.height);
				x += prevD.width + buttonInsets.right;
				y2 = Math.max(y2, y + prevD.height + buttonInsets.bottom);
			}
			if (next != null && next.isVisible()) {
				Dimension nextD = next.getPreferredSize();
				x += buttonInsets.left;
				next.setBounds(x, y + buttonInsets.top, nextD.width,
						nextD.height);
				x += nextD.width + buttonInsets.right;
				y2 = Math.max(y2, y + nextD.height + buttonInsets.bottom);
			}
			if (last != null && last.isVisible()) {
				Dimension lastD = last.getPreferredSize();
				x += buttonInsets.left;
				last.setBounds(x, y + buttonInsets.top, lastD.width,
						lastD.height);
				x += lastD.width + buttonInsets.right;
				y2 = Math.max(y2, y + lastD.height + buttonInsets.bottom);
			}

			int innerWidth = parent.getWidth() - left - masterInsets.right;
			if (label != null && label.isVisible()) {
				y2 += labelInsets.top;
				Dimension labelD = label.getPreferredSize();
				labelD.width = Math.min(innerWidth - labelInsets.left
						- labelInsets.right, labelD.width);
				label.setBounds(parent.getWidth() / 2 - labelD.width / 2, y2,
						labelD.width, labelD.height);
				y2 += labelD.height + labelInsets.bottom;
			}
			if (slider != null && slider.isVisible()) {
				Dimension editorD = slider.getPreferredSize();
				slider.setBounds(left, y2, innerWidth, editorD.height);
			}
		}
	}
}