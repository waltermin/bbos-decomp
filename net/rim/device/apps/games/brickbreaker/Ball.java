package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.games.util.FP;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.XYRect;

final class Ball {
   private int x;
   private int y;
   private int dx;
   private int dy;
   private int oldx;
   private int oldy;
   boolean isActive;
   int dxtemp;
   private int num_non_paddle_hits;
   public int num_non_brick_hits;
   private final int BALL_SIZE;
   private final int HALF_SIZE;
   private final int BOARD_LEFT;
   private final int BOARD_TOP;
   XYRect bubbleTop;
   XYRect bubbleLeft;
   XYRect bubbleRight;
   Board _board;
   Bricks bricks;
   private int _maxHeight;
   private int boardRight;
   private XYPoint result;
   private boolean collision;
   private int xMinus1;
   private int yMinus1;
   private int xPlus1;
   private int yPlus1;
   private int xPlus2;
   private int yPlus2;
   private int deltaX;
   private int deltaY;
   private int tickPos;
   private static final boolean FORCE_STICKY;
   public static int speedFactor = 3;
   private static final Bitmap BALLS = Bitmap.getBitmapResource(Game.currentModule, "balls.png");

   Ball(Board b, Bricks initialBricks) {
      this.BALL_SIZE = BALLS.getWidth() / 3;
      this.HALF_SIZE = this.BALL_SIZE >> 1;
      this.BOARD_LEFT = this.HALF_SIZE;
      this.BOARD_TOP = this.HALF_SIZE;
      this.bubbleTop = (XYRect)(new Object(0, 0, 0, 0));
      this.bubbleLeft = (XYRect)(new Object(0, 0, 0, 0));
      this.bubbleRight = (XYRect)(new Object(0, 0, 0, 0));
      this.result = (XYPoint)(new Object(0, 0));
      this.tickPos = 0;
      this._board = b;
      this.bricks = initialBricks;
      this.init();
   }

   final void init() {
      this.oldx = this.x = Board.WIDTH / 2 - this.HALF_SIZE;
      this.oldy = this.y = this._maxHeight - this.HALF_SIZE;
      this.dx = 1;
      this.dy = 0;
      this.isActive = false;
      this.num_non_paddle_hits = 0;
      this.num_non_brick_hits = 0;
   }

   public final void makeSticky(int x) {
      this.setSpeed(0, 0);
      this.setPosition(x, this._maxHeight - this.HALF_SIZE);
      this.dxtemp = 1;
   }

   public final void setMaxHeight(int h) {
      this._maxHeight = h;
   }

   public final void direction(int dir) {
      if (dir > 0) {
         this.dxtemp++;
         if (this.dxtemp == 0) {
            this.dxtemp = 1;
         }
      }

      if (dir < 0) {
         this.dxtemp--;
         if (this.dxtemp == 0) {
            this.dxtemp = -1;
         }
      }

      if (this.dxtemp > 4) {
         this.dxtemp = 4;
      }

      if (this.dxtemp < -4) {
         this.dxtemp = -4;
      }
   }

   public final int getX() {
      return this.x;
   }

   public final int getY() {
      return this.y;
   }

   public final void setPosition(int a, int b) {
      this.oldx = this.x = a;
      this.oldy = this.y = b;
   }

