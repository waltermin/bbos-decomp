package net.rim.wica.runtime.provisioning.internal;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.wica.runtime.provisioning.internal.elements.AbstractElement;
import net.rim.wica.runtime.provisioning.internal.elements.DataElement;
import net.rim.wica.runtime.provisioning.internal.elements.EnumerationElement;
import net.rim.wica.runtime.provisioning.internal.elements.FieldElement;
import net.rim.wica.runtime.provisioning.internal.elements.GlobalElement;

public class ProvisioningHelper {
   public static int encodeType(String type) {
      return encodeType(type, false);
   }

   public static int encodeType(String type, boolean isArray) {
      int resultingType = -1;
      if ("integer".equals(type)) {
         return isArray ? 32769 : 1;
      }

      if ("string".equals(type)) {
         return isArray ? 32771 : 3;
      }

      if ("long".equals(type)) {
         return isArray ? 32776 : 8;
      }

      if ("data".equals(type)) {
         return isArray ? 32774 : 6;
      }

      if ("decimal".equals(type)) {
         return isArray ? 32770 : 2;
      }

      if ("boolean".equals(type)) {
         return isArray ? 32768 : 0;
      }

      if ("date".equals(type)) {
         return isArray ? 32772 : 4;
      }

      if ("enumeration".equals(type)) {
         if (isArray) {
            return 32773;
         }

         resultingType = 5;
      }

      return resultingType;
   }

   public static AbstractElement findAncestor(AbstractElement startingFrom, String definitionElementName) {
      AbstractElement parent = startingFrom.getParent();

      while (parent.getParent() != null && parent != null && !definitionElementName.equals(parent.getElementName())) {
         parent = parent.getParent();
      }

      return parent;
   }

   public static int parseBoolean(boolean b) {
      return b ? 1 : 0;
   }

   public static boolean parseBoolean(String bool) {
      return "true".equals(bool);
   }

