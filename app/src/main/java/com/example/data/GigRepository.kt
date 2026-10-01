package com.example.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.model.AIRecommendation
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.ChatMessage
import com.example.model.CustomerReview
import com.example.model.MessageReadStatus
import com.example.model.NotificationItem
import com.example.model.Professional
import com.example.model.ServiceCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GigRepository(private val appScope: CoroutineScope) {

  var selectedCity by mutableStateOf("Bengaluru")
  var activeRole by mutableStateOf("CUSTOMER") // "CUSTOMER", "PROFESSIONAL", "ADMIN"
  var activeUserPhone by mutableStateOf("+91 00000 00000")
  var activeUserName by mutableStateOf("Aditi Sharma")
  var activeUserAddress by mutableStateOf("#42, 3rd Cross, 7th Main, Koramangala 4th Block, Bengaluru")

  fun updateCustomerProfile(name: String, phone: String, address: String) {
    activeUserName = name
    activeUserPhone = phone
    activeUserAddress = address
  }

  fun updateProfessionalProfile(
    proId: String,
    workingHours: String,
    serviceArea: String,
    isAvailable: Boolean,
    startingPrice: Int? = null,
    skills: List<String>? = null
  ) {
    val index = professionals.indexOfFirst { it.id == proId }
    if (index >= 0) {
      val old = professionals[index]
      professionals[index] = old.copy(
        workingHours = workingHours,
        serviceArea = serviceArea,
        isAvailableNow = isAvailable,
        startingPrice = startingPrice ?: old.startingPrice,
        skills = skills ?: old.skills
      )
    }
  }

  val serviceCategories = mutableStateListOf<ServiceCategory>().apply {
    addAll(SampleData.serviceCategories)
  }

  val professionals = mutableStateListOf<Professional>().apply {
    addAll(SampleData.professionals)
  }

  val bookings = mutableStateListOf<Booking>().apply {
    addAll(SampleData.initialBookings)
  }

  val chatMessages = mutableStateListOf<ChatMessage>().apply {
    addAll(SampleData.initialChatMessages)
  }

  val reviews = mutableStateListOf<CustomerReview>().apply {
    addAll(SampleData.reviews)
  }

  val notifications = mutableStateListOf<NotificationItem>().apply {
    addAll(SampleData.initialNotifications)
  }

  // Active tracking booking
  var activeTrackingBookingId by mutableStateOf<String?>("BK-8421")
  var activeChatBookingId by mutableStateOf<String?>("BK-8421")

  // Chat typing indicator simulation
  var isProfessionalTyping by mutableStateOf(false)

  fun getFilteredProfessionals(
    categoryId: String? = null,
    searchQuery: String = "",
    city: String = selectedCity,
    onlyAvailable: Boolean = false,
    minRating: Double = 0.0
  ): List<Professional> {
    return professionals.filter { pro ->
      val matchesCity = city.isEmpty() || pro.city.equals(city, ignoreCase = true)
      val matchesCategory = categoryId.isNullOrEmpty() ||
          pro.serviceCategory.contains(categoryId, ignoreCase = true) ||
          serviceCategories.find { it.id == categoryId }?.name?.equals(pro.serviceCategory, ignoreCase = true) == true
      val matchesSearch = searchQuery.isEmpty() ||
          pro.name.contains(searchQuery, ignoreCase = true) ||
          pro.serviceCategory.contains(searchQuery, ignoreCase = true) ||
          pro.skills.any { it.contains(searchQuery, ignoreCase = true) }
      val matchesAvail = !onlyAvailable || pro.isAvailableNow
      val matchesRating = pro.rating >= minRating

      matchesCity && matchesCategory && matchesSearch && matchesAvail && matchesRating
    }
  }

  fun getServiceById(id: String): ServiceCategory? {
    return serviceCategories.find { it.id.equals(id, ignoreCase = true) }
  }

  fun getProfessionalById(id: String): Professional? {
    return professionals.find { it.id == id }
  }

  fun getBookingById(id: String): Booking? {
    return bookings.find { it.id == id }
  }

  fun getReviewsForProfessional(proId: String): List<CustomerReview> {
    return reviews.filter { it.professionalId == proId }
  }

  fun getMessagesForBooking(bookingId: String): List<ChatMessage> {
    return chatMessages.filter { it.bookingId == bookingId }
  }

  // Smart AI natural language diagnosis
  fun analyzeProblemQuery(input: String): AIRecommendation {
    val lower = input.lowercase().trim()

    return when {
      lower.contains("pipe") || lower.contains("leak") || lower.contains("tap") ||
          lower.contains("drain") || lower.contains("water") || lower.contains("flush") ||
          lower.contains("sink") || lower.contains("plumber") || lower.contains("plumbing") -> {
        val emergency = lower.contains("burst") || lower.contains("flood") || lower.contains("broken pipe")
        AIRecommendation(
          detectedCategory = "Plumbing Service",
          serviceId = "plumbing",
          detectedProblems = listOf(
            if (lower.contains("pipe")) "Pipe leakage / ruptured line" else "Water faucet malfunction",
            "Potential water damage risk",
            "Pressure seal degradation"
          ),
          suggestedAction = if (emergency)
            "Shut off your main water valve immediately to avoid property damage. We've matched emergency plumbers within 2 km."
          else
            "Book an on-demand verified plumber to inspect pipe joints, seal washer, and test flow.",
          urgencyLevel = if (emergency) "HIGH - Emergency" else "MEDIUM",
          followUpQuestion = "Where exactly is the water leaking from?",
          followUpOptions = listOf("Under kitchen sink", "Bathroom shower/tap", "Main water meter line", "Overhead ceiling damp"),
          recommendedPros = getFilteredProfessionals(categoryId = "plumbing", city = selectedCity).take(3)
        )
      }

      lower.contains("spark") || lower.contains("shock") || lower.contains("wire") ||
          lower.contains("mcb") || lower.contains("short circuit") || lower.contains("fuse") ||
          lower.contains("switch") || lower.contains("electric") -> {
        AIRecommendation(
          detectedCategory = "Electrical Emergency",
          serviceId = "electrical",
          detectedProblems = listOf(
            "Sparking contacts or loose terminal",
            "High electrical fire risk",
            "Overloaded circuit breaker"
          ),
          suggestedAction = "⚠️ Do NOT touch the switchboard with wet hands. Trip the main MCB breaker if sparking persists.",
          urgencyLevel = "HIGH - Emergency",
          followUpQuestion = "Is the circuit breaker tripping continuously?",
          followUpOptions = listOf("Yes, main MCB trips", "Occasional sparking only", "Burning plastic smell", "No power in one room"),
          recommendedPros = getFilteredProfessionals(categoryId = "electrical", city = selectedCity).take(3)
        )
      }

      lower.contains("ac") || lower.contains("cool") || lower.contains("air condition") ||
          lower.contains("compressor") || lower.contains("filter") -> {
        AIRecommendation(
          detectedCategory = "AC Repair & Servicing",
          serviceId = "ac_repair",
          detectedProblems = listOf(
            "Refrigerant gas pressure drop",
            "Choked condenser fins / air filter",
            "Indoor unit blower dust buildup"
          ),
          suggestedAction = "A foam jet deep wash with digital pressure gauge inspection will restore 100% cooling within 45 mins.",
          urgencyLevel = "STANDARD",
          followUpQuestion = "What type of AC do you have installed?",
          followUpOptions = listOf("Split AC (1.5 / 2 Ton)", "Window AC", "Inverter AC with error code", "Cassette / Central AC"),
          recommendedPros = getFilteredProfessionals(categoryId = "ac_repair", city = selectedCity).take(3)
        )
      }

      lower.contains("clean") || lower.contains("dust") || lower.contains("stain") ||
          lower.contains("dirty") || lower.contains("mop") || lower.contains("sofa") -> {
        AIRecommendation(
          detectedCategory = "Deep Home Cleaning",
          serviceId = "cleaning",
          detectedProblems = listOf(
            "Hard water scale & grout grime",
            "Grease buildup on chimney & stove",
            "Upholstery allergen accumulation"
          ),
          suggestedAction = "Our cooperative cleaning crew arrives with industrial vacuums, steamers, and German biodegradable descaling chemicals.",
          urgencyLevel = "STANDARD",
          followUpQuestion = "Which space requires priority cleaning?",
          followUpOptions = listOf("Full House (2BHK/3BHK)", "Kitchen & Chimney only", "Bathrooms descaling", "Sofa & Carpet shampoo"),
          recommendedPros = getFilteredProfessionals(categoryId = "cleaning", city = selectedCity).take(3)
        )
      }

      lower.contains("elderly") || lower.contains("senior") || lower.contains("grandparent") ||
          lower.contains("parent") || lower.contains("hospital walk") || lower.contains("companion") -> {
        AIRecommendation(
          detectedCategory = "Elderly Companion & Help",
          serviceId = "elderly_assistance",
          detectedProblems = listOf(
            "Need trusted hospital escort",
            "Walking / wheelchair assistance required",
            "Medication schedule reminder & company"
          ),
          suggestedAction = "Certified caregivers with basic first-aid training and verified background checks provide loving accompaniment.",
          urgencyLevel = "STANDARD",
          followUpQuestion = "What assistance is needed most?",
          followUpOptions = listOf("Doctor consultation escort", "Pharmacy & grocery walk", "Post-surgery home help", "Conversational companion"),
          recommendedPros = getFilteredProfessionals(categoryId = "elderly_assistance", city = selectedCity).take(3)
        )
      }

      lower.contains("door") || lower.contains("wood") || lower.contains("lock") ||
          lower.contains("hinge") || lower.contains("furniture") || lower.contains("shelf") ||
          lower.contains("carpenter") -> {
        AIRecommendation(
          detectedCategory = "Carpentry & Woodwork",
          serviceId = "carpentry",
          detectedProblems = listOf(
            "Misaligned door hinge or sagging latch",
            "Jammed lock cylinder",
            "Drawer slider ball-bearing friction"
          ),
          suggestedAction = "Experienced carpenter will bring replacement heavy-duty hinges, chisels, and power planer for smooth closure.",
          urgencyLevel = "STANDARD",
          followUpQuestion = "What is the problem item?",
          followUpOptions = listOf("Main entrance door lock", "Bedroom door dragging floor", "Kitchen wardrobe drawer", "Bed assembly"),
          recommendedPros = getFilteredProfessionals(categoryId = "carpentry", city = selectedCity).take(3)
        )
      }

      lower.contains("washing machine") || lower.contains("fridge") || lower.contains("refrigerator") ||
          lower.contains("geyser") || lower.contains("microwave") || lower.contains("appliance") -> {
        AIRecommendation(
          detectedCategory = "Appliance Repair",
          serviceId = "appliance_repair",
          detectedProblems = listOf(
            "Motor bearing vibration or belt slippage",
            "Heating element calcification",
            "PCB sensor diagnostic error"
          ),
          suggestedAction = "Factory-trained technician will inspect wiring, replace worn parts with OEM genuine items with 90-day warranty.",
          urgencyLevel = "MEDIUM",
          followUpQuestion = "Which appliance is having issues?",
          followUpOptions = listOf("Washing Machine (drum/spinning)", "Refrigerator (not cooling)", "Geyser (no hot water)", "Microwave"),
          recommendedPros = getFilteredProfessionals(categoryId = "appliance_repair", city = selectedCity).take(3)
        )
      }

      lower.contains("delivery") || lower.contains("courier") || lower.contains("package") ||
          lower.contains("pick up") || lower.contains("drop") -> {
        AIRecommendation(
          detectedCategory = "Local Courier & Delivery",
          serviceId = "delivery",
          detectedProblems = listOf(
            "Urgent point-to-point delivery",
            "Prescription or key retrieval",
            "Fragile / warm food handling"
          ),
          suggestedAction = "Cooperative courier rider assigned within 3 minutes with OTP verification and live GPS delivery tracking.",
          urgencyLevel = "HIGH - Urgent",
          followUpQuestion = "What item are you sending?",
          followUpOptions = listOf("Documents / Keys", "Urgent Medicines", "Homemade Tiffin", "Small retail package"),
          recommendedPros = getFilteredProfessionals(categoryId = "delivery", city = selectedCity).take(3)
        )
      }

      else -> {
        AIRecommendation(
          detectedCategory = "General Community & Home Repair",
          serviceId = "plumbing",
          detectedProblems = listOf(
            "Service diagnostic assessment needed",
            "Custom task estimate requested"
          ),
          suggestedAction = "We have matched top-rated verified service professionals in $selectedCity who can inspect and provide an upfront quote.",
          urgencyLevel = "STANDARD",
          followUpQuestion = "Select the closest category to continue:",
          followUpOptions = listOf("Plumbing & Water", "Electrical & Wiring", "AC & Appliances", "Deep Home Cleaning"),
          recommendedPros = getFilteredProfessionals(city = selectedCity).take(3)
        )
      }
    }
  }

  // Book a new service
  fun createBooking(
    serviceId: String,
    professionalId: String,
    problemDescription: String,
    date: String,
    timeSlot: String,
    address: String,
    payNow: Boolean
  ): Booking {
    val service = getServiceById(serviceId)
    val pro = getProfessionalById(professionalId)
    val newId = "BK-${(8430..8999).random()}"

    val basePrice = pro?.startingPrice ?: service?.startingPrice ?: 299
    val platformFee = 29
    val taxes = (basePrice * 0.18).toInt()
    val discount = 50
    val total = basePrice + platformFee + taxes - discount

    val newBooking = Booking(
      id = newId,
      customerName = activeUserName,
      customerPhone = activeUserPhone,
      professionalId = professionalId,
      professionalName = pro?.name ?: "Verified Professional",
      serviceId = serviceId,
      serviceName = service?.name ?: "Home Service",
      problemDescription = problemDescription.ifBlank { "Service appointment requested via Cooperative Gig Platform" },
      date = date,
      timeSlot = timeSlot,
      address = address.ifBlank { activeUserAddress },
      city = selectedCity,
      status = if (payNow) BookingStatus.ACCEPTED else BookingStatus.REQUESTED,
      basePrice = basePrice,
      platformFee = platformFee,
      taxes = taxes,
      discount = discount,
      totalAmount = total,
      paymentStatus = if (payNow) "Paid" else "Pending",
      paymentMethod = if (payNow) "UPI (Instant Online)" else "Cash on Service",
      createdAt = "Just now",
      estimatedArrivalMinutes = if (timeSlot.contains("Immediate", ignoreCase = true)) 18 else 45,
      professionalDistanceKm = pro?.distanceKm ?: 1.5
    )

    bookings.add(0, newBooking)
    activeTrackingBookingId = newId
    activeChatBookingId = newId

    // Add initial automated greeting in chat from pro
    chatMessages.add(
      ChatMessage(
        id = "msg_${System.currentTimeMillis()}",
        bookingId = newId,
        senderRole = "professional",
        senderName = pro?.name ?: "Service Professional",
        text = "Namaskara! I have received your booking for ${newBooking.serviceName}. I am preparing tools and will update you shortly.",
        time = "Just now",
        status = MessageReadStatus.DELIVERED
      )
    )

    // Add notification
    notifications.add(
      0,
      NotificationItem(
        id = "notif_${System.currentTimeMillis()}",
        title = "Booking Confirmed ($newId)",
        message = "Assigned to ${pro?.name} for ${newBooking.serviceName}. Live tracking is now active.",
        timeAgo = "Just now",
        type = "booking",
        isRead = false,
        bookingId = newId
      )
    )

    return newBooking
  }

  // Real-Time Chat message send with simulated response & read receipts
  fun sendChatMessage(bookingId: String, text: String) {
    if (text.isBlank()) return

    val booking = getBookingById(bookingId)
    val proName = booking?.professionalName ?: "Service Professional"

    val userMsg = ChatMessage(
      id = "msg_${System.currentTimeMillis()}",
      bookingId = bookingId,
      senderRole = if (activeRole == "PROFESSIONAL") "professional" else "customer",
      senderName = if (activeRole == "PROFESSIONAL") proName else activeUserName,
      text = text.trim(),
      time = "Just now",
      status = MessageReadStatus.SENT
    )
    chatMessages.add(userMsg)

    // Simulate delivery & read receipt updates, followed by simulated reply if customer sent
    if (activeRole != "PROFESSIONAL") {
      appScope.launch(Dispatchers.Main) {
        delay(600)
        userMsg.status = MessageReadStatus.DELIVERED
        delay(800)
        userMsg.status = MessageReadStatus.READ
        isProfessionalTyping = true
        delay(1800)
        isProfessionalTyping = false

        val replyText = generateSimulatedReply(text, booking?.serviceName ?: "Service")
        chatMessages.add(
          ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            bookingId = bookingId,
            senderRole = "professional",
            senderName = proName,
            text = replyText,
            time = "Just now",
            status = MessageReadStatus.READ
          )
        )
      }
    }
  }

  private fun generateSimulatedReply(customerText: String, service: String): String {
    val lower = customerText.lowercase()
    return when {
      lower.contains("way") || lower.contains("where") || lower.contains("reach") || lower.contains("time") ->
        "Yes, I am en route on my scooter. GPS is on, arriving in approximately 10-12 minutes."
      lower.contains("gate") || lower.contains("building") || lower.contains("flat") || lower.contains("security") ->
        "Noted! I will show my Cooperative Gig ID badge at security and come up."
      lower.contains("parts") || lower.contains("pipe") || lower.contains("spare") || lower.contains("bring") ->
        "Don't worry, I have an assortment of standard OEM spare parts and tools with me."
      lower.contains("price") || lower.contains("cost") || lower.contains("extra") || lower.contains("rate") ->
        "Our cooperative rates are 100% standardized and transparent. Any extra parts needed will be billed at MRP with zero markup."
      else ->
        "Understood! Thank you for the update. I am handling this with utmost priority."
    }
  }

  // Advance tracking status for demonstration
  fun advanceTrackingStatus(bookingId: String) {
    val booking = getBookingById(bookingId) ?: return
    val nextStatus = when (booking.status) {
      BookingStatus.REQUESTED -> BookingStatus.ACCEPTED
      BookingStatus.ACCEPTED -> BookingStatus.ON_THE_WAY
      BookingStatus.ON_THE_WAY -> BookingStatus.ARRIVED
      BookingStatus.ARRIVED -> BookingStatus.SERVICE_STARTED
      BookingStatus.SERVICE_STARTED -> BookingStatus.COMPLETED
      BookingStatus.COMPLETED -> BookingStatus.COMPLETED
      BookingStatus.CANCELLED -> BookingStatus.CANCELLED
    }
    booking.status = nextStatus

    // Update distance/time indicators
    when (nextStatus) {
      BookingStatus.ACCEPTED -> {
        booking.estimatedArrivalMinutes = 20
        booking.professionalDistanceKm = 2.2
      }
      BookingStatus.ON_THE_WAY -> {
        booking.estimatedArrivalMinutes = 10
        booking.professionalDistanceKm = 1.1
      }
      BookingStatus.ARRIVED -> {
        booking.estimatedArrivalMinutes = 0
        booking.professionalDistanceKm = 0.0
        notifications.add(
          0,
          NotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            title = "Professional Arrived!",
            message = "${booking.professionalName} has arrived at your doorstep for booking #${booking.id}.",
            timeAgo = "Just now",
            type = "tracking",
            bookingId = booking.id
          )
        )
      }
      BookingStatus.SERVICE_STARTED -> {
        booking.estimatedArrivalMinutes = 0
        booking.professionalDistanceKm = 0.0
      }
      BookingStatus.COMPLETED -> {
        booking.paymentStatus = "Paid"
        notifications.add(
          0,
          NotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            title = "Service Completed",
            message = "Your service #${booking.id} was completed successfully. Please leave a review!",
            timeAgo = "Just now",
            type = "review",
            bookingId = booking.id
          )
        )
      }
      else -> {}
    }
  }

  // Pay booking
  fun completePayment(bookingId: String, method: String) {
    val booking = getBookingById(bookingId) ?: return
    booking.paymentStatus = "Paid"
    booking.paymentMethod = method

    notifications.add(
      0,
      NotificationItem(
        id = "notif_${System.currentTimeMillis()}",
        title = "Payment Received",
        message = "₹${booking.totalAmount} paid successfully via $method for booking #${booking.id}.",
        timeAgo = "Just now",
        type = "payment",
        bookingId = booking.id
      )
    )
  }

  // Submit review
  fun submitReview(
    bookingId: String,
    professionalId: String,
    overallRating: Float,
    qualityRating: Float,
    behaviourRating: Float,
    punctualityRating: Float,
    pricingRating: Float,
    comment: String
  ) {
    val review = CustomerReview(
      id = "rev_${System.currentTimeMillis()}",
      bookingId = bookingId,
      professionalId = professionalId,
      customerName = activeUserName,
      date = "Today",
      rating = overallRating,
      qualityRating = qualityRating,
      behaviourRating = behaviourRating,
      punctualityRating = punctualityRating,
      pricingRating = pricingRating,
      comment = comment.ifBlank { "Very professional and courteous service under the cooperative platform!" }
    )
    reviews.add(0, review)
  }
}
