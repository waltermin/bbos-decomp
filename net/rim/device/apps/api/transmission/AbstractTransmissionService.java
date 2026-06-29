package net.rim.device.apps.api.transmission;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.gme.GMEDatagram;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.io.TrafficLogger;

public class AbstractTransmissionService implements TransmissionService, GlobalEventListener, EventLoggerEvents {
   private long _factoryIdentifier;
   private Hashtable _listeners;
   protected ServiceIdentifier _serviceIdentifier;
   protected ServiceRecord _serviceRecord;
   protected int _srUidHash;
   protected int _srCidHash;
   private DatagramConnection _connection;
   private DatagramConnection _sendingConnection;
   private DatagramConnection _receivingConnection;
   private PacketSender _sender;
   private DatagramConnectionUser _receiver;
   protected TrafficLogger _tLogger;
   private long _eventLoggerGUID;
   private boolean _useServiceManager = false;
   public static long OBJECT_BEING_PACKETIZED = -7420164137893270147L;

   protected ServiceRecord initServiceRecord() {
      throw null;
   }

   public int getTransmissionStatusForObject(Object o) {
      return -1;
   }

   protected DatagramConnection createConnection(ServiceRecord _1) {
      throw null;
   }

   public DatagramConnection getConnection() {
      return this._connection;
   }

   public DatagramConnection getSendingConnection() {
      return this._sendingConnection != null ? this._sendingConnection : this._connection;
   }

   public DatagramConnection getReceivingConnection() {
      return this._receivingConnection != null ? this._receivingConnection : this._connection;
   }

   public void setSendingConnection(DatagramConnection sendingConnection) {
      this._sendingConnection = sendingConnection;
      this._sender.setDatagramConnection(this._sendingConnection);
   }

   protected void closeSendingConnection(boolean flushPackets) {
      ((DefaultSendThread)this._sender).closeConnection(flushPackets);
   }

   public void setRecevingConnection(DatagramConnection receivingConnection) {
      this._receivingConnection = receivingConnection;
      this._receiver.setDatagramConnection(this._receivingConnection);
   }

   protected DatagramConnection createSendingConnection(ServiceRecord serviceRecord) {
      this._sendingConnection = null;
      return null;
   }

   protected DatagramConnection createReceivingConnection(ServiceRecord serviceRecord) {
      this._receivingConnection = null;
      return null;
   }

   protected PacketSender createSenderThread(DatagramConnection connection) {
      DefaultSendThread thread = new DefaultSendThread(this, this._eventLoggerGUID);
      thread.setDatagramConnection(connection);
      this._sendingConnection = connection;
      return thread;
   }

   protected DatagramConnectionUser createReceiverThread(DatagramConnection connection) {
      DefaultReceiveThread thread = new DefaultReceiveThread((PacketReceiver)this, this._eventLoggerGUID);
      thread.setDatagramConnection(connection);
      this._receivingConnection = connection;
      return thread;
   }

   public GMEDatagram verifyGMEDatagram(DataBuffer packetDataBuffer, boolean verifySecure) {
      GMEDatagram gmeDatagram = null;
      if (!(packetDataBuffer instanceof GMEDatagram)) {
         Firewall.getInstance().incrementBlockedCount((byte)-4);
         return null;
      }

      gmeDatagram = (GMEDatagram)packetDataBuffer;
      if (!gmeDatagram.isFromPeer() && (!verifySecure || gmeDatagram.wasDatagramSecure())) {
         return gmeDatagram;
      }

      Firewall.getInstance().incrementBlockedCount((byte)-4);
      return null;
   }

   public GMEDatagram verifyGMEDatagram(DataBuffer packetDataBuffer) {
      return this.verifyGMEDatagram(packetDataBuffer, true);
   }

   public ServiceIdentifier getServiceIdentifier() {
      return this._serviceIdentifier;
   }

   public ServiceRecord getIncomingServiceRecord() {
      return this._serviceRecord;
   }

   public ServiceRecord getOutgoingServiceRecord() {
      return this._serviceRecord;
   }

