package net.rim.blackberry.api.pim;

class ContactComparator$MatchProvider {
   public boolean matches(ContactImpl _1) {
      throw null;
   }

   protected boolean prefixmatch(String pre, String val) {
      if (val == null) {
         return false;
      }

      String prefix = pre.toLowerCase();
      String value = val.toLowerCase();
      int index = value.indexOf(prefix);
      if (index == 0) {
         return true;
      }

      while (index != -1) {
         char prevChar = value.charAt(index - 1);
         if (prevChar < '0' || prevChar > 'z' || prevChar > '9' && prevChar < 'a') {
            return true;
         }

         index = value.indexOf(prefix, ++index);
      }

      int len = prefix.length();

      for (int i = 0; i < len; i++) {
         char nextChar = prefix.charAt(i);
         if ((nextChar < '0' || nextChar > 'z' || nextChar > '9' && nextChar < 'a')
            && this.prefixmatch(prefix.substring(0, i), val)
            && (i + 1 == len || this.prefixmatch(prefix.substring(i + 1), val))) {
            return true;
         }
      }

      return false;
   }
}
