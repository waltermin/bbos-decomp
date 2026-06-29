package net.rim.device.apps.internal.lbs.content;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.rim.device.api.util.DataBuffer;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class LocationDocumentConverter extends DefaultHandler {
   public static final int PATHTYPE_LINE = 1;
   public static final int PATHTYPE_POINTS = 2;
   public static final int PROFILETYPE_TIME = 1;
   public static final int PROFILETYPE_ALTITUDE = 2;
   public static final int PROFILETYPE_HEARTRATE = 3;

   public static int readCompressedSignedInt(DataBuffer db) {
      int i = 0;
      int used = 0;
      byte b = db.readByte();
      boolean negative = (b & 64) != 0;
      i = b & 63;
      if ((b & 128) == 0) {
         return negative ? -i : i;
      }

      used++;
      i <<= 7;

      while (true) {
         b = db.readByte();
         i |= b & 127;
         if ((b & 128) == 0) {
            if (negative) {
               return -i;
            }

            return i;
         }

         used++;
         if (used > 4 || used == 4 && (i & 234881024) != 0) {
            throw new NumberFormatException();
         }

         i <<= 7;
      }
   }

   public static void writeCompressedSignedInt(DataBuffer db, int i) {
      boolean negative = i < 0;
      if (negative) {
         i = -i;
      }

      if (i < 64) {
         db.writeByte(negative ? i | 64 : i);
      } else if (i < 8192) {
         db.writeByte((negative ? 192 : 128) | i >> 7);
         db.writeByte(i & 127);
      } else if (i < 1048576) {
         db.writeByte((negative ? 192 : 128) | i >> 14);
         db.writeByte(128 | i >> 7);
         db.writeByte(i & 127);
      } else if (i < 134217728) {
         db.writeByte((negative ? 192 : 128) | i >> 21);
         db.writeByte(128 | i >> 14);
         db.writeByte(128 | i >> 7);
         db.writeByte(i & 127);
      } else {
         db.writeByte((negative ? 192 : 128) | i >> 28);
         db.writeByte(128 | i >> 21);
         db.writeByte(128 | i >> 14);
         db.writeByte(128 | i >> 7);
         db.writeByte(i & 127);
      }
   }

   protected static int startElement(DataBuffer db, byte tag) {
      db.writeInt(tag << 24);
      return db.getPosition();
   }

   public static void endElement(DataBuffer db, int marker) {
      int end = db.getPosition();
      int size = end - marker;
      db.setPosition(marker - 3);
      db.writeByte(size >> 14 & 127 | 128);
      db.writeByte(size >> 7 & 127 | 128);
      db.writeByte(size & 127);
      db.setPosition(end);
   }

   protected static void write(DataBuffer db, int tag, String value) {
      if (value != null) {
         try {
            db.writeByte(tag);
            byte[] valueArray = value.getBytes("UTF-8");
            db.writeCompressedInt(valueArray.length);
            db.write(valueArray);
         } finally {
            return;
         }
      }
   }

   protected static void write(DataBuffer db, int tag, int value) {
      db.writeByte(tag);
      if (value > -128 && value < 128) {
         db.writeByte(1);
         db.writeByte(value);
      } else if (value > -32768 && value < 32768) {
         db.writeByte(2);
         db.writeShort(value);
      } else {
         db.writeByte(4);
         db.writeInt(value);
      }
   }

   protected static void write(DataBuffer db, int tag, double value) {
      db.writeByte(tag);
      db.writeByte(8);

      try {
         db.writeDouble(value);
      } finally {
         return;
      }
   }

   protected static void write(DataBuffer db, int tag, byte[] data) {
      db.writeByte(tag);
      db.writeCompressedInt(data.length);
      db.write(data);
   }

   protected static void write(DataBuffer db, int tag, IntArray intArray) {
      write(db, tag, intArray.getBytes());
   }

   public static int startRoute(DataBuffer db, int distance, int time, String routeName, String folderHeirarchy) {
      int marker = startElement(db, (byte)1);
      write(db, 1, distance);
      write(db, 2, time);
      write(db, 3, routeName);
      write(db, 4, folderHeirarchy);
      return marker;
   }

   public static void writeLocation(
      DataBuffer db,
      int x,
      int y,
      int zoom,
      String label,
      String description,
      String address,
      String city,
      String region,
      String country,
      String postalCode,
      String phone,
      String fax,
      String url,
      String email,
      String categories,
      double rating,
      String source,
      String sponsored,
      String folderHeirarchy
   ) {
      int marker = startElement(db, (byte)2);
      write(db, 1, x);
      write(db, 2, y);
      write(db, 3, zoom);
      write(db, 4, label);
      write(db, 5, description);
      write(db, 6, address);
      write(db, 7, city);
      write(db, 8, region);
      write(db, 9, country);
      write(db, 10, postalCode);
      write(db, 11, phone);
      write(db, 12, fax);
      write(db, 13, url);
      write(db, 14, email);
      write(db, 15, categories);
      write(db, 16, rating);
      write(db, 17, source);
      write(db, 18, sponsored);
      write(db, 19, folderHeirarchy);
      endElement(db, marker);
   }

   public static void writePath(DataBuffer db, IntArray d, int type, String id, String layer, String stroke, int strokeWidth, double opacity) {
      int marker = startElement(db, (byte)4);
      write(db, 1, d);
      write(db, 2, type);
      write(db, 3, id);
      write(db, 4, layer);
      write(db, 5, stroke);
      write(db, 6, strokeWidth);
      write(db, 7, opacity);
      endElement(db, marker);
   }

   public static void writeInstruction(
      DataBuffer db,
      int x,
      int y,
      int action,
      int actionInfo,
      String name,
      int distance,
      String address,
      String description,
      String exit,
      String connector,
      String towards
   ) {
      int marker = startElement(db, (byte)5);
      write(db, 1, x);
      write(db, 2, y);
      write(db, 3, action);
      write(db, 4, actionInfo);
      write(db, 5, name);
      write(db, 6, distance);
      write(db, 7, address);
      write(db, 8, description);
      write(db, 9, exit);
      write(db, 10, connector);
      write(db, 11, towards);
      endElement(db, marker);
   }

   public static void writeProfile(DataBuffer db, IntArray d, int type) {
      int marker = startElement(db, (byte)6);
      write(db, 1, d);
      write(db, 2, type);
      endElement(db, marker);
   }

   protected boolean testTag(DataBuffer db, int end, int tag) {
      if (db.getPosition() == end) {
         return false;
      } else {
         int nextTag = db.readByte();
         if (nextTag != tag) {
            db.setPosition(db.getPosition() - 1);
            return false;
         } else {
            return true;
         }
      }
   }

   protected int readint(DataBuffer db, int end, int tag) {
      if (this.testTag(db, end, tag)) {
         int tlength = db.readCompressedInt();
         switch (tlength) {
            case 0:
            case 3:
               break;
            case 1:
            default:
               return db.readByte();
            case 2:
               return db.readShort();
            case 4:
               return db.readInt();
         }
      }

      return 0;
   }

   protected int readint(Attributes attributes, String name, String alias) {
      String value = attributes.getValue(name);
      if (value == null) {
         value = attributes.getValue(alias);
      }

      return value == null ? 0 : Integer.parseInt(value);
   }

   protected int readint(Attributes attributes, String name) {
      String value = attributes.getValue(name);
      return value == null ? 0 : Integer.parseInt(value);
   }

   protected String readString(DataBuffer db, int end, int tag) {
      if (this.testTag(db, end, tag)) {
         int length = db.readCompressedInt();

         try {
            String value = new String(db.getArray(), db.getPosition(), length, "UTF-8");
            db.skipBytes(length);
            return value;
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   protected String readString(Attributes attributes, String name, String alias) {
      String value = attributes.getValue(name);
      if (value == null) {
         value = attributes.getValue(alias);
      }

      return value;
   }

   protected String readString(Attributes attributes, String name) {
      return attributes.getValue(name);
   }

   protected double readdouble(DataBuffer db, int end, int tag) {
      double var10000;
      try {
         if (!this.testTag(db, end, tag)) {
            return (double)4607182418800017408L;
         }

         db.readCompressedInt();
         var10000 = db.readDouble();
      } finally {
         return (double)4607182418800017408L;
      }

      return var10000;
   }

   protected double readdouble(Attributes param1, String param2) {
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
      // 00: aload 1
      // 01: aload 2
      // 02: invokeinterface org/xml/sax/Attributes.getValue (Ljava/lang/String;)Ljava/lang/String; 2
      // 07: astore 3
      // 08: aload 3
      // 09: ifnonnull 12
      // 0c: nop
      // 0d: ldc2_w 4607182418800017408
      // 10: nop
      // 11: dreturn
      // 12: aload 3
      // 13: invokestatic java/lang/Double.parseDouble (Ljava/lang/String;)D
      // 16: nop
      // 17: dreturn
   }

   protected IntArray readIntArray(DataBuffer db, int end, int tag) {
      return this.testTag(db, end, tag) ? new IntArray(db) : null;
   }

   protected IntArray readIntArray(Attributes attributes, String name) {
      return new IntArray(attributes.getValue(name));
   }

   int readPathType(Attributes attributes, String name) {
      if (name.equals("line")) {
         return 1;
      } else {
         return name.equals("points") ? 2 : 0;
      }
   }

   public void parseTLE(DataBuffer db) {
      while (db.available() > 0) {
         this.handlePacket(db);
      }
   }

   private void handlePacket(DataBuffer db) {
      this.parseElements(db);
   }

   private void parseElements(DataBuffer db) {
      try {
         int tag = db.readByte();
         int length = db.readCompressedInt();
         int end = db.getPosition() + length;
         switch (tag) {
            case 1:
               this.onStartRoute(this.readint(db, end, 1), this.readint(db, end, 2), this.readString(db, end, 3), this.readString(db, end, 4));

               while (db.getPosition() < end) {
                  this.parseElements(db);
               }

               this.onEndRoute();
               break;
            case 2:
               this.onLocation(
                  this.readint(db, end, 1),
                  this.readint(db, end, 2),
                  this.readint(db, end, 3),
                  this.readString(db, end, 4),
                  this.readString(db, end, 5),
                  this.readString(db, end, 6),
                  this.readString(db, end, 7),
                  this.readString(db, end, 8),
                  this.readString(db, end, 9),
                  this.readString(db, end, 10),
                  this.readString(db, end, 11),
                  this.readString(db, end, 12),
                  this.readString(db, end, 13),
                  this.readString(db, end, 14),
                  this.readString(db, end, 15),
                  this.readdouble(db, end, 16),
                  this.readString(db, end, 17),
                  this.readString(db, end, 18),
                  this.readString(db, end, 19)
               );
               break;
            case 4:
               this.onPath(
                  this.readIntArray(db, end, 1),
                  this.readint(db, end, 2),
                  this.readString(db, end, 3),
                  this.readString(db, end, 4),
                  this.readString(db, end, 5),
                  this.readint(db, end, 6),
                  this.readdouble(db, end, 7)
               );
               break;
            case 5:
               this.onInstruction(
                  this.readint(db, end, 1),
                  this.readint(db, end, 2),
                  this.readint(db, end, 3),
                  this.readint(db, end, 4),
                  this.readString(db, end, 5),
                  this.readint(db, end, 6),
                  this.readString(db, end, 7),
                  this.readString(db, end, 8),
                  this.readString(db, end, 9),
                  this.readString(db, end, 10),
                  this.readString(db, end, 11)
               );
               break;
            case 6:
               this.onProfile(this.readIntArray(db, end, 1), this.readint(db, end, 2));
               break;
            case 17:
               this.onStartGetRoute();

               while (db.getPosition() < end) {
                  this.parseElements(db);
               }

               this.onEndGetRoute();
               break;
            case 18:
               this.onLegalNotice(this.readString(db, end, 1));
               break;
            case 19:
               this.onStartPoi(this.readString(db, end, 1), this.readString(db, end, 2));

               while (db.getPosition() < end) {
                  this.parseElements(db);
               }

               this.onEndPoi();
         }

         db.setPosition(end);
      } finally {
         return;
      }
   }

   public void parse(String contentType, Object content) {
      if (contentType.indexOf("application") == 0) {
         this.parseTLE(content);
      }

      this.parseXML(content);
   }

   void parseTLE(Object content) {
      DataBuffer db = null;
      if (!(content instanceof DataBuffer)) {
         if (content instanceof byte[]) {
            byte[] data = (byte[])content;
            db = new DataBuffer(data, 0, data.length, true);
         }
      } else {
         db = (DataBuffer)content;
      }

      if (db != null) {
         this.parseTLE(db);
      }
   }

   void parseXML(Object content) {
      if (content instanceof byte[]) {
         byte[] data = (byte[])content;
         this.parseXml(new ByteArrayInputStream(data));
      }

      if (content instanceof InputStream) {
         InputStream stream = (InputStream)content;
         this.parseXml(stream);
      }

      if (content instanceof String) {
         InputStream stream = new ByteArrayInputStream(((String)content).getBytes());
         this.parseXml(stream);
      }
   }

   public void parseXml(InputStream param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/xml/parsers/SAXParserFactory.newInstance ()Lnet/rim/device/api/xml/parsers/SAXParserFactory;
      // 03: astore 2
      // 04: aload 2
      // 05: invokevirtual net/rim/device/api/xml/parsers/SAXParserFactory.newSAXParser ()Lnet/rim/device/api/xml/parsers/SAXParser;
      // 08: astore 3
      // 09: aload 3
      // 0a: aload 1
      // 0b: aload 0
      // 0c: invokevirtual net/rim/device/api/xml/parsers/SAXParser.parse (Ljava/io/InputStream;Lorg/xml/sax/helpers/DefaultHandler;)V
      // 0f: return
      // 10: astore 2
      // 11: return
      // 12: astore 2
      // 13: return
      // 14: astore 2
      // 15: return
      // try (0 -> 9): 10 null
      // try (0 -> 9): 12 null
      // try (0 -> 9): 14 null
   }

   @Override
   public void characters(char[] ch, int start, int length) {
   }

   @Override
   public void endDocument() {
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (localName.equalsIgnoreCase("Route")) {
         this.onEndRoute();
      } else if (!localName.equalsIgnoreCase("Location")) {
         if (!localName.equalsIgnoreCase("Path")) {
            if (!localName.equalsIgnoreCase("Instruction")) {
               if (!localName.equalsIgnoreCase("Profile")) {
                  if (localName.equalsIgnoreCase("GetRoute")) {
                     this.onEndGetRoute();
                  } else if (!localName.equalsIgnoreCase("LegalNotice")) {
                     if (localName.equalsIgnoreCase("Poi")) {
                        this.onEndPoi();
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void error(SAXParseException e) {
   }

   @Override
   public void fatalError(SAXParseException e) throws SAXParseException {
      throw e;
   }

   @Override
   public void ignorableWhitespace(char[] ch, int start, int length) {
   }

   @Override
   public void notationDecl(String name, String publicId, String systemId) {
   }

   @Override
   public void processingInstruction(String target, String data) {
   }

   @Override
   public InputSource resolveEntity(String publicId, String systemId) {
      return null;
   }

   @Override
   public void setDocumentLocator(Locator locator) {
   }

   @Override
   public void startDocument() {
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (localName.equalsIgnoreCase("Route")) {
         this.onStartRoute(
            this.readint(attributes, "distance"),
            this.readint(attributes, "time"),
            this.readString(attributes, "routeName"),
            this.readString(attributes, "folderHeirarchy")
         );
      } else if (!localName.equalsIgnoreCase("Location")) {
         if (localName.equalsIgnoreCase("Path")) {
            this.onPath(
               this.readIntArray(attributes, "d"),
               this.readPathType(attributes, "type"),
               this.readString(attributes, "id"),
               this.readString(attributes, "layer"),
               this.readString(attributes, "stroke"),
               this.readint(attributes, "strokeWidth"),
               this.readdouble(attributes, "opacity")
            );
         } else if (!localName.equalsIgnoreCase("Instruction")) {
            if (localName.equalsIgnoreCase("Profile")) {
               this.onProfile(this.readIntArray(attributes, "d"), this.readPathType(attributes, "type"));
            } else if (localName.equalsIgnoreCase("GetRoute")) {
               this.onStartGetRoute();
            } else if (localName.equalsIgnoreCase("LegalNotice")) {
               this.onLegalNotice(this.readString(attributes, "label"));
            } else {
               if (localName.equalsIgnoreCase("Poi")) {
                  this.onStartPoi(this.readString(attributes, "id"), this.readString(attributes, "source"));
               }
            }
         } else {
            this.onInstruction(
               this.readint(attributes, "x", "lon"),
               this.readint(attributes, "y", "lat"),
               this.readint(attributes, "action", "a"),
               this.readint(attributes, "actionInfo", "i"),
               this.readString(attributes, "name", "n"),
               this.readint(attributes, "distance", "d"),
               this.readString(attributes, "address", "ad"),
               this.readString(attributes, "description", "de"),
               this.readString(attributes, "exit", "e"),
               this.readString(attributes, "connector", "c"),
               this.readString(attributes, "towards", "t")
            );
         }
      } else {
         this.onLocation(
            this.readint(attributes, "x", "lon"),
            this.readint(attributes, "y", "lat"),
            this.readint(attributes, "zoom"),
            this.readString(attributes, "label"),
            this.readString(attributes, "description"),
            this.readString(attributes, "address"),
            this.readString(attributes, "city"),
            this.readString(attributes, "region"),
            this.readString(attributes, "country"),
            this.readString(attributes, "postalCode"),
            this.readString(attributes, "phone"),
            this.readString(attributes, "fax"),
            this.readString(attributes, "url"),
            this.readString(attributes, "email"),
            this.readString(attributes, "categories"),
            this.readdouble(attributes, "rating"),
            this.readString(attributes, "source"),
            this.readString(attributes, "sponsored"),
            this.readString(attributes, "folderHeirarchy")
         );
      }
   }

   public void onStartRoute(int _1, int _2, String _3, String _4) {
      throw null;
   }

   public void onEndRoute() {
      throw null;
   }

   public void onLocation(
      int _1,
      int _2,
      int _3,
      String _4,
      String _5,
      String _6,
      String _7,
      String _8,
      String _9,
      String _10,
      String _11,
      String _12,
      String _13,
      String _14,
      String _15,
      double _16,
      String _18,
      String _19,
      String _20
   ) {
      throw null;
   }

   public void onPath(IntArray _1, int _2, String _3, String _4, String _5, int _6, double _7) {
      throw null;
   }

   public void onInstruction(int _1, int _2, int _3, int _4, String _5, int _6, String _7, String _8, String _9, String _10, String _11) {
      throw null;
   }

   public void onProfile(IntArray _1, int _2) {
      throw null;
   }

   public void onStartGetRoute() {
      throw null;
   }

   public void onEndGetRoute() {
      throw null;
   }

   public void onLegalNotice(String _1) {
      throw null;
   }

   public void onStartPoi(String _1, String _2) {
      throw null;
   }

   public void onEndPoi() {
      throw null;
   }
}
