package com.szhq.iemp.common.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * jwt的工具类
 */
@Component
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    //iemp
    private byte[] key = "iemp".getBytes();

    /**
     * 根据用户的token和公钥验证token的时间
     *
     * @return map
     */
    public String verifyToken(String token) {
        String userId = "";
        try {
            if (StringUtils.isNotEmpty(token)) {
                Claims claim = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
                userId = claim.get("jti").toString();
            }
        } catch (Exception e) {
            logger.error("token is:" + token, e);
            throw new RuntimeException("token已过期，请重新登录!");
        }
        return userId;
    }

}
