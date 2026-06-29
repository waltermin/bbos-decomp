package net.rim.device.api.crypto.tls.wtls20;

import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.firewall.Firewall;
import net.rim.vm.TraceBack;

public class WTLS20Connection implements DatagramConnection {
   private WTLSRecordProtocol _recordProtocol;
   private DatagramConnection _subConnection;
   private WTLS20Connection$DataTransport _dataTransport;
   private DataBuffer _out;
   private DataBuffer _in;
   private boolean _isClosed;
   public static final int USE_CLIENT_ID_INFO;
   public static final int DELETE_SESSIONS_ON_ALERTS;
   public static final int WAP_20_CONFORMANCE;
   public static final int TIMEOUT;

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
         return (Datagram)(new Object(buf, 0, size));
      } else {
         throw new Object();
      }
   }

   @Override
   public Datagram newDatagram(byte[] buf, int size, String addr) {
      this.confirmConnectionOpen();
      if (buf != null && size <= buf.length && addr != null) {
         return (Datagram)(new Object(buf, 0, size, addr));
      } else {
         throw new Object();
      }
   }

   @Override
   public Datagram newDatagram(int size) {
      this.confirmConnectionOpen();
      if (size < 0) {
         throw new Object();
      } else {
         return (Datagram)(new Object(new byte[size], 0, size));
      }
   }

   @Override
   public Datagram newDatagram(int size, String addr) {
      this.confirmConnectionOpen();
      if (size >= 0 && addr != null) {
         return (Datagram)(new Object(new byte[size], 0, size, addr));
      } else {
         throw new Object();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void receive(Datagram datagram) {
      this.confirmConnectionOpen();
      if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51) && !Firewall.getInstance().allowConnection("wtls", "", false)) {
         throw new Object("Permission denied");
      }

      try {
         this._recordProtocol.read(this._in);
         datagram.setData(this._in.getArray(), this._in.getArrayPosition(), this._in.available());
      } catch (Throwable var4) {
         throw new Object(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void send(Datagram datagram) {
      this.confirmConnectionOpen();
      byte[] contents = datagram.getData();

      try {
         if (contents != null && contents.length > 0) {
            this._out.setData(contents, 0, Math.min(contents.length, this._recordProtocol.getMaxPacketSize()));
            this._recordProtocol.write(this._recordProtocol.getApplicationProtocolConstant(), this._out);
         }
      } catch (Throwable var5) {
         throw new Object(e);
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
   ) {
      if (subConnection != null && name != null && apn != null) {
         this._isClosed = false;

         try {
            this._subConnection = subConnection;
            this._dataTransport = new WTLS20Connection$DataTransport(this);
            this._out = (DataBuffer)(new Object());
            this._in = (DataBuffer)(new Object());
            this._recordProtocol = new WTLSRecordProtocol(this._dataTransport, apn, name, flags, clientIdType, clientIdValue, ipAddress, ipPort);
         } catch (Throwable var11) {
            throw new Object(e);
         }
      } else {
         throw new Object();
      }
   }

   private void confirmConnectionOpen() {
      if (this._isClosed) {
         throw new Object("Connection already closed");
      }
   }

   public WTLS20Connection(DatagramConnection subConnection, String apn, String name) {
      this(subConnection, apn, name, 3, 0, "", 0, 0);
   }
}
