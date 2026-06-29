package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.system.Events;

final class SelectDate$SelectScreen extends PopupScreen {
   KeyListener _kl;

   SelectDate$SelectScreen(KeyListener kl) {
      super(new VerticalFieldManager(1152921504606846976L));
      this._kl = kl;
   }

   @Override
   public final boolean dispatchKeyEvent(int event, char key, int status, int time) {
      boolean handled = false;
      if ((status & 1) == 0) {
         if (key == 131 || key == 132) {
            return super.dispatchKeyEvent(event, key, status, time);
         }

         if (key == 129 || key == 130) {
            return this.trackwheelRoll(key == 129 ? -1 : 1, status | 1, time);
         }
      }

      handled |= Events.dispatchKeyEvent(event, key, status, time, this._kl);
      if (!handled) {
         handled |= super.dispatchKeyEvent(event, key, status, time);
      }

      return handled;
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.trackwheelClick(0, 0);
            return true;
         default:
            return false;
      }
   }
}
