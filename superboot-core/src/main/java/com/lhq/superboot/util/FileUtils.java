package com.lhq.superboot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Description: 文件工具类
 * @author: lct
 * @date: 2019年4月25日 下午1:29:04
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static String saveFile(MultipartFile file, String fileName, String suffix, String savePath) throws IllegalStateException, IOException {
        fileName = changName(fileName, suffix);
        File desFile = new File(savePath, fileName);
        if (!desFile.getParentFile().exists()) {
            desFile.mkdirs();
        }

        file.transferTo(desFile);
        return fileName;
    }

    private static String changName(String fileName, String suffix) {

        String time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        int rad = new Random().nextInt(900000000);
        String newName = fileName + "_" + time + "_" + rad + "." + suffix;
        logger.debug("文件新名称：" + newName);
        return newName;
    }

    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

}
