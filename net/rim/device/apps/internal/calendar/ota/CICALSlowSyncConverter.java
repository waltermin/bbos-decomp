package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.internal.synchronization.ota.api.SyncAgentStatistics;
import net.rim.device.internal.synchronization.ota.api.SyncAgentStatisticsCollector;
import net.rim.device.internal.synchronization.ota.api.SyncAgentUrl;
import net.rim.vm.PersistentInteger;

public class CICALSlowSyncConverter extends CICALBaseConverter implements ServiceRoutingListener2 {
   ContextObject _slowSyncContext = (ContextObject)(new Object());
   private boolean _needToAttemptSlowSync;
   private boolean _userInitiated;
   private long _syncStartDate = -1;
   private long _syncEndDate = -1;
   private int _outgoingEventCount = -1;
   private int _incomingEventCount = -1;
   private int _totalEvents = -1;
   private boolean _slowSyncDoneForced = false;
   SyncAgentUrl _currentSlowSyncProcess;
   CICALSlowSyncConverter$SlowSyncThread _slowSyncThread;
   private boolean _slowSyncThreadRunning = false;
   private long[] _pendingSlowSyncs = new long[0];
   private long _slowSyncInProgress;
   private CalendarServiceManager _calendarServiceManager;
   static final long SLOW_SYNC_SESSION_ID_GUID = -2725183197236608288L;
   static final int NO_SLOW_SYNC_SESSION = -1;
   static final long LAST_ACTIVITY_STORE_ID = -6523260229176026567L;
   static final PersistentObject _lastActivityStore = RIMPersistentStore.getPersistentObject(-6523260229176026567L);
   static final int NO_SLOW_SYNC_ACTIVITY = -1;
   static final long LAST_SERVICE_STORE_ID = 1883207937961135925L;
   static final PersistentObject _lastServiceStore = RIMPersistentStore.getPersistentObject(1883207937961135925L);
   static final int NO_SLOW_SYNC_SERVICE = -1;
   static final int SLOW_SYNC_TIMEOUT = 3600000;
   private static final long SINGLETON_ID = 2920655573871026586L;
   public static final int PACKET_EVENT_THRESHOLD = 500;
   private static IntIntHashtable _previousSessions = (IntIntHashtable)(new Object());
   private static final byte[] SLOW_SYNC_COMPONENT_HEADER = new byte[]{16, 1, 1, 5};

   CICALSlowSyncConverter() {
      ServiceRouting sr = ServiceRouting.getInstance();
      sr.addListener(this);
      this._calendarServiceManager = CalendarServiceManager.getInstance();
   }

   public static CICALSlowSyncConverter getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      CICALSlowSyncConverter slowSyncConverter = null;
      synchronized (ar) {
         Object obj = ar.get(2920655573871026586L);
         if (obj instanceof CICALSlowSyncConverter) {
            slowSyncConverter = (CICALSlowSyncConverter)obj;
         }
      }

