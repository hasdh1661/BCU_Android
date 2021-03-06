package com.mandarin.bcu.androidutil.unit.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mandarin.bcu.R
import com.mandarin.bcu.androidutil.StaticStore
import common.system.MultiLangCont
import common.util.pack.Pack
import java.util.*

class DynamicExplanation : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        val view = inflater.inflate(R.layout.unit_info_tab, container, false)
        val pid = arguments!!.getInt("PID", 0)
        val `val` = arguments!!.getInt("Number", 0)
        val id = arguments!!.getInt("ID", 0)

        val p = Pack.map[pid] ?: return view

        if(id >= p.us.ulist.size())
            return view

        val u = p.us.ulist[id]

        var explanation = MultiLangCont.FEXP.getCont(u.forms[`val`])
        if (explanation == null) {
            explanation = arrayOf<String?>("", "", "")
        }
        val unitname = view.findViewById<TextView>(R.id.unitexname)
        val explains = arrayOfNulls<TextView>(3)
        val lineid = intArrayOf(R.id.unitex0, R.id.unitex1, R.id.unitex2)
        for (i in lineid.indices) explains[i] = view.findViewById(lineid[i])
        explains[2]!!.setPadding(0, 0, 0, StaticStore.dptopx(24f, Objects.requireNonNull(activity)))
        var name = MultiLangCont.FNAME.getCont(u.forms[`val`]) ?: u.forms[`val`].name
        if (name == null) name = ""
        unitname.text = name
        for (i in explains.indices) {
            if (i >= explanation.size) {
                explains[i]!!.text = ""
            } else {
                if (explanation[i] != null) explains[i]!!.text = explanation[i]
            }
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(`val`: Int, id: Int, pid: Int, titles: Array<String?>?): DynamicExplanation {
            val explanation = DynamicExplanation()
            val bundle = Bundle()
            bundle.putInt("Number", `val`)
            bundle.putInt("ID", id)
            bundle.putInt("PID",pid)
            bundle.putStringArray("Title", titles)
            explanation.arguments = bundle
            return explanation
        }
    }
}