package net.rim.device.cldc.impl.hrt;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.DebugSupport;

public final class Registration {
   private static final String BLACKBERRY_NET_APN;
   private static final String EMPTY_APN;
   private static final String SIMULATOR_APN;
   private static final long HR_REQ_THREAD_STATE_GUID;
   private static final int SUPPORTED_WAFS = RadioInfo.getSupportedWAFs();

   public static final void HRTMain(String[] args) {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      Proxy p = Proxy.getInstance();
      HRUtils hru = (HRUtils)(new Object());
      reg.put(1308825735943519978L, hru);
      p.addRadioListener(hru);
      SIMCard.addListener(p, hru);
      PersistentObject persist = RIMPersistentStore.getPersistentObject(967027728804687787L);
      HRTReqThreadState state;
      if ((state = (HRTReqThreadState)persist.getContents()) == null) {
         state = new HRTReqThreadState();
         state.setFlag(7);
         persist.setContents(state, 51, false);
         persist.commit();
      }

      registerDefaultRegistrationHrt(reg, hru, state);
      registerDefaultHrt(reg, hru, state);
      HRTRequestThread hrtRequester = new HRTRequestThread(state);
      p.startThread(hrtRequester);
      reg.put(6176832563945898679L, new HRThunksImpl());
      reg.put(4019666953250015899L, hrtRequester);
      reg.put(-258173064361783611L, new RegisterNowRunnable());
   }

   private static final HostRoutingTable registerDefaultHrt(ApplicationRegistry reg, HRUtils hru, HRTReqThreadState state) {
      boolean commit = false;
      PersistentObject persist = RIMPersistentStore.getPersistentObject(-7561592186311481055L);
      HostRoutingTable hrt;
      if ((hrt = (HostRoutingTable)persist.getContents()) == null) {
         hrt = (HostRoutingTable)(new Object());
         persist.setContents(hrt, 51);
         commit = true;
      }

      hrt.init();
      Proxy p = Proxy.getInstance();
      p.addGlobalEventListener(hrt);
      hrt.setActiveIndex();
      reg.put(-7561592186311481055L, hrt);
      if (commit) {
         persist.commit();
      }

      return hrt;
   }

   private static final HostRoutingTable registerDefaultRegistrationHrt(ApplicationRegistry reg, HRUtils hru, HRTReqThreadState state) {
      boolean needToPersist = false;
      PersistentObject persist = RIMPersistentStore.getPersistentObject(278988342877723949L);
      HostRoutingTable hrt;
      if ((hrt = (HostRoutingTable)persist.getContents()) == null) {
         hrt = (HostRoutingTable)(new Object());
         persist.setContents(hrt, 51);
      }

      synchronized (RIMPersistentStore.getSynchObject()) {
         if ((SUPPORTED_WAFS & 2) != 0) {
            needToPersist |= add3GRegistrationHris(2, hrt, state);
         }

         if ((SUPPORTED_WAFS & 1) != 0) {
            needToPersist |= add3GRegistrationHris(1, hrt, state);
         }

         if ((SUPPORTED_WAFS & 8) != 0) {
            needToPersist |= add3GRegistrationHris(8, hrt, state);
         }

         if (needToPersist) {
            PersistentObject.commit(state);
            persist.commit();
         }
      }

      hrt.init();
      Proxy p = Proxy.getInstance();
      p.addGlobalEventListener(hrt);
      hrt.setActiveIndex();
      reg.put(278988342877723949L, hrt);
      return hrt;
   }

