package raft.utils;

import org.apache.log4j.Logger;
import raft.core.RaftLogDataRoute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ruanxin
 * @create 2018-03-01
 * @desc raft utils
 */
public class RaftUtils {

    private static final Logger logger = Logger.getLogger(RaftUtils.class);

//    public static RaftLogDataRoute.GlobleMetaData readGlobleMetaData(
//            RandomAccessFile randomAccessFile) {
//        try {
//            RaftLogDataRoute.GlobleMetaData metaData =
//                    new RaftLogDataRoute.GlobleMetaData(
//                            randomAccessFile.readLong(), randomAccessF);
//        } catch (IOException e) {
//            logger.warn("read globle meta data to file error");
//            throw new RuntimeException("read meta data to file error");
//        }
//
//    }

    public static void closeFile(RandomAccessFile randomAccessFile) {
        try {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        } catch (IOException ex) {
            logger.warn("close file error", ex);
        }
    }

    public static RandomAccessFile openFile (String dir, String fileName, String mode) {
        try {
            String fullFileName = dir + File.separator + fileName;
            File file = new File(fullFileName);
            return new RandomAccessFile(file, mode);
        } catch (FileNotFoundException e) {
            logger.warn("file not fount, file=" + fileName);
            throw new RuntimeException("file not found, file=" + fileName);
        }
    }

    public static void writeMetaToFile(RandomAccessFile file,
                                       String lastSegmentLogName, long lastIndex) {
        byte[] segLog = lastSegmentLogName.getBytes();
        try {
            file.writeLong(lastIndex);
            file.write(segLog);
        } catch (IOException e) {
            logger.warn("write meta data to file error, lastSegmentLogName=" + lastSegmentLogName +
            ", lastIndex=" + lastIndex);
            throw new RuntimeException("write meta data to file error");
        }
    }

    public static List<String> getSortedFilesInDir(String dirName, String rootDirName) throws IOException {
        List<String> fileList = new ArrayList<String>();
        File rootDir = new File(rootDirName);
        File dir = new File(dirName);
        if (!rootDir.isDirectory() || !dir.isDirectory()) {
            return fileList;
        }
        String rootPath = rootDir.getCanonicalPath();
        if (!rootPath.endsWith("/")) {
            rootPath = rootPath + "/";
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                fileList.addAll(getSortedFilesInDir(file.getCanonicalPath(), rootPath));
            } else {
                fileList.add(file.getCanonicalPath().substring(rootPath.length()));
            }
        }
        Collections.sort(fileList);
        return fileList;
    }

    public static void main(String[] args) {
        try {
            List<String> fileList = RaftUtils.getSortedFilesInDir("/Users/ruanxin/IdeaProjects/cheetah/raft",
                    "/Users/ruanxin/IdeaProjects/cheetah/raft");
            for (String fileName : fileList) {
                System.out.println(fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
