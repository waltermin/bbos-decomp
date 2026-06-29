package net.rim.device.api.servicebook;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.TLEFieldController;

final class ServiceBook$SBRestriction implements Persistable, TLEFieldController {
   private int _action;
   private String _uid;
   private String _cid;
   public static final int DONT_CARE = 0;
   public static final int ACCEPT = 1;
   public static final int REJECT = 2;
   public static final int IMPLICIT = Integer.MIN_VALUE;
   public static final int IMPLICIT_ACCEPT = -2147483647;
   public static final int IMPLICIT_REJECT = -2147483646;

   public ServiceBook$SBRestriction(int a) {
      this._action = a;
      this._uid = null;
      this._cid = null;
   }

   public final int isAllowed(ServiceRecord sr, int newType, String newName, String newUid, String newCid) {
      String uid = newUid != null ? newUid : sr.getUid();
      String cid = newCid != null ? newCid : sr.getCid();
      int type = newType != -1 ? newType : sr.getType();
      if (type != 0) {
         return 0;
      }

      int flag = 0;
      if (this._cid != null) {
         if (!StringUtilities.strEqualIgnoreCase(this._cid, cid, 1701707776)) {
            return 0;
         }
      } else {
         flag |= Integer.MIN_VALUE;
      }

      if (this._uid != null) {
         if (!StringUtilities.strEqualIgnoreCase(this._uid, uid, 1701707776)) {
            return 0;
         }
      } else {
         flag |= Integer.MIN_VALUE;
      }

      return this._action | flag;
   }

   @Override
   public final boolean processField(int type, int length, DataBuffer db) {
      switch (type) {
         case 6:
         case 8:
            String str = StringUtilities.cStr2String(db.getArray(), db.getArrayPosition(), length);
            db.skipBytes(length);
            if (str.length() == 1 && str.charAt(0) == '*') {
               str = null;
            }

            if (type == 6) {
               this._uid = str;
               return true;
            }

            this._cid = str;
            return true;
         default:
            return false;
      }
   }

   @Override
   public final void dumpField(int type, DataBuffer db) {
   }
}
