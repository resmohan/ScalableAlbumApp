package model;

public class Album {
  private String artist;
  private String title;
  private String year;

  public Album(String artist, String title, String year) {
    this.artist = artist;
    this.title = title;
    this.year = year;
  }

  public boolean isValidAlbum(){
    return(artist != null && title != null && year != null);
  }

}
