package com.omarkarimli.cora.domain.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult

interface AuthRepository {
    suspend fun signInWithCredential(credential: AuthCredential): AuthResult
    suspend fun signOut()
}