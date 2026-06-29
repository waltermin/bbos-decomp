package net.rim.device.apps.internal.browser.wappush;

import java.io.DataInput;
import java.io.DataInputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.SerializationException;

final class COCConverter extends BaseConverter {
   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public final Object convert(DataInput aDataInput, Object contextObject) throws SerializationException {
      if (contextObject instanceof HttpHeaders && aDataInput instanceof DataInputStream) {
         return COCModelFactory.createCOCModel((DataInputStream)aDataInput, (HttpHeaders)contextObject);
      } else {
         throw new SerializationException();
      }
   }
}
