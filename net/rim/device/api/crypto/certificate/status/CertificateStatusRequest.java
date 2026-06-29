package net.rim.device.api.crypto.certificate.status;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.keystore.CertificateStatusManagerTicket;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ObjectUtilities;

public class CertificateStatusRequest {
   private Certificate[] _certChain;
   private boolean _checkEntireChain;
   private KeyStore _keyStore;
   private CertificateStatusManagerTicket _ticket;
   private Object _cookie;
   private Certificate[] _responseCerts;
   private CertificateStatus[] _responseStatus;
   private CertificateStatus _overallStatus;
   private String _errorMessage;
   private Vector _providerErrorMessages;
   private int _errorCode;
   public static final int SUCCESSFUL;
   public static final int CERTIFICATE_ALREADY_REVOKED;
   public static final int NO_STATUS_PROVIDERS;
   public static final int NO_SERVICE_BOOK_ENTRY;
   public static final int RADIO_IS_OFF;
   public static final int MALFORMED_RESPONSE;
   public static final int PROVIDER_ENCODING_ERROR;
   public static final int PROVIDER_DECODING_ERROR;
   public static final int FATAL_INTERNAL_ERROR;
   public static final int PROXY_CONNECTION_ERROR;
   public static final int PROXY_IO_ERROR;
   public static final int PROXY_VERSION_INCORRECT;
   public static final int PROXY_MALFORMED_REQUEST;
   public static final int PROXY_NO_PROVIDERS;
   public static final int PROXY_PROVIDER_ERROR;
   public static final int PROXY_INTERNAL_ERROR;
   public static final int UPDATING_HANDHELD_STATUS_FAILED;
   public static final int SERVICE_NOT_AVAILABLE;

   public CertificateStatusRequest(Certificate[] certChain, boolean checkEntireChain, KeyStore keyStore, CertificateStatusManagerTicket ticket, Object cookie) {
      if (certChain != null && certChain.length >= 1) {
         for (int i = 0; i < certChain.length; i++) {
            if (certChain[i] == null) {
               throw new Object();
            }
         }

         this._certChain = certChain;
         this._checkEntireChain = checkEntireChain;
         this._keyStore = keyStore;
         this._ticket = ticket;
         this._cookie = cookie;
      } else {
         throw new Object();
      }
   }

   @Override
   public int hashCode() {
      int hashCode = 0;
      hashCode ^= this._checkEntireChain ? 1 : 0;
      if (this._keyStore != null) {
         hashCode ^= this._keyStore.hashCode();
      }

      int numCerts = this._certChain.length;

      for (int i = 0; i < numCerts; i++) {
         hashCode ^= this._certChain[i].hashCode();
      }

      return hashCode;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof CertificateStatusRequest)) {
         return false;
      }

      CertificateStatusRequest other = (CertificateStatusRequest)o;
      return this._checkEntireChain == other._checkEntireChain
         && ObjectUtilities.objEqual(this._keyStore, other._keyStore)
         && Arrays.equals(this._certChain, other._certChain);
   }

   public Certificate[] getCertChain() {
      return this._certChain;
   }

   public boolean checkEntireChain() {
      return this._checkEntireChain;
   }

   public Object getCookie() {
      return this._cookie;
   }

   public CertificateStatus getOverallStatus() {
      return this._overallStatus;
   }

   public int getErrorCode() {
      return this._errorCode;
   }

   public Certificate[] getResponseCerts() {
      return this._responseCerts;
   }

   public CertificateStatus[] getResponseStatus() {
      return this._responseStatus;
   }

   public String getErrorMessage() {
      return this._errorMessage;
   }

   public Enumeration getProviderErrorMessages() {
      return (Enumeration)(this._providerErrorMessages == null ? new Object() : this._providerErrorMessages.elements());
   }

   KeyStore getKeyStore() {
      return this._keyStore;
   }

   CertificateStatusManagerTicket getCertificateStatusManagerTicket() {
      return this._ticket;
   }

   void setResponse(
      Certificate[] responseCerts,
      CertificateStatus[] responseStatus,
      CertificateStatus overallStatus,
      int errorCode,
      String errorMessage,
      Vector providerErrorMessages
   ) {
      this._responseCerts = responseCerts;
      this._responseStatus = responseStatus;
      this._overallStatus = overallStatus;
      this._errorCode = errorCode;
      this._errorMessage = errorMessage;
      this._providerErrorMessages = providerErrorMessages;
   }
}
