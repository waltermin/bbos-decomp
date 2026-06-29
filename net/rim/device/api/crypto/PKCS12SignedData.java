package net.rim.device.api.crypto;

public class PKCS12SignedData extends PKCS12ContentInfo {
   private PKCS12ContentInfo _contentInfo;

   public PKCS12SignedData(byte[] data, PKCS12ContentInfo parent) {
      super(data, parent);
   }

   @Override
   public void parse() {
      if (!super._parsed) {
         this._contentInfo = null;
      }
   }

   public PKCS12ContentInfo getData() {
      this.parse();
      return this._contentInfo;
   }

   @Override
   public PKCS12ContentInfo[] getChildrenContentInfos() {
      this.parse();
      return new PKCS12ContentInfo[]{this._contentInfo};
   }
}
