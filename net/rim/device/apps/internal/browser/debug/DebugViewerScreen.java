package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.browser.core.BrowserHotkeys;

public final class DebugViewerScreen extends AppsMainScreen {
   private VerticalFieldManager _vfm = new VerticalFieldManager();
   private RichTextField _textField = new RichTextField();
   private boolean _isPacketLog;
   private boolean _fullDetails;
   private int _nextMode = 1;
   private String _url;
   private String _title;
   private String _text;

   public DebugViewerScreen(String initialText, String title, String url) {
      this(initialText, title, url, false, false);
   }

   public DebugViewerScreen(String initialText, String title, String url, boolean isPacketLog, boolean fullDetails) {
      super(0);
      this._url = url;
      this._isPacketLog = isPacketLog;
      this._fullDetails = fullDetails;
      this._title = title;
      LabelField titleField = new LabelField(title);
      titleField.setFont(Font.getDefault().derive(1));
      this.setTitle(titleField);
      this._vfm.add(this._textField);
      this.add(this._vfm);
      this.setText(initialText);
      UiApplication.getUiApplication().pushScreen(this);
   }

   private final void setText(String text) {
      this._textField.setText(text);
      this._text = text;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (this._text != null && this._text.length() > 0) {
         menu.add(new DebugViewerScreen$PacketLoggerCopyVerb(this));
         menu.add(new SaveVerb(this._text, this._url));
         if (DeviceInfo.isSimulator()) {
            menu.add(new DumpVerb(this._text, this._url));
         }

         menu.add(new DebugInfoSendVerb(this._text, this._url == null ? this._title : "Source: " + this._url));
      }

      if (this._isPacketLog) {
         menu.add(new DebugViewerScreen$PacketLoggerSwitchViewVerb(this));
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key != ' ') {
         if (super.keyChar(key, status, time)) {
            return true;
         }

         switch (BrowserHotkeys.map(Character.toUpperCase(key))) {
            case 365:
               if (key == 27) {
                  this.close();
                  return true;
               }

               return false;
            case 366:
            default:
               this.scroll(1);
               return true;
            case 367:
               this.scroll(2);
               return true;
         }
      } else {
         int direction = (status & 2) == 0 ? 512 : 256;
         if (!this._isPacketLog) {
            this.scroll(direction);
            return true;
         }

         Field fieldWithFocus = this.getLeafFieldWithFocus();
         if (!(fieldWithFocus instanceof RichTextField)) {
            this.scroll(direction);
            return true;
         }

         RichTextField textField = (RichTextField)fieldWithFocus;
         int cursorPosition = textField.getCursorPosition();
         String text = textField.getText();
         if (direction == 512) {
            cursorPosition = text.indexOf(10, cursorPosition);
            if (cursorPosition != -1) {
               cursorPosition = text.indexOf("---", cursorPosition + 1);
            }

            if (cursorPosition == -1) {
               cursorPosition = text.length() - 1;
            }

            textField.setCursorPosition(cursorPosition);
            this.scroll(512);
            textField.setCursorPosition(cursorPosition);
            return true;
         } else {
            char[] textChars = text.toCharArray();
            int state = 0;
            cursorPosition--;

            for (; cursorPosition >= 0 && state != 5; cursorPosition--) {
               char currentChar = textChars[cursorPosition];
               switch (state) {
                  case -1:
                     break;
                  case 0:
                  default:
                     if (currentChar == ' ') {
                        state = 1;
                     }
                     break;
                  case 1:
                     if (currentChar == '-') {
                        state = 2;
                     } else if (currentChar == ' ') {
                        state = 1;
                     } else {
                        state = 0;
                     }
                     break;
                  case 2:
                     if (currentChar == '-') {
                        state = 3;
                     } else if (currentChar == ' ') {
                        state = 1;
                     } else {
                        state = 0;
                     }
                     break;
                  case 3:
                     if (currentChar == '-') {
                        state = 4;
                     } else if (currentChar == ' ') {
                        state = 1;
                     } else {
                        state = 0;
                     }
                     break;
                  case 4:
                     if (currentChar == '\n') {
                        cursorPosition++;
                        state = 5;
                     } else if (currentChar == ' ') {
                        state = 1;
                     } else {
                        state = 0;
                     }
               }
            }

            textField.setCursorPosition(++cursorPosition);
            return true;
         }
      }
   }
}
