package app.as_service.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import app.as_service.R
import app.as_service.adapter.`interface`.ChangeDialogListener
import app.as_service.dao.AdapterModel
import app.as_service.util.ToastUtils

class GridAdapter(mContext: Context, mTitle: TextView, mButton: AppCompatButton) : BaseAdapter() {
    private val context = mContext
    private val title = mTitle
    private val button = mButton
    private lateinit var listener: ChangeDialogListener
    private var arrayItem = ArrayList<AdapterModel.GridItem>()
    private val spinner_business_title: Array<String> = context.resources.getStringArray(R.array.spinner_business_title)
    private val spinner_business_type: Array<String> = context.resources.getStringArray(R.array.spinner_business_type)
    private val spinner_business_individual: Array<String> = context.resources.getStringArray(R.array.spinner_business_individual)
    private val spinner_business_company: Array<String> = context.resources.getStringArray(R.array.spinner_business_company)
    private val spinner_business_individual_home: Array<String> = context.resources.getStringArray(R.array.spinner_business_individual_home)
    private val spinner_business_company_indoor: Array<String> = context.resources.getStringArray(R.array.spinner_business_company_indoor)
    private val spinner_business_company_outdoor: Array<String> = context.resources.getStringArray(R.array.spinner_business_company_outdoor)
    private var businessType = ""
    var resultBusiness = ""
    private val toast = ToastUtils(mContext as Activity)

    fun setChangeDialogListener(mListener: ChangeDialogListener?) {
        mListener?.let {
            this.listener = it
        }
    }

    override fun getCount(): Int {
        return this.arrayItem.size
    }

    override fun getItem(position: Int): Any {
        return arrayItem[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.grid_item, parent, false)

        val img: ImageView = itemView.findViewById(R.id.gridItemImg)
        val text: TextView = itemView.findViewById(R.id.gridItemText)

        img.setImageDrawable(arrayItem[position].img)
        text.text = arrayItem[position].text

        itemView.setOnClickListener {
            if (title.text.toString() == spinner_business_title[0]) {
                businessType += "${arrayItem[position].text}>"
                if (text.text.toString() == spinner_business_type[0])
                    this.drawIndividualEntry()
                 else
                    this.drawCompanyEntry()
            } else if (title.text.toString() == spinner_business_title[1]) {
                if (text.text.toString() == spinner_business_individual[0]) {
                    businessType += "${arrayItem[position].text}>"
                    this.drawHomeEntry()
                } else if (text.text.toString() == spinner_business_individual[1]) {
                    singleChoice(it,position)
                } else if (text.text.toString() == spinner_business_company[0]) {
                    businessType += "${arrayItem[position].text}>"
                    this.drawIndoorEntry()
                } else {
                    businessType += "${arrayItem[position].text}>"
                    this.drawOutdoorEntry()
                }
            } else if (title.text.toString() == spinner_business_title[2]) {
                singleChoice(it,position)
            }
        }

        button.setOnClickListener {
            if (button.isEnabled) {
                listener.onChangeToName()
            }
        }

        return itemView
    }

    private fun singleChoice(view: View, position: Int) {
        if (view.isSelected) {
            view.isSelected = false
            button.isEnabled = false
        } else {
            view.isSelected = true
            button.isEnabled = true
            val lastBusinessType = businessType + arrayItem[position].text
            toast.shortMessage(lastBusinessType)
            resultBusiness = lastBusinessType
        }
    }

    // 개인, 기업
    fun drawTypeEntry() {
        businessType = ""
        arrayItem.clear()
        title.text = spinner_business_title[0]
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_type[0])
        this.addItem(
            getDrawable(R.drawable.company)
            ,spinner_business_type[1])

        this.notifyDataSetChanged()
    }

    // 실내, 실외
    private fun drawCompanyEntry() {
        arrayItem.clear()
        title.text = spinner_business_title[1]
        this.addItem(
            getDrawable(R.drawable.indoor)
            ,spinner_business_company[0])
        this.addItem(
            getDrawable(R.drawable.outdoor)
            ,spinner_business_company[1])

        this.notifyDataSetChanged()
    }

    // 자택,사무실
    private fun drawIndividualEntry() {
        arrayItem.clear()
        title.text = spinner_business_title[1]
        this.addItem(
            getDrawable(R.drawable.home)
            ,spinner_business_individual[0])
        this.addItem(
            getDrawable(R.drawable.office)
            ,spinner_business_individual[1])

        this.notifyDataSetChanged()
    }

    // 개인 -> 자택
    private fun drawHomeEntry() {
        arrayItem.clear()
        title.text = spinner_business_title[2]
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_individual_home[0])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_individual_home[1])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_individual_home[2])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_individual_home[3])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_individual_home[4])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_individual_home[5])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_individual_home[6])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_individual_home[7])

        this.notifyDataSetChanged()
    }

    // 기업 -> 실내
    private fun drawIndoorEntry() {
        arrayItem.clear()
        title.text = spinner_business_title[2]
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_indoor[0])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_indoor[1])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_indoor[2])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_indoor[3])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_indoor[4])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_indoor[5])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_indoor[6])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_indoor[7])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_indoor[8])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_indoor[9])
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_indoor[10])

        this.notifyDataSetChanged()
    }

    // 기업 -> 실내
    private fun drawOutdoorEntry() {
        arrayItem.clear()
        title.text = spinner_business_title[2]
        this.addItem(
            getDrawable(R.drawable.individual)
            ,spinner_business_company_outdoor[0])
        this.addItem(
            getDrawable(R.drawable.company)
            ,spinner_business_company_outdoor[1])
        this.addItem(
            getDrawable(R.drawable.company)
            ,spinner_business_company_outdoor[2])
        this.addItem(
            getDrawable(R.drawable.company)
            ,spinner_business_company_outdoor[3])
        this.addItem(
            getDrawable(R.drawable.company)
            ,spinner_business_company_outdoor[4])
        this.addItem(
            ResourcesCompat.getDrawable(context.resources,R.drawable.company,null)
            ,spinner_business_company_outdoor[5])
        this.addItem(
            getDrawable(R.drawable.company)
            ,spinner_business_company_outdoor[6])

        this.notifyDataSetChanged()
    }

    private fun getDrawable(id: Int) : Drawable? {
        return ResourcesCompat.getDrawable(context.resources,id,null)
    }

    private fun addItem(img: Drawable?, text: String) {
        this.arrayItem.add(AdapterModel.GridItem(img!!,text))
    }
}