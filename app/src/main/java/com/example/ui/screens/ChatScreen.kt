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
import androidx.compose.material.icons.outlined.*
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
import com.example.model.ChatMessage
import com.example.model.MessageReadStatus
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  repository: GigRepository,
  bookingId: String? = repository.activeChatBookingId,
  onNavigateToTracking: (String) -> Unit,
  onBack: () -> Unit
) {
  val targetBooking = repository.bookings.find { it.id == bookingId } ?: repository.bookings.firstOrNull()

  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  val messages = if (targetBooking != null) {
    repository.getMessagesForBooking(targetBooking.id)
  } else emptyList()

  // Scroll to bottom when new messages arrive
  LaunchedEffect(messages.size, repository.isProfessionalTyping) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val quickReplies = listOf(
    "Are you on your way?",
    "I'm at the gate waiting",
    "Please bring replacement parts",
    "Please call when you reach",
    "Flat 302, 3rd floor"
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
            .padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate800)
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Avatar with online status
          Box {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(BrandPrimary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = targetBooking?.professionalName?.take(1) ?: "P",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 18.sp
              )
            }
            // Online green dot
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Color(0xFF22C55E))
                .border(2.dp, Color.White, CircleShape)
                .align(Alignment.BottomEnd)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = targetBooking?.professionalName ?: "Service Professional",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Slate900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified",
                tint = BrandPrimary,
                modifier = Modifier.size(15.dp)
              )
            }

            if (repository.isProfessionalTyping) {
              Text(
                text = "typing...",
                fontSize = 11.sp,
                color = BrandSecondary,
                fontWeight = FontWeight.SemiBold
              )
            } else {
              Text(
                text = "${targetBooking?.serviceName ?: "Service"} • Online",
                fontSize = 11.sp,
                color = Slate500
              )
            }
          }

          // Booking tracking quick link
          if (targetBooking != null) {
            FilledTonalButton(
              onClick = { onNavigateToTracking(targetBooking.id) },
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              modifier = Modifier.height(34.dp),
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Teal100,
                contentColor = BrandSecondary
              )
            ) {
              Icon(
                Icons.Default.DirectionsBike,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Track", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    },
    bottomBar = {
      Surface(
        color = Color.White,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
          // Quick reply chips
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            items(quickReplies) { chip ->
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = Slate100,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                modifier = Modifier.clickable {
                  if (targetBooking != null) {
                    repository.sendChatMessage(targetBooking.id, chip)
                  }
                }
              ) {
                Text(
                  text = chip,
                  fontSize = 11.sp,
                  color = Slate700,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }

          Divider(color = Slate200, thickness = 0.5.dp)

          // Message input bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Attachment icon
            IconButton(
              onClick = {
                if (targetBooking != null) {
                  repository.sendChatMessage(targetBooking.id, "📷 [Photo Shared: Kitchen pipe crack closeup]")
                }
              },
              modifier = Modifier.size(38.dp)
            ) {
              Icon(
                Icons.Outlined.PhotoCamera,
                contentDescription = "Attach photo",
                tint = Slate500,
                modifier = Modifier.size(22.dp)
              )
            }

            // Input field
            TextField(
              value = inputText,
              onValueChange = { inputText = it },
              placeholder = { Text("Type a message to professional...", fontSize = 13.sp, color = Slate400) },
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 44.dp, max = 100.dp),
              shape = RoundedCornerShape(22.dp),
              colors = TextFieldDefaults.colors(
                focusedContainerColor = Slate100,
                unfocusedContainerColor = Slate100,
                disabledContainerColor = Slate100,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
              ),
              trailingIcon = {
                IconButton(
                  onClick = {
                    if (inputText.isNotBlank() && targetBooking != null) {
                      val textToSend = inputText
                      inputText = ""
                      repository.sendChatMessage(targetBooking.id, textToSend)
                    }
                  },
                  enabled = inputText.isNotBlank()
                ) {
                  Box(
                    modifier = Modifier
                      .size(32.dp)
                      .clip(CircleShape)
                      .background(if (inputText.isNotBlank()) BrandPrimary else Slate300),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      Icons.Default.Send,
                      contentDescription = "Send",
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
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
      if (targetBooking == null) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Slate400, modifier = Modifier.size(48.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text("No Active Chat Session", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate800)
          Text("Book a service first to initiate direct chat with a professional.", fontSize = 13.sp, color = Slate500)
        }
      } else {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Booking info header card
          item {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Blue100.copy(alpha = 0.6f),
              border = androidx.compose.foundation.BorderStroke(1.dp, Blue100),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.Shield,
                  contentDescription = null,
                  tint = BrandPrimary,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Cooperative Protected Chat • #${targetBooking.id}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary
                  )
                  Text(
                    text = "Phone numbers are masked for safety. All communication is backed by our Cooperative Guarantee.",
                    fontSize = 10.sp,
                    color = Slate600
                  )
                }
              }
            }
          }

          // Message items
          items(messages) { msg ->
            val isMe = (repository.activeRole == "CUSTOMER" && msg.senderRole == "customer") ||
                (repository.activeRole == "PROFESSIONAL" && msg.senderRole == "professional")

            MessageBubble(message = msg, isMe = isMe)
          }

          // Typing indicator item
          if (repository.isProfessionalTyping) {
            item {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = Color.White,
                  shadowElevation = 1.dp
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    CircularProgressIndicator(
                      modifier = Modifier.size(12.dp),
                      strokeWidth = 2.dp,
                      color = BrandSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "${targetBooking.professionalName} is typing...",
                      fontSize = 11.sp,
                      color = Slate500,
                      fontWeight = FontWeight.Medium
                    )
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
fun MessageBubble(message: ChatMessage, isMe: Boolean) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
  ) {
    Surface(
      shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isMe) 16.dp else 4.dp,
        bottomEnd = if (isMe) 4.dp else 16.dp
      ),
      color = if (isMe) BrandPrimary else Color.White,
      shadowElevation = if (isMe) 0.dp else 1.dp,
      border = if (isMe) null else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
      modifier = Modifier.widthIn(max = 280.dp)
    ) {
      Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        if (!isMe) {
          Text(
            text = message.senderName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = BrandSecondary
          )
          Spacer(modifier = Modifier.height(2.dp))
        }

        Text(
          text = message.text,
          fontSize = 13.sp,
          color = if (isMe) Color.White else Slate900,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Time and Read Receipt Indicator
        Row(
          modifier = Modifier.align(Alignment.End),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = message.time,
            fontSize = 9.sp,
            color = if (isMe) Color.White.copy(alpha = 0.75f) else Slate400
          )

          if (isMe) {
            Spacer(modifier = Modifier.width(4.dp))
            when (message.status) {
              MessageReadStatus.SENT -> {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Sent",
                  tint = Color.White.copy(alpha = 0.7f),
                  modifier = Modifier.size(12.dp)
                )
              }
              MessageReadStatus.DELIVERED -> {
                Icon(
                  imageVector = Icons.Default.DoneAll,
                  contentDescription = "Delivered",
                  tint = Color.White.copy(alpha = 0.7f),
                  modifier = Modifier.size(14.dp)
                )
              }
              MessageReadStatus.READ -> {
                Icon(
                  imageVector = Icons.Default.DoneAll,
                  contentDescription = "Read",
                  tint = Color(0xFF67E8F9), // Bright cyan double check for read receipt
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}
