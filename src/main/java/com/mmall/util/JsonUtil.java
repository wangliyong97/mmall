package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;


/**
 * Created by wangliyong on 2018/7/9.
 */
@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();
    static{
        //将对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);

        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        //忽略空bean转换json错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        //将所有日期改为指定格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //忽略json中存在但在java对象中不存在的对应属性的情况，放置错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static <T>String obj2String(T obj){
        if(obj == null)
            return null;

        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error",e);
            return null;
        }
    }

    //相比于前者是返回格式化好的
    public static <T>String obj2StringPretty(T obj){
        if(obj == null)
            return null;
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error",e);
            return null;
        }
    }


    public static <T> T String2obj (String str,Class<T> clazz){
        if(StringUtils.isEmpty(str)|| clazz == null)
            return null;
        try {
            return clazz.equals(String.class) ? (T)str : objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }


    public static <T> T String2obj (String str, TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str)|| typeReference == null)
            return null;
        try {
            return (T)(typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str,typeReference));
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    public static <T> T String2obj (String str, Class<?> collectionClass, Class<?>... elementClass){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClass);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

//    public static void main(String[] args) {
//        User u1 = new User();
//        u1.setId(1);
//        u1.setUsername("wang");
//
//        User u2 = new User();
//        u2.setId(2);
//        u2.setUsername("dai");
//
//        String u1Str = JsonUtil.obj2String(u1);
//        String u2Str = JsonUtil.obj2String(u2);
//
//        String u1StrPretty = JsonUtil.obj2StringPretty(u1);
//
//        List<User> list = new ArrayList<>();
//
//        list.add(u1);
//        list.add(u2);
//
//        String userListStr = JsonUtil.obj2StringPretty(list);
//
//        List<User> userList = JsonUtil.String2obj(userListStr , new TypeReference<List<User>>(){ });
//
//        List<User> users = JsonUtil.String2obj(userListStr, List.class,User.class);
//
//        log.info("JsonString{}",u1StrPretty);
//    }

}
