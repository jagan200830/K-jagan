package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GigRepository
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.ui.components.RatingDisplay
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProDashboardScreen(
  repository: GigRepository,
  onOpenChat: (String) -> Unit,
  onSwitchToCustomer: () -> Unit,
  onEditProfile: () -> Unit = {}
) {
  val loggedInPro = repository.professionals.firstOrNull { it.id == "pro_ramesh" } ?: repository.professionals.first()
  val proBookings = repository.bookings.filter { it.professionalId == loggedInPro.id || it.serviceId == "plumbing" }

  var selectedTab by remember { mutableStateOf(0) }
  val tabs = listOf("Active & Pending", "Earnings & Payouts", "Customer Reviews")

  Scaffold(
    topBar = {
      Surface(
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(Color(loggedInPro.initialAvatarColor)),
                contentAlignment = Alignment.Center
              ) {
                Text(loggedInPro.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(loggedInPro.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(Icons.Default.Verified, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(15.dp))
                }
                Text("Cooperative Member ID: KRN-8821 • ${loggedInPro.serviceCategory}", fontSize = 11.sp, color = Slate500)
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              OutlinedButton(
                onClick = onEditProfile,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(32.dp)
              ) {
                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = BrandSecondary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Hours & Area", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandSecondary)
              }
              Spacer(modifier = Modifier.width(4.dp))
              TextButton(onClick = onSwitchToCustomer) {
                Text("Customer Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 4 metric cards
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            MetricPill("Today's Earnings", "₹1,850", Green600, Modifier.weight(1f))
            MetricPill("Rating", "★ 4.9", Color(0xFFD97706), Modifier.weight(1f))
            MetricPill("Jobs Completed", "186", BrandPrimary, Modifier.weight(1f))
            MetricPill("Co-op Dividend", "₹4,200", BrandSecondary, Modifier.weight(1f))
          }

          Spacer(modifier = Modifier.height(10.dp))

          TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = BrandPrimary,
            divider = {}
          ) {
            tabs.forEachIndexed { idx, title ->
              Tab(
                selected = selectedTab == idx,
                onClick = { selectedTab = idx },
                text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal) }
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(Slate50)
        .padding(innerPadding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      if (selectedTab == 0) {
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Schedule, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Your Schedule & Service Area", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                }
                TextButton(
                  onClick = onEditProfile,
                  contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("Manage Hours & Area", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandSecondary)
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text("⏰ Hours: ${loggedInPro.workingHours}", fontSize = 12.sp, color = Slate700)
              Text("📍 Service Area: ${loggedInPro.serviceArea}", fontSize = 12.sp, color = Slate700)
              Text(
                "⚡ Status: ${if (loggedInPro.isAvailableNow) "Active for Emergency Jobs" else "Scheduled Appointments Only"}",
                fontSize = 11.sp,
                color = if (loggedInPro.isAvailableNow) Green600 else Slate500
              )
            }
          }
        }

        item {
          Text("Assigned & Pending Job Requests", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
        }

        if (proBookings.isEmpty()) {
          item {
            Text("No active jobs assigned right now.", fontSize = 13.sp, color = Slate500)
          }
        } else {
          items(proBookings) { booking ->
            ProJobActionCard(
              booking = booking,
              onUpdateStatus = { nextStatus ->
                booking.status = nextStatus
                if (nextStatus == BookingStatus.ARRIVED) {
                  booking.estimatedArrivalMinutes = 0
                  booking.professionalDistanceKm = 0.0
                }
              },
              onChat = { onOpenChat(booking.id) }
            )
          }
        }
      } else if (selectedTab == 1) {
        // EARNINGS
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Cooperative Transparent Earnings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
              Text("Unlike other platforms that deduct 30%, our cooperative retains only 5% for platform operations and returns annual profit dividends.", fontSize = 12.sp, color = Slate600)

              Spacer(modifier = Modifier.height(14.dp))
              Divider(color = Slate100, thickness = 1.dp)
              Spacer(modifier = Modifier.height(14.dp))

              ProEarningLine("Total Gross Services This Month", "₹38,400")
              ProEarningLine("Direct Customer Payouts Received", "₹36,480")
              ProEarningLine("Cooperative Maintenance Reserve (5%)", "₹1,920")
              ProEarningLine("Annual Member Profit Share Accrued", "+₹4,200", isGreen = true)
            }
          }
        }
      } else {
        // CUSTOMER REVIEWS
        item {
          Text("Customer Feedback for Ramesh Kumar", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
        }

        items(repository.getReviewsForProfessional(loggedInPro.id)) { rev ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(rev.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                RatingDisplay(rating = rev.rating.toDouble())
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(rev.comment, fontSize = 12.sp, color = Slate700)
              Spacer(modifier = Modifier.height(4.dp))
              Text("${rev.date} • Verified Service Job", fontSize = 10.sp, color = Slate400)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MetricPill(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = Slate100,
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(title, fontSize = 9.sp, color = Slate500, maxLines = 1)
      Spacer(modifier = Modifier.height(2.dp))
      Text(value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
    }
  }
}

@Composable
private fun ProJobActionCard(
  booking: Booking,
  onUpdateStatus: (BookingStatus) -> Unit,
  onChat: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color.White,
    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Booking #${booking.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandPrimary)
        StatusBadge(status = booking.status)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text("Customer: ${booking.customerName} (${booking.customerPhone})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Slate900)
      Text("Problem: ${booking.problemDescription}", fontSize = 12.sp, color = Slate600)
      Text("Location: ${booking.address}", fontSize = 11.sp, color = Slate500)

      Spacer(modifier = Modifier.height(6.dp))
      Text("Estimated Worker Payout: ₹${booking.totalAmount - 29}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Green600)

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = Slate100, thickness = 0.5.dp)
      Spacer(modifier = Modifier.height(8.dp))

      // Status Advance Controls for Professional
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onChat,
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.height(32.dp)
        ) {
          Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Chat Customer", fontSize = 11.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          when (booking.status) {
            BookingStatus.REQUESTED -> {
              Button(
                onClick = { onUpdateStatus(BookingStatus.ACCEPTED) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                modifier = Modifier.height(32.dp)
              ) {
                Text("Accept Job", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
            BookingStatus.ACCEPTED -> {
              Button(
                onClick = { onUpdateStatus(BookingStatus.ON_THE_WAY) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                modifier = Modifier.height(32.dp)
              ) {
                Text("Start Travel (On Way)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
            BookingStatus.ON_THE_WAY -> {
              Button(
                onClick = { onUpdateStatus(BookingStatus.ARRIVED) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                modifier = Modifier.height(32.dp)
              ) {
                Text("Mark Arrived", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
            BookingStatus.ARRIVED -> {
              Button(
                onClick = { onUpdateStatus(BookingStatus.SERVICE_STARTED) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                modifier = Modifier.height(32.dp)
              ) {
                Text("Start Repair", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
            BookingStatus.SERVICE_STARTED -> {
              Button(
                onClick = { onUpdateStatus(BookingStatus.COMPLETED) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green600),
                modifier = Modifier.height(32.dp)
              ) {
                Text("Mark Completed", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
            BookingStatus.COMPLETED -> {
              Surface(shape = RoundedCornerShape(4.dp), color = Green100) {
                Text("Job Completed & Paid", fontSize = 11.sp, color = Green600, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
              }
            }
            else -> {}
          }
        }
      }
    }
  }
}

@Composable
private fun ProEarningLine(label: String, amount: String, isGreen: Boolean = false) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, fontSize = 12.sp, color = Slate600)
    Text(amount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isGreen) Green600 else Slate900)
  }
}
