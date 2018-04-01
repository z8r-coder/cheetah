package raft.core;

import constants.Globle;
import raft.utils.RaftUtils;
import utils.ParseUtils;
import utils.StringUtils;
import utils.code.CodeUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author ruanxin
 * @create 2018-04-01
 * @desc 日志数据路由
 */
public class RaftLogDataRoute {
    //log meta data path
    private String logMetaDataPath;
    //meta data map
    private Map<String, SegmentMetaData> logMetaDataMap = new TreeMap<String, SegmentMetaData>();
    //meta data file dir
    private String metaDataDir;
    //meta data file name
    private String fileName;
    //map into metaDataFile
    private RandomAccessFile randomAccessFile;

    public RaftLogDataRoute (String logMetaDataPath, String metaDataDir,String fileName) {
        this.metaDataDir = metaDataDir;
        this.fileName = fileName;
        this.logMetaDataPath = logMetaDataPath;
        randomAccessFile = RaftUtils.openFile(metaDataDir, fileName, "wr");
    }

    public static class SegmentMetaData {
        public long startIndex;
        public long endIndex;
        public String fileName;

        public SegmentMetaData(long startIndex, long endIndex, String fileName) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.fileName = fileName;
        }
    }
    public static class GlobleMetaData {
        //last segment log
        private String lastSegmentLogName;
        //last index
        private long lastIndex;
        public GlobleMetaData (String lastSegmentLogName, long lastIndex) {
            this.lastIndex = lastIndex;
            this.lastSegmentLogName = lastSegmentLogName;
        }
    }

    public void readMetaData() throws IOException {
        List<String> fileNameList = RaftUtils.getSortedFilesInDir(logMetaDataPath, logMetaDataPath);
        for (String fileName : fileNameList) {
            String[] fileMeta = ParseUtils.parseByPoint(fileName);
            if (!StringUtils.equals(fileMeta[1], "rl")) {
               continue;
            }
            String[] metaArray = fileMeta[0].split("-");
            SegmentMetaData metaData = new SegmentMetaData(Long.parseLong(metaArray[0]),
                    Long.parseLong(metaArray[1]), fileName);
            logMetaDataMap.put(fileName, metaData);
        }

//        RaftUtils
    }

    public String getLogMetaDataPath() {
        return logMetaDataPath;
    }

    public void setLogMetaDataPath(String logMetaDataPath) {
        this.logMetaDataPath = logMetaDataPath;
    }

    public Map<String, SegmentMetaData> getLogMetaDataMap() {
        return logMetaDataMap;
    }

    public void setLogMetaDataMap(Map<String, SegmentMetaData> logMetaDataMap) {
        this.logMetaDataMap = logMetaDataMap;
    }

    public String getMetaDataDir() {
        return metaDataDir;
    }

    public void setMetaDataDir(String metaDataDir) {
        this.metaDataDir = metaDataDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }

    public void setRandomAccessFile(RandomAccessFile randomAccessFile) {
        this.randomAccessFile = randomAccessFile;
    }

    public static void main(String[] args) {
        File file = new File("/Users/ruanxin/IdeaProjects/cheetah/raft/3.txt");
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
//            randomAccessFile.writeLong(1l);
//            randomAccessFile.writeLong(2l);
            ByteBuffer buffer = ByteBuffer.allocate(512);
            CodeUtils.encode("testtest", buffer);
            FileChannel fileChannel = randomAccessFile.getChannel();
            while (buffer.hasRemaining()) {
                fileChannel.write(buffer);
            }
            fileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ByteBuffer b = ByteBuffer.allocate(512);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            FileChannel fileChannel = randomAccessFile.getChannel();
//            System.out.println(randomAccessFile.readLong());
//            System.out.println(randomAccessFile.readLong());
            fileChannel.read(b);
            String res = (String) CodeUtils.decode(b);
            System.out.println(res);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
