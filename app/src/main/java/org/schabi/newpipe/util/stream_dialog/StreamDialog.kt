package org.schabi.newpipe.util.stream_dialog

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.dialog_title.*
import kotlinx.android.synthetic.main.menu_stream_dialog.*
import org.schabi.newpipe.R
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class StreamDialog(
    private val infoItem: StreamInfoItem,
    private val activity: Activity,
    private val actions: List<StreamDialogEntry>,
    private val title: String,
    private val additionalDetail: String? = null
) : DialogFragment() {

    private lateinit var dialog: AlertDialog
    private lateinit var itemsList: RecyclerView
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var commands: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.menu_stream_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commands = StreamDialogEntry.getCommands(activity)

        itemTitleView.text = title
        itemTitleView.isSelected = true

        if (additionalDetail == null) {
            itemAdditionalDetails.visibility = View.GONE
        } else {
            itemAdditionalDetails.text = additionalDetail
            itemAdditionalDetails.visibility = View.VISIBLE
        }

        additionalDetail?.let {
            itemAdditionalDetails.text = it
            itemAdditionalDetails.visibility = View.VISIBLE
        }

        items_list.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        items_list.adapter = groupAdapter

        val entry = StreamDialogEntry.subEntry.apply {
            resource = R.string.tracks
            subEntries = listOf(StreamDialogEntry.delete, StreamDialogEntry.share)
        }

        groupAdapter.add(
            DialogStreamEntryItem(
                entry,
                onItemClickListener,
                onArrowClickListener
            )
        )
        setupLayoutWithActions(actions)
    }

    private fun setupLayoutWithActions(entries: List<StreamDialogEntry>) {
        groupAdapter.addAll(entries.map { DialogStreamEntryItem(it, onItemClickListener, onArrowClickListener) })
    }

    private val onArrowClickListener: (entries: List<StreamDialogEntry>) -> Unit = { entries ->
        groupAdapter.clear()
        setupLayoutWithActions(entries)
    }

    private val onItemClickListener: (action: StreamDialogEntry) -> Unit = { action ->
        StreamDialogEntry.clickOn(action.ordinal, this, infoItem)
        dismiss()
    }
}
