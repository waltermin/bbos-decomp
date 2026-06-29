package net.rim.device.apps.api.transmission;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.internal.io.TrafficLogger;

public class BaseTransmissionService implements TransmissionService, EventLoggerEvents {
   protected long _eventLoggerGUID;
   private long _factoryIdentifier;
   private Hashtable _listenerManagers;
   protected DatagramConnection _connection;
   protected TrafficLogger _tLogger;

   protected void initializeService() {
   }

   protected void sendObject(Packet aPacket, Object contextObject) {
   }

   protected void statusChanged(int statusInt, Object contextObject) {
      Enumeration managers = this._listenerManagers.elements();

      while (managers.hasMoreElements()) {
         ((TransmissionServiceListenerManager)managers.nextElement()).fireStatusChanged(this, statusInt, contextObject);
      }
   }

   protected boolean receiveObject(String typeString, Object transmissionObject, Object contextObject) {
      TransmissionServiceListenerManager manager = (TransmissionServiceListenerManager)this._listenerManagers.get(typeString);
      return manager != null ? manager.fireReceiveObject(this, transmissionObject, contextObject) : false;
   }

   @Override
   public void transmitObject(String typeString, Object anObject, TransmissionStatusListener aTransmissionStatusListener, int tagInt, Object contextObject) {
      Object contextUsedToFindConverterAndToSend = this.getContext();
      Object contextUsedToConvert = contextObject;
      Converter converter = SerializationManager.getConverter(typeString, contextUsedToFindConverterAndToSend);
      if (converter != null) {
         byte[] bytes = converter.convert(anObject, contextUsedToConvert);
         if (bytes != null) {
            Packet packet = new Packet(bytes, aTransmissionStatusListener, tagInt, contextUsedToConvert);
            this.sendObject(packet, contextUsedToFindConverterAndToSend);
         } else {
            EventLogger.logEvent(this._eventLoggerGUID, 1399747699, 2);
         }
      } else {
         EventLogger.logEvent(this._eventLoggerGUID, 1399025254, 2);
      }
   }

   @Override
   public void cancelTransmitObject(int tagInt, Object contextObject) {
   }

   @Override
   public void setDefaultTransmissionStatusListener(TransmissionStatusListener aTransmissionStatusListener) {
   }

   @Override
   public void removeTransmissionServiceListener(String typeString, TransmissionServiceListener aTransmissionServiceListener) {
      TransmissionServiceListenerManager manager = (TransmissionServiceListenerManager)this._listenerManagers.get(typeString);
      if (manager != null) {
         synchronized (manager) {
            manager.removeTransmissionServiceListener(aTransmissionServiceListener);
            if (manager.isEmpty()) {
               this._listenerManagers.remove(typeString);
            }
         }
      }
   }

   @Override
   public void addTransmissionServiceListener(String typeString, int priorityInt, TransmissionServiceListener aTransmissionServiceListener) {
      if (aTransmissionServiceListener == null) {
         this.removeTransmissionServiceListener(typeString, aTransmissionServiceListener);
      } else {
         TransmissionServiceListenerManager manager;
         synchronized (this._listenerManagers) {
            manager = (TransmissionServiceListenerManager)this._listenerManagers.get(typeString);
            if (manager == null) {
               manager = new TransmissionServiceListenerManager();
               this._listenerManagers.put(typeString, manager);
               manager.setTrafficLogger(this._tLogger);
            }
         }

         synchronized (manager) {
            manager.addTransmissionServiceListener(aTransmissionServiceListener, priorityInt);
         }
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
   public void setTrafficLogger(TrafficLogger tLogger) {
      this._tLogger = tLogger;
      Enumeration managers = this._listenerManagers.elements();

      while (managers.hasMoreElements()) {
         ((TransmissionServiceListenerManager)managers.nextElement()).setTrafficLogger(tLogger);
      }
   }

   @Override
   public int getStatus() {
      throw null;
   }

   protected BaseTransmissionService(long factoryIdentifier, long eventLoggerGUID) {
      this._factoryIdentifier = factoryIdentifier;
      this._eventLoggerGUID = eventLoggerGUID;
      this._listenerManagers = (Hashtable)(new Object());
      this.initializeService();
   }
}
