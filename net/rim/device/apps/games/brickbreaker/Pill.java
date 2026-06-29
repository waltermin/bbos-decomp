package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

final class Pill implements BrickBreakerResResource {
   private int x;
   private int y;
   public int dy;
   public int bonustype;
   public boolean isActive;
   public int count;
   public int _textWidth;
   public int _textHeight;
   String name;
   Board _board;
   Font newFont;
   Font curFont = Font.getDefault();
   private int deltaY;
   private static ResourceBundle _resources = ResourceBundle.getBundle(4228639183813622747L, "net.rim.device.apps.games.brickbreaker.BrickBreakerRes");
   static final int LNG;
   static final int GUN;
   static final int SHR;
   static final int SLW;
   static final int NEW;
   static final int FLP;
   static final int CAT;
   static final int LAS;
   static final int LIF;
   static final int SKP;
   static final int WRP;
   static final int BMB;
   static final Bitmap PILL = Bitmap.getBitmapResource(Game.currentModule, "pills.png");
   private static final int B1;
   private static final int B2;
   private static final int B3;
   private static final int B4;
   static final int WIDTH = PILL.getWidth() / 4;
   static final int HEIGHT = PILL.getHeight();

   Pill(Board b) {
      this.newFont = this.curFont.derive(1, 14);
      this._textHeight = this.newFont.getHeight();
      this._board = b;
      this.init();
   }

   final void init() {
      this.isActive = false;
      this.name = "";
      this.count = 0;
      this._textWidth = -1;
   }

   final void activate(int sx, int sy) {
      this.x = sx;
      this.y = sy;
      this.isActive = true;
      this.count = 0;
      this._textWidth = -1;
   }

   final void eat() {
      this.isActive = false;
   }

   final boolean checkCollision(int paddlex, int paddley, int paddlewidth) {
      return this.y + HEIGHT >= paddley && this.y + HEIGHT < paddley + 10 && this.dy > 0 && this.x > paddlex - WIDTH + 1 && this.x <= paddlex + paddlewidth - 1;
   }

   final void move(int factor) {
      this.deltaY = factor * (Board.FACTORY * this.dy >> 16) >> 16;
      if (this.deltaY == 0) {
         this.deltaY = 1;
      }

      this.y = this.y + this.deltaY;
      if (this.y > Board.HEIGHT) {
         this.isActive = false;
      }
   }

   final void setBonusType(int type) {
      this.bonustype = type;
      switch (type) {
         case 0:
         case 3:
            this.name = _resources.getString(28);
            break;
         case 1:
         default:
            this.name = _resources.getString(20);
            break;
         case 2:
            this.name = _resources.getString(21);
            break;
         case 4:
            this.name = _resources.getString(22);
            break;
         case 5:
            this.name = _resources.getString(24);
            break;
         case 6:
            this.name = _resources.getString(26);
            break;
         case 7:
            this.name = _resources.getString(27);
            break;
         case 8:
            this.name = _resources.getString(23);
            break;
         case 9:
            this.name = _resources.getString(28);
            break;
         case 10:
            this.name = _resources.getString(25);
            break;
         case 11:
            this.name = _resources.getString(36);
            break;
         case 12:
            this.name = _resources.getString(40);
      }

      this._textWidth = -1;
   }

   final void draw(Graphics g) {
      g.setFont(this.newFont);
      this.count++;
      int pos = 0;
      if (this._textWidth == -1) {
         this._textWidth = this.newFont.getBounds(this.name);
      }

      switch (this.count / 3 % 4) {
         case 0:
            break;
         case 1:
         default:
            pos = 1;
            break;
         case 2:
            pos = 2;
            break;
         case 3:
            pos = 3;
      }

      g.drawBitmap(this.x, this.y, WIDTH, HEIGHT, PILL, pos * WIDTH, 0);
      if (this.count < 30) {
         g.setColor(~this._board.getColor());
         if (this.x > Board.WIDTH >> 1) {
            g.drawText(this.name, this.x - WIDTH - 2, this.y + 2);
         } else {
            g.drawText(this.name, this.x + WIDTH + 2, this.y + 2);
         }
      }

      g.setFont(this.curFont);
   }
}
