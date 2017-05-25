package com.jspring.utils;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.jspring.Exceptions;
import com.jspring.io.Files;

public class ImageHelper {

	private ImageHelper() {
	}

	public static void scaleImage(String sourceImageFilename, String targetImageFilename, int width, int height) {
		//log.info("Scale Image: " + sourceImageFilename + " => [" + width + "x" + height + "] " + targetImageFilename);
		try {
			if (!Files.isExist(sourceImageFilename)) {
				throw Exceptions.newInstance("Source file not exists: " + sourceImageFilename);
			}
			BufferedImage Bi = ImageIO.read(new File(sourceImageFilename));
			Image Itemp = Bi.getScaledInstance(width, height, Image.SCALE_FAST);
			double Ratio = 0.0;
			if ((Bi.getHeight() > width) || (Bi.getWidth() > height)) {
				if (Bi.getHeight() > Bi.getWidth())
					Ratio = (double) width / Bi.getHeight();
				else
					Ratio = (double) height / Bi.getWidth();
			}
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(Ratio, Ratio), null);
			Itemp = op.filter(Bi, null);
			String extension = targetImageFilename.substring(targetImageFilename.lastIndexOf('.') + 1);
			ImageIO.write((BufferedImage) Itemp, extension, new File(targetImageFilename));
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}
}
