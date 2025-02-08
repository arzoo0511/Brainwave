package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import com.google.firebase.cloud.FirestoreClient
import com.google.cloud.firestore.FieldValue
import kotlinx.serialization.Serializable

fun Application.configureSubjectRoutes() {
    routing {
        // Get all subjects for a grade
        get("/grades/{grade}/subjects") {
            val grade = call.parameters["grade"]
            if (grade == null) {
                call.respond(HttpStatusCode.BadRequest, "Grade is required")
                return@get
            }
            val firestore = FirestoreClient.getFirestore()
            val document = firestore.collection("grades").document(grade).get().get()
            val subjects = document.get("subjects") as? List<String> ?: emptyList()
            call.respond(HttpStatusCode.OK, subjects)
        }

        // Add a subject to a grade
        post("/grades/{grade}/subjects") {
            val grade = call.parameters["grade"]
            if (grade == null) {
                call.respond(HttpStatusCode.BadRequest, "Grade is required")
                return@post
            }
            val request = call.receive<SubjectRequest>()
            val firestore = FirestoreClient.getFirestore()
            val gradeRef = firestore.collection("grades").document(grade)
            gradeRef.update("subjects", FieldValue.arrayUnion(request.subject)).get()
            firestore.collection("grades").document(grade).collection("subjects").document(request.subject)
                .set(mapOf("chapters" to emptyList<String>()))
            call.respond(HttpStatusCode.Created, "Subject added successfully!")
        }

        // Delete a subject from a grade
        delete("/grades/{grade}/subjects/{subject}") {
            val grade = call.parameters["grade"]
            val subject = call.parameters["subject"]
            if (grade == null || subject == null) {
                call.respond(HttpStatusCode.BadRequest, "Grade and subject are required")
                return@delete
            }
            val firestore = FirestoreClient.getFirestore()
            firestore.collection("grades").document(grade).collection("subjects").document(subject).delete()
            call.respond(HttpStatusCode.OK, "Subject deleted successfully!")
        }
    }
}

@Serializable
data class SubjectRequest(val subject: String)
