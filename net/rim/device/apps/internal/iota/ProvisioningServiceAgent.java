package net.rim.device.apps.internal.iota;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.utility.URL;
import net.rim.vm.Array;

public final class ProvisioningServiceAgent extends UiApplication {
   private MMCProcessor _theProcessor;
   private PersistentObject _persist;
   private Hashtable _storage;
   private Hashtable _tempStorage;
   private IOTARequestBuffer _buffer;
   private boolean _sessionInProgress = false;
   private IOTAStatusDialog _dialog;
   private static final long KEY = -7688435971308426807L;
   public static final int CIIP_PROVISION = 0;
   public static final int CISP_PROVISION = 1;
   public static final int NIP_PROVISION = 2;
   static ResourceBundle _resources = ResourceBundle.getBundle(1200788073156220994L, "net.rim.device.apps.internal.resource.IOTA");

   private ProvisioningServiceAgent() {
      EventLogger.register(4411276428801970910L, "net.rim.iota", 2);
      EventLogger.logEvent(4411276428801970910L, 1347633513);
      this._persist = PersistentStore.getPersistentObject(-7688435971308426807L);
      this._storage = (Hashtable)this._persist.getContents();
      if (this._storage == null) {
         this._storage = new Hashtable();
         this._storage.put("phone:boot.url", "http://hcmci.iota.spcsdns.net:8080/ciip");
         this._storage.put("phone:boot.naiurl", "http://hcmci.iota.spcsdns.net:8080/cisp");
         this._storage.put("browser:domain.trusted", "https:.iota.spcsdns.net");
         this._storage.put("phone:proxy..0.address..0.wdp", "c4000050441c1f01");
         this._storage.put("phone:cdma.prl", new byte[0]);
         this._persist.setContents(this._storage, 51);
         this._persist.commit();
      }

      new IOTAPushlet(this);
      IOTAManagerImpl.register(this);
      this._buffer = new IOTARequestBuffer();
      this._theProcessor = new MMCProcessor(this, this._buffer);
      this._theProcessor.start();
      this._dialog = new IOTAStatusDialog(_resources.getString(5), this);
   }

   public static final void logEvents(String message) {
      EventLogger.logEvent(4411276428801970910L, message.getBytes(), 0);
   }

   public final synchronized boolean initiateSession(int mode, String url) {
      if (!this.isModeValid(mode)) {
         return false;
      }

      EventLogger.logEvent(4411276428801970910L, 1349743408 | mode);
      switch (mode) {
         case -1:
            break;
         case 0:
         case 1:
         default:
            url = mode == 0 ? "phone:boot.url" : "phone:boot.naiurl";
            url = (String)this._storage.get(url);
            break;
         case 2:
            if (!this.trustURL(url)) {
               EventLogger.logEvent(4411276428801970910L, 1349416309, 2);
               return false;
            }
      }

      return this.bufferRequest(mode, url);
   }

   private final boolean bufferRequest(int mode, String url) {
      this._buffer.addRequest(new IOTARequest(mode, url));
      return true;
   }

   public final boolean isModeValid(int mode) {
      return mode >= 0 && mode <= 2;
   }

   final boolean trustURL(String urlStr) {
      if (urlStr != null) {
         try {
            URL url = new URL(urlStr);
            String domainTrusted = (String)this._storage.get("browser:domain.trusted");
            if (domainTrusted == null) {
               return true;
            }

            String scheme = url.getScheme();
            int colon = domainTrusted.indexOf(58);
            if (colon <= 0) {
               if (!"http".equals(scheme) && !"https".equals(scheme)) {
                  return false;
               }
            } else if (!StringUtilities.strEqualIgnoreCase(domainTrusted.substring(0, colon), scheme, 1701707776)) {
               return false;
            }

            String host = url.getHost();
            if (host != null) {
               host = StringUtilities.toLowerCase(host, 1701707776);
               String trustedHost = StringUtilities.toLowerCase(domainTrusted.substring(colon + 1), 1701707776);
               if (trustedHost.length() > 0 && trustedHost.charAt(0) == '.') {
                  return host.endsWith(trustedHost) || trustedHost.length() == host.length() + 1 && trustedHost.endsWith(host);
               }

               return trustedHost.equals(host);
            }
         } finally {
            return false;
         }
      }

      return false;
   }

