package net.rim.ecmascript.runtime;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

public class ESObject {
   boolean _isRedirected;
   int _functionType;
   private String[] _propertyNames;
   private long[] _propertyValues;
   private byte[] _propertyAttributes;
   private long[] _arrayElementValues;
   private long[] _arrayIndexes;
   private int _numFields;
   private int _numArrayElements;
   private int _growthIncrement;
   private ESObject _prototype;
   private String _class;
   private long _arrayLength;
   private int _arrayLengthIndex;
   private String _cachedName;
   private int _cachedIndex;
   static final int Function_NotFunction = 0;
   static final int Function_Unknown = 1;
   static final int Function_HostFunction = 2;
   static final int Function_String_indexOf = 3;
   static final int Function_String_substring = 4;
   static final int Function_String_substr = 5;
   static final int Function_String_lastIndexOf = 6;
   static final int Function_String_charAt = 7;
   static final int Function_String_charCodeAt = 8;
   static final int Function_String_slice = 9;
   public static final int ReadOnly = 1;
   public static final int DontEnum = 2;
   public static final int DontDelete = 4;
   public static final int DontRedirectRead = 8;
   public static final int DontRedirectWrite = 16;
   public static final int DontRedirect = 24;
   static final int MaxArrayElementIndex = 65535;
   static final int GrowthIncrement = 10;
   static final int NotAnArray = -1;

   public ESObject() {
      this("Object", GlobalObject.getInstance().objectPrototype);
   }

   private void initFields() {
      this.setFunctionType(0);
      this._growthIncrement = 10;
      this._arrayLengthIndex = -1;
   }

   private void initArrayFields(long arrayLength) {
      this._arrayLength = arrayLength;
      this._arrayLengthIndex = 0;
      this.addField("length", 6, Value.makeLongValue(arrayLength));
      this._arrayLengthIndex = this.getIndexOfField("length");
   }

   private void clearFields() {
      this._propertyNames = null;
      this._propertyValues = null;
      this._arrayElementValues = null;
      this._propertyAttributes = null;
      this._arrayIndexes = null;
      this._numFields = 0;
      this._cachedIndex = 0;
      this._numArrayElements = 0;
      this._growthIncrement = 10;
      this._arrayLengthIndex = -1;
      if (this._cachedName != null) {
         this._cachedName = null;
      }
   }

   void setIsRedirected() {
      this._isRedirected = true;
   }

   void setFunctionType(int type) {
      this._functionType = type;
   }

   private long parseIndex(long element) {
      if ((element & -4294967296L) == 0) {
         return (int)element;
      }

      String name = Convert.toString(element);

      try {
         long index = Long.parseLong(name);
         if (name.charAt(0) == '0' && name.length() != 1) {
            return -1;
         } else if (index < 0) {
            return -1;
         } else {
            return index > 65535 ? -1 : index;
         }
      } finally {
         return -1;
      }
   }

   public void setGrowthIncrement(int increment) {
      this._growthIncrement = increment;
   }

   public long getArrayLength() {
      return this._arrayLength;
   }

   void enumerate(Vector v) {
      this.enumerate(v, true);
   }

   public void enumerateAll(Vector v) {
      this.enumerate(v, false, false);
   }

   public void enumerate(Vector v, boolean includePrototype) {
      this.enumerate(v, includePrototype, true);
   }

   private void enumerate(Vector v, boolean includePrototype, boolean respectDontEnum) {
      Hashtable shadowed = (Hashtable)(new Object());

      for (ESObject obj = this; obj != null; obj = obj._prototype) {
         for (int i = 0; i < obj._numFields; i++) {
            String name = obj._propertyNames[i];
            if (name != null && shadowed.get(name) == null) {
               shadowed.put(name, name);
               if ((!respectDontEnum || (obj._propertyAttributes[i] & 2) == 0) && obj._propertyValues[i] != Value.PLACEHOLDER_GLOBAL) {
                  v.addElement(name);
               }
            }
         }

         for (int i = 0; i < obj._numArrayElements; i++) {
            if (obj._arrayElementValues[i] != Value.PLACEHOLDER_ARRAY) {
               v.addElement(Misc.stringIntern(Integer.toString(i)));
            }
         }

         if (!includePrototype) {
            return;
         }
      }
   }

   public ESObject(String clazz, ESObject prototype) {
      this(clazz, prototype, false);
   }

