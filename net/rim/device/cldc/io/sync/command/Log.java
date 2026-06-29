package net.rim.device.cldc.io.sync.command;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.sync.SyncCommand;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public final class Log extends SyncCommand {
   private String _logMessage;
   private int _logMessageId;

   public Log() {
      this.setTag(19);
   }

   public final void setLogMessage(String logMessage) {
      this._logMessage = logMessage;
   }

   public final String getLogMessage() {
      return this._logMessage;
   }

   public final void setLogMessageId(int logMessageId) {
      this._logMessageId = logMessageId;
   }

   public final int getLogMessageId() {
      return this._logMessageId;
   }

   @Override
   public final boolean isValid() {
      return true;
   }

   @Override
   public final void readParametersFrom(DataBuffer dins) {
      try {
         while (dins.available() > 0) {
            int xTag = TypeLengthEncoding.readTag(dins);
            switch (xTag) {
               case 102:
                  TypeLengthEncoding.skipValue(dins);
                  break;
               case 103:
                  this._logMessageId = TypeLengthEncoding.readInt(dins);
                  break;
               case 104:
               default:
                  this._logMessage = TypeLengthEncoding.readString(dins);
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public final void writeParametersTo(DataBuffer dout) {
      if (this._logMessageId != 0) {
         TypeLengthEncoding.writeInt(dout, 103, this._logMessageId);
      }

      if (this._logMessage != null) {
         TypeLengthEncoding.writeString(dout, 104, this._logMessage);
      }
   }

   @Override
   public final int size() {
      int xSize = super.size();
      if (this._logMessageId != 0) {
         xSize += 2 + TypeLengthEncoding.getNumberOfBytesRequiredFor(this._logMessageId);
      }

      if (this._logMessage != null) {
         xSize += 2 + this._logMessage.length();
      }

      return xSize;
   }

   @Override
   public final void reset() {
      super.reset();
      this._logMessageId = 0;
      this._logMessage = null;
   }
}
