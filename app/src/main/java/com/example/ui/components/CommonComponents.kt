package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.SampleData
import com.example.model.BookingStatus
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
  repository: GigRepository,
  currentRoute: String,
  onNavigate: (String) -> Unit,
  onOpenNotifications: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showCityDropdown by remember { mutableStateOf(false) }
  var showRoleDialog by remember { mutableStateOf(false) }

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = Color.White,
    shadowElevation = 2.dp
  ) {
    Column {
      // Top line
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Logo & Brand
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { onNavigate("home") }
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(
                Brush.linearGradient(
                  colors = listOf(BrandPrimary, BrandSecondary)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Handshake,
              contentDescription = "Logo",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Cooperative Gig",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
              )
              Spacer(modifier = Modifier.width(4.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Teal100)
                  .padding(horizontal = 5.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "CO-OP",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = BrandSecondary
                )
              }
            }
            Text(
              text = "Fair gigs • Trusted pros",
              fontSize = 11.sp,
              color = Slate500
            )
          }
        }

        // Location & Actions
        Row(verticalAlignment = Alignment.CenterVertically) {
          // City Selector
          Box {
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = Slate100,
              modifier = Modifier.clickable { showCityDropdown = true }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = "City",
                  tint = BrandPrimary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = repository.selectedCity,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Slate800
                )
                Icon(
                  imageVector = Icons.Default.ArrowDropDown,
                  contentDescription = null,
                  tint = Slate500,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            DropdownMenu(
              expanded = showCityDropdown,
              onDismissRequest = { showCityDropdown = false }
            ) {
              SampleData.cities.forEach { city ->
                DropdownMenuItem(
                  text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = if (city == repository.selectedCity) BrandPrimary else Slate400,
                        modifier = Modifier.size(18.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = city,
                        fontWeight = if (city == repository.selectedCity) FontWeight.Bold else FontWeight.Normal,
                        color = if (city == repository.selectedCity) BrandPrimary else Slate800
                      )
                    }
                  },
                  onClick = {
                    repository.selectedCity = city
                    showCityDropdown = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Role Switcher Chip
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = when (repository.activeRole) {
              "PROFESSIONAL" -> Amber100
              "ADMIN" -> Red100
              else -> Blue100
            },
            modifier = Modifier.clickable { showRoleDialog = true }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = when (repository.activeRole) {
                  "PROFESSIONAL" -> Icons.Default.Engineering
                  "ADMIN" -> Icons.Default.AdminPanelSettings
                  else -> Icons.Default.Person
                },
                contentDescription = null,
                tint = when (repository.activeRole) {
                  "PROFESSIONAL" -> Color(0xFFB45309)
                  "ADMIN" -> Color(0xFFB91C1C)
                  else -> BrandPrimary
                },
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = when (repository.activeRole) {
                  "PROFESSIONAL" -> "Pro Mode"
                  "ADMIN" -> "Admin"
                  else -> "Customer"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = when (repository.activeRole) {
                  "PROFESSIONAL" -> Color(0xFF92400E)
                  "ADMIN" -> Color(0xFF991B1B)
                  else -> BrandPrimary
                }
              )
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Notification Bell
          val unreadCount = repository.notifications.count { !it.isRead }
          IconButton(
            onClick = onOpenNotifications,
            modifier = Modifier.size(36.dp)
          ) {
            BadgedBox(
              badge = {
                if (unreadCount > 0) {
                  Badge(containerColor = BrandEmergency) {
                    Text(text = unreadCount.toString(), fontSize = 9.sp)
                  }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = Slate700,
                modifier = Modifier.size(22.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Profile Avatar Button
          IconButton(
            onClick = { onNavigate("profile_editor") },
            modifier = Modifier.size(36.dp)
          ) {
            Box(
              modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(if (repository.activeRole == "PROFESSIONAL") BrandSecondary else BrandPrimary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (repository.activeRole == "PROFESSIONAL") "P" else repository.activeUserName.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      }
    }
  }

  // Role Selection Dialog
  if (showRoleDialog) {
    AlertDialog(
      onDismissRequest = { showRoleDialog = false },
      title = {
        Text("Switch Perspective", fontWeight = FontWeight.Bold, fontSize = 18.sp)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            "Test and experience the Cooperative Gig platform from all stakeholder angles:",
            fontSize = 13.sp,
            color = Slate600
          )

          RoleOptionCard(
            title = "Customer View",
            description = "Browse services, chat with pros, track live worker, make payments & review.",
            icon = Icons.Default.Person,
            isSelected = repository.activeRole == "CUSTOMER",
            onClick = {
              repository.activeRole = "CUSTOMER"
              showRoleDialog = false
              onNavigate("home")
            }
          )

          RoleOptionCard(
            title = "Service Professional Dashboard",
            description = "Logged in as Ramesh Kumar (Plumber). Accept jobs, change trip status, reply to customer chats, view daily earnings.",
            icon = Icons.Default.Engineering,
            isSelected = repository.activeRole == "PROFESSIONAL",
            onClick = {
              repository.activeRole = "PROFESSIONAL"
              showRoleDialog = false
              onNavigate("pro_dashboard")
            }
          )

          RoleOptionCard(
            title = "Platform Admin Portal",
            description = "Monitor platform metrics, verify cooperative members, check dispute logs and commission savings.",
            icon = Icons.Default.AdminPanelSettings,
            isSelected = repository.activeRole == "ADMIN",
            onClick = {
              repository.activeRole = "ADMIN"
              showRoleDialog = false
              onNavigate("admin_dashboard")
            }
          )
        }
      },
      confirmButton = {
        TextButton(onClick = { showRoleDialog = false }) {
          Text("Close", fontWeight = FontWeight.Bold)
        }
      }
    )
  }
}

@Composable
private fun RoleOptionCard(
  title: String,
  description: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) Blue100 else Slate50,
    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BrandPrimary) else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
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
          .size(40.dp)
          .clip(CircleShape)
          .background(if (isSelected) BrandPrimary else Slate200),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) Color.White else Slate700,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
        Text(text = description, fontSize = 11.sp, color = Slate600, lineHeight = 15.sp)
      }
    }
  }
}