   public final void calculateCollisions(int WIDTH, int HEIGHT, Paddle paddle, int factor) {
      if (!this.isStopped()) {
         this.move(factor);
         this.boardRight = WIDTH - this.HALF_SIZE;
         if (this.x >= this.boardRight) {
            if (this.oldx >= this.boardRight) {
               this.collision = true;
               this.result.set(this.boardRight, this.y);
            } else {
               this.collision = FP.intersectionXY(this.boardRight, 0, this.boardRight, HEIGHT, this.oldx, this.oldy, this.x, this.y, this.result);
            }

            if (this.collision) {
               this.dx = -this.dx;
               this.x = this.boardRight;
               this.y = this.result.y;
               this.num_non_paddle_hits++;
               this.num_non_brick_hits++;
               this.bubbleRight.set(WIDTH - 2, this.y - 2, 4, 4);
               if (Options.sounds) {
                  Sounds.getSounds().play(6);
               }
            }
         }

         if (this.x <= this.BOARD_LEFT) {
            if (this.oldx <= this.BOARD_LEFT) {
               this.collision = true;
               this.result.set(this.BOARD_LEFT, this.y);
            } else {
               this.collision = FP.intersectionXY(this.BOARD_LEFT, 0, this.BOARD_LEFT, HEIGHT, this.oldx, this.oldy, this.x, this.y, this.result);
            }

            if (this.collision) {
               this.dx = -this.dx;
               this.x = this.BOARD_LEFT;
               this.y = this.result.y;
               this.num_non_paddle_hits++;
               this.num_non_brick_hits++;
               this.bubbleLeft.set(-2, this.y - 2, 4, 4);
               if (Options.sounds) {
                  Sounds.getSounds().play(6);
               }
            }
         }

         if (this.y < this.BOARD_TOP) {
            if (this.oldy < this.BOARD_TOP) {
               this.collision = true;
               this.result.set(this.x, this.BOARD_TOP);
            } else {
               this.collision = FP.intersectionXY(0, this.BOARD_TOP, WIDTH, this.BOARD_TOP, this.oldx, this.oldy, this.x, this.y, this.result);
            }

            if (this.collision) {
               this.dy = -this.dy;
               this.y = this.BOARD_TOP;
               this.x = this.result.x;
               this._board.increaseBounces(1);
               this.num_non_paddle_hits++;
               this.num_non_brick_hits++;
               this.bubbleTop.set(this.x - 2, -2, 4, 4);
               if (Options.sounds) {
                  Sounds.getSounds().play(6);
               }
            }
         }

         this.checkCollisions();
         XYRect extent = paddle.getExtent();
         this.collision = FP.intersectionXY(
            extent.x, paddle.HALF_HEIGHT, extent.x + extent.width, paddle.HALF_HEIGHT, this.oldx, this.oldy, this.x, this.y, this.result
         );
         if (!this.collision) {
            if (this.y >= HEIGHT) {
               this.deactivate();
               this._board.decreaseBalls();
            }
         } else {
            this.x = this.result.x;
            this.y = paddle.HALF_HEIGHT - this.HALF_SIZE;
            int PaddlePos = this.x - extent.x;
            PaddlePos /= extent.width >> 4;
            switch (PaddlePos) {
               case -1:
                  this.dx = 4;
                  break;
               case 0:
               case 1:
               default:
                  this.dx = -4;
                  break;
               case 2:
               case 3:
                  this.dx = -3;
                  break;
               case 4:
               case 5:
                  this.dx = -2;
                  break;
               case 6:
               case 7:
                  this.dx = -1;
                  break;
               case 8:
               case 9:
                  this.dx = 1;
                  break;
               case 10:
               case 11:
                  this.dx = 2;
                  break;
               case 12:
               case 13:
                  this.dx = 3;
            }

            if (paddle._isSticky) {
               this.dy = 0;
               this.dxtemp = this.dx;
               this.dx = 0;
            }

            this.num_non_paddle_hits = 0;
            if (Options.sounds) {
               Sounds.getSounds().play(7);
            }

            this.dy = -this.dy;
            int b = this._board.increaseBounces(1);
            this.num_non_paddle_hits = 0;
            if (b > 50) {
               this._board.moveDownBricks();
            }

            if (b > 30) {
               speedFactor = 3 + b / 30;
               if (speedFactor > 5) {
                  speedFactor = 3;
               }
            }
         }

         if (this.num_non_brick_hits > 10 || this.num_non_paddle_hits > 27) {
            this.dx = this.dx + (Board.FACTORX >> 16);
            if (this.dx == 0) {
               this.dx = 3;
            }

            this.num_non_paddle_hits = 0;
            this.num_non_brick_hits = 0;
         }
      }
   }

