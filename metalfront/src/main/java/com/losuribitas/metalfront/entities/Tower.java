package com.losuribitas.metalfront.entities;

import com.losuribitas.metalfront.GameConfig;
import java.awt.*;
import java.util.ArrayList;

public class Tower {
    private int x, y, type;
    private int range, damage, cooldown, currentCooldown;
    
    public Tower(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.range = GameConfig.TOWER_RANGE[type];
        this.damage = GameConfig.TOWER_DAMAGE[type];
        this.cooldown = GameConfig.TOWER_COOLDOWN[type];
        this.currentCooldown = 0;
    }
    
    public Enemy findTarget(ArrayList<Enemy> enemies) {
        Enemy target = null;
        double closestDist = range + 1;
        
        for (Enemy e : enemies) {
            double dist = Math.hypot(e.getX() - x, e.getY() - y);
            if (dist <= range) {
                if (type == 1) { // Sniper prioriza más vida
                    if (target == null || e.getHealth() > target.getHealth()) {
                        target = e;
                    }
                } else if (dist < closestDist) {
                    closestDist = dist;
                    target = e;
                }
            }
        }
        return target;
    }
    
    public void draw(Graphics2D g) {
        Color color = GameConfig.TOWER_COLORS[type];
        g.setColor(color);
        
        switch (type) {
            case 0: // Básica
                g.fillRoundRect(x - 15, y - 15, 30, 30, 8, 8);
                g.setColor(Color.YELLOW);
                g.fillOval(x - 5, y - 5, 10, 10);
                break;
            case 1: // Sniper
                g.fillRect(x - 10, y - 10, 20, 20);
                g.setColor(Color.CYAN);
                g.drawLine(x, y, x + 15, y);
                break;
            case 2: // Rápida
                g.fillOval(x - 12, y - 12, 24, 24);
                g.setColor(Color.ORANGE);
                g.fillOval(x - 4, y - 4, 8, 8);
                break;
            case 3: // Cañón
                g.fillRect(x - 20, y - 10, 40, 20);
                g.setColor(Color.RED);
                g.fillOval(x - 6, y - 6, 12, 12);
                break;
        }
        
        g.setColor(new Color(255, 255, 255, 30));
        g.drawOval(x - range, y - range, range * 2, range * 2);
    }
    
    public void decrementCooldown() {
        if (currentCooldown > 0) currentCooldown--;
    }
    
    public boolean canShoot() {
        return currentCooldown <= 0;
    }
    
    public void resetCooldown() {
        currentCooldown = cooldown;
    }
    
    public boolean isAreaDamage() {
        return type == 3; // Cañón
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getType() { return type; }
    public int getRange() { return range; }
    public int getDamage() { return damage; }
}
