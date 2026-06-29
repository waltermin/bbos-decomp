package net.rim.device.cldc.io.ippp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.util.DataBuffer;

public class IPPPDatagramBase extends DatagramBase {
   protected byte _version = 16;
   protected int _sequence;
   protected byte _ipppFlags;
   protected int _connectionID;
   protected byte _protocol;
   protected byte _protocolVersion;
   protected String _specificUID;
   protected String _groupUID;
   private boolean _encrypted;
   private boolean _secure;
   protected String _connectionHandlerName;
   protected int _errorCode = 127;
   protected String _errorMessage = "";
   protected short _flowControlTimeout = 0;
   protected byte[] _reserved = new byte[2];
   public static final byte VERSION = 16;
   public static final byte FLAG_DATA = 0;
   public static final byte FLAG_CONNECT_REQUEST = 1;
   public static final byte FLAG_DISCONNECT_ORDER = 4;
   public static final byte FLAG_ERROR = -128;
   private static final int Min_FlowControlTimeout = 60;
   private static final int Max_FlowControlTimeout = 1800;

   public IPPPDatagramBase() {
      this(null, 0, 0);
   }

   public IPPPDatagramBase(byte[] buffer, int offset, int length) {
      super(buffer, offset, length);
   }

   public IPPPDatagramBase(byte[] buffer, int offset, int length, String address) {
      super(buffer, offset, length, address);
   }

   @Override
   public void simpleReset() {
      super.simpleReset();
      this._connectionID = 0;
      this._protocol = 0;
      this._specificUID = null;
      this._ipppFlags = 0;
      this._errorCode = 0;
      this._errorMessage = null;
      this._flowControlTimeout = 0;
      this._encrypted = false;
   }

   public void setIPPPFlags(byte flags) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public byte getIPPPFlags() {
      return this._ipppFlags;
   }

   public void addIPPPFlags(byte flags) {
      this._ipppFlags |= flags;
   }

   public void clearIPPPFlags(byte flags) {
      this._ipppFlags = (byte)(this._ipppFlags & ~flags);
   }

   public void clearIPPPFlags() {
      this._ipppFlags = 0;
   }

   public boolean testIPPPFlags(byte flags) {
      return (this._ipppFlags & flags) != 0;
   }

   public void setProtocol(byte protocol) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setProtocolVersion(byte protocolVersion) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setSpecificUID(String specificUID) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setConnectionID(int connectionID) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setGroupUID(String groupUID) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public byte getProtocol() {
      return this._protocol;
   }

   public byte getProtocolVersion() {
      return this._protocolVersion;
   }

   public String getSpecificUID() {
      return this._specificUID;
   }

   public int getConnectionID() {
      return this._connectionID;
   }

   public String getGroupUID() {
      return this._groupUID;
   }

   public int getErrorCode() {
      return this._errorCode;
   }

   public String getErrorMessage() {
      return this._errorMessage;
   }

   public void setErrorCode(int errorCode) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setErrorMessage(String errorMessage) {
      if (errorMessage != null) {
         this._errorMessage = errorMessage;
      }
   }

   @Override
   public void copy(DatagramBase srcDatagram) {
      IPPPDatagramBase ipppDatagram = (IPPPDatagramBase)srcDatagram;
      super.copy(ipppDatagram);
      this.setConnectionID(ipppDatagram.getConnectionID());
      this.setProtocol(ipppDatagram.getProtocol());
      this.setProtocolVersion(ipppDatagram.getProtocolVersion());
      this.setSpecificUID(ipppDatagram.getSpecificUID());
      this.setGroupUID(ipppDatagram.getGroupUID());
      this.setIPPPFlags(ipppDatagram.getIPPPFlags());
      this.setErrorCode(ipppDatagram.getErrorCode());
      this.setErrorMessage(ipppDatagram.getErrorMessage());
      this.setSequence(ipppDatagram.getSequence());
      this.setConnectionHandlerName(ipppDatagram.getConnectionHandlerName());
      this.setFlowControlTimeout(ipppDatagram.getFlowControlTimeout());
      this.setEncrypted(ipppDatagram.wasEncrypted());
      this.setDatagramSecure(ipppDatagram.wasDatagramSecure());
   }

