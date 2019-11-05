//package com.xiaomi.thain.sdk;
//
//import com.google.common.collect.ImmutableMap;
//import lombok.val;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.io.IOException;
//
//public class ThainClientTest {
//    private static final ThainClient CLIENT = ThainClient.getInstance("admin", "admin", "http://localhost:9900");
//
//    @Test
//    public void testUpdateJobProperties() throws IOException {
//        val t = CLIENT.updateJobProperties(361, "job1", ImmutableMap.of("url", "123", "aaa", "bbb"));
//        Assert.assertEquals(t.status, 200);
//    }
//
//}
