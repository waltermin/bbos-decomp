package net.rim.device.internal.synchronization.ota.api;

public class SyncApplicationUnOptimizedGroupChangeList extends SyncApplicationUnOptimizedChangeList implements SyncApplicationGroupChangeList {
   private int[] _listOfUids;
   private int _operation;
   private boolean _forSlowSync;

   @Override
   public void setOperation(int anOperation) {
      this._operation = anOperation;
   }

   @Override
   public int getOperation() {
      return this._operation;
   }

   @Override
   public void setForSlowSync(boolean value) {
      this._forSlowSync = value;
   }

   @Override
   public boolean isForSlowSync() {
      return this._forSlowSync;
   }

   @Override
   public void setListOfUids(int[] aListOfUids) {
      this._listOfUids = aListOfUids;
   }

   @Override
   public int[] getListOfUids() {
      return this._listOfUids;
   }
}
