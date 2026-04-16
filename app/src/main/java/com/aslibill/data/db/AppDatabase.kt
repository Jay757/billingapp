package com.aslibill.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    CategoryEntity::class,
    ProductEntity::class,
    CustomerEntity::class,
    BillEntity::class,
    BillItemEntity::class,
    StaffEntity::class,
    CashTransactionEntity::class
  ],
  version = 5,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun categoryDao(): CategoryDao
  abstract fun productDao(): ProductDao
  abstract fun billDao(): BillDao
  abstract fun customerDao(): CustomerDao
  abstract fun staffDao(): StaffDao
  abstract fun cashDao(): CashDao
  abstract fun billAnalyticsDao(): BillAnalyticsDao

  companion object {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun get(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "asli_bill.db"
        )
          .fallbackToDestructiveMigration()
          .build()
          .also { INSTANCE = it }
      }
    }
  }
}
