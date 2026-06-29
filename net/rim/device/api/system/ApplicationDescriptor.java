package net.rim.device.api.system;

import java.util.Calendar;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.vm.Process;

public final class ApplicationDescriptor {
   private int _moduleHandle;
   private String _moduleName;
   private int _index = -1;
   private String _name;
   private String _version;
   private String[] _args;
   private int _flags = -1;
   private Bitmap _icon;
   private int _position = -1;
   private String _nameResourceBundle;
   private String _overrideNameResourceBundle;
   private int _nameResourceId = -1;
   private int _overrideNameResourceId = -1;
   private long _scheduledTime = Long.MAX_VALUE;
   private long _absoluteTime = Long.MAX_VALUE;
   private boolean _absolute;
   private int _powerOnBehavior = 0;
   public static final int DO_NOT_POWER_ON = 0;
   public static final int POWER_ON = 1;
   public static final int POWER_ON_FOR_AUTO_ON = 2;
   public static final int POWER_ON_ALWAYS = 3;
   public static final byte FLAG_RUN_ON_STARTUP = 1;
   public static final byte FLAG_SYSTEM = 2;
   public static final byte FLAG_AUTO_RESTART = 4;
   static final int NUM_TIERS = 8;
   static final int TIERS_SHIFT = 5;

   private ApplicationDescriptor() {
   }

   ApplicationDescriptor(int moduleHandle, int index) {
      this._moduleHandle = moduleHandle;
      this._index = index;
   }

   public ApplicationDescriptor(ApplicationDescriptor original, String[] args) {
      this.copy(original);
      this._args = args == null ? new String[0] : args;
   }

   public ApplicationDescriptor(ApplicationDescriptor original, String name, String[] args) {
      this.copy(original);
      this._name = name == null ? this.getModuleName() : name;
      this._args = args == null ? new String[0] : args;
   }

   public ApplicationDescriptor(
      ApplicationDescriptor original, String name, String[] args, Bitmap icon, int position, String nameResourceBundle, int nameResourceId
   ) {
      this.copy(original);
      this._name = name == null ? this.getModuleName() : name;
      this._args = args == null ? new String[0] : args;
      this._icon = icon;
      this._position = position;
      this._nameResourceBundle = nameResourceBundle;
      this._nameResourceId = nameResourceId;
   }

   public ApplicationDescriptor(
      ApplicationDescriptor original, String name, String[] args, Bitmap icon, int position, String nameResourceBundle, int nameResourceId, int flags
   ) {
      this.copy(original);
      this._name = name == null ? this.getModuleName() : name;
      this._args = args == null ? new String[0] : args;
      this._icon = icon;
      this._position = position;
      this._nameResourceBundle = nameResourceBundle;
      this._nameResourceId = nameResourceId;
      this._flags = flags;
   }

   private final void assertPermission() {
      ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
   }

   public final int getModuleHandle() {
      return this._moduleHandle;
   }

   public final String getModuleName() {
      if (this._moduleName == null) {
         this._moduleName = CodeModuleManager.getModuleName(this._moduleHandle);
      }

      return this._moduleName;
   }

   public final synchronized String getName() {
      if (this._name == null) {
         this._name = CodeModuleManager.getModuleString(this._moduleHandle, "_\u0018pN,es", this._index);
         if (this._name == null) {
            this._name = this.getModuleName();
         }
      }

      return this._name;
   }

   public final int getIndex() {
      return this._index;
   }

   public final synchronized String getNameResourceBundle() {
      if (this._overrideNameResourceBundle != null) {
         return this._overrideNameResourceBundle;
      }

      if (this._nameResourceBundle == null) {
         this._nameResourceBundle = CodeModuleManager.getModuleString(this._moduleHandle, "_\u0018pN,e so®\u0007B¢d~s", this._index);
      }

      return this._nameResourceBundle;
   }

   public final int getNameResourceId() {
      if (this._overrideNameResourceId != -1) {
         return this._overrideNameResourceId;
      }

      if (this._nameResourceId == -1) {
         byte[] data = CodeModuleManager.getModuleData(this._moduleHandle, "_\u0018pN,e so®\u0007ñs");
         int offset = this._index * 4;
         if (data != null && data.length >= offset + 4) {
            this._nameResourceId = (data[offset++] & 255) << 24;
            this._nameResourceId = this._nameResourceId + ((data[offset++] & 255) << 16);
            this._nameResourceId = this._nameResourceId + ((data[offset++] & 255) << 8);
            this._nameResourceId = this._nameResourceId + (data[offset] & 255);
         }
      }

      return this._nameResourceId;
   }

   public final void setOverrideNameResourceId(int id) {
      this._overrideNameResourceId = id;
   }

   public final void setOverrideNameResourceBundle(String bundleName) {
      this._overrideNameResourceBundle = bundleName;
   }

   public final String getLocalizedName() {
      String name = null;
      String bundle = this.getNameResourceBundle();
      if (bundle != null) {
         try {
            ResourceBundle rb = ResourceBundle.getBundle(bundle);
            if (rb != null) {
               name = rb.getString(this.getNameResourceId());
            }
         } catch (Throwable e) {
            System.err.println("WARNING: i18n bad or missing title resource for " + this.getModuleName() + '.' + this.getName());
         }
      }

      if (name == null) {
         name = this.getName();
      }

      return name;
   }

