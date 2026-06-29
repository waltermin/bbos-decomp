package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class ChoiceGroup extends Item implements Choice {
   private VerticalFieldManager _container = new VerticalFieldManager(1152921504606846976L);
   private LabelField _label;
   private BasicChoice _choiceImpl;

   final int getType() {
      return this._choiceImpl._type;
   }

   public ChoiceGroup(String label, int choiceType) {
      synchronized (Application.getEventLock()) {
         if (choiceType != 1 && choiceType != 2 && choiceType != 4) {
            throw new IllegalArgumentException();
         }

         this.setPeer(this._container);
         this.init(label, choiceType, null, null, false);
      }
   }

   public ChoiceGroup(String label, int choiceType, String[] stringElements, Image[] imageElements) {
      synchronized (Application.getEventLock()) {
         if (choiceType != 1 && choiceType != 2 && choiceType != 4) {
            throw new IllegalArgumentException();
         }

         this.setPeer(this._container);
         this.init(label, choiceType, stringElements, imageElements, true);
      }
   }

   ChoiceGroup(int choiceType, String[] stringElements, Image[] imageElements, boolean validateElements) {
      if (choiceType >= 1 && choiceType <= 3) {
         this.setPeer(this._container);
         this.init(null, choiceType, stringElements, imageElements, validateElements);
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void init(String label, int choiceType, String[] stringElements, Image[] imageElements, boolean validateElements) {
      this._container.setCookie(this);
      if (validateElements) {
         int dummy = stringElements.length;
         if (imageElements != null && imageElements.length != stringElements.length) {
            throw new IllegalArgumentException();
         }
      }

      this.setLabel(label);
      if (choiceType == 1 || choiceType == 2 || choiceType == 3) {
         this._choiceImpl = new ListChoice(this, choiceType);
         this._container.add(this._choiceImpl._container);
      }

      if (choiceType == 4) {
         this._choiceImpl = new PopupChoice();
         this._container.add(this._choiceImpl._container);
         ((PopupChoice)this._choiceImpl).setCookie(this);
      }

      if (stringElements != null) {
         for (int i = 0; i < stringElements.length; i++) {
            this.insert(i, stringElements[i], imageElements != null ? imageElements[i] : null);
         }
      }
   }

   @Override
   public void setLabel(String label) {
      synchronized (Application.getEventLock()) {
         if (label == null) {
            if (this._label != null) {
               this._container.delete(this._label);
               this._label = null;
            }
         } else if (this._label == null) {
            this._label = new LabelField(label);
            this._container.insert(this._label, 0);
         } else {
            this._label.setText(label);
         }
      }
   }

   @Override
   public String getLabel() {
      synchronized (Application.getEventLock()) {
         return this._label == null ? null : this._label.getText();
      }
   }

   @Override
   public void setLayout(int layout) {
      super.setLayout(layout);
      this._choiceImpl.setLayout(layout);
   }

   @Override
   public int getLayout() {
      return this._choiceImpl.getLayout();
   }

   @Override
   Field addToForm(FieldChangeListener changeListener) {
      this._choiceImpl.addToForm(changeListener);
      return this._container;
   }

   @Override
   public int size() {
      synchronized (Application.getEventLock()) {
         return this._choiceImpl.size();
      }
   }

   @Override
   public String getString(int elementNum) {
      return this._choiceImpl.getString(elementNum);
   }

   @Override
   public Image getImage(int elementNum) {
      return this._choiceImpl.getImage(elementNum);
   }

   @Override
   public int append(String stringPart, Image imagePart) {
      return this._choiceImpl.append(stringPart, imagePart);
   }

   @Override
   public void insert(int elementNum, String stringElement, Image imageElement) {
      this._choiceImpl.insert(elementNum, stringElement, imageElement);
   }

   @Override
   public void delete(int elementNum) {
      this._choiceImpl.delete(elementNum);
   }

   @Override
   public void deleteAll() {
      this._choiceImpl.deleteAll();
   }

   @Override
   public void set(int elementNum, String stringPart, Image imagePart) {
      this._choiceImpl.set(elementNum, stringPart, imagePart);
   }

   @Override
   public boolean isSelected(int elementNum) {
      return this._choiceImpl.isSelected(elementNum);
   }

   @Override
   public int getSelectedIndex() {
      return this._choiceImpl.getSelectedIndex();
   }

   @Override
   public int getSelectedFlags(boolean[] selectedArray_return) {
      return this._choiceImpl.getSelectedFlags(selectedArray_return);
   }

   @Override
   public void setSelectedIndex(int elementNum, boolean selected) {
      this._choiceImpl.setSelectedIndex(elementNum, selected);
   }

   @Override
   public void setSelectedFlags(boolean[] selectedArray) {
      this._choiceImpl.setSelectedFlags(selectedArray);
   }

   @Override
   public void setFitPolicy(int fitPolicy) {
      this._choiceImpl.setFitPolicy(fitPolicy);
   }

   @Override
   public int getFitPolicy() {
      return this._choiceImpl.getFitPolicy();
   }

   @Override
   public void setFont(int elementNum, Font font) {
      this._choiceImpl.setFont(elementNum, font);
   }

   @Override
   public Font getFont(int elementNum) {
      return this._choiceImpl.getFont(elementNum);
   }
}
