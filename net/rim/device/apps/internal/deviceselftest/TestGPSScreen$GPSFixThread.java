package net.rim.device.apps.internal.deviceselftest;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;
import net.rim.device.api.system.Application;

final class TestGPSScreen$GPSFixThread extends Thread {
   TestGPSScreen screen;
   StringBuffer buf;

   TestGPSScreen$GPSFixThread(TestGPSScreen _screen) {
      this.screen = _screen;
      this.buf = (StringBuffer)(new Object());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      LocationProvider locationProvider = null;
      long t1 = 0;
      long t2 = 0;
      Criteria c1 = (Criteria)(new Object());
      c1.setHorizontalAccuracy(10);
      c1.setVerticalAccuracy(10);
      c1.setCostAllowed(false);
      boolean var19 = false /* VF: Semaphore variable */;

      label100: {
         try {
            label94:
            try {
               var19 = true;
               locationProvider = LocationProvider.getInstance(c1);
               if (locationProvider == null) {
                  this.updateInfo("Failure: GPS fixing times out!");
                  var19 = false;
               } else {
                  t1 = System.currentTimeMillis();
                  Location location = locationProvider.getLocation(150);
                  t2 = System.currentTimeMillis();
                  int fixingTime = (int)(t2 - t1);
                  if (Math.abs(fixingTime - 150000) < 1000) {
                     this.updateInfo("Failure: GPS fixing times out!");
                     var19 = false;
                  } else {
                     this.updateInfo(
                        ((StringBuffer)(new Object("GPS fixing succeeds!\r\nLatitude: ")))
                           .append(location.getQualifiedCoordinates().getLatitude())
                           .append("\r\nLongitude: ")
                           .append(location.getQualifiedCoordinates().getLongitude())
                           .append("\r\nCourse: ")
                           .append(location.getCourse())
                           .append("\r\nAddress Info: ")
                           .append(location.getAddressInfo())
                           .append("\r\nSpeed: ")
                           .append(location.getSpeed())
                           .append("\r\nExtra Info: ")
                           .append(location.getExtraInfo("text/plain"))
                           .append("\r\nTime to get auto fix : ")
                           .append(fixingTime)
                           .append(" ms")
                           .toString()
                     );
                     var19 = false;
                  }
               }
               break label100;
            } catch (Throwable var28) {
               this.updateInfo(le.getMessage());
               var19 = false;
               break label94;
            }
         } finally {
            if (var19) {
               this.screen.isFixing = false;
               synchronized (Application.getEventLock()) {
                  this.screen._status.setText(DeviceSelfTestResources.getString(140));
               }
            }
         }

         this.screen.isFixing = false;
         synchronized (Application.getEventLock()) {
            this.screen._status.setText(DeviceSelfTestResources.getString(140));
            return;
         }
      }

      this.screen.isFixing = false;
      synchronized (Application.getEventLock()) {
         this.screen._status.setText(DeviceSelfTestResources.getString(140));
      }
   }

   final void updateInfo(String str) {
      this.buf.append(str);
      synchronized (Application.getEventLock()) {
         this.screen._info.setText(this.buf.toString());
      }
   }
}
