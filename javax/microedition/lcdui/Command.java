package javax.microedition.lcdui;

public class Command {
   private String _label;
   private String _longLabel;
   private String _menuLabel;
   private int _commandType;
   private int _priority;
   public static final int SCREEN = 1;
   private static final int FIRST_COMMAND_TYPE = 1;
   public static final int BACK = 2;
   public static final int CANCEL = 3;
   public static final int OK = 4;
   public static final int HELP = 5;
   public static final int STOP = 6;
   public static final int EXIT = 7;
   public static final int ITEM = 8;
   private static final int LAST_COMMAND_TYPE = 8;

   public Command(String label, int commandType, int priority) {
      label.length();
      if (commandType >= 1 && commandType <= 8) {
         this._label = label;
         this._menuLabel = label;
         this._commandType = commandType;
         this._priority = priority;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Command(String shortLabel, String longLabel, int commandType, int priority) {
      this(shortLabel, commandType, priority);
      if (longLabel != null) {
         this._longLabel = longLabel;
         this._menuLabel = longLabel;
      }
   }

   public String getLabel() {
      return this._label;
   }

   public String getLongLabel() {
      return this._longLabel;
   }

   public int getCommandType() {
      return this._commandType;
   }

   public int getPriority() {
      return this._priority;
   }

   String getMenuLabel() {
      return this._menuLabel;
   }

   void setMenuLabel(String menuLabel) {
      this._menuLabel = menuLabel;
   }
}
