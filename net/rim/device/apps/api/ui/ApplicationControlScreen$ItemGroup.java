package net.rim.device.apps.api.ui;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.ui.SystemIcon;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

final class ApplicationControlScreen$ItemGroup extends VerticalIndentFieldManager implements FieldChangeListener {
   String _label;
   String _helpCardId;
   HorizontalFieldManager _hfm;
   SeparatorField _sf;
   ApplicationControlScreen$ItemField _group;
   Vector _items;
   ImageField _icon;
   int _indent;
   boolean _expanded;
   boolean _inFieldChanged;
   private final ApplicationControlScreen this$0;

   public final void addItem(ApplicationControlScreen$ItemField item) {
      if (this.this$0._showingAll || !this.this$0._werePermissionsSupplied || ApplicationControl.isBitSet(this.this$0._suppliedPermissions, item._allowFlag)) {
         this._items.addElement(item);
         item.setChangeListener(this);
      }
   }

   public final void showItems() {
      boolean dirty = this.isDirty();
      ApplicationControlScreen$ItemField[] fields = this.getItems();
      if (fields != null && !this._expanded) {
         this._icon.setImage(SystemIcon.COLLECTION.getImage(5));

         for (int i = 0; i < fields.length; i++) {
            super.add(fields[i], this._indent);
         }

         super.add(this._sf, this._indent);
         this._expanded = true;
         if (dirty) {
            this.setDirty(true);
         }
      }
   }

   public final void hideItems() {
      boolean dirty = this.isDirty();
      ApplicationControlScreen$ItemField[] fields = this.getItems();
      if (fields != null && this._expanded) {
         this._icon.setImage(SystemIcon.COLLECTION.getImage(6));

         for (int i = 0; i < fields.length; i++) {
            super.delete(fields[i]);
         }

         super.delete(this._sf);
         this._expanded = false;
         if (dirty) {
            this.setDirty(true);
         }
      }
   }

   public final ApplicationControlScreen$ItemField[] getItems() {
      if (this._items.size() < 1) {
         return new ApplicationControlScreen$ItemField[0];
      }

      ApplicationControlScreen$ItemField[] fields = new ApplicationControlScreen$ItemField[this._items.size()];
      Enumeration e = this._items.elements();
      int i = 0;

      while (e.hasMoreElements()) {
         fields[i++] = (ApplicationControlScreen$ItemField)e.nextElement();
      }

      return fields;
   }

   public final void update() {
      ApplicationControlScreen$ItemField[] fields = this.getItems();
      boolean same = true;
      boolean bold = false;

      for (int i = fields.length - 1; i > 0; i--) {
         if (fields[i].getChoice(fields[i].getSelectedIndex()) != fields[i - 1].getChoice(fields[i - 1].getSelectedIndex())) {
            same = false;
         }

         if (fields[i].isBold(fields[i].getSelectedIndex())) {
            bold = true;
         }
      }

      if (same) {
         int x = fields[0].getSelectedIndex();
         this._group
            .setChoices(
               bold ? ApplicationControlScreen.ALLOW_DEFAULT_DENY : ApplicationControlScreen.ALLOW_DENY,
               bold ? ApplicationControlScreen.ALLOW_DEFAULT_DENY_BA : ApplicationControlScreen.ALLOW_DENY_BN
            );
         this._group.setSelectedIndex(fields[0].getChoice(x));
      } else {
         this._group
            .setChoices(
               bold ? ApplicationControlScreen.ALLOW_CUSTOM_DEFAULT_DENY : ApplicationControlScreen.ALLOW_CUSTOM_DENY,
               bold ? ApplicationControlScreen.ALLOW_CUSTOM_DEFAULT_DENY_BA : ApplicationControlScreen.ALLOW_CUSTOM_DENY_BN
            );
         this._group.setSelectedIndex(ApplicationControlScreen.CUSTOM);
      }
   }

   public final int getNumberOfItems() {
      return this._items.size();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void fieldChanged(Field field, int context) {
      if (!this._inFieldChanged) {
         boolean var10 = false /* VF: Semaphore variable */;

         label68: {
            try {
               var10 = true;
               this._inFieldChanged = true;
               if (!(field instanceof ApplicationControlScreen$ItemField)) {
                  var10 = false;
                  break label68;
               }

               ApplicationControlScreen$ItemField item = (ApplicationControlScreen$ItemField)field;
               if (item == this._group) {
                  int index = this._group.getSelectedIndex();
                  String selection = (String)this._group.getChoice(index);
                  ApplicationControlScreen$ItemField[] fields = this.getItems();
                  if (selection.equals(ApplicationControlScreen.DEFAULT)) {
                     this.selectDefaults(fields);
                  } else if (!selection.equals(ApplicationControlScreen.CUSTOM)) {
                     for (int i = fields.length - 1; i >= 0; i--) {
                        fields[i].setSelectedIndex(selection);
                     }
                  }

                  this.setDirty(true);
               }

               this.update();
               var10 = false;
            } finally {
               if (var10) {
                  this._inFieldChanged = false;
               }
            }

            this._inFieldChanged = false;
            return;
         }

         this._inFieldChanged = false;
      }
   }

   private final void selectDefaults(ApplicationControlScreen$ItemField[] fields) {
      for (int i = 0; i < fields.length; i++) {
         ApplicationControlScreen$ItemField f = fields[i];
         f.setSelectedIndex(this.this$0._policySettingPresent ? 0 : f._choices.length - 1);
      }
   }

   @Override
   public final String toString() {
      return this._group.getLabel();
   }

   public ApplicationControlScreen$ItemGroup(ApplicationControlScreen _1, String name, String helpCardId) {
      super(281474976710656L);
      this.this$0 = _1;
      this._items = new Vector();
      this._label = name;
      this._helpCardId = helpCardId;
      this._group = new ApplicationControlScreen$ItemField(name, ApplicationControlScreen.ALLOW_DENY);
      this._group.setChangeListener(this);
      Font f = this._group.getFont();
      int size = f.getHeight() - 3;
      this._indent = size + 3;
      this._expanded = false;
      this._icon = new ImageField(51539607552L);
      this._icon.setPreferredSize(size, size);
      this._icon.setImage(SystemIcon.COLLECTION.getImage(6));
      this._sf = new SeparatorField();
      this._hfm = new HorizontalFieldManager();
      this._hfm.add(this._icon);
      this._hfm.add(this._group);
      this.add(this._hfm);
   }
}
