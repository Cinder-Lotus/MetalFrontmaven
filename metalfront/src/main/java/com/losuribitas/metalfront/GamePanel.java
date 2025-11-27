package com.losuribitas.metalfront;

import com.losuribitas.metalfront.entities.*;
import com.losuribitas.metalfront.utils.PathGenerator;
import com.losuribitas.metalfront.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private Timer timer;
    private ArrayList<Tower> towers;
    private ArrayList<Enemy> enemies;
    private ArrayList<Projectile> projectiles;
    private int[][] path;
    
    private int scrap;
    private int lives;
    private int wave;
    private int enemiesSpawned;
    private int enemiesPerWave;
    private int spawnTimer;
    private int selectedTowerType;
    private Point mousePos;
    private Tower selectedTowerForRemoval;
    
    public GamePanel() {
        setPreferredSize(new Dimension(GameConfig.WIDTH, GameConfig.HEIGHT));
        setBackground(GameConfig.BACKGROUND_COLOR);
        
        // Precargar todas las imágenes (enemigos y torres)
        ImageLoader.preloadAllImages();
        
        initializeGame();
        setupListeners();
        
        timer = new Timer(16, this);
        timer.start();
    }
    
    private void initializeGame() {
        towers = new ArrayList<>();
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        
        scrap = GameConfig.INITIAL_SCRAP;
        lives = GameConfig.INITIAL_LIVES;
        wave = 0;
        enemiesSpawned = 0;
        enemiesPerWave = GameConfig.INITIAL_ENEMIES_PER_WAVE;
        spawnTimer = 0;
        selectedTowerType = -1;
        mousePos = new Point();
        selectedTowerForRemoval = null;
        
        path = PathGenerator.createStraightPath(
            GameConfig.WIDTH, 
            GameConfig.HEIGHT, 
            GameConfig.CELL_SIZE
        );
    }
    
    private void setupListeners() {
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
            }
        });
    }
    
    private void handleMouseClick(int x, int y) {
        int col = x / GameConfig.CELL_SIZE;
        int row = y / GameConfig.CELL_SIZE;
        
        // Área de botones
        if (y > GameConfig.HEIGHT - 100) {
            handleUIClick(x, y);
            return;
        }
        
        // Modo eliminar torre
        if (selectedTowerForRemoval != null) {
            handleTowerRemoval(x, y);
            return;
        }
        
        // Colocar torre
        if (selectedTowerType >= 0) {
            placeTower(col, row);
        }
    }
    
    private void handleUIClick(int x, int y) {
        if (y > GameConfig.HEIGHT - 90 && y < GameConfig.HEIGHT - 20) {
            // Botones de torres
            if (x >= 10 && x <= 90) selectedTowerType = 0;
            else if (x >= 100 && x <= 180) selectedTowerType = 1;
            else if (x >= 190 && x <= 270) selectedTowerType = 2;
            else if (x >= 280 && x <= 360) selectedTowerType = 3;
        }
        
        // Botón siguiente oleada
        if (x >= 380 && x <= 480 && y > GameConfig.HEIGHT - 90 && y < GameConfig.HEIGHT - 20) {
            startNextWave();
        }
        
        // Botón eliminar torre
        if (x >= 490 && x <= 620 && y > GameConfig.HEIGHT - 90 && y < GameConfig.HEIGHT - 20) {
            selectedTowerType = -1;
            selectedTowerForRemoval = new Tower(0, 0, 0);
        }
    }
    
    private void handleTowerRemoval(int x, int y) {
        Iterator<Tower> it = towers.iterator();
        while (it.hasNext()) {
            Tower t = it.next();
            if (Math.hypot(x - t.getX(), y - t.getY()) < GameConfig.CELL_SIZE / 2) {
                int refund = GameConfig.TOWER_COSTS[t.getType()] / 2;
                scrap += refund;
                it.remove();
                selectedTowerForRemoval = null;
                repaint();
                return;
            }
        }
        selectedTowerForRemoval = null;
        repaint();
    }
    
    private void placeTower(int col, int row) {
        if (towers.size() >= GameConfig.MAX_TOWERS) {
            JOptionPane.showMessageDialog(this, 
                "Límite de torres alcanzado (" + GameConfig.MAX_TOWERS + ").");
            selectedTowerType = -1;
            return;
        }
        
        if (scrap >= GameConfig.TOWER_COSTS[selectedTowerType]) {
            if (row >= 0 && row < path.length && col >= 0 && col < path[0].length) {
                if (path[row][col] == 0 && !isTowerAt(col, row)) {
                    towers.add(new Tower(
                        col * GameConfig.CELL_SIZE + GameConfig.CELL_SIZE / 2,
                        row * GameConfig.CELL_SIZE + GameConfig.CELL_SIZE / 2,
                        selectedTowerType
                    ));
                    scrap -= GameConfig.TOWER_COSTS[selectedTowerType];
                    selectedTowerType = -1;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Chatarra insuficiente.");
            selectedTowerType = -1;
        }
    }
    
    private boolean isTowerAt(int col, int row) {
        for (Tower t : towers) {
            int tCol = t.getX() / GameConfig.CELL_SIZE;
            int tRow = t.getY() / GameConfig.CELL_SIZE;
            if (tCol == col && tRow == row) return true;
        }
        return false;
    }
    
    private void startNextWave() {
        enemiesSpawned = 0;
        spawnTimer = 0;
        wave++;
        enemiesPerWave = GameConfig.INITIAL_ENEMIES_PER_WAVE + wave * 2;
        
        if (wave >= 20) {
            for (Enemy e : enemies) {
                e.scaleHealthForWave(wave);
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        spawnEnemies();
        updateEnemies();
        updateTowers();
        updateProjectiles();
        checkGameOver();
        repaint();
    }
    
    private void spawnEnemies() {
        if (wave > 0 && enemiesSpawned < enemiesPerWave) {
            spawnTimer++;
            int spawnDelay = (enemiesSpawned == enemiesPerWave - 1) ? 120 : 50;
            
            if (spawnTimer > spawnDelay) {
                int type = determineEnemyType();
                enemies.add(new Enemy(wave, type, GameConfig.HEIGHT, GameConfig.CELL_SIZE));
                enemiesSpawned++;
                spawnTimer = 0;
            }
        }
    }
    
    private int determineEnemyType() {
        if (enemiesSpawned == enemiesPerWave - 1) {
            return 3; // Boss
        }
        
        Random rand = new Random();
        int r = rand.nextInt(100);
        if (r < 50) return 0;      // Básico
        else if (r < 75) return 1; // Rápido
        else return 2;             // Tanque
    }
    
    private void updateEnemies() {
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();
            enemy.move();
            enemy.regenerate();
            
            if (enemy.reachedEnd(GameConfig.WIDTH)) {
                lives -= enemy.getLivesCost();
                enemyIt.remove();
            } else if (enemy.isDead()) {
                scrap += enemy.getReward();
                enemyIt.remove();
            }
        }
    }
    
    private void updateTowers() {
        for (Tower tower : towers) {
            tower.decrementCooldown();
            
            if (tower.canShoot()) {
                Enemy target = tower.findTarget(enemies);
                if (target != null) {
                    if (tower.isAreaDamage()) {
                        shootAreaDamage(tower);
                    } else {
                        projectiles.add(new Projectile(
                            tower.getX(), tower.getY(), target, 
                            tower.getDamage(), tower.getType()
                        ));
                    }
                    tower.resetCooldown();
                }
            }
        }
    }
    
    private void shootAreaDamage(Tower tower) {
        for (Enemy enemy : enemies) {
            double dist = Math.hypot(enemy.getX() - tower.getX(), enemy.getY() - tower.getY());
            if (dist <= tower.getRange()) {
                projectiles.add(new Projectile(
                    tower.getX(), tower.getY(), enemy,
                    tower.getDamage(), tower.getType()
                ));
            }
        }
    }
    
    private void updateProjectiles() {
        Iterator<Projectile> projIt = projectiles.iterator();
        while (projIt.hasNext()) {
            Projectile proj = projIt.next();
            proj.move();
            
            if (proj.hasHitTarget()) {
                proj.damageTarget();
                projIt.remove();
            } else if (proj.hasExpired()) {
                projIt.remove();
            }
        }
    }
    
    private void checkGameOver() {
        if (lives <= 0) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over! Llegaste a la oleada " + wave);
            System.exit(0);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawPath(g2d);
        drawTowerPreview(g2d);
        drawEntities(g2d);
        drawUI(g2d);
    }
    
    private void drawPath(Graphics2D g2d) {
        g2d.setColor(GameConfig.PATH_COLOR);
        for (int row = 0; row < path.length; row++) {
            for (int col = 0; col < path[0].length; col++) {
                if (path[row][col] == 1) {
                    g2d.fillRect(col * GameConfig.CELL_SIZE, row * GameConfig.CELL_SIZE, 
                               GameConfig.CELL_SIZE, GameConfig.CELL_SIZE);
                }
            }
        }
    }
    
    private void drawTowerPreview(Graphics2D g2d) {
        if (selectedTowerType >= 0) {
            int col = mousePos.x / GameConfig.CELL_SIZE;
            int row = mousePos.y / GameConfig.CELL_SIZE;
            
            if (row >= 0 && row < path.length && col >= 0 && col < path[0].length) {
                if (path[row][col] == 0 && !isTowerAt(col, row)) {
                    g2d.setColor(new Color(0, 255, 0, 100));
                } else {
                    g2d.setColor(new Color(255, 0, 0, 100));
                }
                g2d.fillRect(col * GameConfig.CELL_SIZE, row * GameConfig.CELL_SIZE, 
                           GameConfig.CELL_SIZE, GameConfig.CELL_SIZE);
                
                g2d.setColor(new Color(255, 255, 255, 50));
                int centerX = col * GameConfig.CELL_SIZE + GameConfig.CELL_SIZE / 2;
                int centerY = row * GameConfig.CELL_SIZE + GameConfig.CELL_SIZE / 2;
                int range = GameConfig.TOWER_RANGE[selectedTowerType];
                g2d.fillOval(centerX - range, centerY - range, range * 2, range * 2);
            }
        }
    }
    
    private void drawEntities(Graphics2D g2d) {
        for (Tower tower : towers) {
            tower.draw(g2d);
            if (selectedTowerForRemoval != null) {
                double dist = Math.hypot(mousePos.x - tower.getX(), mousePos.y - tower.getY());
                if (dist < GameConfig.CELL_SIZE / 2) {
                    g2d.setColor(new Color(255, 0, 0, 100));
                    g2d.fillOval(tower.getX() - GameConfig.CELL_SIZE / 2, 
                               tower.getY() - GameConfig.CELL_SIZE / 2, 
                               GameConfig.CELL_SIZE, GameConfig.CELL_SIZE);
                }
            }
        }
        
        for (Enemy enemy : enemies) {
            enemy.draw(g2d);
        }
        
        for (Projectile proj : projectiles) {
            proj.draw(g2d);
        }
    }
    
    private void drawUI(Graphics2D g2d) {
        g2d.setColor(GameConfig.UI_BACKGROUND);
        g2d.fillRect(0, GameConfig.HEIGHT - 100, GameConfig.WIDTH, 100);
        
        drawTowerButtons(g2d);
        drawWaveButton(g2d);
        drawRemoveButton(g2d);
        drawStats(g2d);
    }
    
    private void drawTowerButtons(Graphics2D g2d) {
        int btnX = 10;
        for (int i = 0; i < 4; i++) {
            boolean selected = selectedTowerType == i;
            boolean canAfford = scrap >= GameConfig.TOWER_COSTS[i];
            
            g2d.setColor(selected ? GameConfig.TOWER_COLORS[i].brighter() : GameConfig.TOWER_COLORS[i]);
            if (!canAfford) g2d.setColor(new Color(80, 80, 80));
            g2d.fillRect(btnX, GameConfig.HEIGHT - 90, 80, 70);
            
            g2d.setColor(selected ? Color.YELLOW : Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString(GameConfig.TOWER_NAMES[i], btnX + 5, GameConfig.HEIGHT - 70);
            g2d.setFont(new Font("Arial", Font.PLAIN, 9));
            g2d.drawString("$" + GameConfig.TOWER_COSTS[i], btnX + 5, GameConfig.HEIGHT - 55);
            g2d.drawString("D:" + GameConfig.TOWER_DAMAGE[i], btnX + 5, GameConfig.HEIGHT - 43);
            g2d.drawString("R:" + GameConfig.TOWER_RANGE[i], btnX + 5, GameConfig.HEIGHT - 31);
            
            btnX += 90;
        }
    }
    
    private void drawWaveButton(Graphics2D g2d) {
        g2d.setColor(new Color(200, 100, 100));
        g2d.fillRect(380, GameConfig.HEIGHT - 90, 100, 70);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("OLEADA", 395, GameConfig.HEIGHT - 60);
        g2d.drawString("# " + (wave + 1), 410, GameConfig.HEIGHT - 40);
    }
    
    private void drawRemoveButton(Graphics2D g2d) {
        g2d.setColor(selectedTowerForRemoval != null ? 
                    new Color(255, 80, 80) : new Color(150, 50, 50));
        g2d.fillRect(490, GameConfig.HEIGHT - 90, 130, 70);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("ELIMINAR", 505, GameConfig.HEIGHT - 60);
        g2d.drawString("TORRE", 520, GameConfig.HEIGHT - 40);
    }
    
    private void drawStats(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Chatarra: " + scrap, 650, GameConfig.HEIGHT - 70);
        g2d.drawString("Vidas: " + lives, 650, GameConfig.HEIGHT - 45);
        g2d.drawString("Torres: " + towers.size() + "/" + GameConfig.MAX_TOWERS, 
                      800, GameConfig.HEIGHT - 60);
    }
}

