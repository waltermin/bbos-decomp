package net.rim.device.apps.internal.activation;

import net.rim.device.api.collection.util.BigLongVector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceIdentifier;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.cldc.io.fastdormancy.FastDormancyManager;
import net.rim.device.internal.provisioning.ActivationService;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.synchronization.ota.api.SyncAgent;
import net.rim.device.internal.synchronization.ota.api.SyncAgentListener;
import net.rim.device.internal.synchronization.ota.api.SyncAgentStatistics;
import net.rim.device.internal.synchronization.ota.api.SyncAgentUrl;
import net.rim.device.internal.synchronization.ota.service.Configuration;
import net.rim.device.internal.synchronization.ota.service.ServicesConfigurationManager;
import net.rim.device.internal.ui.IconCollection;
import net.rim.vm.Array;

final class OTASyncProgressHandler implements SyncAgentListener, ActivationEventQueueCallback, GlobalEventListener {
   private LongHashtable _serviceProgress;
   private ApplicationEntryPoint _currentEntryPoint;
   private ApplicationEntryPoint _baseEntryPoint;
   private ApplicationEntryPoint _progressEntryPoint;
   private long _currentService = -1;
   private BigLongVector _queuedServices = (BigLongVector)(new Object());
   private Object _serviceLock = new Object();
   private final IconCollection _progressIcons = IconCollection.get("net_rim_Activation_Progress", 4);
   private int _progressIconIndex;
   private ActivationServiceImpl _activationService = (ActivationServiceImpl)ActivationService.getInstance();
   private String _lastTitle = "";
   private long[] _activationEventQueue = new long[0];
   private int[] _activationEventValueQueue = new int[0];
   private Object[] _activationEventDataObjectQueue = new Object[0];
   private String[] _titleFormattingString = new Object[1];
   private static final long ID;

   final void setSyncStatus(long serviceId, int state) {
      if (this._serviceProgress.containsKey(serviceId)) {
         this.getServiceProgress(serviceId).setSyncState(state);
      }
   }

   final long getCurrentService() {
      return this._currentService;
   }

   final int getPercentComplete() {
      synchronized (this._serviceLock) {
         OTASyncServiceProgress progress = this.getServiceProgress(this._currentService);
         return progress != null ? progress.getPercentComplete() : 100;
      }
   }

   final boolean isSyncInProgress() {
      synchronized (this._serviceLock) {
         OTASyncServiceProgress progress = this.getServiceProgress(this._currentService);
         return this.isSyncEnabled() && progress != null && this.getPercentComplete() != 100;
      }
   }

