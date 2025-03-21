package com.example.sideindexrecyclerview

data class Contact(val name: String)
  @SuppressLint("Range")
    fun getContactsFromDevice(contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            val contactList: ArrayList<ContactsModel> = ArrayList()

            val deviceContactsList = mutableListOf<ContactsModel>()

            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null,
                null
            )

            if (cursor != null) {
                if (cursor.count > 0) {
                    while (cursor.moveToNext()) {
                        val id =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                        val name =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IS_USER_PROFILE))
                        val phoneNumber = (cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                        )).toInt()

                        if (phoneNumber > 0) {
                            val cursorPhone = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                                arrayOf(id),
                                null
                            )
                            if (cursorPhone != null) {
                                if (cursorPhone.count > 0) {
                                    while (cursorPhone.moveToNext()) {
                                        val phoneNumValue = cursorPhone.getString(
                                            cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                        )
                                        contactList.add(ContactsModel(id, name, phoneNumValue))

                                        contactDao.contactInsertOrUpdate(
                                            ContactsModel(
                                                id,
                                                name,
                                                phoneNumValue.toValidPhone()
                                            )
                                        )
                                        val contact = ContactsModel(id, name, phoneNumValue.toValidPhone())
                                        deviceContactsList.add(contact)
                                    }
                                }
                                cursorPhone.close()
                            }
                        }
                    }
                } else {

                }
            }
            cursor?.close()
            contactDao.removeDeletedContacts(deviceContactsList)
            val lists = contactDao.getAllContacts()
            if (lists.isNullOrEmpty()) {
                _setContactFromDeviceError.postValue("No Data")
            } else {
                _setContactFromDeviceSuccess.postValue(lists)
            }
        }
    }
 @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contactsModel: ContactsModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(contactsModel: ContactsModel)

    @Query("SELECT * FROM tblContacts WHERE contactId = :contactId")
    fun getContactById(contactId: String): ContactsModel?




    @Query("DELETE FROM tblContacts WHERE contactId IN (:contactIds)")
    fun deleteContactsByIds(contactIds: List<String>)


    fun contactInsertOrUpdate(contactsModel: ContactsModel) {
        val id = getContactById(contactsModel.contactId)
        if (id != null) {
            if (id.contactNumber != contactsModel.contactNumber && id.contactName != contactsModel.contactName && contactsModel.contactId == id.contactId) {
                contactsModel.id = id.id
                update(contactsModel)
            } else if (id.contactNumber != contactsModel.contactNumber && id.contactName != contactsModel.contactName) {
                insert(contactsModel)
            } else  {
                contactsModel.id = id.id
                update(contactsModel)
            }
        } else {
            insert(contactsModel)
        }
    }
    fun removeDeletedContacts(deviceContactsList: List<ContactsModel>) {
        val dbContactsList = getAllContacts() ?: emptyList()

        val deviceContactIds = deviceContactsList.map { it.contactId }.toSet()
        val dbContactIds = dbContactsList.map { it.contactId }.toSet()

        val contactsToDelete = dbContactIds - deviceContactIds
        if (contactsToDelete.isNotEmpty()) {
            deleteContactsByIds(contactsToDelete.toList())
        }
    }

