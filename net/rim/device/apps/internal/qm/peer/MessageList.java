package net.rim.device.apps.internal.qm.peer;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.qm.peer.common.HintPollingThread;
import net.rim.device.internal.ui.component.Scrollbar;

final class MessageList extends Manager {
   private VerticalFieldManager _fields = (VerticalFieldManager)(new Object(3477060387306733568L));
   private Scrollbar _scrollbar = (Scrollbar)(new Object());
   private Hashtable _lookup = (Hashtable)(new Object());
   private static Tag TAG = Tag.create("bbmessenger-messagelist");
   private static final long VFM_STYLE = 3477060387306733568L;
   private static final int MAX_NUM_MESSAGES = 250;

   MessageList(ReadableList collection) {
      super(0);
      this.setTag(TAG);
      this.add(this._fields);
      this.add(this._scrollbar);
      this._scrollbar.setClient(this._fields);
      this.populate(collection);
   }

   final void populate(ReadableList collection) {
      int count = collection.size();
      int index = count >= 250 ? count - 250 : 0;
      synchronized (this) {
         for (; index < count; index++) {
            Object obj = collection.getAt(index);
            if (obj instanceof MessengerMessage) {
               Field messageField = ((MessengerMessage)obj).getField(null);
               this._fields.add(messageField);
               this._lookup.put(obj, messageField);
            }
         }
      }

      if (count > 0) {
         this.scrollToField(this._fields.getField(this._fields.getFieldCount() - 1));
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setExtent(width, height);
      this.setVirtualExtent(width, height);
      int remainingWidth = width - this._scrollbar.getPreferredWidth();
      if (this._fields != null) {
         this.layoutChild(this._fields, remainingWidth, height);
         this.setPositionChild(this._fields, 0, 0);
      }

      this.layoutChild(this._scrollbar, width, height);
      this.setPositionChild(this._scrollbar, remainingWidth, 0);
   }

   final void setInitialScroll() {
      int count = this._fields.getFieldCount();
      if (count > 0) {
         this.scrollToField(this._fields.getField(count - 1));
      }
   }

   final void append(RIMModel message) {
      if (message instanceof MessengerMessage) {
         Field messageField = ((MessengerMessage)message).getField(null);
         if (messageField != null) {
            if (this._fields.getFieldCount() >= 250) {
               Field last = this._fields.getField(0);
               this._lookup.remove(last);
               this._fields.delete(last);
            }

            this._fields.add(messageField);
            this._lookup.put(message, messageField);
            this.scrollToField(messageField);
         }
      }
   }

   final void remove(RIMModel message) {
      Object obj = this._lookup.get(message);
      if (obj instanceof Object) {
         this._fields.delete((Field)obj);
         this._lookup.remove(message);
      }
   }

   final void scrollToField(Field fieldToShow) {
      int scrollPos = fieldToShow.getTop() + fieldToShow.getHeight() - this.getHeight();
      if (scrollPos >= 0) {
         this._fields.setVerticalScroll(scrollPos);
      }
   }

   final void clear() {
      this._fields.deleteAll();
   }

   final int size() {
      return this._fields.getFieldCount();
   }

   final Object getAt(int index) {
      return index >= 0 && index <= this.size() - 1 ? this._fields.getField(index) : null;
   }

   final Field scroll(int flag) {
      Field field = null;
      int count = this._fields.getFieldCount();
      if (count > 0) {
         switch (flag) {
            case 140:
               break;
            case 141:
            default:
               field = this._fields.getField(0);
               break;
            case 142:
               field = this._fields.getField(count - 1);
         }

         if (field != null) {
            this.scrollToField(field);
         }
      }

      return field;
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      HintPollingThread.reset();
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int ret = super.moveFocus(amount, status, time);
      HintPollingThread.reset();
      return ret;
   }

   final void lock() {
      Enumeration enumeration = this._lookup.elements();

      while (enumeration.hasMoreElements()) {
         Object obj = enumeration.nextElement();
         if (obj instanceof MessageField) {
            ((MessageField)obj).update();
         }
      }
   }
}
