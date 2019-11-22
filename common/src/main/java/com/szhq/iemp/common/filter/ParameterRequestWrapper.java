package com.szhq.iemp.common.filter;

import com.aep.auth.common.JwtToken;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.util.StringJsonUtils;
import com.szhq.iemp.common.vo.BaseQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
public class ParameterRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> params = new HashMap<String, String[]>();
    private BaseQuery baseQuery = new BaseQuery();

    public ParameterRequestWrapper(HttpServletRequest request) {
        // 将request交给父类，以便于调用对应方法的时候，将其输出
        super(request);
        String token = request.getHeader("token");
        log.info("getted token is ---> " + token);
        JwtToken jwtToken = new JwtToken();
        Map<String, Object> map = jwtToken.verifyToken(token);
        if (map != null && map.size() > 0) {
            log.info("map:" + JSONObject.toJSONString(map));
            log.info("role:" + (String) map.get("role") + ", is equals:" + "2".equals((String) map.get("role")));
            if (map.get("operatorIdList") != null && !"2".equals((String) map.get("role"))) {
                List<Integer> operatorIdList = JSONObject.parseArray(map.get("operatorIdList").toString(), Integer.class);
                if (operatorIdList != null && operatorIdList.get(0) != 0) {
                    baseQuery.setOperatorIdList(operatorIdList);
                }
            }
            if(map.get("operatorId") != null && !"0".equals((String)map.get("operatorId"))) {
                if(!StringUtils.isEmpty((String)map.get("operatorId"))){
                    baseQuery.setOperatorId(Integer.valueOf((String)map.get("operatorId")));
                }
            }
        } else {
            log.info("no token or token decypt error.");
        }
        //将参数表，赋予给当前的Map以便于持有request中的参数
        Map<String, String[]> requestMap = request.getParameterMap();
//		log.info("requestMap转化前参数: " + JSON.toJSONString(requestMap));
        this.params.putAll(requestMap);
        this.modifyParameterValues();
//		log.info("requestMap转化后参数: " + JSON.toJSONString(params));
    }

    /**
     * 重写getInputStream方法  post类型的请求参数必须通过流才能获取到值
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        //非json类型，直接返回
        if (super.getHeader(HttpHeaders.CONTENT_TYPE) != null && !(super.getHeader(HttpHeaders.CONTENT_TYPE).equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE) || super.getHeader(HttpHeaders.CONTENT_TYPE).equalsIgnoreCase(MediaType.APPLICATION_JSON_UTF8_VALUE))) {
            log.info("not json type...");
//			JSONObject jSONObject = new JSONObject();
//			addParameter(jSONObject);
//			Map<String, Object> map = StringJsonUtils.jsonStringToMap(jSONObject.toJSONString());
//			log.info("no json type, body转化后参数: " + JSON.toJSONString(map));
//			ByteArrayInputStream bis = new ByteArrayInputStream(JSON.toJSONString(map).getBytes("utf-8"));
//			return new MyServletInputStream(bis);
            return super.getInputStream();
        }
        //为空，直接返回
        String json = IOUtils.toString(super.getInputStream(), "utf-8");
        if (StringUtils.isEmpty(json)) {
//			return super.getInputStream();
            JSONObject jsonObject = new JSONObject();
            addParameter(jsonObject);
            Map<String, Object> map = StringJsonUtils.jsonStringToMap(jsonObject.toJSONString());
            log.info("no body,body转化后参数: " + JSON.toJSONString(map));
            ByteArrayInputStream bis = new ByteArrayInputStream(JSON.toJSONString(map).getBytes("utf-8"));
            return new MyServletInputStream(bis);
        }
        JSONObject jSONObject = JSONObject.parseObject(json);
        addParameter(jSONObject);
        Map<String, Object> map = StringJsonUtils.jsonStringToMap(jSONObject.toJSONString());
        log.info("body转化后参数: " + JSON.toJSONString(map));
        ByteArrayInputStream bis = new ByteArrayInputStream(JSON.toJSONString(map).getBytes("utf-8"));
        return new MyServletInputStream(bis);
    }

    /**
     * 重写getParameter 参数从当前类中的map获取
     */
    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    /**
     * 重写getParameterValues
     */
    public String[] getParameterValues(String name) {//同上
        return params.get(name);
    }

    /**
     * 将parameter的值去除空格后重写回去
     */
    public void modifyParameterValues() {
        Set<String> set = params.keySet();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String[] values = params.get(key);
//            System.out.println(values[0]);
            //values[0] = values[0].trim();
            //params.put(key, values);
        }
    }

    private void addParameter(JSONObject jSONObject) {
        if (baseQuery.getOperatorId() != null) {
            jSONObject.put("operatorId", baseQuery.getOperatorId());
        }
        if (baseQuery.getOperatorIdList() != null) {
            jSONObject.put("operatorIdList", JSONArray.parseArray(JSON.toJSONString(baseQuery.getOperatorIdList())));
        }
    }

    class MyServletInputStream extends ServletInputStream {

        private ByteArrayInputStream bis;

        public MyServletInputStream(ByteArrayInputStream bis) {
            this.bis = bis;
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }

        @Override
        public int read() throws IOException {
            return bis.read();
        }
    }

}
