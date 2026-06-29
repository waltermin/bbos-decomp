package net.rim.device.api.util;

public class StringPattern$Match {
   public int beginIndex;
   public int endIndex;
   public long id;
   public int prefixLength;

   public void setMatch(StringPattern$Match match) {
      this.beginIndex = match.beginIndex;
      this.endIndex = match.endIndex;
      this.id = match.id;
      this.prefixLength = match.prefixLength;
   }
}
