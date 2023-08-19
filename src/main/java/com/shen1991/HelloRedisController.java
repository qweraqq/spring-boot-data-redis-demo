package com.shen1991;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Base64;
import java.util.Map;


@RestController
@RequestMapping("/api/redis")
public class HelloRedisController {
    private static final String DUMMY_REDIS_KEY = "redis:dummy";

    @Autowired
    private RedisTemplate<Serializable, Serializable> redisSerializableTemplate;

    @Autowired
    private RedisTemplate<String, String> redisStringTemplate;
    @Autowired
    JedisConnectionFactory jedisConnectionFactory;

    private void setRedis(String k, String v){
        // 序列化后再反序列化无法利用
        RedisSerializer keySerializer = redisStringTemplate.getKeySerializer();
        RedisSerializer valueSerializer = redisStringTemplate.getValueSerializer();
        byte[] byteKey = keySerializer.serialize(k);
        byte[] byteValue = valueSerializer.serialize(v);
        System.out.println("raw value to be set in redis: \r\n\r" + new String(Base64.getEncoder().encode(byteValue)));
        jedisConnectionFactory.getConnection().set(byteKey, byteValue);
    }

    private void setRedisRawValueInBase64(String k, String vBase64){
        // 这种直接保存的对象可以利用
        RedisSerializer keySerializer = redisStringTemplate.getKeySerializer();

        // 无论使用redisStringTemplate或是redisSerializableTemplate结果无区别
        RedisSerializer valueSerializer = redisStringTemplate.getValueSerializer();

        byte[] byteKey = keySerializer.serialize(k);

        // 直接将字节保存至redis
        byte[] byteValue = Base64.getDecoder().decode(vBase64);

        // 取消下面一行代码的注释, 漏洞利用就会不成功
        // byteValue = valueSerializer.serialize(new String(byteValue));
        System.out.println("raw value to be set in redis: \r\n\r" + new String(Base64.getEncoder().encode(byteValue)));
        jedisConnectionFactory.getConnection().set(byteKey, byteValue);
    }

    private Object getRedis(String k){
        RedisConnection redisConnection = jedisConnectionFactory.getConnection();
        RedisSerializer keySerializer = redisStringTemplate.getKeySerializer();
        RedisSerializer valueSerializer = redisStringTemplate.getValueSerializer();
        byte[] byteKey = keySerializer.serialize(k);
        byte[] byteValue = redisConnection.get(byteKey);
        if (byteValue != null) {
            Object objectValue = valueSerializer.deserialize(byteValue);
            System.out.println(objectValue.getClass().getCanonicalName());
            System.out.println(objectValue);
            return objectValue;
        }
        redisConnection.close();
        return null;
    }


    @PostMapping("/set")
    @ResponseStatus(HttpStatus.CREATED)
    public String setRedisController(@RequestParam String payload) {
        System.out.println("Receive payload: " + payload);
        setRedis(DUMMY_REDIS_KEY, payload);
        return "set redis value";
    }

    @PostMapping("/set-base64")
    @ResponseStatus(HttpStatus.CREATED)
    public String setRedisBase64Controller(@RequestParam String payload) {
        // 注意payload参数需要url编码
        System.out.println("Receive payload: " + payload);
        setRedisRawValueInBase64(DUMMY_REDIS_KEY, payload);
        return "set redis value in base64";
    }

    @GetMapping("/get")
    public String getRedisController() {
        Object ignored = getRedis(DUMMY_REDIS_KEY);
        return "get redis value";
    }

}
