package net.rim.device.api.crypto.certificate.status.crl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.certificate.CertificateExtension;
import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;
import net.rim.device.api.util.Arrays;

public class CRLDistributionPoint {
   private X509DistinguishedName[] _relativeDirectories = new X509DistinguishedName[0];
   private String[] _uris = new String[0];

   public CRLDistributionPoint(CertificateExtension extension) {
      if (extension == null) {
         throw new IllegalArgumentException();
      }

      this.parse(extension);
   }

   private void parse(CertificateExtension extension) {
      byte[] value = extension.getValue();
      ASN1InputStream asn1 = new ASN1InputStream(new ByteArrayInputStream(value));
      ASN1InputStream distPoints = asn1.readSequence();

      while (!distPoints.endOfStream()) {
         ASN1InputStream distPoint = distPoints.readSequence();
         if (distPoint.peekNextTag() == 0) {
            InputStream inner = distPoint.readStreamWithTag(0);
            ASN1InputStream innerASN1 = new ASN1InputStream(inner);
            if (innerASN1.peekNextTag() == 0) {
               ASN1InputStream generalNames = innerASN1.readSequence(2, 0);

               while (!generalNames.endOfStream()) {
                  switch (generalNames.peekNextTag()) {
                     case 4:
                        Arrays.add(this._relativeDirectories, new X509DistinguishedName(new ASN1InputStream(generalNames.readStreamWithTag(4))));
                        break;
                     case 6:
                        Arrays.add(this._uris, generalNames.readIA5String(2, 6));
                        break;
                     default:
                        generalNames.skipField();
                  }
               }
            } else if (innerASN1.peekNextTag() == 1) {
               Arrays.add(this._relativeDirectories, new X509DistinguishedName(new ASN1InputStream(innerASN1.readStreamWithTag(1))));
            }
         }

         if (distPoint.peekNextTag() == 1) {
            distPoint.readBitString(2, 1);
         }

         if (distPoint.peekNextTag() == 2) {
            ASN1InputStream generalNames = distPoint.readSequence(2, 2);

            while (!generalNames.endOfStream()) {
               switch (generalNames.peekNextTag()) {
                  case 4:
                     Arrays.add(this._relativeDirectories, new X509DistinguishedName(new ASN1InputStream(generalNames.readStreamWithTag(4))));
                     break;
                  case 6:
                     Arrays.add(this._uris, generalNames.readIA5String(2, 6));
                     break;
                  default:
                     generalNames.skipField();
               }
            }
         }
      }
   }

   public X509DistinguishedName[] getRelativeDirectories() {
      return this._relativeDirectories;
   }

   public String[] getURIs() {
      return this._uris;
   }
}
