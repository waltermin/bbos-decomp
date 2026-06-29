package net.rim.device.apps.games.brickbreaker;

import java.util.Random;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.ui.AbstractForeignObject;

final class Board extends AbstractForeignObject implements BrickBreakerResResource {
   private Paddle _paddle;
   private Bricks _bricks;
   private Game _game;
   protected Pills _pills;
   private Bullet _bomb;
   private Bullet[] _lasers;
   private final Bitmap BMP_LASER = Bitmap.getBitmapResource(Game.currentModule, "laser.png");
   private boolean FLASHING = false;
   private boolean SHAKING = false;
   private int shakingCount = 0;
   private final short[] shakePositions = new short[]{
      -10, 10, -8, 8, -6, 6, -10, 10, -4, 4, -2, 2, 0, 256, 28488, 7020, 11, 25345, -16116, 28482, 1954, 115, 25345, -16116, -18100, 29541
   };
   public boolean gotBomb;
   private String flashingString;
   private int flashCount = 0;
   private boolean GHOSTING = false;
   private int ghostingCount = 0;
   protected Ball[] _ball;
   private int num_bounces;
   public int num_active_balls;
   private int stickyPaddleCount;
   public boolean gotLaser;
   private Random rng;
   private int _x;
   private int _y;
   private short[] moveSound = new short[]{200, 40, 220, 30, 13, -12280, -10, 10};
   private int bgColor = 16777215;
   private boolean noBackground = false;
   private Font bigFont = Font.getDefault().derive(1, 42);
   private static ResourceBundle _resources = ResourceBundle.getBundle(4228639183813622747L, "net.rim.device.apps.games.brickbreaker.BrickBreakerRes");
   static final int MINIMUM_SPEED = 50;
   static int WIDTH;
   static int HEIGHT;
   public static int TILEWIDTH;
   public static int TILEHEIGHT;
   static int FACTORX;
   static int FACTORY;
   static final int MAX_NUM_BALLS = 4;
   static final int MAX_NUM_LASERS = 4;
   static final int MAX_STICKY_PADDLE_COUNT = 150;
   private static final int SPEED = 595;

   Board(Game game) {
      this._game = game;
      this._bricks = new Bricks(this);
      this._paddle = new Paddle(this);
      this._ball = new Ball[4];
      this._ball[0] = new Ball(this, this._bricks);
      this._ball[1] = new Ball(this, this._bricks);
      this._ball[2] = new Ball(this, this._bricks);
      this._ball[3] = new Ball(this, this._bricks);
      this.rng = (Random)(new Object());
      this._pills = new Pills(this);
      this._bomb = new Bullet(Game.BMP_BOMB, this);
      this._lasers = new Bullet[4];

      for (int i = 0; i < 4; i++) {
         this._lasers[i] = new Bullet(this.BMP_LASER, this);
      }
   }

   public final Ball[] getBalls() {
      return this._ball;
   }

   public final void position(int x, int y) {
      this.setPosition(x, y);
      this._x = x;
      this._y = y;
   }

   public final void layout(int x, int y) {
      this.setExtent(x, y);
      FACTORX = (x << 16) / 189;
      FACTORY = (y << 16) / 195;
      WIDTH = x;
      HEIGHT = y;
      TILEWIDTH = WIDTH / 7;
      TILEHEIGHT = HEIGHT / 15;
   }

