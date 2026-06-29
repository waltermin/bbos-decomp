package net.rim.device.internal.system;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.UiSettings;

public class LEDEngine implements SystemListener2, GlobalEventListener, LEDConstants, AudioInternalListener {
   private int _stateFlags;
   private int[] _polyPattern;
   private boolean _poweredOff;
   private boolean _isPolychromatic = isPolychromatic();
   private boolean _isMicEnabled;
   protected LEDEventProcessor _ledEventProcessor;
   private static long LED_ENGINE_GUID = 4242253954996401163L;
   private static LEDEngine _ledEngineInstance;

   public void setConfigurationInternal(int type, int onTime, int offTime, int brightness) {
      if (!this.isLEDAccessLocked()) {
         setConfigurationNative(type, onTime, offTime, brightness);
      }
   }

   public void setColorConfigurationInternal(int type, int onTime, int offTime, int color) {
      if (!isValidColor(color)) {
         throw new IllegalArgumentException();
      }

      if (!this.isLEDAccessLocked()) {
         setColorConfigurationNative(type, onTime, offTime, color);
      }
   }

   public void setStateInternal(int type, int state) {
      if (!this.isLEDAccessLocked()) {
         setStateNative(type, state);
      }
   }

   public void setColorPatternInternal(int type, int[] pattern, boolean repeat) {
      if (!this.isLEDAccessLocked()) {
         setColorPatternNative(type, pattern, repeat);
      }
   }

   void lock(boolean lock) {
      ControlledAccess.assertRRISignatures(true);
      lock0(lock);
   }

   public void updateGSMFlag(boolean status) {
      if (status) {
         this.setFlag(8);
      } else {
         this.clearFlag(8);
      }
   }

   public void clearFlag(int flag) {
      synchronized (this._ledEventProcessor.getLockObject()) {
         int currentFlags = this._stateFlags;
         this._stateFlags &= ~flag;
         if (this._stateFlags != currentFlags) {
            this.setPattern(this._stateFlags);
         }
      }
   }

   public void setFlag(int flag) {
      synchronized (this._ledEventProcessor.getLockObject()) {
         int currentFlags = this._stateFlags;
         this._stateFlags |= flag;
         if (this._stateFlags != currentFlags) {
            this.setPattern(this._stateFlags);
         }
      }
   }

   void notifyLEDThread() {
      this._ledEventProcessor.notifyLEDThread();
   }

   public boolean contains(long sourceIdLong) {
      return this._ledEventProcessor.contains(sourceIdLong);
   }

   public void removeEvents(long sourceIdLong, int groupInfo) {
      this._ledEventProcessor.removeEvents(sourceIdLong, groupInfo);
   }

   public boolean isLEDAccessLocked() {
      synchronized (this._ledEventProcessor.getLockObject()) {
         return this.isLEDAccessLocked(this._isMicEnabled);
      }
   }

   public boolean isLEDAccessLocked(boolean isMicEnabled) {
      boolean forceFlashingLEDonEnabledMIC = ITPolicy.getBoolean(24, 54, false);
      return isMicEnabled && forceFlashingLEDonEnabledMIC;
   }

   public void addEvent(long sourceIdLong, boolean holsteredBoolean, int groupInfo) {
      this._ledEventProcessor.addEvent(sourceIdLong, holsteredBoolean, groupInfo);
   }

   @Override
   public void micStatusChange(boolean micEnabled) {
      synchronized (this._ledEventProcessor.getLockObject()) {
         boolean lockDownLED = this.isLEDAccessLocked(micEnabled);
         boolean wasLEDAccessLocked = this.isLEDAccessLocked();
         if (lockDownLED) {
            this.setConfigurationInternal(0, 100, 150, 3);
            this.setStateInternal(0, 2);
         }

         this._isMicEnabled = micEnabled;
         this.lock(lockDownLED);
         if (!lockDownLED) {
            if (!this._isPolychromatic && wasLEDAccessLocked) {
               this.setStateInternal(0, 0);
            }

            this.setPattern(this._stateFlags);
            this.notifyLEDThread();
         }
      }
   }

   @Override
   public void dtmfDataBufferFull() {
   }

   @Override
   public void dtmfDataAvailable() {
   }

   @Override
   public void responseAVCModeChange(boolean success, int mode) {
   }

   @Override
   public void recordStreamFail(int handle) {
   }

