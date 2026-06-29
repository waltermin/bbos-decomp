package com.fourthpass.wapstack.wtp;

import com.fourthpass.wapstack.IPacketTransiver;
import com.fourthpass.wapstack.IWapStackLayer;
import com.fourthpass.wapstack.util.ITimerScheduler;
import com.fourthpass.wapstack.util.PushEvent;
import com.fourthpass.wapstack.util.UserDefinedEvent;
import com.fourthpass.wapstack.util.WTimer;
import com.fourthpass.wapstack.wdp.WDPPacket;
import com.fourthpass.wapstack.wtp.pdu.WTP_AbortPDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_AckPDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_InvokePDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_NegAckPDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_PDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_ResultPDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_SegInvokePDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_SegResultPDU;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.cldc.io.utility.PacketLogger;
import net.rim.device.internal.proxy.Proxy;

public final class WTPLayer implements IWapStackLayer {
   private IWapStackLayer _userLayer;
   private IWapStackLayer _submissionLayer;
   private IPacketTransiver _transiver;
   private WTimer _WTPTimer;
   private Hashtable _lastTIDTable;
   private IntHashtable _transactionsTable;
   private Hashtable _requestorsTable;
   private boolean _sarMode;
   private boolean _closed;
   private WTPListenerThread _wtpListenerThread;
   private int _TID;
   private boolean _TIDnew;
   private PacketLogger _packetLoggerInstance = PacketLogger.getInstance();

   public final void registerRequestor(WTP_Transaction transaction, IWapStackLayer requestor) {
      this._requestorsTable.put(transaction, requestor);
   }

   protected final void registerTransaction(WTP_Transaction transaction) {
      this._transactionsTable.put(transaction.getTID(), transaction);
   }

   protected final void unregisterTransaction(WTP_Transaction transaction) {
      this._transactionsTable.remove(transaction.getTID());
      this._requestorsTable.remove(transaction);
   }

   protected final void reTransmitPDU(WTP_PDU wtpPDU) {
      WDPPacket dataPacket = new WDPPacket(wtpPDU.getDataToBeReTransmitted());
      if (this._transiver.send(dataPacket) == -1) {
      }
   }

   protected final void reTransmitPDU(WTP_Transaction transaction) {
      WTP_PDU wtpPDU = transaction.getLastTransmitted();
      if (wtpPDU != null) {
         WDPPacket dataPacket = new WDPPacket(wtpPDU.getDataToBeReTransmitted());
         if (this._transiver.send(dataPacket) == -1) {
         }
      }
   }

   protected final void transmitNackPDU(WTP_Transaction tr, byte[] seqNums) {
      WTP_NegAckPDU nack = new WTP_NegAckPDU(tr.getTID(), (byte)seqNums.length, seqNums);
      this.transmitPDU(nack);
   }

   public final WTP_Transaction_Initiator requestTransaction(IWapStackLayer requestor, byte transClass) {
      WTP_Transaction_Initiator trInitiator = null;
      int nextTID = this.nextTID();
      switch (transClass) {
         case -1:
            break;
         case 0:
         default:
            trInitiator = new WTP_TCL0_Initiator(this, nextTID, this.getTIDnew());
            break;
         case 1:
            trInitiator = new WTP_TCL1_Initiator(this, nextTID, this.getTIDnew());
            break;
         case 2:
            trInitiator = new WTP_TCL2_Initiator(this, nextTID, this.getTIDnew());
      }

      if (trInitiator != null) {
         this.registerRequestor(trInitiator, requestor);
      }

      return trInitiator;
   }

   protected final void doTransactionNotification(WTP_Transaction transaction) {
      IWapStackLayer requestor = (IWapStackLayer)this._requestorsTable.get(transaction);
      if (requestor != null) {
         requestor.eventOccured(transaction);
      } else {
         if (this._userLayer != null) {
            this._userLayer.eventOccured(transaction);
         }
      }
   }

   public final boolean invokeReceiver() {
      WDPPacket receiveDataPacket = new WDPPacket(0);
      int size = this._transiver.receive(receiveDataPacket);
      if (size <= 0) {
         return false;
      }

      this.packetReceived(receiveDataPacket);
      return true;
   }

