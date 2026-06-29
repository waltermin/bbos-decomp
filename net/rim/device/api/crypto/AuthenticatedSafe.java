package net.rim.device.api.crypto;

import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.util.Arrays;

public class AuthenticatedSafe extends PKCS12ContentInfo {
   private PKCS12ContentInfo[] _contentInfos;

   public AuthenticatedSafe(byte[] data, PKCS12ContentInfo parent) {
      super(data, parent);
   }

   @Override
   public void parse() throws PKCS12ParsingException {
      if (!super._parsed) {
         try {
            ASN1InputByteArray infoByteArrays = new ASN1InputByteArray(super._buffer);
            infoByteArrays.readSequence();

            for (int next = infoByteArrays.peekNextTag(); next != -1 && next == 16; next = infoByteArrays.peekNextTag()) {
               PKCS12ContentInfo contentInfo = PKCS12ContentInfo.getInstance(infoByteArrays.readFieldAsByteArray(), this);
               if (contentInfo != null) {
                  if (this._contentInfos == null) {
                     this._contentInfos = new PKCS12ContentInfo[]{contentInfo};
                  } else {
                     Arrays.add(this._contentInfos, contentInfo);
                  }
               }
            }

            super._parsed = true;
         } finally {
            throw new PKCS12ParsingException();
         }
      }
   }

   @Override
   public PKCS12ContentInfo[] getChildrenContentInfos() {
      this.parse();
      return this._contentInfos;
   }
}
