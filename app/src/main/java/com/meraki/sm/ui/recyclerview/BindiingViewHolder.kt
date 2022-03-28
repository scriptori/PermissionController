package com.meraki.sm.ui.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * ViewHolder that uses a ViewBinding to track its Views.
 */
open class BindingViewHolder<B : ViewBinding>(val binding: B) :
    RecyclerView.ViewHolder(binding.root), ViewHolderLifecycleListener

/**
 * Denotes an object that can be notified throughout a ViewHolder's lifecycle.
 */
interface ViewHolderLifecycleListener {
    /**
     * This default implementation returns false.
     *
     * @see [RecyclerView.Adapter.onViewRecycled]
     */
    fun onFailedToRecycleView() = false

    /**
     * This default implementation does nothing.
     *
     * @see [RecyclerView.Adapter.onViewRecycled]
     */
    fun onViewAttachedToWindow() {}

    /**
     * This default implementation does nothing.
     *
     * @see [RecyclerView.Adapter.onViewRecycled]
     */
    fun onViewDetachedFromWindow() {}

    /**
     * This default implementation does nothing.
     *
     * @see [RecyclerView.Adapter.onViewRecycled]
     */
    fun onViewRecycled() {}
}
