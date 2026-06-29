package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.ui.container.FrameLayout;

public class TextInputField extends FrameLayout {
   protected BasicEditField _edit;
   public static Tag BROWSER_EDIT_TAG = Tag.create("browser-edit");
   public static final byte FLAG_PASSWORD = 1;
   public static final int DEFAULT_INPUT_SIZE = 15;
   private static final String EMAIL_FORMAT_IDENTIFIER = "XXX_RIM_EMAIL_INPUT";

   public TextInputField(BasicEditField edit, int size, boolean wrap, long style) {
      super(style);
      this._edit = edit;
      this._edit.setTag(BROWSER_EDIT_TAG);
      if (size < 1 && wrap) {
         this.add(this._edit);
      } else {
         RigidManager manager = new RigidManager(size > 0 ? size : 15, 1, 1126037345796096L, this._edit.getFont());
         manager.add(this._edit);
         this.add(manager);
      }
   }

   public TextInputField(String title, String initValue, int size, int maxLength, Font font, byte flags, long style) {
      this(title, initValue, size, maxLength, font, flags, style, null);
   }

   public TextInputField(String title, String initValue, int size, int maxLength, Font font, byte flags, long style, String format) {
      super(style);
      if (maxLength > 0) {
         if (initValue != null) {
            int resetLength = initValue.length();
            if (maxLength < resetLength) {
               maxLength = resetLength;
            }
         }
      } else {
         maxLength = 1000000;
      }

      if ((flags & 1) != 0) {
         if (format != null) {
            this._edit = new BrowserPasswordEditField(initValue, maxLength, style | 137438953472L, format);
         } else {
            this._edit = new PasswordEditField(null, initValue, maxLength, style | 137438953472L);
            ((PasswordEditField)this._edit).setAllowUnicodeInput(true);
         }
      } else if (format != null) {
         if (StringUtilities.strEqualIgnoreCase(format, "XXX_RIM_EMAIL_INPUT", 1701707776)) {
            this._edit = new EmailAddressEditField(null, initValue, maxLength, style | 137438953472L);
         } else {
            this._edit = new BrowserEditField(initValue, maxLength, style | 137438953472L, format);
         }
      } else {
         this._edit = new EditField(null, initValue, maxLength, style | 137438953472L);
      }

      this._edit.setTag(BROWSER_EDIT_TAG);
      RigidManager manager = new RigidManager(size > 0 ? size : 15, 1, 1126037345796096L, font);
      manager.add(this._edit);
      this.add(manager);
   }

   public void setTextWithTruncation(String str) {
      int maxSize = this._edit.getMaxSize();
      if (str.length() > maxSize) {
         str = str.substring(0, maxSize);
      }

      this._edit.setText(str);
   }

   @Override
   public void setFont(Font f) {
      if (this._edit != null) {
         this._edit.setFont(f);
      }

      super.setFont(f);
   }

   public String getText() {
      return this._edit.getText();
   }
}
