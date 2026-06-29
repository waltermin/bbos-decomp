package net.rim.device.apps.internal.lbs.locator;

import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.internal.lbs.Location;

public final class SearchUtility {
   private SearchUtility() {
   }

   public static final void parseLocationName(Location loc, String addressWhole, MailingAddressModel model) {
      int start = addressWhole.lastIndexOf(44) + 2;
      int end = addressWhole.length();
      if (start < end) {
         String[] address = new Object[4];

         for (int i = 3; start > -1 && i > -1; i--) {
            address[i] = addressWhole.substring(start, end);
            end = start - 2;
            if (end < 2) {
               end = 0;
            }

            start = addressWhole.lastIndexOf(44, end - 1);
            if (start == -1) {
               start = 0;
            } else {
               start += 2;
            }
         }

         if (model != null && model.getAddressLine1() != null && model.getAddressLine1().length() > 0 && address[0].length() == 0) {
            address[0] = address[1];
            address[1] = "";
         }

         if (model != null && model.getCity() != null && model.getCity().length() > 0 && address[1].length() == 0) {
            address[1] = address[2];
            address[2] = "";
         }

         if (address[1].indexOf("(") != -1) {
            address[1] = address[1].substring(0, address[1].indexOf("("));
         }

         loc._address = address[0];
         loc._city = address[1];
         loc._region = address[2];
         loc._country = address[3];
      }
   }
}
