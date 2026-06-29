package net.rim.device.apps.internal.docview.core;

import net.rim.device.api.util.IntHashtable;

final class ArznClassFactory {
   private IntHashtable _objectMap = (IntHashtable)(new Object(16));
   private ArznVectorPoolManager _nullPool;

   ArznClassFactory() {
      this.initializePool(0, 1);
      this.initializePool(24, 1);
      this.initializePool(28, 1);
      this.initializePool(26, 1);
      this.initializePool(65534, 1);
      this.initializePool(54, 0);
      this.initializePool(71, 0);
      this.initializePool(16, 0);
      this.initializePool(39, 0);
      this.initializePool(21, 0);
      this.initializePool(80, 0);
      this.initializePool(84, 0);
      this._nullPool = new ArznVectorPoolManager(1, 65535);
   }

   private final void initializePool(int iObjectType, int iCapacity) {
      this._objectMap.put(iObjectType, new ArznVectorPoolManager(iCapacity, iObjectType));
   }

   private final ArznVectorPoolManager getPool(int iObjectType) {
      int poolID = iObjectType;
      switch (poolID) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 23:
         case 51:
         case 53:
         case 56:
         case 57:
         case 64:
         case 65:
         case 72:
         case 74:
         case 75:
         case 86:
            poolID = 65534;
            break;
         case 31:
         case 38:
         case 52:
         case 69:
         case 70:
            poolID = 71;
            break;
         case 55:
            poolID = 0;
      }

      ArznVectorPoolManager pool = (ArznVectorPoolManager)this._objectMap.get(poolID);
      if (pool == null) {
         pool = this._nullPool;
      }

      return pool;
   }

   final ArznObject getObject(UCSParser ucsParser, ArznObject parent) {
      int commandCode = ucsParser.getCurrentCommandCode();
      int iObjectCode = ucsParser.readRawCommandCode(commandCode);
      ArznVectorPoolManager pool = this.getPool(iObjectCode);
      if (pool == this._nullPool && !ucsParser.isContainer(commandCode)) {
         return null;
      }

      ArznObject obj = pool.getObject();
      if (obj != null) {
         switch (iObjectCode) {
            case 31:
            case 38:
            case 52:
            case 69:
            case 70:
            case 71:
               ((ArznRefContainer)obj).initialize(ucsParser, this, parent, iObjectCode);
               break;
            default:
               obj.initialize(ucsParser, this, parent);
         }
      }

      return obj;
   }

   final void putObject(Object obj, int iObjectType) {
      this.getPool(iObjectType).putObject(obj);
   }
}
