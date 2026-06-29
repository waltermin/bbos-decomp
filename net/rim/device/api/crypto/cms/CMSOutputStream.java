package net.rim.device.api.crypto.cms;

import java.io.OutputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public class CMSOutputStream extends OutputStream {
   protected OutputStream _out;
   private byte[] _buffer;
   protected boolean _dataOut;
   protected OID _contentType;
   protected boolean _outer;

   protected CMSOutputStream(OutputStream out, int contentType, boolean dataOut, boolean outer) {
      if (out == null) {
         throw new IllegalArgumentException();
      }

      this._out = out;
      this._outer = outer;
      this._dataOut = dataOut;
      if (contentType == 10) {
         this._contentType = OIDs.getOID(541859388);
      } else if (contentType == 11) {
         this._contentType = OIDs.getOID(542121532);
      } else if (contentType == 12) {
         this._contentType = OIDs.getOID(542383676);
      } else if (contentType == 13) {
         this._contentType = OIDs.getOID(-1721352904);
      } else if (contentType == 14) {
         this._contentType = OIDs.getOID(-1721352925);
      } else if (contentType == 15) {
         this._contentType = OIDs.getOID(-477712249);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void write(byte[] _1, int _2, int _3) {
      throw null;
   }

   @Override
   public void write(int data) {
      if (this._buffer == null) {
         this._buffer = new byte[1];
      }

      this._buffer[0] = (byte)data;
      this.write(this._buffer, 0, 1);
   }

   @Override
   public void write(byte[] data) {
      this.write(data, 0, data == null ? 0 : data.length);
   }

   @Override
   public void flush() {
      this._out.flush();
   }

   @Override
   public void close() {
      this.flush();
      this._out.close();
   }
}
