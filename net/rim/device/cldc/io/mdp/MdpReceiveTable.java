package net.rim.device.cldc.io.mdp;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;

final class MdpReceiveTable implements RealtimeClockListener {
   private int _timer;
   private int _duplicateHead;
   private int _duplicateTail;
   private int[] _duplicateTimes = new int[32];
   private int[] _duplicateKeys = new int[32];
   private int[] _duplicateCrcs = new int[32];
   private Object[] _duplicateCrcss = new Object[32];
   private int[] _duplicateRefs = new int[32];
   private byte[] _duplicateFlags = new byte[32];
   private Vector _assemblyTimes = new Vector();
   private IntHashtable _assemblyDatagrams;
   private PersistentObject _assemblyPersist;
   private IntHashtable _urAssemblyDatagrams = new IntHashtable();
   private Vector _confirmationTimes = new Vector();
   private IntHashtable _confirmationDatagrams = new IntHashtable();
   private Transport _mdpTransport;
   private static final int MAXIMUM_DUPLICATES = 32;
   private static final int DUPLICATE_TMO = 30;
   private static final int ASSEMBLY_TMO = 30;
   private static final int ASSEMBLY_COUNT = 32;
   private static final int CONFIRMATION_TMO = 2;
   private static final long GUID = 15827351342928456L;

   MdpReceiveTable() {
      this._assemblyPersist = RIMPersistentStore.getPersistentObject(15827351342928456L);
      this._assemblyDatagrams = (IntHashtable)this._assemblyPersist.getContents();
      if (this._assemblyDatagrams == null) {
         this._assemblyDatagrams = new IntHashtable();
         this._assemblyPersist.setContents(this._assemblyDatagrams, 51);
         this._assemblyPersist.commit();
      } else {
         Enumeration e = this._assemblyDatagrams.elements();

         while (e.hasMoreElements()) {
            MdpRxDatagram datagram = (MdpRxDatagram)e.nextElement();
            datagram._timer = this._timer + 30;
            datagram._assemblyTmo = 32;
            this._assemblyTimes.addElement(datagram);
         }
      }

      this._mdpTransport = Transport.getInstance();
      ProtocolDaemon.getInstance().addRealtimeClockListener(this);
   }

   final void addDuplicateDatagram(int datagramKey, int reference, int crc, int[] crcs, boolean datagramAckFlag, boolean optimizedAckFlag) {
      this._duplicateTimes[this._duplicateHead] = this._timer + 30;
      this._duplicateKeys[this._duplicateHead] = datagramKey;
      this._duplicateRefs[this._duplicateHead] = reference;
      this._duplicateCrcs[this._duplicateHead] = crc;
      this._duplicateCrcss[this._duplicateHead] = crcs;
      this._duplicateFlags[this._duplicateHead] = 0;
      if (datagramAckFlag) {
         this._duplicateFlags[this._duplicateHead] = (byte)(this._duplicateFlags[this._duplicateHead] | 1);
      }

      if (optimizedAckFlag) {
         this._duplicateFlags[this._duplicateHead] = (byte)(this._duplicateFlags[this._duplicateHead] | 2);
      }

      this._duplicateHead = this._duplicateHead + 1 & 31;
      if (this._duplicateHead == this._duplicateTail) {
         this._duplicateTail = this._duplicateTail + 1 & 31;
      }
   }

   final int findDuplicateDatagram(int key, int sequence, int crc, int maxSequence) {
      int i = this._duplicateHead;

      while (i != this._duplicateTail) {
         i = i - 1 & 31;
         if (this._duplicateKeys[i] == key) {
            if (this._duplicateCrcss[i] == null) {
               if (maxSequence == 0 && this._duplicateCrcs[i] == crc) {
                  return i;
               }
            } else {
               int[] crcs = (int[])this._duplicateCrcss[i];
               if (maxSequence + 1 == crcs.length && crcs[sequence] == crc) {
                  return i;
               }
            }
         }
      }

      return -1;
   }

   final void addAssemblyDatagram(int datagramKey, MdpRxDatagram datagram) {
      datagram._timer = this._timer + 30;
      datagram._assemblyTmo = 32;
      this._assemblyTimes.addElement(datagram);
      if (!datagram._unreliable) {
         this._assemblyDatagrams.put(datagramKey, datagram);
      } else {
         this._urAssemblyDatagrams.put(datagramKey, datagram);
      }
   }

   final MdpRxDatagram findAssemblyDatagram(int datagramKey) {
      Object object = this._assemblyDatagrams.get(datagramKey);
      if (object == null) {
         object = this._urAssemblyDatagrams.get(datagramKey);
      }

      return (MdpRxDatagram)object;
   }

   final void removeAssemblyDatagram(int datagramKey) {
      Object object = this._assemblyDatagrams.remove(datagramKey);
      if (object != null) {
         this._assemblyTimes.removeElement(object);
         this._assemblyPersist.commit();
      } else {
         object = this._urAssemblyDatagrams.remove(datagramKey);
         if (object != null) {
            this._assemblyTimes.removeElement(object);
         }
      }
   }

