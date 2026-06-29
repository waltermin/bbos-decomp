package net.rim.blackberry.api.pdap;

import com.sun.cldc.i18n.Helper;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.pim.Contact;
import javax.microedition.pim.Event;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.ToDo;
import net.rim.device.apps.internal.api.serialformats.ICalendarWriter;
import net.rim.device.apps.internal.api.serialformats.VCardWriter;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.MIDletSecurity;

public class PIMImpl extends BlackBerryPIM {
   static String VCARD_VERSION_2_1 = "VCARD/2.1";
   static String VCARD_VERSION_3_0 = "VCARD/3.0";
   static String DEFAULT_VCARD_VERSION = VCARD_VERSION_3_0;
   static String[] VCARD_VERSIONS = new Object[]{VCARD_VERSION_2_1, VCARD_VERSION_3_0};
   static String DEFAULT_VCAL_VERSION = "VCALENDAR/1.0";
   static String[] VCAL_VERSIONS = new Object[]{DEFAULT_VCAL_VERSION, "VCALENDAR/2.0"};
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

      switch (mode) {
         case 0:
            break;
         case 1:
         default:
            MIDletSecurity.checkPermission(9);
            break;
         case 2:
            MIDletSecurity.checkPermission(10);
            break;
         case 3:
            MIDletSecurity.checkPermission(11);
      }

