package net.rim.device.internal.synchronization.ota.api;

import java.util.Vector;
import net.rim.device.api.util.CRC32;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabase;
import net.rim.device.internal.synchronization.ota.util.ReusableObject;
import net.rim.device.internal.synchronization.ota.util.ReusableObjectPool;

public final class SyncAgentGroupOfRecords extends Vector implements ReusableObject {
   private int _groupHashCode;
   private int _id;
   public static final long POOL_GUID;

   public static final SyncAgentGroupOfRecords[] createGroups(
      SyncAgentRecordHashesList recordHashesList, DataSourceDatabase aDataSourceDatabase, boolean aNoneXorHashEnabled
   ) {
      int xNumberOfGroups = aDataSourceDatabase.getNumberOfGroups();
      SyncAgentGroupOfRecords[] xGroups = new SyncAgentGroupOfRecords[xNumberOfGroups];
      ReusableObjectPool xResuableObjectPool = ReusableObjectPool.getSingletonInstance(-7570004851727517767L);
      if (aNoneXorHashEnabled) {
         recordHashesList.Sort();
      }

      for (int xIndex = recordHashesList.size() - 1; xIndex >= 0; xIndex--) {
         SyncAgentRecordHashes xSyncAgentRecordHashes = (SyncAgentRecordHashes)recordHashesList.elementAt(xIndex);
         int xGroupId = Math.abs(xSyncAgentRecordHashes.getUid() % xNumberOfGroups);
         SyncAgentGroupOfRecords xSyncAgentGroupOfRecords = xGroups[xGroupId];
         if (xSyncAgentGroupOfRecords == null) {
            xSyncAgentGroupOfRecords = (SyncAgentGroupOfRecords)xResuableObjectPool.checkOut();
            if (xSyncAgentGroupOfRecords == null) {
               xSyncAgentGroupOfRecords = new SyncAgentGroupOfRecords();
            }

            xSyncAgentGroupOfRecords.setId(xGroupId);
            xGroups[xGroupId] = xSyncAgentGroupOfRecords;
            xGroups[xGroupId].setInitialHashCode(aNoneXorHashEnabled);
         }

         xSyncAgentGroupOfRecords.addSyncAgentRecordHashes(xSyncAgentRecordHashes, aNoneXorHashEnabled);
      }

      return xGroups;
   }

   public SyncAgentGroupOfRecords() {
   }

   public SyncAgentGroupOfRecords(int anId) {
      this();
      this.setId(anId);
   }

   public final void setId(int anId) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getId() {
      return this._id;
   }

   public final void setInitialHashCode(boolean aNoneXorHashEnabled) {
      if (aNoneXorHashEnabled) {
         this._groupHashCode = -1;
      } else {
         this._groupHashCode = 0;
      }
   }

   private final void removeAllSyncAgentRecordHashes() {
      if (!this.isEmpty()) {
         ReusableObjectPool xReusableObjectPool = ReusableObjectPool.getSingletonInstance(-49889245922388290L);

         for (int xIndex = this.size() - 1; xIndex > -1; xIndex--) {
            xReusableObjectPool.checkIn((ReusableObject)this.elementAt(xIndex));
         }
      }
   }

   public final void addSyncAgentRecordHashes(SyncAgentRecordHashes aSyncAgentRecordHashes, boolean aNoneXorHashEnabled) {
      int xUid = aSyncAgentRecordHashes.getUid();
      int xHashCode;
      if (aNoneXorHashEnabled) {
         xHashCode = xUid;
      } else {
         xHashCode = CRC32.updateInt(-1, xUid);
      }

      xHashCode = CRC32.updateInt(xHashCode, aSyncAgentRecordHashes.getAllFieldsHash());
      if (aNoneXorHashEnabled) {
         if (this._groupHashCode == -1) {
            this._groupHashCode = xHashCode;
         } else {
            this._groupHashCode = CRC32.updateInt(this._groupHashCode, xHashCode);
         }
      } else {
         this._groupHashCode ^= xHashCode;
      }

      super.addElement(aSyncAgentRecordHashes);
   }

   @Override
   public final int hashCode() {
      return this._groupHashCode;
   }

   @Override
   public final void reset() {
      this._id = 0;
      this._groupHashCode = 0;
      this.removeAllSyncAgentRecordHashes();
      this.removeAllElements();
   }
}
