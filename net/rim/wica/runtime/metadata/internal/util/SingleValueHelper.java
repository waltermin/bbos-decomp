package net.rim.wica.runtime.metadata.internal.util;

import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.util.Util;

public final class SingleValueHelper {
   public static final Object resolveInValue(UIComponent cmp, Object inValue, int valueType) {
      if (inValue == null) {
         return null;
      } else if (inValue instanceof Object) {
         return inValue;
      } else if (inValue instanceof int[]) {
         return resolveInValue(cmp, (int[])inValue, valueType);
      } else if (inValue instanceof Object[]) {
         return resolveInValue(cmp, (Object[])inValue);
      } else {
         throw new Object("Invalid inValue.");
      }
   }

   public static final Object resolveInValue(UIComponent cmp, int[] array, int valueType) {
      Object result = null;
      int index = 0;
      long resultType = array[index++];
      if (resultType == 5) {
         resultType |= (long)array[index++] << 32;
      }

      if ((resultType & 32768) != 0) {
         throw new Object("Array can not be specified for resolution by SingleValueHelper");
      }

      int depth = array[index++];
      if (array[index] == -1 && array[index + 1] == -1) {
         throw new Object("Repetition can not be specified for resolution by SingleValueHelper");
      }

      WicletEx wiclet = (WicletEx)cmp.getScreen().getWiclet();
      if (array[index] == -1) {
         if (array[index + 1] != -1) {
            long handle = cmp.getScreen().getVarValue(array[index + 1], true);
            result = wiclet.getValue(handle, array, index + 2, index + depth);
         }
      } else {
         long handle = (long)array[index] << 32;
         result = wiclet.getValue(handle, array, index + 1, index + depth);
      }

      if (result != null && valueType != (int)(resultType & 4294967295L)) {
         result = convertValue(wiclet, valueType, resultType, result, 0);
      }

      return result;
   }

   public static final Object resolveInValue(UIComponent cmp, Object[] array) {
      StringBuffer val = (StringBuffer)(new Object());
      int size = array.length;

      for (int i = 0; i < size; i++) {
         if (array[i] instanceof int[]) {
            Object o = resolveInValue(cmp, (int[])array[i], 3);
            if (o != null) {
               val.append(o);
            }
         } else if (array[i] != null) {
            val.append(array[i]);
         }
      }

      return val.toString();
   }

   private static final Object baseCreateValue(int toType) {
      Object result = null;
      switch (toType) {
         case 1:
         case 5:
            result = new Object(0);
         case 0:
         case 7:
            return result;
         case 2:
            return new Object((double)0L);
         case 3:
         default:
            return "";
         case 4:
         case 6:
         case 8:
            return new Object(0);
      }
   }

   public static final Object convertValue(Wiclet wiclet, long toType, long fromType, Object value, int depth) {
      if ((int)(toType & 4294967295L) != 6) {
         if (value instanceof Object) {
            return parseString(wiclet, toType, (String)value);
         }

         if (value != null && (int)(toType & 4294967295L) == 3) {
            switch ((int)(fromType & 4294967295L)) {
               case 3:
                  return value.toString();
               case 4:
                  return Util.DEFAULT_DATE_FORMATTER.format(new Object(value));
               case 5:
               default:
                  int enumType = (int)(fromType >> 32);
                  String[] enumValues = wiclet.getEnums().getEnum(enumType);
                  int index = value;
                  return enumValues != null && enumValues.length > 0 && index >= 0 && index < enumValues.length ? enumValues[index] : null;
            }
         } else {
            return value;
         }
      } else {
         int dataType = (int)(toType >>> 32);
         KeyDataCollection kdc = (KeyDataCollection)wiclet.getDataCollection(dataType);
         int[] fields = kdc.getDef().getKeyFields();
         if (fields.length != 1) {
            return null;
         }

         int fieldType = kdc.getDef().getFieldType(fields[0]);
         toType = fieldType != 6 && fieldType != 5 ? fieldType : (long)kdc.getDef().getFieldReferenceType(fields[0]) << 32 | 4294967295L & fieldType;
         value = convertValue(wiclet, toType, fromType, value, depth + 1);
         if (depth == 0) {
            if (value == null) {
               value = baseCreateValue((int)toType & 32767);
            }

            return new Object(kdc.create(value));
         } else {
            return value;
         }
      }
   }

