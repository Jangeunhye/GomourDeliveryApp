package com.santaistiger.gomourdeliveryapp.ui.orderdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.santaistiger.gomourdeliveryapp.R
import com.santaistiger.gomourdeliveryapp.data.model.Place
import com.santaistiger.gomourdeliveryapp.data.model.Status
import com.santaistiger.gomourdeliveryapp.databinding.FragmentOrderDetailBinding
import kotlinx.android.synthetic.main.activity_base.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

private val DANKOOKUNIV_LOCATION =
    MapPoint.mapPointWithGeoCoord(37.32224683322665, 127.12683613068711)
const val TEST_ORDER_ID = "1620486408728a1s2d3f9"

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private lateinit var viewModel: OrderDetailViewModel
    private lateinit var mapView: MapView
    val TAG = "OrderDetailFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        init(inflater, container)
        setObserver()
        getOrderDetail()

        return binding.root
    }

    /**
     * viewModel 및 binding 설정
     */
    private fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_order_detail,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(OrderDetailViewModel::class.java)
        binding.viewModel = viewModel
        binding.cvDestination.binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setToolbar()
        initKakaoMap()
    }

    private fun setObserver() {
        setOrderObserver()
        setPickupCompleteBtnObserver()
        setDeliveryCompleteBtnObserver()
        setCallBtnObserver()
        setTextBtnObserver()
    }

    private fun getOrderDetail() {
        val orderId =
            OrderDetailFragmentArgs.fromBundle(requireArguments()).orderId
        viewModel.getOrderDetail(orderId)
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.VISIBLE     // 툴바 보이도록 설정
            toolbar_title.text = "주문 조회"      // 툴바 타이틀 변경
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)  // 스와이프 활성화
        }
    }

    /**
     * 카카오 지도 MapView를 띄우고, POIITem 이벤트 리스너를 설정하고,
     * 지도의 중심점을 단국대학교로 이동
     */
    private fun initKakaoMap() {
        mapView = MapView(context).apply {
            binding.mapView.addView(this)
            setMapCenterPointAndZoomLevel(DANKOOKUNIV_LOCATION, 2, true)
        }
    }

    /**
     * 주문자에게 문자하기 버튼 처리하는 함수
     * 다이얼로그를 띄우고, 확인 버튼을 누르면 문자앱으로 이동
     */
    private fun setTextBtnObserver() {
        viewModel.isTextBtnClick.observe(viewLifecycleOwner) { clicked ->
            if (clicked) {
                AlertDialog.Builder(requireContext())
                    .setMessage("주문자에게 문자를 전송하시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        startActivity(
                            Intent(Intent.ACTION_SENDTO)
                                .setData(Uri.parse("smsto:01035575003"))
                                .putExtra("sms_body", "곰아워 배달기사입니다.")
                        )
                        viewModel.doneTextBtnClick()
                    }
                    .setNegativeButton("취소") { _, _ ->
                        viewModel.doneTextBtnClick()
                    }
                    .create()
                    .show()
            }
        }
    }

    /**
     * 주문자에게 전화하기 버튼 처리하는 함수
     * 다이얼로그를 띄우고, 확인 버튼을 누르면 문자앱으로 이동
     */
    private fun setCallBtnObserver() {
        viewModel.isCallBtnClick.observe(viewLifecycleOwner) { clicked ->
            if (clicked) {
                AlertDialog.Builder(requireContext())
                    .setMessage("주문자에게 전화를 거시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        startActivity(
                            Intent(Intent.ACTION_DIAL)
                                .setData(Uri.parse("tel:01035575003"))
                        )
                        viewModel.doneCallBtnClick()
                    }
                    .setNegativeButton("취소") { _, _ ->
                        viewModel.doneCallBtnClick()
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun setPickupCompleteBtnObserver() {
        viewModel.isPickupCompleteBtnClick.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                AlertDialog.Builder(requireContext())
                    .setMessage("픽업 완료를 처리하시겠습니까?\n가격은 다시 바꿀 수 없습니다.")
                    .setPositiveButton("확인") { _, _ ->
                        viewModel.completePickup()
                        viewModel.donePickupCompleteBtnClick()
                        binding.cvDestination.binding.btnPickupComplete.apply {
                            isClickable = false
                            text = "픽업 완료"
                        }
                    }
                    .setNegativeButton("취소") { _, _ ->
                        viewModel.donePickupCompleteBtnClick()
                    }
                    .create()
                    .show()
            }
        })
    }

    private fun setDeliveryCompleteBtnObserver() {
        viewModel.isDeliveryCompleteBtnClick.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                AlertDialog.Builder(requireContext())
                    .setMessage("배달 완료를 처리하시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        viewModel.completeDelivery()
                        viewModel.doneDeliveryCompleteBtnClick()
                        binding.cvDestination.binding.btnDeliveryComplete.apply {
                            isClickable = false
                            text = "배달 완료"
                        }
                    }
                    .setNegativeButton("취소") { _, _ ->
                        viewModel.donePickupCompleteBtnClick()
                    }
                    .create()
                    .show()
            }
        })
    }

    /**
     * 가게와 목적지에 pin을 찍는 함수
     */
    private fun setOrderObserver() {
        viewModel.order.observe(viewLifecycleOwner, { order ->
            // POI가 없으면 POI 생성
            if (mapView.poiItems.isEmpty()) {
                for (store in order?.stores!!) {
                    setPOIItem(
                        store.place,
                        MapPOIItem.MarkerType.BluePin,
                        MapPOIItem.MarkerType.RedPin
                    )
                }
                order.destination?.let {
                    setPOIItem(
                        it,
                        MapPOIItem.MarkerType.RedPin,
                        MapPOIItem.MarkerType.BluePin
                    )
                }
            }

            // status가 Status.PREPARING이 아니면 가격 설정 못하게
            Log.i(TAG, "order.status = ${order?.status}")
            if (order.status != Status.PREPARING) {
                for (child in binding.rvStore.children) {
                    Log.i(TAG, child.toString())
                }
            }
        })
    }

    private fun setPOIItem(
        place: Place,
        marker: MapPOIItem.MarkerType,
        selectedMarker: MapPOIItem.MarkerType
    ) {

        MapPOIItem().apply {
            itemName = place.placeName
            mapPoint = MapPoint.mapPointWithGeoCoord(
                place.latitude!!,
                place.longitude!!
            )
            markerType = marker
            selectedMarkerType = selectedMarker
            userObject = place
            mapView.addPOIItem(this)
        }

    }
}