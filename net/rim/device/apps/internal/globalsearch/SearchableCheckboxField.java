package net.rim.device.apps.internal.globalsearch;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.search.Searchable;

final class SearchableCheckboxField extends VerticalFieldManager implements FieldChangeListener {
   private Searchable _searchable;
   private VerticalFieldManager _vfm = new VerticalFieldManager();
   private CheckboxField[] _children;
   private CheckboxField _parent;
   private long _id;
   private long[] _ids;
   private boolean _handlingFieldChanged;

   final void populateChildren() {
      if (this._ids != null) {
         this._children = new CheckboxField[this._ids.length];
         int numIds = this._ids.length;

         for (int i = 0; i < numIds; i++) {
            this._children[i] = new CheckboxField(this._searchable.getName(this._ids[i]), false);
            this._children[i].setCookie(new Long(this._ids[i]));
            this._children[i].setChangeListener(this);
            this._vfm.add(this._children[i]);
         }

         HorizontalFieldManager hfm = new HorizontalFieldManager();
         hfm.add(new LabelField("  "));
         hfm.add(this._vfm);
         this.add(hfm);
      }
   }

   final SearchableWrapper getSearchableWrapper() {
      if (this._parent.getChecked() && this._ids == null) {
         return new SearchableWrapper(this._id);
      }

      if (this._ids != null) {
         long[] t = new long[0];

         for (int i = this._children.length - 1; i >= 0; i--) {
            if (this._children[i].getChecked()) {
               Long l = (Long)this._children[i].getCookie();
               Arrays.add(t, l.longValue());
            }
         }

         if (this._parent.getChecked() || t.length > 0) {
            return new SearchableWrapper(this._id, t);
         }
      }

      return null;
   }

   final void setChecked(boolean checked) {
      this._parent.setChecked(checked);
   }

   final boolean getChecked() {
      if (this._parent.getChecked()) {
         return true;
      }

      if (this._ids != null) {
         for (int i = this._children.length - 1; i >= 0; i--) {
            if (this._children[i].getChecked()) {
               return true;
            }
         }
      }

      return false;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized void fieldChanged(Field field, int context) {
      if (this._children != null && !this._handlingFieldChanged) {
         this._handlingFieldChanged = true;
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            if (field == this._parent) {
               boolean checked = this._parent.getChecked();

               for (int i = this._children.length - 1; i >= 0; i--) {
                  this._children[i].setChecked(checked);
               }

               var7 = false;
            } else {
               for (int i = this._children.length - 1; i >= 0; i--) {
                  if (field == this._children[i] && this._children[i].getChecked()) {
                     this._parent.setChecked(true);
                  }
               }

               var7 = false;
            }
         } finally {
            if (var7) {
               this._handlingFieldChanged = false;
            }
         }

         this._handlingFieldChanged = false;
      }
   }

   SearchableCheckboxField(Searchable searchable, long id, boolean advancedMode) {
      this._searchable = searchable;
      this._id = id;
      this._ids = this._searchable.getSearchableIds(advancedMode);
      this._parent = new CheckboxField(this._searchable.getName(id), this._searchable.isInitiallyEnabled(id));
      this._parent.setChangeListener(this);
      this.add(this._parent);
      this.populateChildren();
   }
}
