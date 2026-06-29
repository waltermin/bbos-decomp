package net.rim.device.api.crypto.cms;

import java.io.OutputStream;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OID;

public final class CMSSignedReceiptOutputStream extends CMSOutputStream {
   private OutputStream _output;
   private OID _messageType;
   private byte[] _signedID;
   private byte[] _signatureValue;

   public CMSSignedReceiptOutputStream(CMSSignedDataOutputStream output, OID messageContentType, byte[] signedContentIdentifier, byte[] signatureValue) {
      super(output, 14, true, false);
      if (output != null && messageContentType != null && signedContentIdentifier != null && signatureValue != null) {
         this._output = output;
         this._messageType = messageContentType;
         this._signedID = signedContentIdentifier;
         this._signatureValue = signatureValue;
      } else {
         throw new Object();
      }
   }

   @Override
   public final void write(byte[] data, int offset, int length) {
   }

   @Override
   public final void close() {
      ASN1OutputStream stream = (ASN1OutputStream)(new Object(this._output));
      ASN1OutputStream sequence = (ASN1OutputStream)(new Object());
      sequence.writeInteger(1);
      sequence.writeOID(this._messageType);
      sequence.writeOctetString(this._signedID);
      sequence.writeRawByteArray(this._signatureValue);
      stream.writeSequence(sequence);
      this._output.close();
   }
}
