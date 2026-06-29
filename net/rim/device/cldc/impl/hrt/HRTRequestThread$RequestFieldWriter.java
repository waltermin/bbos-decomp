package net.rim.device.cldc.impl.hrt;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEFieldController;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.PersistentInteger;

final class HRTRequestThread$RequestFieldWriter implements TLEFieldController {
   private int _apnId;
   private int _mode;
   private ServiceBook _sb;
   int _deviceId;
   public static final int MODE_DEVICE_INFO;
   public static final int MODE_SERVICE_INFO;
   private static final int OS_VERSION;
   private static final int APPS_VERSION;
   private static final int DEVICE_ID;
   private static final int DEVICE_NAME;
   private static final int CURRENT_NPC;
   private static final int IMEI;
   private static final int IMSI;
   private static final int ESN;
   private static final int MSISDN;
   private static final int ICCID;
   private static final int REGISTRATION_APN;
   private static final int CURRENT_NPC_EXTENSION;
   private static final int MDN;
   private static final int IP_ADDRESS;
   private static final int SIM_ID;
   private static final int UFMI;
   private static final int LEGACY_GID1_AND_SPN;
   private static final int GID2;
   private static final int LOCALIZATION;
   private static final int SUPPORTED_RADIO_TECHNOLOGY;
   private static final int HOME_NPC;
   private static final int EF_SPN;
   private static final int EF_GID1;
   private static final int HOME_NPC_EXTENSION;
   private static final int PROXY_HOME_NPC;
   private static final int PROXY_HOME_NPC_EXTENSION;
   private static final int MEID;
   private static final int SERVICE_INFO_UID;
   private static final int SERVICE_INFO_HOME_ADDRESS;
   private static final int SRT_3GPP_GPRS;
   private static final int SRT_3GPP_UMTS;
   private static final int SRT_3GPP_GAN;
   private static final int SRT_WLAN_DEFAULT;
   private static final int SRT_CDMA_DEFAULT;
   private static final int SRT_IDEN_DEFAULT;

   public HRTRequestThread$RequestFieldWriter(int mode, int apnId, ServiceBook sb) {
      this._mode = mode;
      this._apnId = apnId;
      this._sb = sb;
   }

   public final void setMode(int i) {
      this._mode = i;
   }

   @Override
   public final boolean processField(int type, int length, DataBuffer db) {
      return false;
   }

