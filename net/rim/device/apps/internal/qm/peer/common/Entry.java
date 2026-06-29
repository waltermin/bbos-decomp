package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.utility.props.CharProps;
import net.rim.device.apps.api.utility.props.IntegerProps;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.api.utility.props.StringProps;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.qm.resource.QmResources;
import net.rim.device.internal.ui.Image;
import net.rim.vm.Process;

public class Entry implements EntryPointDescriptor, IntegerProps, StringProps, ObjectProps, CharProps, Runnable, PersistentContentListener {
   private char _hotKeyCache;
   private Integer _position;
   private Object _icon;
   private Object _focusIcon;
   private Object _defaultIcon;
   private Object _defaultFocusIcon;
   protected int _pid;
   private boolean _registered;
   private String _themeCustomIconName;
   protected ApplicationDescriptor _applicationDescriptor;
   private Application _ribbonApplication;
   private boolean _contentProtectionEnabled;
   protected String _state;
   Entry$UpdateRunnable _updateRunnable = new Entry$UpdateRunnable(this);
   public static final long LARGE_BITMAP_FOCUS = 10L;

   public boolean isDeviceLocked() {
      return this._contentProtectionEnabled;
   }

   String getDescription() {
      return QmResources.getString(75);
   }

   public void update() {
      if (this._ribbonApplication != null) {
         this._ribbonApplication.invokeLater(this._updateRunnable);
      }
   }

   protected void deviceLocked() {
      throw null;
   }

   protected void deviceUnlocked() {
      throw null;
   }

   public void storePid() {
      this._pid = Process.currentProcess().getProcessId();
   }

   public String getUid() {
      throw null;
   }

   public void onThemeChanged() {
      this.updateIcons();
   }

   public void updateIcons() {
      this._icon = this._defaultIcon;
      this._focusIcon = this._defaultFocusIcon;
      if (this._themeCustomIconName != null) {
         Theme theme = ThemeManager.getActiveTheme();
         Image img = theme.getApplicationIcon(this._themeCustomIconName, this._state, 0, Integer.MAX_VALUE, null, 2);
         if (img != null) {
            this._icon = img;
            img = theme.getApplicationIcon(this._themeCustomIconName, this._state, 6, Integer.MAX_VALUE, img, 2);
            if (img != null) {
               this._focusIcon = img;
            }
         }
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
   }

   @Override
   public char get(long propID, char defaultReturned) {
      return propID == 8 && this._hotKeyCache != 0 ? this._hotKeyCache : defaultReturned;
   }

   @Override
   public Integer get(long propID, Integer defaultReturned) {
      return propID == 6 ? this._position : defaultReturned;
   }

   @Override
   public Object get(long propID, Object defaultReturned) {
      if (this._ribbonApplication == null) {
         label38:
         try {
            this._ribbonApplication = Application.getApplication();
         } finally {
            break label38;
         }
      }

      if (propID == 4) {
         return this._icon;
      } else if (propID == 10) {
         return this._focusIcon != null ? this._focusIcon : this._icon;
      } else {
         return defaultReturned;
      }
   }

   @Override
   public String get(long propID, String defaultReturned) {
      if (this._registered && this._ribbonApplication == null) {
         label47:
         try {
            this._ribbonApplication = Application.getApplication();
         } finally {
            break label47;
         }
      }

      if (propID == 1) {
         return this.getUid();
      }

      if (propID == 3) {
         String description = this.getDescription();
         if (description == null) {
            return defaultReturned;
         }

         this._hotKeyCache = this.getHotKey(description);
         if (PhoneOptions.getOptions().getBooleanOption(16384)) {
            description = stripOffHotKey(description);
         }

         return description;
      } else {
         return defaultReturned;
      }
   }

   @Override
   public void persistentContentStateChanged(int state) {
      switch (state) {
         case 1:
         default:
            this._contentProtectionEnabled = false;
            this.deviceUnlocked();
            return;
         case 2:
         case 3:
         case 4:
            this._contentProtectionEnabled = true;
            this.deviceLocked();
         case 0:
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      try {
         ApplicationManager m = ApplicationManager.getApplicationManager();
         Process p = Process.getProcess(this._pid);
         if (p != null && p.isAlive()) {
            m.requestForeground(this._pid);
         } else {
            m.runApplication(this._applicationDescriptor);
         }
      } catch (Throwable var4) {
         throw new RuntimeException(e.getMessage());
      }
   }

   @Override
   public void set(long propID, char valueToSet) {
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

   public Entry(Bitmap icon, Bitmap focusIcon, int position, String themeCustomIconName) {
      this._applicationDescriptor = new ApplicationDescriptor(ApplicationDescriptor.currentApplicationDescriptor(), new String[]{"go"});
      this._defaultIcon = icon;
      this._defaultFocusIcon = focusIcon;
      this._themeCustomIconName = themeCustomIconName;
      this._position = new Integer(position);
      this.onThemeChanged();
      RibbonLauncher.getInstance().registerAction(this.getUid(), this);
      this._registered = true;
      PersistentContent.addListener(this);
      if (PersistentContent.getState() == 1) {
         this._contentProtectionEnabled = false;
      } else {
         this._contentProtectionEnabled = true;
      }
   }

   static String stripOffHotKey(String description) {
      int index = -1;
      if ((index = description.indexOf(818)) != -1 && index > 1 && description.charAt(index - 2) == '(' && description.charAt(index + 1) == ')') {
         StringBuffer desc = new StringBuffer();
         if (description.charAt(index - 3) == ' ') {
            StringUtilities.append(desc, description, 0, index - 3);
         } else {
            StringUtilities.append(desc, description, 0, index - 2);
         }

         StringUtilities.append(desc, description, index + 2, description.length() - (index + 2));
         description = desc.toString();
      }

      return StringUtilities.removeChars(description, "̲");
   }

   public static long stringHashToLong(String key) {
      Digest digest = new SHA1Digest();
      digest.update(key.getBytes());
      byte[] hashValBytes = digest.getDigest();
      long hashValLong = 0;

      for (int i = 0; i < 8; i++) {
         hashValLong |= (hashValBytes[i] & 255) << 8 * i;
      }

      return hashValLong;
   }
}
