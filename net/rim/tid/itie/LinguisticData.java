package net.rim.tid.itie;

public class LinguisticData {
   private byte[][][] _data;
   private int _id;
   private int _type;
   private int _version;
   private String _name;
   private String _diagnosticMessage;
   private String _codFile;
   LinguisticData _next;
   public static final byte LINGDATA_GENERIC_FORMAT = 1;
   public static final byte LINGDATA_NGRAMM_FORMAT = 2;
   public static final byte LINGDATA_FAST_FORMAT = 4;
   public static final byte LINGDATA_SHORTCUTS_FORMAT = 8;
   public static final byte LINGDATA_GENERIC_TYPE = 1;
   public static final byte LINGDATA_GROUP_SPECIFIC_TYPE = 2;
   public static final byte LINGDATA_AUXILIARY_TYPE = 3;
   public static final byte LINGDATA_SLANG_TYPE = 4;
   public static final byte LINGDATA_SUPPLEMENTARY1_TYPE = 5;
   public static final byte LINGDATA_SUPPLEMENTARY2_TYPE = 6;
   public static final byte LINGDATA_SUPPLEMENTARY3_TYPE = 7;
   public static final byte LINGDATA_SUPPLEMENTARY4_TYPE = 8;
   public static final byte LINGDATA_SUPPLEMENTARY5_TYPE = 9;
   public static final byte LINGDATA_SUPPLEMENTARY6_TYPE = 10;
   public static final byte LINGDATA_SUPPLEMENTARY7_TYPE = 11;
   public static final int LINGDATA_LOAD_OK = 1;
   public static final int LINGDATA_LOAD_ERROR = 2;
   public static final int LINGDATA_LOAD_IGNORE = 4;
   public static final int LINGDATA_UNLOAD_OK = 1;
   public static final int LINGDATA_UNLOAD_ERROR = 2;
   public static final int LINGDATA_UNLOAD_NOT_FOUND = 4;

   public LinguisticData(String name, int type, int version, byte[][][] data) {
      this(name, type, version, data, null);
   }

   public LinguisticData(String name, int type, int version, byte[][][] data, String diagnosticMessage) {
      this(name, type, version, data, null, null);
   }

   public LinguisticData(String name, int type, int version, byte[][][] data, String diagnosticMessage, String codFileName) {
      if (name != null && data != null) {
         this._name = name;
         this._data = data;
         this._type = type;
         this._version = version;
         this._diagnosticMessage = diagnosticMessage;
         this._codFile = codFileName;
      } else {
         throw new NullPointerException();
      }
   }

   public byte[][][] getData() {
      return this._data;
   }

   public int getType() {
      return this._type;
   }

   public int getVersion() {
      return this._version;
   }

   public String getName() {
      return this._name;
   }

   void setID(int aID) {
      this._id = aID;
   }

   public int getID() {
      return this._id;
   }

   void addToChain(LinguisticData data) {
      if (this._next != null) {
         this._next.addToChain(data);
      } else {
         this._next = data;
      }
   }

   String getCodFileName() {
      return this._codFile;
   }

   public String getDiagnosticMessage() {
      return this._diagnosticMessage;
   }
}
