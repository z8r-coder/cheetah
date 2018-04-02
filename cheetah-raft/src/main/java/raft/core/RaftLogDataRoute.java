package raft.core;

import raft.utils.RaftUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author ruanxin
 * @create 2018-04-01
 * @desc 日志数据路由,no status
 */
public class RaftLogDataRoute {



    public static void main(String[] args) {
        File file = new File("/Users/ruanxin/IdeaProjects/cheetah/raft/3.txt");
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.writeLong(1l);
            randomAccessFile.writeLong(2l);
            byte[] strByte = "testtest".getBytes();
            randomAccessFile.writeInt(strByte.length);
            randomAccessFile.write(strByte);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            System.out.println(randomAccessFile.readLong());
            System.out.println(randomAccessFile.readLong());
            int lenth = randomAccessFile.readInt();
            byte[] buf = new byte[lenth];
            randomAccessFile.read(buf);
            String test = new String(buf);
            System.out.println(test);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
