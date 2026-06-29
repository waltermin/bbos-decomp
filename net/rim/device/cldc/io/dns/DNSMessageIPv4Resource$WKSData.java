package net.rim.device.cldc.io.dns;

import net.rim.device.api.util.DataBuffer;

public class DNSMessageIPv4Resource$WKSData {
   public byte[] address = new byte[4];
   public int protocol;
   public byte[] bitMap;

   public DNSMessageIPv4Resource$WKSData(DataBuffer db, int rdlLength) {
      db.readFully(this.address);
      this.protocol = db.readUnsignedByte();
      this.bitMap = new byte[rdlLength - 4 - 1];
      db.readFully(this.bitMap);
   }
}
