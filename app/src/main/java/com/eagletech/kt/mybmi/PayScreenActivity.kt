package com.eagletech.kt.mybmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.amazon.device.drm.LicensingService
import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.FulfillmentResult
import com.amazon.device.iap.model.ProductDataResponse
import com.amazon.device.iap.model.PurchaseResponse
import com.amazon.device.iap.model.PurchaseUpdatesResponse
import com.amazon.device.iap.model.UserDataResponse
import com.eagletech.kt.mybmi.data.MyDataApp
import com.eagletech.kt.mybmi.databinding.ActivityPayScreenBinding

class PayScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayScreenBinding
    private lateinit var myDataApp: MyDataApp
    private lateinit var currentUserId: String
    private lateinit var currentMarketplace: String

    // Phải thêm sku các gói vào ứng dụng
    companion object {
        const val bmi5 = "com.eagletech.kt.mybmi.buy5use"
        const val bmi10 = "com.eagletech.kt.mybmi.buy10use"
        const val bmi15 = "com.eagletech.kt.mybmi.buy15use"
        const val register = "com.eagletech.kt.mybmi.registerlongtime"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myDataApp = MyDataApp.getInstance(this)
        setupIAPOnCreate()
        setClickItems()

    }

    private fun setClickItems() {
        binding.btnBuy5.setOnClickListener {
//            myDataApp.addBMI(2)
            PurchasingService.purchase(bmi5)
        }
        binding.btnBuy10.setOnClickListener {
            PurchasingService.purchase(bmi10)
        }
        binding.btnBuy15.setOnClickListener {
            PurchasingService.purchase(bmi15)
        }
        binding.btnRegister.setOnClickListener {
            PurchasingService.purchase(register)
//            mySharedPreferences.isPremium = true
        }
        binding.btnExit.setOnClickListener { finish() }
    }

    private fun setupIAPOnCreate() {
        val purchasingListener: PurchasingListener = object : PurchasingListener {
            override fun onUserDataResponse(response: UserDataResponse) {
                when (response.requestStatus!!) {
                    UserDataResponse.RequestStatus.SUCCESSFUL -> {
                        currentUserId = response.userData.userId
                        currentMarketplace = response.userData.marketplace
                        myDataApp.userId(currentUserId)
                    }

                    UserDataResponse.RequestStatus.FAILED, UserDataResponse.RequestStatus.NOT_SUPPORTED ->
                        Log.v("IAP SDK", "loading failed")
                }
            }

            override fun onProductDataResponse(productDataResponse: ProductDataResponse) {
                when (productDataResponse.requestStatus) {
                    ProductDataResponse.RequestStatus.SUCCESSFUL -> {
                        val products = productDataResponse.productData
                        for (key in products.keys) {
                            val product = products[key]
                            Log.v(
                                "Product:", String.format(
                                    "Product: %s\n Type: %s\n SKU: %s\n Price: %s\n Description: %s\n",
                                    product!!.title,
                                    product.productType,
                                    product.sku,
                                    product.price,
                                    product.description
                                )
                            )
                        }
                        //get all unavailable SKUs
                        for (s in productDataResponse.unavailableSkus) {
                            Log.v("Unavailable SKU:$s", "Unavailable SKU:$s")
                        }
                    }

                    ProductDataResponse.RequestStatus.FAILED -> Log.v("FAILED", "FAILED")
                    else -> {}
                }
            }

            override fun onPurchaseResponse(purchaseResponse: PurchaseResponse) {
                when (purchaseResponse.requestStatus) {
                    PurchaseResponse.RequestStatus.SUCCESSFUL -> {

                        if (purchaseResponse.receipt.sku == bmi5) {
                            myDataApp.addBMI(5)
                        }
                        if (purchaseResponse.receipt.sku == bmi10) {
                            myDataApp.addBMI(11)
                        }
                        if (purchaseResponse.receipt.sku == bmi15) {
                            myDataApp.addBMI(15)
                        }

                        PurchasingService.notifyFulfillment(
                            purchaseResponse.receipt.receiptId,
                            FulfillmentResult.FULFILLED
                        )

                        myDataApp.isPremium = !purchaseResponse.receipt.isCanceled
                        Log.v("FAILED", "FAILED")
                    }

                    PurchaseResponse.RequestStatus.FAILED -> {}
                    else -> {}
                }
            }

            override fun onPurchaseUpdatesResponse(response: PurchaseUpdatesResponse) {
                // Process receipts
                when (response.requestStatus) {
                    PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL -> {
                        for (receipt in response.receipts) {
                            myDataApp.isPremium = !receipt.isCanceled
                        }
                        if (response.hasMore()) {
                            PurchasingService.getPurchaseUpdates(false)
                        }

                    }

                    PurchaseUpdatesResponse.RequestStatus.FAILED -> Log.d("FAILED", "FAILED")
                    else -> {}
                }
            }
        }
        PurchasingService.registerListener(this, purchasingListener)
        Log.d(
            "DetailBuyAct",
            "Appstore SDK Mode: " + LicensingService.getAppstoreSDKMode()
        )
    }


    override fun onResume() {
        super.onResume()
        PurchasingService.getUserData()
        val productSkus: MutableSet<String> = HashSet()
        productSkus.add(register)
        productSkus.add(bmi5)
        productSkus.add(bmi10)
        productSkus.add(bmi15)
        PurchasingService.getProductData(productSkus)
        PurchasingService.getPurchaseUpdates(false)
    }
}