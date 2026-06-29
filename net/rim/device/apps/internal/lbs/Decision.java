package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.internal.lbs.render.LabelRender;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class Decision extends Location {
   Route _route;
   int _action;
   int _actionInfo;
   int _distance;
   String _name;
   String _exit;
   String _connector;
   String _towards;
   int _number;
   String _address;
   String _caption;
   Bitmap _sign;
   String _bitMapText;
   static final int ACTION_UNKNOWN = 0;
   static final int ACTION_START = 1;
   static final int ACTION_END = 2;
   static final int ACTION_TURN = 3;
   static final int ACTION_NAME_CHANGE = 4;
   static final int ACTION_STREET_CURVE = 5;
   static final int ACTION_STRAIGHT = 6;
   static final int ACTION_ENTER_RAMP = 7;
   static final int ACTION_EXIT_RAMP = 8;
   static final int ACTION_ENTER_CIRCLE = 9;
   static final int ACTION_EXIT_CIRCLE = 10;
   static final int ACTION_ENTER_SLIPROAD = 11;
   static final int ACTION_EXIT_SLIPROAD = 12;
   static final int ACTION_SIGNAGE = 13;
   static final int ACTION_NO_ECA_START = 14;
   static final int ACTION_NO_ECA_END = 15;
   static final int ACTION_MERGE = 16;
   static final int ACTION_INFO_CONTINUE = 0;
   static final int ACTION_INFO_LEFT_22_DEGREES = 1;
   static final int ACTION_INFO_LEFT_45_DEGREES = 2;
   static final int ACTION_INFO_LEFT_67_DEGREES = 3;
   static final int ACTION_INFO_LEFT_90_DEGREES = 4;
   static final int ACTION_INFO_LEFT_112_DEGREES = 5;
   static final int ACTION_INFO_LEFT_135_DEGREES = 6;
   static final int ACTION_INFO_LEFT_157_DEGREES = 7;
   static final int ACTION_INFO_U_TURN = 8;
   static final int ACTION_INFO_RIGHT_157_DEGREES = 9;
   static final int ACTION_INFO_RIGHT_135_DEGREES = 10;
   static final int ACTION_INFO_RIGHT_112_DEGREES = 11;
   static final int ACTION_INFO_RIGHT_90_DEGREES = 12;
   static final int ACTION_INFO_RIGHT_67_DEGREES = 13;
   static final int ACTION_INFO_RIGHT_45_DEGREES = 14;
   static final int ACTION_INFO_RIGHT_22_DEGREES = 15;
   static final int ACTION_INFO_EXIT = 1;
   static final int ACTION_INFO_VIA = 2;
   static final int ACTION_INFO_TO = 3;

   public Decision() {
   }

   public Decision(
      Route route,
      int longitude,
      int latitude,
      int action,
      int actionInfo,
      String name,
      int distance,
      String address,
      String description,
      String exit,
      String connector,
      String towards
   ) {
      this._route = route;
      super._latitude = latitude;
      super._longitude = longitude;
      super._description = description;
      this._address = address == null ? "" : address;
      this._action = action;
      this._actionInfo = actionInfo;
      this._distance = distance;
      this._exit = exit == null ? "" : exit;
      this._connector = connector == null ? "" : connector;
      this._towards = towards == null ? "" : towards;
      this._caption = "";
      this._name = LabelRender.toLowerCase(name, 0);
      label516:
      switch (action) {
         case -1:
         case 11:
         case 12:
         case 13:
            this._caption = "";
            break;
         case 0:
         case 14:
         case 15:
         default:
            this._caption = "";
            break;
         case 1:
            this._sign = Bitmap.getBitmapResource("resources/directions/continue.png");
            break;
         case 2:
            this._sign = Bitmap.getBitmapResource("resources/directions/end.png");
            if (this._address.length() == 0) {
               this._caption = LBSResources.getString(293);
            } else {
               this._caption = MessageFormat.format(LBSResources.getString(265), new String[]{this._address});
            }
            break;
         case 3:
            switch (actionInfo) {
               case -1:
                  break;
               case 0:
               default:
                  this._sign = Bitmap.getBitmapResource("resources/directions/continue.png");
                  if (this._name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(366), new String[]{this._name, this._towards});
                  } else if (this._name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(365), new String[]{this._name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(367), new String[]{this._towards});
                  } else {
                     this._caption = LBSResources.getString(239);
                  }
                  break;
               case 1:
               case 2:
                  this._sign = Bitmap.getBitmapResource("resources/directions/bearLeft.png");
                  if (this._name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(358), new String[]{this._name, this._towards});
                  } else if (this._name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(357), new String[]{this._name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(359), new String[]{this._towards});
                  } else {
                     this._caption = LBSResources.getString(241);
                  }
                  break;
               case 3:
               case 4:
               case 5:
                  this._sign = Bitmap.getBitmapResource("resources/directions/turnLeft.png");
                  if (this._name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(370), new String[]{this._name, this._towards});
                  } else if (this._name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(369), new String[]{this._name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(371), new String[]{this._towards});
                  } else {
                     this._caption = LBSResources.getString(243);
                  }
                  break;
               case 6:
               case 7:
                  this._sign = Bitmap.getBitmapResource("resources/directions/turnSharpLeft.png");
                  if (this._name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(378), new String[]{this._name, this._towards});
                  } else if (this._name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(377), new String[]{this._name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(379), new String[]{this._towards});
                  } else {
                     this._caption = LBSResources.getString(245);
                  }
                  break;
               case 8:
                  this._sign = Bitmap.getBitmapResource("resources/directions/uTurn.png");
                  if (this._name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(386), new String[]{this._name, this._towards});
                  } else if (this._name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(385), new String[]{this._name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(387), new String[]{this._towards});
                  } else {
                     this._caption = LBSResources.getString(247);
                  }
                  break;
               case 9:
               case 10:
                  this._sign = Bitmap.getBitmapResource("resources/directions/turnSharpRight.png");
                  if (this._name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(382), new String[]{this._name, this._towards});
                  } else if (this._name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(381), new String[]{this._name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(383), new String[]{this._towards});
                  } else {
                     this._caption = LBSResources.getString(1);
                  }
                  break;
               case 11:
               case 12:
               case 13:
                  this._sign = Bitmap.getBitmapResource("resources/directions/turnRight.png");
                  if (this._name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(374), new String[]{this._name, this._towards});
                  } else if (this._name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(373), new String[]{this._name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(375), new String[]{this._towards});
                  } else {
                     this._caption = LBSResources.getString(16);
                  }
                  break;
               case 14:
               case 15:
                  this._sign = Bitmap.getBitmapResource("resources/directions/bearRight.png");
                  if (this._name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(362), new String[]{this._name, this._towards});
                  } else if (this._name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(361), new String[]{this._name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(363), new String[]{this._towards});
                  } else {
                     this._caption = LBSResources.getString(36);
                  }
            }

            this.createHighwayShield(this._name);
            break;
         case 4:
            this._sign = Bitmap.getBitmapResource("resources/directions/continue.png");
            if (this._name.length() > 0 && this._connector.length() > 0) {
               name = this._name + " - " + this._connector;
            } else if (this._name.length() > 0) {
               name = this._name;
            } else {
               name = this._connector;
            }

            this.createHighwayShield(name);
            if (name.length() > 0 && this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(451), new String[]{name, this._towards});
            } else if (name.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(240), new String[]{name});
            } else if (this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(452), new String[]{this._towards});
            } else {
               this._caption = LBSResources.getString(239);
            }
            break;
         case 5:
            switch (actionInfo) {
               case 0:
               case 8:
                  this._sign = Bitmap.getBitmapResource("resources/directions/followCurve.png");
                  break;
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
               default:
                  this._sign = Bitmap.getBitmapResource("resources/directions/followCurveLeft.png");
                  break;
               case 9:
               case 10:
               case 11:
               case 12:
               case 13:
               case 14:
               case 15:
                  this._sign = Bitmap.getBitmapResource("resources/directions/followCurveRight.png");
            }

            if (this._name.length() > 0 && this._connector.length() > 0) {
               name = this._name + " - " + this._connector;
            } else if (this._name.length() > 0) {
               name = this._name;
            } else {
               name = this._connector;
            }

            this.createHighwayShield(name);
            if (name.length() > 0 && this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(445), new String[]{name, this._towards});
            } else if (name.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(82), new String[]{name});
            } else if (this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(446), new String[]{this._towards});
            } else {
               this._caption = LBSResources.getString(55);
            }
            break;
         case 6:
            this._sign = Bitmap.getBitmapResource("resources/directions/continue.png");
            if (this._name.length() > 0 && this._connector.length() > 0) {
               name = this._name + " - " + this._connector;
            } else if (this._name.length() > 0) {
               name = this._name;
            } else {
               name = this._connector;
            }

            this.createHighwayShield(name);
            if (name.length() > 0 && this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(450), new String[]{name, this._towards});
            } else if (name.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(448), new String[]{name});
            } else if (this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(449), new String[]{this._towards});
            } else {
               this._caption = LBSResources.getString(55);
            }
            break;
         case 7:
            if (this._name.length() > 0 && this._connector.length() > 0) {
               name = this._name + " - " + this._connector;
            } else if (this._name.length() > 0) {
               name = this._name;
            } else {
               name = this._connector;
            }

            this.createHighwayShield(name);
            if (this._sign == null) {
               switch (actionInfo) {
                  case -1:
                     this._sign = Bitmap.getBitmapResource("resources/directions/USHighway.png");
                     break;
                  case 0:
                  default:
                     this._sign = Bitmap.getBitmapResource("resources/directions/continue.png");
                     break;
                  case 1:
                  case 2:
                     this._sign = Bitmap.getBitmapResource("resources/directions/bearLeft.png");
                     break;
                  case 3:
                  case 4:
                  case 5:
                     this._sign = Bitmap.getBitmapResource("resources/directions/turnLeft.png");
                     break;
                  case 6:
                  case 7:
                     this._sign = Bitmap.getBitmapResource("resources/directions/turnSharpLeft.png");
                     break;
                  case 8:
                     this._sign = Bitmap.getBitmapResource("resources/directions/uTurn.png");
                     break;
                  case 9:
                  case 10:
                     this._sign = Bitmap.getBitmapResource("resources/directions/turnSharpRight.png");
                     break;
                  case 11:
                  case 12:
                  case 13:
                     this._sign = Bitmap.getBitmapResource("resources/directions/turnRight.png");
                     break;
                  case 14:
                  case 15:
                     this._sign = Bitmap.getBitmapResource("resources/directions/bearRight.png");
               }
            }

            if (name.length() > 0 && this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(444), new String[]{name, this._towards});
            } else if (name.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(442), new String[]{name});
            } else if (this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(443), new String[]{this._towards});
            } else {
               switch (actionInfo) {
                  case -1:
                     this._caption = LBSResources.getString(153);
                     break label516;
                  case 0:
                  default:
                     this._caption = LBSResources.getString(364);
                     break label516;
                  case 1:
                  case 2:
                     this._caption = LBSResources.getString(356);
                     break label516;
                  case 3:
                  case 4:
                  case 5:
                     this._caption = LBSResources.getString(368);
                     break label516;
                  case 6:
                  case 7:
                     this._caption = LBSResources.getString(376);
                     break label516;
                  case 8:
                     this._caption = LBSResources.getString(384);
                     break label516;
                  case 9:
                  case 10:
                     this._caption = LBSResources.getString(380);
                     break label516;
                  case 11:
                  case 12:
                  case 13:
                     this._caption = LBSResources.getString(372);
                     break label516;
                  case 14:
                  case 15:
                     this._caption = LBSResources.getString(360);
               }
            }
            break;
         case 8:
            switch (actionInfo) {
               case 0:
                  if (this._exit.length() > 0) {
                     this._sign = Bitmap.getBitmapResource("resources/directions/exitNumRight.png");
                     int index = this._exit.indexOf(" ");
                     String exitNum;
                     if (index != -1) {
                        exitNum = this._exit.substring(0, index);
                     } else {
                        exitNum = this._exit;
                     }

                     this._bitMapText = LBSResources.getString(472) + " " + exitNum;
                     break;
                  }

                  this._sign = Bitmap.getBitmapResource("resources/directions/exitRight.png");
                  this._bitMapText = LBSResources.getString(472);
                  if (this._name.length() > 0 && this._connector.length() > 0) {
                     name = this._name + " - " + this._connector;
                  } else if (this._name.length() > 0) {
                     name = this._name;
                  } else {
                     name = this._connector;
                  }

                  this.createHighwayShield(name);
                  break;
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
               default:
                  if (this._exit.length() > 0) {
                     this._sign = Bitmap.getBitmapResource("resources/directions/exitNumLeft.png");
                     int index = this._exit.indexOf(" ");
                     String exitNum;
                     if (index != -1) {
                        exitNum = this._exit.substring(0, index);
                     } else {
                        exitNum = this._exit;
                     }

                     this._bitMapText = LBSResources.getString(472) + " " + exitNum;
                  } else {
                     this._sign = Bitmap.getBitmapResource("resources/directions/exitLeft.png");
                     this._bitMapText = LBSResources.getString(472);
                  }
            }

            int attributes = 0;
            if (this._exit.length() > 0) {
               attributes |= 4096;
            }

            if (this._connector.length() > 0) {
               attributes |= 256;
            }

            if (this._name.length() > 0) {
               attributes |= 16;
            }

            if (this._towards.length() > 0) {
               attributes |= 1;
            }

            switch (attributes) {
               case 0:
                  this._caption = LBSResources.getString(422);
                  break label516;
               case 1:
                  this._caption = MessageFormat.format(LBSResources.getString(436), new String[]{this._towards});
                  break label516;
               case 16:
                  this._caption = MessageFormat.format(LBSResources.getString(434), new String[]{this._name});
                  break label516;
               case 17:
                  this._caption = MessageFormat.format(LBSResources.getString(435), new String[]{this._name, this._towards});
                  break label516;
               case 256:
                  this._caption = MessageFormat.format(LBSResources.getString(430), new String[]{this._connector});
                  break label516;
               case 257:
                  this._caption = MessageFormat.format(LBSResources.getString(433), new String[]{this._connector, this._towards});
                  break label516;
               case 272:
                  this._caption = MessageFormat.format(LBSResources.getString(431), new String[]{this._connector, this._name});
                  break label516;
               case 273:
                  this._caption = MessageFormat.format(LBSResources.getString(432), new String[]{this._connector, this._name, this._towards});
                  break label516;
               case 4096:
                  this._caption = MessageFormat.format(LBSResources.getString(349), new String[]{this._exit});
                  break label516;
               case 4097:
                  this._caption = MessageFormat.format(LBSResources.getString(429), new String[]{this._exit, this._towards});
                  break label516;
               case 4112:
                  this._caption = MessageFormat.format(LBSResources.getString(427), new String[]{this._exit, this._name});
                  break label516;
               case 4113:
                  this._caption = MessageFormat.format(LBSResources.getString(428), new String[]{this._exit, this._name, this._towards});
                  break label516;
               case 4352:
                  this._caption = MessageFormat.format(LBSResources.getString(423), new String[]{this._exit, this._connector});
                  break label516;
               case 4353:
                  this._caption = MessageFormat.format(LBSResources.getString(426), new String[]{this._exit, this._connector, this._towards});
                  break label516;
               case 4368:
                  this._caption = MessageFormat.format(LBSResources.getString(424), new String[]{this._exit, this._connector, this._name});
                  break label516;
               case 4369:
                  this._caption = MessageFormat.format(LBSResources.getString(425), new String[]{this._exit, this._connector, this._name, this._towards});
               default:
                  break label516;
            }
         case 9:
            this._sign = Bitmap.getBitmapResource("resources/directions/enterCircle.png");
            if (this._name.length() > 0 && this._connector.length() > 0) {
               name = this._name + " - " + this._connector;
            } else if (this._name.length() > 0) {
               name = this._name;
            } else {
               name = this._connector;
            }

            if (name.length() > 0 && this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(460), new String[]{name, this._towards});
            } else if (name.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(459), new String[]{name});
            } else if (this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(461), new String[]{this._towards});
            } else {
               this._caption = LBSResources.getString(167);
            }
            break;
         case 10:
            if (this._name.length() > 0 && this._connector.length() > 0) {
               name = this._name + " - " + this._connector;
            } else if (this._name.length() > 0) {
               name = this._name;
            } else {
               name = this._connector;
            }

            this._bitMapText = "" + actionInfo;
            this._sign = Bitmap.getBitmapResource("resources/directions/enterCircle.png");
            switch (actionInfo) {
               case 0:
                  if (name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(480), new String[]{this._bitMapText, name, this._towards});
                  } else if (name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(479), new String[]{this._bitMapText, name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(481), new String[]{this._bitMapText, this._towards});
                  } else {
                     this._caption = MessageFormat.format(LBSResources.getString(478), new String[]{this._bitMapText});
                  }
                  break label516;
               case 1:
               default:
                  if (name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(453), new String[]{name, this._towards});
                  } else if (name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(252), new String[]{name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(454), new String[]{this._towards});
                  } else {
                     this._caption = LBSResources.getString(251);
                  }
                  break label516;
               case 2:
                  if (name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(455), new String[]{name, this._towards});
                  } else if (name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(254), new String[]{name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(456), new String[]{this._towards});
                  } else {
                     this._caption = LBSResources.getString(253);
                  }
                  break label516;
               case 3:
                  if (name.length() > 0 && this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(457), new String[]{name, this._towards});
                  } else if (name.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(256), new String[]{name});
                  } else if (this._towards.length() > 0) {
                     this._caption = MessageFormat.format(LBSResources.getString(458), new String[]{this._towards});
                  } else {
                     this._caption = LBSResources.getString(255);
                  }
                  break label516;
            }
         case 16:
            int direction = 0;
            switch (actionInfo) {
               case 0:
               case 8:
                  this._sign = Bitmap.getBitmapResource("resources/directions/continue.png");
                  break;
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
               default:
                  this._sign = Bitmap.getBitmapResource("resources/directions/merge2Left.png");
                  direction = 1;
                  break;
               case 9:
               case 10:
               case 11:
               case 12:
               case 13:
               case 14:
               case 15:
                  this._sign = Bitmap.getBitmapResource("resources/directions/merge2Right.png");
                  direction = 2;
            }

            if (this._name.length() > 0 && this._connector.length() > 0) {
               name = this._name + " - " + this._connector;
            } else if (this._name.length() > 0) {
               name = this._name;
            } else {
               name = this._connector;
            }

            this.createHighwayShield(name);
            if (direction == 1) {
               if (name.length() > 0 && this._towards.length() > 0) {
                  this._caption = MessageFormat.format(LBSResources.getString(466), new String[]{name, this._towards});
               } else if (name.length() > 0) {
                  this._caption = MessageFormat.format(LBSResources.getString(465), new String[]{name});
               } else if (this._towards.length() > 0) {
                  this._caption = MessageFormat.format(LBSResources.getString(467), new String[]{this._towards});
               } else {
                  this._caption = LBSResources.getString(464);
               }
            } else if (direction == 2) {
               if (name.length() > 0 && this._towards.length() > 0) {
                  this._caption = MessageFormat.format(LBSResources.getString(470), new String[]{name, this._towards});
               } else if (name.length() > 0) {
                  this._caption = MessageFormat.format(LBSResources.getString(469), new String[]{name});
               } else if (this._towards.length() > 0) {
                  this._caption = MessageFormat.format(LBSResources.getString(471), new String[]{this._towards});
               } else {
                  this._caption = LBSResources.getString(468);
               }
            } else if (name.length() > 0 && this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(462), new String[]{name, this._towards});
            } else if (name.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(270), new String[]{name});
            } else if (this._towards.length() > 0) {
               this._caption = MessageFormat.format(LBSResources.getString(463), new String[]{this._towards});
            } else {
               this._caption = LBSResources.getString(269);
            }
      }

      if (action != 2) {
         float numKilometres = this._distance / 1148846080;
         this._caption = this._caption + " " + MessageFormat.format(LBSResources.getString(199), new String[]{Distance.formatDistance(numKilometres)});
      }

      super._label = this._caption;
      super._routeSign = this._sign;
      super._routeSignText = this._bitMapText;
      super._routName = name == null ? this._name : name;
      this._action = action;
   }

   private final void createHighwayShield(String name) {
      String signText = "";
      Bitmap sign = null;
      name = name.toUpperCase();
      if (name.length() > 0) {
         if (name.indexOf("HWY ") != -1) {
            signText = this.getHwyDetail(name, "HWY ");
            sign = Bitmap.getBitmapResource("resources/directions/provincialHighway.png");
         } else if (name.indexOf("TRANS-CANADA HWY ") != -1) {
            signText = this.getHwyDetail(name, "TRANS-CANADA HWY ");
            sign = Bitmap.getBitmapResource("resources/directions/transcanadaHighway.png");
         } else if (name.indexOf("T-C HWY ") != -1) {
            signText = this.getHwyDetail(name, "T-C HWY ");
            sign = Bitmap.getBitmapResource("resources/directions/transcanadaHighway.png");
         } else if (name.indexOf("I-") != -1) {
            signText = this.getHwyDetail(name, "I-");
            sign = Bitmap.getBitmapResource("resources/directions/interstate.png");
         } else if (name.indexOf("I ") != -1) {
            signText = this.getHwyDetail(name, "I ");
            sign = Bitmap.getBitmapResource("resources/directions/interstate.png");
         } else if (name.indexOf("AUT ") != -1) {
            signText = this.getHwyDetail(name, "AUT ");
            sign = Bitmap.getBitmapResource("resources/directions/quebecHighway.png");
         } else if (name.indexOf("AUTOROUTE ") != -1) {
            signText = this.getHwyDetail(name, "AUTOROUTE ");
            sign = Bitmap.getBitmapResource("resources/directions/quebecHighway.png");
         } else if (name.indexOf("AUTO ROUTE ") != -1) {
            signText = this.getHwyDetail(name, "AUTO ROUTE ");
            sign = Bitmap.getBitmapResource("resources/directions/quebecHighway.png");
         } else if (name.indexOf("US-") != -1) {
            signText = this.getHwyDetail(name, "US-");
            sign = Bitmap.getBitmapResource("resources/directions/USHighway.png");
         } else if (name.indexOf("US ") != -1) {
            signText = this.getHwyDetail(name, "US ");
            sign = Bitmap.getBitmapResource("resources/directions/USHighway.png");
         }
      }

      if (signText.length() > 0) {
         Decision lastDecision = this._route._decisions.getAt(this._route._decisions._count - 1);
         String lastSignText = lastDecision._bitMapText;
         if (lastSignText == null) {
            lastSignText = "";
         }

         if (!signText.equals(lastSignText)) {
            this._bitMapText = signText;
            this._sign = sign;
            return;
         }

         if (lastDecision._action == 8) {
            this._bitMapText = signText;
            this._sign = sign;
            lastDecision._bitMapText = LBSResources.getString(472);
            switch (lastDecision._actionInfo) {
               case 0:
                  lastDecision._sign = Bitmap.getBitmapResource("resources/directions/exitRight.png");
                  break;
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
               default:
                  lastDecision._sign = Bitmap.getBitmapResource("resources/directions/exitLeft.png");
                  return;
            }
         }
      }
   }

   public final String getHwyDetail(String name, String searchString) {
      String number = "";
      String direction = "";
      direction = this.getHwyDirection(name);
      number = this.getHwyNumber(name, searchString);
      int i = 0;
      boolean isNumber = true;

      while (i < number.length()) {
         if (number.charAt(i) != '0'
            && number.charAt(i) != '1'
            && number.charAt(i) != '2'
            && number.charAt(i) != '3'
            && number.charAt(i) != '4'
            && number.charAt(i) != '5'
            && number.charAt(i) != '6'
            && number.charAt(i) != '7'
            && number.charAt(i) != '8'
            && number.charAt(i) != '9') {
            isNumber = false;
            break;
         }

         i++;
      }

      return isNumber && number.length() != 0 ? number + direction : "";
   }

   private final String getHwyDirection(String name) {
      String direction = "";
      if (name.endsWith(" W")
         || name.indexOf(" W ") != -1
         || name.indexOf(" W-") != -1
         || name.indexOf(" W/") != -1
         || name.endsWith(" WB")
         || name.indexOf(" WB ") != -1
         || name.indexOf(" WB-") != -1
         || name.indexOf(" WB/") != -1) {
         return " " + LBSResources.getString(133);
      }

      if (name.endsWith(" E")
         || name.indexOf(" E ") != -1
         || name.indexOf(" E-") != -1
         || name.indexOf(" E/") != -1
         || name.endsWith(" EB")
         || name.indexOf(" EB ") != -1
         || name.indexOf(" EB-") != -1
         || name.indexOf(" EB/") != -1) {
         return " " + LBSResources.getString(129);
      }

      if (!name.endsWith(" N")
         && name.indexOf(" N ") == -1
         && name.indexOf(" N-") == -1
         && name.indexOf(" N/") == -1
         && !name.endsWith(" NB")
         && name.indexOf(" NB ") == -1
         && name.indexOf(" NB-") == -1
         && name.indexOf(" NB/") == -1) {
         if (name.endsWith(" S")
            || name.indexOf(" S ") != -1
            || name.indexOf(" S-") != -1
            || name.indexOf(" S/") != -1
            || name.endsWith(" SB")
            || name.indexOf(" SB ") != -1
            || name.indexOf(" SB-") != -1
            || name.indexOf(" SB/") != -1) {
            direction = " " + LBSResources.getString(53);
         }

         return direction;
      } else {
         return " " + LBSResources.getString(52);
      }
   }

   private final String getHwyNumber(String name, String searchString) {
      String number = "";
      int index = name.indexOf(searchString);
      if (index != -1) {
         number = name.substring(index + searchString.length());
      }

      index = number.indexOf(" ");
      if (index != -1) {
         number = number.substring(0, index);
      }

      index = number.indexOf("-");
      if (index != -1) {
         number = number.substring(0, index);
      }

      index = number.indexOf("/");
      if (index != -1) {
         number = number.substring(0, index);
      }

      return number;
   }

   @Override
   public final String toString() {
      return "Decision: latitude: "
         + super._latitude / 1203982336
         + ", longitude: "
         + super._longitude / 1203982336
         + ", action: "
         + this._action
         + ", actionInfo: "
         + this._actionInfo
         + ", distance: "
         + this._distance
         + ", address: "
         + this._address
         + ", description: "
         + super._description;
   }

   public final void paint(Graphics graphics, Transform transform, boolean focus, boolean drawCaption) {
      super.paint(graphics, transform, focus, drawCaption, this._number);
   }
}
