package net.rim.device.api.crypto.tls.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.SecureConnection;
import javax.microedition.io.SecurityInfo;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.tls.TLSInputStream;
import net.rim.device.api.crypto.tls.TLSOutputStream;
import net.rim.device.api.crypto.tls.tls10.TLSRecordProtocol;
import net.rim.device.cldc.io.ssl.TLSIOException;

public class TLSServer implements SecureConnection {
   private TLSRecordProtocol _recordProtocol;
   private TLSInputStream _inputStream;
   private TLSOutputStream _outputStream;
   private SocketConnection _subConnection;
   private boolean _isClosed;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void close() {
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         try {
            var5 = true;
            this._recordProtocol.close();
            var5 = false;
         } catch (Throwable var8) {
            throw new TLSIOException(e);
         }
      } finally {
         if (var5) {
            this._isClosed = true;
         }
      }

      this._isClosed = true;
   }

   @Override
   public DataInputStream openDataInputStream() {
      return new DataInputStream(this.openInputStream());
   }

   @Override
   public OutputStream openOutputStream() {
      if (this._outputStream == null) {
         this._outputStream = new TLSOutputStream(this._recordProtocol);
      }

      return this._outputStream;
   }

   @Override
   public DataOutputStream openDataOutputStream() {
      return new DataOutputStream(this.openOutputStream());
   }

   @Override
   public InputStream openInputStream() {
      if (this._inputStream == null) {
         this._inputStream = new TLSInputStream(this._recordProtocol);
      }

      return this._inputStream;
   }

   @Override
   public void setSocketOption(byte option, int value) throws IOException {
      if (this._isClosed) {
         throw new IOException("Connection closed");
      }

      this._subConnection.setSocketOption(option, value);
   }

   @Override
   public int getSocketOption(byte option) throws IOException {
      if (this._isClosed) {
         throw new IOException("Connection closed");
      } else {
         return this._subConnection.getSocketOption(option);
      }
   }

   @Override
   public String getLocalAddress() throws IOException {
      if (this._isClosed) {
         throw new IOException("Connection closed");
      } else {
         return this._subConnection.getLocalAddress();
      }
   }

   @Override
   public int getLocalPort() throws IOException {
      if (this._isClosed) {
         throw new IOException("Connection closed");
      } else {
         return this._subConnection.getLocalPort();
      }
   }

   @Override
   public String getAddress() throws IOException {
      if (this._isClosed) {
         throw new IOException("Connection closed");
      } else {
         return this._subConnection.getAddress();
      }
   }

   @Override
   public int getPort() throws IOException {
      if (this._isClosed) {
         throw new IOException("Connection closed");
      } else {
         return this._subConnection.getPort();
      }
   }

   @Override
   public SecurityInfo getSecurityInfo() {
      return null;
   }

   public TLSServer(StreamConnection subConnection, String name, Certificate cert, PrivateKey key) {
      this._subConnection = (SocketConnection)subConnection;
      this._recordProtocol = new TLSRecordProtocol(subConnection, name, false);
      this._recordProtocol.setLocalCertificateAndKey(cert, key);
      TLSServerHandshakeProtocol handshake = new TLSServerHandshakeProtocol(this._recordProtocol);
      handshake.accept();
      System.out.println("Handshake accepted...");
   }
}
