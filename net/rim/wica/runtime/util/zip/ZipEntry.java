package net.rim.wica.runtime.util.zip;

import java.io.InputStream;

public class ZipEntry {
   private byte[] _bytes;
   private int _compressedSize;
   private int _extent;
   private int _method;
   private String _name;
   private int _offset;
   private int _size;
   public static final int STORED = 0;
   public static final int DEFLATE = 8;

   ZipEntry(byte[] bytes, int offset) {
      this._bytes = bytes;
      int start = offset;
      int signature = ZipFile.extractInt(this._bytes, offset);
      if (signature != 33639248) {
         throw new Object(((StringBuffer)(new Object("Missing central directory header signature: "))).append(this._name).toString());
      }

      offset = offset + 4 + 2 + 2;
      int flags = ZipFile.extractShort(this._bytes, offset);
      if ((flags & 1) != 0) {
         throw new Object("Encrypted input not supported.");
      }

      offset += 2;
      this._method = ZipFile.extractShort(this._bytes, offset);
      this.checkMethod(this._method);
      offset = offset + 2 + 2 + 2 + 4;
      this._compressedSize = ZipFile.extractInt(this._bytes, offset);
      offset += 4;
      this._size = ZipFile.extractInt(this._bytes, offset);
      offset += 4;
      int nameLength = ZipFile.extractShort(this._bytes, offset);
      offset += 2;
      int extraLength = ZipFile.extractShort(this._bytes, offset);
      offset += 2;
      int commentLength = ZipFile.extractShort(this._bytes, offset);
      offset = offset + 2 + 2 + 2 + 4;
      int relativeOffset = ZipFile.extractInt(this._bytes, offset);
      offset += 4;
      this._name = ZipFile.extractString(this._bytes, offset, nameLength);
      offset = offset + nameLength + extraLength + commentLength;
      this._extent = offset - start;
      offset = relativeOffset;
      signature = ZipFile.extractInt(this._bytes, offset);
      if (signature != 67324752) {
         throw new Object("Bad local file header signature.");
      }

      offset = offset + 4 + 2 + 2 + 2 + 2 + 2 + 4 + 4 + 4;
      int nameLen = ZipFile.extractShort(this._bytes, offset);
      offset += 2;
      int extraLen = ZipFile.extractShort(this._bytes, offset);
      offset += 2;
      offset = offset + nameLen + extraLen;
      this._offset = offset;
   }

   public int getCompressedSize() {
      return this._compressedSize;
   }

   public String getName() {
      return this._name;
   }

   public int getMethod() {
      return this._method;
   }

   public int size() {
      return this._size;
   }

   public boolean isDirectory() {
      return this._name.charAt(this._name.length() - 1) == '/';
   }

   int getExtent() {
      return this._extent;
   }

   InputStream getInputStream() {
      InputStream in = (InputStream)(new Object(this._bytes, this._offset, this._compressedSize));
      if (this._method == 8) {
         in = (InputStream)(new Object(in, true));
      }

      return in;
   }

   private final void checkMethod(int method) {
      if (method != 0 && method != 8) {
         throw new Object(((StringBuffer)(new Object("Bad file header compression method: "))).append(method).toString());
      }
   }
}
