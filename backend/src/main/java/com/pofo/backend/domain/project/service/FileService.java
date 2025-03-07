package com.pofo.backend.domain.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    // 파일 저장할 기본 경로
    @Value("${file.upload-dir}")
    private String uploadDir;

    // 썸네일 업로드 메서드
    public String uploadThumbnail(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        try {
            // 1. 업로드 디렉토리 생성 (없다면 생성)
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); //디렉토리 자동 생성
            }

            // 2. 파일 저장 이름 설정 (UUID 활용)
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            // 3. 파일 저장
            file.transferTo(filePath.toFile());

            // 4. 저장된 파일 경로 반환
            return "http://localhost:8080/uploads/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    // 썸네일 삭제 메서드
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return; // 파일 경로가 없으면 삭제할 필요 없음
        }

        // ✅ 저장된 파일의 물리적 경로를 가져와 삭제
        String fileName = filePath.replace("http://localhost:8080/uploads/", "");
        File file = new File(uploadDir + fileName);

        if (file.exists() && !file.delete()) {
            throw new RuntimeException("파일 삭제 실패: " + filePath);
        }
    }
}
