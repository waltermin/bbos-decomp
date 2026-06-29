package simulationservicebook;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEUtilities;

class InsertInternetServiceBook$2 implements InsertInternetServiceBook$ApplicationDataProvider {
   private final InsertInternetServiceBook this$0;

   InsertInternetServiceBook$2(InsertInternetServiceBook _1) {
      this.this$0 = _1;
   }

   @Override
   public byte[] get() {
      DataBuffer db = new DataBuffer();
      db.writeByte(16);
      TLEUtilities.writeIntegerField(db, 240, 3, false);
      byte[] data = new byte[]{0, 0, 0, 2, 0, 0, 0, 2};
      TLEUtilities.writeDataField(db, 48, data);
      String emailAddress = "simulation@this.machine";
      TLEUtilities.writeStringField(db, 16, emailAddress, false);
      db.writeByte(0);
      return db.getArray();
   }
}