   private static final boolean add3GRegistrationHris(int waf, HostRoutingTable hrt, HRTReqThreadState state) {
      String name = null;
      byte[] ipAddress = null;
      int destPort = -1;
      int srcPort = -1;
      String apn = null;
      String username = null;
      String password = null;
      boolean retValue = false;
      boolean isSimulator = DeviceInfo.isSimulator();
      if (waf != RadioInternal.getPrimaryWAF()) {
         byte[] data = Branding.getData(12544);
         if (data != null) {
            label287:
            try {
               ipAddress = UdpAddress.parseIpAddress((String)(new Object(data)), 0);
            } finally {
               break label287;
            }
         }

         data = Branding.getData(12545);
         if (data != null && data.length == 4) {
            destPort = readInt(data);
         }

         data = Branding.getData(12546);
         if (data != null && data.length == 4) {
            srcPort = readInt(data);
         }

         data = Branding.getData(12547);
         if (data != null) {
            apn = (String)(new Object(data));
         }

         data = Branding.getData(12549);
         if (data != null) {
            username = (String)(new Object(data));
         }

         data = Branding.getData(12550);
         if (data != null) {
            password = (String)(new Object(data));
         }
      } else {
         byte[] data = Branding.getData(12288);
         if (data != null) {
            label282:
            try {
               ipAddress = UdpAddress.parseIpAddress((String)(new Object(data)), 0);
            } finally {
               break label282;
            }
         }

         data = Branding.getData(12289);
         if (data != null && data.length == 4) {
            destPort = readInt(data);
         }

         data = Branding.getData(12290);
         if (data != null && data.length == 4) {
            srcPort = readInt(data);
         }

         data = Branding.getData(12291);
         if (data != null) {
            apn = (String)(new Object(data));
         }

         data = Branding.getData(12293);
         if (data != null) {
            username = (String)(new Object(data));
         }

         data = Branding.getData(12294);
         if (data != null) {
            password = (String)(new Object(data));
         }
      }

      if (ipAddress != null || destPort != -1 || srcPort != -1 || apn != null || username != null || password != null) {
         name = "Branding Reg Address";
      }

      if (DebugSupport.getenv("DisableRegistration") != null) {
         state.clearFlag(2);
         PersistentObject.commit(state);
         return false;
      }

      String cfgRegAddress = UdpAddress.makeAddress(false, ipAddress, destPort, srcPort, apn, -1);
      int ipAddressInt = ipAddress != null ? DatagramAddressBase.readInt(ipAddress, 0) : -1;
      if (name == null) {
         name = !isSimulator ? "Live 3G" : "3G Sim";
      }

      if (ipAddressInt == -1) {
         ipAddressInt = getDefault3GIpAddress(waf, isSimulator);
      }

      if (destPort == -1) {
         destPort = getDefault3GDestPort(waf, isSimulator);
      }

      if (srcPort == -1) {
         srcPort = getDefault3GSrcPort(waf, isSimulator);
      }

      if (apn == null) {
         apn = getDefault3GAPN(waf, isSimulator);
      }

      long[] addrs = new long[]{IPv4UdpDAC.makeAddr(ipAddressInt, destPort, srcPort)};
      if (hrt.getNumHris() == 0 || regConfigStateChanged(waf, state, cfgRegAddress, apn, username, password)) {
         HostRoutingInfo hri = make3GHri(waf, apn, username, password);
         hri.setName(name);
         ((IPv4UdpDAC)hri.getDac()).setAddresses(addrs);
         int oldHriIndex = hrt.findHriByNpcAndArt(hri.getNpc(), hri.getArt());
         if (oldHriIndex >= 0) {
            hrt.removeHri(oldHriIndex);
         }

         hrt.addHri(hri);
         updateRegConfigState(waf, state, cfgRegAddress, apn, username, password);
         retValue = true;
      }

      if (!state.isFlagSet(2)) {
         state.setFlag(2);
         retValue = true;
      }

      return retValue;
   }

   private static final boolean regConfigStateChanged(int waf, HRTReqThreadState state, String regAddress, String apn, String username, String password) {
      switch (waf) {
         case 2:
            if (!StringUtilities.strEqual(regAddress, state.regCDMAAddress)) {
               return true;
            }

            return false;
         case 8:
            if (!StringUtilities.strEqual(regAddress, state.regIDENAddress)) {
               return true;
            }

            return false;
         default:
            return !StringUtilities.strEqual(regAddress, state.reg3GPPAddress)
               || !StringUtilities.strEqual(apn, state.regApn)
               || !StringUtilities.strEqual(username, state.regUsername)
               || !StringUtilities.strEqual(password, state.regPassword);
      }
   }

   private static final void updateRegConfigState(int waf, HRTReqThreadState state, String regAddress, String apn, String username, String password) {
      switch (waf) {
         case 2:
            state.regCDMAAddress = regAddress;
            return;
         case 8:
            state.regIDENAddress = regAddress;
            return;
         default:
            state.reg3GPPAddress = regAddress;
            state.regApn = apn;
            state.regUsername = username;
            state.regPassword = password;
      }
   }

   private static final HostRoutingInfo make3GHri(int waf, String apn, String username, String password) {
      HostRoutingInfo hri = null;
      switch (waf) {
         case 2:
            hri = (HostRoutingInfo)(new Object());
            hri.setNpc((long)64);
            break;
         case 8:
            hri = (HostRoutingInfo)(new Object());
            hri.setNpc((long)80);
            break;
         default:
            hri = (HostRoutingInfo)(new Object());
            hri.setNpc((long)48);
            hri.setApn(apn);
            hri.setApnUsername(username);
            hri.setApnPassword(password);
      }

      return hri;
   }

   private static final int getDefault3GIpAddress(int waf, boolean isSimulator) {
      switch (waf) {
         case 2:
            if (!isSimulator) {
               return -835511647;
            }

            return 168005988;
         case 4:
            if (!isSimulator) {
               return 184418760;
            }

            return 174409944;
         case 8:
            if (!isSimulator) {
               return -835511631;
            }

            return 168005988;
         default:
            return !isSimulator ? -835511619 : 168005988;
      }
   }

   private static final String getDefault3GAPN(int waf, boolean isSimulator) {
      if (isSimulator) {
         return "rim.net.gprs";
      }

      switch (waf) {
         case 2:
         case 4:
         case 8:
            return "";
         default:
            return "blackberry.net";
      }
   }

   private static final int getDefault3GDestPort(int waf, boolean isSimulator) {
      switch (waf) {
         case 2:
            if (!isSimulator) {
               return 17781;
            }

            return 19784;
         case 4:
            return 19782;
         case 8:
            if (!isSimulator) {
               return 18781;
            }

            return 19786;
         default:
            return !isSimulator ? 19781 : 19782;
      }
   }

   private static final int getDefault3GSrcPort(int waf, boolean isSimulator) {
      switch (waf) {
         case 2:
            if (!isSimulator) {
               return 17780;
            }

            return 19780;
         case 8:
            if (!isSimulator) {
               return 18780;
            }

            return 19780;
         default:
            return 19780;
      }
   }

   private static final int readInt(byte[] buf) {
      return buf[0] & 0xFF | (buf[1] & 0xFF) << 8 | (buf[2] & 0xFF) << 16 | (buf[3] & 0xFF) << 24;
   }
}
