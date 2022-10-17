package com.imooc.dmp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**************************
 * redis 配置
 *
 * Springboot操作redis,主要是通过RedisTemplate
 * 序列化：Java全部都是对象，对象的储存和传输都要进行序列化。
 ************************************
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        //序列化器的配置
        //StringRedisSerializer,GenericJackson2JsonRedisSerializer

        //将key序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //或者：redisTemplate.setKeySerializer(StringRedisSerializer.toString());
        //将value序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

}
