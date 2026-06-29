package net.rim.wica.runtime.persistence.internal;

import java.util.Vector;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.wica.runtime.persistence.PersistentContentHelper;

public class PersistentContentHelperImpl implements PersistentContentListener, PersistentContentHelper {
   private int _state;
   private boolean _waitingForTicket;
   private Vector _listeners = new Vector();

   public PersistentContentHelperImpl() {
      this.syncPersistentContentState();
   }

   private synchronized void syncPersistentContentState() {
      PersistentContent.addListener(this);
      this._state = PersistentContent.getState();
      this.scheduleWaitForTicket();
   }

   private synchronized void scheduleWaitForTicket() {
      if (!this._waitingForTicket && (this._state == 4 || this._state == 3)) {
         this._waitingForTicket = true;
         Thread thread = new Thread(new PersistentContentHelperImpl$WaitForTicket(this, null));
         thread.start();
      }
   }

   @Override
   public void addListener(PersistentContentListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException();
      }

      this._listeners.addElement(listener);
   }

   @Override
   public void removeListener(PersistentContentListener listener) {
      if (listener != null) {
         synchronized (this._listeners) {
            for (int i = this._listeners.size() - 1; i >= 0; i--) {
               if (this._listeners.elementAt(i) == listener) {
                  this._listeners.removeElementAt(i);
               }
            }
         }
      }
   }

   @Override
   public synchronized void persistentContentStateChanged(int state) {
      int currentState = PersistentContent.getState();
      if (this._state != currentState) {
         this._state = currentState;
         this.scheduleWaitForTicket();
         synchronized (this._listeners) {
            for (int i = this._listeners.size() - 1; i >= 0; i--) {
               ((PersistentContentListener)this._listeners.elementAt(i)).persistentContentStateChanged(this._state);
            }
         }
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      synchronized (this._listeners) {
         for (int i = this._listeners.size() - 1; i >= 0; i--) {
            ((PersistentContentListener)this._listeners.elementAt(i)).persistentContentModeChanged(generation);
         }
      }
   }
}
