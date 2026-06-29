package net.rim.device.cldc.io.tcpsocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.SocketConnection;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.internal.io.BoundNativeSocketListener;
import net.rim.device.internal.io.NativeSocket;
import net.rim.device.internal.io.NativeSocketEventDispatcher;
import net.rim.device.internal.io.PortAssigner;

final class TcpSocket implements SocketConnection, BoundNativeSocketListener {
   private Object _readLock;
   private Object _writeLock;
   private NativeSocket _nativeSocket;
   private int _outputStreamState;
   private int _inputStreamState;
   private InputStream _inStream;
   private OutputStream _outStream;
   private byte[] _readBuffer = new byte[1500];
   private int _readBufferLength;
   private int _readBufferOffset;
   private Tunnel _tunnel;
   private TcpAddress _address;
   private static final int STREAM_STATE_NOT_OPENED;
   private static final int STREAM_STATE_OPENED;
   private static final int STREAM_STATE_SHUTDOWN;
   private static final int STREAM_STATE_CLOSED;
   private static final int READ_BUFFER_SIZE;
   private static final String STR_STREAM_IS_ALREADY_CLOSED;
   private static final String STR_STREAM_IS_ALREADY_OPEN;

   @Override
   public final void close() {
      if ((this._outputStreamState == 0 || this._outputStreamState == 2) && this._outStream != null) {
         this._outStream.close();
      }

      if ((this._inputStreamState == 0 || this._inputStreamState == 2) && this._inStream != null) {
         this._inStream.close();
      }

      this.releaseSocketIfNecessary();
   }

