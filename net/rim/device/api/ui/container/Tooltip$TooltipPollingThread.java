package net.rim.device.api.ui.container;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;

final class Tooltip$TooltipPollingThread extends Thread {
   private int _previous;
   private long _notificationTime;
   private UiApplication _app = UiApplication.getUiApplication();
   private static final int TIP_DELAY;
   private static final int TIP_DELAY_MS;
   private static Tooltip$TooltipPollingThread _instance;

   private static final synchronized Tooltip$TooltipPollingThread getInstance() {
      if (_instance == null) {
         _instance = new Tooltip$TooltipPollingThread();
         _instance.start();
      }

      return _instance;
   }

   @Override
   public final void run() {
      while (true) {
         try {
            Tooltip$TooltipProvider tooltipProvider = this.getTooltipProvider();
            if (tooltipProvider != null && tooltipProvider.hashCode() != this._previous) {
               long idleTime = DeviceInfo.getIdleTime();
               long notificationIdleTime = System.currentTimeMillis() - this._notificationTime;
               if (1 > idleTime || 1000 > notificationIdleTime) {
                  Thread.sleep(200);
               } else {
                  this._previous = tooltipProvider.hashCode();
                  Tooltip.show(tooltipProvider);
               }
            } else {
               Tooltip$TooltipProvider var8 = null;
               synchronized (this) {
                  this.wait();
               }
            }
         } catch (InterruptedException var7) {
         }
      }
   }

   public static final synchronized void reset() {
      Tooltip$TooltipPollingThread instance = getInstance();
      synchronized (instance) {
         instance._previous = 0;
         instance._notificationTime = System.currentTimeMillis();
         instance.notify();
      }
   }

   private final Tooltip$TooltipProvider getTooltipProvider() {
      if (this._app != null && this._app.isForeground()) {
         Screen screen = this._app.getActiveScreen();
         if (screen == null) {
            return null;
         }

         Field field = screen.getLeafFieldWithFocus();
         if (field instanceof Tooltip$TooltipProvider) {
            return (Tooltip$TooltipProvider)field;
         }

         if (field != null) {
            for (Manager manager = field.getManager(); manager != null; manager = manager.getManager()) {
               if (manager instanceof Tooltip$TooltipProvider) {
                  return (Tooltip$TooltipProvider)manager;
               }
            }
         }
      }

      return null;
   }
}
