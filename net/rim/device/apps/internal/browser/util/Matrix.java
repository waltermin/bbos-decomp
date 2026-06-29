package net.rim.device.apps.internal.browser.util;

import net.rim.vm.Array;

public final class Matrix {
   private Object[][] matrix;
   private int sizeX;
   private int sizeY;

   public Matrix() {
   }

   public Matrix(int rows, int cols) {
      this.grow(cols - 1, rows - 1);
   }

   public final Object elementAt(int x, int y) {
      return this.matrix[x][y];
   }

   public final void setElementAt(Object o, int x, int y) {
      this.grow(x, y);
      this.matrix[x][y] = o;
   }

   public final int sizeX() {
      return this.sizeX;
   }

   public final int sizeY() {
      return this.sizeY;
   }

   private final void grow(int x, int y) {
      if (x >= this.sizeX || y >= this.sizeY) {
         int newSizeX = Math.max(this.sizeX, x + 1);
         int newSizeY = Math.max(this.sizeY, y + 1);
         if (this.matrix == null) {
            this.matrix = new Object[newSizeX][newSizeY];
         } else {
            Array.resize(this.matrix, newSizeX);

            for (int i = 0; i < this.sizeX; i++) {
               Array.resize(this.matrix[i], newSizeY);
            }

            for (int i = this.sizeX; i < newSizeX; i++) {
               this.matrix[i] = new Object[newSizeY];
            }
         }

         this.sizeX = newSizeX;
         this.sizeY = newSizeY;
      }
   }
}
