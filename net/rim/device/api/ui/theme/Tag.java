package net.rim.device.api.ui.theme;

public class Tag {
   private String _name;
   private int _id;

   Tag(String name, int id) {
      this._name = name;
      this._id = id;
   }

   public static Tag create(String name) {
      return ThemeManager.createTag(name);
   }

   public static Tag get(String name) {
      return ThemeManager.getTag(name);
   }

   @Override
   public int hashCode() {
      return this._id;
   }

   @Override
   public String toString() {
      return this._name;
   }
}
