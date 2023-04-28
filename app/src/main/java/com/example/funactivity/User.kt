package com.example.funactivity

class User {
    val name: String
    val teamName: String
    var steps: String

    constructor( name: String, teamName: String, steps: String ){
        this.name = name
        this.teamName = teamName
        this.steps = steps
    }
}