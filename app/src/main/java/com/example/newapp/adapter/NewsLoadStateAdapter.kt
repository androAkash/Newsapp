package com.example.newapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newapp.databinding.LoadStateFooterBinding

class NewsLoadStateAdapter: LoadStateAdapter<NewsLoadStateAdapter.LoaderViewHolder>() {

    class LoaderViewHolder(binding: LoadStateFooterBinding): RecyclerView.ViewHolder(binding.root){

        private val progressBar = binding.progressBar

        fun bind(loadState: LoadState){
            progressBar.isVisible = loadState is LoadState.Loading
        }
    }

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoaderViewHolder {
        val binding = LoadStateFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoaderViewHolder(binding)
    }
}