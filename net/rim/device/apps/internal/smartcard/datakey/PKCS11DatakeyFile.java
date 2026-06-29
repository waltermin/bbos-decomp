package net.rim.device.apps.internal.smartcard.datakey;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.rim.device.api.compress.ZLibInputStream;
import net.rim.device.api.util.IntHashtable;
import net.rim.vm.Array;

class PKCS11DatakeyFile extends DatakeyFile {
   IntHashtable _attributes;
   private static final int BLOCK_SIZE = 1024;

   public PKCS11DatakeyFile(DatakeyFileHeader fileHeader, byte[] fileContents) {
      super(fileHeader);
      if (fileContents[0] == -2 && fileContents[1] == -19 && fileContents[2] == -64 && fileContents[3] == -34) {
         InputStream inputStream = new ByteArrayInputStream(fileContents, 6, fileContents.length - 6);
         inputStream = new ZLibInputStream(inputStream);

         label27:
         try {
            fileContents = readStreamCompletely(inputStream);
         } finally {
            break label27;
         }
      }

      this.setContents(fileContents);
      this.parseAttributes();
   }

   private void parseAttributes() {
      this._attributes = PKCS11Parser.getAttributes(this.getContents());
   }

   public PKCS11Attribute getAttribute(int attribute) {
      return (PKCS11Attribute)this._attributes.get(attribute);
   }

   public boolean exists(int attribute) {
      return this._attributes.get(attribute) != null;
   }

   public static byte[] readStreamCompletely(InputStream in) {
      byte[] textBytes = new byte[1024];
      int numTextBytes = 0;

      while (true) {
         int bytesRead = in.read(textBytes, numTextBytes, 1024);
         if (bytesRead == -1) {
            Array.resize(textBytes, numTextBytes);
            return textBytes;
         }

         numTextBytes += bytesRead;
         Array.resize(textBytes, numTextBytes + 1024);
      }
   }
}
