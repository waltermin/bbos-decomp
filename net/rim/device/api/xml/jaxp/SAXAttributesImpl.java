package net.rim.device.api.xml.jaxp;

import net.rim.device.api.util.IntHashtable;
import org.xml.sax.Attributes;

class SAXAttributesImpl implements Attributes, RIMExtendedAttributes {
   private String[] _attributes;
   private int _numAttributes;
   private int _numElements;
   private IntHashtable _opaqueData;
   private static final int URI = 0;
   private static final int LOCALNAME = 1;
   private static final int QNAME = 2;
   private static final int TYPE = 3;
   private static final int VALUE = 4;
   private static final int ISDEFAULT = 5;
   private static final int NUM_ELEMENTS_PER_ATTRIBUTE = 6;
   private static final int ATTRIBUTES_INITIAL_SIZE = 4;

   public void addAttribute(String uri, String localName, String qName, String type, String value, boolean isDefault) {
      int startIndex = this._numElements;
      this._numAttributes++;
      this._numElements += 6;
      if (this._attributes == null) {
         this._attributes = new Object[24];
      } else if (this._numElements > this._attributes.length) {
         int len = this._attributes.length;
         this._attributes = ArrayResize.stringArrayResize(this._attributes, len + len / 2);
      }

      this._attributes[startIndex + 0] = uri;
      this._attributes[startIndex + 1] = localName;
      this._attributes[startIndex + 2] = qName;
      this._attributes[startIndex + 3] = type;
      this._attributes[startIndex + 4] = value;
      int var10001 = startIndex + 5;
      if (isDefault) {
         this._attributes[var10001] = "";
      } else {
         this._attributes[var10001] = null;
      }
   }

   public void addAttribute(String uri, String localName, String qName, String type, String value, boolean isDefault, byte[][] opaqueData) {
      this.addAttribute(uri, localName, qName, type, value, isDefault);
      if (opaqueData != null) {
         int index = this._numAttributes - 1;
         if (this._opaqueData == null) {
            this._opaqueData = (IntHashtable)(new Object());
         }

         this._opaqueData.put(index, new SAXAttributesImpl$ByteArrayArray(opaqueData));
      }
   }

   public byte[][] getOpaqueData(int index) {
      if (this._opaqueData != null) {
         SAXAttributesImpl$ByteArrayArray data = (SAXAttributesImpl$ByteArrayArray)this._opaqueData.get(index);
         if (data != null) {
            return data.getArray();
         }
      }

      return (byte[][])null;
   }

   public byte[][] getOpaqueData(String qName) {
      int index = this.getIndex(qName);
      return index == -1 ? (byte[][])null : this.getOpaqueData(index);
   }

   void setURI(int index, String uri) {
      this._attributes[index * 6 + 0] = uri;
   }

   public byte[][] getOpaqueData(String uri, String localName) {
      int index = this.getIndex(uri, localName);
      return index == -1 ? (byte[][])null : this.getOpaqueData(index);
   }

   @Override
   public boolean isDefault(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/xml/jaxp/SAXAttributesImpl._attributes [Ljava/lang/String;
      // 04: iload 1
      // 05: bipush 6
      // 07: imul
      // 08: bipush 5
      // 0a: iadd
      // 0b: aaload
      // 0c: ifnull 13
      // 0f: bipush 1
      // 10: goto 14
      // 13: bipush 0
      // 14: ireturn
      // 15: astore 2
      // 16: bipush 0
      // 17: ireturn
      // 18: astore 2
      // 19: bipush 0
      // 1a: ireturn
      // try (0 -> 12): 13 null
      // try (0 -> 12): 16 null
   }

   @Override
   public String getQName(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/xml/jaxp/SAXAttributesImpl._attributes [Ljava/lang/String;
      // 04: iload 1
      // 05: bipush 6
      // 07: imul
      // 08: bipush 2
      // 0a: iadd
      // 0b: aaload
      // 0c: areturn
      // 0d: astore 2
      // 0e: aconst_null
      // 0f: areturn
      // 10: astore 2
      // 11: aconst_null
      // 12: areturn
      // try (0 -> 8): 9 null
      // try (0 -> 8): 12 null
   }

   @Override
   public String getType(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/xml/jaxp/SAXAttributesImpl._attributes [Ljava/lang/String;
      // 04: iload 1
      // 05: bipush 6
      // 07: imul
      // 08: bipush 3
      // 0a: iadd
      // 0b: aaload
      // 0c: areturn
      // 0d: astore 2
      // 0e: aconst_null
      // 0f: areturn
      // 10: astore 2
      // 11: aconst_null
      // 12: areturn
      // try (0 -> 8): 9 null
      // try (0 -> 8): 12 null
   }

   @Override
   public String getValue(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/xml/jaxp/SAXAttributesImpl._attributes [Ljava/lang/String;
      // 04: iload 1
      // 05: bipush 6
      // 07: imul
      // 08: bipush 4
      // 0a: iadd
      // 0b: aaload
      // 0c: areturn
      // 0d: astore 2
      // 0e: aconst_null
      // 0f: areturn
      // 10: astore 2
      // 11: aconst_null
      // 12: areturn
      // try (0 -> 8): 9 null
      // try (0 -> 8): 12 null
   }

   @Override
   public int getIndex(String uri, String localName) {
      int index = 0;

      for (int i = 0; i < this._numElements; index++) {
         if (this._attributes[i + 0].equals(uri) && this._attributes[i + 1].equals(localName)) {
            return index;
         }

         i += 6;
      }

      return -1;
   }

   @Override
   public int getIndex(String qName) {
      int index = 0;

      for (int i = 0; i < this._numElements; index++) {
         if (this._attributes[i + 2].equals(qName)) {
            return index;
         }

         i += 6;
      }

      return -1;
   }

   @Override
   public String getType(String uri, String localName) {
      int index = this.getIndex(uri, localName);
      return index == -1 ? null : this.getType(index);
   }

   @Override
   public String getType(String qName) {
      int index = this.getIndex(qName);
      return index == -1 ? null : this.getType(index);
   }

   @Override
   public String getValue(String uri, String localName) {
      int index = this.getIndex(uri, localName);
      return index == -1 ? null : this.getValue(index);
   }

   @Override
   public String getValue(String qName) {
      int index = this.getIndex(qName);
      return index == -1 ? null : this.getValue(index);
   }

   @Override
   public String getLocalName(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/xml/jaxp/SAXAttributesImpl._attributes [Ljava/lang/String;
      // 04: iload 1
      // 05: bipush 6
      // 07: imul
      // 08: bipush 1
      // 09: iadd
      // 0a: aaload
      // 0b: areturn
      // 0c: astore 2
      // 0d: aconst_null
      // 0e: areturn
      // 0f: astore 2
      // 10: aconst_null
      // 11: areturn
      // try (0 -> 8): 9 null
      // try (0 -> 8): 12 null
   }

   @Override
   public String getURI(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/xml/jaxp/SAXAttributesImpl._attributes [Ljava/lang/String;
      // 04: iload 1
      // 05: bipush 6
      // 07: imul
      // 08: bipush 0
      // 09: iadd
      // 0a: aaload
      // 0b: areturn
      // 0c: astore 2
      // 0d: aconst_null
      // 0e: areturn
      // 0f: astore 2
      // 10: aconst_null
      // 11: areturn
      // try (0 -> 8): 9 null
      // try (0 -> 8): 12 null
   }

   @Override
   public int getLength() {
      return this._numAttributes;
   }
}
