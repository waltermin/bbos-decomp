package net.rim.device.cldc.io.sms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import javax.microedition.io.StreamConnection;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.IOPortAlreadyBoundException;
import net.rim.device.api.io.SmsAddress;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.cldc.io.nativebase.NativeConnectionBase;
import net.rim.device.internal.firewall.Firewall;
import net.rim.vm.Process;
import net.rim.vm.TraceBack;
import net.rim.vm.WeakReference;

public final class Protocol extends NativeConnectionBase implements MessageConnection, StreamConnection {
   private Protocol$MessageSegmentQueue _messageSegmentQueue;
   private boolean _isServerMode;
   private String _address;
   private int _port;
   private Protocol$WMAListeningThread _wlt;
   private MessageListener _listener;
   private Vector _messagequeue = new Vector();
   private boolean _stop;
   public static final int SRC_PORT_INDEX = 0;
   public static final int DEST_PORT_INDEX = 1;
   public static String PROPERTY_USER_DATA_HEADER_LENGTH = "sms-udh-length";
   private static final long ID = 5140128676453846469L;
   private static Integer USER_DATA_HEADER_LENGTH_INTEGER = new Integer(6);
   private static final int MESSAGE_QUEUE_LENGTH = 10;
   private static IntHashtable _portTable;
   private static final int[] RESERVED_PORTS = new int[]{
      2805,
      2923,
      2948,
      2949,
      5502,
      5503,
      5508,
      5511,
      5512,
      9200,
      9201,
      9202,
      9203,
      9207,
      49996,
      49999,
      555417856,
      1661009927,
      529165580,
      1916883543,
      -1370457650,
      16803175,
      -345171610,
      1929445491,
      7618858,
      12956929,
      1761869828,
      13167647,
      416518,
      1107712774,
      100683087,
      1146553947,
      100695888,
      2137326188,
      1970341376,
      6649189,
      671612936,
      467227,
      1742219272,
      -330055925,
      1094780928,
      1849755648,
      751475,
      1936605448,
      7539575,
      1852850440,
      1091043569,
      1091043444,
      51980149,
      7562601,
      1718960648,
      134220646,
      1416203842,
      6647929,
      1712341768,
      1124597937,
      -1086273249,
      1124597875,
      6657633,
      745423624,
      1953775973,
      7544123,
      2057782024,
      1124597882
   };

   public final int getPort() {
      return this._port;
   }

   public final Message receiveInternal() {
      if (!this._isServerMode) {
         throw new IOException("operation not permitted on a client connection");
      }

      synchronized (this._messagequeue) {
         if (this._messagequeue.size() > 0) {
            Message m = (Message)this._messagequeue.firstElement();
            this._messagequeue.removeElement(m);
            return m;
         }
      }

      return this.doReceive();
   }

   @Override
   public final int numberOfSegments(Message msg) {
      MessageImpl mi = (MessageImpl)msg;
      int udhPort = 0;
      String address = mi.getAddress();
      if (address != null) {
         if (!address.startsWith("sms://")) {
            return 0;
         }

         int colon = address.indexOf(":");
         colon = address.indexOf(":", colon + 1);
         if (colon != -1) {
            udhPort = 6;
         }
      }

      int messageCoding = mi.getEncoding();
      int characters = mi.getBuffer().length / SMSPacketHeader.getBytesPerCharacter(messageCoding);
      return SMSPacketHeader.getSegments(characters, messageCoding, udhPort);
   }

