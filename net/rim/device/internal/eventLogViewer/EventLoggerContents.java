package net.rim.device.internal.eventLogViewer;

import java.util.Calendar;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.EventLog;
import net.rim.vm.TraceBack;

public final class EventLoggerContents extends MainScreen implements ListFieldCallback {
   final ResourceBundleFamily _rb = ResourceBundle.getBundle(-1347895411836898368L, "net.rim.device.internal.resource.EventLogger");
   private DateFormat _dtFormater = new SimpleDateFormat(this._rb.getString(40));
   private Calendar _calendar = Calendar.getInstance();
   private int _displayMode = 0;
   private PersistentObject _filterPersist;
   private LongIntHashtable _filters;
   private int[] _eventHandles;
   private int[] _filteredEventHandles;
   private int[] _eventHandleIndicies;
   private ListField _list;
   private StringBuffer _strBuf = new StringBuffer();
   private LabelField _title;
   private DefaultDetailsScreen _defViewer = new DefaultDetailsScreen(this);
   private ExceptionDetailsScreen _exViewer = new ExceptionDetailsScreen(this);
   private StringDetailsScreen _strViewer = new StringDetailsScreen(this);
   private static final int MENU_CLOSE = 0;
   private static final int MENU_REFRESH = 2;
   private static final int MENU_VIEW_ITEM = 5;
   private static final int MENU_CLEAR_LOG = 6;
   private static final int MENU_OPTIONS = 7;
   private static final int MENU_COPY_DAY = 8;
   private static final int DISPLAY_SUMMARY = 0;
   private static final int DISPLAY_TIME = 1;
   private static final int DISPLAY_BOTH = 255;
   private static final long EVLV_FILTERS_GUID = -8838774854340583164L;
   static final int FILTER_DISPLAY = 1;
   static final int FILTER_HIDE = 0;
   private static final int TYPE_STARTUP = -1;

   final void changeDisplayedEvent(int delta) {
      UiApplication app = UiApplication.getUiApplication();
      Screen screen = app.getActiveScreen();
      if (screen instanceof BaseDetailsScreen) {
         app.popScreen(screen);
         int index = this._list.getSelectedIndex();
         this._list.setSelectedIndex(index + delta);
         if (this._list.getSelectedIndex() != index) {
            this.viewItem();
         }
      }
   }

   final void optionsChanged() {
      this.setTitle();
      this.filter(true);
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
      int eventHandle = this._filteredEventHandles[this.listIndex2FilteredIndex(index)];
      this._strBuf.setLength(0);
      this.prepareStringBuffer(this._strBuf, eventHandle, this._displayMode);
      return this._strBuf;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      Font font = this.getFont();
      int eventHandle = this._filteredEventHandles[this.listIndex2FilteredIndex(index)];
      this._strBuf.setLength(0);
      switch (this.prepareStringBuffer(this._strBuf, eventHandle, this._displayMode)) {
         case -2:
         case 0:
            font = font.derive(0);
            break;
         case -1:
         default:
            int color = graphics.getColor();
            if (graphics.isDrawingStyleSet(8)) {
               graphics.setColor(8704);
            } else {
               graphics.setColor(14548957);
            }

            graphics.fillRect(0, y, width, this._list.getRowHeight());
            graphics.setColor(color);
            break;
         case 1:
         case 2:
            font = font.derive(1);
      }

      graphics.setFont(font);
      graphics.drawText(this._strBuf, 0, this._strBuf.length(), 0, y, 0, width);
   }

   private final void findEvent(int type, int dir) {
      int max = this._list.getSize();
      int index = max - this._list.getSelectedIndex() - 1;

      for (int i = index + dir; i >= 0 && i < max; i += dir) {
         int eventHandle = this._filteredEventHandles[i];
         long guid = EventLog.getGUID(eventHandle);
         if (EventLogger.getRegisteredViewerType(guid) == type) {
            this._list.setSelectedIndex(max - i - 1);
            return;
         }
      }
   }

   private final void filter(boolean keepIndex) {
      int oldEventHandle = -1;
      int newIndex = -1;
      IntVector iVector = new IntVector(this._eventHandles.length);
      IntVector indexVector = new IntVector(this._eventHandles.length);
      if (keepIndex && this._filteredEventHandles.length != 0) {
         oldEventHandle = this._filteredEventHandles[this.listIndex2FilteredIndex(this._list.getSelectedIndex())];
      }

      for (int i = 0; i < this._eventHandles.length; i++) {
         long guid = EventLog.getGUID(this._eventHandles[i]);
         if (this._filters.get(guid) != 0) {
            if (this._eventHandles[i] == oldEventHandle) {
               newIndex = iVector.size();
            }

            iVector.addElement(this._eventHandles[i]);
            indexVector.addElement(i);
         }
      }

      int size = iVector.size();
      this._filteredEventHandles = iVector.toArray();
      if (this._filteredEventHandles == null) {
         this._filteredEventHandles = new int[0];
      }

      this._eventHandleIndicies = indexVector.toArray();
      if (this._eventHandleIndicies == null) {
         this._eventHandleIndicies = new int[0];
      }

      this._list.setSize(size);
      if (newIndex != -1 && size != 0) {
         this._list.setSelectedIndex(size - newIndex - 1);
      }
   }

