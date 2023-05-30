package com.example.cesta.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable;

@Entity(tableName = "stops")
@Serializable
data class Stop(@PrimaryKey(autoGenerate = true) var id: Long,
                @SerialName("agency_id") @ColumnInfo(name="agency_id") var agencyId: Long,
                @SerialName("name") @ColumnInfo(name="long_name") var longName: String,
                var desc: String = "") {
}