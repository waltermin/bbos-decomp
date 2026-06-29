package net.rim.device.api.crypto.keystore;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateFactory;
import net.rim.device.api.crypto.certificate.CertificateRevocationList;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.CertificateStatusException;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.component.BackgroundDialog;

public final class CertificateStatusManager implements Collection, CollectionEventSource {
   private CollectionListenerManager _listeners = (CollectionListenerManager)(new Object());
   private static final long CLOCK_SKEW;
   private static final long CLEAN_THRESHOLD;
   static final byte ALWAYS_PROMPT;
   static final byte PROMPT_ON_ERROR;
   static final byte NEVER_PROMPT;
   private static final long CERTIFICATE_STATUS_MANAGER;
   private static CertificateStatusManager _manager;
   private static final int NO_UPDATE;
   private static final int UPDATE_NO_TICKET;
   private static final int UPDATE_TICKET;
   private static final long TWO_WEEKS;

   private CertificateStatusManager() {
   }

   private final void initialize() {
      this.kickStartClean(true);
      MemoryCleanerDaemon.addListener(new CertificateStatusManager$CertificateStatusManagerCleaner(), false);
   }

   final void setStatusCleanTimer() {
      this.kickStartClean(false);
   }

   private final void kickStartClean(boolean startup) {
      Proxy.getInstance().invokeLater(new CertificateStatusManager$CleanRunnable(), startup ? 1 : 86400000, false);
   }

   public static final CertificateStatusManager getInstance() {
      if (_manager == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _manager = (CertificateStatusManager)ar.getOrWaitFor(-1593645258214481195L);
         if (_manager == null) {
            _manager = new CertificateStatusManager();
            ar.put(-1593645258214481195L, _manager);
            _manager.initialize();
         }
      }

      return _manager;
   }

   public static final long getStaleTime() {
      return KeyStoreManagerHelper.getInstance().getStaleTime();
   }

   private final boolean doStatusWork(CertificateContainer container, CertificateStatus status) {
      KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
      Hashtable statusHashtable = helper.getStatusHashtable();
      if (statusHashtable == null) {
         statusHashtable = (Hashtable)(new Object());
      }

      Object oldStatus = statusHashtable.put(container, status);
      helper.setStatusHashtable(statusHashtable);
      return oldStatus != null;
   }

   private final void doTicketWork(Certificate certificate, CertificateStatus status, CertificateStatusManagerTicket ticket) {
      if (certificate == null) {
         throw new Object();
      }

      if (ticket instanceof RIMCertificateStatusManagerTicket) {
         RIMCertificateStatusManagerTicket rimTicket = (RIMCertificateStatusManagerTicket)ticket;
         if (rimTicket.access()) {
            return;
         }
      }

      String[] arguments;
      String pattern;
      if (status.getStatus() == 1) {
         pattern = KeyStoreResources.getString(6067);
         arguments = new Object[]{
            certificate.getSubjectFriendlyName(), this.getStatusString(status.getStatus()), RevocationReason.getRevocationReason(status.getRevocationReason())
         };
      } else {
         pattern = KeyStoreResources.getString(5002);
         arguments = new Object[]{certificate.getSubjectFriendlyName(), this.getStatusString(status.getStatus())};
      }

      String fullPrompt = MessageFormat.format(pattern, arguments);
      KeyStoreUtilitiesInternal.getCurrentKeyStorePassword(fullPrompt, true, false);
   }

