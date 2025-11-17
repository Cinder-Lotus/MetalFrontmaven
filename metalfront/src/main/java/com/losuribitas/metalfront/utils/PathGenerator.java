package com.losuribitas.metalfront.utils;

public class PathGenerator {
    
    /**
     * Crea un camino recto horizontal en el centro del mapa
     * @param width Ancho del mapa
     * @param height Alto del mapa
     * @param cellSize Tamaño de cada celda
     * @return Matriz con el camino (1 = camino, 0 = libre)
     */
    public static int[][] createStraightPath(int width, int height, int cellSize) {
        int rows = height / cellSize;
        int cols = width / cellSize;
        int[][] path = new int[rows][cols];
        
        int centerRow = rows / 2;
        
        for (int col = 0; col < cols; col++) {
            path[centerRow][col] = 1;
        }
        
        return path;
    }
    
    /**
     * Crea un camino en zigzag (opcional para futuras mejoras)
     * @param width Ancho del mapa
     * @param height Alto del mapa
     * @param cellSize Tamaño de cada celda
     * @return Matriz con el camino
     */
    public static int[][] createZigzagPath(int width, int height, int cellSize) {
        int rows = height / cellSize;
        int cols = width / cellSize;
        int[][] path = new int[rows][cols];
        
        int currentRow = rows / 4;
        boolean goingDown = true;
        
        for (int col = 0; col < cols; col++) {
            path[currentRow][col] = 1;
            
            // Cambiar dirección cada cierto número de columnas
            if (col % (cols / 4) == 0 && col > 0) {
                goingDown = !goingDown;
            }
            
            // Mover verticalmente
            if (col % 2 == 0) {
                if (goingDown && currentRow < rows - 2) {
                    currentRow++;
                } else if (!goingDown && currentRow > 1) {
                    currentRow--;
                }
            }
        }
        
        return path;
    }
    
    /**
     * Crea un camino en forma de S (opcional)
     * @param width Ancho del mapa
     * @param height Alto del mapa
     * @param cellSize Tamaño de cada celda
     * @return Matriz con el camino
     */
    public static int[][] createSPath(int width, int height, int cellSize) {
        int rows = height / cellSize;
        int cols = width / cellSize;
        int[][] path = new int[rows][cols];
        
        int topRow = rows / 4;
        //int midRow = rows / 2;
        int bottomRow = (3 * rows) / 4;
        
        for (int col = 0; col < cols; col++) {
            if (col < cols / 3) {
                // Primera parte - arriba
                path[topRow][col] = 1;
            } else if (col < (2 * cols) / 3) {
                // Parte media - diagonal
                int row = topRow + ((col - cols / 3) * (bottomRow - topRow)) / (cols / 3);
                if (row >= 0 && row < rows) {
                    path[row][col] = 1;
                }
            } else {
                // Última parte - abajo
                path[bottomRow][col] = 1;
            }
        }
        
        return path;
    }
}
