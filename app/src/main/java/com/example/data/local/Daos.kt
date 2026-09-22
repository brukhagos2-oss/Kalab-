package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BotLogEntity
import com.example.data.model.BotSettingsEntity
import com.example.data.model.HistoricalDecadeEntity
import com.example.data.model.SignalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {
    @Query("SELECT * FROM signals WHERE status = 'ACTIVE' LIMIT 1")
    fun getActiveSignalFlow(): Flow<SignalEntity?>

    @Query("SELECT * FROM signals WHERE status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveSignal(): SignalEntity?

    @Query("SELECT * FROM signals ORDER BY openedAt DESC")
    fun getAllSignals(): Flow<List<SignalEntity>>

    @Query("SELECT * FROM signals ORDER BY openedAt DESC LIMIT 60")
    fun getRecentSignals(): Flow<List<SignalEntity>>

    @Query("SELECT * FROM signals WHERE status IN ('TP_HIT', 'SL_HIT', 'MANUALLY_CLOSED') ORDER BY closedAt DESC")
    fun getResolvedSignals(): Flow<List<SignalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: SignalEntity): Long

    @Update
    suspend fun updateSignal(signal: SignalEntity)

    @Query("DELETE FROM signals")
    suspend fun clearAllSignals()

    @Query("DELETE FROM signals WHERE status = 'ACTIVE'")
    suspend fun deleteActiveSignal()
}

@Dao
interface BotSettingsDao {
    @Query("SELECT * FROM bot_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<BotSettingsEntity?>

    @Query("SELECT * FROM bot_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): BotSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: BotSettingsEntity)
}

@Dao
interface HistoricalDecadeDao {
    @Query("SELECT * FROM historical_decade_data ORDER BY year ASC")
    fun getAllDecadeDataFlow(): Flow<List<HistoricalDecadeEntity>>

    @Query("SELECT * FROM historical_decade_data ORDER BY year ASC")
    suspend fun getAllDecadeData(): List<HistoricalDecadeEntity>

    @Query("SELECT COUNT(*) FROM historical_decade_data")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<HistoricalDecadeEntity>)
}

@Dao
interface BotLogDao {
    @Query("SELECT * FROM bot_logs ORDER BY timestamp DESC LIMIT 100")
    fun getLogsFlow(): Flow<List<BotLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BotLogEntity): Long

    @Query("DELETE FROM bot_logs")
    suspend fun clearLogs()
}
