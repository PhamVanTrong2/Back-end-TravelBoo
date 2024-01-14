package com.bootravel.service.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.var;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class AmazonS3StorageHandler {

    @Value("${aws.s3.bucketName}")
    private String bucketName;
    @Value("${aws.s3.domain}")
    private String s3Domain;

    private final AmazonS3 s3Client;

    public AmazonS3StorageHandler(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String storeFilePublic(InputStream inputStream, String filename, String contentType) {
        var meta = new ObjectMetadata();
        meta.setContentType(contentType);
        try (var bufferedInputStream = new BufferedInputStream(inputStream);
             var outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            byte[] bytes = outputStream.toByteArray();
            TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(s3Client).build();
            Upload upload = transferManager.upload(bucketName, filename, new ByteArrayInputStream(bytes), meta);
            upload.waitForCompletion();
            s3Client.setObjectAcl(bucketName, filename, CannedAccessControlList.PublicRead);
            return s3Client.getUrl(bucketName, filename).toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String urlFile) {
        if(!urlFile.startsWith(s3Domain)) return;

        String fileName = urlFile.substring(s3Domain.length());
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }
}
