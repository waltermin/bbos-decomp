package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.i18n.Locale;

class AbstractEntryPointDescriptor {
   private Locale _currentLocale;
   protected String _description;
   private String _bundleName;
   private int _resourceId;

   AbstractEntryPointDescriptor(String bundleName, int resourceId) {
      this.setResource(bundleName, resourceId);
   }

   protected final void setResource(String bundleName, int resourceId) {
      this._bundleName = bundleName;
      this._resourceId = resourceId;
   }

   protected String getDefaultDescription() {
      throw null;
   }

   protected synchronized String getDescription() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/i18n/Locale.getDefault ()Lnet/rim/device/api/i18n/Locale;
      // 03: astore 1
      // 04: aload 0
      // 05: getfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._currentLocale Lnet/rim/device/api/i18n/Locale;
      // 08: ifnonnull 10
      // 0b: aload 0
      // 0c: aload 1
      // 0d: putfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._currentLocale Lnet/rim/device/api/i18n/Locale;
      // 10: aload 0
      // 11: getfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._currentLocale Lnet/rim/device/api/i18n/Locale;
      // 14: aload 1
      // 15: if_acmpne 1f
      // 18: aload 0
      // 19: getfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._description Ljava/lang/String;
      // 1c: ifnonnull 4a
      // 1f: aload 0
      // 20: aload 1
      // 21: putfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._currentLocale Lnet/rim/device/api/i18n/Locale;
      // 24: aload 0
      // 25: aconst_null
      // 26: putfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._description Ljava/lang/String;
      // 29: aload 0
      // 2a: getfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._bundleName Ljava/lang/String;
      // 2d: ifnull 4a
      // 30: aload 0
      // 31: aload 0
      // 32: getfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._bundleName Ljava/lang/String;
      // 35: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (Ljava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 38: aload 0
      // 39: getfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._resourceId I
      // 3c: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 3f: putfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._description Ljava/lang/String;
      // 42: goto 4a
      // 45: astore 2
      // 46: goto 4a
      // 49: astore 2
      // 4a: aload 0
      // 4b: getfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._description Ljava/lang/String;
      // 4e: ifnull 56
      // 51: aload 0
      // 52: getfield net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor._description Ljava/lang/String;
      // 55: areturn
      // 56: aload 0
      // 57: invokevirtual net/rim/device/apps/internal/ribbon/launcher/AbstractEntryPointDescriptor.getDefaultDescription ()Ljava/lang/String;
      // 5a: areturn
      // try (24 -> 32): 33 null
      // try (24 -> 32): 35 null
   }

   protected void setDescription(String description) {
      this._description = description;
   }

   String getBundleName() {
      return this._bundleName;
   }

   int getResourceId() {
      return this._resourceId;
   }
}