   final void init(int l, boolean isFirstTime) {
      XYRect extent = this._paddle.getExtent();

      for (int i = 0; i < 4; i++) {
         this._ball[i].init();
      }

      for (int var5 = 0; var5 < 4; var5++) {
         this._lasers[var5].init();
      }

      this._bomb.init();
      this._pills.init();
      this._ball[0].activate();
      this.num_active_balls = 1;
      this.num_bounces = 50 * this._game._superLevel;
      this._paddle.init();
      this._ball[0].setMaxHeight(extent.y);
      this._ball[1].setMaxHeight(extent.y);
      this._ball[2].setMaxHeight(extent.y);
      this._ball[3].setMaxHeight(extent.y);
      this._paddle.setMode(0);
      this._ball[0].makeSticky(this._paddle.getExtent().x + this._paddle.getExtent().width / 2);
      this._game.setAmmo(0);
      this.gotLaser = false;
      if (isFirstTime) {
         this._bricks.init(l);
      }

      this.GHOSTING = true;
      Ball.speedFactor = 3;
      this._paddle.warpMode = false;
      this.gotBomb = false;
   }

   public final int getStickyCountPercentage() {
      return 100 * this.stickyPaddleCount / 150;
   }

   final void init(int l) {
      this.init(l, true);
   }

   public final int rand() {
      return this.rng.nextInt(100);
   }

   public final Paddle getPaddle() {
      return this._paddle;
   }

   final void calculations(int elapsed) {
      XYRect extent = this._paddle.getExtent();
      elapsed = 595 * elapsed;
      this.calcBalls(elapsed);
      this._pills.move(elapsed);
      this._bomb.move(elapsed);

      for (int i = 0; i < 4; i++) {
         this._lasers[i].move(elapsed);
      }

      this._pills.checkCollisions(extent.x, extent.y, extent.width);
   }

   final void calcBalls(int factor) {
      for (int i = 3; i >= 0; i--) {
         if (this._ball[i].isActive()) {
            this._ball[i].calculateCollisions(WIDTH, HEIGHT, this._paddle, factor);
         }
      }

      if (this.num_active_balls <= 0) {
         this._game._state = 3;
         this._game.decreaseLives();
      }

      if (this._ball[0].isStopped()) {
         this.stickyPaddleCount++;
         if (this.stickyPaddleCount > 150) {
            this.releaseStickyBall();
         }
      }

      if (this._bricks.num_blocks < 1) {
         this._game._state = 4;
      }
   }

   public final int increaseBounces(int n) {
      this.num_bounces += n;
      return this.num_bounces;
   }

   public final void decreaseBalls() {
      this.num_active_balls--;
   }

   public final void setBGColor(int c) {
      if (c < 0) {
         this.noBackground = true;
      }

      this.bgColor = c;
   }

   @Override
   public final int getHeight() {
      return HEIGHT;
   }

   @Override
   public final int getWidth() {
      return WIDTH;
   }

   @Override
   public final int getX() {
      return this._x;
   }

   @Override
   public final int getY() {
      return this._y;
   }

   public final int getColor() {
      return !this.noBackground ? this.bgColor : -1;
   }

   @Override
   public final void draw(Object g, int x, int y) {
      Graphics bgG = (Graphics)g;
      bgG.pushRegion(x, y, WIDTH, HEIGHT, 0, 0);
      if (!this.noBackground) {
         bgG.setBackgroundColor(this.bgColor);
         bgG.clear();
      }

      if (this.SHAKING) {
         bgG.translate(this.shakePositions[this.shakingCount++], 0);
         if (this.shakingCount >= this.shakePositions.length) {
            this.SHAKING = false;
            this.shakingCount = 0;
         }
      }

      if (this.GHOSTING) {
         bgG.setGlobalAlpha(this.ghostingCount);
         this.ghostingCount += 10;
         if (this.ghostingCount > 255) {
            this.GHOSTING = false;
            this.ghostingCount = 0;
         }
      }

      int bg = 16777215;
      if (this.FLASHING) {
         if (this.flashCount % 2 == 0) {
            bg = 10066176;
         } else {
            bg = 153;
         }

         this.flashCount++;
         if (this.flashCount > 4) {
            this.FLASHING = false;
            this.flashCount = 0;
         }

         bgG.setBackgroundColor(bg);
         bgG.clear();
      }

      this._bricks.draw(bgG);
      this._pills.draw(bgG);
      this.drawBullets(bgG);

      for (int i = 3; i >= 0; i--) {
         if (this._ball[i].isActive()) {
            this._ball[i].draw(bgG);
         }
      }

      this._paddle.paint(bgG);
      if (this.FLASHING) {
         bgG.setColor(~bg);
         bgG.setFont(this.bigFont);
         bgG.drawText(this.flashingString, WIDTH - this.bigFont.getAdvance(this.flashingString) >> 1, 0);
      }

      bgG.popContext();
   }

