package org.ssafy.datacontest.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile multipartFile) {
        String fileName = makeFileName(multipartFile);

        // S3에 파일을 업로드할 때 메타데이터(metadata)를 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType()); // MIME 타입 설정
        objectMetadata.setContentLength(multipartFile.getSize()); // 파일 크기 설정

        // 파일 내용을 읽기 위해 getInputStream() 호출
        try(InputStream inputStream = multipartFile.getInputStream()){
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }catch(IOException e){
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.S3_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private String makeFileName(MultipartFile multipartFile){
        // 사용자가 업로드한 원본 파일 이름 가져오기
        String originalName = multipartFile.getOriginalFilename();
        final String ext = originalName.substring(originalName.lastIndexOf("."));
        // 네트워크 상에서 고유성이 보장되는 id를 만들기 위한 표준 규약
        final String fileName = UUID.randomUUID() + ext;
        // 	현재 JVM(자바 프로그램)이 실행 중인 작업 디렉토리의 경로를 문자열로 가져오는 명령
        return fileName;
    }

    public void deleteFile(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        amazonS3.deleteObject(bucket, key);
    }

    private String extractKeyFromUrl(String fileUrl) {
        // 예: https://your-bucket.s3.amazonaws.com/images/cat.png
        return fileUrl.substring(fileUrl.indexOf(".com/") + 5); // "images/cat.png" 추출
    }
}
