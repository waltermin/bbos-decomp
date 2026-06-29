package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class TiledLayer extends Layer {
   private int cellHeight;
   private int cellWidth;
   private int rows;
   private int columns;
   private int[][] cellMatrix;
   Image sourceImage;
   private int numberOfTiles;
   int[] tileSetX;
   int[] tileSetY;
   private int[] anim_to_static;
   private int numOfAnimTiles;

   public TiledLayer(int columns, int rows, Image image, int tileWidth, int tileHeight) {
      super(columns >= 1 && tileWidth >= 1 ? columns * tileWidth : -1, rows >= 1 && tileHeight >= 1 ? rows * tileHeight : -1);
      if (image.getWidth() % tileWidth == 0 && image.getHeight() % tileHeight == 0) {
         this.columns = columns;
         this.rows = rows;
         this.cellMatrix = new int[rows][columns];
         int noOfFrames = image.getWidth() / tileWidth * (image.getHeight() / tileHeight);
         this.createStaticSet(image, noOfFrames + 1, tileWidth, tileHeight, true);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int createAnimatedTile(int staticTileIndex) {
      if (staticTileIndex >= 0 && staticTileIndex < this.numberOfTiles) {
         if (this.anim_to_static == null) {
            this.anim_to_static = new int[4];
            this.numOfAnimTiles = 1;
         } else if (this.numOfAnimTiles == this.anim_to_static.length) {
            int[] new_anim_tbl = new int[this.anim_to_static.length * 2];
            System.arraycopy(this.anim_to_static, 0, new_anim_tbl, 0, this.anim_to_static.length);
            this.anim_to_static = new_anim_tbl;
         }

         this.anim_to_static[this.numOfAnimTiles] = staticTileIndex;
         this.numOfAnimTiles++;
         return -(this.numOfAnimTiles - 1);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setAnimatedTile(int animatedTileIndex, int staticTileIndex) {
      if (staticTileIndex >= 0 && staticTileIndex < this.numberOfTiles) {
         animatedTileIndex = -animatedTileIndex;
         if (this.anim_to_static != null && animatedTileIndex > 0 && animatedTileIndex < this.numOfAnimTiles) {
            this.anim_to_static[animatedTileIndex] = staticTileIndex;
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getAnimatedTile(int animatedTileIndex) {
      animatedTileIndex = -animatedTileIndex;
      if (this.anim_to_static != null && animatedTileIndex > 0 && animatedTileIndex < this.numOfAnimTiles) {
         return this.anim_to_static[animatedTileIndex];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setCell(int col, int row, int tileIndex) {
      if (col >= 0 && col < this.columns && row >= 0 && row < this.rows) {
         if (tileIndex > 0) {
            if (tileIndex >= this.numberOfTiles) {
               throw new IndexOutOfBoundsException();
            }
         } else if (tileIndex < 0 && (this.anim_to_static == null || -tileIndex >= this.numOfAnimTiles)) {
            throw new IndexOutOfBoundsException();
         }

         this.cellMatrix[row][col] = tileIndex;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getCell(int col, int row) {
      if (col >= 0 && col < this.columns && row >= 0 && row < this.rows) {
         return this.cellMatrix[row][col];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void fillCells(int col, int row, int numCols, int numRows, int tileIndex) {
      if (col >= 0
         && col < this.columns
         && row >= 0
         && row < this.rows
         && numCols >= 0
         && col + numCols <= this.columns
         && numRows >= 0
         && row + numRows <= this.rows) {
         if (tileIndex > 0) {
            if (tileIndex >= this.numberOfTiles) {
               throw new IndexOutOfBoundsException();
            }
         } else if (tileIndex < 0 && (this.anim_to_static == null || -tileIndex >= this.numOfAnimTiles)) {
            throw new IndexOutOfBoundsException();
         }

         for (int rowCount = row; rowCount < row + numRows; rowCount++) {
            for (int columnCount = col; columnCount < col + numCols; columnCount++) {
               this.cellMatrix[rowCount][columnCount] = tileIndex;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final int getCellWidth() {
      return this.cellWidth;
   }

   public final int getCellHeight() {
      return this.cellHeight;
   }

   public final int getColumns() {
      return this.columns;
   }

   public final int getRows() {
      return this.rows;
   }

   public void setStaticTileSet(Image image, int tileWidth, int tileHeight) {
      if (tileWidth >= 1 && tileHeight >= 1 && image.getWidth() % tileWidth == 0 && image.getHeight() % tileHeight == 0) {
         this.setWidthImpl(this.columns * tileWidth);
         this.setHeightImpl(this.rows * tileHeight);
         int noOfFrames = image.getWidth() / tileWidth * (image.getHeight() / tileHeight);
         if (noOfFrames >= this.numberOfTiles - 1) {
            this.createStaticSet(image, noOfFrames + 1, tileWidth, tileHeight, true);
         } else {
            this.createStaticSet(image, noOfFrames + 1, tileWidth, tileHeight, false);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void paint(Graphics g) {
      if (g == null) {
         throw new NullPointerException();
      }

      if (super.visible) {
         int tileIndex = 0;
         int ty = super.y;

         for (int row = 0; row < this.cellMatrix.length; ty += this.cellHeight) {
            int tx = super.x;
            int totalCols = this.cellMatrix[row].length;

            for (int column = 0; column < totalCols; tx += this.cellWidth) {
               tileIndex = this.cellMatrix[row][column];
               if (tileIndex != 0) {
                  if (tileIndex < 0) {
                     tileIndex = this.getAnimatedTile(tileIndex);
                  }

                  g.drawRegion(this.sourceImage, this.tileSetX[tileIndex], this.tileSetY[tileIndex], this.cellWidth, this.cellHeight, 0, tx, ty, 20);
               }

               column++;
            }

            row++;
         }
      }
   }

   private void createStaticSet(Image image, int noOfFrames, int tileWidth, int tileHeight, boolean maintainIndices) {
      this.cellWidth = tileWidth;
      this.cellHeight = tileHeight;
      int imageW = image.getWidth();
      int imageH = image.getHeight();
      this.sourceImage = image;
      this.numberOfTiles = noOfFrames;
      this.tileSetX = new int[this.numberOfTiles];
      this.tileSetY = new int[this.numberOfTiles];
      if (!maintainIndices) {
         for (this.rows = 0; this.rows < this.cellMatrix.length; this.rows++) {
            int totalCols = this.cellMatrix[this.rows].length;

            for (this.columns = 0; this.columns < totalCols; this.columns++) {
               this.cellMatrix[this.rows][this.columns] = 0;
            }
         }

         this.anim_to_static = null;
      }

      int currentTile = 1;

      for (int y = 0; y < imageH; y += tileHeight) {
         for (int x = 0; x < imageW; x += tileWidth) {
            this.tileSetX[currentTile] = x;
            this.tileSetY[currentTile] = y;
            currentTile++;
         }
      }
   }
}
