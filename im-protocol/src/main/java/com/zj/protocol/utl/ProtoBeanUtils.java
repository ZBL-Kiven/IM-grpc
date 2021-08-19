package com.zj.protocol.utl;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

/**
 * The GETTER and SETTER fields of the two objects that are converted to each other must match exactly.
 * in addition, for enum and bytes in Proto Bean, follow the following rules when converting with POJO:
 */
public class ProtoBeanUtils {

    /**
     * Convert ProtoBean objects into POJO objects.
     *
     * @param destPojoClass The class type of the target POJO object.
     * @param sourceMessage The ProtoBean object instance containing the data .
     * @param <PojoType>    The class type paradigm of the target POJO object.
     */
    public static <PojoType> PojoType toPojoBean(Class<PojoType> destPojoClass, Message sourceMessage) throws IOException {
        if (destPojoClass == null) {
            throw new IllegalArgumentException("No destination pojo class specified");
        }
        if (sourceMessage == null) {
            throw new IllegalArgumentException("No source message specified");
        }
        String json = JsonFormat.printer().print(sourceMessage);
        return new Gson().fromJson(json, destPojoClass);
    }

    /**
     * Convert POJO object into ProtoBean object
     *
     * @param destBuilder    Builder class of target Message object.
     * @param sourcePojoBean POJO object containing data.
     */
    public static void toProtoBean(Message.Builder destBuilder, Object sourcePojoBean) throws IOException {
        if (destBuilder == null) {
            throw new IllegalArgumentException("No destination message builder specified");
        }
        if (sourcePojoBean == null) {
            throw new IllegalArgumentException("No source pojo specified");
        }
        String json = new Gson().toJson(sourcePojoBean);
        JsonFormat.parser().merge(json, destBuilder);
    }
}