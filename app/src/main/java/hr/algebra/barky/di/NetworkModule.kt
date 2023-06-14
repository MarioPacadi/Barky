package hr.algebra.barky.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hr.algebra.barky.api.PointsApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

//DI - Dependency Injection

const val FIREBASE_STORAGE_URL = "https://firebasestorage.googleapis.com/v0/b/mobileproject-871e1.appspot.com/o/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient() : OkHttpClient{
        return OkHttpClient.Builder()
            .readTimeout(timeout = 10,TimeUnit.SECONDS)
            .connectTimeout(timeout = 10,TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @ExperimentalSerializationApi
    fun providePointsApi(okHttpClient: OkHttpClient) : PointsApi{
        val json= Json { ignoreUnknownKeys=true }
        return Retrofit.Builder()
            .baseUrl(FIREBASE_STORAGE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PointsApi::class.java)
    }

}