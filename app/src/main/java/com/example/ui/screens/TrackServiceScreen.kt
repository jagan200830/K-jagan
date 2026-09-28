package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.BookingStatus
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackServiceScreen(
  repository: GigRepository,
  bookingId: String? = repository.activeTrackingBookingId,
  onNavigateToChat: (String) -> Unit,
  onServiceCompleted: (String, String) -> Unit,
  onBack: () -> Unit
) {
  val booking = repository.bookings.find { it.id == bookingId } ?: repository.bookings.firstOrNull()
  val pro = booking?.let { repository.getProfessionalById(it.professionalId) }

  var showCallDialog by remember { mutableStateOf(false) }
  var showSosDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Track Service Professional", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "Booking #${booking?.id ?: "N/A"}", fontSize = 11.sp, color = Slate500)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
      )
    },
    bottomBar = {
      Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Call button
          OutlinedButton(
            onClick = { showCallDialog = true },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(44.dp)
          ) {
            Icon(Icons.Default.Call, contentDescription = null, tint = Green600, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Call Pro", color = Green600, fontWeight = FontWeight.Bold)
          }

          // Message Chat button
          Button(
            onClick = {
              if (booking != null) {
                onNavigateToChat(booking.id)
              }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
          ) {
            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Direct Chat", fontWeight = FontWeight.Bold)
          }

          // SOS
          IconButton(
            onClick = { showSosDialog = true },
            modifier = Modifier.size(44.dp)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Red100),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Shield, contentDescription = "SOS", tint = BrandEmergency, modifier = Modifier.size(20.dp))
            }
          }
        }
      }
    }
  ) { innerPadding ->
    if (booking == null) {
      Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        Text("No active service tracking found.")
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .background(Slate50)
          .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // MAP SIMULATION
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFE2E8F0),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate300),
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp)
          ) {
            Box(modifier = Modifier.fillMaxSize()) {
              // Simulated roads
              Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround
              ) {
                repeat(5) {
                  Divider(color = Color.White.copy(alpha = 0.8f), thickness = 3.dp)
                }
              }

              // Customer Destination Marker
              Box(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .padding(end = 24.dp, bottom = 24.dp)
                  .clip(CircleShape)
                  .background(BrandPrimary)
                  .padding(8.dp)
              ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
              }

              // Professional Moving Marker
              val proAlignment = when (booking.status) {
                BookingStatus.REQUESTED, BookingStatus.ACCEPTED -> Alignment.TopStart
                BookingStatus.ON_THE_WAY -> Alignment.Center
                BookingStatus.ARRIVED, BookingStatus.SERVICE_STARTED, BookingStatus.COMPLETED -> Alignment.BottomEnd
                else -> Alignment.Center
              }

              Box(
                modifier = Modifier
                  .align(proAlignment)
                  .padding(24.dp)
                  .clip(CircleShape)
                  .background(BrandSecondary)
                  .padding(8.dp)
              ) {
                Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
              }

              // Live ETA Floating Tag
              Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                modifier = Modifier
                  .align(Alignment.TopCenter)
                  .padding(top = 12.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.AccessTime, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = when (booking.status) {
                      BookingStatus.COMPLETED -> "Service Completed"
                      BookingStatus.SERVICE_STARTED -> "Working on site"
                      BookingStatus.ARRIVED -> "Arrived at doorstep"
                      else -> "Arriving in ~${booking.estimatedArrivalMinutes} mins (${booking.professionalDistanceKm} km away)"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Slate900
                  )
                }
              }

              // Simulation trigger button on map
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Slate900.copy(alpha = 0.85f),
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(10.dp)
                  .clickable {
                    repository.advanceTrackingStatus(booking.id)
                    if (booking.status == BookingStatus.COMPLETED) {
                      onServiceCompleted(booking.id, booking.professionalId)
                    }
                  }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.FastForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Advance Demo Status", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        // PROFESSIONAL PROFILE MINI-CARD
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(Color(pro?.initialAvatarColor ?: 0xFF1E40AF)),
                contentAlignment = Alignment.Center
              ) {
                Text(booking.professionalName.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(booking.professionalName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(Icons.Default.Verified, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(15.dp))
                }
                Text("${booking.serviceName} • Cooperative Member", fontSize = 11.sp, color = Slate600)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Vehicle: TVS Jupiter (KA-05-EX-4491)", fontSize = 10.sp, color = Slate400)
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Blue100
              ) {
                Column(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text("OTP", fontSize = 9.sp, color = BrandPrimary, fontWeight = FontWeight.SemiBold)
                  Text("4821", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = BrandPrimary)
                }
              }
            }
          }
        }

        // TIMELINE STATUS
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Service Timeline", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
              Spacer(modifier = Modifier.height(14.dp))

              val steps = listOf(
                BookingStatus.REQUESTED to "Booking Confirmed",
                BookingStatus.ACCEPTED to "Professional Accepted",
                BookingStatus.ON_THE_WAY to "Professional On the Way",
                BookingStatus.ARRIVED to "Arrived at Doorstep",
                BookingStatus.SERVICE_STARTED to "Service Started",
                BookingStatus.COMPLETED to "Service Completed"
              )

              val currentIdx = steps.indexOfFirst { it.first == booking.status }
              val activeIndex = if (currentIdx >= 0) currentIdx else 1

              Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                steps.forEachIndexed { index, (status, label) ->
                  val isDone = index <= activeIndex
                  val isCurrent = index == activeIndex

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                          when {
                            isCurrent -> BrandSecondary
                            isDone -> Green600
                            else -> Slate200
                          }
                        ),
                      contentAlignment = Alignment.Center
                    ) {
                      if (isDone && !isCurrent) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                      } else if (isCurrent) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                      }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                      Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isDone) Slate900 else Slate400
                      )
                      if (isCurrent) {
                        Text(
                          text = when (status) {
                            BookingStatus.ON_THE_WAY -> "Moving on GPS • Arriving in ~${booking.estimatedArrivalMinutes}m"
                            BookingStatus.ARRIVED -> "Verified at main gate"
                            BookingStatus.SERVICE_STARTED -> "Repair work in progress"
                            BookingStatus.COMPLETED -> "Payment received • Ready for review"
                            else -> "In progress"
                          },
                          fontSize = 11.sp,
                          color = BrandSecondary,
                          fontWeight = FontWeight.SemiBold
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // SERVICE ADDRESS CARD
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("Service Location", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
              Spacer(modifier = Modifier.height(6.dp))
              Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Place, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(booking.address, fontSize = 12.sp, color = Slate700)
              }
            }
          }
        }
      }
    }
  }

  // Call Dialog
  if (showCallDialog) {
    AlertDialog(
      onDismissRequest = { showCallDialog = false },
      title = { Text("Call ${booking?.professionalName}") },
      text = {
        Text("Cooperative masked dialer connects you to ${pro?.phone ?: "+91 98450 21345"} with zero call charges.")
      },
      confirmButton = {
        Button(onClick = { showCallDialog = false }) {
          Text("Dial")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCallDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // SOS Emergency Dialog
  if (showSosDialog) {
    AlertDialog(
      onDismissRequest = { showSosDialog = false },
      title = { Text("Cooperative Safety Assistance") },
      text = {
        Text("Do you require immediate assistance or want to report an issue to our 24x7 Karnataka Cooperative Safety Ombudsman?")
      },
      confirmButton = {
        Button(
          onClick = { showSosDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = BrandEmergency)
        ) {
          Text("Contact Emergency Ombudsman")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSosDialog = false }) {
          Text("Dismiss")
        }
      }
    )
  }
}
