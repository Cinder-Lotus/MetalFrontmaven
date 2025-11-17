package com.losuribitas.metalfront.entities;

import com.losuribitas.metalfront.GameConfig;
import com.losuribitas.metalfront.utils.ImageLoader;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy {
    private double x, y, speed;
    private int health, maxHealth, reward, type, regenTimer;
    private BufferedImage sprite;
    
    public Enemy(int wave, int type, int height, int cellSize) {
        this.type = type;
        this.x = 0;
        this.y = (height / cellSize / 2) * cellSize + cellSize / 2;
        this.regenTimer = 0;
        
        // Cargar la imagen correspondiente al tipo
        loadSprite();
        
        // Configurar stats según tipo
        this.speed = GameConfig.ENEMY_SPEED[type] + (wave * 0.05);
        this.maxHealth = GameConfig.ENEMY_BASE_HEALTH[type] + (wave * (type == 3 ? 50 : 10));
        
        // Recompensa proporcional
        this.reward = Math.max(1, (int)(GameConfig.TOWER_COSTS[type] * 0.25) + wave);
        if (type == 3) this.reward = Math.max(1, (int)(GameConfig.TOWER_COSTS[3] * 0.30) + wave * 2);
        
        // Escalar vida para oleadas altas
        if (wave >= 20) {
            this.maxHealth += this.maxHealth * ((wave - 19) * 0.03);
        }
        
        this.health = this.maxHealth;
    }
    
    /**
     * Carga el sprite correspondiente al tipo de enemigo
     */
    private void loadSprite() {
        String imageName = "";
        switch (type) {
            case 0: imageName = "enemy_basic.png"; break;
            case 1: imageName = "enemy_fast.png"; break;
            case 2: imageName = "enemy_tank.png"; break;
            case 3: imageName = "enemy_boss.png"; break;
        }
        sprite = ImageLoader.loadImage(imageName);
    }
    
    public void move() {
        x += speed;
    }
    
    public void regenerate() {
        if (type == 3 && health > 0) {
            regenTimer++;
            if (regenTimer > 60) {
                health = Math.min(health + 5, maxHealth);
                regenTimer = 0;
            }
        }
    }
    
    public boolean reachedEnd(int width) {
        return x >= width;
    }
    
    public boolean isDead() {
        return health <= 0;
    }
    
    public void scaleHealthForWave(int wave) {
        if (wave >= 20) {
            maxHealth += maxHealth * ((wave - 19) * 0.03);
            health = maxHealth;
        }
    }
    
    public void takeDamage(int damage) {
        health -= damage;
    }
    
    public int getLivesCost() {
        return type == 3 ? 5 : 1;
    }
    
    public void draw(Graphics2D g) {
        int size = (type == 3) ? 34 : (type == 2) ? 28 : (type == 1) ? 20 : 22;
        
        // Si hay sprite, dibujarlo; si no, usar formas geométricas
        if (sprite != null) {
            // Dibujar la imagen centrada en la posición del enemigo
            int imgWidth = size;
            int imgHeight = size;
            g.drawImage(sprite, 
                       (int)x - imgWidth/2, 
                       (int)y - imgHeight/2, 
                       imgWidth, 
                       imgHeight, 
                       null);
        } else {
            // Fallback: dibujar formas geométricas si no hay imagen
            drawGeometricShape(g, size);
        }
        
        // Barra de vida (siempre se dibuja)
        drawHealthBar(g, size);
        
        // Efecto de regeneración del boss
        if (type == 3 && regenTimer > 30) {
            g.setColor(new Color(0, 255, 0, 150));
            g.fillOval((int)x - 5, (int)y - 5, 10, 10);
        }
    }
    
    /**
     * Dibuja formas geométricas como fallback
     */
    private void drawGeometricShape(Graphics2D g, int size) {
        g.setColor(GameConfig.ENEMY_COLORS[type]);
        
        switch (type) {
            case 0: // Nave ligera (triángulo)
                int[] xPts0 = {(int)x, (int)x - size/2, (int)x + size/2};
                int[] yPts0 = {(int)y - size/2, (int)y + size/2, (int)y + size/2};
                g.fillPolygon(xPts0, yPts0, 3);
                g.setColor(Color.DARK_GRAY);
                g.drawPolygon(xPts0, yPts0, 3);
                break;
                
            case 1: // Nave rápida (flecha)
                int[] xPts1 = {(int)x + size/2, (int)x - size/2, (int)x - size/2};
                int[] yPts1 = {(int)y, (int)y - size/2, (int)y + size/2};
                g.fillPolygon(xPts1, yPts1, 3);
                g.setColor(Color.WHITE);
                g.drawPolygon(xPts1, yPts1, 3);
                break;
                
            case 2: // Nave pesada (cápsula)
                g.fillRoundRect((int)x - size/2, (int)y - size/3, size, size/1, 10, 10);
                g.setColor(Color.DARK_GRAY);
                g.drawRoundRect((int)x - size/2, (int)y - size/3, size, size/1, 10, 10);
                break;
                
            case 3: // Jefe (platillo)
                g.fillOval((int)x - size/2, (int)y - size/3, size, size/2);
                g.setColor(new Color(255, 255, 255, 120));
                g.fillOval((int)x - size/6, (int)y - size/6, size/3, size/6);
                g.setColor(Color.DARK_GRAY);
                g.drawOval((int)x - size/2, (int)y - size/3, size, size/2);
                break;
        }
    }
    
    /**
     * Dibuja la barra de vida
     */
    private void drawHealthBar(Graphics2D g, int size) {
        int barWidth = size + 12;
        g.setColor(Color.BLACK);
        g.fillRect((int)x - barWidth/2, (int)y - size/2 - 10, barWidth, 4);
        g.setColor(Color.GREEN);
        int healthWidth = (int)(barWidth * ((double)health / maxHealth));
        g.fillRect((int)x - barWidth/2, (int)y - size/2 - 10, healthWidth, 4);
    }
    
    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public int getHealth() { return health; }
    public int getReward() { return reward; }
    public int getType() { return type; }
}