   public final synchronized String getVersion() {
      if (this._version == null) {
         this._version = CodeModuleManager.getModuleVersion(this._moduleHandle);
      }

      return this._version;
   }

   public final synchronized String[] getArgs() {
      if (this._args == null) {
         String arg = CodeModuleManager.getModuleString(this._moduleHandle, "_\u0018pArgs", this._index);
         if (arg != null) {
            this._args = new String[1];
            this._args[0] = arg;
         } else {
            this._args = new String[0];
         }
      }

      return this._args;
   }

   public final int getFlags() {
      if (this._flags == -1) {
         byte[] data = CodeModuleManager.getModuleData(this._moduleHandle, "_\u0018pF§gs");
         if (data != null && data.length > this._index) {
            this._flags = data[this._index] & 255;
         } else {
            this._flags = 0;
         }
      }

      return this._flags & 31;
   }

   public final int getStartupTier() {
      this.getFlags();
      return 7 - (this._flags >> 5);
   }

   public final synchronized Bitmap getIcon() {
      if (this._icon == null) {
         this._icon = this.getBitmap("_\u0018pIc\u0010s");
      }

      return this._icon;
   }

   public final synchronized int getPosition() {
      if (this._position == -1) {
         byte[] data = CodeModuleManager.getModuleData(this._moduleHandle, "_\u0018pPosý");
         if (data != null && data.length > this._index) {
            this._position = data[this._index] & 255;
         } else {
            this._position = 0;
         }
      }

      return this._position;
   }

   private final Bitmap getBitmap(String id) {
      byte[] data = CodeModuleManager.getModuleData(this._moduleHandle, id, this._index);
      if (data == null) {
         return null;
      }

      boolean findColour = Graphics.isColor();
      boolean loopCheck = findColour;

      do {
         int offset = 0;

         while (offset < data.length) {
            int length = this.getIconLength(offset, data);

            EncodedImage image;
            boolean mono;
            try {
               image = EncodedImage.createEncodedImage(data, offset + 2, length);
               switch (image.getImageType()) {
                  case 2:
                     PNGEncodedImage pngImage = (PNGEncodedImage)image;
                     int colourType = pngImage.getColorType();
                     mono = pngImage.getBitDepth() == 1 && (colourType == 0 || colourType == 4);
                     break;
                  case 4:
                     mono = true;
                     break;
                  default:
                     mono = false;
               }
            } catch (IllegalArgumentException iae) {
               offset += length + 2;
               continue;
            }

            if (findColour != mono) {
               try {
                  return image.getBitmap();
               } catch (IllegalArgumentException iae) {
                  return null;
               }
            }

            offset += length + 2;
         }

         findColour = !findColour;
      } while (loopCheck != findColour);

      return null;
   }

   private final int getIconLength(int start, byte[] data) {
      return ((data[start] & 0xFF) << 8) + (data[start + 1] & 0xFF);
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof ApplicationDescriptor)) {
         return false;
      }

      ApplicationDescriptor ad = (ApplicationDescriptor)o;
      return this._moduleHandle == ad._moduleHandle && Arrays.equals(this.getArgs(), ad.getArgs());
   }

   public static final ApplicationDescriptor currentApplicationDescriptor() {
      return ((ApplicationProcess)Process.currentProcess()).getApplicationDescriptor();
   }

   final long getNextScheduledTime() {
      if (!this._absolute) {
         this._absoluteTime = getNextDate(this._scheduledTime);
      }

      return this._absoluteTime;
   }

   private static final long getNextDate(long relativeTime) {
      Calendar cal = DateTimeUtilities.getNextDate((int)relativeTime);
      return ((CalendarExtensions)cal).getTimeLong();
   }

   final long getScheduledTime() {
      return this._absoluteTime;
   }

   final long setScheduledTime(long scheduledTime, boolean absolute) {
      this._scheduledTime = scheduledTime;
      this._absolute = absolute;
      if (absolute) {
         this._absoluteTime = this._scheduledTime - this._scheduledTime % 60000;
      } else {
         this._absoluteTime = getNextDate(this._scheduledTime);
      }

      return this._absoluteTime;
   }

   public final void setPowerOnBehavior(int behavior) {
      this.assertPermission();
      switch (behavior) {
         case 0:
            this._powerOnBehavior = 0;
            return;
         case 1:
         case 2:
         case 3:
         default:
            this._powerOnBehavior = behavior;
      }
   }

   public final int getPowerOnBehavior() {
      return this._powerOnBehavior;
   }

   private final void copy(ApplicationDescriptor original) {
      this._moduleHandle = original._moduleHandle;
      this._moduleName = original._moduleName;
      this._index = original._index;
      this._name = original._name;
      this._version = original._version;
      this._args = original._args;
      this._flags = original._flags;
      this._icon = original._icon;
      this._position = original._position;
      this._nameResourceBundle = original._nameResourceBundle;
      this._overrideNameResourceBundle = original._overrideNameResourceBundle;
      this._nameResourceId = original._nameResourceId;
      this._overrideNameResourceId = original._overrideNameResourceId;
      this._powerOnBehavior = original._powerOnBehavior;
   }
}
