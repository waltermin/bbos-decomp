package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.vm.WeakReference;

public final class ActiveMediaObservable {
   private ActiveMedia _user;
   private Object[] _listeners;
   private static final long ACTIVEMEDIAOBSERVABLE_GUID = -5866557420524450530L;

   private ActiveMediaObservable() {
   }

   private final synchronized void cleanupWeakRefs() {
      if (this._listeners != null) {
         for (int index = this._listeners.length - 1; index >= 0; index--) {
            ActiveMediaObserver listener = (ActiveMediaObserver)((WeakReference)this._listeners[index]).get();
            if (listener == null) {
               this._listeners = ListenerUtilities.removeListener(this._listeners, this._listeners[index]);
            }
         }
      }
   }

   public static final ActiveMediaObservable getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      ActiveMediaObservable observable = (ActiveMediaObservable)registry.getOrWaitFor(-5866557420524450530L);
      if (observable == null) {
         observable = new ActiveMediaObservable();
         registry.put(-5866557420524450530L, observable);
      }

      return observable;
   }

   public static final void setActive(ActiveMedia newUser) {
      ActiveMediaObservable observable = getInstance();
      ActiveMedia user;
      Object[] listeners;
      synchronized (observable) {
         if (observable._user != newUser) {
            user = observable._user;
            listeners = observable._listeners;
            observable._user = newUser;
         } else {
            user = null;
            listeners = null;
         }
      }

      if (listeners != null) {
         observable.notifyChange(listeners, user, newUser);
      }
   }

   public static final void setInactive(ActiveMedia fromUser) {
      ActiveMediaObservable observable = getInstance();
      ActiveMedia user;
      Object[] listeners;
      synchronized (observable) {
         if (observable._user == fromUser) {
            user = observable._user;
            listeners = observable._listeners;
            observable._user = null;
         } else {
            user = null;
            listeners = null;
         }
      }

      if (listeners != null) {
         observable.notifyChange(listeners, user, null);
      }
   }

   public static final ActiveMedia getActiveMedia() {
      ActiveMediaObservable observable = getInstance();
      return observable._user;
   }

   private final void notifyChange(Object[] listeners, ActiveMedia fromUser, ActiveMedia toUser) {
      if (listeners != null) {
         for (int index = listeners.length - 1; index >= 0; index--) {
            ActiveMediaObserver listener = (ActiveMediaObserver)((WeakReference)listeners[index]).get();
            if (listener != null) {
               try {
                  listener.onChanged(fromUser, toUser);
               } catch (RuntimeException var7) {
               }
            }
         }
      }
   }

   public static final void addListener(ActiveMediaObserver listener) {
      ActiveMediaObservable observable = getInstance();
      synchronized (observable) {
         observable._listeners = ListenerUtilities.addListener(observable._listeners, new WeakReference(listener));
         observable.cleanupWeakRefs();
      }
   }

   public static final void removeListener(ActiveMediaObserver listener) {
      ActiveMediaObservable observable = getInstance();
      synchronized (observable) {
         if (observable._listeners != null) {
            for (int index = observable._listeners.length - 1; index >= 0; index--) {
               if (((WeakReference)observable._listeners[index]).get() == listener) {
                  observable._listeners = ListenerUtilities.removeListener(observable._listeners, observable._listeners[index]);
                  break;
               }
            }
         }
      }
   }
}
