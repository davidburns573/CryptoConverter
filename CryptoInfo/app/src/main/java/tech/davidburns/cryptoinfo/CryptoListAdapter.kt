package tech.davidburns.cryptoinfo

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView


class CryptoListAdapter(private val dataSet: ArrayList<Pair<String, String>>)
    : RecyclerView.Adapter<CryptoListAdapter.ViewHolder>() {
    var tracker: SelectionTracker<Long>? = null

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    init {
        setHasStableIds(true)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtAcr: TextView = view.findViewById(R.id.txt_acr)
        val txtName: TextView = view.findViewById(R.id.txt_name)
        lateinit var pair: Pair<String, String>
        fun bind(pair: Pair<String, String>, isActivated: Boolean = false) {
            this.pair = pair
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = absoluteAdapterPosition
                override fun getSelectionKey(): Long? = itemId
            }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.crypto_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.txtAcr.text = dataSet[position].first
        viewHolder.txtName.text = dataSet[position].second
        tracker?.let {
            viewHolder.bind(dataSet[position], it.isSelected(position.toLong()))
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
    override fun getItemId(position: Int): Long = position.toLong()
}

class CryptoItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as CryptoListAdapter.ViewHolder)
                .getItemDetails()
        }
        return null
    }
}
class CryptoItemKeyProvider(private val recyclerView: RecyclerView) :
    ItemKeyProvider<Long>(ItemKeyProvider.SCOPE_MAPPED) {

    override fun getKey(position: Int): Long? {
        return recyclerView.adapter?.getItemId(position)
    }

    override fun getPosition(key: Long): Int {
        val viewHolder = recyclerView.findViewHolderForItemId(key)
        return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}