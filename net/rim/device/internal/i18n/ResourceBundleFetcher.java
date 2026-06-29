package net.rim.device.internal.i18n;

import net.rim.device.api.i18n.CompressedResourceBundle;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.system.RIMProcessLauncher;
import net.rim.device.internal.system.RIMProcessLauncher$ApplicationCallback;
import net.rim.device.resources.Resource$Internal;

public final class ResourceBundleFetcher implements RIMProcessLauncher$ApplicationCallback {
   private final ResourceBundleFetcher$CompressedResourceHashtable _bundleCache;
   private Application _fetcherAppRef = null;
   private Object _requestLock = new Object();
   private ResourceBundleFetcher$ResourceBundleFetcherRequestRunnable _requestRunnable = new ResourceBundleFetcher$ResourceBundleFetcherRequestRunnable(null);
   private static final long REGISTRY_NAME = 3190551698550597928L;
   private static ResourceBundleFetcher _instance;

   private ResourceBundleFetcher() {
      this._bundleCache = new ResourceBundleFetcher$CompressedResourceHashtable(null);
   }

   private static final ResourceBundleFetcher getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (ResourceBundleFetcher)ar.waitFor(3190551698550597928L);
      }

      return _instance;
   }

   public static final void initialize() {
      _instance = new ResourceBundleFetcher();
      ApplicationRegistry.getApplicationRegistry().put(3190551698550597928L, _instance);
      Locale.get(0);
      launchRequestProcess();
   }

   public static final void flushRequestData() {
      ResourceBundleFetcher fetcher = getInstance();
      synchronized (fetcher._requestLock) {
         Application app = fetcher._fetcherAppRef;
         fetcher._fetcherAppRef = null;
         app.invokeLater(new ResourceBundleFetcher$RequestFlushRunnable(null));
      }

      launchRequestProcess();
   }

   public static final ResourceBundle fetch(String name) {
      ResourceBundleFetcher fetcher = getInstance();
      synchronized (fetcher._requestLock) {
         if (fetcher._fetcherAppRef == null) {
            try {
               fetcher._requestLock.wait();
            } catch (InterruptedException var11) {
            }
         }

         Object obj = fetcher._bundleCache.get(name);
         int handle = 0;
         if (obj != null) {
            handle = (Integer)obj;
         }

         fetcher._requestRunnable.reset(name, handle);
         Object lock = fetcher._requestRunnable.getLock();

         ResourceBundle var10000;
         try {
            synchronized (lock) {
               fetcher._fetcherAppRef.invokeLater(fetcher._requestRunnable);
               lock.wait();
               var10000 = fetcher._requestRunnable.getResult();
            }
         } catch (InterruptedException var10) {
            return null;
         }

         return var10000;
      }
   }

   public static final boolean verifyCompressedResourcePresent(String name) {
      return getInstance()._bundleCache.isLoaded(name);
   }

   @Override
   public final void applicationStarted(Application application) {
      if (this._fetcherAppRef == null) {
         this._fetcherAppRef = application;
         synchronized (this._requestLock) {
            this._requestLock.notifyAll();
         }
      }
   }

   private static final void launchRequestProcess() {
      RIMProcessLauncher.launchApplication(_instance);
   }

   private static final ResourceBundle fetchResourceBundleInternal(String name, int module) {
      if (name == null) {
         throw new IllegalArgumentException();
      }

      byte[] data = null;
      if (module != 0) {
         data = Resource$Internal.getResource(name, module);
         if (data != null) {
            return CompressedResourceBundle.getResourceBundle(data);
         }
      }

      try {
         Class c = Class.forName(name);
         Object o = c.newInstance();
         return (ResourceBundle)o;
      } catch (ClassNotFoundException var6) {
         return null;
      } catch (IllegalAccessException var7) {
         return null;
      } catch (InstantiationException var8) {
         return null;
      }
   }
}
