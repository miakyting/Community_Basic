package com.limingzhu.community.provider;

import com.aliyun.oss.*;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class AliyunProvider {

    @Value("${Aliyun.AFile.AccessKeyId}")
    private String accessKeyId;

    @Value("${Aliyun.AFile.AccessKeySecret}")
    private String accessKeySecret;

    @Value("${Aliyun.AFile.endpoint}")
    private String endpoint;

    @Value("${Aliyun.AFile.bucketName}")
    private String bucketName;

    public String upload(InputStream inputStream, String fileName) {

        OSS ossClient = null;
        try {
            String uuid = UUID.randomUUID().toString();
            String tempFileName = uuid + fileName;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date d = new Date();
            String filePath = sdf.format(d);
            // <yourObjectName>上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
            String objectName = filePath + tempFileName;

            // 创建OSSClient实例。
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 上传内容到指定的存储空间（bucketName）并保存为指定的文件名称（objectName）。
            ossClient.putObject(bucketName, objectName, inputStream);

            //上传成功后，返回图片预览URL,设定URL过期时间10年
            Date expiration = new Date(new Date().getTime() + 10* 365 * 24 * 60 * 60 * 1000);
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName,objectName, HttpMethod.GET);
            req.setExpiration(expiration);

            URL url = ossClient.generatePresignedUrl(req);

            // 返回上传之后的oss存储路径
            return url.toString();
            //String path = "http://" + bucketName + "." + endpoint + "/" + fileName;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message" + oe.getMessage());
            System.out.println("Error Code" + oe.getErrorCode());
            System.out.println("Request ID" + oe.getRequestId());
            System.out.println("Host ID" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
        }finally {
            if (ossClient != null){
                ossClient.shutdown();
            }
        }
        return null;
    }
}