   public static final Object parseString(Wiclet param0, long param1, String param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: lload 1
      // 01: ldc2_w 4294967295
      // 04: land
      // 05: l2i
      // 06: tableswitch 54 -1 8 191 80 56 100 54 161 132 191 191 68
      // 3c: aload 3
      // 3d: areturn
      // 3e: new java/lang/Object
      // 41: dup
      // 42: aload 3
      // 43: invokestatic net/rim/wica/runtime/util/Util.convertStringToInt (Ljava/lang/String;)I
      // 46: invokespecial java/lang/Integer.<init> (I)V
      // 49: areturn
      // 4a: new java/lang/Object
      // 4d: dup
      // 4e: aload 3
      // 4f: invokestatic net/rim/wica/runtime/util/Util.convertStringToLong (Ljava/lang/String;)J
      // 52: invokespecial java/lang/Long.<init> (J)V
      // 55: areturn
      // 56: aload 3
      // 57: ldc_w "true"
      // 5a: invokevirtual java/lang/String.equalsIgnoreCase (Ljava/lang/String;)Z
      // 5d: ifeq 66
      // 60: getstatic java/lang/Boolean.TRUE Ljava/lang/Boolean;
      // 63: goto 69
      // 66: getstatic java/lang/Boolean.FALSE Ljava/lang/Boolean;
      // 69: areturn
      // 6a: new java/lang/Object
      // 6d: dup
      // 6e: aload 3
      // 6f: invokevirtual java/lang/String.trim ()Ljava/lang/String;
      // 72: ldc_w ""
      // 75: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 78: ifeq 82
      // 7b: nop
      // 7c: ldc2_w 0
      // 7f: goto 86
      // 82: aload 3
      // 83: invokestatic java/lang/Double.parseDouble (Ljava/lang/String;)D
      // 86: invokespecial java/lang/Double.<init> (D)V
      // 89: areturn
      // 8a: lload 1
      // 8b: bipush 32
      // 8d: lushr
      // 8e: l2i
      // 8f: istore 4
      // 91: new java/lang/Object
      // 94: dup
      // 95: aload 0
      // 96: invokeinterface net/rim/wica/runtime/metadata/Wiclet.getEnums ()Lnet/rim/wica/common/metadata/component/EnumCollection; 1
      // 9b: iload 4
      // 9d: aload 3
      // 9e: invokeinterface net/rim/wica/common/metadata/component/EnumCollection.getEnumValueAsInt (ILjava/lang/String;)I 3
      // a3: invokespecial java/lang/Integer.<init> (I)V
      // a6: areturn
      // a7: new java/lang/Object
      // aa: dup
      // ab: aload 3
      // ac: invokevirtual java/lang/String.trim ()Ljava/lang/String;
      // af: ldc_w ""
      // b2: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // b5: ifeq bd
      // b8: bipush 0
      // b9: i2l
      // ba: goto c1
      // bd: aload 3
      // be: invokestatic net/rim/device/api/io/http/HttpDateParser.parse (Ljava/lang/String;)J
      // c1: invokespecial java/lang/Long.<init> (J)V
      // c4: areturn
      // c5: new java/lang/Object
      // c8: dup
      // c9: ldc_w "Not recognized type"
      // cc: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // cf: athrow
      // d0: astore 4
      // d2: aconst_null
      // d3: areturn
      // try (0 -> 6): 74 null
      // try (7 -> 12): 74 null
      // try (13 -> 18): 74 null
      // try (19 -> 26): 74 null
      // try (27 -> 40): 74 null
      // try (41 -> 54): 74 null
      // try (55 -> 68): 74 null
      // try (69 -> 74): 74 null
   }
}
