package org.xml.sax.helpers;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class XMLReaderFactory {
   private XMLReaderFactory() {
   }

   public static final XMLReader createXMLReader() throws SAXException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: ldc_w "net.rim.device.api.xml.jaxp.XMLParser"
      // 03: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 06: astore 0
      // 07: aload 0
      // 08: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 0b: checkcast org/xml/sax/XMLReader
      // 0e: areturn
      // 0f: astore 0
      // 10: new org/xml/sax/SAXException
      // 13: dup
      // 14: aload 0
      // 15: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 18: invokespecial org/xml/sax/SAXException.<init> (Ljava/lang/String;)V
      // 1b: athrow
      // 1c: astore 0
      // 1d: new org/xml/sax/SAXException
      // 20: dup
      // 21: aload 0
      // 22: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 25: invokespecial org/xml/sax/SAXException.<init> (Ljava/lang/String;)V
      // 28: athrow
      // 29: astore 0
      // 2a: new org/xml/sax/SAXException
      // 2d: dup
      // 2e: aload 0
      // 2f: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 32: invokespecial org/xml/sax/SAXException.<init> (Ljava/lang/String;)V
      // 35: athrow
      // try (0 -> 6): 7 null
      // try (0 -> 6): 14 null
      // try (0 -> 6): 21 null
   }

   public static final XMLReader createXMLReader(String className) throws SAXException {
      throw new SAXException("Not supported");
   }
}
