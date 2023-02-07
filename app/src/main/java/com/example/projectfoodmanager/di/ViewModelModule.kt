package com.codelytical.flybuy.presentation.di

import android.app.Application
import com.example.projectfoodmanager.data.old.AuthRepository_old
import com.example.projectfoodmanager.data.util.SharedPreference
import com.example.projectfoodmanager.domain.usecase.AuthUseCase
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ViewModelModule {

	@Provides
	@Singleton
	fun providesLoginViewModel(
		authUseCase: AuthUseCase,
		repository: AuthRepository_old,
		application: Application,
		sharedPreference: SharedPreference,
	) : AuthViewModel{
		return AuthViewModel(repository = repository,authUseCase= authUseCase, application = application,sharedPreference =sharedPreference)
	}


}