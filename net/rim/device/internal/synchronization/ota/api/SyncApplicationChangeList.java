package net.rim.device.internal.synchronization.ota.api;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.synchronization.ota.util.Helper;
import net.rim.vm.Persistence;

public class SyncApplicationChangeList implements Persistable {
   private byte _flags;
   protected IntHashtable _changes = (IntHashtable)(new Object());
   protected IntIntHashtable _refIdToHashCodeMap = (IntIntHashtable)(new Object());
   private static final int LOCKED;
   private static final int FILL_IN;

   public synchronized void lock() {
      this._flags = (byte)Helper.setFlagValue(this._flags, true, 1);
   }

   public synchronized void unlock() {
      this._flags = (byte)Helper.setFlagValue(this._flags, false, 1);
   }

   public synchronized boolean isLocked() {
      return Helper.getFlagValue(this._flags, 1);
   }

   public synchronized void shouldBeFilled(boolean fillIn) {
      this._flags = (byte)Helper.setFlagValue(this._flags, fillIn, 2);
      if (fillIn && !this._changes.isEmpty()) {
         Enumeration xList = this._changes.elements();

         while (xList.hasMoreElements()) {
            SyncApplicationChange xChange = (SyncApplicationChange)xList.nextElement();
            xChange.shouldBeFilled(fillIn);
         }
      }
   }

   public synchronized void encrypt(boolean encrypt) {
      Enumeration xList = this._changes.elements();

      while (xList.hasMoreElements()) {
         SyncApplicationChange xChange = (SyncApplicationChange)xList.nextElement();
         xChange.encrypt(encrypt);
      }
   }

   public boolean shouldBeFilled() {
      return Helper.getFlagValue(this._flags, 2);
   }

   public boolean isEmpty() {
      return this.shouldBeFilled() ? false : this.size() == 0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public IntHashtable getChanges(SyncAgentConnection aSyncAgentConenction) {
      IntHashtable xIntHashtable = (IntHashtable)(new Object());
      SyncAgentConnectionListener xSyncAgentConnectionListener = aSyncAgentConenction.getSyncAgentConnectionListener();
      int[] xKeysList = new int[this._changes.size()];
      this._changes.keysToArray(xKeysList);

      for (int xIndex = xKeysList.length - 1; xIndex > -1; xIndex--) {
         int xKey = xKeysList[xIndex];
         SyncApplicationRecordChange xChange = (SyncApplicationRecordChange)this._changes.get(xKey);
         int xRefId = xChange.getRefId();
         if (!this._refIdToHashCodeMap.containsKey(xRefId)) {
            this._refIdToHashCodeMap.put(xRefId, xChange.hashCode());
         }

         if (xChange.shouldBeFilled()) {
            if (xSyncAgentConnectionListener == null) {
               this._changes.remove(xKey);
               this._refIdToHashCodeMap.remove(xRefId);
               continue;
            }

            int xReturnCode = 411;
            boolean var12 = false /* VF: Semaphore variable */;

            try {
               var12 = true;
               aSyncAgentConenction.onSessionEvent(51, null);
               xChange = new SyncApplicationRecordChange(xChange);
               xReturnCode = aSyncAgentConenction.onSessionEvent(61, xChange);
               var12 = false;
            } finally {
               if (var12) {
                  aSyncAgentConenction.onSessionEvent(52, null);
               }
            }

            aSyncAgentConenction.onSessionEvent(52, null);
            if (xReturnCode != 200) {
               this._changes.remove(xKey);
               this._refIdToHashCodeMap.remove(xRefId);
               continue;
            }
         }

         int xGroup = xChange.getGroup();
         Vector xVector = (Vector)xIntHashtable.get(xGroup);
         if (xVector == null) {
            xVector = (Vector)(new Object());
            xIntHashtable.put(xGroup, xVector);
         }

         xVector.addElement(xChange);
      }

      return xIntHashtable;
   }

   public void removeByRefId(int aRefId) {
      if (this._refIdToHashCodeMap.containsKey(aRefId)) {
         int xHashCode = this._refIdToHashCodeMap.get(aRefId);
         this._refIdToHashCodeMap.remove(aRefId);
         this._changes.remove(xHashCode);
      }

      Persistence.commit(this, true);
   }

   public SyncApplicationChange getByRefId(int aRefId) {
      SyncApplicationChange xChange = null;
      if (this._refIdToHashCodeMap.containsKey(aRefId)) {
         int xHashCode = this._refIdToHashCodeMap.get(aRefId);
         xChange = (SyncApplicationChange)this._changes.get(xHashCode);
         if (xChange == null) {
            this._refIdToHashCodeMap.remove(aRefId);
         }
      }

      return xChange;
   }

   public void remove(int aHashCode) {
      SyncApplicationChange xChange = (SyncApplicationChange)this._changes.get(aHashCode);
      if (xChange != null) {
         this._changes.remove(aHashCode);
         this._refIdToHashCodeMap.remove(xChange.getRefId());
      }

      Persistence.commit(this, true);
   }

   public SyncApplicationChange get(int aHashCode) {
      return (SyncApplicationChange)this._changes.get(aHashCode);
   }

   public boolean containsOperation(int aHashCode, int operation) {
      SyncApplicationChange xSyncApplicationChange = this.get(aHashCode);
      return xSyncApplicationChange != null && xSyncApplicationChange.getOperation() == operation;
   }

   public int size() {
      return this._changes.size();
   }

   public Enumeration getChanges() {
      return this._changes.elements();
   }

   public void add(SyncApplicationChange _1) {
      throw null;
   }
}
