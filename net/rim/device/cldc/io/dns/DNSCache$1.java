package net.rim.device.cldc.io.dns;

import net.rim.device.api.util.Comparator;

class DNSCache$1 implements Comparator {
   private final DNSCache this$0;

   DNSCache$1(DNSCache _1) {
      this.this$0 = _1;
   }

   @Override
   public int compare(Object o1, Object o2) {
      return ((DNSCacheNode)o1).getExpiryTime() - ((DNSCacheNode)o2).getExpiryTime();
   }
}
