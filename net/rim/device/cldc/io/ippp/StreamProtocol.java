package net.rim.device.cldc.io.ippp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.io.IOCancelledException;
import net.rim.device.api.io.SocketConnectionEnhanced;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.utility.PacketLogger;

public final class StreamProtocol implements SocketConnection, DatagramStatusListener, SocketConnectionEnhanced {
   private SocketInputStream _inputStream;
   private SocketOutputStream _outputStream;
   private DataInputStream _dataInputStream;
   private DataOutputStream _dataOutputStream;
   private DatagramProtocol _datagramProtocol;
   private StreamProtocolListener _streamProtocolListener;
   private boolean _isClosed;
   private boolean _disconnectOrderSent;
   private boolean _closeRequested;
   private boolean _sendFirstEmptyPacket;
   private PacketLogger _packetLogger;
   private boolean _isListenConnection;
   private int[] _socketOptions = new int[]{
      -1,
      -1,
      -1,
      -1,
      -1,
      207814912,
      1816300141,
      -1507628151,
      16807074,
      1140014960,
      594767724,
      1870004480,
      290219371,
      1979777035,
      1382378351,
      1650552482,
      -1258225538,
      262341,
      -333485820,
      526976000
   };
   private static final String MSG_Connection_Broken = "Connection broken";
   private static final int MAX_DATAGRAM_SIZE = 62000;
   private static final byte[] zeroByteArray = new byte[0];

   @Override
   public final void close() {
      if (!this.closeConnection()) {
         this._closeRequested = true;
         this.clean();
      }
   }

   public final void setStreamProtocolListener(StreamProtocolListener newStreamProtocolListener) {
      this._streamProtocolListener = newStreamProtocolListener;
   }

   public final boolean isChannelEncrypted() {
      return this._datagramProtocol.isChannelEncrypted();
   }

   public final String getSpecificUID() {
      return this._datagramProtocol.getSpecificUID();
   }

   public final String getGroupUID() {
      return this._datagramProtocol.getGroupUID();
   }

   public final boolean isClosed() {
      return this._isClosed;
   }

   public final synchronized void sendOutput(byte[] output, int offset, int length) {
      boolean closed = false;
      if (this._outputStream != null && this._outputStream.isClosed() && this._inputStream != null && this._inputStream.isClosed()) {
         closed = true;
      }

      this.sendOutput(output, offset, length, closed);
   }

