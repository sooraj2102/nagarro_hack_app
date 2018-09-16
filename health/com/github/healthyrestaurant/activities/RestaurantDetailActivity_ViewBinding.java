package dubeyanurag.com.github.healthyrestaurant.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;

public class RestaurantDetailActivity_ViewBinding
  implements Unbinder
{
  private RestaurantDetailActivity target;
  private View view2131296329;
  private View view2131296402;
  private View view2131296456;

  @UiThread
  public RestaurantDetailActivity_ViewBinding(RestaurantDetailActivity paramRestaurantDetailActivity)
  {
    this(paramRestaurantDetailActivity, paramRestaurantDetailActivity.getWindow().getDecorView());
  }

  @UiThread
  public RestaurantDetailActivity_ViewBinding(RestaurantDetailActivity paramRestaurantDetailActivity, View paramView)
  {
    this.target = paramRestaurantDetailActivity;
    paramRestaurantDetailActivity.text1ViewContainer = ((ShimmerFrameLayout)Utils.findRequiredViewAsType(paramView, 2131296481, "field 'text1ViewContainer'", ShimmerFrameLayout.class));
    paramRestaurantDetailActivity.text2ViewContainer = ((ShimmerFrameLayout)Utils.findRequiredViewAsType(paramView, 2131296483, "field 'text2ViewContainer'", ShimmerFrameLayout.class));
    paramRestaurantDetailActivity.collapsingToolbarLayout = ((CollapsingToolbarLayout)Utils.findRequiredViewAsType(paramView, 2131296494, "field 'collapsingToolbarLayout'", CollapsingToolbarLayout.class));
    paramRestaurantDetailActivity.text1 = ((TextView)Utils.findRequiredViewAsType(paramView, 2131296480, "field 'text1'", TextView.class));
    paramRestaurantDetailActivity.text2 = ((TextView)Utils.findRequiredViewAsType(paramView, 2131296482, "field 'text2'", TextView.class));
    paramRestaurantDetailActivity.img1 = ((ImageView)Utils.findRequiredViewAsType(paramView, 2131296363, "field 'img1'", ImageView.class));
    paramRestaurantDetailActivity.img2 = ((ImageView)Utils.findRequiredViewAsType(paramView, 2131296364, "field 'img2'", ImageView.class));
    View localView1 = Utils.findRequiredView(paramView, 2131296402, "field 'morePhotos' and method 'onPhotosClick'");
    paramRestaurantDetailActivity.morePhotos = ((TextView)Utils.castView(localView1, 2131296402, "field 'morePhotos'", TextView.class));
    this.view2131296402 = localView1;
    localView1.setOnClickListener(new DebouncingOnClickListener(paramRestaurantDetailActivity)
    {
      public void doClick(View paramView)
      {
        this.val$target.onPhotosClick();
      }
    });
    paramRestaurantDetailActivity.ratingText = ((TextView)Utils.findRequiredViewAsType(paramView, 2131296426, "field 'ratingText'", TextView.class));
    paramRestaurantDetailActivity.ratingBar = ((RatingBar)Utils.findRequiredViewAsType(paramView, 2131296425, "field 'ratingBar'", RatingBar.class));
    paramRestaurantDetailActivity.count = ((TextView)Utils.findRequiredViewAsType(paramView, 2131296314, "field 'count'", TextView.class));
    View localView2 = Utils.findRequiredView(paramView, 2131296456, "field 'seeMenu' and method 'onSeeMenuClick'");
    paramRestaurantDetailActivity.seeMenu = ((TextView)Utils.castView(localView2, 2131296456, "field 'seeMenu'", TextView.class));
    this.view2131296456 = localView2;
    localView2.setOnClickListener(new DebouncingOnClickListener(paramRestaurantDetailActivity)
    {
      public void doClick(View paramView)
      {
        this.val$target.onSeeMenuClick();
      }
    });
    View localView3 = Utils.findRequiredView(paramView, 2131296329, "method 'onDirectionsClick'");
    this.view2131296329 = localView3;
    localView3.setOnClickListener(new DebouncingOnClickListener(paramRestaurantDetailActivity)
    {
      public void doClick(View paramView)
      {
        this.val$target.onDirectionsClick();
      }
    });
  }

  @CallSuper
  public void unbind()
  {
    RestaurantDetailActivity localRestaurantDetailActivity = this.target;
    if (localRestaurantDetailActivity == null)
      throw new IllegalStateException("Bindings already cleared.");
    this.target = null;
    localRestaurantDetailActivity.text1ViewContainer = null;
    localRestaurantDetailActivity.text2ViewContainer = null;
    localRestaurantDetailActivity.collapsingToolbarLayout = null;
    localRestaurantDetailActivity.text1 = null;
    localRestaurantDetailActivity.text2 = null;
    localRestaurantDetailActivity.img1 = null;
    localRestaurantDetailActivity.img2 = null;
    localRestaurantDetailActivity.morePhotos = null;
    localRestaurantDetailActivity.ratingText = null;
    localRestaurantDetailActivity.ratingBar = null;
    localRestaurantDetailActivity.count = null;
    localRestaurantDetailActivity.seeMenu = null;
    this.view2131296402.setOnClickListener(null);
    this.view2131296402 = null;
    this.view2131296456.setOnClickListener(null);
    this.view2131296456 = null;
    this.view2131296329.setOnClickListener(null);
    this.view2131296329 = null;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.activities.RestaurantDetailActivity_ViewBinding
 * JD-Core Version:    0.6.0
 */