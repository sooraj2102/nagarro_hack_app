package android.support.v7.app;

import android.content.Context;
import android.support.annotation.RequiresApi;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.Window;
import android.view.Window.Callback;
import java.util.List;

@RequiresApi(24)
class AppCompatDelegateImplN extends AppCompatDelegateImplV23
{
  AppCompatDelegateImplN(Context paramContext, Window paramWindow, AppCompatCallback paramAppCompatCallback)
  {
    super(paramContext, paramWindow, paramAppCompatCallback);
  }

  Window.Callback wrapWindowCallback(Window.Callback paramCallback)
  {
    return new AppCompatWindowCallbackN(paramCallback);
  }

  class AppCompatWindowCallbackN extends AppCompatDelegateImplV23.AppCompatWindowCallbackV23
  {
    AppCompatWindowCallbackN(Window.Callback arg2)
    {
      super(localCallback);
    }

    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> paramList, Menu paramMenu, int paramInt)
    {
      AppCompatDelegateImplV9.PanelFeatureState localPanelFeatureState = AppCompatDelegateImplN.this.getPanelState(0, true);
      if ((localPanelFeatureState != null) && (localPanelFeatureState.menu != null))
      {
        super.onProvideKeyboardShortcuts(paramList, localPanelFeatureState.menu, paramInt);
        return;
      }
      super.onProvideKeyboardShortcuts(paramList, paramMenu, paramInt);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.app.AppCompatDelegateImplN
 * JD-Core Version:    0.6.0
 */