   final void drawBullets(Graphics graphics) {
      if (this._bomb.dy > 0) {
         this._bomb.draw(graphics);
         int xpos = (this._bomb.x + this._bomb.width / 2) / TILEWIDTH;
         int ypos = (this._bomb.y - this._bricks.getAmountMoved()) / TILEHEIGHT;
         if (ypos < 10 && ypos >= 0 && xpos < 7 && xpos >= 0 && Bricks.Field[ypos][xpos] > 0) {
            this._bricks.destroyBrick(xpos, ypos);
            this._game.increasePoints(50);
            this._bomb.deactivate();
         }
      }

      for (int i = 3; i >= 0; i--) {
         if (this._lasers[i].dy > 0) {
            this._lasers[i].draw(graphics);
            int xpos = (this._lasers[i].x + this._lasers[i].width / 2) / TILEWIDTH;
            int ypos = (this._lasers[i].y - this._bricks.getAmountMoved()) / TILEHEIGHT;
            if (ypos < 10 && ypos >= 0 && xpos < 7 && xpos >= 0 && Bricks.Field[ypos][xpos] > 0) {
               this._bricks.hitBrick(xpos, ypos, 1);
               this._lasers[i].deactivate();
            }
         }
      }
   }

   final void powerUp(int bonustype) {
      this.FLASHING = true;
      switch (bonustype) {
         case 0:
         case 3:
         case 10:
            break;
         case 1:
            this._game.setAmmo(0);
            this._paddle._isFlipped = false;
            this.gotLaser = false;
            this._paddle._isSticky = false;
            this._paddle.setMode(1);
            this.flashingString = _resources.getString(20);
            break;
         case 2:
         default:
            this.gotBomb = false;
            this._game.setAmmo(3);
            this._paddle._isSticky = false;
            this._paddle._isFlipped = false;
            this.gotLaser = false;
            this._paddle.setMode(3);
            this.flashingString = _resources.getString(21);
            break;
         case 4:
            this.slowDown();
            this._paddle._isFlipped = false;
            this.flashingString = _resources.getString(22);
            break;
         case 5:
            this.addBalls();
            this._paddle._isSticky = false;
            this._paddle._isFlipped = false;
            this.flashingString = _resources.getString(24);
            break;
         case 6:
            this._paddle._isFlipped = true;
            this.flashingString = _resources.getString(26);
            break;
         case 7:
            this.gotBomb = false;
            this._game.setAmmo(0);
            this._paddle._isSticky = true;
            this._paddle._isFlipped = false;
            this.gotLaser = false;
            this.killBallsExceptOne();
            this._paddle.setMode(0);
            this._paddle.warpMode = false;
            this.flashingString = _resources.getString(27);
            break;
         case 8:
            this.gotBomb = false;
            this.gotLaser = true;
            this._paddle._isSticky = false;
            this._paddle._isFlipped = false;
            this._game.setAmmo(0);
            this._paddle.setMode(2);
            this.flashingString = _resources.getString(23);
            break;
         case 9:
            this._game.increaseLives();
            this.gotLaser = false;
            this._game.setAmmo(0);
            this._paddle._isSticky = false;
            this._paddle._isFlipped = false;
            this._paddle.setMode(0);
            this.flashingString = _resources.getString(28);
            break;
         case 11:
            this._paddle.warpMode = true;
            this.flashingString = _resources.getString(36);
            break;
         case 12:
            this.gotBomb = true;
            this.gotLaser = false;
            this._game.setAmmo(0);
            this._paddle._isFlipped = false;
            this._paddle._isSticky = false;
            this._paddle.setMode(0);
            this.flashingString = _resources.getString(40);
            this.killBallsExceptOne();
      }

      this._game.increasePoints(50);
   }

