package com.example.alertapp.entities

class User(){
    private var id = ""
    private var userName = ""
    private var email = ""
    private var password = ""

    constructor(id: String, userName: String, email: String, password: String) : this() {
        this.id = id
        this.userName = userName
        this.email = email
        this.password = password
    }



    fun getId(): String {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getUserName(): String {
        return userName
    }

    fun setUserName(userName: String) {
        this.userName = userName
    }

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getPassword(): String {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
    }
}