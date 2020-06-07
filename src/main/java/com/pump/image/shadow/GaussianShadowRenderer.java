package com.pump.image.shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This renderer uses a Gaussian kernel to blur a shadow.
 */
public class GaussianShadowRenderer implements ShadowRenderer {

	static class Renderer {
		class VerticalPass implements Callable<Void> {
			int passX1, passX2;

			public VerticalPass(int passX1, int passX2) {
				this.passX1 = passX1;
				this.passX2 = passX2;
			}

			@Override
			public Void call() {
				int maxSum = kernelSum * 255;
				for (int dstX = passX1; dstX < passX2; dstX++) {
					int srcX = dstX - k;
					int prevSum = -1;
					for (int dstY = 0; dstY < dstHeight; dstY++) {
						int srcY = dstY - k;
						int g = srcY - k;

						int z = srcX + (g + kernel.length - 1) * srcWidth;
						int w;
						if (z >= 0 && z < srcBuffer.length) {
							w = (srcBuffer[z] >>> 24);
						} else {
							w = 0;
						}
						if (prevSum == 0 && w == 0) {
							// leave w as 0
						} else if (prevSum == maxSum && w == 255) {
							// leave w as 255
						} else {
							w = w * kernel[kernel.length - 1];
							for (int j = 0; j < kernel.length - 1; j++) {
								int kernelY = g + j;
								if (kernelY >= 0 && kernelY < srcHeight) {
									w += (srcBuffer[srcX
											+ kernelY * srcWidth] >>> 24)
											* kernel[j];
								}
							}
							prevSum = w;
							w = w / kernelSum;
						}
						dstBuffer[dstY * dstWidth + dstX] = w;
					}
				}
				return null;
			}
		}

		class HorizontalPass implements Callable<Void> {
			int passY1, passY2;

			public HorizontalPass(int passY1, int passY2) {
				this.passY1 = passY1;
				this.passY2 = passY2;
			}

			@Override
			public Void call() {
				int[] row = new int[dstWidth];
				int maxSum = kernelSum * 255;

				for (int dstY = passY1; dstY < passY2; dstY++) {
					System.arraycopy(dstBuffer, dstY * dstWidth, row, 0,
							row.length);
					int prevSum = -1;
					for (int dstX = 0; dstX < dstWidth; dstX++) {
						int z = dstX - k + kernel.length - 1;
						int w;
						if (z >= 0 && z < row.length) {
							w = row[z];
						} else {
							w = 0;
						}
						if (prevSum == 0 && w == 0) {
							// leave w as 0
						} else if (prevSum == maxSum && w == 255) {
							// leave w as 255
						} else {
							w = w * kernel[kernel.length - 1];
							for (int j = 0; j < kernel.length - 1; j++) {
								int kernelX = dstX - k + j;
								if (kernelX >= 0 && kernelX < dstWidth) {
									w += row[kernelX] * kernel[j];
								}
							}
							prevSum = w;
							w = w / kernelSum;
						}
						dstBuffer[dstY * dstWidth
								+ dstX] = opacityLookup[w] << 24;
					}
				}
				return null;
			}
		}

		static ExecutorService executor = Executors.newCachedThreadPool();

		final int k;
		final int srcWidth, srcHeight, dstWidth, dstHeight;
		volatile int[] dstBuffer;
		final int[] srcBuffer;
		final int[] kernel;
		final int kernelSum;
		int[] opacityLookup = new int[256];

		public Renderer(ARGBPixels srcPixels, ARGBPixels dstPixels,
				ShadowAttributes attr) {
			k = attr.getShadowKernelSize();
			int shadowSize = k * 2;

			srcWidth = srcPixels.getWidth();
			srcHeight = srcPixels.getHeight();

			dstWidth = srcWidth + shadowSize;
			dstHeight = srcHeight + shadowSize;

			dstBuffer = dstPixels.getPixels();
			srcBuffer = srcPixels.getPixels();

			GaussianKernel kernel = new GaussianKernel(k);
			this.kernel = kernel.getArray();
			kernelSum = kernel.getArraySum();

			float opacity = attr.getShadowOpacity();
			for (int a = 0; a < opacityLookup.length; a++) {
				opacityLookup[a] = (int) (a * opacity);
			}
		}

		public void run() throws InterruptedException {
			int x1 = k;
			int x2 = k + srcWidth;

			int clusterSize = 16;
			List<VerticalPass> verticalPasses = new ArrayList<>(
					(x2 - x1) / clusterSize + 1);
			for (int x = x1; x < x2; x += clusterSize) {
				int myClusterSize = Math.min(clusterSize, x2 - x);
				verticalPasses.add(new VerticalPass(x, x + myClusterSize));
			}

			executor.invokeAll(verticalPasses);

			List<HorizontalPass> horizontalPasses = new ArrayList<>(
					dstHeight / clusterSize + 1);
			for (int y = 0; y < dstHeight; y += clusterSize) {
				int myClusterSize = Math.min(clusterSize, dstHeight - y);
				horizontalPasses.add(new HorizontalPass(y, y + myClusterSize));
			}

			executor.invokeAll(horizontalPasses);
		}
	}

	@Override
	public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
			ShadowAttributes attr) {
		int k = attr.getShadowKernelSize();
		if (dst == null)
			dst = new ARGBPixels(src.getWidth() + 2 * k,
					src.getHeight() + 2 * k);
		Renderer r = new Renderer(src, dst, attr);
		try {
			r.run();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return dst;
	}
}