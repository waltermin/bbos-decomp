package net.rim.device.apps.api.transmission;

import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.internal.io.TrafficLogger;
import net.rim.vm.Process;

public class DefaultSendThread extends BaseConnectionThread implements PacketSender {
   private TransmissionStatusListener _defaultListener;
   private IntHashtable _listenersByPacketTag = (IntHashtable)(new Object());
   private TransmissionService _service;
   private IntIntHashtable _dgId2ListenerTag = (IntIntHashtable)(new Object());
   private boolean _stopAsSoonAsPossible;
   private TrafficLogger _tLogger;
   private DatagramBase _currentDatagram;
   private boolean _closeConnectionAfterPacketsFlushed;

   public void setDefaultTransmissionStatusListener(TransmissionStatusListener aTransmissionStatusListener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void cancelTransmitPacket(int tagInt, Object contextObject) {
      DefaultSendThread$SendThreadCollector collector = DefaultSendThread$SendThreadCollector.getInstance();
      synchronized (collector) {
         for (int i = collector._packetQueue.size() - 1; i >= 0; i--) {
            DefaultSendThread$SendThreadDataContainer container = (DefaultSendThread$SendThreadDataContainer)collector._packetQueue.elementAt(i);
            if (container._profile == this && container._packet.getTag() == tagInt) {
               collector._packetQueue.removeElementAt(i);
            }
         }
      }

      DatagramBase datagramToCancel = null;
      synchronized (this) {
         if (this._currentDatagram != null && this._dgId2ListenerTag.get(this._currentDatagram.getDatagramId()) == tagInt) {
            datagramToCancel = this._currentDatagram;
         }
      }

      if (datagramToCancel != null && super._connection instanceof Object) {
         try {
            ((DatagramConnectionBase)super._connection).cancel(datagramToCancel);
         } finally {
            return;
         }
      }
   }

   public void stopAsSoonAsPossible() {
      this._stopAsSoonAsPossible = true;
   }

   public void closeConnection(boolean flushPackets) {
      if (flushPackets) {
         this._closeConnectionAfterPacketsFlushed = true;
      } else {
         try {
            super._connection.close();
         } finally {
            return;
         }
      }
   }

   @Override
   public void transmitPacket(Packet aPacket, Object contextObject) {
      if (this._tLogger != null) {
         Object obj = aPacket.getContextObject();
         if (obj instanceof Object) {
            ContextObject co = (ContextObject)obj;
            co.put(-6151522474633543992L, Process.currentProcess().getModuleName());
         }
      }

      if (this._closeConnectionAfterPacketsFlushed) {
         EventLogger.logEvent(super._eventLoggerGUID, 1399025763, 3);
      } else {
         DefaultSendThread$SendThreadCollector collector = DefaultSendThread$SendThreadCollector.getInstance();
         synchronized (collector) {
            DefaultSendThread$SendThreadDataContainer container = new DefaultSendThread$SendThreadDataContainer(aPacket, this);
            collector._packetQueue.addElement(container);
            collector.notify();
         }
      }
   }

   @Override
   public void setTrafficLogger(TrafficLogger tLogger) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public DefaultSendThread(TransmissionService aTransmissionService, long eventLoggerGUID) {
      super(eventLoggerGUID != 0 ? eventLoggerGUID : -5359313744971625388L);
      this._service = aTransmissionService;
      this._stopAsSoonAsPossible = false;
      this._closeConnectionAfterPacketsFlushed = false;
      if (eventLoggerGUID == 0) {
         try {
            EventLogger.register(-5359313744971625388L, "net.rim.transmission.SendThread", 2);
         } finally {
            return;
         }
      }
   }

   @Override
   public void updateDatagramStatus(int tagInt, int codeInt, Object contextObject) {
      TransmissionStatusListener listener = null;
      synchronized (this._listenersByPacketTag) {
         if ((listener = (TransmissionStatusListener)this._listenersByPacketTag.get(tagInt)) == null) {
            listener = this._defaultListener;
         }
      }

      if (listener != null && this._dgId2ListenerTag.containsKey(tagInt)) {
         listener.updateTransmissionStatus(this._service, this._dgId2ListenerTag.get(tagInt), codeInt, contextObject);
      }
   }

   @Override
   public void setDatagramConnection(DatagramConnection aDatagramConnection) {
      if (aDatagramConnection != null) {
         super._connection = aDatagramConnection;
         EventLogger.logEvent(super._eventLoggerGUID, 1129530708, 5);
         if (super._connection instanceof Object) {
            ((DatagramConnectionBase)super._connection).setDatagramStatusListener(this);
            return;
         }
      } else {
         EventLogger.logEvent(super._eventLoggerGUID, 1129206612, 3);
      }
   }
}
