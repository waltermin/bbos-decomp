package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;

final class Bullet {
   public int x;
   public int y;
   public int dy;
   Bitmap bmp;
   Board _board;
   int height;
   int width;
   static final int MAX_HEIGHT = 0;

   Bullet(Bitmap b, Board board) {
      this.bmp = b;
      this.width = b.getWidth();
      this.height = b.getHeight();
      this.dy = -1;
      this._board = board;
   }

   final void init() {
      this.dy = -1;
   }

   final void move(int factor) {
      if (this.dy > 0) {
         this.y = this.y - (Board.FACTORY * (factor * this.dy >> 16) >> 16);
         if (this.y < 0) {
            this.dy = -1;
         }
      }
   }

   final void deactivate() {
      this.dy = -1;
   }

   final void draw(Graphics g) {
      g.drawBitmap(this.x, this.y, this.width, this.height, this.bmp, 0, 0);
   }
}
