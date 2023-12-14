package model;

public class AlbumReview {

  private String albumId;
  private boolean likeAlbum;

  public AlbumReview(String albumId, boolean likeAlbum) {
    this.albumId = albumId;
    this.likeAlbum = likeAlbum;
  }

  public String getAlbumId() {
    return albumId;
  }

  public boolean isLikeAlbum() {
    return likeAlbum;
  }
}
