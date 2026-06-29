package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.registration.VerbCombinerRepository;
import net.rim.device.apps.api.framework.verb.DefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.VerbCombiner;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.vm.Array;

public class VerbToMenuImpl implements VerbToMenu {
   private Verb[] _verbs;
   private Verb _defaultVerb;
   private int _defaultVerbPriority;
   private static Comparator _verbComparator = new VerbToMenuImpl$VerbComparator();
   private static Comparator _menuItemComparator = new VerbToMenuImpl$MenuItemComparator();

   public VerbToMenuImpl() {
      this.clear();
   }

   @Override
   public final void clear() {
      this._verbs = new Verb[0];
      this._defaultVerb = null;
      this._defaultVerbPriority = 0;
   }

   @Override
   public void addVerb(Verb verbToAdd) {
      if (verbToAdd != null) {
         synchronized (this._verbs) {
            int count = this._verbs.length;
            Array.resize(this._verbs, count + 1);
            this._verbs[count] = verbToAdd;
         }
      }
   }

   @Override
   public void addVerbs(Verb[] verbsToAdd) {
      if (verbsToAdd != null) {
         for (int i = 0; i < verbsToAdd.length; i++) {
            if (verbsToAdd[i] != null) {
               this.addVerb(verbsToAdd[i]);
            }
         }
      }
   }

   @Override
   public Verb[] getVerbs() {
      synchronized (this._verbs) {
         int count = this._verbs.length;
         Verb[] verbs = new Verb[count];
         System.arraycopy(this._verbs, 0, verbs, 0, count);
         return verbs;
      }
   }

   @Override
   public int size() {
      return this._verbs.length;
   }

   private void sortVerbs() {
      Arrays.sort(this._verbs, _verbComparator);
   }

   @Override
   public Verb getFirstVerbAt(int ordering) {
      synchronized (this._verbs) {
         int count = this._verbs.length;
         this.sortVerbs();

         for (int i = 0; i < count; i++) {
            Verb tmpVerb = this._verbs[i];
            if (tmpVerb.getOrdering() > ordering) {
               return tmpVerb;
            }
         }

         return null;
      }
   }

   @Override
   public void getMenu(Menu menuToReturn, Object context) {
      Verb currentVerb = null;
      int lastOrderValue = 0;
      synchronized (this._verbs) {
         this.sortVerbs();
         int length = this._verbs.length;

         for (int i = 0; i < length; i++) {
            currentVerb = this._verbs[i];
            if (i != 0 && currentVerb.getOrdering() / 65536 != lastOrderValue / 65536) {
               MenuItem separator = MenuItem.separator(lastOrderValue);
               menuToReturn.add(separator);
            }

            lastOrderValue = currentVerb.getOrdering();
            int priority = this._defaultVerb == currentVerb ? 0 : Integer.MAX_VALUE;
            VerbMenuItem item = new VerbMenuItem(currentVerb.toString(), lastOrderValue, priority, currentVerb, context);
            if (menuToReturn instanceof SystemEnabledMenu) {
               SystemEnabledMenu systemMenu = (SystemEnabledMenu)menuToReturn;
               if (context == null) {
                  context = systemMenu.getContext();
               }

               item.setContext(context);
               item.setNotify(systemMenu.getScreen());
            }

            menuToReturn.add(item);
         }

         this.clear();
      }
   }