   public final DataBuffer getMoreInput() {
      if (this._outputStream != null) {
         this._outputStream.flush();
      }

      IPPPDatagramBase datagramReceived = (IPPPDatagramBase)this._datagramProtocol.newDatagram();
      this._datagramProtocol.receive(datagramReceived);
      if (this._packetLogger._lowLoggingEnabled) {
         this._packetLogger
            .logPacket(
               datagramReceived.getArray(),
               datagramReceived.getArrayPosition(),
               datagramReceived.available(),
               "IPPP:" + this._datagramProtocol.getConnectionID() + ' ' + this.getGroupUID() + ' ' + this.getSpecificUID(),
               false
            );
      }

      return datagramReceived;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void inputStreamClosed() {
      this._datagramProtocol.cancelReceiving();
      if (this._closeRequested) {
         try {
            this.closeConnection();
         } catch (Throwable var3) {
            e.printStackTrace();
            return;
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void outputStreamClosed() {
      if (this._closeRequested) {
         try {
            this.closeConnection();
         } catch (Throwable var3) {
            e.printStackTrace();
            return;
         }
      }
   }

   final int getOutputStreamSize() {
      return this._datagramProtocol.getDataSize();
   }

   @Override
   public final OutputStream openOutputStream() throws IOCancelledException {
      if (this._outputStream == null) {
         this._outputStream = new SocketOutputStream(this, this._sendFirstEmptyPacket);
      } else if (this._outputStream.isClosed()) {
         throw new IOCancelledException();
      }

      return this._outputStream;
   }

   @Override
   public final DataInputStream openDataInputStream() throws IOException {
      this.checkClosed();
      if (this._dataInputStream == null) {
         this._dataInputStream = new DataInputStream(this.openInputStream());
         return this._dataInputStream;
      } else {
         throw new IOException("Stream already open");
      }
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      if (this._dataOutputStream == null) {
         this._dataOutputStream = new DataOutputStream(this.openOutputStream());
      }

      return this._dataOutputStream;
   }

   @Override
   public final void updateDatagramStatus(int dgId, int code, Object context) {
      if (this._streamProtocolListener != null) {
         this._streamProtocolListener.event(dgId, code, context);
      }
   }

   @Override
   public final InputStream openInputStream() throws IOException, IOCancelledException {
      label31:
      try {
         OutputStream outs = this.openOutputStream();
         outs.write(zeroByteArray);
      } finally {
         break label31;
      }

      if (this._inputStream == null) {
         this._inputStream = new SocketInputStream(this);
         return this._inputStream;
      } else if (this._inputStream.isClosed()) {
         throw new IOCancelledException();
      } else {
         throw new IOException("Stream already open");
      }
   }

   @Override
   public final String getAddress() {
      this.checkClosed();
      return this._datagramProtocol.getURL().getHost();
   }

   @Override
   public final String getLocalAddress() {
      this.checkClosed();
      return new String(RadioInfo.getIPAddress(0));
   }

   @Override
   public final int getLocalPort() {
      this.checkClosed();
      if (this._isListenConnection) {
         return this._datagramProtocol.getURL().getPort();
      }

      int value = this._datagramProtocol.getConnectionID() & 65535;
      return value == 0 ? 48879 : value;
   }

   @Override
   public final int getPort() {
      this.checkClosed();
      if (this._isListenConnection) {
         int value = this._datagramProtocol.getConnectionID() & 65535;
         return value == 0 ? 48879 : value;
      } else {
         return this._datagramProtocol.getURL().getPort();
      }
   }

   @Override
   public final int getSocketOption(byte option) {
      this.checkClosed();
      if (option >= 0 && option <= 4) {
         return this._socketOptions[option];
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void setSocketOption(byte option, int value) {
      this.checkClosed();
      if (option >= 0 && option <= 4 && value >= 0) {
         this._socketOptions[option] = value;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void setSocketOptionEx(short option, long value) {
      this.checkClosed();
      if (option == 256 && value >= 0) {
         this._datagramProtocol.setTimeout(value);
      } else {
         this.setSocketOption((byte)option, (int)value);
      }
   }

   @Override
   public final long getSocketOptionEx(short option) {
      this.checkClosed();
      return option == 256 ? (int)this._datagramProtocol.getTimeout() : this.getSocketOption((byte)option);
   }

   private final boolean closeConnection() {
      if (!this._isClosed) {
         if (this._inputStream != null && !this._inputStream.isClosed() || this._outputStream != null && !this._outputStream.isClosed()) {
            return false;
         }

         this._isClosed = true;
         boolean sendDisconnect = this._datagramProtocol.cancelOutboundPackets();
         if (!this._disconnectOrderSent) {
            this._disconnectOrderSent = true;
            if (sendDisconnect) {
               SocketDatagram txDatagram = (SocketDatagram)this._datagramProtocol.newDatagram();
               txDatagram.addIPPPFlags((byte)4);

               try {
                  this._datagramProtocol.send(txDatagram);
               } catch (ConnectionClosedException var4) {
               }
            }

            this._datagramProtocol.close();
            return true;
         }
      }

      return true;
   }

   public StreamProtocol(DatagramProtocol datagramProtocol, boolean sendFirstEmptyPacket, boolean listenConnection) {
      this._datagramProtocol = datagramProtocol;
      this._datagramProtocol.setDatagramStatusListener(this);
      this._sendFirstEmptyPacket = sendFirstEmptyPacket;
      this._packetLogger = PacketLogger.getInstance();
      this._isListenConnection = listenConnection;
   }

   private final void sendOutput(byte[] output, int offset, int length, boolean close) {
      if (output != null) {
         int endOffset = offset + length;

         do {
            SocketDatagram txDatagram = (SocketDatagram)this._datagramProtocol.newDatagram();
            int bytesToWrite = Math.min(length, 62000);
            txDatagram.write(output, offset, bytesToWrite);
            if (close && offset + bytesToWrite >= endOffset) {
               this._disconnectOrderSent = true;
               txDatagram.addIPPPFlags((byte)4);
            }

            this._datagramProtocol.send(txDatagram);
            if (this._packetLogger._lowLoggingEnabled) {
               this._packetLogger
                  .logPacket(
                     output,
                     offset,
                     bytesToWrite,
                     "IPPP:" + this._datagramProtocol.getConnectionID() + ' ' + this.getGroupUID() + ' ' + this.getSpecificUID(),
                     true
                  );
            }

            offset += bytesToWrite;
            length -= bytesToWrite;
         } while (offset < endOffset);
      }
   }

   private final void clean() {
      this._inputStream = null;
      this._outputStream = null;
      this._dataInputStream = null;
      this._dataOutputStream = null;
      this._streamProtocolListener = null;
   }

   private final void checkClosed() throws IOException {
      if (this._isClosed) {
         throw new IOException();
      }
   }
}
