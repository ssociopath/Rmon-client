package utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author bobo
 * @date 2021/6/22
 */

@Slf4j
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }
    /**
     * 按字段转换成JSON
     *
     * @apiNote 会忽略@JsonProperty注解
     */
    public static String toJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Json解析错误 {}", object, e);
        }
        return object.toString();
    }

    /**
     * JSON转换成字段
     */
    public static JsonNode parse(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("Json解析错误 {}", json, e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return OBJECT_MAPPER.createObjectNode();
    }
    public static <T> T parseObject(JsonNode json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.treeToValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Json解析错误 {}", json, e);
        }
        return null;
    }
    public static <T> T parseObject(String json, Class<T> clazz) {
        return parseObject(parse(json), clazz);
    }

    @SneakyThrows
    public static <T> List<T> parseList(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, clazz));
        } catch (JsonProcessingException e) {
            log.error("Json解析错误 {}", json, e);
        }
        return new ArrayList<>();
    }

    public static <T> Stream<T> asStream(String jsonString, Class<T> clazz) {
        return StreamSupport.stream(parse(jsonString).spliterator(), false).map(json -> parseObject(json, clazz));
    }

    public static <T> List<T> asList(String json, Class<T> clazz) {
        return asStream(json, clazz).collect(Collectors.toList());
    }

}
