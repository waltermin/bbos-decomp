package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import java.util.Enumeration;
import net.rim.device.api.crypto.certificate.pgp.PGPKeyIDKeyStoreIndex;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.xml.XMLHashtable;

class PGPUniversalServer$CompleteEnrollmentEmailThread extends Thread {
   String _authenticationCookie;
   byte[] _ephemeralKeyID;
   private final PGPUniversalServer this$0;

   private PGPUniversalServer$CompleteEnrollmentEmailThread(PGPUniversalServer _1, String authenticationCookie, byte[] ephemeralKeyID) {
      this.this$0 = _1;
      this._authenticationCookie = authenticationCookie;
      this._ephemeralKeyID = ephemeralKeyID;
   }

   @Override
   public void run() {
      PGPUniversalEnrollmentKeyStore enrollmentKeyStore = PGPUniversalEnrollmentKeyStore.getInstance();
      enrollmentKeyStore.addIndex(new PGPKeyIDKeyStoreIndex());
      Enumeration enumeration = enrollmentKeyStore.elements(-2737350786039236692L, this._ephemeralKeyID);

      while (enumeration.hasMoreElements()) {
         KeyStoreData data = (KeyStoreData)enumeration.nextElement();

         try {
            enrollmentKeyStore.removeKey(data, null);
         } finally {
            continue;
         }
      }

      try {
         XMLHashtable authenticateInternalResponse = this.this$0._soapHandler.authenticateInternalWithCookie(this._authenticationCookie, null);
         if (authenticateInternalResponse.getString("/AuthenticateInternalResponse") != null) {
            this.this$0.detectPolicyType(authenticateInternalResponse, "/AuthenticateInternalResponse");
            this.this$0.completeEnrollment(this._authenticationCookie, null);
         }
      } catch (PGPUniversalServerSoapFaultException var11) {
      } finally {
         return;
      }
   }

   PGPUniversalServer$CompleteEnrollmentEmailThread(PGPUniversalServer x0, String x1, byte[] x2, PGPUniversalServer$1 x3) {
      this(x0, x1, x2);
   }
}
