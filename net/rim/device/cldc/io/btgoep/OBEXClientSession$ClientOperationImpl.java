package net.rim.device.cldc.io.btgoep;

import java.io.IOException;
import net.rim.device.api.util.DataBuffer;

class OBEXClientSession$ClientOperationImpl extends OBEXSession$OperationImpl {
   private boolean _isPut;
   private boolean _abort;
   private boolean _isClosed;
   private final OBEXClientSession this$0;

   public OBEXClientSession$ClientOperationImpl(OBEXClientSession _1, boolean isPut, HeaderSetImpl headersToSend) {
      super(_1);
      this.this$0 = _1;
      super._receiveBuffer = new DataBuffer();
      super._sendBuffer = new DataBuffer();
      this._isPut = isPut;
      super._headersToSend = headersToSend;
   }

   private boolean writeRequest(boolean finalBit, HeaderSetImpl headers, DataBuffer bodyData, boolean endOfBody) {
      synchronized (this.this$0) {
         boolean dataRemaining = false;
         if (this._abort) {
            this.this$0._dout.reset();
            this.this$0._dout.writeByte(255);
            this.this$0._dout.writeShort(3);
            this.this$0._dout.flush();
            return false;
         }

         int length = 3;
         DataBuffer headerData = null;
         if (headers != null) {
            headerData = headers.getData();
            if (headerData != null) {
               length += headerData.getLength();
            }
         }

         int bodyDataLength = 0;
         if (bodyData != null) {
            bodyDataLength = bodyData.available();
            if (bodyDataLength != 0) {
               int lengthRemainingForBodyData = this.this$0._maxPacketSize - length - 3;
               if (bodyDataLength > lengthRemainingForBodyData) {
                  dataRemaining = true;
                  bodyDataLength = lengthRemainingForBodyData;
               }

               length += bodyDataLength + 3;
            }
         }

         if (bodyDataLength == 0 && endOfBody) {
            length += 3;
         }

         this.this$0._dout.reset();
         if (finalBit) {
            this.this$0._dout.writeByte(this._isPut ? 130 : 131);
         } else {
            this.this$0._dout.writeByte(this._isPut ? 2 : 3);
         }

         this.this$0._dout.writeShort(length);
         if (headerData != null) {
            headerData.read(this.this$0._dout);
         }

         if (bodyDataLength != 0) {
            if (endOfBody) {
               this.this$0._dout.writeByte(73);
            } else {
               this.this$0._dout.writeByte(72);
            }

            this.this$0._dout.writeShort(bodyDataLength + 3);
            bodyData.read(this.this$0._dout, bodyDataLength);
         } else if (endOfBody) {
            this.this$0._dout.writeByte(73);
            this.this$0._dout.writeShort(3);
         }

         this.this$0._dout.flush();
         return dataRemaining;
      }
   }

   @Override
   protected void doSendReceive(boolean dataToReceive, boolean operationComplete) {
      synchronized (this.this$0) {
         boolean dataToSend = false;
         if (super._sendBuffer.getLength() != 0) {
            dataToSend = true;
            super._sendBuffer.rewind();
         }

         do {
            boolean finalBit;
            if (!this._isPut) {
               finalBit = !dataToSend;
            } else {
               finalBit = operationComplete && !dataToSend;
            }

            if (dataToSend && super._headersToSend == null && !operationComplete && super._sendBuffer.available() < this.this$0._maxPacketSize - 3 - 3) {
               break;
            }

            boolean sendEndOfBody = finalBit && super._sendEndOfBody;
            int position = super._sendBuffer.getPosition();
            dataToSend = this.writeRequest(finalBit, super._headersToSend, super._sendBuffer, sendEndOfBody);
            super._receivedHeaders = this.this$0.readResponse();
            if (this.this$0.authenticateResponseRequired(super._receivedHeaders.getResponseCode(), super._headersToSend, super._receivedHeaders)) {
               super._sendBuffer.setPosition(position);
            } else {
               this.this$0.checkAuthenticateResponse(super._headersToSend, super._receivedHeaders);
               this.checkForIncomingData(super._receivedHeaders);
               super._headersToSend = null;
            }
         } while (dataToSend);

         super._sendBuffer.trimHead(false);
         super._sendBuffer.setPosition(super._sendBuffer.getLength());
      }
   }

   private void checkForIncomingData(HeaderSetImpl headers) {
      if (headers != null) {
         byte[] body = (byte[])headers.getHeader(72);
         if (body != null) {
            this.writeToReceiveBuffer(body);
         }

         body = (byte[])headers.getHeader(73);
         if (body != null) {
            this.writeToReceiveBuffer(body);
         }
      }
   }

   @Override
   public void abort() {
      synchronized (this.this$0) {
         if (!this._isClosed && this.this$0._operationInProgress) {
            this.this$0._dout.reset();
            this._abort = true;
            this.close();
         } else {
            throw new IOException("Operation is closed");
         }
      }
   }

   @Override
   public int getResponseCode() {
      synchronized (this.this$0) {
         if (this._isPut) {
            if (super._out != null) {
               super._out.close();
            }
         } else if (super._in != null) {
            super._in.close();
         }

         if (this._isClosed) {
            throw new IOException("Operation is closed");
         }

         if (super._receivedHeaders == null) {
            this.doSendReceive(false, true);
         }

         while (super._receivedHeaders.getResponseCode() == 144) {
            this.doSendReceive(false, true);
         }

         this.this$0._operationInProgress = false;
         return super._receivedHeaders.getResponseCode();
      }
   }

   @Override
   public void close() {
      synchronized (this.this$0) {
         if (!this._isClosed) {
            if (super._receivedHeaders == null) {
               this.doSendReceive(false, true);
            }

            while (super._receivedHeaders.getResponseCode() == 144) {
               this.doSendReceive(false, true);
            }

            this._isClosed = true;
            super.close();
         }
      }
   }
}
