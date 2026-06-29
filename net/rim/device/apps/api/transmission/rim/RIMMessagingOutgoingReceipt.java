package net.rim.device.apps.api.transmission.rim;

import java.io.IOException;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.utility.serialization.Converter;

public final class RIMMessagingOutgoingReceipt extends RIMMessagingMessage {
   private DataBuffer _buffer = new DataBuffer();

   public RIMMessagingOutgoingReceipt() {
      this._buffer.writeByte(2);
      this._buffer.writeByte(16);
      super._headerParameters.setDataBuffer(this._buffer);
   }

   @Override
   public final DataBuffer write() throws IOException {
      this._buffer.writeByte(0);
      int attachmentCount = this.getAttachmentCount();
      if (attachmentCount <= 0) {
         throw new IOException();
      }

      DataBuffer attachmentBuffer = new DataBuffer();
      this.writeDSNAttachment(attachmentBuffer);
      this._buffer.writeByte(1);
      this._buffer.writeByte(2);
      this._buffer.writeByte(1);
      this._buffer.writeByte(2);
      this._buffer.writeByte(0);
      DataBuffer textBuffer = new DataBuffer();
      if (this.getText() != null) {
         this.writeMessageText(textBuffer);
      }

      int length = textBuffer.getPosition() + attachmentBuffer.getPosition();
      this._buffer.writeCompressedInt(length);
      if (textBuffer.getPosition() > 0) {
         this._buffer.write(textBuffer.getArray(), 0, textBuffer.getPosition());
      }

      this._buffer.write(attachmentBuffer.getArray(), 0, attachmentBuffer.getPosition());
      return this._buffer;
   }

   private final void writeMessageText(DataBuffer dataBuffer) {
      byte[] bytes = null;
      Converter converter = null;
      String type = this.getTextType();
      Object text = this.getText();
      Parameters parameters = null;
      if (type == null) {
         type = CMIMEUtilities.getTextContentType();
      }

      dataBuffer.writeByte(1);
      CMIMEContentType.encodeType(type, dataBuffer, false);
      if ((parameters = (Parameters)this.getTextParameters()) != null) {
         DataBuffer parametersDataBuffer = parameters.getDataBuffer();
         if (parametersDataBuffer != null) {
            parametersDataBuffer.trim();
            dataBuffer.write(parametersDataBuffer.getArray());
         }
      }

      dataBuffer.writeByte(0);
      if (text != null) {
         converter = CMIMEConverterRegistry.getDefaultConverter(type);
         bytes = converter.convert(text, type);
         if (bytes != null) {
            dataBuffer.writeByteArray(bytes);
         } else {
            dataBuffer.writeCompressedInt(0);
         }
      } else {
         dataBuffer.writeCompressedInt(0);
      }
   }

   private final void writeDSNAttachment(DataBuffer dataBuffer) {
      byte[] bytes = null;
      Converter converter = null;
      String type = null;
      Object attachment = null;
      type = this.getAttachmentType(0);
      if (type != null) {
         dataBuffer.writeByte(1);
         CMIMEContentType.encodeType(type, dataBuffer);
         dataBuffer.writeByte(0);
         bytes = null;
         attachment = this.getAttachment(0);
         if (attachment != null) {
            if (!(attachment instanceof byte[])) {
               converter = CMIMEConverterRegistry.getDefaultConverter(type);
               bytes = converter.convert(attachment, null);
            } else {
               bytes = (byte[])attachment;
            }
         }

         if (bytes == null) {
            dataBuffer.writeCompressedInt(0);
         } else {
            dataBuffer.writeByteArray(bytes);
         }
      }
   }
}
