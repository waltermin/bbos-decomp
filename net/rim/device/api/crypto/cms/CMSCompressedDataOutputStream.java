package net.rim.device.api.crypto.cms;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import net.rim.device.api.compress.ZLibOutputStream;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public final class CMSCompressedDataOutputStream extends CMSOutputStream {
   private ByteArrayOutputStream _bufferOut = new ByteArrayOutputStream();
   private OutputStream _compressedOut;
   private OID _compressionType;
   public static final int ZLIB_COMPRESSION = 1;

   public CMSCompressedDataOutputStream(OutputStream out, int contentType, boolean outer) {
      this(out, contentType, 1, outer);
   }

   public CMSCompressedDataOutputStream(OutputStream out, int contentType, int compressionType, boolean outer) {
      super(out, contentType, true, outer);
      if (compressionType == 1) {
         this._compressionType = OIDs.getOID(-1721348808);
         this._compressedOut = new ZLibOutputStream(this._bufferOut);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void write(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         this._compressedOut.write(data, offset, length);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void close() {
      ASN1OutputStream compressedDataOutput = new ASN1OutputStream(super._out);
      ASN1OutputStream compressedData = new ASN1OutputStream();
      compressedData.writeInteger(0);
      ASN1OutputStream compressionAlgorithm = new ASN1OutputStream();
      compressionAlgorithm.writeOID(this._compressionType);
      compressedData.writeSequence(compressionAlgorithm);
      this._compressedOut.close();
      ASN1OutputStream encapsulatedContentInfo = new ASN1OutputStream();
      encapsulatedContentInfo.writeOID(super._contentType);
      encapsulatedContentInfo.writeOctetString(this._bufferOut.toByteArray(), 1, 0);
      compressedData.writeSequence(encapsulatedContentInfo);
      if (super._outer) {
         ASN1OutputStream contentInfo = new ASN1OutputStream();
         contentInfo.writeOID(OIDs.getOID(-1721352904));
         contentInfo.writeSequence(compressedData, 1, 0);
         compressedDataOutput.writeSequence(contentInfo);
      } else {
         compressedDataOutput.writeSequence(compressedData);
      }

      super._out.close();
   }
}
