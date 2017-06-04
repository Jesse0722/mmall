package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by jesse on 2017/6/4.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        boolean isUploadedFtp = false;
        //扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;

        logger.info("开始上传文件，上传文件的文件名：{},上传文件的路径:{},新文件名：{}",fileName,path,uploadFileName);
        File fileDir = new File(path);
        //判断上传的文件夹是否存在
        if(!fileDir.exists()){
            //设置该文件写权限
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //将上传的MultipartFile文件转存到upload文件夹中
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);

            isUploadedFtp = FTPUtil.uploadFile(Lists.newArrayList(targetFile));//上传到FTP,并返回上传结果
            targetFile.delete();//删除临时上传文件
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        //如果上传ftp成功则返回上传文件的name，否则返回空
        return isUploadedFtp?targetFile.getName():null;
    }
}
