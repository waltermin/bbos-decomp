package net.rim.device.apps.api.framework.model;

import net.rim.device.api.util.LongHashtable;
import net.rim.vm.Array;

public final class ContextObject extends LongHashtable {
   private long _bitflags1;
   private long _bitflags2;
   private long _bitflags3;
   private long[] _privateFlags;
   public static final int MAX_BITFLAGS = 192;
   private static final int MAX_PRIVATE_BITFLAGS = 64;

   public ContextObject() {
      super(6);
   }

   public ContextObject(int bitflag) {
      super(6);
      this.setFlag(bitflag);
   }

   public ContextObject(int firstBitflag, int secondBitflag) {
      super(6);
      this.setFlag(firstBitflag, secondBitflag);
   }

   public ContextObject(int flag1, int flag2, int flag3) {
      super(6);
      this.setFlag(flag1, flag2, flag3);
   }

   public ContextObject(int[] bitflags) {
      super(6);
      this.setFlags(bitflags);
   }

   private ContextObject(ContextObject src) {
      super(src);
      this._bitflags1 = src._bitflags1;
      this._bitflags2 = src._bitflags2;
      this._bitflags3 = src._bitflags3;
      long[] privateFlags = src._privateFlags;
      if (privateFlags != null) {
         int length = privateFlags.length;
         this._privateFlags = new long[length];
         System.arraycopy(privateFlags, 0, this._privateFlags, 0, length);
      }
   }

   public static final ContextObject castOrCreate(Object context) {
      return !(context instanceof ContextObject) ? new ContextObject() : (ContextObject)context;
   }

   public final ContextObject clone() {
      return new ContextObject(this);
   }

   public static final ContextObject clone(Object context) {
      if (context instanceof ContextObject) {
         return new ContextObject((ContextObject)context);
      } else if (context == null) {
         return new ContextObject();
      } else {
         throw new Object();
      }
   }

   public static final ContextObject verifyNonNull(Object o) {
      if (!(o instanceof ContextObject)) {
         throw new Object();
      } else {
         return (ContextObject)o;
      }
   }

   private final void resetFlags() {
      this._bitflags1 = 0;
      this._bitflags2 = 0;
      this._bitflags3 = 0;
      this._privateFlags = null;
   }

   public final void reset() {
      this.clear();
      this.resetFlags();
   }

   public final void setFlag(int bitflag) {
      if (bitflag < 0 || bitflag >= 192) {
         throw new Object();
      }

      if (bitflag < 64) {
         this._bitflags1 |= (long)1 << bitflag;
      } else if (bitflag < 128) {
         this._bitflags2 |= (long)1 << bitflag - 64;
      } else {
         this._bitflags3 |= (long)1 << bitflag - 128;
      }
   }

   public final void setFlag(int flag1, int flag2) {
      this.setFlag(flag1);
      this.setFlag(flag2);
   }

   public final void setFlag(int flag1, int flag2, int flag3) {
      this.setFlag(flag1);
      this.setFlag(flag2);
      this.setFlag(flag3);
   }

