package com.zhoujl.demo.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-3-23 10:39
 * @see: com.zhoujl.demo.rpcservice.service
 * @Version: 1.0
 */
public interface FileService {


    void fileUpload(boolean showDirect, MultipartFile xlsFile, String table, HttpServletRequest request, HttpServletResponse response);


}
