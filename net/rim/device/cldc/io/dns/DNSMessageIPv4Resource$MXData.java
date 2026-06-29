package net.rim.device.cldc.io.dns;

import net.rim.device.api.util.DataBuffer;

public class DNSMessageIPv4Resource$MXData {
   public int preference;
   public String domainName;

   public DNSMessageIPv4Resource$MXData(DataBuffer db) {
      this.preference = db.readUnsignedShort();
      this.domainName = DNSMessageIPv4.readDomainName(db);
   }
}
