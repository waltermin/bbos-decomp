package net.rim.device.internal.media;

import java.io.InputStream;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.TimeBase;
import net.rim.device.api.io.IOUtilities;

class ImagePlayer implements Player {
   private byte[] _data;
   private Image _image;
   private ImageItem _item;
   private Control _guiControl;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void read(InputStream stream) {
      try {
         byte[] data = IOUtilities.streamToBytes(stream);
         this._data = data;
      } catch (Throwable var5) {
         throw new Object(e.toString());
      }

      this._image = Image.createImage(this._data, 0, this._data.length);
      String label = null;
      String altText = "";
      this._item = (ImageItem)(new Object(label, this._image, 3, altText));
      this._guiControl = (Control)(new Object(this._item));
   }

   @Override
   public void close() {
   }

   @Override
   public void deallocate() {
   }

   @Override
   public String getContentType() {
      return "image";
   }

   @Override
   public Control getControl(String controlType) {
      return controlType.equals("GUIControl") ? this._guiControl : null;
   }

   @Override
   public Control[] getControls() {
      Control[] controls = new Object[1];
      controls[0] = this._guiControl;
      return controls;
   }

   @Override
   public long getDuration() {
      return 0;
   }

   @Override
   public long getMediaTime() {
      return 0;
   }

   @Override
   public int getState() {
      return 0;
   }

   @Override
   public TimeBase getTimeBase() {
      return Manager.getSystemTimeBase();
   }

   @Override
   public void prefetch() {
   }

   @Override
   public void addPlayerListener(PlayerListener playerListener) {
   }

   @Override
   public void realize() {
   }

   @Override
   public void removePlayerListener(PlayerListener playerListener) {
   }

   @Override
   public void setTimeBase(TimeBase master) {
   }

   @Override
   public void start() {
   }

   @Override
   public void stop() {
   }

   @Override
   public void setLoopCount(int count) {
   }

   @Override
   public long setMediaTime(long now) {
      return 0;
   }

   public ImagePlayer() {
   }
}
