package com.fourthpass.wapstack.wsp;

import com.fourthpass.wapstack.IPacketTransiver;
import com.fourthpass.wapstack.util.Utils;
import com.fourthpass.wapstack.wdp.WDPPacket;
import com.fourthpass.wapstack.wsp.pdu.WSP_ConfirmedPushPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_GetPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_PDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_PostPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_PushPDU;
import com.fourthpass.wapstack.wsp.pdu.WSP_ReplyPDU;
import java.io.InputStream;

public final class WSPConnectionless {
   private WDPPacket _sendPacket;
   private WDPPacket _recvPacket;
   private byte[] _recvBuffer;
   private int _transactionTimeOut;
   private IPacketTransiver _transiver;
   private WSP_ReplyPDU _replyPDU;
   private byte _statusCode;
   private static byte _nextId = (byte)(System.currentTimeMillis() & 255);

   public WSPConnectionless(IPacketTransiver transiver) {
      this(transiver, 1500);
   }

   public WSPConnectionless(IPacketTransiver transiver, int maxBufLength) {
      this._transiver = transiver;
      this._sendPacket = new WDPPacket();
      this._recvBuffer = new byte[maxBufLength];
      this._recvPacket = new WDPPacket(this._recvBuffer);
      this._transactionTimeOut = 5;
   }

   public final InputStream getURL(byte[] URI, WSPHeaders header, byte methodType, byte[] pData) {
      InputStream result = null;
      if (++_nextId == 0) {
         _nextId++;
      }

      WSP_PDU pdu = this.createMethodPDU(URI, header, methodType, pData, _nextId);
      if (!(pdu instanceof WSP_GetPDU) && !(pdu instanceof WSP_PostPDU)) {
         return null;
      }

      byte[] txData = pdu.getDataToBeTransmitted();
      this._replyPDU = null;
      this._sendPacket.setPacketData(txData, txData.length);
      if (this._transiver.send(this._sendPacket) > 0) {
         this._transiver.setReceivingTimeout(100);
         long timeOut = (long)this._transactionTimeOut * 1000 + System.currentTimeMillis();

         do {
            int recv = this._transiver.receive(this._recvPacket);
            if (recv == 0) {
               break;
            }

            if (recv > 0) {
               byte[] data = new byte[this._recvPacket.getDataLength()];
               System.arraycopy(this._recvPacket.getPacketData(), 0, data, 0, this._recvPacket.getDataLength());
               this._replyPDU = new WSP_ReplyPDU(true, (InputStream)(new Object(data)));
               this._statusCode = this._replyPDU.getStatusCode();
               if (this._replyPDU.getTID() == pdu.getTID()) {
                  if (!Utils.isCompleteStatus(this._statusCode)) {
                     return null;
                  }
                  break;
               }
            }
         } while (System.currentTimeMillis() <= timeOut);
      }

      if (this._replyPDU != null) {
         result = this._replyPDU.getData();
         this._replyPDU.getHeaderInfo(header);
      }

      return result;
   }

   public final byte getStatus() {
      return this._statusCode;
   }

   private final WSP_PDU createMethodPDU(byte[] URI, WSPHeaders header, byte methodType, byte[] pData, byte transactionId) {
      if (methodType >= 64 && methodType <= 68) {
         return new WSP_GetPDU(true, transactionId, methodType, header, URI);
      } else if (methodType == 96 || methodType == 97) {
         return new WSP_PostPDU(true, transactionId, header, URI, pData);
      } else if (methodType == 6) {
         return new WSP_PushPDU(true, transactionId, header, pData);
      } else {
         return methodType == 7 ? new WSP_ConfirmedPushPDU(true, transactionId, header, pData) : null;
      }
   }
}
