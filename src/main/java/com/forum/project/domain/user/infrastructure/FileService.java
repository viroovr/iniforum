package com.forum.project.domain.user.infrastructure;

import com.forum.project.core.exception.InfraErrorCode;
import com.forum.project.core.exception.InfraException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    @Value("file.upload-dir")
    private String uploadDir;

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        File checkUploadDir = new File(uploadDir);
        if (!checkUploadDir.exists() && !checkUploadDir.mkdirs()) {
            throw new InfraException(InfraErrorCode.FAIL_IO);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + (fileExtension.isEmpty() ? "" : "." + fileExtension);

        File destinationFile = new File(uploadDir, uniqueFilename);
        file.transferTo(destinationFile);

        return destinationFile.getAbsolutePath();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
