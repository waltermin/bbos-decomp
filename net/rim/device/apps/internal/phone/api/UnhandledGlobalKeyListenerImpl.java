package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Keypad;
import net.rim.device.apps.api.ribbon.RibbonListener;
import net.rim.device.apps.api.ribbon.system.StandbyManager;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.UnhandledGlobalKeyListener;

public class UnhandledGlobalKeyListenerImpl implements RibbonListener, UnhandledGlobalKeyListener, Runnable {
   private Application _application;
   private int _timerID = -1;
   private boolean _isKeyDown;
   private boolean _sendKeyPressed;
   private static final long PHONE_APP_KEY_HANDLER = 1941799441586864365L;
   private static final int INVALID_TIMER_ID = -1;
   private static final int DEFAULT_TIMEOUT = 1000;
   private static UnhandledGlobalKeyListenerImpl _instance;

   protected void handleApplicationKeyPress() {
      throw null;
   }

   protected void handleApplicationLongKeyPress() {
      throw null;
   }

   protected void handleSendKeyPress() {
      throw null;
   }

   protected void handleSendKeyPressedAndHeld() {
      throw null;
   }

   protected void handleEndKeyPress() {
      throw null;
   }

   protected void handleToggleSpeakerPhone() {
   }

   protected void handleToggleMute() {
   }

   public synchronized boolean applicationKeyUp(int time) {
      this._isKeyDown = false;
      if (this._timerID != -1) {
         this.cancelTimer();
         this.handleApplicationKeyPress();
      }

      return true;
   }

   public synchronized boolean applicationKeyDown(int time) {
      this._isKeyDown = true;
      if (this._timerID != -1) {
         this.cancelTimer();
      }

      this.startTimer();
      return true;
   }

   @Override
   public boolean keyUp(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 0) {
         return false;
      }

      if (key == this.getPhoneKey()) {
         this.applicationKeyUp(time);
         return true;
      }

      if (key == 17) {
         if (this._sendKeyPressed) {
            this._sendKeyPressed = false;
            this.handleSendKeyPress();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyRepeat(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 17) {
         if (this._sendKeyPressed) {
            this._sendKeyPressed = false;
            this.handleSendKeyPressedAndHeld();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 0) {
         return false;
      } else if (key == this.getPhoneKey()) {
         this.applicationKeyDown(time);
         return true;
      } else if (key == 18) {
         this.handleEndKeyPress();
         return true;
      } else if (key == 17 && !StandbyManager.getInstance().isInStandby()) {
         this._sendKeyPressed = true;
         return true;
      } else if (PhoneUtilities.isMuteKey(keycode)) {
         this.handleToggleMute();
         return true;
      } else if (PhoneUtilities.isSpeakerPhoneKey(keycode)) {
         this.handleToggleSpeakerPhone();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final synchronized void run() {
      this._timerID = -1;
      this.handleApplicationLongKeyPress();
   }

   public UnhandledGlobalKeyListenerImpl(Application application) {
      this._application = application;
      _instance = this;
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      if (reg.get(1941799441586864365L) != null) {
         reg.replace(1941799441586864365L, _instance);
      } else {
         reg.put(1941799441586864365L, _instance);
      }
   }

   private int getPhoneKey() {
      return Keypad.hasSendEndKeys() && InternalServices.getHardwareID() != 67111173 ? 0 : 21;
   }

   private void startTimer() {
      this._timerID = this._application.invokeLater(this, 1000, false);
   }

   private void cancelTimer() {
      this._application.cancelInvokeLater(this._timerID);
      this._timerID = -1;
   }

   private static UnhandledGlobalKeyListenerImpl getInstance() {
      if (_instance == null) {
         _instance = (UnhandledGlobalKeyListenerImpl)ApplicationRegistry.getApplicationRegistry().get(1941799441586864365L);
      }

      return _instance;
   }

   public static void setApplicationKeyState(boolean isKeyDown) {
      UnhandledGlobalKeyListenerImpl appKeyHandler = getInstance();
      if (appKeyHandler != null) {
         appKeyHandler._isKeyDown = isKeyDown;
      }
   }

   public static boolean isApplicationKeyDown() {
      UnhandledGlobalKeyListenerImpl appKeyHandler = getInstance();
      return appKeyHandler != null ? appKeyHandler._isKeyDown : false;
   }
}
