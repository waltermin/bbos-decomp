package net.rim.device.cldc.io.btgoep;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.obex.Authenticator;
import javax.obex.PasswordAuthentication;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;

public class OBEXSession {
   protected DataInputStream _din;
   protected OBEXDataOutputStream _dout;
   protected DataBuffer _db;
   protected byte[] _buf;
   protected int _maxPacketSize;
   protected int _maxDataSize;
   protected boolean _disconnected;
   protected boolean _operationInProgress;
   protected Authenticator _authenticator;
   protected static final int DATA_PACKET_OVERHEAD;
   protected static final boolean DEBUG;

   public void init(InputStream in, OutputStream out) {
      this._din = (DataInputStream)(new Object(in));
      this._dout = new OBEXDataOutputStream(out);
      this._db = (DataBuffer)(new Object());
   }

   protected HeaderSetImpl readHeaders(int length) {
      return this.readHeaders(length, true);
   }

   protected synchronized HeaderSetImpl readHeaders(int length, boolean allowNull) {
      if (length > 0) {
         this._din.readFully(this._buf, 0, length);
         this._db.setData(this._buf, 0, length);
         return new HeaderSetImpl(this._db);
      } else {
         return allowNull ? null : new HeaderSetImpl();
      }
   }

   protected byte[] getAuthResponse(byte[] challengeData) {
      if (challengeData != null && this._authenticator != null) {
         try {
            DataBuffer buffer = (DataBuffer)(new Object());
            buffer.setData(challengeData, 0, challengeData.length);
            if (buffer.getLength() < 18) {
               return null;
            }

            byte tag = buffer.readByte();
            byte length = buffer.readByte();
            if (tag != 0 && length != 16) {
               return null;
            }

            byte[] nonce = new byte[16];
            buffer.read(nonce, 0, 16);
            boolean userName = false;
            boolean fullAccess = true;
            String realm = null;

            while (!buffer.eof()) {
               tag = buffer.readByte();
               if (tag == 1) {
                  buffer.readByte();
                  int option = buffer.readByte();
                  if ((option & 1) == 1) {
                     userName = true;
                  }

                  if ((option & 2) == 2) {
                     fullAccess = false;
                  }
               } else if (tag == 2) {
                  length = buffer.readByte();
                  int encoding = buffer.readByte() & 255;
                  byte[] realmArray = new byte[length - 1];
                  buffer.read(realmArray);
                  switch (encoding) {
                     case 255:
                        realm = (String)(new Object(realmArray, "UnicodeBigUnmarked"));
                        break;
                     default:
                        realm = (String)(new Object(realmArray));
                  }
               }
            }

            PasswordAuthentication challenge = this._authenticator.onAuthenticationChallenge(realm, userName, fullAccess);
            byte[] digestData = this.getDigestData(nonce, challenge.getPassword());
            DataBuffer resBuffer = (DataBuffer)(new Object());
            resBuffer.writeByte(0);
            resBuffer.writeByte(digestData.length);
            resBuffer.write(digestData);
            if (userName) {
               resBuffer.writeByte(1);
               resBuffer.writeByte(challenge.getUserName().length);
               resBuffer.write(challenge.getUserName());
            }

            resBuffer.writeByte(2);
            resBuffer.writeByte(nonce.length);
            resBuffer.write(nonce);
            return resBuffer.getArray();
         } finally {
            ;
         }
      } else {
         return null;
      }
   }

   protected boolean checkAuthResponse(byte[] authResponse, byte[] nonce) {
      if (authResponse != null && nonce != null) {
         try {
            DataBuffer buffer = (DataBuffer)(new Object());
            byte[] username = null;
            buffer.write(authResponse);
            buffer.setPosition(0);
            buffer.readByte();
            int length = buffer.readByte();
            byte[] data = new byte[length];
            buffer.read(data);
            if (buffer.available() > 0) {
               int code = buffer.readByte();
               if (code == 1) {
                  int var11 = buffer.readByte();
                  username = new byte[var11];
                  buffer.read(username);
               }
            }

            byte[] response = this._authenticator.onAuthenticationResponse(username);
            byte[] digestData = this.getDigestData(nonce, response);
            return Arrays.equals(digestData, data);
         } finally {
            ;
         }
      } else {
         return false;
      }
   }

   private byte[] getDigestData(byte[] nonce, byte[] password) {
      try {
         Digest digest = DigestFactory.getInstance("MD5");
         digest.update(nonce);
         digest.update(58);
         digest.update(password);
         return digest.getDigest();
      } finally {
         ;
      }
   }

   protected byte[] getUsername(byte[] authResponse) {
      if (authResponse == null) {
         return null;
      }

      try {
         DataBuffer buffer = (DataBuffer)(new Object());
         byte[] username = null;
         buffer.write(authResponse);
         buffer.readByte();
         int length = buffer.readByte();
         byte[] data = new byte[length];
         buffer.read(data);
         if (buffer.available() > 0) {
            int code = buffer.readByte();
            if (code == 1) {
               int var9 = buffer.readByte();
               username = new byte[var9];
               buffer.read(username);
            }
         }

         return username;
      } finally {
         ;
      }
   }
}
