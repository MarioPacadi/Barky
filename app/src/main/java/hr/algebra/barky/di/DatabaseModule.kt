package hr.algebra.barky.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hr.algebra.barky.api.DogsApi
import hr.algebra.barky.db.DogDatabase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

private const val DOG_DATABASE = "dog_database"
private const val DOGS_API_URL = "https://dogbreeddb.p.rapidapi.com/"

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): DogDatabase {
        return Room.databaseBuilder(
            context,
            DogDatabase::class.java,
            DOG_DATABASE
        ).build()
    }

    @Provides
    @Singleton
    @ExperimentalSerializationApi
    fun provideDogsApi(okHttpClient: OkHttpClient): DogsApi {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(DOGS_API_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(DogsApi::class.java)
    }
}