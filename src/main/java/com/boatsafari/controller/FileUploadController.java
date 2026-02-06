package com.boatsafari.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    // Use external directory that doesn't require server restart
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping
    public ResponseEntity<?> uploadImage(
            @RequestParam("image") MultipartFile file,
            @RequestParam("folder") String folder) {

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("File is empty"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(createErrorResponse("Only image files are allowed"));
            }

            // Validate file size (optional - 10MB limit)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(createErrorResponse("File size must be less than 10MB"));
            }

            // Create folder-specific directory in EXTERNAL location
            String folderPath = uploadDir + File.separator + folder + File.separator;
            File directory = new File(folderPath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    return ResponseEntity.status(500).body(createErrorResponse("Failed to create upload directory: " + folderPath));
                }
                System.out.println("✅ Created directory: " + directory.getAbsolutePath());
            }

            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
            }

            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            String filePath = folderPath + uniqueFileName;

            // Save the file to EXTERNAL directory
            Path path = Paths.get(filePath);
            Files.copy(file.getInputStream(), path);

            System.out.println("✅ File saved to: " + path.toAbsolutePath());

            // Create web-accessible URL - use /external-uploads/ for immediate access
            String webPath = "/external-uploads/" + folder + "/" + uniqueFileName;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filePath", webPath);
            response.put("fileName", uniqueFileName);
            response.put("message", "File uploaded successfully");
            response.put("absolutePath", path.toAbsolutePath().toString());

            System.out.println("🌐 Web accessible at: http://localhost:8080" + webPath);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("Failed to upload file: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteImage(@RequestParam String filePath) {
        try {
            // Handle both /uploads/ and /external-uploads/ paths
            String fsPath;
            if (filePath.startsWith("/external-uploads/")) {
                // External uploads - remove /external-uploads/ and use uploadDir
                fsPath = filePath.replace("/external-uploads/", uploadDir + File.separator);
            } else if (filePath.startsWith("/uploads/")) {
                // Classpath uploads - use the old path for backward compatibility
                fsPath = "src/main/resources/static" + filePath;
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid file path"));
            }

            // Replace forward slashes with system file separator
            fsPath = fsPath.replace("/", File.separator);

            File file = new File(fsPath);
            System.out.println("🗑️ Attempting to delete: " + file.getAbsolutePath());

            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("✅ File deleted successfully: " + file.getAbsolutePath());
                    return ResponseEntity.ok(createSuccessResponse("File deleted successfully"));
                } else {
                    System.out.println("❌ Failed to delete file: " + file.getAbsolutePath());
                    return ResponseEntity.status(500).body(createErrorResponse("Failed to delete file"));
                }
            } else {
                System.out.println("⚠️ File not found, already deleted: " + file.getAbsolutePath());
                return ResponseEntity.ok(createSuccessResponse("File not found, already deleted"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("Error deleting file: " + e.getMessage()));
        }
    }

    // Endpoint to check if file exists (for debugging)
    @GetMapping("/check")
    public ResponseEntity<?> checkFile(@RequestParam String filePath) {
        try {
            String fsPath;
            if (filePath.startsWith("/external-uploads/")) {
                fsPath = filePath.replace("/external-uploads/", uploadDir + File.separator);
            } else if (filePath.startsWith("/uploads/")) {
                fsPath = "src/main/resources/static" + filePath;
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid file path"));
            }

            fsPath = fsPath.replace("/", File.separator);
            File file = new File(fsPath);

            Map<String, Object> response = new HashMap<>();
            response.put("exists", file.exists());
            response.put("absolutePath", file.getAbsolutePath());
            response.put("filePath", filePath);
            response.put("fileSize", file.exists() ? file.length() : 0);
            response.put("readable", file.exists() ? file.canRead() : false);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse("Error checking file: " + e.getMessage()));
        }
    }

    // Endpoint to list uploaded files (for debugging)
    @GetMapping("/list")
    public ResponseEntity<?> listFiles(@RequestParam String folder) {
        try {
            String folderPath = uploadDir + File.separator + folder + File.separator;
            File directory = new File(folderPath);

            Map<String, Object> response = new HashMap<>();

            if (!directory.exists()) {
                response.put("exists", false);
                response.put("message", "Directory does not exist");
                return ResponseEntity.ok(response);
            }

            File[] files = directory.listFiles();
            String[] fileNames = files != null ?
                    java.util.Arrays.stream(files)
                            .filter(File::isFile)
                            .map(File::getName)
                            .toArray(String[]::new) :
                    new String[0];

            response.put("exists", true);
            response.put("fileCount", fileNames.length);
            response.put("files", fileNames);
            response.put("directory", directory.getAbsolutePath());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse("Error listing files: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return errorResponse;
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("success", true);
        successResponse.put("message", message);
        return successResponse;
    }
}