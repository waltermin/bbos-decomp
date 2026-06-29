package net.rim.device.apps.api.transmission.rim;

import java.util.Vector;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.OutgoingMessage;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.utility.serialization.Converter;

public final class RIMMessagingOutgoingMessage extends RIMMessagingMessage implements OutgoingMessage {
   private Vector _textSuffixObjects;
   private Vector _textSuffixTypes;
   private DataBuffer _buffer;
   private byte _messageType = 32;

   public final void addSuffixText(String aString) {
      this.addSuffixText(aString, null);
   }

   public final void addSuffixText(Object textObject, String textTypeString) {
      if (this._textSuffixObjects == null) {
         this._textSuffixObjects = (Vector)(new Object());
         this._textSuffixTypes = (Vector)(new Object());
      }

      this._textSuffixObjects.addElement(textObject);
      this._textSuffixTypes.addElement(textTypeString != null ? textTypeString : CMIMEUtilities.getTextContentType());
   }

   public final void setMessageType(byte aByte) {
      this._messageType = aByte;
   }

   public final void setNNEPassword(String password) {
      if (password != null && password.length() > 0) {
         super._headerParameters.add((byte)113, password.getBytes());
      }
   }

   @Override
   public final void setTextAndType(Object textObject, Object context) {
      this.setText(
         textObject,
         null,
         ContextObject.getFlag(context, 94)
            ? CMIMEUtilities.getTextContentType(CMIMEUtilities.getEncodings(), false)
            : CMIMEUtilities.getTextContentType((ServiceRecord)ContextObject.get(context, -6095803566992128485L))
      );
   }

   @Override
   public final DataBuffer write() {
      this.writeMessageType(this._buffer);
      this._buffer.writeByte(0);
      int attachmentCount = this.getAttachmentCount();
      if (attachmentCount > 0) {
         this._buffer.writeByte(1);
         this._buffer.writeByte(2);
         this._buffer.writeByte(1);
         this._buffer.writeByte(1);
         this._buffer.writeByte(0);
      }

      DataBuffer textBuffer = (DataBuffer)(new Object());
      this.writeMessageText(textBuffer);
      if (attachmentCount > 0) {
         DataBuffer attachmentBuffer = (DataBuffer)(new Object());
         this.writeMessageAttachments(attachmentBuffer);
         int length = textBuffer.getPosition() + attachmentBuffer.getPosition();
         this._buffer.writeCompressedInt(length);
         this._buffer.write(textBuffer.getArray(), 0, textBuffer.getPosition());
         this._buffer.write(attachmentBuffer.getArray(), 0, attachmentBuffer.getPosition());
      } else {
         this._buffer.write(textBuffer.getArray(), 0, textBuffer.getPosition());
      }

      return this._buffer;
   }

   private final void writeMessageType(DataBuffer dataBuffer) {
      dataBuffer.writeByte(-10);
      dataBuffer.writeCompressedInt(1);
      byte localType = 2;
      switch (this._messageType) {
         case 16:
            localType = 4;
            break;
         case 32:
            localType = 1;
      }

      dataBuffer.writeByte(localType);
      if (this._messageType != 32 && super._headerParameters.has((byte)-14)) {
         dataBuffer.writeByte(-13);
         dataBuffer.writeCompressedInt(4);
         dataBuffer.writeByte(0);
         dataBuffer.writeByte(0);
         dataBuffer.writeByte(0);
         localType = 0;
         switch (this._messageType) {
            case 0:
               localType = 8;
               localType = (byte)(localType | 7);
               break;
            case 2:
               localType = 1;
               break;
            case 4:
               localType = 4;
               break;
            case 8:
               localType = 5;
               break;
            case 16:
               localType = 3;
         }

         dataBuffer.write(localType);
      }
   }

