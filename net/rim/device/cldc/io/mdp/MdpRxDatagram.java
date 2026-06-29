package net.rim.device.cldc.io.mdp;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.cldc.io.udp.UdpInternalAddress;

final class MdpRxDatagram implements Persistable {
   protected int _timer;
   protected Object _subAddress;
   protected int _reference;
   protected int _key;
   protected int _crc;
   protected int[] _crcs;
   protected boolean _datagramAckRequired;
   protected boolean _optimizedAckFlag;
   protected boolean _packetAckRequired;
   protected boolean _extAckRequired;
   protected int _sequence;
   protected DataBuffer _buffer;
   protected int _srcPort;
   protected int _destPort;
   protected boolean _unreliable;
   protected int _assemblyTmo;
   protected Object _wtProfile;
   protected int _wtAddress;
   protected int _lastSequence = -1;

   MdpRxDatagram(DatagramAddressBase subAddress, int reference, int key, int maxSequence) {
      this._reference = reference;
      this._key = key;
      if (maxSequence > 0) {
         this._crc = maxSequence;
         this._crcs = new int[maxSequence + 1];
         Arrays.fill(this._crcs, -1);
      }

      this._subAddress = subAddress;
      if (subAddress instanceof Object) {
         this._wtAddress = ((UdpInternalAddress)subAddress).getGpakHostAddress();
      }
   }

   final void stringDatagramAddress() {
      DatagramAddressBase subAddress = (DatagramAddressBase)this._subAddress;
      this._subAddress = subAddress.getAddress();
   }

   final boolean waitingForPackets() {
      return this._crc > 0;
   }

   final boolean packetReceived(int sequence) {
      return sequence == 0 && this._crcs == null || this._crcs[sequence] != -1;
   }

   final byte[] makeStatusArray() {
      byte[] buffer = null;
      if (this._crcs != null) {
         int offset = this._crcs.length - 1 >> 3;
         buffer = new byte[offset + 1];

         for (int i = this._crcs.length - 1; i > 0; i--) {
            int pos = i & 7;
            if (this._crcs[i] != -1) {
               buffer[offset] = (byte)(buffer[offset] | 1 << pos);
            }

            if (pos == 0) {
               offset--;
            }
         }

         if (this._crcs[0] != -1) {
            buffer[0] = (byte)(buffer[0] | 1);
            return buffer;
         }
      } else {
         buffer = new byte[]{1};
      }

      return buffer;
   }
}
