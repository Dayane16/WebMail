package br.com.fiap.emaillocawebapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.fiap.emaillocawebapp.data.EmailEntity

@Dao
interface EmailDao {
    @Query("SELECT * FROM emails")
    suspend fun getAllEmails(): List<EmailEntity>

    @Insert
    suspend fun insertEmail(email: EmailEntity)

    @Query("SELECT * FROM emails WHERE id = :id")
    suspend fun getEmailById(id: Long): EmailEntity?

    @Delete
    suspend fun deleteEmail(email: EmailEntity)

    @Query("SELECT * FROM emails WHERE subject LIKE '%' || :query || '%' OR sender LIKE '%' || :query || '%'")
    suspend fun searchEmails(query: String): List<EmailEntity>
}