   final void updateProgress(int eventID, Object object) {
      boolean autoDisplayProgress = true;
      long sid = -1;
      ServiceRecord pimRecord = null;
      if (object instanceof Object) {
         object = ((SyncAgentStatistics)object).getSyncAgentUrl();
      }

      if (!(object instanceof Object)) {
         if (object instanceof Object) {
            SyncAgentUrl url = (SyncAgentUrl)object;
            sid = url.getSid();
            pimRecord = ServiceBook.getSB().getRecordByCidAndSid("sync", sid);
         }
      } else {
         sid = ((ServiceIdentifier)object).getSid();
         pimRecord = ((ServiceIdentifier)object).getServiceRecord();
      }

      if (this._currentService != sid
         || eventID == 18
         || eventID == 5
         || eventID == 2
         || this._activationService.isAnyTransactionInProgress()
         || this._activationService.hasServiceEverActivated(sid)) {
         autoDisplayProgress = false;
      }

      OTASyncServiceProgress serviceProgress = this.getServiceProgress(sid);
      boolean eaIconDisabled = false;
      if (ITPolicy.getBoolean(33, 10, false)) {
         autoDisplayProgress = false;
         eaIconDisabled = true;
      }

      int failedDatabases = 0;
      ActivationApp activationApp = ActivationApp.getInstance();
      this.clearActivationEventQueue();
      if (serviceProgress == null) {
         throw new Object();
      }

      synchronized (this._serviceLock) {
         synchronized (serviceProgress) {
            serviceProgress.collectSyncAgentStatistics();
            SyncAgentStatistics[] saStats = serviceProgress.getSyncAgentStatistics();
            int numDatabases = saStats.length;
            int numComplete = 0;
            int currentOverall = 0;
            int totalOverall = 0;
            boolean activationCompleted = false;
            boolean atLeastOneDBStarted = false;
            boolean failedOperationsForThisDatabase = false;
            int syncState = serviceProgress.getSyncState();
            int unit = 0;

            for (int i = 0; i < numDatabases; i++) {
               SyncAgentStatistics stats = saStats[i];
               int current = stats.getTotalNumberOfExecutedOperations();
               int total = stats.getTotalNumberOfOperations();
               if (stats.getTotalNumberOfFailedOperations() != 0) {
                  failedDatabases++;
                  failedOperationsForThisDatabase = true;
               }

               int complete = stats.getTotalNumberOf100PercentHits();
               if (complete >= 1) {
                  numComplete++;
                  if (complete > 1) {
                     continue;
                  }
               }

               if (total < 0) {
                  unit = 10;
               } else {
                  if (total > 0) {
                     unit = 10 + current * 100 / total * 9 / 10;
                  } else if (complete > 0) {
                     unit = 100;
                  }

                  atLeastOneDBStarted = true;
               }

               currentOverall += unit;
               totalOverall += total;
            }

            Object newIcon = null;
            String newTitle = null;
            boolean justCalendar = false;
            boolean calendarOnlyStat = saStats.length == 1 && "Calendar".equals(saStats[0].getSyncAgentUrl().getDatabaseName());
            if (pimRecord != null) {
               if (calendarOnlyStat) {
                  Configuration syncConfiguration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(sid);
                  if (syncConfiguration == null || !syncConfiguration.isUserEnabled() || !syncConfiguration.isUserPreferenceToSyncSet()) {
                     justCalendar = true;
                  }
               }
            } else {
               serviceProgress.setPimConfigRequestPending(false);
               justCalendar = true;
               if (this._currentService == -1 && calendarOnlyStat) {
                  synchronized (this._serviceLock) {
                     this._currentService = sid;
                  }
               }
            }

            if (sid == this._currentService) {
               this._currentEntryPoint = this._baseEntryPoint;
            }

            if (numDatabases > 0 && numComplete < numDatabases) {
               if (sid == this._currentService) {
                  this._currentEntryPoint = this._progressEntryPoint;
               }

               int percentComplete = 0;
               if (atLeastOneDBStarted && (!serviceProgress.getPimConfigRequestPending() || serviceProgress.getPimConfigRequestRecieved())) {
                  percentComplete = currentOverall / numDatabases;
               }

               if (serviceProgress.getPercentComplete() != percentComplete) {
                  if (sid == this._currentService) {
                     this._titleFormattingString[0] = Integer.toString(percentComplete);
                     newTitle = ((StringBuffer)(new Object()))
                        .append(MessageFormat.format(ActivationApp._resources.getString(159), this._titleFormattingString))
                        .append('%')
                        .toString();
                  }

                  serviceProgress.setPercentComplete(percentComplete);
               }

               if (sid == this._currentService && !eaIconDisabled) {
                  newIcon = this._progressIcons.getImage(this._progressIconIndex);
                  this._progressIconIndex = ++this._progressIconIndex % 4;
                  if (newIcon != null) {
                     this._progressEntryPoint.set(4, newIcon);
                  }
               }
            } else {
               serviceProgress.setPercentComplete(100);
               if (sid == this._currentService) {
                  this._progressIconIndex = 0;
               }

               if (saStats.length == 1) {
                  if (!justCalendar && serviceProgress.getPimConfigRequestPending()) {
                     serviceProgress.setPercentComplete(99);
                  }
               } else if (saStats.length == 0 && syncState == 0) {
                  if (serviceProgress.getPimConfigRequestPending()) {
                     serviceProgress.setPercentComplete(0);
                  }

                  CICALConfiguration calconfig = CICALConfiguration.getDefaultConfiguration();
                  if (calconfig.isOTACalendarEnabled() && calconfig.isOTASlowSyncSupported()) {
                     serviceProgress.setPercentComplete(0);
                  }
               }

               if (serviceProgress.getPercentComplete() == 100 && (eventID == 17 || eventID == 18 && syncState == 7 || eventID == 24)) {
                  if (sid == this._currentService) {
                     if (!failedOperationsForThisDatabase) {
                        this._titleFormattingString[0] = ActivationApp._resources.getString(104);
                     } else {
                        this._titleFormattingString[0] = ActivationApp._resources.getString(155);
                     }

                     newTitle = MessageFormat.format(ActivationApp._resources.getString(159), this._titleFormattingString);
                  }

                  activationCompleted = true;
               } else if (sid == this._currentService) {
                  newTitle = ActivationApp.getApplicationTitle(false);
               }
            }

            if (serviceProgress.getPercentComplete() != 100 && syncState == 0 && !ActivationApp.isRunning() && autoDisplayProgress) {
               ActivationApp.run(null);
            }

            if (activationApp == null) {
               activationApp = ActivationApp.getInstance();
            }

            if (syncState == 7) {
               newTitle = ActivationApp._resources.getString(146);
            } else if (syncState == 6) {
               newTitle = ActivationApp._resources.getString(150);
            }

            if (newTitle != null && StringUtilities.compareToIgnoreCase(newTitle, this._lastTitle) != 0) {
               this._lastTitle = newTitle;
               this._currentEntryPoint.set(3, newTitle);
               if (activationApp != null) {
                  this.addActivationEvent(-4731267519193158412L, 3848, new Object[]{newTitle, new Object(sid)});
               }
            }

            FastDormancyManager.getInstance().setFastDormancy(false);
            if (!activationCompleted) {
               if (this._activationService.displayIcon() && !eaIconDisabled && (autoDisplayProgress || ActivationApp.isRunning())) {
                  RibbonLauncher rlauncher = RibbonLauncher.getInstance();
                  if (rlauncher != null && activationApp != null) {
                     activationApp.activationEventOccurred(this, -4731267519193158412L, 0, 0, this._currentEntryPoint, rlauncher);
                  }
               }
            } else {
               FastDormancyManager.getInstance().setFastDormancy(true);
               if (syncState == 7 && numDatabases == 0) {
                  ActivationServiceImpl.logEvent(1094931544, 0);
                  serviceProgress.setSyncState(1);
                  if (activationApp != null) {
                     this.addActivationEvent(-4731267519193158412L, 3843, null);
                  }

                  this._activationService.clearActivationRecord(sid);
                  this._activationService.iconRefresh();
                  this.handleServiceCompletion(sid);
               } else if (syncState == 0 || syncState == 6) {
                  ActivationServiceImpl.logEvent(1094931523, 0);
                  this.handleServiceCompletion(sid);
                  if (ActivationService.getLastSuccessfulActivationDate(sid) <= 0) {
                     ActivationService.activationComplete(true, sid);
                     this._activationService.markServiceActivated(sid);
                     if (failedDatabases == 0 && !serviceProgress.hasOtherFailedDatabases()) {
                        if (activationApp != null) {
                           this.addActivationEvent(-4731267519193158412L, 3842, null);
                        }
                     } else if (activationApp != null) {
                        this.addActivationEvent(-4731267519193158412L, 3851, new Object(sid));
                     }
                  }

                  this._activationService.iconRefresh();
               }
            }

            switch (eventID) {
               case 16:
               case 18:
                  if (activationApp != null) {
                     this.addActivationEvent(ActivationApp.ACTIVATION_APP_SYNC_STATUS_CHANGED, 0, new Object(sid));
                  }
            }
         }
      }

      if (activationApp != null) {
         for (int i = 0; i < this._activationEventQueue.length; i++) {
            activationApp.activationEventOccurred(
               activationApp, this._activationEventQueue[i], this._activationEventValueQueue[i], 0, this._activationEventDataObjectQueue[i], null
            );
         }
      }
   }

