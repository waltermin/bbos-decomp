package simulationservicebook;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.hrt.CdmaHRI;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.hrt.IdenHRI;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.DebugSupport;
import net.rim.vm.Process;

public final class InsertInternetServiceBook {
   private static String APN = "rim.net.gprs";
   private static String EMPTY_APN = "";
   private static String IPPP_HRTNAME = "SimNet";
   private static String _mdsIP = "127.0.0.1";
   private static int PORT = 19781;
   private static String _portValue = "19780";
   private static String _ippp_relayip = null;
   private static String MAIL_HRTNAME = "SimNet_mail";
   private static final long GUID;

   public static final void libMain(String[] args) {
      EventLogger.register(-3363160933426248541L, "InsertInternetServiceBook");
      if (!DeviceInfo.isSimulator()) {
         System.out.println("This COD file is for simulator only!!!... Returning...");
      } else {
         String ipParam = DebugSupport.getenv("MdsIP");
         if (ipParam != null) {
            _mdsIP = ipParam;
         }

         String valueParam = DebugSupport.getenv("IPPPSourcePort");
         if (valueParam != null) {
            _portValue = valueParam;
         }

         _ippp_relayip = ((StringBuffer)(new Object())).append(_mdsIP).append(":").append(PORT).append(":").append(_portValue).toString();
         System.out.println("Setting up simulation SB entries...");
         new InsertInternetServiceBook();
         String apn = null;
         switch (RadioInfo.getNetworkType()) {
            case 3:
            case 7:
               apn = APN;
               break;
            default:
               apn = EMPTY_APN;
         }

         byte[] ipppData = new byte[11];
         ipppData[0] = 16;
         ipppData[1] = 8;
         int pin = DeviceInfo.getDeviceId();
         ipppData[2] = (byte)(pin >> 24);
         ipppData[3] = (byte)(pin >> 16);
         ipppData[4] = (byte)(pin >> 8);
         ipppData[5] = (byte)(pin & 0xFF);
         ipppData[6] = 0;
         ipppData[7] = 0;
         ipppData[8] = 0;
         ipppData[9] = 0;
         ipppData[10] = 0;
         StringBuffer sb = (StringBuffer)(new Object(((StringBuffer)(new Object("udp://"))).append(_mdsIP).append(":").append(PORT).append(";").toString()));
         sb.append(_portValue);
         sb.append('/');
         sb.append(apn);
         initializeStack(sb.toString(), ipppData, -3694417701388914415L);
         runStartupItems();
      }
   }

   private static final void runStartupItems() {
      String url = DebugSupport.getenv("url");
      String appToLaunch = DebugSupport.getenv("launch");
      if (appToLaunch != null && (appToLaunch.indexOf("www.") == 0 || appToLaunch.indexOf("http://") == 0 || appToLaunch.indexOf("https://") == 0)) {
         url = appToLaunch;
      }

      if (url != null) {
         Browser.getDefaultSession().displayPage(url);
      } else if (appToLaunch != null) {
         int handle = CodeModuleManager.getModuleHandle(appToLaunch);
         if (handle == 0) {
            int indexOfdotCod = appToLaunch.indexOf(".cod");
            if (indexOfdotCod > 0) {
               String attempt2 = appToLaunch.substring(0, indexOfdotCod);
               handle = CodeModuleManager.getModuleHandle(attempt2);
            }
         }

         if (handle != 0) {
            ApplicationDescriptor[] appDesc = CodeModuleManager.getApplicationDescriptors(handle);
            if (appDesc != null) {
               ApplicationManager appMan = ApplicationManager.getApplicationManager();

               try {
                  int pid = appMan.runApplication(appDesc[0]);
                  if (pid != 0) {
                     appMan.requestForeground(pid);
                     Process.getProcess(pid);
                  }
               } finally {
                  return;
               }
            }
         } else {
            String appNotFoundMsg = ((StringBuffer)(new Object("app not found:"))).append(appToLaunch).toString();
            EventLogger.logEvent(-3363160933426248541L, ((StringBuffer)(new Object("runStartupItems(): "))).append(appNotFoundMsg).toString().getBytes());
            System.err.println(((StringBuffer)(new Object())).append(appNotFoundMsg).append(" Check if the file exists.").toString());
         }
      }
   }

