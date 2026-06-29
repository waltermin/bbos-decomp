package net.rim.device.apps.internal.mms;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.internal.proxy.Proxy;

public final class MMSPolicy implements GlobalEventListener {
   private Vector _listeners = (Vector)(new Object());
   private boolean _isRegistered = MMSUtilities.isITPolicyEnabled();
   private static MMSPolicy _instance;

   private MMSPolicy() {
   }

   private static final MMSPolicy getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         boolean startListening = false;
         synchronized (applicationRegistry) {
            long MMS_POLICY_GUID = 238853704922185499L;
            _instance = (MMSPolicy)applicationRegistry.get(MMS_POLICY_GUID);
            if (_instance == null) {
               _instance = new MMSPolicy();
               applicationRegistry.put(MMS_POLICY_GUID, _instance);
               startListening = true;
            }
         }

         if (startListening) {
            Proxy.getInstance().addGlobalEventListener(_instance);
         }
      }

      return _instance;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         this.updateRegistration();
      }
   }

   public static final void addListener(MMSPolicy$ChangeListener listener) {
      MMSPolicy mmsPolicy = getInstance();
      synchronized (mmsPolicy) {
         mmsPolicy._listeners.addElement(listener);
         if (mmsPolicy._isRegistered) {
            listener.onPolicyEnabled();
         }
      }
   }

   private final synchronized void updateRegistration() {
      if (MMSUtilities.isITPolicyEnabled()) {
         if (!this._isRegistered) {
            this.registerAll();
            this._isRegistered = true;
            return;
         }
      } else if (this._isRegistered) {
         this.deregisterAll();
         this._isRegistered = false;
      }
   }

   private final void registerAll() {
      int count = this._listeners.size();

      for (int idx = 0; idx < count; idx++) {
         MMSPolicy$ChangeListener listener = (MMSPolicy$ChangeListener)this._listeners.elementAt(idx);
         listener.onPolicyEnabled();
      }
   }

   private final void deregisterAll() {
      int count = this._listeners.size();

      for (int idx = 0; idx < count; idx++) {
         MMSPolicy$ChangeListener listener = (MMSPolicy$ChangeListener)this._listeners.elementAt(idx);
         listener.onPolicyDisabled();
      }
   }
}
