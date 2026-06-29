package net.rim.device.apps.internal.options;

import java.util.Vector;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.options.items.LocalizationOptionsItem;
import net.rim.vm.PersistentInteger;

final class OptionsApp$OptionGroup implements ListFieldCallback {
   private ListField _listField;
   private MainScreen _mainScreen;
   private SimpleSortingVector _listItems;
   private boolean _base;
   private int _titleId;
   private int _selectedIndexID;

   OptionsApp$OptionGroup(long groupId, int titleId, boolean base) {
      this._base = base;
      this._titleId = titleId;
      this._selectedIndexID = PersistentInteger.getId(-7960768038040625127L ^ groupId, 0);
      this._listItems = (SimpleSortingVector)(new Object());
   }

   public final void addItem(Object item) {
      this._listItems.addElement(item);
   }

   public final void resetList() {
      this._listItems.removeAllElements();
   }

   public final void finalizeAddItems(Comparator comparator) {
      this._listItems.setSortComparator(comparator);
      this._listItems.reSort();
   }

   public final Vector getListItems() {
      return this._listItems;
   }

   public final void localeChanged() {
      for (int i = this._listItems.size() - 1; i >= 0; i--) {
         if (this._listItems.elementAt(i) instanceof LocalizationOptionsItem) {
            this._listField.setSelectedIndex(i);
            return;
         }
      }
   }

   public final void shutDown() {
      PersistentInteger.set(this._selectedIndexID, this._listField.getSelectedIndex());
   }

   public final boolean openCurrentItem(Object context) {
      return OptionsApp.openItem(this._listItems.elementAt(this._listField.getSelectedIndex()), context);
   }

   public final void createMainScreen() {
      ListField listField = (ListField)(new Object(this._listItems.size()));
      listField.setCallback(this);
      listField.setCookie(this._listItems);
      MainScreen mainScreen = new OptionsApp$OptionsListScreen(this, this._titleId);
      mainScreen.add(listField);
      mainScreen.setFocus();
      int priorSelectedIndex = PersistentInteger.get(this._selectedIndexID);
      listField.setSelectedIndex(priorSelectedIndex < this._listItems.size() ? priorSelectedIndex : 0);
      mainScreen.setVerticalQuantization(-1);
      this._listField = listField;
      this._mainScreen = mainScreen;
   }

   public final void pushScreen() {
      UiApplication.getUiApplication().pushScreen(this._mainScreen);
   }

   public final void close(boolean allowExit) {
      UiApplication.getUiApplication().popScreen(this._mainScreen);
      if (this._base && allowExit) {
         OptionsApp._app.closeDeviceOptions();
      }

      PersistentInteger.set(this._selectedIndexID, this._listField.getSelectedIndex());
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      Object item = this._listItems.elementAt(index);
      if (item instanceof Object) {
         PaintProvider paintProvider = (PaintProvider)item;
         VariableRowHeightProxy.addHeightAdjusterToContext(OptionsApp._context, listField);
         paintProvider.paint(graphics, 0, y, width, listField.getRowHeight(index), OptionsApp._context);
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._listItems.elementAt(index);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      int size = this._listField.getSize();

      for (int i = start; i < size; i++) {
         Object item = this._listItems.elementAt(i);
         if (item instanceof Object) {
            MatchProvider matchProvider = (MatchProvider)item;
            if (matchProvider.match(prefix) == 1) {
               return i;
            }
         }
      }

      return -1;
   }
}
