package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.ui.DialogWithBackgroundThread;
import net.rim.device.apps.api.ui.DialogWithBackgroundThreadRunnable;
import net.rim.vm.Memory;

public final class PasswordKeeperThread extends Thread implements DialogWithBackgroundThreadRunnable {
   private boolean _activate;
   private DialogWithBackgroundThread _dialogThread;

   public PasswordKeeperThread(boolean activate) {
      this._activate = activate;
   }

   @Override
   public final void run() {
      if (this._activate) {
         UiApplication app = UiApplication.getUiApplication();
         PasswordKeeperManager manager = PasswordKeeperManager.getInstance();

         try {
            manager.checkPassword();
            Screen activeScreen = app.getActiveScreen();
            if (activeScreen instanceof PasswordKeeperSplashScreen) {
               app.popScreen(activeScreen);
            }

            PasswordKeeper passwordKeeper = PasswordKeeper.getInstance();
            PasswordKeeperSync collection = passwordKeeper.getCollection();
            PasswordKeeperList source = collection.getSource();
            source.sort();
            KeywordFilterList list = (KeywordFilterList)(new Object(source, source));
            activeScreen = app.getActiveScreen();
            if (!(activeScreen instanceof PasswordKeeperScreen) && !(activeScreen instanceof PasswordKeeperElementScreen)) {
               PasswordKeeperScreen screen = new PasswordKeeperScreen(PasswordKeeper.getString(2011), list, source, collection);
               app.pushScreen(screen);
               return;
            }
         } catch (CancelException e) {
            manager.exit(false);
            return;
         }
      } else {
         PasswordKeeperManager manager = PasswordKeeperManager.getInstance();
         manager.clean();
         Memory.fullGC();
         if (this._dialogThread != null) {
            this._dialogThread.dismiss();
         }

         manager.exit(false);
      }
   }

   @Override
   public final void stop() {
   }

   @Override
   public final void setDialogWithBackgroundThread(DialogWithBackgroundThread dialogWithBackgroundThread) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
