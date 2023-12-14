package image;

import static util.Constants.BUCKET_NAME;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.Part;
import model.ImageMetadata;
import org.apache.commons.io.IOUtils;

public class ImageClient {
  private final AmazonS3 s3Client;
  private final TransferManager transferManager;

  public ImageClient() {
    this.s3Client = AmazonS3ClientBuilder.standard()
                      .withRegion(Regions.US_WEST_2)
                      .build();
    this.transferManager = TransferManagerBuilder.standard()
                              .withS3Client(s3Client)
                              .build();
  }

  public ImageMetadata uploadImage(Part imagePart, String imageKey){
    InputStream inputStream;
    try {
      inputStream = imagePart.getInputStream();

      byte[] contents = IOUtils.toByteArray(inputStream);
      InputStream stream = new ByteArrayInputStream(contents);

      ObjectMetadata meta = new ObjectMetadata();
      meta.setContentLength(contents.length);
      meta.setContentType("image/png");

      transferManager.upload(BUCKET_NAME, imageKey, stream, meta);

      inputStream.close();
      return new ImageMetadata(BUCKET_NAME);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  public void shutDown(){
    transferManager.shutdownNow();
    s3Client.shutdown();
  }
}
