package com.GlobalCinemaRelease.sdc.interfaces

import com.GlobalCinemaRelease.sdc.response.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ResponseApi {
    @GET("languageList.php")
    fun getLanguage(): Call<CodeResponse>

    @POST("verifyOtp.php")
    fun verifyOtp(@Body params: JsonObject): Call<VerifyOtp>

    @POST("signUpSave.php")
    fun signUpSaveData(@Body params: JsonObject): Call<SignUpSaveData>

    @POST("signUpOtp.php")
    fun signUpSendOtp(@Body params: JsonObject): Call<SendOtp>

    @POST("sendOtp.php")
    fun signInSendOtp(@Body params: JsonObject): Call<SendOtp>

    @POST("userLanguageUpate.php")
    fun languageTokenUpdate(@Body params: JsonObject): Call<UpdateLanguageToken>

    @POST("home.php")
    fun setHomePageMovie(@Body params: JsonObject): Call<HomePageData?>

    @POST("singleMovieDetail.php")
    fun movieDescriptionDetails(@Body params: JsonObject): Call<MoviesDescriptionData>

    @POST("createOrderId.php")
    fun createOrderId(@Body params: JsonObject): Call<CreateOrderIdDataC>

    @POST("completeRentPayment.php")
    fun completePayments(@Body params: JsonObject): Call<CompleatePaymentDataC>

    @POST("profile.php")
    fun setUpUserDetails(@Body params: JsonObject): Call<SetLogInUserDetailsDC>

    @POST("history.php")
    fun setUpProfilePgDetails(@Body params: JsonObject): Call<ProfilePgDetailsDC>

    @POST("seeAll.php")
    fun setUpSearchRes(@Body params: JsonObject): Call<SeeAllSearchResDC>

    @POST("editProfile.php")
    fun updateUserDetails(@Body params: JsonObject): Call<UserDataUpdateDC>

    @POST("socialLogin.php")
    fun socialLogin(@Body params: JsonObject): Call<SocialLogin>

    @POST("movieSearch.php")
    fun searchMoviesRes(@Body params: JsonObject): Call<SearchMoviesDC>

    @POST("searchPage.php")
    fun searchPageDetails(@Body params: JsonObject): Call<SearchPageDetailsDC>

    @POST("addToWatchlist.php")
    fun addToWatchList(@Body params: JsonObject): Call<AddToWatchListDC>

    @POST("userWatchList.php")
    fun watchListMovieShow(@Body params: JsonObject): Call <WatchListMovieShowDC>

    @POST("removeWatchList.php")
    fun removeMovieFromWatchList(@Body params: JsonObject): Call<RemoveMovieFwatchListDC>

    @POST("giftMovie.php")
    fun generateGiftCouponCode(@Body params: JsonObject): Call<GiftCouponDC>

    @POST("claimGift.php")
    fun claimGiftCoupon(@Body params: JsonObject): Call<CouponClaimDC>

    @POST("rateMovie.php")
    fun saveRateMovie(@Body params: JsonObject): Call<RateMovieDC>

    @POST("updateMovieStreamed.php")
    fun updateMoviePlayed(@Body params: JsonObject): Call<UpdateMoviePlayedDC>

    companion object{
        private var Gson = GsonBuilder()
            .setLenient()
            .create()

        operator fun invoke(): ResponseApi{
            return Retrofit.Builder()
                .baseUrl("https://sdcapp.in/development/API/v_0.2/mobileApp/")
                .addConverterFactory(GsonConverterFactory.create(Gson))
                .build()
                .create(ResponseApi::class.java)
        }
    }
}