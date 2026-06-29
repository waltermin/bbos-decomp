package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.PhoneCallListener;
import net.rim.device.api.system.PhoneTimerListener;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ribbon.system.StandbyManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.profiles.AlertEngine;
import net.rim.device.apps.internal.profiles.Profile;
import net.rim.device.apps.internal.profiles.Profiles;
import net.rim.device.internal.deviceoptions.AutoOnOff;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.DebugSupport;
import net.rim.vm.PersistentInteger;

final class SystemOnOffManager
   implements Runnable,
   RadioStatusListener,
   PhoneCallListener,
   PhoneTimerListener,
   SystemListener2,
   GlobalEventListener,
   RealtimeClockListener {
   private SplashScreen _splashScreen;
   private boolean _isFastReset;
   private ApplicationDescriptor _onDescriptor;
   private ApplicationDescriptor _offDescriptor;
   private UiApplication _app;
   private ApplicationManager _appManager;
   private int _lastBatteryLevel = -1;
   private int _lastBatteryStatus;
   private boolean _radioOffAtLowPower;
   private boolean _soundLowBatteryWarning;
   private long _previousLowBatteryWarningTime;
   private int _radioStateId;
   private int _radioIsOn;
   private int _cellRadios = RadioInternal.getSupportedRadios() & -5;
   private boolean _powerIsOn = true;
   private boolean _isPoweringDown = false;
   private boolean _simulatorInStartup = DeviceInfo.isSimulator();
   private Dialog _cradleMismatchDialog;
   private long _lowChargingWarningTime;
   private boolean _firstBootFlag;
   private boolean _isAuto;
   private boolean _autoOffWhenIdle;
   static String AUTO_ON = "AutoOn";
   static String AUTO_OFF = "AutoOff";
   private static final int BAT_TOO_LOW_PROMPT_TIME;
   private static final int BAT_TOO_LOW_WARNING_INTERVAL;
   private static SystemOnOffManager _cachedManager;
   private static final long GUID;
   private static final long RADIO_STATE;
   private static final long LOW_CHARGING_WARNING_INTERVAL;
   private static Dialog _usbLowChargingDialog;
   private static final short[] BATTERY_LOW_TUNE = new short[]{2000, 500, 256, 28488};
   private static final int REMINDER_IDLE_SECONDS;

   static final void init() {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      synchronized (reg) {
         _cachedManager = new SystemOnOffManager();
         reg.put(-2419897739206856258L, _cachedManager);
      }

      _cachedManager.instanceInit();
   }

   private SystemOnOffManager() {
      boolean deviceHasBootedBefore = NvStore.getFlag(32);
      if (!deviceHasBootedBefore) {
         this._firstBootFlag = true;
         NvStore.setFlag(32, true);
      }

      int defaultRadioStateIfMemoryIsWiped = this._cellRadios;
      if (this.isWLANEnabledOnStartup()) {
         defaultRadioStateIfMemoryIsWiped |= 4;
      }

      if (deviceHasBootedBefore || RadioInfo.getNetworkType() == 4) {
         defaultRadioStateIfMemoryIsWiped = 0;
      }

      this._radioStateId = PersistentInteger.getId(734913344992711259L, defaultRadioStateIfMemoryIsWiped);
   }

   private final boolean isWLANEnabledOnStartup() {
      if (WLAN.isSupported()) {
         try {
            byte[] brandingFlag = Branding.getData(28720);
            return brandingFlag == null || brandingFlag.length != 0 && brandingFlag[0] == 0;
         } finally {
            System.out.println("*** WLAN setting read failed");
            return false;
         }
      } else {
         return false;
      }
   }

   final boolean isFirstBoot() {
      return this._firstBootFlag;
   }

   final void setIsPoweringOff(boolean isPoweringOff) {
      this._isPoweringDown = isPoweringOff;
   }

   final boolean getIsPoweringOff() {
      return this._isPoweringDown;
   }

   static final void requestShutdown(boolean isAuto) {
      SystemOnOffManager systemOnOffManager = getInstance();
      if (systemOnOffManager != null) {
         systemOnOffManager.instanceRequestShutdown(isAuto);
      }
   }

   static final void requestPowerOn() {
      SystemOnOffManager systemOnOffManager = getInstance();
      if (systemOnOffManager != null) {
         systemOnOffManager._autoOffWhenIdle = false;
         systemOnOffManager.refreshTimers();
      }
   }

   static final boolean showSplashScreen(Runnable runWhenDone, boolean carrierOnly, boolean setTimer, boolean dismissScreen) {
      SystemOnOffManager systemOnOffManager = getInstance();
      return systemOnOffManager != null ? systemOnOffManager.queueSplashScreen(runWhenDone, carrierOnly, setTimer, dismissScreen) : false;
   }

   static final SystemOnOffManager getInstance() {
      if (_cachedManager != null) {
         return _cachedManager;
      }

      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      synchronized (reg) {
         _cachedManager = (SystemOnOffManager)reg.get(-2419897739206856258L);
         if (_cachedManager == null) {
            throw new Object("SystemOnOffManager not initialized");
         }
      }

      return _cachedManager;
   }

   private final void instanceInit() {
      ApplicationDescriptor original = ApplicationDescriptor.currentApplicationDescriptor();
      this._onDescriptor = (ApplicationDescriptor)(new Object(original, new Object[]{AUTO_ON}));
      this._onDescriptor.setPowerOnBehavior(2);
      this._offDescriptor = (ApplicationDescriptor)(new Object(original, new Object[]{AUTO_OFF}));
      this._offDescriptor.setPowerOnBehavior(0);
      this._appManager = ApplicationManager.getApplicationManager();
      this._app = UiApplication.getUiApplication();
      this._app.addRadioListener(this);
      if (WLAN.isSupported()) {
         this._app.addRadioListener(4, new SystemOnOffManager$WlanRadioStatusListener(this));
      }

      this._app.addSystemListener(this);
      Proxy.getInstance().addGlobalEventListener(this);
      this._splashScreen = new SplashScreen();
      Runnable selfTests = null;
      if (!DeviceInfo.isSimulator()) {
         selfTests = new SystemOnOffManager$1(this);
      }

      this.queueSplashScreen(selfTests, false, false, true);
   }

   private final void refreshTimers() {
      this._appManager.scheduleApplication(this._onDescriptor, AutoOnOff.getNextAutoOnTime(), true);
      this._appManager.scheduleApplication(this._offDescriptor, AutoOnOff.getNextAutoOffTime(this._powerIsOn), true);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -2918606221006897090L || guid == 8877632280522743328L || guid == 3596208183088439728L) {
         this.refreshTimers();
      }
   }

   private final void instanceRequestShutdown(boolean isAuto) {
      this._isAuto = isAuto;
      this._app.invokeLater(this);
   }

   @Override
   public final void run() {
      if (this._powerIsOn && !this._isPoweringDown && (!this._isAuto || !this.shouldAutoOffBeAborted())) {
         this.powerOffDevice(this._isAuto);
      }
   }

   private final void powerOffDevice(boolean auto) {
      new AutoOffDialog(this._app, auto ? 1 : 2).show();
   }

   private final boolean shouldAutoOffBeAborted() {
      boolean abort = false;
      boolean idleOff = false;
      abort = deferAutoOff();
      if (!abort) {
         int behavior = AutoOnOff.determineAutoOffBehavior();
         if (behavior == 2) {
            abort = true;
            if (this._autoOffWhenIdle) {
               this.removeRTCListener();
               this._autoOffWhenIdle = false;
            }
         } else if (behavior == 1) {
            if (!this._autoOffWhenIdle) {
               idleOff = true;
               abort = true;
            } else {
               this._autoOffWhenIdle = false;
            }
         }
      } else {
         idleOff = true;
      }

      if (!this._autoOffWhenIdle && idleOff) {
         this._autoOffWhenIdle = true;
         this.addRTCListener();
      }

      return abort;
   }

   private final boolean deviceIsIdle() {
      return DeviceInfo.getIdleTime() >= 120 && !deferAutoOff();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void launchSystemOnOffManager() {
      boolean var6 = false /* VF: Semaphore variable */;

      label38: {
         try {
            label36:
            try {
               var6 = true;
               ApplicationManager.getApplicationManager()
                  .runApplication((ApplicationDescriptor)(new Object(ApplicationDescriptor.currentApplicationDescriptor(), new Object[]{AUTO_OFF})));
               var6 = false;
               break label38;
            } catch (Throwable var9) {
               String err = ((StringBuffer)(new Object("SILF: "))).append(e.getMessage()).toString();
               EventLogger.logEvent(-7509200465648525729L, err.getBytes(), 3);
               var6 = false;
               break label36;
            }
         } finally {
            if (var6) {
               this.removeRTCListener();
            }
         }

         this.removeRTCListener();
         return;
      }

      this.removeRTCListener();
   }

   private final void removeRTCListener() {
      Application.getApplication().removeRealtimeClockListener(this);
   }

   private final void addRTCListener() {
      Application.getApplication().addRealtimeClockListener(this);
   }

   @Override
   public final void clockUpdated() {
      if (this.deviceIsIdle()) {
         this.launchSystemOnOffManager();
      }
   }

   private static final boolean phoneCallInProgress() {
      return !Phone.isSupported() ? false : Phone.getInstance().isActive();
   }

   static final boolean deferAutoOff() {
      if (phoneCallInProgress()) {
         return true;
      }

      Object deferAutoOff = ApplicationRegistry.getApplicationRegistry().get(2108775066620843828L);
      return deferAutoOff instanceof Object && deferAutoOff;
   }

   private final void saveRadioState() {
      System.out.println(((StringBuffer)(new Object("*** saveRadioState("))).append(this._radioIsOn).append(")").toString());
      if (this._radioIsOn != 0) {
         PersistentInteger.set(this._radioStateId, this._radioIsOn);
      } else {
         int batteryStatus = DeviceInfo.getBatteryStatus();
         if ((batteryStatus & -2134851584) == 0) {
            PersistentInteger.set(this._radioStateId, 0);
         }
      }
   }

   private final void restoreRadioState() {
      int radioState = PersistentInteger.get(this._radioStateId);
      if (radioState != 0) {
         int flagsToCheck = 8437760;
         if ((DeviceInfo.getBatteryStatus() & flagsToCheck) == 0) {
            if (RadioInternal.getSupportedRadios() == 3) {
               radioState = RadioInternal.getEnabledRadios();
            }

            if ((RadioInternal.getSupportedRadios() & 1) != 0 && (radioState & 1) != 0 && RadioInternal.get3GPPActiveRats() == 4) {
               radioState &= -2;
            }

            RadioInternal.activateRadios(radioState);
         }
      }
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
   public final void networkStarted(int networkId, int service) {
      int activeCellRadios = RadioInternal.getActiveRadios() & this._cellRadios;
      if (activeCellRadios != 0 && (this._radioIsOn & this._cellRadios) == 0) {
         this._radioIsOn |= activeCellRadios;
         this.saveRadioState();
      }
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void radioTurnedOff() {
      System.out.println("*** Cellular radioTurnedOff()");
      if (!this._isPoweringDown) {
         this._radioIsOn = this._radioIsOn & ~this._cellRadios;
         this.saveRadioState();
      }
   }

   @Override
   public final void callIncoming(int callId) {
   }

   @Override
   public final void callDisplayUpdated(int callId) {
   }

   @Override
   public final void callWaiting(int callId) {
   }

   @Override
   public final void callInitiated(int callId) {
   }

   @Override
   public final void callConnected(int callId) {
      if (!this._soundLowBatteryWarning && (DeviceInfo.getBatteryStatus() & 268435456) != 0) {
         this._soundLowBatteryWarning = true;
         this._previousLowBatteryWarningTime = System.currentTimeMillis() - 105000;
      }
   }

   @Override
   public final void callFailed(int callId, int error) {
      if (!phoneCallInProgress()) {
         this._soundLowBatteryWarning = false;
      }
   }

   @Override
   public final void callDelivered(int callId) {
   }

   @Override
   public final void callManipulateFailed(int callId, int error) {
   }

   @Override
   public final void callDisconnected(int callId) {
      if (!phoneCallInProgress()) {
         this._soundLowBatteryWarning = false;
      }
   }

   @Override
   public final void callHeld(int callId) {
   }

   @Override
   public final void callResumed(int callId) {
   }

   @Override
   public final void callAdded(int callId) {
   }

   @Override
   public final void callRemoved(int callId) {
   }

   @Override
   public final void callTransferred(int status, int reason) {
   }

   @Override
   public final void callTransferStateUpdated(int callId, int state) {
   }

   @Override
   public final void callTimerUpdated(int callId, int time) {
      if (this._soundLowBatteryWarning) {
         long curTime = System.currentTimeMillis();
         if (curTime - this._previousLowBatteryWarningTime > 120000) {
            this._previousLowBatteryWarningTime = curTime;
            this.audioNotifyBatteryLow();
         }
      }
   }

   @Override
   public final void callVoicePrivacyUpdated(int callId, boolean on) {
   }

   @Override
   public final void callOTAStatusUpdated(int callId, int status) {
   }

   @Override
   public final void dtmfData(int dtmf) {
   }

   @Override
   public final void powerOff() {
      RibbonLauncher rl = RibbonLauncher.getInstance();
      rl.showRootFolder(true);
      this._splashScreen.dismissScreen();
      this._powerIsOn = false;
      this._isPoweringDown = false;
      if (this._autoOffWhenIdle) {
         this.removeRTCListener();
         this._autoOffWhenIdle = false;
      }

      this.refreshTimers();
      showSplashScreen(null, false, false, false);
   }

   @Override
   public final void powerUp() {
      EventLogger.logEvent(-7509200465648525729L, "OnOffMgr: powerUp".getBytes(), 0);
      if (!this._simulatorInStartup || DebugSupport.getenv("JvmRadioOff") == null) {
         this.restoreRadioState();
      }

      if (!this._isFastReset) {
         if (!this._splashScreen.isRunning()) {
            this.queueSplashScreen(null, false, false, true);
         }

         this._splashScreen.setTimer();
      } else {
         this._splashScreen.dismissScreen();
         this._isFastReset = false;
      }

      this._lastBatteryLevel = -1;
      this._powerIsOn = true;
      this._isPoweringDown = false;
      this._autoOffWhenIdle = false;
      this._simulatorInStartup = false;
      this.refreshTimers();
   }

   private final void audioNotifyBatteryLow() {
      int alertVolume = 25;
      if (!phoneCallInProgress()) {
         Profile profile = Profiles.getInstance().getEnabled();
         switch (profile.getIdentifier()) {
            case 0:
               alertVolume = 50;
               break;
            case 1:
            default:
               AlertEngine alertEngine = AlertEngine.getInstance();
               alertEngine.startNewAlert(2, null, 0, 2, 0, 0, -1845850106795451018L);
               return;
            case 2:
               return;
         }
      }

      if (Alert.isAudioSupported()) {
         Alert.startAudio(BATTERY_LOW_TUNE, alertVolume);
      }

      if (Alert.isBuzzerSupported()) {
         Alert.startBuzzer(BATTERY_LOW_TUNE, alertVolume);
      }
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryStatusChange(int status) {
      int level = DeviceInfo.getBatteryLevel();
      if (level != this._lastBatteryLevel || status != this._lastBatteryStatus) {
         int networkType = RadioInfo.getNetworkType();
         if ((status & 1) == 0 || networkType == 4 || networkType == 5) {
            ResourceBundle resources = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
            if ((status & -2147483648) != 0) {
               this.audioNotifyBatteryLow();
               new AutoOffDialog(this._app, 0).show();
            } else if ((status & 16384) != 0 && RadioInternal.getActiveRadios() != 0) {
               if (!PhoneUtilities.isEmergencyCall()) {
                  this._radioOffAtLowPower = true;
                  RadioOffWarningManagerImpl.requestRadioOff();
                  this.audioNotifyBatteryLow();
                  showNoRadioMessage();
               }
            } else if ((status & 268435456) != 0 && phoneCallInProgress() && !this._soundLowBatteryWarning) {
               this._soundLowBatteryWarning = true;
               this._previousLowBatteryWarningTime = System.currentTimeMillis();
            }
         } else if (this._radioOffAtLowPower && networkType != 4 && networkType != 5 && (status & 16384) == 0) {
            this._radioOffAtLowPower = !Radio.powerOn();
         }

         if (this._lastBatteryStatus != status) {
            if ((status & 1) == 0 && (this._lastBatteryStatus & 268435456) == 0 && (status & 268435456) != 0) {
               this.audioNotifyBatteryLow();
            } else if ((status & 268435456) == 0 && (this._lastBatteryStatus & 268435456) != 0) {
               this._soundLowBatteryWarning = false;
            }
         }

         if ((status & 16) != (this._lastBatteryStatus & 16)) {
            Backlight.enable(true);
         }

         if ((status & 8) == 0) {
            if (_usbLowChargingDialog != null) {
               _usbLowChargingDialog.close();
               _usbLowChargingDialog = null;
               this._lowChargingWarningTime = 0;
            }
         } else {
            long currentTime = System.currentTimeMillis();
            long delta = currentTime - this._lowChargingWarningTime;
            if (delta < 0 || delta > 1800000) {
               this._lowChargingWarningTime = currentTime;
               if (_usbLowChargingDialog == null) {
                  ResourceBundle resources = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
                  String message = resources.getString(70);
                  _usbLowChargingDialog = (Dialog)(new Object(message, null, null, 0, Bitmap.getPredefinedBitmap(2), 33554432));
                  _usbLowChargingDialog.setDialogClosedListener(new SystemOnOffManager$USBLowChargingDialogClosedListener());
                  _usbLowChargingDialog.show(-2147483647);
               }
            }
         }

         this._lastBatteryStatus = status;
         this._lastBatteryLevel = level;
      }
   }

   static final void showNoRadioMessage() {
      Status.show(CommonResources.getString(9099), Bitmap.getPredefinedBitmap(2), 30000, 33554432, true, false, -2147483647);
   }

   private final boolean queueSplashScreen(Runnable runWhenDone, boolean carrierOnly, boolean setTimer, boolean dismissScreen) {
      boolean result = false;
      synchronized (this._splashScreen) {
         this._splashScreen.dismissScreen();
         result = this._splashScreen.queue(carrierOnly, runWhenDone, dismissScreen);
         if (setTimer) {
            this._splashScreen.setTimer();
         }

         return result;
      }
   }

   private final boolean isTrackballStandby() {
      return StandbyManager.getInstance().isInStandby();
   }

   @Override
   public final void powerOffRequested(int reason) {
      if (reason == 1) {
         if (!this.isTrackballStandby()) {
            Backlight.enable(true);
            new AutoOffDialog(this._app, 3).show();
         }
      }
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
      if (mismatch) {
         if (this._cradleMismatchDialog == null) {
            ResourceBundle resources = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
            String message = resources.getString(91);
            this._cradleMismatchDialog = (Dialog)(new Object(message, null, null, 0, Bitmap.getPredefinedBitmap(2), 33554432));
            this._cradleMismatchDialog.setDialogClosedListener(new SystemOnOffManager$CradleMismatchDialogClosedListener(this));
            this._cradleMismatchDialog.show(-2147483647);
            return;
         }
      } else if (this._cradleMismatchDialog != null) {
         this._cradleMismatchDialog.close();
         this._cradleMismatchDialog = null;
      }
   }

   @Override
   public final void fastReset() {
      this._isFastReset = true;
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void usbConnectionStateChange(int state) {
      if (this._powerIsOn) {
         switch (state) {
            case 1:
            case 4:
               Backlight.enable(true);
         }
      }
   }

   static final int access$076(SystemOnOffManager x0, int x1) {
      return x0._radioIsOn |= x1;
   }

   static final int access$072(SystemOnOffManager x0, int x1) {
      return x0._radioIsOn &= x1;
   }

   static final Dialog access$302(Dialog x0) {
      _usbLowChargingDialog = x0;
      return x0;
   }
}
