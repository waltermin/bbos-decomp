package com.fourthpass.wapstack.bearer;

import com.fourthpass.wapstack.wdp.WDPPacket;
import net.rim.device.cldc.io.utility.SessionStats;

public interface IBearer {
   int UDP = 0;
   int SMS = 1;
   byte BT_IPV4 = 0;
   byte BT_IPV6 = 1;
   byte BT_GSM_USSD_GSMMSISDN = 2;
   byte BT_GSM_SMS_GSMMSISDN = 3;
   byte BT_IS136_GUTS_IS136MSISDN = 4;
   byte BT_IS95CDMA_SMS_IS637MSISDN = 5;
   byte BT_IS95CDMA_CSD_IPV4 = 6;
   byte BT_IS95CDMA_PACKETDATA_IPV4 = 7;
   byte BT_IS136_CSD_IPV4 = 8;
   byte BT_IS136_PACKETDATA_IPV4 = 9;
   byte BT_GSM_CSD_IPV4 = 10;
   byte BT_GSM_GPRS_IPV4 = 11;
   byte BT_GSM_USSD_IPV4 = 12;
   byte BT_AMPS_CDPD_IPV4 = 13;
   byte BT_PDC_CSD_IPV4 = 14;
   byte BT_PDC_PACKETDATA_IPV4 = 15;
   byte BT_IDEN_SMS_IDENMSISDN = 16;
   byte BT_IDEN_CSD_IPV4 = 17;
   byte BT_IDEN_PACKETDATA_IPV4 = 18;
   byte BT_PAGING_FLEXMSISDN = 19;
   byte BT_PHS_SMS_PHSMSISDN = 20;
   byte BT_PHS_CSD_IPV4 = 21;
   byte BT_GSM_USSD_GSMSC = 22;
   byte BT_TETRA_SDS_TETRAITSI = 23;
   byte BT_TETRA_SDS_TETRAMSISDN = 24;
   byte BT_TETRA_PACKETDATA_IPV4 = 25;

   int receive(WDPPacket var1);

   int send(WDPPacket var1);

   void closeConnection();

   int getTransmissionTimeout();

   void setTransmissionTimeout(int var1);

   void setReceivingTimeout(int var1);

   boolean isClosed();

   SessionStats getSessionStats();
}
