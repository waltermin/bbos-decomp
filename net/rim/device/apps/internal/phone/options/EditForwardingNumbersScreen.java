package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class EditForwardingNumbersScreen extends AppsMainScreen implements ListFieldCallback, AddNumberVerb$AddNumberCallback, GlobalEventListener {
   private ListField _listField;
   private String[] _fwdingNumbers;
   private int _newSelIndex = -1;
   private EditForwardingNumbersScreen$Callback _callback;
   private EditForwardingNumbersScreen$DeleteNumberVerb _deleteNumberVerb = new EditForwardingNumbersScreen$DeleteNumberVerb(this);
   private AddNumberVerb _addNumberVerb = new AddNumberVerb(this, null);
   private String[] _activeForwardNumbers;

   final void refreshForwardingNumberList() {
      this._fwdingNumbers = PhoneOptions.getOptions().getSavedForwardingNumbers(this._activeForwardNumbers);
      this._listField.setSize(this._fwdingNumbers.length);
      if (this._newSelIndex != -1) {
         this._listField.setSelectedIndex(this._newSelIndex);
      }

      this._newSelIndex = -1;
      this.invalidate();
   }

   final void onNumberDeleted() {
      this._newSelIndex = Math.max(this._listField.getSelectedIndex() - 1, 0);
      this.refreshForwardingNumberList();
      if (this._callback != null) {
         this._callback.onForwardingNumbersChanged();
      }
   }

   @Override
   public final void onForwardingNumberAdded(String number, Field selectedField) {
      this.refreshForwardingNumberList();
      if (this._callback != null) {
         this._callback.onForwardingNumbersChanged();
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -2282475915901395486L || guid == -3666745774872801074L) {
         Application.getApplication().invokeLater(new EditForwardingNumbersScreen$1(this));
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      graphics.drawText(this._fwdingNumbers[index], 0, y, 64, width);
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._fwdingNumbers[index];
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   EditForwardingNumbersScreen(EditForwardingNumbersScreen$Callback callback, String[] activeForwardNumbers) {
      super(0);
      this._activeForwardNumbers = activeForwardNumbers;
      this.setTitle((Field)(new Object(PhoneResources.getString(6242), 64)));
      this._callback = callback;
      this._fwdingNumbers = PhoneOptions.getOptions().getSavedForwardingNumbers(this._activeForwardNumbers);
      this._listField = (ListField)(new Object(this._fwdingNumbers.length, 0));
      this._listField.setCallback(this);
      this.add(this._listField);
   }

   private final boolean isDeleteable() {
      return PhoneUtilities.getArrayIndex(this._fwdingNumbers[this._listField.getSelectedIndex()], this._activeForwardNumbers) == -1;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (Keypad.getAltedChar(key) == 127 && this.isDeleteable()) {
         this._deleteNumberVerb.invoke(null);
         return true;
      } else if (key == 27) {
         this.close();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         Application.getApplication().addGlobalEventListener(this);
      } else {
         Application.getApplication().removeGlobalEventListener(this);
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._addNumberVerb);
      menu.setDefault(this._addNumberVerb);
      if (this._listField.getSize() > 0) {
         if (this.isDeleteable()) {
            menu.add(this._deleteNumberVerb);
         }

         menu.add(new EditForwardingNumbersScreen$EditNumberVerb(this));
      }
   }
}
