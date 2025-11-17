package com.losuribitas.metalfront.entities;

import java.awt.*;

public class Projectile {
    private double x, y, speed, distanceTraveled;
    private Enemy target;
    private int damage, towerType;
    
    public Projectile(int x, int y, Enemy target, int damage, int towerType) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.damage = damage;
        this.towerType = towerType;
        this.distanceTraveled = 0;
        
        // Velocidad según tipo de torre
        if (towerType == 1) speed = 8;      // Sniper
        else if (towerType == 2) speed = 6; // Rápida
        else if (towerType == 3) speed = 4; // Cañón
        else speed = 5;                     // Básica
    }
    
    public void move() {
        if (target == null) return;
        
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double dist = Math.hypot(dx, dy);
        
        if (dist > 0) {
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
            distanceTraveled += speed;
        }
    }
    
    public boolean hasHitTarget() {
        return target != null && Math.hypot(target.getX() - x, target.getY() - y) < 15;
    }
    
    public boolean hasExpired() {
        return distanceTraveled > 600;
    }
    
    public void damageTarget() {
        if (target != null && target.getHealth() > 0) {
            target.takeDamage(damage);
        }
    }
    
    public void draw(Graphics2D g) {
        switch (towerType) {
            case 0: // Básica
                g.setColor(Color.YELLOW);
                g.fillOval((int)x - 4, (int)y - 4, 8, 8);
                break;
                
            case 1: // Sniper
                g.setColor(Color.CYAN);
                g.setStroke(new BasicStroke(3));
                double angle = Math.atan2(target.getY() - y, target.getX() - x);
                int len = 15;
                g.drawLine((int)x, (int)y,
                         (int)(x + Math.cos(angle) * len),
                         (int)(y + Math.sin(angle) * len));
                g.setStroke(new BasicStroke(1));
                break;
                
            case 2: // Rápida
                g.setColor(Color.ORANGE);
                g.fillOval((int)x - 3, (int)y - 3, 6, 6);
                break;
                
            case 3: // Cañón
                g.setColor(Color.RED);
                g.fillOval((int)x - 6, (int)y - 6, 12, 12);
                g.setColor(Color.ORANGE);
                g.fillOval((int)x - 3, (int)y - 3, 6, 6);
                break;
        }
    }
    
    // Getters
    public Enemy getTarget() { return target; }
}