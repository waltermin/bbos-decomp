package net.rim.device.apps.api.transmission;

import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.internal.io.TrafficLogger;
import net.rim.vm.Array;

class TransmissionServiceListenerManager {
   private TransmissionServiceListener[] _listeners;
   private int[] _priorities = new int[0];
   private TrafficLogger _tLogger;

   public TransmissionServiceListenerManager() {
      this._listeners = new TransmissionServiceListener[0];
   }

   public synchronized void addTransmissionServiceListener(TransmissionServiceListener aTransmissionServiceListener, int priorityInt) {
      int length = this._priorities.length;
      Array.resize(this._priorities, length + 1);
      Array.resize(this._listeners, length + 1);
      this._priorities[length] = priorityInt;
      this._listeners[length] = aTransmissionServiceListener;
      Arrays.sort(this._priorities, 0, length + 1, this._listeners);
   }

   public synchronized void removeTransmissionServiceListener(TransmissionServiceListener aTransmissionServiceListener) {
      int length = this._priorities.length;

      for (int index = 0; index < length; index++) {
         if (aTransmissionServiceListener == this._listeners[index]) {
            if (index < --length) {
               System.arraycopy(this._priorities, index + 1, this._priorities, index, length - index);
               System.arraycopy(this._listeners, index + 1, this._listeners, index, length - index);
            }

            Array.resize(this._priorities, length);
            Array.resize(this._listeners, length);
            return;
         }
      }
   }

   public synchronized boolean fireReceiveObject(TransmissionService aTransmissionService, Object transmissionObject, Object contextObject) {
      int length = this._priorities.length;

      for (int index = 0; index < length; index++) {
         if (this._listeners[index].receiveObject(aTransmissionService, transmissionObject, contextObject)) {
            if (this._tLogger != null) {
               Integer intObj = (Integer)ContextObject.get(contextObject, -8214296050944071630L);
               Object obj = ContextObject.get(contextObject, 1694473709785469504L);
               byte[] packet = null;
               if (obj instanceof byte[]) {
                  packet = (byte[])obj;
               }

               int size = intObj != null ? intObj : 0;
               this._tLogger.bytesReceived(this._listeners[index], 1, null, size, packet);
            }

            return true;
         }
      }

      return false;
   }

   public synchronized void fireStatusChanged(TransmissionService aTransmissionService, int statusInt, Object contextObject) {
      int length = this._priorities.length;

      for (int index = 0; index < length; index++) {
         this._listeners[index].statusChanged(aTransmissionService, statusInt, contextObject);
      }
   }

   public synchronized void clearOut() {
      Array.resize(this._priorities, 0);
      Array.resize(this._listeners, 0);
   }

   public boolean isEmpty() {
      return this._priorities.length == 0;
   }

   public void setTrafficLogger(TrafficLogger tLogger) {
      this._tLogger = tLogger;
   }
}
