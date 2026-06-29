package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.SimpleSortingVector;

public class Alert extends Screen {
   private DialogFieldManager _dfm;
   private RichTextField _title;
   private BitmapField _image;
   private Image _midpImage;
   private RichTextField _text;
   private AlertType _alertType;
   private int _timeout = this.getDefaultTimeout();
   private Display _display;
   private Displayable _nextDisplayable;
   private Gauge _indicator;
   private CommandListener _commandListener;
   private SimpleSortingVector _commands;
   private VerticalFieldManager _buttonContainer;
   private static int _timerId = -1;
   public static final int FOREVER = -2;
   public static final Command DISMISS_COMMAND = new Command("", 4, 0);

   public Alert(String title) {
      super(null);
      this.init(title, null, null, null);
   }

   public Alert(String title, String alertText, Image alertImage, AlertType alertType) {
      super(null);
      this.init(title, alertText, alertImage, alertType);
   }

   private void init(String title, String alertText, Image alertImage, AlertType alertType) {
      synchronized (Application.getEventLock()) {
         this._commands = new SimpleSortingVector();
         this._commands.setSortComparator(new Alert$CommandComparator());
         this._commands.setSort(true);
         this._buttonContainer = new VerticalFieldManager(281474976710656L);
         this._dfm = new DialogFieldManager();
         this._dfm.addCustomField(this._buttonContainer);
         this.setPeer(new MIDPAlert(this._dfm, alertType));
         this.getPeer().setDisplayable(this);
         this._alertType = alertType;
         this.setImage(alertImage);
         this.setTitle(title);
         this.setString(alertText);
      }
   }

   void show(Display display, Displayable nextDisplayable) {
      this._display = display;
      this._nextDisplayable = nextDisplayable;
      this.getPeer().doLayout();
      this.updateModalState();
      if (this._timeout > 0) {
         _timerId = Application.getApplication().invokeLater(new Alert$AlertRunnable(this), this._timeout, false);
      }
   }

   void dismiss() {
      if (_timerId != -1) {
         Application.getApplication().cancelInvokeLater(_timerId);
         _timerId = -1;
      }

      if (this._commandListener != null) {
         Command command;
         if (this._buttonContainer.getFieldCount() == 0) {
            command = DISMISS_COMMAND;
         } else {
            command = (Command)this._buttonContainer.getFieldWithFocus().getCookie();
         }

         this._commandListener.commandAction(command, this);
      }

      if (this._nextDisplayable != null && this._display.getCurrent() == this) {
         this._display.setCurrent(this._nextDisplayable);
         this._nextDisplayable = null;
      }
   }

   public int getDefaultTimeout() {
      synchronized (Application.getEventLock()) {
         return 4000;
      }
   }

   public int getTimeout() {
      synchronized (Application.getEventLock()) {
         return this._timeout;
      }
   }

   public void setTimeout(int time) {
      synchronized (Application.getEventLock()) {
         if (time <= 0 && time != -2) {
            throw new IllegalArgumentException();
         }

         this._timeout = time;
         if (time == -2 && _timerId != -1) {
            Application.getApplication().cancelInvokeLater(_timerId);
            _timerId = -1;
         }
      }
   }

   public AlertType getType() {
      synchronized (Application.getEventLock()) {
         return this._alertType;
      }
   }

   public void setType(AlertType type) {
      synchronized (Application.getEventLock()) {
         this._alertType = type;
         if (this._midpImage == null) {
            this.setImage(null);
         }
      }
   }

   public String getString() {
      synchronized (Application.getEventLock()) {
         return this._text == null ? null : this._text.getText();
      }
   }

   public void setString(String str) {
      synchronized (Application.getEventLock()) {
         Manager mgr = this._dfm.getCustomManager();
         if (str == null) {
            if (this._text != null) {
               mgr.delete(this._text);
               this._text = null;
            }
         } else {
            if (this._text == null) {
               this._text = new RichTextField(str, 36028797018963968L);
               this._dfm.addCustomField(this._text);
            } else {
               this._text.setText(str);
            }

            this.updateModalState();
         }
      }
   }

