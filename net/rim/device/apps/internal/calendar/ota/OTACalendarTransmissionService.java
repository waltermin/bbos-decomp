package net.rim.device.apps.internal.calendar.ota;

import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceBook$ServiceStatus;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.calendar.ota.OTACalendarConstants;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.service.ServiceObject;
import net.rim.device.apps.api.transmission.AbstractTransmissionService;
import net.rim.device.apps.api.transmission.Packet;
import net.rim.device.apps.api.transmission.PacketReceiver;
import net.rim.device.apps.api.transmission.TransmissionStatusListener;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.SerializationManager;

public class OTACalendarTransmissionService extends AbstractTransmissionService implements PacketReceiver, OTACalendarConstants {
   private byte[] _oldAppData = null;
   private CICALTransmissionManager _transmissionManager;
   private ContextObject _context;
   private IntIntHashtable _slowSyncTrackingTable;
   private static final long ID;
   private static final long SLOW_SYNC_TRACKER_OBJECT;
   static final long CAL_TRASMISSION_PRIVATE_FLAGS;
   static final int DO_NOT_TRACK_PACKET;

   public void initalizeOTACalendar() {
      this.checkForRequiredSlowSyncing();
   }

   public synchronized boolean hasSlowSyncOccurredForService(CalendarService calendarService) {
      ServiceRecord serviceRecord = null;
      Object o = calendarService.getServiceKey();
      if (o instanceof Object) {
         serviceRecord = (ServiceRecord)o;
         CICALConfiguration configuration = calendarService.getCICALConfiguration();
         if (configuration != null) {
            int hashCode = serviceRecord.getKeyHashForService();
            return this._slowSyncTrackingTable.containsKey(hashCode);
         }
      }

      return false;
   }

   void markSlowSyncComplete(CalendarService calendarService) {
      ServiceRecord serviceRecord = calendarService.getServiceRecord();
      if (serviceRecord != null) {
         int hashCode = serviceRecord.getKeyHashForService();
         int currentTime = (int)(System.currentTimeMillis() / 1000);
         this._slowSyncTrackingTable.clear();
         this._slowSyncTrackingTable.put(hashCode, currentTime);
         PersistentObject.commit(this._slowSyncTrackingTable);
      }
   }

   public int getStatusCodeForEvent(Event e) {
      return this._transmissionManager.getStatusCodeForEvent(e);
   }

   public synchronized int[] getLastTimeSlowSyncCompleted() {
      int[] result = new int[this._slowSyncTrackingTable.size()];
      IntEnumeration enumeration = this._slowSyncTrackingTable.elements();
      int count = 0;

      while (enumeration.hasMoreElements()) {
         int timestamp = enumeration.nextElement();
         result[count++] = timestamp;
      }

      return result;
   }

   void retransmitPacket(Packet packet, Object context) {
      CICALEventLogger.logEvent(1380275024, 4, packet.getPayload(), packet.getTag() & 4294967295L);
      super.transmitPacket(packet, context);
   }

   public synchronized void transmitObject(String typeString, Object anObject, Object contextObject) {
      CICALConfiguration cicalConfiguration = null;
      CalendarService calendarService = (CalendarService)ContextObject.get(contextObject, 6741741218837016896L);
      if (calendarService != null) {
         cicalConfiguration = calendarService.getCICALConfiguration();
      }

      if (anObject instanceof Object && calendarService == null) {
         ServiceObject serviceObject = (ServiceObject)anObject;
         calendarService = (CalendarService)serviceObject.getServiceIdentifier();
      }

      if (calendarService != null) {
         cicalConfiguration = calendarService.getCICALConfiguration();
      }

      if (cicalConfiguration != null
         && (cicalConfiguration.isSendSyncEnabled() || anObject instanceof CICALConfigConverter$OTAConfigEvent && cicalConfiguration.isOTAConfigSupported())) {
         boolean slowSyncUpdate = false;
         if (contextObject instanceof Object) {
            ContextObject co = (ContextObject)contextObject;
            if (co.getPrivateFlag(-1677359872409272575L, 1)) {
               slowSyncUpdate = true;
               this._context.setPrivateFlag(-1677359872409272575L, 1);
            }
         }

         if (!slowSyncUpdate) {
            calendarService.getCalendarDatabase().updateOutgoingStatistics(1);
         }

         this.transmitObject(typeString, anObject, null, 0, contextObject);
         this._context.clearPrivateFlag(-1677359872409272575L, 1);
      }
   }

