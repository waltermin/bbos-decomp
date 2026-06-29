package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.ui.Graphics;

final class Pills {
   Pill[] pill;
   Board _board;
   static final int MAXPILLS;

   Pills(Board board) {
      this._board = board;
      this.pill = new Pill[3];

      for (int i = 0; i < 3; i++) {
         this.pill[i] = new Pill(board);
      }
   }

   final void init() {
      for (int i = 0; i < 3; i++) {
         if (this.pill[i].isActive) {
            this.pill[i].init();
         }
      }
   }

   final void move(int elapsed) {
      for (int i = 0; i < 3; i++) {
         if (this.pill[i].isActive) {
            this.pill[i].move(elapsed);
         }
      }
   }

   final void drop(int x, int y, int bonustype, Bricks _bricks) {
      boolean found = false;

      int i;
      for (i = 0; i < 3; i++) {
         if (!this.pill[i].isActive) {
            found = true;
            break;
         }
      }

      if (found) {
         this.pill[i].activate(x * Board.TILEWIDTH + 4, _bricks.getAmountMoved() + y * Board.TILEHEIGHT);
         int r = this._board.rand();
         if (r < 50) {
            this.pill[i].dy = 3;
         } else {
            this.pill[i].dy = 4;
         }

         this.pill[i].setBonusType(bonustype);
         this.pill[i].count = 0;
         if (Options.sounds) {
            Sounds.getSounds().play(0);
         }
      }
   }

   final void checkCollisions(int paddlex, int paddley, int paddlewidth) {
      for (int i = 0; i < 3; i++) {
         if (this.pill[i].isActive && this.pill[i].checkCollision(paddlex, paddley, paddlewidth)) {
            if (Options.sounds) {
               Sounds.getSounds().play(1);
            }

            this._board.powerUp(this.pill[i].bonustype);
            this.pill[i].eat();
         }
      }
   }

   final void draw(Graphics g) {
      for (int i = 0; i < 3; i++) {
         if (this.pill[i].isActive) {
            this.pill[i].draw(g);
         }
      }
   }
}
