package net.rim.device.internal.eventLogViewer;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.component.PropertyField;
import net.rim.vm.EventLog;

class BaseDetailsScreen extends MainScreen {
   protected PropertyField _name;
   protected PropertyField _severity;
   protected PropertyField _guid;
   protected DateField _time;
   protected RichTextField _data;
   protected EventLoggerContents _contents;

   protected BaseDetailsScreen(EventLoggerContents contents) {
      super(299067162755072L);
      this._contents = contents;
      ResourceBundle b = this._contents._rb;
      this._name = new PropertyField(b.getString(14), null);
      this.add(this._name);
      this._severity = new PropertyField(b.getString(16), null);
      this.add(this._severity);
      this._guid = new PropertyField(b.getString(12), null);
      this.add(this._guid);
      this._time = new DateField(b.getString(13), 0, new SimpleDateFormat(b.getString(41)));
      this._time.setEditable(false);
      this.add(this._time);
      this.add(new SeparatorField());
      this._data = new RichTextField(null);
      this.add(this._data);
   }

   private void copyEventData2Clipboard() {
      StringBuffer strBuf = new StringBuffer(64);
      strBuf.append(this._name.getName());
      strBuf.append(' ');
      strBuf.append(this._name.getValue());
      strBuf.append('\n');
      strBuf.append(this._guid.getName());
      strBuf.append(' ');
      strBuf.append(this._guid.getValue());
      strBuf.append('\n');
      strBuf.append(this._time.getLabel());
      strBuf.append(' ');
      strBuf.append(this._time.toString());
      strBuf.append('\n');
      strBuf.append(this._data.getText());
      Clipboard.getClipboard().put(strBuf.toString());
   }

   void displayEventDetails(int eventHandle) {
      this._name.setValue(this.getNameFieldString(eventHandle));
      this._severity.setValue(this.getSeverityFieldString(eventHandle));
      this._guid.setValue(this.getGuidFieldString(eventHandle));
      this._time.setDate(this.getDateFieldTime(eventHandle));
      this._data.setText(this.getDataFieldString(eventHandle));
      this._data.setCursorPosition(0);
      this.invalidateLayout();
      UiApplication.getUiApplication().pushScreen(this);
   }

   protected String getDataFieldString(int _1) {
      throw null;
   }

   private long getDateFieldTime(int eventHandle) {
      return EventLog.getTime(eventHandle);
   }

   protected String getEventSummary(int _1) {
      throw null;
   }

   private String getGuidFieldString(int eventHandle) {
      StringBuffer sb = new StringBuffer();
      NumberUtilities.appendNumber(sb, EventLog.getGUID(eventHandle), 16);
      return sb.toString();
   }

   protected String getNameFieldString(int eventHandle) {
      long guid = EventLog.getGUID(eventHandle);
      return EventLogger.getRegisteredAppName(guid);
   }

   private String getSeverityFieldString(int eventHandle) {
      int stringIndex = EventLoggerContents.errorLevel2StringIndex(EventLog.getSeverity(eventHandle));
      return this._contents._rb.getStringArray(70)[stringIndex];
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         UiApplication.getUiApplication().popScreen(this);
         return true;
      }

      if (InternalServices.isReducedFormFactor()) {
         if (key == 'a') {
            this._contents.changeDisplayedEvent(-1);
            return true;
         }

         if (key == 'z') {
            this._contents.changeDisplayedEvent(1);
            return true;
         }
      } else {
         if (key == 'n') {
            this._contents.changeDisplayedEvent(-1);
            return true;
         }

         if (key == 'p') {
            this._contents.changeDisplayedEvent(1);
            return true;
         }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(new BaseDetailsScreen$1(this, this._contents._rb, 24, 0, 0));
   }

   @Override
   public void setTitle(String title) {
      this.setTitle(new LabelField(title));
   }
}
