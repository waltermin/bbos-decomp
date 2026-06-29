package javax.microedition.rms;

import net.rim.device.api.util.ListenerUtilities;

class RecordEventGenerator {
   protected Object[] _listeners;

   public synchronized void addRecordListener(RecordListener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   public synchronized void removeRecordListener(RecordListener listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   void loadRecordIDs(int[] _1) {
      throw null;
   }

   protected final void notifyRecordAdded(RecordStore recordStore, int recordId) {
      Object[] listeners = this._listeners;
      if (listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            try {
               RecordListener listener = (RecordListener)listeners[i];
               listener.recordAdded(recordStore, recordId);
            } catch (Throwable var6) {
            }
         }
      }
   }

   protected final void notifyRecordChanged(RecordStore recordStore, int recordId) {
      Object[] listeners = this._listeners;
      if (listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            try {
               RecordListener listener = (RecordListener)listeners[i];
               listener.recordChanged(recordStore, recordId);
            } catch (Throwable var6) {
            }
         }
      }
   }

   protected final void notifyRecordDeleted(RecordStore recordStore, int recordId) {
      Object[] listeners = this._listeners;
      if (listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            try {
               RecordListener listener = (RecordListener)listeners[i];
               listener.recordDeleted(recordStore, recordId);
            } catch (Throwable var6) {
            }
         }
      }
   }
}
