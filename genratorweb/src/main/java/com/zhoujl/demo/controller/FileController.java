package com.zhoujl.demo.controller;

import com.zhoujl.demo.service.FileService;
import com.zhoujl.demo.utils.HtFileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/file")
@Slf4j
@Api(tags = "文件控制—FileController")
public class FileController {


    @Autowired
    FileService fileService;

    /**
     * 上传文件，且文件是可以在浏览器访问的
     * */
    @PostMapping({"/uploadpublicFile.do"})
    @ApiOperation(value = "上传文件")
    public void uploadpublicFile(@RequestParam("file1") MultipartFile xlsFile, String table, HttpServletRequest request, HttpServletResponse response) {
        //文件扩展名验证
        if(!HtFileUtil.validFileExt(request)) {
            return;
        }
        fileService.fileUpload(true, xlsFile, table, request, response);
    }
}
