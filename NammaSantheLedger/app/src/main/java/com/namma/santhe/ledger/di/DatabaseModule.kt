package com.namma.santhe.ledger.di

import android.content.Context
import androidx.room.Room
import com.namma.santhe.ledger.data.dao.CustomerDao
import com.namma.santhe.ledger.data.dao.TransactionDao
import com.namma.santhe.ledger.data.database.LedgerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LedgerDatabase {
        return Room.databaseBuilder(
            context,
            LedgerDatabase::class.java,
            "namma_santhe_db"
        ).build()
    }

    @Provides
    fun provideCustomerDao(db: LedgerDatabase): CustomerDao = db.customerDao()

    @Provides
    fun provideTransactionDao(db: LedgerDatabase): TransactionDao = db.transactionDao()
}
