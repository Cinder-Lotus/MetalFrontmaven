package com.losuribitas.metalfront.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    
    public static BufferedImage loadImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }
        
        try {
            InputStream is = ImageLoader.class.getResourceAsStream("/images/" + path);
            if (is == null) {
                System.err.println("No se pudo encontrar la imagen: /images/" + path);
                return null;
            }
            BufferedImage image = ImageIO.read(is);
            imageCache.put(path, image);
            return image;
        } catch (IOException e) {
            System.err.println("Error al cargar imagen: " + path);
            e.printStackTrace();
            return null;
        }
    }
    
    public static void preloadEnemyImages() {
        loadImage("enemy_basic.png");
        loadImage("enemy_fast.png");
        loadImage("enemy_tank.png");
        loadImage("enemy_boss.png");
    }
}
