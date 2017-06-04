package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by jesse on 2017/6/4.
 */
public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static final String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static final String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static final String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pw;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String user, String pw) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pw = pw;
    }

    /***
     * 上传文件集合
     * @param fileList
     * @return
     */
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("ftpIp:{},port:{},user:{},password:{}",ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("开始连接ftp服务器,结束上传，上传结果:{}",result);
        return result;
    }

    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = false;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectFTPServer(this.ip,this.port,this.user,this.pw)){
            //通过remotePath参数，更换工作目录
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);//设置缓冲区
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置文件类型
                ftpClient.enterLocalPassiveMode();//设置被动模式
                for(File fileItem:fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
                uploaded=true;
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;
                e.printStackTrace();
            }finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private boolean connectFTPServer(String ip,int port,String user,String pd){
        boolean isSuccess =false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip,port);
            isSuccess = ftpClient.login(user,pd);

        } catch (IOException e) {
            logger.error("连接ftp服务器异常",e);
            e.printStackTrace();
        }
        return isSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
