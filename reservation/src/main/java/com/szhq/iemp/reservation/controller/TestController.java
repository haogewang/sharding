package com.szhq.iemp.reservation.controller;

import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.vo.Result;
import com.szhq.iemp.reservation.api.service.RegistrationService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "")
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    RegistrationService registrationService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public Result test() {
//        Long l = registrationService.test();
//		registrationService.test();
//        return new Result(200, "222", 0);
        throw new NbiotException(3, "w");
    }

    @RequestMapping(value = "/test2", method = RequestMethod.GET)
    public Result test2() {
        return new Result(200, "222", 0);
    }

    @RequestMapping(value = "/submit")
    public String security() throws Exception {
//		System.out.println(upmsuserService.findByPhone("1234"));
        throw new NbiotException(2, "w");
//		return null;
//		String s = deviceService.send();
//		return s;
    }

    public static void main(String[] args) {
        String url = "/aepreservation/actuator/health";
        if (url.contains("aepreservation/actuator/")) {
            System.out.println("true");
        }
    }

}
