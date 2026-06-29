package net.rim.device.apps.internal.commonmodels.opaque;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;

class OpaqueDataModel implements PersistableRIMModel, ConversionProvider {
   int _fieldId;
   byte[] _data;

   OpaqueDataModel(Object createContext) {
      if (createContext != null) {
         ContextObject contextObject = ContextObject.verifyNonNull(createContext);
         this._fieldId = contextObject.getIntegerData(0);
         this._data = (byte[])contextObject.get(8849067667159082262L);
      }
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 19) && ContextObject.getFlag(context, 57)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         if (syncBuffer != null) {
            syncBuffer.addBytes(this._fieldId, this._data);
         }

         return true;
      } else {
         return false;
      }
   }
}
