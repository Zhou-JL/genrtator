package com.zhoujl.demo.rpcservice.service.Impl;


import com.zhoujl.demo.rpcservice.entity.FileUserInfo;
import com.zhoujl.demo.rpcservice.service.FileUpService;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**

 */
@Slf4j
@Service
public class FileUpServiceImpl implements FileUpService {

    @Override
    public String uploadFileSaveDb(String filePath, byte[] data, boolean append, FileUserInfo fileUserInfo) throws Exception {
        System.out.println("上传文件调用-uploadFileSaveDb----------------");
        String uploadResult = uploadStream(filePath, data, append);
        log.info("上传结果：{}", uploadResult);
        return "上传文件调用-uploadFileSaveDb";
    }

    @Override
    public String uploadFileSaveDb(String filePath, InputStream stream, FileUserInfo fileUserInfo) throws Exception {
        System.out.println("上传文件调用-uploadFileSaveDb+++++++++++++++++");
        String uploadResult = uploadStream(filePath, stream);
        log.info("上传结果：{}", uploadResult);

        return "上传文件调用-uploadFileSaveDb";
    }

    @Override
    public String testDubbo(FileUserInfo fileUserInfo) {
        System.out.println("上传======================");
        return "上传文件调用-uploadFileSaveDb";
    }

    @Override
    public String testDubbo() {
        System.out.println("上传======================");
        return "上传======================";
    }


    String uploadStream(String filePath, InputStream inputStream){
        log.info("***************文件上传开始2***************");
        Long start = Long.valueOf(System.currentTimeMillis());
        int limit = 4194304;
        byte[] buffer = new byte[limit];

        File outputFile = new File("/data/fileserver", filePath);
        File filePDir = outputFile.getParentFile();
        if(!filePDir.exists()){
            filePDir.mkdirs();
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outputFile);
            int len = 0;
            while((len = inputStream.read(buffer)) != -1){
                fileOutputStream.write(buffer, 0, len);
            }
            log.info("***************文件上传结束*************** 耗时{}", Long.valueOf(System.currentTimeMillis() - start));
        } catch (Exception e) {

            e.printStackTrace();
        }finally {
            try {
                fileOutputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return filePath;
    }



    String uploadStream(String filePath, byte[] dataByte, boolean append){
        log.info("***************文件上传开始1***************");
        Long start = Long.valueOf(System.currentTimeMillis());
        StringBuilder sb = checkPath(filePath, null);

        if (sb == null) {
            return null;
        }
        String savePath = sb.toString();

        try {
            File outputFile = new File("/data/fileserver", filePath);
            log.info("outputFile: "+outputFile);
            File filePDir = outputFile.getParentFile();
            System.out.println("filePDir: "+filePDir);
            System.out.println(filePDir.exists());
            if(!filePDir.exists()){
                log.info("mkdir");
                filePDir.mkdirs();
            }
            FileOutputStream oSavedFile = new FileOutputStream(outputFile, append);
            log.info("oSavedFile: "+oSavedFile);
            log.info("dataByte: "+dataByte.length);
            oSavedFile.write(dataByte);
            oSavedFile.close();
            log.debug("耗时{}", Long.valueOf(System.currentTimeMillis() - start.longValue()));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return savePath;

    }


    StringBuilder checkPath(String path, String appPath){
        String[] pathSplit;
        if(path == null || "".equals(path)){
            return null;
        }

        if (path.indexOf("..") > 0) {
            return null;
        }

        if (path.indexOf("\\") >= 0) {

            pathSplit = path.split("\\\\");
        } else {
            pathSplit = path.split("/");
        }
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pathSplit.length; i++) {
            String foldName = pathSplit[i];
            if (foldName != null && !"".equals(foldName)) {
                sb.append("/");
                sb.append(foldName);
                if (first && appPath != null) {
                    sb.append("/").append(appPath);
                }
                first = false;
            }
        }
        return sb;
    }









    }
