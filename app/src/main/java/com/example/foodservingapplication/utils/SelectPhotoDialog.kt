package com.example.foodservingapplication.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.foodservingapplication.R
import kotlinx.android.synthetic.main.dialog_box.view.*

class SelectPhotoDialog : AppCompatDialogFragment() {

    val PICKFILE_REQUEST_CODE:Int=1234
    val CAMERA_REQUEST_CODE:Int=4321

    interface  OnPhotoSelectedListerners{
        fun getImagePath(imagePath: Uri?)
        fun getImageBitmap (bitmap: Bitmap)
    }
    var photoselected:OnPhotoSelectedListerners?=null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog: AlertDialog.Builder = AlertDialog.Builder(context!!)
        val inflater: LayoutInflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_box,null)
        dialog.setView(view)
        view.memory_select.setOnClickListener {
            Log.d("memory_select","selecting photo from memoryy")
            val intent: Intent =  Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent,PICKFILE_REQUEST_CODE)

        }
        view.camera_select.setOnClickListener {
            Log.d("camera_select","selecting photo from cameraa ")
            val intent: Intent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,CAMERA_REQUEST_CODE)
        }


        return dialog.create()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*
        Results for selecting new iamge from memory
         */
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            //to get the uri
            val selectedimageUri: Uri? = data!!.data
            Log.d("onActivityResult","selecting photo from memory"+selectedimageUri)
            photoselected!!.getImagePath(selectedimageUri)
            dialog!!.dismiss()
        }else if(requestCode==CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.d("onActivityResult","selecting photo from cameraa------------")
            val bitmap: Bitmap
            bitmap = data!!.extras!!.get("data") as Bitmap
            photoselected!!.getImageBitmap(bitmap)
            dialog!!.dismiss()
        }
    }

    override fun onAttach(activity: Activity) {

        try {
            photoselected = getActivity() as OnPhotoSelectedListerners

        }   catch (ex:Exception)   {
            Log.d("The TAG",ex.message!!)

        }
        super.onAttach(activity)

    }


    override fun onAttach(context: Context){
        try {
            photoselected = targetFragment as OnPhotoSelectedListerners
        }catch (ex: java.lang.Exception){


        }
        super.onAttach(context)
    }
}
