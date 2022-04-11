package com.zhoujl.demo.rpcservice.service;



import com.zhoujl.demo.rpcservice.entity.FileUserInfo;

import java.io.InputStream;

/**
 */
public interface FileUpService {

    String uploadFileSaveDb(String filePath, byte[] data, boolean append, FileUserInfo fileUserInfo) throws Exception;

    String uploadFileSaveDb(String filePath, InputStream stream, FileUserInfo fileUserInfo) throws Exception;

    String testDubbo(FileUserInfo fileUserInfo);

    String testDubbo();
}
