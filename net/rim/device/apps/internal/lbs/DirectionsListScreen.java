package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.VariableHeightListField;
import net.rim.device.api.ui.component.VariableHeightListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class DirectionsListScreen extends MainScreen implements ListFieldCallback, VariableHeightListFieldCallback, FocusChangeListener {
   private MapField _field;
   private Route _route;
   private DirectionsListScreen$CustomLabelField _labelFrom;
   private DirectionsListScreen$CustomLabelField _labelTo;
   private VariableHeightListField _list;
   private ListField _listActions;
   private Field _activeField;
   private int[] _rowHeight;
   private int _selectedIndex;

   public final void setFocusIndex(int focusIndex) {
      if (focusIndex == -1) {
         if (this._activeField != this._listActions) {
            this._activeField = this._listActions;
         }

         this._listActions.setSelectedIndex(1);
      } else {
         if (this._activeField != this._list && this._list.getManager() != null) {
            this._activeField = this._list;
            this._list.setFocus();
         }

         this._list.setSelectedIndex(focusIndex);
         this._selectedIndex = focusIndex;
         this.updateDisplay();
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (index < listField.getSize()) {
         if (index == 0) {
            graphics.drawText(LBSResources.getString(297), 0, y, 64);
            return;
         }

         if (index == 1) {
            graphics.drawText(LBSResources.getString(298), 0, y, 64);
         }
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      if (index == 0) {
         return LBSResources.getString(297);
      } else {
         return index == 1 ? LBSResources.getString(298) : null;
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return super.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final void drawListRow(VariableHeightListField listField, Graphics graphics, int index, int y, int width) {
      if (index < listField.getSize()) {
         Utilities.drawMultiLineListItem(graphics, listField.getFont(), this._route._decisions.getAt(index)._label, 36, y, Display.getWidth() - 36, true);
         Bitmap bitmap = this._route._decisions.getAt(index)._sign;
         String bitMapText = this._route._decisions.getAt(index)._bitMapText;
         if (bitmap != null) {
            graphics.drawBitmap(0, y + this._rowHeight[index] / 2 - bitmap.getHeight() / 2, bitmap.getWidth(), bitmap.getHeight(), bitmap, 0, 0);
         }

         if (bitMapText != null) {
            int adjust = bitMapText.equals(LBSResources.getString(472)) ? 10 : 15;
            int adjustI = 0;
            int oldColor = graphics.getColor();
            Font oldFont = graphics.getFont();
            Font font = FontRegistry.get("BBCondensed").getFont(1, 4, 2);
            font = font.derive(1, font.getHeight(), 0, 1, 2560, 65536, 0, 0, 65536, 0, 0, 0, 16777215);
            String name = "";
            if (this._route._decisions.getAt(index)._name.length() > 0 && this._route._decisions.getAt(index)._connector.length() > 0) {
               name = this._route._decisions.getAt(index)._name + " - " + this._route._decisions.getAt(index)._connector;
            } else if (this._route._decisions.getAt(index)._name.length() > 0) {
               name = this._route._decisions.getAt(index)._name;
            } else {
               name = this._route._decisions.getAt(index)._connector;
            }

            String nameUpperCase = name.toUpperCase();
            if (this._route._decisions.getAt(index)._action == 10) {
               graphics.setFont(font);
               graphics.setColor(16777215);
            } else if (nameUpperCase.indexOf("HWY ") != -1 && bitMapText.indexOf(LBSResources.getString(472)) == -1) {
               graphics.setFont(FontRegistry.get("BBCondensed").getFont(1, 6, 2));
               graphics.setColor(32768);
               adjust = 20;
            } else if ((nameUpperCase.indexOf("AUT ") != -1 || nameUpperCase.indexOf("AUTOROUTE ") != -1 || nameUpperCase.indexOf("AUTO ROUTE ") != -1)
               && bitMapText.indexOf(LBSResources.getString(472)) == -1) {
               graphics.setFont(font);
               graphics.setColor(16777215);
               adjust = 20;
            } else if ((nameUpperCase.indexOf("I-") != -1 || nameUpperCase.indexOf("I ") != -1) && bitMapText.indexOf(LBSResources.getString(472)) == -1) {
               graphics.setFont(font);
               graphics.setColor(16777215);
               adjustI = 5;
               adjust = 20;
            } else if ((nameUpperCase.indexOf("US-") != -1 || nameUpperCase.indexOf("US ") != -1) && bitMapText.indexOf(LBSResources.getString(472)) == -1) {
               graphics.setFont(FontRegistry.get("BBCondensed").getFont(1, 4, 2));
               graphics.setColor(0);
               adjustI = 5;
               adjust = 20;
            } else {
               graphics.setFont(font);
               graphics.setColor(16777215);
            }

            int idx = bitMapText.indexOf(" ");
            int oldAlpha = graphics.getGlobalAlpha();
            int w = -1;
            graphics.setGlobalAlpha(255);
            if (idx == -1) {
               w = graphics.getFont().getAdvance(bitMapText);
               if (w >= bitmap.getWidth()) {
                  bitMapText = "...";
               }

               graphics.drawText(bitMapText, 0, y + this._rowHeight[index] / 2 - bitmap.getHeight() / 2 + adjust, 36, bitmap.getWidth());
            } else {
               String number = bitMapText.substring(0, idx);
               String direction = bitMapText.substring(idx + 1);
               direction.toLowerCase();
               int color = graphics.getColor();
               Font f = graphics.getFont();
               boolean changedColor = false;
               if (color == 32768) {
                  color = 16777215;
                  f = f.derive(1, font.getHeight(), 0, 1, 2560, 65536, 0, 0, 65536, 0, 0, 0, 16777215);
                  changedColor = true;
               }

               graphics.setColor(color);
               graphics.setFont(f);
               w = graphics.getFont().getAdvance(direction);
               if (w >= bitmap.getWidth()) {
                  direction = "...";
               }

               graphics.drawText(direction, 0, y + this._rowHeight[index] / 2 - bitmap.getHeight() / 2 + 5 + adjustI, 36, bitmap.getWidth());
               if (changedColor) {
                  color = 32768;
                  f = FontRegistry.get("BBCondensed").getFont(1, 6, 2);
               }

               graphics.setColor(color);
               graphics.setFont(f);
               w = graphics.getFont().getAdvance(number);
               if (w >= bitmap.getWidth()) {
                  number = "...";
               }

               graphics.drawText(number, 0, y + this._rowHeight[index] / 2 - bitmap.getHeight() / 2 + adjust, 36, bitmap.getWidth());
            }

            graphics.setFont(oldFont);
            graphics.setColor(oldColor);
            graphics.setGlobalAlpha(oldAlpha);
         }
      }
   }

   @Override
   public final int getRowHeight(VariableHeightListField listField, int index) {
      return index < listField.getSize() ? this._rowHeight[index] : 0;
   }

   @Override
   public final Object get(VariableHeightListField listField, int index) {
      return null;
   }

   @Override
   public final int getPreferredWidth(VariableHeightListField listField) {
      return super.getPreferredWidth();
   }

   @Override
   public final int indexOfList(VariableHeightListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final void focusChanged(Field field, int arg1) {
      this._activeField = field;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 10) {
         this.handleSelection();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      this.handleSelection();
      return true;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (this._activeField == this._list) {
         this.onMenu(0);
         return true;
      } else {
         this.viewOnMap();
         return true;
      }
   }

   @Override
   public final boolean onClose() {
      this._field.setLastUsedScreen(null);
      return super.onClose();
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      if (instance == 0) {
         menu.add(
            new DirectionsListScreen$1(
               this, LBSResources.getString(297), 4, this._activeField == this._listActions && this._listActions.getSelectedIndex() == 0 ? 0 : 524287
            )
         );
         menu.add(
            new DirectionsListScreen$2(
               this, LBSResources.getString(298), 0, this._activeField == this._listActions && this._listActions.getSelectedIndex() == 1 ? 1 : 524287
            )
         );
         if (GPSProvider.isGPSSupported()) {
            menu.add(new DirectionsListScreen$3(this, LBSResources.getString(418), 65536, 524287));
         }

         menu.add(new DirectionsListScreen$4(this, LBSResources.getString(419), 65537, 524287));
      }

      super.makeMenu(menu, instance);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int size = Ui.convertSize(FontRegistry.DEFAULT_SIZE, 3, 0);
      this.setFont(null);
      Font font = this.getFont();
      if (font.getHeight() > size && size > 0) {
         this.setFont(font.derive(font.getStyle(), size));
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         if (this._selectedIndex == -1) {
            this.setFocusIndex(this._selectedIndex);
            return;
         }

         this.setFocusIndex(this._field.getRoute()._decisions._focus);
      }
   }

   private final void handleSelection() {
      if (this._activeField == this._listActions) {
         this.viewOnMap();
      } else {
         if (this._activeField == this._list) {
            this.showOnMap();
         }
      }
   }

   private final void viewOnMap() {
      if (this._listActions.getSelectedIndex() == 0) {
         ((MapScreen)this._field._screen).getDirections();
      } else if (this._listActions.getSelectedIndex() == 1) {
         this._field.zoomToFit(this._route._bbox);
         this._route._decisions.setFocusIndex(0);
         this._field.showLocationMarkerCaption(true);
         if (this._route._decisions._count > 1) {
            this._field.showPOIDirHintLable(0, false);
         }
      }

      this.close();
      this._field.setLastUsedScreen(this);
   }

   private final void showOnMap() {
      Decision decision = this._route._decisions.getAt(this._list.getSelectedIndex());
      this._route._decisions.setFocus(decision);
      this._field._latitude = decision._latitude;
      this._field._longitude = decision._longitude;
      this._field.setZoom(2);
      this._field.updateScreenPosition();
      this._field.update(true);
      this._field.showLocationMarkerCaption(true);
      if (this._route._decisions._count > 1) {
         this._field.showPOIDirHintLable(0, false);
      }

      this.close();
      this._field.setLastUsedScreen(this);
   }

   public DirectionsListScreen(MapField mapField, int focusIndex) {
      super(1153220623309406208L);
      this._field = mapField;
      this._route = mapField.getRoute();
      this.applyTheme();
      float numKilometres = this._route._distance / 1148846080;
      int numDays = this._route._time / 86400;
      int numHours = (this._route._time - numDays * 86400) / 3600;
      int numMinutes = (this._route._time - numDays * 86400 - numHours * 3600) / 60;
      String title = Distance.formatDistance(numKilometres) + " (";
      if (numDays > 1) {
         title = title + numDays + " " + LBSResources.getString(183) + " ";
      } else if (numDays == 1) {
         title = title + numDays + " " + LBSResources.getString(204) + " ";
      }

      if (numHours > 1) {
         title = title + numHours + " " + LBSResources.getString(184) + " ";
      } else if (numHours == 1) {
         title = title + numHours + " " + LBSResources.getString(205) + " ";
      }

      if (numMinutes > 0 && numDays == 0) {
         title = title + numMinutes + " " + LBSResources.getString(185);
      }

      title = title + ")";
      this.setTitle(new LabelField(LBSResources.getString(104) + ": " + title));
      if (this._route != null) {
         String startAddress = this._route._decisions._decisions[0]._address;
         String endAddress = this._route._decisions._decisions[this._route._decisions._count - 1]._address;
         this._labelFrom = new DirectionsListScreen$CustomLabelField(
            MessageFormat.format(LBSResources.getString(291), new String[]{startAddress}), 1152921504606846976L
         );
         this._labelFrom.setFont(Font.getDefault().derive(1));
         this._labelTo = new DirectionsListScreen$CustomLabelField(
            MessageFormat.format(LBSResources.getString(292), new String[]{endAddress}), 1152921504606846976L
         );
         this._labelTo.setFont(Font.getDefault().derive(1));
         this._listActions = new ListField(2);
         this._listActions.setCallback(this);
         this._listActions.setFocusListener(this);
         this._listActions.setFont(Font.getDefault());
         int count = this._route._decisions._count;
         this._list = new VariableHeightListField(count);
         this._rowHeight = new int[count];
         Font font = this._list.getFont();

         for (int i = 0; i < count; i++) {
            this._rowHeight[i] = Utilities.drawMultiLineListItem(null, font, this._route._decisions.getAt(i)._label, 36, 0, Display.getWidth() - 36, false);
            this._rowHeight[i] = Math.max(32, this._rowHeight[i]);
         }

         this._list.setCallback(this);
         this._list.setFocusListener(this);
         this._list.setFont(Font.getDefault());
         if (focusIndex == -1) {
            this._selectedIndex = focusIndex;
         }

         this.add(this._listActions);
         this.add(new SeparatorField(65536));
         this.add(this._labelFrom);
         this.add(this._labelTo);
         this.add(new SeparatorField(65536));
         this.add(this._list);
      }
   }
}
