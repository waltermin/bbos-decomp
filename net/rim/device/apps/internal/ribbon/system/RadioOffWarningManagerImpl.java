package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ribbon.RadioOffWarningManager;
import net.rim.device.apps.api.ribbon.RadioOffWarningManager$Listener;
import net.rim.vm.WeakReference;

final class RadioOffWarningManagerImpl extends RadioOffWarningManager implements Runnable {
   private int _waitTimeout;
   Application _application;
   WeakReference[] _listeners = new WeakReference[0];
   int _pendingInvoke = -1;
   private static final int WAIT_TIMEOUT_WLAN = 2500;
   private static final int WAIT_TIMEOUT_OTHER = 500;
   static RadioOffWarningManagerImpl _instance = (RadioOffWarningManagerImpl)RadioOffWarningManager.getInstance();

   public static final void init() {
      _instance = new RadioOffWarningManagerImpl();
      ApplicationRegistry.getApplicationRegistry().put(2322924269272447401L, _instance);
   }

   RadioOffWarningManagerImpl() {
      this._application = Application.getApplication();
      this._waitTimeout = RadioInfo.getNetworkType() == 6 ? 2500 : 500;
   }

   @Override
   public final void addListener(RadioOffWarningManager$Listener listener) {
      synchronized (this._listeners) {
         Arrays.add(this._listeners, new WeakReference(listener));
      }
   }

   @Override
   public final void removeListener(RadioOffWarningManager$Listener listener) {
      synchronized (this._listeners) {
         for (int i = this._listeners.length - 1; i >= 0; i--) {
            if ((RadioOffWarningManager$Listener)this._listeners[i].get() == listener) {
               Arrays.removeAt(this._listeners, i);
            }
         }
      }
   }

   static final void fire(int type) {
      WeakReference[] listeners = _instance._listeners;
      synchronized (listeners) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            RadioOffWarningManager$Listener listener = (RadioOffWarningManager$Listener)listeners[i].get();
            if (listener != null) {
               try {
                  listener.onEvent(type);
               } finally {
                  continue;
               }
            } else {
               Arrays.removeAt(listeners, i);
            }
         }
      }
   }

   static final void requestRadioOff() {
      _instance.internalRequestRadioOff();
   }

   private final void internalRequestRadioOff() {
      synchronized (this) {
         fire(1);
         if (this._pendingInvoke == -1) {
            this._pendingInvoke = this._application.invokeLater(this, this._waitTimeout, false);
         }
      }
   }

   @Override
   public final void run() {
      synchronized (this) {
         this._pendingInvoke = -1;
         EventLogger.logEvent(-6210296463828503575L, "Radio off request".getBytes(), 0);
         fire(3);
         Radio.requestPowerOff();
      }
   }
}
