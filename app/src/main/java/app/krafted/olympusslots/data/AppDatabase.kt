package app.krafted.olympusslots.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PlayerData::class, LeaderboardEntry::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "olympus_slots_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Raw SQL because INSTANCE isn't set yet during build()
                        db.execSQL(
                            "INSERT OR IGNORE INTO player_data (id, coinBalance, lastBonusClaim, totalSpins, currentWinStreak, bestWinStreak, totalWins, jackpotCount) VALUES (1, 500, 0, 0, 0, 0, 0, 0)"
                        )
                    }
                }).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
