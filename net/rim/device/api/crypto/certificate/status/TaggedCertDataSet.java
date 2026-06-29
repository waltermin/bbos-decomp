package net.rim.device.api.crypto.certificate.status;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import net.rim.device.api.crypto.certificate.CertificateStatus;

final class TaggedCertDataSet extends TaggedDataSet {
   private int _certId;
   private CertificateStatus _status;

   public TaggedCertDataSet(ProviderCompressionTable compressionTable, int certId) {
      super(compressionTable);
      this._certId = certId;
      this._status = (CertificateStatus)(new Object());
   }

   public TaggedCertDataSet(ProviderCompressionTable compressionTable) {
      this(compressionTable, -1);
   }

   public final void createFrom(TaggedCertDataSet dataSet) {
      this._certId = dataSet._certId;
      this._status = dataSet._status;
      super.createFrom(dataSet);
   }

   public final void setStatus(CertificateStatus certStatus) {
      this._status = certStatus;
   }

   public final CertificateStatus getStatus() {
      return this._status;
   }

   public final int getCertId() {
      return this._certId;
   }

   @Override
   public final void serialize(DataOutputStream out) {
      if (out == null) {
         throw new Object();
      }

      out.writeByte((byte)this._certId);
      super.serialize(out);
   }

   @Override
   public final void unSerialize(DataInputStream in) {
      if (in == null) {
         throw new Object();
      }

      this._certId = in.readByte() & 255;
      super.unSerialize(in);
   }
}
