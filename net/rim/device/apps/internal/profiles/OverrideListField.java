package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.SystemIcon;
import net.rim.vm.Array;

class OverrideListField extends CollectionListField implements VerbProvider, ListFieldCallback {
   private boolean _movingOverrideState;

   OverrideListField(ProfilesScreen screen) {
      super(Overrides.getInstance(), null);
      this.setCallback(this);
   }

   private boolean handleRoll(int amount, int status, int time) {
      if ((status & 1) == 0 && !this._movingOverrideState) {
         return false;
      } else if (amount > 0) {
         this.moveOverrideDown();
         return true;
      } else {
         this.moveOverrideUp();
         return true;
      }
   }

   private boolean handleClick(int status, int time) {
      if (this._movingOverrideState) {
         this._movingOverrideState = false;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean trackwheelRoll(int amount, int status, int time) {
      return this.handleRoll(amount, status, time) ? true : super.trackwheelRoll(amount, status, time);
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      return this.handleClick(status, time) ? true : super.trackwheelClick(status, time);
   }

   @Override
   protected boolean navigationUnclick(int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      return this.handleRoll(dy, status, time) ? true : super.navigationMovement(dx, dy, status, time);
   }

   @Override
   protected boolean invokeAction(int action) {
      if (!this._movingOverrideState) {
         switch (action) {
            case 1:
               Override o = (Override)this.getSelectedElement();
               if (o != null) {
                  new EnableOverrideVerb(o).invoke(null);
                  this.invalidate();
                  return true;
               }
         }
      }

      return super.invokeAction(action);
   }

   private void moveOverride(int index, int adjust) {
      Overrides overrides = (Overrides)super._list;
      overrides.switchOrder(index, index + adjust);
      this.invalidate();
   }

   void toggleMoveOverrideState() {
      this._movingOverrideState = !this._movingOverrideState;
   }

   boolean moveOverrideUp() {
      int size = this.getSize();
      int index = this.getSelectedIndex();
      if (index == 0) {
         return true;
      } else if (index < size) {
         this.moveOverride(index, -1);
         this.setSelectedIndex(index - 1);
         return true;
      } else {
         return false;
      }
   }

   boolean moveOverrideDown() {
      int size = this.getSize();
      int index = this.getSelectedIndex();
      if (index == size - 1) {
         return true;
      } else if (index < size && size > 0) {
         this.moveOverride(index, 1);
         this.setSelectedIndex(index + 1);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Verb getVerbs(Object contextObject, Verb[] verbArray) {
      Verb defaultVerb = null;
      Array.resize(verbArray, 0);
      Arrays.add(verbArray, new EditOverrideVerb(null));
      Override override = (Override)this.getSelectedElement();
      if (override != null) {
         defaultVerb = new EditOverrideVerb(override);
         Arrays.add(verbArray, defaultVerb);
         Arrays.add(verbArray, new DeleteOverrideVerb(override));
         if (!Profiles.getInstance().isOffEnabled()) {
            Verb enableVerb = new EnableOverrideVerb(override);
            Arrays.add(verbArray, enableVerb);
            if (!override.isEnabled()) {
               defaultVerb = enableVerb;
            }
         }

         if (this.getSize() > 1) {
            Arrays.add(verbArray, new MoveOverrideVerb(this));
         }
      }

      return defaultVerb;
   }

   @Override
   protected boolean keyChar(char keyChar, int statusInt, int timeInt) {
      Override override = (Override)this.getSelectedElement();
      if (override != null) {
         switch (keyChar) {
            case '\n':
            case ' ':
               new EnableOverrideVerb(override).invoke(null);
               return true;
            case 'd':
               this.moveOverrideDown();
               return true;
            case 'u':
               this.moveOverrideUp();
               return true;
            case '\u007f':
               new DeleteOverrideVerb(override).invoke(null);
               return false;
            default:
               return super.keyChar(keyChar, statusInt, timeInt);
         }
      } else {
         return super.keyChar(keyChar, statusInt, timeInt);
      }
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return super.getPreferredWidth();
   }

   @Override
   public Object get(ListField listField, int index) {
      return null;
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public void drawListRow(ListField aListField, Graphics aGraphics, int indexInt, int yInt, int widthInt) {
      int indent = 10;
      if (indexInt == 0 && Overrides.getInstance().size() == 0) {
         ResourceBundleFamily resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         aGraphics.drawText(resources.getString(252), indent, yInt, 64, widthInt);
      } else {
         IconCollection icon = SystemIcon.COLLECTION;
         Override override = (Override)this.getElementAt(indexInt);
         if (override != null) {
            int box = 7;
            if (!Profiles.getInstance().isOffEnabled()) {
               box = override.isEnabled() ? 1 : 0;
            }

            int boxWidth = icon.getWidth(aGraphics.getFont());
            icon.paint(aGraphics, indent, yInt, box);
            int x = indent + 3 + boxWidth;
            widthInt -= x;
            aGraphics.drawText(override.getName(), x, yInt, 64, widthInt);
         }
      }
   }
}
