package com.example.bookxchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Spy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileUploadServiceTest {

    private FileUploadService fileUploadService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileUploadService = new FileUploadService();
        ReflectionTestUtils.setField(fileUploadService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(fileUploadService, "maxFileSize", 5242880L); // 5MB
    }

    // ==================== saveFile Tests ====================

    @Test
    void testSaveFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "fake image content".getBytes()
        );

        String filename = fileUploadService.saveFile(file);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".jpg"));
        assertTrue(fileUploadService.fileExists(filename));
    }

    @Test
    void testSaveFile_PNG() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.png",
            "image/png",
            "fake png content".getBytes()
        );

        String filename = fileUploadService.saveFile(file);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".png"));
        assertTrue(fileUploadService.fileExists(filename));
    }

    @Test
    void testSaveFile_GIF() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.gif",
            "image/gif",
            "fake gif content".getBytes()
        );

        String filename = fileUploadService.saveFile(file);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".gif"));
        assertTrue(fileUploadService.fileExists(filename));
    }

    @Test
    void testSaveFile_WebP() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.webp",
            "image/webp",
            "fake webp content".getBytes()
        );

        String filename = fileUploadService.saveFile(file);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".webp"));
        assertTrue(fileUploadService.fileExists(filename));
    }

    @Test
    void testSaveFile_CreateUploadDirectory() throws Exception {
        // Clean up the upload directory
        Path uploadPath = Paths.get(tempDir.toString());
        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // Ignore
                    }
                });
        }

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "fake image content".getBytes()
        );

        String filename = fileUploadService.saveFile(file);

        assertNotNull(filename);
        assertTrue(Files.exists(uploadPath.resolve(filename)));
    }

    @Test
    void testSaveFile_EmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> {
            fileUploadService.saveFile(file);
        });
    }

    @Test
    void testSaveFile_NullFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            fileUploadService.saveFile(null);
        });
    }

    @Test
    void testSaveFile_InvalidFileType_PDF() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "document.pdf",
            "application/pdf",
            "fake pdf content".getBytes()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fileUploadService.saveFile(file);
        });

        assertTrue(exception.getMessage().contains("Only image files"));
    }

    @Test
    void testSaveFile_InvalidFileType_TextFile() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "document.txt",
            "text/plain",
            "fake text content".getBytes()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fileUploadService.saveFile(file);
        });

        assertTrue(exception.getMessage().contains("Only image files"));
    }

    @Test
    void testSaveFile_InvalidFileType_Video() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "video.mp4",
            "video/mp4",
            "fake video content".getBytes()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fileUploadService.saveFile(file);
        });

        assertTrue(exception.getMessage().contains("Only image files"));
    }

    @Test
    void testSaveFile_FileSizeExceedsLimit() {
        // Create a mock file that exceeds 5MB
        byte[] largeContent = new byte[(5 * 1024 * 1024) + 1]; // 5MB + 1 byte
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "large.jpg",
            "image/jpeg",
            largeContent
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fileUploadService.saveFile(file);
        });

        assertTrue(exception.getMessage().contains("File size exceeds maximum limit"));
    }

    @Test
    void testSaveFile_MultipleFiles_UniqueFilenames() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "fake image content 1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "fake image content 2".getBytes()
        );

        String filename1 = fileUploadService.saveFile(file1);
        String filename2 = fileUploadService.saveFile(file2);

        assertNotNull(filename1);
        assertNotNull(filename2);
        assertNotEquals(filename1, filename2);
        assertTrue(fileUploadService.fileExists(filename1));
        assertTrue(fileUploadService.fileExists(filename2));
    }

    // ==================== deleteFile Tests ====================

    @Test
    void testDeleteFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "fake image content".getBytes()
        );

        String filename = fileUploadService.saveFile(file);
        assertTrue(fileUploadService.fileExists(filename));

        fileUploadService.deleteFile(filename);

        assertFalse(fileUploadService.fileExists(filename));
    }

    @Test
    void testDeleteFile_NonexistentFile() {
        // Should not throw exception
        assertDoesNotThrow(() -> {
            fileUploadService.deleteFile("nonexistent.jpg");
        });
    }

    @Test
    void testDeleteFile_NullFilename() {
        // Should not throw exception
        assertDoesNotThrow(() -> {
            fileUploadService.deleteFile(null);
        });
    }

    @Test
    void testDeleteFile_EmptyFilename() {
        // Should not throw exception
        assertDoesNotThrow(() -> {
            fileUploadService.deleteFile("");
        });
    }

    @Test
    void testDeleteFile_MultipleFiles() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
            "file",
            "test1.jpg",
            "image/jpeg",
            "fake image content 1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
            "file",
            "test2.jpg",
            "image/jpeg",
            "fake image content 2".getBytes()
        );

        String filename1 = fileUploadService.saveFile(file1);
        String filename2 = fileUploadService.saveFile(file2);

        assertTrue(fileUploadService.fileExists(filename1));
        assertTrue(fileUploadService.fileExists(filename2));

        fileUploadService.deleteFile(filename1);

        assertFalse(fileUploadService.fileExists(filename1));
        assertTrue(fileUploadService.fileExists(filename2));

        fileUploadService.deleteFile(filename2);

        assertFalse(fileUploadService.fileExists(filename2));
    }

    // ==================== fileExists Tests ====================

    @Test
    void testFileExists_ExistingFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "fake image content".getBytes()
        );

        String filename = fileUploadService.saveFile(file);

        assertTrue(fileUploadService.fileExists(filename));
    }

    @Test
    void testFileExists_NonexistentFile() {
        assertFalse(fileUploadService.fileExists("nonexistent.jpg"));
    }

    @Test
    void testFileExists_NullFilename() {
        assertFalse(fileUploadService.fileExists(null));
    }

    @Test
    void testFileExists_EmptyFilename() {
        assertFalse(fileUploadService.fileExists(""));
    }

    @Test
    void testFileExists_DeletedFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "fake image content".getBytes()
        );

        String filename = fileUploadService.saveFile(file);
        assertTrue(fileUploadService.fileExists(filename));

        fileUploadService.deleteFile(filename);

        assertFalse(fileUploadService.fileExists(filename));
    }

    // ==================== getFileUrl Tests ====================

    @Test
    void testGetFileUrl_ValidFilename() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "fake image content".getBytes()
        );

        String filename = fileUploadService.saveFile(file);
        String url = fileUploadService.getFileUrl(filename);

        assertNotNull(url);
        assertTrue(url.startsWith("/uploads/profiles/"));
        assertTrue(url.endsWith(".jpg"));
    }

    @Test
    void testGetFileUrl_NullFilename() {
        assertNull(fileUploadService.getFileUrl(null));
    }

    @Test
    void testGetFileUrl_EmptyFilename() {
        assertNull(fileUploadService.getFileUrl(""));
    }

    @Test
    void testGetFileUrl_SpecificFilename() {
        String url = fileUploadService.getFileUrl("specific-file.jpg");

        assertNotNull(url);
        assertEquals("/uploads/profiles/specific-file.jpg", url);
    }

    @Test
    void testGetFileUrl_MultipleExtensions() {
        String urlJpg = fileUploadService.getFileUrl("image.jpg");
        String urlPng = fileUploadService.getFileUrl("image.png");
        String urlGif = fileUploadService.getFileUrl("image.gif");
        String urlWebp = fileUploadService.getFileUrl("image.webp");

        assertTrue(urlJpg.endsWith(".jpg"));
        assertTrue(urlPng.endsWith(".png"));
        assertTrue(urlGif.endsWith(".gif"));
        assertTrue(urlWebp.endsWith(".webp"));
    }

    // ==================== Integration Tests ====================

    @Test
    void testIntegration_SaveAndDelete() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "fake image content".getBytes()
        );

        // Save file
        String filename = fileUploadService.saveFile(file);
        assertNotNull(filename);
        assertTrue(fileUploadService.fileExists(filename));

        // Get file URL
        String url = fileUploadService.getFileUrl(filename);
        assertNotNull(url);
        assertTrue(url.contains(filename));

        // Delete file
        fileUploadService.deleteFile(filename);
        assertFalse(fileUploadService.fileExists(filename));
    }

    @Test
    void testIntegration_SaveMultipleAndDeleteSelective() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
            "file",
            "test1.jpg",
            "image/jpeg",
            "content1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
            "file",
            "test2.png",
            "image/png",
            "content2".getBytes()
        );

        String filename1 = fileUploadService.saveFile(file1);
        String filename2 = fileUploadService.saveFile(file2);

        assertTrue(fileUploadService.fileExists(filename1));
        assertTrue(fileUploadService.fileExists(filename2));

        fileUploadService.deleteFile(filename1);

        assertFalse(fileUploadService.fileExists(filename1));
        assertTrue(fileUploadService.fileExists(filename2));
    }
}
