package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.LoanTrackerEntity
import com.ivy.data.db.entity.DebtPlannerSettingsEntity
import com.ivy.data.db.entity.IncomeTimelineEntity
import java.util.UUID

interface LoanTrackerDao {
    suspend fun findAll(): List<LoanTrackerEntity>
    suspend fun findById(id: UUID): LoanTrackerEntity?
    suspend fun getSettings(): DebtPlannerSettingsEntity?
    suspend fun getIncomeTimeline(): List<IncomeTimelineEntity>
}
