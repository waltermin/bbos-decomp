package net.rim.device.api.servicebook;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.TLEFieldController;
import net.rim.device.api.util.TLEUtilities;
import net.rim.vm.Array;

class ServiceRecord$SRParser implements TLEFieldController {
   private ServiceRecord _parentLevelRec = null;
   private ServiceRecord _rec;
   private ServiceRecord[] _multipleRecords;

   public ServiceRecord$SRParser(ServiceRecord rec) {
      this._rec = rec;
   }

   public ServiceRecord$SRParser(ServiceRecord[] multipleRecords, int source) {
      this._multipleRecords = multipleRecords;
      this._rec = new ServiceRecord(source);
   }

   private static String makeString(DataBuffer db, int length) {
      String str = TLEUtilities.getStringFromBuffer(db, length);
      if (str.length() == 0) {
         throw new IllegalArgumentException();
      } else {
         return str;
      }
   }

   @Override
   public boolean processField(int type, int length, DataBuffer db) {
      switch (type) {
         case 1:
            this._rec.setName(makeString(db, length));
            return true;
         case 2:
            String str = makeString(db, length);
            DataBuffer appBuf = new DataBuffer(64, true);
            appBuf.writeByte(16);
            appBuf.writeByte(16);
            appBuf.writeCompressedInt(str.length());
            appBuf.write(str.getBytes());
            appBuf.writeByte(48);
            appBuf.writeCompressedInt(8);
            appBuf.writeInt(1026);
            appBuf.writeInt(1026);
            appBuf.writeByte(240);
            appBuf.writeByte(1);
            appBuf.writeByte(1);
            appBuf.writeByte(0);
            this._rec.setApplicationData(appBuf.toArray());
            return true;
         case 3:
            this._rec.setHomeAddress(makeString(db, length));
            return true;
         case 4:
            int netType = RadioInfo.getNetworkType();
            HostRoutingInfo hri = HRUtils.newHriByNetType(netType);
            HostRoutingTable hrtx = new HostRoutingTable();
            int addr = db.readInt();
            hri.setNpc(0);
            switch (netType) {
               case 3:
               case 4:
               case 5:
               case 7:
               default:
                  long[] longs = new long[]{IPv4UdpDAC.makeAddr(addr, 0, 0)};
                  ((IPv4UdpDAC)hri.getDac()).setAddresses(longs);
               case 2:
               case 6:
                  hrtx.addHri(hri);
                  this._rec.setAttachedHrt(hrtx);
                  return true;
            }
         case 6:
            this._rec.setUid(makeString(db, length));
            return true;
         case 8:
            this._rec.setCid(makeString(db, length));
            return true;
         case 9:
            byte[] appData = new byte[length];
            db.readFully(appData);
            this._rec.setApplicationData(appData);
            return true;
         case 10:
            this._rec.setCompressionMode(db.readInt());
            return true;
         case 11:
            this._rec.setEncryptionMode(db.readInt());
            return true;
         case 12:
            this._rec.setCARealm(makeString(db, length));
            return true;
         case 13:
            this._rec.setCAAddress(makeString(db, length));
            return true;
         case 14:
            this._rec.setCAPort(db.readShort());
            return true;
         case 15:
            db.skipBytes(length);
            return true;
         case 20:
            this._rec._cryptoKeyBuffer = db.getArray();
            this._rec._cryptoKeyStart = db.getArrayPosition();
            this._rec._cryptoKeyLength = length;
            db.skipBytes(length);
            return true;
         case 30:
            db.skipBytes(length);
            return true;
         case 40:
            int oldBufLength = db.getLength();
            db.setLength(db.getPosition() + length);
            HostRoutingTable hrt = new HostRoutingTable();
            hrt.setHris(HRUtils.parseBuffer(db));
            this._rec.setAttachedHrt(hrt);
            db.setLength(oldBufLength);
            return true;
         case 43:
            db.skipBytes(length);
            return true;
         case 50:
            this._rec.setDescription(makeString(db, length));
            return true;
         case 160:
            if (this._parentLevelRec == null) {
               this._parentLevelRec = this._rec;
            }

            Array.resize(this._multipleRecords, this._multipleRecords.length + 1);
            ServiceRecord newRecord = new ServiceRecord();
            this._parentLevelRec.copyIntoServerLevel(newRecord);
            this._rec = newRecord;
            this._multipleRecords[this._multipleRecords.length - 1] = newRecord;
            return true;
         case 161:
            this._rec.setDataSourceId(makeString(db, length));
            return true;
         case 162:
            int bbrLength = db.getLength();
            this.parseBBRRoutingInfo(db, length);
            db.setLength(bbrLength);
            return true;
         case 163:
            this._rec.setUserId(db.readInt());
            return true;
         case 165:
            this._rec.setServiceIdentifierValue(db.readInt());
            return true;
         default:
            return false;
      }
   }

   private void parseBBRRoutingInfo(DataBuffer db, int length) {
      String[] hosts = null;
      int[] ports = null;
      int length1 = length + db.getPosition();
      db.setLength(length1);
      db.readUnsignedByte();
      db.readUnsignedByte();
      int length2 = db.readCompressedInt();
      length2 += db.getPosition();
      db.setLength(length2);

      while (!db.eof()) {
         int fieldType = db.readUnsignedByte();
         int length3 = db.readCompressedInt();
         length3 += db.getPosition();
         db.setLength(length3);
         switch (fieldType) {
            case 19:
               break;
            case 20:
               int numPorts = db.readUnsignedByte();
               if (numPorts <= 0) {
                  break;
               }

               ports = new int[numPorts];

               for (int i = 0; i < numPorts; i++) {
                  ports[i] = db.readInt() & 65535;
               }
               break;
            case 21:
            default:
               int numHosts = db.readUnsignedByte();
               if (numHosts > 0) {
                  hosts = new String[numHosts];
                  StringTokenizer strtok = new StringTokenizer(new String(db.getArray(), db.getArrayPosition(), db.available()), "", false);

                  for (int i = 0; i < numHosts; i++) {
                     if (!strtok.hasMoreTokens()) {
                        hosts = null;
                        break;
                     }

                     hosts[i] = strtok.nextToken();
                  }
               }
         }

         db.setLength(length2);
         db.setPosition(length3);
      }

      db.setLength(length1);
      db.setPosition(length2);
      db.readUnsignedByte();
      if (hosts != null && ports != null && hosts.length == ports.length) {
         this._rec.setBBRHosts(hosts);
         this._rec.setBBRPorts(ports);
      }
   }

   @Override
   public void dumpField(int type, DataBuffer db) {
   }
}
