package net.rim.blackberry.api.pdap;

import javax.microedition.pim.PIMException;
import net.rim.device.api.system.CodeModuleManager;

public final class MemoListFactory {
   private MemoListFactory() {
   }

   public static final BlackBerryMemoList createMemoList(int mode) {
      if (CodeModuleManager.getModuleHandle("net_rim_bb_memo_app") != 0) {
         return new MemoListImpl(mode);
      } else {
         throw new PIMException();
      }
   }

   public static final boolean isInternalMemoModel(Object input) {
      try {
         InternalBlackBerryMemoList memoList = (InternalBlackBerryMemoList)createMemoList(1);
         return memoList.isInternalMemoModel(input);
      } catch (PIMException pe) {
         return false;
      }
   }
}
