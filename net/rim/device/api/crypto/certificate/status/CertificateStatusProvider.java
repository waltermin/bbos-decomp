package net.rim.device.api.crypto.certificate.status;

import java.util.Enumeration;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreOptions;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.MultiMap;
import net.rim.device.internal.ui.component.BackgroundDialog;

public class CertificateStatusProvider {
   private long _providerId;
   private static LongHashtable _providers;
   private static MultiMap _queriesInProgress;
   public static final int REQUEST_START;
   public static final int REQUEST_COMPLETE;
   public static final int REQUEST_DISMISS;
   public static final int REQUEST_CANCEL;
   public static final int REQUEST_ERROR;
   private static final long PROVIDERS_ID;
   private static final long QUERIES_IN_PROGRESS_ID;

   protected CertificateStatusProvider(long providerId) {
      this._providerId = providerId;
   }

   public final long getProviderId() {
      return this._providerId;
   }

   public static final boolean register(CertificateStatusProvider provider) {
      if (provider == null) {
         throw new Object();
      }

      if (_providers.get(provider.getProviderId()) != null) {
         return false;
      }

      _providers.put(provider.getProviderId(), provider);
      return true;
   }

   static final CertificateStatusProvider getProvider(long providerId) {
      return (CertificateStatusProvider)_providers.get(providerId);
   }

   static final Enumeration getProviders() {
      return _providers.elements();
   }

   public static final boolean queryStatusAvailability() {
      return _providers.size() > 0;
   }

   public static final boolean queryStatusAvailability(Certificate[] certChain, boolean extendedChecking) {
      if (certChain == null) {
         throw new Object();
      }

      String certificateServiceUID = KeyStoreOptions.getCertificateServiceUID();
      if (certificateServiceUID == null) {
         return false;
      }

      Enumeration enumeration = getProviders();

      while (enumeration.hasMoreElements()) {
         CertificateStatusProvider provider = (CertificateStatusProvider)enumeration.nextElement();
         if (provider.checkCompatibility(certChain, extendedChecking)) {
            return true;
         }
      }

      return false;
   }

   public static final int requestCertificateStatus(
      CertificateStatusRequest request, CertificateStatusListener listener, boolean allowDismiss, boolean allowDetails
   ) {
      if (request == null) {
         throw new Object();
      }

      CertificateStatusQuery query = new CertificateStatusQuery(request, KeyStoreOptions.getCertificateServiceUID(), listener);
      QueryStatusDialog queryDialog = new QueryStatusDialog(query, allowDismiss, 134217728);
      BackgroundDialog.show(queryDialog);
      int closeReason = queryDialog.getCloseReason();
      switch (closeReason) {
         case 0:
            throw new Object();
         case 1:
            CertificateStatusDialog statusDialog = new CertificateStatusDialog(query, request.getKeyStore(), allowDetails, 134217728);
            BackgroundDialog.show(statusDialog);
         case 2:
         case 3:
            break;
         case 4:
         default:
            StatusErrorDialog errorDialog = new StatusErrorDialog(query, 134217728);
            BackgroundDialog.show(errorDialog);
      }

      return closeReason;
   }

   public static final boolean requestCertificateStatusInternal(CertificateStatusRequest request, CertificateStatusListener listener) {
      if (request == null) {
         throw new Object();
      }

      CertificateStatusQuery query = new CertificateStatusQuery(request, KeyStoreOptions.getCertificateServiceUID(), listener);
      QueryStatusDialog queryDialog = new QueryStatusDialog(query, false, 134217728);
      BackgroundDialog.show(queryDialog);
      int closeReason = queryDialog.getCloseReason();
      switch (closeReason) {
         case 0:
            return false;
         case 1:
            if (request.getOverallStatus().getStatus() == 1) {
               return false;
            }

            return true;
         case 2:
         case 3:
         case 4:
         default:
            return false;
      }
   }

   public static final int fetchCertificateStatus(CertificateStatusRequest request, CertificateStatusListener listener) {
      if (request == null) {
         throw new Object();
      }

      CertificateStatusQuery query = new CertificateStatusQuery(request, KeyStoreOptions.getCertificateServiceUID(), listener);
      synchronized (_queriesInProgress) {
         if (_queriesInProgress.size(request) == 0) {
            query.setProgressListener(new CertificateStatusProvider$NonUIQueryListener());
            int errorCode = query.beginQuery();
            if (errorCode != 0) {
               return 4;
            }
         }

         _queriesInProgress.add(request, query);
         return 0;
      }
   }

   protected boolean checkCompatibility(Certificate[] _1, boolean _2) {
      throw null;
   }

   protected void encodeRequest(Certificate[] _1, boolean _2, ProviderRequestData _3, KeyStore _4, ProviderUiContext _5) {
      throw null;
   }

   protected void decodeResponse(Certificate[] _1, boolean _2, ProviderResponseData _3, KeyStore _4, ProviderUiContext _5) {
      throw null;
   }

   static {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      _providers = applicationRegistry.getLongHashtable(-2347883093695494417L);
      _queriesInProgress = (MultiMap)applicationRegistry.getOrWaitFor(5422056262400636262L);
      if (_queriesInProgress == null) {
         _queriesInProgress = (MultiMap)(new Object());
         applicationRegistry.put(5422056262400636262L, _queriesInProgress);
      }
   }
}
