package net.rim.device.apps.internal.deviceselftest;

import java.util.Vector;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

final class TestTrackballScreen$ReportListCallback implements ListFieldCallback {
   private Vector reports = new Vector();

   public final int size() {
      return this.reports.size();
   }

   public final void insert(Object toInsert, int index) {
      this.reports.addElement(toInsert);
   }

   public final void erase() {
      this.reports.removeAllElements();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return this.reports.indexOf(prefix, start);
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this.reports.elementAt(this.reports.size() - 1 - index);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Graphics.getScreenWidth();
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (index < this.reports.size()) {
         String str = (String)this.reports.elementAt(this.reports.size() - 1 - index);
         graphics.drawText(str, 0, y, 0, width);
      }
   }
}
