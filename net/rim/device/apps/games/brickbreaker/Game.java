package net.rim.device.apps.games.brickbreaker;

import java.io.InputStream;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MediaField;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.ImageBitmap;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;
import net.rim.plazmic.mediaengine.MediaManager;
import net.rim.plazmic.mediaengine.MediaPlayer;

final class Game extends AppsMainScreen implements BrickBreakerResResource, Runnable, ResourceProvider {
   int _level;
   int _state;
   int _points;
   int _superLevel;
   int _keys;
   private int _trackBall = 0;
   private boolean _paused;
   protected Board _board;
   private DataField _scoreField;
   private DataField _livesField;
   private DataField _ammoField;
   private DataField _hiScoreField;
   int _moveTime = 80;
   private final String BLANK = "";
   private OptionsScreen optionsScreen;
   private long lastTime;
   private int elapsed;
   private long frameStart;
   private int frameCount;
   private int framesPerSecond = 0;
   private int renderTime = 0;
   private int renderInterval = 35;
   private Application _currentApplication;
   private MediaPlayer _mediaplayer = (MediaPlayer)(new Object());
   private MediaField mediaField = (MediaField)(new Object());
   private final Bitmap BMP_WIN = Bitmap.getBitmapResource("icon.gif");
   private final Bitmap BMP_DEATH;
   private long renderEnd;
   private int averageRenderTime;
   private int targetRenderTime;
   private static ResourceBundle _resources = ResourceBundle.getBundle(4228639183813622747L, "net.rim.device.apps.games.brickbreaker.BrickBreakerRes");
   public static final int TARGET_FPS = 20;
   static final int PLAYING = 0;
   static final int GAMEOVER = 1;
   static final int DEATH = 3;
   static final int FINISHEDLEVEL = 4;
   static final int USERQUIT = -1;
   private static final String PME = "brickbreaker.pme";
   public static int rayHeight;
   public static int rayCounter;
   public static boolean DRAW_LEFT_RAY = false;
   public static boolean DRAW_RIGHT_RAY = false;
   public static String currentModule = null;
   static Options _options = MenuScreen.getOptions();
   static final Bitmap BMP_BOMB;
   static final Bitmap BMP_LIFE;
   public static final Bitmap BMP_BACKGROUND;

   final void increasePoints(int n) {
      this._points += n;
      this._scoreField.setValue(this._points);
   }

   final void init() {
      this._level = 0;
      this._state = 4;
      this._livesField.setValue(3);
      this._points = 0;
      this._keys = 0;
      this._superLevel = 0;
      this._paused = true;
      Sounds.getSounds().gamePaused(this._paused);
      this._hiScoreField.setValue(_options.highScore);
      this._scoreField.setValue(0);
      if (Alert.isMIDISupported() && Options.sounds) {
         Alert.setVolume(Options.getVolume());
      }

      if (Options.isServiceActive) {
         if (!Options.isScoreSent) {
         }

         Options.isServiceActive = false;
         _options.save();
      }
   }

   final void cycle() {
      while (this._state == 4 || this._state == 3) {
         if (this._state == 4) {
            this._level++;
            if (this._level > Bricks.getNumLevels()) {
               this._level = 1;
               this._superLevel++;
            }

            this.displayStoryScreen(this._level);
            this._board.init(this._level);
            this.playGame(this._level);
         } else {
            this._state = 0;
            if (this._livesField.getValue() > 0) {
               this.displayStoryScreen(-1);
               this._board.init(this._level, false);
               this.playGame(this._level);
            } else {
               this._state = 1;
            }
         }
      }

      if (this._state == 1) {
         if (this._level <= Bricks.getNumLevels()) {
            this.displayStoryScreen(-3);
         } else {
            this.displayStoryScreen(-2);
         }

         this.setHighScore(this._points);
         if (UiApplication.getUiApplication().getActiveScreen() == this) {
            UiApplication.getUiApplication().popScreen(this);
         }
      }
   }

