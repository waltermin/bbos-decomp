package net.rim.device.apps.internal.profiles;

import net.rim.device.api.notification.Consequence;
import net.rim.device.api.notification.NotificationsEngine;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.internal.proxy.Proxy;

final class NotificationsEngineImpl extends Thread implements NotificationsEngine, HolsterListener {
   private Profiles _profiles;
   private NotificationsEngineImpl$EventQueue[] _queues;
   private NotificationsEngineImpl$Event _negotiatedEvent;
   private long _lastOutOfHolsterTime;
   private Runnable _guard;
   private int _guardInvokeId;
   private long _lastEventDate = -1;

   public NotificationsEngineImpl() {
      this._profiles = Profiles.getInstance();
      int numQueues = 8;
      this._queues = new NotificationsEngineImpl$EventQueue[numQueues];

      for (int i = numQueues - 1; i >= 0; i--) {
         this._queues[i] = new NotificationsEngineImpl$EventQueue(this);
      }

      this._lastOutOfHolsterTime = System.currentTimeMillis();
      this._guard = new NotificationsEngineImpl$NotificationsEngineGuard(this);
      this._guardInvokeId = -1;
   }

   private final void reactToImmediateEvent(
      long sourceIdLong, int levelIndexInt, long eventIdLong, Object eventReferenceObject, Profile aProfile, int actionInt, Object contextObject
   ) {
      ContextObject context = ContextObject.castOrCreate(contextObject);
      boolean originalDeviceHolstered = context.getFlag(67);
      if (!DeviceInfo.isInHolster()) {
         if (context.getFlag(37) && context.getFlag(66)) {
            return;
         }

         context.clearFlag(67);
      } else {
         context.setFlag(67);
      }

      context.put(-2832590917644170714L, new Integer(levelIndexInt));
      Object configuration = null;
      long consequenceId = -1;
      Consequence consequence = null;
      Profile profile = aProfile;
      String overrideTuneName = null;
      if (profile == null) {
         profile = this._profiles.getEnabled();
         if (actionInt == 0 && !this._profiles.isOffEnabled()) {
            Object obj = context.get(-7004855975111283545L);
            if (obj instanceof Integer) {
               int addresscardUID = (Integer)obj;
               Override override = Overrides.getInstance().getFirstOverrideContaining(addresscardUID);
               if (override != null) {
                  if ((sourceIdLong == 2868625504212929964L + VoiceServices.getCurrentLineId() || sourceIdLong == 3975384895524745189L)
                     && override.getUseTune()) {
                     overrideTuneName = override.getTuneName();
                     if (overrideTuneName != null) {
                        context.put(6476586477082074028L, overrideTuneName);
                     }
                  }

                  int profileUID = override.getProfileUID();
                  if (profileUID != -1) {
                     profile = Profiles.getInstance().getByUID(profileUID);
                  }
               }
            }
         }
      }

      if (profile != null) {
         LongEnumeration consequenceIds = NotificationsManager.enumerateConsequenceIds();

         while (consequenceIds.hasMoreElements()) {
            consequenceId = consequenceIds.nextElement();
            configuration = profile.getConfiguration(consequenceId, sourceIdLong);
            consequence = NotificationsManager.getConsequence(consequenceId);
            if (actionInt == 0) {
               consequence.startNotification(consequenceId, sourceIdLong, eventIdLong, configuration, context);
            } else {
               consequence.stopNotification(consequenceId, sourceIdLong, eventIdLong, configuration, context);
            }
         }
      }

      if (originalDeviceHolstered) {
         context.setFlag(67);
      } else {
         context.clearFlag(67);
      }
   }

   private final NotificationsEngineImpl$EventQueue getEventQueue(int level, int trigger) {
      return this._queues[level * 2 + trigger];
   }

   private final void addFirst(int level, int trigger, long appId, long eventId, Object eventReference, long expiryTime) {
      this.getEventQueue(level, trigger).insert(new NotificationsEngineImpl$Event(appId, eventId, eventReference, level, trigger, expiryTime));
   }

   private final NotificationsEngineImpl$Event getFirst(int trigger) {
      for (int i = 0; i < 4; i++) {
         NotificationsEngineImpl$Event event = this.getEventQueue(i, trigger).getFirst();
         if (event != null) {
            return event;
         }
      }

      return null;
   }

   private final void remove(NotificationsEngineImpl$Event event) {
      this.getEventQueue(event.getLevel(), event.getTrigger()).remove(event);
   }

   private final void removeAll(long appId) {
      for (int i = this._queues.length - 1; i >= 0; i--) {
         this._queues[i].removeAll(appId);
      }
   }

   private final void removeAll(int level, int trigger, long appId) {
      this.getEventQueue(level, trigger).removeAll(appId);
   }