   public final boolean isStopped() {
      return this.dy == 0;
   }

   public final void setSpeed(int a, int b) {
      this.dx = a;
      this.dy = b;
   }

   final void activate() {
      this.isActive = true;
   }

   final void deactivate() {
      this.isActive = false;
      this.y = -1;
   }

   final boolean isActive() {
      return this.isActive;
   }

   private final boolean checkBallTop(int a, int b, int c, int d) {
      int amountMoved = this.bricks.getAmountMoved();
      this.collision = FP.intersectionXY(a, b, c, d, this.oldx, this.oldy - amountMoved, this.x, this.y - amountMoved, this.result);
      if (this.collision) {
         this.x = this.result.x;
         this.y = amountMoved + b - this.HALF_SIZE;
         this.dy = -this.dy;
         return true;
      } else {
         return false;
      }
   }

   private final boolean checkBallLeft(int a, int b, int c, int d) {
      int amountMoved = this.bricks.getAmountMoved();
      this.collision = FP.intersectionXY(a, b, c, d, this.oldx, this.oldy - amountMoved, this.x, this.y - amountMoved, this.result);
      if (this.collision) {
         this.x = a - this.HALF_SIZE;
         this.y = amountMoved + this.result.y;
         this.dx = -this.dx;
         return true;
      } else {
         return false;
      }
   }

   private final boolean checkBallRight(int a, int b, int c, int d) {
      int amountMoved = this.bricks.getAmountMoved();
      this.collision = FP.intersectionXY(a, b, c, d, this.oldx, this.oldy - amountMoved, this.x, this.y - amountMoved, this.result);
      if (this.collision) {
         this.x = c + this.HALF_SIZE;
         this.y = amountMoved + this.result.y;
         this.dx = -this.dx;
         return true;
      } else {
         return false;
      }
   }

   private final boolean checkBallBottom(int a, int b, int c, int d) {
      int amountMoved = this.bricks.getAmountMoved();
      this.collision = FP.intersectionXY(a, b, c, d, this.oldx, this.oldy - amountMoved, this.x, this.y - amountMoved, this.result);
      if (this.collision) {
         this.x = this.result.x;
         this.y = amountMoved + b + this.HALF_SIZE;
         this.dy = -this.dy;
         return true;
      } else {
         return false;
      }
   }

   private final void hit(int x, int y) {
      this._board.increaseBounces(1);
      this.num_non_paddle_hits++;
      this.num_non_brick_hits = 0;
      this.bricks.hitBrick(x, y, 2);
   }

