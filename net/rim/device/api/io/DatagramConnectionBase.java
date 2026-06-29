package net.rim.device.api.io;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Hashtable;
import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.UDPDatagramConnection;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CyclicQueue;
import net.rim.device.cldc.io.daemon.TransportRegistry;
import net.rim.device.cldc.io.utility.MalformedURLException;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.io.TrafficLogger;
import net.rim.vm.TraceBack;

public class DatagramConnectionBase implements DatagramConnection, IOProperties, ConnectionBaseInterface, UDPDatagramConnection {
   protected DatagramAddressBase _addressBase;
   protected DatagramAddressBase _receiveFilter;
   protected DatagramTransportBase _transport;
   protected CyclicQueue _datagrams = new CyclicQueue(8);
   protected Datagram[] _sendingDatagrams = new Datagram[0];
   protected Hashtable _properties;
   protected int _flags;
   protected int _validFlags;
   protected long _timeout;
   protected boolean _isActive;
   protected DatagramStatusListener _listener;
   protected boolean _isTimeOutSet;
   protected TrafficLogger _tLogger;
   protected ConnectionListener _connectionListener;
   private URL _url;
   private String _name;
   protected static long DEFAULT_TIMEOUT = 30000;

   @Override
   public void close() {
      if (this._isActive) {
         this._transport.close(this);
         this._isActive = false;
         synchronized (this._datagrams) {
            this._datagrams.notifyAll();
         }

         int length;
         Datagram[] sendingDatagrams;
         synchronized (this._sendingDatagrams) {
            length = this._sendingDatagrams.length;
            if (length <= 0) {
               return;
            }

            sendingDatagrams = new Datagram[length];
            System.arraycopy(this._sendingDatagrams, 0, sendingDatagrams, 0, length);
         }

         for (int i = length - 1; i >= 0; i--) {
            this._transport.superCancel(sendingDatagrams[i]);
         }
      }
   }

   @Override
   public Connection openPrim(String name, int mode, boolean timeouts) {
      if ((mode & 3) != mode) {
         throw new IllegalArgumentException();
      }

      DatagramTransportBase transportBase = null;

      try {
         this._url = new URL(name);
      } catch (MalformedURLException e) {
         this._name = name;
      }

      String transportName = this.getClass().getName();
      int index = transportName.lastIndexOf(46);
      if (index == -1) {
         throw new IOException("Unable to find underlying transport class (1)");
      }

      transportName = transportName.substring(0, index + 1) + "Transport";
      transportBase = (DatagramTransportBase)TransportRegistry.get(transportName);
      if (timeouts && !this._isTimeOutSet) {
         this._timeout = DEFAULT_TIMEOUT;
      }

      this._transport = transportBase;
      this._addressBase = this.newDatagramAddressBase(name, false);
      this._receiveFilter = this.newDatagramAddressBase(name, true);
      if (transportBase != null) {
         transportBase.addConnection(this);
      }

      this._isActive = true;
      return this;
   }

   @Override
   public int getProperties(String _1) {
      throw null;
   }

   protected void checkForClosed() {
      if (!this._isActive) {
         throw new IOException("Connection closed");
      }
   }

   public void setTrafficLogger(TrafficLogger logger) {
      this._tLogger = logger;
   }

   public void cancel(Datagram datagram) {
      if (!this._isActive) {
         throw new IOException("Connection closed");
      }

      this._transport.superCancel(datagram);
   }

   protected boolean isAddressed(String address) {
      try {
         return this.isAddressed(this.newDatagramAddressBase(address, false));
      } catch (Throwable t) {
         return false;
      }
   }

   protected boolean isAddressed(DatagramAddressBase address) {
      return false;
   }

   public void copyFlagsInto(DatagramBase dg) {
      for (int mask = 1; mask != 0; mask <<= 1) {
         if ((this._validFlags & mask) != 0 && dg.getFlag(mask) == -1) {
            dg.setFlag(mask, (this._flags & mask) != 0);
         }
      }
   }

   public Datagram newDatagram() {
      this.checkForClosed();
      return this._transport.newDatagram(null, 0, 0, null);
   }

   public void datagramProcessed(int dgId) {
      if (dgId != 0) {
         this._transport.datagramProcessed(dgId);
      }
   }

   public void handleDatagramStatus(int datagramId, int event, Object context) {
      if (this._listener != null) {
         try {
            this._listener.updateDatagramStatus(datagramId, event, context);
            return;
         } catch (Throwable var5) {
         }
      }
   }

   public Datagram newDatagram(byte[] buffer) {
      this.checkForClosed();
      return this._transport.newDatagram(buffer, 0, buffer.length, null);
   }

   public int allocateDatagramId(Datagram datagram) {
      return this._transport.allocateDatagramId(datagram);
   }

   public void setConnectionListener(ConnectionListener listener) {
      this._connectionListener = listener;
   }

   public Datagram newDatagram(byte[] buffer, int offset, int length) {
      this.checkForClosed();
      return this._transport.newDatagram(buffer, offset, length, null);
   }

   public Datagram newDatagram(byte[] buffer, int offset, int length, String address) {
      this.checkForClosed();
      return this._transport.newDatagram(buffer, offset, length, address);
   }

   public DatagramAddressBase newDatagramAddressBase(String address, boolean swap) {
      return this._transport.newDatagramAddressBase(address, swap);
   }

   public DatagramAddressBase newDatagramAddressBase(DatagramAddressBase addressBase, boolean swap) {
      return this._transport.newDatagramAddressBase(addressBase, swap);
   }

