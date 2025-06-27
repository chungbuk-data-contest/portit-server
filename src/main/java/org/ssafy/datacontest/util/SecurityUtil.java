package org.ssafy.datacontest.util;

import org.ssafy.datacontest.dto.register.CustomUserDetails;

public class SecurityUtil {
    public static String extractUsername(CustomUserDetails userDetails) {
        return userDetails != null ? userDetails.getUsername() : "";
    }
}