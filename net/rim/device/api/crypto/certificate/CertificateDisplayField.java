package net.rim.device.api.crypto.certificate;

import net.rim.device.api.ui.Field;

public class CertificateDisplayField {
   private String _label;
   private String _value;
   private Field _field;

   public CertificateDisplayField(String label, String value) {
      this._label = label;
      this._value = value;
   }

   public CertificateDisplayField(Field field) {
      this._field = field;
   }

   public String getLabel() {
      return this._label;
   }

   public String getValue() {
      return this._value;
   }

   public Field getField() {
      return this._field;
   }

   public boolean isFieldPresent() {
      return this._field != null;
   }
}