   protected final void addTimerTask(ITimerScheduler scheduler, int secTimeout, byte taskId) {
      if (this._WTPTimer != null) {
         this._WTPTimer.addTask(scheduler, secTimeout * 1000, taskId);
      }
   }

   protected final void removeTimerTask(ITimerScheduler scheduler) {
      if (this._WTPTimer != null) {
         this._WTPTimer.removeTask(scheduler);
      }
   }

   protected final void removeTimerTask(ITimerScheduler scheduler, byte taskId) {
      if (this._WTPTimer != null) {
         this._WTPTimer.removeTask(scheduler, taskId);
      }
   }

   protected final void transmitInvokePDU(WTP_Transaction transaction, byte[] data, boolean segmented, boolean retransmit) {
      WTP_InvokePDU invoke = new WTP_InvokePDU(transaction.getTID(), transaction.getTIDnew(), false, transaction.getClassType(), this._sarMode, !segmented);
      invoke.addData(data);
      if (retransmit) {
         this.reTransmitPDU(invoke);
      } else {
         this.transmitPDU(invoke);
         transaction.setLastTransmitted(invoke);
      }
   }

   protected final void transmitSegmentedInvokePDU(
      WTP_Transaction transaction, byte[] data, int packetNo, boolean lastInGroup, boolean lastPacket, boolean retransmit
   ) {
      WTP_SegInvokePDU invoke = new WTP_SegInvokePDU(transaction.getTID(), packetNo, lastInGroup, lastPacket);
      invoke.addData(data);
      if (retransmit) {
         this.reTransmitPDU(invoke);
      } else {
         this.transmitPDU(invoke);
         transaction.setLastTransmitted(invoke);
      }
   }

   protected final void transmitAbortPDU(WTP_Transaction transaction, byte type, byte reason) {
      WTP_AbortPDU abort = new WTP_AbortPDU(type, transaction.getTID(), reason);
      this.transmitPDU(abort);
      transaction.setLastTransmitted(abort);
   }

   public final void transmitAbortPDU(int TID, byte type, byte reason) {
      WTP_AbortPDU abort = new WTP_AbortPDU(type, TID, reason);
      this.transmitPDU(abort);
   }

   public final void transmitResultPDU(int TID) {
      WTP_ResultPDU result = new WTP_ResultPDU(TID);
      this.transmitPDU(result);
   }

   protected final void transmitAckPDU(WTP_Transaction transaction, boolean tve_tok) {
      WTP_AckPDU ack = new WTP_AckPDU(transaction.getTID(), tve_tok);
      this.transmitPDU(ack);
      transaction.setLastTransmitted(ack);
   }

   public final void transmitAckPDU(int TID, boolean tve_tok) {
      WTP_AckPDU ack = new WTP_AckPDU(TID, tve_tok);
      this.transmitPDU(ack);
   }

   protected final void transmitSARAckPDU(WTP_Transaction tr, int seqNum, boolean isRetransmit) {
      WTP_AckPDU ack = new WTP_AckPDU(tr.getTID(), false);
      ack.addPacketSeqNumTPI((byte)seqNum);
      if (isRetransmit) {
         this.reTransmitPDU(ack);
      } else {
         this.transmitPDU(ack);
      }
   }

   @Override
   public final void close() {
      if (!this._closed) {
         this._closed = true;
         this._userLayer = null;
         this._wtpListenerThread.shutdown();
         this._WTPTimer.stopTimer();
         this._WTPTimer = null;
         if (this._submissionLayer != null) {
            this._submissionLayer.close();
         }

         Enumeration en = this._transactionsTable.elements();

         while (en.hasMoreElements()) {
            WTP_Transaction transactions = (WTP_Transaction)en.nextElement();
            transactions.abortTransaction();
         }

         this._transactionsTable.clear();
         this._requestorsTable.clear();
         this._lastTIDTable.clear();
      }
   }

   @Override
   public final void eventOccured(Object event) {
      if (!(event instanceof UserDefinedEvent)) {
         if (event instanceof WDPPacket) {
            this.packetReceived((WDPPacket)event);
         }
      } else {
         if (this._userLayer != null) {
            this._userLayer.eventOccured(event);
         }

         Enumeration en = this._requestorsTable.elements();

         while (en.hasMoreElements()) {
            IWapStackLayer waplayer = (IWapStackLayer)en.nextElement();
            waplayer.eventOccured(event);
         }
      }
   }

