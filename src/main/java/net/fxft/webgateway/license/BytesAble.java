package net.fxft.webgateway.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * BytesAble
 */
public interface BytesAble extends Cloneable, Serializable {

    static final Logger log = LoggerFactory.getLogger(BytesAble.class);

    /**
     * 转换为byte
     *
     * @param obj
     * @return
     */
    static byte[] toBytes(BytesAble obj) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            bytes = bos.toByteArray();
            bos.close();
        } catch (Exception e) {
            log.error("toBytes出错！cls=" + obj.getClass().getName(), e);
        }
        return bytes;
    }

    /**
     * 转回
     *
     * @param bytes
     * @return
     */
    static BytesAble fromBytes(byte[] bytes) {
        BytesAble obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bis);
            obj = (BytesAble) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            log.error("fromBytes出错！", e);
        }
        return obj;
    }

    default byte[] toBytes() {
        return toBytes(this);
    }
}