   public void processReceivedDatagram(Datagram datagram) {
      synchronized (this._datagrams) {
         this._datagrams.enqueue(datagram);
         this._datagrams.notify();
      }

      if (this._connectionListener != null) {
         try {
            this._connectionListener.dataAvailable(this);
            return;
         } catch (Throwable var5) {
         }
      }
   }

   public DatagramStatusListener getDatagramStatusListener() {
      return this._listener;
   }

   public void setDatagramStatusListener(DatagramStatusListener listener) {
      this._listener = listener;
   }

   public byte[] setup(int callType, Object context) {
      return this._transport.setup(callType, context);
   }

   public void setTimeout(long timeout) {
      this._timeout = timeout;
      this._isTimeOutSet = true;
   }

   @Override
   public boolean isFlagSet(int flag) {
      return (this._validFlags & flag) != 0 && (this._flags & flag) != 0;
   }

   @Override
   public int getFlag(int flag) {
      if ((this._validFlags & flag) != 0) {
         return (this._flags & flag) != 0 ? 1 : 0;
      } else {
         return -1;
      }
   }

   @Override
   public void setFlag(int flag, boolean value) {
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
   public Object setProperty(String name, Object data) {
      if (this._properties == null) {
         this._properties = new Hashtable();
      }

      return this._properties.put(name, data);
   }

   @Override
   public Datagram newDatagram(byte[] buffer, int length, String address) {
      this.checkForClosed();
      return this._transport.newDatagram(buffer, 0, length, address);
   }

   @Override
   public Datagram newDatagram(byte[] buffer, int length) {
      this.checkForClosed();
      return this._transport.newDatagram(buffer, 0, length, null);
   }

   @Override
   public Datagram newDatagram(int length, String address) {
      this.checkForClosed();
      return this._transport.newDatagram(null, 0, length, address);
   }

   @Override
   public Datagram newDatagram(int length) {
      this.checkForClosed();
      return this._transport.newDatagram(null, 0, length, null);
   }

   @Override
   public void receive(Datagram datagram) {
      if (!this._isActive) {
         throw new IOException();
      }

      if (this._url != null) {
         if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)
            && !Firewall.getInstance().allowConnection(this._url.getScheme(), "", this.getProperties(this._url.toString()))) {
            throw new IOException("Permission denied");
         }
      } else if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)
         && !Firewall.getInstance().allowConnection(this._name, "", this.getProperties(this._name))) {
         throw new IOException("Permission denied");
      }

      DatagramBase receivedDatagram = null;
      datagram.reset();
      synchronized (this._datagrams) {
         if (this._datagrams.isEmpty()) {
            try {
               this._datagrams.wait(this._timeout);
            } catch (InterruptedException var6) {
            }

            if (this._datagrams.isEmpty()) {
               if (this._isActive) {
                  throw new InterruptedIOException();
               }

               throw new InterruptedIOException();
            }
         }

         receivedDatagram = (DatagramBase)this._datagrams.dequeue();
      }

      if (!(datagram instanceof DatagramBase)) {
         datagram.setAddress(receivedDatagram);
         datagram.setData(receivedDatagram.getData(), receivedDatagram.getOffset(), receivedDatagram.getLength());
      } else {
         ((DatagramBase)datagram).copy(receivedDatagram);
      }

      if (this._tLogger != null) {
         this._tLogger.bytesReceived(this, 1, receivedDatagram.getAddress(), receivedDatagram.getLength(), receivedDatagram.getData());
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void send(Datagram datagram) {
      this.checkForClosed();
      if (!(datagram instanceof DatagramBase)) {
         if (datagram.getAddress() == null) {
            datagram.setAddress(this._addressBase.getAddress());
         }
      } else {
         DatagramBase dgram = (DatagramBase)datagram;
         if (dgram.getAddressBase() == null) {
            dgram.setAddressBase(this._addressBase);
         }

         if (dgram.getDatagramStatusListener() == null) {
            dgram.setDatagramStatusListener(this._listener);
         }

         this.copyFlagsInto(dgram);
      }

      synchronized (this._sendingDatagrams) {
         Arrays.add(this._sendingDatagrams, datagram);
      }

      boolean var12 = false /* VF: Semaphore variable */;

      try {
         var12 = true;
         this._transport.superSend(datagram);
         var12 = false;
      } finally {
         if (var12) {
            synchronized (this._sendingDatagrams) {
               Arrays.remove(this._sendingDatagrams, datagram);
            }
         }
      }

      synchronized (this._sendingDatagrams) {
         Arrays.remove(this._sendingDatagrams, datagram);
      }

      if (this._tLogger != null) {
         this._tLogger.bytesTransmitted(this, 1, datagram.getAddress(), datagram.getLength(), datagram.getData());
      }
   }

   @Override
   public String getLocalAddress() {
      this.checkForClosed();
      int apn = 0;
      if (this._addressBase instanceof UdpAddress) {
         UdpAddress a = (UdpAddress)this._addressBase;
         String apns = a.getApn();

         try {
            if (apns != null) {
               apn = RadioInfo.getAccessPointNumber(apns);
            }
         } catch (RadioException var5) {
         }
      }

      byte[] ip = RadioInfo.getIPAddress(apn);
      StringBuffer temp = new StringBuffer();

      for (int i = 0; i < ip.length; i++) {
         temp.append(ip[i] & 255);
         temp.append('.');
      }

      temp.deleteCharAt(temp.length() - 1);
      return temp.toString();
   }

   @Override
   public int getLocalPort() {
      this.checkForClosed();
      return -1;
   }

   @Override
   public int getNominalLength() {
      this.checkForClosed();
      return this._transport.getNominalLength();
   }

   @Override
   public int getMaximumLength() {
      this.checkForClosed();
      return this._transport.getMaximumLength();
   }
}
