package net.rim.device.api.ldap;

import java.util.Enumeration;
import net.rim.vm.Array;

class RIMLDAPAttribute implements LDAPAttribute {
   private int _size;
   private String _valueName;
   private Object[] _valueList;

   public RIMLDAPAttribute(String valueName) {
      if (valueName != null && valueName.length() != 0) {
         this._valueName = valueName;
         this._valueList = new Object[0];
      } else {
         throw new Object();
      }
   }

   public void addValue(Object newValue) {
      if (newValue == null) {
         throw new Object();
      }

      this._size++;
      Array.resize(this._valueList, this._size);
      this._valueList[this._size - 1] = newValue;
   }

   @Override
   public int getSize() {
      return this._size;
   }

   @Override
   public String getName() {
      return this._valueName;
   }

   @Override
   public Object getValue(int index) {
      return index >= 0 && index < this._valueList.length ? this._valueList[index] : null;
   }

   @Override
   public Enumeration getValues() {
      return (Enumeration)(new Object(this._valueList));
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof LDAPAttribute)) {
         return false;
      }

      LDAPAttribute attribute = (LDAPAttribute)obj;
      if (!this.getName().equals(attribute.getName())) {
         return false;
      }

      if (this.getSize() != attribute.getSize()) {
         return false;
      }

      int size = this.getSize();

      for (int i = 0; i < size; i++) {
         Object value1 = this.getValue(i);
         Object value2 = attribute.getValue(i);
         if (!value1.equals(value2)) {
            return false;
         }
      }

      return true;
   }
}
