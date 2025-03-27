package com.example.sideindexrecyclerview

data class Contact(val name: String)
  
        }
    }
package amplify.call.room.interfaces

import amplify.call.models.model.FavouritesModel
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavouritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: FavouritesModel)

    @Update
    fun update(model: FavouritesModel)

    @Query("SELECT id FROM tblFavourites WHERE contactId = :contactId AND isCallFavourite=:callFav AND isNetworkFavourite=:netFav")
    fun checkFavourite(contactId: Int, callFav: Int, netFav: Int): Int

//    @Query("SELECT tblFavourites.contactId, tblFavourites.id, tblFavourites.isCallFavourite, " +
//            "tblFavourites.isNetworkFavourite, tblContacts.contactName, tblContacts.contactNumber " +
//            "FROM tblFavourites " +
//            "INNER JOIN tblContacts " +
//            "ON tblContacts.id = tblFavourites.contactId")
//

    fun insertOrUpdate(model: FavouritesModel) {
        val favouriteValues = mapBooleansToInts(model.isCallFavourite, model.isNetworkFavourite)
        val existingId = checkFavourite(model.contactId, favouriteValues.first, favouriteValues.second)
        if (existingId == 0) {
            insert(model)
        } else {
            model.id = existingId
            update(model)
        }
    }

    private fun mapBooleansToInts(isCallFavourite: Boolean, isNetworkFavourite: Boolean): Pair<Int, Int> {
        return Pair(if (isCallFavourite) 1 else 0, if (isNetworkFavourite) 1 else 0)
    }

    @Query("SELECT tblFavourites.contactId, tblFavourites.id, tblFavourites.isCallFavourite, tblFavourites.isNetworkFavourite, tblContacts.contactName, tblContacts.contactNumber FROM tblFavourites INNER JOIN tblContacts ON tblContacts.id = tblFavourites.contactId WHERE isCallFavourite = '1' OR isNetworkFavourite = '1'")
    fun getFavourites(): List<FavouritesModel>

    @Query("SELECT * FROM tblFavourites WHERE isCallFavourite = '1' AND contactId =:contactId")
    fun getFavouritesCall(contactId: Int): FavouritesModel?

    @Query("SELECT * FROM tblFavourites WHERE isNetworkFavourite = '1' AND contactId =:contactId")
    fun getFavouritesNetwork(contactId: Int): FavouritesModel?

    @Delete
    fun softDelete(model: FavouritesModel): Int

    @Query("DELETE FROM tblFavourites")
    fun deleteAllData()

    @Insert
    fun addNewFavData(temp: ArrayList<FavouritesModel>)
}