   public final void setBusy(boolean flag) {
      this._sessionInProgress = flag;
      if (!flag) {
         UiApplication.getUiApplication().invokeLater(new ProvisioningServiceAgent$1(this));
      }
   }

   @Override
   protected final boolean acceptsForeground() {
      return false;
   }

   public final boolean cancelIOTASession() {
      EventLogger.logEvent(4411276428801970910L, 1348690549);
      return this._theProcessor.cancelProcessing();
   }

   public final int currentStatus() {
      IOTARequest req = this._theProcessor.getCurrentRequest();
      if (req == null) {
         return 0;
      } else {
         int mode = req.getMode();
         if (1 != mode && 0 != mode) {
            return this._sessionInProgress ? 1 : 0;
         } else {
            return this._sessionInProgress ? 3 : 2;
         }
      }
   }

   public final boolean writeToStorage(String key, Object value) {
      return this.writeToStorage(key, value, -1);
   }

   public final boolean writeToStorage(String key, Object value, int offset) {
      if (!this.contains(key)) {
         return false;
      }

      synchronized (this._tempStorage) {
         if (offset >= 0) {
            Object existingObject = this._tempStorage.get(key);
            if (!(existingObject instanceof byte[])) {
               this._tempStorage.put(key, value);
            } else {
               byte[] existingData = (byte[])existingObject;
               byte[] newData = (byte[])value;
               if (offset + newData.length > existingData.length) {
                  Array.resize(existingData, offset + newData.length);
               }

               System.arraycopy(newData, 0, existingData, offset, newData.length);
            }
         } else {
            this._tempStorage.put(key, value);
         }

         this._tempStorage.notify();
         return true;
      }
   }

   public final boolean contains(String object) {
      boolean result = false;
      if (this._tempStorage != null) {
         result = this._tempStorage.containsKey(object);
      }

      if (this._storage != null) {
         result |= this._storage.containsKey(object);
      }

      return result;
   }

   public final Object readFromStorage(String key) {
      synchronized (this._tempStorage) {
         Object o = this._tempStorage.get(key);
         this._tempStorage.notify();
         return o;
      }
   }

   public final boolean commitStorage() {
      if (this._tempStorage != null && this._tempStorage.size() > 0) {
         Enumeration e = this._tempStorage.keys();

         while (e.hasMoreElements()) {
            Object key = e.nextElement();
            this._storage.put(key, this._tempStorage.get(key));
         }

         this._tempStorage.clear();
         this._persist.commit();
         return true;
      } else {
         return false;
      }
   }

   public final synchronized void discardTempStorage() {
      if (this._tempStorage != null) {
         this._tempStorage.clear();
      }
   }

   public final synchronized void createTempStorage() {
      if (this._tempStorage == null) {
         this._tempStorage = new Hashtable();
      }

      if (this._storage != null) {
         Enumeration e = this._storage.keys();

         while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            this._tempStorage.put(key, this._storage.get(key));
         }
      }
   }

   public final void showMessage(String msg, int mode, int type) {
      if (mode == 0 || mode == 1) {
         if (type == 0) {
            UiApplication.getUiApplication().invokeLater(new ProvisioningServiceAgent$IOTAStatusRunnable(this, msg));
            return;
         }

         IOTADialog.show(msg);
      }
   }

   public static final boolean isSupported() {
      if (RadioInfo.areWAFsSupported(2)) {
         switch (Branding.getVendorId()) {
            case 1:
            case 104:
            case 213:
            case 225:
               return true;
         }
      }

      return false;
   }

   public static final void main(String[] args) {
      if (isSupported()) {
         new ProvisioningServiceAgent().enterEventDispatcher();
      }
   }
}