   @Override
   public final Message receive() {
      if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)
         && !Firewall.getInstance().allowConnection("sms_receive", null, this.getProperties(null))) {
         throw new SecurityException("Permission denied");
      } else {
         return this.receiveInternal();
      }
   }

   @Override
   public final Message newMessage(String type, String address) {
      if (!type.equals("binary") && !type.equals("text")) {
         throw new IllegalArgumentException("this message type is not supported");
      }

      Message m = null;
      if (type.equals("binary")) {
         return new BinaryMessageImpl(address);
      }

      if (type.equals("text")) {
         m = new TextMessageImpl(address);
      }

      return m;
   }

   @Override
   public final void send(Message msg) {
      if (!ControlledAccess.verifyRRISignatures(true) && !Firewall.getInstance().allowConnection("sms_send", null, this.getProperties(null))) {
         throw new SecurityException("Permission denied");
      }

      if (!super._isActive) {
         throw new IOException("operation not permitted on a closed connection");
      }

      MessageImpl mi = (MessageImpl)msg;
      String addr = mi.getAddress();
      if (addr == null) {
         throw new IllegalArgumentException("Message has no address");
      }

      if (addr.startsWith("sms:")) {
         addr = addr.substring(4);
      }

      DatagramBase msgDatagram = (DatagramBase)super._transport.newDatagram(null, 0, 0, addr);
      msgDatagram.setAddressBase(new SmsAddress(addr));
      byte[] buffer = mi.getBuffer();
      if (buffer != null) {
         msgDatagram.setData(buffer, 0, buffer.length);
      }

      SmsAddress a = null;
      a = (SmsAddress)msgDatagram.getAddressBase();
      if (a == null) {
         throw new IllegalArgumentException("Message has no address");
      }

      int destport = this._port;
      int[] ports = a.getPorts();
      if (ports != null && ports.length > 0) {
         destport = ports[0];
      }

      for (int i = 0; i < RESERVED_PORTS.length; i++) {
         if (RESERVED_PORTS[i] == destport) {
            throw new SecurityException("Cannot open connection on a reserved port");
         }
      }

      if (ports != null) {
         byte[] portdata = new byte[]{(byte)(destport >> 8 & 0xFF), (byte)(destport & 0xFF), (byte)(this._port >> 8 & 0xFF), (byte)(this._port & 0xFF)};
         if ((RadioInfo.getActiveWAFs() & 2) == 2) {
            a.getHeader().setProtocolMeaning(255);
            byte[] originaldata = Arrays.copy(msgDatagram.getData());
            msgDatagram.setLength(0);
            msgDatagram.write(portdata);
            msgDatagram.write(originaldata);
         } else {
            SmsUtil.encode(msgDatagram, (byte)5, portdata);
            msgDatagram.setProperty(PROPERTY_USER_DATA_HEADER_LENGTH, USER_DATA_HEADER_LENGTH_INTEGER);
         }
      }

      SMSPacketHeader header = ((SmsAddress)msgDatagram.getAddressBase()).getHeader();
      header.setMessageCoding(mi.getEncoding());
      this.allocateDatagramId(msgDatagram);
      ((Transport)super._transport).send(msgDatagram, a, null, null, msgDatagram.getDatagramId());
   }

   @Override
   public final void setMessageListener(MessageListener l) {
      if (!ControlledAccess.verifyRRISignatures(true) && !Firewall.getInstance().allowConnection("sms_receive", null, false)) {
         throw new SecurityException("Permission denied");
      }

      if (!this._isServerMode) {
         throw new IOException("operation not permitted on a client connection");
      }

      if (!super._isActive) {
         throw new IOException("operation not permitted on a closed connection");
      }

      synchronized (this) {
         if (this._wlt == null) {
            this._wlt = new Protocol$WMAListeningThread(this, null);
            this._wlt.start();
            ((SmsAddress)super._receiveFilter).getHeader().setPeerAddress(null);
         }

         this._listener = l;
         ((Transport)super._transport).addOutboundMessageListener(l);
      }
   }

   @Override
   public final Message newMessage(String type) {
      String address = this._address;
      if (address != null && address.startsWith("//")) {
         address = "sms:" + address;
      }

      return this.newMessage(type, this._isServerMode ? null : address);
   }

   @Override
   public final InputStream openInputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   @Override
   public final DataInputStream openDataInputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   @Override
   public final OutputStream openOutputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   @Override
   public final void send(Datagram datagram) {
      if (!ControlledAccess.verifyRRISignatures(true) && !Firewall.getInstance().allowConnection("sms_send", null, this.getProperties(null))) {
         throw new SecurityException("Permission denied");
      }

      super.send(datagram);
   }

   @Override
   public final int getNominalLength() {
      return this.getMaximumLength();
   }

   @Override
   public final boolean isAddressed(DatagramAddressBase addressBase) {
      if (this._port == 0) {
         return true;
      }

      SmsAddress a = (SmsAddress)addressBase;
      SMSPacketHeader header = a.getHeader();
      int[] ports = a.getPorts();
      return ports == null || ports.length < 1 ? header.getProtocolMeaning() == 255 : ports[0] == this._port;
   }

   @Override
   public final int getMaximumLength() {
      int headerSize = 0;
      int[] ports = ((SmsAddress)super._addressBase).getPorts();
      if (ports != null) {
         headerSize = SmsUtil.getHeaderSize(ports[0]);
      }

      return super._transport.getMaximumLength() - headerSize;
   }

   @Override
   public final void close() {
      if (this._wlt != null) {
         synchronized (this) {
            this._stop = true;
         }
      }

      super.close();
   }

   private final boolean hasStoreMessage(DatagramBase d) {
      if (this._messageSegmentQueue == null) {
         this._messageSegmentQueue = new Protocol$MessageSegmentQueue(10);
      }

      Integer refNumberInteger = (Integer)d.getProperty(SmsUtil.PROPERTY_REF_NUMBER);
      if (refNumberInteger != null) {
         int refNumber = refNumberInteger;
         SmsAddress a = (SmsAddress)d.getAddressBase();
         String address = SmsAddress.makeAddress(false, a.getHeader(), null);
         String key = address + '-' + refNumber;

         for (int i = 0; i < this._messageSegmentQueue.messages.length; i++) {
            if (this._messageSegmentQueue.messages[i] != null && this._messageSegmentQueue.messages[i].getKey().equals(key)) {
               return true;
            }
         }
      }

      return false;
   }

   private final Message storeMessage(DatagramBase d) {
      if (this._messageSegmentQueue == null) {
         this._messageSegmentQueue = new Protocol$MessageSegmentQueue(10);
      }

      int refNumber = -1;
      int totalSegments = 1;
      Integer ref = (Integer)d.getProperty(SmsUtil.PROPERTY_REF_NUMBER);
      if (ref == null) {
         return null;
      }

      refNumber = ref;
      ref = (Integer)d.getProperty(SmsUtil.PROPERTY_TOTAL_SEGMENTS);
      if (ref == null) {
         return null;
      }

      totalSegments = ref;
      SmsAddress a = (SmsAddress)d.getAddressBase();
      String address = SmsAddress.makeAddress(false, a.getHeader(), null);
      String key = address + '-' + refNumber;
      Message msg = null;
      Protocol$StoreMessage sm = null;

      int i;
      for (i = 0; i < this._messageSegmentQueue.messages.length; i++) {
         if (this._messageSegmentQueue.messages[i] != null && this._messageSegmentQueue.messages[i].getKey().equals(key)) {
            sm = this._messageSegmentQueue.messages[i];
            break;
         }
      }

      if (sm != null) {
         msg = sm.add(d);
         if (msg != null) {
            this._messageSegmentQueue.messages[i] = null;
            return msg;
         } else {
            return null;
         }
      } else {
         sm = new Protocol$StoreMessage(totalSegments, key);
         msg = sm.add(d);
         if (msg == null) {
            this._messageSegmentQueue.add(sm);
            return null;
         } else {
            return msg;
         }
      }
   }

   static final Message makeMessage(DatagramBase datagram) {
      SmsAddress a = (SmsAddress)datagram.getAddressBase();
      SMSPacketHeader header = a.getHeader();
      long date = header.getTimestamp();
      int coding = header.getMessageCoding();
      String address = datagram.getAddress();
      int colon = address.indexOf(58);
      int or = address.indexOf(124);
      String newAddress = address.substring(0, colon + 1) + address.substring(or + 1);
      if (newAddress.startsWith("//")) {
         newAddress = "sms:" + newAddress;
      }

      MessageImpl msg = null;
      switch (coding) {
         case 0:
            msg = new TextMessageImpl(newAddress);
            msg.setBuffer(datagram.getData());
            msg.setEncoding(0);
            break;
         case 1:
         default:
            msg = new BinaryMessageImpl(newAddress);
            msg.setBuffer(datagram.getData());
            msg.setEncoding(1);
            break;
         case 2:
            msg = new TextMessageImpl(newAddress);
            msg.setBuffer(datagram.getData());
            msg.setEncoding(2);
      }

      msg.setSCAddress(header.getSCAddress());
      msg._date = new Date(date);
      return msg;
   }

   private final Message doReceive() {
      DatagramBase d = new DatagramBase();

      while (true) {
         synchronized (this) {
            if (this._stop) {
               return null;
            }
         }

         this.receive(d);
         SmsAddress a = (SmsAddress)d.getAddressBase();
         SMSPacketHeader header = a.getHeader();
         Integer numSegments = (Integer)d.getProperty(SmsUtil.PROPERTY_TOTAL_SEGMENTS);
         Integer segId = (Integer)d.getProperty(SmsUtil.PROPERTY_SEGMENT_NUMBER);
         if (numSegments == null || numSegments == 1) {
            return makeMessage(d);
         }

         if (segId == null || segId <= 1 || header.getProtocolMeaning() != 255 || this.hasStoreMessage(d)) {
            Message msg = this.storeMessage(d);
            if (msg != null) {
               return msg;
            }
         }
      }
   }

   @Override
   protected final boolean checkNetwork() {
      return (RadioInfo.getSupportedWAFs() & 11) != 0;
   }

   @Override
   public final void receive(Datagram datagram) {
      if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)
         && !Firewall.getInstance().allowConnection("sms_receive", null, this.getProperties(null))) {
         throw new SecurityException("Permission denied");
      }

      super.receive(datagram);
   }

   private final boolean validateString(String str) {
      for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         if (!Character.isDigit(c) && c != '+' && c != ':') {
            return false;
         }
      }

      return true;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      if (!this.validateString(name.substring(2))) {
         throw new IllegalArgumentException("Illegal opening string");
      }

      int colon = name.indexOf(58);
      String portString = null;
      if (colon != 2) {
         if (mode == 1) {
            throw new IllegalArgumentException("Read connection cannot be opened with given parameters");
         } else {
            this._address = name.substring(0, name.length());
            if (this._address.length() == 0) {
               throw new IllegalArgumentException("Illegal opening string");
            } else {
               return super.openPrim(name, mode, timeouts);
            }
         }
      } else {
         this._isServerMode = true;
         portString = name.substring(colon + 1);
         if (portString.length() == 0) {
            throw new IllegalArgumentException("Illegal opening string");
         }

         this._port = Integer.parseInt(portString);
         if (!CodeModuleManager.isMidlet(Process.currentProcess().getModuleHandle()) || this._port >= 0 && this._port <= 65535) {
            synchronized (_portTable) {
               if (_portTable.containsKey(this._port)) {
                  WeakReference ref = (WeakReference)_portTable.get(this._port);
                  if (ref.get() != null && ((Protocol)ref.get())._isActive) {
                     throw new IOPortAlreadyBoundException("SMS Port already in use");
                  }
               }

               _portTable.put(this._port, new WeakReference(this));
               return super.openPrim(name, mode, timeouts);
            }
         } else {
            throw new IllegalArgumentException("Illegal port number");
         }
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _portTable = (IntHashtable)ar.getOrWaitFor(5140128676453846469L);
      if (_portTable == null) {
         _portTable = new IntHashtable();
         ar.put(5140128676453846469L, _portTable);
      }
   }
}
