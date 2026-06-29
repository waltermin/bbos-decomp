package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.TrackwheelListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.InHolsterEventListener;
import net.rim.device.apps.internal.profiles.ProfileQuickToggle;

final class InHolsterEventHandler implements KeyListener, TrackwheelListener {
   private InHolsterEventListener _listener;
   private int _lastEscClickTime;
   private static final int ESC_DBL_CLICK_THRESHOLD = 500;

   private InHolsterEventHandler(InHolsterEventListener listener) {
      this._listener = listener;
      this._lastEscClickTime = 0;
   }

   public static final void register(UiApplication app, InHolsterEventListener listener) {
      InHolsterEventHandler handler = new InHolsterEventHandler(listener);
      app.addKeyListener(handler);
      app.addTrackwheelListener(handler);
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      if (DeviceInfo.isInHolster()) {
         this.notifyListener(1, 0);
      }

      return false;
   }

   @Override
   public final boolean trackwheelRoll(int amount, int status, int time) {
      if (DeviceInfo.isInHolster()) {
         this.notifyListener(0, amount);
      }

      return false;
   }

   @Override
   public final boolean trackwheelUnclick(int status, int time) {
      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      if (DeviceInfo.isInHolster()) {
         char key = (char)Keypad.key(keycode);
         if (key == 27) {
            int elapsedTime = time - this._lastEscClickTime;
            if (elapsedTime <= 500) {
               this.notifyListener(2, 0);
               VoiceServices.broadcastEvent(100302);
               return true;
            }

            this._lastEscClickTime = time;
            return true;
         }

         if (!Keypad.hasSendEndKeys() && key == 21) {
            this.notifyListener(6, time);
            return true;
         }

         if (key == 273) {
            if (RIMPhone.getInstance().getIncomingCall() != null) {
               this.notifyListener(8, time);
               return true;
            }

            if (Trackball.isSupported()) {
               return true;
            }

            return ProfileQuickToggle.handleKeyDown(time);
         }

         if ((key == 4097 || key == 4096) && RIMPhone.getInstance().getIncomingCall() != null) {
            this.notifyListener(9, time);
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      if (DeviceInfo.isInHolster() && Keypad.key(keycode) == 21) {
         this.notifyListener(7, time);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      if (!DeviceInfo.isInHolster() || Keypad.key(keycode) != 273) {
         return false;
      } else {
         return Trackball.isSupported() ? true : ProfileQuickToggle.handleKeyRepeat(time);
      }
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   private final void notifyListener(int eventId, int param) {
      this._listener.inHolsterEvent(eventId, param);
   }
}
