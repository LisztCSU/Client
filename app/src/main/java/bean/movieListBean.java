package bean;

public class movieListBean {
    private String id;
    private String title;
    private String score;
    private String star;
    private String duration;
    private String votecount;
    private String region;
    private String director;
    private String  actors;
    private String imgUrl;
    private String wantaccount;
    public movieListBean(String id,String title,String score,String star, String duration,String votecount,String region,String director,String actors,String imgUrl,String wantaccount){
        this.id = id;
        this.title = title;
        this.score = score;
        this.star = star;
        this.duration = duration;
        this.votecount = votecount;
        this.region = region;
        this.director = director;
        this.actors = actors;
        this.imgUrl = imgUrl;
        this.wantaccount= wantaccount;

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRegion() {
        return region;
    }

    public String getScore() {
        return score;
    }

    public String getStar() {
        return star;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return imgUrl;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVotecount() {
        return votecount;
    }

    public void setVotecount(String votecount) {
        this.votecount = votecount;
    }

    public String getWantaccount() {
        return wantaccount;
    }

    public void setWantaccount(String wantaccount) {
        this.wantaccount = wantaccount;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
