package dubeyanurag.com.github.healthyrestaurant.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;

public class NlpActivity_ViewBinding
  implements Unbinder
{
  private NlpActivity target;
  private View view2131296353;

  @UiThread
  public NlpActivity_ViewBinding(NlpActivity paramNlpActivity)
  {
    this(paramNlpActivity, paramNlpActivity.getWindow().getDecorView());
  }

  @UiThread
  public NlpActivity_ViewBinding(NlpActivity paramNlpActivity, View paramView)
  {
    this.target = paramNlpActivity;
    paramNlpActivity.q = ((EditText)Utils.findRequiredViewAsType(paramView, 2131296422, "field 'q'", EditText.class));
    paramNlpActivity.res = ((TextView)Utils.findRequiredViewAsType(paramView, 2131296428, "field 'res'", TextView.class));
    View localView = Utils.findRequiredView(paramView, 2131296353, "field 'go' and method 'go'");
    paramNlpActivity.go = ((Button)Utils.castView(localView, 2131296353, "field 'go'", Button.class));
    this.view2131296353 = localView;
    localView.setOnClickListener(new DebouncingOnClickListener(paramNlpActivity)
    {
      public void doClick(View paramView)
      {
        this.val$target.go();
      }
    });
    paramNlpActivity.progressLayout = ((LinearLayout)Utils.findRequiredViewAsType(paramView, 2131296419, "field 'progressLayout'", LinearLayout.class));
  }

  @CallSuper
  public void unbind()
  {
    NlpActivity localNlpActivity = this.target;
    if (localNlpActivity == null)
      throw new IllegalStateException("Bindings already cleared.");
    this.target = null;
    localNlpActivity.q = null;
    localNlpActivity.res = null;
    localNlpActivity.go = null;
    localNlpActivity.progressLayout = null;
    this.view2131296353.setOnClickListener(null);
    this.view2131296353 = null;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.activities.NlpActivity_ViewBinding
 * JD-Core Version:    0.6.0
 */