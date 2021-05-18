package com.santaistiger.gomourdeliveryapp.ui.loading

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.R
import kotlinx.android.synthetic.main.activity_base.*


class LoadingFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()
        startLoading()
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.GONE  // 툴바 숨기기
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)   // 스와이프 비활성화
        }
    }

    //2초 딜레이
    private fun startLoading() {
        val handler = Handler()
        handler.postDelayed(Runnable {
            auth()
        }, 2000)
    }

    private fun auth() {
        var auth = Firebase.auth

        // shared preference에 로그인 정보 있는지 확인
        val auto = this.requireActivity().getSharedPreferences("auto", Context.MODE_PRIVATE)
        val loginEmail = auto.getString("email", null)
        val loginPwd = auto.getString("password", null)
        if (loginEmail != null && loginPwd != null) {
            auth = Firebase.auth
            auth?.signInWithEmailAndPassword(loginEmail, loginPwd)?.addOnSuccessListener {
                findNavController().navigate(R.id.action_loadingFragment_to_orderListFragment)
                Toast.makeText(context, "안녕", Toast.LENGTH_LONG).show()
            }
        }
        else {
            // 로그인 페이지로 이동
            findNavController().navigate(R.id.action_loadingFragment_to_loginFragment)
        }

    }

}