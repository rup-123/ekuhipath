package com.e_kuhipath.android.fragments

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.adapters.DownloadedVideosAdapter
import com.e_kuhipath.android.models.DownloadedFile
import com.e_kuhipath.android.services.DownloadedFilesDatabase
import kotlinx.android.synthetic.main.fragment_downloads.*
import kotlinx.android.synthetic.main.fragment_paid_courses.*

class DownloadsFragment : Fragment() {

    var downloadedFiles:List<DownloadedFile> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val database = DownloadedFilesDatabase(requireActivity())
        Log.d("dd","DownloadsFragment!!")

        downloadedFiles = database.getAllDownloadedFiles()
        return inflater.inflate(R.layout.fragment_downloads, container, false)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        if (downloadedFiles.isEmpty()){
            downloads_recycler_view.visibility = View.GONE
            no_downloads_ll.visibility = View.VISIBLE
            Log.d("dd","Empty!!")
        }
        else{
            Log.d("dd","Non Empty!!")
            downloads_recycler_view.visibility = View.VISIBLE
            no_downloads_ll.visibility = View.GONE
            downloads_recycler_view.layoutManager = LinearLayoutManager(activity)
            val adapter = DownloadedVideosAdapter(requireActivity(), downloadedFiles)
            downloads_recycler_view.adapter = adapter
        }

    }


}