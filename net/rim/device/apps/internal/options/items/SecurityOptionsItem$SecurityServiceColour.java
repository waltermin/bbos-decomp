package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.internal.options.resources.OptionsResources;

final class SecurityOptionsItem$SecurityServiceColour {
   private int _colour;
   private int _resourceId;

   SecurityOptionsItem$SecurityServiceColour(int colour, int resourceId) {
      this._colour = colour;
      this._resourceId = resourceId;
   }

   public final int getColour() {
      return this._colour;
   }

   @Override
   public final String toString() {
      return OptionsResources.getString(this._resourceId);
   }

   @Override
   public final boolean equals(Object object) {
      if (!(object instanceof SecurityOptionsItem$SecurityServiceColour)) {
         return false;
      }

      SecurityOptionsItem$SecurityServiceColour other = (SecurityOptionsItem$SecurityServiceColour)object;
      return this._colour == other._colour;
   }
}
