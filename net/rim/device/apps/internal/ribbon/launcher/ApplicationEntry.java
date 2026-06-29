package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.ApplicationProperties;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ribbon.indicators.UnreadCount;
import net.rim.device.apps.api.utility.props.BooleanProps;
import net.rim.device.apps.api.utility.props.CharProps;
import net.rim.device.apps.api.utility.props.IntegerProps;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.api.utility.props.StringProps;
import net.rim.device.apps.internal.phone.data.VoiceUnopenedCount;
import net.rim.device.apps.internal.ribbon.RibbonOptions;
import net.rim.vm.Memory;

public class ApplicationEntry extends AbstractEntryPointDescriptor {
   private EntryPointDescriptor _descriptor;
   private ApplicationProperties _applicationProperties;
   private String _uniqueName;
   private String _propertiesName;
   private boolean _hotKeyIndicator;
   private char _hotkey;
   private boolean _isVisible;
   private RibbonIconField _iconField;
   private int _priority;
   private ApplicationEntry$MyMemoryCleanerListener _memoryCleanerListener;
   private static final int DEFAULT_PRIORITY = 200;
   private static final int UNDEFINED_PRIORITY = -1;
   private static StringBuffer _stringBuffer = (StringBuffer)(new Object());

   public ApplicationEntry(EntryPointDescriptor descriptor, boolean hotKeysDisabled) {
      super(null, 0);
      this._descriptor = descriptor;
      if (descriptor instanceof Object) {
         StringProps stringProps = (StringProps)descriptor;
         this._uniqueName = stringProps.get(1, "");
         this._uniqueName = removeControlCharacters(this._uniqueName);
         this._propertiesName = stringProps.get(9, this._uniqueName);
         this._propertiesName = removeControlCharacters(this._propertiesName);
      }

      this.loadDescriptionAndHotKey(hotKeysDisabled);
      RibbonOptions ribbonOptions = RibbonOptions.getOptions();
      int defaultPriority;
      if (!(descriptor instanceof Object)) {
         defaultPriority = 200;
      } else {
         IntegerProps intProps = (IntegerProps)descriptor;
         Integer i = intProps.get(6, (Integer)((Object)null));
         if (i == null) {
            defaultPriority = 200;
         } else {
            defaultPriority = i;
         }
      }

      this._isVisible = ribbonOptions.isVisible(this._uniqueName, true);
      this._priority = ribbonOptions.getPriority(this._uniqueName, defaultPriority);
   }

   static String removeControlCharacters(String string) {
      if (string.indexOf(32) != -1 || string.indexOf(818) != -1) {
         StringBuffer buffer = (StringBuffer)(new Object());
         int length = string.length();

         for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            if (c != ' ' && c != 818) {
               buffer.append(c);
            }
         }

         string = buffer.toString();
      }

