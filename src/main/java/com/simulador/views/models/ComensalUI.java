package com.simulador.views.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.simulador.models.Comensal;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ComensalUI extends Entity {
    private Comensal comensal;

    public ComensalUI(Comensal comensal, double x, double y) {
        this.comensal = comensal;
        this.setPosition(x, y);

        Image cocineroImage = FXGL.getAssetLoader().loadTexture("comensal.png").getImage();
        ImageView cocineroImageView = new ImageView(cocineroImage);
        cocineroImageView.setFitHeight(65);
        cocineroImageView.setFitWidth(65);

        this.getViewComponent().clearChildren();
        this.getViewComponent().addChild(cocineroImageView);
    }

    public Comensal getComensal() {
        return comensal;
    }
}
