package net.rim.wica.transport.internal.message;

import net.rim.wica.transport.internal.message.commonheader.CommonHeaderV1_1;
import net.rim.wica.transport.internal.message.data.DataStreamV1_0;
import net.rim.wica.transport.internal.message.messageheader.MessageHeaderV1_1;
import net.rim.wica.transport.message.TransportMessageException;
import net.rim.wica.transport.message.TransportMessageV2;
import net.rim.wica.transport.message.commonheader.CommonHeaderV1;
import net.rim.wica.transport.message.data.DataStreamV1;
import net.rim.wica.transport.message.messageheader.MessageHeaderV1;
import net.rim.wica.transport.util.CompressedBuffer;

public class TransportMessageV2_0 implements TransportMessageV2 {
   private CommonHeaderV1_1 _commonHeader;
   private MessageHeaderV1_1 _messageHeader;
   private DataStreamV1_0 _dataStream;
   private CompressedBuffer _bundle;
   private TransportMessageV2[] _toBundle;
   private int _bundlePayloadLength;

   int loadPayload(CompressedBuffer payload, boolean copy) {
      this._messageHeader = new MessageHeaderV1_1();
      this._messageHeader.deserialize(payload);
      int dataLength = this._messageHeader.getMessageLength();
      if (payload.available() < dataLength) {
         throw new TransportMessageException(0);
      } else if (copy) {
         this._dataStream = new DataStreamV1_0(dataLength);
         this._dataStream.copy(payload, payload.cursor(), dataLength, false);
         return dataLength;
      } else {
         this._dataStream = new DataStreamV1_0(payload.getBuffer(), payload.cursor(), dataLength);
         return dataLength;
      }
   }

   public int serializePayload(CompressedBuffer buffer) {
      int offset = buffer.cursor();
      if (this._commonHeader.isBundle()) {
         this.serializeBundle(buffer);
      } else {
         this._messageHeader.setMessageLength(this._dataStream.getLength());
         this._messageHeader.serialize(buffer);
         buffer.copy(this._dataStream, this._dataStream.start(), this._dataStream.getLength(), true);
      }

      return buffer.cursor() - offset;
   }

   TransportMessageV2[] getPayloadsToBundle() {
      return this._toBundle;
   }

   CompressedBuffer getBundledPayload() {
      return this._bundle;
   }

   @Override
   public DataStreamV1 getDataStream() {
      return this._dataStream == null ? new DataStreamV1_0(0) : this._dataStream;
   }

   @Override
   public MessageHeaderV1 getMessageHeader() {
      if (this._messageHeader == null) {
         this._messageHeader = new MessageHeaderV1_1();
      }

      return this._messageHeader;
   }

   @Override
   public int getMaxBundleSize() {
      return this._commonHeader.getMaxBundleSize();
   }

   @Override
   public TransportMessageV2 bundle(TransportMessageV2[] messages) {
      if (messages == null) {
         throw new Object();
      } else if (messages.length == 0) {
         return null;
      } else {
         return messages.length == 1 ? messages[0] : new TransportMessageV2_0(messages);
      }
   }

   @Override
   public TransportMessageV2[] debundle() {
      if (!this._commonHeader.isBundle()) {
         return new TransportMessageV2[]{this};
      }

      if (this._toBundle != null) {
         return this._toBundle;
      }

      int bundleSize = this._commonHeader.getMessageCount();
      TransportMessageV2[] messages = new TransportMessageV2[bundleSize];
      int bytesRead = 0;

      for (int i = 0; i < bundleSize; i++) {
         TransportMessageV2_0 newMessage = new TransportMessageV2_0();
         bytesRead = newMessage.loadPayload(this._bundle, true);
         CommonHeaderV1_1 commonHeader = new CommonHeaderV1_1(this._commonHeader);
         commonHeader.setMessageCount(1);
         newMessage.setCommonHeader(commonHeader);
         messages[i] = newMessage;
         this._bundle.forward(bytesRead);
      }

      return messages;
   }

   @Override
   public CommonHeaderV1 getCommonHeader() {
      return this._commonHeader;
   }

