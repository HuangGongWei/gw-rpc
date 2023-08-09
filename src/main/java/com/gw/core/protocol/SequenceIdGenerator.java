package com.gw.core.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: 序列Id生成器
 *
 * @author LinHuiBa-YanAn
 * @date 2023/8/8 10:47
 */
public class SequenceIdGenerator {
    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId() {
        return id.incrementAndGet();
    }
}
