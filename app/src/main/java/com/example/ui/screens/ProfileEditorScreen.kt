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
import androidx.compose.material.icons.outlined.*
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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileEditorScreen(
  repository: GigRepository,
  onBack: () -> Unit
) {
  var isProMode by remember { mutableStateOf(repository.activeRole == "PROFESSIONAL") }

  // Customer profile state
  var customerName by remember { mutableStateOf(repository.activeUserName) }
  var customerPhone by remember { mutableStateOf(repository.activeUserPhone) }
  var customerAddress by remember { mutableStateOf(repository.activeUserAddress) }
  var selectedAddressTag by remember { mutableStateOf("Home") }
  var emergencyContact by remember { mutableStateOf("+91 00000 00009") }
  var specialInstructions by remember { mutableStateOf("Ring bell twice, gate passcode 1928") }

  // Service professional profile state
  val proList = repository.professionals
  var selectedProId by remember { mutableStateOf("pro_ramesh") }
  val activePro = proList.find { it.id == selectedProId } ?: proList.first()

  var proWorkingHours by remember(activePro.id) { mutableStateOf(activePro.workingHours) }
  var proServiceArea by remember(activePro.id) { mutableStateOf(activePro.serviceArea) }
  var proStartingPrice by remember(activePro.id) { mutableStateOf(activePro.startingPrice.toString()) }
  var proIsAvailable by remember(activePro.id) { mutableStateOf(activePro.isAvailableNow) }
  var proRadiusKm by remember { mutableStateOf(15f) }
  var proSkills by remember(activePro.id) { mutableStateOf(activePro.skills.toMutableList()) }
  var newSkillInput by remember { mutableStateOf("") }

  var showSuccessDialog by remember { mutableStateOf(false) }
  var saveSuccessMessage by remember { mutableStateOf("") }

  // Working day selector state
  val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
  var selectedDays by remember { mutableStateOf(setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")) }

  val presetHours = listOf(
    "24x7 Emergency Service",
    "7:00 AM - 9:00 PM (All Days)",
    "8:00 AM - 7:00 PM (Mon - Sat)",
    "9:00 AM - 6:00 PM (Weekdays)",
    "Weekend Only (8:00 AM - 8:00 PM)"
  )

  val cityLocalities = when (repository.selectedCity) {
    "Ballari" -> listOf("Gandhi Nagar", "Brucepet", "Cowl Bazaar", "Shastri Nagar", "Kolagal Road", "Cantonment")
    "Mysuru" -> listOf("Gokulam", "Vijayanagar", "Kuvempunagar", "Saraswathipuram", "Jayalakshmipuram", "Hebbal")
    "Tumakuru" -> listOf("SS Puram", "Ashok Nagar", "Kyatsandra", "Batawadi", "SIT Extension", "Mallasandra")
    else -> listOf("Koramangala", "HSR Layout", "Indiranagar", "BTM Layout", "Jayanagar", "Whitefield", "Bellandur", "JP Nagar", "Electronic City", "Malleshwaram")
  }

  val addressTags = listOf("Home", "Work / Office", "Rental Unit", "Parent's Home")

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Profile & Settings Editor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
              text = if (isProMode) "Manage working hours & service area" else "Manage personal details & service address",
              fontSize = 11.sp,
              color = Slate500
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          // Quick Role Toggle pill in top bar
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isProMode) Amber100 else Blue100,
            modifier = Modifier
              .padding(end = 12.dp)
              .clickable { isProMode = !isProMode }
          ) {
            Text(
              text = if (isProMode) "Switch to Customer" else "Switch to Pro View",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isProMode) Color(0xFFB45309) else BrandPrimary,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
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
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).height(46.dp)
          ) {
            Text("Cancel", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }

          Button(
            onClick = {
              if (isProMode) {
                // Update pro in repository
                val price = proStartingPrice.toIntOrNull() ?: activePro.startingPrice
                repository.updateProfessionalProfile(
                  proId = activePro.id,
                  workingHours = proWorkingHours,
                  serviceArea = proServiceArea,
                  isAvailable = proIsAvailable,
                  startingPrice = price,
                  skills = proSkills
                )
                saveSuccessMessage = "Service professional profile for ${activePro.name} updated successfully! Active working hours set to '$proWorkingHours' covering $proServiceArea."
              } else {
                // Update customer in repository
                repository.updateCustomerProfile(
                  name = customerName.ifBlank { "Aditi Sharma" },
                  phone = customerPhone.ifBlank { "+91 00000 00000" },
                  address = customerAddress.ifBlank { "#42, 3rd Cross, 7th Main, Koramangala 4th Block, ${repository.selectedCity}" }
                )
                saveSuccessMessage = "Customer profile and primary service address saved successfully! Your default booking details have been refreshed."
              }
              showSuccessDialog = true
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(2f).height(46.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isProMode) BrandSecondary else BrandPrimary
            )
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              if (isProMode) "Save Pro Settings" else "Save Customer Profile",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
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
      // Perspective toggle pill
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color.White,
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (!isProMode) BrandPrimary else Color.Transparent,
              modifier = Modifier
                .weight(1f)
                .clickable { isProMode = false }
            ) {
              Row(
                modifier = Modifier
                  .padding(vertical = 10.dp)
                  .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.Person,
                  contentDescription = null,
                  tint = if (!isProMode) Color.White else Slate600,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Customer Profile",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (!isProMode) Color.White else Slate700
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isProMode) BrandSecondary else Color.Transparent,
              modifier = Modifier
                .weight(1f)
                .clickable { isProMode = true }
            ) {
              Row(
                modifier = Modifier
                  .padding(vertical = 10.dp)
                  .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.Engineering,
                  contentDescription = null,
                  tint = if (isProMode) Color.White else Slate600,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Service Pro Mode",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isProMode) Color.White else Slate700
                )
              }
            }
          }
        }
      }

      // ==============================================================
      // 1. CUSTOMER PROFILE EDITOR
      // ==============================================================
      if (!isProMode) {
        // Customer Header Card
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = customerName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(customerName, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Slate900)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Teal100)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text("VERIFIED MEMBER", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = BrandSecondary)
                    }
                  }
                  Text("Cooperative Consumer Member • ${repository.selectedCity}", fontSize = 12.sp, color = Slate500)
                }
              }

              Divider(color = Slate100, thickness = 1.dp)

              Text(
                "Personal & Contact Information",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Slate900
              )

              // Name input
              OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text("Full Name") },
                placeholder = { Text("e.g. Aditi Sharma") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BrandPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BrandPrimary,
                  focusedLabelColor = BrandPrimary
                )
              )

              // Phone input
              OutlinedTextField(
                value = customerPhone,
                onValueChange = { customerPhone = it },
                label = { Text("Mobile Phone Number") },
                placeholder = { Text("+91 00000 00000") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BrandPrimary) },
                supportingText = {
                  Text("Cooperative masked routing enabled for privacy during service calls", fontSize = 10.sp, color = Slate500)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BrandPrimary,
                  focusedLabelColor = BrandPrimary
                )
              )

              // Emergency Contact
              OutlinedTextField(
                value = emergencyContact,
                onValueChange = { emergencyContact = it },
                label = { Text("Family Emergency Contact Number") },
                placeholder = { Text("+91 00000 00009") },
                leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = Green600) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BrandPrimary,
                  focusedLabelColor = BrandPrimary
                )
              )
            }
          }
        }

        // Service Address Card
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("Service Address Management", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                  Text("Used as the default arrival destination for gig requests", fontSize = 11.sp, color = Slate500)
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Blue100)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(repository.selectedCity, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                }
              }

              // Address tag selector
              Text("Address Type:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(addressTags) { tag ->
                  val isSelected = selectedAddressTag == tag
                  Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) BrandPrimary else Slate100,
                    modifier = Modifier.clickable { selectedAddressTag = tag }
                  ) {
                    Text(
                      text = tag,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) Color.White else Slate700,
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                  }
                }
              }

              // Address Input
              OutlinedTextField(
                value = customerAddress,
                onValueChange = { customerAddress = it },
                label = { Text("Primary Service Address") },
                placeholder = { Text("Flat/House No., Building, Street, Area, Landmark") },
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = BrandPrimary) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(110.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BrandPrimary,
                  focusedLabelColor = BrandPrimary
                )
              )

              // Quick Locality Chips
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                  "Quick Localities in ${repository.selectedCity} (tap to append):",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Slate600
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  items(cityLocalities) { locality ->
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = Slate100,
                      border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                      modifier = Modifier.clickable {
                        if (!customerAddress.contains(locality)) {
                          customerAddress = if (customerAddress.isBlank()) locality else "$customerAddress, $locality"
                        }
                      }
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Icon(Icons.Default.AddLocation, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(locality, fontSize = 11.sp, color = Slate700)
                      }
                    }
                  }
                }
              }

              Divider(color = Slate100, thickness = 1.dp)

              // Delivery / Gate Instructions
              OutlinedTextField(
                value = specialInstructions,
                onValueChange = { specialInstructions = it },
                label = { Text("Service Entry Notes & Landmarks") },
                placeholder = { Text("e.g. Ring bell twice, 4th floor, lift available") },
                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = Slate400) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BrandPrimary,
                  focusedLabelColor = BrandPrimary
                )
              )
            }
          }
        }
      }

      // ==============================================================
      // 2. SERVICE PROFESSIONAL PROFILE & WORKING HOURS EDITOR
      // ==============================================================
      if (isProMode) {
        // Professional Switcher (allows editing Ramesh or other registered pros)
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text("Managing Service Professional Profile:", fontSize = 11.sp, color = Slate500, fontWeight = FontWeight.Bold)

              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(proList.take(6)) { p ->
                  val isSelected = p.id == selectedProId
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Teal100 else Slate100,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, BrandSecondary) else null,
                    modifier = Modifier.clickable {
                      selectedProId = p.id
                    }
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Box(
                        modifier = Modifier
                          .size(24.dp)
                          .clip(CircleShape)
                          .background(Color(p.initialAvatarColor)),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(p.name.take(1), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                      }
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text(
                          p.name,
                          fontSize = 11.sp,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                          color = if (isSelected) BrandSecondary else Slate800
                        )
                        Text(p.serviceCategory, fontSize = 9.sp, color = Slate500)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // Active Pro Information Header
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(activePro.initialAvatarColor)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(activePro.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(activePro.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Slate900)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Verified, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                  }
                  Text(
                    "${activePro.serviceCategory} Specialist • ${activePro.experienceYears} yrs exp • ★ ${activePro.rating} (${activePro.completedJobs} jobs)",
                    fontSize = 11.sp,
                    color = Slate600
                  )
                }
              }

              Divider(color = Slate100, thickness = 1.dp)

              // Instant Availability Switch
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (proIsAvailable) Green50 else Slate100)
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(32.dp)
                      .clip(CircleShape)
                      .background(if (proIsAvailable) Green600 else Slate400),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      if (proIsAvailable) Icons.Default.Bolt else Icons.Default.Schedule,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = if (proIsAvailable) "Available Now (Emergency Active)" else "Off-Duty / Scheduled Only",
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = if (proIsAvailable) Green800 else Slate700
                    )
                    Text(
                      text = if (proIsAvailable) "Appearing in instant 20-min emergency bookings" else "Only future booked appointments will be routed",
                      fontSize = 10.sp,
                      color = if (proIsAvailable) Green600 else Slate500
                    )
                  }
                }
                Switch(
                  checked = proIsAvailable,
                  onCheckedChange = { proIsAvailable = it }
                )
              }
            }
          }
        }

        // WORKING HOURS MANAGEMENT CARD
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text("Working Hours Management", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                  Text("Set when customers can schedule and book your services", fontSize = 11.sp, color = Slate500)
                }
              }

              // Active Schedule Input
              OutlinedTextField(
                value = proWorkingHours,
                onValueChange = { proWorkingHours = it },
                label = { Text("Display Working Hours") },
                placeholder = { Text("e.g. 8:00 AM - 7:00 PM (Mon - Sat)") },
                leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = BrandSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BrandSecondary,
                  focusedLabelColor = BrandSecondary
                )
              )

              // Preset Shifts Chips
              Text("Quick Schedule Presets:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
              LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(presetHours) { preset ->
                  val isSelected = proWorkingHours == preset
                  Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) Teal100 else Slate100,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary) else null,
                    modifier = Modifier.clickable { proWorkingHours = preset }
                  ) {
                    Text(
                      text = preset,
                      fontSize = 11.sp,
                      color = if (isSelected) BrandSecondary else Slate700,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                  }
                }
              }

              // Working Days Toggle Pills
              Text("Active Working Days:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                daysOfWeek.forEach { day ->
                  val isWorking = selectedDays.contains(day)
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isWorking) BrandSecondary else Slate100,
                    modifier = Modifier
                      .size(42.dp)
                      .clickable {
                        val newSet = selectedDays.toMutableSet()
                        if (isWorking) newSet.remove(day) else newSet.add(day)
                        selectedDays = newSet
                        // Update text representation
                        proWorkingHours = when {
                          newSet.size == 7 -> "7:00 AM - 9:00 PM (All 7 Days)"
                          newSet.containsAll(listOf("Mon", "Tue", "Wed", "Thu", "Fri")) && newSet.size == 5 -> "9:00 AM - 6:00 PM (Mon - Fri)"
                          newSet.containsAll(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")) && newSet.size == 6 -> "8:00 AM - 7:00 PM (Mon - Sat)"
                          else -> "Custom: ${newSet.joinToString(", ")}"
                        }
                      }
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text = day,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isWorking) Color.White else Slate600
                      )
                    }
                  }
                }
              }
            }
          }
        }

        // SERVICE AREA MANAGEMENT CARD
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text("Service Area Management (${repository.selectedCity})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                  Text("Define the localities and radius where you accept on-demand jobs", fontSize = 11.sp, color = Slate500)
                }
              }

              // Area Input Field
              OutlinedTextField(
                value = proServiceArea,
                onValueChange = { proServiceArea = it },
                label = { Text("Localities Covered") },
                placeholder = { Text("e.g. Koramangala, HSR Layout, BTM Layout") },
                leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = BrandSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BrandSecondary,
                  focusedLabelColor = BrandSecondary
                )
              )

              // Interactive Locality Chips (tap to toggle)
              Text("Quick Locality Coverage (tap to add or remove):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
              LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(cityLocalities) { locality ->
                  val isIncluded = proServiceArea.contains(locality, ignoreCase = true)
                  Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isIncluded) Teal100 else Slate100,
                    border = if (isIncluded) androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary) else null,
                    modifier = Modifier.clickable {
                      if (isIncluded) {
                        // Remove locality
                        val areas = proServiceArea.split(",").map { it.trim() }.filter { !it.equals(locality, ignoreCase = true) }
                        proServiceArea = areas.joinToString(", ")
                      } else {
                        // Add locality
                        proServiceArea = if (proServiceArea.isBlank()) locality else "$proServiceArea, $locality"
                      }
                    }
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        if (isIncluded) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = null,
                        tint = if (isIncluded) BrandSecondary else Slate500,
                        modifier = Modifier.size(12.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        locality,
                        fontSize = 11.sp,
                        fontWeight = if (isIncluded) FontWeight.Bold else FontWeight.Normal,
                        color = if (isIncluded) BrandSecondary else Slate700
                      )
                    }
                  }
                }
              }

              Divider(color = Slate100, thickness = 1.dp)

              // Service Radius Slider
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Maximum Travel Radius:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
                Text("${proRadiusKm.toInt()} km", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = BrandSecondary)
              }

              Slider(
                value = proRadiusKm,
                onValueChange = { proRadiusKm = it },
                valueRange = 5f..35f,
                steps = 5,
                colors = SliderDefaults.colors(
                  thumbColor = BrandSecondary,
                  activeTrackColor = BrandSecondary,
                  inactiveTrackColor = Slate200
                )
              )

              // Coverage summary banner
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Slate50,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.NearMe, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "Coverage Zone: ${proRadiusKm.toInt()} km radius around ${repository.selectedCity}",
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      color = Slate900
                    )
                    Text(
                      text = "Typical arrival response window: ${(proRadiusKm * 1.5).toInt()}-${(proRadiusKm * 2.2).toInt()} mins",
                      fontSize = 10.sp,
                      color = Slate500
                    )
                  }
                }
              }
            }
          }
        }

        // RATES & SPECIALTY SKILLS CARD
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Text("Pricing & Verified Skill Tags", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)

              // Base Pricing
              OutlinedTextField(
                value = proStartingPrice,
                onValueChange = { proStartingPrice = it },
                label = { Text("Base Visit & Inspection Fee (₹)") },
                placeholder = { Text("299") },
                leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = BrandSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BrandSecondary,
                  focusedLabelColor = BrandSecondary
                )
              )

              // Skills Chips
              Text("Specialty Skills & Certifications:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
              FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                proSkills.forEach { skill ->
                  Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Blue50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Blue200)
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(skill, fontSize = 11.sp, color = BrandPrimary, fontWeight = FontWeight.Medium)
                      Spacer(modifier = Modifier.width(4.dp))
                      Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = BrandPrimary,
                        modifier = Modifier
                          .size(12.dp)
                          .clickable {
                            proSkills = proSkills.filter { it != skill }.toMutableList()
                          }
                      )
                    }
                  }
                }
              }

              // Add Skill Input
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedTextField(
                  value = newSkillInput,
                  onValueChange = { newSkillInput = it },
                  placeholder = { Text("Add skill (e.g. RO Fitting)") },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(10.dp)
                )
                Button(
                  onClick = {
                    if (newSkillInput.isNotBlank() && !proSkills.contains(newSkillInput.trim())) {
                      proSkills = (proSkills + newSkillInput.trim()).toMutableList()
                      newSkillInput = ""
                    }
                  },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary)
                ) {
                  Text("Add", fontSize = 12.sp)
                }
              }
            }
          }
        }
      }
    }
  }

  // Confirmation Alert Dialog
  if (showSuccessDialog) {
    AlertDialog(
      onDismissRequest = { showSuccessDialog = false },
      icon = {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green600, modifier = Modifier.size(36.dp))
      },
      title = {
        Text("Profile Updated Successfully", fontWeight = FontWeight.Bold, fontSize = 17.sp)
      },
      text = {
        Text(saveSuccessMessage, fontSize = 13.sp, color = Slate700, lineHeight = 18.sp)
      },
      confirmButton = {
        Button(
          onClick = { showSuccessDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
          Text("Continue")
        }
      }
    )
  }
}