   private final void removeAllExpired(long currentTime, long lastOutOfHolsterTime, boolean inHolster) {
      for (int i = this._queues.length - 1; i >= 0; i--) {
         this._queues[i].removeAllExpired(currentTime, lastOutOfHolsterTime, inHolster);
      }
   }

   private final void removeFirst(int level, int trigger, long appId, long eventId, Object eventReference) {
      this.getEventQueue(level, trigger).removeFirst(appId, eventId, eventReference);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void proceedWithNegotiatedEvent(NotificationsEngineImpl$Event event) {
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         if (this._negotiatedEvent != null) {
            if (this._negotiatedEvent.equals(event)) {
               EventLogger.logEvent(6982943375119825480L, 1146441797, 5);
               return;
            }

            this.deferNegotiatedEvent();
         }

         if (event != null) {
            this._negotiatedEvent = event;
            this._negotiatedEvent.proceed();
            return;
         }

         var4 = false;
      } finally {
         if (var4) {
            this.remove(event);
            return;
         }
      }
   }

   private final void deferNegotiatedEvent() {
      this._negotiatedEvent.defer();
      this._negotiatedEvent = null;
   }

   @Override
   public final void triggerImmediateEvent(
      long sourceIdLong, Object sourceObject, int levelIndexInt, long eventIdLong, Object eventReferenceObject, Object contextObject
   ) {
      this._lastEventDate = System.currentTimeMillis();
      this.reactToImmediateEvent(sourceIdLong, levelIndexInt, eventIdLong, eventReferenceObject, null, 0, contextObject);
   }

   @Override
   public final void cancelImmediateEvent(
      long sourceIdLong, Object sourceObject, int levelIndexInt, long eventIdLong, Object eventReferenceObject, Object contextObject
   ) {
      this.reactToImmediateEvent(sourceIdLong, levelIndexInt, eventIdLong, eventReferenceObject, null, 1, contextObject);
   }

   @Override
   public final boolean isImmediateEventOccuring(long sourceIdLong) {
      AlertConsequence consequence = (AlertConsequence)NotificationsManager.getConsequence(-2870941457036655797L);
      return consequence.isImmediateEventOccuring(sourceIdLong);
   }

   @Override
   public final void negotiateDeferredEvent(
      long sourceIdLong,
      Object sourceObject,
      int levelIndexInt,
      long eventIdLong,
      Object eventReferenceObject,
      long eventExpiryLong,
      int triggerIndexInt,
      Object contextObject
   ) {
      this._lastEventDate = System.currentTimeMillis();
      ContextObject context = ContextObject.castOrCreate(contextObject);
      if (levelIndexInt >= 4) {
         throw new IllegalArgumentException("LI");
      }

      if (triggerIndexInt > 2) {
         throw new IllegalArgumentException("TI");
      }

      if (triggerIndexInt == 0 && !DeviceInfo.isInHolster()) {
         context.put(4086083307293257364L, Boolean.FALSE);
         EventLogger.logEvent(6982943375119825480L, 1329744200, 5);
      } else {
         Profile profile = this._profiles.getEnabled();
         AlertConsequence consequence = (AlertConsequence)NotificationsManager.getConsequence(-2870941457036655797L);
         Object configurationObject = profile.getConfiguration(-2870941457036655797L, sourceIdLong);
         if (!consequence.shouldNegotiateDeferredEvent(sourceIdLong, configurationObject)) {
            context.put(4086083307293257364L, Boolean.FALSE);
            EventLogger.logEvent(6982943375119825480L, 1145392982, 5);
         } else if (consequence.isSensoryAlert(sourceIdLong, configurationObject, true) || triggerIndexInt != 0 || !DeviceInfo.isInHolster()) {
            synchronized (this._queues) {
               int levelIndexToUse = ContextObject.getIntegerData(contextObject, levelIndexInt);
               Long delegateAppIdLong = (Long)context.get(-442409970680484936L);
               long delegateAppId = delegateAppIdLong != null ? delegateAppIdLong : -1;
               if (context.getFlag(65)) {
                  this.removeAll(sourceIdLong);
               }

               if (delegateAppId != -1) {
                  sourceIdLong = delegateAppId;
               }

               this.addFirst(levelIndexToUse, triggerIndexInt, sourceIdLong, eventIdLong, eventReferenceObject, eventExpiryLong);
               this._queues.notify();
            }
         }
      }
   }

   @Override
   public final int getDeferredEventCount(long sourceIdLong) {
      int result = 0;
      synchronized (this._queues) {
         for (int i = 0; i < this._queues.length; i++) {
            NotificationsEngineImpl$EventQueue eq = this._queues[i];
            result += eq.getEventCountBySourceID(sourceIdLong);
         }

         return result;
      }
   }

