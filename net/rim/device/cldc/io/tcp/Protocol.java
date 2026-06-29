package net.rim.device.cldc.io.tcp;

import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.io.ConnectionCloseListener;
import net.rim.device.api.io.ConnectionCloseProvider;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.SocketConnectionEnhanced;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.daemon.TransportRegistry;
import net.rim.device.cldc.io.utility.EventThreadCheck;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.io.streamdatagram.StreamDatagramAddressBase;
import net.rim.device.internal.io.streamdatagram.StreamDatagramConnectionBase;
import net.rim.device.internal.io.tcp.TcpAddress;
import net.rim.device.internal.io.tcp.TcpConnectionIdentifier;
import net.rim.device.internal.io.tcp.TcpConstants;
import net.rim.device.internal.io.tcp.TcpDataBlock;
import net.rim.device.internal.io.tcp.TcpDatagramBase;
import net.rim.device.internal.io.tcp.TcpDatagramProperties;
import net.rim.device.internal.io.tcp.TcpObjectPool;
import net.rim.device.internal.io.tcp.TcpProcess;
import net.rim.device.internal.io.tcp.TcpTimerInterface;
import net.rim.device.internal.io.tcp.TcpTimers;
import net.rim.device.internal.io.tcp.TcpUtils;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.device.internal.system.TCPPacketHeader;

