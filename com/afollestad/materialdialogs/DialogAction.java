package com.afollestad.materialdialogs;

public enum DialogAction
{
  static
  {
    NEUTRAL = new DialogAction("NEUTRAL", 1);
    NEGATIVE = new DialogAction("NEGATIVE", 2);
    DialogAction[] arrayOfDialogAction = new DialogAction[3];
    arrayOfDialogAction[0] = POSITIVE;
    arrayOfDialogAction[1] = NEUTRAL;
    arrayOfDialogAction[2] = NEGATIVE;
    $VALUES = arrayOfDialogAction;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.DialogAction
 * JD-Core Version:    0.6.0
 */