   @Override
   public byte[] serialize() {
      CompressedBuffer buffer = new CompressedBuffer(this._commonHeader.getHeaderLength() + this.getPayloadLength());
      this._commonHeader.serialize(buffer);
      this.serializePayload(buffer);
      buffer.trimToSize();
      return buffer.getBuffer();
   }

   @Override
   public TransportMessageV2 cloneMessage() {
      return new TransportMessageV2_0(this);
   }

   @Override
   public int size() {
      return this._commonHeader.getHeaderLength() + this.getPayloadLength();
   }

   TransportMessageV2_0(TransportMessageV2_0 clone) {
      this._commonHeader = new CommonHeaderV1_1((CommonHeaderV1_1)clone.getCommonHeader());
      if (this._commonHeader.isBundle()) {
         this._bundle = clone.getBundledPayload();
         this._toBundle = clone.getPayloadsToBundle();
      } else {
         this._messageHeader = new MessageHeaderV1_1((MessageHeaderV1_1)clone.getMessageHeader());
         DataStreamV1_0 dataStream = (DataStreamV1_0)clone.getDataStream();
         this._dataStream = new DataStreamV1_0(dataStream.getLength());
         this._dataStream.copy(dataStream, dataStream.start(), dataStream.getLength(), false);
      }
   }

   TransportMessageV2_0() {
   }

   private void prepareBundle(TransportMessageV2[] bundle) {
      this._toBundle = bundle;
      TransportMessageV2 next = null;
      MessageHeaderV1_1 messageHeader = null;
      int messageCount = 0;

      for (int i = this._toBundle.length - 1; i >= 0; i--) {
         next = this._toBundle[i];
         if (next == null) {
            throw new Object();
         }

         if (!(next instanceof TransportMessageV2_0)) {
            throw new TransportMessageException(100);
         }

         messageCount += next.getCommonHeader().getMessageCount();
         messageHeader = (MessageHeaderV1_1)next.getMessageHeader();
         messageHeader.setMessageLength(((CompressedBuffer)next.getDataStream()).getLength());
         this._bundlePayloadLength = this._bundlePayloadLength + messageHeader.getMessageLength();
      }

      CommonHeaderV1_1 clone = (CommonHeaderV1_1)next.getCommonHeader();
      if (messageCount > clone.getMaxBundleSize()) {
         throw new TransportMessageException(103);
      }

      this._commonHeader = new CommonHeaderV1_1((CommonHeaderV1_1)next.getCommonHeader());
      this._commonHeader.setMessageCount(messageCount);
   }

   private int getPayloadLength() {
      if (this._commonHeader.isBundle()) {
         return this._toBundle != null ? this._bundlePayloadLength : this._bundle.available();
      }

      this._messageHeader.setMessageLength(this._dataStream.getLength());
      return this._messageHeader.getMessageLength();
   }

   public TransportMessageV2_0(int capacity) {
      this._commonHeader = new CommonHeaderV1_1();
      this._commonHeader.setVersion(2);
      this._messageHeader = new MessageHeaderV1_1();
      this._dataStream = capacity < 0 ? new DataStreamV1_0() : new DataStreamV1_0(capacity);
   }

   private void serializeBundle(CompressedBuffer buffer) {
      if (this._toBundle != null) {
         int bundleSize = this._toBundle.length;

         for (int i = 0; i < bundleSize; i++) {
            TransportMessageV2 next = this._toBundle[i];
            ((MessageHeaderV1_1)next.getMessageHeader()).serialize(buffer);
            CompressedBuffer data = (CompressedBuffer)next.getDataStream();
            buffer.copy(data, data.start(), data.getLength(), true);
         }
      } else {
         buffer.copy(this._bundle, this._bundle.cursor(), this._bundle.available(), false);
      }
   }

   TransportMessageV2_0(TransportMessageV2[] bundle) {
      this.prepareBundle(bundle);
   }

   public TransportMessageV2_0(byte[] buffer) {
      CompressedBuffer message = new CompressedBuffer(buffer);
      this._commonHeader = new CommonHeaderV1_1();
      this._commonHeader.deserialize(message);
      if (this._commonHeader.isBundle()) {
         this._bundle = message;
      } else {
         this.loadPayload(message, false);
      }
   }

   private void setCommonHeader(CommonHeaderV1_1 commonHeader) {
      this._commonHeader = commonHeader;
   }
}
