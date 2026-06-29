package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardEFListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.system.NetworkInfo;
import net.rim.vm.Array;

public final class SimCardEfHandler implements SIMCardEFListener {
   private int _efId;
   private SimCardEfHandlerCallback _cb;
   private Application _app;
   private int _state;
   private int _failureCode;
   private int _structure;
   private int _fileSize;
   private int _numRecords;
   private byte[] _data;
   private int _actualSize;
   private NetworkInfo[] _infos;
   private static final int STATE_WAIT_FLAG = Integer.MIN_VALUE;
   private static final int STATE_DUMMY = -1;
   private static final int STATE_IDLE = 0;
   private static final int STATE_REQ_INFO_PLMNW_ACT = 1;
   private static final int STATE_REQ_INFO_PLMNW_ACT_WAIT = -2147483647;
   private static final int STATE_REQ_INFO_PLMN_SEL = 2;
   private static final int STATE_REQ_INFO_PLMN_SEL_WAIT = -2147483646;
   private static final int STATE_REQ_DATA = 3;
   private static final int STATE_REQ_DATA_WAIT = -2147483645;
   private static final int STATE_WRITE_DATA = 100;
   private static final int STATE_WRITE_DATA_WAIT = -2147483548;
   private static final int STATE_GEN_FAILURE = 1001;
   private static final int STATE_PARSE_DATA = 5;
   private static final int STATE_FAILURE = 1002;
   private static final int STATE_SUCCESS = 1000;
   private static final int REC_SIZE_EF_PLMN_SEL = 3;
   private static int _rec_size_ef_plmnw_act = 5;
   private static boolean _rec_size_ef_plmnw_act_initialized;
   private static boolean _usimCardPresent;
   private static final int ACCESS_TECH_GSM = 1;
   private static final int ACCESS_TECH_GSM_COMPACT = 2;
   private static final int ACCESS_TECH2_GSM = 128;
   private static final int ACCESS_TECH2_GSM_COMPACT = 64;
   private static final int ACCESS_TECH2_UTRAN = 32768;
   private static final int ACCESS_TECH2_UNSELECTED = 0;

   public SimCardEfHandler(SimCardEfHandlerCallback cb) {
      this._cb = cb;
      this._app = Application.getApplication();
   }

   public final NetworkInfo[] getNetworkInfos() {
      return this._infos;
   }

   public final void read() {
      if (this._state == 0) {
         SIMCard.addListener(this._app, this);
         this.doNextState(1);
      }
   }

   public final void write(NetworkInfo[] infos) {
      if (this._state == 0 && (this._efId == 68 || this._efId == 4)) {
         this._infos = infos;
         SIMCard.addListener(this._app, this);
         this.doNextState(100);
      }
   }

   public final int getNumRecords() {
      return this._numRecords;
   }

   private final synchronized int requestInfo(int state, int efId) {
      this._efId = efId;
      this._state = state;

      try {
         SIMCard.requestEFInfo(efId);
         this._state = state | -2147483648;
         return this._state;
      } finally {
         ;
      }
   }

   private final synchronized int requestData() {
      try {
         this._state = 3;
         int numBytes = SIMCard.requestEFRead(this._efId, this._structure, 0, this._data);
         this._state |= Integer.MIN_VALUE;
         this._actualSize = numBytes;
         return numBytes < 0 ? this._state : 5;
      } finally {
         ;
      }
   }

