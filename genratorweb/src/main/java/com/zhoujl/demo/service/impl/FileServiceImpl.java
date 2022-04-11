package com.zhoujl.demo.service.impl;

import com.zhoujl.demo.config.FinanceProperties;
import com.zhoujl.demo.rpcservice.entity.FileUserInfo;
import com.zhoujl.demo.rpcservice.service.FileUpService;
import com.zhoujl.demo.service.FileService;
import com.zhoujl.demo.utils.HtUploadUtil;
import com.zhoujl.demo.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSON;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-3-23 10:41
 * @see: com.zhoujl.demo.service.impl
 * @Version: 1.0
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    FinanceProperties financeProperties;


    @com.alibaba.dubbo.config.annotation.Reference
    FileUpService fileUpService;


    private static long count = 0;




    @Override
    public void fileUpload(boolean showDirect, MultipartFile xlsFile, String table,
                           HttpServletRequest request, HttpServletResponse response) {
        String savePath;
        if (showDirect) {
            //上传浏览器可访问文件
            savePath = financeProperties.getFileServer().getPublicFileUrl() + "/";
        } else {
            //上传浏览器访问会下载文件
            savePath = financeProperties.getFileServer().getUploadFileUrl() + "/";
        }

        if (StringUtils.isNotEmpty(table)) {
            savePath = savePath + table + "/";
        }

        try{
            //路径拼接上传时间
            savePath = uploadFilePath(savePath);
            log.info("----savePath: "+savePath);
            String fileName = xlsFile.getOriginalFilename();
            String extendName = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            if(count == 10L){
                count = 0;
            }
            Long lastTime = System.currentTimeMillis();
            String fileAddName = lastTime + "" + String.format("%03d", count);
            String name = fileAddName + extendName;

            //本地文件数据
            File saveFile = new File(this.getDiskPath(request, savePath, name));
            //本地缓存
            xlsFile.transferTo(saveFile);
            //携带信息
            log.info("当前用户信息为测试数据*************************");
            FileUserInfo fileUserInfo = new FileUserInfo();
            fileUserInfo.setUserName("测试用户wu");
            fileUserInfo.setTime(new Timestamp(System.currentTimeMillis()));
            fileUserInfo.setAppCode("001");
            savePath = this.uploadFile(savePath, name, saveFile, fileUserInfo);
            log.info("===================文件开始上传===================");
            saveFile.delete();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("filesize", xlsFile.getSize());
            resultMap.put("filepath", savePath);
            resultMap.put("name", fileName);
            resultMap.put("extendName", extendName);
            writer.append(JSON.toJSONString(resultMap));

        }catch (Exception e){
            e.printStackTrace();
            log.error("上传文件异常：{}", e.getMessage());
        }
    }


    public String uploadFile(String parentPath, String fileName, File file, FileUserInfo fileUserInfo) throws Exception {
        String filePath = this.generateFilePath(parentPath, fileName);
        log.info("===========*********filePath: "+filePath);
        try {
            this.writeOutputStream(filePath, new FileInputStream(file), fileUserInfo);
            return filePath;
        } catch (IOException var7) {
            var7.printStackTrace();
            return null;
        }
    }



    protected String uploadFilePath(String savePath) throws Exception {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        String dateString = formatter.format(date);
        savePath = savePath + dateString + "/";
        return savePath;
    }

    protected String getDiskPath(HttpServletRequest request, String savePath, String name) {
        ServletContext sc = request.getServletContext();
        String path = sc.getRealPath(savePath);
        File direct = new File(path);
        if (!direct.exists()) {
            direct.mkdirs();
        }

        return direct.getAbsolutePath() + "/" + name;
    }

    private String generateFilePath(String parentPath, String fileName) {
        StringBuilder sb = HtUploadUtil.checkPath(parentPath);
        if (!org.springframework.util.StringUtils.isEmpty(fileName)) {
            sb.append("/");
            sb.append(fileName);
        }

        return sb.toString();
    }

    private void writeOutputStream(String filePath, InputStream inStream, FileUserInfo fileUserInfo) throws Exception {
        try {
            if (this.getStreamSupported()) {
                fileUpService.uploadFileSaveDb(filePath, inStream, fileUserInfo);
                return;
            }

            byte[] buffer = new byte[1048576];

            int len;
            for(boolean append = false; (len = inStream.read(buffer)) != -1; append = true) {
                if (len == 1048576) {
                    fileUpService.uploadFileSaveDb(filePath, buffer, append, fileUserInfo);
                } else {
                    fileUpService.uploadFileSaveDb(filePath, subBytes(buffer, 0, len), append, fileUserInfo);
                }
            }
        } finally {
            inStream.close();
        }

    }


    private Boolean getStreamSupported() {
        return this.financeProperties != null && this.financeProperties.getFileServer() != null ? this.financeProperties.getFileServer().getStreamSupported() : false;
    }


    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];

        for(int i = begin; i < begin + count; ++i) {
            bs[i - begin] = src[i];
        }

        return bs;
    }

}