   private final String getStatusString(int certificateStatus) {
      switch (certificateStatus) {
         case -1:
         default:
            return KeyStoreResources.getString(7002);
         case 0:
            return KeyStoreResources.getString(7001);
         case 1:
            return KeyStoreResources.getString(7003);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void revoke(CertificateRevocationList crl) {
      if (crl == null) {
         throw new Object();
      }

      KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
      Hashtable statusHashtable = helper.getStatusHashtable();
      if (statusHashtable != null) {
         Enumeration enumeration = statusHashtable.keys();

         while (enumeration.hasMoreElements()) {
            CertificateContainer container = (CertificateContainer)enumeration.nextElement();
            boolean var12 = false /* VF: Semaphore variable */;

            Certificate certificate;
            try {
               var12 = true;
               certificate = CertificateFactory.getInstance(container.getType(), container.getEncoding());
               var12 = false;
            } finally {
               if (var12) {
                  throw new CertificateStatusException();
               }
            }

            if (certificate != null) {
               try {
                  CertificateStatus newStatus = crl.getCertificateStatus(certificate);
                  if (newStatus != null) {
                     this.setStatus(certificate, newStatus, null);
                  }
               } finally {
                  continue;
               }
            }
         }

         helper.setStatusHashtable(statusHashtable);
      }
   }

   public final void setStatus(Certificate certificate, CertificateStatus status, CertificateStatusManagerTicket ticket) {
      this.setStatus(certificate, null, null, status, ticket, false);
   }

   public final void setStatus(Certificate certificate, int reason, CertificateStatusManagerTicket ticket) {
      long now = System.currentTimeMillis();
      CertificateStatus status = new CertificateStatus(1, now, now, 0, now, reason);
      this.setStatus(certificate, null, null, status, ticket, false);
   }

   public final void setStatus(byte[] encoding, String type, CertificateStatus status, CertificateStatusManagerTicket ticket) {
      this.setStatus(null, encoding, type, status, ticket, false);
   }

   final synchronized boolean setStatus(
      Certificate certificate, byte[] encoding, String type, CertificateStatus status, CertificateStatusManagerTicket ticket, boolean silentAccess
   ) {
      if (status == null) {
         throw new Object();
      }

      CertificateContainer container;
      if (encoding != null && type != null) {
         container = new CertificateContainer(encoding, type);
      } else {
         if (certificate == null) {
            throw new Object();
         }

         container = new CertificateContainer(certificate.getEncoding(), certificate.getType());
      }

      long currentTime = System.currentTimeMillis() + 1800000;
      if (status.getProducedAtTime() <= currentTime || !silentAccess && this.acceptFutureStatus(certificate, status)) {
         int action = this.determineAction(container, status);
         if (action == 0) {
            return false;
         }

         if (action == 2 && !silentAccess) {
            this.doTicketWork(certificate, status, ticket);
         }

         boolean previousStatusExisted = this.doStatusWork(container, status);
         if (certificate != null) {
            if (previousStatusExisted) {
               this._listeners.fireElementUpdated(this, certificate, certificate);
               return true;
            }

            this._listeners.fireElementAdded(this, certificate);
         }

         return true;
      } else {
         return false;
      }
   }

   public final boolean isTicketRequired(Certificate certificate, CertificateStatus newStatus) {
      if (certificate != null && newStatus != null) {
         return this.isTicketRequired(certificate.getEncoding(), certificate.getType(), newStatus);
      } else {
         throw new Object();
      }
   }

   public final boolean isTicketRequired(byte[] certificateEncoding, String certificateType, CertificateStatus newStatus) {
      if (certificateEncoding != null && certificateType != null && newStatus != null) {
         KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
         return !helper.isPassphraseSet() ? true : this.determineAction(new CertificateContainer(certificateEncoding, certificateType), newStatus) == 2;
      } else {
         throw new Object();
      }
   }

   private final int determineAction(CertificateContainer certificateContainer, CertificateStatus newStatus) {
      KeyStoreManagerHelper keyStoreManagerHelper = KeyStoreManagerHelper.getInstance();
      CertificateStatus currentStatus = null;
      Hashtable statusHashtable = keyStoreManagerHelper.getStatusHashtable();
      if (statusHashtable != null) {
         currentStatus = (CertificateStatus)statusHashtable.get(certificateContainer);
      }

      if (currentStatus == null) {
         return newStatus.getStatus() == 1 ? 2 : 1;
      }

      boolean promptForSameStatus = keyStoreManagerHelper.getPromptForStatus();
      long newStatusProducedAt = newStatus.getProducedAtTime();
      long currentStatusProducedAt = currentStatus.getProducedAtTime();
      switch (currentStatus.getStatus()) {
         case -2:
            throw new Object();
         case -1:
         default:
            switch (newStatus.getStatus()) {
               case -2:
                  throw new Object();
               case -1:
               default:
                  if (newStatusProducedAt < currentStatusProducedAt) {
                     throw new InvalidTimeException();
                  } else if (newStatusProducedAt == currentStatusProducedAt) {
                     return 0;
                  } else {
                     if (promptForSameStatus) {
                        return 2;
                     }

                     return 1;
                  }
               case 0:
                  return 2;
               case 1:
                  return 2;
            }
         case 0:
            switch (newStatus.getStatus()) {
               case -2:
                  throw new Object();
               case -1:
               default:
                  throw new BackwardStatusException();
               case 0:
                  if (newStatusProducedAt < currentStatusProducedAt) {
                     throw new InvalidTimeException();
                  } else if (newStatusProducedAt == currentStatusProducedAt) {
                     return 0;
                  } else {
                     if (promptForSameStatus) {
                        return 2;
                     }

                     return 1;
                  }
               case 1:
                  return 2;
            }
         case 1:
            if (currentStatus.getRevocationReason() == 6) {
               switch (newStatus.getStatus()) {
                  case -2:
                     throw new Object();
                  case -1:
                  default:
                     throw new BackwardStatusException();
                  case 0:
                     if (newStatusProducedAt <= currentStatusProducedAt) {
                        throw new InvalidTimeException();
                     }

                     return 2;
                  case 1:
                     if (newStatus.getRevocationReason() == 6) {
                        if (newStatusProducedAt < currentStatusProducedAt || newStatus.getRevocationTime() > currentStatus.getRevocationTime()) {
                           throw new InvalidTimeException();
                        } else if (newStatusProducedAt == currentStatusProducedAt) {
                           return 0;
                        } else {
                           return promptForSameStatus ? 2 : 1;
                        }
                     } else {
                        return 2;
                     }
               }
            } else {
               switch (newStatus.getStatus()) {
                  case -2:
                     throw new Object();
                  case -1:
                  default:
                     throw new BackwardStatusException();
                  case 0:
                     throw new BackwardStatusException();
                  case 1:
                     if (newStatus.getRevocationReason() == 6) {
                        throw new BackwardStatusException();
                     } else if (newStatusProducedAt < currentStatusProducedAt || newStatus.getRevocationTime() > currentStatus.getRevocationTime()) {
                        throw new InvalidTimeException();
                     } else if (newStatusProducedAt == currentStatusProducedAt) {
                        return 0;
                     } else {
                        return promptForSameStatus ? 2 : 1;
                     }
               }
            }
      }
   }

   public final CertificateStatus getStatus(Certificate certificate) {
      KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
      Hashtable statusHashtable = helper.getStatusHashtable();
      return statusHashtable == null ? null : (CertificateStatus)statusHashtable.get(certificate);
   }

   private final boolean acceptFutureStatus(Certificate certificate, CertificateStatus status) {
      if (certificate == null) {
         throw new Object();
      }

      InvalidTimeDialog dialog = new InvalidTimeDialog(certificate, status, 134217728);
      BackgroundDialog.show(dialog);
      return dialog.getCloseReason() != -1;
   }

   final synchronized void clean() {
      KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
      Hashtable old = helper.getStatusHashtable();
      if (old != null) {
         Hashtable temp = (Hashtable)(new Object());
         KeyStoreManager keyStoreManager = KeyStoreManager.getInstance();
         Enumeration keyStores = keyStoreManager.getRegisteredKeyStores();

         while (keyStores.hasMoreElements()) {
            KeyStore keyStore = (KeyStore)keyStores.nextElement();
            Enumeration keyStoreDatas = keyStore.elements();

            while (keyStoreDatas.hasMoreElements()) {
               KeyStoreData data = (KeyStoreData)keyStoreDatas.nextElement();
               Certificate certificate = data.getCertificate();
               if (certificate != null) {
                  CertificateContainer container = new CertificateContainer(certificate.getEncoding(), certificate.getType());
                  temp.put(container, container);
               }
            }
         }

         Enumeration enumeration = old.keys();

         while (enumeration.hasMoreElements()) {
            Object o = enumeration.nextElement();
            if (!(o instanceof Certificate)) {
               if (o instanceof CertificateContainer) {
                  CertificateContainer container = (CertificateContainer)o;
                  if (!temp.containsKey(container)) {
                     CertificateStatus status = (CertificateStatus)old.get(container);
                     if (System.currentTimeMillis() - status.getCreationDate() > 1209600000) {
                        old.remove(container);
                     }
                  }
               }
            } else {
               Certificate certificate = (Certificate)o;
               if (!temp.containsKey(certificate)) {
                  CertificateStatus status = (CertificateStatus)old.get(certificate);
                  if (status.getCreationDate() - System.currentTimeMillis() > 1209600000) {
                     old.remove(certificate);
                  }
               }
            }
         }

         helper.setStatusHashtable(old);
      }
   }

   public final CertificateStatusManagerTicket getTicket() {
      return new RIMCertificateStatusManagerTicket();
   }

   public final boolean checkTicket(CertificateStatusManagerTicket ticket) {
      return ticket instanceof CertificateStatusManagerTicket ? ((RIMCertificateStatusManagerTicket)ticket).access() : false;
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._listeners.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._listeners.removeCollectionListener(listener);
   }
}
