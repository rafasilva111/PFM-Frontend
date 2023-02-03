package com.codelytical.flybuy.presentation.di


import com.example.projectfoodmanager.data.repository.AuthRepository
import com.example.projectfoodmanager.domain.usecase.AuthUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

	@Provides
	@Singleton
	fun providesAuthUseCase(authRepository: AuthRepository): AuthUseCase {
		return AuthUseCase(authRepository = authRepository)
	}
}