package com.simulador.views.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.simulador.models.Comensal;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ComensalUI extends Entity {
    private Comensal comensal;
    private String path = "comensal.png";

    public ComensalUI(Comensal comensal, double x, double y) {
        this.comensal = comensal;
        this.setPosition(x, y);

        actualizarImagen();
    }


    public void actualizarImagen() {
        String imagePath;
        if (comensal.getEstado().equals("Terminado")) {
            imagePath = "comensal.png";
        } else if (comensal.getEstado().equals("Comiendo")) {
            imagePath = "comensalComiendo.png";
        } else {
            imagePath = "comensal.png";
        }
        Image image = FXGL.getAssetLoader().loadTexture(imagePath).getImage();
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(65);
        imageView.setFitWidth(65);

        if (imagePath.equals("comensalComiendo.png")){
            imageView.setScaleX(-1);
        }
        this.getViewComponent().clearChildren();
        this.getViewComponent().addChild(imageView);
    }

    public Comensal getComensal() {
        return comensal;
    }

    public int getId() {
        return comensal.getId();
    }
}