   final String[] getIncompleteDatabases(long sid) {
      String[] databases = null;
      if (sid != -1) {
         int index = 0;
         OTASyncServiceProgress serviceProgress = this.getServiceProgress(sid);
         SyncAgentStatistics[] saStats = serviceProgress.getSyncAgentStatistics();

         for (int i = 0; i < saStats.length; i++) {
            if (saStats[i].getTotalNumberOfFailedOperations() != 0) {
               index++;
               if (databases == null) {
                  databases = new Object[1];
               } else {
                  Array.resize(databases, index);
               }

               SyncAgentUrl url = saStats[i].getSyncAgentUrl();
               databases[index - 1] = url.getDatabaseName();
            }
         }

         if (serviceProgress.hasOtherFailedDatabases()) {
            if (databases == null) {
               databases = new Object[0];
            }

            Arrays.append(databases, serviceProgress.getOtherFailedDatabases());
            serviceProgress.resetOtherFailedDatabases();
         }
      }

      return databases;
   }

   @Override
   public final void onEventFromActivationEventQueue(long guid, int data0, int data1, Object object0, Object object1) {
      ApplicationEntryPoint entryPoint = (ApplicationEntryPoint)object0;
      RibbonLauncher rlauncher = (RibbonLauncher)object1;
      if (rlauncher != null) {
         if (entryPoint != rlauncher.getRegisteredAction("net.rim.ActivationHomeScreenApp")) {
            rlauncher.registerAction("net.rim.ActivationHomeScreenApp", entryPoint);
            return;
         }

         rlauncher.updateRegisteredAction("net.rim.ActivationHomeScreenApp");
      }
   }

