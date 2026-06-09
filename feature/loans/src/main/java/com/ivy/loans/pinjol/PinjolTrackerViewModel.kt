package com.ivy.loans.pinjol

import androidx.lifecycle.ViewModel
import com.ivy.domain.usecase.loans.DebtSurvivalPlannerLogic
import com.ivy.domain.usecase.loans.PlannerSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PinjolTrackerViewModel @Inject constructor(
    private val plannerLogic: DebtSurvivalPlannerLogic
) : ViewModel() {

    suspend fun loadSummary(): PlannerSummary {
        return plannerLogic.getPlannerSummary()
    }
}
