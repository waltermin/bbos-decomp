package net.rim.device.api.servicebook.selector;

import net.rim.vm.Persistable;

final class SRSelectorData implements Persistable {
   public String name;
   public long guid;
   public String cid;
   public int defaultId;
   public boolean userSet;
   public int cbIndex;
   public String stashedUid;
   public boolean stashedUserSet;
   public static final int NULL_CB = -1;

   public SRSelectorData(String n, long g, String c) {
      this.name = n;
      this.guid = g;
      this.cid = c;
      this.userSet = false;
      this.defaultId = -1;
      this.cbIndex = -1;
   }

   public SRSelectorData() {
      this(null, 0, null);
   }
}
