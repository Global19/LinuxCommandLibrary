package com.inspiredandroid.linuxcommandbibliotheca.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.inspiredandroid.linuxcommandbibliotheca.BuildConfig
import com.inspiredandroid.linuxcommandbibliotheca.R
import com.inspiredandroid.linuxcommandbibliotheca.adapter.BasicChildrenAdapter.ItemViewHolder
import com.inspiredandroid.linuxcommandbibliotheca.misc.Utils
import com.inspiredandroid.linuxcommandbibliotheca.models.CommandChildModel
import com.inspiredandroid.linuxcommandbibliotheca.models.CommandGroupModel
import com.inspiredandroid.linuxcommandbibliotheca.view.TerminalTextView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.row_scriptchild.view.*
import java.util.*

/**
 * Created by simon on 23/01/17.
 */
class BasicChildrenAdapter(data: OrderedRealmCollection<CommandGroupModel>?, autoUpdate: Boolean, private val mFirebaseAnalytics: FirebaseAnalytics) : RealmRecyclerViewAdapter<CommandGroupModel, ItemViewHolder>(data, autoUpdate) {

    private val expanded: HashMap<Int, Boolean> = HashMap()
    private var searchQuery = ""

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ItemViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_scriptchild, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ItemViewHolder, position: Int) {
        val item = data!![position]
        viewHolder.bind(item)
    }

    private fun trackSelectContent(id: String?) {
        if (BuildConfig.DEBUG) {
            return
        }
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Basic Group")
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    private fun isExpanded(position: Int): Boolean {
        return expanded.containsKey(position) && expanded[position]!!
    }

    fun setSearchQuery(query: String) {
        searchQuery = query
    }

    /**
     * let user share the command with any compatible app
     *
     * @param command
     */
    private fun startShareActivity(context: Context, command: CommandChildModel) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, command.command)

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: CommandGroupModel) {
            itemView.title.text = Utils.highlightQueryInsideText(itemView.context, searchQuery, item.desc!!)
            itemView.icon.setImageResource(item.imageResourceId)
            itemView.commands.removeAllViews()
            for (command in item.commands!!) {
                val v = LayoutInflater.from(itemView.context).inflate(R.layout.row_scriptchild_child, itemView.commands, false)

                val tv = v.findViewById<View>(R.id.row_scriptdescription_child_tv_description) as TerminalTextView
                tv.text = command.command
                tv.setCommands(command.getMansAsStringArray())

                val btn = v.findViewById<View>(R.id.row_scriptdescription_child_iv_share) as ImageButton
                btn.setOnClickListener { view -> startShareActivity(view.context, command) }

                itemView.commands.addView(v)
            }
            itemView.commands.visibility = if (isExpanded(adapterPosition)) View.VISIBLE else View.GONE
            itemView.setOnClickListener { _ ->
                expanded[adapterPosition] = !isExpanded(adapterPosition)
                notifyItemChanged(adapterPosition)
                trackSelectContent(item.desc)
            }
        }
    }
}
