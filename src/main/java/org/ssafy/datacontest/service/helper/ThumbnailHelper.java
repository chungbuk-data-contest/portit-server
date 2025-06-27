package org.ssafy.datacontest.service.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.List;

@Component
public class ThumbnailHelper {

    public int isDuplicateThumbnail(MultipartFile thumbnail, List<MultipartFile> files) throws Exception {
        if (thumbnail == null || files == null || files.isEmpty()) {
            return -1;
        }

        String thumbnailHash = getHash(thumbnail);

        for (int i = 0; i < files.size(); i++) {
            String fileHash = getHash(files.get(i));
            if (fileHash.equals(thumbnailHash)) {
                return i;
            }
        }

        return -1;
    }

    private String getHash(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = file.getBytes();
        byte[] hash = digest.digest(bytes);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

}
