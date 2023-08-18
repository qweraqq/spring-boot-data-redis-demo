package com.shen1991;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.OxmSerializer;
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
    private static final String STRING_KEY_PREFIX = "redi2read:strings:";
    private static final String RAW_KEY_IN_BASE64 = "wqzDrQAFdAAIcmViZXlvbmQ=";

    @Autowired
    private RedisTemplate<Serializable, Serializable> redisTemplate;

    @Autowired
    JedisConnectionFactory jedisConnectionFactory;

    @PostMapping("/strings")
    @ResponseStatus(HttpStatus.CREATED)
    public Map.Entry<String, String> setString(@RequestBody Map.Entry<String, String> kvp) {
        byte[] decodedBytes = Base64.getDecoder().decode(kvp.getValue());
        String decodedString = new String(decodedBytes);
//        redisTemplate.opsForValue().set(STRING_KEY_PREFIX + kvp.getKey(), decodedBytes);
        redisTemplate.opsForValue().set(STRING_KEY_PREFIX + kvp.getKey(), decodedString);

//        redisTemplate.opsForValue().set("\xac\xed\x00\x05t\x00\brebeyond", decodedString);

        return kvp;
    }

    @PostMapping("/strings/set")
    @ResponseStatus(HttpStatus.CREATED)
    public String setPayload(@RequestBody String payload) {
        System.out.println("Receive payload: " + payload);
        byte[] decodedBytes = Base64.getDecoder().decode(payload);
        String decodedString = new String(decodedBytes);
        System.out.println(new String(Base64.getDecoder().decode(RAW_KEY_IN_BASE64)));
//        jedisConnectionFactory.getConnection().set(Base64.getDecoder().decode(STRING_KEY_PREFIX), decodedBytes);
        jedisConnectionFactory.getConnection().set(STRING_KEY_PREFIX.getBytes(StandardCharsets.UTF_8), decodedBytes);
//        redisTemplate.opsForValue().set(STRING_KEY_PREFIX, decodedString);
//        jedisConnectionFactory.getConnection().

//        redisTemplate.opsForValue().set(STRING_KEY_PREFIX, payload);
        return "set success";
    }

    @GetMapping("/strings/get")
    public String getPayload() {
        Object value =  redisTemplate.opsForValue().get(STRING_KEY_PREFIX);
        byte[] tmp = jedisConnectionFactory.getConnection().get(STRING_KEY_PREFIX.getBytes(StandardCharsets.UTF_8));
        OxmSerializer jdkSerializationRedisSerializer = new OxmSerializer();
        redisTemplate.getValueSerializer().deserialize(tmp);
//        jdkSerializationRedisSerializer.deserialize(tmp);
//        System.out.println(value);
//        System.out.println(jedisConnectionFactory.getConnection().get(STRING_KEY_PREFIX.getBytes(StandardCharsets.UTF_8)));
        return "get success";
    }

    @GetMapping("/strings/{key}")
    public Map.Entry<String, String> getString(@PathVariable("key") String key) {
        Object value =  redisTemplate.opsForValue().get(STRING_KEY_PREFIX + key);

        return new SimpleEntry<String, String>(key, (String)value);
    }

}