   final void makeAssemblyDatagramUnreliable(int datagramKey, MdpRxDatagram datagram) {
      this._urAssemblyDatagrams.put(datagramKey, datagram);
      this._assemblyDatagrams.remove(datagramKey);
      this._assemblyPersist.commit();
   }

   final void commitAssemblyDatagrams() {
      this._assemblyPersist.commit();
   }

   final void updateAssemblyDatagrams() {
      for (int i = this._assemblyTimes.size() - 1; i >= 0; i--) {
         MdpRxDatagram mdpDatagram = (MdpRxDatagram)this._assemblyTimes.elementAt(i);
         if (--mdpDatagram._assemblyTmo <= 0) {
            if (this._assemblyDatagrams.remove(mdpDatagram._key) == null) {
               this._urAssemblyDatagrams.remove(mdpDatagram._key);
            }

            this._assemblyTimes.removeElementAt(i);
         }
      }
   }

   final void addConfirmationDatagram(int datagramKey, MdpRxDatagram datagram) {
      datagram._timer = this._timer + 2;
      this._confirmationTimes.addElement(datagram);
      this._confirmationDatagrams.put(datagramKey, datagram);
   }

   final MdpRxDatagram findConfirmationDatagram(int datagramKey) {
      return (MdpRxDatagram)this._confirmationDatagrams.get(datagramKey);
   }

   final void removeConfirmationDatagram(int datagramKey) {
      Object object = this._confirmationDatagrams.remove(datagramKey);
      if (object != null) {
         this._confirmationTimes.removeElement(object);
      }
   }

   static final int calculateDatagramKey(DatagramAddressBase addressBase, int reference) {
      return addressBase.hashCode() ^ reference;
   }

   final byte[] getDuplicateDatagrams() {
      byte[] buf = new byte[this._duplicateHead - this._duplicateTail & 31];
      int count = 0;

      for (int i = this._duplicateTail; i != this._duplicateHead; i = i + 1 & 31) {
         buf[count++] = (byte)this._duplicateRefs[i];
      }

      return buf;
   }

   final byte getDuplicateDatagramFlags(int index) {
      return index >= 0 && index < this._duplicateFlags.length ? this._duplicateFlags[index] : 0;
   }

   final void removeDuplicateDatagrams() {
      this._duplicateTail = this._duplicateHead;
   }

   final byte[] getProcessingDatagrams() {
      int assemblySize = this._assemblyTimes.size();
      int confirmationSize = this._confirmationTimes.size();
      byte[] buf = new byte[assemblySize + confirmationSize];

      for (int i = 0; i < assemblySize; i++) {
         buf[i] = (byte)((MdpRxDatagram)this._assemblyTimes.elementAt(i))._reference;
      }

      for (int i = 0; i < confirmationSize; i++) {
         buf[assemblySize + i] = (byte)((MdpRxDatagram)this._confirmationTimes.elementAt(i))._reference;
      }

      return buf;
   }

   final void removeProcessingDatagrams() {
      this._assemblyTimes.removeAllElements();
      this._assemblyDatagrams.clear();
      this._assemblyPersist.commit();
      this._urAssemblyDatagrams.clear();
      this._confirmationTimes.removeAllElements();
      this._confirmationDatagrams.clear();
   }

   @Override
   public final void clockUpdated() {
      synchronized (this) {
         this._timer++;

         while (this._duplicateTail != this._duplicateHead && this._timer >= this._duplicateTimes[this._duplicateTail]) {
            this._duplicateCrcss[this._duplicateTail] = null;
            this._duplicateTail = this._duplicateTail + 1 & 31;
         }

         boolean commit = false;

         while (!this._assemblyTimes.isEmpty()) {
            MdpRxDatagram mdpDatagram = (MdpRxDatagram)this._assemblyTimes.firstElement();
            if (this._timer < mdpDatagram._timer) {
               break;
            }

            EventLogger.logEvent(4080229686686977759L, 1380213112, 0);
            if (this._assemblyDatagrams.remove(mdpDatagram._key) != null) {
               commit = true;
            } else {
               this._urAssemblyDatagrams.remove(mdpDatagram._key);
            }

            this._assemblyTimes.removeElementAt(0);
         }

         if (commit) {
            this._assemblyPersist.commit();
         }

         while (!this._confirmationTimes.isEmpty()) {
            MdpRxDatagram mdpDatagram = (MdpRxDatagram)this._confirmationTimes.firstElement();
            if (this._timer < mdpDatagram._timer) {
               break;
            }

            EventLogger.logEvent(4080229686686977759L, 1380213624, 0);
            this._confirmationDatagrams.remove(mdpDatagram._key);
            this._confirmationTimes.removeElementAt(0);
            if (mdpDatagram._wtProfile != null) {
               this._mdpTransport.updateWindowArrays((WirelessTransportProfile)mdpDatagram._wtProfile, mdpDatagram._key, (byte)3);
               mdpDatagram._wtProfile = null;
            }
         }

         this._mdpTransport.requestWirelessTransportProfileParam();
      }
   }
}
