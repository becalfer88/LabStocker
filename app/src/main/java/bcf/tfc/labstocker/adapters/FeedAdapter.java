package bcf.tfc.labstocker.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bcf.tfc.labstocker.MainActivity;
import bcf.tfc.labstocker.R;
import bcf.tfc.labstocker.UnderConstructionActivity;
import bcf.tfc.labstocker.fragments.FormFragment;
import bcf.tfc.labstocker.utils.Utils;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private List<ItemFeed> feedList;
    private Context context;

    public FeedAdapter(List<ItemFeed> feedList) {
        this.feedList = feedList;
    }

    // Inflate the layout to create a ViewHolder
    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    // Bind the data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        holder.bind(feedList.get(position));
        context = holder.itemView.getContext();
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView quantity;
        Button editItem;
        Button deleteItem;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            quantity = itemView.findViewById(R.id.quantity);
            editItem = itemView.findViewById(R.id.edit_item);
            deleteItem = itemView.findViewById(R.id.delete_item);
        }

        public void bind(ItemFeed feed) {
            if (feed == null){
                return;
            }
            if (feed.getQuantity() == null){
                quantity.setVisibility(View.INVISIBLE);
            } else {
                quantity.setVisibility(View.VISIBLE);
                quantity.setText(String.valueOf(feed.getQuantity()));
            }
            name.setText(feed.getName());

            editItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (feed. getParentType() != null &&feed.getParentType().equals("Subject")){
                        FormFragment fragment = FormFragment.newInstance("practices", feed.getId(), feed.getIdParent());
                        ((MainActivity) context).loadFragment(fragment);
                    } else {
                        Intent intent = new Intent(v.getContext(), UnderConstructionActivity.class);
                        (v.getContext()).startActivity(intent);
                    }
                }
            });

            if (feed.getIdParent() == null){
                deleteItem.setVisibility(View.INVISIBLE);
            } else {
                deleteItem.setVisibility(View.VISIBLE);
                deleteItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog dialog = Utils.getDeleteDialog(v.getContext(), feed);
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(v.getContext(), R.color.secondaryDark));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(v.getContext(), R.color.primaryDark));
                    }
                });
            }
        }
    }

}


