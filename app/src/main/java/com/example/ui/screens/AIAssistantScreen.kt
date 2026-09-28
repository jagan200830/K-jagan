package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GigRepository
import com.example.model.AIRecommendation
import com.example.model.Professional
import com.example.ui.components.RatingDisplay
import com.example.ui.theme.*

data class AIDialogueTurn(
  val id: String,
  val userMessage: String,
  val recommendation: AIRecommendation,
  var selectedFollowUp: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
  repository: GigRepository,
  initialQuery: String? = null,
  onSelectProToBook: (Professional, String, String) -> Unit,
  onViewProProfile: (String) -> Unit
) {
  var textInput by remember { mutableStateOf(initialQuery ?: "") }
  val history = remember { mutableStateListOf<AIDialogueTurn>() }
  val listState = rememberLazyListState()

  // Process initial query if provided
  LaunchedEffect(initialQuery) {
    if (!initialQuery.isNullOrBlank() && history.none { it.userMessage == initialQuery }) {
      val res = repository.analyzeProblemQuery(initialQuery)
      history.add(AIDialogueTurn(id = "turn_${System.currentTimeMillis()}", userMessage = initialQuery, recommendation = res))
    }
  }

  // Pre-canned prompt suggestions
  val promptChips = listOf(
    "My kitchen pipe is broken and water is leaking",
    "My AC is not cooling properly",
    "Living room switch is sparking",
    "Bathroom drain is blocked",
    "Washing machine making loud vibration noise",
    "Elderly mother needs help visiting doctor"
  )

  Scaffold(
    topBar = {
      Surface(
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(colors = listOf(BrandPrimary, BrandSecondary))
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("Cooperative AI Assistant", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
              Spacer(modifier = Modifier.width(6.dp))
              Surface(shape = RoundedCornerShape(4.dp), color = Teal100) {
                Text("Smart Diagnosis", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandSecondary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
              }
            }
            Text("Describe your problem and we'll match the right service & local pros.", fontSize = 11.sp, color = Slate500)
          }
        }
      }
    },
    bottomBar = {
      Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          // Quick suggestion chips
          if (history.isEmpty()) {
            Text("Try common problem queries:", fontSize = 11.sp, color = Slate500, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              items(promptChips) { chip ->
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = Slate100,
                  border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                  modifier = Modifier.clickable {
                    textInput = chip
                    val res = repository.analyzeProblemQuery(chip)
                    history.add(AIDialogueTurn(id = "turn_${System.currentTimeMillis()}", userMessage = chip, recommendation = res))
                    textInput = ""
                  }
                ) {
                  Text(text = chip, fontSize = 11.sp, color = Slate700, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          // Input Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = textInput,
              onValueChange = { textInput = it },
              placeholder = { Text("What problem are you facing? (e.g. pipe leaking)", fontSize = 12.sp, color = Slate400) },
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 46.dp),
              shape = RoundedCornerShape(24.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = Slate300
              ),
              trailingIcon = {
                IconButton(
                  onClick = {
                    if (textInput.isNotBlank()) {
                      val query = textInput.trim()
                      val res = repository.analyzeProblemQuery(query)
                      history.add(AIDialogueTurn(id = "turn_${System.currentTimeMillis()}", userMessage = query, recommendation = res))
                      textInput = ""
                    }
                  },
                  enabled = textInput.isNotBlank()
                ) {
                  Box(
                    modifier = Modifier
                      .size(32.dp)
                      .clip(CircleShape)
                      .background(if (textInput.isNotBlank()) BrandPrimary else Slate300),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp))
                  }
                }
              }
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Slate50)
        .padding(innerPadding)
    ) {
      if (history.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(Blue100),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(36.dp))
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "Tell us what's broken or what help you need",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Slate900,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Our AI evaluates damage risks, matches certified cooperative specialists in ${repository.selectedCity}, and provides immediate safety steps.",
            fontSize = 12.sp,
            color = Slate600,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 17.sp
          )
        }
      } else {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          items(history) { turn ->
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              // User Query Bubble
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                Surface(
                  shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
                  color = BrandPrimary,
                  modifier = Modifier.widthIn(max = 300.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = turn.userMessage, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
                  }
                }
              }

              // AI Diagnosis Response Card
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  // AI header
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(24.dp)
                          .clip(CircleShape)
                          .background(Teal100),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(14.dp))
                      }
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("AI Diagnosis", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    }

                    // Urgency Badge
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = if (turn.recommendation.urgencyLevel.contains("HIGH")) Red100 else Blue100
                    ) {
                      Text(
                        text = turn.recommendation.urgencyLevel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (turn.recommendation.urgencyLevel.contains("HIGH")) BrandEmergency else BrandPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  // Recommended Service Highlight
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate100,
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.BuildCircle, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(26.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("Recommended Service:", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.SemiBold)
                        Text(turn.recommendation.detectedCategory, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Slate900)
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  // Problem Detected
                  Text(
                    text = "Problems Identified:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                  )
                  Column(
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                  ) {
                    turn.recommendation.detectedProblems.forEach { p ->
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FiberManualRecord, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(8.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = p, fontSize = 12.sp, color = Slate700)
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  // Suggested Action
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Amber100.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Amber100)
                  ) {
                    Row(
                      modifier = Modifier.padding(8.dp),
                      verticalAlignment = Alignment.Top
                    ) {
                      Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = turn.recommendation.suggestedAction,
                        fontSize = 11.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 15.sp
                      )
                    }
                  }

                  // Follow up question if any
                  if (turn.recommendation.followUpQuestion != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                      text = turn.recommendation.followUpQuestion,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Slate900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      items(turn.recommendation.followUpOptions) { opt ->
                        val isSelected = turn.selectedFollowUp == opt
                        Surface(
                          shape = RoundedCornerShape(12.dp),
                          color = if (isSelected) Teal100 else Slate100,
                          border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary) else null,
                          modifier = Modifier.clickable {
                            turn.selectedFollowUp = opt
                          }
                        ) {
                          Text(
                            text = opt,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) BrandSecondary else Slate700,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                          )
                        }
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(12.dp))
                  Divider(color = Slate200, thickness = 0.5.dp)
                  Spacer(modifier = Modifier.height(10.dp))

                  // Recommended Professionals Carousel/List
                  Text(
                    text = "Nearby Recommended Professionals in ${repository.selectedCity}:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Slate900
                  )

                  Spacer(modifier = Modifier.height(8.dp))

                  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val pros = turn.recommendation.recommendedPros.ifEmpty {
                      repository.getFilteredProfessionals(city = repository.selectedCity).take(2)
                    }

                    pros.forEach { pro ->
                      Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Slate50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                          Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Box(
                              modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(pro.initialAvatarColor)),
                              contentAlignment = Alignment.Center
                            ) {
                              Text(pro.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                              Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(pro.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(13.dp))
                              }
                              Text("${pro.serviceCategory} • ${pro.experienceYears}y exp • ${pro.completedJobs} jobs", fontSize = 10.sp, color = Slate500)
                              Spacer(modifier = Modifier.height(2.dp))
                              Row(verticalAlignment = Alignment.CenterVertically) {
                                RatingDisplay(rating = pro.rating, reviewCount = pro.reviewCount)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${pro.distanceKm} km away", fontSize = 10.sp, color = BrandSecondary, fontWeight = FontWeight.Bold)
                              }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                              Text("₹${pro.startingPrice}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = BrandPrimary)
                              Text("starting", fontSize = 9.sp, color = Slate400)
                              Surface(shape = RoundedCornerShape(4.dp), color = Green100) {
                                Text("Available now", fontSize = 9.sp, color = Green600, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                              }
                            }
                          }

                          Spacer(modifier = Modifier.height(8.dp))

                          // Action Buttons
                          Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            OutlinedButton(
                              onClick = { onViewProProfile(pro.id) },
                              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                              modifier = Modifier.height(30.dp),
                              shape = RoundedCornerShape(8.dp)
                            ) {
                              Text("View Profile", fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                              onClick = {
                                val problemSummary = "${turn.userMessage}${if (turn.selectedFollowUp != null) " (${turn.selectedFollowUp})" else ""}"
                                onSelectProToBook(pro, turn.recommendation.serviceId, problemSummary)
                              },
                              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                              modifier = Modifier.height(30.dp),
                              shape = RoundedCornerShape(8.dp),
                              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                            ) {
                              Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
                              Spacer(modifier = Modifier.width(4.dp))
                              Text("Book Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
        }
      }
    }
  }
}
