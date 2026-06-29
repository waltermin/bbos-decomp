package net.rim.device.apps.internal.addressbook.vcard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import net.rim.device.apps.api.addressbook.AddressCardModel;

class ConversionInputStream extends InputStream {
   private AddressCardModel _model;
   private int _version;
   private int _attributeMask;
   private boolean _convertForBluetooth;
   private String[] _extensionData;
   private ByteArrayInputStream _istream;

   public ConversionInputStream(AddressCardModel model, int version, int attributeMask, boolean convertForBluetooth, String[] extensionData) {
      this._model = model;
      this._version = version;
      this._attributeMask = attributeMask;
      this._convertForBluetooth = convertForBluetooth;
      this._extensionData = extensionData;
   }

   @Override
   public String toString() {
      return this._model.toString();
   }

   @Override
   public int read() {
      this.ensureStream();
      return this._istream.read();
   }

   @Override
   public int read(byte[] b) {
      this.ensureStream();
      return this._istream.read(b);
   }

   @Override
   public int read(byte[] b, int off, int len) {
      this.ensureStream();
      return this._istream.read(b, off, len);
   }

   @Override
   public long skip(long n) {
      this.ensureStream();
      return this._istream.skip(n);
   }

   @Override
   public int available() {
      this.ensureStream();
      return this._istream.available();
   }

   @Override
   public void close() {
      this.ensureStream();
      this._istream.close();
   }

   @Override
   public synchronized void mark(int readlimit) {
      this.ensureStream();
      this._istream.mark(readlimit);
   }

   @Override
   public synchronized void reset() {
      this.ensureStream();
      this._istream.reset();
   }

   @Override
   public boolean markSupported() {
      this.ensureStream();
      return this._istream.markSupported();
   }

   private void ensureStream() {
      if (this._istream == null) {
         try {
            ByteArrayOutputStream ostream = (ByteArrayOutputStream)(new Object());
            AddressCardToVCardConverter.writeVCard(this._model, ostream, this._version, this._attributeMask, this._convertForBluetooth, this._extensionData);
            this._istream = (ByteArrayInputStream)(new Object(ostream.toByteArray()));
         } finally {
            this._istream = (ByteArrayInputStream)(new Object(new byte[0]));
            return;
         }
      }
   }
}
