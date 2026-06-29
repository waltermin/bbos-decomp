package net.rim.plazmic.internal.contentpreview.device.apps;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.plazmic.app.themereader.Main;
import net.rim.plazmic.internal.contentpreview.device.dispatcherclient.DispatcherClient;
import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherServiceException;
import net.rim.plazmic.internal.contentpreview.dispatcher.NoSuchSessionException;

public final class BootstrapApplication extends Application implements HolsterListener {
   public static final String rcsid;

   public BootstrapApplication() {
      this.addHolsterListener(this);
      this.registerPMEBrowserFieldFactory();
      this.sendSessionReady();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendSessionReady() {
      boolean var6 = false /* VF: Semaphore variable */;

      DispatcherServiceException e;
      try {
         var6 = true;
         int var10 = DeviceInfo.getDeviceId();
         DispatcherClient dispatcher = new DispatcherClient();
         dispatcher.sessionReady(var10);
         dispatcher.close();
         return;
      } catch (DispatcherServiceException var7) {
         e = var7;
         var6 = false;
      } catch (NoSuchSessionException var8) {
         return;
      } finally {
         if (var6) {
            return;
         }
      }

      System.err.println(((StringBuffer)(new Object("Could not send SessionReady signal: "))).append(e.toString()).toString());
   }

   private final void registerPMEBrowserFieldFactory() {
      ApplicationRegistry.getApplicationRegistry().put(-4027123499468556973L, new ContentPreviewPMEBrowserFieldFactory());
   }

   public static final void main(String[] args) {
      if (DeviceInfo.isSimulator()) {
         new BootstrapApplication().enterEventDispatcher();
      }
   }

   @Override
   public final void inHolster() {
   }

   @Override
   public final void outOfHolster() {
      try {
         int pin = DeviceInfo.getDeviceId();
         DispatcherClient dispatcher = new DispatcherClient();
         String themeToBeRegistered = dispatcher.dequeueThemeRegistrationRequest(pin);
         if (themeToBeRegistered != null && themeToBeRegistered.length() > 0) {
            Main.registerTheme(themeToBeRegistered);
         }

         String themeToBeActivated = dispatcher.dequeueThemeActivationRequest(pin);
         if (themeToBeActivated != null && themeToBeActivated.length() > 0) {
            ThemeManager.setActiveTheme(themeToBeActivated);
         }

         dispatcher.close();
      } catch (DispatcherServiceException var7) {
      } finally {
         return;
      }
   }
}
