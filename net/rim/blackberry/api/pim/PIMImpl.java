package net.rim.blackberry.api.pim;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.apps.internal.api.serialformats.ICalendarWriter;
import net.rim.device.apps.internal.api.serialformats.VCardWriter;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

class PIMImpl extends PIM {
   private static PIM _pim;

   private PIMImpl() {
   }

   private static void assertPermission() {
      ApplicationControl.assertPIMAllowed(true);
   }

   public static PIM getInstance() {
      assertPermission();
      if (_pim == null) {
         _pim = new PIMImpl();
      }

      return _pim;
   }

   @Override
   public PIMList openPIMList(int pimListType, int mode) {
      assertPermission();
      if (!isValidMode(mode)) {
         throw new Object();
      }

      switch (pimListType) {
         case 0:
            throw new Object();
         case 1:
         default:
            return new ContactListImpl(mode);
         case 2:
            return new EventListImpl(mode);
         case 3:
            return ToDoListFactory.createToDoList(mode);
      }
   }

   @Override
   public PIMList openPIMList(int pimListType, int mode, String name) {
      assertPermission();
      if (!isValidMode(mode)) {
         throw new Object();
      }

      if (name == null) {
         throw new Object();
      }

      switch (pimListType) {
         case 0:
            throw new Object();
         case 1:
         default:
            if (ContactListImpl.LIST_NAME.equals(name)) {
               return new ContactListImpl(mode);
            }
            break;
         case 2:
            if (EventListImpl.LIST_NAME.equals(name)) {
               return new EventListImpl(mode);
            }
            break;
         case 3:
            if ("ToDo List".equals(name)) {
               return ToDoListFactory.createToDoList(mode);
            }
      }

      throw new PIMException(((StringBuffer)(new Object("List "))).append(name).append(" is not a valid PIMList.").toString(), 3);
   }

   @Override
   public String[] listPIMLists(int pimListType) {
      assertPermission();
      switch (pimListType) {
         case 0:
            throw new Object();
         case 1:
         default:
            return new Object[]{ContactListImpl.LIST_NAME};
         case 2:
            return new Object[]{EventListImpl.LIST_NAME};
         case 3:
            try {
               ToDoListFactory.createToDoList(1);
               return new String[]{"ToDo List"};
            } catch (PIMException pe) {
               return new Object[0];
            }
      }
   }