   private final int findDateBoundaryIndex(int[] handles, int startIndex, Calendar srcCal, int delta) {
      int curIndex = startIndex;
      Calendar curCal = Calendar.getInstance();
      int day = srcCal.get(5);
      boolean magicDay = day == 1 && srcCal.get(2) == 0 && srcCal.get(1) == 2001;

      for (int nextIndex = curIndex + delta; nextIndex >= 0 && nextIndex < handles.length; nextIndex += delta) {
         ((CalendarExtensions)curCal).setTimeLong(EventLog.getTime(handles[nextIndex]));
         int curDay = curCal.get(5);
         if (magicDay && curDay != 1) {
            day = curDay;
            magicDay = false;
         } else if (curDay != day && (curDay != 1 || curCal.get(2) != 0 || curCal.get(1) != 2001)) {
            break;
         }

         curIndex = nextIndex;
      }

      return curIndex;
   }

   public static final int errorLevel2StringIndex(int level) {
      switch (level) {
         case -1:
            return 6;
         case 0:
         default:
            return 0;
         case 1:
            return 1;
         case 2:
            return 2;
         case 3:
            return 3;
         case 4:
            return 4;
         case 5:
            return 5;
      }
   }

   private final void initFilters() {
      boolean dirty = false;
      this._filterPersist = RIMPersistentStore.getPersistentObject(-8838774854340583164L);
      if ((this._filters = (LongIntHashtable)this._filterPersist.getContents()) == null) {
         this._filters = new LongIntHashtable();
         this._filterPersist.setContents(this._filters, 51);
         dirty = true;
      }

      long[] guids = EventLog.getRegisteredGUIDs();
      LongEnumeration enumeration = this._filters.keys();

      while (enumeration.hasMoreElements()) {
         long key = enumeration.nextElement();

         int i;
         for (i = 0; i < guids.length; i++) {
            if (guids[i] != 0 && guids[i] == key) {
               guids[i] = 0;
               break;
            }
         }

         if (i == guids.length) {
            this._filters.remove(key);
            dirty = true;
         }
      }

      for (int i = 0; i < guids.length; i++) {
         if (guids[i] != 0) {
            this._filters.put(guids[i], 1);
            dirty = true;
         }
      }

      if (dirty) {
         this._filterPersist.commit();
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (this._filteredEventHandles.length != 0) {
               this.viewItem();
               return true;
            }
         default:
            return false;
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            this.viewItem();
            return true;
         case '\u001b':
            this.close();
            return true;
         case ' ':
            this._displayMode = ++this._displayMode & 1;
            this._list.invalidate();
            return true;
         default:
            boolean result;
            if (InternalServices.isReducedFormFactor()) {
               result = this.keyCharReducedKeypad(key, status, time);
            } else {
               result = this.keyCharNormalKeypad(key, status, time);
            }

            return result ? result : super.keyChar(key, status, time);
      }
   }

   private final boolean keyCharNormalKeypad(char key, int status, int time) {
      switch (key) {
         case 'X':
            this.findEvent(3, 1);
            return true;
         case 'b':
            this._list.setSelectedIndex(this._list.getSize() - 1);
            return true;
         case 'r':
            this.refresh();
            return true;
         case 't':
            this._list.setSelectedIndex(0);
            return true;
         case 'x':
            this.findEvent(3, -1);
            return true;
         default:
            return false;
      }
   }

   private final boolean keyCharReducedKeypad(char key, int status, int time) {
      int index = this._list.getSelectedIndex();
      switch (key) {
         case 'a':
            this.findEvent(3, 1);
            return true;
         case 'b':
            if (++index >= this._list.getSize()) {
               index = this._list.getSize() - 1;
            }
            break;
         case 'c':
            index = this._list.getSize() - 1;
            break;
         case 'd':
            this._displayMode = --this._displayMode & 1;
            this._list.invalidate();
            return true;
         case 'e':
            index = 0;
            break;
         case 'g':
            this.viewItem();
            return true;
         case 'j':
            this._displayMode = ++this._displayMode & 1;
            this._list.invalidate();
            return true;
         case 't':
            if (--index < 0) {
               index = 0;
            }
            break;
         case 'z':
            this.findEvent(3, -1);
            return true;
         default:
            return false;
      }

      this._list.setSelectedIndex(index);
      return true;
   }

