package rpc.nio;

import rpc.RpcObject;
import rpc.constants.Constants;
import rpc.constants.RpcType;
import rpc.exception.RpcException;
import rpc.utils.ArraysUtils;
import rpc.utils.NioUtils;
import rpc.utils.RpcUtils;

/**
 * @Author:Roy
 * @Date: Created in 13:49 2017/10/15 0015
 */
public class RpcNioBuffer {
    private byte[] buf;
    private int readIndex;
    private int writeIndex;

    public RpcNioBuffer() {

    }

    public RpcNioBuffer(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        buf = new byte[size];
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity - buf.length > 0) {
            grow(minCapacity);
        }
    }

    public void write(byte b[], int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(writeIndex + len);
        System.arraycopy(b, off, buf, writeIndex, len);
        writeIndex += len;
    }

    public void write(byte b[]) {
        write(b, 0, b.length);
    }

    public void reset() {
        writeIndex = 0;
    }

    public byte toByteArray()[] {
        return ArraysUtils.copyOfRange(buf, readIndex, writeIndex);
    }

    @Override
    public String toString() {
        return new String(buf, readIndex, writeIndex);
    }

    public synchronized int size() {
        return writeIndex - readIndex;
    }

    public void compact() {
        if (readIndex > 0) {
            for (int i = readIndex;i < writeIndex;i++) {
                buf[i - readIndex] = buf[i];
            }
            writeIndex = writeIndex - readIndex;
            readIndex = 0;
        }
    }

    public boolean hasRpcObject() {
        if (writeIndex - readIndex >= Constants.RPC_PROTOCOL_HEAD_LEN) {
            byte[] lenbuf = new byte[4];
            System.arraycopy(buf, readIndex + 16, lenbuf, 0, 4);
            int len = RpcUtils.bytesToInt(lenbuf);
            if (writeIndex - readIndex >= NioUtils.RPC_PROTOCOL_HEAD_LEN + len) {
                return true;
            }
        }
        return false;
    }

    public void writeInt(int i) {
        byte[] bytes = RpcUtils.intToBytes(i);
        this.write(bytes);
    }

    public void writeLong(long v) {
        byte[] bytes = RpcUtils.longToBytes(v);
        this.write(bytes);
    }

    public int readInt() {
        byte[] intBuf = new byte[4];
        System.arraycopy(buf, readIndex, intBuf,0,4);
        readIndex += 4;
        return RpcUtils.bytesToInt(intBuf);
    }

    public long readLong() {
        byte[] longBuf = new byte[8];
        System.arraycopy(buf, readIndex, longBuf, 0, 8);
        readIndex += 8;
        return RpcUtils.bytesToLong(longBuf);
    }
    public byte[] readBytes(int len) {
        byte[] byteBuf = new byte[len];
        System.arraycopy(buf, readIndex, byteBuf, 0, len);
        readIndex += len;
        return byteBuf;
    }

    public byte[] readBytes() {
        int len = writeIndex - readIndex;
        byte[] byteBuf = new byte[len];
        System.arraycopy(buf, readIndex, byteBuf, 0, len);
        readIndex += len;
        if (readIndex > buf.length / 2) {
            this.compact();
        }
        return byteBuf;
    }

    public void clear() {
        readIndex = 0;
        writeIndex = 0;
    }

    public void writeRpcObject(RpcObject rpcObject) {
        this.writeInt(rpcObject.getType().getType());
        this.writeLong(rpcObject.getThreadId());
        this.writeInt(rpcObject.getIndex());
        this.writeInt(rpcObject.getLength());
        if (rpcObject.getLength() > 0) {
            this.write(rpcObject.getData());
        }
    }

    public RpcObject readRpcObject() {
        RpcObject rpcObject = new RpcObject();
        int type = this.readInt();
        rpcObject.setType(RpcType.getByType(type));
        rpcObject.setThreadId(this.readLong());
        rpcObject.setIndex(this.readInt());
        rpcObject.setLength(this.readInt());
        if (rpcObject.getLength() > 0) {
            if (rpcObject.getLength() > RpcUtils.MEM_1M) {
                throw new RpcException("rpc data is too long " + rpcObject.getLength());
            }
            rpcObject.setData(this.readBytes(rpcObject.getLength()));
        }
        if (readIndex > buf.length / 2) {
            this.compact();
        }
        return rpcObject;
    }

    private void grow(int minCapacity) {
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        if (newCapacity < 0) {
            if (minCapacity < 0)
                throw new OutOfMemoryError();
            newCapacity = Integer.MAX_VALUE;
        }
       buf = ArraysUtils.copyOf(buf, newCapacity);
    }
}
