package com.example.alertapp.entities

class Contact() {
    private var contactName: String = ""
    private var phoneNumber: String = ""

    constructor(contactName:String, phoneNumber:String):this(){
        this.contactName = contactName
        this.phoneNumber = phoneNumber
    }

    fun getContactName():String{
        return contactName
    }

    fun setContactName(contactName: String){
        this.contactName = contactName
    }

    fun getPhoneNumber():String{
        return phoneNumber
    }

    fun setPhoneNumber(phoneNumber: String){
        this.phoneNumber = phoneNumber
    }



}