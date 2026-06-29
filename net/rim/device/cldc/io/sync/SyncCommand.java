package net.rim.device.cldc.io.sync;

import java.io.IOException;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.synchronization.ota.util.LengthEncoding;
import net.rim.device.internal.synchronization.ota.util.ReusableObject;

public class SyncCommand implements ReusableObject {
   private int _tag;
   private int _id;
   protected static final IOException NotSupportedVersionException = (IOException)(new Object("NSV"));
   public static final byte UNKNOWEN;
   public static final byte ADD;
   public static final byte DELETE;
   public static final byte UPDATE;
   public static final byte REPLACE;
   public static final byte GET;
   public static final byte RECORD;
   public static final byte USE;
   public static final byte STATUS;
   public static final byte GET_SYNC_CONFIGURATION;
   public static final byte SYNC_CONFIGURATION;
   public static final byte INITIATESYNC;
   public static final byte SUSPEND;
   public static final byte RESUME;
   public static final byte GET_RECORDS_HASHES;
   public static final byte RECORDS_HASHES;
   public static final byte UPDATE_SYNC_CONFIGURATION;
   public static final byte LOG;

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
