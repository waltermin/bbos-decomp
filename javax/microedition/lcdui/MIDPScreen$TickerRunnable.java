package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;

class MIDPScreen$TickerRunnable implements Runnable {
   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         MIDPScreen._idleTimer = false;
         MIDPScreen._pendingTimer = false;
         Displayable current = Display.getCurrentDisplayable();
         if (current != null && current.getPeer().advanceTicker()) {
            if (DeviceInfo.getIdleTime() < 120) {
               MIDPScreen.scheduleTickerTimer(150);
            } else {
               MIDPScreen._idleTimer = true;
            }
         }
      }
   }
}
