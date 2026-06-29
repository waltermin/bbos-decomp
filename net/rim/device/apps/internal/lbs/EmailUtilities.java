package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.apps.api.messaging.messagelist.ForwardAsVerb;
import net.rim.device.apps.api.messaging.messagelist.MessageAttachment;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class EmailUtilities {
   private EmailUtilities() {
   }

   public static final void emailLocation(int latitude, int longitude, int zoom, boolean emailAsUserLocation) {
      emailLocationInternal(latitude, longitude, zoom, "", "", "", "", "", "", "", "", "", "", "", "", "", emailAsUserLocation);
   }

   public static final void emailLocation(Location location, int zoom) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private static final void emailLocationInternal(
      int latitude,
      int longitude,
      int zoom,
      String label,
      String description,
      String address,
      String city,
      String region,
      String country,
      String postalCode,
      String phone,
      String fax,
      String url,
      String email,
      String categories,
      String rating,
      boolean emailAsUserLocation
   ) {
      StringBuffer body;
      if (emailAsUserLocation) {
         body = (StringBuffer)(new Object(LBSResources.getString(296)));
      } else {
         body = (StringBuffer)(new Object(LBSResources.getString(29)));
      }

      body.append("\n\n");
      body.append(
         Utilities.createLbsUrl(
            latitude, longitude, zoom, label, description, address, city, region, country, postalCode, phone, fax, url, email, categories, rating
         )
      );
      String subject = "";
      if (label != null && !label.equals("")) {
         subject = MessageFormat.format(LBSResources.getString(120), new Object[]{label});
      } else {
         subject = LBSResources.getString(28);
      }

      createEmail(subject, body.toString(), false);
   }

   public static final void emailDirections(Route route) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private static final void emailDirectionsInternal(
      String startAddress, int startLatitude, int startLongitude, String endAddress, int endLatitude, int endLongitude, Route route
   ) {
      StringBuffer body = (StringBuffer)(new Object(LBSResources.getString(234)));
      body.append("\n\n");
      body.append(Utilities.createDirectionsUrl(startAddress, startLatitude, startLongitude, endAddress, endLatitude, endLongitude));
      if (route != null) {
         body.append("\n");

         for (int i = 0; i < route._decisions._count; i++) {
            body.append(((StringBuffer)(new Object("\n"))).append(route._decisions.getAt(i)._label).toString());
         }
      }

      String subject = "";
      if (startAddress != null && startAddress.length() > 0 && endAddress != null && endAddress.length() > 0) {
         subject = MessageFormat.format(LBSResources.getString(236), new Object[]{startAddress, endAddress});
      } else if (startAddress != null && startAddress.length() > 0) {
         subject = ((StringBuffer)(new Object())).append(LBSResources.getString(107)).append(" ").append(startAddress).toString();
      } else if (endAddress != null && endAddress.length() > 0) {
         subject = ((StringBuffer)(new Object())).append(LBSResources.getString(106)).append(" ").append(endAddress).toString();
      } else {
         subject = LBSResources.getString(235);
      }

      createEmail(subject, body.toString(), true);
   }

   static final void createEmail(String subject, String body, boolean asDirections) {
      createEmail(subject, body, asDirections, null);
   }

   static final void createEmail(String subject, String body, boolean asDirections, MessageAttachment attachment) {
      EmailUtilities$BrowserMessagePartsProvider bmpr = new EmailUtilities$BrowserMessagePartsProvider(body, subject, attachment);
      ForwardAsVerb fwVerb = (ForwardAsVerb)(new Object(bmpr, 0, LBSResources.getResourceBundle(), asDirections ? 275 : 274));
      if (fwVerb.canInvoke(null)) {
         fwVerb.invoke(null);
      }
   }
}
