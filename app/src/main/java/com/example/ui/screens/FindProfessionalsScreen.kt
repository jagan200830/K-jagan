package com.example.ui.screens

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
import com.example.ui.components.RatingDisplay
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindProfessionalsScreen(
  repository: GigRepository,
  preselectedCategory: String? = null,
  onViewProfile: (String) -> Unit,
  onBookPro: (Professional) -> Unit
) {
  var selectedCategory by remember { mutableStateOf(preselectedCategory ?: "") }
  var searchQuery by remember { mutableStateOf("") }
  var onlyAvailable by remember { mutableStateOf(false) }
  var sortBy by remember { mutableStateOf("distance") } // "distance", "rating", "price"
  var showMapSimulation by remember { mutableStateOf(true) }

  val categories = listOf("All") + repository.serviceCategories.map { it.name }

  var filteredList = repository.getFilteredProfessionals(
    categoryId = if (selectedCategory == "All" || selectedCategory.isEmpty()) null else selectedCategory,
    searchQuery = searchQuery,
    city = repository.selectedCity,
    onlyAvailable = onlyAvailable
  )

  filteredList = when (sortBy) {
    "rating" -> filteredList.sortedByDescending { it.rating }
    "price" -> filteredList.sortedBy { it.startingPrice }
    else -> filteredList.sortedBy { it.distanceKm }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .padding(bottom = 70.dp)
  ) {
    // Header & Filter bar
    Surface(
      color = Color.White,
      shadowElevation = 1.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Find Professionals",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Slate900
            )
            Text(
              text = "${filteredList.size} verified workers in ${repository.selectedCity}",
              fontSize = 11.sp,
              color = Slate500
            )
          }

          // Toggle map view
          TextButton(
            onClick = { showMapSimulation = !showMapSimulation }
          ) {
            Icon(
              imageVector = if (showMapSimulation) Icons.Default.List else Icons.Default.Map,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = BrandPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (showMapSimulation) "Hide Map" else "Show Map",
              fontSize = 12.sp,
              color = BrandPrimary,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search field
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by name, skill, or service (e.g. Ramesh, plumbing)", fontSize = 12.sp, color = Slate400) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandPrimary,
            unfocusedBorderColor = Slate200
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(categories) { cat ->
            val isSelected = (cat == "All" && selectedCategory.isEmpty()) || selectedCategory == cat
            FilterChip(
              selected = isSelected,
              onClick = {
                selectedCategory = if (cat == "All") "" else cat
              },
              label = { Text(cat, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Blue100,
                selectedLabelColor = BrandPrimary
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sort & Availability row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            SortPill(
              text = "Nearest",
              isSelected = sortBy == "distance",
              onClick = { sortBy = "distance" }
            )
            SortPill(
              text = "Top Rated",
              isSelected = sortBy == "rating",
              onClick = { sortBy = "rating" }
            )
            SortPill(
              text = "Lowest Price",
              isSelected = sortBy == "price",
              onClick = { sortBy = "price" }
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onlyAvailable = !onlyAvailable }
          ) {
            Checkbox(
              checked = onlyAvailable,
              onCheckedChange = { onlyAvailable = it },
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Available Now", fontSize = 11.sp, color = Slate700)
          }
        }
      }
    }

    // MAP VISUAL SIMULATION
    if (showMapSimulation) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .padding(horizontal = 14.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFE2E8F0),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate300)
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          // Simulated stylized map grid
          Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
          ) {
            repeat(4) {
              Divider(color = Color.White.copy(alpha = 0.7f), thickness = 2.dp)
            }
          }

          // User Pin (Center)
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .clip(CircleShape)
              .background(BrandPrimary)
              .padding(6.dp)
          ) {
            Icon(Icons.Default.PersonPinCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
          }

          // Worker pins nearby
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(start = 30.dp, top = 20.dp)
              .clip(CircleShape)
              .background(BrandSecondary)
              .padding(4.dp)
          ) {
            Icon(Icons.Default.Build, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          }

          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(end = 40.dp, bottom = 25.dp)
              .clip(CircleShape)
              .background(Color(0xFFD97706))
              .padding(4.dp)
          ) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          }

          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(end = 60.dp, top = 15.dp)
              .clip(CircleShape)
              .background(Color(0xFF059669))
              .padding(4.dp)
          ) {
            Icon(Icons.Default.CleaningServices, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          }

          // Map legend chip
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(8.dp)
          ) {
            Text(
              text = "📍 Local Radius: ${repository.selectedCity} (0.5km - 4.5km)",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Slate800,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }

    // List of Professionals
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp, vertical = 6.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      if (filteredList.isEmpty()) {
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.SearchOff, contentDescription = null, tint = Slate400, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("No professionals found", fontWeight = FontWeight.Bold, color = Slate700)
            Text("Try clearing some filters or switching to another category.", fontSize = 12.sp, color = Slate500)
          }
        }
      } else {
        items(filteredList) { pro ->
          ProDetailedCard(
            pro = pro,
            onViewProfile = { onViewProfile(pro.id) },
            onBook = { onBookPro(pro) }
          )
        }
      }
    }
  }
}

@Composable
private fun SortPill(text: String, isSelected: Boolean, onClick: () -> Unit) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) BrandPrimary else Slate100,
    modifier = Modifier.clickable { onClick() }
  ) {
    Text(
      text = text,
      fontSize = 10.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      color = if (isSelected) Color.White else Slate700,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
  }
}

@Composable
fun ProDetailedCard(
  pro: Professional,
  onViewProfile: () -> Unit,
  onBook: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color.White,
    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
    shadowElevation = 1.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
      ) {
        // Avatar
        Box(
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color(pro.initialAvatarColor)),
          contentAlignment = Alignment.Center
        ) {
          Text(pro.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = pro.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.Default.Verified, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(15.dp))
            }

            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (pro.isAvailableNow) Green100 else Slate100
            ) {
              Text(
                text = if (pro.isAvailableNow) "Available" else "Busy",
                fontSize = 10.sp,
                color = if (pro.isAvailableNow) Green600 else Slate500,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = "${pro.serviceCategory} • ${pro.experienceYears} yrs experience",
            fontSize = 12.sp,
            color = Slate600
          )

          Spacer(modifier = Modifier.height(4.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RatingDisplay(rating = pro.rating, reviewCount = pro.reviewCount)
            Spacer(modifier = Modifier.width(8.dp))
            Text("•", color = Slate400)
            Spacer(modifier = Modifier.width(8.dp))
            Text("${pro.completedJobs} jobs done", fontSize = 11.sp, color = Slate500)
          }

          Spacer(modifier = Modifier.height(4.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Place, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("${pro.distanceKm} km away (${pro.serviceArea.split(",").firstOrNull() ?: pro.city})", fontSize = 11.sp, color = Slate600)
          }
        }
      }

      // Skills chips
      Spacer(modifier = Modifier.height(8.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        items(pro.skills.take(3)) { skill ->
          Surface(shape = RoundedCornerShape(4.dp), color = Slate100) {
            Text(text = skill, fontSize = 10.sp, color = Slate700, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = Slate100, thickness = 0.5.dp)
      Spacer(modifier = Modifier.height(8.dp))

      // Footer: price and actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "₹${pro.startingPrice}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandPrimary)
          Text(text = "starting inspection", fontSize = 10.sp, color = Slate400)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = onViewProfile,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Text("View Profile", fontSize = 12.sp)
          }

          Button(
            onClick = onBook,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            modifier = Modifier.height(34.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
          ) {
            Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Book", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
