package net.rim.device.apps.api.ribbon;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.utility.props.BooleanProps;
import net.rim.device.apps.api.utility.props.CharProps;
import net.rim.device.apps.api.utility.props.IntegerProps;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.api.utility.props.StringProps;

public class Action implements EntryPointDescriptor, IntegerProps, StringProps, ObjectProps, CharProps, BooleanProps, Runnable {
   private char _hotKeyCache;
   private ApplicationDescriptor _applicationDescriptor;
   private String _entryName;
   private Integer _ribbonPosition;

   protected String getState() {
      return null;
   }

   public void register() {
      RibbonLauncher launcher = RibbonLauncher.getInstance();
      if (launcher != null) {
         launcher.registerAction(this._entryName, this);
      }
   }

   public void unregister() {
      RibbonLauncher launcher = RibbonLauncher.getInstance();
      if (launcher != null) {
         launcher.unregisterAction(this._entryName);
      }
   }

   public void update() {
      RibbonLauncher launcher = RibbonLauncher.getInstance();
      if (launcher != null) {
         launcher.updateRegisteredAction(this._entryName);
      }
   }

   protected String getDescription() {
      throw null;
   }

   protected Object getExtraInfo() {
      return null;
   }

   @Override
   public Object get(long propID, Object defaultReturned) {
      return propID == 12 ? this.getExtraInfo() : defaultReturned;
   }

   @Override
   public String get(long propID, String defaultReturned) {
      if (propID == 1) {
         return this._entryName;
      } else if (propID == 3) {
         String description = this.getDescription();
         this._hotKeyCache = this.getHotKey(description);
         return description;
      } else if (propID == 2) {
         String description = this.getState();
         this._hotKeyCache = this.getHotKey(description);
         return description;
      } else {
         return defaultReturned;
      }
   }

   @Override
   public Integer get(long propID, Integer defaultReturned) {
      return propID == 6 ? this._ribbonPosition : defaultReturned;
   }

   @Override
   public Boolean get(long propID, Boolean defaultReturned) {
      return defaultReturned;
   }

   @Override
   public char get(long propID, char defaultReturned) {
      return propID == 8 && this._hotKeyCache != 0 ? this._hotKeyCache : defaultReturned;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      try {
         ApplicationManager.getApplicationManager().runApplication(this._applicationDescriptor);
      } catch (Throwable var3) {
         throw new Object(e.getMessage());
      }
   }

   @Override
   public void set(long propID, char valueToSet) {
   }

   @Override
   public void set(long propID, Boolean valueToSet) {
   }

   @Override
   public void set(long propID, Integer valueToSet) {
   }

   @Override
   public void set(long propID, Object valueToSet) {
   }

   @Override
   public void set(long propID, String valueToSet) {
   }

   private char getHotKey(String desc) {
      if (desc == null) {
         return '\u0000';
      }

      int index = desc.indexOf(818);
      char charVal = 0;
      if (index > 0) {
         charVal = Character.toLowerCase(CharacterUtilities.getOriginal(desc.charAt(index - 1)));
      }

      return charVal;
   }

   public Action(ApplicationDescriptor applicationDescriptor, String entryName, int ribbonPosition) {
      this._applicationDescriptor = applicationDescriptor;
      this._entryName = entryName;
      this._ribbonPosition = (Integer)(new Object(ribbonPosition));
   }
}