@Composable
fun AppBottomNav(
  currentRoute: String,
  onNavigate: (String) -> Unit,
  unreadChatCount: Int = 1,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = Color.White,
    shadowElevation = 8.dp
  ) {
    NavigationBar(
      containerColor = Color.White,
      tonalElevation = 0.dp
    ) {
      NavigationBarItem(
        selected = currentRoute == "home",
        onClick = { onNavigate("home") },
        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
        label = { Text("Home", fontSize = 11.sp) },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = BrandPrimary,
          selectedTextColor = BrandPrimary,
          indicatorColor = Blue100
        )
      )

      NavigationBarItem(
        selected = currentRoute == "services",
        onClick = { onNavigate("services") },
        icon = { Icon(Icons.Default.GridView, contentDescription = "Services") },
        label = { Text("Services", fontSize = 11.sp) },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = BrandPrimary,
          selectedTextColor = BrandPrimary,
          indicatorColor = Blue100
        )
      )

      NavigationBarItem(
        selected = currentRoute == "ai_assistant",
        onClick = { onNavigate("ai_assistant") },
        icon = {
          Box {
            Icon(Icons.Default.AutoAwesome, contentDescription = "AI")
          }
        },
        label = { Text("AI Assist", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = BrandSecondary,
          selectedTextColor = BrandSecondary,
          indicatorColor = Teal100
        )
      )

      NavigationBarItem(
        selected = currentRoute == "chat",
        onClick = { onNavigate("chat") },
        icon = {
          BadgedBox(
            badge = {
              if (unreadChatCount > 0) {
                Badge(containerColor = BrandPrimary) {
                  Text(text = unreadChatCount.toString(), fontSize = 9.sp)
                }
              }
            }
          ) {
            Icon(Icons.Default.Chat, contentDescription = "Chat")
          }
        },
        label = { Text("Chat", fontSize = 11.sp) },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = BrandPrimary,
          selectedTextColor = BrandPrimary,
          indicatorColor = Blue100
        )
      )

      NavigationBarItem(
        selected = currentRoute == "my_bookings" || currentRoute == "track_service",
        onClick = { onNavigate("my_bookings") },
        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Bookings") },
        label = { Text("Bookings", fontSize = 11.sp) },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = BrandPrimary,
          selectedTextColor = BrandPrimary,
          indicatorColor = Blue100
        )
      )

      NavigationBarItem(
        selected = currentRoute == "profile_editor",
        onClick = { onNavigate("profile_editor") },
        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
        label = { Text("Profile", fontSize = 11.sp) },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = BrandPrimary,
          selectedTextColor = BrandPrimary,
          indicatorColor = Blue100
        )
      )
    }
  }
}

