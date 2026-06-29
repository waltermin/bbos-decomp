package net.rim.device.internal.media;

import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.WLAN;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.device.internal.system.InternalServices;

final class RTSPDataSourceFactoryImpl extends RTSPDataSourceFactory {
   private static final String DEFAULT_USER_AGENT;

   @Override
   public final RTSPDataSource createDataSource(String locator, String userAgent) {
      if (!InternalServices.isSoftwareCapable(13)) {
         return null;
      }

      if (userAgent == null) {
         userAgent = "BlackBerry";
      }

      try {
         URL url = (URL)(new Object(locator));
         String urlToOpen = url.toStringWithoutRIMParams();
         URLParameters params = url.getRIMParameters();
         String apn = null;
         String apnUsername = null;
         String apnPassword = null;
         String int0 = null;
         if (params != null) {
            String connectionUid = params.getValue("connectionuid");
            if (connectionUid != null) {
               ServiceBook sb = ServiceBook.getSB();
               ServiceRecord rec = sb.getRecordByUidAndCid(connectionUid, WPTCPServiceRecord.SERVICE_CID);
               if (rec == null) {
                  throw new Object("Could not find record");
               }

               WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(rec);
               int0 = record.getPropertyAsString(19);
               if (int0 == null) {
                  HostRoutingTable hrt = rec.getAttachedHrt();
                  if (hrt != null) {
                     HostRoutingInfo hri = hrt.getHris()[0];
                     if (hri instanceof GprsHRI) {
                        GprsHRI gprshri = (GprsHRI)hri;
                        apn = gprshri.getApn();
                        apnUsername = gprshri.getApnUsername();
                        apnPassword = gprshri.getApnPassword();
                     }
                  }
               }
            } else {
               int0 = params.getValue("interface");
               if (int0 == null) {
                  apn = params.getValue("apn");
                  if (apn != null) {
                     apnUsername = params.getValue("tunnelauthusername");
                     apnPassword = params.getValue("tunnelauthpassword");
                  }
               }
            }
         }

         if (apn == null) {
            if (int0 == null || int0.equals("cellular")) {
               TunnelCredentialsProvider provider = TunnelCredentialsProvider.getInstance();
               apn = provider.getApn();
               apnUsername = provider.getApnUsername();
               apnPassword = provider.getApnPassword();
            } else if (int0.equals("wifi")) {
               apn = WLAN.WLAN_PSEUDO_APN;
               apnUsername = null;
               apnPassword = null;
            }
         }

         return (RTSPDataSource)(new Object(urlToOpen, userAgent, apn, apnUsername, apnPassword));
      } finally {
         throw new Object("Bad locator");
      }
   }
}
