package com.itellyou.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class FileUtils {

    private static String tempPath = "./.tmp";

    public static String download(String fileURL) {
        String extname = fileURL.substring(fileURL.lastIndexOf(".") + 1).toLowerCase();
        String fileKey = StringUtils.createToken(fileURL).replaceAll("\\/","__");
        return download(fileURL, tempPath, fileKey + "." + extname);
    }

    public static String download(String fileURL, String filename) {
        return download(fileURL, tempPath, filename);
    }

    public static String download(String fileURL, String path, String filename) {
        try {
            URL url = new URL(fileURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(30 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);

            //文件保存位置
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String filePath = dir + File.separator + filename;
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("download error ! url:" + fileURL);
            return null;
        }
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static MultipartFile fileToMultipartFile(String path) {
        try {
            File file = new File(path);
            InputStream inputStream = new FileInputStream(file);
            return new MockMultipartFile(file.getName(), inputStream);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void delete(String path){
        try {
            File file = new File(path);
            file.delete();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
