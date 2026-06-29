package net.rim.tools.jar;

import java.io.InputStream;
import net.rim.tools.compiler.exec.CharacterHelper;
import net.rim.tools.compiler.io.StructuredInputStream;

public class Manifest {
   private String _string;
   private byte[] _bytes;

   public Manifest(InputStream inputStream) {
      this._bytes = StructuredInputStream.readAll(inputStream, -1, "META-INF/MANIFEST.MF");
   }

   public byte[] getBytes() {
      return this._bytes;
   }

   public String getString() {
      if (this._string == null) {
         this._string = CharacterHelper.utf8ToString(this._bytes, 0, this._bytes.length);
      }

      return this._string;
   }

   public Attributes getMainAttributes() {
      return null;
   }
}
