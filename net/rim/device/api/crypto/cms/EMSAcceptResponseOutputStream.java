package net.rim.device.api.crypto.cms;

import net.rim.device.api.crypto.asn1.ASN1BitSet;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;

public final class EMSAcceptResponseOutputStream extends CMSOutputStream {
   private CMSSignedDataOutputStream _output;
   private String[] _names;
   private String _displayName;
   private X509Certificate[] _certificates;
   private ASN1BitSet _flags;
   private int _version;
   private String _clientId;
   public static final int EMS_7_0;
   public static final int EMS_7_1;

   public EMSAcceptResponseOutputStream(CMSSignedDataOutputStream output, String[] names, X509Certificate[] certificates) {
      this(output, names, certificates, null, null, 0, null);
   }

   public EMSAcceptResponseOutputStream(
      CMSSignedDataOutputStream output, String[] names, X509Certificate[] certificates, String displayName, ASN1BitSet flags, int version, String clientId
   ) {
      super(output, 15, true, false);
      if (output != null && names != null) {
         this._output = output;
         this._names = names;
         this._displayName = displayName;
         this._certificates = certificates;
         this._flags = flags;
         this._version = version;
         this._clientId = clientId;
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
      ASN1OutputStream generalNames = (ASN1OutputStream)(new Object());

      for (int i = this._names.length - 1; i >= 0; i--) {
         generalNames.writeIA5String(this._names[i], 2, 1);
      }

      sequence.writeSequence(generalNames, 2, 0);
      if (this._displayName != null) {
         sequence.writeUTF8String(this._displayName, 2, 1);
      }

      if (this._certificates != null) {
         ASN1OutputStream certificates = (ASN1OutputStream)(new Object());

         for (int i = this._certificates.length - 1; i >= 0; i--) {
            certificates.writeRawByteArray(this._certificates[i].getEncoding());
         }

         sequence.writeSequence(certificates, 2, 2);
      }

      if (this._flags != null) {
         sequence.writeBitString(this._flags, 2, 3);
      }

      if (this._version != 0) {
         sequence.writeInteger(this._version, 2, 4);
      }

      if (this._clientId != null) {
         sequence.writeUTF8String(this._clientId, 2, 5);
      }

      stream.writeSequence(sequence);
      this._output.close();
   }
}