   public void processChangedService(long guid, int data0, int data1, Object object0, Object object1) {
      ServiceRecord targetSR = ServiceBook.getSB().getRecordById(data0);
      if (targetSR == null) {
         if (guid == -7853136852381124900L && object0 instanceof ServiceRecord) {
            this.serviceRecordChanged(targetSR, (ServiceRecord)object0);
         }
      } else {
         try {
            ServiceRecord oldServiceRecord = this._serviceRecord;
            if (object1 instanceof ServiceRecord) {
               oldServiceRecord = (ServiceRecord)object1;
            }

            if (this._serviceRecord == null) {
               ServiceRecord usingSR = this.initServiceRecord();
               if (usingSR != null && usingSR == targetSR) {
                  this._serviceRecord = targetSR;
               }
            }

            if (this._serviceRecord == null) {
               this._srUidHash = 0;
               this._srCidHash = 0;
               return;
            }

            if (this._serviceRecord.getUidHash() != this._srUidHash || this._serviceRecord.getCidHash() != this._srCidHash) {
               DatagramConnection oldReceivingConnection = this._receivingConnection;
               DatagramConnection oldSendingConnection = this._sendingConnection;
               this._sendingConnection = this.createSendingConnection(this._serviceRecord);
               this._receivingConnection = this.createReceivingConnection(this._serviceRecord);
               if (this._sendingConnection == null || this._receivingConnection == null) {
                  this._connection = this.createConnection(this._serviceRecord);
               }

               if (this._sendingConnection == null) {
                  this._sendingConnection = this._connection;
               }

               if (this._receivingConnection == null) {
                  this._receivingConnection = this._connection;
               }

               this._receiver.setDatagramConnection(this._receivingConnection);
               this._sender.setDatagramConnection(this._sendingConnection);
               if (oldReceivingConnection != null) {
                  oldReceivingConnection.close();
               }

               if (oldSendingConnection != null) {
                  oldSendingConnection.close();
               }

               this._srUidHash = this._serviceRecord.getUidHash();
               this._srCidHash = this._serviceRecord.getCidHash();
            }

            this.serviceRecordChanged(targetSR, oldServiceRecord);
         } finally {
            return;
         }
      }
   }

   protected void serviceRecordChanged(ServiceRecord targetSR, ServiceRecord oldServiceRecord) {
   }

   protected void transmitPacket(Packet packet, Object contextUsedToFindConverterAndToSend) {
      this._sender.transmitPacket(packet, contextUsedToFindConverterAndToSend);
   }

   protected boolean receiveObject(String typeString, Object transmissionObject, Object contextObject) {
      TransmissionServiceListenerManager manager = (TransmissionServiceListenerManager)this._listeners.get(typeString);
      return manager != null ? manager.fireReceiveObject(this, transmissionObject, contextObject) : false;
   }

   @Override
   public void removeTransmissionServiceListener(String typeString, TransmissionServiceListener aTransmissionServiceListener) {
      TransmissionServiceListenerManager manager = (TransmissionServiceListenerManager)this._listeners.get(typeString);
      if (manager != null) {
         synchronized (manager) {
            manager.removeTransmissionServiceListener(aTransmissionServiceListener);
            if (manager.isEmpty()) {
               this._listeners.remove(typeString);
            }
         }
      }
   }

   @Override
   public void transmitObject(String typeString, Object anObject, TransmissionStatusListener aTransmissionStatusListener, int tagInt, Object contextObject) {
      Object contextUsedToFindConverterAndToSend = this.getContext();
      Object contextUsedToConvert = contextObject;
      Converter converter = SerializationManager.getConverter(typeString, contextUsedToFindConverterAndToSend);
      if (converter != null) {
         byte[] bytes = converter.convert(anObject, contextUsedToConvert);
         if (bytes == null) {
            EventLogger.logEvent(this._eventLoggerGUID, 1399747699, 2);
            return;
         }

         ContextObject co = null;
         if (contextUsedToFindConverterAndToSend instanceof ContextObject) {
            co = (ContextObject)contextUsedToFindConverterAndToSend;
            co.put(OBJECT_BEING_PACKETIZED, anObject);
         }

         Packet packet = new Packet(bytes, aTransmissionStatusListener, tagInt, contextUsedToConvert);
         this.transmitPacket(packet, contextUsedToFindConverterAndToSend);
         if (co != null) {
            co.remove(OBJECT_BEING_PACKETIZED);
            return;
         }
      } else {
         EventLogger.logEvent(this._eventLoggerGUID, 1399025254, 2);
      }
   }

   @Override
   public void cancelTransmitObject(int tagInt, Object contextObject) {
   }

