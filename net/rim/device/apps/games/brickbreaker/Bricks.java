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
   static final int XSIZE;
   static final int YSIZE;
   static final int XSIZE_MINUS_1;
   static final int YSIZE_MINUS_1;
   static short[][][] Field;
   static short[][][] Bonus;
   private static final Bitmap BRICKS = Bitmap.getBitmapResource(Game.currentModule, "bricks.png");
   private static final int X4;
   private static final int X3;
   private static final int X2;
   private static final int X1;
   private static final int A;
   private static final int B;
   private static final int C;
   private static final int D;
   private static final int GLASS;
   private static int numLevels;

   Bricks(Board board) {
      this._brickWidth = BRICKS.getWidth();
      this._brickHeight = BRICKS.getHeight() / 9;
      this._levels = null;
      this.amountMoved = 0;
      Field = new short[10][7][];
      Bonus = new short[10][7][];
      this._board = board;
      Resource resource = null;
      String module = "net_rim_device_apps_games_brickbreaker";
      resource = Resource$Internal.getResourceClass(module);
      if (resource != null) {
         this._levels = resource.getResource("levels.bin");
         if (this._levels == null) {
            throw new Object("No levels!");
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

      Field[y][x] = (short[])false;
   }

   final void hitBrick(int x, int y, int damage) {
      if (!(Field[y][x] <= 0) && !(Field[y][x] >= 90)) {
         this._board.increasePoints(10);
         this.CheckForBonus(x, y);
         if (!this._board.gotBomb) {
            Field[y][x] = (short[])((short)(Field[y][x] - damage));
            if (Field[y][x] <= 0) {
               if (Options.sounds) {
                  Sounds.getSounds().play(4);
               }
            } else if (Field[y][x] > 0 && Options.sounds) {
               Sounds.getSounds().play(5);
            }
         } else {
            Field[y][x] = (short[])false;
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
         this._board._pills.drop(x, y, (int)Bonus[y][x], this);
         Bonus[y][x] = (short[])false;
      }
   }

   public final void paint(Graphics g, int x, int y, int position) {
      g.drawBitmap(x * Board.TILEWIDTH, this.amountMoved + y * Board.TILEHEIGHT, this._brickWidth, this._brickHeight, BRICKS, 0, position * this._brickHeight);
   }

   final void draw(Graphics param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Invalid switch case set: [[const(-3)], [const(-2)], [const(-1)], [const(0)], [const(1), const(2)], [const(3), const(4)], [const(5), const(6)], [const(7), const(8)], [const(99)], [null]] for selector of type [S
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.SwitchHeadExprent.checkExprTypeBounds(SwitchHeadExprent.java:63)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:137)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 2
      // 002: iload 2
      // 003: bipush 10
      // 005: if_icmplt 00b
      // 008: goto 133
      // 00b: bipush 0
      // 00c: istore 3
      // 00d: iload 3
      // 00e: bipush 7
      // 010: if_icmplt 016
      // 013: goto 12d
      // 016: getstatic net/rim/device/apps/games/brickbreaker/Bricks.Field [[[S
      // 019: iload 2
      // 01a: aaload
      // 01b: iload 3
      // 01c: saload
      // 01d: lookupswitch 266 13 -3 115 -2 138 -1 161 0 185 1 209 2 209 3 221 4 221 5 233 6 233 7 245 8 245 99 257
      // 090: getstatic net/rim/device/apps/games/brickbreaker/Bricks.Field [[[S
      // 093: iload 2
      // 094: aaload
      // 095: iload 3
      // 096: dup2
      // 097: saload
      // 098: bipush 1
      // 099: isub
      // 09a: i2s
      // 09b: sastore
      // 09c: aload 0
      // 09d: aload 1
      // 09e: iload 3
      // 09f: iload 2
      // 0a0: bipush 0
      // 0a1: invokevirtual net/rim/device/apps/games/brickbreaker/Bricks.paint (Lnet/rim/device/api/ui/Graphics;III)V
      // 0a4: goto 127
      // 0a7: getstatic net/rim/device/apps/games/brickbreaker/Bricks.Field [[[S
      // 0aa: iload 2
      // 0ab: aaload
      // 0ac: iload 3
      // 0ad: dup2
      // 0ae: saload
      // 0af: bipush 1
      // 0b0: isub
      // 0b1: i2s
      // 0b2: sastore
      // 0b3: aload 0
      // 0b4: aload 1
      // 0b5: iload 3
      // 0b6: iload 2
      // 0b7: bipush 1
      // 0b8: invokevirtual net/rim/device/apps/games/brickbreaker/Bricks.paint (Lnet/rim/device/api/ui/Graphics;III)V
      // 0bb: goto 127
      // 0be: aload 0
      // 0bf: aload 1
      // 0c0: iload 3
      // 0c1: iload 2
      // 0c2: bipush 2
      // 0c4: invokevirtual net/rim/device/apps/games/brickbreaker/Bricks.paint (Lnet/rim/device/api/ui/Graphics;III)V
      // 0c7: getstatic net/rim/device/apps/games/brickbreaker/Bricks.Field [[[S
      // 0ca: iload 2
      // 0cb: aaload
      // 0cc: iload 3
      // 0cd: dup2
      // 0ce: saload
      // 0cf: bipush 1
      // 0d0: isub
      // 0d1: i2s
      // 0d2: sastore
      // 0d3: goto 127
      // 0d6: aload 0
      // 0d7: aload 1
      // 0d8: iload 3
      // 0d9: iload 2
      // 0da: bipush 3
      // 0dc: invokevirtual net/rim/device/apps/games/brickbreaker/Bricks.paint (Lnet/rim/device/api/ui/Graphics;III)V
      // 0df: getstatic net/rim/device/apps/games/brickbreaker/Bricks.Field [[[S
      // 0e2: iload 2
      // 0e3: aaload
      // 0e4: iload 3
      // 0e5: dup2
      // 0e6: saload
      // 0e7: bipush 1
      // 0e8: isub
      // 0e9: i2s
      // 0ea: sastore
      // 0eb: goto 127
      // 0ee: aload 0
      // 0ef: aload 1
      // 0f0: iload 3
      // 0f1: iload 2
      // 0f2: bipush 4
      // 0f4: invokevirtual net/rim/device/apps/games/brickbreaker/Bricks.paint (Lnet/rim/device/api/ui/Graphics;III)V
      // 0f7: goto 127
      // 0fa: aload 0
      // 0fb: aload 1
      // 0fc: iload 3
      // 0fd: iload 2
      // 0fe: bipush 5
      // 100: invokevirtual net/rim/device/apps/games/brickbreaker/Bricks.paint (Lnet/rim/device/api/ui/Graphics;III)V
      // 103: goto 127
      // 106: aload 0
      // 107: aload 1
      // 108: iload 3
      // 109: iload 2
      // 10a: bipush 6
      // 10c: invokevirtual net/rim/device/apps/games/brickbreaker/Bricks.paint (Lnet/rim/device/api/ui/Graphics;III)V
      // 10f: goto 127
      // 112: aload 0
      // 113: aload 1
      // 114: iload 3
      // 115: iload 2
      // 116: bipush 7
      // 118: invokevirtual net/rim/device/apps/games/brickbreaker/Bricks.paint (Lnet/rim/device/api/ui/Graphics;III)V
      // 11b: goto 127
      // 11e: aload 0
      // 11f: aload 1
      // 120: iload 3
      // 121: iload 2
      // 122: bipush 8
      // 124: invokevirtual net/rim/device/apps/games/brickbreaker/Bricks.paint (Lnet/rim/device/api/ui/Graphics;III)V
      // 127: iinc 3 1
      // 12a: goto 00d
      // 12d: iinc 2 1
      // 130: goto 002
      // 133: return
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

               Field[y][x] = (short[])((short)(2 * temp));
               this.num_blocks++;
            } else if (temp == 0) {
               Field[y][x] = (short[])-4;
            } else if (temp == 15) {
               Field[y][x] = (short[])99;
            }
         }
      }

      pointer = 0;

      for (int y = 0; y < 10; y++) {
         for (int x = 0; x < 7; x++) {
            byte temp = (byte)(240 & currentLevel[pointer++]);
            if (temp == 0) {
               Bonus[y][x] = (short[])false;
            } else {
               Bonus[y][x] = (short[])this.RandomSpecialPill();
            }
         }
      }
   }
}
