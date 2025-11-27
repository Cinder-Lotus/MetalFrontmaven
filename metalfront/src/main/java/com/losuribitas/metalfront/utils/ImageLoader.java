package com.losuribitas.metalfront.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    
    /**
     * Carga una imagen desde resources
     * @param path Ruta relativa desde resources/images/
     * @return BufferedImage o null si no se encuentra
     */
    public static BufferedImage loadImage(String path) {
        // Si ya está en caché, devolverla
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
    
    /**
     * Precarga todas las imágenes de enemigos al inicio
     */
    public static void preloadEnemyImages() {
        loadImage("enemy_basic.png");
        loadImage("enemy_fast.png");
        loadImage("enemy_tank.png");
        loadImage("enemy_boss.png");
    }
    
    /**
     * Precarga todas las imágenes de torres al inicio
     */
    public static void preloadTowerImages() {
        loadImage("tower_basic.png");
        loadImage("tower_sniper.png");
        loadImage("tower_rapid.png");
        loadImage("tower_cannon.png");
    }
    
    /**
     * Precarga todas las imágenes del juego
     */
    public static void preloadAllImages() {
        preloadEnemyImages();
        preloadTowerImages();
    }
}