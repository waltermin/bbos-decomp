package net.rim.device.apps.api.memo;

import net.rim.device.api.system.ApplicationRegistry;

public final class MemoCollectionHolder {
   private static final long MEMO_COLLECTION_ID = -5965364677584041048L;

   private MemoCollectionHolder() {
   }

   public static final MemoCollection getMemoCollection() {
      return (MemoCollection)ApplicationRegistry.getApplicationRegistry().waitFor(-5965364677584041048L);
   }
}