   private final void writeMessageText(DataBuffer dataBuffer) {
      byte[] bytes = null;
      Converter converter = null;
      String type = this.getTextType();
      Object text = this.getText();
      Parameters parameters = null;
      boolean toBeEncoded = false;
      DataBuffer localDataBuffer = null;
      String messageContentType = null;
      byte forcedEncoding = -1;
      if (super._isEncoded) {
         if (this._textSuffixObjects != null && this._textSuffixObjects.size() > 0) {
            int suffixCount = this._textSuffixObjects.size();

            for (int index = 0; index < suffixCount; index++) {
               String suffixContentType = (String)this._textSuffixTypes.elementAt(index);
               forcedEncoding = CMIMEUtilities.parseEncoding(suffixContentType);
               if (forcedEncoding != -1) {
                  Object data = this._textSuffixObjects.elementAt(index);
                  if (data != null && data instanceof Object) {
                     if (!ConverterUtilities.isIntellisyncCompatible((String)data)) {
                        messageContentType = suffixContentType;
                        toBeEncoded = true;
                        break;
                     }

                     this._textSuffixTypes.setElementAt(CMIMEUtilities.getTextContentType(), index);
                     forcedEncoding = -1;
                  }
               }
            }
         }

         boolean isEncodingHinted;
         boolean isAsciiTextMessage;
         isEncodingHinted = super._isEncoded && (super._encodingCode & 112) != 0;
         isAsciiTextMessage = text == null || text instanceof Object && StringUtilities.getCharacterSize((String)text) == 1;
         label146:
         if (isAsciiTextMessage && isEncodingHinted) {
            if (forcedEncoding == -1) {
               forcedEncoding = super._encodingCode;
               messageContentType = CMIMEUtilities.getTextContentType(forcedEncoding);
               toBeEncoded = true;
            }

            if (text != null) {
               if (!(text instanceof Object)) {
                  break label146;
               }

               if (((String)text).length() != 0) {
                  break label146;
               }
            }

            text = "";
         }

         if (isAsciiTextMessage && !isEncodingHinted) {
            type = messageContentType;
         } else if (type != null && text instanceof Object && StringUtilities.startsWithIgnoreCase(type, "text/plain", 1701707776)) {
            localDataBuffer = (DataBuffer)(new Object());
            toBeEncoded = CMIMEUtilities.addTextEncoded(localDataBuffer, (String)text, super._encodingCode, false, forcedEncoding);
            if (!toBeEncoded) {
               type = messageContentType;
               if (forcedEncoding != -1) {
                  toBeEncoded = true;
               }
            } else {
               forcedEncoding = -1;
            }
         } else if (type != null) {
            toBeEncoded = false;
            forcedEncoding = -1;
         }
      }

      if (type == null) {
         type = CMIMEUtilities.getTextContentType();
      }

      dataBuffer.writeByte(1);
      CMIMEContentType.encodeType(type, dataBuffer, toBeEncoded);
      if ((parameters = (Parameters)this.getTextParameters()) != null) {
         DataBuffer parametersDataBuffer = parameters.getDataBuffer();
         if (parametersDataBuffer != null) {
            parametersDataBuffer.trim();
            dataBuffer.write(parametersDataBuffer.getArray());
         }
      }

      dataBuffer.writeByte(0);
      if (this._textSuffixObjects != null && this._textSuffixObjects.size() > 0) {
         if (localDataBuffer == null) {
            localDataBuffer = (DataBuffer)(new Object());
            if (text != null) {
               this._textSuffixObjects.insertElementAt(text, 0);
               this._textSuffixTypes.insertElementAt(type, 0);
            }
         }

         if (forcedEncoding == -1) {
            messageContentType = type;
         }

         int suffixCount = this._textSuffixObjects.size();

         for (int index = 0; index < suffixCount; index++) {
            type = (String)this._textSuffixTypes.elementAt(index);
            converter = CMIMEConverterRegistry.getDefaultConverter(type);
            bytes = converter.convert(this._textSuffixObjects.elementAt(index), messageContentType);
            if (bytes != null && bytes.length > 0) {
               localDataBuffer.write(bytes);
            }
         }

         if (!toBeEncoded) {
            dataBuffer.writeCompressedInt(localDataBuffer.getPosition());
         } else {
            dataBuffer.writeCompressedInt(localDataBuffer.getPosition() + 1);
            dataBuffer.writeByte(forcedEncoding == -1 ? super._encodingCode : forcedEncoding);
         }

         dataBuffer.write(localDataBuffer.getArray(), 0, localDataBuffer.getPosition());
      } else if (text != null) {
         if (localDataBuffer == null) {
            converter = CMIMEConverterRegistry.getDefaultConverter(type);
            bytes = converter.convert(text, type);
         } else {
            bytes = localDataBuffer.getArray();
         }

         if (bytes != null) {
            int dataLength = localDataBuffer == null ? bytes.length : localDataBuffer.getPosition();
            if (!toBeEncoded) {
               dataBuffer.writeCompressedInt(dataLength);
            } else {
               dataBuffer.writeCompressedInt(dataLength + 1);
               dataBuffer.writeByte(super._encodingCode);
            }

            dataBuffer.write(bytes, 0, dataLength);
         } else {
            dataBuffer.writeCompressedInt(0);
         }
      } else {
         dataBuffer.writeCompressedInt(0);
      }
   }

   private final void writeMessageAttachments(DataBuffer dataBuffer) {
      byte[] bytes = null;
      Converter converter = null;
      String type = null;
      Object attachment = null;
      Parameters parameters = null;
      int attachmentCount = this.getAttachmentCount();

      for (int index = 0; index < attachmentCount; index++) {
         type = this.getAttachmentType(index);
         if (type != null) {
            dataBuffer.writeByte(1);
            CMIMEContentType.encodeType(type, dataBuffer);
            if ((parameters = (Parameters)this.getAttachmentParameters(index)) != null) {
               DataBuffer parametersDataBuffer = parameters.getDataBuffer();
               if (parametersDataBuffer != null) {
                  parametersDataBuffer.trim();
                  dataBuffer.write(parametersDataBuffer.getArray());
               }
            }

            dataBuffer.writeByte(0);
            bytes = null;
            attachment = this.getAttachment(index);
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

   public static final byte getRIMMessageType(byte messageTypeByte) {
      switch (messageTypeByte) {
         case 1:
         case 2:
         case 4:
         case 8:
            return 2;
         case 16:
            return 4;
         default:
            return 1;
      }
   }

   public RIMMessagingOutgoingMessage() {
      this._buffer = (DataBuffer)(new Object());
      this._buffer.writeByte(2);
      this._buffer.writeByte(16);
      super._headerParameters.setDataBuffer(this._buffer);
   }
}
