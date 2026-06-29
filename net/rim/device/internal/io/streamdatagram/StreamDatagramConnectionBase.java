package net.rim.device.internal.io.streamdatagram;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.IOProperties;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.cldc.io.daemon.TransportRegistry;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.internal.io.TrafficLogger;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.device.internal.io.tunnel.TunnelWorker;

public class StreamDatagramConnectionBase
   implements StreamConnection,
   IOProperties,
   ConnectionBaseInterface,
   StreamDatagramConnectionConstants,
   RadioStatusListener {
   protected StreamDatagramAddressBase _myAddress;
   protected String _address;
   protected StreamDatagramTransportBase _transport;
   protected InputStream _inStream;
   protected OutputStream _outStream;
   protected Hashtable _properties;
   protected int _mode;
   protected boolean _timeouts;
   protected long _timeout;
   protected boolean _abortWasCalled;
   protected boolean _isActive;
   protected boolean _isListenConnection;
   protected boolean _closeRequested;
   protected boolean _outputStreamClosed;
   protected boolean _inputStreamClosed;
   protected boolean _outputStreamOpened;
   protected boolean _inputStreamOpened;
   protected int _currentTcpState;
   protected boolean _freshnessSealed = true;
   protected Tunnel _tunnel;
   protected TrafficLogger _tLogger;
   private TunnelWorker _tunnelWorker = new TunnelWorker();
   private long EVL_SDConnectionBase_GUID = -5856082934285904418L;
   private String EvlName = "StreamDatagramConnectionBase";
   private int _flags;
   private int _validFlags;
   protected static String STR_STREAM_IS_ALREADY_CLOSED = "Stream was already closed";
   protected static String STR_STREAM_IS_ALREADY_OPEN = "Input stream already open";

   @Override
   public void close() {
      if (this._isActive) {
         this._closeRequested = true;
         this._transport.close(this);
         this._isActive = false;
      }
   }

   @Override
   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      String transportName = this.getClass().getName();
      this._mode = mode;
      this._timeouts = timeouts;
      int index = transportName.lastIndexOf(46);
      if (index == -1) {
         throw new IOException("Unable to find underlying transport class");
      }

      transportName = transportName.substring(0, index + 1) + "Transport";
      if ((this._transport = (StreamDatagramTransportBase)TransportRegistry.get(transportName)) != null && !this._isListenConnection) {
         if (this._transport.isConnectionTableFull()) {
            EventLogger.logEvent(447071754022829032L, 1413696867, 3);
            this._transport.logConnections();
            throw new IOException("Max connections opened.");
         }

         this._transport.addConnection(this);
      }

      if (timeouts) {
         this._timeout = 30000;
      }

      this._address = name;
      this._isActive = true;
      if (!this._freshnessSealed) {
         this._closeRequested = false;
         this._outputStreamClosed = false;
         this._inputStreamClosed = false;
         this._outputStreamOpened = false;
         this._inputStreamOpened = false;
         this._mode = mode;
         this._timeouts = timeouts;
      }

      this._freshnessSealed = false;
      return this;
   }

   @Override
   public int getProperties(String name) {
      return 2;
   }

   public int getIpAddress() {
      return this._myAddress.getIpAddress();
   }

   protected void sendDatagram(Datagram dgram) {
      this._transport.sendDatagram(dgram);
   }

   public void mobilityManagementEvent(int eventCode, int cause) {
   }

   public Tunnel openTunnel(String apn, String apnUsername, String apnPassword, int lingerTimeout) {
      if (apn == null || apn.length() == 0) {
         apn = TunnelCredentialsProvider.getInstance().getApn();
         apnUsername = TunnelCredentialsProvider.getInstance().getApnUsername();
         apnPassword = TunnelCredentialsProvider.getInstance().getApnPassword();
      }

      TunnelConfig tunnelConfig = new TunnelConfig(apn, "net.rim.tcp", null, apnUsername, apnPassword, this._tunnelWorker);
      if (lingerTimeout >= 0) {
         tunnelConfig.setLingerTimeout(lingerTimeout);
      }

      return this._tunnelWorker.open(tunnelConfig);
   }

   public void setTrafficLogger(TrafficLogger logger) {
      this._tLogger = logger;
   }

   public int getLocalPortInternal() {
      throw null;
   }

   public int getLocalPort() {
      throw null;
   }

   public void copyFlagsInto(DatagramBase dg) {
      this.copyFlagsInto(dg, true);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void copyFlagsInto(DatagramBase dg, boolean externalCall) {
      if (externalCall) {
         boolean var5 = false /* VF: Semaphore variable */;

         try {
            var5 = true;
            ControlledAccess.assertRRISignatures(true);
            var5 = false;
         } finally {
            if (var5) {
               return;
            }
         }
      }

      for (int mask = 1; mask != 0; mask <<= 1) {
         if ((this._validFlags & mask) != 0 && dg.getFlag(mask) == -1) {
            dg.setFlag(mask, (this._flags & mask) != 0);
         }
      }
   }

   public void setTimeout(long timeout) {
      this._timeout = timeout;
   }

   public byte[] setup(int callType, Object context) {
      return this._transport.setup(callType, context);
   }

   public int getPort() {
      throw null;
   }

   protected void checkIfConnectionIsClosedClosingOrAborted() {
      throw null;
   }

   public String getLocalAddress() {
      this.checkIfConnectionIsClosedClosingOrAborted();
      return StreamDatagramAddressBase.getLocalAddressInternal(this._myAddress.getApnName());
   }

   public String getAddress() {
      this.checkIfConnectionIsClosedClosingOrAborted();
      return StreamDatagramAddressBase.getPeerAddressInternal(this._myAddress.getIpAddress());
   }

   public boolean isInputStreamClosed() {
      return this._inputStreamClosed;
   }

   protected void processReceivedDatagram(Datagram _1) {
      throw null;
   }

   protected boolean isAddressed(DatagramAddressBase _1) {
      throw null;
   }

   public void abort() {
      throw null;
   }

   public StreamDatagramAddressBase getAddressBase() {
      return this._myAddress;
   }

   @Override
   public DataOutputStream openDataOutputStream() {
      return new DataOutputStream(this.openOutputStream());
   }

   @Override
   public OutputStream openOutputStream() throws IOException, ConnectionClosedException {
      if (this._outStream == null || this._closeRequested) {
         throw new ConnectionClosedException();
      }

      if (this._outputStreamClosed) {
         throw new IOException(STR_STREAM_IS_ALREADY_CLOSED);
      }

      if (this._outputStreamOpened) {
         throw new IOException(STR_STREAM_IS_ALREADY_OPEN);
      }

      this._outputStreamOpened = true;
      return this._outStream;
   }

   @Override
   public DataInputStream openDataInputStream() {
      return new DataInputStream(this.openInputStream());
   }

   @Override
   public InputStream openInputStream() throws IOException, ConnectionClosedException {
      if (this._inStream == null || this._closeRequested) {
         throw new ConnectionClosedException();
      }

      if (this._inputStreamClosed) {
         throw new IOException(STR_STREAM_IS_ALREADY_CLOSED);
      }

      if (this._inputStreamOpened) {
         throw new IOException(STR_STREAM_IS_ALREADY_OPEN);
      }

      this._inputStreamOpened = true;
      return this._inStream;
   }

   @Override
   public boolean isFlagSet(int flag) {
      try {
         ControlledAccess.assertRRISignatures(true);
      } finally {
         ;
      }

      return (this._validFlags & flag) != 0 && (this._flags & flag) != 0;
   }

   @Override
   public int getFlag(int flag) {
      try {
         ControlledAccess.assertRRISignatures(true);
      } finally {
         ;
      }

      if ((this._validFlags & flag) != 0) {
         return (this._flags & flag) != 0 ? 1 : 0;
      } else {
         return -1;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void setFlag(int flag, boolean value) {
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         ControlledAccess.assertRRISignatures(true);
         var5 = false;
      } finally {
         if (var5) {
            return;
         }
      }

      if (value) {
         this._flags |= flag;
      } else {
         this._flags &= ~flag;
      }

      this._validFlags |= flag;
   }

   @Override
   public Object getProperty(String name) {
      return this._properties == null ? null : this._properties.get(name);
   }

   @Override
   public void radioTurnedOff() {
   }

   @Override
   public void pdpStateChange(int apn, int state, int cause) {
      label31:
      try {
         if (this._myAddress == null || apn != RadioInfo.getAccessPointNumber(this._myAddress.getApnName())) {
            return;
         }
      } finally {
         break label31;
      }

      switch (state) {
         case 1:
            this.handleIpAddressChange(false);
         default:
            return;
         case 3:
            this.handleIpAddressChange(true);
      }
   }

   @Override
   public void signalLevel(int level) {
   }

   @Override
   public void networkStarted(int networkId, int service) {
   }

   @Override
   public void baseStationChange() {
   }

   @Override
   public void networkStateChange(int state) {
   }

   @Override
   public void networkScanComplete(boolean success) {
   }

   @Override
   public Object setProperty(String name, Object data) {
      if (this._properties == null) {
         this._properties = new Hashtable();
      }

      return this._properties.put(name, data);
   }

   @Override
   public void networkServiceChange(int networkId, int service) {
   }

   public StreamDatagramConnectionBase() {
      EventLogger.register(this.EVL_SDConnectionBase_GUID, this.EvlName, 2);
   }

   private void handleIpAddressChange(boolean ipAddressChange) {
      switch (RadioInfo.getNetworkService()) {
         case 5:
            if (ipAddressChange) {
               return;
            }
         default:
            this.abort();
      }
   }
}
