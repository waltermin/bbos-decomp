package net.rim.wica.runtime.authentication.internal.credentialstore;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.LongHashtable;
import net.rim.vm.Array;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.persistence.Recryptable;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.util.Lock;

public class CredentialStore implements PersistentContentListener, EventListener {
   private LifecycleService _lifecycle;
   private Hashtable _credentials;
   private LongHashtable _locks;
   private net.rim.wica.runtime.persistence.CredentialStore _store;
   static Class class$net$rim$wica$runtime$lifecycle$LifecycleService;
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;
   static Class class$net$rim$wica$runtime$event$EventService;

   public CredentialStore(ServiceProvider provider) {
      this._lifecycle = (LifecycleService)provider.getService(
         class$net$rim$wica$runtime$lifecycle$LifecycleService == null
            ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
            : class$net$rim$wica$runtime$lifecycle$LifecycleService
      );
      PersistenceService persistence = (PersistenceService)provider.getService(
         class$net$rim$wica$runtime$persistence$PersistenceService == null
            ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
            : class$net$rim$wica$runtime$persistence$PersistenceService
      );
      this._store = persistence.getCredentialStore();
      EventService eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      eventService.addListener(new int[]{803, 805, 51, -804991603, 67324752, 10, 1645346816, 14051}, this);
      this._credentials = (Hashtable)(new Object());
      this._locks = (LongHashtable)(new Object());
      PersistentContent.addListener(this);
   }

   public Object getAccessTicket() {
      return PersistentContent.getTicket();
   }

   public Credentials getCredentials(long wicletId, String url) {
      String key = this.getKey(wicletId, url);
      Credentials credentials = (Credentials)this._credentials.get(key);
      if (credentials != null) {
         return credentials;
      }

      Lock lock = this.getLock(wicletId);
      lock.acquire();
      credentials = this.persistentCacheFind(wicletId, url);
      if (credentials != null) {
         credentials = this.copyToTransientCache(wicletId, url, credentials);
      }

      lock.release();
      return credentials;
   }

   public Credentials removeCredentials(long wicletId, String url) {
      Lock lock = this.getLock(wicletId);
      lock.acquire();
      Credentials credentials = (Credentials)this._credentials.remove(this.getKey(wicletId, url));
      Credentials persistentCredentials = this.persistentCacheRemove(wicletId, url);
      lock.release();
      return credentials != null ? credentials : persistentCredentials;
   }

   public void storeCredentials(long wicletId, String url, int scheme, String username, String domain, byte[] secretToken, boolean persist) {
      if (url != null && username != null && secretToken != null) {
         Wiclet wiclet = this._lifecycle.getWiclet(wicletId);
         if (wiclet != null) {
            Lock lock = this.getLock(wicletId);
            lock.acquire();
            if (wiclet.getExecutionState() == 2) {
               TransientCredentials credentials = new TransientCredentials(wicletId, scheme, username, domain, secretToken);
               this._credentials.put(this.getKey(wicletId, url), credentials);
            }

            if (persist) {
               PersistentCredentials credentials = new PersistentCredentials(url, scheme, username, domain, secretToken);
               this.persistentCacheStore(wicletId, url, credentials);
            } else {
               this.persistentCacheRemove(wicletId, url);
            }

            lock.release();
         }
      } else {
         throw new Object();
      }
   }

   private Credentials persistentCacheFind(long wicletId, String url) {
      PersistentCredentials[] persisted = (PersistentCredentials[])this._store.loadCredentials(wicletId);
      if (persisted == null) {
         return null;
      }

      int index = this.find(url, persisted);
      return index < 0 ? null : persisted[index];
   }

   private Credentials persistentCacheRemove(long wicletId, String url) {
      PersistentCredentials[] persisted = (PersistentCredentials[])this._store.loadCredentialsForWrite(wicletId);
      if (persisted == null) {
         return null;
      }

      int index = this.find(url, persisted);
      if (index < 0) {
         return null;
      }

      Credentials credentials = persisted[index];
      int length = persisted.length;
      if (++index < length) {
         System.arraycopy(persisted, index, persisted, index - 1, length - index);
      }

      Array.resize(persisted, index - 1);
      this._store.storeCredentials(wicletId, persisted);
      return credentials;
   }

