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
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
  repository: GigRepository,
  onTrackBooking: (String) -> Unit,
  onChatBooking: (String) -> Unit,
  onPayBooking: (String) -> Unit,
  onReviewBooking: (String, String) -> Unit,
  onNewBookingClick: () -> Unit
) {
  var selectedTab by remember { mutableStateOf(0) }
  val tabTitles = listOf("Active", "Upcoming", "Completed", "Cancelled", "All")

  val filteredBookings = repository.bookings.filter { b ->
    when (selectedTab) {
      0 -> b.status in listOf(BookingStatus.ON_THE_WAY, BookingStatus.ARRIVED, BookingStatus.SERVICE_STARTED)
      1 -> b.status in listOf(BookingStatus.REQUESTED, BookingStatus.ACCEPTED)
      2 -> b.status == BookingStatus.COMPLETED
      3 -> b.status == BookingStatus.CANCELLED
      else -> true
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .padding(bottom = 70.dp)
  ) {
    // Header
    Surface(
      color = Color.White,
      shadowElevation = 1.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("My Bookings", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Slate900)
            Text("Manage your household and community service appointments", fontSize = 12.sp, color = Slate500)
          }

          Button(
            onClick = onNewBookingClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(34.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("New Gig", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        ScrollableTabRow(
          selectedTabIndex = selectedTab,
          edgePadding = 0.dp,
          containerColor = Color.White,
          contentColor = BrandPrimary,
          divider = {}
        ) {
          tabTitles.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  text = title,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 13.sp
                )
              }
            )
          }
        }
      }
    }

    // List
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      if (filteredBookings.isEmpty()) {
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Slate400, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("No bookings in this tab", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate700)
            Text("Switch tabs or book a new service via our AI Assistant.", fontSize = 12.sp, color = Slate500)
          }
        }
      } else {
        items(filteredBookings) { booking ->
          BookingCard(
            booking = booking,
            onTrack = { onTrackBooking(booking.id) },
            onChat = { onChatBooking(booking.id) },
            onPay = { onPayBooking(booking.id) },
            onReview = { onReviewBooking(booking.id, booking.professionalId) },
            onCancel = {
              booking.status = BookingStatus.CANCELLED
            }
          )
        }
      }
    }
  }
}

@Composable
fun BookingCard(
  booking: Booking,
  onTrack: () -> Unit,
  onChat: () -> Unit,
  onPay: () -> Unit,
  onReview: () -> Unit,
  onCancel: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color.White,
    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
    shadowElevation = 1.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top line
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "#${booking.id}",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = BrandPrimary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("•", color = Slate400)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = booking.createdAt,
            fontSize = 11.sp,
            color = Slate500
          )
        }

        StatusBadge(status = booking.status)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Main Info
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Blue100),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Build, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(text = booking.serviceName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
          Text(text = "Pro: ${booking.professionalName}", fontSize = 12.sp, color = Slate600)
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(text = "₹${booking.totalAmount}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Slate900)
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (booking.paymentStatus == "Paid") Green100 else Amber100
          ) {
            Text(
              text = booking.paymentStatus,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = if (booking.paymentStatus == "Paid") Green600 else Color(0xFFB45309),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Problem preview
      Surface(shape = RoundedCornerShape(8.dp), color = Slate50, modifier = Modifier.fillMaxWidth()) {
        Text(
          text = booking.problemDescription,
          fontSize = 11.sp,
          color = Slate700,
          maxLines = 2,
          modifier = Modifier.padding(8.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Date & location
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Event, contentDescription = null, tint = Slate400, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("${booking.date} • ${booking.timeSlot}", fontSize = 11.sp, color = Slate600)
      }

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = Slate100, thickness = 0.5.dp)
      Spacer(modifier = Modifier.height(8.dp))

      // Actions Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Chat Button
        OutlinedButton(
          onClick = onChat,
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.height(32.dp)
        ) {
          Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Chat", fontSize = 11.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Track button if active
          if (booking.status in listOf(BookingStatus.ACCEPTED, BookingStatus.ON_THE_WAY, BookingStatus.ARRIVED, BookingStatus.SERVICE_STARTED)) {
            Button(
              onClick = onTrack,
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
              modifier = Modifier.height(32.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary)
            ) {
              Icon(Icons.Default.DirectionsBike, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Track Live", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          // Pay button if pending
          if (booking.paymentStatus == "Pending" && booking.status != BookingStatus.CANCELLED) {
            Button(
              onClick = onPay,
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
              modifier = Modifier.height(32.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
              Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Pay ₹${booking.totalAmount}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          // Rate & review if completed
          if (booking.status == BookingStatus.COMPLETED) {
            Button(
              onClick = onReview,
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
              modifier = Modifier.height(32.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
            ) {
              Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          // Cancel option if requested
          if (booking.status == BookingStatus.REQUESTED) {
            TextButton(
              onClick = onCancel,
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Text("Cancel", fontSize = 11.sp, color = BrandEmergency)
            }
          }
        }
      }
    }
  }
}
