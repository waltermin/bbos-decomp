package net.rim.device.apps.internal.lbs.verbs;

import net.rim.device.api.ui.component.ActiveFieldContext;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.LocationDocument;
import net.rim.device.apps.internal.lbs.locator.Directions;
import net.rim.device.cldc.io.utility.URIDecoder;

final class LBSHyperlinkFactory implements Factory {
   private static final boolean isDoubleChar(char c) {
      if (CharacterUtilities.isDigit(c)) {
         return true;
      }

      switch (c) {
         case ',':
            return false;
         case '-':
         case '.':
         default:
            return true;
      }
   }

   final double getParamDouble(String text, int end, String param) {
      int start = text.indexOf(param);
      if (start == -1) {
         return Double.MIN_VALUE;
      }

      start += param.length();
      int term = start + 1;

      while (term < end && isDoubleChar(text.charAt(term))) {
         term++;
      }

      return Double.parseDouble(text.substring(start, term));
   }

   final String getString(String text, int end, String param) {
      int start = text.indexOf(param);
      if (start == -1) {
         return "";
      }

      start += param.length();
      if (start < end && text.charAt(start) != '&') {
         int term = start + 1;

         while (term < end && text.charAt(term) != '&') {
            term++;
         }

         if (term > end) {
            term = end;
         }

         return URIDecoder.decode(text.substring(start, term), "UTF-8");
      } else {
         return "";
      }
   }

   @Override
   public final Object createInstance(Object initialData) {
      if (initialData instanceof Object) {
         ActiveFieldContext afc = (ActiveFieldContext)initialData;
         String text = afc.getData();
         int end = text.length();

         try {
            if (text.indexOf("doc=") > 0) {
               return new LocationDocument(text);
            }

            if (this.getString(text, end, "lat=").length() > 0) {
               int lat = (int)(this.getParamDouble(text, end, "lat=") * 4681608360884174848L);
               int lon = (int)(this.getParamDouble(text, end, "lon=") * 4681608360884174848L);
               String zoomStr = this.getString(text, end, "z=");
               double zoom = (double)4613937818241073152L;
               if (zoomStr != null && !zoomStr.equals("")) {
                  zoom = Double.valueOf(zoomStr);
                  if (zoom == Double.MIN_VALUE) {
                     zoom = (double)4613937818241073152L;
                  }
               }

               String label = this.getString(text, end, "label=");
               String description = this.getString(text, end, "desc=");
               String address = this.getString(text, end, "address=");
               String city = this.getString(text, end, "city=");
               String region = this.getString(text, end, "region=");
               String country = this.getString(text, end, "country=");
               String postalCode = this.getString(text, end, "postalCode=");
               String phone = this.getString(text, end, "phone=");
               String fax = this.getString(text, end, "fax=");
               String url = this.getString(text, end, "url=");
               String email = this.getString(text, end, "email=");
               String categories = this.getString(text, end, "categories=");
               String ratingStr = this.getString(text, end, "rating=");
               double rating = (double)-4616189618054758400L;
               if (ratingStr != null && !ratingStr.equals("")) {
                  rating = Double.valueOf(ratingStr);
                  if (rating == Double.MIN_VALUE) {
                     rating = (double)-4616189618054758400L;
                  }
               }

               String source = this.getString(text, end, "source=");
               String sponsored = this.getString(text, end, "sponsored=");
               return new Location(
                  lat,
                  lon,
                  (int)zoom,
                  label,
                  description,
                  address,
                  city,
                  region,
                  country,
                  postalCode,
                  phone,
                  fax,
                  url,
                  email,
                  categories,
                  rating,
                  source,
                  sponsored,
                  null,
                  null
               );
            } else {
               if (this.getString(text, end, "startLat=").length() > 0
                  && this.getString(text, end, "startLon=").length() > 0
                  && this.getString(text, end, "endLat=").length() > 0
                  && this.getString(text, end, "endLon=").length() > 0) {
                  String label = this.getString(text, end, "label=");
                  String startAddress = this.getString(text, end, "startAddress=");
                  int startLat = (int)(this.getParamDouble(text, end, "startLat=") * 4681608360884174848L);
                  int startLon = (int)(this.getParamDouble(text, end, "startLon=") * 4681608360884174848L);
                  String endAddress = this.getString(text, end, "endAddress=");
                  int endLat = (int)(this.getParamDouble(text, end, "endLat=") * 4681608360884174848L);
                  int endLon = (int)(this.getParamDouble(text, end, "endLon=") * 4681608360884174848L);
                  return new Directions(startAddress, startLat, startLon, endAddress, endLat, endLon);
               }

               if ((this.getString(text, end, "startLat=").length() == 0 || this.getString(text, end, "startLon=").length() == 0)
                  && this.getString(text, end, "endLat=").length() > 0
                  && this.getString(text, end, "endLon=").length() > 0) {
                  String endAddress = this.getString(text, end, "endAddress=");
                  int endLat = (int)(this.getParamDouble(text, end, "endLat=") * 4681608360884174848L);
                  int endLon = (int)(this.getParamDouble(text, end, "endLon=") * 4681608360884174848L);
                  String label = this.getString(text, end, "label=");
                  return new Directions("", Integer.MAX_VALUE, Integer.MAX_VALUE, endAddress, endLat, endLon);
               }

               if (this.getString(text, end, "startLat=").length() <= 0
                  || this.getString(text, end, "startLon=").length() <= 0
                  || this.getString(text, end, "endLat=").length() != 0 && this.getString(text, end, "endLon=").length() != 0) {
                  return null;
               }

               String startAddress = this.getString(text, end, "startAddress=");
               int startLat = (int)(this.getParamDouble(text, end, "startLat=") * 4681608360884174848L);
               int startLon = (int)(this.getParamDouble(text, end, "startLon=") * 4681608360884174848L);
               String label = this.getString(text, end, "label=");
               return new Directions(startAddress, startLat, startLon, "", Integer.MAX_VALUE, Integer.MAX_VALUE);
            }
         } finally {
            ;
         }
      } else {
         return null;
      }
   }
}
