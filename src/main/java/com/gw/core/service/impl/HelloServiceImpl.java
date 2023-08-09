package com.gw.core.service.impl;

import com.gw.core.service.HelloService;

/**
 * Description:
 *
 * @author LinHuiBa-YanAn
 * @date 2023/8/8 19:10
 */
public class HelloServiceImpl implements HelloService {


    @Override
    public String sayHello(String name) {
        return "hello, " + name;
    }
}
