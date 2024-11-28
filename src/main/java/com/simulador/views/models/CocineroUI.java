package com.simulador.views.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.simulador.models.Cocinero;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CocineroUI extends Entity {
    private Cocinero cocinero;

    public CocineroUI(Cocinero cocinero, double x, double y) {
        this.cocinero = cocinero;
        this.setPosition(x, y);
        this.getViewComponent().addChild(new Rectangle(30, 30, Color.RED));

        Image cocineroImage = FXGL.getAssetLoader().loadTexture("cocinero.png").getImage();
        ImageView cocineroImageView = new ImageView(cocineroImage);

        this.getViewComponent().clearChildren();
        this.getViewComponent().addChild(cocineroImageView);
    }
}
