package net.rim.tools.jar;

import java.io.InputStream;

public class JarInputStream extends InputStream {
   private InputStream _inputStream;
   private InputStream _entryStream;
   private JarEntry _firstEntry;
   private Manifest _manifest;
   private static byte[] _four = new byte[4];
   private static byte[] _two = new byte[2];

   public JarInputStream(InputStream inputStream, boolean verify) {
      this._inputStream = inputStream;
      int signature = readInt(this._inputStream);
      if (signature != 67324752) {
         throw new Object(((StringBuffer)(new Object("bad local file header signature: 0x"))).append(Integer.toHexString(signature)).toString());
      }

      this._firstEntry = new JarEntry(this._inputStream);
      if (this._firstEntry != null && !this._firstEntry.isDirectory()) {
         String entryName = this._firstEntry.getName();
         if (entryName.regionMatches(true, 0, "META-INF/MANIFEST.MF", 0, 20)) {
            InputStream in = this._firstEntry.makeInputStream();
            this._manifest = new Manifest(in);
            in.close();
            this._firstEntry = null;
         }
      }
   }

   public Manifest getManifest() {
      return this._manifest;
   }

   static int readShort(InputStream in) {
      synchronized (_two) {
         byte[] bytes = _two;
         if (in.read(bytes, 0, bytes.length) != bytes.length) {
            throw new Object("unable to read short");
         } else {
            return (bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8) & 65535;
         }
      }
   }

   static int readInt(InputStream in) {
      synchronized (_four) {
         byte[] bytes = _four;
         if (in.read(bytes, 0, bytes.length) != bytes.length) {
            throw new Object("unable to read int");
         } else {
            return bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF) << 16 | bytes[3] << 24;
         }
      }
   }

   public JarEntry getNextJarEntry() {
      if (this._inputStream == null) {
         throw new Object("stream closed");
      }

      JarEntry jarEntry = null;
      if (this._firstEntry != null) {
         jarEntry = this._firstEntry;
         this._firstEntry = null;
      } else {
         int signature = readInt(this._inputStream);
         if (signature == 67324752) {
            jarEntry = new JarEntry(this._inputStream);
         } else if (signature != 33639248) {
            throw new Object(((StringBuffer)(new Object("bad central file header signature: 0x"))).append(Integer.toHexString(signature)).toString());
         }
      }

      if (jarEntry != null) {
         this._entryStream = jarEntry.makeInputStream();
      }

      return jarEntry;
   }

   public void closeEntry() {
      this._entryStream.close();
      this._entryStream = null;
   }

   @Override
   public int read() {
      if (this._entryStream == null) {
         throw new Object("stream closed");
      } else {
         return this._entryStream.read();
      }
   }

   @Override
   public int read(byte[] buffer, int offset, int length) {
      if (this._entryStream == null) {
         throw new Object("stream closed");
      } else {
         return this._entryStream.read(buffer, offset, length);
      }
   }

   @Override
   public void close() {
      this._firstEntry = null;
      this._manifest = null;
      if (this._entryStream != null) {
         this._entryStream.close();
         this._entryStream = null;
      }

      byte[] buffer = new byte[128];

      while (this._inputStream.read(buffer, 0, buffer.length) != -1) {
      }

      byte[] var2 = null;
      this._inputStream.close();
      this._inputStream = null;
   }
}
