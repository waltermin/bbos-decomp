package net.rim.device.apps.api.ribbon.indicators;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntIntHashtable;

public class NewMessageEventManager {
   private IntIntHashtable _unreadEventFlags = (IntIntHashtable)(new Object());
   private NewMessageEventListener[] _newMessageEventListeners;
   public static final int NEW_MESSAGE;
   public static final int MISSED_CALL;
   private static final long NEW_MESSAGE_EVENT_MANAGER;
   private static NewMessageEventManager _newMessageEventManager;

   private NewMessageEventManager() {
   }

   public static void addFlag(int newEventFlag, int addressCardUID) {
      _newMessageEventManager.internalAddFlag(newEventFlag, addressCardUID);
   }

   private void internalAddFlag(int newEventFlag, int addressCardUID) {
      if (newEventFlag != 0 && addressCardUID > 0) {
         this._unreadEventFlags.put(addressCardUID, this.internalGetFlags(addressCardUID) | newEventFlag);
         this.notifyListeners(addressCardUID);
      }
   }

   public static boolean removeFlags(int addressCardUID) {
      return _newMessageEventManager.internalRemoveFlags(addressCardUID);
   }

   private boolean internalRemoveFlags(int addressCardUID) {
      if (addressCardUID > 0) {
         int removedIndex = this._unreadEventFlags.remove(addressCardUID);
         if (removedIndex != -1) {
            this.notifyListeners(addressCardUID);
            return true;
         }
      }

      return false;
   }

   private void notifyListeners(int uid) {
      if (this._newMessageEventListeners != null) {
         synchronized (this._newMessageEventListeners) {
            for (int i = 0; i < this._newMessageEventListeners.length; i++) {
               if (uid > 0) {
                  this._newMessageEventListeners[i].flagsChanged(uid);
               } else {
                  this._newMessageEventListeners[i].flagsDeleted();
               }
            }
         }
      }
   }

   public static void addListener(NewMessageEventListener nel) {
      _newMessageEventManager.internalAddListener(nel);
   }

   private void internalAddListener(NewMessageEventListener nel) {
      if (this._newMessageEventListeners == null) {
         this._newMessageEventListeners = new NewMessageEventListener[0];
      }

      synchronized (this._newMessageEventListeners) {
         if (!Arrays.contains(this._newMessageEventListeners, nel)) {
            Arrays.add(this._newMessageEventListeners, nel);
         }
      }
   }

   public static void removeListener(NewMessageEventListener nel) {
      _newMessageEventManager.internalRemoveListener(nel);
   }

   private void internalRemoveListener(NewMessageEventListener nel) {
      synchronized (this._newMessageEventListeners) {
         Arrays.remove(this._newMessageEventListeners, nel);
      }
   }

   public static void purgeFlags() {
      _newMessageEventManager.internalPurgeFlags();
   }

   private void internalPurgeFlags() {
      this._unreadEventFlags.clear();
      this.notifyListeners(0);
   }

   public static int getFlags(int addressCardUID) {
      return _newMessageEventManager.internalGetFlags(addressCardUID);
   }

   private int internalGetFlags(int addressCardUID) {
      return this._unreadEventFlags.containsKey(addressCardUID) ? this._unreadEventFlags.get(addressCardUID) : 0;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _newMessageEventManager = (NewMessageEventManager)ar.getOrWaitFor(-7306523617215506685L);
      if (_newMessageEventManager == null) {
         _newMessageEventManager = new NewMessageEventManager();
         ar.put(-7306523617215506685L, _newMessageEventManager);
      }
   }
}
