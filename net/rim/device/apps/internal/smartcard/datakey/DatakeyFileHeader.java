package net.rim.device.apps.internal.smartcard.datakey;

import net.rim.device.internal.system.DebugUtilities;

class DatakeyFileHeader {
   private byte[] _header;
   public static final int RSA_BOTH_PUBLIC_KEY_FILE;
   public static final int RSA_BOTH_PRIVATE_KEY_FILE;
   public static final int ATTRIBUTE_FILE;

   public DatakeyFileHeader(byte[] header) {
      if (header != null && header.length == 11) {
         this._header = header;
      } else {
         throw new Object();
      }
   }

   public void printHeader() {
      System.out.println("File Header: ");
      System.out.print("    MaxSize: ");
      DebugUtilities.printArrayContents(this._header, 0, 2);
      System.out.print("    File ID: ");
      DebugUtilities.printArrayContents(this._header, 2, 2);
      System.out.print("    File Type: ");
      DebugUtilities.printArrayContents(this._header, 4, 2);
      System.out.print("    Security Nibles: ");
      DebugUtilities.printArrayContents(this._header, 6, 3);
      System.out.print("    High Water Mark: ");
      DebugUtilities.printArrayContents(this._header, 9, 2);
   }

   public int getFileType() {
      return this._header[5];
   }

   public int getHighWaterMark() {
      int highWaterMark = 0;
      highWaterMark |= this._header[9] << 8 & 0xFF00;
      return highWaterMark | this._header[10] & 0xFF;
   }
}