   public void setSequence(int sequence) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setConnectionHandlerName(String connectionHandlerName) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean wasEncrypted() {
      return this._encrypted;
   }

   public void setEncrypted(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean wasDatagramSecure() {
      return this._secure;
   }

   public void setDatagramSecure(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean isServerConnection() {
      return this.getConnectionID() < 0;
   }

   public boolean isConnectRequest() {
      return this.testIPPPFlags((byte)1);
   }

   public boolean isDisconnectOrder() {
      return this.testIPPPFlags((byte)4);
   }

   public boolean isError() {
      return this.testIPPPFlags((byte)-128);
   }

   @Override
   public String getAddress() {
      return Integer.toString(this.getConnectionID());
   }

   public int getSequence() {
      return this._sequence;
   }

   public short getFlowControlTimeout() {
      return this._flowControlTimeout;
   }

   public void setFlowControlTimeout(short flowControlTimeout) {
      if (flowControlTimeout >= 60 && flowControlTimeout <= 1800) {
         this._flowControlTimeout = flowControlTimeout;
      }
   }

   public String getConnectionHandlerName() {
      if (this._connectionHandlerName == null) {
         this._connectionHandlerName = "";
      }

      return this._connectionHandlerName;
   }

   public void writeProtocolData(DataOutput _1) {
      throw null;
   }

   public void readProtocolData(DataInput _1) {
      throw null;
   }

   public short getPort() {
      throw null;
   }

   @Override
   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("VER=").append(this._version).append('\n');
      buffer.append("CID=").append(this.getConnectionID()).append('\n');
      buffer.append("SEQ=").append(this.getSequence()).append('\n');
      buffer.append("CMD=");
      switch (this.getIPPPFlags() & 0xFF) {
         case 0:
            buffer.append("DT");
            break;
         case 1:
            buffer.append("CR");
            break;
         case 4:
            buffer.append("DO");
            break;
         case 5:
            buffer.append("NF");
            break;
         case 128:
            buffer.append("ER");
            break;
         default:
            buffer.append('X');
      }

      buffer.append("FCT=").append(this.getFlowControlTimeout()).append('\n');
      buffer.append('\n');
      buffer.append("LEN=").append(this.getData().length).append('\n');
      return buffer.toString();
   }

   public void writeTo(DataBuffer out) throws IOException {
      out.writeByte(16);
      out.writeInt(this.getConnectionID());
      out.writeByte(this.getSequence());
      out.writeByte(this.getIPPPFlags());
      out.writeShort(this.getFlowControlTimeout());
      out.write(this._reserved);
      switch (this.getIPPPFlags() & 0xFF) {
         case 1:
         case 5:
            out.writeUTF(this.getConnectionHandlerName());
            out.writeByte(this.getProtocol());
            out.writeByte(this.getProtocolVersion());
            this.writeProtocolData(out);
         case 0:
         case 4:
            out.write(this.getArray());
            return;
         case 128:
            out.write(this.getErrorCode());
            out.write(this.getErrorMessage().getBytes());
            return;
         default:
            throw new IOException("Invalid IPPP flags written");
      }
   }

   public void readFrom(DataBuffer in) throws IOException {
      this._version = in.readByte();
      if (this._version != 16) {
         throw new IOException("Incompatible version in the received IPPP datagram");
      }

      this.setConnectionID(in.readInt());
      this.setSequence(in.readByte() & 255);
      int flags = in.readByte() & 255;
      this.setIPPPFlags((byte)flags);
      this.setFlowControlTimeout(in.readShort());
      in.readFully(this._reserved);
      switch (flags) {
         case 1:
         case 5:
            this.setConnectionHandlerName(in.readUTF());
            this.setProtocol(in.readByte());
            this.setProtocolVersion(in.readByte());
            this.readProtocolData(in);
         case 0:
         case 4:
            this.setData(in.getArray(), in.getArrayPosition(), in.available());
            return;
         case 128:
            int errorCode = in.readByte() & 255;
            this.setErrorCode(errorCode);
            int errorMsgLength = in.available();
            byte[] errorMsgBytes = new byte[errorMsgLength];
            in.read(errorMsgBytes);
            this.setErrorMessage(new String(errorMsgBytes));
            Object var6 = null;
            return;
         default:
            throw new IOException("Invalid IPPP flags read");
      }
   }
}
