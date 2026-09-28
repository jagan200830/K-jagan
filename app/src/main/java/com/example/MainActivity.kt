package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.GigRepository
import com.example.model.Professional
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppHeader
import com.example.ui.components.NotificationSheet
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val repository = GigRepository(lifecycleScope)

    setContent {
      MyApplicationTheme {
        MainApp(repository = repository)
      }
    }
  }
}

@Composable
fun MainApp(repository: GigRepository) {
  var currentRoute by remember { mutableStateOf("home") }
  val backStack = remember { mutableStateListOf("home") }

  var selectedProIdForProfile by remember { mutableStateOf<String?>(null) }
  var selectedCategoryForFindPros by remember { mutableStateOf<String?>(null) }
  var initialAIQuery by remember { mutableStateOf<String?>(null) }
  var bookingProId by remember { mutableStateOf<String?>(null) }
  var bookingServiceId by remember { mutableStateOf<String?>(null) }
  var bookingProblem by remember { mutableStateOf<String?>(null) }
  var paymentBookingId by remember { mutableStateOf<String?>(null) }
  var reviewBookingId by remember { mutableStateOf<String?>(null) }
  var reviewProId by remember { mutableStateOf<String?>(null) }

  var showNotifications by remember { mutableStateOf(false) }

  fun navigateTo(route: String) {
    if (currentRoute != route) {
      backStack.add(route)
      currentRoute = route
    }
  }

  fun navigateBack() {
    if (backStack.size > 1) {
      backStack.removeAt(backStack.size - 1)
      currentRoute = backStack.last()
    } else {
      currentRoute = "home"
    }
  }

  BackHandler(enabled = backStack.size > 1) {
    navigateBack()
  }

  // Count unread chat messages for customer
  val unreadChatCount = repository.chatMessages.count {
    it.senderRole == "professional" && it.status != com.example.model.MessageReadStatus.READ
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      if (currentRoute in listOf("home", "services", "find_pros", "my_bookings")) {
        AppHeader(
          repository = repository,
          currentRoute = currentRoute,
          onNavigate = { route -> navigateTo(route) },
          onOpenNotifications = { showNotifications = true }
        )
      }
    },
    bottomBar = {
      if (currentRoute in listOf("home", "services", "ai_assistant", "my_bookings", "chat", "find_pros", "profile_editor") && repository.activeRole == "CUSTOMER") {
        AppBottomNav(
          currentRoute = currentRoute,
          onNavigate = { route -> navigateTo(route) },
          unreadChatCount = unreadChatCount
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Crossfade(targetState = currentRoute, label = "screen_transition") { route ->
        when (route) {
          "home" -> {
            HomeScreen(
              repository = repository,
              onNavigateToServiceDetail = { serviceId ->
                selectedCategoryForFindPros = serviceId
                navigateTo("find_pros")
              },
              onNavigateToProProfile = { proId ->
                selectedProIdForProfile = proId
                navigateTo("pro_profile")
              },
              onNavigateToAIAssistantWithQuery = { query ->
                initialAIQuery = query
                navigateTo("ai_assistant")
              },
              onNavigateToFindPros = { navigateTo("find_pros") },
              onNavigateToServices = { navigateTo("services") },
              onNavigateToProfile = { navigateTo("profile_editor") }
            )
          }

          "services" -> {
            ServicesScreen(
              repository = repository,
              onSelectService = { s ->
                selectedCategoryForFindPros = s.id
                navigateTo("find_pros")
              },
              onFindProsForService = { sId ->
                selectedCategoryForFindPros = sId
                navigateTo("find_pros")
              }
            )
          }

          "ai_assistant" -> {
            AIAssistantScreen(
              repository = repository,
              initialQuery = initialAIQuery,
              onSelectProToBook = { pro, serviceId, problem ->
                bookingProId = pro.id
                bookingServiceId = serviceId
                bookingProblem = problem
                navigateTo("booking")
              },
              onViewProProfile = { proId ->
                selectedProIdForProfile = proId
                navigateTo("pro_profile")
              }
            )
          }

          "find_pros" -> {
            FindProfessionalsScreen(
              repository = repository,
              preselectedCategory = selectedCategoryForFindPros,
              onViewProfile = { proId ->
                selectedProIdForProfile = proId
                navigateTo("pro_profile")
              },
              onBookPro = { pro ->
                bookingProId = pro.id
                bookingServiceId = repository.serviceCategories.find { it.name.equals(pro.serviceCategory, ignoreCase = true) }?.id ?: "plumbing"
                bookingProblem = "Appointment requested with ${pro.name}"
                navigateTo("booking")
              }
            )
          }

          "pro_profile" -> {
            ProfessionalProfileScreen(
              repository = repository,
              proId = selectedProIdForProfile ?: "pro_ramesh",
              onBookPro = { pro ->
                bookingProId = pro.id
                bookingServiceId = repository.serviceCategories.find { it.name.equals(pro.serviceCategory, ignoreCase = true) }?.id ?: "plumbing"
                bookingProblem = "Service appointment booked with ${pro.name}"
                navigateTo("booking")
              },
              onStartChat = { pro ->
                // Check if existing booking exists with this pro or use active
                val existing = repository.bookings.find { it.professionalId == pro.id }
                if (existing != null) {
                  repository.activeChatBookingId = existing.id
                }
                navigateTo("chat")
              },
              onBack = { navigateBack() }
            )
          }

          "booking" -> {
            BookingScreen(
              repository = repository,
              preselectedServiceId = bookingServiceId,
              preselectedProId = bookingProId,
              prefilledProblem = bookingProblem,
              onBookingConfirmed = { bookingId ->
                repository.activeTrackingBookingId = bookingId
                repository.activeChatBookingId = bookingId
                navigateTo("track_service")
              },
              onBack = { navigateBack() }
            )
          }

          "my_bookings" -> {
            MyBookingsScreen(
              repository = repository,
              onTrackBooking = { bookingId ->
                repository.activeTrackingBookingId = bookingId
                navigateTo("track_service")
              },
              onChatBooking = { bookingId ->
                repository.activeChatBookingId = bookingId
                navigateTo("chat")
              },
              onPayBooking = { bookingId ->
                paymentBookingId = bookingId
                navigateTo("payment")
              },
              onReviewBooking = { bId, pId ->
                reviewBookingId = bId
                reviewProId = pId
                navigateTo("review")
              },
              onNewBookingClick = { navigateTo("services") }
            )
          }

          "track_service" -> {
            TrackServiceScreen(
              repository = repository,
              bookingId = repository.activeTrackingBookingId,
              onNavigateToChat = { bId ->
                repository.activeChatBookingId = bId
                navigateTo("chat")
              },
              onServiceCompleted = { bId, pId ->
                reviewBookingId = bId
                reviewProId = pId
                navigateTo("review")
              },
              onBack = { navigateTo("my_bookings") }
            )
          }

          "chat" -> {
            ChatScreen(
              repository = repository,
              bookingId = repository.activeChatBookingId,
              onNavigateToTracking = { bId ->
                repository.activeTrackingBookingId = bId
                navigateTo("track_service")
              },
              onBack = { navigateBack() }
            )
          }

          "payment" -> {
            PaymentScreen(
              repository = repository,
              bookingId = paymentBookingId ?: repository.bookings.first().id,
              onPaymentSuccess = { bId ->
                repository.activeTrackingBookingId = bId
                navigateTo("track_service")
              },
              onBack = { navigateBack() }
            )
          }

          "review" -> {
            ReviewScreen(
              repository = repository,
              bookingId = reviewBookingId ?: repository.bookings.first().id,
              professionalId = reviewProId ?: repository.professionals.first().id,
              onReviewSubmitted = {
                navigateTo("my_bookings")
              },
              onBack = { navigateBack() }
            )
          }

          "pro_dashboard" -> {
            ProDashboardScreen(
              repository = repository,
              onOpenChat = { bId ->
                repository.activeChatBookingId = bId
                navigateTo("chat")
              },
              onSwitchToCustomer = {
                repository.activeRole = "CUSTOMER"
                navigateTo("home")
              },
              onEditProfile = {
                navigateTo("profile_editor")
              }
            )
          }

          "admin_dashboard" -> {
            AdminDashboardScreen(
              repository = repository,
              onSwitchToCustomer = {
                repository.activeRole = "CUSTOMER"
                navigateTo("home")
              }
            )
          }

          "profile_editor" -> {
            ProfileEditorScreen(
              repository = repository,
              onBack = { navigateBack() }
            )
          }
        }
      }

      // Notification Drawer
      if (showNotifications) {
        NotificationSheet(
          repository = repository,
          onClose = { showNotifications = false },
          onNavigateToBooking = { bookingId ->
            repository.activeTrackingBookingId = bookingId
            navigateTo("track_service")
          },
          onNavigateToChat = { bookingId ->
            repository.activeChatBookingId = bookingId
            navigateTo("chat")
          }
        )
      }
    }
  }
}
