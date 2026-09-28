package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
fun PaymentScreen(
  repository: GigRepository,
  bookingId: String,
  onPaymentSuccess: (String) -> Unit,
  onBack: () -> Unit
) {
  val booking = repository.getBookingById(bookingId) ?: repository.bookings.first()

  var selectedMethod by remember { mutableStateOf("UPI (Google Pay / PhonePe)") }
  var isProcessing by remember { mutableStateOf(false) }
  var isPaidSuccess by remember { mutableStateOf(booking.paymentStatus == "Paid") }
  var transactionId by remember { mutableStateOf("TXN-${(100000..999999).random()}") }
  var showReceiptDialog by remember { mutableStateOf(false) }

  val paymentMethods = listOf(
    "UPI (Google Pay / PhonePe)",
    "Credit / Debit Card",
    "Net Banking (SBI / HDFC / ICICI)",
    "Cooperative Wallet",
    "Cash on Service"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(if (isPaidSuccess) "Payment Receipt" else "Secure Payment", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
      )
    },
    bottomBar = {
      if (!isPaidSuccess) {
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
              Text("Total Amount", fontSize = 11.sp, color = Slate500)
              Text("₹${booking.totalAmount}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = BrandPrimary)
            }

            Button(
              onClick = {
                isProcessing = true
                repository.completePayment(booking.id, selectedMethod)
                isPaidSuccess = true
                isProcessing = false
              },
              enabled = !isProcessing,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.height(46.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
              if (isProcessing) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
              } else {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pay Now", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
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
      if (isPaidSuccess) {
        // SUCCESS RECEIPT VIEW
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Green100),
            shadowElevation = 2.dp
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(60.dp)
                  .clip(CircleShape)
                  .background(Green100),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green600, modifier = Modifier.size(36.dp))
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text("Payment Successful!", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Slate900)
              Text("Your transaction has been securely processed", fontSize = 12.sp, color = Slate500)

              Spacer(modifier = Modifier.height(16.dp))
              Divider(color = Slate100, thickness = 1.dp)
              Spacer(modifier = Modifier.height(16.dp))

              // Details
              ReceiptRow(label = "Amount Paid", value = "₹${booking.totalAmount}", isBold = true)
              ReceiptRow(label = "Transaction ID", value = transactionId)
              ReceiptRow(label = "Booking ID", value = "#${booking.id}")
              ReceiptRow(label = "Service", value = booking.serviceName)
              ReceiptRow(label = "Professional", value = booking.professionalName)
              ReceiptRow(label = "Payment Method", value = selectedMethod)
              ReceiptRow(label = "Date & Time", value = "Today • Completed")

              Spacer(modifier = Modifier.height(20.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedButton(
                  onClick = { showReceiptDialog = true },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("View Receipt", fontSize = 12.sp)
                }

                Button(
                  onClick = { onPaymentSuccess(booking.id) },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f),
                  colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                  Text("Track Service", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      } else {
        // PAYMENT FORM
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
              Spacer(modifier = Modifier.height(10.dp))

              Surface(shape = RoundedCornerShape(8.dp), color = Slate50, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(booking.serviceName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                  Text("Professional: ${booking.professionalName}", fontSize = 11.sp, color = Slate600)
                  Text("Booking #${booking.id}", fontSize = 11.sp, color = BrandPrimary, fontWeight = FontWeight.SemiBold)
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Price rows
              ReceiptRow(label = "Service Charge", value = "₹${booking.basePrice}")
              ReceiptRow(label = "Cooperative Platform Fee", value = "₹${booking.platformFee}")
              ReceiptRow(label = "GST (18%)", value = "₹${booking.taxes}")
              ReceiptRow(label = "Cooperative First Gig Discount", value = "-₹${booking.discount}", isGreen = true)
              Divider(color = Slate200, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
              ReceiptRow(label = "Total Payable", value = "₹${booking.totalAmount}", isBold = true)
            }
          }
        }

        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
              Text("All payments are 100% encrypted & cooperative protected", fontSize = 11.sp, color = Slate500)
              Spacer(modifier = Modifier.height(12.dp))

              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                paymentMethods.forEach { method ->
                  val isSelected = selectedMethod == method
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Blue100 else Slate50,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BrandPrimary) else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { selectedMethod = method }
                  ) {
                    Row(
                      modifier = Modifier.padding(12.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      RadioButton(selected = isSelected, onClick = { selectedMethod = method })
                      Spacer(modifier = Modifier.width(8.dp))
                      Icon(
                        imageVector = when {
                          method.contains("UPI") -> Icons.Default.QrCode
                          method.contains("Card") -> Icons.Default.CreditCard
                          method.contains("Banking") -> Icons.Default.AccountBalance
                          method.contains("Wallet") -> Icons.Default.AccountBalanceWallet
                          else -> Icons.Default.Money
                        },
                        contentDescription = null,
                        tint = if (isSelected) BrandPrimary else Slate600,
                        modifier = Modifier.size(20.dp)
                      )
                      Spacer(modifier = Modifier.width(10.dp))
                      Text(method, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp, color = Slate800)
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

  if (showReceiptDialog) {
    AlertDialog(
      onDismissRequest = { showReceiptDialog = false },
      title = { Text("Tax Invoice / Receipt") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("COOPERATIVE GIG SERVICES SOCIETY", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text("Registration #KRN/COOP/2026/41029", fontSize = 10.sp, color = Slate500)
          Divider(modifier = Modifier.padding(vertical = 4.dp))
          Text("Customer: ${booking.customerName}")
          Text("Phone: ${booking.customerPhone}")
          Text("Address: ${booking.address}")
          Text("Transaction ID: $transactionId")
          Text("Amount: ₹${booking.totalAmount} (GST Included)")
          Text("Status: Paid (${booking.paymentMethod})")
        }
      },
      confirmButton = {
        Button(onClick = { showReceiptDialog = false }) {
          Text("Close")
        }
      }
    )
  }
}

@Composable
private fun ReceiptRow(label: String, value: String, isBold: Boolean = false, isGreen: Boolean = false) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, fontSize = 12.sp, color = Slate500)
    Text(
      value,
      fontSize = 12.sp,
      fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold,
      color = if (isGreen) Green600 else if (isBold) BrandPrimary else Slate800
    )
  }
}
