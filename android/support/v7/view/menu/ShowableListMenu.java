package android.support.v7.view.menu;

import android.support.annotation.RestrictTo;
import android.widget.ListView;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public abstract interface ShowableListMenu
{
  public abstract void dismiss();

  public abstract ListView getListView();

  public abstract boolean isShowing();

  public abstract void show();
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.view.menu.ShowableListMenu
 * JD-Core Version:    0.6.0
 */