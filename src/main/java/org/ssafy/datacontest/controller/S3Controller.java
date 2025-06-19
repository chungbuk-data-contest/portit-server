package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.datacontest.service.S3FileService;

@RestController
@RequiredArgsConstructor
@Tag(name = "S3", description = "S3 파일 관련 API")
@RequestMapping("/s3")
public class S3Controller {

    private final S3FileService s3FileService;

    @PostMapping("")
    @Operation(
            summary = "S3에 파일 업로드",
            description = "Multipart 형식으로 파일을 업로드하면, 저장된 파일의 접근 URL을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 업로드 성공"),
    })
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = s3FileService.uploadFile(file);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("파일 업로드 실패");
        }
    }

}
