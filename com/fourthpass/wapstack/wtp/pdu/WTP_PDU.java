package com.fourthpass.wapstack.wtp.pdu;

import com.fourthpass.wapstack.WAPManagementEntity;
import com.fourthpass.wapstack.wdp.WDPPacket;
import net.rim.device.api.util.IntHashtable;
import net.rim.vm.Array;

public class WTP_PDU {
   protected byte[] _PDU;
   protected byte[] _userData;
   protected short _pduLength;
   protected short _userDataLength;
   protected short _totalTPILength;
   protected short _TPICount;
   protected short _headerLength;
   private IntHashtable _tpiIdentities;

   public WTP_PDU() {
      this._PDU = new byte[WAPManagementEntity.getWTP_PDUBufferSize()];
      this._pduLength = (short)this._PDU.length;
   }

   public WTP_PDU(WDPPacket wdpPacket) {
      byte[] packetData = wdpPacket.getPacketData();
      byte pduType = (byte)(packetData[0] >> 3 & 15);
      switch (pduType) {
         case 0:
            this._headerLength = 0;
            break;
         case 1:
         case 4:
         case 5:
         case 6:
         default:
            this._headerLength = 4;
            break;
         case 2:
         case 3:
            this._headerLength = 3;
            break;
         case 7:
            this._headerLength = (short)(4 + packetData[3]);
      }

      short index = this._headerLength;
      if ((packetData[0] & 128) != 0) {
         this._tpiIdentities = new IntHashtable();

         short tpiIndex;
         do {
            tpiIndex = index;
            this._TPICount++;
            int identity = packetData[index] >> 3 & 15;
            int length = 0;
            short var11;
            if ((packetData[index] & 4) == 0) {
               var11 = (short)(packetData[index] & 3);
            } else {
               var11 = (short)(packetData[++index] & 0xFF);
            }

            index++;
            byte[] item = new byte[var11];
            System.arraycopy(packetData, index, item, 0, var11);
            this._tpiIdentities.put(identity, item);
            index += var11;
         } while ((packetData[tpiIndex] & 128) != 0);
      }

      this._PDU = new byte[index];
      System.arraycopy(packetData, 0, this._PDU, 0, index);
      if (index < packetData.length) {
         int size = packetData.length - index;
         System.arraycopy(packetData, index, packetData, 0, size);
         Array.resize(packetData, size);
         this._userData = packetData;
         this._userDataLength = (short)size;
      }
   }

   public byte getPDUType() {
      return (byte)(this._PDU[0] >> 3 & 15);
   }

   public int getTID() {
      int tid = this._PDU[1] << 8 & 0xFF00;
      if (tid > 0) {
         tid ^= 32768;
      }

      return tid + (this._PDU[2] & 0xFF);
   }

   public void addData(byte[] data, short startIndex, short endIndex) {
      short dataSize = (short)(endIndex - startIndex);
      this.ensureUserDataBufferCapacity(dataSize);
      System.arraycopy(data, 0, this._userData, 0, dataSize);
      this._userDataLength = dataSize;
   }

   public void addData(byte[] data) {
      this.addData(data, (short)0, (short)data.length);
   }

   public byte[] getUserData() {
      return this._userData;
   }

   public boolean isRetransmittedPacket() {
      return (this._PDU[0] & 1) != 0;
   }

   public byte[] getTPI(int id) {
      return this._tpiIdentities == null ? null : (byte[])this._tpiIdentities.get(id);
   }

   public byte[] getDataToBeTransmitted() {
      short pduLength = (short)(this._headerLength + this._totalTPILength);
      byte[] txData = new byte[pduLength + this._userDataLength];
      if (pduLength != 0) {
         System.arraycopy(this._PDU, 0, txData, 0, pduLength);
      }

      if (this._userDataLength != 0) {
         System.arraycopy(this._userData, 0, txData, pduLength, this._userDataLength);
      }

      return txData;
   }

   public byte[] getDataToBeReTransmitted() {
      if ((this._PDU[0] & 1) == 0) {
         this._PDU[0]++;
      }

      return this.getDataToBeTransmitted();
   }

   protected void ensureUserDataBufferCapacity(short capacity) {
      if (this._userData == null || this._userData.length < capacity) {
         if (capacity < 0) {
            capacity = WAPManagementEntity.getWTP_PDUBufferSize();
         }

         byte[] userData = new byte[capacity];
         if (this._userData != null) {
            System.arraycopy(this._userData, 0, userData, 0, this._userData.length);
         }

         this._userData = userData;
      }
   }

   protected void populateTID(int TID) {
      this._PDU[1] = (byte)(TID >> 8 & 0xFF);
      this._PDU[2] = (byte)(TID & 0xFF);
   }
}
