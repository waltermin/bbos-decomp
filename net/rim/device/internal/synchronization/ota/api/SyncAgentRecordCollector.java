package net.rim.device.internal.synchronization.ota.api;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

public final class SyncAgentRecordCollector extends Vector {
   public SyncAgentRecordCollector(SyncAgentRecord aSyncAgentRecord) {
      super(aSyncAgentRecord.getLastIndex() + 1);
   }

   private final byte[] getCollectedFields() {
      try {
         ByteArrayOutputStream xTemp = (ByteArrayOutputStream)(new Object());
         int xLastIndex = this.size();

         for (int xIndex = 0; xIndex < xLastIndex; xIndex++) {
            xTemp.write((byte[])this.elementAt(xIndex));
         }

         xTemp.close();
         return xTemp.toByteArray();
      } finally {
         ;
      }
   }

   public final SyncAgentRecord add(SyncAgentRecord aSyncAgentRecord) {
      if (aSyncAgentRecord.isFragmented()) {
         byte[] xFields = aSyncAgentRecord.getFields();
         byte[] xFieldsCopy = new byte[xFields.length];
         System.arraycopy(xFields, 0, xFieldsCopy, 0, xFields.length);
         int xIndex = aSyncAgentRecord.getIndex();
         this.insertElementAt(xFieldsCopy, xIndex);
         if (aSyncAgentRecord.getLastIndex() == this.size() - 1) {
            byte[] xTemp = this.getCollectedFields();
            if (xTemp != null) {
               aSyncAgentRecord.setFields(xTemp);
               aSyncAgentRecord.setIndex(0);
               aSyncAgentRecord.setLastIndex(0);
               return aSyncAgentRecord;
            }

            aSyncAgentRecord = null;
         }
      }

      return aSyncAgentRecord;
   }
}
