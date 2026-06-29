package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.internal.proxy.Proxy;

public class WizardIdleHelper implements RealtimeClockListener, PhoneEventListener {
   private ApplicationDescriptor _descriptor;
   private static final long GUID_WIZARD_SCHEDULED_RUN_FAILED = 4002776916212129330L;
   private static final int REMINDER_IDLE_SECONDS = 120;

   private WizardIdleHelper(ApplicationDescriptor descriptor) {
      this._descriptor = descriptor;
   }

   public static void startApplicationWhenIdle(ApplicationDescriptor descriptor) {
      Proxy.getInstance().addRealtimeClockListener(new WizardIdleHelper(descriptor));
   }

   public static void startApplication(ApplicationDescriptor descriptor) {
      WizardIdleHelper helper = new WizardIdleHelper(descriptor);
      if (Phone.getInstance().isActive()) {
         VoiceServices.addPhoneEventListener(helper);
      } else {
         helper.launchApp();
      }
   }

   private boolean deviceIsIdle() {
      return DeviceInfo.getIdleTime() >= 120 && (!Phone.isSupported() || !Phone.getInstance().isActive());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void launchApp() {
      try {
         ApplicationManager.getApplicationManager().runApplication(this._descriptor);
      } catch (Throwable var4) {
         String err = "SILF: " + e.getMessage();
         EventLogger.logEvent(4002776916212129330L, err.getBytes(), 3);
         return;
      }
   }

   @Override
   public void clockUpdated() {
      if (this.deviceIsIdle()) {
         this.launchApp();
         Application.getApplication().removeRealtimeClockListener(this);
      }
   }

   @Override
   public void phoneEventNotify(int eventId, int param1, Object param2) {
      if (!Phone.getInstance().isActive()) {
         this.launchApp();
         VoiceServices.removePhoneEventListener(this);
      }
   }
}
