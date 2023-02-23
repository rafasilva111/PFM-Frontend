package com.codelytical.flybuy.presentation.di

import android.app.Application
import com.example.projectfoodmanager.data.old.AuthRepository_old
import com.example.projectfoodmanager.data.repository.AuthRepository
import com.example.projectfoodmanager.data.util.SharedPreference
import com.example.projectfoodmanager.domain.usecase.AuthUseCase
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
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
		repositoryOld: AuthRepository_old,
		repository: AuthRepository,
		application: Application,
		sharedPreference: SharedPreference,
	) : AuthViewModel{
		return AuthViewModel(repositoryOld = repositoryOld,authUseCase= authUseCase, application = application,sharedPreference =sharedPreference,repository = repository)
	}


}