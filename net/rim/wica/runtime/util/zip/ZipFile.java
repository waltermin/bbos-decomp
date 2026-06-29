package net.rim.wica.runtime.util.zip;

import java.io.IOException;
import java.io.InputStream;

public class ZipFile {
   private ZipEntry[] _entries;
   private String _name;
   static final int LOCAL_FILE_HEADER_SIGNATURE = 67324752;
   static final int CENTRAL_FILE_HEADER_SIGNATURE = 33639248;
   static final int END_CENTRAL_DIRECTORY_SIGNATURE = 101010256;

   public ZipFile(String name, byte[] bytes) {
      this.init(name, bytes);
   }

   public void close() {
      this._name = null;
      this._entries = null;
   }

   public ZipEntry[] entries() {
      this.checkClosed();
      return this._entries;
   }

   public ZipEntry getEntry(String name) {
      this.checkClosed();
      int num = this._entries.length;

      for (int i = 0; i < num; i++) {
         ZipEntry entry = this._entries[i];
         if (entry.getName().equals(name)) {
            return entry;
         }
      }

      return null;
   }

   public InputStream getInputStream(ZipEntry entry) {
      this.checkClosed();
      return entry.getInputStream();
   }

   public String getName() {
      return this._name;
   }

   public int size() {
      this.checkClosed();
      return this._entries.length;
   }

   static int extractInt(byte[] bytes, int offset) {
      return bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset + 2] & 0xFF) << 16 | bytes[offset + 3] << 24;
   }

   static int extractShort(byte[] bytes, int offset) {
      return (bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8) & 65535;
   }

   static String extractString(byte[] bytes, int offset, int length) {
      StringBuffer buff = new StringBuffer(length);

      for (int i = 0; i < length; i++) {
         buff.append((char)bytes[offset + i]);
      }

      return buff.toString();
   }

   private void checkClosed() {
      if (this._entries == null) {
         throw new IllegalStateException("The ZIP file is closed.");
      }
   }

   private void init(String name, byte[] bytes) throws IOException {
      this._name = name;
      int length = bytes.length;
      int numEntries = 0;

      int offset;
      for (offset = length; offset > 0; offset--) {
         if (extractShort(bytes, offset - 2) + offset == length && extractInt(bytes, offset - 22) == 101010256) {
            int start = extractInt(bytes, offset - 6);
            if (extractInt(bytes, start) == 33639248) {
               numEntries = extractShort(bytes, offset - 14);
               offset = start;
               break;
            }
         }
      }

      if (offset <= 0) {
         throw new IOException("Cannot locate central directory: " + this._name);
      }

      this._entries = new ZipEntry[numEntries];

      for (int i = 0; i < numEntries; i++) {
         this._entries[i] = new ZipEntry(bytes, offset);
         offset += this._entries[i].getExtent();
      }
   }
}
