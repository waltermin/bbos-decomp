package net.rim.device.api.ui;

import net.rim.device.api.system.Application;

public final class PNGImageWriterManagerHack {
   private PNGImageWriterManagerHack() {
   }

   public static final void printContent(Graphics g, Manager mgr) {
      synchronized (Application.getEventLock()) {
         mgr.paint(g);
      }
   }
}
