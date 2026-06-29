package net.rim.device.api.hrt;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEUtilities;
import net.rim.vm.PersistentInteger;

public final class HRUtils implements RadioStatusListener, SIMCardStatusListener {
   private boolean _pretendCDMA;
   private long _currentNpc;
   private long _homeNpc3GPP;
   private long _homeNpcCDMA;
   private long _homeNpcIDEN;
   public static final long DEFAULT_HRT_GUID;
   public static final long DEFAULT_REGISTRATION_HRT_GUID;
   public static final long HRUTILS_GUID;
   public static final long NPC_MOBITEX_BASE;
   public static final long NPC_GPRS_BASE;
   public static final long NPC_CDMA_BASE;
   public static final long NPC_IDEN_BASE;
   public static final long NPC_WIFI_BASE;
   public static final long NPC_BASE_MASK;
   public static final long NPC_MOBITEX_US;
   public static final long NPC_MOBITEX_CA;
   public static final long NPC_MOBITEX_VENEZUELA;
   public static final int VIEW_MODE_UNKNOWN;
   public static final int VIEW_MODE_RW;
   public static final int VIEW_MODE_R;
   public static final int VIEW_MODE_RO;
   private static final int NETWORK_TYPE = RadioInfo.getNetworkType();
   private static final int SUPPORTED_WAFS = RadioInfo.getSupportedWAFs();
   public static final long GUID_NPC_CHANGED_EVENT;
   public static final long GUID_HOME_NPC_CHANGED_EVENT;
   public static final long NULL_NPC;
   private static int PRETEND_CDMA_ID = PersistentInteger.getId(8664576174761824809L, 0);

   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public final void radioTurnedOff() {
      this.updateCurrentNpc(0, 0);
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.updateCurrentNpc(networkId, service);
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.updateCurrentNpc(networkId, service);
   }

   @Override
   public final void cardReady() {
      this.updateHomeNpc();
   }

   @Override
   public final void cardUpdated() {
      this.updateHomeNpc();
   }

   @Override
   public final void cardInserted() {
   }

   @Override
   public final void cardInvalid(int code, int subCode) {
   }

   @Override
   public final void cardFault(int code) {
   }

   @Override
   public final void smsEFFull() {
   }

   @Override
   public final void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public final void responseMarkSMSAsRead(int status, int packetId) {
   }

   public static final long getNpcForHomeNetwork() {
      return getNpcForHomeNetwork(getActiveWAF());
   }

