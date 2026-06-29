package net.rim.device.apps.internal.calendar.ota;

import java.util.Enumeration;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.calendar.ota.TrackingData;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.AbstractTransmissionService;
import net.rim.device.apps.api.transmission.Packet;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionStatusListener;
import net.rim.vm.Array;

public class CICALTransmissionManager extends Thread implements TransmissionStatusListener, PersistentContentListener {
   private OTACalendarTransmissionService _transmissionService;
   private IntHashtable _trackingStore;
   private boolean _connectionStateGood;
   private int[] _tagIds;
   private static final long STORE_ID = -6581519639896522540L;
   private static final long MAX_ERROR_RETRIES = 3L;
   private static final long GOOD_SEND_REMOVE_TIME = 172800000L;
   private static final long ANY_ELEMENT_REMOVE_TIME = 2592000000L;
   private static final long UNSENT_REQUIRED_ELAPSED_TIME = 240000L;
   private static final long ERROR_REQUIRED_ELAPSED_TIME = 1200000L;
   private static final long RESEND_FROM_QUEUED_STATE_ELAPSED_TIME = 43200000L;

   int allocateTagId() {
      return UIDGenerator.getUID();
   }

   void setDataForPacket(int tagId, byte[] data, int offset, int length, Object context) {
      Event e = null;
      CICALSlowSyncEvent slowSyncEvent = null;
      if (context instanceof Object) {
         ContextObject co = (ContextObject)context;
         Object o = co.get(AbstractTransmissionService.OBJECT_BEING_PACKETIZED);
         if (!(o instanceof Object)) {
            if (o instanceof CICALSlowSyncEvent) {
               slowSyncEvent = (CICALSlowSyncEvent)o;
            }
         } else {
            e = (Event)o;
         }
      }

      byte[] saveMe = data;
      if (data != null && (offset != 0 || data.length != length)) {
         saveMe = new byte[length];
         System.arraycopy(data, offset, saveMe, 0, length);
      }

      TrackingData trackingData = (TrackingData)(new Object(saveMe));
      if (e != null) {
         trackingData.setUID(e.getUID());
         trackingData.setPacketType(1);
      } else if (slowSyncEvent != null) {
         trackingData.setPacketType(5);
         trackingData.setUID(slowSyncEvent.getSessionID());
      }

      if (!trackingData.checkCrypt(true, true)) {
         trackingData.reCrypt(true, true);
      }

      synchronized (this._trackingStore) {
         this._trackingStore.put(tagId, trackingData);
         PersistentObject.commit(this._trackingStore);
      }
   }

   public int getStatusCodeForEvent(Event e) {
      int result = -1;
      synchronized (this._trackingStore) {
         long lastPacket = Long.MIN_VALUE;
         Enumeration trackingElements = this._trackingStore.elements();

         while (trackingElements.hasMoreElements()) {
            TrackingData data = (TrackingData)trackingElements.nextElement();
            if (data != null && data._uid == e.getUID() && data._lastUpdate > lastPacket) {
               result = data._statusCode;
            }
         }

         return result;
      }
   }

   public TrackingData[] getAllTrackingDataForEvent(int uid) {
      TrackingData[] result = null;
      synchronized (this._trackingStore) {
         Enumeration trackingElements = this._trackingStore.elements();

         while (trackingElements.hasMoreElements()) {
            TrackingData data = (TrackingData)trackingElements.nextElement();
            if (data != null && data._uid == uid) {
               if (result == null) {
                  result = new Object[1];
               } else {
                  Array.resize(result, result.length + 1);
               }

               result[result.length - 1] = data;
            }
         }

         return result;
      }
   }

   public TrackingData[] getAllTrackingEvents() {
      synchronized (this._trackingStore) {
         TrackingData[] result = new Object[this._trackingStore.size()];
         Enumeration trackingElements = this._trackingStore.elements();
         int count = 0;

         while (trackingElements.hasMoreElements()) {
            result[count++] = (TrackingData)trackingElements.nextElement();
         }

         return result;
      }
   }

   OTACalendarTransmissionService getTransmissionService() {
      return this._transmissionService;
   }