   final void displayStoryScreen(int level) {
      String msg = "";
      Bitmap face = null;
      if (level > 0) {
         face = this.BMP_WIN;
         msg = ((StringBuffer)(new Object())).append(_resources.getString(4)).append(" ").append(level).append("/").append(Bricks.getNumLevels()).toString();
         if (this._superLevel > 1) {
            msg = ((StringBuffer)(new Object())).append(this._superLevel).append("x(").append(msg).append(')').toString();
         }
      } else {
         switch (level) {
            case -4:
               break;
            case -3:
               face = this.BMP_DEATH;
               msg = _resources.getString(31);
               break;
            case -2:
               face = this.BMP_WIN;
               msg = _resources.getString(6);
               break;
            case -1:
            default:
               face = this.BMP_DEATH;
               msg = _resources.getString(5);
         }
      }

      Dialog d = (Dialog)(new Object(msg, CommonResource.getStringArray(10004), null, 0, null, 0));
      d.setIcon(ImageBitmap.create(face));
      d.doModal();
   }

   final void playGame(int level) {
      this._state = 0;
      this.startTimer();
   }

   public final void setHighScore(int score) {
      _options.isGameActive();
      if (score > _options.highScore) {
         _options.highScore = score;
         _options.highLevel = this._level;
         _options.save();
         _options.sendScore(true);
      }
   }

   public final void startTimer() {
      this._paused = false;
      Sounds.getSounds().gamePaused(this._paused);
      this.lastTime = (int)System.currentTimeMillis();
      this.frameStart = System.currentTimeMillis();
      this.frameCount = 0;
      Application.getApplication().invokeLater(this);
      if (Alert.isMIDISupported() && Options.sounds) {
         Alert.setVolume(Options.getVolume());
      }
   }

   public final void pause() {
      this._paused = true;
      Sounds.getSounds().gamePaused(this._paused);
   }

   public final void quit() {
      this.pause();
      _options.save();
      this.setHighScore(this._points);
      this._mediaplayer.stop();
      System.exit(0);
   }

   public final void decreaseAmmo() {
      this._ammoField.decreaseValue(1);
   }

   public final void onOptions() {
      this.optionsScreen = new OptionsScreen();
      this.optionsScreen.setOptions(_options);
      UiApplication.getUiApplication().pushModalScreen(this.optionsScreen);
      if (this.optionsScreen.isDirty()) {
         _options.save();
         this.optionsScreen.setDirty(false);
      }

      this._hiScoreField.setValue(_options.highScore);
   }

   public final void decreaseLives() {
      this._livesField.decreaseValue(1);
   }

   public final void increaseLives() {
      this._livesField.increaseValue(1);
   }

   public final void setAmmo(int n) {
      this._ammoField.setValue(n);
   }

   public final int getAmmo() {
      return this._ammoField.getValue();
   }

   @Override
   public final void run() {
      long curTime = System.currentTimeMillis();
      if (this._currentApplication == null) {
         this._currentApplication = Application.getApplication();
      }

      this.elapsed = (int)(curTime - this.lastTime);
      if (this._state != 0) {
         this.pause();
         this.cycle();
      } else {
         if (!this._paused) {
            if (this._trackBall != 0) {
               this._board.getPaddle().move(this._trackBall, this._board);
               this._trackBall = 0;
            }

            this._board.calculations(this.elapsed);
            this._board.getPeer().invalidate(this._board);
            this.mediaField.mediaEvent(this, 20, 0, null);
            this.doPaint();
            this.frameDone(curTime);
            if (this.renderInterval <= 0) {
               this._currentApplication.invokeLater(this);
            } else if (this._currentApplication.invokeLater(this, this.renderInterval, false) == -1) {
               this._currentApplication.invokeLater(this);
            }
         }

         this.lastTime = curTime;
      }
   }

   @Override
   public final Object createResource(String type, Object o1, ResourceContext o2, Object referrer) {
      return null;
   }

