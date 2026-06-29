package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;

final class Bricks {
   private Board _board;
   int num_blocks;
   private final int _brickWidth;
   private final int _brickHeight;
   private byte[] _levels;
   private int amountMoved;
   static final int XSIZE = 7;
   static final int YSIZE = 10;
   static final int XSIZE_MINUS_1 = 6;
   static final int YSIZE_MINUS_1 = 9;
   static short[][] Field;
   static short[][] Bonus;
   private static final Bitmap BRICKS = Bitmap.getBitmapResource(Game.currentModule, "bricks.png");
   private static final int X4 = 0;
   private static final int X3 = 1;
   private static final int X2 = 2;
   private static final int X1 = 3;
   private static final int A = 4;
   private static final int B = 5;
   private static final int C = 6;
   private static final int D = 7;
   private static final int GLASS = 8;
   private static int numLevels;

   Bricks(Board board) {
      this._brickWidth = BRICKS.getWidth();
      this._brickHeight = BRICKS.getHeight() / 9;
      this._levels = null;
      this.amountMoved = 0;
      Field = new short[10][7];
      Bonus = new short[10][7];
      this._board = board;
      Resource resource = null;
      String module = "net_rim_device_apps_games_brickbreaker";
      resource = Resource$Internal.getResourceClass(module);
      if (resource != null) {
         this._levels = resource.getResource("levels.bin");
         if (this._levels == null) {
            throw new RuntimeException("No levels!");
         }

         numLevels = this._levels[0];
      }
   }

   public static final int getNumLevels() {
      return numLevels;
   }

   public final boolean isDestroyed(int x, int y) {
      return Field[y][x] <= 0;
   }

   final void destroyBrick(int x, int y) {
      if (Options.sounds) {
         Sounds.getSounds().play(4);
      }

      this.CheckForBonus(x, y);
      if (Field[y][x] < 90) {
         this.num_blocks--;
      }

      Field[y][x] = 0;
   }

   final void hitBrick(int x, int y, int damage) {
      if (Field[y][x] > 0 && Field[y][x] < 90) {
         this._board.increasePoints(10);
         this.CheckForBonus(x, y);
         if (!this._board.gotBomb) {
            Field[y][x] = (short)(Field[y][x] - damage);
            if (Field[y][x] <= 0) {
               if (Options.sounds) {
                  Sounds.getSounds().play(4);
               }
            } else if (Field[y][x] > 0 && Options.sounds) {
               Sounds.getSounds().play(5);
            }
         } else {
            Field[y][x] = 0;
            if (Options.sounds) {
               Sounds.getSounds().play(3);
            }

            this._board.explode();
            this._board.increasePoints(10);
            if (y > 0) {
               this.hitBrick(x, y - 1, 2);
               if (x > 0) {
                  this.hitBrick(x - 1, y - 1, 1);
               }

               if (x < 6) {
                  this.hitBrick(x + 1, y - 1, 1);
               }
            }

            if (y < 9) {
               this.hitBrick(x, y + 1, 2);
               if (x > 0) {
                  this.hitBrick(x - 1, y + 1, 1);
               }

               if (x < 6) {
                  this.hitBrick(x + 1, y + 1, 1);
               }
            }

            if (x > 0) {
               this.hitBrick(x - 1, y, 2);
            }

            if (x < 6) {
               this.hitBrick(x + 1, y, 2);
            }
         }

         if (Field[y][x] <= 0) {
            this.num_blocks--;
         }
      }
   }

   final short RandomSpecialPill() {
      int temp = this._board.rand();
      if (temp < 10) {
         return 1;
      } else if (temp < 15) {
         return 12;
      } else if (temp < 30) {
         return 9;
      } else if (temp < 35) {
         return 11;
      } else if (temp < 45) {
         return 2;
      } else if (temp < 60) {
         return 7;
      } else if (temp < 65) {
         return 4;
      } else if (temp < 75) {
         return 5;
      } else if (temp < 80) {
         return 6;
      } else {
         return (short)(temp < 100 ? 8 : 0);
      }
   }

   final void CheckForBonus(int x, int y) {
      if (Bonus[y][x] > 0 && Field[y][x] < 90) {
         this._board._pills.drop(x, y, Bonus[y][x], this);
         Bonus[y][x] = 0;
      }
   }

   public final void paint(Graphics g, int x, int y, int position) {
      g.drawBitmap(x * Board.TILEWIDTH, this.amountMoved + y * Board.TILEHEIGHT, this._brickWidth, this._brickHeight, BRICKS, 0, position * this._brickHeight);
   }

   final void draw(Graphics g) {
      for (int y = 0; y < 10; y++) {
         for (int x = 0; x < 7; x++) {
            switch (Field[y][x]) {
               case -3:
                  Field[y][x]--;
                  this.paint(g, x, y, 0);
                  break;
               case -2:
                  Field[y][x]--;
                  this.paint(g, x, y, 1);
                  break;
               case -1:
                  this.paint(g, x, y, 2);
                  Field[y][x]--;
                  break;
               case 0:
                  this.paint(g, x, y, 3);
                  Field[y][x]--;
                  break;
               case 1:
               case 2:
                  this.paint(g, x, y, 4);
                  break;
               case 3:
               case 4:
                  this.paint(g, x, y, 5);
                  break;
               case 5:
               case 6:
                  this.paint(g, x, y, 6);
                  break;
               case 7:
               case 8:
                  this.paint(g, x, y, 7);
                  break;
               case 99:
                  this.paint(g, x, y, 8);
            }
         }
      }
   }

   public final void moveDown() {
      if (this.amountMoved <= 3 * Board.TILEHEIGHT) {
         this.amountMoved += 5;
      }
   }

   public final int getAmountMoved() {
      return this.amountMoved;
   }

   final void init(int level) {
      this.amountMoved = 0;
      numLevels = this._levels[0];
      byte[] currentLevel = new byte[70];
      System.arraycopy(this._levels, 1 + (level - 1) * 70, currentLevel, 0, 70);
      this.num_blocks = 0;
      int pointer = 0;

      for (int y = 0; y < 10; y++) {
         for (int x = 0; x < 7; x++) {
            byte temp = (byte)(15 & currentLevel[pointer++]);
            if (temp > 0 && temp < 15) {
               if (temp > 4) {
                  temp = 4;
               }

               Field[y][x] = (short)(2 * temp);
               this.num_blocks++;
            } else if (temp == 0) {
               Field[y][x] = -4;
            } else if (temp == 15) {
               Field[y][x] = 99;
            }
         }
      }

      pointer = 0;

      for (int y = 0; y < 10; y++) {
         for (int x = 0; x < 7; x++) {
            byte temp = (byte)(240 & currentLevel[pointer++]);
            if (temp == 0) {
               Bonus[y][x] = 0;
            } else {
               Bonus[y][x] = this.RandomSpecialPill();
            }
         }
      }
   }
}
