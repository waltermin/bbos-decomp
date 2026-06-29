package net.rim.device.apps.internal.setupwizard;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.internal.proxy.Proxy;

final class ScheduledAppLauncher implements SystemListener, RadioStatusListener {
   private ApplicationDescriptor _applicationDescriptor;
   private boolean _useCdmaRadioListener;
   static final long REMINDER_KEY;

   public static final void register(ApplicationDescriptor application, boolean useCdmaRadioListener) {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      synchronized (appRegistry) {
         ScheduledAppLauncher scheduledAppLauncher = (ScheduledAppLauncher)appRegistry.get(-3358579878181152826L);
         if (scheduledAppLauncher == null) {
            scheduledAppLauncher = new ScheduledAppLauncher(application, useCdmaRadioListener);
            Proxy.getInstance().addSystemListener(scheduledAppLauncher);
            if (useCdmaRadioListener) {
               Proxy.getInstance().addRadioListener(scheduledAppLauncher);
            }

            appRegistry.put(-3358579878181152826L, scheduledAppLauncher);
         }
      }
   }

   public static final void unregister() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      synchronized (appRegistry) {
         ScheduledAppLauncher scheduledAppLauncher = (ScheduledAppLauncher)appRegistry.remove(-3358579878181152826L);
         if (scheduledAppLauncher != null) {
            Proxy.getInstance().removeSystemListener(scheduledAppLauncher);
            if (scheduledAppLauncher._useCdmaRadioListener) {
               Proxy.getInstance().removeRadioListener(scheduledAppLauncher);
            }
         }
      }
   }

   private ScheduledAppLauncher(ApplicationDescriptor applicationDescriptor, boolean useCdmaRadioListener) {
      this._applicationDescriptor = applicationDescriptor;
      this._useCdmaRadioListener = useCdmaRadioListener;
   }

   private final void launchApplication() {
      if (!SetupWizard.cdmaActivationRequired()) {
         unregister();

         try {
            ApplicationManager.getApplicationManager().runApplication(this._applicationDescriptor, false);
         } finally {
            return;
         }
      }
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
      this.launchApplication();
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.launchApplication();
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }
}
