package net.rim.device.api.ui.component;

import java.util.Vector;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;

public class RadioButtonGroup {
   private boolean _notifyReselected;
   private Vector _buttons = new Vector();
   private int _selected = -1;
   private FieldChangeListener _changeListener;

   public int add(RadioButtonField button) {
      if (button.getGroup() != null) {
         throw new IllegalStateException();
      }

      button._group = this;
      button._index = this._buttons.size();
      this._buttons.addElement(button);
      return button._index;
   }

   public FieldChangeListener getChangeListener() {
      return this._changeListener;
   }

   public final boolean getNotifyReselected() {
      return this._notifyReselected;
   }

   public int getSelectedIndex() {
      return this._selected;
   }

   public final int getSize() {
      return this._buttons.size();
   }

   public void remove(RadioButtonField button) {
      int index = button._index;
      if (button._group != this) {
         throw new IllegalArgumentException();
      }

      Manager mgr = button.getManager();
      if (mgr != null && mgr.isValidLayout()) {
         throw new IllegalStateException("RadioButton must be removed from its manager before being removed from its group.");
      }

      if (this._selected == index) {
         this._selected = -1;
      }

      if (this._selected > index) {
         this._selected--;
      }

      this._buttons.removeElementAt(index);

      while (index < this._buttons.size()) {
         ((RadioButtonField)this._buttons.elementAt(index))._index--;
         index++;
      }

      button._group = null;
      button._index = -1;
   }

   public void setChangeListener(FieldChangeListener listener) {
      if (listener != null && this._changeListener != null) {
         throw new IllegalStateException("Multiple change listeners not allowed.");
      }

      this._changeListener = listener;
   }

   public final void setNotifyReselected(boolean notifyReselected) {
      this._notifyReselected = notifyReselected;
   }

   public void setSelectedIndex(int selected) {
      this.setSelectedIndex(selected, Integer.MIN_VALUE);
   }

   void setSelectedIndex(int selected, int context) {
      if (selected < -1 || selected >= this._buttons.size()) {
         throw new IllegalArgumentException();
      }

      if (this._selected == selected) {
         if (this._notifyReselected) {
            RadioButtonField newButton = null;
            if (this._selected != -1) {
               newButton = (RadioButtonField)this._buttons.elementAt(this._selected);
               if (this._changeListener != null) {
                  try {
                     this._changeListener.fieldChanged(newButton, context);
                  } catch (Throwable var6) {
                  }
               }

               newButton.selectionChange(context);
            }
         }
      } else {
         RadioButtonField oldButton = null;
         if (this._selected != -1) {
            oldButton = (RadioButtonField)this._buttons.elementAt(this._selected);
         }

         this._selected = selected;
         if (oldButton != null) {
            oldButton.selectionChange(context);
         }

         RadioButtonField newButton = null;
         if (this._selected != -1) {
            newButton = (RadioButtonField)this._buttons.elementAt(this._selected);
            newButton.selectionChange(context);
         }

         if (this._changeListener != null) {
            if (oldButton != null) {
               try {
                  this._changeListener.fieldChanged(oldButton, context);
               } catch (Throwable var7) {
               }
            }

            if (newButton != null) {
               try {
                  this._changeListener.fieldChanged(newButton, context);
                  return;
               } catch (Throwable var8) {
               }
            }
         }
      }
   }
}
