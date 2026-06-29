package net.rim.device.apps.internal.browser.core;

import java.util.Vector;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.internal.browser.page.PageModel;

final class PendingRequestThread {
   private PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(-4718791899384625364L);
   private PendingRequestThread$PendingRequestStore _store;
   private PendingRequestThread$RunThread _pendingRequestThread;
   private static final long PENDING_REQUEST_STORE_KEY = -4718791899384625364L;
   private static PendingRequestThread _instance;

   private PendingRequestThread() {
      Object contents = this._persistentObject.getContents();
      if (contents instanceof PendingRequestThread$PendingRequestStore) {
         this._store = (PendingRequestThread$PendingRequestStore)contents;
      } else {
         this._store = new PendingRequestThread$PendingRequestStore();
         this._persistentObject.setContents(this._store, 51);
         this._persistentObject.commit();
      }

      EventLogger.logEvent(1907089860548946979L, 1148220019, 5);
   }

   public static final PendingRequestThread getInstance() {
      if (_instance == null) {
         _instance = new PendingRequestThread();
      }

      return _instance;
   }

   public final Vector getPendingRequests() {
      return this._store._pendingRequests;
   }

   public final Vector getSavedPendingRequests() {
      return this._store._savedPendingRequests;
   }

   public final void addPendingRequest(PageModel savedPageModel) {
      this.addPendingRequest(savedPageModel, null);
   }

   public final void addPendingRequest(PageModel savedPageModel, PendingRequestListener pendingRequestListener) {
      synchronized (this._store._pendingRequests) {
         this._store._pendingRequests.addElement(savedPageModel);
         this._store._pendingRequestListeners.addElement(pendingRequestListener);
         this._persistentObject.commit();
         EventLogger.logEvent(1907089860548946979L, 1148220014, 5);
         this.kickPendingRequests();
      }
   }

   public final void addSavedPendingRequest(PageModel savedPageModel) {
      synchronized (this._store._savedPendingRequests) {
         this._store._savedPendingRequests.addElement(savedPageModel);
         this._persistentObject.commit();
      }
   }

   public final void kickPendingRequests() {
      synchronized (this._store._pendingRequests) {
         if (this._store._pendingRequests.size() > 0) {
            Object ticket = PersistentContent.getTicket();
            if (this._pendingRequestThread == null
               && ticket != null
               && (BrowserDaemonRegistry.getInstance().getInCoverage() || ServiceRouting.getInstance().isSerialBypassActive())) {
               this._pendingRequestThread = new PendingRequestThread$RunThread(this, ticket);
               this._pendingRequestThread.start();
            }

            this._store._pendingRequests.notify();
         }
      }
   }
}