      return string;
   }

   public String getCustomImageName() {
      return this._applicationProperties.getCustomImageName();
   }

   void setApplicationProperties(ApplicationProperties applicationProperties) {
      this._applicationProperties = applicationProperties;
      if (applicationProperties != null) {
         this.setResource(applicationProperties.getBundleName(), applicationProperties.getResourceId());
      } else {
         this.setResource(null, 0);
      }

      this.clearDescription();
   }

   public int getPriority() {
      if (this._applicationProperties != null) {
         int position = this._applicationProperties.getPosition();
         if (position != -1) {
            return position;
         }
      }

      return this._priority;
   }

   public void setPriority(int priority) {
      if (this._applicationProperties != null) {
         this._applicationProperties.setPosition(priority);
      } else {
         this._priority = priority;
      }
   }

   public EntryPointDescriptor getDescriptor() {
      return this._descriptor;
   }

   public void refreshBitmap() {
      synchronized (this._descriptor) {
         if (this._iconField != null) {
            this._iconField.setBitmap();
         }
      }
   }

   public boolean isVisible() {
      return this._applicationProperties != null ? this._applicationProperties.getVisible() : this._isVisible;
   }

   public boolean isDisabledByDefault() {
      boolean returnValue = false;
      if (this._descriptor instanceof Object) {
         BooleanProps boolProps = (BooleanProps)this._descriptor;
         Boolean ret = boolProps.get(11, Boolean.FALSE);
         returnValue = ret == null ? false : ret;
      }

      return returnValue;
   }

   public void setVisible(boolean isVisible) {
      if (this._applicationProperties != null) {
         this._applicationProperties.setVisible(isVisible);
      } else {
         this._isVisible = isVisible;
      }
   }

   public boolean canHide() {
      boolean canHide = true;
      if (this._descriptor instanceof Object) {
         Boolean ret = ((BooleanProps)this._descriptor).get(7, (Boolean)(new Object(canHide)));
         if (ret != null) {
            canHide = ret;
            if (canHide && this._applicationProperties != null) {
               canHide = this._applicationProperties.canHide();
            }
         }
      }

      return canHide;
   }

   public String getDescription(boolean hotKeysDisabled) {
      if (this._hotKeyIndicator != hotKeysDisabled || super._description == null) {
         this.loadDescriptionAndHotKey(hotKeysDisabled);
      }

      return super._description;
   }

   public synchronized void clearDescription() {
      super._description = null;
      this._hotkey = 0;
   }

   public char getHotKey() {
      return this._hotkey == 0 ? this.findHotKey(this.determineDescription(false)) : this._hotkey;
   }

   public String getState() {
      if (!(this._descriptor instanceof Object)) {
         return null;
      }

      StringProps stringProps = (StringProps)this._descriptor;
      return stringProps.get(2, null);
   }

   public String getUniqueName() {
      return this._uniqueName;
   }

   public String getPropertiesName() {
      return this._propertiesName;
   }

   private Object getBitmap(long propertyId) {
      Object bitmap = null;
      if (this._descriptor instanceof Object) {
         ObjectProps oprops = (ObjectProps)this._descriptor;
         bitmap = oprops.get(propertyId, bitmap);
      }

      return bitmap;
   }

   public Object getBitmap() {
      return this.getBitmap(4);
   }

   public Object getBitmapFocus() {
      return this.getBitmap(10);
   }

   public String getExtraInfo() {
      if (this._descriptor instanceof Object) {
         ObjectProps oprops = (ObjectProps)this._descriptor;
         Object info = oprops.get(12, null);
         if (info != null) {
            if (info instanceof Object) {
               UnreadCount uc = (UnreadCount)info;
               return String.valueOf(uc.getUnreadCount());
            }

            if (info instanceof Object) {
               VoiceUnopenedCount vuc = (VoiceUnopenedCount)info;
               return String.valueOf(vuc.getNewCount());
            }

            if (info instanceof Object) {
               return (String)info;
            }
         }
      }

      return "";
   }

   public Object getBitmapDefault() {
      return this.getBitmap(5);
   }

   public RibbonIconField getRibbonIcon() {
      if (this._iconField == null) {
         this._iconField = new RibbonIconField(this);
         this._iconField.setCookie(this);
      }

      return this._iconField;
   }

   public void invoke() {
      if (this._descriptor instanceof Object) {
         Runnable runnable = (Runnable)this._descriptor;
         runnable.run();
      }
   }

   private char findHotKey(String description) {
      char hotkey = this.determineHotKey(description);
      if (hotkey == 0 && this._descriptor instanceof Object) {
         CharProps props = (CharProps)this._descriptor;
         hotkey = props.get(8, '\u0000');
      }

      return hotkey;
   }

   private String determineDescription(boolean disableHotKeys) {
      String description = this.getDescription();
      if (description == null && this._descriptor instanceof Object) {
         StringProps stringProps = (StringProps)this._descriptor;
         description = stringProps.get(3, "");
         if (Memory.isPlaintext(description) && this._memoryCleanerListener == null) {
            this._memoryCleanerListener = new ApplicationEntry$MyMemoryCleanerListener(this, null);
            MemoryCleanerDaemon.addWeakListener(this._memoryCleanerListener, false);
         }
      }

      return description;
   }

   public String getDescriptionNoHotkey() {
      String description = this.determineDescription(false);
      return description != null ? ApplicationEntryPoint.removeHotkeyFromDescription(description) : null;
   }

   private char determineHotKey(String description) {
      char hotkey = 0;
      if (description != null) {
         int index = description.indexOf(818);
         if (index > 0) {
            hotkey = Character.toLowerCase(CharacterUtilities.getOriginal(description.charAt(index - 1)));
         }
      }

      return hotkey;
   }

   private synchronized void loadDescriptionAndHotKey(boolean disableHotKeys) {
      super._description = this.determineDescription(disableHotKeys);
      this._hotKeyIndicator = disableHotKeys;
      this._hotkey = this.determineHotKey(super._description);
      if (disableHotKeys && super._description != null) {
         super._description = ApplicationEntryPoint.removeHotkeyFromDescription(super._description);
      }
   }

   @Override
   protected String getDefaultDescription() {
      String state = this.getState();
      if (state != null) {
         synchronized (_stringBuffer) {
            _stringBuffer.append(this._uniqueName);
            _stringBuffer.append('.');
            _stringBuffer.append(state);
            String name = _stringBuffer.toString();
            _stringBuffer.setLength(0);
            InternalApplicationHierarchy hierarchy = HierarchyManager.getInstance().getActiveHierarchy();
            if (hierarchy != null) {
               ApplicationProperties properties = hierarchy.getApplicationProperties(name);
               if (properties != null) {
                  String bundleName = properties.getBundleName();
                  if (bundleName != null) {
                     return ResourceBundle.getBundle(bundleName).getString(properties.getResourceId());
                  }
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public String toString() {
      return this.getDescriptionNoHotkey();
   }

   private synchronized boolean cleanDescription(int event) {
      if (event == 10 && super._description != null) {
         super._description = null;
         return true;
      } else {
         return false;
      }
   }
}