   public Image getImage() {
      synchronized (Application.getEventLock()) {
         return this._midpImage;
      }
   }

   public void setImage(Image img) {
      synchronized (Application.getEventLock()) {
         if (img != null) {
            if (this._image != null) {
               this._image.setBitmap(img.getBitmap());
               this._midpImage = img;
               return;
            }

            this._image = new BitmapField(img.getBitmap());
         } else {
            int type = 0;
            if (this._alertType == AlertType.ALARM || this._alertType == AlertType.ERROR) {
               type = 2;
            } else if (this._alertType == AlertType.CONFIRMATION) {
               type = 1;
            }

            this._image = new BitmapField(Bitmap.getPredefinedBitmap(type));
         }

         this._midpImage = img;
         this._dfm.setIcon(this._image);
      }
   }

   public void setIndicator(Gauge indicator) {
      if (indicator == null
         || !indicator.isInteractive()
            && indicator.getOwner() == null
            && indicator.getCommands().size() == 0
            && indicator.getItemCommandListener() == null
            && indicator.getLabel() == null
            && indicator._preferredHeight == -1
            && indicator._preferredWidth == -1
            && indicator.getLayout() == 0) {
         if (this._indicator != null) {
            this._dfm.deleteCustomField(this._indicator.getPeer());
         }

         if (indicator != null) {
            this._dfm.addCustomField(indicator.getPeer());
            indicator.setOwner(this);
         }

         this._indicator = indicator;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Gauge getIndicator() {
      return this._indicator;
   }

   @Override
   public void addCommand(Command cmd) {
      if (cmd == null) {
         throw new NullPointerException();
      }

      if (cmd != DISMISS_COMMAND) {
         synchronized (Application.getEventLock()) {
            this._commands.addElement(cmd);
            int commandIndex = this._commands.find(cmd);
            ButtonField buttonField = new ButtonField(cmd.getLabel(), 18014398509481984L);
            buttonField.setCookie(cmd);
            this._buttonContainer.insert(buttonField, commandIndex);
            this.updateModalState();
            if (commandIndex == 0 && this._buttonContainer.isVisible()) {
               this._buttonContainer.setFieldWithFocus(buttonField);
            }
         }
      }
   }

   @Override
   public void removeCommand(Command cmd) {
      if (cmd != null && cmd != DISMISS_COMMAND) {
         synchronized (Application.getEventLock()) {
            int commandIndex = this._commands.find(cmd);
            if (commandIndex >= 0) {
               this._commands.removeElementAt(commandIndex);
               if (commandIndex == 0 && this._buttonContainer.getFieldCount() > 1) {
                  this._buttonContainer.setFieldWithFocus(this._buttonContainer.getField(1));
               }

               this._buttonContainer.deleteRange(commandIndex, 1);
            }

            this.updateModalState();
         }
      }
   }

   @Override
   public void setCommandListener(CommandListener l) {
      synchronized (Application.getEventLock()) {
         this._commandListener = l;
      }
   }

   @Override
   public String getTitle() {
      synchronized (Application.getEventLock()) {
         return this._title == null ? null : this._title.getText();
      }
   }

   @Override
   public void setTitle(String title) {
      synchronized (Application.getEventLock()) {
         if (this._title == null) {
            if (title != null) {
               this._title = new RichTextField(title, 36028797018963968L);
               this._dfm.setMessage(this._title);
            }
         } else if (title != null) {
            this._title.setText(title);
         } else {
            this._dfm.setMessage(null);
            this._title = null;
         }
      }
   }

   private void updateModalState() {
      Manager mgr = this._dfm.getCustomManager();
      boolean makeModal = mgr.getVirtualHeight() > mgr.getHeight() || this._buttonContainer.getFieldCount() > 1;
      if (!makeModal && this._text != null) {
         makeModal = this._text.getHeight() > this._dfm.getHeight();
      }

      if (makeModal) {
         this.setTimeout(-2);
      }
   }
}
