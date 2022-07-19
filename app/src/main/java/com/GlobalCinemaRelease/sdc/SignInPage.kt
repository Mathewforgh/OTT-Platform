package com.GlobalCinemaRelease.sdc

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.GlobalCinemaRelease.sdc.databinding.ActivitySignInPageBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.SendOtp
import com.GlobalCinemaRelease.sdc.response.SetLogInUserDetailsDC
import com.GlobalCinemaRelease.sdc.response.SocialLogin
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger


const val RC_SIGN_IN = 123

@Suppress("DEPRECATION")
class SignInPage : AppCompatActivity() {
    private val ids by lazy { ActivitySignInPageBinding.inflate(layoutInflater) }
    private lateinit var sharedPref: SharedPreferences
    lateinit var loader: Dialog
    private val callbackManager = CallbackManager.Factory.create()

    private lateinit var enteredPhNumber: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext);
        setContentView(ids.root)
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        /*-----------------*/
        loader = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        val view: View = View.inflate(this, R.layout.loader, null)
        loader.setContentView(view)
        loader.setCancelable(true)
        loader.create()
        /*-----------------*/

        ids.googleBtn.setOnDebounceListener {
            loader.show()
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
            Store.socialIdFrom = "google"
        }

        ids.emailNumEdTxt.setText(Store.phNumber)
        ids.emailNumEdTxt.requestFocus()

        ids.continueBtn.setOnDebounceListener {
            enteredPhNumber = ids.emailNumEdTxt.text.toString()

            if (ids.emailNumEdTxt.text.isNotEmpty()) {
                signInSendOtp(enteredPhNumber)
            } else {
                toast("Field can't be empty")
            }
            Store.from = "signIn"
        }

        ids.signupTv.setOnDebounceListener {
            startActivity(Intent(this, SignUpPage::class.java))
            loader.show()
        }
        /* -----------face book social login activity-------------*/
        ids.faceBookBtn.setOnDebounceListener {
            loader.show()
            ids.facebookLogin.performClick()
            Store.socialIdFrom = "facebook"
            ids.facebookLogin.setPermissions(listOf("email"))
            ids.facebookLogin.registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        Store.socialId = result.accessToken.userId
                        val bundle = Bundle()
                        bundle.putString("fields", "id, email, first_name, last_name")

                        //Graph API to access the data of user's facebook account
                        val request =
                            GraphRequest.newMeRequest(result.accessToken) { fbObject, response ->
                                Log.v("Login Success", response.toString())
                                //For safety measure enclose the request with try and catch
                                try {
                                    Log.d(TAG, "onSuccess: fbObject $fbObject")
                                    Store.userEmail = fbObject?.getString("email").toString()
                                    val name = fbObject?.getString("last_name")
                                    Store.userName = "${fbObject?.getString("first_name")} $name"
                                    if (Store.socialIdFrom == "facebook") {
                                        socialLogin(Store.socialId)
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    loader.dismiss()
                                }
                            }
                        request.parameters = bundle
                        request.executeAsync()
                    }

                    override fun onCancel() {
                        loader.dismiss()
                        Log.d(TAG, "onCancel: called")
                        toast("You Canceled")
                    }

                    override fun onError(error: FacebookException) {
                        loader.dismiss()
                        Log.d(TAG, "onError: called")
                        toast("Something went wrong")
                    }
                })
        }
/* -----------face book social login activity- ends------------*/
    }

    private fun signInSendOtp(phNumber: String) {
        val jsonObject = JsonObject()

        try {
            jsonObject.addProperty("mobileNumber", phNumber)
            jsonObject.addProperty("type", "sendotp")

            // api call
            ResponseApi().signInSendOtp(jsonObject).enqueue(object : Callback<SendOtp?> {
                override fun onResponse(call: Call<SendOtp?>, response: Response<SendOtp?>) {
                    if (response.isSuccessful) {
                        if (response.body()?.code == 201) {
                            Store.phNumber = phNumber
                            Store.countryCode = response.body()?.countryCode.toString()
                            startActivity(Intent(this@SignInPage, OtpVerificationPage::class.java))
                        } else {
                            toast("${response.body()?.message}")
                            Store.tempVar = ids.emailNumEdTxt.text.toString()
                            Store.socialIdFrom = ""
                            startActivity(Intent(this@SignInPage, SignUpPage::class.java))
                        }
                    }
                }

                override fun onFailure(call: Call<SendOtp?>, t: Throwable) {
                    toast("${t.message}")
                    toast("No Internet Connection")
                }
            })
        } catch (E: JSONException) {
            E.printStackTrace()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@SignInPage, LoginSignPage::class.java))
        super.onBackPressed()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            Store.socialId = account.id.toString()
            if (Store.socialIdFrom == "google") {
                Store.userName = account.displayName.toString()
                Store.userEmail = account.email.toString()

                socialLogin(Store.socialId)
            } else toast("Something Went Wrong")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun socialLogin(socialId: String?) {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("socialId", socialId)

            ResponseApi().socialLogin(jsonObject).enqueue(object : Callback<SocialLogin?> {
                override fun onResponse(
                    call: Call<SocialLogin?>,
                    response: Response<SocialLogin?>,
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.code == 201) {
                            val userToken = response.body()?.userToken.toString()
                            setUserDetails(userToken)
                        } else {
                            startActivity(Intent(this@SignInPage, SignUpPage::class.java))
                        }
                    } else toast("Failed to Connect")
                    loader.dismiss()
                }

                override fun onFailure(call: Call<SocialLogin?>, t: Throwable) {
                    Log.e("test", t.message.toString())
                    loader.dismiss()
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
            loader.dismiss()
        }
    }

    private fun setUserDetails(userToken: String) {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", userToken)

            ResponseApi().setUpUserDetails(jsonObject)
                .enqueue(object : Callback<SetLogInUserDetailsDC?> {
                    override fun onResponse(
                        call: Call<SetLogInUserDetailsDC?>,
                        response: Response<SetLogInUserDetailsDC?>,
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()?.code == 201) {
                                Log.i("test", "btn-- take user details")
                                Store.userName = response.body()?.data?.name.toString()
                                Store.userEmail = response.body()?.data?.email.toString()
                                Store.countryCode = response.body()?.data?.countryCode.toString()
                                Store.phNumber = response.body()?.data?.mobilenumber.toString()

                                sharedPref.edit().apply {
                                    putString("userToken", response.body()?.data?.userToken)
                                    putString("userName", Store.userName)
                                    putString("userEmail", Store.userEmail)
                                    putString("phNumber", Store.phNumber)
                                    putString("countryCode", Store.countryCode)
                                    apply()
                                }
                                startActivity(Intent(this@SignInPage, HomePage::class.java))
                                loader.dismiss()
                            } else {
                                Toast.makeText(this@SignInPage,
                                    "Error On Receive data",
                                    Toast.LENGTH_SHORT).show()
                                loader.dismiss()
                            }
                        }
                    }

                    override fun onFailure(call: Call<SetLogInUserDetailsDC?>, t: Throwable) {
                        Toast.makeText(this@SignInPage, "${t.message}", Toast.LENGTH_SHORT).show()
                        loader.dismiss()
                    }
                })

        } catch (e: JSONException) {
            e.printStackTrace()
            loader.dismiss()
        }
    }
}