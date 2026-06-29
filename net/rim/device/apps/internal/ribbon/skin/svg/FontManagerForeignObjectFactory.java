package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.apps.internal.ribbon.components.GlobalListenerFactoryHelper;

class FontManagerForeignObjectFactory {
   private GlobalListenerFactoryHelper _helper;
   private SAXParser _parser;
   private static FontManagerForeignObjectFactory _factory;

   public static FontManagerForeignObjectFactory getFactory() {
      if (_factory == null) {
         _factory = new FontManagerForeignObjectFactory();
      }

      return _factory;
   }

   private FontManagerForeignObjectFactory() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial java/lang/Object.<init> ()V
      // 04: aload 0
      // 05: invokestatic net/rim/device/api/xml/parsers/SAXParserFactory.newInstance ()Lnet/rim/device/api/xml/parsers/SAXParserFactory;
      // 08: invokevirtual net/rim/device/api/xml/parsers/SAXParserFactory.newSAXParser ()Lnet/rim/device/api/xml/parsers/SAXParser;
      // 0b: putfield net/rim/device/apps/internal/ribbon/skin/svg/FontManagerForeignObjectFactory._parser Lnet/rim/device/api/xml/parsers/SAXParser;
      // 0e: goto 16
      // 11: astore 1
      // 12: goto 16
      // 15: astore 1
      // 16: aload 0
      // 17: new net/rim/device/apps/internal/ribbon/components/GlobalListenerFactoryHelper
      // 1a: dup
      // 1b: invokespecial net/rim/device/apps/internal/ribbon/components/GlobalListenerFactoryHelper.<init> ()V
      // 1e: putfield net/rim/device/apps/internal/ribbon/skin/svg/FontManagerForeignObjectFactory._helper Lnet/rim/device/apps/internal/ribbon/components/GlobalListenerFactoryHelper;
      // 21: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 24: aload 0
      // 25: getfield net/rim/device/apps/internal/ribbon/skin/svg/FontManagerForeignObjectFactory._helper Lnet/rim/device/apps/internal/ribbon/components/GlobalListenerFactoryHelper;
      // 28: invokevirtual net/rim/device/api/system/Application.addGlobalEventListener (Lnet/rim/device/api/system/GlobalEventListener;)V
      // 2b: return
      // try (2 -> 6): 7 null
      // try (2 -> 6): 9 null
   }

   public FontManagerForeignObject createInstance(String xml) {
      FontManagerForeignObject instance = new FontManagerForeignObject(xml, this._parser);
      this._helper.addComponentForUpdate(instance);
      return instance;
   }
}
