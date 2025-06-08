package bcf.tfc.labstocker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bcf.tfc.labstocker.R;

/**
 * Basic adapter for a list of SimpleItem
 * It's used to show a list of items with a single text and a custom listener.
 *
 * @see SimpleItem
 * @author Beatriz Calzo
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(SimpleItem item);
    }

    private List<SimpleItem> items;
    private OnItemClickListener listener;

    public SimpleAdapter(List<SimpleItem> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SimpleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_simple, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SimpleAdapter.ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView label;

        public ViewHolder(@NonNull View view) {
            super(view);
            label = view.findViewById(R.id.simple_item_label);
        }

        public void bind(SimpleItem item) {
            if (item == null) return;
            label.setText(item.getLabel());
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }


}