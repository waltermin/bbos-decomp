package net.rim.plazmic.internal.mediaengine.event;

import java.util.Enumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.plazmic.internal.mediaengine.service.EventSubscription;
import net.rim.plazmic.internal.mediaengine.util.MEUtilities;
import net.rim.plazmic.internal.mediaengine.util.SafeArray;
import net.rim.plazmic.mediaengine.MediaListener;

public class EventSubscriptionHelper implements EventSubscription {
   final long ALL_EVENTS = Long.MIN_VALUE;
   LongHashtable _listenerTable = new LongHashtable();

   public void dispatchEvent(Event ev) {
      this.dispatchEvent(Long.MIN_VALUE, ev);
      long key = (long)ev._event << 32 | 4294967295L;
      this.dispatchEvent(key, ev);
      if (ev._eventParam != -1) {
         key = (long)ev._event << 32 | ev._eventParam & 4294967295L;
         this.dispatchEvent(key, ev);
      }
   }

   public void dispatchEvent(Object sender, int event, int eventParam, Object data) {
      this.dispatchEvent(Long.MIN_VALUE, sender, event, eventParam, data);
      long key = (long)event << 32 | 4294967295L;
      this.dispatchEvent(key, sender, event, eventParam, data);
      if (eventParam != -1) {
         key = (long)event << 32 | eventParam & 4294967295L;
         this.dispatchEvent(key, sender, event, eventParam, data);
      }
   }

   @Override
   public void addListener(int event, int eventParam, MediaListener listener) {
      long key = (long)event << 32 | eventParam & 4294967295L;
      this.addListener(key, listener);
   }

   @Override
   public void removeListener(MediaListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("Listener can not be null");
      }

      Enumeration enumeration = this._listenerTable.elements();

      while (enumeration.hasMoreElements()) {
         SafeArray listeners = (SafeArray)enumeration.nextElement();
         if (listeners != null) {
            listeners.remove(listener);
         }
      }
   }

   @Override
   public void addListener(int event, MediaListener listener) {
      long key = (long)event << 32 | 4294967295L;
      this.addListener(key, listener);
   }

   @Override
   public void addListener(MediaListener listener) {
      this.addListener(Long.MIN_VALUE, listener);
   }

   private void addListener(long key, MediaListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("Listener can not be null");
      }

      SafeArray listeners = (SafeArray)this._listenerTable.get(key);
      if (listeners == null) {
         listeners = new SafeArray(true);
         this._listenerTable.put(key, listeners);
      }

      listeners.add(listener);
   }

   private void dispatchEvent(long key, Event ev) {
      SafeArray listeners = (SafeArray)this._listenerTable.get(key);
      if (listeners != null) {
         MEUtilities.fireMediaEvent(ev._sender, listeners, ev._event, ev._eventParam, ev);
      }
   }

   private void dispatchEvent(long key, Object sender, int event, int eventParam, Object data) {
      SafeArray listeners = (SafeArray)this._listenerTable.get(key);
      if (listeners != null) {
         MEUtilities.fireMediaEvent(sender, listeners, event, eventParam, data);
      }
   }
}
