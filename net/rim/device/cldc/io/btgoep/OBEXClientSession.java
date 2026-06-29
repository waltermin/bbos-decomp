package net.rim.device.cldc.io.btgoep;

import java.io.IOException;
import javax.obex.Authenticator;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import net.rim.device.api.util.DataBuffer;

public class OBEXClientSession extends OBEXSession implements ClientSession {
   private long _connectionID = -1;
   private boolean _connected;

   @Override
   public void setAuthenticator(Authenticator auth) {
      if (auth == null) {
         throw new NullPointerException();
      }

      super._authenticator = auth;
   }

   @Override
   public HeaderSet createHeaderSet() {
      return new HeaderSetImpl();
   }

   @Override
   public void setConnectionID(long id) {
      if (id >= 0 && id <= 4294967295L) {
         this._connectionID = id;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public long getConnectionID() {
      return this._connectionID;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public synchronized HeaderSet connect(HeaderSet headers) throws IOException {
      HeaderSetImpl responseHeaders = null;

      while (!this._connected) {
         if (super._operationInProgress) {
            throw new IOException("Operation in progress");
         }

         int length = 7;
         DataBuffer headerData = null;
         if (headers != null) {
            boolean var9 = false /* VF: Semaphore variable */;

            try {
               var9 = true;
               headerData = ((HeaderSetImpl)headers).getData();
               if (headerData != null) {
                  length += headerData.getLength();
                  var9 = false;
               } else {
                  var9 = false;
               }
            } finally {
               if (var9) {
                  throw new IllegalArgumentException();
               }
            }
         }

         super._dout.reset();
         super._dout.writeByte(128);
         super._dout.writeShort(length);
         super._dout.writeByte(16);
         super._dout.writeByte(0);
         super._dout.writeShort(8192);
         if (headerData != null) {
            headerData.read(super._dout);
         }

         super._dout.flush();
         int responseCode = super._din.readUnsignedByte();
         length = super._din.readUnsignedShort();
         int version = super._din.readUnsignedByte();
         int flags = super._din.readUnsignedByte();
         super._maxPacketSize = Math.min(super._din.readUnsignedShort(), 8192);
         super._maxDataSize = super._maxPacketSize - 6;
         super._buf = new byte[super._maxPacketSize];
         responseHeaders = this.readHeaders(length - 7);
         if (responseHeaders == null) {
            responseHeaders = new HeaderSetImpl();
         }

         responseHeaders.setResponseCode(responseCode);
         if (responseCode == 160) {
            this._connected = true;
         }

         if (!this.authenticateResponseRequired(responseCode, headers, responseHeaders)) {
            this.checkAuthenticateResponse(headers, responseHeaders);
            return responseHeaders;
         }
      }

      throw new IOException("Already connected");
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public synchronized HeaderSet disconnect(HeaderSet headers) throws IOException {
      HeaderSetImpl responseHeaders = null;

      while (this._connected) {
         if (super._operationInProgress) {
            throw new IOException("Operation in progress");
         }

         int length = 3;
         DataBuffer headerData = null;
         if (headers != null) {
            boolean var7 = false /* VF: Semaphore variable */;

            try {
               var7 = true;
               headerData = ((HeaderSetImpl)headers).getData();
               if (headerData != null) {
                  length += headerData.getLength();
                  var7 = false;
               } else {
                  var7 = false;
               }
            } finally {
               if (var7) {
                  throw new IllegalArgumentException();
               }
            }
         }

         super._dout.reset();
         super._dout.writeByte(129);
         super._dout.writeShort(length);
         if (headerData != null) {
            headerData.read(super._dout);
         }

         super._dout.flush();
         responseHeaders = this.readResponse();
         int responseCode = responseHeaders.getResponseCode();
         if (responseCode == 160) {
            this._connected = false;
         }

         if (!this.authenticateResponseRequired(responseCode, headers, responseHeaders)) {
            this.checkAuthenticateResponse(headers, responseHeaders);
            return responseHeaders;
         }
      }

      throw new IOException("Not connected");
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public synchronized HeaderSet setPath(HeaderSet headers, boolean backup, boolean create) throws IOException {
      HeaderSetImpl responseHeaders = null;

      while (this._connected) {
         if (super._operationInProgress) {
            throw new IOException("Operation in progress");
         }

         int length = 5;
         DataBuffer headerData = null;
         if (headers != null) {
            boolean var11 = false /* VF: Semaphore variable */;

            try {
               var11 = true;
               headerData = ((HeaderSetImpl)headers).getData();
               if (headerData != null) {
                  length += headerData.getLength();
                  var11 = false;
               } else {
                  var11 = false;
               }
            } finally {
               if (var11) {
                  throw new IllegalArgumentException();
               }
            }
         }

         byte flags = 0;
         if (backup) {
            flags = (byte)(flags | 1);
         }

         if (!create) {
            flags = (byte)(flags | 2);
         }

         byte constants = 0;
         super._dout.reset();
         super._dout.writeByte(133);
         super._dout.writeShort(length);
         super._dout.writeByte(flags);
         super._dout.writeByte(constants);
         if (headerData != null) {
            headerData.read(super._dout);
         }

         super._dout.flush();
         responseHeaders = this.readResponse();
         int responseCode = responseHeaders.getResponseCode();
         if (!this.authenticateResponseRequired(responseCode, headers, responseHeaders)) {
            this.checkAuthenticateResponse(headers, responseHeaders);
            return responseHeaders;
         }
      }

      throw new IOException("Not connected");
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public synchronized HeaderSet delete(HeaderSet headers) throws IOException {
      HeaderSetImpl responseHeaders = null;

      while (this._connected) {
         if (super._operationInProgress) {
            throw new IOException("Operation in progress");
         }

         int length = 3;
         DataBuffer headerData = null;
         if (headers != null) {
            boolean var7 = false /* VF: Semaphore variable */;

            try {
               var7 = true;
               headerData = ((HeaderSetImpl)headers).getData();
               if (headerData != null) {
                  length += headerData.getLength();
                  var7 = false;
               } else {
                  var7 = false;
               }
            } finally {
               if (var7) {
                  throw new IllegalArgumentException();
               }
            }
         }

         super._dout.reset();
         super._dout.writeByte(130);
         super._dout.writeShort(length);
         if (headerData != null) {
            headerData.read(super._dout);
         }

         super._dout.flush();
         responseHeaders = this.readResponse();
         int responseCode = responseHeaders.getResponseCode();
         if (!this.authenticateResponseRequired(responseCode, headers, responseHeaders)) {
            this.checkAuthenticateResponse(headers, responseHeaders);
            return responseHeaders;
         }
      }

      throw new IOException("Not connected");
   }

   private boolean authenticateResponseRequired(int responseCode, HeaderSet headers, HeaderSet responseHeaders) {
      if (responseCode == 193) {
         byte[] challengeData = (byte[])responseHeaders.getHeader(77);
         if (challengeData != null && headers != null && headers.getHeader(78) == null) {
            byte[] authResponse = this.getAuthResponse(challengeData);
            headers.setHeader(78, authResponse);
            return true;
         }
      }

      return false;
   }

   private void checkAuthenticateResponse(HeaderSet headers, HeaderSet resHeaders) throws IOException {
      if (headers != null && headers.getHeader(77) != null) {
         byte[] authResponseArray = (byte[])resHeaders.getHeader(78);
         if (authResponseArray == null || !this.checkAuthResponse(authResponseArray, ((HeaderSetImpl)headers)._nonce)) {
            throw new IOException("Authentication Failure");
         }
      }
   }

   @Override
   public synchronized Operation get(HeaderSet headers) throws IOException {
      if (!this._connected) {
         throw new IOException("Not connected");
      }

      if (super._operationInProgress) {
         throw new IOException("Operation in progress");
      }

      try {
         return new OBEXClientSession$ClientOperationImpl(this, false, (HeaderSetImpl)headers);
      } finally {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public synchronized Operation put(HeaderSet headers) throws IOException {
      if (!this._connected) {
         throw new IOException("Not connected");
      }

      if (super._operationInProgress) {
         throw new IOException("Operation in progress");
      }

      try {
         return new OBEXClientSession$ClientOperationImpl(this, true, (HeaderSetImpl)headers);
      } finally {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void close() {
   }

   private HeaderSetImpl readResponse() {
      int responseCode = super._din.readUnsignedByte();
      int length = super._din.readUnsignedShort();
      HeaderSetImpl responseHeaders = this.readHeaders(length - 3);
      if (responseHeaders == null) {
         responseHeaders = new HeaderSetImpl();
      }

      responseHeaders.setResponseCode(responseCode);
      return responseHeaders;
   }
}