   public ESObject(String clazz, ESObject prototype, boolean nullPrototypeOK) {
      this._class = clazz;
      this._prototype = prototype;
      this.initFields();
   }

   public String getObjectClass() {
      return this._class;
   }

   void setObjectClass(String clazz) {
      this._class = clazz;
   }

   ESObject(String clazz, ESObject prototype, long arrayLength) {
      this._class = clazz;
      this._prototype = prototype;
      this.initFields();
      this.initArrayFields(arrayLength);
   }

   public void clear() {
      this.clearFields();
   }

   protected void clearArray(long arrayLength) {
      this.clearFields();
      this.initArrayFields(arrayLength);
   }

   native int getIndexOfField(String var1);

   public int getFieldIndex(String name) {
      return this.getIndexOfField(name);
   }

   public String getFieldName(int index) {
      return this._propertyNames[index];
   }

   long getOwnField(String name) {
      return this.getOwnFieldNoAssert(name);
   }

   private long getOwnFieldNoAssert(String name) {
      int i = this.getIndexOfField(name);
      return i == -1 ? Value.UNDEFINED : this._propertyValues[i];
   }

   public boolean hasOwnField(String name) {
      return this.hasOwnFieldNoAssert(name);
   }

   private boolean hasOwnFieldNoAssert(String name) {
      return this.getIndexOfField(name) != -1;
   }

   public boolean hasOwnElement(long element) {
      long index = this.parseIndex(element);
      return index >= 0 ? this.hasOwnIndex(index) : this.getIndexOfField(Convert.toInternString(element)) != -1;
   }

   public boolean hasOwnIndex(long index) {
      return index < this._numArrayElements
         ? this._arrayElementValues[(int)index] != Value.PLACEHOLDER_ARRAY
         : this.getIndexOfField(Misc.stringIntern(Long.toString(index))) != -1;
   }

   void replaceField(String name, int attrib, long value) {
      this.replaceFieldNoAssert(name, attrib, value);
   }

   private void replaceFieldNoAssert(String name, int attrib, long value) {
      int i = this.getIndexOfField(name);
      if (i == -1) {
         this.addField(name, attrib, value);
      } else {
         this._propertyValues[i] = value;
         this._propertyAttributes[i] = (byte)attrib;
      }
   }

   int createPlaceHolder(String name) {
      int i = this.getIndexOfField(name);
      if (i == -1) {
         this.addField(name, 0, Value.PLACEHOLDER_GLOBAL);
         i = this.getIndexOfField(name);
      }

      return i;
   }

   public void addField(String name, int attrib, long value) {
      this.addFieldNoAssert(name, attrib, value);
   }

   private void addFieldNoAssert(String name, int attrib, long value) {
      int i = this._numFields++;
      int newLength = this._numFields + this._growthIncrement;
      if (this._propertyNames == null) {
         this._propertyNames = new Object[newLength];
         this._propertyValues = Misc.newMixedArray(newLength);
         this._propertyAttributes = new byte[newLength];
         if (this._arrayLengthIndex != -1) {
            this._arrayIndexes = new long[newLength];
         }
      } else if (this._numFields > this._propertyNames.length) {
         this._propertyNames = Misc.stringArrayResize(this._propertyNames, newLength);
         this._propertyValues = Misc.longArrayResize(this._propertyValues, newLength);
         this._propertyAttributes = Misc.byteArrayResize(this._propertyAttributes, newLength);
         if (this._arrayLengthIndex != -1) {
            this._arrayIndexes = Misc.longArrayResize(this._arrayIndexes, newLength);
         }
      }

      this._propertyNames[i] = name;
      this._propertyValues[i] = value;
      this._propertyAttributes[i] = (byte)attrib;
   }

   static long toArrayIndex(long value) {
      switch (Value.getType(value)) {
         case 0:
            long index = Value.getIntegerValue(value);
            if (index < 0) {
               return -1;
            }

            return index;
         case 7:
            double d = Value.getDoubleValue(value);
            if (d < 0L) {
               return -1;
            } else if (d > 4751297606873776128L) {
               return -1;
            } else {
               long index = (long)d;
               if (index != d) {
                  return -1;
               }

               return index;
            }
         default:
            return -1;
      }
   }

   static long toArrayIndex(String name) {
      try {
         long value = Long.parseLong(name);
         if (value > 4294967295L) {
            return -1;
         } else if (value < 0) {
            return -1;
         } else {
            return !name.equals(Long.toString(value)) ? -1 : value;
         }
      } finally {
         return -1;
      }
   }