   private static final void initializeStack(String uri, byte[] data, long guid) {
      Proxy p = Proxy.getInstance();
      InsertInternetServiceBook$Helper h = new InsertInternetServiceBook$Helper(null);
      p.submitRunnable(new InsertInternetServiceBook$1(uri, data, guid, h));
      synchronized (h._lock) {
         if (!h._hasrun) {
            try {
               h._lock.wait();
            } finally {
               return;
            }
         }
      }
   }

   private InsertInternetServiceBook() {
      HostRoutingTable hrt = getHrtForMail();
      InsertInternetServiceBook$SBEntry mail = new InsertInternetServiceBook$SBEntry(
         "S 00005", "CMIME", "Email", 0, 1, 1, "Service book configuration for Email", new InsertInternetServiceBook$2(this), hrt
      );
      mail.add(false);
      hrt = getHrtForIPPP();
      InsertInternetServiceBook$SBEntry ippp = new InsertInternetServiceBook$SBEntry(
         "S 00005", BrowserConfigRecord.IPPP_SERVICE_CID, "Internet", 0, 1, 1, "Internet connection for simulation", new InsertInternetServiceBook$3(this), hrt
      );
      InsertInternetServiceBook$SBEntry browserconfig = new InsertInternetServiceBook$SBEntry(
         "S 00005",
         BrowserConfigRecord.SERVICE_CID,
         "Browser Configuration",
         0,
         1,
         1,
         "Configuration for running with MDS",
         new InsertInternetServiceBook$4(this),
         hrt
      );
      ippp.add(false);
      browserconfig.add(false);
      hrt = getHrtForMail();
      InsertInternetServiceBook$SBEntry syncSB = new InsertInternetServiceBook$SBEntry(
         "S 00005", "SYNC", "Sync Service book", 0, 1, 1, "Sync server service book", new InsertInternetServiceBook$5(this), hrt
      );
      syncSB.setDSID("-1");
      syncSB.add(false);
   }

   private static final HostRoutingTable getHrtForIPPP() {
      HostRoutingTable hrt = (HostRoutingTable)(new Object());
      switch (RadioInfo.getNetworkType()) {
         case 2:
         case 6:
            return null;
         case 3:
         case 7:
         default:
            GprsHRI ghri = (GprsHRI)(new Object());
            long[] dacs = new long[]{IPv4UdpDAC.string2Addr(_ippp_relayip)};
            ((IPv4UdpDAC)ghri.getDac()).setAddresses(dacs);
            ghri.setApn(APN);
            ghri.setName(IPPP_HRTNAME);
            long npc = HRUtils.getNpcForActiveNetwork();
            if (npc != -1) {
               ghri.setNpc(npc);
            } else {
               ghri.setNpc((long)48);
               System.out.println("NPC set to base");
            }

            if (hrt.addHri(ghri)) {
               System.out.println("Sim: (IPPP) GRPS HRT Entry entry accepted!");
               hrt.commit();
               return hrt;
            }

            System.out.println("FAILED! Sim: (IPPP) GPRS HRT Entry");
            return hrt;
         case 4:
            CdmaHRI hri = (CdmaHRI)(new Object());
            long[] dacs = new long[]{IPv4UdpDAC.string2Addr(_ippp_relayip)};
            ((IPv4UdpDAC)hri.getDac()).setAddresses(dacs);
            hri.setName(IPPP_HRTNAME);
            long npc = HRUtils.getNpcForActiveNetwork();
            if (npc != -1) {
               hri.setNpc(npc);
            } else {
               hri.setNpc((long)64);
               System.out.println("NPC set to base");
            }

            if (hrt.addHri(hri)) {
               System.out.println("Sim: (IPPP) Cdma HRT Entry entry accepted!");
               hrt.commit();
               return hrt;
            }

            System.out.println("FAILED! Sim: (IPPP) Cdma HRT Entry");
            return hrt;
         case 5:
            IdenHRI hri = (IdenHRI)(new Object());
            long[] dacs = new long[]{IPv4UdpDAC.string2Addr(_ippp_relayip)};
            ((IPv4UdpDAC)hri.getDac()).setAddresses(dacs);
            hri.setName(IPPP_HRTNAME);
            long npc = HRUtils.getNpcForActiveNetwork();
            if (npc != -1) {
               hri.setNpc(npc);
            } else {
               hri.setNpc((long)80);
               System.out.println("NPC set to base");
            }

            if (hrt.addHri(hri)) {
               System.out.println("Sim: (IPPP) Iden HRT Entry entry accepted!");
               hrt.commit();
               return hrt;
            } else {
               System.out.println("FAILED! Sim: (IPPP) Iden HRT Entry");
               return hrt;
            }
      }
   }

