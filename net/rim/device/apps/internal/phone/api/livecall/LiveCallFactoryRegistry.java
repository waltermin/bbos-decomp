package net.rim.device.apps.internal.phone.api.livecall;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;

public class LiveCallFactoryRegistry {
   private Vector _factories;
   private LiveCallFactory _defaultFactory;
   private LiveCall _createdCall;
   private static final long GUID = -7549607856025806600L;
   private static LiveCallFactoryRegistry _instance;

   public static LiveCallFactoryRegistry getRegistry() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         synchronized (ar) {
            _instance = (LiveCallFactoryRegistry)ar.get(-7549607856025806600L);
            if (_instance == null) {
               _instance = new LiveCallFactoryRegistry();
               ar.put(-7549607856025806600L, _instance);
            }
         }
      }

      return _instance;
   }

   public LiveCall createLiveCall(PhoneCallInitialData data, Object context) {
      LiveCall call = this._createdCall = null;
      if (this._factories != null) {
         for (int i = this._factories.size() - 1; i >= 0; i--) {
            LiveCallFactory factory = (LiveCallFactory)this._factories.elementAt(i);
            Application app = factory.getApplication();
            if (app != null) {
               PhoneCallInitialData localData = data;
               Object localContext = context;
               app.invokeLater(new LiveCallFactoryRegistry$1(this, factory, localData, localContext));
               synchronized (this) {
                  if (this._createdCall == null) {
                     try {
                        this.wait(3000);
                     } finally {
                        continue;
                     }
                  }
               }
            } else {
               this._createdCall = factory.createInstance(data, context);
            }
         }

         if (this._createdCall != null) {
            return this._createdCall;
         }
      }

      if (this._defaultFactory != null) {
         call = this._defaultFactory.createInstance(data, context);
      }

      return call;
   }

   public synchronized void setDefaultFactory(LiveCallFactory f) {
      if (f != null) {
         this._defaultFactory = f;
      }
   }

   public synchronized void addFactory(LiveCallFactory f) {
      if (f != null) {
         if (this._factories == null) {
            this._factories = (Vector)(new Object());
         }

         this._factories.addElement(f);
      }
   }

   public synchronized void removeFactory(LiveCallFactory f) {
      if (this._factories != null) {
         this._factories.removeElement(f);
      }
   }
}