   @Override
   public void persistentContentStateChanged(int state) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void persistentContentModeChanged(int generation) {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         ticket.hashCode();
      }

      synchronized (this._trackingStore) {
         boolean var10 = false /* VF: Semaphore variable */;

         try {
            var10 = true;
            boolean e = false;
            Enumeration enumeration = this._trackingStore.elements();

            while (enumeration.hasMoreElements()) {
               TrackingData data = (TrackingData)enumeration.nextElement();
               if (!data.checkCrypt(true, true)) {
                  data.reCrypt(true, true);
                  e = true;
               }
            }

            if (e) {
               PersistentObject.commit(this._trackingStore);
               var10 = false;
            } else {
               var10 = false;
            }
         } finally {
            if (var10) {
               CICALEventLogger.logEvent(1413760326, 2);
               return;
            }
         }
      }
   }

   @Override
   public void updateTransmissionStatus(TransmissionService aTransmissionService, int tagId, int codeInt, Object contextObject) {
      ServiceRecord serviceRecord = (ServiceRecord)ContextObject.get(contextObject, -6095803566992128485L);
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarServiceByKey(serviceRecord);
      int slowSyncSessionId = -1;
      int newStatusCode = -1;
      TrackingData data = null;
      synchronized (this._trackingStore) {
         data = (TrackingData)this._trackingStore.get(tagId);
         if (data != null) {
            synchronized (data) {
               switch (codeInt) {
                  case 0:
                  case 5:
                  case 6:
                  case 7:
                     newStatusCode = 1;
                     this._connectionStateGood = true;
                     break;
                  default:
                     if ((codeInt & 128) == 128) {
                        newStatusCode = 3;
                        if (data._packetType == 5) {
                           slowSyncSessionId = data._uid;
                        }

                        this._connectionStateGood = false;
                     } else {
                        newStatusCode = 2;
                     }
               }

               if (data._statusCode != newStatusCode) {
                  CICALEventLogger.logEvent(1381258069, 4, null, (long)newStatusCode << 32 | tagId & 4294967295L);
               }

               data._statusCode = newStatusCode;
               data._lastUpdate = System.currentTimeMillis();
               if (!data.checkCrypt(true, true)) {
                  data.reCrypt(true, true);
               }

               PersistentObject.commit(data);
            }
         }
      }

      if (slowSyncSessionId != -1) {
         System.out.print("CICALSlowSync Packet [");
         System.out.print(tagId);
         System.out.print("] code = ");
         System.out.println(codeInt);
         CICALEventLogger.logEvent(1398363205, 0, null, (long)newStatusCode << 32 | tagId & 4294967295L);
         CICALSlowSyncConverter cicalSlowSyncConverter = CICALSlowSyncConverter.getInstance();
         if (cicalSlowSyncConverter.isSlowSyncInProgress() && slowSyncSessionId != cicalSlowSyncConverter.getCurrentSlowSyncSessionID(calendarService)) {
            this._trackingStore.remove(tagId);
            PersistentObject.commit(this._trackingStore);
         }
      }
   }

   @Override
   public void run() {
      while (true) {
         synchronized (this) {
            label44:
            try {
               this.wait();
            } finally {
               break label44;
            }
         }

         if (this._trackingStore.size() > 0) {
            synchronized (this._trackingStore) {
               this.checkForRetries();
            }
         }
      }
   }

   public CICALTransmissionManager(OTACalendarTransmissionService transmissionService) {
      this._transmissionService = transmissionService;
      this._tagIds = new int[0];
      this._connectionStateGood = false;
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-6581519639896522540L);
      this._trackingStore = (IntHashtable)persistentObject.getContents();
      if (this._trackingStore == null) {
         this._trackingStore = (IntHashtable)(new Object());
         persistentObject.setContents(this._trackingStore, 51);
         persistentObject.commit();
      }

      this.resetUnsent();
      PersistentContent.addListener(this);
      this.start();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void checkForRetries() {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         if ((this._transmissionService.getStatus() & 1) != 0) {
            if (RadioInfo.getState() == 1) {
               if (RadioInfo.getSignalLevel() != -256) {
                  long currentTime = System.currentTimeMillis();
                  int count;
                  synchronized (this._trackingStore) {
                     count = this._trackingStore.size();
                     if (this._tagIds.length != count) {
                        Array.resize(this._tagIds, count);
                     }

                     this._trackingStore.keysToArray(this._tagIds);
                  }

                  Packet packet = null;
                  count--;

                  for (; count >= 0; count--) {
                     int tagId = this._tagIds[count];
                     TrackingData data = (TrackingData)this._trackingStore.get(tagId);
                     boolean var20 = false /* VF: Semaphore variable */;

                     try {
                        var20 = true;
                        if (data == null) {
                           var20 = false;
                        } else {
                           synchronized (data) {
                              long elapsedTime = currentTime - data._lastUpdate;
                              if (elapsedTime < 0) {
                                 data._lastUpdate = currentTime;
                                 elapsedTime = 0;
                                 PersistentObject.commit(data);
                              }

                              if (elapsedTime > 2592000000L) {
                                 CICALEventLogger.logEvent(1163408176, 2, null, tagId & 4294967295L);
                                 this._trackingStore.remove(tagId);
                                 PersistentObject.commit(this._trackingStore);
                                 var20 = false;
                              } else {
                                 if (this._connectionStateGood) {
                                    elapsedTime <<= 1;
                                 }

                                 boolean resendPacket = false;
                                 switch (data._statusCode) {
                                    case -1:
                                    case 2:
                                       break;
                                    case 0:
                                       if (elapsedTime > 240000) {
                                          resendPacket = true;
                                       }
                                       break;
                                    case 1:
                                    default:
                                       if (elapsedTime > 172800000) {
                                          CICALEventLogger.logEvent(1163407410, 4, null, tagId & 4294967295L);
                                          this._trackingStore.remove(tagId);
                                          PersistentObject.commit(this._trackingStore);
                                          var20 = false;
                                          continue;
                                       }
                                       break;
                                    case 3:
                                       int multiplier = Math.min(2, data._failCount);
                                       long requiredElapsedTime = (long)1200000 << multiplier;
                                       label151:
                                       if (elapsedTime > requiredElapsedTime) {
                                          if (data._packetType == 5) {
                                             CICALSlowSyncConverter cicalSlowSyncConverter = CICALSlowSyncConverter.getInstance();
                                             if (cicalSlowSyncConverter.isSlowSyncInProgress()
                                                && data._uid != cicalSlowSyncConverter.getCurrentSlowSyncSessionID()) {
                                                this._trackingStore.remove(tagId);
                                                PersistentObject.commit(this._trackingStore);
                                                break label151;
                                             }
                                          }

                                          resendPacket = true;
                                          data._failCount++;
                                       }
                                       break;
                                    case 4:
                                       if (elapsedTime > 43200000) {
                                          resendPacket = true;
                                       }
                                 }

                                 if (resendPacket) {
                                    if (packet == null) {
                                       packet = (Packet)(new Object());
                                    }

                                    packet.setPayload(data.getData());
                                    packet.setTag(tagId);
                                    this._transmissionService.retransmitPacket(packet, null);
                                    data._lastUpdate = currentTime;
                                    data._statusCode = 4;
                                    PersistentObject.commit(data);
                                    if (!this._connectionStateGood) {
                                       var20 = false;
                                       break;
                                    }
                                 }

                                 var20 = false;
                              }
                           }
                        }
                     } finally {
                        if (var20) {
                           CICALEventLogger.logEvent(1162168660, 2, null, tagId & 4294967295L);
                           this._trackingStore.remove(tagId);
                           continue;
                        }
                     }
                  }

                  ticket = null;
               }
            }
         }
      }
   }

   private final void resetUnsent() {
      synchronized (this._trackingStore) {
         Enumeration enumeration = this._trackingStore.elements();
         long currentTime = System.currentTimeMillis();

         while (enumeration.hasMoreElements()) {
            TrackingData data = (TrackingData)enumeration.nextElement();
            data._lastUpdate = currentTime;
            switch (data._statusCode) {
               case 1:
                  break;
               case 2:
               case 3:
               case 4:
               default:
                  data._statusCode = 0;
            }
         }

         PersistentObject.commit(this._trackingStore);
      }
   }
}
