package net.rim.device.api.system;

import java.io.UnsupportedEncodingException;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC16;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.SE13NetworkTable;
import net.rim.vm.Process;

public final class SIMCard {
   public static final int PIN1 = 1;
   public static final int PIN2 = 2;
   public static final int MEP_CATEGORY_SIM = 0;
   public static final int MEP_CATEGORY_NETWORK = 1;
   public static final int MEP_CATEGORY_NETWORK_SUBSET = 2;
   public static final int MEP_CATEGORY_SERVICE_PROVIDER = 3;
   public static final int MEP_CATEGORY_CORPORATE = 4;
   public static final int MEP_STATE_INACTIVE = 0;
   public static final int MEP_STATE_PASSED = 1;
   public static final int MEP_STATE_DISABLED = 2;
   public static final int MEP_STATE_BLOCKED = 3;
   public static final int MEP_STATE_UNKNOWN = 4;
   public static final int MEP_STATE_FAILED = 5;
   public static final int MEP_STATE_NOT_CHECKED = 6;
   public static final int INDICATOR_LINE_1 = 1;
   public static final int INDICATOR_LINE_2 = 2;
   public static final int INDICATOR_DATA = 4;
   public static final int INDICATOR_FAX = 8;
   public static final int EF_ICCID = 0;
   public static final int EF_LP = 1;
   public static final int EF_IMSI = 2;
   public static final int EF_KC = 3;
   public static final int EF_PLMN_SEL = 4;
   public static final int EF_HPLMN = 5;
   public static final int EF_ACM_MAX = 6;
   public static final int EF_SST = 7;
   public static final int EF_EST = 101;
   public static final int EF_UST = 100;
   public static final int EF_ACM = 8;
   public static final int EF_GID1 = 9;
   public static final int EF_GID2 = 10;
   public static final int EF_SPN = 11;
   public static final int EF_PUCT = 12;
   public static final int EF_CBMI = 13;
   public static final int EF_BCCH = 14;
   public static final int EF_ACC = 15;
   public static final int EF_FPLMN = 16;
   public static final int EF_LOCI = 17;
   public static final int EF_AD = 18;
   public static final int EF_PHASE = 19;
   public static final int EF_ADN = 20;
   public static final int EF_FDN = 21;
   public static final int EF_SMS = 22;
   public static final int EF_CCP = 23;
   public static final int EF_MSISDN = 24;
   public static final int EF_SMSP = 25;
   public static final int EF_SMSS = 26;
   public static final int EF_LND = 27;
   public static final int EF_EXT1 = 28;
   public static final int EF_EXT2 = 29;
   public static final int EF_CPS = 30;
   public static final int EF_ELP = 31;
   public static final int EF_VGCS = 32;
   public static final int EF_VGCSS = 33;
   public static final int EF_VBS = 34;
   public static final int EF_VBSS = 35;
   public static final int EF_EMLPP = 36;
   public static final int EF_AAEM = 37;
   public static final int EF_CBMID = 38;
   public static final int EF_ECC = 39;
   public static final int EF_CBMIR = 40;
   public static final int EF_DCK = 41;
   public static final int EF_CNL = 42;
   public static final int EF_NIA = 43;
   public static final int EF_SDN = 44;
   public static final int EF_EXT3 = 45;
   public static final int EF_BDN = 46;
   public static final int EF_EXT4 = 47;
   public static final int EF_SMSR = 48;
   public static final int EF_CPHS_VMWF = 49;
   public static final int EF_CPHS_CFF = 50;
   public static final int EF_CPHS_ON = 51;
   public static final int EF_CPHS_CSP = 52;
   public static final int EF_CPHS_INFO = 53;
   public static final int EF_CPHS_MN = 54;
   public static final int EF_CPHS_ONS = 55;
   public static final int EF_CPHS_INFO_NUM = 56;
   public static final int EF_CPHS_INFO_NUM_OLD = 57;
   public static final int EF_KC_GPRS = 58;
   public static final int EF_LOCI_GPRS = 59;
   public static final int EF_OPL = 60;
   public static final int EF_PNN = 61;
   public static final int EF_HZ_PARAMETERS = 62;
   public static final int EF_HZ_CACHE1 = 63;
   public static final int EF_HZ_CACHE2 = 64;
   public static final int EF_HZ_CACHE3 = 65;
   public static final int EF_HZ_CACHE4 = 66;
   public static final int EF_MWIS = 67;
   public static final int EF_PLMNW_ACT = 68;
   public static final int EF_OPLMNW_ACT = 69;
   public static final int EF_HPLMNW_ACT = 70;
   public static final int EF_CPBCCH = 71;
   public static final int EF_INVSCAN = 72;
   public static final int EF_SPDI = 84;
   public static final int EF_LI = 102;
   public static final int EF_PBR = 103;
   public static final int EF_ADN2 = 108;
   public static final int EF_ADN3 = 109;
   public static final int EF_ADN4 = 110;
   public static final int EF_MBDN = 79;
   public static final int EF_MBI = 80;
   public static final int EF_STRUCTURE_TRANSPARENT = 0;
   public static final int EF_STRUCTURE_LINEAR_FIXED = 1;
   public static final int EF_STRUCTURE_CYCLIC = 2;
   public static final int CSP_CALL_OFFERING = 0;
   public static final int CSP_CALL_OFFERING_CFU = 128;
   public static final int CSP_CALL_OFFERING_CFB = 64;
   public static final int CSP_CALL_OFFERING_CFNRY = 32;
   public static final int CSP_CALL_OFFERING_CFNRC = 16;
   public static final int CSP_CALL_OFFERING_CT = 8;
   public static final int CSP_CALL_RESTRICTION = 1;
   public static final int CSP_CALL_RESTRICTION_BOAC = 128;
   public static final int CSP_CALL_RESTRICTION_BOIC = 64;
   public static final int CSP_CALL_RESTRICTION_BOIC_EXHC = 32;
   public static final int CSP_CALL_RESTRICTION_BAIC = 16;
   public static final int CSP_CALL_RESTRICTION_BICROAM = 8;
   public static final int CSP_OTHER_SUPP_SERV = 2;
   public static final int CSP_OTHER_SUPP_SERV_MPTY = 128;
   public static final int CSP_OTHER_SUPP_SERV_CUG = 64;
   public static final int CSP_OTHER_SUPP_SERV_AOC = 32;
   public static final int CSP_OTHER_SUPP_SERV_PREF_CUG = 16;
   public static final int CSP_OTHER_SUPP_SERV_CUG_OA = 8;
   public static final int CSP_CALL_COMPLETION = 3;
   public static final int CSP_CALL_COMPLETION_HOLD = 128;
   public static final int CSP_CALL_COMPLETION_CW = 64;
   public static final int CSP_CALL_COMPLETION_CCBS = 32;
   public static final int CSP_CALL_COMPLETION_USR_USR_SIG = 16;
   public static final int CSP_TELESERV = 4;
   public static final int CSP_TELESERV_SM_MT = 128;
   public static final int CSP_TELESERV_SM_MO = 64;
   public static final int CSP_TELESERV_SM_CB = 32;
   public static final int CSP_TELESERV_REPLY_PATH = 16;
   public static final int CSP_TELESERV_DELIVER_CONF = 8;
   public static final int CSP_TELESERV_PROTOCOL_ID = 4;
   public static final int CSP_TELESERV_VALIDITY_PERIOD = 2;
   public static final int CSP_CPHS_TELESERV = 5;
   public static final int CSP_CPHS_TELESERV_ALS = 128;
   public static final int CSP_CPHS_FEATURES_SST = 128;
   public static final int CSP_NUMBER_ID = 7;
   public static final int CSP_NUMBER_ID_CLIP = 128;
   public static final int CSP_NUMBER_ID_COLR = 32;
   public static final int CSP_NUMBER_ID_COLP = 16;
   public static final int CSP_NUMBER_ID_MCI = 8;
   public static final int CSP_NUMBER_ID_CLI_SEND = 2;
   public static final int CSP_NUMBER_ID_CLI_BLOCK = 1;
   public static final int CSP_PHASE2P_SERV = 8;
   public static final int CSP_PHASE2P_SERV_GPRS = 128;
   public static final int CSP_PHASE2P_SERV_HSCSD = 64;
   public static final int CSP_PHASE2P_SERV_VOICE_GRP_CALL = 32;
   public static final int CSP_PHASE2P_SERV_VOICE_BCAST_SERV = 16;
   public static final int CSP_PHASE2P_SERV_MULT_SUBSC_PROFILE = 8;
   public static final int CSP_PHASE2P_SERV_MULT_BAND = 4;
   public static final int CSP_VALUE_ADDED_1 = 9;
   public static final int CSP_VALUE_ADDED_1_PLMNMODE = 128;
   public static final int CSP_VALUE_ADDED_1_VPS = 64;
   public static final int CSP_VALUE_ADDED_1_SM_MO_PAGING = 32;
   public static final int CSP_VALUE_ADDED_1_SM_MO_EMAIL = 16;
   public static final int CSP_VALUE_ADDED_1_FAX = 8;
   public static final int CSP_VALUE_ADDED_1_DATA = 4;
   public static final int CSP_VALUE_ADDED_1_LANGUAGE = 1;
   public static final int CSP_VALUE_ADDED_2 = 10;
   public static final int CSP_VALUE_ADDED_2_WAP = 128;
   public static final int CSP_VALUE_ADDED_2_IM = 64;
   public static final int CSP_INFO_NUMBERS = 11;
   public static final int EVENT_MT_CALL = 1;
   public static final int EVENT_CALL_CONNECTED = 2;
   public static final int EVENT_CALL_DISCONNECTED = 4;
   public static final int EVENT_LOCATION_STATUS = 8;
   public static final int EVENT_USER_ACTIVITY = 16;
   public static final int EVENT_IDLE_SCREEN = 32;
   public static final int EVENT_LANGUAGE_SELECTION = 64;
   public static final int EVENT_BROWSER_TERMINATION = 128;
   public static final int SIM_PB_STATUS_INVALID_PARAMETER = 0;
   public static final int SIM_PB_STATUS_OS_BUSY = 1;
   public static final int SIM_PB_STATUS_NO_MORE_ENTRIES = 2;
   public static final int SIM_PB_STATUS_SUCCESS = 3;
   public static final int SIM_JSR177_OPEN_COMPLETE = 2348;
   public static final int SIM_JSR177_EXCHANGE_COMPLETE = 2349;
   public static final int SIM_JSR177_CLOSE_COMPLETE = 2350;
   public static final int SIM_JSR177_PIN_OPERATION_COMPLETE = 2351;
   public static final int JSR177_OPEN_SUCCESS = 0;
   public static final int JSR177_OPEN_FAILED = 1;
   public static final int JSR177_OPEN_ERROR_JSR177_NOT_SUPPORTED = 2;
   public static final int JSR177_OPEN_ERROR_JSR177_BAD_AID = 3;
   public static final int JSR177_OPEN_ERROR_NO_FREE_CHANNELS = 4;
   public static final int JSR177_EXCHANGE_SUCCESS = 0;
   public static final int JSR177_EXCHANGE_FAILED = 1;
   public static final int JSR177_EXCHANGE_ERROR_ZERO_LENGTH_APDU = 2;
   public static final int JSR177_EXCHANGE_ERROR_INVALID_CLASS = 3;
   public static final int JSR177_EXCHANGE_ERROR_SENT_SELECT_CMD = 4;
   public static final int JSR177_EXCHANGE_ERROR_SENT_MANAGE_CHANNEL_CMD = 5;
   public static final int JSR177_EXCHANGE_ERROR_MALFORMED_APDU = 6;
   public static final int JSR177_EXCHANGE_ERROR_NO_BUFFER_AVAILABLE = 7;
   public static final int JSR177_CLOSE_SUCCESS = 0;
   public static final int JSR177_CLOSE_FAILED = 1;
   public static final int JSR177_CLOSE_ERROR_BAD_CHANNEL = 2;
   public static final int JSR177_OPEN_NO_FCP_INFO = 0;
   public static final int JSR177_OPEN_FCP_WITH_REF_CLASS = 1;
   public static final int JSR177_OPEN_FCP_WITH_REF_INTERFACE = 2;
   public static final int SIM_CHV_REQ_OK = 0;
   public static final int SIM_CHV_REQ_INVALID_PARAMS = 1;
   public static final int SIM_CHV_REQ_SIM_GENERAL_FAULT = 5;

