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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
  repository: GigRepository,
  bookingId: String,
  professionalId: String,
  onReviewSubmitted: () -> Unit,
  onBack: () -> Unit
) {
  val booking = repository.getBookingById(bookingId) ?: repository.bookings.first()
  val pro = repository.getProfessionalById(professionalId) ?: repository.professionals.first()

  var overallRating by remember { mutableStateOf(5) }
  var qualityRating by remember { mutableStateOf(5) }
  var behaviourRating by remember { mutableStateOf(5) }
  var punctualityRating by remember { mutableStateOf(5) }
  var pricingRating by remember { mutableStateOf(5) }
  var reviewText by remember { mutableStateOf("") }
  var isSubmitted by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Rate & Review Service", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
        Box(modifier = Modifier.padding(14.dp)) {
          Button(
            onClick = {
              repository.submitReview(
                bookingId = booking.id,
                professionalId = pro.id,
                overallRating = overallRating.toFloat(),
                qualityRating = qualityRating.toFloat(),
                behaviourRating = behaviourRating.toFloat(),
                punctualityRating = punctualityRating.toFloat(),
                pricingRating = pricingRating.toFloat(),
                comment = reviewText
              )
              isSubmitted = true
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
          ) {
            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Submit Review", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(pro.initialAvatarColor)),
              contentAlignment = Alignment.Center
            ) {
              Text(pro.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("How was your service with ${pro.name}?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
            Text(booking.serviceName, fontSize = 12.sp, color = Slate500)

            Spacer(modifier = Modifier.height(12.dp))

            // Overall Star Picker
            StarRatingBar(
              rating = overallRating,
              onRatingChanged = { overallRating = it },
              starSize = 32.dp
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = when (overallRating) {
                5 -> "Outstanding Experience! ★★★★★"
                4 -> "Very Good! ★★★★☆"
                3 -> "Average Service ★★★☆☆"
                2 -> "Needs Improvement ★★☆☆☆"
                else -> "Unsatisfactory ★☆☆☆☆"
              },
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = BrandPrimary
            )
          }
        }
      }

      // Detailed Rating Categories
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Text("Rate Specific Categories", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)

            CategoryRatingRow(
              title = "Service Quality",
              subtitle = "Workmanship, leak stoppage, tool precision",
              rating = qualityRating,
              onRatingChanged = { qualityRating = it }
            )

            CategoryRatingRow(
              title = "Professional Behaviour",
              subtitle = "Courtesy, cleanliness & cooperative values",
              rating = behaviourRating,
              onRatingChanged = { behaviourRating = it }
            )

            CategoryRatingRow(
              title = "Punctuality",
              subtitle = "Arrived within estimated time frame",
              rating = punctualityRating,
              onRatingChanged = { punctualityRating = it }
            )

            CategoryRatingRow(
              title = "Pricing Fairness",
              subtitle = "Transparent rate with zero hidden costs",
              rating = pricingRating,
              onRatingChanged = { pricingRating = it }
            )
          }
        }
      }

      // Comment Box
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Write a Review", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
            Text("Your review helps fellow community members make informed decisions.", fontSize = 11.sp, color = Slate500)

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = reviewText,
              onValueChange = { reviewText = it },
              placeholder = { Text("Describe your experience (e.g. Ramesh arrived on time, was extremely polite, and fixed the kitchen drain pipe in 20 minutes cleanly.)", fontSize = 12.sp) },
              modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = Slate300
              )
            )
          }
        }
      }
    }
  }

  if (isSubmitted) {
    AlertDialog(
      onDismissRequest = onReviewSubmitted,
      title = { Text("Review Submitted!") },
      text = {
        Text("Thank you for supporting cooperative gig workers. Your feedback has been published on ${pro.name}'s verified profile.")
      },
      confirmButton = {
        Button(onClick = onReviewSubmitted) {
          Text("Go to Bookings")
        }
      }
    )
  }
}

@Composable
private fun CategoryRatingRow(
  title: String,
  subtitle: String,
  rating: Int,
  onRatingChanged: (Int) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Slate900)
      Text(subtitle, fontSize = 10.sp, color = Slate500)
    }

    StarRatingBar(
      rating = rating,
      onRatingChanged = onRatingChanged,
      starSize = 20.dp
    )
  }
}

@Composable
private fun StarRatingBar(
  rating: Int,
  onRatingChanged: (Int) -> Unit,
  starSize: androidx.compose.ui.unit.Dp
) {
  Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
    (1..5).forEach { star ->
      Icon(
        imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
        contentDescription = "Star $star",
        tint = Color(0xFFF59E0B),
        modifier = Modifier
          .size(starSize)
          .clickable { onRatingChanged(star) }
      )
    }
  }
}
