package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableSet;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.utility.framework.RecurUtil;

class OTACalendarListener implements CollectionListener {
   private LongHashtable _transmissionServiceTable;
   private OTACalendarSyncDataManager _otaSyncDataManager = OTACalendarSyncDataManager.getInstance();
   private ContextObject _context;
   private CalendarServiceManager _calendarServiceManager = CalendarServiceManager.getInstance();
   private static final long ID;
   private static OTACalendarListener _instance;

   private OTACalendarListener() {
      this._transmissionServiceTable = (LongHashtable)(new Object(3));
      this._context = (ContextObject)(new Object());
   }

   public static OTACalendarListener getInstance() {
      if (_instance == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            _instance = (OTACalendarListener)reg.getOrWaitFor(5200073708061784973L);
            if (_instance == null) {
               _instance = new OTACalendarListener();
               reg.put(5200073708061784973L, _instance);
            }
         }

         init();
      }

      return _instance;
   }

   private static void init() {
      CalDB[] calDBs = CalendarServiceManager.getInstance().getCalendarDatabases(true);

      for (int i = 0; i < calDBs.length; i++) {
         calDBs[i].addCollectionListener(_instance);
      }
   }

   public void addTransmissionService(TransmissionService transmissionService) {
      OTACalendarTransmissionService calendarTransmissionService = (OTACalendarTransmissionService)transmissionService;
      CalendarService calendarService = (CalendarService)calendarTransmissionService.getServiceIdentifier();
      long serviceID = calendarService.getUniqueServiceID();
      this._transmissionServiceTable.put(serviceID, calendarTransmissionService);
   }

   public TransmissionService removeTransmissionService(long calendarServiceID) {
      return (TransmissionService)this._transmissionServiceTable.remove(calendarServiceID);
   }

   private TransmissionService getTransmissionService(CalendarService calendarService) {
      return (TransmissionService)this._transmissionServiceTable.get(calendarService.getUniqueServiceID());
   }

   @Override
   public void reset(Collection collection) {
      if (collection instanceof Object) {
         ReadableSet set = (ReadableSet)collection;
         if (set.size() == 0) {
            this._otaSyncDataManager.clear();
         }
      }
   }

   private void doSendCheck(Event param1, Event param2, CalendarService param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 3
      // 001: invokevirtual net/rim/device/apps/api/calendar/caldb/CalendarService.getCICALConfiguration ()Lnet/rim/device/apps/api/calendar/ota/CICALConfiguration;
      // 004: astore 4
      // 006: aload 1
      // 007: invokestatic net/rim/device/apps/api/calendar/modelcontrollerinterface/EventUtilities.doesEventHaveIntTimeOverflow (Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/Event;)Z
      // 00a: ifeq 00e
      // 00d: return
      // 00e: bipush 0
      // 00f: istore 5
      // 011: aload 0
      // 012: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._otaSyncDataManager Lnet/rim/device/apps/api/calendar/ota/OTACalendarSyncDataManager;
      // 015: aload 1
      // 016: invokevirtual net/rim/device/apps/api/sync/OTASyncDataManager.get (Lnet/rim/device/apps/api/sync/OTASyncIDProvider;)Lnet/rim/device/apps/api/sync/OTASyncData;
      // 019: astore 6
      // 01b: aload 6
      // 01d: ifnonnull 023
      // 020: goto 160
      // 023: aload 6
      // 025: invokevirtual net/rim/device/apps/api/sync/OTASyncData.isDirty ()Z
      // 028: ifne 02e
      // 02b: goto 160
      // 02e: aload 6
      // 030: aload 1
      // 031: invokestatic net/rim/device/apps/api/calendar/modelcontrollerinterface/EventUtilities.incrementDeviceSequence (Lnet/rim/device/apps/api/sync/OTASyncData;Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/Event;)V
      // 034: bipush 0
      // 035: istore 7
      // 037: aload 1
      // 038: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.isMeeting ()Z 1
      // 03d: ifne 043
      // 040: goto 112
      // 043: aload 4
      // 045: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isMeetingSyncEnabled ()Z
      // 048: ifne 04e
      // 04b: goto 112
      // 04e: aload 1
      // 04f: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getMeetingInfo ()Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/MeetingInfo; 1
      // 054: astore 8
      // 056: aload 8
      // 058: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/MeetingInfo.meetingCanBeModified ()Z 1
      // 05d: ifne 063
      // 060: goto 112
      // 063: aload 3
      // 064: aload 1
      // 065: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getLUID ()J 1
      // 06a: invokestatic net/rim/device/apps/internal/calendar/meeting/MeetingUtilities.getNotifyAttendees (Lnet/rim/device/apps/api/calendar/caldb/CalendarService;J)Z
      // 06d: ifne 073
      // 070: goto 108
      // 073: aload 4
      // 075: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isOutboundOTATrafficDisabled ()Z
      // 078: ifeq 07e
      // 07b: goto 108
      // 07e: aload 2
      // 07f: ifnonnull 085
      // 082: goto 0fc
      // 085: aload 2
      // 086: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.isRecurring ()Z 1
      // 08b: ifeq 0fc
      // 08e: aload 1
      // 08f: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.isRecurring ()Z 1
      // 094: ifeq 0fc
      // 097: aload 2
      // 098: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getRecurrenceCopy ()Lnet/rim/device/apps/api/framework/model/Recur; 1
      // 09d: astore 9
      // 09f: aload 1
      // 0a0: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getRecurrenceCopy ()Lnet/rim/device/apps/api/framework/model/Recur; 1
      // 0a5: astore 10
      // 0a7: aload 9
      // 0a9: aload 10
      // 0ab: invokestatic net/rim/device/apps/api/utility/framework/RecurUtil.getAddedExclusions (Lnet/rim/device/apps/api/framework/model/Recur;Lnet/rim/device/apps/api/framework/model/Recur;)[J
      // 0ae: astore 11
      // 0b0: aload 11
      // 0b2: arraylength
      // 0b3: ifle 0fc
      // 0b6: aload 4
      // 0b8: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.supportsRecurrenceOptimization ()Z
      // 0bb: ifeq 0fc
      // 0be: bipush 0
      // 0bf: istore 12
      // 0c1: iload 12
      // 0c3: aload 11
      // 0c5: arraylength
      // 0c6: if_icmpge 0fc
      // 0c9: aload 0
      // 0ca: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._context Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0cd: ldc2_w -1184541483416107193
      // 0d0: new java/lang/Object
      // 0d3: dup
      // 0d4: aload 11
      // 0d6: iload 12
      // 0d8: laload
      // 0d9: invokespecial java/lang/Long.<init> (J)V
      // 0dc: invokestatic net/rim/device/apps/api/framework/model/ContextObject.put (Ljava/lang/Object;JLjava/lang/Object;)Ljava/lang/Object;
      // 0df: pop
      // 0e0: aload 1
      // 0e1: aload 0
      // 0e2: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._context Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0e5: invokestatic net/rim/device/apps/internal/calendar/ota/CICALMeetingEmailer.sendMeetingCancellation (Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/Event;Lnet/rim/device/apps/api/framework/model/ContextObject;)V
      // 0e8: aload 0
      // 0e9: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._context Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0ec: ldc2_w -1184541483416107193
      // 0ef: invokestatic net/rim/device/apps/api/framework/model/ContextObject.remove (Ljava/lang/Object;J)Ljava/lang/Object;
      // 0f2: pop
      // 0f3: bipush 1
      // 0f4: istore 7
      // 0f6: iinc 12 1
      // 0f9: goto 0c1
      // 0fc: iload 7
      // 0fe: ifne 108
      // 101: aload 1
      // 102: invokestatic net/rim/device/apps/internal/calendar/ota/CICALMeetingEmailer.sendMeetingRequest (Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/Event;)V
      // 105: bipush 1
      // 106: istore 7
      // 108: aload 3
      // 109: aload 1
      // 10a: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getLUID ()J 1
      // 10f: invokestatic net/rim/device/apps/internal/calendar/meeting/MeetingUtilities.clearNotifyAttendees (Lnet/rim/device/apps/api/calendar/caldb/CalendarService;J)V
      // 112: iload 7
      // 114: ifne 140
      // 117: aload 4
      // 119: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isSendSyncEnabled ()Z
      // 11c: ifeq 140
      // 11f: aload 4
      // 121: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isOutboundOTATrafficDisabled ()Z
      // 124: ifne 140
      // 127: aload 0
      // 128: aload 3
      // 129: invokespecial net/rim/device/apps/internal/calendar/ota/OTACalendarListener.getTransmissionService (Lnet/rim/device/apps/api/calendar/caldb/CalendarService;)Lnet/rim/device/apps/api/transmission/TransmissionService;
      // 12c: ldc_w "net.rim.RIMCalendarApptUpdate"
      // 12f: aload 1
      // 130: aconst_null
      // 131: bipush 0
      // 132: aload 0
      // 133: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._context Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 136: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.transmitObject (Ljava/lang/String;Ljava/lang/Object;Lnet/rim/device/apps/api/transmission/TransmissionStatusListener;ILjava/lang/Object;)V 6
      // 13b: aload 6
      // 13d: invokevirtual net/rim/device/apps/api/sync/OTASyncData.markClean ()V
      // 140: aload 0
      // 141: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._otaSyncDataManager Lnet/rim/device/apps/api/calendar/ota/OTACalendarSyncDataManager;
      // 144: aload 1
      // 145: aload 6
      // 147: invokevirtual net/rim/device/apps/api/sync/OTASyncDataManager.add (Lnet/rim/device/apps/api/sync/OTASyncIDProvider;Lnet/rim/device/apps/api/sync/OTASyncData;)V
      // 14a: return
      // 14b: astore 5
      // 14d: ldc_w 1397966405
      // 150: bipush 2
      // 152: invokestatic net/rim/device/apps/api/calendar/ota/CICALEventLogger.logEvent (II)V
      // 155: return
      // 156: astore 5
      // 158: ldc_w 1398031941
      // 15b: bipush 2
      // 15d: invokestatic net/rim/device/apps/api/calendar/ota/CICALEventLogger.logEvent (II)V
      // 160: return
      // try (7 -> 141): 142 null
      // try (7 -> 141): 147 null
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      if (element instanceof Object) {
         Event event = (Event)element;
         CalendarService calendarService = this._calendarServiceManager.findCalendarService(event);
         this.doSendCheck(event, null, calendarService);
      }
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (newElement instanceof Object) {
         Event newEvent = (Event)newElement;
         OTASyncData syncData = null;
         Event oldEvent = null;
         CalendarService calendarService = this._calendarServiceManager.findCalendarService(newEvent);
         CICALConfiguration cicalConfiguration = calendarService.getCICALConfiguration();
         if (oldElement instanceof Object) {
            oldEvent = (Event)oldElement;
            syncData = this._otaSyncDataManager.get(oldEvent);
            this._otaSyncDataManager.remove(oldEvent);
         }

         if (syncData != null && this._otaSyncDataManager.get(newEvent) == null) {
            this._otaSyncDataManager.add(newEvent, syncData);
         }

         if (syncData != null && syncData.isDirty() && oldEvent != null && oldEvent.isRecurring() && newEvent.isRecurring()) {
            Recur oldRecur = oldEvent.getRecurrenceCopy();
            Recur newRecur = newEvent.getRecurrenceCopy();
            long[] instancesToDelete = RecurUtil.getRemovedOccurrences(oldRecur, newRecur);
            if (instancesToDelete.length > 0) {
               if (cicalConfiguration.supportsRecurrenceOptimization()) {
                  for (int i = 0; i < instancesToDelete.length; i++) {
                     this.optimizedRemoved(collection, newElement, instancesToDelete[i], calendarService);
                  }

                  return;
               }

               this._context.put(-8188970212168295222L, instancesToDelete);
            }
         }

         this.doSendCheck(newEvent, oldEvent, calendarService);
      }
   }

   private void optimizedRemoved(Collection param1, Object param2, long param3, CalendarService param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 5
      // 002: invokevirtual net/rim/device/apps/api/calendar/caldb/CalendarService.getCICALConfiguration ()Lnet/rim/device/apps/api/calendar/ota/CICALConfiguration;
      // 005: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isSendSyncEnabled ()Z
      // 008: ifne 00c
      // 00b: return
      // 00c: aload 2
      // 00d: dup
      // 00e: instanceof java/lang/Object
      // 011: ifne 018
      // 014: pop
      // 015: goto 10b
      // 018: checkcast java/lang/Object
      // 01b: astore 6
      // 01d: aload 5
      // 01f: invokevirtual net/rim/device/apps/api/calendar/caldb/CalendarService.getCICALConfiguration ()Lnet/rim/device/apps/api/calendar/ota/CICALConfiguration;
      // 022: astore 7
      // 024: aload 0
      // 025: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._otaSyncDataManager Lnet/rim/device/apps/api/calendar/ota/OTACalendarSyncDataManager;
      // 028: aload 6
      // 02a: invokevirtual net/rim/device/apps/api/sync/OTASyncDataManager.get (Lnet/rim/device/apps/api/sync/OTASyncIDProvider;)Lnet/rim/device/apps/api/sync/OTASyncData;
      // 02d: astore 8
      // 02f: aload 8
      // 031: ifnonnull 037
      // 034: goto 10b
      // 037: bipush 0
      // 038: istore 9
      // 03a: aload 6
      // 03c: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getAllDayFlag ()Z 1
      // 041: ifeq 057
      // 044: aload 6
      // 046: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getTimeZoneID ()Ljava/lang/String; 1
      // 04b: invokestatic java/util/TimeZone.getTimeZone (Ljava/lang/String;)Ljava/util/TimeZone;
      // 04e: astore 10
      // 050: lload 3
      // 051: aload 10
      // 053: invokestatic net/rim/device/apps/api/calendar/modelcontrollerinterface/EventUtilities.adjustAllDayDate (JLjava/util/TimeZone;)J
      // 056: lstore 3
      // 057: aload 0
      // 058: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._context Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 05b: ldc2_w -1184541483416107193
      // 05e: new java/lang/Object
      // 061: dup
      // 062: lload 3
      // 063: invokespecial java/lang/Long.<init> (J)V
      // 066: invokestatic net/rim/device/apps/api/framework/model/ContextObject.put (Ljava/lang/Object;JLjava/lang/Object;)Ljava/lang/Object;
      // 069: pop
      // 06a: aload 6
      // 06c: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.isMeeting ()Z 1
      // 071: ifeq 0c3
      // 074: aload 7
      // 076: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isMeetingSyncEnabled ()Z
      // 079: ifeq 0c3
      // 07c: aload 7
      // 07e: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isOutboundOTATrafficDisabled ()Z
      // 081: ifne 0c3
      // 084: aload 6
      // 086: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getMeetingInfo ()Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/MeetingInfo; 1
      // 08b: astore 10
      // 08d: aload 10
      // 08f: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/MeetingInfo.meetingCanBeModified ()Z 1
      // 094: ifeq 0c3
      // 097: aload 5
      // 099: aload 6
      // 09b: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getLUID ()J 1
      // 0a0: invokestatic net/rim/device/apps/internal/calendar/meeting/MeetingUtilities.getNotifyAttendees (Lnet/rim/device/apps/api/calendar/caldb/CalendarService;J)Z
      // 0a3: ifeq 0b7
      // 0a6: aload 6
      // 0a8: aload 0
      // 0a9: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._context Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0ac: invokestatic net/rim/device/apps/internal/calendar/ota/CICALMeetingEmailer.sendMeetingCancellation (Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/Event;Lnet/rim/device/apps/api/framework/model/ContextObject;)V
      // 0af: bipush 1
      // 0b0: istore 9
      // 0b2: aload 8
      // 0b4: invokevirtual net/rim/device/apps/api/sync/OTASyncData.markClean ()V
      // 0b7: aload 5
      // 0b9: aload 6
      // 0bb: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getLUID ()J 1
      // 0c0: invokestatic net/rim/device/apps/internal/calendar/meeting/MeetingUtilities.clearNotifyAttendees (Lnet/rim/device/apps/api/calendar/caldb/CalendarService;J)V
      // 0c3: iload 9
      // 0c5: ifne 100
      // 0c8: aload 7
      // 0ca: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isOutboundOTATrafficDisabled ()Z
      // 0cd: ifne 100
      // 0d0: aload 0
      // 0d1: aload 5
      // 0d3: invokespecial net/rim/device/apps/internal/calendar/ota/OTACalendarListener.getTransmissionService (Lnet/rim/device/apps/api/calendar/caldb/CalendarService;)Lnet/rim/device/apps/api/transmission/TransmissionService;
      // 0d6: ldc_w "net.rim.RIMCalendarApptDelete"
      // 0d9: aload 6
      // 0db: aconst_null
      // 0dc: bipush 0
      // 0dd: aload 0
      // 0de: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._context Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0e1: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.transmitObject (Ljava/lang/String;Ljava/lang/Object;Lnet/rim/device/apps/api/transmission/TransmissionStatusListener;ILjava/lang/Object;)V 6
      // 0e6: goto 100
      // 0e9: astore 9
      // 0eb: ldc_w 1397966405
      // 0ee: bipush 2
      // 0f0: invokestatic net/rim/device/apps/api/calendar/ota/CICALEventLogger.logEvent (II)V
      // 0f3: goto 100
      // 0f6: astore 9
      // 0f8: ldc_w 1398031941
      // 0fb: bipush 2
      // 0fd: invokestatic net/rim/device/apps/api/calendar/ota/CICALEventLogger.logEvent (II)V
      // 100: aload 0
      // 101: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._context Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 104: ldc2_w -1184541483416107193
      // 107: invokestatic net/rim/device/apps/api/framework/model/ContextObject.remove (Ljava/lang/Object;J)Ljava/lang/Object;
      // 10a: pop
      // 10b: return
      // try (24 -> 93): 94 null
      // try (24 -> 93): 99 null
   }

   @Override
   public void elementRemoved(Collection param1, Object param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 2
      // 001: dup
      // 002: instanceof java/lang/Object
      // 005: ifne 00c
      // 008: pop
      // 009: goto 10d
      // 00c: checkcast java/lang/Object
      // 00f: astore 3
      // 010: aload 0
      // 011: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._calendarServiceManager Lnet/rim/device/apps/api/calendar/caldb/CalendarServiceManager;
      // 014: aload 3
      // 015: invokevirtual net/rim/device/apps/api/calendar/caldb/CalendarServiceManager.findCalendarService (Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/Event;)Lnet/rim/device/apps/api/calendar/caldb/CalendarService;
      // 018: astore 4
      // 01a: aload 4
      // 01c: invokevirtual net/rim/device/apps/api/calendar/caldb/CalendarService.getCICALConfiguration ()Lnet/rim/device/apps/api/calendar/ota/CICALConfiguration;
      // 01f: astore 5
      // 021: aload 4
      // 023: invokevirtual net/rim/device/apps/api/calendar/caldb/CalendarService.getCalendarDatabase ()Lnet/rim/device/apps/api/calendar/caldb/CalDB;
      // 026: astore 6
      // 028: aload 0
      // 029: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._otaSyncDataManager Lnet/rim/device/apps/api/calendar/ota/OTACalendarSyncDataManager;
      // 02c: aload 3
      // 02d: invokevirtual net/rim/device/apps/api/sync/OTASyncDataManager.get (Lnet/rim/device/apps/api/sync/OTASyncIDProvider;)Lnet/rim/device/apps/api/sync/OTASyncData;
      // 030: astore 7
      // 032: aload 7
      // 034: ifnonnull 03a
      // 037: goto 10d
      // 03a: aload 0
      // 03b: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._context Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 03e: ldc2_w -1184541483416107193
      // 041: invokestatic net/rim/device/apps/api/framework/model/ContextObject.remove (Ljava/lang/Object;J)Ljava/lang/Object;
      // 044: pop
      // 045: bipush 0
      // 046: istore 8
      // 048: aload 3
      // 049: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.isMeeting ()Z 1
      // 04e: ifeq 0c5
      // 051: aload 5
      // 053: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isMeetingSyncEnabled ()Z
      // 056: ifeq 0c5
      // 059: aload 3
      // 05a: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getMeetingInfo ()Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/MeetingInfo; 1
      // 05f: astore 9
      // 061: aload 9
      // 063: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/MeetingInfo.meetingCanBeModified ()Z 1
      // 068: ifeq 0c5
      // 06b: aload 4
      // 06d: aload 3
      // 06e: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getLUID ()J 1
      // 073: invokestatic net/rim/device/apps/internal/calendar/meeting/MeetingUtilities.getNotifyAttendees (Lnet/rim/device/apps/api/calendar/caldb/CalendarService;J)Z
      // 076: ifeq 0ba
      // 079: aload 5
      // 07b: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isOutboundOTATrafficDisabled ()Z
      // 07e: ifne 0ba
      // 081: aload 6
      // 083: aload 3
      // 084: invokestatic net/rim/device/apps/api/calendar/modelcontrollerinterface/RecurUtilities.locateRelatedEvents (Lnet/rim/device/apps/api/calendar/caldb/CalDB;Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/Event;)[Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/Event;
      // 087: astore 10
      // 089: aload 10
      // 08b: ifnull 0af
      // 08e: aload 10
      // 090: arraylength
      // 091: bipush 1
      // 092: isub
      // 093: istore 11
      // 095: iload 11
      // 097: iflt 0af
      // 09a: aload 4
      // 09c: aload 10
      // 09e: iload 11
      // 0a0: aaload
      // 0a1: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getLUID ()J 1
      // 0a6: invokestatic net/rim/device/apps/internal/calendar/meeting/MeetingUtilities.dontNotifyAttendees (Lnet/rim/device/apps/api/calendar/caldb/CalendarService;J)V
      // 0a9: iinc 11 -1
      // 0ac: goto 095
      // 0af: aload 3
      // 0b0: aload 0
      // 0b1: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._context Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0b4: invokestatic net/rim/device/apps/internal/calendar/ota/CICALMeetingEmailer.sendMeetingCancellation (Lnet/rim/device/apps/api/calendar/modelcontrollerinterface/Event;Lnet/rim/device/apps/api/framework/model/ContextObject;)V
      // 0b7: bipush 1
      // 0b8: istore 8
      // 0ba: aload 4
      // 0bc: aload 3
      // 0bd: invokeinterface net/rim/device/apps/api/calendar/modelcontrollerinterface/Event.getLUID ()J 1
      // 0c2: invokestatic net/rim/device/apps/internal/calendar/meeting/MeetingUtilities.clearNotifyAttendees (Lnet/rim/device/apps/api/calendar/caldb/CalendarService;J)V
      // 0c5: iload 8
      // 0c7: ifne 0ef
      // 0ca: aload 5
      // 0cc: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isSendSyncEnabled ()Z
      // 0cf: ifeq 0ef
      // 0d2: aload 5
      // 0d4: invokevirtual net/rim/device/apps/api/calendar/ota/CICALConfiguration.isOutboundOTATrafficDisabled ()Z
      // 0d7: ifne 0ef
      // 0da: aload 0
      // 0db: aload 4
      // 0dd: invokespecial net/rim/device/apps/internal/calendar/ota/OTACalendarListener.getTransmissionService (Lnet/rim/device/apps/api/calendar/caldb/CalendarService;)Lnet/rim/device/apps/api/transmission/TransmissionService;
      // 0e0: ldc_w "net.rim.RIMCalendarApptDelete"
      // 0e3: aload 3
      // 0e4: aconst_null
      // 0e5: bipush 0
      // 0e6: aload 0
      // 0e7: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._context Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0ea: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.transmitObject (Ljava/lang/String;Ljava/lang/Object;Lnet/rim/device/apps/api/transmission/TransmissionStatusListener;ILjava/lang/Object;)V 6
      // 0ef: aload 0
      // 0f0: getfield net/rim/device/apps/internal/calendar/ota/OTACalendarListener._otaSyncDataManager Lnet/rim/device/apps/api/calendar/ota/OTACalendarSyncDataManager;
      // 0f3: aload 3
      // 0f4: invokevirtual net/rim/device/apps/api/sync/OTASyncDataManager.remove (Lnet/rim/device/apps/api/sync/OTASyncIDProvider;)V
      // 0f7: return
      // 0f8: astore 8
      // 0fa: ldc_w 1397966405
      // 0fd: bipush 2
      // 0ff: invokestatic net/rim/device/apps/api/calendar/ota/CICALEventLogger.logEvent (II)V
      // 102: return
      // 103: astore 8
      // 105: ldc_w 1398031941
      // 108: bipush 2
      // 10a: invokestatic net/rim/device/apps/api/calendar/ota/CICALEventLogger.logEvent (II)V
      // 10d: return
      // try (27 -> 107): 108 null
      // try (27 -> 107): 113 null
   }
}
