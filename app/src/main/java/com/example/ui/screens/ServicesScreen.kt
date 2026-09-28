package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GigRepository
import com.example.model.CategoryGroup
import com.example.model.ServiceCategory
import com.example.ui.components.RatingDisplay
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
  repository: GigRepository,
  onSelectService: (ServiceCategory) -> Unit,
  onFindProsForService: (String) -> Unit
) {
  var selectedTab by remember { mutableStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedServiceDetail by remember { mutableStateOf<ServiceCategory?>(null) }

  val tabGroups = listOf("All", "Home Repair", "Home Services", "Community")

  val filteredServices = repository.serviceCategories.filter { service ->
    val matchesGroup = when (selectedTab) {
      1 -> service.group == CategoryGroup.HOME_REPAIR
      2 -> service.group == CategoryGroup.HOME_SERVICES
      3 -> service.group == CategoryGroup.COMMUNITY_SERVICES
      else -> true
    }
    val matchesSearch = searchQuery.isEmpty() ||
        service.name.contains(searchQuery, ignoreCase = true) ||
        service.shortDescription.contains(searchQuery, ignoreCase = true) ||
        service.problemsHandled.any { it.contains(searchQuery, ignoreCase = true) }

    matchesGroup && matchesSearch
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 70.dp)
    ) {
      // Header & Search
      Surface(
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Services Marketplace",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900
          )
          Text(
            text = "Browse 17+ certified cooperative household & community services",
            fontSize = 12.sp,
            color = Slate500
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search services (e.g. pipe, pest, sofa, cleaning)", fontSize = 12.sp, color = Slate400) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Slate400)
                }
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BrandPrimary,
              unfocusedBorderColor = Slate200
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Category tabs
          ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 0.dp,
            containerColor = Color.White,
            contentColor = BrandPrimary,
            divider = {}
          ) {
            tabGroups.forEachIndexed { index, title ->
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

      // Services List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        item {
          Text(
            text = "${filteredServices.size} services available in ${repository.selectedCity}",
            fontSize = 12.sp,
            color = Slate500,
            fontWeight = FontWeight.SemiBold
          )
        }

        items(filteredServices) { service ->
          ServiceListCard(
            service = service,
            onClick = { selectedServiceDetail = service },
            onFindPros = { onFindProsForService(service.id) }
          )
        }
      }
    }

    // Detailed Service Sheet / Dialog
    if (selectedServiceDetail != null) {
      val s = selectedServiceDetail!!
      val prosForService = repository.getFilteredProfessionals(categoryId = s.id, city = repository.selectedCity)

      AlertDialog(
        onDismissRequest = { selectedServiceDetail = null },
        title = {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Blue100),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Build, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(text = s.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                Text(text = s.group.title, fontSize = 11.sp, color = BrandSecondary, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        },
        text = {
          LazyColumn(
            modifier = Modifier.heightIn(max = 440.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            item {
              Text(text = s.fullDescription, fontSize = 13.sp, color = Slate700, lineHeight = 18.sp)
            }

            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Slate100,
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text("Starts From", fontSize = 10.sp, color = Slate500)
                    Text("₹${s.startingPrice}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = BrandPrimary)
                  }
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Slate100,
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text("Response Time", fontSize = 10.sp, color = Slate500)
                    Text(s.avgResponseTime, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate800)
                  }
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Slate100,
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text("Rating", fontSize = 10.sp, color = Slate500)
                    RatingDisplay(rating = s.rating, reviewCount = s.professionalCount * 8)
                  }
                }
              }
            }

            item {
              Text(
                text = "Types of Problems Handled:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Slate900
              )
              Spacer(modifier = Modifier.height(4.dp))
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                s.problemsHandled.forEach { prob ->
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = BrandSecondary,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = prob, fontSize = 12.sp, color = Slate700)
                  }
                }
              }
            }

            item {
              Text(
                text = "Nearby Professionals (${prosForService.size} in ${repository.selectedCity}):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Slate900
              )
              Spacer(modifier = Modifier.height(4.dp))
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                prosForService.take(2).forEach { pro ->
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Slate50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(8.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Box(
                        modifier = Modifier
                          .size(32.dp)
                          .clip(CircleShape)
                          .background(Color(pro.initialAvatarColor)),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(pro.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                      }
                      Spacer(modifier = Modifier.width(8.dp))
                      Column(modifier = Modifier.weight(1f)) {
                        Text(pro.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                        Text("${pro.distanceKm} km away • ${pro.experienceYears}y exp", fontSize = 10.sp, color = Slate500)
                      }
                      RatingDisplay(rating = pro.rating)
                    }
                  }
                }
              }
            }
          }
        },
        confirmButton = {
          Button(
            onClick = {
              val serviceId = s.id
              selectedServiceDetail = null
              onFindProsForService(serviceId)
            },
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
          ) {
            Text("Find Pros Near Me", fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          TextButton(onClick = { selectedServiceDetail = null }) {
            Text("Close")
          }
        }
      )
    }
  }
}

@Composable
private fun ServiceListCard(
  service: ServiceCategory,
  onClick: () -> Unit,
  onFindPros: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color.White,
    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Blue100),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when (service.id) {
              "plumbing" -> Icons.Default.Plumbing
              "electrical" -> Icons.Default.Bolt
              "carpentry" -> Icons.Default.Carpenter
              "ac_repair" -> Icons.Default.AcUnit
              "cleaning" -> Icons.Default.CleaningServices
              "delivery" -> Icons.Default.DeliveryDining
              "elderly_assistance" -> Icons.Default.VolunteerActivism
              "gardening" -> Icons.Default.Yard
              "appliance_repair" -> Icons.Default.HomeRepairService
              else -> Icons.Default.Build
            },
            contentDescription = null,
            tint = BrandPrimary,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = service.name,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Slate900
            )

            if (service.emergencyAvailable) {
              Surface(shape = RoundedCornerShape(4.dp), color = Red100) {
                Text(
                  text = "24x7 Emergency",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = BrandEmergency,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }

          Text(
            text = service.shortDescription,
            fontSize = 11.sp,
            color = Slate500,
            lineHeight = 15.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            RatingDisplay(rating = service.rating, reviewCount = service.professionalCount)

            Text(
              text = "Starts ₹${service.startingPrice}",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 13.sp,
              color = BrandPrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = Slate100, thickness = 0.5.dp)
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Avg arrival: ${service.avgResponseTime}",
          fontSize = 11.sp,
          color = Slate500
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(30.dp),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Details", fontSize = 11.sp)
          }

          Button(
            onClick = onFindPros,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(30.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
          ) {
            Text("Find Pros", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
