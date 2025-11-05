package com.omarkarimli.cora.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.omarkarimli.cora.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun signInWithCredential(credential: AuthCredential): AuthResult {
        try {
            return auth.signInWithCredential(credential).await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}
