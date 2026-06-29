package net.rim.device.api.crypto.cms;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.oid.OIDs;

public final class CMSDataOutputStream extends CMSOutputStream {
   private ByteArrayOutputStream _buffer;

   public CMSDataOutputStream(OutputStream out, boolean outer) {
      super(out, 10, true, outer);
      if (outer) {
         this._buffer = (ByteArrayOutputStream)(new Object());
      } else {
         this._buffer = null;
      }
   }

   @Override
   public final void write(byte[] data, int offset, int length) {
      if (data == null || offset < 0 || length < 0 || data.length - length < offset) {
         throw new Object();
      }

      if (super._outer) {
         this._buffer.write(data, offset, length);
      } else {
         super._out.write(data, offset, length);
      }
   }

   @Override
   public final void close() {
      if (super._outer) {
         this._buffer.close();
         ASN1OutputStream output = (ASN1OutputStream)(new Object(super._out));
         ASN1OutputStream contentInfo = (ASN1OutputStream)(new Object());
         contentInfo.writeOID(OIDs.getOID(541859388));
         contentInfo.writeOctetString(this._buffer.toByteArray(), 1, 0);
         output.writeSequence(contentInfo);
         super._out.close();
      } else {
         super.close();
      }
   }
}
