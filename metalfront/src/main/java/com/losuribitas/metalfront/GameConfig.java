package com.losuribitas.metalfront;

import java.awt.*;

public class GameConfig {
    // Dimensiones del juego
    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;
    public static final int CELL_SIZE = 40;
    
    // Configuración del juego
    public static final int INITIAL_SCRAP = 250;
    public static final int INITIAL_LIVES = 20;
    public static final int MAX_TOWERS = 10;
    public static final int INITIAL_ENEMIES_PER_WAVE = 8;
    
    // Configuración de torres
    public static final int[] TOWER_COSTS = {50, 100, 75, 150};
    public static final int[] TOWER_DAMAGE = {20, 50, 10, 60};
    public static final int[] TOWER_RANGE = {120, 250, 100, 150};
    public static final int[] TOWER_COOLDOWN = {30, 60, 10, 40};
    public static final String[] TOWER_NAMES = {"Basica", "Sniper", "Rapida", "Cañon"};
    public static final Color[] TOWER_COLORS = {
        new Color(70, 70, 70),
        new Color(50, 100, 150),
        new Color(200, 100, 50),
        new Color(120, 50, 50)
    };
    
    // Colores del juego
    public static final Color BACKGROUND_COLOR = new Color(34, 139, 34);
    public static final Color PATH_COLOR = new Color(139, 90, 43);
    public static final Color UI_BACKGROUND = new Color(40, 40, 40);
    
    // Configuración de enemigos
    public static final double[] ENEMY_SPEED = {1.2, 2.5, 0.6, 0.8};
    public static final int[] ENEMY_BASE_HEALTH = {60, 30, 200, 500};
    public static final Color[] ENEMY_COLORS = {
        new Color(80, 200, 120),   // Básico
        new Color(100, 180, 255),  // Rápido
        new Color(255, 170, 90),   // Tanque
        new Color(200, 120, 255)   // Boss
    };
}
