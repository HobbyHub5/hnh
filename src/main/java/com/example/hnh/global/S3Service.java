package com.example.hnh.global;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 이미지 업로드 로직
     *
     * @param image 업로드할 이미지
     * @return
     * @throws IOException
     */
    public String uploadImage(MultipartFile image) throws IOException {

        //이미지 형식
        String extension = createExtension(image);

        //업로드 할 이미지 이름 생성
        String imageName = UUID.randomUUID() + "_" + extension;

        //메타데이터 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(image.getContentType());
        objectMetadata.setContentLength(image.getSize());

        //업로드 요청 생성
        PutObjectRequest putObjectRequest = null;
        try {
            putObjectRequest = new PutObjectRequest(bucket, imageName, image.getInputStream(), objectMetadata);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        //이미지 업로드
        amazonS3.putObject(putObjectRequest);

        return createUrl(imageName);
    }

    /**
     * 업로드한 이미지 삭제 로직
     *
     * @param imagePath 업로드한 이미지 주소
     */
    public void deleteImage(String imagePath) {

        try {
            // 파일 URL 이 유효한지 확인
            if (imagePath == null || !imagePath.contains(".com/")) {
                throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
            }

            // 파일 이름 추출
            String splitStr = ".com/";
            String fileName = imagePath.substring(imagePath.lastIndexOf(splitStr) + splitStr.length());

            // S3에서 파일 삭제
            amazonS3.deleteObject(bucket, fileName);

        } catch (Exception e) {
            // AWS S3 관련 예외 처리
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 실패", e);
        }
    }

    /**
     * 업로드한 이미지 주소 생성 로직
     *
     * @param imageName 업로드한 이미지 이름
     * @return
     */
    private String createUrl(String imageName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(), imageName);
    }

    /**
     * 이미지 형식 생성, 검증 로직
     *
     * @param image
     * @return
     */
    private String createExtension(MultipartFile image) {

        String extension = "";
        String originalFilename = image.getOriginalFilename();

        //파일 형식 생성
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        //파일 형식 검증
        if (!extension.equals(".png") && !extension.equals(".jpg") && !extension.equals(".jpeg")) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_FORMAT);
        }
        return extension;
    }
}
