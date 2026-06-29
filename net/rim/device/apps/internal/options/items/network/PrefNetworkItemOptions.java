package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.NetworkInfo;

public final class PrefNetworkItemOptions extends AppsMainScreen implements ListFieldCallback, FieldChangeListener {
   private int _origPriority;
   private NetworkInfo _netInfo;
   private NetworkInfo _origNetInfo;
   private PrefNetworkList _netList;
   private EditField _priorityField;
   private EditField _netMccField;
   private EditField _netMncField;
   private ObjectChoiceField _net3GField;
   private RichTextField _netNameField;
   private int _type;
   static final int TYPE_ADD = 1;
   static final int TYPE_EDIT = 2;
   static final int TYPE_VIEW = 3;
   static final int MCC_MNC_FIELD_SIZE = 6;

   public PrefNetworkItemOptions(int priority, PrefNetworkList netList, int type) {
      super(2251799813685248L);
      this._netList = netList;
      this._type = type;
      this._origPriority = priority;
      this.initialize();
      this.refresh();
   }

   @Override
   protected final void onExposed() {
      this.refresh();
      super.onExposed();
   }

   protected final void initialize() {
      int size = 0;
      int priority = 0;
      if (this._type == 1) {
         this.setTitle(OptionsResources.getString(1873));
      } else if (this._type == 2) {
         this.setTitle(OptionsResources.getString(1874));
      } else if (this._type == 3) {
         this.setTitle(OptionsResources.getString(1875));
      }

      if (this._netList == null) {
         this._netList = new PrefNetworkList(null);
      } else {
         size = this._netList.getListSize();
      }

      if (this._origPriority < 0 || size <= 0) {
         this._origPriority = 0;
      } else if (this._type == 1) {
         this._origPriority++;
      }

      if (this._origPriority < size) {
         priority = this._origPriority;
      } else {
         priority = size;
      }

      if (this._origNetInfo == null) {
         this._origNetInfo = new NetworkInfo();
         if ((this._type == 2 || this._type == 3) && this._netList != null && this._netList.getItem(this._origPriority) != null) {
            this.copyNetworkInfo(this._origNetInfo, this._netList.getItem(this._origPriority));
         } else {
            this._origNetInfo.setNetworkId(0);
            this._origNetInfo.setName("");
            if (NetworkOptionsUtils.is3GSupported()) {
               this._origNetInfo.setCategory(16);
            }
         }
      }

      if (this._netInfo == null) {
         this._netInfo = new NetworkInfo();
         this.copyNetworkInfo(this._netInfo, this._origNetInfo);
      }

      this.buildFields(priority);
   }