   @Override
   public final void setUserLayer(IWapStackLayer userLayer) {
      this._userLayer = userLayer;
   }

   private final void transmitPDU(WTP_PDU wtpPDU) {
      WDPPacket dataPacket = new WDPPacket(wtpPDU.getDataToBeTransmitted());
      if (this._packetLoggerInstance._highLoggingEnabled) {
         this._packetLoggerInstance.logPacket(dataPacket.getPacketData(), 0, dataPacket.getDataLength(), "<unknown_dest>:<unknown_port>", true);
      }

      if (this._transiver.send(dataPacket) == -1) {
      }
   }

   public WTPLayer(IWapStackLayer submissionLayer, boolean sarMode) {
      this._sarMode = sarMode;
      this._requestorsTable = (Hashtable)(new Object());
      this._transactionsTable = (IntHashtable)(new Object());
      this._lastTIDTable = (Hashtable)(new Object());
      this._submissionLayer = submissionLayer;
      this._transiver = (IPacketTransiver)submissionLayer;
      this._wtpListenerThread = new WTPListenerThread(this);
      Proxy.getInstance().startThread(this._wtpListenerThread);
      Random r = (Random)(new Object(System.currentTimeMillis()));
      this._TID = r.nextInt() & 16383;
      this._TIDnew = true;
      this._WTPTimer = new WTimer();
      if (this._TID >= 0 && this._TID <= 2) {
         this._TID = this.nextTID();
      }
   }

   private final void packetReceived(WDPPacket inPacket) {
      if (this._packetLoggerInstance._highLoggingEnabled) {
         this._packetLoggerInstance.logPacket(inPacket.getPacketData(), 0, inPacket.getDataLength(), "<unknown_dest>:<unknown_port>", false);
      }

      int length = inPacket.getDataLength();
      if (length >= 1) {
         byte[] data = inPacket.getPacketData();
         byte pduType = (byte)(data[0] >> 3 & 15);
         WTP_PDU wtpPDU = null;
         switch (pduType) {
            case 0:
               return;
            case 1:
            default:
               wtpPDU = new WTP_InvokePDU(inPacket);
               this.processInvokePDU(wtpPDU);
               return;
            case 2:
               wtpPDU = new WTP_ResultPDU(inPacket);
               break;
            case 3:
               wtpPDU = new WTP_AckPDU(inPacket);
               break;
            case 4:
               wtpPDU = new WTP_AbortPDU(inPacket);
               break;
            case 5:
               wtpPDU = new WTP_SegInvokePDU(inPacket);
               break;
            case 6:
               wtpPDU = new WTP_SegResultPDU(inPacket);
               break;
            case 7:
               wtpPDU = new WTP_NegAckPDU(inPacket);
         }

         WTP_Transaction transaction = (WTP_Transaction)this._transactionsTable.get(wtpPDU.getTID());
         if (transaction != null) {
            if (!transaction.receivedPDU(wtpPDU)) {
            }
         } else {
            if (wtpPDU.getPDUType() != 4) {
               this.transmitAckPDU(wtpPDU.getTID(), false);
            }
         }
      }
   }

   private final synchronized int nextTID() {
      if (++this._TID >= 16384) {
         this._TID = 3;
         this._TIDnew = true;
      }

      return this._TID;
   }

   private final void processInvokePDU(WTP_PDU wtpPDU) {
      if (wtpPDU != null) {
         byte[] wspData = wtpPDU.getUserData();
         if (wspData != null) {
            int type = wspData[0];
            if (type == 7 || type == 6) {
               PushEvent pushEvent = new PushEvent(wtpPDU);
               UserDefinedEvent event = new UserDefinedEvent((short)3, pushEvent);
               this.eventOccured(event);
            }
         }
      }
   }

   private final synchronized boolean getTIDnew() {
      if (this._TIDnew) {
         this._TIDnew = false;
         return true;
      } else {
         return false;
      }
   }
}