   @Override
   public final Object[] getDeferredEvents(long sourceIdLong) {
      Object[] result = new Object[0];
      synchronized (this._queues) {
         for (int i = 0; i < this._queues.length; i++) {
            NotificationsEngineImpl$EventQueue eq = this._queues[i];
            eq.getEventReferencesBySourceID(sourceIdLong, result);
         }

         return result;
      }
   }

   @Override
   public final long[] getDeferredEventIds(long sourceIdLong) {
      long[] result = new long[0];
      synchronized (this._queues) {
         for (int i = 0; i < this._queues.length; i++) {
            NotificationsEngineImpl$EventQueue eq = this._queues[i];
            eq.getEventIdsBySourceID(sourceIdLong, result);
         }

         return result;
      }
   }

   @Override
   public final void cancelDeferredEvent(
      long sourceIdLong, Object sourceObject, int levelIndexInt, long eventIdLong, Object eventReferenceObject, int triggerIndexInt, Object contextObject
   ) {
      Long delegateAppIdLong = (Long)ContextObject.get(contextObject, -442409970680484936L);
      long delegateAppId = delegateAppIdLong != null ? delegateAppIdLong : -1;
      if (delegateAppId != -1) {
         sourceIdLong = delegateAppId;
      }

      synchronized (this._queues) {
         this.removeFirst(levelIndexInt, triggerIndexInt, sourceIdLong, eventIdLong, eventReferenceObject);
         this._queues.notify();
      }
   }

   @Override
   public final void cancelAllDeferredEvents(long sourceIdLong, Object sourceObject, int levelIndexInt, int triggerIndexInt, Object contextObject) {
      Long delegateAppIdLong = (Long)ContextObject.get(contextObject, -442409970680484936L);
      long delegateAppId = delegateAppIdLong != null ? delegateAppIdLong : -1;
      if (delegateAppId != -1) {
         sourceIdLong = delegateAppId;
      }

      synchronized (this._queues) {
         this.removeAll(levelIndexInt, triggerIndexInt, sourceIdLong);
         this._queues.notify();
      }
   }

   @Override
   public final void inHolster() {
   }

   @Override
   public final void outOfHolster() {
      this._lastOutOfHolsterTime = System.currentTimeMillis();
      synchronized (this._queues) {
         this._queues.notify();
      }
   }

   @Override
   public final void run() {
      NotificationsEngineImpl$Event eventToNegotiate = null;
      NotificationsEngineImpl$Event manualEvent = null;
      NotificationsEngineImpl$Event outOfHolsterEvent = null;
      boolean interrupted = false;
      boolean postOutOfHolster = false;
      Proxy proxy = Proxy.getInstance();

      while (true) {
         try {
            synchronized (this._queues) {
               do {
                  interrupted = false;

                  try {
                     this._queues.wait();
                  } finally {
                     ;
                  }
               } while (interrupted);

               if (this._guardInvokeId != -1) {
                  label222:
                  try {
                     proxy.cancelInvokeLater(this._guardInvokeId);
                  } finally {
                     break label222;
                  }

                  this._guardInvokeId = -1;
               }

               long currentTime = System.currentTimeMillis();
               boolean isInHolster = DeviceInfo.isInHolster();
               this.removeAllExpired(currentTime, this._lastOutOfHolsterTime, isInHolster);
               if (isInHolster) {
                  eventToNegotiate = this.getFirst(1);
               } else {
                  manualEvent = this.getFirst(1);
                  outOfHolsterEvent = this.getFirst(0);
                  eventToNegotiate = manualEvent;
                  if (outOfHolsterEvent != null && (manualEvent == null || manualEvent.getLevel() > outOfHolsterEvent.getLevel())) {
                     eventToNegotiate = outOfHolsterEvent;
                     postOutOfHolster = true;
                  }
               }

               long timeToExpiration;
               if (eventToNegotiate != null && (timeToExpiration = eventToNegotiate.getTimeToExpiration(currentTime)) > 0) {
                  this._guardInvokeId = proxy.invokeLater(this._guard, timeToExpiration, false);
                  if (this._guardInvokeId == -1) {
                     EventLogger.logEvent(6982943375119825480L, 1348891508, 2);
                  }
               }
            }

            this.proceedWithNegotiatedEvent(eventToNegotiate);
            if (postOutOfHolster) {
               if (eventToNegotiate != null) {
                  if (AlertConfiguration.supportsEnableBackLight(eventToNegotiate._appId)) {
                     Profile profile = this._profiles.getEnabled();
                     AlertConfiguration configuration = (AlertConfiguration)profile.getConfiguration(-2870941457036655797L, eventToNegotiate._appId);
                     if (configuration.getHolsterIndependentSetting(12) == 2) {
                        Backlight.enable(true);
                     }
                  }

                  eventToNegotiate.fireStateChanged(1);
               }

               postOutOfHolster = false;
            }
         } finally {
            continue;
         }
      }
   }

   @Override
   public final long getLastEventDate() {
      return this._lastEventDate;
   }
}
