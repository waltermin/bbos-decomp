package net.rim.device.apps.internal.sms;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.internal.deviceoptions.SMSOptions;

public class SMSUiRegistry {
   private Vector _registeredUis = (Vector)(new Object());
   private Object _currentDelegate;
   private static final long SMS_UI_REGISTRY;
   private static SMSUiRegistry _registry;

   public static SMSUiRegistry getRegistry() {
      if (_registry == null) {
         ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
         _registry = (SMSUiRegistry)registry.get(6113993352280971942L);
         if (_registry == null) {
            _registry = new SMSUiRegistry();
            registry.put(6113993352280971942L, _registry);
         }
      }

      return _registry;
   }

   private SMSUiRegistry() {
   }

   public void addUiDelegate(Object delegateUi) {
      this._registeredUis.addElement(delegateUi);
      if (SMSOptions.getPresetUiId() == delegateUi.hashCode()) {
         this._currentDelegate = delegateUi;
         RIMGlobalMessagePoster.postGlobalEvent(7884295420352689779L);
      }
   }

   public Vector getUiDelegates() {
      return this._registeredUis;
   }

   public Object getCurrentUi() {
      return this._currentDelegate;
   }

   public void setCurrentUi(Object delegate) {
      this._currentDelegate = delegate;
   }
}
