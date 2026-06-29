package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.apps.api.framework.model.KeyProvider;

public final class CalendarLongKeyAdaptor implements LongKeyProviderAdaptor {
   private long _keyId;
   private long[] _keyArray = new long[]{
      0L,
      3036626870574777110L,
      -5537618871186774487L,
      6672705438745047216L,
      -7607282562496045479L,
      8776164264454047010L,
      3966910304299774007L,
      -1227969331878083456L
   };

   public CalendarLongKeyAdaptor(long keyId) {
      this._keyId = keyId;
   }

   @Override
   public final long getLongKey(Object element) {
      ((KeyProvider)element).getKeys(null, this._keyArray, 0, this._keyId);
      return this._keyArray[0];
   }
}
