package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.Professional
import com.example.model.ServiceCategory
import com.example.ui.components.RatingDisplay
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
  repository: GigRepository,
  preselectedServiceId: String? = null,
  preselectedProId: String? = null,
  prefilledProblem: String? = null,
  onBookingConfirmed: (String) -> Unit,
  onBack: () -> Unit
) {
  var currentStep by remember { mutableStateOf(if (preselectedProId != null) 4 else 1) }

  var selectedServiceId by remember { mutableStateOf(preselectedServiceId ?: "plumbing") }
  var problemDescription by remember { mutableStateOf(prefilledProblem ?: "Kitchen sink drain pipe burst and water leaking onto floor") }
  var selectedProId by remember { mutableStateOf(preselectedProId ?: repository.professionals.firstOrNull { it.city == repository.selectedCity }?.id ?: "pro_ramesh") }
  var selectedDate by remember { mutableStateOf("Today") }
  var selectedTimeSlot by remember { mutableStateOf("Immediate (Emergency within 30 mins)") }
  var serviceAddress by remember { mutableStateOf(repository.activeUserAddress) }
  var payOnlineNow by remember { mutableStateOf(true) }

  val selectedService = repository.getServiceById(selectedServiceId) ?: repository.serviceCategories.first()
  val selectedPro = repository.getProfessionalById(selectedProId) ?: repository.professionals.first()

  val baseCost = selectedPro.startingPrice
  val platformFee = 29
  val taxes = (baseCost * 0.18).toInt()
  val discount = 50
  val total = baseCost + platformFee + taxes - discount

  val dateOptions = listOf("Today", "Tomorrow", "In 2 Days", "This Weekend")
  val timeOptions = listOf(
    "Immediate (Emergency within 30 mins)",
    "Morning (9:00 AM - 12:00 PM)",
    "Afternoon (1:00 PM - 4:00 PM)",
    "Evening (5:00 PM - 8:00 PM)"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Book Service", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Step $currentStep of 6 • Cooperative Booking", fontSize = 11.sp, color = Slate500)
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
          if (currentStep > 1) {
            OutlinedButton(
              onClick = { currentStep-- },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.height(44.dp)
            ) {
              Text("Back")
            }
          } else {
            Spacer(modifier = Modifier.width(1.dp))
          }

          if (currentStep < 6) {
            Button(
              onClick = { currentStep++ },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.height(44.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
              Text("Continue to Step ${currentStep + 1}", fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(6.dp))
              Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
          } else {
            Button(
              onClick = {
                val newBooking = repository.createBooking(
                  serviceId = selectedService.id,
                  professionalId = selectedPro.id,
                  problemDescription = problemDescription,
                  date = selectedDate,
                  timeSlot = selectedTimeSlot,
                  address = serviceAddress,
                  payNow = payOnlineNow
                )
                onBookingConfirmed(newBooking.id)
              },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.height(46.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Confirm Booking (₹$total)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Step Indicators
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          (1..6).forEach { step ->
            val isDone = step < currentStep
            val isCurrent = step == currentStep
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                  when {
                    isCurrent -> BrandPrimary
                    isDone -> BrandSecondary
                    else -> Slate200
                  }
                ),
              contentAlignment = Alignment.Center
            ) {
              if (isDone) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
              } else {
                Text(
                  text = "$step",
                  color = if (isCurrent) Color.White else Slate600,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }

      // STEP 1: SELECT SERVICE
      if (currentStep == 1) {
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("STEP 1: Select Service", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
              Text("Pick which category your requirement falls under", fontSize = 11.sp, color = Slate500)
              Spacer(modifier = Modifier.height(12.dp))

              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repository.serviceCategories.forEach { s ->
                  val isSelected = s.id == selectedServiceId
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Blue100 else Slate50,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BrandPrimary) else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { selectedServiceId = s.id }
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      RadioButton(
                        selected = isSelected,
                        onClick = { selectedServiceId = s.id }
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Column(modifier = Modifier.weight(1f)) {
                        Text(s.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                        Text(s.shortDescription, fontSize = 11.sp, color = Slate600)
                      }
                      Text("₹${s.startingPrice}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = BrandPrimary)
                    }
                  }
                }
              }
            }
          }
        }
      }

      // STEP 2: DESCRIBE PROBLEM
      if (currentStep == 2) {
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("STEP 2: Describe Problem", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
              Text("Provide details so the professional brings right parts and tools", fontSize = 11.sp, color = Slate500)
              Spacer(modifier = Modifier.height(12.dp))

              OutlinedTextField(
                value = problemDescription,
                onValueChange = { problemDescription = it },
                label = { Text("Problem details") },
                placeholder = { Text("Describe the issue, leak location, broken part, or specific help needed...") },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(130.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BrandPrimary,
                  unfocusedBorderColor = Slate300
                )
              )

              Spacer(modifier = Modifier.height(12.dp))
              Text("Common tags:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate600)
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(selectedService.problemsHandled) { tag ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate100,
                    modifier = Modifier.clickable {
                      problemDescription = "$problemDescription. $tag"
                    }
                  ) {
                    Text("+ $tag", fontSize = 10.sp, color = Slate700, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                  }
                }
              }
            }
          }
        }
      }

      // STEP 3: SELECT PROFESSIONAL
      if (currentStep == 3) {
        val availablePros = repository.getFilteredProfessionals(categoryId = selectedService.id, city = repository.selectedCity)
          .ifEmpty { repository.getFilteredProfessionals(city = repository.selectedCity) }

        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("STEP 3: Select Service Professional", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
              Text("Choose from verified cooperative members nearby in ${repository.selectedCity}", fontSize = 11.sp, color = Slate500)
              Spacer(modifier = Modifier.height(12.dp))

              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availablePros.forEach { pro ->
                  val isSelected = pro.id == selectedProId
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Blue100 else Slate50,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BrandPrimary) else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { selectedProId = pro.id }
                  ) {
                    Row(
                      modifier = Modifier.padding(12.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      RadioButton(
                        selected = isSelected,
                        onClick = { selectedProId = pro.id }
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Box(
                        modifier = Modifier
                          .size(40.dp)
                          .clip(CircleShape)
                          .background(Color(pro.initialAvatarColor)),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(pro.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                      }
                      Spacer(modifier = Modifier.width(10.dp))
                      Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(pro.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                          Spacer(modifier = Modifier.width(4.dp))
                          Icon(Icons.Default.Verified, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(13.dp))
                        }
                        Text("${pro.distanceKm} km away • ${pro.experienceYears}y exp", fontSize = 11.sp, color = Slate500)
                        RatingDisplay(rating = pro.rating, reviewCount = pro.reviewCount)
                      }
                      Column(horizontalAlignment = Alignment.End) {
                        Text("₹${pro.startingPrice}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = BrandPrimary)
                        Text("visit fee", fontSize = 9.sp, color = Slate400)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      // STEP 4: DATE & TIME
      if (currentStep == 4) {
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("STEP 4: Choose Date & Time", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
              Text("Select when you want the professional to arrive", fontSize = 11.sp, color = Slate500)
              Spacer(modifier = Modifier.height(14.dp))

              Text("Date:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate800)
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(dateOptions) { d ->
                  val isSelected = selectedDate == d
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) BrandPrimary else Slate100,
                    modifier = Modifier.clickable { selectedDate = d }
                  ) {
                    Text(
                      text = d,
                      fontSize = 12.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) Color.White else Slate800,
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              Text("Time Slot:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate800)
              Spacer(modifier = Modifier.height(6.dp))
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                timeOptions.forEach { t ->
                  val isSelected = selectedTimeSlot == t
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Blue100 else Slate50,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, BrandPrimary) else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { selectedTimeSlot = t }
                  ) {
                    Row(
                      modifier = Modifier.padding(12.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      RadioButton(selected = isSelected, onClick = { selectedTimeSlot = t })
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = t,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Slate800
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // STEP 5: SERVICE ADDRESS
      if (currentStep == 5) {
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("STEP 5: Enter Service Address", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
              Text("Where should the cooperative professional visit?", fontSize = 11.sp, color = Slate500)
              Spacer(modifier = Modifier.height(12.dp))

              OutlinedTextField(
                value = serviceAddress,
                onValueChange = { serviceAddress = it },
                label = { Text("Complete Street Address") },
                placeholder = { Text("Flat/House No, Building, Landmark, Area") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BrandPrimary,
                  unfocusedBorderColor = Slate300
                )
              )

              Spacer(modifier = Modifier.height(12.dp))

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Slate100,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.MyLocation, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "City: ${repository.selectedCity} (Assigned from cooperative district pool)",
                    fontSize = 11.sp,
                    color = Slate700,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }

      // STEP 6: REVIEW BOOKING & COST BREAKDOWN
      if (currentStep == 6) {
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("STEP 6: Review & Confirmation", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
              Text("Please verify your booking details before confirming", fontSize = 11.sp, color = Slate500)

              Spacer(modifier = Modifier.height(14.dp))

              // Overview box
              Surface(shape = RoundedCornerShape(10.dp), color = Slate50, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  ReviewLine(label = "Service", value = selectedService.name)
                  ReviewLine(label = "Professional", value = "${selectedPro.name} (★${selectedPro.rating})")
                  ReviewLine(label = "Date & Time", value = "$selectedDate • $selectedTimeSlot")
                  ReviewLine(label = "Address", value = serviceAddress)
                  ReviewLine(label = "Problem", value = problemDescription)
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              // Cost Breakdown
              Text("Fair Price Breakdown", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
              Spacer(modifier = Modifier.height(8.dp))

              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                CostLine(label = "Standard Service / Visit Charge", amount = "₹$baseCost")
                CostLine(label = "Cooperative Platform Fee", amount = "₹$platformFee")
                CostLine(label = "Taxes (GST 18%)", amount = "₹$taxes")
                CostLine(label = "Community First-Booking Discount", amount = "-₹$discount", isDiscount = true)
                Divider(color = Slate200, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Total Payable", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Slate900)
                  Text("₹$total", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = BrandPrimary)
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Payment Choice
              Text("Payment Option:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate800)
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (payOnlineNow) Blue100 else Slate100,
                  border = if (payOnlineNow) androidx.compose.foundation.BorderStroke(1.5.dp, BrandPrimary) else null,
                  modifier = Modifier
                    .weight(1f)
                    .clickable { payOnlineNow = true }
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text("Instant UPI / Card", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (payOnlineNow) BrandPrimary else Slate800)
                    Text("Pay now online", fontSize = 9.sp, color = Slate500)
                  }
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (!payOnlineNow) Blue100 else Slate100,
                  border = if (!payOnlineNow) androidx.compose.foundation.BorderStroke(1.5.dp, BrandPrimary) else null,
                  modifier = Modifier
                    .weight(1f)
                    .clickable { payOnlineNow = false }
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text("Pay After Service", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (!payOnlineNow) BrandPrimary else Slate800)
                    Text("Cash / UPI upon completion", fontSize = 9.sp, color = Slate500)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ReviewLine(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    Text(text = label, fontSize = 11.sp, color = Slate500, modifier = Modifier.width(90.dp))
    Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate800, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
  }
}

@Composable
private fun CostLine(label: String, amount: String, isDiscount: Boolean = false) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(text = label, fontSize = 12.sp, color = if (isDiscount) BrandSecondary else Slate600)
    Text(text = amount, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (isDiscount) BrandSecondary else Slate900)
  }
}