   @Override
   public final void dumpField(int type, DataBuffer db) {
      switch (this._mode) {
         case -1:
            break;
         case 0:
         default:
            boolean isOnPrimaryWAF = false;
            boolean isCDMAConnection = HRTRequestThread.isCDMAConnection(this._apnId);
            int activeWAFs = RadioInfo.getActiveWAFs();
            if ((activeWAFs & RadioInternal.getPrimaryWAF()) != 0 || isCDMAConnection && 2 == RadioInternal.getPrimaryWAF()) {
               isOnPrimaryWAF = true;
            }

            TLEUtilities.writeIntegerField(db, 1, this.getVersion(DeviceInfo.getPlatformVersion()), true);
            TLEUtilities.writeIntegerField(db, 2, this.getVersion(ApplicationDescriptor.currentApplicationDescriptor().getVersion()), true);
            TLEUtilities.writeIntegerField(db, 3, DeviceInfo.getDeviceId(), true);
            TLEUtilities.writeStringField(db, 4, this.getDeviceName(isOnPrimaryWAF), true);
            long currentNpc = HRUtils.getNpcForActiveNetwork();
            if (HRUtils.isPretendCDMA()) {
               int index = RadioInfo.getCurrentNetworkIndex();
               if (index >= 0) {
                  int curNetId = RadioInfo.getNetworkId(index);
                  currentNpc = (curNetId & 4095) << 20 & 4293918720L | curNetId >>> 8 & 1048320 | 48;
               }
            }

            this.dumpNpc(currentNpc, db, 5, 12);
            byte[] data;
            if (isOnPrimaryWAF) {
               data = Branding.getData(12295);
            } else {
               data = Branding.getData(12551);
            }

            int flags = data != null && data.length > 0 ? data[0] & 0xFF : 0;
            boolean support3GPP_WAF = (HRTRequestThread.SUPPORTED_WAFS & 1) != 0;
            boolean supportCDMA_WAF = (HRTRequestThread.SUPPORTED_WAFS & 2) != 0;
            boolean supportIDEN_WAF = (HRTRequestThread.SUPPORTED_WAFS & 8) != 0;
            boolean supportWLAN_WAF = (HRTRequestThread.SUPPORTED_WAFS & 4) != 0;
            byte bitfieldSRT = 0;
            if (support3GPP_WAF) {
               int threeGPP_RATs = RadioInternal.get3GPPSupportedRats();
               if ((threeGPP_RATs & 1) != 0) {
                  bitfieldSRT = (byte)(bitfieldSRT | 1);
               }

               if ((threeGPP_RATs & 2) != 0) {
                  bitfieldSRT = (byte)(bitfieldSRT | 2);
               }

               if ((threeGPP_RATs & 4) != 0) {
                  bitfieldSRT = (byte)(bitfieldSRT | 4);
               }
            }

            if (supportCDMA_WAF) {
               bitfieldSRT = (byte)(bitfieldSRT | 16);
            }

            if (supportIDEN_WAF) {
               bitfieldSRT = (byte)(bitfieldSRT | 32);
            }

            if (supportWLAN_WAF) {
               bitfieldSRT = (byte)(bitfieldSRT | 8);
            }

            db.writeByte(20);
            db.writeCompressedInt(1);
            db.writeByte(bitfieldSRT);
            if (isCDMAConnection) {
               supportCDMA_WAF = true;
               support3GPP_WAF = false;
               supportIDEN_WAF = false;
            }

            this.dumpHardwareIDs(db);
            if (support3GPP_WAF) {
               label1317:
               try {
                  TLEUtilities.writeStringField(db, 11, RadioInfo.getAccessPointName(this._apnId), true);
               } finally {
                  break label1317;
               }

               this.dumpNpc(HRUtils.getNpcForHomeNetwork(1), db, 21, 28);
               if (SIMCard.isSupported()) {
                  label1313:
                  try {
                     TLEUtilities.writeDataField(db, 7, SIMCard.getIMSI());
                  } finally {
                     break label1313;
                  }
               }

               label1310:
               try {
                  if ((flags & 1) == 0) {
                     TLEUtilities.writeStringField(db, 9, Phone.getInstance().getNumber(0), true);
                  }
               } finally {
                  break label1310;
               }

               if (SIMCard.isSupported()) {
                  label1305:
                  try {
                     if ((flags & 2) == 0) {
                        byte[] iccid = SIMCard.getICCID();
                        byte[] phase = HRTRequestThread.readEF(19);
                        if (phase != null && phase.length == 1 && phase[0] == 0) {
                           for (int i = 0; i < iccid.length; i++) {
                              int temp = iccid[i] & 255;
                              iccid[i] = (byte)(temp >> 4);
                              iccid[i] += (byte)(temp << 4);
                           }
                        }

                        TLEUtilities.writeDataField(db, 10, iccid);
                     }
                  } finally {
                     break label1305;
                  }

                  boolean sendEFSPNBackdoor = PersistentInteger.get(HRTRequestThread.SEND_EF_SPN_ID) == 1;
                  byte[] gid1 = HRTRequestThread.readEF(9);
                  byte[] spn = null;
                  byte[] legacyGID1SPN = null;
                  if ((flags & 4) != 0) {
                     spn = HRTRequestThread.readEF(11);
                     if (spn != null && spn.length > 1) {
                        if (gid1 != null) {
                           int offset = gid1.length;
                           legacyGID1SPN = new byte[offset + spn.length - 1];
                           System.arraycopy(gid1, 0, legacyGID1SPN, 0, offset);
                           System.arraycopy(spn, 1, legacyGID1SPN, offset, spn.length - 1);
                        } else {
                           legacyGID1SPN = new byte[spn.length - 1];
                           System.arraycopy(spn, 1, legacyGID1SPN, 0, spn.length - 1);
                        }
                     }
                  } else {
                     legacyGID1SPN = Arrays.copy(gid1);
                  }

                  if (sendEFSPNBackdoor) {
                     if (gid1 != null) {
                        TLEUtilities.writeDataField(db, 23, gid1);
                     }

                     if (spn != null) {
                        TLEUtilities.writeDataField(db, 22, spn);
                     }
                  }

                  if (legacyGID1SPN != null) {
                     TLEUtilities.writeDataField(db, 17, legacyGID1SPN);
                  }

                  TLEUtilities.writeDataField(db, 18, HRTRequestThread.readEF(10));
               }

               TLEUtilities.writeStringField(db, 19, Locale.getDefaultForSystem().toString(), true);
               return;
            }

            if (supportCDMA_WAF) {
               TLEUtilities.writeDataField(db, 7, CDMAInfo.getIMSI());

               label1264:
               try {
                  if ((flags & 1) == 0) {
                     TLEUtilities.writeStringField(db, 13, Phone.getInstance().getNumber(0), true);
                  }
               } finally {
                  break label1264;
               }

               this.dumpNpc(HRUtils.getNpcForHomeNetwork(2), db, 21, 28);
               if ((activeWAFs & 1) != 0) {
                  this.dumpNpc(HRUtils.getNpcForHomeNetwork(1), db, 24, 25);
               }

               TLEUtilities.writeStringField(db, 19, Locale.getDefaultForSystem().toString(), true);
               return;
            }

            if (supportIDEN_WAF) {
               label1288:
               try {
                  if ((flags & 1) == 0) {
                     TLEUtilities.writeStringField(db, 9, Phone.getInstance().getNumber(0), true);
                  }
               } finally {
                  break label1288;
               }

               label1285:
               try {
                  TLEUtilities.writeDataField(db, 14, RadioInfo.getIPAddress(RadioInfo.getAccessPointNumber("")));
               } finally {
                  break label1285;
               }

               if (SIMCard.isSupported()) {
                  label1281:
                  try {
                     if ((flags & 2) == 0) {
                        byte[] simid = SIMCard.getICCID();

                        for (int i = simid.length - 1; i >= 0; i--) {
                           byte temp = simid[i];
                           simid[i] = (byte)(temp << 4 | temp >> 4 & 15);
                        }

                        TLEUtilities.writeDataField(db, 15, simid);
                     }
                  } finally {
                     break label1281;
                  }
               }

               db.writeByte(16);
               db.writeCompressedInt(12);
               db.writeInt(DirectConnect.getId(1));
               db.writeInt(DirectConnect.getId(2));
               db.writeInt(DirectConnect.getId(0));
               this.dumpNpc(HRUtils.getNpcForHomeNetwork(8), db, 21, 28);
               TLEUtilities.writeStringField(db, 19, Locale.getDefaultForSystem().toString(), true);
               return;
            }
            break;
         case 1:
            ServiceRecord[] records = this._sb.findRecordsByType(0);

            for (int i = records.length - 1; i >= 0; i--) {
               ServiceRecord rec = records[i];
               String uid = rec.getUid();
               String homeAddr = rec.getHomeAddress();
               if (HRTRequestThread.isValidUid(uid)) {
                  TLEUtilities.writeStringField(db, 1, uid, true);
                  if (homeAddr != null && homeAddr.length() > 0) {
                     TLEUtilities.writeStringField(db, 5, homeAddr, true);
                  }
               }
            }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int getVersion(String strVer) {
      int version = 0;
      int start = 0;
      int strLength = strVer.length();

      for (int shift = 24; shift >= 0 && start < strLength; shift -= 8) {
         int end = strVer.indexOf(46, start);
         if (end == -1) {
            end = strLength;
         }

         boolean var11 = false /* VF: Semaphore variable */;

         try {
            var11 = true;
            int value = Integer.parseInt(strVer.substring(start, end));
            version |= value << shift;
            start = end + 1;
            var11 = false;
         } finally {
            if (var11) {
               if (version != 0) {
                  return version;
               }

               return 589824;
            }
         }
      }

      return version;
   }

   private final String getDeviceName(boolean primaryWAF) {
      String name = DeviceInfo.getDeviceName();
      if (name != null) {
         name = ((StringBuffer)(new Object("Rim"))).append(name).toString();
      }

      byte[] data = Branding.getData(primaryWAF ? 12292 : 12548);
      if (data != null) {
         name = ((StringBuffer)(new Object())).append(name).append('/').append((String)(new Object(data))).toString();
      }

      return name;
   }

   private final void dumpNpc(long npc, DataBuffer db, int baseField, int extendedField) {
      TLEUtilities.writeIntegerField(db, baseField, (int)npc, true);
      int temp = (int)(npc >> 32);
      if (temp != 0) {
         TLEUtilities.writeIntegerField(db, extendedField, temp, true);
      }
   }

   private final void dumpHardwareIDs(DataBuffer db) {
      if ((HRTRequestThread.SUPPORTED_WAFS & 2) != 0) {
         TLEUtilities.writeIntegerField(db, 8, CDMAInfo.getESN(), true);
      }

      if ((HRTRequestThread.SUPPORTED_WAFS & 1) != 0) {
         TLEUtilities.writeDataField(db, 6, GPRSInfo.getIMEI());
      } else {
         if ((HRTRequestThread.SUPPORTED_WAFS & 8) != 0) {
            TLEUtilities.writeDataField(db, 6, IDENInfo.getIMEI());
         }
      }
   }
}