   public void addHostFunction(HostFunction func) {
      this.addField(func.getName(), 2, Value.makeObjectValue(func));
   }

   long[] getValueArray() {
      return this._propertyValues;
   }

   long[] noRedirectGetValueArray() {
      return this._propertyValues;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   long[] getArray() {
      long length;
      if (this._arrayLengthIndex != -1) {
         length = this._arrayLength;
      } else {
         length = this.getField("length");
      }

      if (length <= 0) {
         return new long[0];
      }

      if (length > Integer.MAX_VALUE) {
         throw ThrownValue.internalError(Resources.getString(31));
      }

      boolean var6 = false /* VF: Semaphore variable */;

      long[] array;
      try {
         var6 = true;
         array = new long[(int)length];
         var6 = false;
      } finally {
         if (var6) {
            throw ThrownValue.internalError(Resources.getString(31));
         }
      }

      for (int i = (int)(length - 1); i >= 0; i--) {
         if (!this.hasIndex(i)) {
            array[i] = Value.DEFAULT;
         } else {
            array[i] = this.getIndex(i);
         }
      }

      return array;
   }

   void putArray(long[] array) {
      for (int i = 0; i < array.length; i++) {
         if (array[i] == Value.DEFAULT) {
            this.deleteIndex(i);
         } else {
            this.putIndex(i, array[i]);
         }
      }
   }

   public long noRedirectGetField(String name) {
      return this.getFieldNoAssert(name);
   }

   public long getField(String name) {
      return this.noRedirectGetField(name);
   }

   public long getFieldAllowExceptions(String name) {
      return this.getField(name);
   }

   private long getFieldNoAssert(String name) {
      if (name == "__proto__") {
         return this._prototype == null ? Value.NULL : Value.makeObjectValue(this._prototype);
      } else {
         int i = this.getIndexOfField(name);
         if (i != -1) {
            return this._propertyValues[i];
         } else {
            return this._prototype == null ? Value.UNDEFINED : this._prototype.getField(name);
         }
      }
   }

   long noRedirectGetElement(long element) {
      long index = this.parseIndex(element);
      return index >= 0 ? this.noRedirectGetIndex(index) : this.getFieldNoAssert(Convert.toInternString(element));
   }

   public long getElement(long element) {
      return this.noRedirectGetElement(element);
   }

   public long getElementAllowExceptions(long element) {
      return this.getElement(element);
   }

   long noRedirectGetIndex(long param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/ecmascript/runtime/ESObject._arrayElementValues [J
      // 04: lload 1
      // 05: l2i
      // 06: laload
      // 07: lreturn
      // 08: astore 3
      // 09: goto 0d
      // 0c: astore 3
      // 0d: aload 0
      // 0e: lload 1
      // 0f: invokestatic java/lang/Long.toString (J)Ljava/lang/String;
      // 12: invokestatic net/rim/ecmascript/util/Misc.stringIntern (Ljava/lang/String;)Ljava/lang/String;
      // 15: invokespecial net/rim/ecmascript/runtime/ESObject.getFieldNoAssert (Ljava/lang/String;)J
      // 18: lreturn
      // try (0 -> 5): 6 null
      // try (0 -> 5): 8 null
   }

   public long getIndex(long index) {
      return this.noRedirectGetIndex(index);
   }

   public ESObject getPrototype() {
      return this._prototype;
   }

   protected boolean putArrayField(String name, long value) {
      return this.putArrayFieldNoAssert(name, value);
   }

   private boolean putArrayFieldNoAssert(String name, long value) {
      if (name == "__proto__") {
         return this.putPrototype(value);
      }

      if (!this.canPutField(name, value)) {
         return false;
      }

      int propertyIndex = this.getIndexOfField(name);
      if (propertyIndex == -1) {
         this.addFieldNoAssert(name, 0, value);
         propertyIndex = this.getIndexOfField(name);
         this._arrayIndexes[propertyIndex] = toArrayIndex(name);
      } else {
         if (propertyIndex == this._arrayLengthIndex) {
            long newNumArrayElements = Convert.toUint32(value);
            if (Value.getType(value) == 0) {
               if (newNumArrayElements > Integer.MAX_VALUE) {
                  throw ThrownValue.badArrayLength(Convert.toString(value));
               }
            } else if (newNumArrayElements != Convert.toDouble(value)) {
               throw ThrownValue.badArrayLength(Convert.toString(value));
            }

            if (newNumArrayElements > this._numArrayElements) {
               for (int i = this._arrayIndexes.length - 1; i >= 0; i--) {
                  long index = this._arrayIndexes[i];
                  if (index >= newNumArrayElements) {
                     this.deletePropertyByIndex(i);
                     this._arrayIndexes[i] = -1;
                  }
               }
            }

            if (this._numArrayElements > newNumArrayElements) {
               this._arrayElementValues = Misc.longArrayResize(this._arrayElementValues, (int)newNumArrayElements);
               this._numArrayElements = (int)newNumArrayElements;
            }

            this._arrayLength = newNumArrayElements;
            this._propertyValues[propertyIndex] = Value.makeLongValue(newNumArrayElements);
            return true;
         }

         this._propertyValues[propertyIndex] = value;
      }

      long aIndex = this._arrayIndexes[propertyIndex];
      if (aIndex >= this._arrayLength) {
         aIndex += 1;
         this._arrayLength = aIndex;
         this._propertyValues[this._arrayLengthIndex] = Value.makeLongValue(aIndex);
      }

      return true;
   }

   protected boolean putArrayElement(long element, long value) {
      long index = this.parseIndex(element);
      return index >= 0 ? this.putArrayIndex(index, value) : this.putArrayFieldNoAssert(Convert.toInternString(element), value);
   }

   private boolean ensureCapacity(long index) {
      if (index < this._numArrayElements) {
         return true;
      }

      if (index > 65535) {
         return false;
      }

      int newNumArrayElements = (int)index + this._growthIncrement;

      try {
         if (this._arrayElementValues == null) {
            this._arrayElementValues = Misc.newMixedArray(newNumArrayElements);
         } else {
            this._arrayElementValues = Misc.longArrayResize(this._arrayElementValues, newNumArrayElements);
         }
      } finally {
         ;
      }

      for (int i = this._numArrayElements; i < newNumArrayElements; i++) {
         this._arrayElementValues[i] = Value.PLACEHOLDER_ARRAY;
      }

      this._numArrayElements = newNumArrayElements;
      return true;
   }

   protected boolean putArrayIndex(long param1, long param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: lload 1
      // 01: l2i
      // 02: istore 5
      // 04: iload 5
      // 06: i2l
      // 07: aload 0
      // 08: getfield net/rim/ecmascript/runtime/ESObject._arrayLength J
      // 0b: lcmp
      // 0c: ifge 20
      // 0f: aload 0
      // 10: getfield net/rim/ecmascript/runtime/ESObject._arrayElementValues [J
      // 13: iload 5
      // 15: lload 3
      // 16: lastore
      // 17: bipush 1
      // 18: ireturn
      // 19: astore 5
      // 1b: goto 20
      // 1e: astore 5
      // 20: aload 0
      // 21: lload 1
      // 22: invokespecial net/rim/ecmascript/runtime/ESObject.ensureCapacity (J)Z
      // 25: ifeq 52
      // 28: aload 0
      // 29: getfield net/rim/ecmascript/runtime/ESObject._arrayElementValues [J
      // 2c: lload 1
      // 2d: l2i
      // 2e: lload 3
      // 2f: lastore
      // 30: lload 1
      // 31: aload 0
      // 32: getfield net/rim/ecmascript/runtime/ESObject._arrayLength J
      // 35: lcmp
      // 36: iflt 50
      // 39: lload 1
      // 3a: bipush 1
      // 3b: i2l
      // 3c: ladd
      // 3d: lstore 1
      // 3e: aload 0
      // 3f: lload 1
      // 40: putfield net/rim/ecmascript/runtime/ESObject._arrayLength J
      // 43: aload 0
      // 44: getfield net/rim/ecmascript/runtime/ESObject._propertyValues [J
      // 47: aload 0
      // 48: getfield net/rim/ecmascript/runtime/ESObject._arrayLengthIndex I
      // 4b: lload 1
      // 4c: invokestatic net/rim/ecmascript/runtime/Value.makeLongValue (J)J
      // 4f: lastore
      // 50: bipush 1
      // 51: ireturn
      // 52: aload 0
      // 53: lload 1
      // 54: invokestatic java/lang/Long.toString (J)Ljava/lang/String;
      // 57: invokestatic net/rim/ecmascript/util/Misc.stringIntern (Ljava/lang/String;)Ljava/lang/String;
      // 5a: lload 3
      // 5b: invokespecial net/rim/ecmascript/runtime/ESObject.putArrayFieldNoAssert (Ljava/lang/String;J)Z
      // 5e: ireturn
      // try (0 -> 15): 16 null
      // try (0 -> 15): 18 null
   }

   boolean putPrototype(long value) {
      try {
         if (value != Value.NULL) {
            this._prototype = Convert.toObject(value);
            return true;
         }

         this._prototype = null;
      } catch (ThrownValue tv) {
         this._prototype = null;
      }

      return true;
   }

   boolean noRedirectPutField(String name, long value) {
      return this.putFieldNoAssert(name, value);
   }

   public boolean putField(String name, long value) {
      return this.noRedirectPutField(name, value);
   }

   private boolean putFieldNoAssert(String name, long value) {
      if (name == "__proto__") {
         return this.putPrototype(value);
      } else if (!this.canPutField(name, value)) {
         return false;
      } else {
         int i = this.getIndexOfField(name);
         if (i != -1) {
            this._propertyValues[i] = value;
            return true;
         } else {
            this.addField(name, 0, value);
            return true;
         }
      }
   }

   public boolean putElement(long element, long value) {
      return this.noRedirectPutElement(element, value);
   }

   boolean noRedirectPutElement(long element, long value) {
      long index = this.parseIndex(element);
      return index >= 0 ? this.noRedirectPutIndex(index, value) : this.putFieldNoAssert(Convert.toInternString(element), value);
   }

   public boolean putIndex(long index, long value) {
      return this.noRedirectPutIndex(index, value);
   }

   boolean noRedirectPutIndex(long param1, long param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/ecmascript/runtime/ESObject._arrayElementValues [J
      // 04: lload 1
      // 05: l2i
      // 06: lload 3
      // 07: lastore
      // 08: bipush 1
      // 09: ireturn
      // 0a: astore 5
      // 0c: goto 11
      // 0f: astore 5
      // 11: aload 0
      // 12: lload 1
      // 13: invokespecial net/rim/ecmascript/runtime/ESObject.ensureCapacity (J)Z
      // 16: ifeq 23
      // 19: aload 0
      // 1a: getfield net/rim/ecmascript/runtime/ESObject._arrayElementValues [J
      // 1d: lload 1
      // 1e: l2i
      // 1f: lload 3
      // 20: lastore
      // 21: bipush 1
      // 22: ireturn
      // 23: aload 0
      // 24: lload 1
      // 25: invokestatic java/lang/Long.toString (J)Ljava/lang/String;
      // 28: invokestatic net/rim/ecmascript/util/Misc.stringIntern (Ljava/lang/String;)Ljava/lang/String;
      // 2b: lload 3
      // 2c: invokespecial net/rim/ecmascript/runtime/ESObject.putFieldNoAssert (Ljava/lang/String;J)Z
      // 2f: ireturn
      // try (0 -> 7): 8 null
      // try (0 -> 7): 10 null
   }

   private boolean canPutField(String name, long value) {
      int i = this.getIndexOfField(name);
      if (i != -1) {
         return (this._propertyAttributes[i] & 1) == 0;
      } else {
         return this._prototype == null ? true : this._prototype.canPutField(name, value);
      }
   }

   public boolean hasField(String name) {
      return this.hasFieldNoAssert(name);
   }

   private boolean hasFieldNoAssert(String name) {
      int i = this.getIndexOfField(name);
      if (i != -1) {
         return true;
      } else {
         return this._prototype != null ? this._prototype.hasField(name) : false;
      }
   }

   public boolean hasElement(long element) {
      long index = this.parseIndex(element);
      return index >= 0 ? this.hasIndex(index) : this.hasFieldNoAssert(Convert.toInternString(element));
   }

   public boolean hasIndex(long param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/ecmascript/runtime/ESObject._arrayElementValues [J
      // 04: lload 1
      // 05: l2i
      // 06: laload
      // 07: getstatic net/rim/ecmascript/runtime/Value.PLACEHOLDER_ARRAY J
      // 0a: lcmp
      // 0b: ifeq 12
      // 0e: bipush 1
      // 0f: goto 13
      // 12: bipush 0
      // 13: ireturn
      // 14: astore 3
      // 15: goto 19
      // 18: astore 3
      // 19: aload 0
      // 1a: lload 1
      // 1b: invokestatic java/lang/Long.toString (J)Ljava/lang/String;
      // 1e: invokestatic net/rim/ecmascript/util/Misc.stringIntern (Ljava/lang/String;)Ljava/lang/String;
      // 21: invokespecial net/rim/ecmascript/runtime/ESObject.hasFieldNoAssert (Ljava/lang/String;)Z
      // 24: ireturn
      // try (0 -> 11): 12 null
      // try (0 -> 11): 14 null
   }

   public boolean deleteField(String name) {
      return this.deleteFieldNoAssert(name);
   }

   private boolean deleteFieldNoAssert(String name) {
      int i = this.getIndexOfField(name);
      return i == -1 ? true : this.deletePropertyByIndex(i);
   }

   public boolean deleteElement(long element) {
      long index = this.parseIndex(element);
      return index >= 0 ? this.deleteIndex(index) : this.deleteFieldNoAssert(Convert.toInternString(element));
   }

   public boolean deleteIndex(long param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/ecmascript/runtime/ESObject._arrayElementValues [J
      // 04: lload 1
      // 05: l2i
      // 06: getstatic net/rim/ecmascript/runtime/Value.PLACEHOLDER_ARRAY J
      // 09: lastore
      // 0a: bipush 1
      // 0b: ireturn
      // 0c: astore 3
      // 0d: goto 11
      // 10: astore 3
      // 11: aload 0
      // 12: lload 1
      // 13: invokestatic java/lang/Long.toString (J)Ljava/lang/String;
      // 16: invokestatic net/rim/ecmascript/util/Misc.stringIntern (Ljava/lang/String;)Ljava/lang/String;
      // 19: invokespecial net/rim/ecmascript/runtime/ESObject.deleteFieldNoAssert (Ljava/lang/String;)Z
      // 1c: ireturn
      // try (0 -> 7): 8 null
      // try (0 -> 7): 10 null
   }

   private boolean deletePropertyByIndex(int i) {
      this._cachedIndex = 0;
      if ((this._propertyAttributes[i] & 4) != 0) {
         return false;
      }

      this._propertyNames[i] = null;
      this._propertyValues[i] = Value.UNDEFINED;
      this._propertyAttributes[i] = 0;
      return true;
   }

   String getName(int index) {
      return this._propertyNames[index];
   }

   int getAttributesFieldNoAssert(String name) {
      int i = this.getIndexOfField(name);
      return i != -1 ? this._propertyAttributes[i] : 0;
   }

   int getAttributesIndex(long index) {
      return 0;
   }

   int getAttributesElement(long element) {
      long index = this.parseIndex(element);
      return index >= 0 ? this.getAttributesIndex(index) : this.getAttributesFieldNoAssert(Convert.toInternString(element));
   }

   void setAllAttributes(int attrib) {
      for (int i = 0; i < this._numFields; i++) {
         this._propertyAttributes[i] = (byte)attrib;
      }
   }

   public void setAttributesField(String name, int attrib) {
      int i = this.getIndexOfField(name);
      if (i != -1) {
         this._propertyAttributes[i] = (byte)attrib;
      }
   }

   public long defaultValue() {
      return this.getDefaultNumber();
   }

   public long defaultStringValue() {
      long value = Context.callProperty(this, "toString", Names.NoParms);
      if (Value.isPrimitive(value)) {
         return value;
      } else {
         value = Context.callProperty(this, "valueOf", Names.NoParms);
         if (Value.isPrimitive(value)) {
            return value;
         } else {
            throw ThrownValue.typeError(Resources.getString(59));
         }
      }
   }

   public long defaultNumberValue() {
      return this.getDefaultNumber();
   }

   public long getDefaultNumber() {
      long value = Context.callProperty(this, "valueOf", Names.NoParms);
      if (Value.isPrimitive(value)) {
         return value;
      } else {
         value = Context.callProperty(this, "toString", Names.NoParms);
         if (Value.isPrimitive(value)) {
            return value;
         } else {
            throw ThrownValue.typeError(Resources.getString(59));
         }
      }
   }

   public void setPrototype(ESObject prototype) {
      this._prototype = prototype;
   }

   boolean putRedirectedField(String name, long value) {
      return false;
   }

   long getRedirectedField(String name) {
      return Value.DEFAULT;
   }
}
