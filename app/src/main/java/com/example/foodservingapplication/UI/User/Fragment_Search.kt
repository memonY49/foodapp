package com.example.foodservingapplication.UI.User

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.foodservingapplication.Models.User
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.UserListAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*


class Fragment_Search : Fragment(){

    val TAG = "Fragment_Search"

    //widegts
    lateinit var mSearchParam:EditText
    lateinit var mListView:ListView

    //vars

    lateinit var usersList:ArrayList<User>

    //firebase
    lateinit var mDb:FirebaseFirestore



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        Toast.makeText(context!!, "The Search activity", Toast.LENGTH_SHORT).show()
        mDb = FirebaseFirestore.getInstance()
        mSearchParam = view.findViewById(R.id.editText_search)
        mListView = view.findViewById(R.id.listView_activity_search)
        CloseKeyBoard()
        initTextListerner()

        return view
    }


    private fun initTextListerner() {
       logs("nitTextListerner: The initing text listerner")
        usersList = java.util.ArrayList<User>()
        mSearchParam.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                val text: String = mSearchParam.getText().toString().toLowerCase(Locale.getDefault())
                SearchForTheKeyword(text)
            }
        })
    }






    private fun SearchForTheKeyword(Keyword: String) {
        logs("SearchForTheKeyword: searching for the matchhing keyword $Keyword")
        usersList.clear()
        //update the users list
        if (Keyword.length == 0) {
            logs("SearchForTheKeyword: The lenght of the keyword is 0")
        } else {
            mDb.collection(getString(R.string.users_collection))
                .orderBy(getString(R.string.field_username))
                .startAt(Keyword).endAt(Keyword + "\uf8ff")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                         if (document.get(getString(R.string.type_of_user))!!.equals(getString(R.string.donor))){
                             usersList.add(document.toObject<User>())
                             updateUserList()
                         }else{
                             logs("SORRRYY THE ITEM WAS VOLUNTEER")

                         }



                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    toast("Can't get the data  Check your Internet Connection ")
                }
        }
    }

    fun updateUserList(){
        val adapter = UserListAdapter(activity!!.applicationContext, R.layout.ticket_search_userlist, usersList)
        mListView.setAdapter(adapter)
        mListView.setOnItemClickListener(OnItemClickListener { adapterView, view, i, l ->
            toast("CLICKED ITEM "+usersList.get(i).toString())
            val fragment: Fragment = Fragment_View_profile()
            val args = Bundle()
            args.putString(getString(R.string.intent_user_id_search), usersList.get(i).userid)
            fragment.setArguments(args)
            val fragmentManager: FragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction: FragmentTransaction =
                fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_main, fragment)
            fragmentTransaction.addToBackStack(getString(R.string.fragment_search))
            fragmentTransaction.commit()

        })


    }




    fun CloseKeyBoard(){
        try {
            var view:View = activity!!.currentFocus!!
            var imm:InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken,0)
        } catch (e: Exception) {
            logs("Exception Occcures"+e.printStackTrace())
        }

    }


        fun logs(string: String){
        Log.d(TAG,string)

    }
    fun toast(string: String){
        Toast.makeText(activity,string,Toast.LENGTH_SHORT).show()
    }


}

