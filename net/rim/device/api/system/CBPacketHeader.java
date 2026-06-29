package net.rim.device.api.system;

public final class CBPacketHeader implements RadioPacketHeader {
   private boolean _embeddedLanguage;
   private boolean _compressedText;
   private int _language;
   private int _messageCoding;
   private int _messageClass;
   private int _geoScope;
   private int _messageCode;
   private int _updateNumber;
   private int _messageID;
   public static final int GEO_SCOPE_IMMEDIATE_CELL_WIDE = 0;
   public static final int GEO_SCOPE_NORMAL_PLMN_WIDE = 1;
   public static final int GEO_SCOPE_NORMAL_LOCATION_AREA_WIDE = 2;
   public static final int GEO_SCOPE_NORMAL_CELL_WIDE = 3;

   public CBPacketHeader() {
   }

   public CBPacketHeader(int messageID, int geoScope) {
      this._messageCoding = 0;
      this._messageID = messageID;
      this._geoScope = geoScope;
   }

   @Override
   public final void reset() {
   }

   public final boolean isEmbeddedLanguage() {
      return this._embeddedLanguage;
   }

   public final boolean isCompressedText() {
      return this._compressedText;
   }

   public final int getLanguage() {
      return this._language;
   }

   public final int getMessageCoding() {
      return this._messageCoding;
   }

   public final int getMessageClass() {
      return this._messageClass;
   }

   public final int getGeographicScope() {
      return this._geoScope;
   }

   public final int getCode() {
      return this._messageCode;
   }

   public final int getUpdateNumber() {
      return this._updateNumber;
   }

   public final int getMessageID() {
      return this._messageID;
   }
}
