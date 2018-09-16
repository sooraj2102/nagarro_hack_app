package dubeyanurag.com.github.healthyrestaurant.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioGroup;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;

public class UserDetailActivity_ViewBinding
  implements Unbinder
{
  private UserDetailActivity target;
  private View view2131296296;

  @UiThread
  public UserDetailActivity_ViewBinding(UserDetailActivity paramUserDetailActivity)
  {
    this(paramUserDetailActivity, paramUserDetailActivity.getWindow().getDecorView());
  }

  @UiThread
  public UserDetailActivity_ViewBinding(UserDetailActivity paramUserDetailActivity, View paramView)
  {
    this.target = paramUserDetailActivity;
    paramUserDetailActivity.name = ((EditText)Utils.findRequiredViewAsType(paramView, 2131296368, "field 'name'", EditText.class));
    paramUserDetailActivity.age = ((EditText)Utils.findRequiredViewAsType(paramView, 2131296366, "field 'age'", EditText.class));
    paramUserDetailActivity.height = ((EditText)Utils.findRequiredViewAsType(paramView, 2131296367, "field 'height'", EditText.class));
    paramUserDetailActivity.weight = ((EditText)Utils.findRequiredViewAsType(paramView, 2131296370, "field 'weight'", EditText.class));
    paramUserDetailActivity.gend = ((RadioGroup)Utils.findRequiredViewAsType(paramView, 2131296347, "field 'gend'", RadioGroup.class));
    paramUserDetailActivity.diebat = ((RadioGroup)Utils.findRequiredViewAsType(paramView, 2131296328, "field 'diebat'", RadioGroup.class));
    paramUserDetailActivity.hear = ((RadioGroup)Utils.findRequiredViewAsType(paramView, 2131296354, "field 'hear'", RadioGroup.class));
    View localView = Utils.findRequiredView(paramView, 2131296296, "method 'onSubmit'");
    this.view2131296296 = localView;
    localView.setOnClickListener(new DebouncingOnClickListener(paramUserDetailActivity)
    {
      public void doClick(View paramView)
      {
        this.val$target.onSubmit();
      }
    });
  }

  @CallSuper
  public void unbind()
  {
    UserDetailActivity localUserDetailActivity = this.target;
    if (localUserDetailActivity == null)
      throw new IllegalStateException("Bindings already cleared.");
    this.target = null;
    localUserDetailActivity.name = null;
    localUserDetailActivity.age = null;
    localUserDetailActivity.height = null;
    localUserDetailActivity.weight = null;
    localUserDetailActivity.gend = null;
    localUserDetailActivity.diebat = null;
    localUserDetailActivity.hear = null;
    this.view2131296296.setOnClickListener(null);
    this.view2131296296 = null;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.activities.UserDetailActivity_ViewBinding
 * JD-Core Version:    0.6.0
 */