   private final boolean checkCollisions() {
      int xpos = this.x / Board.TILEWIDTH;
      int ypos = (this.y - this.bricks.getAmountMoved()) / Board.TILEHEIGHT;
      if (xpos < 0 || xpos >= 7 || ypos < 0 || ypos >= 10) {
         return false;
      }

      if (this.bricks.isDestroyed(xpos, ypos)) {
         return false;
      }

      this.xMinus1 = xpos - 1;
      this.yMinus1 = ypos - 1;
      this.xPlus1 = xpos + 1;
      this.yPlus1 = ypos + 1;
      this.xPlus2 = xpos + 2;
      this.yPlus2 = ypos + 2;
      if (this.x > this.oldx && this.y < this.oldy) {
         if (xpos > 0
            && !this.bricks.isDestroyed(this.xMinus1, ypos)
            && this.checkBallLeft(this.xMinus1 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, this.xMinus1 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT)) {
            this.hit(this.xMinus1, ypos);
            return true;
         }

         if (ypos < 9
            && !this.bricks.isDestroyed(xpos, this.yPlus1)
            && this.checkBallBottom(xpos * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT)) {
            this.hit(xpos, this.yPlus1);
            return true;
         }

         if (xpos > 0 && ypos < 9 && !this.bricks.isDestroyed(this.xMinus1, this.yPlus1)) {
            if (this.checkBallLeft(
               this.xMinus1 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT, this.xMinus1 * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT
            )) {
               this.hit(this.xMinus1, this.yPlus1);
               return true;
            }

            if (this.checkBallBottom(this.xMinus1 * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT, xpos * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT)) {
               this.hit(this.xMinus1, this.yPlus1);
               return true;
            }
         }

         if (xpos > 0
            && !this.bricks.isDestroyed(this.xMinus1, ypos)
            && this.checkBallBottom(this.xMinus1 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT, xpos * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT)) {
            this.hit(this.xMinus1, ypos);
            return true;
         } else if (ypos < 9
            && !this.bricks.isDestroyed(xpos, this.yPlus1)
            && this.checkBallLeft(xpos * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT, xpos * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT)) {
            this.hit(xpos, this.yPlus1);
            return true;
         } else if (this.checkBallLeft(xpos * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, xpos * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT)) {
            this.hit(xpos, ypos);
            return true;
         } else if (this.checkBallBottom(xpos * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT)
            )
          {
            this.hit(xpos, ypos);
            return true;
         } else {
            return true;
         }
      } else if (this.x > this.oldx && this.y > this.oldy) {
         if (xpos > 0
            && !this.bricks.isDestroyed(this.xMinus1, ypos)
            && this.checkBallLeft(this.xMinus1 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, this.xMinus1 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT)) {
            this.hit(this.xMinus1, ypos);
            return true;
         }

         if (ypos > 0
            && !this.bricks.isDestroyed(xpos, this.yMinus1)
            && this.checkBallTop(xpos * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT)) {
            this.hit(xpos, this.yMinus1);
            return true;
         }

         if (xpos > 0 && ypos > 0 && !this.bricks.isDestroyed(this.xMinus1, this.yMinus1)) {
            if (this.checkBallLeft(this.xMinus1 * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT, this.xMinus1 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT)) {
               this.hit(this.xMinus1, this.yMinus1);
               return true;
            }

            if (this.checkBallTop(this.xMinus1 * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT, xpos * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT)) {
               this.hit(this.xMinus1, this.yMinus1);
               return true;
            }
         }

         if (xpos > 0
            && !this.bricks.isDestroyed(this.xMinus1, ypos)
            && this.checkBallTop(this.xMinus1 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, xpos * Board.TILEWIDTH, ypos * Board.TILEHEIGHT)) {
            this.hit(this.xMinus1, ypos);
            return true;
         } else if (ypos > 0
            && !this.bricks.isDestroyed(xpos, this.yMinus1)
            && this.checkBallLeft(xpos * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT, xpos * Board.TILEWIDTH, ypos * Board.TILEHEIGHT)) {
            this.hit(xpos, this.yMinus1);
            return true;
         } else if (this.checkBallLeft(xpos * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, xpos * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT)) {
            this.hit(xpos, ypos);
            return true;
         } else if (this.checkBallTop(xpos * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT)) {
            this.hit(xpos, ypos);
            return true;
         } else {
            return true;
         }
      } else {
         if (this.x < this.oldx && this.y > this.oldy) {
            if (xpos < 6
               && !this.bricks.isDestroyed(this.xPlus1, ypos)
               && this.checkBallRight(this.xPlus2 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, this.xPlus2 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT)) {
               this.hit(this.xPlus1, ypos);
               return true;
            }

            if (ypos > 0
               && !this.bricks.isDestroyed(xpos, this.yMinus1)
               && this.checkBallTop(xpos * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT)) {
               this.hit(xpos, this.yMinus1);
               return true;
            }

            if (xpos < 6 && ypos > 0 && !this.bricks.isDestroyed(this.xPlus1, this.yMinus1)) {
               if (this.checkBallRight(this.xPlus2 * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT, this.xPlus2 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT)) {
                  this.hit(this.xPlus1, this.yMinus1);
                  return true;
               }

               if (this.checkBallTop(
                  this.xPlus1 * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT, this.xPlus2 * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT
               )) {
                  this.hit(this.xPlus1, this.yMinus1);
                  return true;
               }
            }

            if (xpos < 6
               && !this.bricks.isDestroyed(this.xPlus1, ypos)
               && this.checkBallTop(this.xPlus1 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, this.xPlus2 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT)) {
               this.hit(this.xPlus1, ypos);
               return true;
            }

            if (ypos > 0
               && !this.bricks.isDestroyed(xpos, this.yMinus1)
               && this.checkBallRight(this.xPlus1 * Board.TILEWIDTH, this.yMinus1 * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT)) {
               this.hit(xpos, this.yMinus1);
               return true;
            }

            if (this.checkBallRight(this.xPlus1 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT)) {
               this.hit(xpos, ypos);
               return true;
            }

            if (this.checkBallTop(xpos * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT)) {
               this.hit(xpos, ypos);
               return true;
            }
         } else if (this.x < this.oldx && this.y < this.oldy) {
            if (xpos < 6
               && !this.bricks.isDestroyed(this.xPlus1, ypos)
               && this.checkBallRight(this.xPlus2 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, this.xPlus2 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT)) {
               this.hit(this.xPlus1, ypos);
               return true;
            }

            if (ypos < 9
               && !this.bricks.isDestroyed(xpos, this.yPlus1)
               && this.checkBallBottom(xpos * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT)) {
               this.hit(xpos, this.yPlus1);
               return true;
            }

            if (xpos < 6 && ypos < 9 && !this.bricks.isDestroyed(this.xPlus1, this.yPlus1)) {
               if (this.checkBallRight(
                  this.xPlus2 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT, this.xPlus2 * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT
               )) {
                  this.hit(this.xPlus1, this.yPlus1);
                  return true;
               }

               if (this.checkBallBottom(
                  this.xPlus1 * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT, this.xPlus2 * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT
               )) {
                  this.hit(this.xPlus1, this.yPlus1);
                  return true;
               }
            }

            if (xpos < 6
               && !this.bricks.isDestroyed(this.xPlus1, ypos)
               && this.checkBallBottom(
                  this.xPlus1 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT, this.xPlus2 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT
               )) {
               this.hit(this.xPlus1, ypos);
               return true;
            }

            if (ypos < 9
               && !this.bricks.isDestroyed(xpos, this.yPlus1)
               && this.checkBallRight(
                  this.xPlus1 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, this.yPlus2 * Board.TILEHEIGHT
               )) {
               this.hit(xpos, this.yPlus1);
               return true;
            }

            if (this.checkBallRight(this.xPlus1 * Board.TILEWIDTH, ypos * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT)) {
               this.hit(xpos, ypos);
               return true;
            }

            if (this.checkBallBottom(xpos * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT, this.xPlus1 * Board.TILEWIDTH, this.yPlus1 * Board.TILEHEIGHT)) {
               this.hit(xpos, ypos);
               return true;
            }
         }

         return false;
      }
   }

