package com.szhq.iemp.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.codec.Base64;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ListTranscoder<M extends Serializable> extends SerializeTranscoder {

    @SuppressWarnings("unchecked")
    @Override
    public String serialize(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null");
        }
        List<M> values = (List<M>) value;
        byte[] results = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;

        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            for (M m : values) {
                os.writeObject(m);
            }
            os.writeObject(null);//不加会报java.io.EOFException异常
            os.close();
            bos.close();
            results = bos.toByteArray();
        } catch (IOException e) {
            log.error("e", e);
            throw new IllegalArgumentException("Non-serializable object", e);
        } finally {
            close(os);
            close(bos);
        }
        return Base64.encodeToString(results);
    }


    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(String in) {
        List<M> list = new ArrayList<M>();
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (in != null) {
                bis = new ByteArrayInputStream(Base64.decode(in));
                is = new ObjectInputStream(bis);
                while (true) {
                    M m = (M) is.readObject();
                    if (m == null) {
                        break;
                    }
                    list.add(m);
                }
                is.close();
                bis.close();
            }
        } catch (IOException e) {
            log.error("e", e);
        } catch (ClassNotFoundException e) {
            log.error("e", e);
        } finally {
            close(is);
            close(bis);
        }
        return list;
    }

}
