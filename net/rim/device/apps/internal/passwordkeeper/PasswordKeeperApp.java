package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.UiApplication;

public final class PasswordKeeperApp extends UiApplication {
   private static final long ID = -8494186572112499737L;

   private PasswordKeeperApp() {
      PasswordKeeper.getInstance();
      PasswordKeeperManager.getInstance().setUiApplication(this);
   }

   @Override
   public final void activate() {
      UiApplication app = UiApplication.getUiApplication();
      PasswordKeeperThread thread = new PasswordKeeperThread(true);
      app.invokeLater(thread);
   }

   @Override
   public final void deactivate() {
      PasswordKeeperManager.getInstance().lock();
   }

   public static final void main(String[] args) {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      Object obj = registry.get(-8494186572112499737L);
      if (obj == null) {
         PasswordKeeper.getInstance();
         registry.put(-8494186572112499737L, new Object());
         PasswordKeeperManager.getInstance();
         PasswordKeeperOptions.getOptions().enableSynchronization();
      } else {
         PasswordKeeperApp app = new PasswordKeeperApp();
         PasswordKeeperSplashScreen screen = new PasswordKeeperSplashScreen();
         app.pushScreen(screen);
         app.enterEventDispatcher();
      }
   }
}
