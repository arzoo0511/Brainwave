package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import com.google.firebase.cloud.FirestoreClient
import kotlinx.serialization.Serializable

fun Application.configureGradeRoutes() {
    routing {
        // Get all grades
        get("/grades") {
            val firestore = FirestoreClient.getFirestore()
            val documents = firestore.collection("grades").get().get()
            val grades = documents.documents.map { it.id }
            call.respond(HttpStatusCode.OK, grades)
        }

        // Add a new grade
        post("/grades") {
            val request = call.receive<GradeRequest>()
            val firestore = FirestoreClient.getFirestore()
            val gradeRef = firestore.collection("grades").document(request.grade)
            gradeRef.set(mapOf("subjects" to emptyList<String>())) // Initialize with empty subjects
            call.respond(HttpStatusCode.Created, "Grade added successfully!")
        }

        // Delete a grade
        delete("/grades/{grade}") {
            val grade = call.parameters["grade"]
            if (grade == null) {
                call.respond(HttpStatusCode.BadRequest, "Grade is required")
                return@delete
            }
            val firestore = FirestoreClient.getFirestore()
            firestore.collection("grades").document(grade).delete()
            call.respond(HttpStatusCode.OK, "Grade deleted successfully!")
        }
    }
}

@Serializable
data class GradeRequest(val grade: String)
