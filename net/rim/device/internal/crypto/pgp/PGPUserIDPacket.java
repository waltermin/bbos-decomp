package net.rim.device.internal.crypto.pgp;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class PGPUserIDPacket extends PGPPacket implements Persistable {
   private String _userID;
   private String _name;
   private String _emailAddress;
   private String _blankName;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public PGPUserIDPacket(int tag, byte[] encoding) {
      super(tag, encoding);
      boolean var18 = false /* VF: Semaphore variable */;

      label128:
      try {
         var18 = true;
         this._userID = new String(encoding, "utf-8");
         var18 = false;
      } finally {
         if (var18) {
            this._userID = new String(encoding);
            break label128;
         }
      }

      int startOffset = Arrays.getIndex(encoding, (byte)60);
      int endOffset = Arrays.getIndex(encoding, (byte)62);
      if (startOffset >= 0 && endOffset > startOffset) {
         boolean var14 = false /* VF: Semaphore variable */;

         label120:
         try {
            var14 = true;
            this._name = new String(encoding, 0, startOffset, "utf-8").trim();
            this._emailAddress = new String(encoding, startOffset + 1, endOffset - startOffset - 1, "utf-8");
            var14 = false;
         } finally {
            if (var14) {
               this._name = new String(encoding, 0, startOffset).trim();
               this._emailAddress = new String(encoding, startOffset + 1, endOffset - startOffset - 1);
               break label120;
            }
         }
      } else {
         this._name = this._userID;
      }

      if (this._name == null || this._name.length() == 0) {
         int location = this._emailAddress.indexOf(64);
         if (location != 0) {
            endOffset = location;
            startOffset = Arrays.getIndex(encoding, (byte)60) + 1;
         }

         boolean var10 = false /* VF: Semaphore variable */;

         label109:
         try {
            var10 = true;
            this._blankName = new String(encoding, startOffset, endOffset, "utf-8").trim();
            var10 = false;
         } finally {
            if (var10) {
               this._blankName = new String(encoding, startOffset, endOffset).trim();
               break label109;
            }
         }

         this._name = this._blankName;
      }
   }

   public final String getUserID() {
      return this._userID;
   }

   public final String getName() {
      return this._name;
   }

   public final String getEmailAddress() {
      return this._emailAddress;
   }
}