   public static int parseBooleanAsStringToInt(String bool) {
      return parseBoolean("true".equals(bool));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static double parseDouble(String s) {
      boolean var3 = false /* VF: Semaphore variable */;

      double var10000;
      try {
         var3 = true;
         var10000 = Double.parseDouble(s);
         var3 = false;
      } finally {
         if (var3) {
            return (double)0L;
         }
      }

      return var10000;
   }

   public static long parseDoubleAsStringToLong(String s) {
      try {
         return Double.doubleToLongBits(Double.parseDouble(s));
      } finally {
         return Double.doubleToLongBits((double)0L);
      }
   }

   public static int parseInt(String s) {
      try {
         return Integer.parseInt(s);
      } finally {
         ;
      }
   }

   public static long parseLong(String s) {
      try {
         return Long.parseLong(s);
      } finally {
         return 0;
      }
   }

   public static long parseNonArrayValue(String defaultAttributeValue, int attributeType, Vector objectData) {
      long result = 0;
      switch (attributeType) {
         case 0:
            return parseBooleanAsStringToInt(defaultAttributeValue);
         case 1:
            return parseInt(defaultAttributeValue);
         case 2:
            return parseDoubleAsStringToLong(defaultAttributeValue);
         case 3:
         default:
            objectData.addElement(defaultAttributeValue);
            return objectData.size() - 1;
         case 4:
            result = convertString2Date(defaultAttributeValue).getTime();
         case -1:
         case 5:
         case 6:
         case 7:
            return result;
         case 8:
            return parseLong(defaultAttributeValue);
      }
   }

   public static long parseDefaultValue(FieldElement fe, Vector objectData) {
      String defaultValue = fe.getDefaultValue();
      int attributeType = encodeType(fe.getType());
      long result = 0;
      switch (attributeType) {
         case -1:
         case 6:
         case 7:
            break;
         case 0:
            result = parseBooleanAsStringToInt(defaultValue);
            break;
         case 1:
            result = parseInt(defaultValue);
            break;
         case 2:
            result = parseDoubleAsStringToLong(defaultValue);
            break;
         case 3:
         default:
            objectData.addElement(defaultValue);
            result = objectData.size() - 1;
            break;
         case 4:
            result = convertString2Date(defaultValue).getTime();
            break;
         case 5:
            EnumerationElement ee = (EnumerationElement)fe.getComponent();
            Vector enumValues = ee.getValues();
            result = enumValues.indexOf(defaultValue);
            break;
         case 8:
            result = parseLong(defaultValue);
      }

      return result;
   }

   public static void visitDefinitionElementsHelper(Hashtable table, DefinitionVisitor v) {
      if (table != null) {
         Enumeration enumeration = table.elements();

         while (enumeration.hasMoreElements()) {
            AbstractElement element = (AbstractElement)enumeration.nextElement();
            element.accept(v);
         }
      }
   }

   public static void visitDefinitionElementsHelper(Vector vector, DefinitionVisitor v) {
      if (vector != null) {
         Enumeration enumeration = vector.elements();

         while (enumeration.hasMoreElements()) {
            AbstractElement element = (AbstractElement)enumeration.nextElement();
            element.accept(v);
         }
      }
   }

   public static Object parseArray(Vector defaultValues, int attributeType) {
      Object result = null;
      switch (attributeType) {
         case 0:
            return parseBooleanArray(defaultValues);
         case 1:
            return parseIntArray(defaultValues);
         case 2:
            return parseDoubleArray(defaultValues);
         case 3:
         default:
            String[] sArray = new String[defaultValues.size()];
            defaultValues.copyInto(sArray);
            return sArray;
         case 4:
            result = parseDateArray(defaultValues);
         case -1:
         case 5:
         case 6:
         case 7:
            return result;
         case 8:
            return parseLongArray(defaultValues);
      }
   }

   private static int[] parseBooleanArray(Vector v) {
      int size = v.size();
      int[] a = new int[size];

      for (int i = 0; i < size; i++) {
         a[i] = parseBooleanAsStringToInt((String)v.elementAt(i));
      }

      return a;
   }

   private static long[] parseDateArray(Vector v) {
      int size = v.size();
      long[] a = new long[size];

      for (int i = 0; i < size; i++) {
         a[i] = convertString2Date((String)v.elementAt(i)).getTime();
      }

      return a;
   }

   private static double[] parseDoubleArray(Vector v) {
      int size = v.size();
      double[] a = new double[size];

      for (int i = 0; i < size; i++) {
         a[i] = parseDouble((String)v.elementAt(i));
      }

      return a;
   }

   private static long[] parseLongArray(Vector v) {
      int size = v.size();
      long[] a = new long[size];

      for (int i = 0; i < size; i++) {
         a[i] = parseLong((String)v.elementAt(i));
      }

      return a;
   }

   private static int[] parseIntArray(Vector v) {
      int size = v.size();
      int[] a = new int[size];

      for (int i = 0; i < size; i++) {
         a[i] = parseInt((String)v.elementAt(i));
      }

      return a;
   }

   public static int encodeFieldDefaultHi(long value) {
      return (int)(value >>> 32);
   }

   public static int encodeFieldDefaultLo(long value) {
      return (int)(value & -1);
   }

   public static int encodeMessageFieldBits(FieldElement field) {
      return field.hasDefaultValue() ? 1 : 0;
   }

   public static int encodeMessageFieldBits(GlobalElement global) {
      return global.hasValues() ? 1 : 0;
   }

   public static int encodeDataFieldBits(DataElement owner, FieldElement field) {
      int fieldBits = 0;
      if (field.hasDefaultValue()) {
         fieldBits |= 2;
      }

      if (owner.isKeyField(field)) {
         fieldBits |= 8;
      }

      return fieldBits;
   }

   public static int encodeDataFieldBits(GlobalElement global) {
      int fieldBits = 0;
      if (global.hasValues()) {
         fieldBits |= 2;
      }

      if (!global.isPersist()) {
         fieldBits |= 1;
      }

      return fieldBits;
   }

   public static long resolveDefaultValue(GlobalElement global, Vector objectData) {
      long result = 0;
      Vector defaultValues = global.getValues();
      if (defaultValues == null || defaultValues.isEmpty()) {
         return result;
      }

      if (global.isArray()) {
         Object array = parseArray(defaultValues, encodeType(global.getType()));
         objectData.addElement(array);
         return objectData.size() - 1;
      }

      String defaultValue = (String)defaultValues.elementAt(0);
      if (defaultValue != null && defaultValue.length() > 0) {
         result = parseNonArrayValue(defaultValue, encodeType(global.getType()), objectData);
      }

      return result;
   }

   public static long resolveDefaultValue(FieldElement fe, Vector objectData) {
      long result = 0;
      String defaultValue = fe.getDefaultValue();
      if (defaultValue != null && defaultValue.length() > 0 && !fe.isArray()) {
         result = parseDefaultValue(fe, objectData);
      }

      return result;
   }

   public static String getClassName(Object o) {
      String result = "null";
      if (o != null) {
         String fullClassName = o.getClass().getName();
         int index = fullClassName.lastIndexOf(46);
         if (index != -1) {
            return fullClassName.substring(index + 1);
         }

         result = "primitive array";
      }

      return result;
   }

   private static Date convertString2Date(String param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 1
      // 002: aload 0
      // 003: bipush 45
      // 005: invokevirtual java/lang/String.indexOf (I)I
      // 008: istore 2
      // 009: iload 2
      // 00a: bipush -1
      // 00c: if_icmpne 017
      // 00f: new java/util/Date
      // 012: dup
      // 013: invokespecial java/util/Date.<init> ()V
      // 016: areturn
      // 017: aload 0
      // 018: bipush 0
      // 019: iload 2
      // 01a: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 01d: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 020: istore 3
      // 021: aload 0
      // 022: bipush 45
      // 024: iload 2
      // 025: bipush 1
      // 026: iadd
      // 027: invokevirtual java/lang/String.indexOf (II)I
      // 02a: istore 4
      // 02c: iload 4
      // 02e: bipush -1
      // 030: if_icmpne 03b
      // 033: new java/util/Date
      // 036: dup
      // 037: invokespecial java/util/Date.<init> ()V
      // 03a: areturn
      // 03b: aload 0
      // 03c: iload 2
      // 03d: bipush 1
      // 03e: iadd
      // 03f: iload 4
      // 041: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 044: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 047: istore 5
      // 049: aload 0
      // 04a: bipush 84
      // 04c: iload 4
      // 04e: bipush 1
      // 04f: iadd
      // 050: invokevirtual java/lang/String.indexOf (II)I
      // 053: istore 6
      // 055: iload 6
      // 057: bipush -1
      // 059: if_icmpne 064
      // 05c: new java/util/Date
      // 05f: dup
      // 060: invokespecial java/util/Date.<init> ()V
      // 063: areturn
      // 064: aload 0
      // 065: iload 4
      // 067: bipush 1
      // 068: iadd
      // 069: iload 6
      // 06b: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 06e: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 071: istore 7
      // 073: aload 0
      // 074: bipush 58
      // 076: iload 6
      // 078: bipush 1
      // 079: iadd
      // 07a: invokevirtual java/lang/String.indexOf (II)I
      // 07d: istore 8
      // 07f: iload 8
      // 081: bipush -1
      // 083: if_icmpne 08e
      // 086: new java/util/Date
      // 089: dup
      // 08a: invokespecial java/util/Date.<init> ()V
      // 08d: areturn
      // 08e: aload 0
      // 08f: iload 6
      // 091: bipush 1
      // 092: iadd
      // 093: iload 8
      // 095: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 098: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 09b: istore 9
      // 09d: aload 0
      // 09e: bipush 58
      // 0a0: iload 8
      // 0a2: bipush 1
      // 0a3: iadd
      // 0a4: invokevirtual java/lang/String.indexOf (II)I
      // 0a7: istore 10
      // 0a9: iload 10
      // 0ab: bipush -1
      // 0ad: if_icmpne 0b8
      // 0b0: new java/util/Date
      // 0b3: dup
      // 0b4: invokespecial java/util/Date.<init> ()V
      // 0b7: areturn
      // 0b8: aload 0
      // 0b9: iload 8
      // 0bb: bipush 1
      // 0bc: iadd
      // 0bd: iload 10
      // 0bf: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 0c2: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 0c5: istore 11
      // 0c7: aload 0
      // 0c8: bipush 46
      // 0ca: iload 10
      // 0cc: bipush 1
      // 0cd: iadd
      // 0ce: invokevirtual java/lang/String.indexOf (II)I
      // 0d1: istore 12
      // 0d3: iload 12
      // 0d5: bipush -1
      // 0d7: if_icmpne 0e2
      // 0da: new java/util/Date
      // 0dd: dup
      // 0de: invokespecial java/util/Date.<init> ()V
      // 0e1: areturn
      // 0e2: aload 0
      // 0e3: iload 10
      // 0e5: bipush 1
      // 0e6: iadd
      // 0e7: iload 12
      // 0e9: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 0ec: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 0ef: istore 13
      // 0f1: new java/lang/StringBuffer
      // 0f4: dup
      // 0f5: ldc_w "GMT"
      // 0f8: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0fb: aload 0
      // 0fc: iload 12
      // 0fe: bipush 1
      // 0ff: iadd
      // 100: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 103: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 106: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 109: astore 14
      // 10b: invokestatic java/util/Calendar.getInstance ()Ljava/util/Calendar;
      // 10e: astore 1
      // 10f: aload 1
      // 110: aload 14
      // 112: invokestatic java/util/TimeZone.getTimeZone (Ljava/lang/String;)Ljava/util/TimeZone;
      // 115: invokevirtual java/util/Calendar.setTimeZone (Ljava/util/TimeZone;)V
      // 118: aload 1
      // 119: bipush 1
      // 11a: iload 3
      // 11b: invokevirtual java/util/Calendar.set (II)V
      // 11e: aload 1
      // 11f: bipush 2
      // 121: iload 5
      // 123: bipush 1
      // 124: isub
      // 125: invokevirtual java/util/Calendar.set (II)V
      // 128: aload 1
      // 129: bipush 5
      // 12b: iload 7
      // 12d: invokevirtual java/util/Calendar.set (II)V
      // 130: aload 1
      // 131: bipush 11
      // 133: iload 9
      // 135: invokevirtual java/util/Calendar.set (II)V
      // 138: aload 1
      // 139: bipush 12
      // 13b: iload 11
      // 13d: invokevirtual java/util/Calendar.set (II)V
      // 140: aload 1
      // 141: bipush 13
      // 143: iload 13
      // 145: invokevirtual java/util/Calendar.set (II)V
      // 148: goto 15d
      // 14b: astore 2
      // 14c: new java/util/Date
      // 14f: dup
      // 150: invokespecial java/util/Date.<init> ()V
      // 153: areturn
      // 154: astore 2
      // 155: new java/util/Date
      // 158: dup
      // 159: invokespecial java/util/Date.<init> ()V
      // 15c: areturn
      // 15d: aload 1
      // 15e: invokevirtual java/util/Calendar.getTime ()Ljava/util/Date;
      // 161: areturn
      // try (2 -> 12): 174 null
      // try (13 -> 32): 174 null
      // try (33 -> 54): 174 null
      // try (55 -> 76): 174 null
      // try (77 -> 98): 174 null
      // try (99 -> 120): 174 null
      // try (121 -> 173): 174 null
      // try (2 -> 12): 179 null
      // try (13 -> 32): 179 null
      // try (33 -> 54): 179 null
      // try (55 -> 76): 179 null
      // try (77 -> 98): 179 null
      // try (99 -> 120): 179 null
      // try (121 -> 173): 179 null
   }
}
