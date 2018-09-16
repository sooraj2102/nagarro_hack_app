package android.support.v4.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

@Deprecated
public abstract interface LayoutInflaterFactory
{
  public abstract View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.view.LayoutInflaterFactory
 * JD-Core Version:    0.6.0
 */