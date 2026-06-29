package net.rim.device.apps.internal.applicationcontrol;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.ui.ApplicationControlScreen;
import net.rim.device.internal.ui.component.BackgroundDialog;

public final class ApplicationPermissionsProxy extends UiApplication {
   private static final long DESCRIPTOR_ID;
   private static final long SCREEN_ID;
   private static String _randomString = (String)(new Object(RandomSource.getBytes(32)));

   public static final void main(String[] args) {
      if (args.length == 0) {
         ApplicationDescriptor appDesc = ApplicationDescriptor.currentApplicationDescriptor();
         ApplicationDescriptor applicationPermissionProxyDescriptor = (ApplicationDescriptor)(new Object(appDesc, new Object[]{_randomString}));
         ApplicationRegistry.getApplicationRegistry()
            .put(3353949624016666498L, new Object(applicationPermissionProxyDescriptor, CodeSigningKey.getBuiltInKey(51)));
      } else {
         if (args.length == 1) {
            ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
            ApplicationControlScreen appControlScreen = (ApplicationControlScreen)appReg.get(5402085941883890214L);
            ApplicationPermissionsProxy proxyUi = new ApplicationPermissionsProxy();
            proxyUi.showScreen(appControlScreen);
         }
      }
   }

   private ApplicationPermissionsProxy() {
   }

   private final void showScreen(ApplicationControlScreen appControlScreen) {
      new ApplicationPermissionsProxy$EventThread(this, null).start();
      ApplicationPermissionsProxy$PermissionsRequestDialog permRequest = new ApplicationPermissionsProxy$PermissionsRequestDialog(appControlScreen.getTitle());
      BackgroundDialog.show(permRequest);
      synchronized (Application.getEventLock()) {
         this.requestForeground();
         this.pushScreen(appControlScreen);
      }
   }
}