   @Override
   public void getMenu(Menu menuToReturn, Field f, Object context) {
      if (f == null) {
         this.getMenu(menuToReturn, context);
      } else {
         ContextMenu contextMenu = f.getContextMenu();
         MenuItem[] items = contextMenu.getItems();
         MenuItem defaultItem = contextMenu.getDefaultItem();
         if (defaultItem != null && (defaultItem.getPriority() < this._defaultVerbPriority || f.isSelecting())) {
            this._defaultVerb = null;
         }

         boolean setDefault = false;
         int n2 = contextMenu.getSize();
         if (n2 == 0) {
            this.getMenu(menuToReturn, context);
         } else {
            synchronized (this._verbs) {
               int n1 = this._verbs.length;
               Verb currentVerb = null;
               MenuItem currentMenuItem = null;
               int lastOrder = -1;
               Arrays.sort(items, _menuItemComparator);
               this.sortVerbs();
               int i1 = 0;
               int i2 = 0;

               while (i1 < n1 || i2 < n2) {
                  int menuOrder;
                  String menuString;
                  Object menuCookie;
                  if (i2 < n2) {
                     currentMenuItem = items[i2];
                     menuOrder = currentMenuItem.getOrdinal();
                     menuString = currentMenuItem.toString();
                     menuCookie = currentMenuItem;
                     if (defaultItem == items[i2] && this._defaultVerb == null) {
                        setDefault = true;
                     }

                     if (i1 < n1) {
                        currentVerb = this._verbs[i1];
                        if (currentVerb.getOrdering() < menuOrder) {
                           menuOrder = currentVerb.getOrdering();
                           menuString = currentVerb.toString();
                           menuCookie = currentVerb;
                           i1++;
                           if (this._defaultVerb == currentVerb) {
                              setDefault = true;
                           }
                        } else {
                           i2++;
                        }
                     } else {
                        i2++;
                     }
                  } else {
                     currentVerb = this._verbs[i1];
                     menuOrder = currentVerb.getOrdering();
                     menuString = currentVerb.toString();
                     menuCookie = currentVerb;
                     i1++;
                     if (this._defaultVerb == currentVerb) {
                        setDefault = true;
                     }
                  }

                  if (lastOrder != -1 && menuOrder / 65536 != lastOrder / 65536) {
                     MenuItem separator = MenuItem.separator(menuOrder);
                     menuToReturn.add(separator);
                  }

                  lastOrder = menuOrder;
                  MenuItem item = null;
                  if (!(menuCookie instanceof Verb)) {
                     item = (MenuItem)menuCookie;
                  } else {
                     Verb verb = (Verb)menuCookie;
                     int priority = setDefault ? 0 : Integer.MAX_VALUE;
                     VerbMenuItem verbItem = new VerbMenuItem(menuString, menuOrder, priority, verb, context);
                     item = verbItem;
                     if (menuToReturn instanceof SystemEnabledMenu) {
                        SystemEnabledMenu systemMenu = (SystemEnabledMenu)menuToReturn;
                        if (context == null) {
                           context = systemMenu.getContext();
                        }

                        verbItem.setContext(context);
                        verbItem.setNotify(systemMenu.getScreen());
                     }
                  }

                  menuToReturn.add(item);
                  if (setDefault) {
                     menuToReturn.setDefault(item);
                     setDefault = false;
                  }
               }

               this.clear();
            }
         }
      }
   }

   @Override
   public Object invoke(Object cookie, Object context) {
      if (cookie instanceof VerbMenuItem) {
         VerbMenuItem vmi = (VerbMenuItem)cookie;
         cookie = vmi.getVerb();
      }

      if (!(cookie instanceof Verb)) {
         return null;
      }

      Verb v = (Verb)cookie;
      return v.invoke(context);
   }

   @Override
   public void setDefaultVerb(Verb defaultVerb) {
      this._defaultVerb = defaultVerb;
   }

   @Override
   public void setDefaultVerbPriority(int priority) {
      this._defaultVerbPriority = priority;
   }

   @Override
   public Verb getDefaultVerb() {
      return this._defaultVerb;
   }

   @Override
   public int getDefaultVerbPriority() {
      return this._defaultVerbPriority;
   }

   @Override
   public void coalesce(long verbCombinerCollectionId, DefaultVerbProvider defaultVerbProvider) {
      VerbCombiner[] combiners = VerbCombinerRepository.getCombiners(verbCombinerCollectionId);
      Verb coalescedDefaultVerb = null;
      Verb defaultWrapperVerb = null;
      if (combiners != null && combiners.length > 0) {
         Verb[] verbs = this.getVerbs();
         int verbCount = verbs.length;
         if (verbCount > 0) {
            Verb[] tmpVerbs = new Verb[0];

            for (VerbCombiner combiner : combiners) {
               int index = 0;
               verbCount = verbs.length;
               Array.resize(tmpVerbs, verbCount);
               coalescedDefaultVerb = null;

               for (int j = 0; j < verbCount; j++) {
                  Verb theVerb = verbs[j];
                  if (theVerb != null && combiner.recognize(theVerb)) {
                     if (this._defaultVerb == theVerb) {
                        coalescedDefaultVerb = theVerb;
                     }

                     tmpVerbs[index++] = theVerb;
                     verbs[j] = null;
                  }
               }

               if (index > 0) {
                  Array.resize(verbs, verbCount + 1);
                  if (index == 1) {
                     verbs[verbCount] = tmpVerbs[0];
                  } else if (index > 1) {
                     Array.resize(tmpVerbs, index);
                     Verb newDefaultVerb = null;
                     if (defaultVerbProvider != null) {
                        newDefaultVerb = defaultVerbProvider.getDefaultVerb(tmpVerbs);
                     }

                     if (coalescedDefaultVerb != null) {
                        newDefaultVerb = coalescedDefaultVerb;
                     }

                     Verb wrapperVerb = combiner.createWrapperVerb(tmpVerbs, newDefaultVerb);
                     verbs[verbCount] = wrapperVerb;
                     if (coalescedDefaultVerb != null) {
                        defaultWrapperVerb = wrapperVerb;
                        coalescedDefaultVerb = null;
                     }
                  }
               }
            }

            Verb defaultVerb = this._defaultVerb;
            this.clear();
            this.addVerbs(verbs);
            if (defaultWrapperVerb != null) {
               this._defaultVerb = defaultWrapperVerb;
               return;
            }

            this._defaultVerb = defaultVerb;
         }
      }
   }
}
