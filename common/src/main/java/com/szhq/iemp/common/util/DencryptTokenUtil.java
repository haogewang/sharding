package com.szhq.iemp.common.util;

import com.aep.auth.common.JwtToken;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public class DencryptTokenUtil {

    public static List<Integer> getOperatorIds(HttpServletRequest request) {
        JwtToken jwtToken = new JwtToken();
        Map<String, Object> map = jwtToken.verifyToken(request.getHeader("token"));
        if (map != null && map.size() > 0) {
            if (map.get("operatorIdList") != null) {
                List<Integer> operatorIdList = JSONObject.parseArray(map.get("operatorIdList").toString(), Integer.class);
                return operatorIdList;
            }
        }
        return null;
    }

    public static Integer getOperatorId(HttpServletRequest request) {
        JwtToken jwtToken = new JwtToken();
        Map<String, Object> map = jwtToken.verifyToken(request.getHeader("token"));
        if (map != null && map.size() > 0) {
            if (map.get("operatorId") != null) {
                Integer operatorId = Integer.valueOf(map.get("operatorId").toString());
                return operatorId;
            }
        }
        return null;
    }

    public static Map<String, Object> decyptToken(HttpServletRequest request) {
        JwtToken jwtToken = new JwtToken();
        Map<String, Object> map = jwtToken.verifyToken(request.getHeader("token"));
        if (map != null && map.size() > 0) {
            return map;
        }
        return null;
    }

    public static String getUserId(HttpServletRequest request) {
        JwtToken jwtToken = new JwtToken();
        Map<String, Object> map = jwtToken.verifyToken(request.getHeader("token"));
        if (map != null && map.size() > 0 && map.get("userId") != null) {
            String userId = (String)map.get("userId");
            return userId;
        }
        return null;
    }

}
