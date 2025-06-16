package org.ssafy.datacontest.util;

import java.util.UUID;

public class RandomUtil {
    public static String email() {
        return UUID.randomUUID().toString().substring(0, 3) + "@mail.com";
    }

    public static String phone() {
        return "010-" + (int)(Math.random() * 9000 + 1000)
                + "-" + (int)(Math.random() * 9000 + 1000);
    }
}
