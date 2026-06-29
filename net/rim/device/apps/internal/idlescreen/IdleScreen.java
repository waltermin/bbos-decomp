package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.DirectConnectListener;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.PhoneCallListener;
import net.rim.device.api.system.StylusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.system.TrackwheelListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.container.FullScreen;

final class IdleScreen extends FullScreen implements HolsterListener, Runnable, SystemListener, DirectConnectListener, PhoneCallListener {
   private IdleScreenApplication _application;
   private boolean _highPriority;
   private boolean _busy;

   final void show(IdleScreenApplication application) {
      this._application = application;
      this._application.addHolsterListener(this);
      this._application.addSystemListener(this);
      if (DirectConnect.isSupported()) {
         this._application.addRadioListener(this);
      }

      if (this._application.getHook() == null && DeviceInfo.getIdleTime() >= IdleScreenOptions.getTimeout()) {
         this._highPriority = false;
      } else {
         this._highPriority = true;
      }

      this._application.pushGlobalScreen(this, this._highPriority ? -1073741825 : Integer.MAX_VALUE, 2);
   }

   public final void elevatePriority() {
      if (!this._highPriority) {
         this._highPriority = true;
         this._application.popScreen(this);
         this._application.pushGlobalScreen(this, -1073741825, 2);
      }
   }

   private final void hide() {
      this._application.popScreen(this);
      this._application.invokeLater(this);
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         if (!this._highPriority && DeviceInfo.getIdleTime() < IdleScreenOptions.getTimeout()) {
            this.hide();
         }

         if (Phone.getInstance().isActive()) {
            this.hide();
         }
      }
   }

   @Override
   protected final void onObscured() {
      if (this.getUiEngine() != null) {
         this.hide();
      }
   }

   @Override
   public final void run() {
      this._application.exit();
   }

   @Override
   public final boolean stylusTap(int x, int y, int status, int time) {
      this.hide();
      Application hook = this._application.getHook();
      if (hook instanceof StylusListener) {
         StylusListener stylusListener = (StylusListener)hook;
         int finalX = x;
         int finalY = y;
         int finalStatus = status;
         int finalTime = time;
         boolean busy;
         synchronized (stylusListener) {
            busy = this._busy;
            this._busy = true;
         }

         if (!busy) {
            hook.invokeLater(new IdleScreen$1(this, stylusListener, finalX, finalY, finalStatus, finalTime));
         }
      }

      return true;
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      this.hide();
      Application hook = this._application.getHook();
      if (hook instanceof TrackwheelListener) {
         TrackwheelListener trackwheelListener = (TrackwheelListener)hook;
         int finalStatus = status;
         int finalTime = time;
         boolean busy;
         synchronized (trackwheelListener) {
            busy = this._busy;
            this._busy = true;
         }

         if (!busy) {
            hook.invokeLater(new IdleScreen$2(this, trackwheelListener, finalStatus, finalTime));
         }
      }

      return true;
   }

   @Override
   public final boolean trackwheelRoll(int amount, int status, int time) {
      this.hide();
      Application hook = this._application.getHook();
      if (hook instanceof TrackwheelListener) {
         TrackwheelListener trackwheelListener = (TrackwheelListener)hook;
         int finalAmount = amount;
         int finalStatus = status;
         int finalTime = time;
         boolean busy;
         synchronized (trackwheelListener) {
            busy = this._busy;
            this._busy = true;
         }

         if (!busy) {
            hook.invokeLater(new IdleScreen$3(this, trackwheelListener, finalAmount, finalStatus, finalTime));
         }
      }

      return true;
   }

   @Override
   public final boolean trackwheelUnclick(int status, int time) {
      return true;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      this.hide();
      Application hook = this._application.getHook();
      if (hook instanceof KeyListener) {
         KeyListener keyListener = (KeyListener)hook;
         char finalKey = key;
         int finalStatus = status;
         int finalTime = time;
         boolean busy;
         synchronized (keyListener) {
            busy = this._busy;
            this._busy = true;
         }

         if (!busy) {
            hook.invokeLater(new IdleScreen$4(this, keyListener, finalKey, finalStatus, finalTime));
         }
      }

      return true;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if ((key < 65 || key > 90) && (key < 48 || key > 57)) {
         this.hide();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyUp(int keycode, int tim) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      if (Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) == 0) {
         return false;
      }

      this.hide();
      return true;
   }

   @Override
   public final void inHolster() {
      this.hide();
   }

   @Override
   public final void outOfHolster() {
      this.hide();
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
      this.hide();
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
   public final void dcCallConnected(int callId, int callType, boolean incoming) {
      this.hide();
   }

   @Override
   public final void dcCallDisconnected(int callId, int callType, int reason) {
   }

   @Override
   public final void dcRequestFailed(int callId, int callType, int reason) {
   }

   @Override
   public final void dcCallStatusUpdated(int callId, int callType) {
   }

   @Override
   public final void dcTalkStatusUpdated(int callId, int callType, int talkStatus) {
   }

   @Override
   public final void dcTalkGroupIdUpdated(int callId, boolean success, int talkGroupId) {
   }

   @Override
   public final void dcCallAlertUpdate(int callId, int alertState, int reason) {
   }

   @Override
   public final void dcServiceUpdated(int service, boolean success, boolean enabled) {
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
      this.hide();
   }

   @Override
   public final void callConnected(int callId) {
   }

   @Override
   public final void callFailed(int callId, int error) {
   }

   @Override
   public final void callDelivered(int callId) {
   }

   @Override
   public final void callManipulateFailed(int callId, int error) {
   }

   @Override
   public final void callDisconnected(int callId) {
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
   public final void callVoicePrivacyUpdated(int callId, boolean on) {
   }

   @Override
   public final void callOTAStatusUpdated(int callId, int status) {
   }

   @Override
   public final void dtmfData(int dtmf) {
   }
}
