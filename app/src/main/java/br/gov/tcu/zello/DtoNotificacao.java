package br.gov.tcu.zello;

import android.graphics.Bitmap;

class DtoNotificacao {
    private String title;
    private String text;
    private String pkg;
    private Bitmap imaBitmap;

    String getPkg() {
        return pkg;
    }

    void setPkg(String pkg) {
        this.pkg = pkg;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getText() {
        return text;
    }

    void setText(String text) {
        this.text = text;
    }

    Bitmap getImage() {
        return imaBitmap;
    }

    void setImage(Bitmap imaBitmap) {
        this.imaBitmap = imaBitmap;
    }
}
