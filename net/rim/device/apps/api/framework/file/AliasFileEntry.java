package net.rim.device.apps.api.framework.file;

import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.icons.ApplicationIcon;
import net.rim.device.internal.ui.Image;

public class AliasFileEntry implements VerbProvider {
   private String _name;
   private Verb _verb;
   private String _path;
   private Image _image;
   private int _prevSize;
   private String _imageName;

   public String getName() {
      return this._name;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      return null;
   }

   public Image getImage(int size, Graphics graphics) {
      boolean inFocus = graphics.isDrawingStyleSet(8);
      int saveSize = inFocus ? -size : size;
      if (this._imageName != null && this._prevSize != saveSize) {
         Image image = ApplicationIcon.getApplicationIconImage(this._imageName, inFocus ? 1 : 0);
         this._image = image;
         this._prevSize = saveSize;
         return image;
      } else {
         return this._image;
      }
   }

   public String getPath() {
      return this._path;
   }

   public Verb getVerb() {
      return this._verb;
   }

   public boolean isActiveInDirectory(String path, FileSelectionFilter selectionFilter) {
      return true;
   }

   public AliasFileEntry(String name, Verb verb, Image image) {
      this("/TMP/", name, verb, image);
   }

   public AliasFileEntry(String name, Verb verb, String imageName) {
      this._name = name;
      this._path = "/TMP/";
      this._verb = verb;
      this._imageName = imageName;
   }

   public AliasFileEntry(String path, String name, Verb verb, Image image) {
      this._path = path;
      this._name = name;
      this._verb = verb;
      this._image = image;
   }
}