   private final synchronized int writeData() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: bipush 100
      // 03: putfield net/rim/device/apps/internal/options/items/network/SimCardEfHandler._state I
      // 06: aload 0
      // 07: getfield net/rim/device/apps/internal/options/items/network/SimCardEfHandler._efId I
      // 0a: bipush 68
      // 0c: if_icmpne 17
      // 0f: aload 0
      // 10: invokespecial net/rim/device/apps/internal/options/items/network/SimCardEfHandler.preparePlmnwActData ()[B
      // 13: astore 3
      // 14: goto 2c
      // 17: aload 0
      // 18: getfield net/rim/device/apps/internal/options/items/network/SimCardEfHandler._efId I
      // 1b: bipush 4
      // 1d: if_icmpne 28
      // 20: aload 0
      // 21: invokespecial net/rim/device/apps/internal/options/items/network/SimCardEfHandler.preparePlmnSelData ()[B
      // 24: astore 3
      // 25: goto 2c
      // 28: sipush 1001
      // 2b: ireturn
      // 2c: aload 0
      // 2d: getfield net/rim/device/apps/internal/options/items/network/SimCardEfHandler._efId I
      // 30: aload 0
      // 31: getfield net/rim/device/apps/internal/options/items/network/SimCardEfHandler._structure I
      // 34: bipush 0
      // 35: aload 3
      // 36: invokestatic net/rim/device/api/system/SIMCard.requestEFWrite (III[B)V
      // 39: aload 0
      // 3a: aload 0
      // 3b: getfield net/rim/device/apps/internal/options/items/network/SimCardEfHandler._state I
      // 3e: ldc_w -2147483648
      // 41: ior
      // 42: putfield net/rim/device/apps/internal/options/items/network/SimCardEfHandler._state I
      // 45: aload 0
      // 46: getfield net/rim/device/apps/internal/options/items/network/SimCardEfHandler._state I
      // 49: ireturn
      // 4a: astore 4
      // 4c: sipush 1001
      // 4f: ireturn
      // 50: astore 4
      // 52: sipush 1001
      // 55: ireturn
      // try (0 -> 20): 37 null
      // try (21 -> 36): 37 null
      // try (0 -> 20): 40 null
      // try (21 -> 36): 40 null
   }

   private final int parsePlmnwActData(DataBuffer db) {
      if (!_rec_size_ef_plmnw_act_initialized) {
         _rec_size_ef_plmnw_act = this.determinePLMNwActRecordLength(db);
         if (_rec_size_ef_plmnw_act == 4) {
            this._numRecords = db.available() / _rec_size_ef_plmnw_act;
         }
      }

      try {
         this._infos = new Object[this._numRecords];

         int i;
         for (i = 0; i < this._infos.length; i++) {
            int netId = this.parseNetworkId(db);
            if (_rec_size_ef_plmnw_act != 5) {
               int category = db.readUnsignedByte();
               if ((netId == 268374015 || netId == 16715775) && category == 255) {
                  break;
               }

               this._infos[i] = (NetworkInfo)(new Object(netId, category));
            } else {
               int accessTech = db.readUnsignedShort();
               if (netId == 268374015 || netId == 16715775) {
                  break;
               }

               int category = 0;
               if ((accessTech & 32768) != 0) {
                  category |= 64;
               }

               switch (accessTech & 192) {
                  case 64:
                     category |= 2;
                     break;
                  case 128:
                     category |= 17;
                     break;
                  case 192:
                     category |= 3;
               }

               this._infos[i] = (NetworkInfo)(new Object(netId, category, accessTech));
            }
         }

         if (i != this._infos.length) {
            Array.resize(this._infos, i);
         }

         return i == 0 && !_usimCardPresent ? 2 : 1000;
      } finally {
         ;
      }
   }

   private final byte[] preparePlmnwActData() {
      DataBuffer db = (DataBuffer)(new Object(this._actualSize, true));

      for (int i = 0; i < this._infos.length; i++) {
         this.writeNetworkId(db, this._infos[i].getNetworkId());
         int category = this._infos[i].getCategory();
         if (_rec_size_ef_plmnw_act != 5) {
            db.writeByte(category);
         } else {
            int accessTech = this._infos[i].getAccessTechnology();
            if ((category & 64) != 0) {
               accessTech &= -193;
               accessTech |= 32768;
            } else if ((category & 16) != 0) {
               accessTech &= -32833;
               accessTech |= 128;
            }

            db.writeShort(accessTech);
         }
      }

      int i = this._infos.length * _rec_size_ef_plmnw_act;
      if (_rec_size_ef_plmnw_act == 5) {
         while (this._actualSize - i >= _rec_size_ef_plmnw_act) {
            this.writeNetworkId(db, 268374015);
            db.writeShort(0);
            i += _rec_size_ef_plmnw_act;
         }
      }

      while (i < this._actualSize) {
         db.writeByte(255);
         i++;
      }

      db.trim();
      return db.getArray();
   }

   private final int parsePlmnSelData(DataBuffer db) {
      try {
         this._infos = new Object[db.available() / 3];

         int i;
         for (i = 0; i < this._infos.length; i++) {
            int netId = this.parseNetworkId(db);
            if (netId == 268374015 || netId == 16715775) {
               break;
            }

            this._infos[i] = (NetworkInfo)(new Object());
            this._infos[i].setNetworkId(netId);
         }

         if (i != this._infos.length) {
            Array.resize(this._infos, i);
         }

         return 1000;
      } finally {
         ;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int determinePLMNwActRecordLength(DataBuffer db) {
      if (db.available() < 4) {
         return 5;
      }

      int byte4 = 0;
      boolean var7 = false /* VF: Semaphore variable */;
      boolean var10 = false /* VF: Semaphore variable */;

      label102: {
         try {
            label87:
            try {
               var10 = true;
               var7 = true;
               int e = 4;

               while (--e >= 0) {
                  byte4 = db.readUnsignedByte();
               }

               var7 = false;
               var10 = false;
               break label102;
            } finally {
               if (var10) {
                  byte4 = 0;
                  var7 = false;
                  break label87;
               }
            }
         } finally {
            if (var7) {
               db.rewind();
            }
         }

         db.rewind();
         return (byte4 & 0xFF) != 255 && byte4 != 1 && byte4 != 2 ? 5 : 4;
      }

      db.rewind();
      return (byte4 & 0xFF) != 255 && byte4 != 1 && byte4 != 2 ? 5 : 4;
   }

   private final byte[] preparePlmnSelData() {
      DataBuffer db = (DataBuffer)(new Object(this._actualSize, true));

      for (int i = 0; i < this._infos.length; i++) {
         this.writeNetworkId(db, this._infos[i].getNetworkId());
      }

      for (int i = this._infos.length * 3; i < this._actualSize; i++) {
         db.writeByte(255);
      }

      db.trim();
      return db.getArray();
   }

   private final int parseNetworkId(DataBuffer db) {
      int byte1 = db.readUnsignedByte();
      int byte2 = db.readUnsignedByte();
      int byte3 = db.readUnsignedByte();
      int mcc = (byte1 & 15) << 8;
      mcc |= byte1 & 240;
      mcc |= byte2 & 15;
      int mnc;
      if ((byte2 & 240) == 240) {
         mnc = (byte3 & 15) << 4;
         mnc |= (byte3 & 240) >> 4;
         if (SIMCard.is3DigitMNC(mcc, mnc)) {
            mnc <<= 4;
         }
      } else {
         mnc = (byte3 & 15) << 8;
         mnc |= byte3 & 240;
         mnc |= (byte2 & 240) >> 4;
      }

      return mnc << 16 | mcc;
   }

   private final void writeNetworkId(DataBuffer db, int netId) {
      int mcc = netId & 4095;
      int mnc = netId >> 16 & 4095;
      int byte1 = mcc >> 8 & 15;
      byte1 |= mcc & 240;
      int byte2 = mcc & 15;
      int byte3;
      if (SIMCard.is3DigitMNC(mcc, mnc)) {
         byte3 = mnc >> 8 & 15;
         byte3 |= mnc & 240;
         byte2 |= (mnc & 15) << 4;
      } else {
         byte3 = (mnc & 240) >> 4;
         byte3 |= (mnc & 15) << 4;
         byte2 |= 240;
      }

      db.writeByte(byte1);
      db.writeByte(byte2);
      db.writeByte(byte3);
   }

   private final void doNextState(int nextState) {
      while (true) {
         switch (nextState) {
            case -2147483647:
            case -2147483646:
            case -2147483645:
               return;
            case 1:
               nextState = this.requestInfo(nextState, 68);
               if ((this._state & -2147483648) != 0) {
                  return;
               }

               if (nextState == 1001) {
                  nextState = 2;
               }

               this._cb.efHandlerActionStarted();
               break;
            case 2:
               nextState = this.requestInfo(nextState, 4);
               break;
            case 3:
               if (this._data == null || this._data.length != this._fileSize) {
                  this._data = new byte[this._fileSize];
               }

               nextState = this.requestData();
               break;
            case 5:
               this._state = 5;
               DataBuffer db = (DataBuffer)(new Object(this._data, 0, this._actualSize, true));
               if (this._efId == 68) {
                  nextState = this.parsePlmnwActData(db);
               } else {
                  if (this._efId == 4) {
                     nextState = this.parsePlmnSelData(db);
                     continue;
                  }

                  nextState = 1001;
               }
               break;
            case 100:
               nextState = this.writeData();
               if ((this._state & -2147483648) != 0) {
                  return;
               }

               this._cb.efHandlerActionStarted();
               break;
            case 1000:
               SIMCard.removeListener(this._app, this);
               this._state = 0;
               this._cb.efHandlerActionSuccess();
               return;
            case 1001:
               this._failureCode = 5;
               nextState = 1002;
               break;
            case 1002:
               SIMCard.removeListener(this._app, this);
               this._state = 0;
               this._cb.efHandlerActionFailed(this._failureCode);
               return;
         }
      }
   }

   @Override
   public final synchronized void responseEFInfo(int code, int id, int fileStatus, int structure, int fileSize, int recordLength, int numRecords) {
      int nextState = -1;
      if (id == this._efId) {
         if (this._state == -2147483647) {
            if (code == 0 && fileSize > 0) {
               nextState = 3;
            } else if ((code == 3 || fileSize == 0) && !_usimCardPresent) {
               nextState = 2;
            } else {
               this._failureCode = code;
               nextState = 1002;
            }
         } else if (this._state == -2147483646) {
            if (code == 0) {
               nextState = 3;
            } else {
               this._failureCode = code;
               nextState = 1002;
            }
         }

         if (nextState != -1) {
            if (nextState == 3) {
               this._structure = structure;
               this._fileSize = fileSize;
               if (this._structure == 1 || this._structure == 2) {
                  this._numRecords = numRecords;
               } else if (id == 68) {
                  this._numRecords = numRecords / _rec_size_ef_plmnw_act;
               } else if (id == 4) {
                  this._numRecords = numRecords / 3;
               }
            }

            this.doNextState(nextState);
         }
      }
   }

   @Override
   public final synchronized void responseEFRead(int code, int id, int structure, int length, int recordNumber) {
      if (id == this._efId && this._state == -2147483645) {
         if (code == 0) {
            this._actualSize = length;
            this.doNextState(3);
            return;
         }

         this._failureCode = code;
         this.doNextState(1002);
      }
   }

   @Override
   public final synchronized void responseEFWrite(int code, int id, int structure, int recordNumber) {
      if (id == this._efId && this._state == -2147483548) {
         if (code == 0) {
            SIMCard.fileUpdated(id);
            this.doNextState(1000);
            return;
         }

         this._failureCode = code;
         this.doNextState(1002);
      }
   }

   static {
      label29:
      try {
         _usimCardPresent = SIMCard.isUSIMPresent();
      } finally {
         break label29;
      }

      if (RadioInfo.getNetworkType() == 7 && _usimCardPresent) {
         _rec_size_ef_plmnw_act_initialized = true;
      }
   }
}