   public final void move(int factor) {
      this.oldx = this.x;
      this.oldy = this.y;
      this.deltaX = factor * (Board.FACTORX * speedFactor * this.dx >> 16) >> 16;
      this.deltaY = factor * (Board.FACTORY * speedFactor * this.dy >> 16) >> 16;
      if (this.deltaY == 0 && this.dy != 0) {
         if (this.dy > 0) {
            this.deltaY = 1;
         } else {
            this.deltaY = -1;
         }
      }

      if (this.deltaY != 0 && this.deltaX == 0) {
         if (this.dx > 0) {
            this.deltaX = 1;
         } else {
            this.deltaX = -1;
         }
      }

      if (this.deltaY > Board.TILEHEIGHT) {
         this.deltaY = Board.TILEHEIGHT;
      }

      if (this.deltaY < -Board.TILEHEIGHT) {
         this.deltaY = -Board.TILEHEIGHT;
      }

      if (this.deltaX > Board.TILEWIDTH) {
         this.deltaX = Board.TILEWIDTH;
      }

      if (this.deltaX < -Board.TILEWIDTH) {
         this.deltaX = -Board.TILEWIDTH;
      }

      this.x = this.x + this.deltaX;
      this.y = this.y + this.deltaY;
   }

   final void draw(Graphics g) {
      if (this.bubbleTop.width > 0) {
         g.setColor(1);
         g.setGlobalAlpha(255 - this.bubbleTop.width * 8);
         g.drawArc(this.bubbleTop.x, this.bubbleTop.y, this.bubbleTop.width, this.bubbleTop.height, 0, 360);
         this.bubbleTop.translate(-2, -2);
         this.bubbleTop.setSize(this.bubbleTop.width + 4, this.bubbleTop.height + 4);
         if (this.bubbleTop.width > 30) {
            this.bubbleTop.width = 0;
         }
      }

      if (this.bubbleLeft.width > 0) {
         if (Game.DRAW_LEFT_RAY) {
            Game.rayCounter += 40;
            if (Game.rayCounter > 80) {
               Game.rayCounter = 0;
            }
         } else {
            Game.DRAW_LEFT_RAY = true;
            Game.rayCounter = 0;
            Game.rayHeight = this.y - 14;
         }

         g.setColor(1);
         g.setGlobalAlpha(255 - this.bubbleLeft.width * 8);
         g.drawArc(this.bubbleLeft.x, this.bubbleLeft.y, this.bubbleLeft.width, this.bubbleLeft.height, 0, 360);
         this.bubbleLeft.translate(-2, -2);
         this.bubbleLeft.setSize(this.bubbleLeft.width + 4, this.bubbleLeft.height + 4);
         if (this.bubbleLeft.width > 30) {
            this.bubbleLeft.width = 0;
            Game.DRAW_LEFT_RAY = false;
         }
      }

      if (this.bubbleRight.width > 0) {
         if (Game.DRAW_RIGHT_RAY) {
            Game.rayCounter += 40;
            if (Game.rayCounter > 80) {
               Game.rayCounter = 0;
            }
         } else {
            Game.DRAW_RIGHT_RAY = true;
            Game.rayCounter = 40 * (this.bubbleRight.width % 4);
            Game.rayHeight = this.y - 14;
         }

         g.setColor(1);
         g.setGlobalAlpha(255 - this.bubbleRight.width * 8);
         g.drawArc(this.bubbleRight.x, this.bubbleRight.y, this.bubbleRight.width, this.bubbleRight.height, 0, 360);
         this.bubbleRight.translate(-2, -2);
         this.bubbleRight.setSize(this.bubbleRight.width + 4, this.bubbleRight.height + 4);
         if (this.bubbleRight.width > 30) {
            Game.DRAW_RIGHT_RAY = false;
            this.bubbleRight.width = 0;
         }
      }

      g.setGlobalAlpha(255);
      if (this._board.gotBomb) {
         this.tickPos++;
         if (this.tickPos >= 24) {
            this.tickPos = 0;
         }
      } else {
         this.tickPos = 0;
      }

      if (this.isStopped()) {
         g.setColor(153);
         g.setGlobalAlpha(255 - (this._board.getStickyCountPercentage() << 1));
         this.deltaX = this.dxtemp << 2;
         g.drawLine(this.x, this.y, this.x + this.deltaX, this.y - 20);
         g.setGlobalAlpha(255);
      }

      g.drawBitmap(this.x - this.HALF_SIZE, this.y - this.HALF_SIZE, this.BALL_SIZE, this.BALL_SIZE, BALLS, (this.tickPos >> 3) * this.BALL_SIZE, 0);
   }
}
