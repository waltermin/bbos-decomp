package net.rim.device.apps.api.transmission.rim;

import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.selector.SRSelector;
import net.rim.device.api.servicebook.selector.SRSelectorCallback;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.util.AnonymousMessages;
import net.rim.device.apps.api.transmission.BaseTransmissionService;
import net.rim.device.apps.api.transmission.DefaultReceiveThread;
import net.rim.device.apps.api.transmission.DefaultSendThread;
import net.rim.device.apps.api.transmission.EmailServiceBookRegistry;
import net.rim.device.apps.api.transmission.Packet;
import net.rim.device.apps.api.transmission.PacketReceiver;
import net.rim.device.apps.api.transmission.TransmissionStatusListener;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.gme.GMEDatagram;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.io.TrafficLogger;

public class RIMMessagingService
   extends BaseTransmissionService
   implements GlobalEventListener,
   SRSelectorCallback,
   PacketReceiver,
   CMIMEConstants,
   RIMMessagingConstants {
   private DefaultSendThread _sender;
   private DefaultReceiveThread _receiver;
   private ServiceRecord _serviceRecord;
   private ContextObject _statusContext = new ContextObject();
   private static ResourceBundle _resources = ResourceBundle.getBundle(-6786735338084768790L, "net.rim.device.apps.internal.resource.RIMTransmission");

   public ServiceRecord[] getAllOutgoingServiceRecords() {
      ServiceRecord[] srs = ServiceBook.getSB().findRecordsByCid("CMIME");
      EmailServiceBookRegistry esbr = EmailServiceBookRegistry.getInstance();
      esbr.getEmailServiceRecords(srs);
      return srs;
   }

   public ServiceRecord getOutgoingServiceRecord() {
      int defaultId = SRSelector.getInstance().getDefault(-1015348247639805096L, "CMIME");
      if (defaultId != (this._serviceRecord == null ? -1 : this._serviceRecord.getId())) {
         this.defaultChanged(defaultId);
      }

      return this._serviceRecord;
   }

   @Override
   public void receivePacket(DataBuffer packetDataBuffer, Object context) {
      if (!(packetDataBuffer instanceof GMEDatagram)) {
         Firewall.getInstance().incrementBlockedCount((byte)-4);
      } else {
         GMEDatagram gmeDatagram = (GMEDatagram)packetDataBuffer;
         int command = -1;
         RIMMessagingTransmission transmission = null;
         String type = null;
         ContextObject contextObject = ContextObject.clone(context);
         if (packetDataBuffer.available() > 0) {
            command = packetDataBuffer.readUnsignedByte();
            switch (command) {
               case 1:
                  transmission = new RIMMessagingIncomingMessage();
                  type = "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE";
                  contextObject.clearFlag(94);
                  break;
               case 2:
                  transmission = new RIMMessagingIncomingMessage();
                  type = "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE";
                  contextObject.setFlag(94);
                  break;
               case 4:
                  transmission = new RIMMessagingIncomingMoreRequest();
                  type = "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE";
                  break;
               case 5:
                  transmission = new RIMMessagingMoreMessage();
                  type = "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MORE";
                  break;
               case 6:
                  transmission = new MoreMessageCompleteTransmission();
                  type = "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE";
                  break;
               case 7:
                  transmission = new RIMMessagingFolderManagement();
                  type = "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_FOLDER_MANAGEMENT";
                  break;
               case 8:
                  transmission = new CancelMoreMessagingTransmission();
                  type = "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE";
                  break;
               case 251:
                  transmission = new RIMMessagingDeliveryToAddress();
                  type = "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_DELIVERY";
                  break;
               case 254:
                  transmission = new RIMMessagingMessageError();
                  type = "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_ERROR";
            }

            if (command != 2 && gmeDatagram.isFromPeer() || command == 2 && !gmeDatagram.isFromPeer()) {
               Firewall.getInstance().incrementBlockedCount((byte)-4);
               EventLogger.logEvent(super._eventLoggerGUID, 1382249062, 3);
               return;
            }
         }

         if (transmission == null) {
            EventLogger.logEvent(super._eventLoggerGUID, 1383428212, 2);
         } else {
            ServiceRecord serviceRecord = (ServiceRecord)ContextObject.get(context, -6095803566992128485L);
            if (serviceRecord != null
               && serviceRecord.isSecureService()
               && !ContextObject.getFlag(context, 126)
               && (command == 1 && ITPolicy.getBoolean(24, 74, false) || command != 1)) {
               Firewall.getInstance().incrementBlockedCount((byte)-2);
               EventLogger.logEvent(super._eventLoggerGUID, 1382249062, 3);
               return;
            }

            transmission.read(packetDataBuffer);
            if (super._tLogger != null) {
               contextObject.put(-8214296050944071630L, new Integer(packetDataBuffer.getLength()));
               contextObject.put(1694473709785469504L, packetDataBuffer.getArray());
            }

            this.receiveObject(type, transmission, contextObject);
            if (super._tLogger != null) {
               contextObject.remove(-8214296050944071630L);
               contextObject.remove(1694473709785469504L);
               return;
            }
         }
      }
   }

   @Override
   public void defaultChanged(int recordIdInt) {
      if (this._serviceRecord == null || this._serviceRecord.getId() != recordIdInt) {
         this._serviceRecord = recordIdInt != -1 ? ServiceBook.getSB().getRecordById(recordIdInt) : null;
         this.forceStatusChanged();
      }
   }

   @Override
   public int chooseNewDefault(ServiceBook aServiceBook, String cidString, int oldDefaultIdInt, boolean userSetBoolean) {
      ServiceRecord defSr = oldDefaultIdInt != -1 ? aServiceBook.getRecordById(oldDefaultIdInt) : null;
      ServiceRecord encSr = null;
      ServiceRecord backupSr = null;
      if (defSr != null && defSr.getType() != 0) {
         defSr = null;
      }

      if (defSr == null || !userSetBoolean && (defSr.getEncryptionMode() & 2) == 0) {
         ServiceRecord[] records = aServiceBook.findRecordsByCid("CMIME");

         for (int i = 0; i < records.length; i++) {
            ServiceRecord sr = records[i];
            if (sr.getType() == 0) {
               if ((sr.getEncryptionMode() & 2) != 0) {
                  encSr = sr;
                  break;
               }

               if (backupSr == null) {
                  backupSr = sr;
               }
            }
         }

         int result = -1;
         if (encSr != null) {
            result = encSr.getId();
         } else if (defSr != null) {
            result = defSr.getId();
         } else if (backupSr != null) {
            result = backupSr.getId();
         }

         return result;
      } else {
         return defSr.getId();
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -8927980184023446756L) {
         AnonymousMessages.createAnonymousMessage(getResourceString(9), getResourceString(8), getResourceString(7));
      }
   }

   @Override
   public void setDefaultTransmissionStatusListener(TransmissionStatusListener aTransmissionStatusListener) {
      this._sender.setDefaultTransmissionStatusListener(aTransmissionStatusListener);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void transmitObject(String typeString, Object anObject, TransmissionStatusListener aTransmissionStatusListener, int tagInt, Object contextObject) {
      boolean var12 = false /* VF: Semaphore variable */;

      try {
         var12 = true;
         DataBuffer anIOException = ((RIMMessagingTransmission)anObject).write();
         if (anIOException.getLength() == 0) {
            EventLogger.logEvent(super._eventLoggerGUID, 1399747699, 2);
            return;
         }

         ContextObject packetContext = ContextObject.castOrCreate(contextObject);
         String address = null;
         DatagramAddressBase addressBase = (DatagramAddressBase)ContextObject.get(packetContext, -7981905408958106750L);
         if (addressBase != null) {
            address = (String)ContextObject.get(packetContext, -5971550291443523639L);
         }

         if (address == null) {
            ServiceRecord boundServiceRecord = (ServiceRecord)ContextObject.get(packetContext, -6095803566992128485L);
            if (boundServiceRecord == null) {
               if (this._serviceRecord != null) {
                  address = "CMIME/" + this._serviceRecord.getUid();
                  ContextObject.put(packetContext, -5971550291443523639L, address);
               } else {
                  EventLogger.logEvent(super._eventLoggerGUID, 1399742834, 2);
               }
            } else {
               address = "CMIME/" + boundServiceRecord.getUid();
               ContextObject.put(packetContext, -5971550291443523639L, address);
            }
         }

         this._sender.transmitPacket(new Packet(anIOException, aTransmissionStatusListener, tagInt, packetContext), this.getContext());
         var12 = false;
      } finally {
         if (var12) {
            EventLogger.logEvent(super._eventLoggerGUID, 1399418743, 2);
            return;
         }
      }
   }

   @Override
   public void cancelTransmitObject(int tagInt, Object contextObject) {
      this._sender.cancelTransmitPacket(tagInt, contextObject);
   }

   @Override
   protected void initializeService() {
      try {
         super._connection = (DatagramConnection)Connector.open("gme:CMIME");
         this._receiver = new DefaultReceiveThread(this, super._eventLoggerGUID);
         this._receiver.setDatagramConnection(super._connection);
         this._sender = new DefaultSendThread(this, super._eventLoggerGUID);
         this._sender.setDatagramConnection(super._connection);
         this._sender.setTrafficLogger(super._tLogger);
      } finally {
         EventLogger.logEvent(super._eventLoggerGUID, 1399743331, 2);
         return;
      }
   }

   private void forceStatusChanged() {
      this.statusChanged(this._serviceRecord != null ? 3 : 2, this._statusContext);
   }

   public RIMMessagingService() {
      super(8399767144006445082L, 3020044433160143544L);
      String name = getResourceString(1);
      this.defaultChanged(SRSelector.getInstance().register(name, -1015348247639805096L, "CMIME", this));
      ProtocolDaemon.getInstance().addGlobalEventListener(this);
   }

   public static String getResourceString(int id) {
      return _resources.getString(id);
   }

   @Override
   public int getStatus() {
      return this._serviceRecord != null ? 3 : 2;
   }

   @Override
   public void setTrafficLogger(TrafficLogger tLogger) {
      super.setTrafficLogger(tLogger);
      if (this._sender != null) {
         this._sender.setTrafficLogger(tLogger);
      }
   }
}
