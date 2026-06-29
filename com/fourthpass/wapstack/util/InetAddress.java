package com.fourthpass.wapstack.util;

public final class InetAddress {
   private byte[] _netAdd;
   private String _addString;

   public InetAddress(byte[] netadd) {
      if (netadd != null && netadd.length == 4) {
         this._netAdd = netadd;
      }
   }

   public InetAddress(int netadd) {
      this._netAdd = new byte[]{(byte)(netadd >> 24 & 0xFF), (byte)(netadd >> 16 & 0xFF), (byte)(netadd >> 8 & 0xFF), (byte)(netadd & 0xFF)};
   }

   public InetAddress(byte[] netadd, int offset, int length) {
      if (netadd != null && length == 4) {
         this._netAdd = new byte[length];
         System.arraycopy(netadd, offset, this._netAdd, 0, length);
      }
   }

   public InetAddress(String netadd) {
      int index1 = netadd.indexOf(46);
      if (index1 != -1) {
         int index2 = netadd.indexOf(46, index1 + 1);
         if (index2 != -1) {
            int index3 = netadd.indexOf(46, index2 + 1);
            if (index3 != -1) {
               try {
                  byte[] add = new byte[]{
                     (byte)(Integer.parseInt(netadd.substring(0, index1)) & 0xFF),
                     (byte)(Integer.parseInt(netadd.substring(index1 + 1, index2)) & 0xFF),
                     (byte)(Integer.parseInt(netadd.substring(index2 + 1, index3)) & 0xFF),
                     (byte)(Integer.parseInt(netadd.substring(index3 + 1, netadd.length())) & 0xFF)
                  };
                  this._netAdd = add;
               } finally {
                  return;
               }
            }
         }
      }
   }

   public final byte[] getAddress() {
      return this._netAdd;
   }

   public static final InetAddress getByName(String netAdd) {
      return new InetAddress(netAdd);
   }

   public static final InetAddress getLocalHost() {
      return new InetAddress("127.0.0.1");
   }

   public final boolean equals(InetAddress other) {
      return other != null && this.getAddString().equals(other.getAddString());
   }

   public final String getAddString() {
      if (this._addString != null) {
         return this._addString;
      }

      if (this._netAdd != null && this._netAdd.length == 4) {
         StringBuffer buffer = new StringBuffer();
         buffer.append(this._netAdd[0] & 255)
            .append('.')
            .append(this._netAdd[1] & 255)
            .append('.')
            .append(this._netAdd[2] & 255)
            .append('.')
            .append(this._netAdd[3] & 255);
         this._addString = buffer.toString();
      }

      return this._addString;
   }

   @Override
   public final String toString() {
      return this.getAddString();
   }

   public final String getDisplayableString() {
      return this.getAddString();
   }
}
