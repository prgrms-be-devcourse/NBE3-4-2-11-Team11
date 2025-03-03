package com.pofo.backend.domain.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    // 파일 저장할 기본 경로 (로컬 서버)
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";


    // 🔹 1️⃣ 썸네일 업로드 메서드
    public String uploadThumbnail(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        try {
            // 1. 업로드 디렉토리 생성 (없다면 생성)
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs(); //디렉토리 자동 생성
            }

            // 2. 파일 저장 이름 설정 (UUID 활용)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            // 3. 파일 저장
            file.transferTo(filePath.toFile());

            // 4. 저장된 파일 경로 반환
            return filePath.toString();
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

        File file = new File(filePath);
        if (file.exists() && !file.delete()) {
            throw new RuntimeException("파일 삭제 실패: " + filePath);
        }
    }
}
