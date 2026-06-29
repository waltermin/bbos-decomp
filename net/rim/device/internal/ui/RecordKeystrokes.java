package net.rim.device.internal.ui;

import java.util.Vector;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.vm.Message;
import net.rim.vm.MessageQueue;
import net.rim.vm.Process;

class RecordKeystrokes extends MainScreen {
   private Vector _messages = new Vector();
   private RichTextField _output;

   public RecordKeystrokes() {
      this.add(
         new RichTextField("Enter keystrokes and trackwheel actions to execute.  Hit 'w' when finished, 'q' to cancel. The actions will be repeated 100 times.")
      );
      this.add(new SeparatorField());
      this._output = new RichTextField("");
      this.add(this._output);
   }

   public void fillqueue(int repeat) {
      if (this._messages != null) {
         Message[] msgs = new Message[this._messages.size() * repeat];
         int i = 0;

         for (int r = 0; r < repeat; r++) {
            for (int m = 0; m < this._messages.size(); m++) {
               msgs[i] = new Message();
               msgs[i].copy((Message)this._messages.elementAt(m));
               i++;
            }
         }

         MessageQueue queue = Process.currentProcess().getMessageQueue();
         synchronized (queue) {
            int needed = queue.getSize() + msgs.length + 5;
            if (needed > queue.getMaxCapacity()) {
               queue.setMaxCapacity(needed);
            }

            i = 0;

            while (i < msgs.length && queue.enqueue(msgs[i])) {
               i++;
            }
         }
      }
   }

   private void output(String msg) {
      this._output.setText(this._output.getText() + msg + "\n");
      this._output.setCursorPosition(this._output.getText().length() - 1);
      this._output.setFocus();
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      boolean handled = false;
      switch (Keypad.map(keycode)) {
         case 'q':
            this._messages = null;
            handled = true;
         case 'w':
            UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
            handled = true;
            break;
         default:
            this._messages.addElement(new Message(2, 513, Keypad.key(keycode), Keypad.status(keycode), 0));
            this.output("keyDown '" + keycode + '\'');
      }

      if (!handled) {
         handled = super.keyDown(keycode, time);
      }

      return handled;
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      this._messages.addElement(new Message(2, 515, Keypad.key(keycode), Keypad.status(keycode), 0));
      this.output("keyUp '" + keycode + '\'');
      return true;
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      this._messages.addElement(new Message(2, 514, Keypad.key(keycode), Keypad.status(keycode), 0));
      this.output("keyRepeat '" + keycode + '\'');
      return true;
   }

   @Override
   protected boolean keyStatus(int keycode, int time) {
      this._messages.addElement(new Message(2, 520, Keypad.key(keycode), Keypad.status(keycode), 0));
      this.output("keyStatus");
      return true;
   }

   @Override
   public boolean trackwheelRoll(int amount, int status, int time) {
      this._messages.addElement(new Message(2, 519, amount, status, 0));
      if (amount > 0) {
         this.output("trackwheelRollDown of " + amount);
         return true;
      } else {
         this.output("trackwheelRollUp of " + -amount);
         return true;
      }
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      this._messages.addElement(new Message(2, 516, 0, status, 0));
      this.output("trackwheelClick");
      return true;
   }

   @Override
   public boolean trackwheelUnclick(int status, int time) {
      this._messages.addElement(new Message(2, 517, 0, status, 0));
      this.output("trackwheelUnclick");
      return true;
   }
}