   public void logIncommingMeetingPacket(Object sender, String extraInfo, byte[] packet) {
      if (super._tLogger != null && packet != null) {
         super._tLogger.bytesReceived(sender, 1, extraInfo, packet.length, packet);
      }
   }

   public void logOutgoingMeetingPacket(Object sender, String extraInfo, byte[] packet) {
      if (super._tLogger != null && packet != null) {
         super._tLogger.bytesTransmitted(sender, 1, extraInfo, packet.length, packet);
      }
   }

   public void setTransmissionManager(CICALTransmissionManager transmissionManager) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public CICALTransmissionManager getTransmissionManager() {
      return this._transmissionManager;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void receivePacket(DataBuffer packetDataBuffer, Object contextObject) {
      if (this.verifyGMEDatagram(packetDataBuffer, false) == null) {
         CICALEventLogger.logEvent(1196246342, 0);
      } else if (!CICALConfiguration.isOTACalendarAllowed()) {
         CICALEventLogger.logEvent(1230262348, 3);
      } else {
         ServiceRecord boundServiceRecord = null;
         ServiceRecord var23 = ContextObject.get(contextObject, -6095803566992128485L);
         CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarServiceByKey(var23);
         CICALConfiguration cicalConfiguration = calendarService.getCICALConfiguration();
         byte[] packet = new byte[packetDataBuffer.getLength() - packetDataBuffer.getPosition()];
         boolean var20 = false /* VF: Semaphore variable */;

         try {
            var20 = true;
            packetDataBuffer.readFully(packet);
            var20 = false;
         } finally {
            if (var20) {
               CICALEventLogger.logEvent(1129727314, 2, packet);
               return;
            }
         }

         CICALEventLogger.logEvent(1380144720, 5, packet);
         int command = packet != null && packet.length > 0 ? packet[0] & 0xFF : -1;
         if (!cicalConfiguration.isReceiveSyncEnabled() && command != 27 && command != 26 && command != 28) {
            CICALEventLogger.logEvent(1229409872, 4, packet);
         } else {
            String type = null;
            switch (command) {
               case 22:
               case 24:
               case 30:
                  CICALEventLogger.logEvent(1431194435, 2, packet);
                  return;
               case 23:
               default:
                  type = "net.rim.RIMCalendarApptUpdate";
                  break;
               case 25:
                  type = "net.rim.RIMCalendarApptDelete";
                  break;
               case 26:
               case 27:
               case 28:
                  type = "net.rim.RIMCalendarConfig";
                  break;
               case 29:
               case 31:
               case 32:
               case 33:
                  type = "net.rim.RIMCalendarSlowSync";
            }

            Object convertContext = this.getContext();
            if (convertContext instanceof Object) {
               if (var23 != null) {
                  ContextObject.put(convertContext, -6095803566992128485L, var23);
               }

               ContextObject.put(convertContext, 6741741218837016896L, calendarService);
            }

            Converter converter = SerializationManager.getConverter(type, convertContext);
            if (converter == null) {
               CICALEventLogger.logEvent(1313817430, 2, packet);
            } else {
               boolean var17 = false /* VF: Semaphore variable */;

               label163:
               try {
                  var17 = true;
                  Object e = converter.convert(packet, convertContext);
                  if (super._tLogger != null) {
                     ContextObject.put(convertContext, -8214296050944071630L, new Object(packet.length));
                     ContextObject.put(convertContext, 1694473709785469504L, packet);
                  }

                  if (e instanceof Object[]) {
                     Object[] objects = (Object[])e;
                     int count = objects.length;

                     for (int i = 0; i < count; i++) {
                        this.receiveObject(type, objects[i], convertContext);
                     }
                  } else if (e != null) {
                     this.receiveObject(type, e, convertContext);
                  }

                  if (super._tLogger != null) {
                     ContextObject.remove(convertContext, -8214296050944071630L);
                     ContextObject.remove(convertContext, 1694473709785469504L);
                     var17 = false;
                  } else {
                     var17 = false;
                  }
               } finally {
                  if (var17) {
                     CICALEventLogger.logEvent(1129727314, 2, packet);
                     break label163;
                  }
               }
            }

            ContextObject.remove(convertContext, -6095803566992128485L);
         }
      }
   }

   @Override
   protected ServiceRecord initServiceRecord() {
      ServiceRecord result = null;
      if (super._serviceIdentifier != null && super._serviceIdentifier instanceof Object) {
         CalendarService calendarService = (CalendarService)super._serviceIdentifier;
         Object key = calendarService.getServiceKey();
         if (key instanceof Object) {
            result = (ServiceRecord)key;
         }
      }

      if (result == null && this._slowSyncTrackingTable != null) {
         this._slowSyncTrackingTable.clear();
         PersistentObject.commit(this._slowSyncTrackingTable);
      }

      return result;
   }

   @Override
   protected DatagramConnection createConnection(ServiceRecord serviceRecord) {
      if (serviceRecord == null) {
         throw new Object("Null Service Record");
      } else {
         return (DatagramConnection)Connector.open(((StringBuffer)(new Object("gme:CICAL/"))).append(serviceRecord.getUid()).toString());
      }
   }

   @Override
   protected void serviceRecordChanged(ServiceRecord targetSR, ServiceRecord oldServiceRecord) {
      boolean wasCICALChanged = false;
      ServiceRecord serviceRecord = targetSR != null ? targetSR : oldServiceRecord;
      if (serviceRecord != null) {
         String cid = serviceRecord.getCid();
         if (StringUtilities.strEqualIgnoreCase(cid, "CICAL")) {
            wasCICALChanged = true;
         }
      }

      if (wasCICALChanged) {
         CICALEventLogger.logEvent(1396853066, 0);
         super._serviceRecord = this.initServiceRecord();
         if (super._serviceRecord != null) {
            this.compareServiceRecords(oldServiceRecord, targetSR);
            return;
         }

         CICALEventLogger.logEvent(1313821522, 0);
      }
   }

   private void startCalendarSlowSync(CalendarService calendarService) {
      CICALSlowSyncConverter slowSyncConverter = CICALSlowSyncConverter.getInstance();
      if (slowSyncConverter != null) {
         slowSyncConverter.startCalendarSlowSync(-9027560642818204641L, calendarService);
      }
   }

   @Override
   public void transmitObject(String typeString, Object anObject, TransmissionStatusListener aTransmissionStatusListener, int tagInt, Object contextObject) {
      if (!CICALConfiguration.isOTACalendarAllowed()) {
         CICALEventLogger.logEvent(1230262348, 3);
      } else {
         Object ticket = PersistentContent.getTicket();
         if (ticket == null && anObject instanceof Object) {
            try {
               throw new Object();
            } finally {
               ;
            }
         } else {
            if (tagInt == 0 && this._transmissionManager != null) {
               tagInt = this._transmissionManager.allocateTagId();
            }

            super.transmitObject(typeString, anObject, aTransmissionStatusListener, tagInt, contextObject);
            ticket = null;
         }
      }
   }

   @Override
   public int getTransmissionStatusForObject(Object o) {
      if (!(o instanceof Object)) {
         return super.getTransmissionStatusForObject(o);
      }

      Event e = (Event)o;
      return this._transmissionManager.getStatusCodeForEvent(e);
   }

   public OTACalendarTransmissionService(long factoryIdentifier, CalendarService calendarService) {
      super(factoryIdentifier, -256469206327664059L, true, true, calendarService);
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-2174959204821111592L);
      this._slowSyncTrackingTable = (IntIntHashtable)persistentObject.getContents();
      if (this._slowSyncTrackingTable == null) {
         this._slowSyncTrackingTable = (IntIntHashtable)(new Object(1));
         persistentObject.setContents(this._slowSyncTrackingTable, 51);
         persistentObject.commit();
      }

      this._context = (ContextObject)(new Object());
   }

