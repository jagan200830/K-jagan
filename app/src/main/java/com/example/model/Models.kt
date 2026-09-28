package com.example.model

enum class CategoryGroup(val title: String) {
  HOME_REPAIR("Home Repair"),
  HOME_SERVICES("Home Services"),
  COMMUNITY_SERVICES("Community Services")
}

data class ServiceCategory(
  val id: String,
  val name: String,
  val group: CategoryGroup,
  val iconName: String,
  val shortDescription: String,
  val fullDescription: String,
  val startingPrice: Int,
  val rating: Double,
  val professionalCount: Int,
  val problemsHandled: List<String>,
  val emergencyAvailable: Boolean = false,
  val avgResponseTime: String = "30-45 mins"
)

data class Professional(
  val id: String,
  val name: String,
  val serviceCategory: String,
  val rating: Double,
  val reviewCount: Int,
  val completedJobs: Int,
  val experienceYears: Int,
  val distanceKm: Double,
  val city: String,
  val isAvailableNow: Boolean,
  val startingPrice: Int,
  val isVerified: Boolean = true,
  val skills: List<String>,
  val serviceArea: String,
  val workingHours: String,
  val languages: List<String>,
  val phone: String,
  val about: String,
  val initialAvatarColor: Long = 0xFF1E40AF
)

enum class BookingStatus(val displayName: String) {
  REQUESTED("Requested"),
  ACCEPTED("Accepted"),
  ON_THE_WAY("On the Way"),
  ARRIVED("Arrived"),
  SERVICE_STARTED("Service Started"),
  COMPLETED("Completed"),
  CANCELLED("Cancelled")
}

data class Booking(
  val id: String,
  val customerName: String,
  val customerPhone: String,
  val professionalId: String,
  val professionalName: String,
  val serviceId: String,
  val serviceName: String,
  val problemDescription: String,
  val date: String,
  val timeSlot: String,
  val address: String,
  val city: String,
  var status: BookingStatus,
  val basePrice: Int,
  val platformFee: Int = 29,
  val taxes: Int = 45,
  val discount: Int = 50,
  val totalAmount: Int,
  var paymentStatus: String = "Pending", // "Pending" or "Paid"
  var paymentMethod: String = "UPI (Google Pay)",
  val createdAt: String,
  var estimatedArrivalMinutes: Int = 18,
  var professionalDistanceKm: Double = 1.8
)

enum class MessageReadStatus {
  SENT, DELIVERED, READ
}

data class ChatMessage(
  val id: String,
  val bookingId: String,
  val senderRole: String, // "customer" or "professional"
  val senderName: String,
  val text: String,
  val time: String,
  var status: MessageReadStatus = MessageReadStatus.READ
)

data class CustomerReview(
  val id: String,
  val bookingId: String,
  val professionalId: String,
  val customerName: String,
  val date: String,
  val rating: Float,
  val qualityRating: Float,
  val behaviourRating: Float,
  val punctualityRating: Float,
  val pricingRating: Float,
  val comment: String
)

data class NotificationItem(
  val id: String,
  val title: String,
  val message: String,
  val timeAgo: String,
  val type: String,
  var isRead: Boolean = false,
  val bookingId: String? = null
)

data class AIRecommendation(
  val detectedCategory: String,
  val serviceId: String,
  val detectedProblems: List<String>,
  val suggestedAction: String,
  val urgencyLevel: String, // "HIGH - Emergency", "MEDIUM", "STANDARD"
  val followUpQuestion: String? = null,
  val followUpOptions: List<String> = emptyList(),
  val recommendedPros: List<Professional> = emptyList()
)
