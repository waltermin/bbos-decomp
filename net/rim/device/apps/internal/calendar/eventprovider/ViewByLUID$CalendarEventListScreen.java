package net.rim.device.apps.internal.calendar.eventprovider;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenuFactory;

class ViewByLUID$CalendarEventListScreen extends FullScreen implements ListFieldCallback {
   Event[] _events = null;
   ListField _listField;

   ViewByLUID$CalendarEventListScreen(Event[] events) {
      super(281474976907264L);
      this._events = events;
      this._listField = new ListField(this._events.length);
      this._listField.setCallback(this);
      this.add(this._listField);
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      VerbToMenu verbToMenu = VerbToMenuFactory.createInstance();
      EventImpl e = (EventImpl)this._events[this._listField.getSelectedIndex()];
      Verb[] verbs = new Verb[0];
      e.getVerbs(null, verbs);
      verbToMenu.addVerbs(verbs);
      verbToMenu.getMenu(menu, null, null);
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      graphics.drawText(this._events[index].toString(), 0, y);
   }

   @Override
   public Object get(ListField listField, int index) {
      return this._events[index];
   }

   @Override
   public int getPreferredWidth(ListField field) {
      return Display.getWidth() - 10;
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }
}