   final void write(byte[] data, int offset, int length) {
      synchronized (this._writeLock) {
         if (this._outputStreamState == 3) {
            throw new IOException();
         }

         if (data != null && offset >= 0 && length >= 0 && offset + length <= data.length) {
            while (length > 0) {
               int result = this._nativeSocket.send(data, offset, length, 0);
               if (result == 0) {
                  try {
                     this._writeLock.wait();
                  } catch (InterruptedException var8) {
                  }
               } else {
                  offset += result;
                  length -= result;
               }
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public final void connect(int ipv4Addr, int destPort) {
      if (this._nativeSocket == null) {
         throw new IOException();
      }

      this._nativeSocket.connectIPv4(ipv4Addr, destPort);
   }

   final void write(int data) {
      synchronized (this._writeLock) {
         if (this._outputStreamState == 3) {
            throw new IOException();
         }

         while (true) {
            int result = this._nativeSocket.send(data, 0);
            if (result != 0) {
               return;
            }

            try {
               this._writeLock.wait();
            } catch (InterruptedException var6) {
            }
         }
      }
   }

   final int read(byte[] data, int offset, int length) {
      synchronized (this._readLock) {
         if (this._inputStreamState == 3) {
            throw new IOException();
         }

         if (length == 0) {
            return 0;
         }

         if (data != null && offset >= 0 && length >= 0 && offset + length <= data.length) {
            if (this._readBufferOffset < this._readBufferLength) {
               int toRead = Math.min(this._readBufferLength - this._readBufferOffset, length);
               System.arraycopy(this._readBuffer, this._readBufferOffset, data, offset, toRead);
               offset += toRead;
               length -= toRead;
               this._readBufferOffset += toRead;
               return toRead;
            }

            do {
               int numRead = 0;
               if (length < 1500) {
                  this._readBufferLength = this._nativeSocket.receive(this._readBuffer, 0, this._readBuffer.length, 0);
                  this._readBufferOffset = 0;
                  int toRead = Math.min(this._readBufferLength - this._readBufferOffset, length);
                  System.arraycopy(this._readBuffer, this._readBufferOffset, data, offset, toRead);
                  offset += toRead;
                  this._readBufferOffset += toRead;
                  numRead = toRead;
               } else {
                  numRead = this._nativeSocket.receive(data, offset, length, 0);
               }

               if (numRead != 0) {
                  return numRead;
               }

               if (this._inputStreamState == 2) {
                  return -1;
               }

               try {
                  this._readLock.wait();
               } catch (InterruptedException var8) {
               }

               if (this._inputStreamState == 2) {
                  return -1;
               }
            } while (this._inputStreamState != 3);

            throw new IOException();
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   final int available() {
      synchronized (this._readLock) {
         return this._readBufferLength - this._readBufferOffset + this._nativeSocket.available();
      }
   }

   final int read() {
      synchronized (this._readLock) {
         if (this._inputStreamState == 3) {
            throw new IOException();
         }

         if (this._readBufferOffset < this._readBufferLength) {
            return this._readBuffer[this._readBufferOffset++];
         }

         do {
            this._readBufferLength = this._nativeSocket.receive(this._readBuffer, 0, this._readBuffer.length, 0);
            this._readBufferOffset = 0;
            if (this._readBufferLength != 0) {
               return this._readBuffer[this._readBufferOffset++];
            }

            if (this._inputStreamState == 2) {
               return -1;
            }

            try {
               this._readLock.wait();
            } catch (InterruptedException var4) {
            }
         } while (this._inputStreamState != 3);

         throw new IOException();
      }
   }

   final void inputClosed() {
      this._inputStreamState = 3;
      this._inStream = null;
      synchronized (this._readLock) {
         this._readLock.notify();
      }

      this.releaseSocketIfNecessary();
   }

   final void outputClosed() {
      this._outputStreamState = 3;
      this._outStream = null;
      synchronized (this._writeLock) {
         this._writeLock.notify();
      }

      this.releaseSocketIfNecessary();
   }

   public final void init() {
      this._nativeSocket.create(this._tunnel.getIdentifier(), 1, 6, this._address.getLocalPort());
      this._inStream = new TcpInputStream(this);
      this._outStream = new TcpOutputStream(this);
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return new DataInputStream(this.openInputStream());
   }

   @Override
   public final OutputStream openOutputStream() {
      if (this._outStream != null && this._nativeSocket != null) {
         switch (this._outputStreamState) {
            case 0:
               this._outputStreamState = 1;
               return this._outStream;
            case 1:
               throw new IOException("Stream already open");
            case 2:
            case 3:
            default:
               throw new IOException("Stream already closed");
         }
      } else {
         throw new IOException();
      }
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      return new DataOutputStream(this.openOutputStream());
   }

   @Override
   public final InputStream openInputStream() {
      if (this._inStream != null && this._nativeSocket != null) {
         switch (this._inputStreamState) {
            case 0:
               this._inputStreamState = 1;
               return this._inStream;
            case 1:
               throw new IOException("Stream already open");
            case 2:
            case 3:
            default:
               throw new IOException("Stream already closed");
         }
      } else {
         throw new IOException();
      }
   }

   @Override
   public final int getPort() {
      return this._address.getConnectionDestinationPort();
   }

   @Override
   public final String getAddress() {
      return this._address.getConnectionAddress();
   }

   @Override
   public final int getLocalPort() {
      return this._address.getLocalPort();
   }

   @Override
   public final String getLocalAddress() {
      return this._address.getLocalAddress();
   }

   @Override
   public final int getSocketOption(byte option) {
      return 0;
   }

   @Override
   public final void setSocketOption(byte option, int value) {
   }

   @Override
   public final int getSocketId() {
      return this._nativeSocket == null ? -1 : this._nativeSocket.getSocketId();
   }

   @Override
   public final void socketDataReady() {
      synchronized (this._readLock) {
         this._readLock.notify();
      }
   }

   @Override
   public final void socketWriteReady() {
      synchronized (this._writeLock) {
         this._writeLock.notify();
      }
   }

   @Override
   public final void socketDisconnected() {
      this._inputStreamState = 2;
      synchronized (this._readLock) {
         this._readLock.notify();
      }

      this.outputClosed();
   }

   public TcpSocket(Tunnel tunnel, TcpAddress address) {
      this._readLock = new Object();
      this._writeLock = new Object();
      this._tunnel = tunnel;
      this._nativeSocket = new NativeSocket();
      this._address = address;
      PortAssigner.getInstance(6).registerConnection(address.getLocalPort(), this, tunnel.getConfig().getName());
      NativeSocketEventDispatcher.addListener(this);
   }

   private final void releaseSocketIfNecessary() {
      if (this._nativeSocket != null) {
         if (this._inputStreamState == 3 && this._outputStreamState == 3) {
            try {
               this._nativeSocket.close();
            } catch (IOException var2) {
            }

            this._nativeSocket = null;
         }

         NativeSocketEventDispatcher.removeListener(this);
         PortAssigner.getInstance(6).deregisterConnection(this._address.getLocalPort(), this, this._tunnel.getConfig().getName());
         this._tunnel.close();
      }
   }
}
