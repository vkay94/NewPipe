package org.schabi.newpipe.util.stream_dialog

import android.view.View
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.menu_item_title_arrow.view.*
import org.schabi.newpipe.R

class DialogStreamEntryItem(
    val entry: StreamDialogEntry,
    private val onClickAction: (action: StreamDialogEntry) -> Unit,

    // If it's a item with sub-entries then use this entry and change the items list
    // accordingly to its sub entries
    private val onArrowClick: (entries: List<StreamDialogEntry>) -> Unit
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        with(viewHolder.root) {
            if (entry.hasSubEntries()) {
                arrow.visibility = View.VISIBLE
                setOnClickListener { onArrowClick.invoke(entry.subActions) }
            } else {
                setOnClickListener { onClickAction.invoke(entry) }
            }
            title.setText(entry.resource)
        }
    }

    override fun getLayout(): Int = R.layout.menu_item_title_arrow
}
