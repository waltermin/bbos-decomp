package net.rim.wica.transport.internal.message;

import net.rim.wica.transport.internal.message.commonheader.CommonHeaderV1_0;
import net.rim.wica.transport.internal.message.data.DataStreamV1_0;
import net.rim.wica.transport.internal.message.messageheader.MessageHeaderV1_0;
import net.rim.wica.transport.message.TransportMessageException;
import net.rim.wica.transport.message.TransportMessageV1;
import net.rim.wica.transport.message.commonheader.CommonHeaderV1;
import net.rim.wica.transport.message.data.DataStreamV1;
import net.rim.wica.transport.message.messageheader.MessageHeaderV1;
import net.rim.wica.transport.util.CompressedBuffer;

public class TransportMessageV1_0 implements TransportMessageV1 {
   private CommonHeaderV1_0 _commonHeader;
   private MessageHeaderV1_0 _messageHeader;
   private DataStreamV1_0 _dataStream;
   private CompressedBuffer _bundle;
   private TransportMessageV1[] _toBundle;
   private int _bundlePayloadLength;

   int loadPayload(CompressedBuffer payload, boolean copy) {
      this._messageHeader = new MessageHeaderV1_0();
      int bytesRead = this._messageHeader.deserialize(payload);
      int dataLength = this._messageHeader.getMessageLength() - bytesRead;
      if (payload.available() < dataLength) {
         throw new TransportMessageException(0);
      }

      if (copy) {
         this._dataStream = new DataStreamV1_0(dataLength);
         this._dataStream.copy(payload, payload.cursor(), dataLength, false);
      } else {
         this._dataStream = new DataStreamV1_0(payload.getBuffer(), payload.cursor(), dataLength);
      }

      return dataLength;
   }

