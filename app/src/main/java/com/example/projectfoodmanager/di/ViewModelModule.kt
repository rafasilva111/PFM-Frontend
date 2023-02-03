package com.codelytical.flybuy.presentation.di

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
		auth: FirebaseAuth,
		database: FirebaseFirestore,
		sharedPreference: SharedPreference,
	) : AuthViewModel{
		return AuthViewModel(repository,authUseCase,auth,database,sharedPreference)
	}


}