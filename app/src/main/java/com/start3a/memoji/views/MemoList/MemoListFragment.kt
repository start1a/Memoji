package com.start3a.memoji.views.MemoList


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.start3a.memoji.R
import com.start3a.memoji.viewmodel.MemoListViewModel
import com.start3a.memoji.views.EditMemo.EditMemoActivity
import kotlinx.android.synthetic.main.fragment_memo_list.*

class MemoListFragment : Fragment() {

    private lateinit var listAdapterLinear: MemoListAdapter
    private lateinit var listAdapterGrid: MemoListAdapter
    private var viewModel: MemoListViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_memo_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity!!.application!!.let {
            ViewModelProvider(
                activity!!.viewModelStore,
                ViewModelProvider.AndroidViewModelFactory(it)
            )
                .get(MemoListViewModel::class.java)
        }

        viewModel!!.let { VM ->
            initAdapter()
            VM.layout_memoList.observe(this, Observer {
                notifyAdapter(it)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        getCurAdapter().notifyDataSetChanged()
    }

    private fun initAdapter() {
        viewModel!!.let { VM ->
            VM.memoListLiveData.value?.let {
                listAdapterLinear =
                    MemoListAdapter(
                        it,
                        R.layout.item_memo
                    )
                listAdapterGrid =
                    MemoListAdapter(
                        it,
                        R.layout.item_memo_card
                    )
            }
            listAdapterLinear.itemClickListener = {
                setItemClickListener(it)
            }
            listAdapterGrid.itemClickListener = {
                setItemClickListener(it)
            }
        }
    }

    private fun getCurAdapter(): MemoListAdapter {
        viewModel!!.let { vm ->
            return when (vm.layout_memoList.value) {
                vm.LAYOUT_LINEAR -> listAdapterLinear
                else -> listAdapterGrid
            }
        }
    }

    private fun notifyAdapter(type: Int) {
        viewModel!!.let { VM ->
            when (type) {
                VM.LAYOUT_LINEAR -> {
                    memoListView.layoutManager =
                        LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                    memoListView.adapter = listAdapterLinear
                }
                VM.LAYOUT_GRID -> {
                    memoListView.layoutManager =
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    memoListView.adapter = listAdapterGrid
                }
            }
        }
    }

    private fun setItemClickListener(id: String) {
        val intent = Intent(activity, EditMemoActivity::class.java)
        intent.putExtra("memoId", id)
        startActivity(intent)
    }
}