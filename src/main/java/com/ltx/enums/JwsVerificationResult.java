package com.ltx.enums;

/**
 * JWS验证结果枚举
 * VALID: 验证成功
 * EXPIRED: Token过期
 * INVALID: Token无效(签名错误/格式错误)
 *
 * @author tianxing
 */
public enum JwsVerificationResult {
    VALID, EXPIRED, INVALID
}
