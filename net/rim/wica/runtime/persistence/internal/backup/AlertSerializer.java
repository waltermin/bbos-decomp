package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.wica.runtime.lifecycle.Alert;

final class AlertSerializer extends AbstractArraySerializer {
   private static AlertSerializer _instance;

   static final AlertSerializer getInstance() {
      if (_instance == null) {
         _instance = new AlertSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final Object createObject() {
      return new Alert();
   }

   @Override
   protected final Object[] createArray(int size) {
      return new Alert[size];
   }

   private AlertSerializer() {
   }
}