   @Override
   public PIMItem[] fromSerialFormat(InputStream param1, String param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: invokestatic net/rim/blackberry/api/pim/PIMImpl.assertPermission ()V
      // 003: aload 1
      // 004: ifnull 00b
      // 007: aload 2
      // 008: ifnonnull 013
      // 00b: new java/lang/Object
      // 00e: dup
      // 00f: invokespecial java/lang/NullPointerException.<init> ()V
      // 012: athrow
      // 013: aload 2
      // 014: invokevirtual java/lang/String.length ()I
      // 017: ifne 01e
      // 01a: ldc_w "UTF8"
      // 01d: astore 2
      // 01e: new java/lang/Object
      // 021: dup
      // 022: aload 1
      // 023: aload 2
      // 024: invokespecial java/io/InputStreamReader.<init> (Ljava/io/InputStream;Ljava/lang/String;)V
      // 027: astore 3
      // 028: bipush 15
      // 02a: istore 4
      // 02c: iload 4
      // 02e: newarray 5
      // 030: astore 5
      // 032: aload 3
      // 033: sipush 500
      // 036: invokevirtual java/io/InputStreamReader.mark (I)V
      // 039: aload 3
      // 03a: aload 5
      // 03c: invokevirtual java/io/Reader.read ([C)I
      // 03f: pop
      // 040: new java/lang/Object
      // 043: dup
      // 044: aload 5
      // 046: invokespecial java/lang/String.<init> ([C)V
      // 049: astore 6
      // 04b: aload 6
      // 04d: ldc_w "VCARD"
      // 050: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 053: bipush -1
      // 055: if_icmpeq 09e
      // 058: aload 3
      // 059: invokevirtual java/io/InputStreamReader.reset ()V
      // 05c: new net/rim/blackberry/api/pim/ContactImpl
      // 05f: dup
      // 060: invokespecial net/rim/blackberry/api/pim/ContactImpl.<init> ()V
      // 063: astore 7
      // 065: new net/rim/blackberry/api/pim/ContactVCardProvider
      // 068: dup
      // 069: aload 7
      // 06b: invokespecial net/rim/blackberry/api/pim/ContactVCardProvider.<init> (Lnet/rim/blackberry/api/pim/ContactImpl;)V
      // 06e: astore 8
      // 070: new java/lang/Object
      // 073: dup
      // 074: aload 8
      // 076: aload 1
      // 077: aload 2
      // 078: invokespecial net/rim/device/apps/internal/api/serialformats/VCardReader.<init> (Lnet/rim/device/apps/internal/api/serialformats/VCardProvider;Ljava/io/InputStream;Ljava/lang/String;)V
      // 07b: astore 9
      // 07d: aload 9
      // 07f: invokevirtual net/rim/device/apps/internal/api/serialformats/VCardReader.parseIt ()Lnet/rim/device/apps/internal/api/serialformats/VCardProvider;
      // 082: pop
      // 083: goto 094
      // 086: astore 10
      // 088: new net/rim/blackberry/api/pim/PIMException
      // 08b: dup
      // 08c: ldc_w "Unable to read VCard."
      // 08f: bipush 1
      // 090: invokespecial net/rim/blackberry/api/pim/PIMException.<init> (Ljava/lang/String;I)V
      // 093: athrow
      // 094: bipush 1
      // 095: anewarray 430
      // 098: dup
      // 099: bipush 0
      // 09a: aload 7
      // 09c: aastore
      // 09d: areturn
      // 09e: aload 6
      // 0a0: ldc_w "VCALENDAR"
      // 0a3: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 0a6: bipush -1
      // 0a8: if_icmpne 0ae
      // 0ab: goto 1ac
      // 0ae: bipush 6
      // 0b0: newarray 5
      // 0b2: astore 7
      // 0b4: aload 3
      // 0b5: invokevirtual java/io/InputStreamReader.read ()I
      // 0b8: istore 8
      // 0ba: iload 8
      // 0bc: bipush 58
      // 0be: if_icmpeq 0c4
      // 0c1: goto 186
      // 0c4: aload 3
      // 0c5: aload 7
      // 0c7: invokevirtual java/io/Reader.read ([C)I
      // 0ca: pop
      // 0cb: new java/lang/Object
      // 0ce: dup
      // 0cf: aload 7
      // 0d1: invokespecial java/lang/String.<init> ([C)V
      // 0d4: astore 9
      // 0d6: aload 9
      // 0d8: ldc_w "VEVENT"
      // 0db: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 0de: bipush -1
      // 0e0: if_icmpeq 12f
      // 0e3: aload 3
      // 0e4: invokevirtual java/io/InputStreamReader.reset ()V
      // 0e7: new net/rim/blackberry/api/pim/EventImpl
      // 0ea: dup
      // 0eb: bipush 1
      // 0ec: invokespecial net/rim/blackberry/api/pim/EventImpl.<init> (I)V
      // 0ef: astore 10
      // 0f1: new net/rim/blackberry/api/pim/EventICalendarProvider
      // 0f4: dup
      // 0f5: aload 10
      // 0f7: invokespecial net/rim/blackberry/api/pim/EventICalendarProvider.<init> (Lnet/rim/blackberry/api/pim/EventImpl;)V
      // 0fa: astore 11
      // 0fc: new java/lang/Object
      // 0ff: dup
      // 100: aload 11
      // 102: aload 1
      // 103: aload 2
      // 104: invokespecial net/rim/device/apps/internal/api/serialformats/ICalendarReader.<init> (Lnet/rim/device/apps/internal/api/serialformats/ICalendarProvider;Ljava/io/InputStream;Ljava/lang/String;)V
      // 107: astore 12
      // 109: aload 12
      // 10b: invokevirtual net/rim/device/apps/internal/api/serialformats/ICalendarReader.parseIt ()Lnet/rim/device/apps/internal/api/serialformats/ICalendarProvider;
      // 10e: pop
      // 10f: aload 11
      // 111: invokevirtual net/rim/blackberry/api/pim/EventICalendarProvider.setRepeat ()V
      // 114: goto 125
      // 117: astore 13
      // 119: new net/rim/blackberry/api/pim/PIMException
      // 11c: dup
      // 11d: ldc_w "Unable to read VCalendar."
      // 120: bipush 1
      // 121: invokespecial net/rim/blackberry/api/pim/PIMException.<init> (Ljava/lang/String;I)V
      // 124: athrow
      // 125: bipush 1
      // 126: anewarray 520
      // 129: dup
      // 12a: bipush 0
      // 12b: aload 10
      // 12d: aastore
      // 12e: areturn
      // 12f: aload 9
      // 131: ldc_w "VTODO"
      // 134: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 137: bipush -1
      // 139: if_icmpeq 186
      // 13c: aload 3
      // 13d: invokevirtual java/io/InputStreamReader.reset ()V
      // 140: bipush 1
      // 141: invokestatic net/rim/blackberry/api/pim/ToDoFactory.createToDo (I)Lnet/rim/blackberry/api/pim/ToDo;
      // 144: astore 10
      // 146: aload 10
      // 148: ifnonnull 14d
      // 14b: aconst_null
      // 14c: areturn
      // 14d: new net/rim/blackberry/api/pim/ToDoICalendarProvider
      // 150: dup
      // 151: aload 10
      // 153: invokespecial net/rim/blackberry/api/pim/ToDoICalendarProvider.<init> (Lnet/rim/blackberry/api/pim/ToDo;)V
      // 156: astore 11
      // 158: new java/lang/Object
      // 15b: dup
      // 15c: aload 11
      // 15e: aload 1
      // 15f: aload 2
      // 160: invokespecial net/rim/device/apps/internal/api/serialformats/ICalendarReader.<init> (Lnet/rim/device/apps/internal/api/serialformats/ICalendarProvider;Ljava/io/InputStream;Ljava/lang/String;)V
      // 163: astore 12
      // 165: aload 12
      // 167: invokevirtual net/rim/device/apps/internal/api/serialformats/ICalendarReader.parseIt ()Lnet/rim/device/apps/internal/api/serialformats/ICalendarProvider;
      // 16a: pop
      // 16b: goto 17c
      // 16e: astore 13
      // 170: new net/rim/blackberry/api/pim/PIMException
      // 173: dup
      // 174: ldc_w "Unable to read VCalendar."
      // 177: bipush 1
      // 178: invokespecial net/rim/blackberry/api/pim/PIMException.<init> (Ljava/lang/String;I)V
      // 17b: athrow
      // 17c: bipush 1
      // 17d: anewarray 574
      // 180: dup
      // 181: bipush 0
      // 182: aload 10
      // 184: aastore
      // 185: areturn
      // 186: iload 8
      // 188: iflt 18e
      // 18b: goto 0b4
      // 18e: goto 1ac
      // 191: astore 6
      // 193: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 196: aload 6
      // 198: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // 19b: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 19e: goto 1ac
      // 1a1: astore 6
      // 1a3: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 1a6: ldc_w "Serial format not supported."
      // 1a9: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 1ac: new net/rim/blackberry/api/pim/PIMException
      // 1af: dup
      // 1b0: invokespecial net/rim/blackberry/api/pim/PIMException.<init> ()V
      // 1b3: athrow
      // try (60 -> 63): 64 null
      // try (127 -> 132): 133 null
      // try (173 -> 176): 177 null
      // try (25 -> 77): 195 null
      // try (78 -> 146): 195 null
      // try (147 -> 160): 195 null
      // try (161 -> 190): 195 null
      // try (191 -> 194): 195 null
      // try (25 -> 77): 201 null
      // try (78 -> 146): 201 null
      // try (147 -> 160): 201 null
      // try (161 -> 190): 201 null
      // try (191 -> 194): 201 null
   }

