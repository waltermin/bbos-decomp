package net.rim.tools.jar;

import java.io.InputStream;
import net.rim.tools.compiler.exec.MyArrays;

public class JarEntry {
   private byte[] _bytes;
   private int _offset;
   private int _compression;
   private int _compressedLength;
   private String _name;
   private int _uncompressedLength;
   private static final int STORED;
   private static final int DEFLATE;
   private static final int BUFFER_SIZE;
   private static final int TRAILER_SIZE;
   private static final int TRAILER_FIRST_BYTE;

   private final void checkCompression(int compression) {
      if (compression != 0 && compression != 8) {
         throw new Object(((StringBuffer)(new Object("bad file header compression method: "))).append(compression).toString());
      }
   }

   JarEntry(byte[] bytes, int offset) {
      this._bytes = bytes;
      int signature = JarFile.extractInt(this._bytes, offset);
      if (signature != 33639248) {
         throw new Object(((StringBuffer)(new Object("missing central directory header signature: "))).append(this._name).toString());
      }

      offset = offset + 4 + 2 + 2;
      int flags = JarFile.extractShort(this._bytes, offset);
      if ((flags & 1) != 0) {
         throw new Object("encrypted input not supported");
      }

      offset += 2;
      this._compression = JarFile.extractShort(this._bytes, offset);
      this.checkCompression(this._compression);
      offset = offset + 2 + 2 + 2 + 4;
      this._compressedLength = JarFile.extractInt(this._bytes, offset);
      offset += 4;
      this._uncompressedLength = JarFile.extractInt(this._bytes, offset);
      offset += 4;
      int nameLength = JarFile.extractShort(this._bytes, offset);
      offset += 2;
      int extraLength = JarFile.extractShort(this._bytes, offset);
      offset += 2;
      int commentLength = JarFile.extractShort(this._bytes, offset);
      offset = offset + 2 + 2 + 2 + 4;
      int relativeOffset = JarFile.extractInt(this._bytes, offset);
      offset += 4;
      this._name = extractString(this._bytes, offset, nameLength);
      offset = offset + nameLength + extraLength + commentLength;
      offset = relativeOffset;
      signature = JarFile.extractInt(this._bytes, offset);
      if (signature != 67324752) {
         throw new Object("bad local file header signature");
      }

      offset = offset + 4 + 2 + 2 + 2 + 2 + 2 + 4 + 4 + 4;
      int nameLen = JarFile.extractShort(this._bytes, offset);
      offset += 2;
      int extraLen = JarFile.extractShort(this._bytes, offset);
      offset += 2;
      offset = offset + nameLen + extraLen;
      this._offset = offset;
   }

   private static String extractString(byte[] bytes, int offset, int length) {
      StringBuffer buff = (StringBuffer)(new Object(length));

      for (int i = 0; i < length; i++) {
         buff.append((char)bytes[offset + i]);
      }

      return buff.toString();
   }

   JarEntry(InputStream in) {
      in.skip(2);
      int gpbf = JarInputStream.readShort(in);
      boolean trailer = (gpbf & 8) != 0;
      this._compression = JarInputStream.readShort(in);
      this.checkCompression(this._compression);
      in.skip(8);
      this._compressedLength = JarInputStream.readInt(in);
      this._uncompressedLength = JarInputStream.readInt(in);
      int nameLen = JarInputStream.readShort(in);
      int extraLen = JarInputStream.readShort(in);
      byte[] nameBytes = new byte[nameLen];
      if (in.read(nameBytes, 0, nameLen) != nameLen) {
         throw new Object("unable to read entry name");
      }

      this._name = extractString(nameBytes, 0, nameLen);
      byte[] var11 = null;
      in.skip(extraLen);
      if (!trailer) {
         this._bytes = new byte[this._compressedLength];
         int have = 0;

         while (have < this._bytes.length) {
            int got = in.read(this._bytes, have, this._bytes.length - have);
            if (got < 0) {
               throw new Object("unable to read all data bytes");
            }

            have += got;
         }
      } else {
         this._bytes = new byte[4096];
         int have = 0;
         int sofar = 16;

         while (true) {
            while (have >= sofar) {
               int offset = sofar - 16;
               if (this._bytes[offset] == 80) {
                  int signature = JarFile.extractInt(this._bytes, offset);
                  if (signature == 134695760) {
                     this._compressedLength = JarFile.extractInt(this._bytes, offset + 8);
                     if (this._compressedLength == offset) {
                        this._uncompressedLength = JarFile.extractInt(this._bytes, sofar - 4);
                        return;
                     }
                  }
               }

               if (sofar + 16 >= this._bytes.length) {
                  this._bytes = MyArrays.resize(this._bytes, sofar + 4096);
               }

               offset++;

               while (this._bytes[offset] != 80 && offset < sofar) {
                  offset++;
               }

               sofar = offset + 16;
            }

            int got = in.read(this._bytes, have, sofar - have);
            if (got < 0) {
               throw new Object("unable to find trailer bytes");
            }

            have += got;
         }
      }
   }

   public String getName() {
      return this._name;
   }

   public boolean isDirectory() {
      return this._name.charAt(this._name.length() - 1) == '/';
   }

   public int getSize() {
      return this._uncompressedLength;
   }

   InputStream makeInputStream() {
      InputStream in = (InputStream)(new Object(this._bytes, this._offset, this._compressedLength));
      if (this._compression == 8) {
         in = (InputStream)(new Object(in, true));
      }

      return in;
   }
}