   @Override
   public Object getContext() {
      return this._context;
   }

   @Override
   public int getStatus() {
      return super._serviceRecord != null ? 3 : 2;
   }

   private void checkForRequiredSlowSyncing() {
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("CICAL");

      for (int i = 0; i < records.length; i++) {
         ServiceRecord sr = records[i];
         this.checkForRequiredSlowSyncing(sr);
      }
   }

   private void checkForRequiredSlowSyncing(ServiceRecord service) {
      CICALSlowSyncConverter slowSyncConverter = CICALSlowSyncConverter.getInstance();
      if (slowSyncConverter != null && service != null) {
         CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarServiceByKey(service);
         if (!this.hasSlowSyncOccurredForService(calendarService)) {
            this.startCalendarSlowSync(calendarService);
         } else {
            CICALEventLogger.logEvent(1095517273, 0);
         }
      } else {
         if (slowSyncConverter == null) {
            CICALEventLogger.logEvent(1396919881, 2);
         }
      }
   }

   private void compareServiceRecords(ServiceRecord oldServiceRecord, ServiceRecord newServiceRecord) {
      if (super._serviceRecord != null) {
         boolean changed = false;
         if (oldServiceRecord != null && newServiceRecord != null) {
            String oldDataSourceId = oldServiceRecord.getDataSourceId();
            String newDataSourceId = newServiceRecord.getDataSourceId();
            if (oldDataSourceId != null && newDataSourceId != null) {
               changed = changed || StringUtilities.compareToIgnoreCase(oldDataSourceId, newDataSourceId) != 0;
               changed = changed || oldServiceRecord.getUserId() != newServiceRecord.getUserId();
            }
         } else if (oldServiceRecord == null) {
            changed = true;
         }

         byte[] oldAppData = this._oldAppData;
         if (this._oldAppData == null && oldServiceRecord != null) {
            oldAppData = oldServiceRecord.getApplicationData();
         }

         byte[] newAppData = newServiceRecord != null ? newServiceRecord.getApplicationData() : null;
         if ((oldAppData == null || newAppData == null) && oldAppData != newAppData) {
            changed = true;
         } else {
            changed = changed || oldServiceRecord.isRestoredFromBackup() || !Arrays.equals(oldAppData, newAppData);
         }

         if (newServiceRecord != null) {
            ServiceBook$ServiceStatus serviceStatus = ServiceBook.getSB().getStatusForUid(newServiceRecord.getUid());
            if (serviceStatus != null) {
               int currentPIN = DeviceInfo.getDeviceId();
               int lastPIN = serviceStatus.getLastPIN();
               if (currentPIN != lastPIN) {
                  changed = true;
                  CICALEventLogger.logEvent(1346981419, 0);
               }
            }
         }

         if (changed && newServiceRecord != null && !newServiceRecord.isRestoredFromBackup()) {
            int hashCode = newServiceRecord.getKeyHashForService();
            if (this._slowSyncTrackingTable.containsKey(hashCode)) {
               this._slowSyncTrackingTable.remove(hashCode);
            }

            CICALEventLogger.logEvent(1396851528, 0);
            CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarServiceByKey(newServiceRecord);
            this.startCalendarSlowSync(calendarService);
         } else {
            this.checkForRequiredSlowSyncing(newServiceRecord);
         }

         this._oldAppData = Arrays.copy(newAppData);
      }
   }

   @Override
   protected void transmitPacket(Packet packet, Object contextUsedToFindConverterAndSend) {
      boolean trackPacket = !this._context.getPrivateFlag(-1677359872409272575L, 1);
      if (contextUsedToFindConverterAndSend instanceof Object) {
         ContextObject co = (ContextObject)contextUsedToFindConverterAndSend;
         if (co.getPrivateFlag(-1677359872409272575L, 1)) {
            trackPacket = false;
         }
      }

      if (this._transmissionManager != null && trackPacket) {
         this._transmissionManager
            .setDataForPacket(packet.getTag(), packet.getPayload(), packet.getPayloadOffset(), packet.getPayloadLength(), contextUsedToFindConverterAndSend);
      }

      this._context.clearPrivateFlag(-1677359872409272575L, 1);
      CICALEventLogger.logEvent(1397638224, 4, packet.getPayload(), packet.getTag() & 4294967295L);
      super.transmitPacket(packet, contextUsedToFindConverterAndSend);
   }
}
