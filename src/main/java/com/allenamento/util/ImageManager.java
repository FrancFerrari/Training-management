package com.allenamento.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;

/**
 * Utility for managing image files.
 * Handles loading, copying, and deleting images on disk.
 */
public class ImageManager {
    private static final Logger logger = LoggerFactory.getLogger(ImageManager.class);
    private static final String IMAGES_SUBDIRECTORY = "data/images";
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp"};
    private static final Path APP_BASE_DIR;
    private static final Path IMAGES_DIRECTORY;

    static {
        // Determine the application's base directory
        APP_BASE_DIR = determineAppBaseDir();
        IMAGES_DIRECTORY = APP_BASE_DIR.resolve(IMAGES_SUBDIRECTORY);
        logger.info("Application base directory: " + APP_BASE_DIR);
        logger.info("Images directory: " + IMAGES_DIRECTORY);
        
        // Create images directory if it doesn't exist
        try {
            Files.createDirectories(IMAGES_DIRECTORY);
        } catch (IOException e) {
            logger.error("Error creating images directory", e);
        }
    }

    /**
     * Determines the application's base directory.
     * Tries to resolve from JAR path, otherwise uses CWD.
     */
    private static Path determineAppBaseDir() {
        try {
            // Get the path from which the code is executed
            java.net.URI codeSourceUri = ImageManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI();
            Path codePath = Paths.get(codeSourceUri);

            if (codePath.toString().endsWith(".jar")) {
                // JAR execution: JAR is in target/, go up to project root
                Path jarDir = codePath.getParent();
                if (jarDir != null && jarDir.getFileName() != null
                        && jarDir.getFileName().toString().equals("target")) {
                    return jarDir.getParent().toAbsolutePath();
                }
                return jarDir.toAbsolutePath();
            } else {
                // Execution from classes (e.g. from IDE or mvn exec): go up from target/classes
                Path classesDir = codePath;
                if (classesDir.getFileName() != null
                        && classesDir.getFileName().toString().equals("classes")) {
                    Path targetDir = classesDir.getParent();
                    if (targetDir != null && targetDir.getFileName() != null
                            && targetDir.getFileName().toString().equals("target")) {
                        return targetDir.getParent().toAbsolutePath();
                    }
                }
                return classesDir.toAbsolutePath();
            }
        } catch (Exception e) {
            logger.warn("Cannot determine directory from code source, using CWD", e);
        }
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    /**
     * Saves an image to the filesystem.
     *
     * @param sourceFile the source file to copy
     * @return the relative path of the saved file, null on error
     */
    public static String saveImage(File sourceFile) {
        if (sourceFile == null || !sourceFile.exists()) {
            logger.warn("Source file null or non-existent");
            return null;
        }

        if (!isValidImageFile(sourceFile)) {
            logger.warn("Invalid file: " + sourceFile.getName());
            return null;
        }

        try {
            // Generate a unique filename
            String fileName = generateUniqueFileName(sourceFile.getName());
            Path destinationPath = IMAGES_DIRECTORY.resolve(fileName);

            // Copy the file
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Image saved: " + destinationPath.toAbsolutePath());

            // Save the absolute path
            return destinationPath.toAbsolutePath().toString();
        } catch (IOException e) {
            logger.error("Error saving image", e);
            return null;
        }
    }

    /**
     * Deletes an image from the filesystem.
     *
     * @param imagePath the path of the image to delete
     * @return true if deletion succeeded
     */
    public static boolean deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            logger.warn("Image path null or empty");
            return false;
        }

        try {
            Path path = resolveImagePath(imagePath);
            if (path != null && Files.exists(path)) {
                Files.delete(path);
                logger.info("Image deleted: " + path);
                return true;
            } else {
                logger.warn("Image file not found: " + imagePath);
            }
        } catch (IOException e) {
            logger.error("Error deleting image", e);
        }
        return false;
    }

    /**
     * Resolves the path of an image, handling both absolute and relative paths.
     * Compatible with images saved with relative paths (old format)
     * and absolute paths (new format).
     *
     * @param imagePath the image path (absolute or relative)
     * @return the resolved Path, or null if not found
     */
    public static Path resolveImagePath(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        // Try the path as is (works if absolute or if CWD is correct)
        Path directPath = Paths.get(imagePath);
        if (Files.exists(directPath)) {
            return directPath;
        }

        // If it's a relative path, try to resolve it from app's base directory
        if (!directPath.isAbsolute()) {
            Path resolvedPath = APP_BASE_DIR.resolve(imagePath);
            if (Files.exists(resolvedPath)) {
                return resolvedPath;
            }
        }

        // Try to search only the filename in the images directory
        String fileName = Paths.get(imagePath).getFileName().toString();
        Path inImagesDir = IMAGES_DIRECTORY.resolve(fileName);
        if (Files.exists(inImagesDir)) {
            return inImagesDir;
        }

        logger.warn("Immagine non trovata in nessun percorso: " + imagePath);
        return null;
    }

    /**
     * Gets the resolved File of an image given its path from the database.
     *
     * @param imagePath the image path from the database
     * @return the image File, or null if not found
     */
    public static File resolveImageFile(String imagePath) {
        Path resolved = resolveImagePath(imagePath);
        if (resolved != null) {
            return resolved.toFile();
        }
        return null;
    }

    /**
     * Verifies if a file is a valid image.
     *
     * @param file the file to verify
     * @return true if it's a valid image
     */
    private static boolean isValidImageFile(File file) {
        String filename = file.getName().toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
            if (filename.endsWith("." + ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a unique filename.
     *
     * @param originalName the original filename
     * @return a unique filename
     */
    private static String generateUniqueFileName(String originalName) {
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String uniqueName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;
        return uniqueName;
    }

    /**
     * Gets the full path of an image.
     *
     * @param relativePath the relative path
     * @return the full path of the image
     */
    public static String getFullImagePath(String relativePath) {
        Path resolved = resolveImagePath(relativePath);
        if (resolved != null) {
            return resolved.toAbsolutePath().toString();
        }
        return new File(relativePath).getAbsolutePath();
    }

    /**
     * Gets the images directory.
     *
     * @return the Path of the directory
     */
    public static Path getImagesDirectory() {
        return IMAGES_DIRECTORY;
    }

    /**
     * Gets the application's base directory.
     *
     * @return the Path of the base directory
     */
    public static Path getAppBaseDirectory() {
        return APP_BASE_DIR;
    }
}
