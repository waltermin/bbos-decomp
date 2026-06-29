package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.i18n.CommonResource;

public class List extends Screen implements Choice {
   ChoiceGroup _list;
   private Command _selectCommand;
   public static final Command SELECT_COMMAND = new Command("", 1, 0);

   public void setSelectCommand(Command command) {
      synchronized (Application.getEventLock()) {
         if (this._list.getType() == 3) {
            if (command == SELECT_COMMAND) {
               if (this._selectCommand != command) {
                  this.removeCommand(this._selectCommand);
                  if (this._list.size() > 0) {
                     this.addCommand(command);
                  }

                  this._selectCommand = command;
               }
            } else if (command == null) {
               if (this._selectCommand != null) {
                  this.removeCommand(this._selectCommand);
               }

               this._selectCommand = null;
            } else {
               this.removeCommand(this._selectCommand);
               if (this._list.size() > 0) {
                  this.addCommand(command);
               }

               this._selectCommand = command;
            }
         }
      }
   }

   @Override
   public String getString(int elementNum) {
      return this._list.getString(elementNum);
   }

   @Override
   public Image getImage(int elementNum) {
      return this._list.getImage(elementNum);
   }

   @Override
   public int append(String stringPart, Image imagePart) {
      int result = this._list.append(stringPart, imagePart);
      if (this._list.getType() == 3 && this._list.size() == 1 && this._selectCommand != null) {
         this.addCommand(this._selectCommand);
      }

      return result;
   }

   @Override
   public void insert(int elementNum, String stringPart, Image imagePart) {
      this._list.insert(elementNum, stringPart, imagePart);
      if (this._list.getType() == 3 && this._list.size() == 1 && this._selectCommand != null) {
         this.addCommand(this._selectCommand);
      }
   }

   @Override
   public void delete(int elementNum) {
      this._list.delete(elementNum);
      if (this._list.getType() == 3 && this._list.size() == 0) {
         this.removeCommandInternal(this._selectCommand, false);
      }
   }

   @Override
   public void deleteAll() {
      this._list.deleteAll();
      if (this._list.getType() == 3) {
         this.removeCommandInternal(this._selectCommand, false);
      }
   }

   @Override
   public void set(int elementNum, String stringPart, Image imagePart) {
      this._list.set(elementNum, stringPart, imagePart);
   }

   @Override
   public boolean isSelected(int elementNum) {
      return this._list.isSelected(elementNum);
   }

   @Override
   public int getSelectedIndex() {
      synchronized (Application.getEventLock()) {
         int index = this._list.getSelectedIndex();
         if (index == -1 && this._list.getType() != 2 && this._list.size() > 0) {
            index = 0;
         }

         return index;
      }
   }

   @Override
   public int getSelectedFlags(boolean[] selectedArray_return) {
      return this._list.getSelectedFlags(selectedArray_return);
   }

   @Override
   public void setSelectedIndex(int elementNum, boolean selected) {
      this._list.setSelectedIndex(elementNum, selected);
   }

   @Override
   public void setSelectedFlags(boolean[] selectedArray) {
      this._list.setSelectedFlags(selectedArray);
   }

   @Override
   public int size() {
      return this._list.size();
   }

   @Override
   public void setFitPolicy(int fitPolicy) {
      this._list.setFitPolicy(fitPolicy);
   }

   @Override
   public int getFitPolicy() {
      return this._list.getFitPolicy();
   }

   @Override
   public void setFont(int elementNum, Font font) {
      this._list.setFont(elementNum, font);
   }

   @Override
   public Font getFont(int elementNum) {
      return this._list.getFont(elementNum);
   }

   private void init(String title, int listType, String[] stringElements, Image[] imageElements, boolean validateElements) {
      synchronized (Application.getEventLock()) {
         this.getPeer().setDisplayable(this);
         Manager container = new VerticalFieldManager(3459045988797251584L);
         this.getPeer().add(container);
         this._list = new ChoiceGroup(listType, stringElements, imageElements, validateElements);
         container.add(this._list.addToForm(null));
         this.setTitle(title);
         boolean addSelectCommand = listType == 3 && this._list.size() > 0;
         if (addSelectCommand) {
            this.addCommand(SELECT_COMMAND);
         }
      }
   }

   public List(String title, int listType, String[] stringElements, Image[] imageElements) {
      super(new MIDPScreen());
      this._selectCommand = SELECT_COMMAND;
      this.init(title, listType, stringElements, imageElements, true);
   }

   @Override
   public void removeCommand(Command cmd) {
      this.removeCommandInternal(cmd, true);
   }

   public List(String title, int listType) {
      super(new MIDPScreen());
      this._selectCommand = SELECT_COMMAND;
      this.init(title, listType, null, null, false);
   }

   private void removeCommandInternal(Command cmd, boolean canClearSelectCommand) {
      synchronized (Application.getEventLock()) {
         super.removeCommand(cmd);
         if (canClearSelectCommand && cmd == this._selectCommand) {
            this._selectCommand = null;
         }
      }
   }

   static {
      SELECT_COMMAND.setMenuLabel(CommonResource.getString(7));
   }
}
