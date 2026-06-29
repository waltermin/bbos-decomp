package net.rim.device.apps.api.ribbon;

import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.props.BooleanProps;
import net.rim.device.apps.api.utility.props.CharProps;
import net.rim.device.apps.api.utility.props.IntegerProps;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.api.utility.props.StringProps;

public final class ApplicationEntryPoint implements EntryPointDescriptor, BooleanProps, IntegerProps, StringProps, ObjectProps, CharProps, Runnable {
   private ApplicationDescriptor _applicationDescriptor;
   private String _uniqueName;
   private String _propertiesName;
   private String _defaultDescription;
   private String _description;
   private Locale _localeOfDescription;
   private char _hotKey;
   private Integer _order;
   private Object _customIcon;
   private Object _customIconFocus;
   private String _state;
   private boolean _canHide = true;
   private String _descriptionOverride;
   private static final long PROPERTIES_OVERRIDE_ID_KEY = 800229957454453956L;
   public static final long APP_DESCRIPTOR = -8880124975077471920L;

   public final ApplicationDescriptor getApplicationDescriptor() {
      return this._applicationDescriptor;
   }

   public final void descriptionChanged() {
      this._localeOfDescription = null;
   }

   @Override
   public final String get(long propID, String defaultReturned) {
      if (propID == 1) {
         return this._uniqueName;
      } else if (propID == 3) {
         return this.getDescription();
      } else if (propID == 9) {
         return this._propertiesName != null ? this._propertiesName : this._uniqueName;
      } else {
         return propID == 2 ? this._state : defaultReturned;
      }
   }

   @Override
   public final void set(long propID, String valueToSet) {
      if (propID == 9) {
         this._propertiesName = valueToSet;
      } else if (propID == 3) {
         this._descriptionOverride = valueToSet;
      } else {
         if (propID == 2) {
            this._state = valueToSet;
         }
      }
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      if (propID == 5) {
         Bitmap icon = this._applicationDescriptor.getIcon();
         if (icon != null) {
            return icon;
         }
      } else if (propID == 4) {
         if (this._customIcon != null) {
            return this._customIcon;
         }
      } else if (propID == 10) {
         if (this._customIconFocus != null) {
            return this._customIconFocus;
         }
      } else if (propID == -8880124975077471920L) {
         return this._applicationDescriptor;
      }

      return defaultReturned;
   }

   @Override
   public final void set(long propID, Object valueToSet) {
      if (propID == 4) {
         this._customIcon = valueToSet;
      } else {
         if (propID == 10) {
            this._customIconFocus = valueToSet;
         }
      }
   }

   @Override
   public final Integer get(long propID, Integer defaultReturned) {
      return propID == 6 && this._order != null ? this._order : defaultReturned;
   }

   @Override
   public final void set(long propID, Integer valueToSet) {
   }

   @Override
   public final char get(long propID, char defaultReturned) {
      return propID == 8 && this._hotKey != 0 ? this._hotKey : defaultReturned;
   }

   @Override
   public final void set(long propID, char valueToSet) {
      if (propID == 8) {
         this._hotKey = valueToSet;
      }
   }

   @Override
   public final Boolean get(long propID, Boolean defaultReturned) {
      return (Boolean)(propID == 7 ? new Object(this._canHide) : defaultReturned);
   }

