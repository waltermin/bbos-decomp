package net.rim.tid.dynamic_ling_data;

import net.rim.device.api.util.Arrays;
import net.rim.tid.im.conv.europe.repository.IContextPairsTable;

public class ContextPairsTable_fr_qwerty_default___QRED implements IContextPairsTable {
   private final int[] KEYS;
   private final int[][][] CONTEXT_HASHES;

   @Override
   public final boolean hasContextHash(int keyHashCode, int contextHasCode) {
      int keyIndex = Arrays.binarySearch(this.KEYS, keyHashCode);
      return keyIndex < 0 ? false : Arrays.binarySearch((int[])this.CONTEXT_HASHES[keyIndex], contextHasCode) >= 0;
   }
}