      switch (pimListType) {
         case 0:
         case 4:
            throw new Object();
         case 1:
         default:
            return new ContactListImpl(mode);
         case 2:
            return new EventListImpl(mode);
         case 3:
            return ToDoListFactory.createToDoList(mode);
         case 5:
            return MemoListFactory.createMemoList(mode);
      }
   }

   @Override
   public PIMList openPIMList(int pimListType, int mode, String name) throws PIMException {
      assertPermission();
      if (!isValidMode(mode)) {
         throw new Object();
      }

      if (name == null) {
         throw new Object();
      }

      switch (pimListType) {
         case 0:
         case 4:
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
            break;
         case 5:
            if ("Memo List".equals(name)) {
               return MemoListFactory.createMemoList(mode);
            }
      }

      throw new PIMException(((StringBuffer)(new Object("List "))).append(name).append(" is not a valid PIMList.").toString(), 3);
   }

   @Override
   public String[] listPIMLists(int pimListType) {
      assertPermission();
      switch (pimListType) {
         case 0:
         case 4:
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
         case 5:
            try {
               MemoListFactory.createMemoList(1);
               return new String[]{"Memo List"};
            } catch (PIMException pe) {
               return new Object[0];
            }
      }
   }

   @Override
   public PIMItem[] fromSerialFormat(InputStream param1, String param2) throws PIMException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: invokestatic net/rim/blackberry/api/pdap/PIMImpl.assertPermission ()V
      // 003: aload 1
      // 004: ifnonnull 00f
      // 007: new java/lang/Object
      // 00a: dup
      // 00b: invokespecial java/lang/NullPointerException.<init> ()V
      // 00e: athrow
      // 00f: aload 2
      // 010: ifnonnull 017
      // 013: ldc_w "UTF-8"
      // 016: astore 2
      // 017: new java/lang/Object
      // 01a: dup
      // 01b: aload 1
      // 01c: aload 2
      // 01d: invokespecial java/io/InputStreamReader.<init> (Ljava/io/InputStream;Ljava/lang/String;)V
      // 020: astore 3
      // 021: bipush 15
      // 023: istore 4
      // 025: iload 4
      // 027: newarray 5
      // 029: astore 5
      // 02b: aload 3
      // 02c: sipush 500
      // 02f: invokevirtual java/io/InputStreamReader.mark (I)V
      // 032: aload 3
      // 033: aload 5
      // 035: invokevirtual java/io/Reader.read ([C)I
      // 038: pop
      // 039: new java/lang/Object
      // 03c: dup
      // 03d: aload 5
      // 03f: invokespecial java/lang/String.<init> ([C)V
      // 042: astore 6
      // 044: aload 6
      // 046: ldc_w "VCARD"
      // 049: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 04c: bipush -1
      // 04e: if_icmpeq 097
      // 051: aload 3
      // 052: invokevirtual java/io/InputStreamReader.reset ()V
      // 055: new net/rim/blackberry/api/pdap/ContactImpl
      // 058: dup
      // 059: invokespecial net/rim/blackberry/api/pdap/ContactImpl.<init> ()V
      // 05c: astore 7
      // 05e: new net/rim/blackberry/api/pdap/ContactVCardProvider
      // 061: dup
      // 062: aload 7
      // 064: invokespecial net/rim/blackberry/api/pdap/ContactVCardProvider.<init> (Lnet/rim/blackberry/api/pdap/ContactImpl;)V
      // 067: astore 8
      // 069: new java/lang/Object
      // 06c: dup
      // 06d: aload 8
      // 06f: aload 1
      // 070: aload 2
      // 071: invokespecial net/rim/device/apps/internal/api/serialformats/VCardReader.<init> (Lnet/rim/device/apps/internal/api/serialformats/VCardProvider;Ljava/io/InputStream;Ljava/lang/String;)V
      // 074: astore 9
      // 076: aload 9
      // 078: invokevirtual net/rim/device/apps/internal/api/serialformats/VCardReader.parseIt ()Lnet/rim/device/apps/internal/api/serialformats/VCardProvider;
      // 07b: pop
      // 07c: goto 08d
      // 07f: astore 10
      // 081: new javax/microedition/pim/PIMException
      // 084: dup
      // 085: ldc_w "Unable to read VCard."
      // 088: bipush 1
      // 089: invokespecial javax/microedition/pim/PIMException.<init> (Ljava/lang/String;I)V
      // 08c: athrow
      // 08d: bipush 1
      // 08e: anewarray 493
      // 091: dup
      // 092: bipush 0
      // 093: aload 7
      // 095: aastore
      // 096: areturn
      // 097: aload 6
      // 099: ldc_w "VCALENDAR"
      // 09c: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 09f: bipush -1
      // 0a1: if_icmpne 0a7
      // 0a4: goto 1a5
      // 0a7: bipush 6
      // 0a9: newarray 5
      // 0ab: astore 7
      // 0ad: aload 3
      // 0ae: invokevirtual java/io/InputStreamReader.read ()I
      // 0b1: istore 8
      // 0b3: iload 8
      // 0b5: bipush 58
      // 0b7: if_icmpeq 0bd
      // 0ba: goto 17f
      // 0bd: aload 3
      // 0be: aload 7
      // 0c0: invokevirtual java/io/Reader.read ([C)I
      // 0c3: pop
      // 0c4: new java/lang/Object
      // 0c7: dup
      // 0c8: aload 7
      // 0ca: invokespecial java/lang/String.<init> ([C)V
      // 0cd: astore 9
      // 0cf: aload 9
      // 0d1: ldc_w "VEVENT"
      // 0d4: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 0d7: bipush -1
      // 0d9: if_icmpeq 128
      // 0dc: aload 3
      // 0dd: invokevirtual java/io/InputStreamReader.reset ()V
      // 0e0: new net/rim/blackberry/api/pdap/EventImpl
      // 0e3: dup
      // 0e4: bipush 1
      // 0e5: invokespecial net/rim/blackberry/api/pdap/EventImpl.<init> (I)V
      // 0e8: astore 10
      // 0ea: new net/rim/blackberry/api/pdap/EventICalendarProvider
      // 0ed: dup
      // 0ee: aload 10
      // 0f0: invokespecial net/rim/blackberry/api/pdap/EventICalendarProvider.<init> (Lnet/rim/blackberry/api/pdap/EventImpl;)V
      // 0f3: astore 11
      // 0f5: new java/lang/Object
      // 0f8: dup
      // 0f9: aload 11
      // 0fb: aload 1
      // 0fc: aload 2
      // 0fd: invokespecial net/rim/device/apps/internal/api/serialformats/ICalendarReader.<init> (Lnet/rim/device/apps/internal/api/serialformats/ICalendarProvider;Ljava/io/InputStream;Ljava/lang/String;)V
      // 100: astore 12
      // 102: aload 12
      // 104: invokevirtual net/rim/device/apps/internal/api/serialformats/ICalendarReader.parseIt ()Lnet/rim/device/apps/internal/api/serialformats/ICalendarProvider;
      // 107: pop
      // 108: aload 11
      // 10a: invokevirtual net/rim/blackberry/api/pdap/EventICalendarProvider.setRepeat ()V
      // 10d: goto 11e
      // 110: astore 13
      // 112: new javax/microedition/pim/PIMException
      // 115: dup
      // 116: ldc_w "Unable to read VCalendar."
      // 119: bipush 1
      // 11a: invokespecial javax/microedition/pim/PIMException.<init> (Ljava/lang/String;I)V
      // 11d: athrow
      // 11e: bipush 1
      // 11f: anewarray 583
      // 122: dup
      // 123: bipush 0
      // 124: aload 10
      // 126: aastore
      // 127: areturn
      // 128: aload 9
      // 12a: ldc_w "VTODO"
      // 12d: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 130: bipush -1
      // 132: if_icmpeq 17f
      // 135: aload 3
      // 136: invokevirtual java/io/InputStreamReader.reset ()V
      // 139: bipush 1
      // 13a: invokestatic net/rim/blackberry/api/pdap/ToDoFactory.createToDo (I)Ljavax/microedition/pim/ToDo;
      // 13d: astore 10
      // 13f: aload 10
      // 141: ifnonnull 146
      // 144: aconst_null
      // 145: areturn
      // 146: new net/rim/blackberry/api/pdap/ToDoICalendarProvider
      // 149: dup
      // 14a: aload 10
      // 14c: invokespecial net/rim/blackberry/api/pdap/ToDoICalendarProvider.<init> (Ljavax/microedition/pim/ToDo;)V
      // 14f: astore 11
      // 151: new java/lang/Object
      // 154: dup
      // 155: aload 11
      // 157: aload 1
      // 158: aload 2
      // 159: invokespecial net/rim/device/apps/internal/api/serialformats/ICalendarReader.<init> (Lnet/rim/device/apps/internal/api/serialformats/ICalendarProvider;Ljava/io/InputStream;Ljava/lang/String;)V
      // 15c: astore 12
      // 15e: aload 12
      // 160: invokevirtual net/rim/device/apps/internal/api/serialformats/ICalendarReader.parseIt ()Lnet/rim/device/apps/internal/api/serialformats/ICalendarProvider;
      // 163: pop
      // 164: goto 175
      // 167: astore 13
      // 169: new javax/microedition/pim/PIMException
      // 16c: dup
      // 16d: ldc_w "Unable to read VCalendar."
      // 170: bipush 1
      // 171: invokespecial javax/microedition/pim/PIMException.<init> (Ljava/lang/String;I)V
      // 174: athrow
      // 175: bipush 1
      // 176: anewarray 637
      // 179: dup
      // 17a: bipush 0
      // 17b: aload 10
      // 17d: aastore
      // 17e: areturn
      // 17f: iload 8
      // 181: iflt 187
      // 184: goto 0ad
      // 187: goto 1a5
      // 18a: astore 6
      // 18c: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 18f: aload 6
      // 191: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // 194: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 197: goto 1a5
      // 19a: astore 6
      // 19c: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 19f: ldc_w "Serial format not supported."
      // 1a2: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 1a5: new javax/microedition/pim/PIMException
      // 1a8: dup
      // 1a9: invokespecial javax/microedition/pim/PIMException.<init> ()V
      // 1ac: athrow
      // try (57 -> 60): 61 null
      // try (124 -> 129): 130 null
      // try (170 -> 173): 174 null
      // try (22 -> 74): 192 null
      // try (75 -> 143): 192 null
      // try (144 -> 157): 192 null
      // try (158 -> 187): 192 null
      // try (188 -> 191): 192 null
      // try (22 -> 74): 198 null
      // try (75 -> 143): 198 null
      // try (144 -> 157): 198 null
      // try (158 -> 187): 198 null
      // try (188 -> 191): 198 null
   }

   @Override
   public void toSerialFormat(PIMItem item, OutputStream os, String enc, String dataFormat) throws PIMException {
      assertPermission();
      if (item != null && os != null && dataFormat != null) {
         String encoding = enc;
         if (encoding == null) {
            encoding = "UTF-8";
         }

         if (!Helper.isSupportedEncoding(encoding)) {
            throw new Object();
         }

         if (item instanceof Contact) {
            int vcardversion = 2;
            if (dataFormat.equals(VCARD_VERSION_2_1)) {
               vcardversion = 1;
            } else if (!dataFormat.equals(VCARD_VERSION_3_0)) {
               throw new Object(((StringBuffer)(new Object("VCard version "))).append(dataFormat).append(" not supported.").toString());
            }

            ContactVCardProvider vcard = new ContactVCardProvider((ContactImpl)item);
            vcard.setVersion(vcardversion);
            VCardWriter vcardWriter = (VCardWriter)(new Object(vcard, os, encoding));

            try {
               vcardWriter.encodeVCard();
            } finally {
               throw new PIMException("Unable to write VCard.", 1);
            }
         } else if (item instanceof Event) {
            if (!isValidSerialVersion(dataFormat)) {
               throw new Object(((StringBuffer)(new Object("VCal version "))).append(dataFormat).append(" not supported.").toString());
            }

            try {
               EventICalendarProvider vCal = new EventICalendarProvider((EventImpl)item);
               vCal.setVersion(dataFormat);
               if (dataFormat.equals("VCALENDAR/2.0")) {
                  vCal.setAlarmNestedBeginTag(-1770502245);
               }

               ICalendarWriter icalWriter = (ICalendarWriter)(new Object(vCal, os, encoding));
               icalWriter.encodeICalendar();
            } finally {
               throw new PIMException("Unable to write VCal.", 1);
            }
         } else if (item instanceof ToDo) {
            if (!isValidSerialVersion(dataFormat)) {
               throw new Object(((StringBuffer)(new Object("VCal version "))).append(dataFormat).append(" not supported.").toString());
            }

            try {
               ToDoICalendarProvider vCal = new ToDoICalendarProvider((ToDo)item);
               vCal.setVersion(dataFormat);
               ICalendarWriter icalWriter = (ICalendarWriter)(new Object(vCal, os, encoding));
               icalWriter.encodeICalendar();
            } finally {
               throw new PIMException("Unable to write VCal.", 1);
            }
         }
      } else {
         throw new Object();
      }
   }

   private static boolean isValidSerialVersion(String version) {
      for (int i = VCAL_VERSIONS.length - 1; i >= 0; i--) {
         if (VCAL_VERSIONS[i].equals(version)) {
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
            return VCARD_VERSIONS;
         case 2:
            return VCAL_VERSIONS;
         case 3:
            return VCAL_VERSIONS;
      }
   }

   private static boolean isValidMode(int mode) {
      return mode >= 1 && mode <= 3;
   }
}