   private SIMCard() {
   }

   public static final boolean isSupported() {
      return RadioInfo.areWAFsSupported(9);
   }

   public static final boolean isSecuritySupported() {
      return RadioInfo.areWAFsSupported(1);
   }

   public static final native byte[] getIMSI();

   public static final String imsiToString(byte[] imsi) {
      if (imsi == null) {
         return null;
      }

      StringBuffer sb = new StringBuffer();
      int mncPoint = 4;
      int mcc = 0;

      for (int i = 0; i < imsi.length; i++) {
         sb.append((char)(imsi[i] + 48));
         mcc *= 10;
         mcc += imsi[i];
         if (i == 2) {
            try {
               if (is3DigitMNC()) {
                  mncPoint++;
               }
            } catch (SIMCardException var6) {
            }

            sb.append('.');
         } else if (i == mncPoint) {
            sb.append('.');
         }
      }

      return sb.toString();
   }

   public static final native byte[] getICCID();

   public static final String iccidToString(byte[] iccid) {
      if (iccid == null) {
         return null;
      }

      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < iccid.length; i++) {
         int bcdDigit = iccid[i] & 15;
         if (bcdDigit > 9) {
            break;
         }

         sb.append((char)(bcdDigit + 48));
         bcdDigit = iccid[i] >>> 4 & 15;
         if (bcdDigit > 9) {
            break;
         }

         sb.append((char)(bcdDigit + 48));
      }

