package model;

public class AlbumDetails {

  private String albumId;
  private Album album;
  private ImageMetadata imageMetadata;

  public String getAlbumId() {
    return albumId;
  }

  public void setAlbumId(String albumId) {
    this.albumId = albumId;
  }

  public Album getAlbum() {
    return album;
  }

  public void setAlbum(Album album) {
    this.album = album;
  }

  public ImageMetadata getImageMetadata() {
    return imageMetadata;
  }

  public void setImageMetadata(ImageMetadata imageMetadata) {
    this.imageMetadata = imageMetadata;
  }
}
