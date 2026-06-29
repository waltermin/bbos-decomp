package net.rim.device.apps.internal.deviceselftest;

import java.util.TimerTask;

final class TimeoutAction extends TimerTask {
   TestRFAntennaScreen screen;

   @Override
   public final void run() {
      this.screen.insertReport();
   }

   TimeoutAction(TestRFAntennaScreen _screen) {
      this.screen = _screen;
   }
}
