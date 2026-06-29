package net.rim.device.apps.internal.profiles;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.apps.api.framework.profiles.ProfileIDProvider;

final class NotificationsEngineImpl$Event {
   long _appId;
   private long _eventId;
   private Object _eventReference;
   private int _level;
   private int _trigger;
   private long _creationTime = System.currentTimeMillis();
   private long _expiryTime;

   NotificationsEngineImpl$Event(long appId, long eventId, Object eventReference, int level, int trigger, long expiryTime) {
      this._appId = appId;
      this._eventId = eventId;
      this._eventReference = eventReference;
      this._level = level;
      this._trigger = trigger;
      this._expiryTime = expiryTime;
   }

   final boolean isExpired() {
      return this._expiryTime - System.currentTimeMillis() <= 0;
   }

   final long getTimeToExpiration(long currentTime) {
      long exactRemainingTime = this._expiryTime - currentTime;
      return exactRemainingTime <= 0 ? -1 : exactRemainingTime;
   }

   final boolean isValid(long currentTime, long lastOutOfHolsterTime, boolean inHolster) {
      if (this._trigger == 0) {
         return inHolster ? this._expiryTime > currentTime : this._creationTime < lastOutOfHolsterTime && this._expiryTime > currentTime;
      } else {
         return true;
      }
   }

   final boolean belongsToApplication(long appId) {
      boolean result = appId == this._appId;
      if (!result && this._eventReference instanceof Object) {
         result = appId == ((ProfileIDProvider)this._eventReference).getProfileID();
      }

      return result;
   }

   final int getTrigger() {
      return this._trigger;
   }

   final int getLevel() {
      return this._level;
   }

   final void clear() {
      this._eventId = 0;
      this._eventReference = null;
   }

   final Object getEventReference() {
      return this._eventReference;
   }

   final long getEventId() {
      return this._eventId;
   }

   final void fireStateChanged(int state) {
      NotificationsManager.fireStateChanged(state, this._appId, this._eventId, this._eventReference, null);
   }

   final void proceed() {
      NotificationsManager.fireProceed(this._appId, this._eventId, this._eventReference, null);
   }

   final void defer() {
      NotificationsManager.fireDefer(this._appId, this._eventId, this._eventReference, null);
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof NotificationsEngineImpl$Event) {
         NotificationsEngineImpl$Event event = (NotificationsEngineImpl$Event)obj;
         if (this._appId == event._appId) {
            Object thisRef = this._eventReference;
            Object thatRef = event._eventReference;
            if (thisRef != null) {
               if (thatRef != null) {
                  return thisRef.equals(thatRef);
               }
            } else if (thatRef == null) {
               if (event._eventId == this._eventId) {
                  return true;
               }

               return false;
            }
         }
      }

      return false;
   }

   final boolean equals(long appId, long eventId, Object eventReference) {
      if (appId == this._appId) {
         Object thisRef = this._eventReference;
         return thisRef == null ? eventId == this._eventId : thisRef.equals(eventReference);
      } else {
         return false;
      }
   }
}
