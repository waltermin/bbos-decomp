package net.rim.device.cldc.io.dns;

public final class DNSCachedRR {
   private int _type;
   private int _expiryTime;
   private Object _data;

   public DNSCachedRR(DNSMessageIPv4Resource resource, int timeStamp) {
      this._type = resource.getType();
      this._expiryTime = timeStamp + resource.getTTL();
      switch (this._type) {
         case 6:
            this._data = ((DNSMessageIPv4Resource$SOAData)resource.getData()).mName;
            return;
         case 11:
            this._data = ((DNSMessageIPv4Resource$WKSData)resource.getData()).address;
            return;
         case 14:
            this._data = ((DNSMessageIPv4Resource$MINFOData)resource.getData()).rMailBox;
            return;
         case 15:
            this._data = ((DNSMessageIPv4Resource$MXData)resource.getData()).domainName;
            return;
         default:
            this._data = resource.getData();
      }
   }

   public final int getType() {
      return this._type;
   }

   public final int getExpiryTime() {
      return this._expiryTime;
   }

   public final Object getData() {
      return this._data;
   }
}
