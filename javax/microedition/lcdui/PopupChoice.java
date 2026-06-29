package javax.microedition.lcdui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.Arrays;

final class PopupChoice extends BasicChoice {
   HorizontalFieldManager _popupContainer = new HorizontalFieldManager();
   private ObjectChoiceField _popup = new ObjectChoiceField();
   private BitmapField[] _popupImages;
   private String[] _popupStrings;

   PopupChoice() {
      this._popup.setChangeListener(new PopupChoice$PopupChangeListener(this));
      this._popupStrings = new String[0];
      this._popupImages = new BitmapField[0];
      super._type = 4;
   }

   @Override
   final Field addToForm(FieldChangeListener changeListener) {
      super._changeListener = changeListener;
      this._popupContainer.setChangeListener(null);
      this._popupContainer.setChangeListener(changeListener);
      return super._container;
   }

   @Override
   protected final String doGetString(int elementNum) {
      return this._popupStrings[elementNum];
   }

   @Override
   protected final Image doGetImage(int elementNum) {
      BitmapField imageElement = this._popupImages[elementNum];
      return imageElement != null ? (Image)imageElement.getCookie() : null;
   }

   public final void setCookie(Object cookie) {
      this._popup.setCookie(cookie);
   }

   @Override
   public final int getSelectedIndex() {
      return super._currentlySelectedIndex;
   }

   @Override
   public final void setLayout(int layout) {
      super.setLayout(layout);
      this._popupContainer.delete(this._popup);
      Object cookie = this._popup.getCookie();
      if (this._popupStrings != null && this._popupStrings.length > 0) {
         this._popup = new ObjectChoiceField(
            null, this._popupStrings, super._currentlySelectedIndex != -1 ? super._currentlySelectedIndex : 0, Item.getFieldLayoutStyle(this.getLayout(), 0)
         );
      } else {
         this._popup = new ObjectChoiceField(null, null, 0, Item.getFieldLayoutStyle(this.getLayout(), 0));
      }

      this._popup.setChangeListener(new PopupChoice$PopupChangeListener(this));
      this._popup.setCookie(cookie);
      this._popupContainer.add(this._popup);
      super._container.invalidate();
   }

   @Override
   protected final void doInsert(int elementNum, String stringElement, Image imageElement) {
      Arrays.insertAt(this._popupStrings, stringElement, elementNum);
      if (imageElement != null) {
         Arrays.insertAt(this._popupImages, new BitmapField(imageElement.getBitmap()), elementNum);
         this._popupImages[elementNum].setCookie(imageElement);
      } else {
         Arrays.insertAt(this._popupImages, null, elementNum);
      }

      if (this.size() == 0) {
         this._popupContainer.insert(this._popup, 0);
         if (this._popupImages[0] != null) {
            this._popupContainer.insert(this._popupImages[0], 0);
         }

         if (this._popupContainer.getManager() == null) {
            super._container.insert(this._popupContainer, 0);
         }

         super._currentlySelectedIndex = 0;
      }

      this._popup.setChoices(this._popupStrings);
   }

   @Override
   protected final void doDelete(int elementNum) {
      Arrays.removeAt(this._popupStrings, elementNum);
      Arrays.removeAt(this._popupImages, elementNum);
      this._popup.setChoices(this._popupStrings);
      if (this.size() > 0) {
         this._popup.setSelectedIndex(0);
      } else {
         if (this.size() == 0) {
            this._popupContainer.deleteAll();
         }
      }
   }

   @Override
   protected final void doSet(int elementNum, String stringPart, Image imagePart) {
      int selectedIndex = this.getSelectedIndex();
      this._popupStrings[elementNum] = stringPart;
      this._popupImages[elementNum] = new BitmapField(imagePart.getBitmap());
      this._popup.setChoices(this._popupStrings);
      this._popup.setSelectedIndex(selectedIndex);
   }

   @Override
   protected final boolean doIsSelected(int elementNum) {
      return super._currentlySelectedIndex == elementNum;
   }

   @Override
   protected final void doSetSelectedIndex(int elementNum, boolean selected) {
      if (selected) {
         this._popup.setSelectedIndex(elementNum);
      }
   }

   @Override
   protected final void doSetSelectedFlags(boolean[] selectedArray) {
      int count = this.size();
      int i = 0;

      while (i < count && !selectedArray[i]) {
         i++;
      }

      if (i == count) {
         i = 0;
      }

      this._popup.setSelectedIndex(i);
   }

   @Override
   protected final void setFieldFont(net.rim.device.api.ui.Font font, int elementNum) {
      if (super._currentlySelectedIndex == elementNum) {
         this._popup.setFont(font);
      }
   }
}
