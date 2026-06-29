package net.rim.device.internal.eventLogViewer;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.vm.EventLog;

public final class EventLoggerOptions extends MainScreen implements ListFieldCallback {
   private ObjectChoiceField _levelField;
   private ListField _list;
   private long[] _guids;
   private byte[] _flags;
   private EventLoggerContents _evlContents;
   private PersistentObject _persist;
   private boolean _filterFlagsDirty;
   private static final int MENU_CLOSE;
   private static final int MENU_SAVE;
   private static final int MENU_TOGGLE_FILTER;
   private static final int MENU_SET_ALL;
   private static final int MENU_CLEAR_ALL;
   private static final String SPACE;

   public EventLoggerOptions(EventLoggerContents evlContents, PersistentObject persist) {
      this._evlContents = evlContents;
      this._persist = persist;
      LongIntHashtable filterFlags = (LongIntHashtable)persist.getContents();
      ResourceBundle rb = this._evlContents._rb;
      this.setTitle((Field)(new Object(rb.getString(52))));
      String[] strs = new Object[3];
      String[] levelStrs = rb.getStringArray(70);
      strs[0] = levelStrs[EventLoggerContents.errorLevel2StringIndex(3)];
      strs[1] = levelStrs[EventLoggerContents.errorLevel2StringIndex(4)];
      strs[2] = levelStrs[EventLoggerContents.errorLevel2StringIndex(5)];
      this._levelField = (ObjectChoiceField)(new Object(rb.getString(15), strs, this.level2ChoiceIndex(EventLogger.getMinimumLevel())));
      this.add(this._levelField);
      this.add((Field)(new Object()));
      this.add((Field)(new Object(rb.getString(2), 36028797019226112L)));
      this._list = (ListField)(new Object());
      this._list.setCallback(this);
      this.add(this._list);
      this._guids = new long[filterFlags.size()];
      this._flags = new byte[this._guids.length];
      LongEnumeration enumeration = filterFlags.keys();

      for (int i = 0; i < this._guids.length; i++) {
         this._guids[i] = enumeration.nextElement();
         this._flags[i] = (byte)filterFlags.get(this._guids[i]);
      }

      this._list.setSize(this._guids.length);
      String[] temp = new Object[this._guids.length];

      for (int i = this._guids.length - 1; i >= 0; i--) {
         temp[i] = EventLog.getRegisteredAppName(this._guids[i]);
      }

      Arrays.sort(temp, 0, temp.length, this._guids, new EventLoggerOptions$StringComparator(null));
      this._filterFlagsDirty = false;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      ResourceBundle b = this._evlContents._rb;
      if (this.getLeafFieldWithFocus() == this._list && !this._list.isEmpty()) {
         menu.add(new EventLoggerOptions$ELMenu(this, b, 25));
         menu.add(new EventLoggerOptions$ELMenu(this, b, 27));
         menu.add(new EventLoggerOptions$ELMenu(this, b, 28));
         menu.addSeparator();
      }

      if (this._levelField.isDirty() || this._filterFlagsDirty) {
         menu.add(new EventLoggerOptions$ELMenu(this, CommonResource.getBundle(), 18));
         menu.addSeparator();
      }
   }

   @Override
   public final void save() {
      if (this._levelField.isDirty()) {
         EventLogger.setMinimumLevel(this.choiceIndex2Level(this._levelField.getSelectedIndex()));
         this._levelField.setDirty(false);
      }

      if (this._filterFlagsDirty) {
         LongIntHashtable hash = (LongIntHashtable)this._persist.getContents();

         for (int i = 0; i < this._guids.length; i++) {
            hash.put(this._guids[i], this._flags[i]);
         }

         this._persist.commit();
         this._filterFlagsDirty = false;
      }

      this._evlContents.optionsChanged();
   }

   @Override
   public final void close() {
      if (this._levelField.isDirty() || this._filterFlagsDirty) {
         int result = Dialog.ask(1, this._evlContents._rb.getString(60));
         if (result == -1) {
            return;
         }

         if (result == 1) {
            this.save();
         }
      }

      UiApplication.getUiApplication().popScreen(this);
   }

   final boolean toggleCurr() {
      if (this.getFieldWithFocus() == this._list && !this._list.isEmpty()) {
         int index = this._list.getSelectedIndex();
         this._flags[index] = (byte)(this._flags[index] ^ 1);
         this._list.invalidate(index);
         this._filterFlagsDirty = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
         case ' ':
            if (this.toggleCurr()) {
               return true;
            }
         default:
            return super.keyChar(key, status, time);
         case '\u001b':
            this.close();
            return true;
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      long guid = this._guids[index];
      char box;
      if (this._flags[index] == 1) {
         box = 9745;
      } else {
         box = 9744;
      }

      int w = graphics.drawText(box, 0, y, 54, width);
      int space = graphics.drawText(" ", w, y);
      graphics.drawText(EventLog.getRegisteredAppName(guid), w + space, y);
   }

   @Override
   public final int getPreferredWidth(ListField field) {
      return this.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final Object get(ListField listField, int index) {
      long guid = this._guids[index];
      return EventLog.getRegisteredAppName(guid);
   }

   private final int level2ChoiceIndex(int level) {
      switch (level) {
         case 2:
            return -1;
         case 3:
         default:
            return 0;
         case 4:
            return 1;
         case 5:
            return 2;
      }
   }

   private final int choiceIndex2Level(int index) {
      switch (index) {
         case -1:
            return -1;
         case 0:
         default:
            return 3;
         case 1:
            return 4;
         case 2:
            return 5;
      }
   }
}