      if (RadioInfo.areWAFsSupported(8)) {
         sb.setLength(sb.length() - 1);
      }

      return sb.toString();
   }

   public static final int getMCCFromIMSI(byte[] imsi) {
      if (imsi != null && imsi.length >= 3) {
         int mcc = 0;

         for (int i = 0; i < 3; i++) {
            mcc *= 10;
            mcc += imsi[i];
         }

         return mcc;
      } else {
         return -1;
      }
   }

   public static final int getMNCFromIMSI(byte[] imsi) {
      try {
         int mncDigitCount = is3DigitMNC() ? 3 : 2;
         if (imsi != null && imsi.length >= 3 + mncDigitCount) {
            int mnc = 0;

            for (int i = 3; i < 3 + mncDigitCount; i++) {
               mnc *= 10;
               mnc += imsi[i];
            }

            return mnc;
         } else {
            return -1;
         }
      } catch (SIMCardException var4) {
         return -1;
      }
   }

   public static final native boolean isValid();

   public static final native boolean isUSIMPresent();

   public static final native boolean isReady();

   public static final native boolean isPINEnabled();

   public static final native boolean isPINRequired(int var0);

   public static final native int getPINRetriesRemaining(int var0);

   public static final native boolean isPUKRequired(int var0);

   public static final native int getPUKRetriesRemaining(int var0);

   public static final native void sendPIN(byte[] var0);

   public static final native void sendPUK(byte[] var0, byte[] var1);

   public static final native void requestEnablePIN(byte[] var0);

   public static final native void requestDisablePIN(byte[] var0);

   public static final native void requestChangePIN(int var0, byte[] var1, byte[] var2, byte[] var3);

   public static final native void requestValidatePIN(int var0, byte[] var1);

   public static final native int getMEPState(int var0);

   public static final native int getMEPDeactivateRetriesRemaining(int var0);

   public static final native int getMEPDeactivateAttempts(int var0);

   public static final native void requestDeactivateMEP(int var0, byte[] var1);

   public static final native String getVoiceMailNumber(int var0);

   public static final native int getWaitingIndicators();

   public static final native void setWaitingIndicators(int var0);

   public static final native int getCSPFlags(int var0);

   public static final native void requestDeleteSMS(int var0);

   public static final native void requestMarkSMSAsRead(int var0);

   private static final native void requestEFInfo(int var0, int var1);

   public static final void requestEFInfo(int id) {
      requestEFInfo(id, Process.currentProcess().getProcessId());
   }

   private static final native int requestEFRead(int var0, int var1, int var2, byte[] var3, int var4);

   public static final int requestEFRead(int id, int structure, int record, byte[] data) {
      return requestEFRead(id, structure, record, data, Process.currentProcess().getProcessId());
   }

   private static final native void requestEFWrite(int var0, int var1, int var2, byte[] var3, int var4);

   public static final void requestEFWrite(int id, int structure, int record, byte[] data) {
      requestEFWrite(id, structure, record, data, Process.currentProcess().getProcessId());
   }

   public static final native void fileUpdated(int var0);

   public static final native void atDisplayTextAck(boolean var0);

   public static final native void atGetInkeyAck(int var0);

   public static final native void atGetInputAck(byte[] var0);

   public static final native void atSelectItemAck(int var0);

   public static final native void atSetUpMenuAck(boolean var0);

   public static final native void atMenuSelected(int var0, boolean var1);

   public static final native int atSetUpCallAck(boolean var0);

   public static final native void atBack();

   public static final native void atCancel();

   public static final native void atHelp(int var0);

   public static final native void atPlayToneAck();

   public static final native void atLaunchBrowserAck(boolean var0);

   public static final native void atEventActive(int var0);

   public static final native void atSetLocale(int var0);

   public static final String decodeAlphaId(byte[] alphaId) {
      return alphaId == null ? null : decodeAlphaId(alphaId, 0, alphaId.length);
   }

   private static final String decodeMixedCoding(byte[] alphaId, int offset, int length, int ucs2Base) {
      StringBuffer sb = new StringBuffer();

      try {
         int i = 0;

         while (i < length) {
            if ((alphaId[offset] & 128) == 0) {
               sb.append(new String(alphaId, offset, 1, "SMS"));
            } else {
               sb.append((char)(ucs2Base + (alphaId[offset] & 127)));
            }

            i++;
            offset++;
         }
      } catch (UnsupportedEncodingException var6) {
      }

      return sb.toString();
   }

   public static final String decodeAlphaId(byte[] alphaId, int offset, int length) {
      if (alphaId == null) {
         return null;
      }

      if (length != 0) {
         switch (alphaId[offset]) {
            case -129:
               try {
                  int stringLength = 0;

                  while (stringLength < length && alphaId[offset + stringLength] != -1) {
                     stringLength++;
                  }

                  return new String(alphaId, offset, stringLength, "SMS");
               } catch (UnsupportedEncodingException var5) {
                  break;
               }
            case -128:
            default:
               try {
                  int end = ++offset;
                  int max = offset + length - 1;

                  while (end < max && (alphaId[end] != -1 || alphaId[end + 1] != -1)) {
                     end += 2;
                  }

                  return new String(alphaId, offset, end - offset, "UnicodeBigUnmarked");
               } catch (UnsupportedEncodingException var6) {
                  break;
               }
            case -127:
               if (length > 3) {
                  int stringLength = alphaId[++offset] & 255;
                  int ucs2Base = (alphaId[++offset] & 255) << 7;
                  return decodeMixedCoding(alphaId, ++offset, stringLength, ucs2Base);
               }
               break;
            case -126:
               if (length > 4) {
                  int stringLength = alphaId[++offset] & 255;
                  int ucs2Base = (alphaId[++offset] & 255) << 8 | alphaId[++offset] & 255;
                  return decodeMixedCoding(alphaId, ++offset, stringLength, ucs2Base);
               }
         }
      }

      return "";
   }

   public static final byte[] encodeAlphaId(String alphaId) {
      if (alphaId == null) {
         return null;
      }

      byte[] bytes = null;
      boolean ucs2 = false;
      int length = alphaId.length();

      for (int i = 0; i < length; i++) {
         if (!SMSPacketHeader.validateForDefaultMessageCoding(alphaId.charAt(i))) {
            ucs2 = true;
            break;
         }
      }

      try {
         if (!ucs2) {
            return alphaId.getBytes("SMS");
         }

         bytes = alphaId.getBytes("UnicodeBigUnmarked");
         Arrays.insertAt(bytes, (byte)-128, 0);
      } catch (UnsupportedEncodingException var5) {
      }

      return bytes;
   }

   public static final boolean is3DigitMNC() {
      byte[] imsi = getIMSI();
      if (imsi != null) {
         int mnc = imsi[3] << 4;
         mnc |= imsi[4];
         int mcc = imsi[0] << 8;
         mcc |= imsi[1] << 4;
         mcc |= imsi[2];
         SE13NetworkTable se13NetTable = (SE13NetworkTable)ApplicationRegistry.getApplicationRegistry().waitFor(-7927117593081548760L);
         if (DeviceInfo.isSimulator() || se13NetTable != null && se13NetTable.is3DigitMNC(mnc << 16 | mcc)) {
            return true;
         }
      }

      return false;
   }

   public static final int getIMSICRC() {
      try {
         return CRC16.update(65535, getIMSI());
      } catch (SIMCardException var1) {
         return 0;
      } catch (UnsupportedOperationException var2) {
         return 0;
      }
   }

   public static final boolean is3DigitMNC(int mcc, int mnc) {
      int networkId = mnc << 16 | mcc;
      SE13NetworkTable se13NetTable = (SE13NetworkTable)ApplicationRegistry.getApplicationRegistry().waitFor(-7927117593081548760L);
      return se13NetTable.is3DigitMNC(networkId);
   }

   public static final native int requestPhoneBookRead(int var0);

   public static final native int getPhoneBookEntry(SIMCardPhoneBookEntry var0);

   public static final native int requestPhoneBookWrite(SIMCardPhoneBookEntry var0);

   public static final native int requestPhoneBookDelete(int var0);

   public static final void openAPDUConnection(byte[] aid, byte offset, byte aidlen, byte tag, byte fcpResponseType) {
      if (aid == null) {
         openAPDUConnection(null, tag, fcpResponseType);
      } else {
         if (aid.length - offset < aidlen) {
            throw new IllegalArgumentException();
         }

         openAPDUConnection(Arrays.copy(aid, offset, aidlen), tag, fcpResponseType);
      }
   }

   public static final native void openAPDUConnection(byte[] var0, byte var1, byte var2);

   public static final native void exchangeAPDU(byte[] var0, byte var1, byte var2);

   public static final native byte[] getAPDUResponse(byte var0, byte var1);

   public static final native void closeAPDUConnection(byte var0, byte var1);

   public static final native byte[] getATR();

   public static final native void doAPDUPinOperation(byte var0, byte var1, byte var2, byte[] var3, byte[] var4);

   public static final native boolean isJSR177Supported();

   public static final void addListener(Application app, SIMCardListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(43) == null) {
            dispatchManager.setDispatcher(43, new SIMCardStatusEventDispatcher());
            dispatchManager.setDispatcher(44, new SIMCardSecurityEventDispatcher());
            dispatchManager.setDispatcher(46, new SIMCardEFEventDispatcher());
            dispatchManager.setDispatcher(45, new SIMCardATEventDispatcher());
            dispatchManager.setDispatcher(47, new SIMCardPBEventDispatcher());
            dispatchManager.setDispatcher(50, new SIMCardAPDUEventDispatcher());
         }
      }

      if (listener instanceof SIMCardStatusListener) {
         app.addListener(43, listener);
      }

      if (listener instanceof SIMCardSecurityListener) {
         app.addListener(44, listener);
      }

      if (listener instanceof SIMCardEFListener) {
         app.addListener(46, listener);
      }

      if (listener instanceof SIMCardATListener) {
         app.addListener(45, listener);
      }

      if (listener instanceof SIMCardPhoneBookListener) {
         app.addListener(47, listener);
      }

      if (listener instanceof SIMCardAPDUListener) {
         app.addListener(50, listener);
      }
   }

   public static final void removeListener(Application app, SIMCardListener listener) {
      app.removeListener(43, listener);
      app.removeListener(44, listener);
      app.removeListener(46, listener);
      app.removeListener(45, listener);
      app.removeListener(47, listener);
      app.removeListener(50, listener);
   }
}
