package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.LoanTrackerEntity
import com.ivy.data.db.entity.DebtPlannerSettingsEntity
import com.ivy.data.db.entity.IncomeTimelineEntity
import java.util.UUID

interface WriteLoanTrackerDao {
    suspend fun saveLoan(value: LoanTrackerEntity)
    suspend fun saveSettings(value: DebtPlannerSettingsEntity)
    suspend fun saveIncome(value: IncomeTimelineEntity)
    suspend fun deleteLoanById(id: UUID)
    suspend fun deleteIncomeById(id: UUID)
    suspend fun deleteSettings()
    suspend fun deleteAllLoans()
}
