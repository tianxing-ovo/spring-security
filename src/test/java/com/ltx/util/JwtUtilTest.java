package com.ltx.util;

import org.junit.jupiter.api.Test;

/**
 * @author tianxing
 */
public class JwtUtilTest {

    @Test
    public void genSecret() {
        System.out.println(JwtUtil.genSecret());
    }
}