   public final void increasePoints(int n) {
      this._game.increasePoints(n);
   }

   final void addBalls() {
      int activeball = -1;
      this.num_active_balls = 4;

      for (int i = 0; i < 4 && activeball == -1; i++) {
         if (this._ball[i].isActive) {
            activeball = i;
         }
      }

      if (activeball != -1) {
         for (int i = 0; i < 4; i++) {
            this._ball[i].setPosition(this._ball[activeball].getX(), this._ball[activeball].getY());
         }
      }

      this._ball[0].setSpeed(-3, -3);
      this._ball[1].setSpeed(-1, -3);
      this._ball[2].setSpeed(1, -3);
      this._ball[3].setSpeed(3, -3);

      for (int i = 0; i < 4; i++) {
         this._ball[i].activate();
      }
   }

   final void killBallsExceptOne() {
      int activeball = -1;

      for (int i = 0; i < 4; i++) {
         if (this._ball[i].isActive && (activeball == -1 || this._ball[i].getY() < this._ball[activeball].getY())) {
            activeball = i;
         }
      }

      if (activeball != -1 && activeball != 0) {
         Ball _swap = this._ball[0];
         this._ball[0] = this._ball[activeball];
         this._ball[activeball] = _swap;
      }

      for (int i = 1; i < 4; i++) {
         this._ball[i].isActive = false;
      }

      this._ball[0].isActive = true;
      this.num_active_balls = 1;
   }

   final void slowDown() {
      Ball.speedFactor = 3;
      this.num_bounces = 0;
   }

   final void releaseStickyBall() {
      if (this._ball[0].isStopped()) {
         this._ball[0].setSpeed(this._ball[0].dxtemp, -3);
         this._ball[0].move(4096);
         this.stickyPaddleCount = 0;
      }
   }

   public final void moveDownBricks() {
      if (Options.sounds && Alert.isBuzzerSupported()) {
         Alert.startBuzzer(this.moveSound, Options.getVolume(), 0);
      }

      this._bricks.moveDown();
   }

   final void shoot() {
      this.releaseStickyBall();
      XYRect extent = this._paddle.getExtent();
      if (this._game.getAmmo() > 0 && this._bomb.dy < 0) {
         this._bomb.y = extent.y - 7;
         this._bomb.x = extent.x + (extent.width >> 1) - (this._bomb.width >> 1);
         this._bomb.dy = 10;
         this._game.decreaseAmmo();
         if (this._game.getAmmo() == 0) {
            this._paddle.setMode(0);
            return;
         }
      } else if (this.gotLaser) {
         int left = -1;
         int right = -1;
         if (this._lasers[0].dy < 0) {
            left = 0;
         } else if (this._lasers[1].dy < 0) {
            left = 1;
         }

         if (this._lasers[2].dy < 0) {
            right = 2;
         } else if (this._lasers[3].dy < 0) {
            right = 3;
         }

         if (left >= 0) {
            this._lasers[left].y = extent.y;
            this._lasers[left].x = extent.x + (this._paddle._size >> 2);
            this._lasers[left].dy = 15;
         }

         if (right >= 0) {
            this._lasers[right].y = extent.y;
            this._lasers[right].x = extent.x + 3 * (this._paddle._size >> 2);
            this._lasers[right].dy = 15;
         }

         if ((left >= 0 || right >= 0) && Options.sounds) {
            Sounds.getSounds().play(2);
         }
      }
   }

   public final void explode() {
      this.SHAKING = true;
      this.FLASHING = true;
      this.flashingString = null;
      this.gotBomb = false;
   }
}
