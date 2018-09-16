package dubeyanurag.com.github.healthyrestaurant.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;

public class MainActivity_ViewBinding
  implements Unbinder
{
  private MainActivity target;
  private View view2131296308;

  @UiThread
  public MainActivity_ViewBinding(MainActivity paramMainActivity)
  {
    this(paramMainActivity, paramMainActivity.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(MainActivity paramMainActivity, View paramView)
  {
    this.target = paramMainActivity;
    paramMainActivity.fab = ((FloatingActionButton)Utils.findRequiredViewAsType(paramView, 2131296341, "field 'fab'", FloatingActionButton.class));
    paramMainActivity.confirmSaveContainer = ((ViewGroup)Utils.findRequiredViewAsType(paramView, 2131296310, "field 'confirmSaveContainer'", ViewGroup.class));
    paramMainActivity.resultsScrim = Utils.findRequiredView(paramView, 2131296431, "field 'resultsScrim'");
    paramMainActivity.saveConfirmed = ((Button)Utils.findRequiredViewAsType(paramView, 2131296436, "field 'saveConfirmed'", Button.class));
    paramMainActivity.scrollView = ((NestedScrollView)Utils.findRequiredViewAsType(paramView, 2131296444, "field 'scrollView'", NestedScrollView.class));
    paramMainActivity.recyclerView = ((RecyclerView)Utils.findRequiredViewAsType(paramView, 2131296427, "field 'recyclerView'", RecyclerView.class));
    paramMainActivity.rg = ((RadioGroup)Utils.findRequiredViewAsType(paramView, 2131296412, "field 'rg'", RadioGroup.class));
    paramMainActivity.spinner = ((AppCompatSpinner)Utils.findRequiredViewAsType(paramView, 2131296411, "field 'spinner'", AppCompatSpinner.class));
    View localView = Utils.findRequiredView(paramView, 2131296308, "field 'tv' and method 'newAct'");
    paramMainActivity.tv = ((TextView)Utils.castView(localView, 2131296308, "field 'tv'", TextView.class));
    this.view2131296308 = localView;
    localView.setOnClickListener(new DebouncingOnClickListener(paramMainActivity)
    {
      public void doClick(View paramView)
      {
        this.val$target.newAct();
      }
    });
    paramMainActivity.progressLayout = ((LinearLayout)Utils.findRequiredViewAsType(paramView, 2131296419, "field 'progressLayout'", LinearLayout.class));
  }

  @CallSuper
  public void unbind()
  {
    MainActivity localMainActivity = this.target;
    if (localMainActivity == null)
      throw new IllegalStateException("Bindings already cleared.");
    this.target = null;
    localMainActivity.fab = null;
    localMainActivity.confirmSaveContainer = null;
    localMainActivity.resultsScrim = null;
    localMainActivity.saveConfirmed = null;
    localMainActivity.scrollView = null;
    localMainActivity.recyclerView = null;
    localMainActivity.rg = null;
    localMainActivity.spinner = null;
    localMainActivity.tv = null;
    localMainActivity.progressLayout = null;
    this.view2131296308.setOnClickListener(null);
    this.view2131296308 = null;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.activities.MainActivity_ViewBinding
 * JD-Core Version:    0.6.0
 */