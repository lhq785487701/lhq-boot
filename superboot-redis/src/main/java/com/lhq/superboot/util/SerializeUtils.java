package com.lhq.superboot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.*;

/**
 * @Description: redis序列化
 * @author: lihaoqi
 * @date: 2019年4月19日
 * @version: 1.0.0
 */
@SuppressWarnings("rawtypes")
public class SerializeUtils implements RedisSerializer {

    private static Logger logger = LoggerFactory.getLogger(SerializeUtils.class);

    public static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    /**
     * @param object
     * @return
     * @throws SerializationException
     * @Description: 序列化
     */
    @Override
    public byte[] serialize(Object object) throws SerializationException {
        byte[] result = null;

        if (object == null) {
            return new byte[0];
        }
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream)) {

            if (!(object instanceof Serializable)) {
                throw new IllegalArgumentException(
                        SerializeUtils.class.getSimpleName() + " requires a Serializable payload "
                                + "but received an object of type [" + object.getClass().getName() + "]");
            }

            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            result = byteStream.toByteArray();
        } catch (Exception ex) {
            logger.error("Failed to serialize", ex);
        }
        return result;
    }

    /**
     * @param bytes
     * @return
     * @throws SerializationException
     * @Description: 反序列化
     */
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {

        Object result = null;

        if (isEmpty(bytes)) {
            return null;
        }

        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteStream)) {
            result = objectInputStream.readObject();
        } catch (Exception e) {
            logger.error("Failed to deserialize", e);
        }
        return result;
    }

}
