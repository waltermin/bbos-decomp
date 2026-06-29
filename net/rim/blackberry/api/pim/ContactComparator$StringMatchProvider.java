package net.rim.blackberry.api.pim;

class ContactComparator$StringMatchProvider extends ContactComparator$MatchProvider {
   private String _matchString;

   public ContactComparator$StringMatchProvider(String matchString) {
      this._matchString = matchString;
   }

   @Override
   public boolean matches(ContactImpl contact) {
      int[] fields = contact.getFields();

      for (int i = 0; i < fields.length; i++) {
         int field = fields[i];
         int count = contact.countValues(field);
         if (field != 106 && field != 100) {
            for (int j = 0; j < count; j++) {
               String value = contact.getString(field, j);
               if (this.prefixmatch(this._matchString, value)) {
                  return true;
               }
            }
         } else {
            for (int j = 0; j < count; j++) {
               String[] values = contact.getStringArray(field, j);
               int numvals = values.length;

               for (int k = 0; k < numvals; k++) {
                  if (this.prefixmatch(this._matchString, values[k])) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }
}
