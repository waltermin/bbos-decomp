package net.rim.device.api.crypto.cms;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OIDs;

public final class CMSReceiptRequest {
   private byte[] _signedContentIdentifier;
   private String[] _receiptsTo;
   private String[] _receiptsFrom;

   public CMSReceiptRequest(String[] receiptsTo, String[] receiptsFrom) {
      if (receiptsTo != null && receiptsTo.length != 0 && receiptsTo[0] != null) {
         this._receiptsTo = receiptsTo;
         this._receiptsFrom = receiptsFrom;
         byte[] firstName = this._receiptsTo[0].getBytes();
         byte[] time = String.valueOf(System.currentTimeMillis()).getBytes();
         byte[] random = RandomSource.getBytes(20);
         this._signedContentIdentifier = new byte[firstName.length + time.length + random.length];
         System.arraycopy(firstName, 0, this._signedContentIdentifier, 0, firstName.length);
         System.arraycopy(time, 0, this._signedContentIdentifier, firstName.length, time.length);
         System.arraycopy(random, 0, this._signedContentIdentifier, firstName.length + time.length, random.length);
      } else {
         throw new Object();
      }
   }

   public final byte[] getSignedContentIdentifier() {
      return this._signedContentIdentifier;
   }

   public final String[] getReceiptsTo() {
      return this._receiptsTo;
   }

   public final String[] getReceiptsFrom() {
      return this._receiptsFrom;
   }

   final CMSAttribute getReceiptRequestAttribute() {
      try {
         ASN1OutputStream struct = (ASN1OutputStream)(new Object());
         ASN1OutputStream set = (ASN1OutputStream)(new Object());
         ASN1OutputStream sequence = (ASN1OutputStream)(new Object());
         sequence.writeOctetString(this._signedContentIdentifier);
         if (this._receiptsFrom != null && this._receiptsFrom.length != 0) {
            ASN1OutputStream names = (ASN1OutputStream)(new Object());

            for (int i = 0; i < this._receiptsFrom.length; i++) {
               ASN1OutputStream generalName = (ASN1OutputStream)(new Object());
               generalName.writeIA5String(this._receiptsFrom[i], 2, 1);
               names.writeSequence(generalName);
            }

            sequence.writeSequence(names, 2, 1);
         } else {
            sequence.writeInteger(0, 2, 0);
         }

         ASN1OutputStream names = (ASN1OutputStream)(new Object());

         for (int i = 0; i < this._receiptsTo.length; i++) {
            ASN1OutputStream generalName = (ASN1OutputStream)(new Object());
            generalName.writeIA5String(this._receiptsTo[i], 2, 1);
            names.writeSequence(generalName);
         }

         sequence.writeSequence(names);
         set.writeSequence(sequence);
         struct.writeSet(set);
         return new CMSAttribute(OIDs.getOID(-1721363152), struct.toByteArray(), true);
      } finally {
         ;
      }
   }
}
