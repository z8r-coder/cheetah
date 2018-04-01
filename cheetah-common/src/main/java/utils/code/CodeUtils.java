package utils.code;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 编码工具类
 * Created by rx on 2017/9/2.
 */
public class CodeUtils {

    private static final Map<String, TypeKind> KINDS = initKinds();

    /**
     * 类型集合初始化
     * @return
     */
    private static Map<String, TypeKind> initKinds() {
        Map<String, TypeKind> kinds = new HashMap<String, TypeKind>();

        kinds.put("boolean", TypeKind.BOOLEAN);

        kinds.put("byte", TypeKind.BYTE);

        kinds.put("char", TypeKind.CHAR);

        kinds.put("short", TypeKind.SHORT);

        kinds.put("int", TypeKind.INT);

        kinds.put("long", TypeKind.LONG);

        kinds.put("float", TypeKind.FLOAT);

        kinds.put("double", TypeKind.DOUBLE);

        kinds.put("java.lang.String", TypeKind.STRING);

        kinds.put("java.lang.Boolean", TypeKind.BOOLEAN_OBJ);

        kinds.put("java.lang.Byte", TypeKind.BYTE_OBJ);

        kinds.put("java.lang.Character", TypeKind.CHAR_OBJ);

        kinds.put("java.lang.Short", TypeKind.SHORT_OBJ);

        kinds.put("java.lang.Integer", TypeKind.INT_OBJ);

        kinds.put("java.lang.Long", TypeKind.LONG_OBJ);

        kinds.put("java.lang.Float", TypeKind.FLOAT_OBJ);

        kinds.put("java.lang.Double", TypeKind.DOUBLE_OBJ);

        kinds.put("java.util.Date", TypeKind.DATE);

        return kinds;
    }

    /**
     * 除了 NULL的所有类型
     * @param type
     * @return
     */
    public static TypeKind kindOf(Class<?> type) {
        String typeName = type.getName();
        TypeKind kind = CodeUtils.KINDS.get(typeName);

        if (kind == null) {
            kind = TypeKind.OBJECT;
        }

        return kind;
    }

    /**
     * 所有类型包括NULL
     * @param value
     * @return
     */
    public static TypeKind kindOf(Object value) {
        if (value == null) {
            return TypeKind.NULL;
        }

        String typeName = value.getClass().getName();
        TypeKind kind = CodeUtils.KINDS.get(typeName);

        if (kind == null) {
            kind = TypeKind.OBJECT;
        }

        return kind;
    }

    /**
     * 序列化对象
     * @param value
     * @return
     */
    public static byte[] serialize(Object value) {
        ByteArrayOutputStream output = null;
        ObjectOutputStream out = null;
        try {
            output = new ByteArrayOutputStream();
            out = new ObjectOutputStream(output);
            out.writeObject(value);

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 反序列化
     * @param buf
     * @return
     */
    public static Object deserialize(byte[] buf) {
        ByteArrayInputStream input = null;
        ObjectInputStream in = null;
        try {
            input = new ByteArrayInputStream(buf);
            in = new ObjectInputStream(input);
            Object obj = in.readObject();
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 序列化对象
     * @param value
     * @param buf
     */
    public static void serialize(Object value, ByteBuffer buf) {
        byte[] bytes = serialize(value);
        buf.putInt(bytes.length);//4字节
        buf.put(bytes);
    }

    /**
     * 获取对象序列化后字节长度
     * @param value
     * @return
     */
    public static int getBytesArrLength(Object value) {
        byte[] bytes = serialize(value);
        return bytes.length;
    }

    /**
     * 反序列化对象
     * @param buf
     * @return
     */
    public static Object deserialize(ByteBuffer buf) {
        int len = buf.getInt();
        byte[] bytes = new byte[len];
        buf.get(bytes);
        return deserialize(bytes);
    }
    /**
     * 对不同类型进行字节编码
     * @param value
     * @param buf
     */
    public static void encode(Object value, ByteBuffer buf) {
        TypeKind kind = kindOf(value);

        int ordinal = kind.ordinal();
        assert ordinal < 128;

        byte kindCode = (byte) ordinal;//1字节
        buf.put(kindCode);

        switch (kind) {
            case NULL:
                //nothing to do
                break;
            case BOOLEAN:
            case BYTE:
            case SHORT:
            case CHAR:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                throw Errors.notExpected();
            case STRING:
                String str = (String) value;
                byte[] bytes = str.getBytes();
                //0-255
                int len = bytes.length;
                if (len < 255) {
                    byte len2 = (byte) (len - 128);
                    buf.put(len2);
                } else {
                    byte len2  = (byte) (255 - 128);
                    buf.put(len2);
                    buf.putInt(len);
                }
                buf.put(bytes);
                break;

            case BOOLEAN_OBJ:
                Boolean val = (Boolean) value;
                buf.put((byte) (val? 1:0));
                break;
            case BYTE_OBJ:
                buf.put((Byte) value);
                break;
            case SHORT_OBJ:
                buf.putShort((Short) value);
                break;
            case CHAR_OBJ:
                buf.putChar((Character) value);
                break;
            case INT_OBJ:
                buf.putInt((Integer) value);
                break;
            case LONG_OBJ:
                buf.putLong((Long) value);
                break;
            case FLOAT_OBJ:
                buf.putFloat((Float) value);
                break;
            case DOUBLE_OBJ:
                buf.putDouble((Double) value);
                break;

            case OBJECT:
                serialize(value, buf);
                break;

            case DATE:
                buf.putLong(((Date) value).getTime());
                break;

            default:
                throw Errors.notExpected();
        }
    }

    /**
     * 解码
     * @param buf
     * @return
     */
    public static Object decode(ByteBuffer buf) {
        //获取ordinary,并且切换成读模式
        buf.flip();
        byte kindCode = buf.get();

        TypeKind kind = TypeKind.values()[kindCode];

        switch (kind) {

            case NULL:
                return null;

            case BOOLEAN:
            case BOOLEAN_OBJ:
                return buf.get() != 0;

            case BYTE:
            case BYTE_OBJ:
                return buf.get();

            case SHORT:
            case SHORT_OBJ:
                return buf.getShort();

            case CHAR:
            case CHAR_OBJ:
                return buf.getChar();

            case INT:
            case INT_OBJ:
                return buf.getInt();

            case LONG:
            case LONG_OBJ:
                return buf.getLong();

            case FLOAT:
            case FLOAT_OBJ:
                return buf.getFloat();

            case DOUBLE:
            case DOUBLE_OBJ:
                return buf.getDouble();

            case STRING:
                byte len = buf.get();
                int realLen = len + 128;
                if (realLen == 255) {
                    realLen = buf.getInt();
                }
                byte[] sbuf = new byte[realLen];
                buf.get(sbuf);
                return new String(sbuf);

            case OBJECT:
                return deserialize(buf);

            case DATE:
                return new Date(buf.getLong());

            default:
                throw Errors.notExpected();
        }
    }

    public static byte[] buf2bytes(ByteBuffer buf) {
        ByteBuffer buf2 = buf.duplicate();

        buf2.rewind();
        buf2.limit(buf2.capacity());

        byte[] bytes = new byte[buf2.capacity()];
        buf2.get(bytes);

        return bytes;
    }

}
