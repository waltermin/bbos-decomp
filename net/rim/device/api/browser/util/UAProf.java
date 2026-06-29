package net.rim.device.api.browser.util;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.cldc.io.utility.URIEncoder;

public final class UAProf {
   public static final String SPRINT_UAPROF_URI = "http://device.sprintpcs.com/RIM/BlackBerry{0}/{2}.rdf";

   private UAProf() {
   }

   public static final String getDefaultUAProfURI() {
      return getDefaultUAProfURI(false);
   }

   public static final String getDefaultUAProfURI(boolean useBrandingData) {
      String brandingUAProf = null;
      if (useBrandingData) {
         switch (Branding.getVendorId()) {
            case 104:
            case 213:
            case 225:
               brandingUAProf = "http://device.sprintpcs.com/RIM/BlackBerry{0}/{2}.rdf";
               break;
            default:
               byte[] brandingUAProfBytes = Branding.getData(17152);
               if (brandingUAProfBytes != null) {
                  brandingUAProf = (String)(new Object(brandingUAProfBytes));
               }
         }
      }

      if (brandingUAProf != null) {
         return getFormattedUAProfURI(brandingUAProf);
      }

      StringBuffer result = (StringBuffer)(new Object("http://www.blackberry.net/go/mobile/profiles/uaprof/"));
      URIEncoder.encode(result, DeviceInfo.getDeviceName(), "iso-8859-1", true);
      result.append('/');
      result.append(UserAgent.getBrowserVersion());
      result.append(".rdf");
      return result.toString();
   }

   public static final String getFormattedUAProfURI(String formatString) {
      return formatString == null
         ? null
         : MessageFormat.format(
            formatString,
            new Object[]{
               URIEncoder.encode(null, DeviceInfo.getDeviceName(), "iso-8859-1", true), UserAgent.getBrowserVersion(false), UserAgent.getBrowserVersion(true)
            }
         );
   }
}
