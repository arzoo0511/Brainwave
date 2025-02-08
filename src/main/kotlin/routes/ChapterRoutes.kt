package com.example.routes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import com.google.firebase.cloud.FirestoreClient
import com.google.cloud.firestore.FieldValue
import kotlinx.serialization.Serializable

fun Application.configureChapterRoutes() {
    routing {
        // Get all chapters for a subject
        get("/grades/{grade}/subjects/{subject}/chapters") {
            val grade = call.parameters["grade"]
            val subject = call.parameters["subject"]
            if (grade == null || subject == null) {
                call.respond(HttpStatusCode.BadRequest, "Grade and subject are required")
                return@get
            }
            val firestore = FirestoreClient.getFirestore()
            val document = firestore.collection("grades").document(grade)
                .collection("subjects").document(subject).get().get()
            val chapters = document.get("chapters") as? List<String> ?: emptyList()
            call.respond(HttpStatusCode.OK, chapters)
        }

        // Add a chapter to a subject
        post("/grades/{grade}/subjects/{subject}/chapters") {
            val grade = call.parameters["grade"]
            val subject = call.parameters["subject"]
            if (grade == null || subject == null) {
                call.respond(HttpStatusCode.BadRequest, "Grade and subject are required")
                return@post
            }
            val request = call.receive<ChapterRequest>()
            val firestore = FirestoreClient.getFirestore()
            val subjectRef = firestore.collection("grades").document(grade)
                .collection("subjects").document(subject)
            subjectRef.update("chapters", FieldValue.arrayUnion(request.chapter)).get()
            call.respond(HttpStatusCode.Created, "Chapter added successfully!")
        }

        // Update a chapter (Rename)
        put("/grades/{grade}/subjects/{subject}/chapters/{chapter}") {
            call.respond(HttpStatusCode.NotImplemented, "Chapter update not implemented yet.")
        }

        // Delete a chapter
        delete("/grades/{grade}/subjects/{subject}/chapters/{chapter}") {
            val grade = call.parameters["grade"]
            val subject = call.parameters["subject"]
            val chapter = call.parameters["chapter"]
            if (grade == null || subject == null || chapter == null) {
                call.respond(HttpStatusCode.BadRequest, "Grade, subject, and chapter are required")
                return@delete
            }
            val firestore = FirestoreClient.getFirestore()
            val subjectRef = firestore.collection("grades").document(grade)
                .collection("subjects").document(subject)
            subjectRef.update("chapters", FieldValue.arrayRemove(chapter)).get()
            call.respond(HttpStatusCode.OK, "Chapter deleted successfully!")
        }
    }
}

@Serializable
data class ChapterRequest(val chapter: String)
