package net.rim.device.apps.internal.options.items.network;

import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.internal.system.NetworkInfo;
import net.rim.vm.Array;

public final class PrefNetworkSelectOption$PrefNetworkInfo extends NetworkInfo implements KeyProvider {
   public PrefNetworkSelectOption$PrefNetworkInfo() {
   }

   public PrefNetworkSelectOption$PrefNetworkInfo(String name, int netId) {
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      String name = this.getName();
      int keyCount = 0;
      if (name != null) {
         if (index + 1 > keyArray.length) {
            Array.resize(keyArray, index + 1);
         }

         keyArray[index] = name;
         keyCount = 1;
      }

      return keyCount;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }
}
