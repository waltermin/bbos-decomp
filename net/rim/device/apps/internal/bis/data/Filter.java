package net.rim.device.apps.internal.bis.data;

public final class Filter {
   private String _id;
   private String _name;
   private int _sequenceId;
   private boolean _sendAlert;
   private boolean _levelOne;
   private boolean _headersOnly;
   private String _operator;
   private String _value;
   public static final String FILTER_OPERATOR_NEW_MAIL;
   public static final String FILTER_OPERATOR_PRIORITY_MAIL;
   public static final String FILTER_OPERATOR_FROM;
   public static final String FILTER_OPERATOR_SUBJECT;
   public static final String FILTER_OPERATOR_TO;
   public static final String FILTER_OPERATOR_CC;

   public final String getId() {
      return this._id;
   }

   public final void setId(String filterId) {
      this._id = filterId;
   }

   public final String getName() {
      return this._name;
   }

   public final void setName(String filterName) {
      this._name = filterName;
   }

   public final void setSequenceId(int sequenceId) {
      this._sequenceId = sequenceId;
   }

   public final boolean getSendAlert() {
      return this._sendAlert;
   }

   public final void setSendAlert(boolean sendAlert) {
      this._sendAlert = sendAlert;
   }

   public final boolean getLevelOne() {
      return this._levelOne;
   }

   public final void setLevelOne(boolean levelOne) {
      this._levelOne = levelOne;
   }

   public final boolean getHeadersOnly() {
      return this._headersOnly;
   }

   public final void setHeadersOnly(boolean headersOnly) {
      this._headersOnly = headersOnly;
   }

   public final String getOperator() {
      return this._operator;
   }

   public final void setOperator(String filterOperator) {
      this._operator = filterOperator;
   }

   public final String getValue() {
      return this._value;
   }

   public final void setValue(String value) {
      this._value = value;
   }
}