   protected final void buildFields(int priority) {
      String value = null;
      int maxPriotityChars = Integer.toString(this._netList.getListSize()).length() + 1;
      long style = 1152921504606846976L;
      if (this._type == 3) {
         style |= 9007199254740992L;
      }

      value = Integer.toString(priority + 1);
      this._priorityField = new EditField(this.getLabel(OptionsResources.getString(1876)), value, maxPriotityChars, style);
      this._priorityField.setFilter(TextFilter.get(1));
      int netId = this._netInfo != null ? this._netInfo.getNetworkId() : 0;
      value = netId != 0 ? NetworkOptionsUtils.mobileCodeToString(this._netInfo.getMcc()) : null;
      this._netMccField = new EditField(this.getLabel(OptionsResources.getString(1883)), value, 3, style);
      this._netMccField.setFilter(TextFilter.get(1));
      value = netId != 0 ? NetworkOptionsUtils.mobileCodeToString(this._netInfo.getMnc()) : null;
      this._netMncField = new EditField(this.getLabel(OptionsResources.getString(1884)), value, 3, style);
      this._netMncField.setFilter(TextFilter.get(1));
      this._netNameField = new RichTextField(45036004863639552L);
      this.add(this._priorityField);
      this.add(this._netMccField);
      this.add(this._netMncField);
      this._netMccField.setChangeListener(this);
      this._netMncField.setChangeListener(this);
      if (NetworkOptionsUtils.is3GSupported()) {
         String[] choices = new String[]{OptionsResources.getStringArray(1970)[1], OptionsResources.getStringArray(1970)[0]};
         int initialIndex = this._netInfo != null && (this._netInfo.getCategory() & 64) != 0 ? 1 : 0;
         this._net3GField = new ObjectChoiceField(OptionsResources.getString(1972), choices, initialIndex, style);
         this.add(new SeparatorField());
         this.add(this._net3GField);
         this._net3GField.setChangeListener(this);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      String text = null;
      if (field == this._netMccField) {
         text = this._netMccField.getText();
         if (text != null && text.length() != 0) {
            this._netInfo.setMcc(NetworkOptionsUtils.stringToMobileCode(text));
         } else {
            this._netInfo.setMcc(0);
         }
      } else if (field == this._netMncField) {
         text = this._netMncField.getText();
         if (text != null && text.length() != 0) {
            this._netInfo.setMnc(NetworkOptionsUtils.stringToMobileCode(text));
         } else {
            this._netInfo.setMnc(0);
         }
      } else if (field == this._net3GField) {
         int selectedIndex = this._net3GField.getSelectedIndex();
         int newValue = this._netInfo.getCategory();
         int clearFlag = 80;
         newValue = NetworkOptionsUtils.clearFlag(newValue, clearFlag);
         if (selectedIndex == 0) {
            this._netInfo.setCategory(newValue | 16);
         } else {
            this._netInfo.setCategory(newValue | 64);
         }
      }

      String name = NetworkOptionsUtils.getPredefinedNetworkName(this._netInfo.getNetworkId());
      this._netInfo.setName(name);
      this.deleteField(this._netNameField);
      if (name != null && name.length() != 0) {
         this._netNameField.setText(this.getString(OptionsResources.getString(1878), name));
         this.insert(this._netNameField, 0);
      } else {
         this._netNameField.setText("");
      }
   }

   private final void refresh() {
      if (this._type == 3) {
         this.deleteField(this._netNameField);
         if (this._netInfo != null) {
            String value = this._netInfo.getName();
            if (value != null && value.length() != 0) {
               this._netNameField.setText(this.getString(OptionsResources.getString(1878), value));
               this.insert(this._netNameField, 0);
               return;
            }

            this._netNameField.setText("");
            return;
         }

         this._netNameField.setText("");
      }
   }

   private final void deleteField(Field f) {
      Manager m = f.getManager();
      if (m != null) {
         m.delete(f);
      }
   }

   private final String getString(String label, String value) {
      StringBuffer strBuf = new StringBuffer();
      strBuf.append(label);
      strBuf.append(": ");
      strBuf.append(value);
      return strBuf.toString();
   }

   private final String getLabel(String label) {
      return label + ":" + " ";
   }

   private final void copyNetworkInfo(NetworkInfo toNetInfo, NetworkInfo fromNetInfo) {
      if (toNetInfo != null && fromNetInfo != null) {
         toNetInfo.setNetworkId(fromNetInfo.getNetworkId());
         toNetInfo.setName(fromNetInfo.getName());
         toNetInfo.setCategory(fromNetInfo.getCategory());
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
   }

   @Override
   public final Object get(ListField listField, int index) {
      return "";
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   protected final boolean onSave() {
      int priority = this.getPriority();
      if (priority != -1 && this._netInfo.getNetworkId() != 0) {
         if ((this._netInfo.getNetworkId() & 65535) == 0) {
            Status.show(OptionsResources.getString(2074), Bitmap.getPredefinedBitmap(2), 2000);
            return false;
         }

         if (this._type == 1) {
            if (this._netList.isItemInList(this._netInfo) != -1) {
               Status.show(OptionsResources.getString(1894), Bitmap.getPredefinedBitmap(2), 2000);
               return false;
            } else {
               this._netList.add(priority, this._netInfo);
               return true;
            }
         } else {
            if (this._type == 2 && this._origNetInfo.getNetworkId() != this._netInfo.getNetworkId() || this._origPriority != priority) {
               if (this._origNetInfo.getNetworkId() != this._netInfo.getNetworkId() && this._netList.isItemInList(this._netInfo) != -1) {
                  Status.show(OptionsResources.getString(1894), Bitmap.getPredefinedBitmap(2), 2000);
                  return false;
               }

               this._netList.change(this._origPriority, priority, this._netInfo);
            }

            return true;
         }
      } else {
         Status.show(OptionsResources.getString(1893), Bitmap.getPredefinedBitmap(2), 2000);
         return false;
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      int priority = this.getPriority();
      if (this._type == 1
         || this._type == 2
            && (
               this._priorityField != null && this._priorityField.isDirty()
                  || this._netMccField != null && this._netMccField.isDirty()
                  || this._netMncField != null && this._netMncField.isDirty()
                  || this._netInfo.getNetworkId() != this._origNetInfo.getNetworkId()
                  || this._net3GField != null && this._net3GField.isDirty()
                  || priority != this._origPriority
            )) {
         menu.add(new PrefNetworkItemOptions$PrefNetworkDoneVerb(this));
      }
   }

   private final int getPriority() {
      String priorityText = this._priorityField.getText();
      return priorityText != null && priorityText.length() != 0 ? Integer.parseInt(priorityText) - 1 : -1;
   }
}
