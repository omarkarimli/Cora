package com.omarkarimli.cora.domain.use_case.firestore

import com.omarkarimli.cora.domain.models.ReportIssueModel
import com.omarkarimli.cora.domain.repository.FirestoreRepository
import javax.inject.Inject

class AddReportIssueUseCase @Inject constructor(
    private val repository: FirestoreRepository
) {
    suspend operator fun invoke(reportIssueModel: ReportIssueModel) = repository.addReportIssue(reportIssueModel)
}