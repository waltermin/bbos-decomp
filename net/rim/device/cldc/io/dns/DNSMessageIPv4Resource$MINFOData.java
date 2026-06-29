package net.rim.device.cldc.io.dns;

import net.rim.device.api.util.DataBuffer;

public final class DNSMessageIPv4Resource$MINFOData {
   public String rMailBox;
   public String eMailBox;

   public DNSMessageIPv4Resource$MINFOData(DataBuffer db) {
      this.rMailBox = DNSMessageIPv4.readDomainName(db);
      this.eMailBox = DNSMessageIPv4.readDomainName(db);
   }
}
