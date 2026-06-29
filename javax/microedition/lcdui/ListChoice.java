package javax.microedition.lcdui;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;

final class ListChoice extends BasicChoice {
   private RadioButtonGroup _radioGroup;
   private ChoiceGroup _choiceGroupFacade;

   ListChoice(ChoiceGroup facade, int choiceType) {
      super._type = choiceType;
      this._choiceGroupFacade = facade;
      if (choiceType == 1) {
         this._radioGroup = new RadioButtonGroup();
      }
   }

   @Override
   final Field addToForm(FieldChangeListener changeListener) {
      super._changeListener = changeListener;
      switch (super._type) {
         case 1:
            this._radioGroup.setChangeListener(null);
            this._radioGroup.setChangeListener(changeListener);
            break;
         default:
            for (int i = 0; i < super._container.getFieldCount(); i++) {
               Field field = super._container.getField(i);
               field.setChangeListener(null);
               field.setChangeListener(changeListener);
            }
      }

      return super._container;
   }

   @Override
   public final int getSelectedIndex() {
      synchronized (Application.getEventLock()) {
         switch (super._type) {
            case 1:
               for (int i = 0; i < super._container.getFieldCount(); i++) {
                  if (((RadioButtonField)super._container.getField(i)).isSelected()) {
                     return i;
                  }
               }
               break;
            case 3:
               if (!super._onScreen) {
                  return super._currentlySelectedIndex;
               }

               int index = super._container.getFieldWithFocusIndex();
               if (index != -1) {
                  return index;
               }
         }

         return -1;
      }
   }

   @Override
   protected final String doGetString(int elementNum) {
      Field field = this.getField(elementNum);
      switch (super._type) {
         case 1:
            return ((RadioButtonField)field).getLabel();
         case 2:
            return ((CheckboxField)field).getLabel();
         case 3:
         default:
            return ((LabelField)field).getText();
      }
   }

   @Override
   protected final Image doGetImage(int elementNum) {
      Field field = this.getField(elementNum);
      return (Image)((Vector)field.getCookie()).elementAt(1);
   }

   @Override
   protected final void doInsert(int elementNum, String stringElement, Image imageElement) {
      stringElement.length();
      Field field;
      switch (super._type) {
         case 1:
            boolean selected = super._container.getFieldCount() == 0;
            field = new RadioButtonField(stringElement, this._radioGroup, selected);
            if (imageElement != null) {
               ((RadioButtonField)field).setImage(imageElement.getBitmap());
            }
            break;
         case 2:
            field = new CheckboxField(stringElement, false);
            field.setChangeListener(super._changeListener);
            if (imageElement != null) {
               ((CheckboxField)field).setImage(imageElement.getBitmap());
            }
            break;
         case 3:
         default:
            field = new LabelField(stringElement, 18014398509481984L);
            field.setChangeListener(super._changeListener);
            if (imageElement != null) {
               ((LabelField)field).setImage(imageElement.getBitmap());
            }
      }

      int oldSelectedIndex = this.getSelectedIndex();
      super._container.insert(field, elementNum);
      if (this.size() == 0) {
         if (super._type == 3 || super._type == 1) {
            this.doSetSelectedIndex(0, true);
         }
      } else if (!super._onScreen && super._type == 3 && elementNum < oldSelectedIndex) {
         this.doSetSelectedIndex(oldSelectedIndex + 1, true);
      }

      Vector cookieVector = new Vector(2);
      cookieVector.addElement(this._choiceGroupFacade);
      cookieVector.addElement(imageElement);
      field.setCookie(cookieVector);
   }

   @Override
   protected final void doDelete(int elementNum) {
      boolean mustAdjustSelectedIndex = elementNum < this.getSelectedIndex();
      Field field = this.getField(elementNum);
      field.setChangeListener(null);
      super._container.delete(field);
      if (super._type == 1) {
         RadioButtonField radio = (RadioButtonField)field;
         boolean selected = radio.isSelected();
         this._radioGroup.remove(radio);
         if (selected && this.size() - 1 > 0) {
            this._radioGroup.setSelectedIndex(0);
            return;
         }
      } else if (super._type == 3 && !super._onScreen && mustAdjustSelectedIndex) {
         this.doSetSelectedIndex(this.getSelectedIndex() - 1, true);
      }
   }

   @Override
   protected final void doSet(int elementNum, String stringPart, Image imagePart) {
      stringPart.length();
      Field field = super._container.getField(elementNum);
      switch (super._type) {
         case 1:
            RadioButtonField radio = (RadioButtonField)field;
            radio.setLabel(stringPart);
            if (imagePart != null) {
               radio.setImage(imagePart.getBitmap());
            }
            break;
         case 2:
            CheckboxField checkbox = (CheckboxField)field;
            checkbox.setLabel(stringPart);
            if (imagePart != null) {
               checkbox.setImage(imagePart.getBitmap());
            }
            break;
         case 3:
         default:
            LabelField label = (LabelField)field;
            label.setText(stringPart);
            if (imagePart != null) {
               label.setImage(imagePart.getBitmap());
            }
      }

      Vector cookieVector = new Vector(2);
      cookieVector.addElement(this._choiceGroupFacade);
      cookieVector.addElement(imagePart);
      field.setCookie(cookieVector);
   }

   @Override
   protected final boolean doIsSelected(int elementNum) {
      synchronized (Application.getEventLock()) {
         Field field = this.getField(elementNum);
         switch (super._type) {
            case 1:
               return ((RadioButtonField)field).isSelected();
            case 3:
               if (super._onScreen) {
                  return super._container.getFieldWithFocus() == field;
               }

               return super._currentlySelectedIndex == elementNum;
            default:
               return ((CheckboxField)field).getChecked();
         }
      }
   }

   @Override
   protected final void doSetSelectedIndex(int elementNum, boolean selected) {
      Field field = this.getField(elementNum);
      switch (super._type) {
         case 1:
            if (selected) {
               ((RadioButtonField)field).setSelected(true);
               return;
            }
            break;
         case 3:
            if (selected) {
               if (super._onScreen) {
                  field.setFocus();
               }

               super._currentlySelectedIndex = elementNum;
               return;
            }
            break;
         default:
            ((CheckboxField)field).setChecked(selected);
      }
   }

   @Override
   protected final void doSetSelectedFlags(boolean[] selectedArray) {
      synchronized (Application.getEventLock()) {
         int count = this.size();
         if (super._type == 2) {
            for (int i = 0; i < count; i++) {
               ((CheckboxField)super._container.getField(i)).setChecked(selectedArray[i]);
            }
         } else {
            int i = 0;

            while (i < count && !selectedArray[i]) {
               i++;
            }

            if (i == count) {
               i = 0;
            }

            Field field = super._container.getField(i);
            switch (super._type) {
               case 1:
                  ((RadioButtonField)field).setSelected(true);
                  break;
               default:
                  this.setSelectedIndex(i, true);
            }
         }
      }
   }
}
