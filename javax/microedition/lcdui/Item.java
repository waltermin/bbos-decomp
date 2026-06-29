package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.util.SimpleSortingVector;

public class Item {
   private Field _peer;
   private int _layoutMode;
   int _preferredWidth = -1;
   int _preferredHeight = -1;
   private Screen _owner;
   private ItemCommandListener _itemCommandListener;
   private ItemCommands _commands = new ItemCommands();
   public static final int LAYOUT_DEFAULT = 0;
   public static final int LAYOUT_LEFT = 1;
   public static final int LAYOUT_RIGHT = 2;
   public static final int LAYOUT_CENTER = 3;
   public static final int LAYOUT_TOP = 16;
   public static final int LAYOUT_BOTTOM = 32;
   public static final int LAYOUT_VCENTER = 48;
   public static final int LAYOUT_NEWLINE_BEFORE = 256;
   public static final int LAYOUT_NEWLINE_AFTER = 512;
   public static final int LAYOUT_SHRINK = 1024;
   public static final int LAYOUT_EXPAND = 2048;
   public static final int LAYOUT_VSHRINK = 4096;
   public static final int LAYOUT_VEXPAND = 8192;
   public static final int LAYOUT_2 = 16384;
   public static final int PLAIN = 0;
   public static final int HYPERLINK = 1;
   public static final int BUTTON = 2;
   static final int VALID_LAYOUT = 32563;
   static final int FIELD_LAYOUT = 0;
   static final int DRAW_STYLE_LAYOUT = 1;
   static final int RICH_TEXT_FIELD_LAYOUT = 2;

   static final long getFieldLayoutStyle(int midpLayoutStyle, int layout) {
      long result;
      int verticalLayout;
      int horizontalExpandLayout;
      int verticalExpandLayout;
      result = 0;
      int horizontalLayout = midpLayoutStyle & 15;
      verticalLayout = midpLayoutStyle & 240;
      horizontalExpandLayout = midpLayoutStyle & 3840;
      verticalExpandLayout = midpLayoutStyle & 61440;
      label51:
      switch (horizontalLayout) {
         case 0:
            break;
         case 1:
            switch (layout) {
               case 0:
                  result |= 4294967296L;
                  break label51;
               case 1:
               default:
                  result |= 6;
                  break label51;
               case 2:
                  result |= 0;
                  break label51;
            }
         case 2:
            switch (layout) {
               case 0:
                  result |= 8589934592L;
                  break label51;
               case 1:
               default:
                  result |= 5;
                  break label51;
               case 2:
                  result |= 524288;
                  break label51;
            }
         case 3:
         default:
            switch (layout) {
               case 0:
                  result |= 12884901888L;
                  break;
               case 1:
               default:
                  result |= 4;
                  break;
               case 2:
                  result |= 262144;
            }
      }

      switch (verticalLayout) {
         case 16:
            result |= layout == 1 ? 48 : 17179869184L;
            break;
         case 32:
            result |= layout == 1 ? 40 : 34359738368L;
            break;
         case 48:
            result |= layout == 1 ? 32 : 51539607552L;
      }

      if (horizontalExpandLayout == 2048) {
         result |= 1152921504606846976L;
      }

      if (verticalExpandLayout == 8192) {
         result |= 2305843009213693952L;
      }

      return result;
   }

   final Screen getOwner() {
      synchronized (Application.getEventLock()) {
         return this._owner;
      }
   }

   void setOwner(Screen owner) {
      synchronized (Application.getEventLock()) {
         if (this._owner != null && owner != null) {
            throw new IllegalStateException();
         }

         this._owner = owner;
      }
   }

   void setPeer(Field peer) {
      synchronized (Application.getEventLock()) {
         this._peer = peer;
      }
   }

   Field getPeer() {
      synchronized (Application.getEventLock()) {
         return this._peer;
      }
   }

   Item() {
   }

   public void setLabel(String label) {
   }

   public String getLabel() {
      return null;
   }

   public int getLayout() {
      synchronized (Application.getEventLock()) {
         return this._layoutMode;
      }
   }

   public void setLayout(int layout) {
      synchronized (Application.getEventLock()) {
         if ((layout & ~VALID_LAYOUT) != 0) {
            throw new IllegalArgumentException();
         }

         if (this._owner instanceof Alert) {
            throw new IllegalStateException();
         }

         this._layoutMode = layout;
      }
   }

   public void addCommand(Command cmd) {
      synchronized (Application.getEventLock()) {
         cmd.getPriority();
         if (this._owner != null && this._owner instanceof Alert) {
            throw new IllegalStateException();
         }

         this._commands.addCommand(cmd);
      }
   }

   public void removeCommand(Command cmd) {
      synchronized (Application.getEventLock()) {
         this._commands.removeCommand(cmd);
      }
   }

   public void setItemCommandListener(ItemCommandListener l) {
      synchronized (Application.getEventLock()) {
         if (this._owner != null && this._owner instanceof Alert) {
            throw new IllegalStateException();
         }

         this._itemCommandListener = l;
      }
   }

   ItemCommandListener getItemCommandListener() {
      synchronized (Application.getEventLock()) {
         return this._itemCommandListener;
      }
   }

   public int getPreferredWidth() {
      synchronized (Application.getEventLock()) {
         return this._preferredWidth < 0 ? this.getMinimumWidth() : this._preferredWidth;
      }
   }

   public int getPreferredHeight() {
      synchronized (Application.getEventLock()) {
         return this._preferredHeight < 0 ? this.getMinimumHeight() : this._preferredHeight;
      }
   }

   public void setPreferredSize(int width, int height) {
      if (width >= -1 && height >= -1) {
         synchronized (Application.getEventLock()) {
            if (this._owner != null && this._owner instanceof Alert) {
               throw new IllegalStateException();
            }

            int minWidth = this.getMinimumWidth();
            int minHeight = this.getMinimumHeight();
            this._preferredWidth = width != -1 && width < minWidth ? minWidth : width;
            this._preferredHeight = height != -1 && height < minHeight ? minHeight : height;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getMinimumWidth() {
      synchronized (Application.getEventLock()) {
         return this._peer.getPreferredWidth();
      }
   }

   public int getMinimumHeight() {
      synchronized (Application.getEventLock()) {
         return this._peer.getPreferredHeight();
      }
   }

   public void setDefaultCommand(Command cmd) {
      synchronized (Application.getEventLock()) {
         if (this._owner != null && this._owner instanceof Alert) {
            throw new IllegalStateException();
         }

         this._commands.setDefaultCommand(cmd);
      }
   }

   public void notifyStateChanged() {
      synchronized (Application.getEventLock()) {
         Form ownerForm = (Form)this._owner;
         if (ownerForm == null) {
            throw new IllegalStateException();
         }

         ItemStateListener ownerFormISL = ownerForm.getItemStateListener();
         if (ownerFormISL != null) {
            ownerFormISL.itemStateChanged(this);
         }
      }
   }

   SimpleSortingVector getCommands() {
      synchronized (Application.getEventLock()) {
         return this._commands.getCommands();
      }
   }

   Field addToForm(FieldChangeListener _1) {
      throw null;
   }
}