public final class Protocol
   extends StreamDatagramConnectionBase
   implements SocketConnection,
   TcpConstants,
   TcpTimerInterface,
   ConnectionCloseProvider,
   SocketConnectionEnhanced {
   TcpQueueManager _tcpQueueManager;
   TcpWindowManager _tcpWindowManager;
   TcpOptionManager _tcpOptionManager;
   SendHotlist _sendHotlist;
   protected TcpIOThread _tcpIOThread;
   private ConnectionCloseListener _closeListener;
   protected Object _tcpStateLock;
   protected TcpBuffer _sendBuffer;
   public TcpTimerThread _timers;
   int _sendBufferPointer;
   int _timerBackOffCounter;
   long _startIdleTime;
   int _connectionOpenedWithType = 0;
   boolean _noMoreData;
   protected boolean _shutdownInProgress;
   protected boolean _connectionTimedOut;
   private boolean _isInPersistMode;
   private int _connectTimeOut;
   protected TcpReassemblyDataList _tcpReassemblyQueue;
   private TcpObjectPool _tcpObjectPool;
   private int _readTimeout;
   private static String INTERFACE_WIFI = ";interface=wifi";
   private static String INTERFACE = "interface";
   private static String ALLOW_NO_CONTEXT_RETRY = ";retrynocontext=true";
   private static String LOCAL_PORT = ";localport=";

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void init(String name, int mode, boolean timeouts) throws IOException {
      super._isListenConnection = false;

      try {
         boolean var35 = false /* VF: Semaphore variable */;

         try {
            var35 = true;
            this.openPrimHelper(name, mode, timeouts);
            var35 = false;
         } finally {
            if (var35) {
               throw new Object("Malformed Address.  Returning null connection");
            }
         }

         synchronized (this._tcpStateLock) {
            this.setState(2);
            if (super._tunnel.getStatus() != 3) {
               synchronized (name) {
                  boolean var28 = false /* VF: Semaphore variable */;

                  try {
                     var28 = true;
                     name.wait(5000);
                     var28 = false;
                  } finally {
                     if (var28) {
                        throw new Object();
                     }
                  }
               }
            }

            boolean var21 = false /* VF: Semaphore variable */;

            try {
               var21 = true;
               int iae = TcpUtils.addToTcpConnectionDatabase(this, (TcpConnectionIdentifier)super._myAddress);
               if (super._myAddress.getLocalPort() != iae) {
                  super._myAddress.setLocalPort(iae);
                  var21 = false;
               } else {
                  var21 = false;
               }
            } finally {
               if (var21) {
                  EventLogger.logEvent(447071754022829032L, 1413696867, 0);
                  TcpUtils.logConnectionDatabase();
                  throw new Object("Max connections opened.");
               }
            }

            this.tcpConnect();
         }
      } finally {
         this.initiateShutDown();
      }
   }

   final void prepForCaching() {
      this._sendBuffer.restoreOriginalSize();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final Connection spawnNewConnectionForServerSocket(TcpDatagramBase tcpDatagram, int mode, boolean timeouts) {
      super._isListenConnection = false;
      super._myAddress = new TcpAddress((TcpAddress)tcpDatagram.getAddressBase());
      boolean var8 = false /* VF: Semaphore variable */;

      try {
         var8 = true;
         this.openPrimHelper(super._myAddress.getConnectionAddress(), mode, timeouts);
         var8 = false;
      } finally {
         if (var8) {
            throw new Object("Malformed Address.");
         }
      }

      synchronized (this._tcpStateLock) {
         this.setState(1);
      }

      this._tcpQueueManager.init();
      int apnId = this.retrieveApn(false);
      this._tcpWindowManager
         .setPeerMaxSegmentSizeAttribute(Math.min(TCPPacketHeader.getMaxPacketSize(apnId), ((Transport)super._transport).getMaximumLength()));
      this._tcpWindowManager.init(apnId);
      this._timers.startTimer(this, 0, this._connectTimeOut);
      this.processReceivedDatagram(tcpDatagram);
      return this;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void shutDownConnection() {
      if (!this._shutdownInProgress) {
         this._shutdownInProgress = true;
         boolean shutDownSuccessful = true;
         boolean var8 = false /* VF: Semaphore variable */;

         label198:
         try {
            var8 = true;
            super.close();
            if (this._timers != null) {
               this._timers.stopAll(this);
               this._timers.removeConnection(this);
            }

            if (this._tcpIOThread != null) {
               this._tcpIOThread.requestShutDown();
            }

            if (super._myAddress != null) {
               TcpUtils.removeFromTcpConnectionDatabase(this, (TcpConnectionIdentifier)super._myAddress);
               super._myAddress = null;
            }

            if (super._tunnel != null) {
               super._tunnel.close();
               super._tunnel = null;
            }

            if (this._sendBuffer != null) {
               synchronized (this._sendBuffer) {
                  this._sendBuffer.notifyAll();
               }
            }

            if (!super._outputStreamClosed && super._outStream != null) {
               try {
                  super._outStream.close();
               } finally {
                  ;
               }
            }

            if (!super._abortWasCalled && !this._connectionTimedOut) {
               var8 = false;
            } else if (!super._inputStreamClosed) {
               boolean var13 = false /* VF: Semaphore variable */;

               label181:
               try {
                  var13 = true;
                  if (super._inStream != null) {
                     super._inStream.close();
                     var8 = false;
                     var13 = false;
                  } else {
                     var8 = false;
                     var13 = false;
                  }
               } finally {
                  if (var13) {
                     shutDownSuccessful = false;
                     var8 = false;
                     break label181;
                  }
               }
            } else {
               var8 = false;
            }
         } finally {
            if (var8) {
               EventLogger.logEvent(447071754022829032L, 1413694327, 3);
               shutDownSuccessful = false;
               break label198;
            }
         }

         if (shutDownSuccessful) {
            TcpConnectionFactory.getInstance().addConnection(this);
         }
      }
   }

   final void startDelayedAckTimer() {
      if (!this._timers.isActive(this, 2)) {
         this._timers.startTimer(this, 2);
      }
   }

   protected final void checkPersistTimer() {
      if (this._timers.isActive(this, 1)) {
         this._timers.stopTimer(this, 1);
      }

      if (!this._timers.isActive(this, 3)) {
         this._timers.startTimer(this, 3);
      }
   }

   public final TcpTimers getTimers() {
      return this._timers == null ? null : this._timers.getTimers(this);
   }

   public final void retransmissionTimerExpired() {
      if (this._sendHotlist._ourFinWasAcked) {
         this.stopRetransmitTimer();
      } else if (this._timerBackOffCounter >= 11) {
         this._connectionTimedOut = true;
         this.initiateShutDown();
      } else {
         int base = 3000;
         int nextTimeout = base * TcpConstants.TCP_EXP_BACKOFF[this._timerBackOffCounter];
         if (nextTimeout > 64000) {
            nextTimeout = 64000;
         }

         this._timers.startTimer(this, 1, nextTimeout);
         this._timerBackOffCounter++;
         switch (super._currentTcpState) {
            case 2:
               this._sendHotlist._mustSendSyn = true;
               this._tcpWindowManager.retransmissionTimerExpiredWithSegmentLoss();
               break;
            default:
               this._tcpQueueManager.retransmissionTimerExpired();
               this._tcpWindowManager.retransmissionTimerExpiredWithSegmentLoss();
         }

         this._noMoreData = false;
         this._tcpIOThread.addSendRequest();
      }
   }

   public final void inactivityTimerExpired() {
      this.initiateShutDown();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final boolean tcpOutput() {
      synchronized (this._tcpStateLock) {
         int maxNumOfBytesToSend;
         switch (super._currentTcpState) {
            case 3:
               maxNumOfBytesToSend = 0;
               break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            default:
               maxNumOfBytesToSend = this._isInPersistMode ? 1 : this._tcpWindowManager.getAvailableWindow();
         }

         TcpDatagramProperties props = this._tcpObjectPool.getNewTcpDatagramProperties();
         byte[] optionBytes = new byte[0];
         int optionBytesLength = 0;
         switch (super._currentTcpState) {
            case 1:
            case 2:
               props._maxSegmentSize = this._tcpWindowManager.getMaxSegmentSize();
               optionBytes = this._tcpOptionManager.getSynOptionsByteArray(props);
               optionBytesLength = optionBytes.length;
               props._dataOffset = 5 + (optionBytesLength >> 2);
               break;
            case 4:
            case 6:
            case 9:
               if (this._tcpOptionManager._sackEnabled) {
                  props._sackBlocks = this._tcpQueueManager._tcpReassemblyQueue.getSackBlocks();
               }

               optionBytes = this._tcpOptionManager.getOptionsByteArray(props);
               optionBytesLength = optionBytes.length;
               props._dataOffset = 5 + (optionBytesLength >> 2);
               break;
            default:
               props._dataOffset = 5;
         }

         if (!this._sendHotlist._haveSentFin) {
            props._sequenceNumber = this._sendHotlist.getSendNext();
         } else {
            props._sequenceNumber = this._sendHotlist.getLocalFinSequenceNumber() + 1;
         }

         int flags = 16;
         switch (super._currentTcpState) {
            case 0:
               break;
            case 1:
               flags |= 2;
               break;
            case 2:
            default:
               flags = 2;
               break;
            case 3:
               if (this._sendHotlist._mustSendSyn) {
                  flags |= 2;
                  this._sendHotlist._mustSendSyn = false;
               }
         }

         props._window = this._tcpWindowManager.getLocalWindow() >> this._tcpWindowManager.getLocalScale();
         props._urgentPointer = 0;
         props._acknowledgementNumber = this._sendHotlist.getAckNumberToSend();
         if (this._sendHotlist._haveReceivedFin && props._acknowledgementNumber == this._sendHotlist.getPeerFinSequenceNumber()) {
            this._sendHotlist.sendAckForPeerFin();
            props._acknowledgementNumber++;
         }

         byte[] tcpDatagramBuffer;
         int numBytesToSend;
         synchronized (this._sendBuffer) {
            int maxSegmentSize = this._tcpWindowManager._maxSegmentSize - optionBytesLength;
            TcpDataBlock blockToSend = this._sendHotlist.getNextSendBlock(maxSegmentSize < maxNumOfBytesToSend ? maxSegmentSize : maxNumOfBytesToSend);
            if (blockToSend == null) {
               if (super._outputStreamClosed && this._sendHotlist._sendRequestsList.isEmpty()) {
                  this._noMoreData = true;
                  this._sendHotlist.setLocalFinSequenceNumber(this._sendHotlist.getSendNext());
               }

               numBytesToSend = 0;
               tcpDatagramBuffer = new byte[optionBytesLength];
               if (this._sendHotlist._keepAlivePacketRequired) {
                  props._sequenceNumber--;
               }
            } else {
               int leftEdge = blockToSend.getLeftEdge();
               int rightEdge = blockToSend.getRightEdge();
               numBytesToSend = rightEdge - leftEdge;
               if (numBytesToSend < 0) {
                  numBytesToSend = 0;
               }

               tcpDatagramBuffer = new byte[numBytesToSend + optionBytesLength];

               try {
                  this._sendBuffer.readBySeqNumbers(tcpDatagramBuffer, optionBytesLength, leftEdge, rightEdge);
               } catch (Throwable var30) {
                  throw new Object(e.getMessage());
               }

               if (this._sendHotlist._sendRequestsList.isEmpty()) {
                  flags |= 8;
                  if (super._outputStreamClosed) {
                     this._noMoreData = true;
                     this._sendHotlist.setLocalFinSequenceNumber(rightEdge);
                  }
               }

               synchronized (this._tcpReassemblyQueue) {
                  this._tcpReassemblyQueue.notifyAll();
               }
            }

            if (!this._sendHotlist._ourFinWasAcked) {
               switch (super._currentTcpState) {
                  case 3:
                     break;
                  case 4:
                  case 5:
                     if (this._noMoreData) {
                        this._sendHotlist.localFinWasSent();
                        flags |= 1;
                     }
                     break;
                  case 6:
                  case 7:
                  case 8:
                  default:
                     if (this._sendHotlist._mustSendFinRightAway && this._noMoreData) {
                        this._sendHotlist.localFinWasSent();
                        flags |= 1;
                     }
               }
            }
         }

         props._flags = flags;
         TcpDatagramBase tcpDatagram = this._tcpObjectPool.getNewTcpDatagram();
         tcpDatagram.setData(tcpDatagramBuffer, 0, numBytesToSend + optionBytesLength, super._myAddress);
         this.copyFlagsInto(tcpDatagram, false);
         tcpDatagram._tcpProps = props;
         System.arraycopy(optionBytes, 0, tcpDatagram.getData(), 0, optionBytesLength);
         this.checkRetransmissionAndPersistTimers(numBytesToSend);
         boolean var23 = false /* VF: Semaphore variable */;

         try {
            var23 = true;
            if (super._abortWasCalled) {
               var23 = false;
               return true;
            }

            this.sendDatagram(tcpDatagram);
            if (super._currentTcpState != 4) {
               this.kickInactivityTimer();
            }

            this._sendHotlist._immediateAckRequired = false;
            this._sendHotlist._mustSendSyn = false;
            this._sendHotlist._delayedAckRequired = false;
            this._tcpWindowManager._immediateAckRequired = false;
            this._tcpWindowManager.recalculateFlightSize();
            this.stopDelayedAckTimer();
            switch (super._currentTcpState) {
               case 0:
               case 2:
               case 7:
               case 8:
                  var23 = false;
                  break;
               case 1:
               default:
                  this.setState(3);
                  var23 = false;
                  break;
               case 4:
                  if (this._sendHotlist._haveReceivedFin) {
                     this.setState(5);
                     var23 = false;
                     break;
                  }
               case 3:
                  if (!this._sendHotlist._haveSentFin) {
                     var23 = false;
                  } else {
                     this.setState(6);
                     this._sendHotlist.endSendTransmission();
                     var23 = false;
                  }
                  break;
               case 5:
                  if (this._sendHotlist._haveSentFin) {
                     this.setState(8);
                     var23 = false;
                  } else {
                     var23 = false;
                  }
                  break;
               case 6:
                  if (this._sendHotlist._peerFinWasAcked) {
                     this.setState(7);
                     var23 = false;
                  } else {
                     var23 = false;
                  }
                  break;
               case 9:
                  if (this._sendHotlist._peerFinWasAcked) {
                     this.setState(10);
                     var23 = false;
                  } else {
                     var23 = false;
                  }
            }
         } finally {
            if (var23) {
               EventLogger.logEvent(447071754022829032L, 1413698354, 3);
               return true;
            }
         }

         return true;
      }
   }

   public final boolean shouldWeSendAPacket(boolean justSentPacket) {
      if (!this._tcpWindowManager.shouldWeSendAPacket() && !this._tcpQueueManager.shouldWeSendAPacket()) {
         if (super._outputStreamClosed && (!justSentPacket || this._tcpWindowManager.getAvailableWindow() != 0)) {
            synchronized (this._sendHotlist._sendRequestsList) {
               return !this._sendHotlist._sendRequestsList.isEmpty();
            }
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   protected final void tcpConnect() {
      this._tcpQueueManager.init();
      int apnId = this.retrieveApn(false);
      this._tcpWindowManager
         .setPeerMaxSegmentSizeAttribute(Math.min(TCPPacketHeader.getMaxPacketSize(apnId), ((Transport)super._transport).getMaximumLength()));
      this._tcpWindowManager.init(apnId);
      this._timers.startTimer(this, 0, this._connectTimeOut);
      this._sendHotlist._mustSendSyn = true;
      this._tcpIOThread.addSendRequest();
      synchronized (this._tcpStateLock) {
         while (super._currentTcpState == 2 || super._currentTcpState == 3) {
            try {
               this._tcpStateLock.wait();
            } finally {
               continue;
            }
         }
      }

      if (super._currentTcpState != 4) {
         throw new Object("Unable to open connection.");
      }
   }

   final void addToOutputBuffer(byte[] output, int offset, int length) {
      int origLength = length;

      while (length > 0) {
         if (super._abortWasCalled) {
            throw new Object();
         }

         if (this._sendBuffer == null) {
            throw new Object();
         }

         synchronized (this._sendBuffer) {
            int sendBufferAvailableSpace = this._sendBuffer.getAvailableSpace();
            switch (super._currentTcpState) {
               case 0:
               case 6:
               case 7:
               case 8:
               case 9:
               case 10:
                  throw new Object("Output stream closed");
            }

            if (sendBufferAvailableSpace <= 0) {
               label86:
               try {
                  this._sendBuffer.wait();
                  if (this._connectionTimedOut) {
                     throw new Object("Connection timed out");
                  }
                  continue;
               } finally {
                  break label86;
               }
            }

            int writeSize;
            if (sendBufferAvailableSpace > length) {
               writeSize = length;
            } else {
               writeSize = sendBufferAvailableSpace;
            }

            writeSize = this._sendBuffer.write(output, offset, writeSize);
            this._sendHotlist.newDataToSend(this._sendBufferPointer, writeSize);
            if (this.shouldWeSendAPacket()) {
               this._tcpIOThread.addSendRequest();
            }

            offset += writeSize;
            length -= writeSize;
            this._sendBufferPointer += writeSize;
         }
      }

      if (super._tLogger != null) {
         super._tLogger.bytesTransmitted(this, 0, super._address, origLength, null);
      }
   }

   final TcpReassembleDataNode readNextBlock() {
      boolean holeDetected = false;

      while (!super._abortWasCalled) {
         if (this._tcpReassemblyQueue == null) {
            throw new Object("ReceiveBuffer nulled out");
         }

         synchronized (this._tcpReassemblyQueue) {
            if (holeDetected || this._tcpReassemblyQueue.isEmpty()) {
               holeDetected = false;
               switch (super._currentTcpState) {
                  case 0:
                  case 5:
                  case 7:
                  case 8:
                  case 10:
                     return null;
               }

               label104:
               try {
                  long timeBefore = System.currentTimeMillis();
                  this._tcpReassemblyQueue.wait(this._readTimeout);
                  if (!this._connectionTimedOut && (this._readTimeout == 0 || timeBefore + this._readTimeout > System.currentTimeMillis())) {
                     if (!super._inputStreamClosed) {
                        continue;
                     }

                     throw new Object("Connection closed");
                  }

                  throw new Object("Connection timed out");
               } finally {
                  break label104;
               }
            }

            TcpReassembleDataNode node = this._tcpReassemblyQueue.getNextNode();
            this._tcpReassemblyQueue.notify();
            if (node != null) {
               if (super._tLogger != null) {
                  super._tLogger.bytesReceived(this, 0, super._address, node._length, null);
               }

               return node;
            }

            holeDetected = true;
         }
      }

      throw new Object();
   }

   final void inputStreamClosed() {
      super._inputStreamClosed = true;
      if (this._tcpReassemblyQueue != null) {
         synchronized (this._tcpReassemblyQueue) {
            this._tcpReassemblyQueue.notifyAll();
         }
      }
   }

   final void outputStreamClosed() throws IOException {
      if (!super._outputStreamClosed && !super._abortWasCalled && !this._shutdownInProgress) {
         super._outputStreamClosed = true;
         if (this._tcpOptionManager._lingerTimeout > 0) {
            this._timers.startTimer(this, 7, this._tcpOptionManager._lingerTimeout * 1000);
         }

         if (!super._closeRequested) {
            try {
               this.close();
               if (!this._shutdownInProgress) {
                  this.sendAllPendingDataNow();
               }

               this.closeConnectionInternally();
            } finally {
               ;
            }
         } else {
            this.closeConnectionInternally();
         }
      } else {
         super._outputStreamClosed = true;
      }
   }

   final void sendAllPendingDataNow() {
      if (!this._sendHotlist._sendRequestsList.isEmpty()) {
         this._sendHotlist.flushAllData();
         if (this.shouldWeSendAPacket()) {
            this._tcpIOThread.addSendRequest();
         }
      }
   }

   public final int getInStreamDataAvailable() {
      return this._tcpReassemblyQueue.getAvailableData();
   }

   public final int getOutStreamBufSpaceAvailable() {
      return this._sendBuffer.getAvailableSpace();
   }

   final void tcpInput(TcpDatagramBase datagram) {
      this.processIncomingDatagram(datagram);
      this._tcpObjectPool.giveBackDatagram(datagram);
      if (super._currentTcpState != 0 && !super._abortWasCalled && this.shouldWeSendAPacket()) {
         this._tcpIOThread.addSendRequest();
      }
   }

   final void processIncomingDatagram(TcpDatagramBase tcpDatagram) {
      if (!super._abortWasCalled) {
         TcpDatagramProperties props = tcpDatagram._tcpProps;
         int segSeq = props._sequenceNumber;
         int segAck = props._acknowledgementNumber;
         int segLen = tcpDatagram.getLength();
         int segFlags = props._flags;
         int sndNxt = this._sendHotlist.getSendNext();
         int sndUna = this._sendHotlist.getHighestAckNumberReceived();
         int rcvNxt = this._sendHotlist.getAckNumberToSend();
         this._startIdleTime = System.currentTimeMillis();
         if (super._currentTcpState == 4
            && (segFlags & 39) == 0
            && (segFlags & 16) != 0
            && segSeq == rcvNxt
            && props._window == this._tcpWindowManager._peerWindow
            && props._sackBlocks == null) {
            if (segLen == 0 && TcpUtils.seqGT(segAck, sndUna) && TcpUtils.seqGT(sndNxt, segAck) && this._tcpQueueManager._peerSackList.isEmpty()) {
               this._tcpWindowManager._lastAckNumberReceived = segAck;
               this._tcpWindowManager.nonDuplicateAckReceived();
               this._tcpWindowManager.recalculateFlightSize();
               this._isInPersistMode = this._tcpWindowManager.isInPersistMode();
               this._tcpQueueManager.fastExtractSequencingInfo(tcpDatagram);
               if (!this._isInPersistMode) {
                  this.stopRetransmitTimer();
                  if (segAck != sndNxt) {
                     this._timers.startTimer(this, 1);
                  }
               }

               return;
            }

            if (segLen > 0 && segAck == sndUna && this._tcpReassemblyQueue.getAvailableSpace() >= segLen) {
               this._tcpQueueManager.extractDataFromDatagram(tcpDatagram);
               return;
            }
         }

         switch (super._currentTcpState) {
            case 1:
            default:
               if ((segFlags & 2) != 0) {
                  this._tcpQueueManager.setIrs(segSeq);
                  this.processSynOnlyTcpOptions(props);
                  this._tcpIOThread.addSendRequest();
                  return;
               }

               return;
            case 2:
               sndNxt++;
               if ((segFlags & 16) != 0) {
                  if (segAck != this._sendHotlist.getIss() + 1) {
                     if ((segFlags & 4) != 0) {
                        return;
                     }

                     this.sendImmediateReset(segAck);
                     return;
                  }

                  if ((segFlags & 4) != 0) {
                     this.setState(0);
                     return;
                  }
               } else if ((segFlags & 4) != 0) {
                  return;
               }

               if ((segFlags & 2) != 0) {
                  this._tcpQueueManager.setIrs(segSeq);
                  if ((segFlags & 16) == 0) {
                     this._connectionOpenedWithType = 1;
                     this.setState(3);
                     this._sendHotlist._mustSendSyn = true;
                     this.sendAck();
                     if (props._sackPermitted) {
                        this._tcpOptionManager._sackEnabled = true;
                     } else {
                        this._tcpOptionManager._sackEnabled = false;
                     }

                     if (props._maxSegmentSize != -1) {
                        this._tcpWindowManager.setPeerMaxSegmentSize(props._maxSegmentSize);
                     }

                     if (props._windowScale != -1) {
                        this._tcpWindowManager.setPeerWindowScale(props._windowScale);
                     }

                     return;
                  }

                  this._sendHotlist.ackReceivedInSynSent(segAck);
                  this._tcpQueueManager._peerSackList.removeUpTo(segAck);
                  this._tcpWindowManager.ackReceived(segAck, props._window);
                  this.stopRetransmitTimer();
                  this._sendHotlist._immediateAckRequired = true;
                  if (this._sendHotlist._ourSynWasAcked) {
                     this._connectionOpenedWithType = 2;
                     this.setState(4);
                     this.sendAck();
                     this.processSynOnlyTcpOptions(props);
                     this.processDataAndFin(tcpDatagram, segFlags);
                     return;
                  }
               }

               if ((segFlags & 2) == 0 && (segFlags & 4) == 0) {
                  return;
               }
            case 0:
               boolean resetValid = true;
               if ((segFlags & 4) != 0) {
                  resetValid = TcpUtils.isValidSequenceNumber(
                     segSeq, rcvNxt - this._tcpWindowManager._maxSegmentSize, this._tcpWindowManager.getLocalWindow(), segLen
                  );
               }

               if (resetValid && TcpUtils.isValidSequenceNumber(segSeq, rcvNxt, this._tcpWindowManager.getLocalWindow(), segLen)) {
                  if ((segFlags & 2) != 0 && super._currentTcpState == 10 && TcpUtils.seqGT(segSeq, this._sendHotlist.getSendNext())) {
                     this.initiateShutDown();
                  } else if (this._tcpWindowManager.getLocalWindow() == 0 && segSeq == rcvNxt && !this._sendHotlist._haveSentFin) {
                     this._tcpWindowManager._immediateAckRequired = true;
                     this._sendHotlist._immediateAckRequired = true;
                  } else {
                     if ((segFlags & 4) != 0) {
                        switch (super._currentTcpState) {
                           case 2:
                              break;
                           case 3:
                           default:
                              if (this._connectionOpenedWithType == 2) {
                                 this.setState(0);
                                 return;
                              }

                              this.setState(0);
                              return;
                           case 4:
                           case 5:
                           case 6:
                           case 7:
                           case 8:
                           case 9:
                           case 10:
                              this.setState(0);
                              if (!super._inputStreamClosed) {
                                 try {
                                    if (super._inStream != null) {
                                       EventLogger.logEvent(447071754022829032L, 1413704051, 5);
                                       super._inStream.close();
                                       return;
                                    }
                                 } finally {
                                    return;
                                 }
                              }

                              return;
                        }
                     }

                     if ((segFlags & 2) != 0) {
                        this.sendImmediateReset((segFlags & 16) != 0 ? segAck : this._sendHotlist.getSendNext());

                        try {
                           this.close();
                        } finally {
                           this.initiateShutDown();
                           return;
                        }
                     } else if ((segFlags & 16) != 0) {
                        switch (super._currentTcpState) {
                           case 3:
                           default:
                              if (!TcpUtils.isValidAck(this._sendHotlist.getIss(), segAck, ++sndNxt)) {
                                 this.sendImmediateReset(segAck);

                                 try {
                                    this.close();
                                    return;
                                 } finally {
                                    this.initiateShutDown();
                                    return;
                                 }
                              } else {
                                 this._sendHotlist.ackReceivedInSynSent(segAck);
                                 this._tcpQueueManager._peerSackList.removeUpTo(segAck);
                                 this._tcpWindowManager.ackReceived(segAck, props._window);
                                 this.setState(4);
                              }
                           case 4:
                           case 5:
                           case 6:
                           case 7:
                           case 8:
                           case 9:
                           case 10:
                              int previousHighestAckNumberReceived = this._sendHotlist.getHighestAckNumberReceived();
                              int highestSentSequenceNumber = this._sendHotlist._highestSentSequenceNumber;
                              if (TcpUtils.seqGT(segAck, highestSentSequenceNumber)
                                 && (!this._sendHotlist._haveSentFin || segAck != this._sendHotlist.getLocalFinSequenceNumber() + 1)) {
                                 this._sendHotlist._immediateAckRequired = true;
                                 return;
                              } else {
                                 if (!TcpUtils.seqLT(segAck, sndUna)) {
                                    this._tcpQueueManager.extractSequencingInfo(tcpDatagram);
                                    this._tcpWindowManager.ackReceived(segAck, props._window);
                                    if (this._tcpOptionManager._keepAliveEnabled) {
                                       this._sendHotlist._keepAlivePacketRequired = false;
                                       this.restartKeepAliveTimer();
                                    }

                                    if (this._sendHotlist._ourFinWasAcked) {
                                       this.stopRetransmitTimer();
                                    } else if (TcpUtils.seqGT(segAck, previousHighestAckNumberReceived)) {
                                       this.stopRetransmitTimer();
                                       if (segAck != highestSentSequenceNumber || this._sendHotlist._haveEverSentFin) {
                                          this._timers.startTimer(this, 1);
                                       }
                                    } else if (segAck == this._sendHotlist._highestSentSequenceNumber && !this._sendHotlist._haveEverSentFin) {
                                       this.stopRetransmitTimer();
                                    }
                                 }

                                 switch (super._currentTcpState) {
                                    case 5:
                                    case 9:
                                       break;
                                    case 6:
                                    default:
                                       if (this._sendHotlist._ourFinWasAcked) {
                                          this.setState(9);
                                       }
                                       break;
                                    case 7:
                                       if (this._sendHotlist._ourFinWasAcked) {
                                          this.setState(10);
                                       }
                                       break;
                                    case 8:
                                       if (this._sendHotlist._ourFinWasAcked) {
                                          this.setState(0);
                                          return;
                                       }
                                       break;
                                    case 10:
                                       if ((segFlags & 1) != 0) {
                                          this._sendHotlist._immediateAckRequired = true;
                                       }
                                 }
                              }
                           case 2:
                              this.processDataAndFin(tcpDatagram, segFlags);
                        }
                     }
                  }
               } else if ((segFlags & 4) == 0) {
                  this._sendHotlist._immediateAckRequired = true;
               }
         }
      }
   }

   final int pendingConnectionDatagramReceived(TcpDatagramBase tcpDatagram) {
      this.tcpInput(tcpDatagram);
      return super._currentTcpState;
   }

   public final boolean shouldWeSendAPacket() {
      return this.shouldWeSendAPacket(false);
   }

   @Override
   public final void setConnectionCloseListener(ConnectionCloseListener listener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final boolean connectionStatusAvailable() {
      return true;
   }

   @Override
   public final boolean isConnectionEstablished() {
      return super._currentTcpState == 4 && !super._closeRequested && !super._abortWasCalled && !this._shutdownInProgress;
   }

   @Override
   public final long getSocketOptionEx(short option) {
      this.checkIfConnectionIsClosedClosingOrAborted();
      return option == 256 ? this._readTimeout : this.getSocketOption((byte)option);
   }

   @Override
   public final void timerExpired(int timer) {
      if (this._timers == null) {
         throw new Object();
      }

      switch (timer) {
         case 0:
         default:
            synchronized (this._tcpStateLock) {
               try {
                  this.close();
               } finally {
                  ;
               }

               return;
            }
         case 1:
            this.retransmissionTimerExpired();
            return;
         case 2:
            this.delayedAckTimerExpired();
            return;
         case 3:
            this.persistTimerExpired();
            return;
         case 4:
            if (super._currentTcpState != 9) {
               throw new Object();
            }

            this.finWait2Expired();
            return;
         case 5:
            if (super._currentTcpState != 10) {
               throw new Object();
            }

            this.setState(0);
            return;
         case 6:
            this.keepAliveTimerExpired();
            return;
         case 7:
            this.lingerTimerExpired();
            return;
         case 9:
            this.inactivityTimerExpired();
         case -1:
         case 8:
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
   public final int getSocketOption(byte option) {
      this.checkIfConnectionIsClosedClosingOrAborted();
      switch (option) {
         case -1:
            throw new Object();
         case 0:
            return -1;
         case 1:
            return this._tcpOptionManager._lingerTimeout;
         case 2:
            if (this._tcpOptionManager._keepAliveEnabled) {
               return 1;
            }

            return 0;
         case 3:
            return this._tcpReassemblyQueue.getMaxDataLimit();
         case 4:
         default:
            return this._sendBuffer.getBufferSize();
      }
   }

   @Override
   public final void setSocketOption(byte option, int value) {
      this.checkIfConnectionIsClosedClosingOrAborted();
      if (value < 0) {
         throw new Object();
      }

      switch (option) {
         case -1:
            throw new Object();
         case 1:
         default:
            if (value < 0) {
               throw new Object();
            }

            this._tcpOptionManager._lingerTimeout = value;
            return;
         case 2:
            if (value == 0) {
               this._tcpOptionManager._keepAliveEnabled = false;
               if (this._timers.isActive(this, 6)) {
                  this._timers.stopTimer(this, 6);
                  return;
               }
            } else {
               this._tcpOptionManager._keepAliveEnabled = true;
               if (!this._timers.isActive(this, 6)) {
                  this._timers.startTimer(this, 6);
                  return;
               }
            }
         case 0:
            return;
         case 3:
            synchronized (this._tcpReassemblyQueue) {
               this._tcpReassemblyQueue.setMaxDataLimit(value);
               return;
            }
         case 4:
            if (value < this._sendBuffer.getLength()) {
               throw new Object();
            } else {
               synchronized (this._sendBuffer) {
                  this._sendBuffer.resizeBuffer(value);
               }
            }
      }
   }

   @Override
   public final boolean isAddressed(DatagramAddressBase inputAddress) {
      return super._myAddress != null ? super._myAddress.equals(inputAddress) : false;
   }

   @Override
   public final void abort() {
      if (!super._abortWasCalled) {
         super._abortWasCalled = true;
         if (super._currentTcpState != 0) {
            if (super._currentTcpState != 1) {
               if (super._currentTcpState == 2) {
               }

               synchronized (this._tcpStateLock) {
                  if (super._currentTcpState == 3
                     || super._currentTcpState == 4
                     || super._currentTcpState == 6
                     || super._currentTcpState == 9
                     || super._currentTcpState == 5) {
                     this.sendImmediateReset(this._sendHotlist.getSendNext());
                  }
               }

               this._tcpIOThread.abort();
               if (this._tcpQueueManager != null) {
                  this._tcpQueueManager.abort();
               }

               if (this._tcpWindowManager != null) {
                  this._tcpWindowManager.abort();
               }

               if (this._tcpOptionManager != null) {
                  this._tcpOptionManager.abort();
               }

               this.setState(0);
            }
         }
      }
   }

   private final void sendImmediateReset(int resetSequenceNumber) {
      TcpDatagramProperties props = this._tcpObjectPool.getNewTcpDatagramProperties();
      props.setData(resetSequenceNumber, 0, 5, 4, 0, 0, true);
      TcpDatagramBase datagram = this._tcpObjectPool.getNewTcpDatagram();
      datagram.setData(new byte[0], 0, 0, super._myAddress);
      this.copyFlagsInto(datagram, false);
      datagram._tcpProps = props;

      try {
         this.sendDatagram(datagram);
      } finally {
         return;
      }
   }

   private final void setState(int newState) {
      boolean shouldCallShutdown = false;
      synchronized (this._tcpStateLock) {
         super._currentTcpState = newState;
         switch (newState) {
            case 0:
               shouldCallShutdown = true;
               break;
            case 4:
               this._timers.stopTimer(this, 0);
               break;
            case 5:
               if (this._closeListener != null) {
                  this._closeListener.connectionClosed(this);
               }
               break;
            case 9:
               this._timers.startTimer(this, 4);
               break;
            case 10:
               this._timers.stopAll(this);
               this._timers.startTimer(this, 5);
               if (this._tcpReassemblyQueue != null) {
                  synchronized (this._tcpReassemblyQueue) {
                     this._tcpReassemblyQueue.notifyAll();
                  }
               }

               if (this._sendBuffer != null) {
                  synchronized (this._sendBuffer) {
                     this._sendBuffer.notifyAll();
                  }
               }
         }

         this._tcpStateLock.notifyAll();
      }

      if (shouldCallShutdown) {
         this.initiateShutDown();
      }

      switch (newState) {
         case 0:
         case 1:
         case 4:
            this.stopInactivityTimer();
            return;
         default:
            this.kickInactivityTimer();
      }
   }

   private final void sendAck() {
      this._sendHotlist._immediateAckRequired = true;
      this._tcpIOThread.addSendRequest();
   }

   @Override
   protected final void processReceivedDatagram(Datagram datagram) {
      this._tcpIOThread.addReceiveRequest((TcpDatagramBase)datagram);
   }

   @Override
   public final int getPort() {
      this.checkIfConnectionIsClosedClosingOrAborted();
      return super._myAddress.getDestPort();
   }

   @Override
   public final int getLocalPort() {
      this.checkIfConnectionIsClosedClosingOrAborted();
      return this.getLocalPortInternal();
   }

   @Override
   public final int getLocalPortInternal() {
      return super._myAddress.getLocalPort();
   }

   private final void processSynOnlyTcpOptions(TcpDatagramProperties props) {
      if (props._sackPermitted) {
         this._tcpOptionManager._sackEnabled = true;
      } else {
         this._tcpOptionManager._sackEnabled = false;
      }

      if (props._maxSegmentSize != -1) {
         this._tcpWindowManager.setPeerMaxSegmentSize(props._maxSegmentSize);
      }

      if (props._windowScale != -1) {
         this._tcpWindowManager.setPeerWindowScale(props._windowScale);
      }
   }

   private final void processDataAndFin(TcpDatagramBase tcpDatagram, int segFlags) {
      switch (super._currentTcpState) {
         case 4:
         case 6:
         case 9:
         default:
            this._tcpQueueManager.extractDataFromDatagram(tcpDatagram);
            if (this._sendHotlist._haveReceivedFin && this._sendHotlist.getPeerFinSequenceNumber() + 1 == this._sendHotlist.getAckNumberToSend()) {
               this.changeStateToCloseWait();
            }
         case 5:
         case 7:
         case 8:
         case 10:
            switch (super._currentTcpState) {
               case -1:
                  if ((segFlags & 1) != 0) {
                     this._sendHotlist._immediateAckRequired = true;
                     if (tcpDatagram._tcpProps._sequenceNumber + tcpDatagram.getLength() == this._sendHotlist.getAckNumberToSend()) {
                        if (!this._sendHotlist._haveReceivedFin) {
                           this._sendHotlist.peerFinReceived(tcpDatagram._tcpProps._sequenceNumber + tcpDatagram.getLength());
                        }

                        switch (super._currentTcpState) {
                           case 2:
                           case 5:
                           case 8:
                              break;
                           case 3:
                           case 4:
                           default:
                              this.changeStateToCloseWait();
                              return;
                           case 6:
                           case 7:
                              if (this._sendHotlist._ourFinWasAcked) {
                                 this.setState(10);
                                 return;
                              }
                              break;
                           case 9:
                              if (this._sendHotlist._haveReceivedFin) {
                                 this._sendHotlist._immediateAckRequired = true;
                              }

                              this._sendHotlist._immediateAckRequired = true;
                              return;
                           case 10:
                              if (!this._timers.isActive(this, 5)) {
                                 this._timers.stopTimer(this, 5);
                              }

                              this._timers.startTimer(this, 5);
                        }
                     }
                  }

                  return;
               case 0:
               case 1:
               case 2:
            }
      }
   }

   private final void changeStateToCloseWait() {
      this.setState(5);
      if (this._tcpReassemblyQueue != null) {
         synchronized (this._tcpReassemblyQueue) {
            this._tcpReassemblyQueue.notifyAll();
         }
      }
   }

   @Override
   protected final void checkIfConnectionIsClosedClosingOrAborted() {
      if (super._currentTcpState == 0 || super._closeRequested || super._abortWasCalled || this._shutdownInProgress) {
         throw new Object();
      }
   }

   private final void initiateShutDown() {
      if (this._tcpIOThread != null) {
         this._tcpIOThread.requestShutDown();
      }
   }

   private final void closeConnectionInternally() {
      EventThreadCheck.throwException();
      synchronized (this._tcpStateLock) {
         if (super._abortWasCalled) {
            super._currentTcpState = 0;
         }

         if (super._currentTcpState != 0) {
            switch (super._currentTcpState) {
               case 0:
                  break;
               case 1:
               case 2:
               default:
                  this.setState(0);
                  break;
               case 3:
               case 4:
                  this._tcpIOThread.addSendRequest();
                  break;
               case 5:
                  this._sendHotlist.endSendTransmission();
                  this._tcpIOThread.addSendRequest();
            }
         }
      }
   }

   private final void finWait2Expired() {
      this.initiateShutDown();
   }

   private final void persistTimerExpired() {
      this._tcpIOThread.addSendRequest();
      int base = 3000;
      int nextTimeout = base * TcpConstants.TCP_EXP_BACKOFF[this._timerBackOffCounter];
      if (nextTimeout > 60000) {
         nextTimeout = 60000;
      }

      this._timers.startTimer(this, 3, nextTimeout);
      if (this._timerBackOffCounter < 11) {
         this._timerBackOffCounter++;
      }
   }

   @Override
   public final void close() {
      if (!super._closeRequested && !super._abortWasCalled && !this._shutdownInProgress) {
         super._closeRequested = true;
         if (!super._outputStreamOpened && super._outStream != null) {
            super._outStream.close();
         }

         if (!super._inputStreamOpened && super._inStream != null) {
            super._inStream.close();
         }
      }
   }

   @Override
   public final int getProperties(String name) {
      URL url = (URL)(new Object("tcp", name));
      URLParameters params = url.getRIMParameters();
      if (params != null && params.containParameter(INTERFACE)) {
         String value = params.getValue(INTERFACE);
         if (StringUtilities.strEqualIgnoreCase(value, "wifi", 1701707776)) {
            return 18;
         }
      }

      return 2;
   }

   private final int retrieveApn(boolean any) {
      int apnId = -1;
      if (super._myAddress != null && super._myAddress.getApnName() != null) {
         try {
            if (any || super._myAddress.compareApn(WLAN.WLAN_PSEUDO_APN)) {
               return RadioInfo.getAccessPointNumber(super._myAddress.getApnName());
            }
         } finally {
            return apnId;
         }
      }

      return apnId;
   }

   private final void checkRetransmissionAndPersistTimers(int numBytesToSend) {
      if (this._sendHotlist._keepAlivePacketRequired && !this._timers.isActive(this, 1)) {
         this.resetRetransmitTimer();
      } else {
         switch (super._currentTcpState) {
            case 1:
            case 5:
               return;
            case 2:
            case 3:
            default:
               if (!this._timers.isActiveOrRunning(this, 1)) {
                  this.resetRetransmitTimer();
               }

               return;
            case 4:
               if (this._tcpWindowManager.isInCongestionControlMode() && !this._isInPersistMode && !this._timers.isActive(this, 1) && numBytesToSend > 0) {
                  this.resetRetransmitTimer();
               }

               if (this._isInPersistMode && !this._timers.isActive(this, 3)) {
                  if (this._timers.isActive(this, 1)) {
                     this.stopRetransmitTimer();
                  }

                  this._timerBackOffCounter = 0;
                  this._timers.stopTimer(this, 3);
                  this._timers.startTimer(this, 3);
               }
            case 6:
            case 7:
            case 8:
               if (!this._timers.isActiveOrRunning(this, 1) && this._sendHotlist._haveEverSentFin && !this._sendHotlist._ourFinWasAcked) {
                  this.resetRetransmitTimer();
               }
         }
      }
   }

   private final void resetRetransmitTimer() {
      if (this._timers.isActive(this, 3)) {
         this._timers.stopTimer(this, 3);
      }

      this.stopRetransmitTimer();
      this._timers.startTimer(this, 1);
   }

   private final void stopRetransmitTimer() {
      this._timers.stopTimer(this, 1);
      this._timerBackOffCounter = 0;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      if ((mode & 3) != mode) {
         throw new Object();
      }

      int loc = name.indexOf(":");
      if (loc != -1 && loc != 2) {
         if (!TunnelCredentialsProvider.getInstance().isOutgoingSocketsAllowed()) {
            throw new Object("Tcp SocketConnections not allowed");
         } else {
            return TcpConnectionFactory.getInstance().getConnection(name, mode, timeouts);
         }
      } else {
         if (!TunnelCredentialsProvider.getInstance().isIncomingSocketsAllowed()) {
            throw new Object("Tcp ServerSocketsConnections not allowed");
         }

         super._isListenConnection = true;

         try {
            super._transport = (Transport)TransportRegistry.get("net.rim.device.cldc.io.tcp.Transport");
            String[] args = new Object[3];
            this._connectTimeOut = StreamDatagramAddressBase.retrieveSettings(name, args);
            int sessionTimeout = StreamDatagramAddressBase.retrieveSessionTimeout(name);
            if (StringUtilities.toLowerCase(name, 1701707776).indexOf(INTERFACE_WIFI) >= 0) {
               args[0] = WLAN.WLAN_PSEUDO_APN;
               args[1] = null;
               args[2] = null;
            }

            TcpServerSocketConnection tcpServerSocketConnection = new TcpServerSocketConnection(
               super._transport, this.openTunnel(args[0], args[1], args[2], sessionTimeout)
            );
            return tcpServerSocketConnection.openPrim(name, args[0], mode, timeouts);
         } finally {
            ;
         }
      }
   }

   private final void delayedAckTimerExpired() {
      this._sendHotlist._immediateAckRequired = true;
      this._sendHotlist._delayedAckRequired = false;
      this._tcpIOThread.addSendRequest();
   }

   private final void stopInactivityTimer() {
      if (this._timers.isActive(this, 9)) {
         this._timers.stopTimer(this, 9);
      }
   }

   private final void startInactivityTimer() {
      this._timers.startTimer(this, 9);
   }

   private final void kickInactivityTimer() {
      this.stopInactivityTimer();
      this.startInactivityTimer();
   }

   private final void stopDelayedAckTimer() {
      if (this._timers.isActive(this, 2)) {
         this._timers.stopTimer(this, 2);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void openPrimHelper(String name, int mode, boolean timeouts) {
      EventThreadCheck.throwException();
      if (super._freshnessSealed) {
         this._tcpStateLock = new Object();
         this._sendBuffer = new TcpBuffer(7200);
         this._tcpQueueManager = new TcpQueueManager();
         this._sendHotlist = this._tcpQueueManager._sendHotlist;
         this._tcpReassemblyQueue = this._tcpQueueManager._tcpReassemblyQueue;
         this._tcpWindowManager = new TcpWindowManager();
         this._tcpOptionManager = new TcpOptionManager();
      } else {
         this._sendBuffer.reset();
         this._tcpQueueManager.reset();
         this._tcpWindowManager.reset();
         this._tcpOptionManager.reset();
         super._currentTcpState = 0;
         this._connectionOpenedWithType = 0;
         this._timerBackOffCounter = 0;
         this._sendBufferPointer = 0;
         this._noMoreData = false;
         super._outputStreamClosed = false;
         super._inputStreamClosed = false;
         super._closeRequested = false;
         this._connectionTimedOut = false;
         super._abortWasCalled = false;
         this._shutdownInProgress = false;
         this._isInPersistMode = false;
      }

      super.openPrim(name, mode, timeouts);
      super._inStream = new TcpInputStream(this);
      super._outStream = new TcpOutputStream(this);
      this._tcpIOThread = new TcpIOThread(this);
      TcpProcess.getInstance().startThread(this._tcpIOThread);
      this._tcpWindowManager
         .setAttributes(
            this._tcpQueueManager._tcpReassemblyQueue, this._tcpOptionManager, this._tcpQueueManager, this, ((Transport)super._transport).getMaximumLength()
         );
      this._tcpQueueManager.setAttributes(this, this._tcpWindowManager, this._tcpOptionManager, this._sendBuffer);
      this._tcpQueueManager.init();
      this._tcpWindowManager.init(-1);
      super._freshnessSealed = false;
      this._timers = ((Transport)super._transport).getTcpTimers();
      this._timers.addConnection(this);
      this._readTimeout = 0;
      this._startIdleTime = System.currentTimeMillis();
      EventLogger.logEvent(447071754022829032L, 1413695843, 4);
      String[] args = new Object[3];
      this._connectTimeOut = StreamDatagramAddressBase.retrieveSettings(name, args);
      int sessionTimeout = StreamDatagramAddressBase.retrieveSessionTimeout(name);
      String n = StringUtilities.toLowerCase(name, 1701707776);
      boolean wifi = n.indexOf(INTERFACE_WIFI) >= 0;
      boolean retryOnNoContext = n.indexOf(ALLOW_NO_CONTEXT_RETRY) >= 0;
      if (wifi || retryOnNoContext) {
         if (wifi) {
            args[0] = WLAN.WLAN_PSEUDO_APN;
            args[1] = null;
            args[2] = null;
         }

         if (retryOnNoContext) {
            boolean var17 = false /* VF: Semaphore variable */;

            label126:
            try {
               var17 = true;
               ControlledAccess.assertRRISignatures(true);
               this.setFlag(1024, true);
               var17 = false;
            } finally {
               if (var17) {
                  EventLogger.logEvent(447071754022829032L, 1413695843, 3);
                  break label126;
               }
            }
         }
      }

      super._tunnel = this.openTunnel(args[0], args[1], args[2], sessionTimeout);
      String apn = args[0];
      if (super._tunnel != null) {
         apn = super._tunnel.getConfig().getName();
      }

      if (!super._isListenConnection) {
         super._myAddress = new TcpAddress(name, apn);
         if (super._myAddress.getLocalPort() == -1) {
            int index = n.indexOf(LOCAL_PORT);
            if (index >= 0) {
               label119:
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
                  break label119;
               }
            }
         }
      }

      this._tcpObjectPool = ((net.rim.device.cldc.io.tcpdatagram.Protocol)((Transport)super._transport).getSubConnection()).getTcpObjectPool();
   }

   private final void restartKeepAliveTimer() {
      if (this._timers.isActive(this, 6)) {
         this._timers.stopTimer(this, 6);
      }

      this._timers.startTimer(this, 6);
   }

   private final void keepAliveTimerExpired() {
      this._sendHotlist._keepAlivePacketRequired = true;
      this._tcpIOThread.addSendRequest();
   }

   private final void lingerTimerExpired() {
      if (!this._sendHotlist._ourFinWasAcked) {
         this.abort();
      }
   }
}
