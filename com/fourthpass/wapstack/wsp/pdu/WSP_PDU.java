package com.fourthpass.wapstack.wsp.pdu;

import com.fourthpass.wapstack.WAPManagementEntity;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import net.rim.vm.Array;

public class WSP_PDU {
   protected byte[] _PDU;
   protected InputStream _pduData;
   protected int _pduLength;
   protected int _dataLength;
   protected boolean _connectionLessMode;
   protected byte[] _uintvar;

   public WSP_PDU() {
      this(false);
   }

   public WSP_PDU(boolean connectionLessMode) {
      this._PDU = new byte[WAPManagementEntity.getWSP_PDUBufferSize()];
      this._pduLength = this._PDU.length;
      this._uintvar = new byte[5];
      this._connectionLessMode = connectionLessMode;
   }

   public WSP_PDU(boolean connectionLessMode, byte[] input) {
      this._connectionLessMode = connectionLessMode;
      byte extraOctet = 1;
      if (this._connectionLessMode) {
         extraOctet = 0;
      }

      this._PDU = new byte[input.length + extraOctet];
      this._dataLength = this._PDU.length;
      System.arraycopy(input, 0, this._PDU, extraOctet, input.length);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public WSP_PDU(boolean connectionLessMode, InputStream input) {
      this._connectionLessMode = connectionLessMode;
      byte extraOctet = 1;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      boolean var8 = false /* VF: Semaphore variable */;

      byte[] data;
      try {
         var8 = true;

         for (int e = input.read(); e != -1; e = input.read()) {
            bos.write(e);
         }

         data = bos.toByteArray();
         var8 = false;
      } finally {
         if (var8) {
            return;
         }
      }

      if (this._connectionLessMode) {
         extraOctet = 0;
      }

      this._PDU = new byte[data.length + extraOctet];
      this._dataLength = this._PDU.length;
      System.arraycopy(data, 0, this._PDU, extraOctet, data.length);
   }

   public byte[] getDataToBeTransmitted() {
      int beginIndex = 1;
      int dataLength = this._dataLength - 1;
      if (this._connectionLessMode) {
         beginIndex = 0;
         dataLength = this._dataLength;
      }

      byte[] data = new byte[dataLength];
      System.arraycopy(this._PDU, beginIndex, data, 0, dataLength);
      return data;
   }

   public byte getPDUType() {
      return this._dataLength > 0 ? this._PDU[1] : 0;
   }

   public byte getTID() {
      return this._PDU[0];
   }

   public void appendPDU(byte[] pduData, int startIndex, int endIndex) {
      if (pduData != null && pduData.length != 0 && startIndex >= 0 && endIndex > 0 && endIndex - startIndex <= pduData.length) {
         int size = endIndex - startIndex;
         this.ensurePDUBufferCapicity(size);
         System.arraycopy(pduData, startIndex, this._PDU, this._dataLength, size);
         this._dataLength += size;
      }
   }

   public void appendPDU(byte[] pduData) {
      if (pduData != null) {
         this.appendPDU(pduData, 0, pduData.length);
      }
   }

   public static WSP_PDU PDUFactory(boolean connectionLess, InputStream wtpInput) {
      if (wtpInput != null) {
         try {
            if (connectionLess) {
               wtpInput.read();
            }

            int type = wtpInput.read();
            wtpInput.reset();
            switch (type) {
               case 1:
                  break;
               case 2:
               default:
                  return new WSP_ConnectReplyPDU(connectionLess, wtpInput);
               case 3:
                  return new WSP_RedirectPDU(connectionLess, wtpInput);
               case 4:
                  return new WSP_ReplyPDU(connectionLess, wtpInput);
               case 5:
                  return new WSP_DisconnectPDU(connectionLess, wtpInput);
               case 6:
                  wtpInput.reset();
                  return new WSP_PushPDU(connectionLess, wtpInput);
               case 7:
                  wtpInput.reset();
                  return new WSP_ConfirmedPushPDU(connectionLess, wtpInput);
               case 8:
                  return new WSP_SuspendPDU(connectionLess, wtpInput);
               case 9:
                  return new WSP_ResumePDU(connectionLess, wtpInput);
            }
         } finally {
            return null;
         }
      }

      return null;
   }

   protected void ensurePDUBufferCapicity(int additionalCapacity) {
      int minRequired = this._dataLength + additionalCapacity;
      if (minRequired > this._pduLength) {
         Array.resize(this._PDU, minRequired);
         this._pduLength = minRequired;
      }
   }
}
