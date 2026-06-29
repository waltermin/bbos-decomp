package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.internal.system.InternalServices;

final class DataReader extends Thread {
   TestLightSensorScreen taskScreen;

   DataReader(TestLightSensorScreen ts) {
      this.taskScreen = ts;
   }

   @Override
   public final void run() {
      while (true) {
         synchronized (Application.getEventLock()) {
            this.taskScreen._reading.setText(((StringBuffer)(new Object(" "))).append(InternalServices.getLightSensorADCReading()).toString());
         }

         try {
            Thread.sleep(1500);
         } finally {
            continue;
         }
      }
   }
}