      if (slowSyncConverter == null) {
         slowSyncConverter = new CICALSlowSyncConverter();
         synchronized (ar) {
            ar.replace(2920655573871026586L, slowSyncConverter);
            return slowSyncConverter;
         }
      } else {
         return slowSyncConverter;
      }
   }

   @Override
   public boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public byte[] convert(Object inputObject, Object contextObject) {
      if (!(inputObject instanceof CICALSlowSyncEvent)) {
         return null;
      }

      CICALSlowSyncEvent slowSyncEvent = (CICALSlowSyncEvent)inputObject;
      DataBuffer dataBuffer = (DataBuffer)(new Object());
      dataBuffer.writeByte(slowSyncEvent.getType());
      dataBuffer.write(SLOW_SYNC_COMPONENT_HEADER);
      byte[] contextObjectBytes = slowSyncEvent.convert(contextObject);
      if (contextObjectBytes != null) {
         dataBuffer.write(contextObjectBytes);
      }

      dataBuffer.writeByte(0);
      dataBuffer.writeByte(0);
      return dataBuffer.toArray();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Object convert(byte[] inputBytes, Object contextObject) {
      DataBuffer data = (DataBuffer)(new Object(inputBytes, 0, inputBytes.length, true));
      CalendarService calendarService = null;

      try {
         ContextObject converterContext = ContextObject.castOrCreate(contextObject);
         ServiceRecord sr = (ServiceRecord)ContextObject.get(converterContext, -6095803566992128485L);
         calendarService = (CalendarService)ContextObject.get(converterContext, 6741741218837016896L);
         if (calendarService == null) {
            calendarService = this._calendarServiceManager.findCalendarServiceByKey(sr);
         }

         boolean sessionIdFound = false;
         byte command = data.readByte();
         if (data.readByte() != 16) {
            throw new Object("Wrong version number");
         }

         byte componentId = data.readByte();
         if (componentId == 0) {
            return null;
         }

         if (componentId != 1) {
            throw new Object("Expecting component ID");
         }

         byte byteData = 0;
         int intData = data.readCompressedInt();
         if (intData != 1) {
            throw new Object("Expecting length of 1");
         }

         if (data.readByte() != 5) {
            throw new Object("Wrong component type");
         }

         CICALSlowSyncEvent event = null;
         int outgoingEventCount = -1;
         int incomingEventCount = -1;
         int trueRequestedAmount = 0;
         switch (command) {
            case 28:
            case 30:
               CICALEventLogger.logEvent(1398363459, 2);
               break;
            case 29:
            case 31:
            case 32:
            case 33:
            default:
               event = new CICALSlowSyncEvent(calendarService, command);

               while (!data.eof()) {
                  byte fieldId = data.readByte();
                  if (fieldId == 0) {
                     break;
                  }

                  switch (fieldId) {
                     case 2:
                     case 64:
                     case 65:
                     case 72:
                     case 74:
                     case 76:
                        intData = data.readCompressedInt();
                        if (intData != 4) {
                           throw new Object("Expecting 4");
                        }

                        intData = data.readInt();
                        switch (fieldId) {
                           case 2:
                              event.addEvent(intData);
                              trueRequestedAmount++;
                              continue;
                           case 64:
                           case 83:
                              if (command == 31) {
                                 System.out
                                    .println(
                                       ((StringBuffer)(new Object("Server is requesting "))).append(intData).append(" events for sync from device.").toString()
                                    );
                                 CICALEventLogger.logEvent(((StringBuffer)(new Object("RSTS"))).append(intData).toString(), 0);
                              }

                              outgoingEventCount = intData;
                              continue;
                           case 65:
                              if (intData != this.getCurrentSlowSyncSessionID(calendarService) && command != 33) {
                                 System.out
                                    .println(
                                       ((StringBuffer)(new Object("Server for service[")))
                                          .append(calendarService)
                                          .append("] is requesting records with an invalid session id. (Current=")
                                          .append(this.getCurrentSlowSyncSessionID(calendarService))
                                          .append("Requested = ")
                                          .append(intData)
                                          .append(")")
                                          .toString()
                                    );
                                 this.abortSlowSync(calendarService, intData, (byte)4, false);
                                 return null;
                              }

                              event.setSessionID(intData);
                              sessionIdFound = true;
                              continue;
                           case 72:
                              event.setSyncStartDate((long)intData * 1000);
                              continue;
                           case 74:
                              if (command == 31) {
                                 System.out
                                    .println(
                                       ((StringBuffer)(new Object("Server will be sending "))).append(intData).append(" events to the device.").toString()
                                    );
                                 CICALEventLogger.logEvent(((StringBuffer)(new Object("RSFS"))).append(intData).toString(), 0);
                              }

                              incomingEventCount = intData;
                              continue;
                           case 76:
                              event.setSyncEndDate((long)intData * 1000);
                           default:
                              continue;
                        }
                     case 73:
                        intData = data.readCompressedInt();
                        if (intData != 1) {
                           throw new Object("Expecting 4");
                        }

                        byteData = data.readByte();
                        event.setResult(byteData);
                        break;
                     default:
                        intData = data.readCompressedInt();
                        data.skipBytes(intData);
                  }
               }

               if (command == 31) {
                  event.setIncomingRecordCount(incomingEventCount);
                  event.setOutgoingRecordCount(outgoingEventCount);
                  this._incomingEventCount = event.getIncomingRecordCount();
                  if (this._outgoingEventCount == -1) {
                     this._outgoingEventCount = event.getOutgoingRecordCount();
                  }

                  if (trueRequestedAmount != event.getOutgoingRecordCount()) {
                     System.out
                        .println(
                           ((StringBuffer)(new Object("Server is really requesting only ")))
                              .append(trueRequestedAmount)
                              .append(" for this request.")
                              .toString()
                        );
                     CICALEventLogger.logEvent(((StringBuffer)(new Object("RSRS"))).append(trueRequestedAmount).toString(), 0);
                  }

                  this._totalEvents = this._incomingEventCount + this._outgoingEventCount;
                  long sid = calendarService.getUniqueServiceID();
                  String datasourceName = EventUtilities.getEmailAddress(sr);
                  if (datasourceName == null) {
                     datasourceName = "<empty>";
                  }

                  if (!this._userInitiated) {
                     this._currentSlowSyncProcess = (SyncAgentUrl)(new Object(sid, datasourceName, "Calendar", true));
                     SyncAgentStatistics stats = SyncAgentStatisticsCollector.getSyncAgentStatisticsFor(this._currentSlowSyncProcess, false);
                     stats.setRemainingNumberOfOperations(this._totalEvents, this.getCurrentSlowSyncSessionID(calendarService));
                  }
               }

               if (command == 33) {
                  event.setIncomingRecordCount(incomingEventCount);
                  event.setOutgoingRecordCount(outgoingEventCount);
               }

               if (this.checkIfSlowSyncCompleted(calendarService)) {
                  this.processNextSlowSync();
               }
         }

         if (!sessionIdFound && command != 29) {
            CICALEventLogger.logEvent(1398362957, 2);
            this.abortSlowSync(calendarService, this.getCurrentSlowSyncSessionID(calendarService), (byte)6, false);
            return null;
         } else {
            return event;
         }
      } catch (Throwable var21) {
         if (calendarService != null) {
            this.abortSlowSync(calendarService, this.getCurrentSlowSyncSessionID(calendarService), (byte)7, false);
         }

         if (!(e instanceof Object)) {
            return null;
         } else {
            throw (Object)e;
         }
      }
   }

   synchronized int getCurrentSlowSyncSessionID() {
      return this.getCurrentSlowSyncSessionID(null);
   }

   synchronized int getCurrentSlowSyncSessionID(CalendarService calendarService) {
      int id = PersistentInteger.getId(-2725183197236608288L, -1);
      return PersistentInteger.get(id);
   }

   private synchronized long getLastSlowSyncActivity() {
      long lastSlowSyncActivity = -1;
      if (_lastActivityStore.getContents() != null) {
         Long lastActivity = (Long)_lastActivityStore.getContents();
         return lastActivity;
      } else {
         Long lastActivity = (Long)(new Object(-1));
         _lastActivityStore.setContents(lastActivity, 51);
         _lastActivityStore.commit();
         EventLogger.logEvent(-256469206327664059L, 1279349317, 0);
         return lastSlowSyncActivity;
      }
   }

   private synchronized void setLastSlowSyncActivity() {
      this.setLastSlowSyncActivity(System.currentTimeMillis());
   }

   private synchronized void setLastSlowSyncActivity(long time) {
      Long lastActivity = (Long)(new Object(time));
      _lastActivityStore.setContents(lastActivity, 51);
      _lastActivityStore.commit();
   }

   private synchronized long getLastSlowSyncService() {
      long lastSlowSyncService = -1;
      if (_lastServiceStore.getContents() != null) {
         Long lastService = (Long)_lastServiceStore.getContents();
         return lastService;
      } else {
         Long lastService = (Long)(new Object(-1));
         _lastServiceStore.setContents(lastService, 51);
         _lastServiceStore.commit();
         EventLogger.logEvent(-256469206327664059L, 1280528965, 0);
         return lastSlowSyncService;
      }
   }

   private synchronized void setLastSlowSyncService(long serviceID) {
      Long lastService = (Long)(new Object(serviceID));
      _lastServiceStore.setContents(lastService, 51);
      _lastServiceStore.commit();
   }

   private synchronized void resetTimeOutValues() {
      this.setLastSlowSyncService(-1);
      this.setLastSlowSyncActivity(-1);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   void transmitCICALEvent(CalendarService calendarService, Object event) {
      if (event instanceof CICALSlowSyncEvent) {
         CICALSlowSyncEvent slowSyncEvent = (CICALSlowSyncEvent)event;
         byte command = slowSyncEvent.getType();
         byte[] commandHex = Integer.toHexString(command).getBytes();
         short commandCode = (short)((commandHex[0] << 8) + commandHex[1]);
         int code = 1396506624 | commandCode;
         CICALEventLogger.logEvent(code, 0);
      }

      try {
         if (!calendarService.getCICALConfiguration().isSendSyncEnabled()) {
            CICALEventLogger.logEvent(1397966381, 0);
            return;
         }

         String type = "net.rim.RIMCalendarSlowSync";
         if (event instanceof Object) {
            type = "net.rim.RIMCalendarApptUpdate";
         }

         ContextObject.put(this._slowSyncContext, 6741741218837016896L, calendarService);
         OTACalendarTransmissionService otaCalendarTransmissionService = (OTACalendarTransmissionService)TransmissionServiceManager.get(
            calendarService.getTransmissionServiceID()
         );
         otaCalendarTransmissionService.transmitObject(type, event, this._slowSyncContext);
         ContextObject.remove(this._slowSyncContext, 6741741218837016896L);
      } catch (Throwable var9) {
         if (e instanceof Object) {
            CICALEventLogger.logEvent(1398363181, 2);
            return;
         }

         CICALEventLogger.logEvent(1398363437, 2);
         return;
      }
   }

   synchronized void abortSlowSync(CalendarService calendarService, int sessionID, byte result, boolean serverAbort) {
      if (!_previousSessions.contains(sessionID) && !serverAbort) {
         CICALSlowSyncEvent abort = new CICALSlowSyncEvent(calendarService, (byte)32, sessionID, -1);
         abort.setResult(result);
         this.transmitCICALEvent(calendarService, abort);
         _previousSessions.put(sessionID, (int)(System.currentTimeMillis() / 1000));
      }

      CICALEventLogger.logEvent(this.getLogCode(1396768768, result), 3);
      this.reset(calendarService, true, result);
      this.processNextSlowSync();
   }

   private int getLogCode(int first2byteCode, int code) {
      int high = code / 16;
      int low = code % 16;
      low = low < 10 ? low + 48 : low + 87;
      high = high < 10 ? high + 48 : high + 87;
      return first2byteCode | high << 8 | low;
   }

   private void purgePreviousSessionList() {
      IntEnumeration elements = _previousSessions.elements();
      IntEnumeration keys = _previousSessions.keys();

      while (elements.hasMoreElements()) {
         int time = elements.nextElement();
         int key = keys.nextElement();
         if (System.currentTimeMillis() - (long)time * 1000 > 1800000) {
            _previousSessions.remove(key);
         }
      }
   }

   void sendRequestedEvents(CICALSlowSyncEvent request) {
      CalendarService calendarService = (CalendarService)request.getServiceIdentifier();
      if (this.getCurrentSlowSyncSessionID(calendarService) == request.getSessionID()) {
         new CICALSlowSyncConverter$EventSenderThread(this, request).start();
      }
   }

   public synchronized int decrementIncomingQueue(CalendarService calendarService, int sessionID) {
      if (this.getCurrentSlowSyncSessionID(calendarService) == sessionID) {
         this._incomingEventCount--;
      }

      if (this._incomingEventCount < 0) {
         this._incomingEventCount = 0;
      }

      if (!this._userInitiated && this._currentSlowSyncProcess != null) {
         SyncAgentStatistics stats = SyncAgentStatisticsCollector.getSyncAgentStatisticsFor(this._currentSlowSyncProcess, false);
         stats.incrementNumberOfExecutedOperations();
      }

      if (this.checkIfSlowSyncCompleted(calendarService)) {
         this.processNextSlowSync();
      } else {
         this.setLastSlowSyncActivity();
      }

      return this._incomingEventCount;
   }

   public synchronized int decrementOutgoingQueue(CalendarService calendarService, int sessionID) {
      if (this.getCurrentSlowSyncSessionID(calendarService) == sessionID) {
         this._outgoingEventCount--;
      }

      if (this._outgoingEventCount < 0) {
         this._outgoingEventCount = 0;
      }

      if (!this._userInitiated && this._currentSlowSyncProcess != null) {
         SyncAgentStatistics stats = SyncAgentStatisticsCollector.getSyncAgentStatisticsFor(this._currentSlowSyncProcess, false);
         if (stats != null) {
            stats.incrementNumberOfExecutedOperations();
         }
      }

      if (this.checkIfSlowSyncCompleted(calendarService)) {
         this.processNextSlowSync();
      } else {
         this.setLastSlowSyncActivity();
      }

      return this._outgoingEventCount;
   }

   private synchronized void resetOutgoingQueue(CalendarService calendarService, int sessionID, boolean setFailedOps) {
      int missedEvents = 0;
      if (this.getCurrentSlowSyncSessionID(calendarService) == sessionID) {
         missedEvents = this._outgoingEventCount;
         this._outgoingEventCount = 0;
      }

      if (!this._userInitiated && this._currentSlowSyncProcess != null) {
         SyncAgentStatistics stats = SyncAgentStatisticsCollector.getSyncAgentStatisticsFor(this._currentSlowSyncProcess, false);
         if (stats != null) {
            if (missedEvents == 0) {
               missedEvents = 1;
            }

            for (int i = 0; i < missedEvents; i++) {
               if (setFailedOps) {
                  stats.incrementNumberOfFailedOperations();
               } else {
                  stats.incrementNumberOfExecutedOperations();
               }
            }
         }
      }
   }

   private synchronized void resetIncommingQueue(CalendarService calendarService, int sessionID, boolean setFailedOps) {
      int missedEvents = 0;
      if (this.getCurrentSlowSyncSessionID(calendarService) == sessionID) {
         missedEvents = this._incomingEventCount;
         this._incomingEventCount = 0;
      }

      if (!this._userInitiated && this._currentSlowSyncProcess != null) {
         SyncAgentStatistics stats = SyncAgentStatisticsCollector.getSyncAgentStatisticsFor(this._currentSlowSyncProcess, false);
         if (stats != null) {
            if (missedEvents == 0) {
               missedEvents = 1;
            }

            for (int i = 0; i < missedEvents; i++) {
               if (setFailedOps) {
                  stats.incrementNumberOfFailedOperations();
               } else {
                  stats.incrementNumberOfExecutedOperations();
               }
            }
         }
      }
   }

   public synchronized boolean checkIfSlowSyncCompleted(CalendarService calendarService) {
      boolean result = true;
      if (this.isSlowSyncInProgress(calendarService)) {
         if (this._incomingEventCount == 0 && this._outgoingEventCount == 0) {
            this.reset(calendarService, false, (byte)0);
            calendarService.getCalendarDatabase().resetStatistics();
            OTACalendarTransmissionService otaCalendarTransmissionService = (OTACalendarTransmissionService)TransmissionServiceManager.get(
               calendarService.getTransmissionServiceID()
            );
            otaCalendarTransmissionService.markSlowSyncComplete(calendarService);
         } else {
            result = false;
         }
      }

      if (result) {
         CICALEventLogger.logEvent(1396985413, 0);
      }

      return result;
   }

   public synchronized int[] getLastTimeSlowSyncCompleted(CalendarService calendarService) {
      OTACalendarTransmissionService otaCalendarTransmissionService = (OTACalendarTransmissionService)TransmissionServiceManager.get(
         calendarService.getTransmissionServiceID()
      );
      return otaCalendarTransmissionService.getLastTimeSlowSyncCompleted();
   }

   public synchronized boolean isSlowSyncInProgress() {
      return this.isSlowSyncInProgress(null);
   }

   public synchronized boolean isSlowSyncInProgress(CalendarService calendarService) {
      return this.getCurrentSlowSyncSessionID(calendarService) != -1;
   }

   private synchronized boolean isSlowSyncTimedOut() {
      long lastSlowSyncActivity = this.getLastSlowSyncActivity();
      long currentTime = System.currentTimeMillis();
      long timeDifference = currentTime - lastSlowSyncActivity;
      long idleMinutes = timeDifference / 60000;
      if (lastSlowSyncActivity == -1) {
         EventLogger.logEvent(-256469206327664059L, 1431060820, 2);
         return false;
      } else {
         EventLogger.logEvent(-256469206327664059L, 1397967181, idleMinutes, 10, 3);
         return currentTime > lastSlowSyncActivity + 3600000;
      }
   }

   public synchronized boolean receivedStatistics() {
      return this._totalEvents != -1;
   }

   public synchronized void purgeSlowSyncStatistics(CalendarService calendarService) {
      if (this._currentSlowSyncProcess == null) {
         SyncAgentStatistics[] saStats = SyncAgentStatisticsCollector.getAllSyncAgentStatistics();
         if (saStats != null) {
            for (int i = 0; i < saStats.length; i++) {
               String databaseName = saStats[i].getSyncAgentUrl().getDatabaseName();
               if (StringUtilities.strEqual(databaseName, "Calendar")) {
                  this._currentSlowSyncProcess = saStats[i].getSyncAgentUrl();
                  break;
               }
            }
         }
      }

      if (this._currentSlowSyncProcess != null) {
         SyncAgentStatisticsCollector.purgeSyncAgentStatisticsFor(this._currentSlowSyncProcess);
      }
   }

   private synchronized void reset(CalendarService calendarService, boolean removeStats, byte result) {
      int id = PersistentInteger.getId(-2725183197236608288L, -1);
      PersistentInteger.set(id, -1);
      this.resetTimeOutValues();
      this._outgoingEventCount = -1;
      this._incomingEventCount = -1;
      this._slowSyncDoneForced = false;
      if (removeStats) {
         this.purgeSlowSyncStatistics(calendarService);
      }

      this._totalEvents = -1;
      this._currentSlowSyncProcess = null;
      this._slowSyncContext.clear();
      this.purgePreviousSessionList();
      calendarService.getCalendarDatabase().resetStatistics();
      this._syncStartDate = -1;
      this._syncEndDate = -1;
   }

   public synchronized boolean isSlowSyncRequired(CalendarService calendarService) {
      return this._needToAttemptSlowSync;
   }

   public synchronized void setSlowSyncRequired(CalendarService calendarService, boolean needToAttempSlowSync) {
      this._needToAttemptSlowSync = needToAttempSlowSync;
   }

   public synchronized void stopCurrentCalendarSlowSync(CalendarService calendarService) {
      int sessiondId = this.getCurrentSlowSyncSessionID(calendarService);
      if (sessiondId != -1) {
         if (calendarService == null) {
            calendarService = this._calendarServiceManager.findCalendarService(this._slowSyncInProgress);
            if (calendarService == null) {
               return;
            }
         }

         this.abortSlowSync(calendarService, sessiondId, (byte)12, false);
      } else {
         this.reset(calendarService, true, (byte)-1);
      }
   }

   public synchronized void forceComplete(CICALSlowSyncEvent slowSyncEvent) {
      CalendarService calendarService = (CalendarService)slowSyncEvent.getServiceIdentifier();
      int sessiondId = this.getCurrentSlowSyncSessionID(calendarService);
      if (sessiondId == slowSyncEvent.getSessionID()) {
         if (this._incomingEventCount != 0) {
            CICALEventLogger.logEvent(1279869266, 2);
            this.resetIncommingQueue(calendarService, sessiondId, false);
         }

         if (this._outgoingEventCount != 0) {
            CICALEventLogger.logEvent(1398032716, 0);
         }

         this._slowSyncDoneForced = true;
         CICALEventLogger.logEvent(1396985414, 0);
         if (this.checkIfSlowSyncCompleted(calendarService)) {
            this.processNextSlowSync();
         }
      }
   }

   public synchronized void startCalendarSlowSync(long catalystGUID, CalendarService calendarService) {
      this.startCalendarSlowSync(catalystGUID, calendarService, false);
   }

   public synchronized void startCalendarSlowSync(long catalystGUID, CalendarService calendarService, boolean userInitiated) {
      this.startCalendarSlowSync(catalystGUID, calendarService, -1, -1, (byte)82, false, userInitiated);
   }

   synchronized void startCalendarSlowSync(
      long catalystGUID, CalendarService calendarService, long syncStartDate, long syncEndDate, byte hashVersion, boolean debug, boolean userInitiated
   ) {
      this._needToAttemptSlowSync = false;
      String catalystGUIDString = Long.toString(catalystGUID);
      CICALEventLogger.logEvent(((StringBuffer)(new Object("1398362705="))).append(catalystGUIDString).toString(), 0);
      CICALSlowSyncConverter slowSyncConverter = getInstance();
      if (slowSyncConverter != null && !slowSyncConverter.isSlowSyncInProgress(calendarService)) {
         CICALConfiguration configuration = calendarService.getCICALConfiguration();
         if (configuration.isOTAConfigSupported()) {
            CICALConfigConverter configConverter = CICALConfigConverter.getInstance();
            configConverter.sendDeviceConfiguration(calendarService);
         } else {
            CICALEventLogger.logEvent(1482911334, 3);
         }

         if (!configuration.isOTASlowSyncSupported()) {
            CICALEventLogger.logEvent(1483962723, 3);
         }

         if (this._slowSyncThread == null) {
            this._slowSyncThread = new CICALSlowSyncConverter$SlowSyncThread(this, calendarService, syncStartDate, syncEndDate, hashVersion, debug);
         }

         if (this._slowSyncThread.isAlive()) {
            CICALEventLogger.logEvent(1414025796, 3);
         }

         if (!this._slowSyncThreadRunning) {
            this._userInitiated = userInitiated;
            this._slowSyncThread = new CICALSlowSyncConverter$SlowSyncThread(this, calendarService, syncStartDate, syncEndDate, hashVersion, debug);
            this._slowSyncInProgress = calendarService.getUniqueServiceID();
            this._slowSyncThreadRunning = true;
            this._slowSyncThread.start();
         } else {
            this.queueSlowSync(calendarService);
         }
      } else {
         if (slowSyncConverter != null) {
            CICALEventLogger.logEvent(1396788306, 3);
            this.queueSlowSync(calendarService);
            if (this.isSlowSyncTimedOut()) {
               long serviceToAbortID = this.getLastSlowSyncService();
               if (serviceToAbortID != -1) {
                  CalendarService serviceToAbort = this._calendarServiceManager.findCalendarService(serviceToAbortID);
                  if (serviceToAbort != null) {
                     this.abortSlowSync(serviceToAbort, this.getCurrentSlowSyncSessionID(serviceToAbort), (byte)9, false);
                     this.queueSlowSync(serviceToAbort);
                     this.processNextSlowSync();
                     return;
                  }

                  EventLogger.logEvent(-256469206327664059L, 1230197839, 2);
                  return;
               }

               EventLogger.logEvent(-256469206327664059L, 1431524431, 2);
            }
         }
      }
   }

   private boolean queueSlowSync(CalendarService calendarService) {
      long serviceID = calendarService.getUniqueServiceID();
      boolean addedToQueue;
      synchronized (this._pendingSlowSyncs) {
         if (Arrays.getIndex(this._pendingSlowSyncs, serviceID) < 0 && serviceID != this._slowSyncInProgress) {
            Arrays.add(this._pendingSlowSyncs, serviceID);
            addedToQueue = true;
         } else {
            addedToQueue = false;
         }
      }

      EventLogger.logEvent(-256469206327664059L, 1397969217, this._pendingSlowSyncs.length, 10, 3);
      return addedToQueue;
   }

   private boolean processNextSlowSync() {
      this._slowSyncInProgress = 0;
      EventLogger.logEvent(-256469206327664059L, 1397969234, this._pendingSlowSyncs.length, 10, 3);
      long queuedService;
      synchronized (this._pendingSlowSyncs) {
         if (this._pendingSlowSyncs.length == 0) {
            return false;
         }

         queuedService = this._pendingSlowSyncs[0];
         Arrays.removeAt(this._pendingSlowSyncs, 0);
      }

      CalendarService calendarService = this._calendarServiceManager.findCalendarService(queuedService);
      if (calendarService != null) {
         this.startCalendarSlowSync(2920655573871026586L, calendarService);
         return true;
      } else {
         EventLogger.logEvent(-256469206327664059L, 1397967187, 2);
         return this.processNextSlowSync();
      }
   }

   @Override
   public void serviceRoutingStateChanged(String service, boolean serviceState) {
      ServiceRouting sr = ServiceRouting.getInstance();
      int route = sr.getRouteHandle(ServiceRoutingProperties.STP);
      if (serviceState && route >= 0 && sr.isServiceRoutable(service, route)) {
         ServiceIdentifier[] services = this._calendarServiceManager.getCalendarServices();

         for (int i = 0; i < services.length; i++) {
            CalendarService calendarService = (CalendarService)services[i];
            String calService = calendarService.getCICALConfiguration().getUID();
            if (StringUtilities.strEqual(service, calService) && this.isSlowSyncRequired(calendarService)) {
               this.startCalendarSlowSync(2920655573871026586L, calendarService);
            }
         }
      }
   }

   @Override
   public void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      ServiceRouting sr = ServiceRouting.getInstance();
      if (routeState && routeHandle >= 0 && sr.getRouteHandle(ServiceRoutingProperties.STP) == routeHandle) {
         ServiceIdentifier[] services = this._calendarServiceManager.getCalendarServices();

         for (int i = 0; i < services.length; i++) {
            CalendarService calendarService = (CalendarService)services[i];
            String calService = calendarService.getCICALConfiguration().getUID();
            if (sr.isServiceRoutable(calService, routeHandle) && this.isSlowSyncRequired(calendarService)) {
               this.startCalendarSlowSync(2920655573871026586L, calendarService);
            }
         }
      }
   }

   @Override
   public void serviceRoutingCapabilitiesChanged(String service) {
   }
}
