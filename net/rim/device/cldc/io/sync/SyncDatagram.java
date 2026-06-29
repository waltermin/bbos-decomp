package net.rim.device.cldc.io.sync;

import java.io.IOException;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public final class SyncDatagram extends SyncDatagramBase {
   private int _sessionId;
   private int _currentChangeListId;
   private int _expectedChangeListId;
   private int _currentSequenceNumber;
   private int _lastSequenceNumber;
   private int _initialCommandIdIndex;
   private int _sessionTimeout;
   private byte _verificationBits;
   private DataBuffer _commandsDataBuffer;
   private int _nextCommandDataBufferOffset;
   private int _commandsBufferLength;
   private SyncCommandsPool _syncCommandsPool;
   private int _availableBytes;
   private int _numberOfCommandsProccessed;
   public static final long POOL_GUID = 7926551755126522851L;
   public static final int PAYLOAD_SIZE = 32768;
   private static final int HEADER_VERFICATION_REQUEST = 1;
   private static final int HEADER_VERFICATION_RESPONSE = 3;
   private static final byte HEADER_END = 0;
   private static final byte HEADER_DEVICESESSION_ID = 1;
   private static final byte HEADER_SERVERSESSION_ID = 2;
   private static final byte HEADER_CURRENT_CHANGELIST_ID = 3;
   private static final byte HEADER_EXPECTED_CHANGELIST_ID = 4;
   private static final byte HEADER_CURRENT_SEQUENCE = 5;
   private static final byte HEADER_LAST_SEQUENCE = 6;
   private static final byte HEADER_VERFICATION = 7;
   private static final byte HEADER_SESSION_TIMEOUT = 8;

   public SyncDatagram() {
      this.reset();
   }

   public SyncDatagram(int protocolVersion) {
      this();
      this.setProtocolVersion(protocolVersion);
   }

   @Override
   public final void reset() {
      super.reset();
      this._sessionId = 0;
      this._currentChangeListId = 0;
      this._expectedChangeListId = 0;
      this._currentSequenceNumber = 0;
      this._lastSequenceNumber = 0;
      this._initialCommandIdIndex = 0;
      this._verificationBits = 0;
      this._numberOfCommandsProccessed = 0;
      this._sessionTimeout = 0;
      if (this._commandsDataBuffer != this) {
         if (this._commandsDataBuffer != null) {
            this._commandsDataBuffer.reset();
         }

         this._commandsDataBuffer = this;
      }

      this._commandsDataBuffer.rewind();
      this._commandsDataBuffer.setLength(0);
      this._commandsDataBuffer.trim();
      this._syncCommandsPool = null;
      this._nextCommandDataBufferOffset = 0;
      this._commandsBufferLength = 0;
      this._availableBytes = 32768;
   }

   public final boolean IsForVerificationRequest() {
      return this.isResponse() && (this._verificationBits & 1) == 1;
   }

   public final boolean IsForVerificationResponse() {
      return this.isResponse() && (this._verificationBits & 3) == 3;
   }

   public final boolean isForVerification() {
      return (this._verificationBits & 1) == 1;
   }

   public final void setAsVerificationRequest() {
      this._verificationBits = 1;
   }

   public final void setAsVerificationResponse() {
      this._verificationBits = 3;
   }

   public final void resetVerficationBits() {
      this._verificationBits = 0;
   }

   public final boolean isResponse() {
      return this._currentChangeListId == 0 && this._expectedChangeListId == 0;
   }

   public final void setSessionId(int sessionId) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getSessionId() {
      return this._sessionId;
   }

   public final void setCurrentChangeListId(int currentChangeListId) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getCurrentChangeListId() {
      return this._currentChangeListId;
   }

   public final void setExpectedChangeListId(int expectedChangeListId) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getExpectedChangeListId() {
      return this._expectedChangeListId;
   }

   public final void setCurrentSequenceNumber(int aSequenceNumber) {
      this._currentSequenceNumber = aSequenceNumber;
      this._initialCommandIdIndex = aSequenceNumber << 16;
   }

   public final int getCurrentSequenceNumber() {
      return this._currentSequenceNumber;
   }

   public final void setLastSequenceNumber(int lastSequenceNumber) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getLastSequenceNumber() {
      return this._lastSequenceNumber;
   }

   @Override
   public final int getAvailableBytes() {
      return this._availableBytes;
   }

   @Override
   public final boolean isValid() {
      return this._currentSequenceNumber >= 0 && this._currentSequenceNumber <= this._lastSequenceNumber && this._sessionId != 0;
   }

   @Override
   public final void writeTo(DataBuffer dout) {
      if (this._sessionId == 0) {
         throw new IllegalStateException();
      }

      dout.writeByte(this.getProtocolVersion());
      if (this._sessionId > 0) {
         TypeLengthEncoding.writeInt(dout, 1, this._sessionId);
      } else {
         TypeLengthEncoding.writeInt(dout, 2, this._sessionId * -1);
      }

      if (this.isForVerification()) {
         TypeLengthEncoding.writeBoolean(dout, 7, this.IsForVerificationResponse());
         dout.writeByte(0);
      } else {
         if (this._currentChangeListId != 0) {
            TypeLengthEncoding.writeInt(dout, 3, this._currentChangeListId);
         }

         if (this._expectedChangeListId != 0) {
            TypeLengthEncoding.writeInt(dout, 4, this._expectedChangeListId);
         }

         if (this._currentSequenceNumber != 0) {
            TypeLengthEncoding.writeInt(dout, 5, this._currentSequenceNumber);
         }

         if (this._lastSequenceNumber != 0) {
            TypeLengthEncoding.writeInt(dout, 6, this._lastSequenceNumber);
         }

         if (this._sessionTimeout != 0) {
            TypeLengthEncoding.writeInt(dout, 8, this._sessionTimeout);
         }

         dout.writeByte(0);
         this.writeCommandsTo(dout);
      }
   }

   @Override
   public final void readFrom(DataBuffer din) throws IOException {
      boolean xEndOfHeaderFound = false;

      while (!xEndOfHeaderFound) {
         int xTag = TypeLengthEncoding.readTag(din);
         switch (xTag) {
            case -1:
               TypeLengthEncoding.skipValue(din);
               break;
            case 0:
               xEndOfHeaderFound = true;
               break;
            case 1:
            default:
               this._sessionId = TypeLengthEncoding.readInt(din);
               break;
            case 2:
               this._sessionId = TypeLengthEncoding.readInt(din);
               if (this._sessionId > 0) {
                  this._sessionId *= -1;
               }
               break;
            case 3:
               this._currentChangeListId = TypeLengthEncoding.readInt(din);
               break;
            case 4:
               this._expectedChangeListId = TypeLengthEncoding.readInt(din);
               break;
            case 5:
               this.setCurrentSequenceNumber(TypeLengthEncoding.readInt(din));
               break;
            case 6:
               this._lastSequenceNumber = TypeLengthEncoding.readInt(din);
               break;
            case 7:
               this._verificationBits = (byte)(TypeLengthEncoding.readBoolean(din) ? 3 : 1);
               break;
            case 8:
               this._sessionTimeout = TypeLengthEncoding.readInt(din);
         }
      }

      if (!xEndOfHeaderFound) {
         throw new IOException();
      }

      this._commandsDataBuffer = din;
      this._nextCommandDataBufferOffset = din.getArrayPosition();
      this._commandsBufferLength = din.available();
   }

   public final boolean isSessionTimeoutProvided() {
      return this._sessionTimeout != 0;
   }

   public final void setSessionTimeout(int aSessionTimeout) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getSessionTimeout() {
      return this._sessionTimeout;
   }

   public final boolean couldAddSyncCommand(SyncCommand aSyncCommand) {
      return this._availableBytes >= aSyncCommand.size();
   }

   public final boolean couldAddSyncCommands(SyncCommand aSyncCommand, SyncCommand bSyncCommand) {
      return this._availableBytes >= aSyncCommand.size() + bSyncCommand.size();
   }

   public final void addCommand(SyncCommand aSyncCommand) {
      aSyncCommand.setId(this._initialCommandIdIndex++);

      try {
         aSyncCommand.writeTo(this._commandsDataBuffer);
         this._availableBytes = this._availableBytes - aSyncCommand.size();
         this._numberOfCommandsProccessed++;
      } finally {
         return;
      }
   }

   public final int getNumberOfSyncCommandsProccessed() {
      return this._numberOfCommandsProccessed;
   }

   public final void checkInSyncCommand(SyncCommand aSynCommand) {
      if (aSynCommand != null) {
         if (this._syncCommandsPool == null) {
            this._syncCommandsPool = SyncCommandsPool.getSingletonInstance(this.getSid());
         }

         this._syncCommandsPool.checkIn(aSynCommand);
      }
   }

   public final SyncCommand checkOutSyncCommand() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/cldc/io/sync/SyncDatagram._syncCommandsPool Lnet/rim/device/cldc/io/sync/SyncCommandsPool;
      // 04: ifnonnull 12
      // 07: aload 0
      // 08: aload 0
      // 09: invokevirtual net/rim/device/cldc/io/sync/SyncDatagramBase.getSid ()J
      // 0c: invokestatic net/rim/device/cldc/io/sync/SyncCommandsPool.getSingletonInstance (J)Lnet/rim/device/cldc/io/sync/SyncCommandsPool;
      // 0f: putfield net/rim/device/cldc/io/sync/SyncDatagram._syncCommandsPool Lnet/rim/device/cldc/io/sync/SyncCommandsPool;
      // 12: aload 0
      // 13: getfield net/rim/device/cldc/io/sync/SyncDatagram._commandsDataBuffer Lnet/rim/device/api/util/DataBuffer;
      // 16: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 19: astore 1
      // 1a: aload 0
      // 1b: getfield net/rim/device/cldc/io/sync/SyncDatagram._commandsDataBuffer Lnet/rim/device/api/util/DataBuffer;
      // 1e: aload 1
      // 1f: aload 0
      // 20: getfield net/rim/device/cldc/io/sync/SyncDatagram._nextCommandDataBufferOffset I
      // 23: aload 0
      // 24: getfield net/rim/device/cldc/io/sync/SyncDatagram._commandsBufferLength I
      // 27: invokevirtual net/rim/device/api/util/DataBuffer.setData ([BII)V
      // 2a: aload 0
      // 2b: getfield net/rim/device/cldc/io/sync/SyncDatagram._commandsDataBuffer Lnet/rim/device/api/util/DataBuffer;
      // 2e: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 31: ifle ae
      // 34: aload 0
      // 35: getfield net/rim/device/cldc/io/sync/SyncDatagram._commandsDataBuffer Lnet/rim/device/api/util/DataBuffer;
      // 38: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 3b: istore 2
      // 3c: aload 0
      // 3d: getfield net/rim/device/cldc/io/sync/SyncDatagram._syncCommandsPool Lnet/rim/device/cldc/io/sync/SyncCommandsPool;
      // 40: iload 2
      // 41: invokevirtual net/rim/device/cldc/io/sync/SyncCommandsPool.checkOut (I)Lnet/rim/device/cldc/io/sync/SyncCommand;
      // 44: astore 3
      // 45: aload 0
      // 46: getfield net/rim/device/cldc/io/sync/SyncDatagram._nextCommandDataBufferOffset I
      // 49: istore 4
      // 4b: aload 0
      // 4c: getfield net/rim/device/cldc/io/sync/SyncDatagram._commandsDataBuffer Lnet/rim/device/api/util/DataBuffer;
      // 4f: invokestatic net/rim/device/internal/synchronization/ota/util/LengthEncoding.read (Ljava/io/DataInput;)I
      // 52: istore 5
      // 54: aload 0
      // 55: aload 0
      // 56: getfield net/rim/device/cldc/io/sync/SyncDatagram._nextCommandDataBufferOffset I
      // 59: aload 0
      // 5a: getfield net/rim/device/cldc/io/sync/SyncDatagram._commandsDataBuffer Lnet/rim/device/api/util/DataBuffer;
      // 5d: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 60: iadd
      // 61: putfield net/rim/device/cldc/io/sync/SyncDatagram._nextCommandDataBufferOffset I
      // 64: aload 0
      // 65: getfield net/rim/device/cldc/io/sync/SyncDatagram._commandsDataBuffer Lnet/rim/device/api/util/DataBuffer;
      // 68: aload 1
      // 69: aload 0
      // 6a: getfield net/rim/device/cldc/io/sync/SyncDatagram._nextCommandDataBufferOffset I
      // 6d: iload 5
      // 6f: invokevirtual net/rim/device/api/util/DataBuffer.setData ([BII)V
      // 72: aload 0
      // 73: aload 0
      // 74: getfield net/rim/device/cldc/io/sync/SyncDatagram._nextCommandDataBufferOffset I
      // 77: iload 5
      // 79: iadd
      // 7a: putfield net/rim/device/cldc/io/sync/SyncDatagram._nextCommandDataBufferOffset I
      // 7d: aload 3
      // 7e: aload 0
      // 7f: getfield net/rim/device/cldc/io/sync/SyncDatagram._commandsDataBuffer Lnet/rim/device/api/util/DataBuffer;
      // 82: invokevirtual net/rim/device/cldc/io/sync/SyncCommand.readParametersFrom (Lnet/rim/device/api/util/DataBuffer;)V
      // 85: aload 0
      // 86: aload 0
      // 87: getfield net/rim/device/cldc/io/sync/SyncDatagram._commandsBufferLength I
      // 8a: aload 0
      // 8b: getfield net/rim/device/cldc/io/sync/SyncDatagram._nextCommandDataBufferOffset I
      // 8e: iload 4
      // 90: isub
      // 91: isub
      // 92: putfield net/rim/device/cldc/io/sync/SyncDatagram._commandsBufferLength I
      // 95: aload 3
      // 96: aload 0
      // 97: aload 0
      // 98: getfield net/rim/device/cldc/io/sync/SyncDatagram._initialCommandIdIndex I
      // 9b: dup_x1
      // 9c: bipush 1
      // 9d: iadd
      // 9e: putfield net/rim/device/cldc/io/sync/SyncDatagram._initialCommandIdIndex I
      // a1: invokevirtual net/rim/device/cldc/io/sync/SyncCommand.setId (I)V
      // a4: aload 3
      // a5: areturn
      // a6: astore 1
      // a7: aconst_null
      // a8: areturn
      // a9: astore 1
      // aa: aload 1
      // ab: invokestatic net/rim/device/internal/synchronization/ota/api/Logger.logErrorMessage (Ljava/lang/Object;)V
      // ae: aconst_null
      // af: areturn
      // try (0 -> 84): 85 null
      // try (0 -> 84): 88 null
   }

   public final int getNumberOfCommands() {
      return 0;
   }

   private final void writeCommandsTo(DataBuffer dout) {
      this._commandsDataBuffer.trim();
      dout.write(this._commandsDataBuffer.getArray());
   }
}
