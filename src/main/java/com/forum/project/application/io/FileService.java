package com.forum.project.application.io;

import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    @Value("file.upload-dir")
    private String uploadDir;

    public String uploadFile(MultipartFile file) throws IOException {
        // 디렉토리 존재 여부 확인 및 생성
        File checkUploadDir = new File(uploadDir);
        if (!checkUploadDir.exists() && !checkUploadDir.mkdirs()) {
            throw new ApplicationException(ErrorCode.FAIL_IO);
        }

        // 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + (fileExtension.isEmpty() ? "" : "." + fileExtension);

        // 파일 저장
        File destinationFile = new File(uploadDir, uniqueFilename);
        file.transferTo(destinationFile);

        return destinationFile.getAbsolutePath();
    }

    // 파일 확장자 추출
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
