package net.rim.device.cldc.io.tcp;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.internal.io.tcp.SimpleTcpDataBlock;
import net.rim.device.internal.io.tcp.TcpConstants;
import net.rim.device.internal.io.tcp.TcpDataBlock;
import net.rim.device.internal.io.tcp.TcpDatagramBase;
import net.rim.device.internal.io.tcp.TcpDatagramProperties;
import net.rim.device.internal.io.tcp.TcpUtils;

final class TcpQueueManager implements TcpConstants {
   protected Protocol _tcpConnection;
   protected TcpReassemblyDataList _tcpReassemblyQueue;
   protected TcpOptionManager _tcpOptionManager;
   protected TcpDataList _peerSackList;
   SendHotlist _sendHotlist;
   protected TcpBuffer _sendBuffer;
   protected TcpWindowManager _tcpWindowManager;
   protected int _globalIss;
   protected int _irs;
   protected int _lastAckNumberReceived;
   protected int _duplicateAcks;
   boolean _sackEnabled = true;
   protected boolean _freshnessSealed = true;

   TcpQueueManager() {
      this.reset();
   }

   final void reset() {
      this._globalIss = RandomSource.getInt();
      if (this._freshnessSealed) {
         this._peerSackList = new TcpDataList();
         this._sendHotlist = new SendHotlist();
         this._tcpReassemblyQueue = new TcpReassemblyDataList(this._sendHotlist);
      } else {
         this._tcpReassemblyQueue.purge();
         this._peerSackList.purge();
         this._sendHotlist.init();
         this._irs = 0;
         this._sackEnabled = true;
         this._lastAckNumberReceived = 0;
         this._duplicateAcks = 0;
      }

      this._freshnessSealed = false;
   }

   final void abort() {
      this._tcpReassemblyQueue.purge();
      this._peerSackList.purge();
      this._sendHotlist.abort();
      this._irs = 0;
      this._sackEnabled = true;
      this._lastAckNumberReceived = 0;
      this._duplicateAcks = 0;
   }

   final void retransmissionTimerExpired() {
      this._sendHotlist.retransmissionTimeoutOccurred();
      this._peerSackList.purge();
   }

   final void setAttributes(Protocol tcpConnection, TcpWindowManager tcpWindowManager, TcpOptionManager tcpOptionManager, TcpBuffer sendBuffer) {
      this._tcpConnection = tcpConnection;
      this._tcpWindowManager = tcpWindowManager;
      this._tcpOptionManager = tcpOptionManager;
      this._sendBuffer = sendBuffer;
      this._sendHotlist.setAttributes(tcpConnection);
   }

   final void setTcpWindowManager(TcpWindowManager tcpWindowManager) {
      this._tcpWindowManager = tcpWindowManager;
   }

   final void setTcpOptionManager(TcpOptionManager tcpOptionManager) {
      this._tcpOptionManager = tcpOptionManager;
   }

   final void extractDataFromDatagram(TcpDatagramBase tcpDatagramIn) {
      if (tcpDatagramIn.getLength() > 0) {
         TcpDatagramProperties props = tcpDatagramIn._tcpProps;
         TcpReassembleDataNode node = new TcpReassembleDataNode(
            props._sequenceNumber, tcpDatagramIn.getLength(), tcpDatagramIn.getOffset(), tcpDatagramIn.getData()
         );
         synchronized (this._tcpReassemblyQueue) {
            this._tcpReassemblyQueue.addData(node);
            this._tcpReassemblyQueue.notify();
         }
      }
   }

   final int fastExtractSequencingInfo(TcpDatagramBase tcpDatagramIn) {
      TcpDatagramProperties props = tcpDatagramIn._tcpProps;
      this._duplicateAcks = 0;
      synchronized (this._sendBuffer) {
         this._sendBuffer.removeUpTo(props._acknowledgementNumber, this._sendHotlist._ourFinWasAcked);
         this._sendHotlist.ackReceived(props._acknowledgementNumber);
         this._lastAckNumberReceived = props._acknowledgementNumber;
         this._sendBuffer.notifyAll();
      }

      return this._sendHotlist._highestAckNumberReceived;
   }

   final int extractSequencingInfo(TcpDatagramBase tcpDatagramIn) {
      TcpDatagramProperties props = tcpDatagramIn._tcpProps;
      if (props._acknowledgementNumber == this._lastAckNumberReceived
         && TcpUtils.seqLT(this._lastAckNumberReceived, this._sendHotlist._highestSentSequenceNumber)) {
         this._sendHotlist.ackReceived(props._acknowledgementNumber);
         this._peerSackList.removeUpTo(props._acknowledgementNumber);
         this._duplicateAcks++;
         if (this._duplicateAcks >= 3) {
            this._duplicateAcks = 0;
            this._sendHotlist._receiverFastRetransmitOn = true;
            this._sendHotlist.enqueueFastRetransmitBlock(this._sendBuffer.getLength());
         }
      } else {
         this._duplicateAcks = 0;
         if (TcpUtils.seqGEQ(props._acknowledgementNumber, this._sendHotlist._highestAckNumberReceived)) {
            synchronized (this._sendBuffer) {
               if (this._sendHotlist._haveEverSentFin && props._acknowledgementNumber == this._sendHotlist.getLocalFinSequenceNumber() + 1) {
                  this._sendHotlist.localFinWasAcked();
               }

               this._sendBuffer.removeUpTo(props._acknowledgementNumber, this._sendHotlist._ourFinWasAcked);
               this._sendHotlist.ackReceived(props._acknowledgementNumber);
               this._peerSackList.removeUpTo(props._acknowledgementNumber);
               this._lastAckNumberReceived = props._acknowledgementNumber;
               this._sendBuffer.notifyAll();
            }
         }
      }

      if (this._tcpOptionManager._sackEnabled && props._sackBlocks != null) {
         TcpDataBlock[] sackBlocks = props._sackBlocks;

         for (int i = 0; i < sackBlocks.length; i++) {
            TcpDataBlock sb = sackBlocks[i];
            if (TcpUtils.seqGT(sb.getLeftEdge(), this._sendHotlist.getHighestAckNumberReceived())
               && TcpUtils.seqGEQ(this._sendHotlist.getSendNext(), sb.getRightEdge())
               && TcpUtils.seqGT(sb.getRightEdge(), sb.getLeftEdge())) {
               this._peerSackList.addData(sb);
            }
         }

         if (this._peerSackList.isEmpty()) {
            return this._sendHotlist._highestAckNumberReceived;
         }

         if (TcpUtils.seqGT(this._peerSackList.getLeftEdge(), this._lastAckNumberReceived)) {
            this._sendHotlist.prependSendRequest(new SimpleTcpDataBlock(this._lastAckNumberReceived, this._peerSackList.getLeftEdge()));
         }
      }

      return this._sendHotlist._highestAckNumberReceived;
   }

   final void init() {
      this._sendHotlist.setIss(this._globalIss);
      this._sendBuffer.setStartSeqNumber(this._globalIss + 1);
      this.incrementIss();
      this._sendHotlist.init();
      this._tcpReassemblyQueue.purge();
   }

   final void setIrs(int segSeq) {
      this._tcpReassemblyQueue.setInitialAck(segSeq + 1);
      this._sendHotlist.setIrs(segSeq);
   }

   final void incrementIss() {
      this._globalIss += 64000;
      this._globalIss *= -1;
   }

   final boolean shouldWeSendAPacket() {
      return this._sendHotlist.shouldWeSendAPacket(this._tcpWindowManager.getAvailableWindow());
   }
}