   @Override
   public void toSerialFormat(PIMItem item, OutputStream os, String enc, String dataFormat) {
      assertPermission();
      if (item == null || os == null || enc == null || dataFormat == null) {
         throw new Object();
      }

      if (item instanceof ContactImpl) {
         int vcardversion = 2;
         if (dataFormat.equals(PIM.VCARD_VERSION_2_1)) {
            vcardversion = 1;
         } else if (!dataFormat.equals(PIM.VCARD_VERSION_3_0)) {
            throw new PIMException(((StringBuffer)(new Object("VCard version "))).append(dataFormat).append(" not supported.").toString(), 0);
         }

         ContactVCardProvider vcard = new ContactVCardProvider((ContactImpl)item);
         vcard.setVersion(vcardversion);
         VCardWriter vcardWriter = (VCardWriter)(new Object(vcard, os, enc));

         try {
            vcardWriter.encodeVCard();
         } finally {
            throw new PIMException("Unable to write VCard.", 1);
         }
      } else if (item instanceof EventImpl) {
         if (!isValidSerialVersion(dataFormat)) {
            throw new PIMException(((StringBuffer)(new Object("VCal version "))).append(dataFormat).append(" not supported.").toString(), 0);
         }

         try {
            EventICalendarProvider vCal = new EventICalendarProvider((EventImpl)item);
            vCal.setVersion(dataFormat);
            if (dataFormat.equals("VCALENDAR/2.0")) {
               vCal.setAlarmNestedBeginTag(-1770502245);
            }

            ICalendarWriter icalWriter = (ICalendarWriter)(new Object(vCal, os, enc));
            icalWriter.encodeICalendar();
         } finally {
            throw new PIMException("Unable to write VCal.", 1);
         }
      } else if (item instanceof ToDo) {
         if (!isValidSerialVersion(dataFormat)) {
            throw new PIMException(((StringBuffer)(new Object("VCal version "))).append(dataFormat).append(" not supported.").toString(), 0);
         }

         try {
            ToDoICalendarProvider vCal = new ToDoICalendarProvider((ToDo)item);
            vCal.setVersion(dataFormat);
            ICalendarWriter icalWriter = (ICalendarWriter)(new Object(vCal, os, enc));
            icalWriter.encodeICalendar();
         } finally {
            throw new PIMException("Unable to write VCal.", 1);
         }
      }
   }

   private static boolean isValidSerialVersion(String version) {
      for (int i = PIM.VCAL_VERSIONS.length - 1; i >= 0; i--) {
         if (PIM.VCAL_VERSIONS[i].equals(version)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String[] supportedSerialFormats(int pimListType) {
      switch (pimListType) {
         case 0:
            throw new Object();
         case 1:
         default:
            return PIM.VCARD_VERSIONS;
         case 2:
            return PIM.VCAL_VERSIONS;
         case 3:
            return PIM.VCAL_VERSIONS;
      }
   }

   private static boolean isValidMode(int mode) {
      return mode >= 1 && mode <= 3;
   }
}
