package com.airapssinsj.happyplaceapp.utils

import android.content.Context //앱의 현재 상태 정보를 제공하는 클래스, Geocoder 초기화 시 필요
import android.location.Address //주소 정보를 나타내는 객체
import android.location.Geocoder //위도와 경도를 주소로 변환하거나, 반대로 주소를 위도/경도로 변환할 수 있는 유틸리티 클래스
import android.os.AsyncTask //비동기적으로 작업을 처리하기 위한 클래스
import java.util.Locale //기기의 언어 및 지역 설정 정보를 나타내는 클래스

// 같은 스레드에서 동시에 실행되지않게 하기 위해 AsyncTask 상속받음
class GetAddressFromLatLog(context: Context,
                           private val latitude:Double,
                           private val longitude:Double)
    : AsyncTask<Void,String,String>(){

    //위도와 경도로 주소 가져오는 부분
    //1. GeoCoder 설정 : 경도와 위도를 사용해 주소로 생성해 주는 클래스
    // Geocoder(Context, 언어설정) / Locale.getDefault() : 기기의 디폴트 언어 사용
    private val geocoder : Geocoder = Geocoder(context, Locale.getDefault())
    private lateinit var mAddressListener: AddressListener // 주소를 가져온 후 결과를 호출하는 데 사용

    //백그라운드에서 실행
    override fun doInBackground(vararg params: Void?): String {
        
        //Geocoder는 네트워크 연결 문제나 API 호출 실패 시 예외를 던질 수 있기 때문에 예외처리
        try {
            //getFromLocation(위도 , 경도, 주소 결과의 최대 개수) 
            val addressList:List<Address>? = geocoder.getFromLocation(latitude,longitude, 1)
            //-> 세번째 매개변수를 1로 둠 : 결과값의  첫 번째 주소만 가져오겠단 얘기

            //그 중 첫번째 값을 Address 변수에 저장
            if (addressList != null && addressList.isNotEmpty()) {
                val address : Address = addressList[0]
                //리턴 값에 맞춰 String으로 처리
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    //현재 식별된 주소 정보를 사용하기 위해 가장 큰 인덱스 값을 생성해
                    // 주소가 여러줄이거나 복잡한 정보여도 값을 얻어내기 위함
                    // address객체가 가진 변수 append하기
                    sb.append(address.getAddressLine(i)).append(" ")
                }
                sb.deleteCharAt(sb.length-1) //마지막 공백 지우기
                return sb.toString()
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
        return ""
    }

    //onPostExecute : 비동기 작업이 끝난 후 메인(UI) 스레드에서 호출
    //비동기 작업 결과(resultString)를 인터페이스를 통해 전달
    override fun onPostExecute(resultString: String?) {
        super.onPostExecute(resultString)

        if (resultString == null) {
            //주소 가져오지 못한 경우 처리
            mAddressListener.inError()
        } else {
            // 정상적으로 주소를 가져온 경우 인터페이스를 통해 전달
            mAddressListener.onAddressFound(resultString)
        }

    }

    //결과를 전달받을 수 있도록 인터페이스 준비 시키는 초기화 담당
    fun setAddressListener(addressListener: AddressListener) {
        mAddressListener = addressListener
    }

    //주소를 가져오는 기능을 가진 함수
    //AsyncTask를 실행하기 위한 함수 : 이 함수가 호출되면 비동기 작업이 시작
    fun getAddress() {
        execute()
    }

    //실제 기능들을 적용하기 전에 인터페이스를 생성하고 AddressListener라는 인터페이스 사용
    //주소 조회 결과를 전달하기 위한 인터페이스
    interface AddressListener{
        fun onAddressFound(address:String?) //주소가 성공적으로 조회되었을 때 호출
        fun inError() //조회 실패 시 호출
    }

}