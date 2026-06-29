package net.rim.device.apps.internal.supl;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardInfo;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.provisioning.SuplHandler;

public final class SuplOptions {
   private boolean TLSEnabled;
   private SetCapabilities setCapabilities = new SetCapabilities();
   private int suplPortNumber;
   private Fqdn slpAddress = new Fqdn();
   private Object[] suplInfo;
   String uri;
   static final int SUPL_PORT = 7275;
   private static PersistentObject persist;
   private static long MY_KEY = -5700456430655177747L;

   SuplOptions() {
      persist = PersistentStore.getPersistentObject(MY_KEY);
      this.suplInfo = (Object[])persist.getContents();
      if (this.suplInfo != null) {
         this.uri = PersistentContent.decodeString(this.suplInfo[0]);
      }

      if (this.isPersistedUriValid()) {
         this.setConnectionPrefix(this.uri);
      } else if (!this.setConnectionPrefix(SuplHandler.getConnectionPrefix())) {
         this.suplPortNumber = 7275;
         this.TLSEnabled = true;

         try {
            byte[] imsi = new byte[8];
            imsi = SIMCardInfo.getIMSI();
            Integer mccInt = (Integer)(new Object(SIMCard.getMCCFromIMSI(imsi)));
            Integer mncInt = (Integer)(new Object(SIMCard.getMNCFromIMSI(imsi)));
            String mccStr = mccInt.toString();
            String mncStr = mncInt.toString();

            while (mncStr.length() < 3) {
               mncStr = ((StringBuffer)(new Object("0"))).append(mncStr).toString();
            }

            while (mccStr.length() < 3) {
               mccStr = ((StringBuffer)(new Object("0"))).append(mccStr).toString();
            }

            this.slpAddress
               .setDomainName(((StringBuffer)(new Object("h-slp.mnc"))).append(mncStr).append(".mcc").append(mccStr).append(".pub.3gppnetwork.org").toString());
            Fqdn fqdn = this.slpAddress;
            fqdn.print();
         } finally {
            return;
         }
      }
   }

   private final boolean isPersistedUriValid() {
      return this.uri == null ? false : !this.uri.equals("");
   }

   public final void setSlpAddress(String domainName) {
      this.slpAddress.setDomainName(domainName);
   }

   public final boolean isTLSEnabled() {
      return this.TLSEnabled;
   }

   public final int getSuplPortNumber() {
      return this.suplPortNumber;
   }

   public final Fqdn getSlpAddress() {
      return this.slpAddress;
   }

   public final SetCapabilities getSetCapabilities() {
      return this.setCapabilities;
   }

   public final boolean setConnectionPrefix(String connectionPrefix) {
      if (connectionPrefix == null) {
         return false;
      }

      URL url;
      try {
         url = (URL)(new Object(connectionPrefix));
      } finally {
         ;
      }

      System.out.println(((StringBuffer)(new Object("SUPL SLP Scheme: "))).append(url.getScheme()).toString());
      System.out.println(((StringBuffer)(new Object("SUPL SLP Host: "))).append(url.getHost()).toString());
      System.out.println(((StringBuffer)(new Object("SUPL SLP Port: "))).append(url.getPort()).toString());
      this.TLSEnabled = url.getScheme().equals("tls");
      this.setSlpAddress(url.getHost());
      this.suplPortNumber = url.getPort();
      return true;
   }
}
