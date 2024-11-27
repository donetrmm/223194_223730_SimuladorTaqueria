package com.simulador.views;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RestauranteSpawner implements EntityFactory {

    @Spawns("mesa")
    public Entity newMesa(SpawnData data) {
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(EnumModels.MESA)
                .viewWithBBox(new Rectangle(40, 40, Color.BROWN))
                .build();
    }

    @Spawns("mesero")
    public Entity newMesero(SpawnData data) {
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(EnumModels.MESERO)
                .viewWithBBox(new Rectangle(30, 30, Color.BLUE))
                .build();
    }

    @Spawns("cocinero")
    public Entity newCocinero(SpawnData data) {
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(EnumModels.COCINERO)
                .viewWithBBox(new Rectangle(30, 30, Color.RED))
                .build();
    }
}
