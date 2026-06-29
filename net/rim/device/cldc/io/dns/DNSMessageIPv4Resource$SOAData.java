package net.rim.device.cldc.io.dns;

import net.rim.device.api.util.DataBuffer;

public class DNSMessageIPv4Resource$SOAData {
   public String mName;
   public String rName;
   public int serial;
   public int refresh;
   public int retry;
   public int expire;
   public int minimum;

   public DNSMessageIPv4Resource$SOAData(DataBuffer db) {
      this.mName = DNSMessageIPv4.readDomainName(db);
      this.rName = DNSMessageIPv4.readDomainName(db);
      this.serial = db.readInt();
      this.refresh = db.readInt();
      this.retry = db.readInt();
      this.expire = db.readInt();
      this.minimum = db.readInt();
   }
}
