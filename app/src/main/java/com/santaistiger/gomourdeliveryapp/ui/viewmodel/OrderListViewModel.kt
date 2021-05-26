// 배달원 - mvvm 적용 orderlist view

package com.santaistiger.gomourdeliveryapp.ui.viewmodel

/**
 * Created by Jieun Park.
 */

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl

class OrderListViewModel : ViewModel() {
    private val TAG = "OrderListViewModel"
    private val repository: Repository = RepositoryImpl

    // 사용자의 주문 내역이 realtime database에 존재하는지 여부 표시
    private val _isOrderExist = MutableLiveData<Boolean>()
    val isOrderExist: LiveData<Boolean>
        get() = _isOrderExist

    val orders = mutableListOf<Order>()

    /**
     * realtime database에서 현재 사용자의 주문 목록을 받아와 최근 날짜 순으로 어댑터의 orderList에 넣어준다.
     * 만약 주문 내역이 없을 경우 주문 내역이 없다는 안내 문구가 보이도록 설정한다.
     */
    fun getOrderList(customerUid: String) {

        // realtime database에서 주문 목록 받아오기
        val orderList = repository.readOrderList(customerUid)

        val orderListListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                orders.clear()

                if (dataSnapshot.exists()) {
                    _isOrderExist.value = true
                    for (messageSnapshot in dataSnapshot.children) {
                        val order: Order? = messageSnapshot.getValue(Order::class.java)
                        if (order != null) {
                            orders.add(order)
                        }
                    }
                } else {
                    _isOrderExist.value = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        orderList.addValueEventListener(orderListListener)
    }
}