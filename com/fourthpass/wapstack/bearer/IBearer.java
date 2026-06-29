package com.fourthpass.wapstack.bearer;

import com.fourthpass.wapstack.wdp.WDPPacket;
import net.rim.device.cldc.io.utility.SessionStats;

public interface IBearer {
   int UDP;
   int SMS;
   byte BT_IPV4;
   byte BT_IPV6;
   byte BT_GSM_USSD_GSMMSISDN;
   byte BT_GSM_SMS_GSMMSISDN;
   byte BT_IS136_GUTS_IS136MSISDN;
   byte BT_IS95CDMA_SMS_IS637MSISDN;
   byte BT_IS95CDMA_CSD_IPV4;
   byte BT_IS95CDMA_PACKETDATA_IPV4;
   byte BT_IS136_CSD_IPV4;
   byte BT_IS136_PACKETDATA_IPV4;
   byte BT_GSM_CSD_IPV4;
   byte BT_GSM_GPRS_IPV4;
   byte BT_GSM_USSD_IPV4;
   byte BT_AMPS_CDPD_IPV4;
   byte BT_PDC_CSD_IPV4;
   byte BT_PDC_PACKETDATA_IPV4;
   byte BT_IDEN_SMS_IDENMSISDN;
   byte BT_IDEN_CSD_IPV4;
   byte BT_IDEN_PACKETDATA_IPV4;
   byte BT_PAGING_FLEXMSISDN;
   byte BT_PHS_SMS_PHSMSISDN;
   byte BT_PHS_CSD_IPV4;
   byte BT_GSM_USSD_GSMSC;
   byte BT_TETRA_SDS_TETRAITSI;
   byte BT_TETRA_SDS_TETRAMSISDN;
   byte BT_TETRA_PACKETDATA_IPV4;

   int receive(WDPPacket var1);

   int send(WDPPacket var1);

   void closeConnection();

   int getTransmissionTimeout();

   void setTransmissionTimeout(int var1);

   void setReceivingTimeout(int var1);

   boolean isClosed();

   SessionStats getSessionStats();
}
