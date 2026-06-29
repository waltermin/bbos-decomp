package net.rim.device.cldc.io.sync;

import java.io.IOException;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.synchronization.ota.util.LengthEncoding;
import net.rim.device.internal.synchronization.ota.util.ReusableObject;

public class SyncCommand implements ReusableObject {
   private int _tag;
   private int _id;
   protected static final IOException NotSupportedVersionException = new IOException("NSV");
   public static final byte UNKNOWEN = 0;
   public static final byte ADD = 1;
   public static final byte DELETE = 2;
   public static final byte UPDATE = 3;
   public static final byte REPLACE = 4;
   public static final byte GET = 5;
   public static final byte RECORD = 6;
   public static final byte USE = 7;
   public static final byte STATUS = 8;
   public static final byte GET_SYNC_CONFIGURATION = 9;
   public static final byte SYNC_CONFIGURATION = 10;
   public static final byte INITIATESYNC = 11;
   public static final byte SUSPEND = 12;
   public static final byte RESUME = 13;
   public static final byte GET_RECORDS_HASHES = 16;
   public static final byte RECORDS_HASHES = 17;
   public static final byte UPDATE_SYNC_CONFIGURATION = 18;
   public static final byte LOG = 19;

   protected void setTag(int aTag) {
      this._tag = aTag;
   }

   public int getTag() {
      return this._tag;
   }

   public void setId(int anId) {
      this._id = anId;
   }

   public int getId() {
      return this._id;
   }

   public void writeTo(DataBuffer dout) {
      dout.write(this.getTag());
      dout.writeInt(0);
      int xStartPositionOfParameters = dout.getPosition();
      this.writeParametersTo(dout);
      int xEndPositionOfParameters = dout.getPosition();
      int xParametersLength = xEndPositionOfParameters - xStartPositionOfParameters;
      dout.setPosition(xStartPositionOfParameters - 4);
      dout.writeInt(LengthEncoding.getFixedEncodingLengthFor(xParametersLength));
      dout.setPosition(xEndPositionOfParameters);
   }

   public boolean isValid() {
      return false;
   }

   public void readParametersFrom(DataBuffer din) {
   }

   public void writeParametersTo(DataBuffer dout) {
   }

   public SyncCommand[] fragment(SyncCommandsPool aSyncCommandsPool, int fragmentSize) {
      return new SyncCommand[]{this};
   }

   public int size() {
      return 3;
   }

   @Override
   public void reset() {
      this._id = 0;
   }

   @Override
   public boolean equals(Object anObject) {
      if (this == anObject) {
         return true;
      }

      if (!(anObject instanceof SyncCommand)) {
         return false;
      }

      SyncCommand xSyncCommand = (SyncCommand)anObject;
      return xSyncCommand._id == this._id && xSyncCommand._tag == this._tag;
   }

   @Override
   public int hashCode() {
      return this._id;
   }

   protected SyncCommand() {
   }

   public SyncCommand(int anId, int aTag) {
      this.setId(anId);
      this.setTag(aTag);
   }
}
