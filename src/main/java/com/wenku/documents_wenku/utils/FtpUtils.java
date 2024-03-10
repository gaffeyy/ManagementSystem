package com.wenku.documents_wenku.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Date;

/**
 * FTP 工具类
 *
 * @author gaffey
 */
@Slf4j
@Component
@Configuration
public class FtpUtils {

	//ftp服务器地址
//	@Value("${upload.host}")
	private String hostname = "1.14.1.82";

	//ftp服务器端口
//	@Value("${upload.port}")
	private int port = 21;

	//ftp登录账号
//	@Value("${upload.username}")
	private String username = "ftpuser";

	//ftp登录密码
//	@Value("${upload.password}")
	private String password = "syj4527466";

	//ftp保存目录
//	@Value("${upload.bastPath}")
	private String basePath = "";


	/**
	 * 初始化ftp服务器
	 */
	public FTPClient getFtpClient() {
		FTPClient ftpClient = new FTPClient();
		ftpClient.setControlEncoding("utf-8");

		try {
			ftpClient.setDataTimeout(1000 * 120);//设置连接超时时间
			log.info("connecting...ftp服务器 ---- " + hostname + ":" + hostname);
			log.info("connecting...ftp服务器 ---- " + port + ":" + port);
			log.info("connecting...ftp服务器 ---- " + username + ":" + username);
			log.info("connecting...ftp服务器 ---- " + password + ":" + password);
			ftpClient.connect(hostname, port); // 连接ftp服务器
			ftpClient.login(username, password); // 登录ftp服务器
			int replyCode = ftpClient.getReplyCode(); // 是否成功登录服务器
			if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(
					"OPTS UTF8", "ON"))) {      // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
//				LOCAL_CHARSET = "UTF-8";
			}
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				log.error("connect failed...ftp服务器:" + hostname + ":" + port);
			}
			log.info("connect successfu...ftp服务器:" + hostname + ":" + port);
		} catch (MalformedURLException e) {

		} catch (IOException e) {

		}
		return ftpClient;
	}
	/**
	 * 上传文件
	 *
	 * @param targetDir    ftp服务保存地址
	 * @param fileName    上传到ftp的文件名
	 * @param inputStream 输入文件流
	 * @return
	 */
	public boolean uploadFileToFtp(String targetDir, String fileName, InputStream inputStream) {
		boolean isSuccess = false;
		targetDir = "Documents";
		String servicePath = String.format("%s%s%s", basePath, "/", targetDir,"/");
//		String servicePath = "/home/ftpuser/ftp/Documents";
		FTPClient ftpClient = getFtpClient();
		try {
			if (ftpClient.isConnected()) {
				log.info("开始上传文件到FTP,文件名称:" + fileName + new Date());
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);//设置上传文件类型为二进制，否则将无法打开文件
				ftpClient.changeToParentDirectory();
				ftpClient.makeDirectory(servicePath);
				boolean changed = ftpClient.changeWorkingDirectory(servicePath);
				if(changed){

				}else {

				}
//				ftpClient.changeToParentDirectory();
				//设置为被动模式(如上传文件夹成功，不能上传文件，注释这行，否则报错refused:connect  )
//				ftpClient.enterLocalPassiveMode();//设置被动模式，文件传输端口设置
				ftpClient.storeFile(fileName, inputStream);
				System.out.println(servicePath);
				inputStream.close();
				ftpClient.logout();
				isSuccess = true;
				log.info(fileName + "文件上传到FTP成功"+ new Date());
			} else {
				log.error("FTP连接建立失败" + new Date());
			}
		} catch (Exception e) {
			log.error(fileName + "文件上传到FTP出现异常"+ e);
		} finally {
			closeFtpClient(ftpClient);
			closeStream(inputStream);
			log.info("关闭FtpClient和inputstream"+new Date());
		}
		return isSuccess;
	}

	public void closeStream(Closeable closeable) {
		if (null != closeable) {
			try {
				closeable.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}
	public void closeFtpClient(FTPClient ftpClient) {
		if (ftpClient.isConnected()) {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}


}
