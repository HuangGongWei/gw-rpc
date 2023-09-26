package com.gw.core.config;

import com.gw.core.protocol.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Description: 配置类
 *
 * @author LinHuiBa-YanAn
 * @date 2023/8/7 20:31
 */
public class Config {
    static Properties properties;
    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }


    /**
     * 获取项目端口号
     * @return 端口号
     */
    public static int getProjectPort() {
        String value = properties.getProperty("project.port");
        if(value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * 获取服务提供方ip
     * @return 端口号
     */
    public static String getServerIp() {
        String value = properties.getProperty("server.ip");
        if(value == null) {
            return "127.0.0.1";
        } else {
            return value;
        }
    }

    /**
     * 获取服务提供方端口号
     * @return 端口号
     */
    public static int getServerPort() {
        String value = properties.getProperty("server.port");
        if(value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * 获取序列化方式
     * @return 序列化方式枚举
     */
    public static Serializer.Algorithm getSerializerAlgorithm() {
        String value = properties.getProperty("serializer.algorithm");
        if(value == null) {
            return Serializer.Algorithm.Java;
        } else {
            return Serializer.Algorithm.valueOf(value);
        }
    }
}
