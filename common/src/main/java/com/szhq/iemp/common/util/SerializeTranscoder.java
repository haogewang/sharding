package com.szhq.iemp.common.util;

import java.io.Closeable;

public abstract class SerializeTranscoder {

    public abstract String serialize(Object value);

    public abstract Object deserialize(String in);

    public void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