   @Override
   public final void onSyncAgentEvent(int eventID, Object object) {
      OTASyncServiceProgress serviceProgress = null;
      long serviceId = -1;
      if (object instanceof Object) {
         ServiceIdentifier identifier = (ServiceIdentifier)object;
         serviceId = identifier.getSid();
         serviceProgress = this.getServiceProgress(serviceId);
      }

      switch (eventID) {
         case 19:
            boolean isCalendarStat = false;
            int syncState = 4;
            if (object instanceof Object) {
               SyncAgentUrl url = (SyncAgentUrl)object;
               isCalendarStat = StringUtilities.compareObjectToStringIgnoreCase(url.getDatabaseName(), "Calendar", 1701707776) == 0;
               serviceProgress = this.getServiceProgress(url.getSid());
               syncState = serviceProgress.getSyncState();
            }

            if (!isCalendarStat) {
               if (syncState == 1 || syncState == 7 || syncState == 2 || syncState == 6) {
                  SyncManager.getInstance().enableOTASync(false);
               } else if (serviceProgress != null) {
                  serviceProgress.setSyncState(0);
               }
            }

            this.updateProgress(eventID, object);
            return;
         case 24:
            if (serviceProgress != null) {
               serviceProgress.setSyncState(6);
            }
         case 2:
            if (serviceProgress != null) {
               serviceProgress.setPimConfigRequestRecieved(false);
            }
         case 18:
            if (object instanceof Object) {
               SyncAgentStatistics stats = (SyncAgentStatistics)object;
               if (stats.removedDueToFailure()) {
                  this.getServiceProgress(stats.getSyncAgentUrl().getSid()).addOtherFailedDatabase(stats.getSyncAgentUrl().getDatabaseName());
               }
            }
         case 16:
         case 17:
         case 20:
            this.updateStatusListeners(object);
            this.updateProgress(eventID, object);
            return;
         case 25:
            if (serviceProgress != null) {
               serviceProgress.setSyncState(3);
               serviceProgress.setPimConfigRequestPending(true);
               return;
            }
            break;
         case 26:
            if (serviceProgress != null) {
               serviceProgress.setPimConfigRequestPending(false);
               serviceProgress.setPimConfigRequestRecieved(true);
               if (ITPolicy.getBoolean(33, 6, false)) {
                  ActivationApp activationApp = ActivationApp.getInstance();
                  if (activationApp != null) {
                     activationApp.activationEventOccurred(activationApp, -4731267519193158412L, 1398166104, 0, null, null);
                     return;
                  }
               }
            }
            break;
         case 28:
            if (serviceProgress != null) {
               serviceProgress.setSyncState(0);
               synchronized (this._serviceLock) {
                  if (this._currentService == -1) {
                     this._currentService = serviceId;
                  } else if (this._queuedServices.firstIndexOf(serviceId) == -1) {
                     this._queuedServices.addElement(serviceId);
                  }

                  return;
               }
            }
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8288627527798139133L) {
         ServiceRecord sr = this.getServiceRecordFromObject(object0);
         if (sr != null) {
            String cid = sr.getCid();
            if (cid.equalsIgnoreCase("sync") || cid.equalsIgnoreCase("cical")) {
               long sid = ServiceIdentifier.createSid(sr);
               int srType = sr.getType();
               if (srType != 2 && srType != 6) {
                  if (!this._serviceProgress.containsKey(sid)) {
                     ServiceRecord oldSr = this.getServiceRecordFromObject(object1);
                     if (oldSr != null && sr.isDuplicate(oldSr, -1, null, null, null, null, -1) && !sr.getUid().equalsIgnoreCase(oldSr.getUid())) {
                        this.reMapServiceIds(ServiceIdentifier.createSid(oldSr), sid);
                     }
                  }
               } else if (!this.isServiceViable(sid)) {
                  this.handleServiceCompletion(sid);
                  this.purgeService(sid);
                  ActivationService.activationComplete(false, sid);
                  return;
               }
            }
         }
      }
   }

