package br.gov.tcu.zello;

import android.graphics.Bitmap;

public class NotificacaoBean {
    private String title;
    private String text;
    private String pkg;
    private Bitmap imaBitmap;

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getImage() {
        return imaBitmap;
    }

    public void setImage(Bitmap imaBitmap) {
        this.imaBitmap = imaBitmap;
    }
}
