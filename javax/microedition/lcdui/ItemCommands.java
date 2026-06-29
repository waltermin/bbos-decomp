package javax.microedition.lcdui;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.SimpleSortingVector;

class ItemCommands implements Comparator {
   private SimpleSortingVector _commands = new SimpleSortingVector();
   private Command _defaultCommand;

   public SimpleSortingVector getCommands() {
      return this._commands;
   }

   public void addCommand(Command cmd) {
      if (cmd != null) {
         int n = this._commands.size();

         for (int i = 0; i < n; i++) {
            if (this._commands.elementAt(i) == cmd) {
               return;
            }
         }

         this._commands.addElement(cmd);
      }
   }

   public void removeCommand(Command cmd) {
      if (cmd != null && this._commands != null) {
         int n = this._commands.size();

         for (int i = 0; i < n; i++) {
            if (this._commands.elementAt(i) == cmd) {
               this._commands.removeElementAt(i);
               if (cmd == this._defaultCommand) {
                  this._defaultCommand = null;
                  return;
               }
               break;
            }
         }
      }
   }

   public void setDefaultCommand(Command c) {
      this._defaultCommand = c;
      if (this._defaultCommand != null) {
         this._commands.addElement(this._defaultCommand);
      }
   }

   @Override
   public int compare(Object o1, Object o2) {
      if (this._defaultCommand != null && o1 == this._defaultCommand) {
         return -1;
      } else {
         return this._defaultCommand != null && o2 == this._defaultCommand ? 1 : ((Command)o1).getPriority() - ((Command)o2).getPriority();
      }
   }

   public ItemCommands() {
      this._commands.setSortComparator(this);
      this._commands.setSort(true);
   }
}
