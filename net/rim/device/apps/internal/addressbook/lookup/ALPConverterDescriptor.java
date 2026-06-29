package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;

final class ALPConverterDescriptor implements ConverterDescriptor {
   private Converter _requestConverter = new ALPRequestConverter();
   private Converter _resultConverter = new ALPResultConverter();

   @Override
   public final boolean canConvert(byte[] inputBytes, Object contextObject) {
      return false;
   }

   @Override
   public final boolean canConvert(Object inputObject, Object contextObject) {
      return false;
   }

   @Override
   public final Object getContext() {
      return TransmissionServiceManager.get(-8892319056465090102L).getContext();
   }

   @Override
   public final Converter createConverterInstance(String type) {
      if (type == null) {
         return null;
      } else if (type.equals("net.rim.AddressLookupProtocol.Request")) {
         return this._requestConverter;
      } else {
         return type.equals("net.rim.AddressLookupProtocol.Result") ? this._resultConverter : null;
      }
   }
}
