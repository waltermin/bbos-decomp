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
   public static final byte LINGDATA_GENERIC_FORMAT;
   public static final byte LINGDATA_NGRAMM_FORMAT;
   public static final byte LINGDATA_FAST_FORMAT;
   public static final byte LINGDATA_SHORTCUTS_FORMAT;
   public static final byte LINGDATA_GENERIC_TYPE;
   public static final byte LINGDATA_GROUP_SPECIFIC_TYPE;
   public static final byte LINGDATA_AUXILIARY_TYPE;
   public static final byte LINGDATA_SLANG_TYPE;
   public static final byte LINGDATA_SUPPLEMENTARY1_TYPE;
   public static final byte LINGDATA_SUPPLEMENTARY2_TYPE;
   public static final byte LINGDATA_SUPPLEMENTARY3_TYPE;
   public static final byte LINGDATA_SUPPLEMENTARY4_TYPE;
   public static final byte LINGDATA_SUPPLEMENTARY5_TYPE;
   public static final byte LINGDATA_SUPPLEMENTARY6_TYPE;
   public static final byte LINGDATA_SUPPLEMENTARY7_TYPE;
   public static final int LINGDATA_LOAD_OK;
   public static final int LINGDATA_LOAD_ERROR;
   public static final int LINGDATA_LOAD_IGNORE;
   public static final int LINGDATA_UNLOAD_OK;
   public static final int LINGDATA_UNLOAD_ERROR;
   public static final int LINGDATA_UNLOAD_NOT_FOUND;

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