   @Override
   public final Object createResourceFromURI(String uri, String suggestedType, ResourceContext context, Object referrer) {
      if (uri.startsWith("bg.")) {
         return BMP_BACKGROUND;
      }

      if (uri.startsWith("bomb.")) {
         return BMP_BOMB;
      }

      if (uri.startsWith("minipaddle.")) {
         return BMP_LIFE;
      }

      if (uri.startsWith("x-object:/board")) {
         StringTokenizer st = (StringTokenizer)(new Object(uri.substring(uri.indexOf(32))));
         this._board.position(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
         this._board.layout(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
         int bgColor = Integer.parseInt(st.nextToken());
         this._board.setBGColor(bgColor);
         return this._board;
      }

      if (!uri.startsWith("x-object:/score")
         && !uri.startsWith("x-object:/hiscore")
         && !uri.startsWith("x-object:/bullet")
         && !uri.startsWith("x-object:/life")) {
         return null;
      }

      DataField f;
      if (uri.startsWith("x-object:/score")) {
         f = this._scoreField;
      } else if (uri.startsWith("x-object:/hiscore")) {
         f = this._hiScoreField;
      } else if (uri.startsWith("x-object:/life")) {
         f = this._livesField;
      } else {
         f = this._ammoField;
      }

      try {
         StringTokenizer st = (StringTokenizer)(new Object(uri.substring(uri.indexOf(32))));
         f.setPosition(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
         f.layout(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
         f.setColors(Integer.parseInt(st.nextToken(), 16), Integer.parseInt(st.nextToken(), 16));
         String font = st.nextToken();
         int fontType = 0;
         if (font.startsWith("PLAIN")) {
            fontType = 0;
         } else if (font.startsWith("BOLD")) {
            fontType = 1;
         } else if (font.startsWith("ITALIC")) {
            fontType = 2;
         } else if (font.startsWith("EMBOSSED")) {
            fontType = 1280;
         }

         f.setFont(fontType, Integer.parseInt(st.nextToken(), 10));
      } finally {
         return f;
      }

      return f;
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      if (this._state != 4) {
         this.onMenu(0);
      }

      return true;
   }

   @Override
   public final boolean trackwheelRoll(int amount, int status, int time) {
      if (!this._paused) {
         this._board.getPaddle().move(amount, this._board);
      }

      return true;
   }

   @Override
   public final boolean keyControl(char c, int status, int time) {
      switch (c) {
         case '\u0095':
            break;
         case '\u0096':
         default:
            int volumeSetting = Options.getVolume() / 10;
            if (volumeSetting < 10) {
               volumeSetting++;
            }

            Options.setVolume(volumeSetting * 10);
            if (Options.sounds) {
               Sounds.getSounds().play(7);
            }
            break;
         case '\u0097':
            int volumeSetting = Options.getVolume() / 10;
            if (volumeSetting > 0) {
               volumeSetting--;
            }

            Options.setVolume(volumeSetting * 10);
            if (Options.sounds) {
               Sounds.getSounds().play(7);
            }
      }

      return super.keyControl(c, status, time);
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      switch (key) {
         case 27:
            UiApplication.getUiApplication().requestBackground();
            return true;
         case 32:
            if (!this._paused) {
               this._board.shoot();
            }

            return true;
         case 273:
            Options.sounds = !Options.sounds;
            Sounds.getSounds().updatePlayerVolumes();
         default:
            return super.keyDown(keycode, time);
         case 4098:
            if (this._state != 4) {
               this.onMenu(1073741824);
            }

            return true;
      }
   }

   @Override
   public final void paint(Graphics g) {
      super.paint(g);
      if (this._paused) {
         int color = g.getColor();
         int midx = Display.getWidth() >> 1;
         g.setColor(1052688);
         Font curFont = g.getFont();
         Font newFont = curFont.derive(1, 20);
         g.setFont(newFont);
         int h = 6 + newFont.getHeight();
         int textSize = 10 + newFont.getBounds(_resources.getString(3));
         g.fillRoundRect(midx - textSize / 2, Display.getHeight() / 2 - h / 2, textSize, h, 3, 3);
         g.setColor(15461355);
         g.drawText(_resources.getString(3), 5 + midx - (textSize >> 1), Display.getHeight() / 2 - h / 2 + 3);
         g.setColor(color);
         g.setFont(curFont);
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      boolean _pausedtmp = this._paused;
      menu.add(MenuItem.separator(10000));
      MenuItem resume = new Game$1(this, _resources.getString(7), 10001, 100);
      menu.add(resume);
      MenuItem newgame = new Game$2(this, _resources.getString(8), 10002, 100, _pausedtmp);
      menu.add(newgame);
      if (Options.isThereAHSS) {
         menu.add(MenuItem.separator(10003));
         if (!Options.isScoreSent) {
            MenuItem send = new Game$3(this, _resources.getString(44), 10004, 100);
            menu.add(send);
         }

         MenuItem highscores = new Game$4(this, _resources.getString(45), 10005, 100);
         menu.add(highscores);
         menu.add(MenuItem.separator(10006));
      }

      MenuItem options = new Game$5(this, _resources.getString(10), 10007, 100);
      menu.add(options);
      MenuItem about = new Game$6(this, _resources.getString(2), 10008, 100);
      menu.add(about);
      menu.add(MenuItem.separator(10011));
      MenuItem hide = new Game$7(this, _resources.getString(9), 10012, 100);
      menu.add(hide);
   }

   @Override
   public final boolean onMenu(int instance) {
      this.pause();
      this.invalidate();
      return super.onMenu(instance);
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached && Trackball.isSupported()) {
         this.setTrackballSensitivityXOffset(100);
         this.setTrackballSensitivityYOffset(-100);
      }
   }

   @Override
   public final boolean onClose() {
      Dialog diag = (Dialog)(new Object(3, _resources.getString(69), 0, null, 0));
      int r = diag.doModal();
      if (r == 4) {
         this.quit();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean navigationUnclick(int status, int time) {
      return true;
   }

   private final void frameDone(long startTime) {
      this.frameCount++;
      this.renderEnd = System.currentTimeMillis();
      this.renderTime = (int)(this.renderTime + (this.renderEnd - startTime));
      if (this.renderEnd - this.frameStart >= 1000) {
         this.framesPerSecond = this.frameCount;
         this.averageRenderTime = this.renderTime / this.frameCount;
         this.targetRenderTime = 50;
         this.renderInterval = this.targetRenderTime - this.averageRenderTime;
         if (this.renderInterval < 0) {
            this.renderInterval = 0;
         }

         this.frameCount = 0;
         this.frameStart = this.renderEnd;
         this.renderTime = 0;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   Game() {
      super(598134325510144L);
      this.BMP_DEATH = Bitmap.getBitmapResource(currentModule, "you_lose.png");
      this.setHelp("brickbreaker");
      boolean var3 = false /* VF: Semaphore variable */;

      label20:
      try {
         var3 = true;
         this._livesField = new DataField(0);
         this._ammoField = new DataField(0);
         this._board = new Board(this);
         this._scoreField = new DataField(0);
         this._hiScoreField = new DataField(0);
         Object e = readLevel("brickbreaker.pme", "application/x-vnd.rim.pme", this);
         this._mediaplayer.setMedia(e);
         this._mediaplayer.setUI(this.mediaField);
         var3 = false;
      } finally {
         if (var3) {
            Dialog.alert("Brickbreaker Error. \nCouldn't load game");
            System.exit(1);
            break label20;
         }
      }

      this.add(this.mediaField);
      this._board.init(1);
      this.init();
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      if (!this._paused) {
         this._trackBall += dx + dy;
      }

      return true;
   }

   @Override
   public final boolean navigationClick(int status, int time) {
      if (!this._paused) {
         this._board.shoot();
         return true;
      } else {
         this.invalidate();
         this.startTimer();
         return true;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final Object readLevel(String track, String type, ResourceProvider fop) {
      InputStream inputstream = null;
      Object model = null;

      label66:
      try {
         Resource resource = null;
         resource = Resource$Internal.getResourceClass(currentModule);
         if (resource == null) {
            Dialog.alert(((StringBuffer)(new Object())).append(currentModule).append(" not found.").toString());
            System.exit(1);
            return null;
         }

         byte[] data = resource.getResource(track);
         if (data == null) {
            throw new Object("Data es NULL");
         }

         inputstream = (InputStream)(new Object(data));
         MediaManager _manager = (MediaManager)(new Object());
         _manager.setCustomResourceProvider(fop);
         model = _manager.createResource(type, inputstream, null, null);
      } catch (Throwable var13) {
         System.out.println(exception);
         break label66;
      }

      try {
         if (inputstream != null) {
            inputstream.close();
         }
      } finally {
         return model;
      }

      return model;
   }

   @Override
   public final void onObscured() {
      this.pause();
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      currentModule = (String)ar.get(2907038216469438093L);
      String series = (String)ar.get(-760004391223775377L);
      Sounds.getSounds().setProperties(currentModule, series);
      BMP_BOMB = Bitmap.getBitmapResource(currentModule, "bomb.png");
      BMP_LIFE = Bitmap.getBitmapResource(currentModule, "minipaddle.png");
      BMP_BACKGROUND = Bitmap.getBitmapResource(currentModule, "bg.png");
   }
}
