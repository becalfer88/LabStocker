package bcf.tfc.labstocker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bcf.tfc.labstocker.R;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private List<ItemFeed> feedList;

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
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {

        TextView value;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            value = itemView.findViewById(R.id.itemText);
        }

        public void bind(ItemFeed feed) {
            value.setText(feed.getValue());
        }
    }
}


