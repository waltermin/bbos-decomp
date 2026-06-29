package net.rim.device.api.crypto;

public class PKCS12EnvelopedData extends PKCS12ContentInfo {
   public PKCS12EnvelopedData(byte[] data, PKCS12ContentInfo parent) {
      super(data, parent);
   }

   @Override
   public void parse() {
      if (!super._parsed) {
         ;
      }
   }

   @Override
   public PKCS12ContentInfo[] getChildrenContentInfos() {
      return null;
   }
}
