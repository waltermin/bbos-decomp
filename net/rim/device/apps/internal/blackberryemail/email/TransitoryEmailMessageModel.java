package net.rim.device.apps.internal.blackberryemail.email;

import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.apps.api.framework.model.RIMModel;

public class TransitoryEmailMessageModel implements RIMModel, ReadableList {
   protected EmailMessageModel _message;
   protected Vector _additionalItems = (Vector)(new Object());

   public void setModel(EmailMessageModel message) {
      this._message = message;
      this._additionalItems.removeAllElements();
   }

   public EmailMessageModel getModel() {
      return this._message;
   }

   @Override
   public Object getAt(int index) {
      int size = this._message.size();
      return index < size ? this._message.getAt(index) : this._additionalItems.elementAt(index - size);
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public int getIndex(Object element) {
      return ReadableListUtil.getIndex(element, this);
   }

   @Override
   public int size() {
      return this._message.size() + this._additionalItems.size();
   }
}
