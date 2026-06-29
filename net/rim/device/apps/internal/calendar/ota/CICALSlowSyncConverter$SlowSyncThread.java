package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.sync.OTASyncIDProvider;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.utility.serialization.SerializationException;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.vm.PersistentInteger;

class CICALSlowSyncConverter$SlowSyncThread extends Thread {
   private CalendarService _calendarService;
   private long _threadSyncStartDate;
   private long _threadSyncEndDate;
   private int _hashVersion;
   private boolean _debug;
   private boolean _slowSyncNotStarted;
   private final CICALSlowSyncConverter this$0;

   public CICALSlowSyncConverter$SlowSyncThread(
      CICALSlowSyncConverter _1, CalendarService calendarService, long syncStartDate, long syncEndDate, int hashVersion, boolean debug
   ) {
      this.this$0 = _1;
      this._calendarService = calendarService;
      this._threadSyncStartDate = syncStartDate;
      this._threadSyncEndDate = syncEndDate;
      this._hashVersion = hashVersion;
      this._debug = debug;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      boolean var31 = false /* VF: Semaphore variable */;
      boolean var37 = false /* VF: Semaphore variable */;

      label539: {
         label540: {
            label562: {
               label541: {
                  label563: {
                     try {
                        label527:
                        try {
                           var37 = true;
                           var31 = true;
                           OTACalendarSyncDataManager e = OTACalendarSyncDataManager.getInstance();
                           CalDB var48 = this._calendarService.getCalendarDatabase();
                           OTACalendarTransmissionService otaCalendarTransmissionService = (OTACalendarTransmissionService)TransmissionServiceManager.get(
                              this._calendarService.getTransmissionServiceID()
                           );
                           if (!this._calendarService.isSystemDefault()) {
                              this.moveCalendarEvents(this._calendarService);
                           }

                           CICALConfiguration cicalConfig = this._calendarService.getCICALConfiguration();
                           if (cicalConfig.isOTASlowSyncSupported() && cicalConfig.isOTACalendarEnabled()) {
                              ServiceRouting sr = ServiceRouting.getInstance();
                              if (!sr.isServiceRoutable(cicalConfig.getUID(), -1)) {
                                 CICALEventLogger.logEvent(1380927046, 0);
                                 this.this$0._needToAttemptSlowSync = true;
                                 this._slowSyncNotStarted = true;
                                 var31 = false;
                                 var37 = false;
                                 break label539;
                              }

                              if (ITPolicy.getBoolean(33, 6, false)) {
                                 int handle = sr.getRouteHandle(2);
                                 if (handle == -1 || !sr.isServiceRoutable(cicalConfig.getUID(), handle)) {
                                    CICALEventLogger.logEvent(1396854853, 0);
                                    this.this$0._needToAttemptSlowSync = true;
                                    this._slowSyncNotStarted = true;
                                    var31 = false;
                                    var37 = false;
                                    break label540;
                                 }
                              }

                              Object ticket = PersistentContent.getTicket();
                              if (ticket == null) {
                                 CICALEventLogger.logEvent(1347571540, 0);
                                 this.this$0._needToAttemptSlowSync = true;
                                 this._slowSyncNotStarted = true;
                                 var31 = false;
                                 var37 = false;
                                 break label562;
                              }

                              this.this$0._currentSlowSyncProcess = null;
                              this.this$0.purgeSlowSyncStatistics(this._calendarService);
                              this.this$0._syncStartDate = this._threadSyncStartDate;
                              this.this$0._syncEndDate = this._threadSyncEndDate;
                              long currentTime = System.currentTimeMillis();
                              int newSessionId = (int)(currentTime / 1000);
                              this.this$0.setLastSlowSyncService(this._calendarService.getUniqueServiceID());
                              this.this$0.setLastSlowSyncActivity();
                              int id = PersistentInteger.getId(-2725183197236608288L, -1);
                              PersistentInteger.set(id, newSessionId);
                              System.out.println("Starting slow sync with sessionID = " + newSessionId);
                              this.this$0._outgoingEventCount = -1;
                              this.this$0._incomingEventCount = -1;
                              this.this$0._slowSyncDoneForced = false;
                              synchronized (otaCalendarTransmissionService) {
                                 synchronized (var48.getLockObject()) {
                                    if (this._hashVersion != 16) {
                                    }

                                    this.this$0._slowSyncContext.clear();
                                    ContextObject.put(this.this$0._slowSyncContext, -2725183197236608288L, new Integer(newSessionId));
                                    if (this.this$0._syncStartDate < 0) {
                                       this.this$0._syncStartDate = System.currentTimeMillis() - 2592000000L;
                                       this.this$0._syncEndDate = Long.MAX_VALUE;
                                    }

                                    if (this.this$0._syncEndDate != Long.MAX_VALUE && this.this$0._syncEndDate < this.this$0._syncStartDate) {
                                       this.this$0._syncEndDate = Long.MAX_VALUE;
                                    }

                                    long maxIntegerDate = 2147483647000L;
                                    if (this.this$0._syncStartDate > maxIntegerDate) {
                                       this.this$0._syncStartDate = maxIntegerDate;
                                    }

                                    if (this.this$0._syncEndDate > maxIntegerDate) {
                                       this.this$0._syncEndDate = maxIntegerDate;
                                    }

                                    Object[] events = var48.getAllEventsInRange(this.this$0._syncStartDate, this.this$0._syncEndDate);
                                    this.resetSyncData(this._calendarService);
                                    int size = events == null ? 0 : events.length;
                                    CICALSlowSyncEvent slowSyncEvent = new CICALSlowSyncEvent(
                                       this._calendarService, (byte)30, newSessionId, size, this.this$0._syncStartDate, this.this$0._syncEndDate
                                    );
                                    int index = 0;
                                    int threshold = 500;

                                    label480:
                                    try {
                                       CICALEventLogger.logEvent(1398361667, 0);
                                       CICALEventLogger.logEvent("ECNT" + size, 0);
                                       boolean commitRequired = false;

                                       while (index < size) {
                                          if (slowSyncEvent == null) {
                                             slowSyncEvent = new CICALSlowSyncEvent(this._calendarService, (byte)30, newSessionId, size);
                                          }

                                          Event event = (Event)events[index];
                                          OTASyncData syncData = e.get(event);
                                          if (syncData == null) {
                                             syncData = new OTASyncData(0, 0);
                                          }

                                          slowSyncEvent.addEvent(event, syncData, this._debug);
                                          e.addWithoutCommit(event, syncData);
                                          commitRequired = true;
                                          index++;
                                          if (--threshold == 0) {
                                             if (commitRequired) {
                                                e.commit();
                                                commitRequired = false;
                                             }

                                             this.this$0.transmitCICALEvent(this._calendarService, slowSyncEvent);
                                             slowSyncEvent = null;
                                             threshold = 500;
                                          }
                                       }

                                       if (commitRequired) {
                                          e.commit();
                                       }

                                       if (slowSyncEvent != null) {
                                          this.this$0.transmitCICALEvent(this._calendarService, slowSyncEvent);
                                       }

                                       ticket = null;
                                    } catch (Throwable var43) {
                                       this.this$0.abortSlowSync(this._calendarService, newSessionId, (byte)1, false);
                                       if (e instanceof SerializationException) {
                                          CICALEventLogger.logEvent(1398358829, 2);
                                       } else {
                                          CICALEventLogger.logEvent(1398363437, 2);
                                       }
                                       break label480;
                                    }
                                 }

                                 var31 = false;
                                 var37 = false;
                                 break label541;
                              }
                           }

                           this._slowSyncNotStarted = true;
                           var31 = false;
                           var37 = false;
                           break label563;
                        } finally {
                           if (var37) {
                              int id = PersistentInteger.getId(-2725183197236608288L, -1);
                              PersistentInteger.set(id, -1);
                              this.this$0._needToAttemptSlowSync = true;
                              CICALEventLogger.logEvent(1398031698, 2);
                              QuincyManager.sendJavaLogworthy("SlowSyncThreadError");
                              this._slowSyncNotStarted = true;
                              var31 = false;
                              break label527;
                           }
                        }
                     } finally {
                        if (var31) {
                           this.this$0._slowSyncThreadRunning = false;
                           if (this._slowSyncNotStarted) {
                              this.this$0.processNextSlowSync();
                           }
                        }
                     }

                     this.this$0._slowSyncThreadRunning = false;
                     if (this._slowSyncNotStarted) {
                        this.this$0.processNextSlowSync();
                        return;
                     }

                     return;
                  }

                  this.this$0._slowSyncThreadRunning = false;
                  if (this._slowSyncNotStarted) {
                     this.this$0.processNextSlowSync();
                  }

                  return;
               }

               this.this$0._slowSyncThreadRunning = false;
               if (this._slowSyncNotStarted) {
                  this.this$0.processNextSlowSync();
                  return;
               }

               return;
            }

            this.this$0._slowSyncThreadRunning = false;
            if (this._slowSyncNotStarted) {
               this.this$0.processNextSlowSync();
            }

            return;
         }

         this.this$0._slowSyncThreadRunning = false;
         if (this._slowSyncNotStarted) {
            this.this$0.processNextSlowSync();
         }

         return;
      }

      this.this$0._slowSyncThreadRunning = false;
      if (this._slowSyncNotStarted) {
         this.this$0.processNextSlowSync();
      }
   }

