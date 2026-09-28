package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun ProfessionalProfileScreen(
  repository: GigRepository,
  proId: String,
  onBookPro: (Professional) -> Unit,
  onStartChat: (Professional) -> Unit,
  onBack: () -> Unit
) {
  val pro = repository.getProfessionalById(proId) ?: repository.professionals.first()
  val reviews = repository.getReviewsForProfessional(pro.id)
  var showShareToast by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(pro.name, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { showShareToast = true }) {
            Icon(Icons.Default.Share, contentDescription = "Share Profile")
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
          Column {
            Text(text = "₹${pro.startingPrice}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = BrandPrimary)
            Text(text = "starting visit charge", fontSize = 11.sp, color = Slate500)
          }

          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
              onClick = { onStartChat(pro) },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.height(44.dp)
            ) {
              Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Chat", fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = { onBookPro(pro) },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.height(44.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
              Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Book Service", fontWeight = FontWeight.Bold)
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
      // Header Card
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .background(Color(pro.initialAvatarColor)),
                contentAlignment = Alignment.Center
              ) {
                Text(pro.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp)
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(pro.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate900)
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(Icons.Default.Verified, contentDescription = "Verified Member", tint = BrandPrimary, modifier = Modifier.size(18.dp))
                }
                Text(pro.serviceCategory, fontSize = 13.sp, color = Slate600, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  RatingDisplay(rating = pro.rating, reviewCount = pro.reviewCount)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("•", color = Slate400)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("${pro.distanceKm} km away", fontSize = 11.sp, color = BrandSecondary, fontWeight = FontWeight.Bold)
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Slate100, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // 3-column stats
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              StatPill(title = "Experience", value = "${pro.experienceYears} Years")
              StatPill(title = "Completed", value = "${pro.completedJobs} Jobs")
              StatPill(title = "City", value = pro.city)
            }
          }
        }
      }

      // About Section
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("About the Professional", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = pro.about, fontSize = 13.sp, color = Slate700, lineHeight = 18.sp)

            Spacer(modifier = Modifier.height(12.dp))
            Text("Skills & Specializations", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              items(pro.skills) { s ->
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Blue100
                ) {
                  Text(text = s, fontSize = 11.sp, color = BrandPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
              }
            }
          }
        }
      }

      // Service Details (Areas, Hours, Languages)
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text("Service Information", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)

            InfoRow(icon = Icons.Default.Place, label = "Service Area", value = pro.serviceArea)
            InfoRow(icon = Icons.Default.Schedule, label = "Working Hours", value = pro.workingHours)
            InfoRow(icon = Icons.Default.Translate, label = "Languages", value = pro.languages.joinToString(", "))
            InfoRow(icon = Icons.Default.Phone, label = "Cooperative Helpline", value = pro.phone)
          }
        }
      }

      // Customer Reviews Section
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Customer Reviews", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
              RatingDisplay(rating = pro.rating, reviewCount = reviews.size)
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (reviews.isEmpty()) {
              Text("No reviews written yet. Be the first to book!", fontSize = 12.sp, color = Slate500)
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                reviews.forEach { rev ->
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate50,
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(rev.customerName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                        Text(rev.date, fontSize = 10.sp, color = Slate400)
                      }
                      Spacer(modifier = Modifier.height(2.dp))
                      RatingDisplay(rating = rev.rating.toDouble())
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(rev.comment, fontSize = 12.sp, color = Slate700, lineHeight = 16.sp)
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

  if (showShareToast) {
    AlertDialog(
      onDismissRequest = { showShareToast = false },
      title = { Text("Share Profile") },
      text = {
        Text("Profile link copied for ${pro.name} - Cooperative Certified ${pro.serviceCategory} in ${pro.city}.")
      },
      confirmButton = {
        Button(onClick = { showShareToast = false }) {
          Text("OK")
        }
      }
    )
  }
}

@Composable
private fun StatPill(title: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = title, fontSize = 10.sp, color = Slate400)
    Spacer(modifier = Modifier.height(2.dp))
    Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate800)
  }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
  Row(verticalAlignment = Alignment.Top) {
    Icon(icon, contentDescription = null, tint = Slate500, modifier = Modifier.size(16.dp))
    Spacer(modifier = Modifier.width(8.dp))
    Column {
      Text(label, fontSize = 10.sp, color = Slate400)
      Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate800)
    }
  }
}
