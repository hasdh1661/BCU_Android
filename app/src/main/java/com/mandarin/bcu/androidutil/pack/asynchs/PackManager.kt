package com.mandarin.bcu.androidutil.pack.asynchs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.view.View
import android.view.Window
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mandarin.bcu.PackManagement
import com.mandarin.bcu.R
import com.mandarin.bcu.androidutil.Definer
import com.mandarin.bcu.androidutil.StaticStore
import com.mandarin.bcu.androidutil.adapters.SingleClick
import com.mandarin.bcu.androidutil.pack.adapters.PackManagementAdapter
import common.pack.PackData
import common.pack.UserProfile
import java.lang.ref.WeakReference

class PackManager(ac: Activity) : AsyncTask<Void, String, Void>() {
    private val w = WeakReference(ac)

    private val done = "done"

    override fun onPreExecute() {
        val ac = w.get() ?: return

        val swipe = ac.findViewById<SwipeRefreshLayout>(R.id.pmanrefresh)
        val more = ac.findViewById<FloatingActionButton>(R.id.pmanoption)

        swipe.visibility = View.GONE
        more.visibility = View.GONE
    }

    override fun doInBackground(vararg params: Void?): Void? {
        val ac = w.get() ?: return null

        Definer.define(ac, this::updateProg, this::updateText)

        publishProgress(done)

        return null
    }

    override fun onProgressUpdate(vararg values: String) {
        val ac = w.get() ?: return

        val st = ac.findViewById<TextView>(R.id.status)

        when(values[0]) {
            StaticStore.TEXT -> st.text = values[1]
            StaticStore.PROG -> {
                val prog = ac.findViewById<ProgressBar>(R.id.prog)

                if (values[1].toInt() == -1) {
                    prog.isIndeterminate = true

                    return
                }

                prog.isIndeterminate = false
                prog.max = 10000
                prog.progress = values[1].toInt()
            }
            done -> {
                val prog = ac.findViewById<ProgressBar>(R.id.prog)
                val list = ac.findViewById<ListView>(R.id.pmanlist)
                val import = ac.findViewById<FloatingActionButton>(R.id.pmanoption)

                import.setOnClickListener(object : SingleClick() {
                    override fun onSingleClick(v: View?) {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.type = "*/*"

                        ac.startActivityForResult(Intent.createChooser(intent, "Choose Directory"), 100)
                    }
                })

                val packList = ArrayList<PackData.UserPack>()

                for(pack in UserProfile.getUserPacks()) {
                    packList.add(pack)
                }

                list.adapter = PackManagementAdapter(ac, packList)

                prog.isIndeterminate = true
            }
        }
    }

    override fun onPostExecute(result: Void?) {
        val ac = w.get() ?: return

        val st = ac.findViewById<TextView>(R.id.status)
        val prog = ac.findViewById<ProgressBar>(R.id.prog)
        val list = ac.findViewById<SwipeRefreshLayout>(R.id.pmanrefresh)
        val more = ac.findViewById<FloatingActionButton>(R.id.pmanoption)

        st.visibility = View.GONE
        prog.visibility = View.GONE
        list.visibility = View.VISIBLE
        more.visibility = View.VISIBLE
    }

    private fun updateText(info: String) {
        val ac = w.get() ?: return

        publishProgress(StaticStore.TEXT, StaticStore.getLoadingText(ac, info))
    }

    private fun updateProg(p: Double) {
        publishProgress(StaticStore.PROG, (p * 10000.0).toInt().toString())
    }
}