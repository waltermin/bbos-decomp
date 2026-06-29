package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.system.StandbyManager;
import net.rim.device.internal.ui.UiSettings;
import net.rim.device.internal.vad.VADUserEventListener;
import net.rim.device.internal.vad.VADUserEvents;

public final class StandbyManagerImpl
   extends StandbyManager
   implements SystemListener2,
   GlobalEventListener,
   PhoneEventListener,
   KeyListener,
   VADUserEventListener {
   private UiApplication _app;
   private boolean _isEnteringStandby;
   private boolean _inStandby;
   private boolean _inStandbyForPhone;
   private boolean _isVADActive;
   private boolean _muteKeyRepeated = false;
   private boolean _interruptEnterStandby = false;
   private boolean _enableBacklightForNotify = false;
   private boolean _muteKeyPressed;
   private ResourceBundle _rb = ResourceBundle.getBundle(-6812884907508133143L, "net.rim.device.internal.resource.Common");
   private static final int TWO_SECONDS;
   private static final int FIVE_SECONDS;

   static final void init() {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      synchronized (reg) {
         StandbyManager._cachedManager = new StandbyManagerImpl();
         reg.put(-4691466650299662765L, StandbyManager._cachedManager);
      }

      ((StandbyManagerImpl)StandbyManager._cachedManager).instanceInit();
   }

   private StandbyManagerImpl() {
   }

   private final void instanceInit() {
      this._app = UiApplication.getUiApplication();
      this._app.addGlobalEventListener(this);
      this._app.addKeyListener(this);
      this._app.addSystemListener(this);
      VoiceServices.addPhoneEventListener(this);
      VADUserEvents.addListener(this._app, this);
   }

   @Override
   public final boolean isEnteringStandby() {
      return this._isEnteringStandby;
   }

   @Override
   public final boolean isInStandby() {
      return this._inStandby;
   }

   private final void exitStandby() {
      Keypad.enableStandbyMode(false);
      Backlight.enable(true);
      this._inStandby = false;
      this._isEnteringStandby = false;
      this._enableBacklightForNotify = false;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (!Phone.getInstance().isActive()) {
         if (Trackball.isSupported() && key == 273 && this.isInStandby()) {
            this.exitStandby();
            return false;
         }

         if (this.isInStandby() && (key == 17 || key == 18 || key == 4098 || key == 27) && !this._enableBacklightForNotify) {
            this._enableBacklightForNotify = true;
            Backlight.enable(true);
            StandbyManagerImpl$EnterStandbyDialog standbyNotifyDialog = new StandbyManagerImpl$EnterStandbyDialog(this, false);
            new StandbyManagerImpl$StandbyNotifyIdleMonitor(this, standbyNotifyDialog, 5000);
            UiApplication.getUiApplication().pushGlobalScreen(standbyNotifyDialog, -2147483647, 1);
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -5577605925563127340L) {
         this._inStandbyForPhone = false;
         if (this.isInStandby()) {
            this.exitStandby();
         }
      }
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object param2) {
      if (eventId == 1000) {
         if (this.isInStandby()) {
            this._inStandbyForPhone = true;
         }

         this._interruptEnterStandby = true;
         Backlight.setTimeout(UiSettings.getBacklightTimeout());
         if (this.isInStandby()) {
            Keypad.enableStandbyMode(false);
            Backlight.enable(true);
            return;
         }
      } else if (eventId == 1002) {
         if (this._inStandbyForPhone) {
            Backlight.enable(false);
            Keypad.enableStandbyMode(true);
            this._inStandby = true;
            this._inStandbyForPhone = false;
            return;
         }

         this._inStandby = false;
      }
   }

   @Override
   public final void vadEvent(int event, int context) {
      if (event == 0) {
         this._interruptEnterStandby = true;
         if (this.isInStandby()) {
            this.exitStandby();
         }

         this._isVADActive = true;
      } else {
         if (event == 3) {
            this._isVADActive = false;
         }
      }
   }

   @Override
   public final void fastReset() {
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }

   @Override
   public final void backlightStateChange(boolean on) {
      if (on && this.isInStandby() && !this._enableBacklightForNotify) {
         this.exitStandby();
      }
   }

   @Override
   public final void powerOffRequested(int reason) {
      if (reason == 1 && this.isInStandby()) {
         this.exitStandby();
      }
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
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
   public final boolean handleKeyDown(int time) {
      if (!this._inStandby) {
         this._muteKeyRepeated = false;
      }

      return false;
   }

   @Override
   public final boolean handleKeyRepeat(int time) {
      if (!this._muteKeyRepeated) {
         this._muteKeyRepeated = true;
         if (!Phone.getInstance().isActive() && !this._isVADActive) {
            this.enterStandby();
         }
      }

      return true;
   }

   private final void enterStandby() {
      this._interruptEnterStandby = false;
      this._inStandbyForPhone = false;
      StandbyManagerImpl$EnterStandbyDialog standbyDialog = new StandbyManagerImpl$EnterStandbyDialog(this, true);
      new StandbyManagerImpl$EnterStandbyIdleMonitor(this, standbyDialog, 2000);
      UiApplication.getUiApplication().pushGlobalScreen(standbyDialog, -2147483647, 1);
   }
}