   @Override
   public void addTransmissionServiceListener(String typeString, int priorityInt, TransmissionServiceListener aTransmissionServiceListener) {
      if (aTransmissionServiceListener == null) {
         this.removeTransmissionServiceListener(typeString, aTransmissionServiceListener);
      } else {
         TransmissionServiceListenerManager manager;
         synchronized (this._listeners) {
            manager = (TransmissionServiceListenerManager)this._listeners.get(typeString);
            if (manager == null) {
               manager = new TransmissionServiceListenerManager();
               manager.setTrafficLogger(this._tLogger);
               this._listeners.put(typeString, manager);
            }
         }

         synchronized (manager) {
            manager.addTransmissionServiceListener(aTransmissionServiceListener, priorityInt);
         }
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      long eventType = -1;
      if (!this._useServiceManager) {
         if (guid == -4220058463650496006L) {
            eventType = -860845403685493259L;
         } else if (guid == 8288627527798139133L) {
            eventType = 8478935834746748823L;
         } else if (guid == 2522898683889177438L) {
            eventType = -7853136852381124900L;
         }
      } else if (guid == -860845403685493259L || guid == 8478935834746748823L || guid == -7853136852381124900L) {
         eventType = guid;
      }

      if (eventType != -1) {
         this.processChangedService(eventType, data0, data1, object0, object1);
      }
   }

   @Override
   public void setDefaultTransmissionStatusListener(TransmissionStatusListener tsl) {
      ((DefaultSendThread)this._sender).setDefaultTransmissionStatusListener(tsl);
   }

   @Override
   public void setTrafficLogger(TrafficLogger tLogger) {
      this._tLogger = tLogger;
      if (this._sender != null) {
         this._sender.setTrafficLogger(tLogger);
      }

      Enumeration managers = this._listeners.elements();

      while (managers.hasMoreElements()) {
         ((TransmissionServiceListenerManager)managers.nextElement()).setTrafficLogger(tLogger);
      }
   }

   @Override
   public long getFactoryIdentifier() {
      return this._factoryIdentifier;
   }

   @Override
   public Object getContext() {
      return this;
   }

   @Override
   public int getStatus() {
      throw null;
   }

   protected AbstractTransmissionService(long factoryIdentifier, long eventLoggerGUID) {
      this(factoryIdentifier, eventLoggerGUID, true, false);
   }

   protected AbstractTransmissionService(long factoryIdentifier, long eventLoggerGUID, boolean requireSR, boolean useServiceManager) {
      this(factoryIdentifier, eventLoggerGUID, requireSR, useServiceManager, null);
   }

   protected AbstractTransmissionService(long factoryIdentifier, long eventLoggerGUID, boolean requireSR) {
      this(factoryIdentifier, eventLoggerGUID, requireSR, false);
   }

   protected AbstractTransmissionService(
      long factoryIdentifier, long eventLoggerGUID, boolean requireSR, boolean useServiceManager, ServiceIdentifier serviceIdentifier
   ) {
      this._factoryIdentifier = factoryIdentifier;
      this._listeners = new Hashtable();
      this._eventLoggerGUID = eventLoggerGUID;
      this._srUidHash = 0;
      this._srCidHash = 0;
      this._serviceIdentifier = serviceIdentifier;
      this._serviceRecord = null;
      this._useServiceManager = useServiceManager;

      try {
         if (requireSR) {
            this._serviceRecord = this.initServiceRecord();
            if (this._serviceRecord != null) {
               this._srUidHash = this._serviceRecord.getUidHash();
               this._srCidHash = this._serviceRecord.getCidHash();
            }

            ProtocolDaemon.getInstance().addGlobalEventListener(this);
         }

         if (this._serviceRecord != null || !requireSR) {
            this._sendingConnection = this.createSendingConnection(this._serviceRecord);
            this._receivingConnection = this.createReceivingConnection(this._serviceRecord);
            if (this._sendingConnection == null || this._receivingConnection == null) {
               this._connection = this.createConnection(this._serviceRecord);
            }
         }

         if (this._sendingConnection == null) {
            this._sendingConnection = this._connection;
         }

         if (this._receivingConnection == null) {
            this._receivingConnection = this._connection;
         }

         this._receiver = this.createReceiverThread(this._receivingConnection);
         this._sender = (PacketSender)this.createSenderThread(this._sendingConnection);
      } finally {
         return;
      }
   }
}
