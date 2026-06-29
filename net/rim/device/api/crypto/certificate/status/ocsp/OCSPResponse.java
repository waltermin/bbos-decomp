package net.rim.device.api.crypto.certificate.status.ocsp;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.status.ResponseParsingException;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;

final class OCSPResponse {
   private byte[] _responseBytes;
   private int _responderIDType;
   private byte[] _responderID;
   private long _producedAt;
   private byte[] _nonce;
   private Vector _certInfo;
   public static final int RESPONDER_BY_NAME = 1;
   public static final int RESPONDER_BY_KEY_HASH = 2;
   private static final int TAG_OCSP_VERSION_INFO = 0;
   private static final int TAG_OCSP_VERSION_1 = 0;
   private static final int TAG_RESPONSE_EXTENSION = 1;
   private static final int TAG_STATUS_GOOD = 0;
   private static final int TAG_STATUS_REVOKED = 1;
   private static final int TAG_STATUS_UNKNOWN = 2;
   private static final int TAG_NEXT_UPDATE = 0;
   private static final int TAG_CRL_REASON = 0;

   public OCSPResponse(byte[] tbsResponseBytes) throws ResponseParsingException {
      this._responseBytes = tbsResponseBytes;
      this._certInfo = new Vector();
      ASN1InputStream tbsResponse = new ASN1InputStream(new ByteArrayInputStream(tbsResponseBytes));
      tbsResponse = tbsResponse.readSequence();
      if (tbsResponse.peekNextTag() == 0 && tbsResponse.readInteger(1, 0) != 0) {
         throw new ResponseParsingException();
      }

      this._responderIDType = tbsResponse.peekNextTag();
      if (this._responderIDType == 1) {
         ASN1InputStream in = new ASN1InputStream(tbsResponse.readStreamWithTag(1));
         this._responderID = in.readFieldAsByteArray();
      } else {
         this._responderID = new byte[20];
         tbsResponse.readOctetString(1, 2).read(this._responderID);
      }

      this._producedAt = tbsResponse.readGeneralizedTime();
      ASN1InputStream sequence = tbsResponse.readSequence();

      while (!sequence.endOfStream()) {
         this.parseSingleResponse(sequence);
      }

      this._nonce = null;
      if (tbsResponse.peekNextTag() == 1) {
         OID oidNonce = OIDs.getOID(-1299087525);
         sequence = tbsResponse.readSequence(1, 1);

         while (!sequence.endOfStream()) {
            ASN1InputStream extension = sequence.readSequence();
            if (oidNonce.equals(extension.readOID())) {
               extension.readBoolean(3, 0, false);
               this._nonce = extension.readOctetStringAsByteArray();
               return;
            }
         }
      }
   }

   private final void parseSingleResponse(ASN1InputStream response) throws ResponseParsingException {
      ASN1InputStream singleResponse = response.readSequence();
      ASN1InputStream certID = singleResponse.readSequence();
      if (!certID.readSequence().readOID().equals(OIDs.getOID(774767465))) {
         throw new ResponseParsingException();
      }

      byte[] issuerNameHash = certID.readOctetStringAsByteArray();
      byte[] issuerKeyHash = certID.readOctetStringAsByteArray();
      byte[] serialNumber = certID.readIntegerAsByteArray();
      int status = -1;
      int revocationReason = -1;
      long thisUpdate = 0;
      long nextUpdate = 0;
      long revocationDate = 0;
      switch (singleResponse.peekNextTag()) {
         case -1:
            break;
         case 0:
         default:
            singleResponse.readNull(2, 0);
            status = 0;
            break;
         case 1:
            ASN1InputStream revokedInfo = singleResponse.readSequence(2, 1);
            status = 1;
            revocationDate = revokedInfo.readGeneralizedTime();
            if (!revokedInfo.endOfStream() && revokedInfo.peekNextTag() == 0) {
               int reasonCode = revokedInfo.readEnumerated(1, 0);
               revocationReason = reasonCode;
            }
            break;
         case 2:
            singleResponse.readNull(2, 2);
            status = -1;
      }

      thisUpdate = singleResponse.readGeneralizedTime();
      if (!singleResponse.endOfStream() && singleResponse.peekNextTag() == 0) {
         nextUpdate = singleResponse.readGeneralizedTime(1, 0);
      }

      CertificateStatus certStatus = new CertificateStatus(status, this._producedAt, thisUpdate, nextUpdate, revocationDate, revocationReason);
      this._certInfo.addElement(new OCSPResponse$OCSPCertificateInfo(issuerNameHash, issuerKeyHash, serialNumber, certStatus));
   }

   public final byte[] getResponseBytes() {
      return this._responseBytes;
   }

   public final int getResponderIDType() {
      return this._responderIDType;
   }

   public final byte[] getResponderID() {
      return this._responderID;
   }

   public final byte[] getNonceBytes() {
      return this._nonce;
   }

   public final CertificateStatus getCertificateStatus(byte[] issuerDNHash, byte[] issuerPKHash, byte[] serialNo) {
      Enumeration certEnum = this._certInfo.elements();

      while (certEnum.hasMoreElements()) {
         OCSPResponse$OCSPCertificateInfo thisInfo = (OCSPResponse$OCSPCertificateInfo)certEnum.nextElement();
         if (Arrays.equals(thisInfo._issuerDNHash, issuerDNHash)
            && Arrays.equals(thisInfo._issuerPKHash, issuerPKHash)
            && Arrays.equals(thisInfo._serialNo, serialNo)) {
            return thisInfo._status;
         }
      }

      return null;
   }
}