   private final void addActivationEvent(long eventID, int eventType, Object objectData) {
      Array.resize(this._activationEventQueue, this._activationEventQueue.length + 1);
      Array.resize(this._activationEventValueQueue, this._activationEventValueQueue.length + 1);
      Array.resize(this._activationEventDataObjectQueue, this._activationEventDataObjectQueue.length + 1);
      this._activationEventQueue[this._activationEventQueue.length - 1] = eventID;
      this._activationEventValueQueue[this._activationEventValueQueue.length - 1] = eventType;
      this._activationEventDataObjectQueue[this._activationEventDataObjectQueue.length - 1] = objectData;
   }

   static final void register() {
      OTASyncProgressHandler handler = new OTASyncProgressHandler();
      ApplicationRegistry.getApplicationRegistry().put(4874767306658954404L, handler);
      SyncAgent.getSingletonInstance().registerListener(handler);
   }

   OTASyncProgressHandler() {
      this._serviceProgress = OTASyncServiceProgress.loadServicesProgressFromPersistence();
      ApplicationDescriptor base = ApplicationDescriptor.currentApplicationDescriptor();
      ApplicationDescriptor newDescriptor = (ApplicationDescriptor)(new Object(
         base, base.getName(), null, base.getIcon(), base.getPosition(), null, 0, base.getFlags()
      ));
      this._baseEntryPoint = (ApplicationEntryPoint)(new Object(newDescriptor));
      this._baseEntryPoint.set(9, "net_rim_bb_activation.ActivationApp");
      this._baseEntryPoint.set(1, "net_rim_bb_activation.ActivationApp");
      this._progressEntryPoint = (ApplicationEntryPoint)(new Object(newDescriptor));
      this._progressEntryPoint.set(9, "net_rim_bb_activation.ActivationApp");
      this._progressEntryPoint.set(1, "net_rim_bb_activation.ActivationApp");
      this._currentEntryPoint = this._baseEntryPoint;
      Proxy.getInstance().addGlobalEventListener(this);
   }

   static final OTASyncProgressHandler getInstance() {
      return (OTASyncProgressHandler)ApplicationRegistry.getApplicationRegistry().waitFor(4874767306658954404L);
   }

