package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class TiledLayer extends Layer {
   private int cellHeight;
   private int cellWidth;
   private int rows;
   private int columns;
   private int[][][] cellMatrix;
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
         this.cellMatrix = new int[rows][columns][];
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
      throw new RuntimeException("cod2jar: array store: unknown element");
   }

   public int getCell(int col, int row) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   public void fillCells(int col, int row, int numCols, int numRows, int tileIndex) {
      throw new RuntimeException("cod2jar: array store: unknown element");
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
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   private void createStaticSet(Image image, int noOfFrames, int tileWidth, int tileHeight, boolean maintainIndices) {
      throw new RuntimeException("cod2jar: array store: unknown element");
   }
}
