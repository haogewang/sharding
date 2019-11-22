package com.szhq.iemp.reservation.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.ResultConstant;
import com.szhq.iemp.common.util.TimeStampUtil;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.reservation.api.model.TcommonConfig;
import com.szhq.iemp.reservation.api.service.*;
import com.szhq.iemp.reservation.listener.MyWebSocketHandler;
import com.szhq.iemp.reservation.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(value = "/common", description = "通用模块")
@RestController
@RequestMapping("/common")
public class CommonController {
    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private CommonService commonService;
    @Resource(name = "primaryRedisUtil")
    private RedisUtil redisUtil;
    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "获取系统时间", notes = "获取系统时间")
    @RequestMapping(value = "/getSysTime", method = RequestMethod.GET)
    public Result getSysTime() {
        String date = TimeStampUtil.formatDate(new Date(), null);
        return new Result(ResultConstant.SUCCESS, date);
    }

    @ApiOperation(value = "获取每天最大预约数", notes = "获取每天最大预约数")
    @RequestMapping(value = "/getMaxReservationNum", method = RequestMethod.GET)
    public Result getMaxReservationNum() {
        return new Result(ResultConstant.SUCCESS, commonService.findByName(CommonConstant.MAX_RESERVATION_NUM_KEY));
    }

    @ApiOperation(value = "获取最大预约天数", notes = "获取最大预约天数")
    @RequestMapping(value = "/getReservationInDaysNum", method = RequestMethod.GET)
    public Result getReservationInDaysNum() {
        return new Result(ResultConstant.SUCCESS, commonService.findByName(CommonConstant.RESERVATION_IN_DAYS_KEY));
    }

    @ApiOperation(value = "修改每天最大预约数", notes = "修改每天最大预约数")
    @RequestMapping(value = "/setMaxReservationNum", method = RequestMethod.POST)
    public Result setMaxReservationNum(@RequestParam("value") String value) {
        return new Result(ResultConstant.SUCCESS, commonService.setByName(CommonConstant.MAX_RESERVATION_NUM_KEY, value));
    }

    @ApiOperation(value = "修改最大预约天数", notes = "修改最大预约天数")
    @RequestMapping(value = "/setReservationInDaysNum", method = RequestMethod.POST)
    public Result setReservationInDaysNum(@RequestParam("value") String value) {
        return new Result(ResultConstant.SUCCESS, commonService.setByName(CommonConstant.RESERVATION_IN_DAYS_KEY, value));
    }

    @ApiOperation(value = "获取在线人数", notes = "获取在线人数")
    @RequestMapping(value = "/getOnlineNum", method = RequestMethod.GET)
    public Result getOnlineNum() {
        return new Result(ResultConstant.SUCCESS, MyWebSocketHandler.getOnlineNum());
    }

    @ApiOperation(value = "获取W310使用说明书", notes = "获取W310使用说明书")
    @RequestMapping(value = "/get310instruction", method = RequestMethod.GET)
    public Result get310instruction() {
        TcommonConfig common = commonService.findByName(CommonConstant.INSTRUCTION_KEY);
        if (common != null) {
            return new Result(ResultConstant.SUCCESS, common.getValue());
        }
        return new Result(ResultConstant.FAILED, "not find");
    }

    @ApiOperation(value = "删除工程所有redis缓存", notes = "删除工程所有redis缓存")
    @RequestMapping(value = "/deleteAllRedis", method = RequestMethod.DELETE)
    public Result deleteAllRedis() {
        String key1 = CommonConstant.ELECALL_ID_KEY + "*";
        String key2 = CommonConstant.ELEC_IMEI + "*";
        String key3 = CommonConstant.ELEC_PLATENUMBER + "*";
        String key4 = CommonConstant.REGISTER_IOTDEVICEID + "*";
        String key5 = CommonConstant.REGISTER_IMEI + "*";
        String key6 = CommonConstant.IOTDEVICEID + "*";
        String key7 = CommonConstant.DEVICE_IMEI + "*";
        String key8 = CommonConstant.REGISER_PATTERN;
        String key9 = CommonConstant.RESERVATION_PATTERN;
        String key10 = CommonConstant.ELEC_COLORS_PATTERN;
        String key11 = CommonConstant.OPERATOR_REDISKEY + "*";
        String key12 = CommonConstant.ELEC_TYPES_PATTERN;
        String key13 = CommonConstant.ELEC_VENDORS_PATTERN;
        String key14 = CommonConstant.ELEC_PATTERN;
        String key15 = CommonConstant.SITE_PATTERN;
        String key16 = CommonConstant.DEVICE_PATTERN;
        String key17 = CommonConstant.OPERATOR_PATTERN;
        String key18 = CommonConstant.HISTORY_DISPACHE_LOG_PATTERN;
        String key19 = CommonConstant.NOTRACKER_PATTERN;
        String key20 = CommonConstant.RT_DATA_KEY;
        String key21 = CommonConstant.REGION_PATTERN;
        String key22 = CommonConstant.USER_ID;
        String key23 = CommonConstant.ELEC_ID;
        int sum = deleteRedis(key1, key2, key3, key4, key5, key6, key7,
                key8, key9, key10, key11, key12, key13, key14, key15, key16,
                key17, key18, key19, key20, key21, key22, key23);
        return new Result(ResultConstant.SUCCESS, sum);
    }

    /**
     * 获取项目所有的URL
     */
    @RequestMapping(value = "/getAllURL", method = RequestMethod.POST)
    public Object getAllURL() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> mappingInfoHandlerMethodEntry : map.entrySet()) {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            Map<String, Map<String, String>> parmeterMap = new LinkedHashMap<>();
            Map<String, Map<String, String>> requestParmeterMap = new LinkedHashMap<>();
            RequestMappingInfo requestMappingInfo = mappingInfoHandlerMethodEntry.getKey();
            HandlerMethod handlerMethod = mappingInfoHandlerMethodEntry.getValue();
            resultMap.put("className", handlerMethod.getMethod().getDeclaringClass().getName()); // 类名
            Annotation[] parentAnnotations = handlerMethod.getBeanType().getAnnotations();
            for (Annotation annotation : parentAnnotations) {
                if (annotation instanceof Api) {
                    Api api = (Api) annotation;
                    resultMap.put("classDesc", api.value());
                } else if (annotation instanceof RequestMapping) {
                    RequestMapping requestMapping = (RequestMapping) annotation;
                    if (null != requestMapping.value() && requestMapping.value().length > 0) {
                        resultMap.put("classURL", requestMapping.value()[0]);//类URL
                    }
                }
            }
            resultMap.put("methodName", handlerMethod.getMethod().getName()); // 方法名
            Annotation[] annotations = handlerMethod.getMethod().getDeclaredAnnotations();
            if (annotations != null) {
                // 处理具体的方法信息
                for (Annotation annotation : annotations) {
                    if (annotation instanceof ApiOperation) {
                        ApiOperation methodDesc = (ApiOperation) annotation;
                        String desc = methodDesc.value();
                        resultMap.put("methodDesc", desc);//接口描述
                    }
                }
            }
            MethodParameter[] parameters = handlerMethod.getMethodParameters();
            if (parameters != null && parameters.length > 0) {
                for (MethodParameter parameter : parameters) {
                    Map<String, String> pmap = new HashMap<>();
                    Map<String, String> rpmap = new HashMap<>();
                    String parmeterName = parameter.getParameter().getName();
                    Class<?> classes = parameter.getParameterType();
                    RequestBody parameterAnnotation = parameter.getParameterAnnotation(RequestBody.class);
                    RequestParam requestParamAnnotation = parameter.getParameterAnnotation(RequestParam.class);
                    if (parameterAnnotation != null) {
                        Boolean required = parameterAnnotation.required();
                        pmap.put("required", required.toString());
                        pmap.put("classes", classes.getSimpleName());
                        parmeterMap.put(parmeterName, pmap);
                    }
                    if (requestParamAnnotation != null) {
                        Boolean required = requestParamAnnotation.required();
                        rpmap.put("required", required.toString());
                        rpmap.put("classes", classes.getSimpleName());
                        if ("offset".equals(parmeterName)) {
                            rpmap.put("name", "页数-1");
                        }
                        if ("pagesize".equals(parmeterName)) {
                            rpmap.put("name", "每页条数");
                        }
                        if ("sort".equals(parmeterName)) {
                            rpmap.put("name", "排序");
                        }
                        if ("order".equals(parmeterName)) {
                            rpmap.put("name", "升序或降序(asc/desc)");
                        }
                        requestParmeterMap.put(parmeterName, rpmap);
                    }
                }
                resultMap.put("parameters", parmeterMap);
                resultMap.put("rparameters", requestParmeterMap);
            }
            PatternsRequestCondition p = requestMappingInfo.getPatternsCondition();
            for (String url : p.getPatterns()) {
                resultMap.put("methodURL", url);//请求URL
            }
            RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                resultMap.put("requestType", requestMethod.toString());//请求方式：POST/PUT/GET/DELETE
            }
            resultList.add(resultMap);
        }
        List<JSONObject> result = new ArrayList<>();
        if (!resultList.isEmpty()) {
            for (Map<String, Object> resultMap : resultList) {
                JSONObject json = new JSONObject();
                String methodURL = (String) resultMap.get("methodURL");
                String requestType = (String) resultMap.get("requestType");
                String methodDesc = (String) resultMap.get("methodDesc");

                json.put("request_url", "aepreservation" + methodURL);
                json.put("method", requestType);
                json.put("instruction", methodDesc);
                json.put("description", methodDesc);
                json.put("if_name", methodDesc);
                Map<String, Map<String, String>> parMap = (Map<String, Map<String, String>>) resultMap.get("parameters");
                Map<String, Map<String, String>> rparMap = (Map<String, Map<String, String>>) resultMap.get("rparameters");
                if (parMap != null && !parMap.isEmpty()) {
                    JSONArray jsonArray = new JSONArray();
                    for (String parName : parMap.keySet()) {
                        Map<String, String> parValueMap = parMap.get(parName);
                        JSONObject jsons = new JSONObject();
                        if (parValueMap.containsKey("required")) {
                            String clazzType = parValueMap.get("classes");
                            String isRequired = parValueMap.get("required");
                            if ("true".equals(isRequired)) {
                                jsons.put("must", "yes");
                            } else {
                                jsons.put("must", "no");
                            }
                            jsons.put("param", parName);
                            jsons.put("vartype", clazzType);
                            jsonArray.add(jsons);
//							json.put("request_body_params", jsons.toJSONString());
                        } else {
                            String clazzType = parValueMap.get("classes");
                            jsons.put("param", parName);
                            jsons.put("vartype", clazzType);
                            jsonArray.add(jsons);
//							json.put("request_url_params", jsons.toJSONString());
                        }
                    }
                    json.put("request_body_params", jsonArray.toJSONString());
                } else {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsons = new JSONObject();
                    jsons.put("param", "");
                    jsons.put("vartype", "");
                    jsons.put("must", "");

                }
                if (rparMap != null && !rparMap.isEmpty()) {
                    JSONArray jsonArray = new JSONArray();
                    for (String parName : rparMap.keySet()) {
                        Map<String, String> parValueMap = rparMap.get(parName);
                        JSONObject jsons = new JSONObject();
                        if (parValueMap.containsKey("required")) {
                            String clazzType = parValueMap.get("classes");
                            String isRequired = parValueMap.get("required");
                            String name = parValueMap.get("name");
                            if ("true".equals(isRequired)) {
                                jsons.put("must", "yes");
                            } else {
                                jsons.put("must", "no");
                            }
                            jsons.put("param", parName);
                            jsons.put("vartype", clazzType);
                            jsons.put("name", name);
                            jsonArray.add(jsons);
//							json.put("request_body_params", jsons.toJSONString());
                        } else {
                            String clazzType = parValueMap.get("classes");
                            jsons.put("param", parName);
                            jsons.put("vartype", clazzType);
                            jsonArray.add(jsons);
//							json.put("request_url_params", jsons.toJSONString());
                        }
                    }
                    json.put("request_url_params", jsonArray.toJSONString());
                }
                json.put("if_type", 56);
                json.put("project_id", 24);
                JSONArray jsonArray = new JSONArray();
                JSONObject jsons1 = new JSONObject();
                jsons1.put("param", "code");
                jsons1.put("name", "响应码");
                jsons1.put("instruction", "0:失败,1:成功");
                jsons1.put("vartype", "int");
                JSONObject jsons2 = new JSONObject();
                jsons2.put("param", "message");
                jsons2.put("name", "消息");
                jsons2.put("instruction", "返回值");
                jsons2.put("vartype", "String");
                JSONObject jsons3 = new JSONObject();
                jsons3.put("param", "data");
                jsons3.put("name", "数据");
                jsons3.put("instruction", "返回数据");
                jsons3.put("vartype", "Object");
                jsonArray.add(jsons1);
                jsonArray.add(jsons2);
                jsonArray.add(jsons3);
                json.put("response_param", jsonArray.toJSONString());
//				ResponseEntity response = restTemplate.postForEntity(url, json, JSONObject.class);
                result.add(json);
            }
        }
        return result;
    }

    public static final String url = "http://47.99.125.192:8100/autoapi/interface_add/";



    private int deleteRedis(String... keys) {
        int count = 0;
        for(String skey : keys){
            Set<String> set = redisUtil.keys(skey);
            if (set != null && set.size() > 0) {
                for (String key : set) {
                    redisUtil.del(key);
                    count ++;
                }
            }
        }
        return  count;
    }

}
