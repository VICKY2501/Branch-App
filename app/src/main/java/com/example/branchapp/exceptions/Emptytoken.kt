package com.example.branchapp.exceptions

class Emptytoken : Exception() {
    override fun getLocalizedMessage(): String? {
        // Custom error message indicating empty auth token or data fetching error
        return "Empty auth token or there was some error in fetching the data"
    }
}