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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GigRepository
import com.example.model.Professional
import com.example.model.ServiceCategory
import com.example.ui.components.RatingDisplay
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  repository: GigRepository,
  onNavigateToServiceDetail: (String) -> Unit,
  onNavigateToProProfile: (String) -> Unit,
  onNavigateToAIAssistantWithQuery: (String) -> Unit,
  onNavigateToFindPros: () -> Unit,
  onNavigateToServices: () -> Unit,
  onNavigateToProfile: () -> Unit = {}
) {
  var problemInput by remember { mutableStateOf("") }

  val samplePrompts = listOf(
    "My kitchen pipe is broken and water is leaking",
    "My AC is not cooling",
    "Electrical switch is sparking",
    "Bathroom drain is blocked",
    "Need elderly companion for doctor visit",
    "House deep cleaning before festival"
  )

  val popularServices = repository.serviceCategories.take(6)
  val nearbyPros = repository.getFilteredProfessionals(city = repository.selectedCity).take(4)

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50),
    contentPadding = PaddingValues(bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // HERO SECTION
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 1.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          // Customer greeting & address badge
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(BrandPrimary),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = repository.activeUserName.take(1).uppercase(),
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Welcome, ${repository.activeUserName}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Slate900
                )
                Text(
                  text = repository.activeUserAddress,
                  fontSize = 10.sp,
                  color = Slate500,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Slate100,
              modifier = Modifier.clickable { onNavigateToProfile() }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Edit Profile", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Cooperative Badge
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(Teal100)
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Icon(
              Icons.Default.VerifiedUser,
              contentDescription = null,
              tint = BrandSecondary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Worker-Owned Cooperative Platform",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = BrandSecondary
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Your Problem. Our Professionals. One Simple Platform.",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Slate900,
            lineHeight = 28.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Find trusted local service professionals for household and community services in ${repository.selectedCity} with zero commission exploitation.",
            fontSize = 13.sp,
            color = Slate600,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(18.dp))

          // AI Problem Search Box
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Slate50,
            border = androidx.compose.foundation.BorderStroke(2.dp, BrandPrimary.copy(alpha = 0.5f)),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.AutoAwesome,
                  contentDescription = "AI",
                  tint = BrandPrimary,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "What problem are you facing?",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate800
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = problemInput,
                onValueChange = { problemInput = it },
                placeholder = {
                  Text(
                    text = "e.g. My bathroom tap is leaking & pipe ruptured",
                    fontSize = 12.sp,
                    color = Slate400
                  )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = BrandPrimary,
                  unfocusedBorderColor = Slate300
                )
              )

              Spacer(modifier = Modifier.height(10.dp))

              Button(
                onClick = {
                  val query = problemInput.ifBlank { "My kitchen pipe is broken and water is leaking" }
                  onNavigateToAIAssistantWithQuery(query)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
              ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Find the Right Service", fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Quick prompt chips
          Text(
            text = "Try asking about:",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate500
          )
          Spacer(modifier = Modifier.height(6.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(samplePrompts) { prompt ->
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = Slate100,
                modifier = Modifier.clickable {
                  onNavigateToAIAssistantWithQuery(prompt)
                }
              ) {
                Text(
                  text = prompt,
                  fontSize = 11.sp,
                  color = Slate700,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }
        }
      }
    }

    // QUICK STATS / COOP VALUE
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        CoopValuePill(
          icon = Icons.Default.Groups,
          number = "100%",
          label = "Cooperative Owned",
          modifier = Modifier.weight(1f)
        )
        CoopValuePill(
          icon = Icons.Default.Verified,
          number = "4.9★",
          label = "Vetted Experts",
          modifier = Modifier.weight(1f)
        )
        CoopValuePill(
          icon = Icons.Default.FlashOn,
          number = "18m",
          label = "Avg Arrival",
          modifier = Modifier.weight(1f)
        )
      }
    }

    // POPULAR SERVICES
    item {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Popular Services",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Slate900
            )
            Text(
              text = "Most booked household services in ${repository.selectedCity}",
              fontSize = 12.sp,
              color = Slate500
            )
          }
          TextButton(onClick = onNavigateToServices) {
            Text("View All", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grid 2 columns
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          popularServices.chunked(2).forEach { rowItems ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              rowItems.forEach { service ->
                PopularServiceCard(
                  service = service,
                  onClick = { onNavigateToServiceDetail(service.id) },
                  modifier = Modifier.weight(1f)
                )
              }
              if (rowItems.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      }
    }

    // HOW IT WORKS
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "How Cooperative Gig Works",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900
          )
          Spacer(modifier = Modifier.height(12.dp))

          StepItem(
            stepNumber = "1",
            title = "Describe Problem in Natural Language",
            description = "AI analyzes your query, assesses emergency risks, and determines exact service required."
          )
          Divider(modifier = Modifier.padding(vertical = 8.dp), color = Slate100)
          StepItem(
            stepNumber = "2",
            title = "Pick Vetted Cooperative Professional",
            description = "Browse nearby certified members with genuine ratings, transparent prices, and direct worker payout."
          )
          Divider(modifier = Modifier.padding(vertical = 8.dp), color = Slate100)
          StepItem(
            stepNumber = "3",
            title = "Track Live & Real-Time Direct Chat",
            description = "Communicate securely with read receipts, photo sharing, and live GPS arrival updates."
          )
          Divider(modifier = Modifier.padding(vertical = 8.dp), color = Slate100)
          StepItem(
            stepNumber = "4",
            title = "Pay Fairly & Rate Service",
            description = "Standardized fees with zero hidden middleman markup. 90% of earnings go straight to the professional."
          )
        }
      }
    }

    // TOP-RATED PROFESSIONALS NEARBY
    item {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Top-Rated Professionals",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Slate900
            )
            Text(
              text = "Verified cooperative members near you",
              fontSize = 12.sp,
              color = Slate500
            )
          }
          TextButton(onClick = onNavigateToFindPros) {
            Text("Find More", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          nearbyPros.forEach { pro ->
            ProQuickCard(
              pro = pro,
              onClick = { onNavigateToProProfile(pro.id) }
            )
          }
        }
      }
    }

    // CUSTOMER REVIEWS
    item {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = "Community Reviews",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = Slate900
        )
        Text(
          text = "Real feedback from neighbors in Karnataka",
          fontSize = 12.sp,
          color = Slate500
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          items(repository.reviews.take(4)) { review ->
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = Color.White,
              border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
              modifier = Modifier.width(260.dp)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = review.customerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Slate900
                  )
                  RatingDisplay(rating = review.rating.toDouble())
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "\"${review.comment}\"",
                  fontSize = 12.sp,
                  color = Slate600,
                  maxLines = 3,
                  overflow = TextOverflow.Ellipsis,
                  lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "${review.date} • Verified Customer",
                  fontSize = 10.sp,
                  color = BrandSecondary,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }
    }

    // SAFETY & TRUST FOOTER BANNER
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Slate900
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "The Cooperative Trust Guarantee",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Unlike commercial gig aggregators that take 25-35% cuts, our cooperative platform is operated directly by local service unions. Professionals get fair wages, and customers receive honest, insured service.",
            fontSize = 12.sp,
            color = Slate300,
            lineHeight = 17.sp
          )
        }
      }
    }
  }
}