   @Override
   public void recordStreamDone(int handle, int headerLength) {
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6270993390899536868L) {
         this.updateGSMFlag(UiSettings.getLEDCoverageIndicatorStatus());
      }
   }

   @Override
   public void powerOffRequested(int reason) {
   }

   @Override
   public void cradleMismatch(boolean mismatch) {
   }

   @Override
   public void fastReset() {
   }

   @Override
   public void backlightStateChange(boolean on) {
   }

   @Override
   public void usbConnectionStateChange(int state) {
   }

   @Override
   public void batteryLow() {
   }

   @Override
   public void batteryGood() {
   }

   @Override
   public void powerOff() {
      this._poweredOff = true;
   }

   @Override
   public void powerUp() {
      this._poweredOff = false;
      this.batteryStatusChange(DeviceInfo.getBatteryStatus());
      this.setPattern(this._stateFlags);
   }

   @Override
   public void batteryStatusChange(int status) {
      if ((268435456 & status) != 268435456 && (-2147483648 & status) != Integer.MIN_VALUE && (16384 & status) != 16384) {
         this.setFlag(4);
      } else {
         this.clearFlag(4);
      }
   }

   private static native void lock0(boolean var0);

   private void setPattern(int flags) {
      if (!this._poweredOff) {
         if (this._isPolychromatic) {
            synchronized (this._ledEventProcessor.getLockObject()) {
               int patternColours = LEDUtilities.stateToColours(flags);
               int numFlags = LEDUtilities.countSetBits(patternColours);
               if (numFlags == 0) {
                  this._polyPattern = null;
                  this.setStateInternal(0, 0);
               } else {
                  int offTime = (flags & 64) == 0 ? 2850 : 150;
                  this._polyPattern = new int[numFlags * 6];
                  int i = 0;

                  for (int j = 0; j < 32; j++) {
                     if ((patternColours >> j & 1) == 1) {
                        int offset = i * 6;
                        this._polyPattern[offset + 0] = LEDUtilities.getFlagColour(j);
                        this._polyPattern[offset + 1] = 150;
                        this._polyPattern[offset + 2] = 0;
                        this._polyPattern[offset + 3] = 0;
                        this._polyPattern[offset + 4] = offTime;
                        this._polyPattern[offset + 5] = 0;
                        i++;
                     }
                  }

                  this._ledEventProcessor.getLockObject().notify();
                  if (this._polyPattern != null && !this._poweredOff) {
                     this.setColorPatternInternal(0, this._polyPattern, true);
                  }
               }
            }
         }
      }
   }

   public static boolean isSupported(int type) {
      if (type != 0 && type != 1) {
         throw new IllegalArgumentException("invalid type");
      } else {
         return type == 0 ? true : InternalServices.isDeviceCapable(23);
      }
   }

   private static native void setStateNative(int var0, int var1);

   public static boolean isPolychromatic(int type) {
      if (type != 0 && type != 1) {
         throw new IllegalArgumentException("invalid type");
      } else {
         return InternalServices.isDeviceCapable(type == 1 ? 23 : 11);
      }
   }

   private static native void setColorPatternNative(int var0, int[] var1, boolean var2);

   public static boolean isPolychromatic() {
      return isPolychromatic(0);
   }

   private static native void setColorConfigurationNative(int var0, int var1, int var2, int var3);

   private static boolean isValidColor(int color) {
      return color >= 0 && color <= 16777215;
   }

   public static LEDEngine getInstance() {
      if (_ledEngineInstance == null) {
         ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
         _ledEngineInstance = (LEDEngine)registry.getOrWaitFor(LED_ENGINE_GUID);
         if (_ledEngineInstance == null) {
            _ledEngineInstance = new LEDEngine();
            registry.put(LED_ENGINE_GUID, _ledEngineInstance);
         }
      }

      return _ledEngineInstance;
   }

   private static native void setConfigurationNative(int var0, int var1, int var2, int var3);

   public LEDEngine() {
      this._poweredOff = false;
      int initialBatteryStatus = 4;
      if ((DeviceInfo.getBatteryStatus() & 268435456) != 0) {
         initialBatteryStatus = 0;
      }

      this._stateFlags = 16 | initialBatteryStatus;
      if (UiSettings.getLEDCoverageIndicatorStatus()) {
         this._stateFlags |= 8;
      }

      Proxy proxy = Proxy.getInstance();
      this._ledEventProcessor = new LEDEventProcessor(this);
      proxy.startThread(this._ledEventProcessor);
      if (isPolychromatic() && (Display.getProperties() & 16384) != 0) {
         proxy.addSystemListener(this);
         proxy.addGlobalEventListener(this);
      }

      Audio.addListener(proxy, this);
      EventLogger.register(6390170866224596725L, "LED", 2);
   }
}
