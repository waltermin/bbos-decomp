package net.rim.device.cldc.io.mdp;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.util.Arrays;

final class MdpSendTable {
   private byte[] _pendingDatagramsState = new byte[128];
   private byte[][][] _pendingDatagramsPacketsState = new byte[128][][];

   final void addPendingDatagram(int reference, int maxSequence) {
      this._pendingDatagramsPacketsState[reference] = (byte[][])(new byte[maxSequence + 1]);
      this.resetPendingDatagram(reference);
   }

   final void resetPendingDatagram(int reference) {
      if (this._pendingDatagramsPacketsState[reference] != null) {
         this._pendingDatagramsState[reference] = 0;
         Arrays.fill((byte[])this._pendingDatagramsPacketsState[reference], (byte)0);
      }
   }

   final void setPendingDatagramState(int reference, byte state) {
      this._pendingDatagramsState[reference] = state;
   }

   final byte getPendingDatagramState(int reference) {
      return this._pendingDatagramsState[reference];
   }

   final byte[] findPendingDatagramPackets(int reference) {
      return (byte[])this._pendingDatagramsPacketsState[reference];
   }

   final void setPendingDatagramPacketState(int reference, int sequence, byte state) {
      this._pendingDatagramsPacketsState[reference][sequence] = (byte[])state;
   }

   final byte getPendingDatagramPacketState(int reference, int sequence) {
      return (byte)this._pendingDatagramsPacketsState[reference][sequence];
   }

   final int findPendingDatagramPacketByState(int reference, byte state) {
      return Arrays.getIndex((byte[])this._pendingDatagramsPacketsState[reference], state);
   }

   final void removePendingDatagram(int reference) {
      this._pendingDatagramsState[reference] = 0;
      this._pendingDatagramsPacketsState[reference] = null;
   }

   final void removeAllPendingDatagrams() {
      for (int i = this._pendingDatagramsState.length - 1; i >= 0; i--) {
         this.removePendingDatagram(i);
      }
   }

   static final int calculateDatagramKey(DatagramAddressBase addressBase, int reference) {
      return addressBase.hashCode() ^ reference;
   }
}
