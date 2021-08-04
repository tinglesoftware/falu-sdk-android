![Language](https://img.shields.io/badge/language-Kotlin%205.0-green.svg)

# Falu Android SDK

Falu's Android SDK simplifies the process of building excellent financial services into Android applications.
We expose APIs that will enable to you to make payment and client evaluation requests.

## Installation

### Requirements
* Android 5.0 (API level 21) and above
* [Android Gradle Plugin] (https://developer.android.com/studio/releases/gradle-plugin) 4.2.2

### Setup
Add `falu` to your `build.gradle` dependencies.

```gradle
implementation "io.falu:falu-android:VERSION_NUMBER"
```
# Getting Started

### Setup Guide
Get started with our [Setup Guide](https://docs.falu.io/guides/developer/quickstart).


### Creating an instance of `Falu`

Create an instance `Falu` since its the entry point to SDK

```kotlin
val falu = FaluApiClient(this, "PUBLIC_KEY")
```
The [public key](https://docs.falu.io/guides/keys) is mandatory. Failing to provide it will result into an `Exception` when interacting with Falu.

You can also enable logging of network operations as follows:
```kotlin
val falu = FaluApiClient(this, "PUBLIC_KEY", true)
```
**NOTE**: It is recommended to **disable** logging in production

# Features

Once you have finished the setup process, you can proceed to use the features and functionalities offered by the SDK


## Evalautions

Use this feature when you want to know the credit score/worth of a  user.
This will allow you to know your user's spending habits from their financial statements.
Vist [Credit scoring customers using the Evaluations API](https://docs.falu.io/guides/evaluations) for more information relating to this.

```kotlin
val request = EvaluationRequest(
        scope = EvaluationScope.PERSONAL,
        name = "JOHN DOE",
        phone = "+2547123456789",
        password = "12345678",
        file = file,
    )

falu.createEvaluation(request, callback)

...

private val callback = object : ApiResultCallback<Evaluation> {
    override fun onSuccess(result: Evaluation) {
        // display in UI element
    }

    override fun onError(e: Exception) {
        print(e)
    }
}    
```

## Payments

Create a `Payment` object when initiating payments from a customer.
Falu supports several payment methods including `MPESA`.
See [How to Authorize Payments](https://docs.falu.io/guides/payments/authorizations) and
[How to Accept Payments](https://docs.falu.io/guides/payments) for information.

How to initiate `MPESA` payments:

```kotlin
val mpesa = MpesaPaymentRequest(
        phone = "+254712345678",
        reference = "254712345678",
        paybill = true,
        destination = "00110"
    )

val request = PaymentRequest(
        amount = 100,
        currency = "kes",
        mpesa = mpesa
    )

falu.createPayment(request, callback)  

...  

private val callback = object : ApiResultCallback<Payment> {
    override fun onSuccess(result: Payment) {
        // display in UI element
    }

    override fun onError(e: Exception) {
        print(e)
    }
} 
```