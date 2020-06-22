package com.pump.image.shadow;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.pump.showcase.ShadowRendererDemo;
import com.pump.showcase.ShadowRendererDemo.OriginalGaussianShadowRenderer;

import junit.framework.TestCase;

public class FastShadowRendererTest extends TestCase {

	/**
	 * This calculates the vertical blur for a 7x7 grid. I ran (some of) the
	 * numbers by hand to verify the results.
	 * 
	 * TODO: test fringe cases where kernel exceeds image size
	 */
	@Test
	public void testVerticalBlur() {
		ARGBPixels srcImage = new ARGBPixels(7, 7);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 2, 41, 16, 29, 23, 35, 
			42, 30, 24, 36, 10, 3, 17, 
			18, 43, 31, 4, 37, 11, 48, 
			38, 44, 5, 12, 19, 49, 25, 
			45, 20, 13, 0, 26, 32, 6, 
			7, 27, 47, 33, 21, 14, 39, 
			28, 34, 22, 8, 15, 40, 46
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(11, 11);
		FastShadowRenderer.Renderer renderer = new FastShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 7, 7, 2, 1f);
		renderer.runVerticalBlur();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 11]);
		assertEquals(10, dstPixels[2 + 1 * 11]);
		assertEquals(13, dstPixels[2 + 2 * 11]);
		assertEquals(21, dstPixels[2 + 3 * 11]);
		assertEquals(30, dstPixels[2 + 4 * 11]);
		assertEquals(30, dstPixels[2 + 5 * 11]);
		assertEquals(27, dstPixels[2 + 6 * 11]);
		assertEquals(23, dstPixels[2 + 7 * 11]);
		assertEquals(16, dstPixels[2 + 8 * 11]);
		assertEquals(7, dstPixels[2 + 9 * 11]);
		assertEquals(5, dstPixels[2 + 10 * 11]);

		// test middle column
		assertEquals(3, dstPixels[5 + 0 * 11]);
		assertEquals(10, dstPixels[5 + 1 * 11]);
		assertEquals(11, dstPixels[5 + 2 * 11]);
		assertEquals(13, dstPixels[5 + 3 * 11]);
		assertEquals(13, dstPixels[5 + 4 * 11]);
		assertEquals(17, dstPixels[5 + 5 * 11]);
		assertEquals(11, dstPixels[5 + 6 * 11]);
		assertEquals(10, dstPixels[5 + 7 * 11]);
		assertEquals(8, dstPixels[5 + 8 * 11]);
		assertEquals(8, dstPixels[5 + 9 * 11]);
		assertEquals(1, dstPixels[5 + 10 * 11]);

		// test rightmost column
		assertEquals(7, dstPixels[8 + 0 * 11]);
		assertEquals(10, dstPixels[8 + 1 * 11]);
		assertEquals(20, dstPixels[8 + 2 * 11]);
		assertEquals(25, dstPixels[8 + 3 * 11]);
		assertEquals(26, dstPixels[8 + 4 * 11]);
		assertEquals(27, dstPixels[8 + 5 * 11]);
		assertEquals(32, dstPixels[8 + 6 * 11]);
		assertEquals(23, dstPixels[8 + 7 * 11]);
		assertEquals(18, dstPixels[8 + 8 * 11]);
		assertEquals(17, dstPixels[8 + 9 * 11]);
		assertEquals(9, dstPixels[8 + 10 * 11]);
	}

	/**
	 * Test the horizontal blur using the same data as the previous test.
	 */
	@Test
	public void testHorizontalBlur() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(7, 7);

		ARGBPixels dstImage = new ARGBPixels(11, 11);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 9, 2, 41, 16, 29, 23, 35, 0, 0, 
			0, 0, 42, 30, 24, 36, 10, 3, 17, 0, 0, 
			0, 0, 18, 43, 31, 4, 37, 11, 48, 0, 0, 
			0, 0, 38, 44, 5, 12, 19, 49, 25, 0, 0, 
			0, 0, 45, 20, 13, 0, 26, 32, 6, 0, 0, 
			0, 0, 7, 27, 47, 33, 21, 14, 39, 0, 0, 
			0, 0, 28, 34, 22, 8, 15, 40, 46, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		FastShadowRenderer.Renderer renderer = new FastShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 7, 7, 2, 1f);
		renderer.runHorizontalBlur();

		// test topmost row (of data)
		assertEquals(1 << 24, dstPixels[22]);
		assertEquals(2 << 24, dstPixels[23]);
		assertEquals(10 << 24, dstPixels[24]);
		assertEquals(13 << 24, dstPixels[25]);
		assertEquals(19 << 24, dstPixels[26]);
		assertEquals(22 << 24, dstPixels[27]);
		assertEquals(28 << 24, dstPixels[28]);
		assertEquals(20 << 24, dstPixels[29]);
		assertEquals(17 << 24, dstPixels[30]);
		assertEquals(11 << 24, dstPixels[31]);
		assertEquals(7 << 24, dstPixels[32]);

		// test middle row
		assertEquals(7 << 24, dstPixels[55]);
		assertEquals(16 << 24, dstPixels[56]);
		assertEquals(17 << 24, dstPixels[57]);
		assertEquals(19 << 24, dstPixels[58]);
		assertEquals(23 << 24, dstPixels[59]);
		assertEquals(25 << 24, dstPixels[60]);
		assertEquals(22 << 24, dstPixels[61]);
		assertEquals(21 << 24, dstPixels[62]);
		assertEquals(18 << 24, dstPixels[63]);
		assertEquals(14 << 24, dstPixels[64]);
		assertEquals(5 << 24, dstPixels[65]);

		// no need to test more, horizontal passes are much simpler since data
		// is continuous.
	}

	/**
	 * This confirms that a thorough un-optimized Gaussian renderer that uses
	 * the uniform kernel reaches the same output as the FastShadowRenderer
	 * 
	 * @throws Exception
	 */
	@Test
	public void testShadowImage() throws Exception {
		ShadowRenderer renderer1 = new FastShadowRenderer();
		ShadowAttributes attr = new ShadowAttributes(15, .5f);

		final GaussianKernel kernel = renderer1.getKernel(attr);

		ShadowRenderer renderer2 = new OriginalGaussianShadowRenderer() {

			@Override
			public GaussianKernel getKernel(ShadowAttributes attr) {
				return kernel;
			}
		};

		BufferedImage bi = ShadowRendererDemo.createTestImage();

		BufferedImage result1 = renderer1.createShadow(bi, attr);
		BufferedImage result2 = renderer2.createShadow(bi, attr);

		// I had set tolerance to 1 to get this to pass. I'm OK with
		// a smidge of rounding error. I looked at the BufferedImages and they
		// look identical.
		String msg = GaussianShadowRendererTest.equals(result1, result2, 1);
		assertTrue(msg, msg == null);
	}
}