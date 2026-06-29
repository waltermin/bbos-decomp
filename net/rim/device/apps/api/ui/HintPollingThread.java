package net.rim.device.apps.api.ui;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;

public final class HintPollingThread extends Thread {
   private int _previous;
   private UiApplication _app = UiApplication.getUiApplication();
   private static final int HINT_DELAY = 1;
   private static HintPollingThread _instance;

   private static final synchronized HintPollingThread getInstance() {
      if (_instance == null) {
         _instance = new HintPollingThread();
         _instance.start();
      }

      return _instance;
   }

   @Override
   public final void run() {
      while (true) {
         try {
            HintPollingThread$HintProvider hintProvider = this.getHintProvider();
            if (hintProvider != null && hintProvider.hashCode() != this._previous) {
               long idleTime = DeviceInfo.getIdleTime();
               if (1 > idleTime) {
                  Thread.sleep(200);
               } else {
                  this._previous = hintProvider.hashCode();
                  HintPopup.show(hintProvider);
               }
            } else {
               HintPollingThread$HintProvider var8 = null;
               synchronized (this) {
                  this.wait();
               }
            }
         } finally {
            continue;
         }
      }
   }

   public static final synchronized void reset() {
      HintPollingThread instance = getInstance();
      synchronized (instance) {
         instance._previous = 0;
         instance.notify();
      }
   }

   private final HintPollingThread$HintProvider getHintProvider() {
      if (this._app != null && this._app.isForeground()) {
         Screen screen = this._app.getActiveScreen();
         if (screen == null) {
            return null;
         }

         Field field = screen.getLeafFieldWithFocus();
         if (field instanceof HintPollingThread$HintProvider) {
            return (HintPollingThread$HintProvider)field;
         }

         if (field != null) {
            for (Manager manager = field.getManager(); manager != null; manager = manager.getManager()) {
               if (manager instanceof HintPollingThread$HintProvider) {
                  return (HintPollingThread$HintProvider)manager;
               }
            }
         }
      }

      return null;
   }
}
