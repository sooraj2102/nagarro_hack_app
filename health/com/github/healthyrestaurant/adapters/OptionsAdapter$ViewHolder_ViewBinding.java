package dubeyanurag.com.github.healthyrestaurant.adapters;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;

public class OptionsAdapter$ViewHolder_ViewBinding
  implements Unbinder
{
  private OptionsAdapter.ViewHolder target;

  @UiThread
  public OptionsAdapter$ViewHolder_ViewBinding(OptionsAdapter.ViewHolder paramViewHolder, View paramView)
  {
    this.target = paramViewHolder;
    paramViewHolder.Dish = ((TextView)Utils.findRequiredViewAsType(paramView, 2131296258, "field 'Dish'", TextView.class));
    paramViewHolder.nutrient = ((TextView)Utils.findRequiredViewAsType(paramView, 2131296411, "field 'nutrient'", TextView.class));
    paramViewHolder.restaurantName = ((TextView)Utils.findRequiredViewAsType(paramView, 2131296430, "field 'restaurantName'", TextView.class));
    paramViewHolder.imgView = ((ImageView)Utils.findRequiredViewAsType(paramView, 2131296362, "field 'imgView'", ImageView.class));
  }

  @CallSuper
  public void unbind()
  {
    OptionsAdapter.ViewHolder localViewHolder = this.target;
    if (localViewHolder == null)
      throw new IllegalStateException("Bindings already cleared.");
    this.target = null;
    localViewHolder.Dish = null;
    localViewHolder.nutrient = null;
    localViewHolder.restaurantName = null;
    localViewHolder.imgView = null;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.adapters.OptionsAdapter.ViewHolder_ViewBinding
 * JD-Core Version:    0.6.0
 */