   private static final HostRoutingTable getHrtForMail() {
      HostRoutingTable hrt = (HostRoutingTable)(new Object());
      switch (RadioInfo.getNetworkType()) {
         case 2:
         case 6:
            return null;
         case 3:
         case 7:
         default:
            GprsHRI ghri = (GprsHRI)(new Object());
            long[] dacs = new long[]{IPv4UdpDAC.string2Addr(_ippp_relayip)};
            ((IPv4UdpDAC)ghri.getDac()).setAddresses(dacs);
            ghri.setApn(APN);
            ghri.setName(MAIL_HRTNAME);
            long npc = HRUtils.getNpcForActiveNetwork();
            if (npc != -1) {
               ghri.setNpc(npc);
            } else {
               ghri.setNpc((long)48);
               System.out.println("NPC set to base");
            }

            if (hrt.addHri(ghri)) {
               System.out.println("Sim: (Mail) GRPS HRT Entry entry accepted!");
               hrt.commit();
               return hrt;
            }

            System.out.println("FAILED! Sim: (Mail) GPRS HRT Entry");
            return hrt;
         case 4:
            CdmaHRI hri = (CdmaHRI)(new Object());
            long[] dacs = new long[]{IPv4UdpDAC.string2Addr(_ippp_relayip)};
            ((IPv4UdpDAC)hri.getDac()).setAddresses(dacs);
            hri.setName(MAIL_HRTNAME);
            long npc = HRUtils.getNpcForActiveNetwork();
            if (npc != -1) {
               hri.setNpc(npc);
            } else {
               hri.setNpc((long)64);
               System.out.println("NPC set to base");
            }

            if (hrt.addHri(hri)) {
               System.out.println("Sim: (Mail) Cdma HRT Entry entry accepted!");
               hrt.commit();
               return hrt;
            }

            System.out.println("FAILED! Sim: (Mail) Cdma HRT Entry");
            return hrt;
         case 5:
            IdenHRI hri = (IdenHRI)(new Object());
            long[] dacs = new long[]{IPv4UdpDAC.string2Addr(_ippp_relayip)};
            ((IPv4UdpDAC)hri.getDac()).setAddresses(dacs);
            hri.setName(MAIL_HRTNAME);
            long npc = HRUtils.getNpcForActiveNetwork();
            if (npc != -1) {
               hri.setNpc(npc);
            } else {
               hri.setNpc((long)80);
               System.out.println("NPC set to base");
            }

            if (hrt.addHri(hri)) {
               System.out.println("Sim: (Mail) Iden HRT Entry entry accepted!");
               hrt.commit();
               return hrt;
            } else {
               System.out.println("FAILED! Sim: (Mail) Iden HRT Entry");
               return hrt;
            }
      }
   }
}
