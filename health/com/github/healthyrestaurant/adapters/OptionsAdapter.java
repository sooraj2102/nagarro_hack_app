package dubeyanurag.com.github.healthyrestaurant.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import dubeyanurag.com.github.healthyrestaurant.activities.RestaurantDetailActivity;
import dubeyanurag.com.github.healthyrestaurant.models.DishDetails;
import dubeyanurag.com.github.healthyrestaurant.models.MenuResponseModel;
import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<ViewHolder>
{
  private List<MenuResponseModel> array;
  private Context context;

  public OptionsAdapter(Context paramContext)
  {
    this.context = paramContext;
  }

  public void clearData()
  {
    if (this.array != null)
    {
      this.array.clear();
      notifyDataSetChanged();
    }
  }

  public int getItemCount()
  {
    if (this.array != null)
      return this.array.size();
    return 0;
  }

  public void onBindViewHolder(ViewHolder paramViewHolder, int paramInt)
  {
    MenuResponseModel localMenuResponseModel = (MenuResponseModel)this.array.get(paramInt);
    paramViewHolder.Dish.setText(localMenuResponseModel.getDishDetails().getDish());
    paramViewHolder.nutrient.setText("Calories : " + localMenuResponseModel.getDishDetails().getCalories());
    paramViewHolder.restaurantName.setText(localMenuResponseModel.getName());
    Picasso.with(this.context).load(localMenuResponseModel.getDishDetails().getThumb()).into(paramViewHolder.imgView);
    paramViewHolder.imgView.setOnClickListener(new View.OnClickListener(localMenuResponseModel)
    {
      public void onClick(View paramView)
      {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(OptionsAdapter.this.context);
        localBuilder.setTitle("Nutrition Contents.");
        localBuilder.setMessage("Sugar: " + this.val$dishItem.getSugar() + "\nProteins :" + this.val$dishItem.getProtein() + "\nFiber : " + this.val$dishItem.getFiber() + "\nCalories : " + this.val$dishItem.getCalories() + "\nCarbohydrates : " + this.val$dishItem.getCarbohydrate() + "\n Cholestrol" + this.val$dishItem.getCholesterol() + "\n Saturated Fat" + this.val$dishItem.getSaturatedFat());
        localBuilder.setCancelable(true);
        localBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            paramDialogInterface.cancel();
          }
        });
        localBuilder.create().show();
      }
    });
    paramViewHolder.view.setOnClickListener(new View.OnClickListener(localMenuResponseModel)
    {
      public void onClick(View paramView)
      {
        Bundle localBundle = new Bundle();
        localBundle.putString("rid", this.val$dishItem.getZomatoId() + "");
        Intent localIntent = new Intent(OptionsAdapter.this.context.getApplicationContext(), RestaurantDetailActivity.class);
        localIntent.putExtras(localBundle);
        OptionsAdapter.this.context.startActivity(localIntent);
      }
    });
  }

  public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    return new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2131427395, paramViewGroup, false));
  }

  public void setData(List<MenuResponseModel> paramList)
  {
    this.array = paramList;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder
  {

    @BindView(2131296258)
    TextView Dish;

    @BindView(2131296362)
    ImageView imgView;

    @BindView(2131296411)
    TextView nutrient;

    @BindView(2131296430)
    TextView restaurantName;
    View view;

    public ViewHolder(View arg2)
    {
      super();
      ButterKnife.bind(this, localView);
      this.view = localView;
      localView.setOnClickListener(new View.OnClickListener(OptionsAdapter.this)
      {
        public void onClick(View paramView)
        {
        }
      });
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.adapters.OptionsAdapter
 * JD-Core Version:    0.6.0
 */