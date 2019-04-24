package bean;

public class nearbyListBean {
    private String uid2;
    private String username;
    private String nickname;
    private double longitude;
    private double latitude;
    private String mid;
  public   nearbyListBean(String uid2,String username,String nickname,double longitude,double latitude,String mid){
      this.uid2 = uid2;
      this.username = username;
      this.nickname = nickname;
      this.longitude =longitude;
      this.latitude = latitude;
      this.mid = mid;
  }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUid2() {
        return uid2;
    }

    public String getUsername() {
        return username;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }
}
