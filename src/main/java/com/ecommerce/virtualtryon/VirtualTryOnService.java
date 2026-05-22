package com.ecommerce.virtualtryon;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Virtual Try-On Service - OpenCV-based clothing/accessory visualization
 */
public class VirtualTryOnService {
    private static final Logger logger = LoggerFactory.getLogger(VirtualTryOnService.class);
    private static VirtualTryOnService instance;
    private VideoCapture camera;
    private Mat frame;
    private boolean cameraActive = false;

    static {
        try {
            nu.pattern.OpenCV.loadLocally();
        } catch (Exception e) {
            logger.warn("OpenCV not available, virtual try-on disabled");
        }
    }

    private VirtualTryOnService() {}

    public static synchronized VirtualTryOnService getInstance() {
        if (instance == null) {
            instance = new VirtualTryOnService();
        }
        return instance;
    }

    /**
     * Initialize camera
     */
    public boolean initializeCamera() {
        try {
            camera = new VideoCapture(0);
            if (camera.isOpened()) {
                cameraActive = true;
                logger.info("Camera initialized successfully");
                return true;
            }
        } catch (Exception e) {
            logger.error("Error initializing camera", e);
        }
        return false;
    }

    /**
     * Get camera frame
     */
    public Image getCameraFrame() {
        if (!cameraActive || camera == null || !camera.isOpened()) {
            return null;
        }

        try {
            frame = new Mat();
            if (camera.read(frame)) {
                return matToImage(frame);
            }
        } catch (Exception e) {
            logger.error("Error capturing frame", e);
        }
        return null;
    }

    /**
     * Apply clothing overlay on image
     */
    public Image applyClothingOverlay(String imagePath, String clothingImagePath, double x, double y, double width, double height) {
        try {
            Mat image = Imgcodecs.imread(imagePath);
            Mat clothingImage = Imgcodecs.imread(clothingImagePath);

            if (image.empty() || clothingImage.empty()) {
                logger.error("Failed to load images");
                return null;
            }

            // Resize clothing image
            Mat resized = new Mat();
            Imgproc.resize(clothingImage, resized, new Size(width, height));

            // Apply alpha blending if clothing has transparency
            Mat result = image.clone();
            blendImages(result, resized, (int)x, (int)y);

            logger.info("Clothing overlay applied");
            return matToImage(result);
        } catch (Exception e) {
            logger.error("Error applying clothing overlay", e);
        }
        return null;
    }

    /**
     * Apply glasses overlay
     */
    public Image applyGlassesOverlay(String imagePath, String glassesImagePath, double eyeX, double eyeY) {
        try {
            Mat image = Imgcodecs.imread(imagePath);
            Mat glassesImage = Imgcodecs.imread(glassesImagePath);

            if (image.empty() || glassesImage.empty()) {
                logger.error("Failed to load images");
                return null;
            }

            // Resize glasses to fit face
            Mat resized = new Mat();
            Imgproc.resize(glassesImage, resized, new Size(image.width() / 3, image.height() / 6));

            // Position at eye level
            Mat result = image.clone();
            blendImages(result, resized, (int)(eyeX - resized.width() / 2), (int)(eyeY - resized.height() / 2));

            logger.info("Glasses overlay applied");
            return matToImage(result);
        } catch (Exception e) {
            logger.error("Error applying glasses overlay", e);
        }
        return null;
    }

    /**
     * Apply shoe overlay
     */
    public Image applyShoeOverlay(String imagePath, String shoeImagePath, double footX, double footY) {
        try {
            Mat image = Imgcodecs.imread(imagePath);
            Mat shoeImage = Imgcodecs.imread(shoeImagePath);

            if (image.empty() || shoeImage.empty()) {
                logger.error("Failed to load images");
                return null;
            }

            // Resize shoe to fit foot area
            Mat resized = new Mat();
            Imgproc.resize(shoeImage, resized, new Size(image.width() / 4, image.height() / 5));

            // Position at foot level
            Mat result = image.clone();
            blendImages(result, resized, (int)(footX - resized.width() / 2), (int)(footY - resized.height() / 2));

            logger.info("Shoe overlay applied");
            return matToImage(result);
        } catch (Exception e) {
            logger.error("Error applying shoe overlay", e);
        }
        return null;
    }

    /**
     * Apply accessory overlay
     */
    public Image applyAccessoryOverlay(String imagePath, String accessoryImagePath, double x, double y, double rotation) {
        try {
            Mat image = Imgcodecs.imread(imagePath);
            Mat accessoryImage = Imgcodecs.imread(accessoryImagePath);

            if (image.empty() || accessoryImage.empty()) {
                logger.error("Failed to load images");
                return null;
            }

            // Apply rotation
            Mat rotationMatrix = Imgproc.getRotationMatrix2D(
                    new Point(accessoryImage.width() / 2, accessoryImage.height() / 2),
                    rotation, 1.0);
            Mat rotated = new Mat();
            Imgproc.warpAffine(accessoryImage, rotated, rotationMatrix, accessoryImage.size());

            // Blend accessory
            Mat result = image.clone();
            blendImages(result, rotated, (int)x, (int)y);

            logger.info("Accessory overlay applied");
            return matToImage(result);
        } catch (Exception e) {
            logger.error("Error applying accessory overlay", e);
        }
        return null;
    }

    /**
     * Release camera resources
     */
    public void releaseCamera() {
        try {
            if (camera != null && camera.isOpened()) {
                camera.release();
                cameraActive = false;
                logger.info("Camera released");
            }
        } catch (Exception e) {
            logger.error("Error releasing camera", e);
        }
    }

    // Private helper methods
    private void blendImages(Mat destination, Mat source, int x, int y) {
        try {
            int xEnd = Math.min(x + source.width(), destination.width());
            int yEnd = Math.min(y + source.height(), destination.height());
            
            if (x >= 0 && y >= 0 && x < destination.width() && y < destination.height()) {
                Mat roi = destination.submat(y, yEnd, x, xEnd);
                Mat srcRoi = source.submat(0, yEnd - y, 0, xEnd - x);
                Core.addWeighted(roi, 0.7, srcRoi, 0.3, 0, roi);
            }
        } catch (Exception e) {
            logger.error("Error blending images", e);
        }
    }

    private Image matToImage(Mat mat) {
        try {
            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".jpg", mat, buffer);
            byte[] data = buffer.toArray();
            return new Image(new java.io.ByteArrayInputStream(data));
        } catch (Exception e) {
            logger.error("Error converting Mat to Image", e);
        }
        return null;
    }
}