   public static final long getNpcForHomeNetwork(int waf) {
      if ((SUPPORTED_WAFS & waf) == 0) {
         return -1;
      }

      HRUtils hru = (HRUtils)getFactoryObject(1308825735943519978L);
      synchronized (hru) {
         long homeNpc = -1;
         if ((SUPPORTED_WAFS & 1) != 0 && waf == 1) {
            homeNpc = hru._homeNpc3GPP;
         } else if ((SUPPORTED_WAFS & 2) != 0 && waf == 2) {
            homeNpc = hru._homeNpcCDMA;
         } else if ((SUPPORTED_WAFS & 8) != 0 && waf == 8) {
            homeNpc = hru._homeNpcIDEN;
         }

         return homeNpc;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public HRUtils() {
      this._pretendCDMA = PersistentInteger.get(PRETEND_CDMA_ID) != 0;
      boolean var5 = false /* VF: Semaphore variable */;

      label27:
      try {
         var5 = true;
         int netId = RadioInfo.getNetworkId(RadioInfo.getCurrentNetworkIndex());
         this._currentNpc = this.calculateNpc(netId, RadioInfo.getNetworkService(), getActiveWAF());
         var5 = false;
      } finally {
         if (var5) {
            this._currentNpc = -1;
            break label27;
         }
      }

      this._homeNpc3GPP = this.calculateHomeNpc(1);
      this._homeNpcCDMA = this.calculateHomeNpc(2);
      this._homeNpcIDEN = this.calculateHomeNpc(8);
   }

   private static final int getActiveWAF() {
      int activeWAFs = RadioInfo.getActiveWAFs();
      int activeWAF = 0;
      if ((activeWAFs & 1) != 0) {
         return 1;
      }

      if ((activeWAFs & 2) != 0) {
         return 2;
      }

      if ((activeWAFs & 8) != 0) {
         activeWAF = 8;
      }

      return activeWAF;
   }

   private final synchronized void updateCurrentNpc(int netId, int service) {
      long oldNpc = this._currentNpc;
      this._currentNpc = this.calculateNpc(netId, service, getActiveWAF());
      if (oldNpc != this._currentNpc) {
         RIMGlobalMessagePoster.postGlobalEvent(-254931370837867202L, (int)(this._currentNpc & 4294967295L), (int)(this._currentNpc >> 32), null, null);
      }
   }

   private final synchronized void updateHomeNpc() {
      boolean updateNpcCache = false;
      if ((SUPPORTED_WAFS & 1) != 0) {
         long newNpc = this.calculateHomeNpc(1);
         if (newNpc != this._homeNpc3GPP && (this._homeNpc3GPP == -1 || newNpc != -1)) {
            updateNpcCache = true;
            this._homeNpc3GPP = newNpc;
         }
      }

      if ((SUPPORTED_WAFS & 2) != 0) {
         long newNpc = this.calculateHomeNpc(2);
         if (newNpc != this._homeNpcCDMA && (this._homeNpcCDMA == -1 || newNpc != -1)) {
            updateNpcCache = true;
            this._homeNpcCDMA = newNpc;
         }
      }

      if ((SUPPORTED_WAFS & 8) != 0) {
         long newNpc = this.calculateHomeNpc(8);
         if (newNpc != this._homeNpcIDEN && (this._homeNpcIDEN == -1 || newNpc != -1)) {
            updateNpcCache = true;
            this._homeNpcIDEN = newNpc;
         }
      }

      if (updateNpcCache) {
         RIMGlobalMessagePoster.postGlobalEvent(-8896339270692810071L);
      }
   }

   private final long calculateNpc(int netId, int service, int waf) {
      if ((service & 4) == 0) {
         return -1;
      }

      if (isPretendCDMA()) {
         return (CDMAInfo.getHomeSID() & 65535) << 8 | 64;
      }

      switch (waf) {
         case 1:
            int mccxx = netId & 4095;
            if (mccxx != 0) {
               return mccxx << 20 & 4293918720L | netId >>> 8 & 1048320 | 48;
            }
            break;
         case 2:
            int sid = netId & 65535;
            long npc = 64 | sid << 8;
            if (sid == 0) {
               npc |= 2147483648L;
            }

            return npc;
         case 4:
            int mcc = netId & 4095;
            if (mcc != 0) {
               return mcc << 20 & 4293918720L | netId >>> 8 & 1048320 | 96;
            }
            break;
         case 8:
            int mccx = netId & 4095;
            if (mccx != 0) {
               return mccx << 20 & 4293918720L | netId >>> 8 & 1048320 | 80;
            }
      }

      return -1;
   }

   private final long calculateHomeNpc(int waf) {
      if ((SUPPORTED_WAFS & 1) != 0 && waf == 1) {
         long npc = -1;
         int mcc = GPRSInfo.getHomeMCC();
         int mnc = GPRSInfo.getHomeMNC();
         if (mcc != -1 && mnc != -1) {
            npc = mcc << 20 & 4293918720L | mnc << 8 | 48;
         }

         return npc;
      } else if ((SUPPORTED_WAFS & 2) != 0 && waf == 2) {
         return (CDMAInfo.getHomeSID() & 65535) << 8 | 64;
      } else if ((SUPPORTED_WAFS & 8) != 0 && waf == 8) {
         return IDENInfo.getHomeMCC() << 20 & 4293918720L | IDENInfo.getHomeNDC() << 8 | 80;
      } else {
         return (SUPPORTED_WAFS & 4) != 0 && waf == 4 ? 2290649136L : -1;
      }
   }

   public static final boolean isPretendCDMA() {
      if ((SUPPORTED_WAFS & 3) == 3 && (RadioInfo.getActiveWAFs() & 1) != 0) {
         HRUtils hru = (HRUtils)getFactoryObject(1308825735943519978L);
         return hru._pretendCDMA;
      } else {
         return false;
      }
   }

   public static final void setPretendCDMA(boolean pretendCDMA) {
      if ((SUPPORTED_WAFS & 3) == 3) {
         HRUtils hru = (HRUtils)getFactoryObject(1308825735943519978L);
         if (hru._pretendCDMA != pretendCDMA) {
            hru._pretendCDMA = pretendCDMA;
            PersistentInteger.set(PRETEND_CDMA_ID, hru._pretendCDMA ? 1 : 0);
            int index = RadioInfo.getCurrentNetworkIndex();
            if (index >= 0) {
               hru.updateCurrentToPretendCDMA(RadioInfo.getNetworkId(index), RadioInfo.getNetworkService());
            }
         }
      }
   }

   private final synchronized void updateCurrentToPretendCDMA(int netId, int service) {
      this._currentNpc = this.calculateNpc(netId, service, getActiveWAF());
   }

   public static final void checkPretendCDMA(HostRoutingInfo[] defaultHris) {
      if ((SUPPORTED_WAFS & 1) != 0 && (SUPPORTED_WAFS & 2) != 0 && (RadioInfo.getActiveWAFs() & 1) != 0) {
         long cdmaHomeSID = (CDMAInfo.getHomeSID() & 65535) << 8 | 64;
         long currentGprsNpc = -1;
         int index = RadioInfo.getCurrentNetworkIndex();
         if (index >= 0) {
            int curNetId = RadioInfo.getNetworkId(index);
            currentGprsNpc = (curNetId & 4095) << 20 & 4293918720L | curNetId >>> 8 & 1048320 | 48;
         }

         boolean foundActiveGprs = false;
         boolean foundCdmaHome = false;

         for (int i = defaultHris.length - 1; i >= 0; i--) {
            if (defaultHris[i].getNpc() == currentGprsNpc && (defaultHris[i].getArt() & 1) != 0) {
               foundActiveGprs = true;
               break;
            }

            if (defaultHris[i].getNpc() == cdmaHomeSID && (defaultHris[i].getArt() & 16) != 0) {
               foundCdmaHome = true;
            }
         }

         setPretendCDMA(!foundActiveGprs && foundCdmaHome);
      }
   }

   private static final long getNPC(int type) {
      switch (type) {
         case 2:
            throw new Object();
         case 3:
         case 7:
         default:
            return 48;
         case 4:
            return 64;
         case 5:
            return 80;
         case 6:
            return 96;
      }
   }

   public static final HostRoutingTable getDefaultHRT() {
      return (HostRoutingTable)getFactoryObject(-7561592186311481055L);
   }

   public static final HostRoutingTable getRegistrationHRT() {
      return (HostRoutingTable)getFactoryObject(278988342877723949L);
   }

   public static final HRThunks getThunks() {
      return (HRThunks)getFactoryObject(6176832563945898679L);
   }

   public static final HostRoutingInfo newHriByNetType(int type) {
      long npc = getNPC(type);
      return newHriByNpc(npc);
   }

   public static final HostRoutingInfo newHriByNpc(int npc) {
      return newHriByNpc(npc & 4294967295L);
   }

   public static final HostRoutingInfo newHriByNpc(long npc) {
      if (npc == 0) {
         return newHriByNetType(NETWORK_TYPE);
      }

      long npcBase = npc & 252;
      HostRoutingInfo hri;
      if (npcBase == 48) {
         hri = new GprsHRI();
      } else if (npcBase == 64) {
         hri = new CdmaHRI();
      } else if (npcBase == 80) {
         hri = new IdenHRI();
      } else {
         if (npcBase != 96) {
            throw new Object();
         }

         hri = new WifiHRI();
      }

      hri.setNpc(npc);
      hri.setDirty(false);
      return hri;
   }

   public static final HostRoutingInfo newHriByWAF(int waf) {
      if (waf == 1) {
         HostRoutingInfo hri = new GprsHRI();
         return hri;
      } else if (waf == 2) {
         HostRoutingInfo hri = new CdmaHRI();
         return hri;
      } else if (waf == 8) {
         HostRoutingInfo hri = new IdenHRI();
         return hri;
      } else if (waf == 4) {
         return new WifiHRI();
      } else {
         throw new Object();
      }
   }

   public static final Object getFactoryObject(long guid) {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      return reg.waitFor(guid);
   }

   public static final boolean isWorldPhone() {
      int waf = 3;
      return (SUPPORTED_WAFS & waf) == waf;
   }

   public static final long convertOldNpc(long npc) {
      return npc == 0 ? getNPC(NETWORK_TYPE) : npc;
   }

   public static final HostRoutingInfo[] parseBuffer(DataBuffer buf) {
      buf.setBigEndian(true);

      try {
         switch (buf.readUnsignedByte() & 240) {
            case 16:
               if (buf.readUnsignedByte() == 8) {
                  return parseVersionOne(buf);
               }
               break;
            case 32:
               if (buf.readUnsignedByte() == 8) {
                  return parseVersionTwo(buf);
               }
               break;
            case 48:
            case 64:
            case 80:
               return parseVersionThree(buf);
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   private static final HostRoutingInfo[] parseVersionOne(DataBuffer buf) {
      HostRoutingInfo[] hris = new HostRoutingInfo[1];
      switch (NETWORK_TYPE) {
         case 2:
            HostRoutingInfo hri = newHriByNetType(NETWORK_TYPE);
            int num = buf.readInt();
            if (num >= 1 && num <= 255) {
               int[] pins = new int[num];

               for (int i = 0; i < num; i++) {
                  pins[i] = buf.readInt();
                  buf.skipBytes(4);
               }

               IntDAC dac = (IntDAC)hri.getDac();
               dac.setAddresses(pins);
               dac.setLoadSharingCode(5);
               hris[0] = hri;
               return hris;
            } else {
               throw new Object();
            }
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            throw new Object();
      }
   }

   private static final HostRoutingInfo[] parseVersionTwo(DataBuffer buf) {
      int[] npcs = new int[0];
      HostRoutingInfo hri = null;
      int startPos = buf.getPosition();

      while (!buf.eof() && buf.readUnsignedByte() != 0) {
         int npc = (int)convertOldNpc(buf.readUnsignedByte());
         int size = buf.readCompressedInt();
         int i = npcs.length - 1;

         while (i >= 0 && npcs[i] != npc) {
            i--;
         }

         if (i < 0) {
            Arrays.add(npcs, npc);
         }

         buf.skipBytes(size);
      }

      HostRoutingInfo[] hris = new HostRoutingInfo[npcs.length];

      for (int i = 0; i < npcs.length; i++) {
         hris[i] = newHriByNpc(npcs[i]);
      }

      buf.setPosition(startPos);

      int type;
      while (!buf.eof() && (type = buf.readUnsignedByte()) != 0) {
         int npc = (int)convertOldNpc(buf.readUnsignedByte());
         int size = buf.readCompressedInt();

         int var16;
         for (var16 = hris.length - 1; var16 >= 0; var16--) {
            hri = hris[var16];
            if (hri.getNpc() == npc) {
               break;
            }
         }

         if (var16 >= 0 && hri.parseField(type, size, buf)) {
            if (type == 16) {
               DAC hriDAC = (DAC)hri.getDac();
               if (hriDAC instanceof IPv4UdpDAC) {
                  IPv4UdpDAC dac = (IPv4UdpDAC)hriDAC;
                  long[] addr = dac.getAddresses();
                  if (addr != null) {
                     int ii = addr.length - 1;

                     while (var16 >= 0) {
                        addr[ii] = IPv4UdpDAC.makeAddr(IPv4UdpDAC.addr2IpAddress(addr[0]), 19781, 19780);
                        var16--;
                     }

                     dac.setAddresses(addr);
                  }
               }

               if (hri instanceof GprsHRI) {
                  GprsHRI ghri = (GprsHRI)hri;
                  ghri.setApn(((GprsHRI)getRegistrationHRT().getActiveHri()).getApn());
               }
            }
         } else {
            buf.skipBytes(size);
         }
      }

      return hris;
   }

   private static final HostRoutingInfo[] parseVersionThree(DataBuffer buf) {
      HostRoutingInfo[] hris = new HostRoutingInfo[0];
      HostRoutingInfo[] clonableHris = new HostRoutingInfo[0];
      HRUtils$ClonedHriInfo[] cloneInfo = new HRUtils$ClonedHriInfo[0];
      HRUtils$VersionThreeParser p = new HRUtils$VersionThreeParser();

      int fieldType;
      while (!buf.eof() && (fieldType = buf.readUnsignedByte()) != 0) {
         int length = buf.readCompressedInt();
         p.reset(fieldType);
         switch (fieldType) {
            case 63:
               buf.skipBytes(length);
               break;
            case 64:
            default:
               TLEUtilities.parseField(buf, p, length);
               int art = p.getArt();
               if ((art & 7) != 0) {
                  processHris(hris, clonableHris, (HostRoutingInfo)p.getResult(1));
               }

               if ((art & 16) != 0) {
                  processHris(hris, clonableHris, (HostRoutingInfo)p.getResult(2));
               }

               if ((art & 32) != 0) {
                  processHris(hris, clonableHris, (HostRoutingInfo)p.getResult(8));
               }

               if ((art & 8) != 0) {
                  processHris(hris, clonableHris, (HostRoutingInfo)p.getResult(4));
               }
               break;
            case 65:
               TLEUtilities.parseField(buf, p, length);
               Arrays.add(cloneInfo, p.getResult(0));
         }
      }

      for (int i = cloneInfo.length - 1; i >= 0; i--) {
         HRUtils$ClonedHriInfo cInfo = cloneInfo[i];
         HostRoutingInfo[] baseHris = new HostRoutingInfo[0];

         for (int j = clonableHris.length - 1; j >= 0; j--) {
            HostRoutingInfo baseHri = clonableHris[j];
            if (cInfo.baseNpc == baseHri.getNpc() && (cInfo.baseArt & baseHri.getArt()) != 0) {
               Arrays.add(baseHris, baseHri);
            }
         }

         if (baseHris.length > 0) {
            for (int j = cInfo.clonedNpcs.size() - 1; j >= 0; j--) {
               long clonedNpc = cInfo.clonedNpcs.elementAt(j);
               long clonedNpcBase = clonedNpc & 4294967295L & 252;

               for (int k = 0; k < baseHris.length; k++) {
                  HostRoutingInfo clonableHRI = baseHris[k];
                  if (clonedNpcBase == 48 && clonableHRI instanceof GprsHRI
                     || clonedNpcBase == 64 && clonableHRI instanceof CdmaHRI
                     || clonedNpcBase == 80 && clonableHRI instanceof IdenHRI) {
                     HostRoutingInfo newHri = (HostRoutingInfo)clonableHRI.clone();
                     newHri.setNpc(clonedNpc);
                     boolean duplicateFound = false;

                     for (int m = hris.length - 1; m >= 0; m--) {
                        if (newHri.getNpc() == hris[m].getNpc() && (newHri.getArt() & hris[m].getArt()) != 0) {
                           duplicateFound = true;
                           break;
                        }
                     }

                     if (!duplicateFound) {
                        Arrays.add(hris, newHri);
                     }
                     break;
                  }
               }
            }
         }
      }

      return hris;
   }

   private static final void processHris(HostRoutingInfo[] hris, HostRoutingInfo[] clonableHris, HostRoutingInfo hri) {
      if (hri != null) {
         long npc = hri.getNpc();
         if (npc == 0) {
            throw new Object();
         }

         long npcBase = npc & 4294967295L & 252;
         if (npcBase == 48 && hri instanceof GprsHRI
            || npcBase == 64 && hri instanceof CdmaHRI
            || npcBase == 80 && hri instanceof IdenHRI
            || npcBase == 96 && hri instanceof WifiHRI) {
            for (int i = hris.length - 1; i >= 0; i--) {
               if (hri.getNpc() == hris[i].getNpc() && (hri.getArt() & hris[i].getArt()) != 0) {
                  throw new Object();
               }
            }

            Arrays.add(hris, hri);
            if (clonableHris != null) {
               Arrays.add(clonableHris, hri);
            }
         }
      }
   }

   public static final long getNpcForActiveNetwork() {
      HRUtils hru = (HRUtils)getFactoryObject(1308825735943519978L);
      synchronized (hru) {
         return hru._currentNpc;
      }
   }
}