   @Override
   public final void set(long propID, Boolean valueToSet) {
      if (propID == 7) {
         this._canHide = valueToSet;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      try {
         ApplicationManager.getApplicationManager().runApplication(this._applicationDescriptor);
      } catch (Throwable var3) {
         throw new Object(e.getMessage());
      }
   }

   public static final String removeHotkeyFromDescription(String description) {
      int underlineIndex = description.indexOf("̲");
      if (underlineIndex != -1
         && underlineIndex + 1 == description.length() - 1
         && underlineIndex - 2 > 0
         && description.charAt(underlineIndex - 2) == '('
         && description.charAt(underlineIndex + 1) == ')') {
         description = description.substring(0, underlineIndex - 2);
      }

      return StringUtilities.removeChars(description, "̲");
   }

   public static final void setPropertyOverride(String uniqueName, long propertyId, Object property) {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      Hashtable hashNames = reg.getHashtable(800229957454453956L);
      if (hashNames != null) {
         hashNames.put(uniqueName, new ApplicationEntryPoint$ApplicationEntryPropertyOverride(propertyId, property));
      }
   }

   public ApplicationEntryPoint(ApplicationDescriptor ad) {
      this._applicationDescriptor = ad;
      String moduleName = ad.getModuleName();
      String name = ad.getName();
      StringBuffer buff = (StringBuffer)(new Object(moduleName.length() + name.length() + 1));
      buff.append(moduleName).append('.').append(name);
      this._uniqueName = buff.toString();
      this._defaultDescription = name;
      int order = ad.getPosition();
      if (order != 0) {
         this._order = (Integer)(new Object(order));
      }

      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      Hashtable hashNames = reg.getHashtable(800229957454453956L);
      if (hashNames != null && hashNames.containsKey(this._uniqueName)) {
         ApplicationEntryPoint$ApplicationEntryPropertyOverride override = (ApplicationEntryPoint$ApplicationEntryPropertyOverride)hashNames.get(
            this._uniqueName
         );
         if (override._id == 7) {
            this.set(override._id, (Boolean)override._value);
            return;
         }

         if (override._id == 3 || override._id == 9 || override._id == 2) {
            this.set(override._id, (String)override._value);
            return;
         }

         if (override._id == 4 || override._id == 10) {
            this.set(override._id, override._value);
            return;
         }

         if (override._id == 6) {
            this.set(override._id, (Integer)override._value);
         }
      }
   }

   private final String getDescription() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._descriptionOverride Ljava/lang/String;
      // 04: ifnull 0c
      // 07: aload 0
      // 08: getfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._descriptionOverride Ljava/lang/String;
      // 0b: areturn
      // 0c: invokestatic net/rim/device/api/i18n/Locale.getDefault ()Lnet/rim/device/api/i18n/Locale;
      // 0f: astore 1
      // 10: aload 0
      // 11: getfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._localeOfDescription Lnet/rim/device/api/i18n/Locale;
      // 14: aload 1
      // 15: if_acmpeq 8b
      // 18: aload 0
      // 19: aload 1
      // 1a: putfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._localeOfDescription Lnet/rim/device/api/i18n/Locale;
      // 1d: aload 0
      // 1e: getfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._applicationDescriptor Lnet/rim/device/api/system/ApplicationDescriptor;
      // 21: invokevirtual net/rim/device/api/system/ApplicationDescriptor.getNameResourceBundle ()Ljava/lang/String;
      // 24: astore 2
      // 25: aload 2
      // 26: ifnull 8b
      // 29: aload 0
      // 2a: aload 2
      // 2b: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (Ljava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 2e: aload 0
      // 2f: getfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._applicationDescriptor Lnet/rim/device/api/system/ApplicationDescriptor;
      // 32: invokevirtual net/rim/device/api/system/ApplicationDescriptor.getNameResourceId ()I
      // 35: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 38: putfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._description Ljava/lang/String;
      // 3b: goto 8b
      // 3e: astore 3
      // 3f: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 42: new java/lang/Object
      // 45: dup
      // 46: ldc_w "WARNING: i18n "
      // 49: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 4c: aload 0
      // 4d: getfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._uniqueName Ljava/lang/String;
      // 50: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 53: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 56: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 59: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 5c: aload 3
      // 5d: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 60: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 63: goto 8b
      // 66: astore 3
      // 67: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 6a: new java/lang/Object
      // 6d: dup
      // 6e: ldc_w "WARNING: i18n "
      // 71: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 74: aload 0
      // 75: getfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._uniqueName Ljava/lang/String;
      // 78: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 7b: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 7e: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 81: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 84: aload 3
      // 85: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 88: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 8b: aload 0
      // 8c: getfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._description Ljava/lang/String;
      // 8f: ifnull 97
      // 92: aload 0
      // 93: getfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._description Ljava/lang/String;
      // 96: areturn
      // 97: aload 0
      // 98: getfield net/rim/device/apps/api/ribbon/ApplicationEntryPoint._defaultDescription Ljava/lang/String;
      // 9b: areturn
      // try (21 -> 29): 30 null
      // try (21 -> 29): 46 null
   }
}
