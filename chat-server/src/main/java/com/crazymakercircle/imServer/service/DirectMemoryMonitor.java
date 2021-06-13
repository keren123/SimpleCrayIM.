package com.crazymakercircle.imServer.service;

import com.crazymakercircle.util.ThreadUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import sun.management.ManagementFactoryHelper;

import javax.annotation.PostConstruct;
import java.lang.management.BufferPoolMXBean;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service("directMemoryMonitor")
public class DirectMemoryMonitor
{
    AtomicLong directMemoryCounter;

    //hasCleaner的DirectByteBuffer监控
    BufferPoolMXBean directBufferMXBean;

    @PostConstruct
    public void loadField()
    {
        //通过反射，获取堆外内存的统计字段元数据
        Field field = ReflectionUtils.findField(
                PlatformDependent.class,
                "DIRECT_MEMORY_COUNTER");

        field.setAccessible(true);

        //通过反射，获取堆外内存的统计字段
        try
        {
            directMemoryCounter = (AtomicLong) field.get(PlatformDependent.class);
            startReport();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        List<BufferPoolMXBean> bufferPoolMXBeans = ManagementFactoryHelper.getBufferPoolMXBeans();

// hasCleaner的DirectBuffer 的MXBean
        directBufferMXBean = bufferPoolMXBeans.get(0);


        log.info("io.netty.maxDirectMemory {}", SystemPropertyUtil.getLong("io.netty.maxDirectMemory", -1));
    }

    public void startReport()
    {
        ThreadUtil.scheduleAtFixedRate(() ->
        {
            long size = directMemoryCounter.get() / 1024;
            log.info("noCleaner的 directMemoryCounter {}k", size);

            // hasCleaner的DirectBuffer的数量
            long count = directBufferMXBean.getCount();
            // hasCleaner的DirectBuffer的堆外内存占用大小，单位字节
            long memoryUsed = directBufferMXBean.getMemoryUsed();

            log.info("hasCleaner的DirectByteBuffer {}, {}k", count, memoryUsed / 1023);

        }, 50, TimeUnit.SECONDS);
    }

}