   public static final void setFlag(Object context, int bitflag) {
      if (context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)context;
         contextObject.setFlag(bitflag);
      }
   }

   public static final void setFlag(Object context, int flag1, int flag2) {
      if (context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)context;
         contextObject.setFlag(flag1, flag2);
      }
   }

   public static final void setFlag(Object context, int flag1, int flag2, int flag3) {
      if (context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)context;
         contextObject.setFlag(flag1, flag2, flag3);
      }
   }

   public final void setFlags(int[] bitflags) {
      int count = bitflags.length;

      for (int i = 0; i < count; i++) {
         this.setFlag(bitflags[i]);
      }
   }

   public static final void setFlags(Object context, int[] bitflags) {
      if (context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)context;
         contextObject.setFlags(bitflags);
      }
   }

   public final void clearFlag(int bitflag) {
      if (bitflag < 0 || bitflag >= 192) {
         throw new Object();
      }

      if (bitflag < 64) {
         this._bitflags1 &= (long)1 << bitflag ^ -1;
      } else if (bitflag < 128) {
         this._bitflags2 &= (long)1 << bitflag - 64 ^ -1;
      } else {
         this._bitflags3 &= (long)1 << bitflag - 128 ^ -1;
      }
   }

   public static final void clearFlag(Object context, int bitflag) {
      if (context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)context;
         contextObject.clearFlag(bitflag);
      }
   }

   public final void clearFlags(int[] bitflags) {
      int count = bitflags.length;

      for (int i = 0; i < count; i++) {
         this.clearFlag(bitflags[i]);
      }
   }

   public static final void clearFlags(Object context, int[] bitflags) {
      if (context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)context;
         contextObject.clearFlags(bitflags);
      }
   }

   public final void clearFlags(int flag1, int flag2) {
      this.clearFlag(flag1);
      this.clearFlag(flag2);
   }

   public static final void clearFlags(Object context, int flag1, int flag2) {
      if (context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)context;
         contextObject.clearFlags(flag1, flag2);
      }
   }

   public final boolean getFlag(int bitflag) {
      if (bitflag < 0 || bitflag >= 192) {
         throw new Object();
      } else if (bitflag < 64) {
         return (this._bitflags1 & (long)1 << bitflag) != 0;
      } else {
         return bitflag < 128 ? (this._bitflags2 & (long)1 << bitflag - 64) != 0 : (this._bitflags3 & (long)1 << bitflag - 128) != 0;
      }
   }

   public static final boolean getFlag(Object context, int bitflag) {
      if (!(context instanceof ContextObject)) {
         return false;
      }

      ContextObject contextObject = (ContextObject)context;
      return contextObject.getFlag(bitflag);
   }

   public final boolean getFlags(int bitflag1, int bitflag2) {
      return this.getFlag(bitflag1) && this.getFlag(bitflag2);
   }

   public static final boolean getFlags(Object context, int bitflag1, int bitflag2) {
      if (!(context instanceof ContextObject)) {
         return false;
      }

      ContextObject contextObject = (ContextObject)context;
      return contextObject.getFlag(bitflag1) && contextObject.getFlag(bitflag2);
   }

   public final boolean getFlags(int bitflag1, int bitflag2, int bitflag3) {
      return this.getFlag(bitflag1) && this.getFlag(bitflag2) && this.getFlag(bitflag3);
   }

   public static final boolean getFlags(Object context, int bitflag1, int bitflag2, int bitflag3) {
      if (!(context instanceof ContextObject)) {
         return false;
      }

      ContextObject contextObject = (ContextObject)context;
      return contextObject.getFlag(bitflag1) && contextObject.getFlag(bitflag2) && contextObject.getFlag(bitflag3);
   }

   public static final Object get(Object context, long name) {
      if (!(context instanceof ContextObject)) {
         return null;
      }

      ContextObject contextObject = (ContextObject)context;
      return contextObject.get(name);
   }

   public static final Object put(Object context, long key, Object value) {
      if (!(context instanceof ContextObject)) {
         throw new Object();
      }

      ContextObject contextObject = (ContextObject)context;
      return contextObject.put(key, value);
   }

   public static final Object remove(Object context, long key) {
      if (!(context instanceof ContextObject)) {
         throw new Object();
      }

      ContextObject contextObject = (ContextObject)context;
      return contextObject.remove(key);
   }

   private final int getPrivateFlagsIndex(long privateKey, boolean create) {
      if (this._privateFlags == null) {
         if (create) {
            this._privateFlags = new long[2];
            this._privateFlags[0] = privateKey;
            return 1;
         } else {
            return -1;
         }
      } else {
         int count = this._privateFlags.length;

         for (int i = 0; i < count; i += 2) {
            if (this._privateFlags[i] == privateKey) {
               return i + 1;
            }
         }

         if (create) {
            Array.resize(this._privateFlags, count + 2);
            this._privateFlags[count] = privateKey;
            return count + 1;
         } else {
            return -1;
         }
      }
   }

   public final void setPrivateFlag(long privateKey, int flag) {
      if (flag < 0) {
         throw new Object();
      }

      if (flag >= 64) {
         privateKey += flag / 64;
         flag %= 64;
      }

      int index = this.getPrivateFlagsIndex(privateKey, true);
      this._privateFlags[index] = this._privateFlags[index] | (long)1 << flag;
   }

   public final void clearPrivateFlag(long privateKey, int flag) {
      if (flag < 0) {
         throw new Object();
      }

      if (flag >= 64) {
         privateKey += flag / 64;
         flag %= 64;
      }

      int index = this.getPrivateFlagsIndex(privateKey, false);
      if (index != -1) {
         this._privateFlags[index] = this._privateFlags[index] & ((long)1 << flag ^ -1);
      }
   }

   public final boolean getPrivateFlag(long privateKey, int flag) {
      if (flag < 0) {
         throw new Object();
      }

      if (flag >= 64) {
         privateKey += flag / 64;
         flag %= 64;
      }

      int index = this.getPrivateFlagsIndex(privateKey, false);
      return index != -1 ? (this._privateFlags[index] & (long)1 << flag) != 0 : false;
   }

   public static final void setPrivateFlag(Object context, long privateKey, int flag) {
      if (!(context instanceof ContextObject)) {
         throw new Object();
      }

      ContextObject contextObject = (ContextObject)context;
      contextObject.setPrivateFlag(privateKey, flag);
   }

   public static final void clearPrivateFlag(Object context, long privateKey, int flag) {
      if (!(context instanceof ContextObject)) {
         if (context != null) {
            throw new Object();
         }
      } else {
         ContextObject contextObject = (ContextObject)context;
         contextObject.clearPrivateFlag(privateKey, flag);
      }
   }

   public static final boolean getPrivateFlag(Object context, long privateKey, int flag) {
      if (!(context instanceof ContextObject)) {
         if (context == null) {
            return false;
         } else {
            throw new Object();
         }
      } else {
         ContextObject contextObject = (ContextObject)context;
         return contextObject.getPrivateFlag(privateKey, flag);
      }
   }

   public final void putIntegerData(int value) {
      this.put(-4054673099568009991L, new Object(value));
   }

   public final int getIntegerData(int defaultValue) {
      Integer data = (Integer)this.get(-4054673099568009991L);
      return data != null ? data : defaultValue;
   }

   public static final int getIntegerData(Object context, int defaultValue) {
      if (!(context instanceof ContextObject)) {
         return defaultValue;
      }

      ContextObject contextObject = (ContextObject)context;
      return contextObject.getIntegerData(defaultValue);
   }
}
