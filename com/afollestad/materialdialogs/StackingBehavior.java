package com.afollestad.materialdialogs;

public enum StackingBehavior
{
  static
  {
    ADAPTIVE = new StackingBehavior("ADAPTIVE", 1);
    NEVER = new StackingBehavior("NEVER", 2);
    StackingBehavior[] arrayOfStackingBehavior = new StackingBehavior[3];
    arrayOfStackingBehavior[0] = ALWAYS;
    arrayOfStackingBehavior[1] = ADAPTIVE;
    arrayOfStackingBehavior[2] = NEVER;
    $VALUES = arrayOfStackingBehavior;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.afollestad.materialdialogs.StackingBehavior
 * JD-Core Version:    0.6.0
 */