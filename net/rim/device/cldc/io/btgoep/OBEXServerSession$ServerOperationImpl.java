package net.rim.device.cldc.io.btgoep;

import java.io.IOException;
import net.rim.device.api.util.DataBuffer;

class OBEXServerSession$ServerOperationImpl extends OBEXSession$OperationImpl {
   private boolean _clientAborted;
   private boolean _finalBitReceived;
   private final OBEXServerSession this$0;

   public OBEXServerSession$ServerOperationImpl(OBEXServerSession _1, boolean isPut, HeaderSetImpl requestHeaders) {
      super(_1);
      this.this$0 = _1;
      super._receivedHeaders = requestHeaders;
      super._receiveBuffer = new DataBuffer();
      super._sendBuffer = new DataBuffer();
      this.checkForIncomingData(requestHeaders);
   }

   void finalBitReceived() {
      this._finalBitReceived = true;
   }

   void clientAborted() {
      this._clientAborted = true;
      this.close();
   }

   void operationComplete(int responseCode) {
      synchronized (this.this$0) {
         if (!this._clientAborted) {
            if (super._out != null) {
               super._out.close();
            }

            this.doSendReceive(false, true);
            this.this$0.writeResponse(responseCode, super._headersToSend, null, super._sendEndOfBody);
         }
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

         while (
            (dataToSend || dataToReceive || operationComplete && !this._finalBitReceived)
               && (super._headersToSend != null || !dataToSend || this._finalBitReceived || super._sendBuffer.available() >= this.this$0._maxPacketSize - 3 - 3)
         ) {
            if (super._headersToSend != null) {
               Object challenge = super._headersToSend.getHeader(77);
               if (challenge != null) {
                  HeaderSetImpl challengeHeaders = new HeaderSetImpl();
                  challengeHeaders.setHeader(77, challenge);
                  dataToSend = this.this$0.writeResponse(193, challengeHeaders, null, false);
                  HeaderSetImpl returnHeaders = this.this$0.handleNextRequest(this);
                  if (returnHeaders == null || returnHeaders.getHeader(78) == null) {
                     this.this$0._handler.onAuthenticationFailure(null);
                     return;
                  }

                  byte[] responseArray = (byte[])returnHeaders.getHeader(78);
                  if (!this.this$0.checkAuthResponse(responseArray, super._headersToSend._nonce)) {
                     this.this$0._handler.onAuthenticationFailure(this.this$0.getUsername(responseArray));
                     return;
                  }

                  super._receivedHeaders = returnHeaders;
                  super._headersToSend.removeHeader(77);
               }
            }

            if (super._receivedHeaders != null && super._receivedHeaders.getHeader(77) != null) {
               byte[] requestChallengeArray = (byte[])super._receivedHeaders.getHeader(77);
               if (super._headersToSend == null) {
                  super._headersToSend = new HeaderSetImpl();
               }

               super._headersToSend.setHeader(78, this.this$0.getAuthResponse(requestChallengeArray));
            }

            dataToSend = this.this$0.writeResponse(144, super._headersToSend, super._sendBuffer, false);
            super._headersToSend = null;
            HeaderSetImpl requestHeaders = this.this$0.handleNextRequest(this);
            super._receivedHeaders = requestHeaders;
            if (super._isClosed) {
               break;
            }

            if (this.checkForIncomingData(requestHeaders)) {
               dataToReceive = false;
            }
         }

         super._sendBuffer.trimHead(false);
         super._sendBuffer.setPosition(super._sendBuffer.getLength());
      }
   }

   private boolean checkForIncomingData(HeaderSetImpl requestHeaders) {
      boolean dataReceived = false;
      if (requestHeaders != null) {
         byte[] body = (byte[])requestHeaders.getHeader(72);
         if (body != null) {
            this.writeToReceiveBuffer(body);
            dataReceived = true;
         }

         body = (byte[])requestHeaders.getHeader(73);
         if (body != null) {
            this.writeToReceiveBuffer(body);
            dataReceived = true;
         }
      }

      return dataReceived;
   }

   @Override
   public int getResponseCode() throws IOException {
      throw new IOException();
   }

   @Override
   public void abort() throws IOException {
      throw new IOException();
   }

   @Override
   public void close() {
      synchronized (this.this$0) {
         super._isClosed = true;
         if (super._in != null) {
            super._in.close();
         }

         if (super._out != null) {
            super._out.close();
         }
      }
   }
}