   private void persistentCacheStore(long wicletId, String url, PersistentCredentials credentials) {
      PersistentCredentials[] persisted = (PersistentCredentials[])this._store.loadCredentialsForWrite(wicletId);
      if (persisted == null) {
         persisted = new PersistentCredentials[]{credentials};
      } else {
         int index = this.find(url, persisted);
         if (index < 0) {
            int length = persisted.length;
            Array.resize(persisted, length + 1);
            persisted[length] = credentials;
         } else {
            persisted[index] = credentials;
         }
      }

      this._store.storeCredentials(wicletId, persisted);
   }

   private Credentials copyToTransientCache(long wicletId, String url, Credentials credentials) {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null && PersistentContent.getState() == 1) {
         Wiclet wiclet = this._lifecycle.getWiclet(wicletId);
         if (wiclet != null && wiclet.getExecutionState() == 2) {
            credentials = new TransientCredentials(
               wicletId, credentials.getScheme(), credentials.getUsername(), credentials.getDomain(), credentials.getSecretToken()
            );
            this._credentials.put(this.getKey(wicletId, url), credentials);
         }
      }

      return credentials;
   }

   public void clearCredentials(boolean clearPersisted) {
      Wiclet[] wiclets = this._lifecycle.getWiclets();
      this.acquireLocks(wiclets);
      this._credentials.clear();
      if (clearPersisted) {
         this._store.deleteAllCredentials();
      }

      this.releaseLocks(wiclets);
   }

   public void clearCredentials(long wicletId, boolean clearPersisted) {
      Lock lock = this.getLock(wicletId);
      lock.acquire();
      Vector toRemove = (Vector)(new Object(4));
      synchronized (this._credentials) {
         Enumeration keys = this._credentials.keys();
         Enumeration elements = this._credentials.elements();

         while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            TransientCredentials credentials = (TransientCredentials)elements.nextElement();
            if (credentials.getWicletId() == wicletId) {
               toRemove.addElement(key);
            }
         }
      }

      for (int var13 = toRemove.size() - 1; var13 >= 0; var13--) {
         this._credentials.remove(toRemove.elementAt(var13));
      }

      if (clearPersisted) {
         this._store.deleteCredentials(wicletId);
      }

      lock.release();
   }

   @Override
   public void persistentContentStateChanged(int state) {
      if (state == 2) {
         Wiclet[] wiclets = this._lifecycle.getWiclets();
         this.acquireLocks(wiclets);
         this._credentials.clear();
         this.releaseLocks(wiclets);
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      if (generation == PersistentContent.getModeGeneration()) {
         Wiclet[] wiclets = this._lifecycle.getWiclets();
         this.acquireLocks(wiclets);
         Enumeration elements = this._credentials.elements();

         while (elements.hasMoreElements()) {
            ((Recryptable)elements.nextElement()).recrypt();
         }

         for (int i = wiclets.length - 1; i >= 0; i--) {
            PersistentCredentials[] persisted = (PersistentCredentials[])this._store.loadCredentialsForWrite(wiclets[i].getId());
            if (persisted != null) {
               for (int j = persisted.length - 1; j >= 0; j--) {
                  persisted[j].recrypt();
               }

               this._store.storeCredentials(wiclets[i].getId(), persisted);
            }
         }

         this.releaseLocks(wiclets);
      }
   }

   private int find(String url, PersistentCredentials[] persisted) {
      for (int i = persisted.length - 1; i >= 0; i--) {
         if (url.equals(persisted[i].getUrl())) {
            return i;
         }
      }

      return -1;
   }

   private String getKey(long wicletId, String url) {
      return ((StringBuffer)(new Object(url))).append(wicletId).toString();
   }

   private Lock getLock(long wicletId) {
      synchronized (this._locks) {
         if (!this._locks.containsKey(wicletId)) {
            Lock lock = new Lock();
            this._locks.put(wicletId, lock);
            return lock;
         } else {
            return (Lock)this._locks.get(wicletId);
         }
      }
   }

   private void acquireLocks(Wiclet[] wiclets) {
      for (int i = wiclets.length - 1; i >= 0; i--) {
         this.getLock(wiclets[i].getId()).acquire();
      }
   }

   private void releaseLocks(Wiclet[] wiclets) {
      for (int i = wiclets.length - 1; i >= 0; i--) {
         this.getLock(wiclets[i].getId()).release();
      }
   }

   @Override
   public void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 803:
            this.clearCredentials(((Wiclet)data).getId(), false);
            return;
         case 805:
            long wicletId = ((Wiclet)data).getId();
            this.clearCredentials(wicletId, true);
            this._locks.remove(wicletId);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
