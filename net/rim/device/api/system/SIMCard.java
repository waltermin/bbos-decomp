package net.rim.device.api.system;

import java.io.UnsupportedEncodingException;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC16;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.SE13NetworkTable;
import net.rim.vm.Process;

public final class SIMCard {
   public static final int PIN1;
   public static final int PIN2;
   public static final int MEP_CATEGORY_SIM;
   public static final int MEP_CATEGORY_NETWORK;
   public static final int MEP_CATEGORY_NETWORK_SUBSET;
   public static final int MEP_CATEGORY_SERVICE_PROVIDER;
   public static final int MEP_CATEGORY_CORPORATE;
   public static final int MEP_STATE_INACTIVE;
   public static final int MEP_STATE_PASSED;
   public static final int MEP_STATE_DISABLED;
   public static final int MEP_STATE_BLOCKED;
   public static final int MEP_STATE_UNKNOWN;
   public static final int MEP_STATE_FAILED;
   public static final int MEP_STATE_NOT_CHECKED;
   public static final int INDICATOR_LINE_1;
   public static final int INDICATOR_LINE_2;
   public static final int INDICATOR_DATA;
   public static final int INDICATOR_FAX;
   public static final int EF_ICCID;
   public static final int EF_LP;
   public static final int EF_IMSI;
   public static final int EF_KC;
   public static final int EF_PLMN_SEL;
   public static final int EF_HPLMN;
   public static final int EF_ACM_MAX;
   public static final int EF_SST;
   public static final int EF_EST;
   public static final int EF_UST;
   public static final int EF_ACM;
   public static final int EF_GID1;
   public static final int EF_GID2;
   public static final int EF_SPN;
   public static final int EF_PUCT;
   public static final int EF_CBMI;
   public static final int EF_BCCH;
   public static final int EF_ACC;
   public static final int EF_FPLMN;
   public static final int EF_LOCI;
   public static final int EF_AD;
   public static final int EF_PHASE;
   public static final int EF_ADN;
   public static final int EF_FDN;
   public static final int EF_SMS;
   public static final int EF_CCP;
   public static final int EF_MSISDN;
   public static final int EF_SMSP;
   public static final int EF_SMSS;
   public static final int EF_LND;
   public static final int EF_EXT1;
   public static final int EF_EXT2;
   public static final int EF_CPS;
   public static final int EF_ELP;
   public static final int EF_VGCS;
   public static final int EF_VGCSS;
   public static final int EF_VBS;
   public static final int EF_VBSS;
   public static final int EF_EMLPP;
   public static final int EF_AAEM;
   public static final int EF_CBMID;
   public static final int EF_ECC;
   public static final int EF_CBMIR;
   public static final int EF_DCK;
   public static final int EF_CNL;
   public static final int EF_NIA;
   public static final int EF_SDN;
   public static final int EF_EXT3;
   public static final int EF_BDN;
   public static final int EF_EXT4;
   public static final int EF_SMSR;
   public static final int EF_CPHS_VMWF;
   public static final int EF_CPHS_CFF;
   public static final int EF_CPHS_ON;
   public static final int EF_CPHS_CSP;
   public static final int EF_CPHS_INFO;
   public static final int EF_CPHS_MN;
   public static final int EF_CPHS_ONS;
   public static final int EF_CPHS_INFO_NUM;
   public static final int EF_CPHS_INFO_NUM_OLD;
   public static final int EF_KC_GPRS;
   public static final int EF_LOCI_GPRS;
   public static final int EF_OPL;
   public static final int EF_PNN;
   public static final int EF_HZ_PARAMETERS;
   public static final int EF_HZ_CACHE1;
   public static final int EF_HZ_CACHE2;
   public static final int EF_HZ_CACHE3;
   public static final int EF_HZ_CACHE4;
   public static final int EF_MWIS;
   public static final int EF_PLMNW_ACT;
   public static final int EF_OPLMNW_ACT;
   public static final int EF_HPLMNW_ACT;
   public static final int EF_CPBCCH;
   public static final int EF_INVSCAN;
   public static final int EF_SPDI;
   public static final int EF_LI;
   public static final int EF_PBR;
   public static final int EF_ADN2;
   public static final int EF_ADN3;
   public static final int EF_ADN4;
   public static final int EF_MBDN;
   public static final int EF_MBI;
   public static final int EF_STRUCTURE_TRANSPARENT;
   public static final int EF_STRUCTURE_LINEAR_FIXED;
   public static final int EF_STRUCTURE_CYCLIC;
   public static final int CSP_CALL_OFFERING;
   public static final int CSP_CALL_OFFERING_CFU;
   public static final int CSP_CALL_OFFERING_CFB;
   public static final int CSP_CALL_OFFERING_CFNRY;
   public static final int CSP_CALL_OFFERING_CFNRC;
   public static final int CSP_CALL_OFFERING_CT;
   public static final int CSP_CALL_RESTRICTION;
   public static final int CSP_CALL_RESTRICTION_BOAC;
   public static final int CSP_CALL_RESTRICTION_BOIC;
   public static final int CSP_CALL_RESTRICTION_BOIC_EXHC;
   public static final int CSP_CALL_RESTRICTION_BAIC;
   public static final int CSP_CALL_RESTRICTION_BICROAM;
   public static final int CSP_OTHER_SUPP_SERV;
   public static final int CSP_OTHER_SUPP_SERV_MPTY;
   public static final int CSP_OTHER_SUPP_SERV_CUG;
   public static final int CSP_OTHER_SUPP_SERV_AOC;
   public static final int CSP_OTHER_SUPP_SERV_PREF_CUG;
   public static final int CSP_OTHER_SUPP_SERV_CUG_OA;
   public static final int CSP_CALL_COMPLETION;
   public static final int CSP_CALL_COMPLETION_HOLD;
   public static final int CSP_CALL_COMPLETION_CW;
   public static final int CSP_CALL_COMPLETION_CCBS;
   public static final int CSP_CALL_COMPLETION_USR_USR_SIG;
   public static final int CSP_TELESERV;
   public static final int CSP_TELESERV_SM_MT;
   public static final int CSP_TELESERV_SM_MO;
   public static final int CSP_TELESERV_SM_CB;
   public static final int CSP_TELESERV_REPLY_PATH;
   public static final int CSP_TELESERV_DELIVER_CONF;
   public static final int CSP_TELESERV_PROTOCOL_ID;
   public static final int CSP_TELESERV_VALIDITY_PERIOD;
   public static final int CSP_CPHS_TELESERV;
   public static final int CSP_CPHS_TELESERV_ALS;
   public static final int CSP_CPHS_FEATURES_SST;
   public static final int CSP_NUMBER_ID;
   public static final int CSP_NUMBER_ID_CLIP;
   public static final int CSP_NUMBER_ID_COLR;
   public static final int CSP_NUMBER_ID_COLP;
   public static final int CSP_NUMBER_ID_MCI;
   public static final int CSP_NUMBER_ID_CLI_SEND;
   public static final int CSP_NUMBER_ID_CLI_BLOCK;
   public static final int CSP_PHASE2P_SERV;
   public static final int CSP_PHASE2P_SERV_GPRS;
   public static final int CSP_PHASE2P_SERV_HSCSD;
   public static final int CSP_PHASE2P_SERV_VOICE_GRP_CALL;
   public static final int CSP_PHASE2P_SERV_VOICE_BCAST_SERV;
   public static final int CSP_PHASE2P_SERV_MULT_SUBSC_PROFILE;
   public static final int CSP_PHASE2P_SERV_MULT_BAND;
   public static final int CSP_VALUE_ADDED_1;
   public static final int CSP_VALUE_ADDED_1_PLMNMODE;
   public static final int CSP_VALUE_ADDED_1_VPS;
   public static final int CSP_VALUE_ADDED_1_SM_MO_PAGING;
   public static final int CSP_VALUE_ADDED_1_SM_MO_EMAIL;
   public static final int CSP_VALUE_ADDED_1_FAX;
   public static final int CSP_VALUE_ADDED_1_DATA;
   public static final int CSP_VALUE_ADDED_1_LANGUAGE;
   public static final int CSP_VALUE_ADDED_2;
   public static final int CSP_VALUE_ADDED_2_WAP;
   public static final int CSP_VALUE_ADDED_2_IM;
   public static final int CSP_INFO_NUMBERS;
   public static final int EVENT_MT_CALL;
   public static final int EVENT_CALL_CONNECTED;
   public static final int EVENT_CALL_DISCONNECTED;
   public static final int EVENT_LOCATION_STATUS;
   public static final int EVENT_USER_ACTIVITY;
   public static final int EVENT_IDLE_SCREEN;
   public static final int EVENT_LANGUAGE_SELECTION;
   public static final int EVENT_BROWSER_TERMINATION;
   public static final int SIM_PB_STATUS_INVALID_PARAMETER;
   public static final int SIM_PB_STATUS_OS_BUSY;
   public static final int SIM_PB_STATUS_NO_MORE_ENTRIES;
   public static final int SIM_PB_STATUS_SUCCESS;
   public static final int SIM_JSR177_OPEN_COMPLETE;
   public static final int SIM_JSR177_EXCHANGE_COMPLETE;
   public static final int SIM_JSR177_CLOSE_COMPLETE;
   public static final int SIM_JSR177_PIN_OPERATION_COMPLETE;
   public static final int JSR177_OPEN_SUCCESS;
   public static final int JSR177_OPEN_FAILED;
   public static final int JSR177_OPEN_ERROR_JSR177_NOT_SUPPORTED;
   public static final int JSR177_OPEN_ERROR_JSR177_BAD_AID;
   public static final int JSR177_OPEN_ERROR_NO_FREE_CHANNELS;
   public static final int JSR177_EXCHANGE_SUCCESS;
   public static final int JSR177_EXCHANGE_FAILED;
   public static final int JSR177_EXCHANGE_ERROR_ZERO_LENGTH_APDU;
   public static final int JSR177_EXCHANGE_ERROR_INVALID_CLASS;
   public static final int JSR177_EXCHANGE_ERROR_SENT_SELECT_CMD;
   public static final int JSR177_EXCHANGE_ERROR_SENT_MANAGE_CHANNEL_CMD;
   public static final int JSR177_EXCHANGE_ERROR_MALFORMED_APDU;
   public static final int JSR177_EXCHANGE_ERROR_NO_BUFFER_AVAILABLE;
   public static final int JSR177_CLOSE_SUCCESS;
   public static final int JSR177_CLOSE_FAILED;
   public static final int JSR177_CLOSE_ERROR_BAD_CHANNEL;
   public static final int JSR177_OPEN_NO_FCP_INFO;
   public static final int JSR177_OPEN_FCP_WITH_REF_CLASS;
   public static final int JSR177_OPEN_FCP_WITH_REF_INTERFACE;
   public static final int SIM_CHV_REQ_OK;
   public static final int SIM_CHV_REQ_INVALID_PARAMS;
   public static final int SIM_CHV_REQ_SIM_GENERAL_FAULT;

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
