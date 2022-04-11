package com.zhoujl.demo.rpcservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUserInfo implements Serializable {
    private String userName;
    private String fileName;
    private Timestamp time;
    private String sign;
    private String appCode;
    private String actionFlag;
    private Boolean showDirect = false;
    private String path;
}
