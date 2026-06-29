package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

final class Paddle {
   public int _size;
   Bitmap _paddleBitmap;
   public boolean _isSticky;
   public boolean _isFlipped;
   public boolean warpMode = false;
   private int MAXPADDLE_X = Board.WIDTH;
   private int MINPADDLE_X = 0;
   private final Bitmap PADDLE = Bitmap.getBitmapResource(Game.currentModule, "paddles.png");
   private final Bitmap BMP_LONG = Bitmap.getBitmapResource(Game.currentModule, "paddlelong.png");
   private int position;
   private XYRect bounds = (XYRect)(new Object());
   private final int DEFAULT_WIDTH = this.PADDLE.getWidth();
   private final int HEIGHT = this.PADDLE.getHeight() / 3;
   public int HALF_HEIGHT;
   public final int PADDLE_Y = Board.HEIGHT - this.HEIGHT;
   public static final int DEFAULT = 0;
   public static final int LONG = 1;
   public static final int LASER = 2;
   public static final int GUN = 3;

   Paddle(Board board) {
      this.init();
   }

   public final void init() {
      this._size = this.DEFAULT_WIDTH;
      this._paddleBitmap = this.PADDLE;
      this._isSticky = false;
      this._isFlipped = false;
      this.setLocation(Board.WIDTH / 2 - this._size / 2, Board.HEIGHT - this.HEIGHT);
      this.MAXPADDLE_X = Board.WIDTH;
      this.HALF_HEIGHT = Board.HEIGHT - this.HEIGHT;
   }

   private final void setLocation(int x, int y) {
      this.bounds.x = x;
      this.bounds.y = y;
   }

   private final void setSize(int width, int height) {
      this.bounds.width = width;
      this.bounds.height = height;
   }

   public final XYRect getExtent() {
      return this.bounds;
   }

   final void setMode(int mode) {
      switch (mode) {
         case 0:
            this._paddleBitmap = this.PADDLE;
            this.position = 0;
            this._size = this.PADDLE.getWidth();
            break;
         case 1:
         default:
            this._paddleBitmap = this.BMP_LONG;
            this.position = 0;
            this._size = this.BMP_LONG.getWidth();
            break;
         case 2:
            this._paddleBitmap = this.PADDLE;
            this.position = 2;
            this._size = this.PADDLE.getWidth();
            break;
         case 3:
            this._paddleBitmap = this.PADDLE;
            this.position = 1;
            this._size = this.PADDLE.getWidth();
      }

      this.setSize(this._size, this.HEIGHT);
   }

   public final void move(int dir, Board board) {
      if (board.getBalls()[0].isStopped()) {
         board.getBalls()[0].direction(dir);
      } else {
         int x = this.bounds.x;
         if (this._isFlipped) {
            dir = -dir;
         }

         int s = Game._options.paddleSpeed;
         int a = 1;
         if (Game._options.paddleAccel) {
            if (dir > 1 || dir < -1) {
               a = 2;
            }

            if (dir > 3 || dir < -3) {
               a = 3;
            }
         }

         x += Board.FACTORX * dir * (s << 1) * a >> 16;
         if (this.warpMode) {
            if (x < this.MINPADDLE_X - this._size) {
               x = this.MAXPADDLE_X - this._size;
            } else if (x > this.MAXPADDLE_X) {
               x = this.MINPADDLE_X;
            }
         } else if (x > this.MAXPADDLE_X - this._size) {
            x = this.MAXPADDLE_X - this._size;
         } else if (x < this.MINPADDLE_X) {
            x = this.MINPADDLE_X;
         }

         this.setLocation(x, this.bounds.y);
      }
   }

   public final void paint(Graphics g) {
      g.drawBitmap(this.bounds.x, this.bounds.y, this._size, this.HEIGHT, this._paddleBitmap, 0, this.position * this.HEIGHT);
   }
}
