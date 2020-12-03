package org.schabi.newpipe.util.stream_dialog

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.dialog_title.*
import kotlinx.android.synthetic.main.menu_stream_dialog.*
import org.schabi.newpipe.R
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class StreamDialog : DialogFragment() {

    var infoItem: StreamInfoItem? = null
    var title: String = "EMPTY"
    var additionalDetail: String? = null
    var actions: List<StreamDialogEntry>? = null

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var commands: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Do not inflate dialog on config changes (for example rotation). Simply dismiss it
        // like AlertDialog does
        savedInstanceState?.let { dismiss() }

        setStyle(STYLE_NO_TITLE, getThemeResId(requireContext()))

//        infoItem = StreamDialogEntryObject.infoItem
//        actions = StreamDialogEntryObject.actions
//        title = StreamDialogEntryObject.title
//        additionalDetail = StreamDialogEntryObject.additionalDetail
    }

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

        actions?.let { setupLayoutWithActions(it) }
    }

    private fun setupLayoutWithActions(entries: List<StreamDialogEntry>) {
        groupAdapter.addAll(entries.map { DialogStreamEntryItem(it, onItemClickListener, onArrowClickListener) })
    }

    private val onArrowClickListener: (entries: List<StreamDialogEntry>) -> Unit = { entries ->
        groupAdapter.clear()
        setupLayoutWithActions(entries)
    }

    private val onItemClickListener: (action: StreamDialogEntry) -> Unit = { action ->
        // Pass the parent fragment instead of this dialog due to dismiss()
        action.action.onClick(parentFragment, infoItem)
        dismiss()
    }

    private fun getThemeResId(context: Context, themeId: Int = 0): Int {
        var themeIdX = themeId
        if (themeId == 0) {
            // If the provided theme is 0, then retrieve the dialogTheme from our theme
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.alertDialogTheme, outValue, true)
            themeIdX = outValue.resourceId
        }
        return themeIdX
    }

    class Builder(val streamItem: StreamInfoItem) {
        private var mTitle = "STREAM_TITLE"
        private var mDetails: String? = null
        private val mActions: ArrayList<StreamDialogEntry> = arrayListOf()

        fun setTitle(title: String) = apply { this.mTitle = title }
        fun setDetails(details: String) = apply { this.mDetails = details }

        fun addAction(action: StreamDialogEntry) = apply { mActions.add(action) }
        fun addGroup(resId: Int, actions: List<StreamDialogEntry>) = apply {
            this.mActions.add(
                StreamDialogEntry.groupEntry.apply {
                    resource = resId
                    subEntries = actions
                }
            )
        }
        fun setActions(actions: List<StreamDialogEntry>) = apply {
            this.mActions.clear()
            this.mActions.addAll(actions)
        }

        fun build(): StreamDialog {
            return StreamDialog().apply {
                infoItem = streamItem
                title = mTitle
                additionalDetail = mDetails
                actions = mActions
            }
        }
    }
}