@Composable
private fun CoopValuePill(
  icon: ImageVector,
  number: String,
  label: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = Color.White,
    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(icon, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = number, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Slate900)
      Text(text = label, fontSize = 10.sp, color = Slate500, textAlign = TextAlign.Center)
    }
  }
}

@Composable
private fun PopularServiceCard(
  service: ServiceCategory,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.clickable { onClick() },
    shape = RoundedCornerShape(14.dp),
    color = Color.White,
    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
    shadowElevation = 1.dp
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
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
              else -> Icons.Default.Build
            },
            contentDescription = null,
            tint = BrandPrimary,
            modifier = Modifier.size(20.dp)
          )
        }

        if (service.emergencyAvailable) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = Red100
          ) {
            Text(
              text = "24x7",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = BrandEmergency,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = service.name,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = Slate900,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = service.shortDescription,
        fontSize = 10.sp,
        color = Slate500,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 14.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "₹${service.startingPrice}",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 13.sp,
          color = BrandPrimary
        )

        RatingDisplay(rating = service.rating)
      }
    }
  }
}

@Composable
private fun StepItem(stepNumber: String, title: String, description: String) {
  Row(verticalAlignment = Alignment.Top) {
    Box(
      modifier = Modifier
        .size(26.dp)
        .clip(CircleShape)
        .background(BrandPrimary),
      contentAlignment = Alignment.Center
    ) {
      Text(text = stepNumber, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = description, fontSize = 11.sp, color = Slate600, lineHeight = 16.sp)
    }
  }
}

@Composable
private fun ProQuickCard(pro: Professional, onClick: () -> Unit) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color.White,
    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(Color(pro.initialAvatarColor)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = pro.name.take(1),
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = pro.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.Default.Verified, contentDescription = "Verified", tint = BrandPrimary, modifier = Modifier.size(14.dp))
        }

        Text(
          text = "${pro.serviceCategory} • ${pro.experienceYears} yrs exp",
          fontSize = 11.sp,
          color = Slate500
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          RatingDisplay(rating = pro.rating, reviewCount = pro.reviewCount)
          Spacer(modifier = Modifier.width(10.dp))
          Text(text = "${pro.distanceKm} km away", fontSize = 11.sp, color = BrandSecondary, fontWeight = FontWeight.Medium)
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "₹${pro.startingPrice}",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 14.sp,
          color = BrandPrimary
        )
        Text(text = "starting", fontSize = 10.sp, color = Slate400)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
          shape = RoundedCornerShape(12.dp),
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
    }
  }
}
