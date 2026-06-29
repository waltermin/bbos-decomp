package net.rim.device.cldc.io.btgoep;

import java.io.InputStream;
import java.io.OutputStream;
import javax.obex.Authenticator;
import javax.obex.ServerRequestHandler;
import net.rim.device.api.util.DataBuffer;

public class OBEXServerSession extends OBEXSession implements Runnable {
   private ServerRequestHandler _handler;

   public void init(ServerRequestHandler handler, InputStream in, OutputStream out, Authenticator auth) {
      super.init(in, out);
      this._handler = handler;
      super._authenticator = auth;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public synchronized void run() {
      try {
         super._disconnected = false;

         while (!super._disconnected) {
            this.handleNextRequest(null);
         }
      } catch (Throwable var3) {
         System.out.println("IOException caught: " + e.getMessage());
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private synchronized HeaderSetImpl handleNextRequest(OBEXServerSession$ServerOperationImpl operationInProgress) {
      HeaderSetImpl replyHeaders = new HeaderSetImpl();
      boolean finalBit = true;
      boolean var16 = false /* VF: Semaphore variable */;

      int b;
      try {
         var16 = true;
         b = super._din.readByte() & 255;
         var16 = false;
      } finally {
         if (var16) {
            super._disconnected = true;
            return null;
         }
      }

      switch (b) {
         case 2:
            finalBit = false;
         case 130: {
            int length = super._din.readUnsignedShort() - 3;
            HeaderSetImpl var23 = this.readHeaders(length);
            if (operationInProgress != null) {
               if (finalBit) {
                  operationInProgress.finalBitReceived();
               }

               return var23;
            }

            if (var23 == null) {
               var23 = new HeaderSetImpl();
            }

            if (finalBit && var23.getHeader(72) == null && var23.getHeader(73) == null) {
               int responseCode = this._handler.onDelete(var23, replyHeaders);
               this.writeResponse(responseCode, replyHeaders);
               return null;
            }

            operationInProgress = new OBEXServerSession$ServerOperationImpl(this, true, var23);
            if (finalBit) {
               operationInProgress.finalBitReceived();
            }

            int responseCode = this._handler.onPut(operationInProgress);
            operationInProgress.operationComplete(responseCode);
            return null;
         }
         case 3:
            finalBit = false;
         case 131: {
            int length = super._din.readUnsignedShort() - 3;
            HeaderSetImpl var22 = this.readHeaders(length);
            if (operationInProgress != null) {
               if (finalBit) {
                  operationInProgress.finalBitReceived();
               }

               return var22;
            }

            if (var22 == null) {
               var22 = new HeaderSetImpl();
            }

            operationInProgress = new OBEXServerSession$ServerOperationImpl(this, false, var22);
            if (finalBit) {
               operationInProgress.finalBitReceived();
            }

            int responseCode = this._handler.onGet(operationInProgress);
            operationInProgress.operationComplete(responseCode);
            return null;
         }
         case 128: {
            int length = super._din.readUnsignedShort() - 3;
            int version = super._din.readByte() & 255;
            length--;
            int flags = super._din.readByte() & 255;
            length--;
            super._maxPacketSize = Math.min(super._din.readUnsignedShort(), 8192);
            length -= 2;
            super._buf = new byte[super._maxPacketSize];
            super._maxDataSize = super._maxPacketSize - 6;
            HeaderSetImpl requestHeaders = this.readHeaders(length, false);
            byte[] target = (byte[])requestHeaders.getHeader(70);
            if (target != null) {
               replyHeaders.setHeader(203, new Long(1));
               replyHeaders.setHeader(74, target);
            }

            if (requestHeaders.getHeader(78) != null) {
               return requestHeaders;
            }

            int responseCode = this._handler.onConnect(requestHeaders, replyHeaders);
            byte[] challengeArray = (byte[])replyHeaders.getHeader(77);
            if (challengeArray != null) {
               HeaderSetImpl challengeHeaders = new HeaderSetImpl();
               challengeHeaders.setHeader(77, challengeArray);
               this.writeConnectResponse(193, challengeHeaders, version, flags);
               HeaderSetImpl authResponseHeaders = this.handleNextRequest(null);
               byte[] responseArray = (byte[])authResponseHeaders.getHeader(78);
               if (!this.checkAuthResponse(responseArray, replyHeaders._nonce)) {
                  this._handler.onAuthenticationFailure(this.getUsername(responseArray));
                  return null;
               }

               replyHeaders.removeHeader(77);
               responseCode = 160;
            }

            byte[] requestChallengeArray = (byte[])requestHeaders.getHeader(77);
            if (requestChallengeArray != null) {
               byte[] challengeResponse = this.getAuthResponse(requestChallengeArray);
               replyHeaders.setHeader(78, challengeResponse);
            }

            this.writeConnectResponse(responseCode, replyHeaders, version, flags);
            return null;
         }
         case 129: {
            int length = super._din.readUnsignedShort() - 3;
            HeaderSetImpl var20 = this.readHeaders(length, false);
            this._handler.onDisconnect(var20, replyHeaders);
            this.writeResponse(160, replyHeaders);
            return null;
         }
         case 133: {
            int length = super._din.readUnsignedShort() - 3;
            int flags = super._din.readByte() & 255;
            length--;
            int constants = super._din.readByte() & 255;
            HeaderSetImpl requestHeaders = this.readHeaders(--length, false);
            if (requestHeaders.getHeader(78) != null) {
               return requestHeaders;
            }

            boolean backup = (flags & 1) != 0;
            boolean create = (flags & 2) == 0;
            int responseCode = this._handler.onSetPath(requestHeaders, replyHeaders, backup, create);
            this.writeResponse(responseCode, replyHeaders);
            return null;
         }
         case 255: {
            int length = super._din.readUnsignedShort() - 3;
            this.readHeaders(length);
            this.writeResponse(160);
            if (operationInProgress != null) {
               operationInProgress.clientAborted();
               return null;
            }
            break;
         }
         default:
            this.writeResponse(192, null);
      }

      return null;
   }

   private boolean writeResponse(int responseCode) {
      return this.writeResponse(responseCode, null);
   }

   private void writeConnectResponse(int responseCode, HeaderSetImpl headers, int version, int flags) {
      int length = 7;
      DataBuffer headerData = headers.getData();
      if (headerData != null) {
         length += headerData.getLength();
      }

      super._dout.reset();
      super._dout.writeByte(responseCode);
      super._dout.writeShort(length);
      super._dout.writeByte(version);
      super._dout.writeByte(flags);
      super._dout.writeShort(super._maxPacketSize);
      if (headerData != null) {
         headerData.read(super._dout);
      }

      super._dout.flush();
   }

   private boolean writeResponse(int responseCode, HeaderSetImpl headers) {
      return this.writeResponse(responseCode, headers, null, false);
   }

   private synchronized boolean writeResponse(int responseCode, HeaderSetImpl headers, DataBuffer bodyData, boolean endOfBody) {
      boolean dataRemaining = false;
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
            int lengthRemainingForBodyData = super._maxPacketSize - length - 3;
            if (bodyDataLength > lengthRemainingForBodyData) {
               dataRemaining = true;
               bodyDataLength = lengthRemainingForBodyData;
            }

            length += bodyDataLength + 3;
         }
      }

      if (endOfBody) {
         length += 3;
      }

      super._dout.reset();
      super._dout.writeByte(responseCode);
      super._dout.writeShort(length);
      if (headerData != null) {
         headerData.read(super._dout);
      }

      if (bodyDataLength != 0) {
         super._dout.writeByte(72);
         super._dout.writeShort(bodyDataLength + 3);
         bodyData.read(super._dout, bodyDataLength);
      }

      if (endOfBody) {
         super._dout.writeByte(73);
         super._dout.writeShort(3);
      }

      super._dout.flush();
      return dataRemaining;
   }
}