   private final int listIndex2FilteredIndex(int index) {
      return this._filteredEventHandles.length - index - 1;
   }

   private final int listIndex2UnfilteredIndex(int index) {
      return this._eventHandleIndicies[this.listIndex2FilteredIndex(index)];
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this._filteredEventHandles.length != 0) {
         menu.add(new EventLoggerContents$MyMenuItem(this, 5));
      }

      if (this._filteredEventHandles.length != 0) {
         menu.add(new EventLoggerContents$MyMenuItem(this, 8));
      }

      menu.add(new EventLoggerContents$MyMenuItem(this, 2));
      menu.add(new EventLoggerContents$MyMenuItem(this, 6));
      menu.add(new EventLoggerContents$MyMenuItem(this, 7));
   }

   private final int prepareStringBuffer(StringBuffer strBuf, int eventHandle, int mode) {
      int severity = EventLog.getSeverity(eventHandle);
      long guid = EventLog.getGUID(eventHandle);
      strBuf.append(this._rb.getString(71).charAt(errorLevel2StringIndex(severity)));
      strBuf.append(' ');
      strBuf.append(EventLogger.getRegisteredAppName(guid));
      strBuf.append(" - ");
      switch (mode) {
         case 0:
         case 255:
            BaseDetailsScreen ev = this.getViewer(guid);
            String summary = ev.getEventSummary(eventHandle);
            strBuf.append(summary);
            if ("System Startup".equals(summary)) {
               severity = -1;
            }

            if (mode == 0) {
               break;
            }

            strBuf.append(" - ");
         case 1:
            ((CalendarExtensions)this._calendar).setTimeLong(EventLog.getTime(eventHandle));
            this._dtFormater.format(this._calendar, strBuf, null);
      }

      if (mode == 255 && EventLogger.getRegisteredViewerType(guid) == 3) {
         Object backTrace = EventLog.getData(eventHandle);
         int j = 1;

         while (true) {
            String msg = TraceBack.getMessage(backTrace, j);
            if (msg == null) {
               break;
            }

            strBuf.append("\r\n| ");
            strBuf.append(msg);
            j++;
         }
      }

      return severity;
   }

   private final void refresh() {
      EventLog.freeSnapshot(this._eventHandles);
      this._eventHandles = EventLog.getSnapshot();
      this.filter(false);
   }

   private final void setTitle() {
      StringBuffer buf = new StringBuffer(80);
      buf.append(this._rb.getString(11));
      buf.append(' ');
      buf.append('(');
      buf.append(this._rb.getStringArray(70)[errorLevel2StringIndex(EventLogger.getMinimumLevel())]);
      buf.append(')');
      this._title.setText(buf);
   }

   private final void viewItem() {
      if (this.getFieldWithFocus() == this._list) {
         int eventHandle = this._filteredEventHandles[this.listIndex2FilteredIndex(this._list.getSelectedIndex())];
         long guid = EventLog.getGUID(eventHandle);
         this.getViewer(guid).displayEventDetails(eventHandle);
      }
   }

   private final BaseDetailsScreen getViewer(long guid) {
      switch (EventLogger.getRegisteredViewerType(guid)) {
         case 1:
            return this._defViewer;
         case 2:
         default:
            return this._strViewer;
         case 3:
            return this._exViewer;
      }
   }

   @Override
   public final void close() {
      EventLog.freeSnapshot(this._eventHandles);
      super.close();
   }

   public EventLoggerContents() {
      super(299067162755072L);
      this._list = new ListField();
      this._list.setCallback(this);
      this.add(this._list);
      this._title = new LabelField(this._rb.getString(11));
      this.setTitle(this._title);
      this.initFilters();
      this.setTitle();
      UiApplication.getUiApplication().pushScreen(this);
      this.refresh();
   }

   private final ResourceBundleFamily getMenuResourceBundle(int id) {
      switch (id) {
         case 1:
         case 3:
         case 4:
            return null;
         case 2:
            return this._rb;
         case 5:
         default:
            return CommonResource.getBundle();
         case 6:
            return this._rb;
         case 7:
            return CommonResource.getBundle();
         case 8:
            return this._rb;
      }
   }

   private final int getMenuId(int id) {
      switch (id) {
         case 1:
         case 3:
         case 4:
            return 0;
         case 2:
            return 22;
         case 5:
         default:
            return 14;
         case 6:
            return 23;
         case 7:
            return 20;
         case 8:
            return 26;
      }
   }

   private final int getMenuOrdinal(int id) {
      switch (id) {
         case 1:
         case 3:
         case 4:
            return 0;
         case 2:
            return 65537;
         case 5:
         default:
            return 0;
         case 6:
            return 65536;
         case 7:
            return 65538;
         case 8:
            return 65539;
      }
   }
}
