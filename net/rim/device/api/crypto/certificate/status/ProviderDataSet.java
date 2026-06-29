package net.rim.device.api.crypto.certificate.status;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;

final class ProviderDataSet implements ProviderRequestData, ProviderResponseData {
   private CertificateStatusProvider _provider;
   private ProviderCompressionTable _compressionTable;
   private Vector _certificates;
   private TaggedDataSet _globalFields;
   private IntHashtable _certFields;
   private Object _contextObject;

   public final void createFrom(ProviderDataSet dataSet) {
      this._provider = dataSet._provider;
      this._certificates = dataSet._certificates;
      this._contextObject = dataSet._contextObject;
      this._globalFields.createFrom(dataSet._globalFields);
      IntEnumeration enumeration = dataSet._certFields.keys();

      while (enumeration.hasMoreElements()) {
         int certId = enumeration.nextElement();
         TaggedCertDataSet certData = new TaggedCertDataSet(this._compressionTable);
         certData.createFrom((TaggedCertDataSet)dataSet._certFields.get(certId));
         this._certFields.put(certId, certData);
      }
   }

   public final CertificateStatusProvider getProvider() {
      return this._provider;
   }

   public final void unSerialize(DataInputStream in) {
      if (in == null) {
         throw new Object();
      }

      this._provider = CertificateStatusProvider.getProvider(in.readLong());
      if (this._provider == null) {
         throw new ResponseParsingException();
      }

      this._globalFields.unSerialize(in);
      int count = in.readByte() & 255;

      while (count-- > 0) {
         TaggedCertDataSet certData = new TaggedCertDataSet(this._compressionTable);
         certData.unSerialize(in);
         int certId = certData.getCertId();
         if (certId >= this._certificates.size()) {
            throw new ResponseParsingException();
         }

         this._certFields.put(certId, certData);
      }
   }

   public final void serialize(DataOutputStream out) {
      if (out == null) {
         throw new Object();
      }

      out.writeLong(this._provider.getProviderId());
      this._globalFields.serialize(out);
      out.writeByte((byte)this._certFields.size());
      Enumeration certEnum = this._certFields.elements();

      while (certEnum.hasMoreElements()) {
         TaggedCertDataSet certData = (TaggedCertDataSet)certEnum.nextElement();
         certData.serialize(out);
      }
   }

   public final int getCertificateCount() {
      return this._certificates.size();
   }

   public final CertificateStatus getCertificateStatus(Certificate certificate) {
      int certId = this.getCertId(certificate, false);
      if (certId != -1) {
         TaggedCertDataSet certData = (TaggedCertDataSet)this._certFields.get(certId);
         if (certData != null) {
            return certData.getStatus();
         }
      }

      return null;
   }

   @Override
   public final Enumeration getCertificates() {
      Vector certs = (Vector)(new Object());
      IntEnumeration enumeration = this._certFields.keys();

      while (enumeration.hasMoreElements()) {
         certs.addElement(this._certificates.elementAt(enumeration.nextElement()));
      }

      return certs.elements();
   }

   @Override
   public final void setCertificateStatus(Certificate certificate, CertificateStatus status) {
      int certId = this.getCertId(certificate, false);
      if (certId != -1) {
         TaggedCertDataSet certData = (TaggedCertDataSet)this._certFields.get(certId);
         if (certData != null) {
            certData.setStatus(status);
            return;
         }
      }

      throw new Object();
   }

   @Override
   public final void addGlobalField(int tag, byte[] value) {
      this._globalFields.addField(tag, value);
   }

   @Override
   public final byte[] getGlobalField(int tag) {
      return this._globalFields.getField(tag);
   }

   @Override
   public final void setContextObject(Object contextObject) {
      this._contextObject = contextObject;
   }

   @Override
   public final Object getContextObject() {
      return this._contextObject;
   }

   @Override
   public final byte[] getCertField(Certificate certificate, int tag) {
      int certId = this.getCertId(certificate, false);
      if (certId != -1) {
         TaggedCertDataSet certData = (TaggedCertDataSet)this._certFields.get(certId);
         if (certData != null) {
            return certData.getField(tag);
         }
      }

      return null;
   }

   @Override
   public final void addCertField(Certificate certificate, int tag, byte[] value) {
      int certId = this.getCertId(certificate, true);
      TaggedCertDataSet certData = (TaggedCertDataSet)this._certFields.get(certId);
      if (certData == null) {
         certData = new TaggedCertDataSet(this._compressionTable, certId);
         this._certFields.put(certId, certData);
      }

      certData.addField(tag, value);
   }

   private final int getCertId(Certificate cert, boolean addNewCerts) {
      int index = this._certificates.indexOf(cert);
      if (index == -1 && addNewCerts) {
         this._certificates.addElement(cert);
         index = this._certificates.size() - 1;
      }

      return index;
   }

   public ProviderDataSet(ProviderCompressionTable compressionTable, Vector certificates) {
      this(null, compressionTable, certificates);
   }

   public ProviderDataSet(CertificateStatusProvider provider, ProviderCompressionTable compressionTable, Vector certificates) {
      this._compressionTable = compressionTable;
      this._provider = provider;
      this._certificates = certificates;
      this._globalFields = new TaggedDataSet(compressionTable);
      this._certFields = (IntHashtable)(new Object());
      this._contextObject = null;
   }
}
