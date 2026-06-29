package net.rim.device.api.listener;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class EventListenerManager {
   private Vector _listeners = new Vector();
   private Vector _applications = new Vector();
   private Vector _eventNotifications = new Vector();
   private Thread _eventNotificationsThread;
   private boolean _eventNotificationsThreadRunning;
   private Application _proxy = Proxy.getInstance();
   private WeakReference _proxyWR = new WeakReference(this._proxy);
   private int _proxyProcessID = this._proxy.getProcessId();

   public synchronized void add(Object listener, boolean weakListener) {
      this.remove(listener);

      WeakReference applicationWR;
      try {
         applicationWR = new WeakReference(Application.getApplication());
      } catch (IllegalStateException e) {
         applicationWR = this._proxyWR;
      }

      this._listeners.addElement(weakListener ? new WeakReference(listener) : listener);
      this._applications.addElement(applicationWR);
   }

   public synchronized boolean isListener(Object listener) {
      for (int i = this._listeners.size() - 1; i >= 0; i--) {
         Object element = this._listeners.elementAt(i);
         if (element instanceof WeakReference) {
            element = ((WeakReference)element).get();
         }

         if (element == listener) {
            return true;
         }
      }

      return false;
   }

   public synchronized void remove(Object listener) {
      for (int i = this._listeners.size() - 1; i >= 0; i--) {
         Object element = this._listeners.elementAt(i);
         if (element instanceof WeakReference) {
            element = ((WeakReference)element).get();
         }

         if (element == null || element == listener) {
            this._listeners.removeElementAt(i);
            this._applications.removeElementAt(i);
         }
      }
   }

   public synchronized Object[] getListeners(Object[] array) {
      int size = this._listeners.size();
      Array.resize(array, size);
      int num = 0;

      for (int i = 0; i < size; i++) {
         Object element = this._listeners.elementAt(i);
         if (element instanceof WeakReference) {
            element = ((WeakReference)element).get();
         }

         if (element != null) {
            array[num++] = element;
         }
      }

      if (num != size) {
         Array.resize(array, num);
      }

      return array;
   }

   public synchronized void update(Object listener, Event event) {
      if (event == null) {
         throw new IllegalArgumentException();
      }

      WeakReference callersApplicationWR;
      try {
         callersApplicationWR = new WeakReference(Application.getApplication());
      } catch (IllegalStateException e) {
         callersApplicationWR = this._proxyWR;
      }

      synchronized (this._eventNotifications) {
         this._eventNotifications.addElement(new EventListenerManager$PreEventNotification(callersApplicationWR, event));
         this._eventNotifications.addElement(new EventListenerManager$MainEventNotification(callersApplicationWR, listener, event));
         this._eventNotifications.addElement(new EventListenerManager$PostEventNotification(callersApplicationWR, event));
         this.startNotificationThread();
      }
   }

   public synchronized void update(Event event) {
      if (event == null) {
         throw new IllegalArgumentException();
      }

      WeakReference callersApplicationWR;
      try {
         callersApplicationWR = new WeakReference(Application.getApplication());
      } catch (IllegalStateException e) {
         callersApplicationWR = this._proxyWR;
      }

      this.remove(null);
      synchronized (this._eventNotifications) {
         this._eventNotifications.addElement(new EventListenerManager$PreEventNotification(callersApplicationWR, event));

         for (int i = this._listeners.size() - 1; i >= 0; i--) {
            WeakReference listenersApplicationWR = (WeakReference)this._applications.elementAt(i);
            Object listener = this._listeners.elementAt(i);
            this._eventNotifications.addElement(new EventListenerManager$MainEventNotification(listenersApplicationWR, listener, event));
         }

         this._eventNotifications.addElement(new EventListenerManager$PostEventNotification(callersApplicationWR, event));
         this.startNotificationThread();
      }
   }

   private synchronized void startNotificationThread() {
      if (!this._eventNotificationsThreadRunning) {
         new EventListenerManager$EventNotificationsThreadLauncher(this, null).run();
         this._eventNotificationsThreadRunning = true;
      }
   }

   public synchronized boolean isUpdateComplete() {
      return !this._eventNotificationsThreadRunning;
   }
}