   private void resetSyncData(CalendarService calendarService) {
      CalDB calDB = calendarService.getCalendarDatabase();
      OTACalendarSyncDataManager otaSyncDataManager = OTACalendarSyncDataManager.getInstance();
      IntEnumeration intEnum = otaSyncDataManager.getSyncIDs();
      boolean commitRequired = false;

      while (intEnum.hasMoreElements()) {
         int key = intEnum.nextElement();
         OTASyncData syncData = otaSyncDataManager.get(key);
         if (syncData != null) {
            int ownerID = syncData.getOwnerId();
            syncData.reset();
            if (ownerID != -1) {
               syncData.setOwnerId(ownerID);
            }
         } else {
            otaSyncDataManager.removeWithoutCommit(key);
            commitRequired = true;
         }
      }

      Object[] elements = new Object[0];
      calDB.getElements(elements);

      for (int i = 0; i < elements.length; i++) {
         OTASyncData syncData = otaSyncDataManager.get((OTASyncIDProvider)elements[i]);
         if (syncData == null) {
            syncData = new OTASyncData(0, 0);
            otaSyncDataManager.addWithoutCommit((OTASyncIDProvider)elements[i], syncData);
            commitRequired = true;
         }
      }

      if (commitRequired) {
         otaSyncDataManager.commit();
      }
   }

   private void moveCalendarEvents(CalendarService destinationCalendar) {
      ServiceIdentifier[] calendarServices = this.this$0._calendarServiceManager.getCalendarServices(true);
      synchronized (EventUtilities.getMoveEventsLockObject()) {
         EventLogger.logEvent(-256469206327664059L, 1129141075, destinationCalendar.getUniqueServiceID(), 10, 0);
         if (calendarServices != null) {
            for (int i = 0; i < calendarServices.length; i++) {
               CalendarService service = (CalendarService)calendarServices[i];
               EventUtilities.moveCalendarEvents(service, destinationCalendar);
            }
         }
      }
   }
}
