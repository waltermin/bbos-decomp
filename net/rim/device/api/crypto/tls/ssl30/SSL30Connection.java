package net.rim.device.api.crypto.tls.ssl30;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.SecurityInfo;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.crypto.tls.TLSInputStream;
import net.rim.device.api.crypto.tls.TLSOutputStream;
import net.rim.device.api.io.ConnectionCloseListener;
import net.rim.device.api.io.ConnectionCloseProvider;
import net.rim.device.api.io.ParentStreamProvider;
import net.rim.device.api.io.SocketConnectionEnhanced;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.cldc.io.ssl.SSLConnection;
import net.rim.device.cldc.io.ssl.SSLConnectionOptions;
import net.rim.vm.TraceBack;

public class SSL30Connection implements SSLConnection, ParentStreamProvider, ConnectionCloseProvider, ConnectionCloseListener, SocketConnectionEnhanced {
   private StreamConnection _subConnection;
   private SSLRecordProtocol _recordProtocol;
   private boolean _isClosed;
   private TLSInputStream _inputStream;
   private TLSOutputStream _outputStream;
   private ConnectionCloseListener _closeListener;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void close() {
      if (!this._isClosed) {
         if ((this._inputStream == null || this._inputStream.isClosed()) && (this._outputStream == null || this._outputStream.isClosed())) {
            boolean var5 = false /* VF: Semaphore variable */;

            try {
               try {
                  var5 = true;
                  this._recordProtocol.close();
                  var5 = false;
               } catch (Throwable var8) {
                  throw new Object(e);
               }
            } finally {
               if (var5) {
                  this._isClosed = true;
               }
            }

            this._isClosed = true;
         }
      }
   }

   @Override
   public DataInputStream openDataInputStream() {
      if (this._isClosed) {
         throw new Object();
      } else {
         return (DataInputStream)(new Object(this.openInputStream()));
      }
   }

   @Override
   public OutputStream openOutputStream() {
      if (this._isClosed) {
         throw new Object();
      }

      if (this._outputStream == null) {
         this._outputStream = (TLSOutputStream)(new Object(this._recordProtocol));
      } else if (this._outputStream.isClosed()) {
         throw new Object("output stream closed");
      }

      return this._outputStream;
   }

   @Override
   public DataOutputStream openDataOutputStream() {
      if (this._isClosed) {
         throw new Object();
      } else {
         return (DataOutputStream)(new Object(this.openOutputStream()));
      }
   }

   @Override
   public InputStream openInputStream() {
      if (this._isClosed) {
         throw new Object();
      }

      if (this._inputStream == null) {
         this._inputStream = (TLSInputStream)(new Object(this._recordProtocol));
      }

      return this._inputStream;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public SecurityInfo getSecurityInfo() {
      if (this._isClosed) {
         throw new Object();
      }

      try {
         this._recordProtocol.connect();
         return this._recordProtocol.getSecurityInfo();
      } catch (Throwable var3) {
         throw new Object(e);
      }
   }

   @Override
   public void setSocketOption(byte option, int value) {
      if (!(this._subConnection instanceof Object)) {
         throw new Object();
      }

      ((SocketConnection)this._subConnection).setSocketOption(option, value);
   }

   @Override
   public int getSocketOption(byte option) {
      if (!(this._subConnection instanceof Object)) {
         throw new Object();
      } else {
         return ((SocketConnection)this._subConnection).getSocketOption(option);
      }
   }

   @Override
   public String getLocalAddress() {
      if (!(this._subConnection instanceof Object)) {
         throw new Object();
      } else {
         return ((SocketConnection)this._subConnection).getLocalAddress();
      }
   }

   @Override
   public int getLocalPort() {
      if (!(this._subConnection instanceof Object)) {
         throw new Object();
      } else {
         return ((SocketConnection)this._subConnection).getLocalPort();
      }
   }

   @Override
   public String getAddress() {
      if (!(this._subConnection instanceof Object)) {
         throw new Object();
      } else {
         return ((SocketConnection)this._subConnection).getAddress();
      }
   }

   @Override
   public int getPort() {
      if (!(this._subConnection instanceof Object)) {
         throw new Object();
      } else {
         return ((SocketConnection)this._subConnection).getPort();
      }
   }

   @Override
   public StreamConnection getParentStream() {
      return this._subConnection;
   }

   @Override
   public boolean connectionStatusAvailable() {
      return !(this._subConnection instanceof Object) ? false : ((ConnectionCloseProvider)this._subConnection).connectionStatusAvailable();
   }

   @Override
   public boolean isConnectionEstablished() {
      return this._isClosed || !(this._subConnection instanceof Object) || !((ConnectionCloseProvider)this._subConnection).isConnectionEstablished()
         ? false
         : !this._recordProtocol.peekForAlert();
   }

   @Override
   public void setConnectionCloseListener(ConnectionCloseListener listener) {
      if (this._subConnection instanceof Object) {
         ((ConnectionCloseProvider)this._subConnection).setConnectionCloseListener(this);
      }

      this._closeListener = listener;
   }

   @Override
   public void connectionClosed(ConnectionCloseProvider connection) {
      if (this._closeListener != null) {
         this._closeListener.connectionClosed(this);
      }
   }

   @Override
   public void setSocketOptionEx(short option, long value) {
      if (!(this._subConnection instanceof Object)) {
         throw new Object();
      }

      ((SocketConnectionEnhanced)this._subConnection).setSocketOptionEx(option, value);
   }

   @Override
   public long getSocketOptionEx(short option) {
      if (!(this._subConnection instanceof Object)) {
         throw new Object();
      } else {
         return ((SocketConnectionEnhanced)this._subConnection).getSocketOptionEx(option);
      }
   }

   @Override
   public void setOverrideConnectionOptions(SSLConnectionOptions connectionOptions) {
      ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51);
      this._recordProtocol.setOverrideConnectionOptions(connectionOptions);
   }

   public SSL30Connection(StreamConnection subConnection, String name) {
      this(subConnection, name, true);
   }

   public SSL30Connection(StreamConnection subConnection, String name, boolean startHandshake) {
      if (subConnection != null && name != null) {
         this._subConnection = subConnection;
         this._recordProtocol = new SSLRecordProtocol(subConnection, name);
         if (startHandshake) {
            this._recordProtocol.connect();
         }
      } else {
         throw new Object();
      }
   }
}
