package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;

public final class HintPollingThread extends Thread {
   private int _previous;
   private UiApplication _app = UiApplication.getUiApplication();
   private boolean _alive;
   private static final int HINT_DELAY;
   private static HintPollingThread _instance;

   private static final synchronized HintPollingThread getInstance() {
      if (_instance == null) {
         _instance = new HintPollingThread();
         _instance._alive = true;
         _instance.start();
      }

      return _instance;
   }

   @Override
   public final void run() {
      while (this._alive) {
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

      _instance = null;
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
         if (screen != null) {
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
      }

      return null;
   }
}
