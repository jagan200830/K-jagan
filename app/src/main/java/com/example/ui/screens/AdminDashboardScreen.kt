package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.model.Professional
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  repository: GigRepository,
  onSwitchToCustomer: () -> Unit
) {
  var selectedTab by remember { mutableStateOf(0) }
  val tabs = listOf("Professionals Verification", "Service Categories", "Disputes & Complaints")

  Scaffold(
    topBar = {
      Surface(
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Cooperative Admin Portal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate900)
              Text("Platform Governance & Member Verification", fontSize = 11.sp, color = Slate500)
            }

            TextButton(onClick = onSwitchToCustomer) {
              Text("Back to App", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Key metrics 3x2 grid
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              AdminStatCard("Total Users", "1,420", BrandPrimary, Modifier.weight(1f))
              AdminStatCard("Verified Pros", "${repository.professionals.size}", BrandSecondary, Modifier.weight(1f))
              AdminStatCard("Active Gigs", "${repository.bookings.size}", Color(0xFFD97706), Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              AdminStatCard("Completed Gigs", "1,180", Green600, Modifier.weight(1f))
              AdminStatCard("Platform Revenue", "₹48,290", Slate900, Modifier.weight(1f))
              AdminStatCard("Open Disputes", "0", Green600, Modifier.weight(1f))
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

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
                text = { Text(title, fontSize = 11.sp, fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal) }
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
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      if (selectedTab == 0) {
        item {
          Text("Manage & Verify Cooperative Service Members", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
        }

        items(repository.professionals) { pro ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(Color(pro.initialAvatarColor)),
                contentAlignment = Alignment.Center
              ) {
                Text(pro.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(pro.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                Text("${pro.serviceCategory} • ${pro.city} • ${pro.phone}", fontSize = 11.sp, color = Slate500)
                Text("Background Verified • Aadhaar & Police Check OK", fontSize = 10.sp, color = Green600, fontWeight = FontWeight.SemiBold)
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (pro.isVerified) Green100 else Amber100
              ) {
                Text(
                  text = if (pro.isVerified) "Verified" else "Pending",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (pro.isVerified) Green600 else Color(0xFFB45309),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }
          }
        }
      } else if (selectedTab == 1) {
        item {
          Text("Active Service Categories (Standardized Rates)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
        }

        items(repository.serviceCategories) { cat ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(cat.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                Text("Base visit fee: ₹${cat.startingPrice} • Rating: ★${cat.rating}", fontSize = 11.sp, color = Slate600)
              }
              Surface(shape = RoundedCornerShape(4.dp), color = Teal100) {
                Text(cat.group.title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
              }
            }
          }
        }
      } else {
        item {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Zero Open Customer Disputes", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
              Spacer(modifier = Modifier.height(6.dp))
              Text("Cooperative ombudsman policy ensures that 100% of reported issues are addressed via direct worker-customer mediation within 24 hours.", fontSize = 12.sp, color = Slate600)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AdminStatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = Slate100,
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
      Text(title, fontSize = 9.sp, color = Slate500, maxLines = 1)
      Spacer(modifier = Modifier.height(2.dp))
      Text(value, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = color)
    }
  }
}