   void setCommonHeader(CommonHeaderV1_0 commonHeader) {
      this._commonHeader = commonHeader;
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

   void setMessageHeader(MessageHeaderV1_0 messageHeader) {
      this._messageHeader = messageHeader;
   }

   public byte[] serializePayload() {
      CompressedBuffer buffer = new CompressedBuffer(this.getPayloadLength());
      this.serializePayload(buffer);
      buffer.trimToSize();
      return buffer.getBuffer();
   }

   public int getPayloadLength() {
      if (this._commonHeader.isBundle()) {
         return this._toBundle != null ? this._bundlePayloadLength : this._bundle.available();
      }

      this._messageHeader.setMessageLength(this._dataStream.getLength());
      return this._messageHeader.getMessageLength();
   }

   CompressedBuffer getBundledPayload() {
      return this._bundle;
   }

   TransportMessageV1[] getPayloadsToBundle() {
      return this._toBundle;
   }

   @Override
   public int getMaxBundleSize() {
      return this._commonHeader.getMaxBundleSize();
   }

   @Override
   public TransportMessageV1 bundle(TransportMessageV1[] messages) {
      if (messages == null) {
         throw new Object();
      } else if (messages.length == 0) {
         return null;
      } else {
         return messages.length == 1 ? messages[0] : new TransportMessageV1_0(messages);
      }
   }

   @Override
   public TransportMessageV1[] debundle() {
      if (!this._commonHeader.isBundle()) {
         return new TransportMessageV1[]{this};
      }

      if (this._toBundle != null) {
         return this._toBundle;
      }

      int bundleSize = this._commonHeader.getMessageCount();
      TransportMessageV1[] messages = new TransportMessageV1[bundleSize];
      int bytesRead = 0;

      for (int i = 0; i < bundleSize; i++) {
         TransportMessageV1_0 newMessage = new TransportMessageV1_0();
         bytesRead = newMessage.loadPayload(this._bundle, true);
         CommonHeaderV1_0 commonHeader = new CommonHeaderV1_0(this._commonHeader);
         commonHeader.setMessageCount(1);
         newMessage.setCommonHeader(commonHeader);
         messages[i] = newMessage;
         this._bundle.forward(bytesRead);
      }

      return messages;
   }

   @Override
   public DataStreamV1 getDataStream() {
      return this._dataStream == null ? new DataStreamV1_0(0) : this._dataStream;
   }

   @Override
   public MessageHeaderV1 getMessageHeader() {
      return this._messageHeader == null ? new MessageHeaderV1_0() : this._messageHeader;
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
   public TransportMessageV1 cloneMessage() {
      return new TransportMessageV1_0(this);
   }

   @Override
   public int size() {
      return this._commonHeader.getHeaderLength() + this.getPayloadLength();
   }

   TransportMessageV1_0() {
   }

   TransportMessageV1_0(TransportMessageV1_0 clone) {
      this._commonHeader = new CommonHeaderV1_0((CommonHeaderV1_0)clone.getCommonHeader());
      if (this._commonHeader.isBundle()) {
         this._bundle = clone.getBundledPayload();
         this._toBundle = clone.getPayloadsToBundle();
      } else {
         this._messageHeader = new MessageHeaderV1_0((MessageHeaderV1_0)clone.getMessageHeader());
         DataStreamV1_0 dataStream = (DataStreamV1_0)clone.getDataStream();
         this._dataStream = new DataStreamV1_0(dataStream.getLength());
         this._dataStream.copy(dataStream, dataStream.start(), dataStream.getLength(), false);
      }
   }

   public TransportMessageV1_0(byte[] buffer) {
      CompressedBuffer message = new CompressedBuffer(buffer);
      this._commonHeader = new CommonHeaderV1_0();
      this._commonHeader.deserialize(message);
      if (this._commonHeader.isBundle()) {
         this._bundle = message;
      } else {
         this.loadPayload(message, false);
      }
   }

   private void serializeBundle(CompressedBuffer buffer) {
      if (this._toBundle != null) {
         int bundleSize = this._toBundle.length;

         for (int i = 0; i < bundleSize; i++) {
            TransportMessageV1 next = this._toBundle[i];
            ((MessageHeaderV1_0)next.getMessageHeader()).serialize(buffer);
            CompressedBuffer data = (CompressedBuffer)next.getDataStream();
            buffer.copy(data, data.start(), data.getLength(), true);
         }
      } else {
         buffer.copy(this._bundle, this._bundle.cursor(), this._bundle.available(), true);
      }
   }

   TransportMessageV1_0(TransportMessageV1[] bundle) {
      this.prepareBundle(bundle);
   }

   public TransportMessageV1_0(int capacity) {
      this._commonHeader = new CommonHeaderV1_0();
      this._commonHeader.setVersion(1);
      this._messageHeader = new MessageHeaderV1_0();
      this._dataStream = capacity < 0 ? new DataStreamV1_0() : new DataStreamV1_0(capacity);
   }

   private void prepareBundle(TransportMessageV1[] bundle) {
      this._toBundle = bundle;
      TransportMessageV1 next = null;
      MessageHeaderV1_0 messageHeader = null;
      int messageCount = 0;

      for (int i = this._toBundle.length - 1; i >= 0; i--) {
         next = this._toBundle[i];
         if (next == null) {
            throw new Object();
         }

         if (!(next instanceof TransportMessageV1_0)) {
            throw new TransportMessageException(100);
         }

         messageCount += next.getCommonHeader().getMessageCount();
         messageHeader = (MessageHeaderV1_0)next.getMessageHeader();
         messageHeader.setMessageLength(((CompressedBuffer)next.getDataStream()).getLength());
         this._bundlePayloadLength = this._bundlePayloadLength + messageHeader.getMessageLength();
      }

      CommonHeaderV1_0 clone = (CommonHeaderV1_0)next.getCommonHeader();
      if (messageCount > clone.getMaxBundleSize()) {
         throw new TransportMessageException(103);
      }

      this._commonHeader = new CommonHeaderV1_0(clone);
      this._commonHeader.setMessageCount(messageCount);
   }
}
