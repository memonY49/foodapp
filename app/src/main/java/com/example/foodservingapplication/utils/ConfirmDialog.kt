package com.example.foodservingapplication.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.foodservingapplication.R
import kotlinx.android.synthetic.main.dialog_box_confirm.view.*

public class ConfirmDialog : AppCompatDialogFragment() {
    interface  OnConfirmDialogListerner{
        fun getPasswordFromDialog(password:String)

    }

    lateinit var password: String


    var onConfirmDialogListerner:OnConfirmDialogListerner?=null



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog: AlertDialog.Builder = AlertDialog.Builder(context!!)
        val inflater: LayoutInflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_box_confirm,null)
         dialog.setView(view)

       view.confirm_button_password.setOnClickListener({
           password = view.password_confirm_dialog.text.toString()
            onConfirmDialogListerner!!.getPasswordFromDialog(password)
            getDialog()!!.dismiss()

        })




        return dialog.create()


    }
    override fun onAttach(activity: Activity) {

        try {
            onConfirmDialogListerner = getActivity() as OnConfirmDialogListerner

        }   catch (ex:Exception)   {
            Log.d("The TAG",ex.message!!)

        }
        super.onAttach(activity)

    }

    override fun onAttach(context: Context){
        try {
            onConfirmDialogListerner = targetFragment as OnConfirmDialogListerner
        }catch (ex: java.lang.Exception){


        }
        super.onAttach(context)
    }




}

