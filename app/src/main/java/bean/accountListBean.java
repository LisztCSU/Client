package bean;

import android.content.ComponentName;

public class accountListBean {
    private String title;
    private String info;
    private int ImageID;
    private ComponentName[] componentNames;


    public accountListBean(String title,String info,int imageID,ComponentName[] componentNames){
        this.ImageID = imageID;
        this.title = title;
        this.info = info;
        this.componentNames = componentNames;

    }

    public String getTitle() {
        return title;
    }

    public void setText(String title) {
        this.title = title;
    }
    public String getInfo(){
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getImageID() {
        return ImageID;
    }

    public void setImageID(int imageID) {
        ImageID = imageID;
    }

    public void setComponentName(ComponentName[] componentName) {
        this.componentNames = componentNames;
    }

    public ComponentName[] getComponentName() {
        return componentNames;
    }
}
