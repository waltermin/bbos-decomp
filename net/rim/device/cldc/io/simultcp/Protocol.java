package net.rim.device.cldc.io.simultcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.io.ConnectionCloseListener;
import net.rim.device.api.io.ConnectionCloseProvider;
import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.IOCancelledException;
import net.rim.device.api.io.SocketConnectionEnhanced;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.daemon.TransportRegistry;
import net.rim.device.cldc.io.simultcpdatagram.SimulTcpDatagramBase;
import net.rim.device.internal.io.streamdatagram.StreamDatagramAddressBase;
import net.rim.device.internal.io.streamdatagram.StreamDatagramConnectionBase;
import net.rim.device.internal.io.tcp.TcpConnectionIdentifier;
import net.rim.device.internal.io.tcp.TcpConstants;
import net.rim.device.internal.io.tcp.TcpUtils;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.device.internal.system.RadioInternal;

public final class Protocol
   extends StreamDatagramConnectionBase
   implements SimulTcpConstants,
   SocketConnection,
   SocketConnectionEnhanced,
   ConnectionCloseProvider {
   public Object tcpBlock = new Object();
   public Object receiveLock = new Object();
   public Object closeLock = new Object();
   public Object closeBlock = new Object();
   public Object processBlock = new Object();
   private net.rim.device.cldc.io.simultcpdatagram.Transport tcpObjectPool;
   protected SimulTcpBuffer receiveBuffer = new SimulTcpBuffer(null, 0, 0);
   protected SimulTcpBuffer sendBuffer = new SimulTcpBuffer(null, 0, 0);
   protected SimulTcpSendThread sendThread;
   private boolean _sendThreadTerminated;
   public boolean _dataWaitingToBeSent;
   public int socketID = -1;
   private int userBytesToRead;
   private boolean SuperClosed;
   private boolean WinsockBufferEmpty;
   private boolean outStreamCreated;
   private boolean inStreamCreated;
   private boolean closeCalled;
   private boolean closeInputCalled;
   private boolean closeOutputCalled;
   private boolean readInProgress;
   private boolean writeInProgress;
   private int _connectTimeout;
   private int _readTimeout;
   private byte[] scrapBuffer = new byte[8192];
   private SimulTcpBuffer scrapBufferWrapper = new SimulTcpBuffer(this.scrapBuffer, 0, 0);
   public int maxPayloadSize = 1400;
   int maxRetransmit;
   boolean throwIOException;
   private ConnectionCloseListener _closeListener;

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      if ((mode & 3) != mode) {
         throw new IllegalArgumentException();
      }

      try {
         String[] args = new String[3];
         this._connectTimeout = StreamDatagramAddressBase.retrieveSettings(name, args);
         int sessionTimeout = StreamDatagramAddressBase.retrieveSessionTimeout(name);
         String n = StringUtilities.toLowerCase(name, 1701707776);
         if (n.indexOf(";interface=wifi") >= 0) {
            args[0] = WLAN.WLAN_PSEUDO_APN;
            args[1] = null;
            args[2] = null;
         }

         super._tunnel = this.openTunnel(args[0], args[1], args[2], sessionTimeout);
         String apn = args[0];
         if (super._tunnel != null) {
            apn = super._tunnel.getConfig().getName();
         }

         super._myAddress = new SimulTcpAddress(name, apn);

         try {
            if (super._myAddress.isListenAddress()) {
               if (!TunnelCredentialsProvider.getInstance().isIncomingSocketsAllowed()) {
                  throw new IOException("Tcp ServerSocketsConnections not allowed");
               }

               try {
                  super._transport = (Transport)TransportRegistry.get("net.rim.device.cldc.io.simultcp.Transport");
                  SimulTcpServerSocketConnection simulTcpServerSocketConnection = new SimulTcpServerSocketConnection(super._transport, super._tunnel);
                  return simulTcpServerSocketConnection.openPrim(name, mode, timeouts);
               } finally {
                  ;
               }
            }

            if (super._myAddress.getLocalPort() == -1) {
               int index = n.indexOf(";localport=");
               if (index >= 0) {
                  label353:
                  try {
                     ControlledAccess.assertRRISignatures(true);
                     int nextIndex = n.indexOf(59, index + 1);
                     if (nextIndex < 0) {
                        nextIndex = n.length();
                     }

                     if (index + 11 < nextIndex) {
                        int ret = DatagramAddressBase.parseInt(n, index + 11, nextIndex, 10);
                        if (ret >= 0 && ret <= 65535) {
                           super._myAddress.setLocalPort(ret);
                        }
                     }
                  } finally {
                     break label353;
                  }
               }
            }

            if (!TunnelCredentialsProvider.getInstance().isOutgoingSocketsAllowed()) {
               throw new IOException("Tcp SocketConnections not allowed");
            }
         } catch (Throwable var40) {
            throw new IllegalArgumentException(e.toString());
         }

         super.openPrim(name, mode, timeouts);
         super._inStream = new SimulTcpInputStream(this);
         super._outStream = new SimulTcpOutputStream(this);
         this.sendThread = new SimulTcpSendThread(this);
         this.maxPayloadSize = ((Transport)super._transport).getMaximumPayloadLength();
         boolean var21 = false /* VF: Semaphore variable */;

         try {
            var21 = true;
            int var42 = TcpUtils.addToTcpConnectionDatabase(this, (TcpConnectionIdentifier)super._myAddress);
            if (((TcpConnectionIdentifier)super._myAddress).getConnectionLocalPort() != var42) {
               ((TcpConnectionIdentifier)super._myAddress).setConnectionLocalPort(var42);
               var21 = false;
            } else {
               var21 = false;
            }
         } finally {
            if (var21) {
               EventLogger.logEvent(447071754022829032L, 1413696867, 0);
               TcpUtils.logConnectionDatabase();
               throw new IOException("Max connections opened.");
            }
         }

         super._myAddress.setLocalPort(-2);
         this.tcpObjectPool = ((net.rim.device.cldc.io.simultcpdatagram.Protocol)((Transport)super._transport).getSubConnection()).getTransport();
         this.setState(2);
         this.tcpConnect();

         for (int i = 0; i < TcpConstants.TIMER_DEFAULT.length; i++) {
            if (TcpConstants.TCP_EXP_BACKOFF[i] * 3000 > 64000) {
               this.maxRetransmit += 64000;
            } else {
               this.maxRetransmit = this.maxRetransmit + TcpConstants.TCP_EXP_BACKOFF[i] * 3000;
            }
         }

         if (super._currentTcpState != 0) {
            SimulTcpProcess.getInstance().startThread(this.sendThread);
            return this;
         } else {
            throw new IOException("Unable to open connection");
         }
      } catch (Throwable var41) {
         this.nullConnection();
         if (!(e instanceof IOException)) {
            throw new IOException(e.getMessage());
         } else {
            throw (IOException)e;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final Connection spawnNewConnectionForServerSocket(SimulTcpDatagramBase simulTcpDatagram, int mode, boolean timeouts) throws IOException {
      super._isListenConnection = false;
      String[] args = new String[3];
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         super.openPrim(null, mode, timeouts);
         var7 = false;
      } finally {
         if (var7) {
            throw new IOException("Malformed Address");
         }
      }

      this.setState(6);
      if (simulTcpDatagram.tcpProps != null) {
         this.socketID = simulTcpDatagram.tcpProps.socketID;
      }

      super._myAddress = new SimulTcpAddress((SimulTcpAddress)simulTcpDatagram.getAddressBase());
      args[0] = super._myAddress.getApnName();
      args[1] = super._myAddress.getApnUsername();
      args[2] = super._myAddress.getApnPassword();
      super._tunnel = this.openTunnel(args[0], args[1], args[2], -1);
      super._inStream = new SimulTcpInputStream(this);
      super._outStream = new SimulTcpOutputStream(this);
      this.sendThread = new SimulTcpSendThread(this);
      this.maxPayloadSize = ((Transport)super._transport).getMaximumPayloadLength();
      this.tcpObjectPool = ((net.rim.device.cldc.io.simultcpdatagram.Protocol)((Transport)super._transport).getSubConnection()).getTransport();

      for (int i = 0; i < TcpConstants.TIMER_DEFAULT.length; i++) {
         if (TcpConstants.TCP_EXP_BACKOFF[i] * 3000 > 64000) {
            this.maxRetransmit += 64000;
         } else {
            this.maxRetransmit = this.maxRetransmit + TcpConstants.TCP_EXP_BACKOFF[i] * 3000;
         }
      }

      SimulTcpProcess.getInstance().startThread(this.sendThread);
      return this;
   }

   public final void postAccept(int mode, boolean timeouts, int socketID) throws IOException {
      this.socketID = socketID;
      int port;
      if ((port = RadioInternal.simulTCPCommand(10, socketID, 0, 0, 0)) < 0) {
         throw new IOException("Invalid port");
      }

      ((SimulTcpAddress)super._myAddress).setPort(port);
      int ipAddressAsInt;
      if ((ipAddressAsInt = RadioInternal.simulTCPCommand(8, socketID, 0, 0, 0)) < 0) {
         throw new IOException("Invalid ip");
      }

      ((SimulTcpAddress)super._myAddress).setIpAddressAsInt(ipAddressAsInt);
      super._address = super._myAddress.getConnectionAddress();
      this.setState(1);
   }

   final void closeInput() {
      synchronized (this.closeLock) {
         if (this.readInProgress) {
            this.closeInputCalled = true;
            this.WinsockBufferEmpty = true;
            synchronized (this.receiveLock) {
               this.receiveLock.notifyAll();
            }
         } else {
            if (this.scrapBufferWrapper != null) {
               this.scrapBufferWrapper.clear();
            }

            if (this.closeCalled && (!this.outStreamCreated || super._outStream != null && ((SimulTcpOutputStream)super._outStream).isClosed())) {
               this.nullConnection();
            }
         }
      }
   }

   final void closeOutput() {
      synchronized (this.closeLock) {
         if (super._inStream != null || super._outStream != null) {
            switch (super._currentTcpState) {
               case -1:
               case 2:
                  break;
               case 0:
               case 3:
               default:
                  return;
               case 1:
               case 5:
                  this.setState(3);
                  if (this.writeInProgress) {
                     this.closeOutputCalled = true;
                     return;
                  }

                  if (this.inStreamCreated && (super._inStream == null || !((SimulTcpInputStream)super._inStream).isClosed())) {
                     this.sendThread.stopSending(false, true);
                  } else {
                     this.sendThread.stopSending(true, true);
                     this.nullConnection();
                  }
                  break;
               case 4:
                  this.setState(0);
                  if (!this.inStreamCreated || super._inStream != null && ((SimulTcpInputStream)super._inStream).isClosed()) {
                     this.sendThread.stopSending(true, true);
                     if (this.writeInProgress) {
                        this.closeOutputCalled = true;
                     } else {
                        this.nullConnection();
                     }
                  } else {
                     this.sendThread.stopSending(false, true);
                  }
            }
         }
      }
   }

   final void checkCloseOutput() {
      this.writeInProgress = false;
      if (this.closeOutputCalled) {
         this.closeOutputCalled = false;
         this.closeOutput();
      }
   }

   final void checkCloseInput() {
      this.readInProgress = false;
      if (this.closeInputCalled) {
         this.closeInputCalled = false;
         this.closeInput();
      }
   }

   @Override
   public final void close() {
      synchronized (this.closeBlock) {
         this.closeCalled = true;
         if (super._inStream != null || super._outStream != null) {
            if ((!this.outStreamCreated || super._outStream == null || ((SimulTcpOutputStream)super._outStream).isClosed())
               && (!this.inStreamCreated || super._inStream == null || ((SimulTcpInputStream)super._inStream).isClosed())) {
               this.setState(0);
               if (!this._sendThreadTerminated) {
                  this.sendThread.stopSending(true, true);
                  synchronized (this.sendThread) {
                     this.sendThread.notify();
                  }
               }

               this.nullConnection();
            } else if (!this.outStreamCreated) {
               this.closeOutput();
            } else if (!this.inStreamCreated) {
               this.closeInput();
            }
         }
      }
   }

   private final void nullConnection() {
      if (!this.SuperClosed) {
         label42:
         try {
            if (super._myAddress != null) {
               TcpUtils.removeFromTcpConnectionDatabase(this, (TcpConnectionIdentifier)super._myAddress);
            }

            super.close();
            if (super._tunnel != null) {
               super._tunnel.close();
            }

            this.SuperClosed = true;
         } finally {
            break label42;
         }
      }

      if (this.socketID != -1 && !this._sendThreadTerminated) {
         this.sendThread.stopSending(true, false);
      }

      this._sendThreadTerminated = true;
      this.receiveBuffer = null;
      this.scrapBufferWrapper = null;
      super._tunnel = null;
      super._inStream = null;
      super._outStream = null;
   }

   @Override
   protected final boolean isAddressed(DatagramAddressBase address) {
      return super._myAddress == null ? false : super._myAddress.equals(address);
   }

   final void addToOutputBuffer(byte[] output, int offset, int length) throws IOException, ConnectionClosedException {
      if (this.throwIOException) {
         throw new IOException();
      }

      this.writeInProgress = true;
      if (super._currentTcpState != 0 && super._currentTcpState != 3) {
         if (this.sendBuffer == null) {
            this.sendBuffer = new SimulTcpBuffer(output, offset, length);
         } else {
            this.sendBuffer.reset(output, offset, length);
         }

         synchronized (this.sendThread) {
            this._dataWaitingToBeSent = true;
            this.sendThread.notifyAll();

            while (this._dataWaitingToBeSent) {
               try {
                  this.sendThread.wait();
               } finally {
                  continue;
               }
            }
         }

         if (super._abortWasCalled) {
            throw new ConnectionClosedException();
         }

         if (this.throwIOException) {
            this.abort();
            throw new IOException();
         }
      } else {
         throw new IOException("OutputStream closed");
      }
   }

   final int readFromInputBuffer(byte[] input, int offset, int length) throws IOException {
      if (this.throwIOException) {
         throw new IOException("Connection aborted");
      }

      this.readInProgress = true;
      if (this.scrapBufferWrapper != null && (this.scrapBufferWrapper.getLength() != 0 || !this.WinsockBufferEmpty)) {
         if (length <= 0) {
            this.userBytesToRead = 0;
            return 0;
         }

         if (this.scrapBufferWrapper.getLength() >= length) {
            this.scrapBufferWrapper.read(input, offset, length);
            this.userBytesToRead = 0;
            return length;
         }

         if (this.scrapBufferWrapper.getLength() > 0 && this.WinsockBufferEmpty) {
            int temp = this.scrapBufferWrapper.getLength();
            this.scrapBufferWrapper.read(input, offset, temp);
            this.userBytesToRead = 0;
            return temp;
         }

         if (this.receiveBuffer == null) {
            this.receiveBuffer = new SimulTcpBuffer(input, offset, 0);
         } else {
            this.receiveBuffer.reset(input, offset, 0);
         }

         int bytesRequested = 0;

         label174:
         while (bytesRequested < length) {
            if (this.scrapBufferWrapper.getLength() > 0) {
               byte[] temp = new byte[this.scrapBufferWrapper.getLength()];
               this.scrapBufferWrapper.read(temp, 0, temp.length);
               this.receiveBuffer.write(temp, 0, temp.length);
               bytesRequested += temp.length;
            }

            int numToRead = Math.min(Math.max(length - bytesRequested, 0), 24576);
            this.userBytesToRead = this.receiveBuffer.getLength() + numToRead;
            bytesRequested += numToRead;
            if (this.socketID != -1 && RadioInternal.simulTCPCommand(3, this.socketID, numToRead, 0, 0) != -1) {
               while (true) {
                  if (this.receiveBuffer.getLength() < bytesRequested) {
                     synchronized (this.receiveLock) {
                        label163:
                        try {
                           long timeBefore = System.currentTimeMillis();
                           this.receiveLock.wait(this._readTimeout);
                           if (this._readTimeout != 0 && timeBefore + this._readTimeout <= System.currentTimeMillis()) {
                              throw new InterruptedIOException();
                           }
                        } finally {
                           break label163;
                        }

                        if (!this.WinsockBufferEmpty) {
                           if (this.closeInputCalled && this.receiveBuffer.getLength() < length) {
                              throw new IOCancelledException();
                           }

                           if (super._abortWasCalled) {
                              throw new ConnectionClosedException();
                           }
                           continue;
                        }

                        bytesRequested = length;
                     }
                  }

                  if (this.receiveBuffer != null) {
                     continue label174;
                  }
                  break label174;
               }
            }

            this.WinsockBufferEmpty = true;
            if (this.receiveBuffer.getLength() > 0) {
               int temp = this.receiveBuffer.getLength();
               this.receiveBuffer.reset(null, 0, 0);
               this.userBytesToRead = 0;
               return temp;
            }

            this.userBytesToRead = 0;
            return -1;
         }

         int buffLength = 0;
         if (this.receiveBuffer != null) {
            buffLength = this.receiveBuffer.getLength();
            this.receiveBuffer.reset(null, 0, 0);
         }

         if (buffLength > 0) {
            this.userBytesToRead = 0;
            return buffLength > length ? length : buffLength;
         } else {
            this.userBytesToRead = 0;
            return -1;
         }
      } else {
         this.userBytesToRead = 0;
         return -1;
      }
   }

   @Override
   protected final void processReceivedDatagram(Datagram datagram) {
      synchronized (this.processBlock) {
         SimulTcpDatagramBase pntr = (SimulTcpDatagramBase)datagram;
         switch (pntr.tcpProps.controlCode) {
            case 0:
            case 5:
            case 7:
               break;
            case 1:
               if (!this._sendThreadTerminated) {
                  this.sendThread.confirmSend(0, 0);
               }
               break;
            case 2:
               if (super._currentTcpState != 0 || this.receiveBuffer != null) {
                  if (datagram.getLength() + this.receiveBuffer.getLength() > this.userBytesToRead) {
                     byte[] dgData = datagram.getData();
                     int dgOffset = datagram.getOffset();
                     int spaceInReceiveBuffer = this.userBytesToRead - this.receiveBuffer.getLength();
                     this.receiveBuffer.write(dgData, dgOffset, spaceInReceiveBuffer);
                     this.scrapBufferWrapper.write(dgData, dgOffset + spaceInReceiveBuffer, datagram.getLength() - spaceInReceiveBuffer - dgOffset);
                  } else {
                     this.receiveBuffer.write(datagram.getData(), datagram.getOffset(), datagram.getLength());
                  }

                  if (pntr.tcpProps.controlDescription == 1) {
                     this.WinsockBufferEmpty = true;
                  }

                  if (this.receiveBuffer.getLength() >= this.userBytesToRead || pntr.tcpProps.controlDescription == 1) {
                     synchronized (this.receiveLock) {
                        this.receiveLock.notify();
                     }
                  }
               }
               break;
            case 3:
               this.socketID = pntr.tcpProps.socketID;
               this.setState(1);
               synchronized (this.tcpBlock) {
                  this.tcpBlock.notifyAll();
                  break;
               }
            case 4:
               if (super._currentTcpState == 3) {
                  if (pntr.tcpProps.controlDescription == 1) {
                     this.WinsockBufferEmpty = true;
                     synchronized (this.receiveLock) {
                        this.receiveLock.notify();
                     }
                  }

                  this.setState(0);
                  if (!this._sendThreadTerminated) {
                     this.sendThread.stopSending(false, true);
                     synchronized (this.sendThread) {
                        this.sendThread.notify();
                     }
                  }
               } else {
                  this.setState(4);
                  if (pntr.tcpProps.controlDescription == 1) {
                     this.WinsockBufferEmpty = true;
                     if (this.receiveBuffer != null) {
                        synchronized (this.receiveLock) {
                           this.receiveLock.notify();
                        }
                     }
                  }
               }
               break;
            case 6:
               if (super._currentTcpState != 0) {
                  if (super._currentTcpState == 2) {
                     RadioInternal.simulTCPCommand(1, pntr.tcpProps.socketID, 0, 0, 0);
                     this.setState(0);
                     synchronized (this.tcpBlock) {
                        this.tcpBlock.notifyAll();
                     }
                  } else {
                     this.setState(0);
                     if (!this._sendThreadTerminated) {
                        this.sendThread.stopSending(true, false);
                        this._sendThreadTerminated = true;
                        this.WinsockBufferEmpty = true;
                        synchronized (this.receiveLock) {
                           this.receiveLock.notify();
                        }

                        if (!this.SuperClosed) {
                           this.nullConnection();
                        }
                     }
                  }
               }
               break;
            case 8:
               if (this.receiveBuffer != null) {
                  this.receiveBuffer.write(datagram.getData(), datagram.getOffset(), datagram.getLength());
               }

               this.WinsockBufferEmpty = true;
               synchronized (this.receiveLock) {
                  this.receiveLock.notify();
                  break;
               }
            case 9:
            default:
               if (!this._sendThreadTerminated) {
                  this.sendThread.confirmSend(pntr.tcpProps.controlDescription, pntr.tcpProps.sequenceNumber);
               }
               break;
            case 10:
               this.abort();
         }

         this.tcpObjectPool.giveBackDatagram((SimulTcpDatagramBase)datagram);
      }
   }

   final void tcpOutput(byte[] data, int offset, int length, int sequenceNumber) {
      SimulTcpDatagramBase tcpDatagram = this.tcpObjectPool.getNewSimulTcpDatagram();
      tcpDatagram.setData(data, offset, length, super._myAddress);
      tcpDatagram.tcpProps = this.tcpObjectPool.getNewSimulTcpDatagramProperties();
      tcpDatagram.tcpProps
         .setData(0, super._myAddress.getIpAddress(), super._myAddress.getLocalPort(), super._myAddress.getDestPort(), 9, 0, this.socketID, sequenceNumber);

      try {
         this.sendDatagram(tcpDatagram);
      } finally {
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final void tcpConnect() {
      long start = 0;
      synchronized (this.tcpBlock) {
         start = System.currentTimeMillis();
         int apnNumber = 0;

         try {
            apnNumber = RadioInfo.getAccessPointNumber(super._myAddress.getApnName(), 0, super._myAddress.getApnName().length());
         } catch (Throwable var14) {
            throw new IOException(e.getMessage());
         }

         if (RadioInternal.simulTCPCommand(13, this.socketID, super._myAddress.getIpAddress(), super._myAddress.getDestPort(), apnNumber) != 0) {
            throw new IOException("Unable to open connection");
         }

         boolean var10 = false /* VF: Semaphore variable */;

         label78:
         try {
            var10 = true;
            this.tcpBlock.wait(this._connectTimeout);
            var10 = false;
         } finally {
            if (var10) {
               EventLogger.logEvent(447071754022829032L, 1413695863, 3);
               break label78;
            }
         }

         if (System.currentTimeMillis() - start >= this._connectTimeout && super._currentTcpState == 2) {
            this.setState(0);
         } else {
            if (super._currentTcpState == 1) {
               ((SimulTcpAddress)super._myAddress).setLocalPort(this.getLocalPortInternal());
            }
         }
      }
   }

   private final void setState(int newState) {
      synchronized (this.tcpBlock) {
         super._currentTcpState = newState;
      }

      if (this._closeListener != null && newState == 0) {
         this._closeListener.connectionClosed(this);
      }
   }

   public final int available() throws ConnectionClosedException {
      if (this.closeCalled || this.receiveBuffer == null) {
         throw new ConnectionClosedException();
      }

      if (this.userBytesToRead > 0) {
         return 0;
      }

      int numBytesFromBelow;
      return this.socketID != -1 && (numBytesFromBelow = RadioInternal.simulTCPCommand(5, this.socketID, 0, 0, 0)) >= 0 && !this.WinsockBufferEmpty
         ? this.receiveBuffer.getLength() + numBytesFromBelow
         : this.receiveBuffer.getLength();
   }

   public final int getState() {
      return super._currentTcpState;
   }

   @Override
   public final void abort() {
      if (!super._abortWasCalled) {
         super._abortWasCalled = true;
         if (super._currentTcpState != 0) {
            if (super._currentTcpState == 2) {
               this.setState(0);
               synchronized (this.tcpBlock) {
                  this.tcpBlock.notifyAll();
                  return;
               }
            }

            this.setState(0);
            if (!this._sendThreadTerminated) {
               this.sendThread.stopSending(true, false);
               this._sendThreadTerminated = true;
               this.WinsockBufferEmpty = true;
            }

            synchronized (this.receiveLock) {
               this.receiveLock.notify();
            }

            if (!this.SuperClosed) {
               this.nullConnection();
            }
         }
      }
   }

   @Override
   public final String getLocalAddress() throws IOException {
      this.checkIfConnectionIsClosedClosingOrAborted();
      if (super._myAddress == null) {
         throw new IOException();
      } else {
         return StreamDatagramAddressBase.getLocalAddressInternal(super._myAddress.getApnName());
      }
   }

   @Override
   public final int getPort() throws IOException {
      this.checkIfConnectionIsClosedClosingOrAborted();
      int port = -1;
      if (this.socketID != -1 && (port = RadioInternal.simulTCPCommand(10, this.socketID, 0, 0, 0)) != -1) {
         return port;
      } else {
         throw new IOException();
      }
   }

   @Override
   public final int getLocalPort() throws ConnectionClosedException {
      this.checkIfConnectionIsClosedClosingOrAborted();
      if (super._currentTcpState == 2) {
         throw new ConnectionClosedException();
      } else {
         return this.getLocalPortInternal();
      }
   }

   @Override
   public final int getLocalPortInternal() {
      return this.socketID == -1 ? -1 : RadioInternal.simulTCPCommand(9, this.socketID, 0, 0, 0);
   }

   @Override
   public final void setSocketOption(byte option, int value) throws IOException, ConnectionClosedException {
      this.checkIfConnectionIsClosedClosingOrAborted();
      if (value < 0) {
         throw new IllegalArgumentException();
      }

      if (option != 0) {
         switch (option) {
            case 0:
               throw new IllegalArgumentException();
            case 1:
            default:
               if (value < 0) {
                  throw new IllegalArgumentException();
               }

               if (this._sendThreadTerminated) {
                  throw new ConnectionClosedException();
               }

               this.sendThread.setLingerPeriod(value);
               return;
            case 2:
               if (value != 0) {
                  value = 1;
               }

               if (this.socketID == -1 || RadioInternal.simulTCPCommand(6, this.socketID, 2, value, 0) == -1) {
                  throw new IOException();
               }
               break;
            case 3:
               if (value < 0) {
                  throw new IllegalArgumentException();
               }

               if (this.socketID == -1 || RadioInternal.simulTCPCommand(6, this.socketID, 3, value, 0) == -1) {
                  throw new IOException();
               }
               break;
            case 4:
               if (value < 0) {
                  throw new IllegalArgumentException();
               }

               if (this.socketID == -1 || RadioInternal.simulTCPCommand(6, this.socketID, 4, value, 0) == -1) {
                  throw new IOException();
               }
         }
      }
   }

   @Override
   public final int getSocketOption(byte option) throws IOException {
      this.checkIfConnectionIsClosedClosingOrAborted();
      if (option == 0) {
         return -1;
      }

      switch (option) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         default:
            if (this._sendThreadTerminated) {
               throw new IOException();
            }

            return (int)this.sendThread.getLingerPeriod();
         case 2:
            int tempx = -1;
            if (this.socketID != -1 && (tempx = RadioInternal.simulTCPCommand(7, this.socketID, 2, 0, 0)) != -1) {
               return tempx;
            }

            throw new IOException();
         case 3:
            int var4 = -1;
            if (this.socketID != -1 && (var4 = RadioInternal.simulTCPCommand(7, this.socketID, 3, 0, 0)) != -1) {
               return var4;
            }

            throw new IOException();
         case 4:
            int temp = -1;
            if (this.socketID != -1 && (temp = RadioInternal.simulTCPCommand(7, this.socketID, 4, 0, 0)) != -1) {
               return temp;
            } else {
               throw new IOException();
            }
      }
   }

   @Override
   public final void setSocketOptionEx(short option, long value) {
      this.checkIfConnectionIsClosedClosingOrAborted();
      if (option == 256 && value >= 0) {
         this._readTimeout = (int)value;
      } else {
         this.setSocketOption((byte)option, (int)value);
      }
   }

   @Override
   public final long getSocketOptionEx(short option) {
      this.checkIfConnectionIsClosedClosingOrAborted();
      return option == 256 ? this._readTimeout : this.getSocketOption((byte)option);
   }

   @Override
   public final InputStream openInputStream() throws IOException {
      if (!this.closeCalled
         && super._inStream != null
         && !((SimulTcpInputStream)super._inStream).isClosed()
         && (!this.WinsockBufferEmpty || this.receiveBuffer.getLength() != 0)) {
         if (this.inStreamCreated) {
            throw new IOException("Input stream already open");
         }

         this.inStreamCreated = true;
         return super._inStream;
      } else {
         throw new IOException(StreamDatagramConnectionBase.STR_STREAM_IS_ALREADY_CLOSED);
      }
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return new DataInputStream(this.openInputStream());
   }

   @Override
   public final OutputStream openOutputStream() throws IOException {
      if (this.closeCalled || super._outStream == null || ((SimulTcpOutputStream)super._outStream).isClosed() || this._sendThreadTerminated) {
         throw new IOException(StreamDatagramConnectionBase.STR_STREAM_IS_ALREADY_CLOSED);
      }

      if (this.outStreamCreated) {
         throw new IllegalArgumentException("Stream already open");
      }

      this.outStreamCreated = true;
      return super._outStream;
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      return new DataOutputStream(this.openOutputStream());
   }

   @Override
   protected final void checkIfConnectionIsClosedClosingOrAborted() throws ConnectionClosedException {
      if (this.closeCalled || this.socketID == -1 || super._currentTcpState == 0) {
         throw new ConnectionClosedException();
      }
   }

   @Override
   public final boolean isConnectionEstablished() {
      return super._currentTcpState == 1 && !this.closeCalled && this.socketID != -1;
   }

   @Override
   public final boolean connectionStatusAvailable() {
      return true;
   }

   @Override
   public final void setConnectionCloseListener(ConnectionCloseListener listener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
