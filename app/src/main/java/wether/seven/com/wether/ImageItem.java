package wether.seven.com.wether;

import android.graphics.Bitmap;

public class ImageItem {
    private String degree;
    private Bitmap image;
    private String title;

    public ImageItem(String degree, Bitmap image, String title) {
        super();
        this.degree = degree;
        this.image = image;
        this.title = title;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
