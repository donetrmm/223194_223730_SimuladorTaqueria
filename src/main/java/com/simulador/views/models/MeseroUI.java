package com.simulador.views.models;

import com.almasb.fxgl.entity.Entity;
import com.simulador.models.Mesero;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.almasb.fxgl.dsl.FXGL;


public class MeseroUI extends Entity {
    private Mesero mesero;

    public MeseroUI(Mesero mesero, double x, double y) {
        this.mesero = mesero;
        this.setPosition(x, y);

        Image meseroImage = FXGL.getAssetLoader().loadTexture("mesero.png").getImage();
        ImageView meseroImageView = new ImageView(meseroImage);
        meseroImageView.setScaleX(-1);

        this.getViewComponent().clearChildren();
        this.getViewComponent().addChild(meseroImageView);

        //this.getViewComponent().addChild(new Rectangle(30, 30, Color.BLUE));
    }

}
