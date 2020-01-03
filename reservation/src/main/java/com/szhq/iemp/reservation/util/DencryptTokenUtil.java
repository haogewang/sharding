package com.szhq.iemp.reservation.util;

import com.aep.auth.common.JwtToken;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

//public class DencryptTokenUtil {
//
//    public static List<Integer> getOperatorIds(HttpServletRequest request) {
//        JwtToken jwtToken = new JwtToken();
//        Map<String, Object> map = jwtToken.verifyToken(request.getHeader("token"));
//        if (map != null && map.size() > 0) {
//            if (map.get("operatorIdList") != null) {
//                List<Integer> operatorIdList = JSONObject.parseArray(map.get("operatorIdList").toString(), Integer.class);
//                return operatorIdList;
//            }
//        }
//        return null;
//    }
//
//    public static Map<String, Object> decyptToken(HttpServletRequest request) {
//        JwtToken jwtToken = new JwtToken();
//        Map<String, Object> map = jwtToken.verifyToken(request.getHeader("token"));
//        if (map != null && map.size() > 0) {
//            return map;
//        }
//        return null;
//    }
//}
