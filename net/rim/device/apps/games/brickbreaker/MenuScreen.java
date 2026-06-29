package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.internal.ui.component.PropertyField;

final class MenuScreen extends AppsMainScreen implements BrickBreakerResResource {
   private Game _game;
   private RichTextField text;
   private RichTextField title;
   private PropertyField username;
   private PropertyField score;
   private PropertyField level;
   private static ResourceBundle _resources = ResourceBundle.getBundle(4228639183813622747L, "net.rim.device.apps.games.brickbreaker.BrickBreakerRes");
   private static Options _options = new Options();
   static OptionsScreen optionsScreen;

   MenuScreen() {
      super(0);
      this.setHelp("brickbreaker");
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object(299067162755072L));
      vfm.add((Field)(new Object(_resources.getString(30))));
      BitmapField icon = (BitmapField)(new Object(Bitmap.getBitmapResource("icon.gif"), 36028809903865856L));
      vfm.add(icon);
      vfm.add(this.username = (PropertyField)(new Object(_resources.getString(46), Options.userName)));
      vfm.add(this.score = (PropertyField)(new Object(_resources.getString(51), ((StringBuffer)(new Object(""))).append(_options.highScore).toString())));
      vfm.add(this.level = (PropertyField)(new Object(_resources.getString(4), ((StringBuffer)(new Object(""))).append(_options.highLevel).toString())));
      vfm.add((Field)(new Object()));
      vfm.add(this.title = (RichTextField)(new Object(_resources.getString(15), 36028797018963968L)));
      this.text = (RichTextField)(new Object(
         ((StringBuffer)(new Object("\n")))
            .append(_resources.getString(32))
            .append("\n")
            .append(_resources.getString(13))
            .append("\n")
            .append(_resources.getString(14))
            .append("\n")
            .append(_resources.getString(52))
            .toString(),
         18014398509481984L
      ));
      vfm.add(this.text);
      this.add(vfm);
   }

   @Override
   protected final void onVisibilityChange(boolean change) {
      this.onExposed();
   }

   @Override
   protected final void onExposed() {
      this.username.setValue(Options.userName);
      this.username.setLabel(_resources.getString(46));
      this.score.setValue(((StringBuffer)(new Object(""))).append(_options.highScore).toString());
      this.score.setLabel(_resources.getString(51));
      this.level.setValue(((StringBuffer)(new Object(""))).append(_options.highLevel).toString());
      this.level.setLabel(_resources.getString(4));
      this.title.setText(_resources.getString(15));
      this.text
         .setText(
            ((StringBuffer)(new Object("\n")))
               .append(_resources.getString(32))
               .append("\n")
               .append(_resources.getString(13))
               .append("\n")
               .append(_resources.getString(14))
               .append("\n")
               .append(_resources.getString(52))
               .toString()
         );
      this.invalidate();
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (action == 1) {
         this.onMenu(1073741824);
         return true;
      } else {
         return super.invokeAction(action);
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(MenuItem.separator(10000));
      MenuItem newGame = new MenuScreen$1(this, _resources.getString(8), 10001, 100);
      menu.add(newGame);
      if (Options.isThereAHSS) {
         if (!Options.isScoreSent) {
            MenuItem send = new MenuScreen$2(this, _resources.getString(44), 10002, 100);
            menu.add(send);
         }

         MenuItem highscores = new MenuScreen$3(this, _resources.getString(45), 10003, 100);
         menu.add(highscores);
      }

      MenuItem options = new MenuScreen$4(this, _resources.getString(10), 10004, 100);
      menu.add(options);
   }

   @Override
   public final boolean onMenu(int instance) {
      super.onMenu(instance);
      this.invalidate();
      return true;
   }

   public final void setGame(Game g) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public static final Options getOptions() {
      return _options;
   }

   public static final synchronized void onOptions() {
      if (optionsScreen == null) {
         optionsScreen = new OptionsScreen();
      }

      optionsScreen.setOptions(_options);
      UiApplication.getUiApplication().pushModalScreen(optionsScreen);
      if (optionsScreen.isDirty()) {
         _options.save();
         optionsScreen.setDirty(false);
      }
   }
}
