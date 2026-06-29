package net.rim.device.apps.api.messaging;

import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.apps.api.framework.model.KeyProvider;

public final class DateSortKeyProviderIndirection implements LongKeyProviderAdaptor {
   private long[] keyValues = new long[1];

   @Override
   public final long getLongKey(Object element) {
      KeyProvider keyProvider = (KeyProvider)element;
      keyProvider.getKeys(null, this.keyValues, 0, 92199951187614847L);
      return this.keyValues[0];
   }
}
