package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.api.quincy.QuincyManager;

public final class SystemActions implements RadioStatusListener {
   private RadioToggleAction _radioToggle;
   private Application _app;
   private SystemActions$RadioOnThreadSwitch _radioOnSwitcher = new SystemActions$RadioOnThreadSwitch(this);
   private SystemActions$RadioOffThreadSwitch _radioOffSwitcher = new SystemActions$RadioOffThreadSwitch(this);
   static String RADIO_ON_PARAM_STRING = "radioon";
   static String RADIO_OFF_PARAM_STRING = "radiooff";
   static String POWER_OFF_PARAM_STRING = "poweroff";
   static String LOCK_PARAM_STRING = "lock";
   private static long GUID = 1155696765752266012L;

   public static final void init() {
      SystemActions sysActions = new SystemActions();
      RibbonLauncher rl = RibbonLauncher.getInstance();
      sysActions._app = Application.getApplication();
      ApplicationDescriptor original = ApplicationDescriptor.currentApplicationDescriptor();
      ApplicationDescriptor radioOn = new ApplicationDescriptor(original, new String[]{RADIO_ON_PARAM_STRING});
      ApplicationDescriptor radioOff = new ApplicationDescriptor(original, new String[]{RADIO_OFF_PARAM_STRING});
      sysActions._radioToggle = new RadioToggleAction(radioOn, radioOff, userCanTurnRadioOn());
      RadioToggleAction radioToggle = sysActions._radioToggle;
      PowerOffAction powerOff = new PowerOffAction(new ApplicationDescriptor(original, new String[]{POWER_OFF_PARAM_STRING}));
      LockAction lock = new LockAction(new ApplicationDescriptor(original, new String[]{LOCK_PARAM_STRING}));
      if (CodeModuleManager.getModuleHandle("net_rim_bb_manage_connections") == 0) {
         rl.registerAction(radioToggle.get(1, (String)null), radioToggle);
      }

      rl.registerAction(powerOff.get(1, (String)null), powerOff);
      rl.registerAction(lock.get(1, (String)null), lock);
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      synchronized (appReg) {
         appReg.put(GUID, sysActions);
      }

      sysActions._app.addRadioListener(sysActions);
   }

   public static final void invoke(String[] args) {
      if (args != null && args.length == 1) {
         if (args[0].equals(RADIO_ON_PARAM_STRING)) {
            radioOn();
         } else if (args[0].equals(RADIO_OFF_PARAM_STRING)) {
            radioOff();
         } else if (args[0].equals(POWER_OFF_PARAM_STRING)) {
            powerOff(false);
         } else if (args[0].equals(LOCK_PARAM_STRING)) {
            lock();
         } else if (args[0].equals(SystemOnOffManager.AUTO_OFF)) {
            powerOff(true);
         } else {
            if (args[0].equals(SystemOnOffManager.AUTO_ON)) {
               SystemOnOffManager.requestPowerOn();
            }
         }
      }
   }

   private SystemActions() {
   }

   private static final SystemActions getInstance() {
      SystemActions sa = null;
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      synchronized (appReg) {
         return (SystemActions)appReg.get(GUID);
      }
   }

   public static final boolean isFirstBoot() {
      return SystemOnOffManager.getInstance().isFirstBoot();
   }

   private static final RadioToggleAction getRadioToggle() {
      RadioToggleAction radioToggleAction = null;
      SystemActions sa = getInstance();
      if (sa != null) {
         radioToggleAction = sa._radioToggle;
      }

      return radioToggleAction;
   }

   private static final void showUserCanTurnRadioOn(boolean userCanTurnRadioOn) {
      RadioToggleAction radioToggle = getRadioToggle();
      if (radioToggle != null && radioToggle.updateActions(userCanTurnRadioOn)) {
         RibbonLauncher rl = RibbonLauncher.getInstance();
         rl.updateRegisteredAction(radioToggle.get(1, (String)null));
      }
   }

   private static final boolean userCanTurnRadioOn() {
      switch (RadioInfo.getState()) {
         case 1:
         case 5:
            return false;
         default:
            return true;
      }
   }

   private final void radioOnMainThread() {
      int batteryStatus = DeviceInfo.getBatteryStatus();
      if ((batteryStatus & 16384) != 0 || (batteryStatus & 32768) != 0) {
         stateChangeError(CommonResources.getString(9099));
      } else if ((batteryStatus & 8388608) != 0) {
         stateChangeError(CommonResources.getString(9100));
      } else {
         int state = RadioInfo.getState();
         if (state == 1) {
            showUserCanTurnRadioOn(false);
         } else {
            if (state == 0) {
               if (Radio.powerOn()) {
                  showUserCanTurnRadioOn(false);
                  return;
               }
            } else {
               reportState(state);
            }
         }
      }
   }

   static final void radioOn() {
      SystemActions sa = getInstance();
      if (!sa._radioOnSwitcher._updatePending) {
         sa._radioOnSwitcher._updatePending = true;
         sa._app.invokeLater(sa._radioOnSwitcher);
      }
   }

   private final void radioOffMainThread() {
      int state = RadioInfo.getState();
      if (state == 0) {
         System.out.println("*** radioThread: state=off");
         showUserCanTurnRadioOn(true);
      } else if (state == 1) {
         System.out.println("*** radioThread: state=on");
         RadioOffWarningManagerImpl.requestRadioOff();
         showUserCanTurnRadioOn(true);
      } else {
         System.out.println("*** radioThread: state=" + state);
         reportState(state);
      }

      QuincyManager.sendRadioLogworthy("Radio Off", "Source: ribbon");
   }

   static final void radioOff() {
      SystemActions sa = getInstance();
      if (!sa._radioOffSwitcher._updatePending) {
         sa._radioOffSwitcher._updatePending = true;
         sa._app.invokeLater(sa._radioOffSwitcher);
      }
   }

   static final void powerOff(boolean isAutoOff) {
      SystemOnOffManager.requestShutdown(isAutoOff);
   }

   public static final void lock() {
      ApplicationRegistry.getApplicationRegistry().replace(386954390916129487L, Boolean.TRUE);
      ApplicationManager.getApplicationManager().lockSystem(true);
   }

   private static final void reportState(int radioState) {
      switch (radioState) {
         case 2:
            stateChangeError(CommonResources.getString(9099));
         case 1:
         case 3:
         case 7:
            return;
         case 4:
            stateChangeError(47);
            return;
         case 5:
         default:
            stateChangeError(32);
            return;
         case 6:
            stateChangeError(46);
            return;
         case 8:
            stateChangeError(94);
      }
   }

   private static final void stateChangeError(String str) {
      Status.show(str, Bitmap.getPredefinedBitmap(2), 2000, 33554432, true, false, 50);
   }

   private static final void stateChangeError(int resourceId) {
      ResourceBundleFamily resources = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
      stateChangeError(resources.getString(resourceId));
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void signalLevel(int level) {
      showUserCanTurnRadioOn(userCanTurnRadioOn());
   }

   @Override
   public final void radioTurnedOff() {
      showUserCanTurnRadioOn(userCanTurnRadioOn());
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      showUserCanTurnRadioOn(userCanTurnRadioOn());
   }
}
