package net.rim.tools.compiler;

import java.util.Vector;

public class Applet {
   private String _name;
   private String _className;
   private Vector _iconNames;
   private Vector _icons;

   public Applet(String name, String className) {
      this._name = name;
      this._className = className;
      this._iconNames = new Vector();
      this._icons = new Vector();
   }

   public void setName(String name) {
      this._name = name;
   }

   public String getName() {
      return this._name;
   }

   public void setClassName(String className) {
      this._className = className;
   }

   public String getClassName() {
      return this._className;
   }

   public int getNumIconNames() {
      return this._iconNames.size();
   }

   public String getIconName(int index) {
      return (String)this._iconNames.elementAt(index);
   }

   public void setIconName(String iconName, int index) {
      Vector v = this._iconNames;
      if (v.size() <= index) {
         v.setSize(index + 1);
      }

      v.setElementAt(iconName, index);
   }

   public int getNumIcons() {
      return this._icons.size();
   }

   public ImageFile getIcon(int index) {
      return (ImageFile)this._icons.elementAt(index);
   }

   public void setIcon(ImageFile icon, int index) {
      Vector v = this._icons;
      if (v.size() <= index) {
         v.setSize(index + 1);
      }

      v.setElementAt(icon, index);
      icon.markIcon();
   }
}