@Composable
fun StatusBadge(status: BookingStatus) {
  val (bgColor, textColor, icon) = when (status) {
    BookingStatus.REQUESTED -> Triple(Slate100, Slate700, Icons.Default.Schedule)
    BookingStatus.ACCEPTED -> Triple(Blue100, BrandPrimary, Icons.Default.ThumbUp)
    BookingStatus.ON_THE_WAY -> Triple(Teal100, BrandSecondary, Icons.Default.DirectionsBike)
    BookingStatus.ARRIVED -> Triple(Amber100, Color(0xFFB45309), Icons.Default.Place)
    BookingStatus.SERVICE_STARTED -> Triple(Blue100, BrandPrimary, Icons.Default.Build)
    BookingStatus.COMPLETED -> Triple(Green100, Green600, Icons.Default.CheckCircle)
    BookingStatus.CANCELLED -> Triple(Red100, BrandEmergency, Icons.Default.Cancel)
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bgColor
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = textColor,
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = status.displayName,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = textColor
      )
    }
  }
}

@Composable
fun RatingDisplay(rating: Double, reviewCount: Int? = null) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = Icons.Default.Star,
      contentDescription = "Rating",
      tint = Color(0xFFF59E0B),
      modifier = Modifier.size(15.dp)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = String.format("%.1f", rating),
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = Slate800
    )
    if (reviewCount != null) {
      Spacer(modifier = Modifier.width(2.dp))
      Text(
        text = "($reviewCount)",
        fontSize = 11.sp,
        color = Slate500
      )
    }
  }
}

@Composable
fun NotificationSheet(
  repository: GigRepository,
  onClose: () -> Unit,
  onNavigateToBooking: (String) -> Unit,
  onNavigateToChat: (String) -> Unit
) {
  AlertDialog(
    onDismissRequest = onClose,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Notifications, contentDescription = null, tint = BrandPrimary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        TextButton(onClick = {
          repository.notifications.forEach { it.isRead = true }
        }) {
          Text("Mark all read", fontSize = 12.sp)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 420.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (repository.notifications.isEmpty()) {
          Text(
            text = "No notifications yet.",
            fontSize = 13.sp,
            color = Slate500,
            modifier = Modifier.padding(vertical = 16.dp)
          )
        } else {
          repository.notifications.forEach { item ->
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (item.isRead) Slate50 else Blue100.copy(alpha = 0.5f),
              border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  item.isRead = true
                  if (item.bookingId != null) {
                    onClose()
                    if (item.type == "chat") {
                      onNavigateToChat(item.bookingId)
                    } else {
                      onNavigateToBooking(item.bookingId)
                    }
                  }
                }
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.Top
              ) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                      when (item.type) {
                        "tracking" -> Teal100
                        "chat" -> Blue100
                        "payment" -> Green100
                        else -> Amber100
                      }
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = when (item.type) {
                      "tracking" -> Icons.Default.DirectionsBike
                      "chat" -> Icons.Default.Chat
                      "payment" -> Icons.Default.Paid
                      else -> Icons.Default.Bookmark
                    },
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(16.dp)
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = item.title,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = Slate900
                    )
                    Text(text = item.timeAgo, fontSize = 10.sp, color = Slate400)
                  }
                  Spacer(modifier = Modifier.height(3.dp))
                  Text(text = item.message, fontSize = 12.sp, color = Slate700)
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(onClick = onClose) {
        Text("Done")
      }
    }
  )
}
