package net.rim.device.apps.internal.browser.wappush;

import java.io.DataInput;
import java.io.DataInputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.api.utility.serialization.BaseConverter;

final class SICConverter extends BaseConverter {
   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public final Object convert(DataInput aDataInput, Object contextObject) {
      if (contextObject instanceof Object && aDataInput instanceof Object) {
         return SICModelFactory.createSICModel((DataInputStream)aDataInput, (HttpHeaders)contextObject);
      } else {
         throw new Object();
      }
   }
}
