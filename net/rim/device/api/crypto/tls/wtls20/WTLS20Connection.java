package net.rim.device.api.crypto.tls.wtls20;

import java.io.IOException;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.ssl.TLSException;
import net.rim.device.cldc.io.ssl.TLSIOException;
import net.rim.device.internal.firewall.Firewall;
import net.rim.vm.TraceBack;

public class WTLS20Connection implements DatagramConnection {
   private WTLSRecordProtocol _recordProtocol;
   private DatagramConnection _subConnection;
   private WTLS20Connection$DataTransport _dataTransport;
   private DataBuffer _out;
   private DataBuffer _in;
   private boolean _isClosed;
   public static final int USE_CLIENT_ID_INFO = 1;
   public static final int DELETE_SESSIONS_ON_ALERTS = 2;
   public static final int WAP_20_CONFORMANCE = 4;
   public static final int TIMEOUT = 5000;

   @Override
   public void close() {
      if (!this._isClosed) {
         this._isClosed = true;
         this._subConnection.close();
      }
   }

   @Override
   public int getNominalLength() {
      this.confirmConnectionOpen();
      return this._subConnection.getNominalLength();
   }

   @Override
   public Datagram newDatagram(byte[] buf, int size) {
      this.confirmConnectionOpen();
      if (buf != null && size <= buf.length) {
         return new DatagramBase(buf, 0, size);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public Datagram newDatagram(byte[] buf, int size, String addr) {
      this.confirmConnectionOpen();
      if (buf != null && size <= buf.length && addr != null) {
         return new DatagramBase(buf, 0, size, addr);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public Datagram newDatagram(int size) {
      this.confirmConnectionOpen();
      if (size < 0) {
         throw new IllegalArgumentException();
      } else {
         return new DatagramBase(new byte[size], 0, size);
      }
   }

   @Override
   public Datagram newDatagram(int size, String addr) {
      this.confirmConnectionOpen();
      if (size >= 0 && addr != null) {
         return new DatagramBase(new byte[size], 0, size, addr);
      } else {
         throw new IllegalArgumentException();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void receive(Datagram datagram) throws IOException, TLSIOException {
      this.confirmConnectionOpen();
      if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51) && !Firewall.getInstance().allowConnection("wtls", "", false)) {
         throw new IOException("Permission denied");
      }

      try {
         this._recordProtocol.read(this._in);
         datagram.setData(this._in.getArray(), this._in.getArrayPosition(), this._in.available());
      } catch (Throwable var4) {
         throw new TLSIOException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void send(Datagram datagram) throws TLSIOException {
      this.confirmConnectionOpen();
      byte[] contents = datagram.getData();

      try {
         if (contents != null && contents.length > 0) {
            this._out.setData(contents, 0, Math.min(contents.length, this._recordProtocol.getMaxPacketSize()));
            this._recordProtocol.write(this._recordProtocol.getApplicationProtocolConstant(), this._out);
         }
      } catch (Throwable var5) {
         throw new TLSIOException(e);
      }
   }

   @Override
   public int getMaximumLength() {
      this.confirmConnectionOpen();
      return this._subConnection.getMaximumLength();
   }

   public WTLS20Connection(DatagramConnection subConnection, String apn, String name, int flags, int clientIdType, String clientIdValue) {
      this(subConnection, apn, name, flags, clientIdType, clientIdValue, 0, 0);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public WTLS20Connection(
      DatagramConnection subConnection, String apn, String name, int flags, int clientIdType, String clientIdValue, int ipAddress, int ipPort
   ) throws TLSException {
      if (subConnection != null && name != null && apn != null) {
         this._isClosed = false;

         try {
            this._subConnection = subConnection;
            this._dataTransport = new WTLS20Connection$DataTransport(this);
            this._out = new DataBuffer();
            this._in = new DataBuffer();
            this._recordProtocol = new WTLSRecordProtocol(this._dataTransport, apn, name, flags, clientIdType, clientIdValue, ipAddress, ipPort);
         } catch (Throwable var11) {
            throw new TLSException(e);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void confirmConnectionOpen() throws IOException {
      if (this._isClosed) {
         throw new IOException("Connection already closed");
      }
   }

   public WTLS20Connection(DatagramConnection subConnection, String apn, String name) {
      this(subConnection, apn, name, 3, 0, "", 0, 0);
   }
}