   private final boolean isSyncEnabled() {
      ServicesConfigurationManager configurationManager = ServicesConfigurationManager.getSingletonInstance();
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("sync");
      ServiceRecord record = null;
      Configuration syncConfiguration = null;

      for (int i = records.length - 1; i >= 0; i--) {
         record = records[i];
         if (record != null) {
            syncConfiguration = configurationManager.getConfiguration(ServiceIdentifier.createSid(record));
            if (syncConfiguration != null && syncConfiguration.isUserEnabled() && syncConfiguration.isUserPreferenceToSyncSet()) {
               return true;
            }
         }
      }

      records = ServiceBook.getSB().findRecordsByCid("cical");
      CalendarServiceManager calManager = CalendarServiceManager.getInstance();
      if (calManager != null) {
         for (int i = records.length - 1; i >= 0; i--) {
            CalendarService calService = calManager.findCalendarService(ServiceIdentifier.createSid(records[i]));
            if (calService != null) {
               CICALConfiguration calConfig = calService.getCICALConfiguration();
               if (calConfig != null && calConfig.isOTACalendarEnabled()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private final void updateStatusListeners(Object object) {
      if (object instanceof Object) {
         SyncAgentStatistics saStats = (SyncAgentStatistics)object;
         int current = saStats.getTotalNumberOfExecutedOperations();
         int total = saStats.getTotalNumberOfOperations();
         if (saStats.getTotalNumberOf100PercentHits() > 0 || current == total) {
            this._activationService.notifyActivationStatusListener(saStats.getSyncAgentUrl().getDatabaseName(), saStats.getTotalNumberOfFailedOperations() > 0);
         }
      }
   }

   private final OTASyncServiceProgress getServiceProgress(long sid) {
      OTASyncServiceProgress progress = (OTASyncServiceProgress)this._serviceProgress.get(sid);
      if (progress == null && sid != -1) {
         progress = new OTASyncServiceProgress(sid);
         this._serviceProgress.put(sid, progress);
      }

      return progress;
   }

   private final void handleServiceCompletion(long sid) {
      synchronized (this._serviceLock) {
         if (sid == this._currentService) {
            if (this._queuedServices.isEmpty()) {
               this._currentService = -1;
            } else {
               this._currentService = this._queuedServices.elementAt(0);
               this._queuedServices.removeElementAt(0);
            }
         } else {
            int index = this._queuedServices.firstIndexOf(sid);
            if (index >= 0) {
               this._queuedServices.removeElementAt(index);
            }
         }
      }
   }

   private final void purgeService(long sid) {
      synchronized (this._serviceLock) {
         OTASyncServiceProgress progress = (OTASyncServiceProgress)this._serviceProgress.get(sid);
         if (progress != null) {
            this._serviceProgress.remove(sid);
            progress.purgeState();
         }
      }
   }

   private final void clearActivationEventQueue() {
      Array.resize(this._activationEventQueue, 0);
      Array.resize(this._activationEventValueQueue, 0);
      Array.resize(this._activationEventDataObjectQueue, 0);
   }

   private final boolean isServiceViable(long sid) {
      ServiceBook sb = ServiceBook.getSB();
      return sb.getRecordByCidAndSid("sync", sid) != null || sb.getRecordByCidAndSid("cical", sid) != null;
   }

   private final ServiceRecord getServiceRecordFromObject(Object object) {
      return (ServiceRecord)(!(object instanceof Object) ? null : object);
   }

   private final void reMapServiceIds(long oldSid, long newSid) {
      synchronized (this._serviceLock) {
         if (this._currentService != -1 || !this._serviceProgress.isEmpty() || !this._queuedServices.isEmpty()) {
            if (this._currentService == oldSid) {
               this._currentService = newSid;
            }

            if (this._serviceProgress.containsKey(oldSid)) {
               Object tempObject = this._serviceProgress.get(oldSid);
               this._serviceProgress.remove(oldSid);
               this._serviceProgress.put(newSid, tempObject);
            }

            int index = this._queuedServices.firstIndexOf(oldSid);
            if (index >= 0) {
               this._queuedServices.setElementAt(newSid, index);
            }

            ActivationServiceImpl.serviceIdUpdated(oldSid, newSid);
         }
      }
   }
}
