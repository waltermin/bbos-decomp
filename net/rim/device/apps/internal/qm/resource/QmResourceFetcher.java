package net.rim.device.apps.internal.qm.resource;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public class QmResourceFetcher {
   protected ResourceBundleFamily _rbf;
   protected ResourceBundle _primary;
   protected ResourceBundle _secondary;
   protected ResourceBundle _root;
   protected ResourceBundle _fallback;
   private int _localeCode;

   protected QmResourceFetcher(ResourceBundleFamily rbf) {
      this._rbf = rbf;
      this._fallback = this._rbf.getBundle(Locale.get(1701707776, "peer"));
   }

   protected void fetchResourceBundle() {
      Locale defLocale = Locale.getDefault();
      String language = defLocale.getLanguage();
      Locale fetchLocale = Locale.get(language, defLocale.getCountry(), "peer");
      this._primary = this._rbf.getBundle(fetchLocale);
      if (this._primary != null && this._primary.getLocale().getCode() != fetchLocale.getCode()) {
         this._primary = null;
      }

      fetchLocale = Locale.get(language, "", "peer");
      this._secondary = this._rbf.getBundle(fetchLocale);
      if (this._secondary != null && this._secondary.getLocale().getCode() != fetchLocale.getCode()) {
         this._secondary = null;
      }

      fetchLocale = Locale.get(language);
      this._root = this._rbf.getBundle(fetchLocale);
      if (this._root != null && this._root.getLocale().getCode() != fetchLocale.getCode()) {
         this._root = null;
      }

      this._localeCode = Locale.getDefault().getCode();
   }

   public String fetchString(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/qm/resource/QmResourceFetcher._localeCode I
      // 04: invokestatic net/rim/device/api/i18n/Locale.getDefault ()Lnet/rim/device/api/i18n/Locale;
      // 07: invokevirtual net/rim/device/api/i18n/Locale.getCode ()I
      // 0a: if_icmpeq 11
      // 0d: aload 0
      // 0e: invokevirtual net/rim/device/apps/internal/qm/resource/QmResourceFetcher.fetchResourceBundle ()V
      // 11: aload 0
      // 12: iload 1
      // 13: invokespecial net/rim/device/apps/internal/qm/resource/QmResourceFetcher.getObject (I)Ljava/lang/Object;
      // 16: checkcast java/lang/String
      // 19: areturn
      // 1a: astore 2
      // 1b: aconst_null
      // 1c: areturn
      // 1d: astore 2
      // 1e: aconst_null
      // 1f: areturn
      // try (7 -> 11): 12 null
      // try (7 -> 11): 15 null
   }

   private Object getObject(int id) {
      Object result = null;
      if (this._primary != null) {
         result = this._primary.getObject(id, false);
      }

      if (result == null && this._secondary != null) {
         result = this._secondary.getObject(id, false);
      }

      if (result == null && this._root != null) {
         result = this._root.getObject(id, false);
      }

      if (result == null && this._fallback != null) {
         result = this._fallback.getObject(id, false);
      }

      return result;
   }
}
