package net.rim.tid.io;

import java.io.ByteArrayOutputStream;
import javax.microedition.rms.RecordStore;

public class RMSOutputStream extends ByteArrayOutputStream {
   RecordStore iRs;
   int iID;

   public RMSOutputStream(RecordStore aRs, int aID) {
      this.iRs = aRs;
      this.iID = aID;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void close() {
      label17:
      try {
         this.iRs.setRecord(this.iID, super.buf, 0, super.count);
      } catch (Throwable var3) {
         e.printStackTrace();
         break label17;
      }

      super.close();
   }
}
