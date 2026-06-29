package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.VariableHeightListField;
import net.rim.device.api.ui.component.VariableHeightListFieldCallback;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.Utilities;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class FinderResultsSelectionScreen extends PopupScreen implements VariableHeightListFieldCallback {
   int _selectedIndex;
   Location[] _choices;
   private VariableHeightListField _listField;
   private int[] _rowHeight;
   private int _width;

   FinderResultsSelectionScreen(Location[] choices) {
      super((Manager)(new Object()));
      this._choices = choices;
      this.add((Field)(new Object(LBSResources.getString(72))));
      this.add((Field)(new Object()));
      this._listField = (VariableHeightListField)(new Object());
      this._listField.setSize(choices.length);
      this._listField.setCallback(this);
      this._listField.setFont(Font.getDefault());
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object(299067162755072L));
      vfm.add(this._listField);
      this.add(vfm);
      this._width = Display.getWidth() - this.getMarginRight() - this.getMarginLeft();
      this._rowHeight = new int[choices.length];
      Font font = this._listField.getFont();

      for (int i = 0; i < choices.length; i++) {
         this._rowHeight[i] = Utilities.drawMultiLineListItem(null, font, choices[i]._label, 0, 0, this._width, false);
      }
   }

   public final int get() {
      UiApplication.getUiApplication().invokeAndWait(new FinderResultsSelectionScreen$1(this));
      return this._selectedIndex;
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return this.trackwheelClick(status, time);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this._selectedIndex = this._listField.getSelectedIndex();
      this.close();
      return true;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            this._selectedIndex = this._listField.getSelectedIndex();
            this.close();
            return true;
         case '\u001b':
            this._selectedIndex = -1;
            this.close();
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }

   @Override
   public final void drawListRow(VariableHeightListField listField, Graphics graphics, int index, int y, int width) {
      if (index < listField.getSize()) {
         Utilities.drawMultiLineListItem(graphics, listField.getFont(), this._choices[index]._label, 0, y, this._width, true);
      }
   }

   @Override
   public final int getRowHeight(VariableHeightListField listField, int index) {
      return index < listField.getSize() ? this._rowHeight[index] : 0;
   }

   @Override
   public final Object get(VariableHeightListField listField, int index) {
      return index < listField.getSize() ? this._choices[index]._label : null;
   }

   @Override
   public final int getPreferredWidth(VariableHeightListField listField) {
      return super.getPreferredWidth();
   }

   @Override
   public final int indexOfList(VariableHeightListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }
}
