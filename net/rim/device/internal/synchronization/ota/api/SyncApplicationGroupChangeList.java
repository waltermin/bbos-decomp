package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.Persistable;

public interface SyncApplicationGroupChangeList extends Persistable {
   void setOperation(int var1);

   int getOperation();

   void setForSlowSync(boolean var1);

   boolean isForSlowSync();

   void setListOfUids(int[] var1);

   int[] getListOfUids();
}
